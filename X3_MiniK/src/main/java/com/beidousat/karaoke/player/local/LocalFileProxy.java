package com.beidousat.karaoke.player.local;

import android.util.Log;

import com.beidousat.karaoke.player.proxy.Config;
import com.beidousat.karaoke.player.proxy.Config.ProxyRequest;
import com.beidousat.karaoke.player.proxy.HttpParser;
import com.beidousat.karaoke.player.proxy.Utils;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;

/**
 * 代理服务器类
 *
 * @author hellogv
 */
public class LocalFileProxy {

    final static public String TAG = "LocalFileProxy";

    /**
     * 链接带的端口
     */
    private int remotePort = -1;
    /**
     * 远程服务器地址
     */
    private String remoteHost;
    /**
     * 代理服务器使用的端口
     */
    private int localPort;
    /**
     * 本地服务器地址
     */
    private String localHost;
    /**
     * TCP Server，接收Media Player连接
     */
    private ServerSocket localServer = null;
    /**
     * 服务器的Address
     */
    private SocketAddress serverAddress;
    /**
     * 视频id，预加载文件以ID命名
     */
    private String mUrl;
    /**
     * 有效的媒体文件链接(重定向之后)
     */
    private String mMediaUrl;

    /**
     * 预加载是否可用
     */
    private boolean mEnable = false;

    private Proxy proxy = null;


    /**
     * 初始化代理服务器，并启动代理服务器
     */
    public LocalFileProxy(final String savePath) {
        try {
            //初始化代理服务器
            localHost = Config.LOCAL_IP_ADDRESS;
            localServer = new ServerSocket(0, 1, InetAddress.getByName(localHost));
            localPort = localServer.getLocalPort();//有ServerSocket自动分配端口
            //启动代理服务器
            new Thread() {
                public void run() {
                    startProxy(savePath);
                }
            }.start();
            mEnable = true;
        } catch (Exception e) {
            mEnable = false;
        }
    }

    /**
     * 开始预加载,一个时间只能预加载一个视频
     *
     * @param url 视频链接
     * @throws Exception
     */
    public void startDownload(String url) throws Exception {
        mUrl = url;
    }

    /**
     * 获取播放链接
     */
    public String getLocalURL() {
        //Log.e("Http mUrl",mUrl);
        //排除HTTP特殊,如重定向
        mMediaUrl = mUrl;
//        Utils.getRedirectUrl(mUrl);
        //Log.e("Http mMediaUrl",mMediaUrl);
        // ----获取对应本地代理服务器的链接----//
        String localUrl = "";
        URI originalURI = URI.create(mMediaUrl);
        System.out.println("originalURI " + originalURI + " == " + mMediaUrl);
        remoteHost = originalURI.getHost();
        Logger.d(TAG, "getLocalURL :" + remoteHost + " getPort: " + originalURI.getPort());
        if (originalURI.getPort() != -1) {// URL带Port
            serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
            remotePort = originalURI.getPort();// 保存端口，中转时替换
            localUrl = mMediaUrl.replace(remoteHost + ":" + originalURI.getPort(), localHost + ":" + localPort);
        } else {// URL不带Port
            serverAddress = new InetSocketAddress(remoteHost, Config.HTTP_PORT);// 使用80端口
            remotePort = -1;
            localUrl = mMediaUrl.replace(remoteHost, localHost + ":" + localPort);
        }
        Logger.d(TAG, "getLocalURL :" + " localUrl: " + localUrl);
        return localUrl;
    }

