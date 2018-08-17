package com.beidousat.libbns.net.socket;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by J Wong on 2017/4/27.
 */

public class BnsSocket {

    private static final String TAG = "BnsSocket";
    /**
     * 心跳检测时间
     */
    private long HEART_BEAT_RATE = 5 * 1000;


    private long sendTime = 0L;

    /**
     * 弱引用 在引用对象的同时允许对垃圾对象进行回收
     */
    private WeakReference<Socket> mSocket;

    private ReadThread mReadThread;

    private String mHost;

    private int mPort;

    public BnsSocket(String hort, int port) {
        mHost = hort;
        mPort = port;
    }

    public void setHeartRate(long interval) {
        HEART_BEAT_RATE = interval;
    }

    public void start() {
        new InitSocketThread().start();
    }

    // 初始化socket
    private void initSocket() throws UnknownHostException, IOException {
//        Log.e("test","initSocket");
        Socket socket = new Socket(mHost, mPort);
        mSocket = new WeakReference<Socket>(socket);
        mReadThread = new ReadThread(socket);
        mReadThread.start();
        mHandler.postDelayed(heartBeatRunnable, 100);// 初始化成功后，就准备发送心跳包
    }

    // 释放socket
    private  void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (sk != null && !sk.isClosed()) {
                    sk.close();
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class ReadThread extends Thread {

        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                try {
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int length = 0;
                    while (!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(Arrays.copyOf(buffer, length)).trim();
                            Logger.d(TAG, "收到服务器发送来的消息：" + message);

                            // 收到服务器过来的消息，就通过Broadcast发送出去
//                            if (message.equals("ok")) {// 处理心跳回复
//                                Intent intent = new Intent(HEART_BEAT_ACTION);
//                                sendBroadcast(intent);
//                            } else {
//                                // 其他消息回复
//                                Intent intent = new Intent(MESSAGE_ACTION);
//                                intent.putExtra("message", message);
//                                sendBroadcast(intent);
//                            }
                            if (mBnsSocketListener != null) {
                                mBnsSocketListener.onSocketReceive(message);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 发送心跳包
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE && !TextUtils.isEmpty(mHeartPackage)) {
                boolean isSuccess = sendMsg(System.nanoTime() + mHeartPackage);// 就发送一个\r\n过去, 如果发送失败，就重新初始化一个socket
                if (!isSuccess) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private String mHeartPackage;

    public void setHeartPackage(String heartPackage) {
        mHeartPackage = heartPackage;
    }

    public boolean sendMsg(String msg) {

        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        Socket soc = mSocket.get();
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
                String message = msg + "\r\n";
                os.write(message.getBytes());
                os.flush();
                sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
                Logger.d(TAG, "发送成功的时间：" + sendTime + "  内容：" + message);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private BnsSocketListener mBnsSocketListener;

    public void setBnsSocketListener(BnsSocketListener listener) {
        this.mBnsSocketListener = listener;
    }

    public interface BnsSocketListener {
        void onSocketReceive(String msg);
    }
}
