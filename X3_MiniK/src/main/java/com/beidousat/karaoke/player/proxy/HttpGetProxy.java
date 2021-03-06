package com.beidousat.karaoke.player.proxy;

import android.util.Log;

import com.beidousat.karaoke.player.proxy.Config.ProxyRequest;
import com.beidousat.karaoke.player.proxy.Config.ProxyResponse;

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
public class HttpGetProxy {

    /**
     * 避免某些Mediaplayer不播放尾部就结束
     */
    private static final int SIZE = 1024 * 1024;

    final static public String TAG = "HttpGetProxy";

    /**
     * 预加载缓存文件的最大数量
     */
    private int mBufferFileMaximum;
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
    /**下载线程*/
//	private DownloadThread downloadThread = null;
    /**
     * Response对象
     */
    private ProxyResponse proxyResponse = null;

    /**
     * 视频id，预加载文件以ID命名
     */
    private String mUrl;
    /**
     * 有效的媒体文件链接(重定向之后)
     */
    private String mMediaUrl;
    /**
     * 预加载文件路径
     */
    private String mMediaFilePath;
    /**
     * 预加载是否可用
     */
    private boolean mEnable = false;

    private Proxy proxy = null;

    /**
     * 初始化代理服务器，并启动代理服务器
     **/
    public HttpGetProxy() {
        try {
            //初始化代理服务器
            localHost = Config.LOCAL_IP_ADDRESS;
            localServer = new ServerSocket(0, 1, InetAddress.getByName(localHost));
            localPort = localServer.getLocalPort();//有ServerSocket自动分配端口
            //启动代理服务器
            new Thread() {
                public void run() {
                    startProxy();
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
        mMediaUrl = mUrl;
        // ----获取对应本地代理服务器的链接----//
        String localUrl = "";
        URI originalURI = URI.create(mMediaUrl);
        System.out.println("originalURI " + originalURI + " == " + mMediaUrl);
        remoteHost = originalURI.getHost();
        if (originalURI.getPort() != -1) {// URL带Port
            serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
            remotePort = originalURI.getPort();// 保存端口，中转时替换
            localUrl = mMediaUrl.replace(remoteHost + ":" + originalURI.getPort(), localHost + ":" + localPort);
        } else {// URL不带Port
            serverAddress = new InetSocketAddress(remoteHost, Config.HTTP_PORT);// 使用80端口
            remotePort = -1;
            localUrl = mMediaUrl.replace(remoteHost, localHost + ":" + localPort);
        }
        return localUrl;
    }

    private void startProxy() {
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
//				Log.i(TAG, "......started...........=="+s);
                proxy = new Proxy(s, mUrl);

                new Thread() {
                    public void run() {
                        Log.i(TAG, "......ready to start..2.........");
                        try {
                            Socket s = localServer.accept();
//							if(proxy.checkClose(mUrl))
                            {
                                proxy.closeSockets();
                                Thread.sleep(100);
                                Log.i(TAG, "......started....2.......=" + s);
                                proxy = new Proxy(s, mUrl);
                                proxy.run();
                            }
                        } catch (Exception e) {
//							Log.e(TAG, e.toString());
                        }

                    }
                }.start();
                proxy.run();
            } catch (IOException e) {
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

        public Proxy(Socket sckPlayer, String url) {
            this.sckPlayer = sckPlayer;
            this.url = url;
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
            } catch (IOException e1) {
            }
        }

        public void run() {
            HttpParser httpParser = null;
            HttpGetProxyUtils utils = null;
            String header;
            boolean sentResponseHeader = false;
            int BLEN = 1024 * 50;
            byte[] remote_reply = new byte[BLEN];
            byte[] local_request = new byte[1024];
            byte[] remote_cach = new byte[1024 * 300];
            int cachlen = 0;
            int bytes_read;
            int len, start = 0;
            try {
                System.out.println("serverAddress = " + serverAddress + " mUrl " + mUrl);
                utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
                httpParser = new HttpParser(remoteHost, remotePort, localHost,
                        localPort, false);

                ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(
                        local_request)) != -1) {
                    byte[] buffer = httpParser.getRequestBody(local_request,
                            bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest2(buffer);
                        break;
                    }
                }

                while (true) {
                    synchronized (this) {
                        if (!sentResponseHeader) {
                            start = (int) request._rangePosition;
                            header = CacheFile.getInstance().getHeader(mUrl, request._rangePosition);
                            System.out.println("===header==" + header);
                            if (null != header) {
                                sentResponseHeader = true;
                                utils.sendToMP(header.getBytes());
                                if (CacheFile.getInstance().isError(mUrl))
                                    break;
                            } else {
                                if (CacheFile.getInstance().find(mUrl) < 0)
                                    break;
                                Thread.sleep(2);
                            }
                        } else {
                            Cache cachebuf = CacheFile.getInstance().getBuf(mUrl, start);
                            if (cachebuf != null) {
                                System.out.println("downedLen:" + cachebuf.downedLen);
                                int cpylen = cachebuf.downedLen - start;
                                byte[] buf = new byte[cpylen];
                                System.out.println("downedLen:" + cachebuf.downedLen + " start : " + start + " cpylen " + cpylen);
                                System.arraycopy(cachebuf.buf, start, buf, 0, cpylen);
                                utils.sendToMP(buf, cpylen);
                                start += cpylen;
                            } else if (CacheFile.getInstance().find(mUrl) < 0)
                                break;
                            long pretime = System.currentTimeMillis();
                            sckServer = utils.sentToServer(httpParser.getServerBody(request, start + (CacheFile.getInstance().isEnc(mUrl) ? 8 : 0)));
                            sentResponseHeader = false;
                            while (sckServer != null
                                    && ((bytes_read = sckServer.getInputStream().read(
                                    remote_reply)) != -1)) {
                                if (sentResponseHeader) {
                                    if (bytes_read > 0) utils.sendToMP(remote_reply, bytes_read);
                                } else {
                                    System.arraycopy(remote_reply, 0, remote_cach, cachlen, bytes_read);
                                    cachlen += bytes_read;
                                    proxyResponse = httpParser.getProxyResponse(remote_cach,
                                            cachlen);
                                    if (proxyResponse == null)
                                        continue;
                                    sentResponseHeader = true;
                                    System.out.println("time  = " + (System.currentTimeMillis() - pretime));
                                    if (null != proxyResponse._other)
                                        utils.sendToMP(proxyResponse._other);
                                }
                            }
                            System.out.println("time 3  " + (System.currentTimeMillis() - pretime));
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