    private void startProxy(final String savePath) {
        while (true) {
            // --------------------------------------
            // 监听MediaPlayer的请求，MediaPlayer->代理服务器
            // --------------------------------------
            Log.i(TAG, "......ready to start...........");
            try {
                Socket s = localServer.accept();
                if (proxy != null) {
                    proxy.closeSockets();
                }
                proxy = new Proxy(s, mUrl,savePath);
                new Thread() {
                    public void run() {
                        Log.i(TAG, "......ready to start..2.........");
                        try {
                            Socket s = localServer.accept();
                            {
                                proxy.closeSockets();
                                Thread.sleep(100);
                                Log.i(TAG, "......started....2.......=" + s);
                                proxy = new Proxy(s, mUrl,savePath);
                                proxy.run();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            Log.e(TAG, Utils.getExceptionMessage(e));
                        }

                    }
                }.start();
                proxy.run();
            } catch (IOException e) {
//				Log.e(TAG, e.toString());
//				Log.e(TAG, Utils.getExceptionMessage(e));
            }
        }
    }


    private class Proxy {
        /**
         * 收发Media Player请求的Socket
         */
        private Socket sckPlayer = null;
        /**
         * 收发Media Server请求的Socket
         */
        private Socket sckServer = null;
        private boolean isclose = false;
        private String url;
        private FileInputStream fileInputStream;

        public Proxy(Socket sckPlayer, String url,String savePath) {
            Logger.d(TAG, "Proxy url" + url);
            this.sckPlayer = sckPlayer;
            this.url = url;
            LocalFileCache.getInstance().add(url, url);
            File file = DiskFileUtil.getDiskFileByUrl(savePath);
            // if (DiskFileUtil.getSdcardFileByUrl(url) != null) {
            //   file = DiskFileUtil.getSdcardFileByUrl(url);
            //}
            Logger.d(TAG, "Proxy local file==" + file.getAbsolutePath());
            try {
                fileInputStream = new FileInputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean checkClose(String url) {
            if (isclose)
                return true;
            if (this.url.equals(url))
                return false;
            return true;
        }

        /**
         * 关闭现有的链接
         */
        public void closeSockets() {
            isclose = true;
            try {// 开始新的request之前关闭过去的Socket
                if (sckPlayer != null) {
                    sckPlayer.close();
                    sckPlayer = null;
                }
                if (sckServer != null) {
                    sckServer.close();
                    sckServer = null;
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                    fileInputStream = null;
                }
            } catch (IOException e1) {
                Logger.w(TAG, "closeSockets IOException:" + e1.toString());
            } catch (Exception e) {
                Logger.w(TAG, "closeSockets Exception:" + e.toString());
            }
        }

        public void run() {
            HttpParser httpParser = null;
            LocalFileProxyUtils utils = null;
            String header;
            boolean sentResponseHeader = false;
            byte[] local_request = new byte[1024];
            int bytes_read;
            int start = 0;
            try {
                System.out.println("serverAddress = " + serverAddress + " mUrl " + mUrl);
                utils = new LocalFileProxyUtils(sckPlayer, serverAddress);
                httpParser = new HttpParser(remoteHost, remotePort, localHost, localPort, false);
                ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(local_request)) != -1) {
                    byte[] buffer = httpParser.getRequestBody(local_request, bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest2(buffer);
                        break;
                    }
                }
                while (!isclose) {
                    synchronized (this) {
                        if (!sentResponseHeader) {
                            start = (int) request._rangePosition;
                            header = LocalFileCache.getInstance().getHeader(mUrl, request._rangePosition);
                            System.out.println("===header==" + header);
                            if (null != header) {
                                sentResponseHeader = true;
                                utils.sendToMP(header.getBytes());
                                if (LocalFileCache.getInstance().isError(mUrl))
                                    break;
                            } else {
                                if (LocalFileCache.getInstance().find(mUrl) < 0)
                                    break;
                                Thread.sleep(2);
                            }
                        } else {
                            Cache cachebuf = LocalFileCache.getInstance().getBuf(mUrl, start);
                            if (cachebuf != null) {
                                System.out.println("downedLen:" + cachebuf.downedLen);
                                int cpylen = cachebuf.downedLen - start;
                                byte[] buf = new byte[cpylen];
                                System.out.println("downedLen:" + cachebuf.downedLen + " start : " + start + " cpylen " + cpylen);
                                System.arraycopy(cachebuf.buf, start, buf, 0, cpylen);
                                utils.sendToMP(buf, cpylen);
                                start += cpylen;
                            } else if (LocalFileCache.getInstance().find(mUrl) < 0)
                                break;
                            long preTime = System.currentTimeMillis();
                            int seek = start + (LocalFileCache.getInstance().isEnc(mUrl) ? 8 : 0);
                            sckServer = utils.sentToServer(httpParser.getServerBody(request, seek));
                            long skip = fileInputStream.skip(seek);
                            Logger.d(TAG, "run ship :" + skip + "  seek :" + seek);
                            byte[] bytes1 = new byte[1024];
                            int rLen;
                            while ((rLen = fileInputStream.read(bytes1)) != -1) {
                                utils.sendToMP(bytes1, rLen);
                            }
                            System.out.println("time 3  " + (System.currentTimeMillis() - preTime));
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                closeSockets();
            }
        }
    }
}