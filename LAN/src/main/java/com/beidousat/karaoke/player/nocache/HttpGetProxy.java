package com.beidousat.karaoke.player.nocache;

import android.text.TextUtils;

import com.beidousat.karaoke.player.proxy.Config;
import com.beidousat.karaoke.player.proxy.EncrytMap;
import com.beidousat.karaoke.player.proxy.Utils;
import com.beidousat.libbns.util.Logger;

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
     * Response对象
     */
    private Config.ProxyResponse proxyResponse = null;

    /**
     * 视频id，预加载文件以ID命名
     */
    private String mId, mUrl;
    /**
     * 有效的媒体文件链接(重定向之后)
     */
    private String mMediaUrl;


    private Proxy proxy = null;

    /**
     * 初始化代理服务器，并启动代理服务器
     */
    public HttpGetProxy() {
        try {
            //初始化代理服务器
            localHost = Config.LOCAL_IP_ADDRESS;
            localServer = new ServerSocket(0, 1, InetAddress.getByName(localHost));
            localPort = localServer.getLocalPort();//有ServerSocket自动分配端口
            //启动代理服务器
            Thread thread = new Thread() {
                public void run() {
                    startProxy();
                }
            };
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        } catch (Exception e) {
        }
    }

    /**
     * 开始预加载,一个时间只能预加载一个视频
     *
     * @param id  视频唯一id，长时间有效
     * @param url 视频链接
     * @throws Exception
     */
    public void startDownload(String id, String url) throws Exception {
        mId = id;
        mUrl = url;
    }

    /**
     * 获取播放链接
     *
     * @param id
     */
    public String getLocalURL(String id) {
        if (TextUtils.isEmpty(mId)        //没预加载过
                || mId.equals(id) == false)//与预加载的Id不符合
            return "";
        //排除HTTP特殊,如重定向
        mMediaUrl = Utils.getRedirectUrl(mUrl);
        String localUrl = "";
        URI originalURI = URI.create(mMediaUrl);
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
            Logger.i(TAG, "......ready to start...........");
            try {
                Socket s = localServer.accept();
                if (proxy != null) {
                    proxy.closeSockets();
                }
                Logger.i(TAG, "......started...........");
                proxy = new Proxy(s);

                Thread thread = new Thread() {
                    public void run() {
                        Logger.i(TAG, "......ready to start..2.........");
                        try {
                            Socket s = localServer.accept();
                            proxy.closeSockets();
                            Logger.i(TAG, "......started....2.......");
                            proxy = new Proxy(s);
                            proxy.run();
                        } catch (IOException e) {
                            Logger.e(TAG, e.toString());
                            Logger.e(TAG, Utils.getExceptionMessage(e));
                        }

                    }
                };
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
                proxy.run();
                //break;
            } catch (IOException e) {
                Logger.e(TAG, e.toString());
                Logger.e(TAG, Utils.getExceptionMessage(e));
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

        public Proxy(Socket sckPlayer) {
            this.sckPlayer = sckPlayer;
        }

        /**
         * 关闭现有的链接
         */
        public void closeSockets() {
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
            int bytes_read;
            byte[] local_request = new byte[1024];
            byte[] remote_reply = new byte[1024 * 50];
            byte[] remote_cach = new byte[1024 * 50];
            int cachlen = 0;
            boolean sentResponseHeader = false;
            try {
                httpParser = new HttpParser(remoteHost, remotePort, localHost,
                        localPort, EncrytMap.getInstance().check(mId));
                Config.ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(
                        local_request)) != -1) {
                    byte[] buffer = httpParser.getRequestBody(local_request,
                            bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest(buffer);
                        break;
                    }
                }
//                System.out.println("=================run=================================");
                Logger.d(TAG,"=================run=================================");
                utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
                if (request != null) {// MediaPlayer的request有效
                    sckServer = utils.sentToServer(request._body);// 发送MediaPlayer的request
                } else {// MediaPlayer的request无效
                    closeSockets();
                    return;
                }
                // ------------------------------------------------------
                // 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
                // ------------------------------------------------------
                while (sckServer != null
                        && ((bytes_read = sckServer.getInputStream().read(
                        remote_reply)) != -1)) {
                    if (sentResponseHeader) {
                        try {// 拖动进度条时，容易在此异常，断开重连
                            utils.sendToMP(remote_reply, bytes_read);
                        } catch (Exception e) {
                            Logger.e(TAG, e.toString());
                            Logger.e(TAG, Utils.getExceptionMessage(e));
                            break;// 发送异常直接退出while
                        }

                        if (proxyResponse == null)
                            continue;// 没Response Header则退出本次循环

                        // 已完成读取
                        if (proxyResponse._currentPosition > proxyResponse._duration - SIZE) {
                            Logger.i(TAG, "....ready....over....");
                            proxyResponse._currentPosition = -1;
                        } else if (proxyResponse._currentPosition != -1) {// 没完成读取
                            proxyResponse._currentPosition += bytes_read;
                        }

                        continue;// 退出本次while
                    }
                    System.arraycopy(remote_reply, 0, remote_cach, cachlen, bytes_read);
                    cachlen += bytes_read;
                    if (cachlen < Config.ENCODE_HEAD_LEN)
                        continue;
                    proxyResponse = httpParser.getProxyResponse(remote_cach,
                            cachlen);
                    if (proxyResponse == null)
                        continue;// 没Response Header则退出本次循环
                    if (httpParser.getisEncrypt())
                        EncrytMap.getInstance().add(mId);
                    sentResponseHeader = true;
                    // send http header to mediaplayer
                    utils.sendToMP(httpParser.MPbody(proxyResponse._body, request._rangePosition));
                    // 发送剩余数据
                    if (proxyResponse._other != null) {
                        if (httpParser.getisEncrypt()) {
                            if (request._rangePosition > 0 && request._rangePosition < Config.ENCODE_LEN) {
                                if (request._rangePosition < proxyResponse._other.length) {
                                    int clen = (int) (proxyResponse._other.length - request._rangePosition);
                                    byte[] sendbyte = new byte[clen];
                                    System.arraycopy(proxyResponse._other, (int) request._rangePosition, sendbyte, 0, clen);
                                    utils.sendToMP(sendbyte);
                                }
                            } else
                                utils.sendToMP(proxyResponse._other);
                        } else
                            utils.sendToMP(proxyResponse._other);
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, e.toString());
                Logger.e(TAG, Utils.getExceptionMessage(e));
            } finally {
                // 关闭 2个SOCKET
                closeSockets();
            }
        }
    }
}