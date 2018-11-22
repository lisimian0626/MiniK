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
     * 预加载所需的大小
     */
    private int mBufferSize;
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
     * 缓存文件夹
     */
    private String mBufferDirPath = null;
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
     *
     * @param dirPath     缓存文件夹的路径
     * @param size    所需预加载的大小
     * @param maximum 预加载文件最大数
     */
    public HttpGetProxy(String dirPath, int size, int maximum) {
        try {
            //初始化代理服务器
            mBufferDirPath = dirPath;
            mBufferSize = size;
            mBufferFileMaximum = maximum;
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
     * 代理服务器是否可用
     * @return
     */
//	public boolean getEnable(boolean ischeck){
//		//判断外部存储器是否可用
//		if(ischeck){
//		File dir = new File(mBufferDirPath);
//		mEnable=dir.exists();
//		if(!mEnable)
//			return mEnable;
//
//		//获取可用空间大小
//		long freeSize = Utils.getAvailaleSize(mBufferDirPath);
//		mEnable = (freeSize > mBufferSize);
//		
//		return mEnable;
//		}
//		else
//			return true;
//	}

    /**
     * 停止下载
     */
//	public void stopDownload(){
//		if (downloadThread != null && downloadThread.isDownloading())
//			downloadThread.stopThread();
//	}

    /**
     * 开始预加载,一个时间只能预加载一个视频
     *
     * @param url        视频链接
     * @throws Exception
     */
    public void startDownload(String url) throws Exception {
        //代理服务器不可用
//		if(!getEnable(isDownload))
//			return;

        //清除过去的缓存文件
//		if(isDownload)
//		Utils.asynRemoveBufferFile(mBufferDirPath, mBufferFileMaximum);
        mUrl = url;
//		String fileName = Utils.getValidFileName(mId);
//		mMediaFilePath = mBufferDirPath + "/" + fileName;
//
//		//判断文件是否存在，忽略已经缓冲过的文件
//		File tmpFile = new File(mMediaFilePath);
//		if(tmpFile.exists() && tmpFile.length()>=mBufferSize){
//			Log.i(TAG, "----exists:" + mMediaFilePath+" size:"+tmpFile.length());
//			return;
//		}
//		stopDownload();
//		if (isDownload) {
//			downloadThread = new DownloadThread(mUrl, mMediaFilePath,mBufferSize);
//			downloadThread.startThread();
//			Log.i(TAG, "----startDownload:" + mMediaFilePath);
//		}
    }

    /**
     * 获取播放链接
     *
     *
     */
    public String getLocalURL() {
//		if(TextUtils.isEmpty(mId)		//没预加载过 
//				|| mId.equals(id)==false)//与预加载的Id不符合
//			return "";

        //代理服务器不可用
//		if(!getEnable())
//			return mUrl;

        //Log.e("Http mUrl",mUrl);
        //排除HTTP特殊,如重定向
        mMediaUrl = mUrl;//Utils.getRedirectUrl(mUrl);
        //Log.e("Http mMediaUrl",mMediaUrl);
        // ----获取对应本地代理服务器的链接----//
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
//			Log.i(TAG, "......ready to start...........");
            try {
                Socket s = localServer.accept();
                if (proxy != null) {
                    proxy.closeSockets();
                }
//				Log.i(TAG, "......started...........=="+s);
                proxy = new Proxy(s, mUrl);

                new Thread() {
                    public void run() {
//						Log.i(TAG, "......ready to start..2.........");
                        try {
                            Socket s = localServer.accept();
//							if(proxy.checkClose(mUrl))
                            {
                                proxy.closeSockets();
//							Log.i(TAG, "......started....2.......="+s);
                                proxy = new Proxy(s, mUrl);
                                proxy.run();
                            }
//							else
//							{
//								s.close();
//							}
                        } catch (IOException e) {
//							Log.e(TAG, e.toString());
//							Log.e(TAG, Utils.getExceptionMessage(e));
                        }

                    }
                }.start();
                proxy.run();
                //break;
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
//				System.out.println("serverAddress = "+serverAddress+" mUrl "+mUrl);
                utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
                httpParser = new HttpParser(remoteHost, remotePort, localHost,
                        localPort);

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
//                            System.out.println("===header==" + header);
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
//						System.out.println(start+" =start= "+len+" = "+mUrl);
                            if (cachebuf != null) {
//                                System.out.println("downedLen:" + cachebuf.downedLen);
                                int cpylen = cachebuf.downedLen - start;
                                byte[] buf = new byte[cpylen];
//                                System.out.println("downedLen:" + cachebuf.downedLen + " start : " + start + " cpylen " + cpylen);
                                System.arraycopy(cachebuf.buf, start, buf, 0, cpylen);

                                utils.sendToMP(buf, cpylen);
                                start += cpylen;

                            } else if (CacheFile.getInstance().find(mUrl) < 0)
                                break;

                            sckServer = utils.sentToServer(httpParser.getServerBody(request, start + (CacheFile.getInstance().isEnc(mUrl) ? 8 : 0)));
                            sentResponseHeader = false;
                            while (sckServer != null
                                    && ((bytes_read = sckServer.getInputStream().read(
                                    remote_reply)) != -1)) {
                                if (sentResponseHeader) {
//								System.out.println("bytes_read "+bytes_read);
                                    if (bytes_read > 0) utils.sendToMP(remote_reply, bytes_read);
                                } else {
                                    System.arraycopy(remote_reply, 0, remote_cach, cachlen, bytes_read);
                                    cachlen += bytes_read;
                                    proxyResponse = httpParser.getProxyResponse(remote_cach,
                                            cachlen);

                                    if (proxyResponse == null)
                                        continue;
//							System.out.println("s body "+new String (proxyResponse._body));
                                    sentResponseHeader = true;
                                    if (null != proxyResponse._other)
                                        utils.sendToMP(proxyResponse._other);
//							break;
                                }
                            }

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

        public void run2() {
            HttpParser httpParser = null;
            HttpGetProxyUtils utils = null;
            int bytes_read;

            byte[] local_request = new byte[1024];
            byte[] remote_reply = new byte[1024 * 150];
            byte[] remote_cach = new byte[1024 * 300];
            int cachlen = 0;
            boolean sentResponseHeader = false;

            try {
//				Log.i(TAG, "<----------------------------------->");
//				stopDownload();

                httpParser = new HttpParser(remoteHost, remotePort, localHost,
                        localPort);

                ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(
                        local_request)) != -1) {
                    byte[] buffer = httpParser.getRequestBody(local_request,
                            bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest(buffer);
                        break;
                    }
                }
//                System.out.println("=================run=================================" + request._body);
                utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
//				boolean isExists = new File(mMediaFilePath).exists();
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
//							Log.e(TAG, e.toString());
//							Log.e(TAG, Utils.getExceptionMessage(e));
                            break;// 发送异常直接退出while
                        }

                        if (proxyResponse == null)
                            continue;// 没Response Header则退出本次循环

                        // 已完成读取
                        if (proxyResponse._currentPosition > proxyResponse._duration - SIZE) {
                            Log.i(TAG, "....ready....over....");
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

                    sentResponseHeader = true;
                    // send http header to mediaplayer
                    byte[] mpbody = httpParser.MPbody(proxyResponse._body, request._rangePosition);
//                    System.out.println(" = mpbody=" + new String(mpbody));
                    utils.sendToMP(mpbody);
                    /*if (isExists) {// 需要发送预加载到MediaPlayer
//						Log.i(TAG, "----------------->需要发送预加载到MediaPlayer "+request._rangePosition);
						isExists = false;
						long sentBufferSize = 0;
						sentBufferSize = utils.sendPrebufferToMP(
								mMediaFilePath, request._rangePosition);
						if (sentBufferSize > 0) {// 成功发送预加载，重新发送请求到服务器
							// 修改Range后的Request发送给服务器
//							System.out.println("=sentBufferSize==="+sentBufferSize);
							long newRange;
							if(httpParser.getisEncrypt())
								newRange= (sentBufferSize + request._rangePosition+Config.HEAD_LEN);
							else
								newRange= (sentBufferSize + request._rangePosition);
							String newRequestStr = httpParser
									.modifyRequestRange(request._body, newRange);
//							Log.i(TAG, "========new========\r\n"+newRequestStr);
							try {
								if (sckServer != null)
									sckServer.close();
							} catch (IOException ex) {
							}
							sckServer = utils.sentToServer(newRequestStr);
							// 把服务器的Response的Header去掉
							
							proxyResponse = utils.removeResponseHeader(
									sckServer, httpParser);
							request._rangePosition=0;
							continue;
						}
					}*/
                    // 发送剩余数据
                    if (proxyResponse._other != null) {

                            utils.sendToMP(proxyResponse._other);
                    }
                }


            } catch (Exception e) {
//				Log.e(TAG, e.toString());
//				Log.e(TAG, Utils.getExceptionMessage(e));
            } finally {
                // 关闭 2个SOCKET
                closeSockets();
            }
        }
    }

}