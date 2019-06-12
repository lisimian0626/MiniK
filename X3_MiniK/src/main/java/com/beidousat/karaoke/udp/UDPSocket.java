package com.beidousat.karaoke.udp;

import android.content.Context;
import android.util.Log;

import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by melo on 2017/9/20.
 */

public class UDPSocket {

    private static final String TAG = "UDPSocket";

    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private static final String BROADCAST_IP = "39.108.224.58";

    public static final int CLIENT_PORT = 18811;
    public static final int SERVER_PORT=8960;
    private boolean isThreadRunning = false;

    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    private long lastReceiveTime = 0;
    private static final long TIME_OUT = 120 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 10 * 1000;

    private ExecutorService mThreadPool;
    private Thread clientThread;
    private HeartbeatTimer timer;
    private int heartbeatcount=20;
    public UDPSocket(Context context) {

        this.mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();

//        createUser();
    }


    public void startUDPSocket() {
        if (client != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(CLIENT_PORT);

            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启发送数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();

        startHeartbeatTimer();
//        sendMessage();
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String strReceive = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Log.d(TAG, strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
            try {
                int indexOf = strReceive.indexOf("VH2.0");
                String json = strReceive.substring(indexOf, strReceive.length());
                Gson gson = new Gson();
                SignDown signDown = gson.fromJson(json,new TypeToken<List<SignDown>>(){}.getType());
                if(signDown.getStatus().toUpperCase().equals("OK")){
                    EventBusUtil.postSticky(EventBusId.Udp.SUCCESS,signDown);
                }else {
                    EventBusUtil.postSticky(EventBusId.Udp.ERROR,signDown);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            //解析接收到的 json 信息

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (timer != null) {
            timer.exit();
        }
    }

    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startHeartbeatTimer() {
        timer = new HeartbeatTimer();
        timer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                heartbeatcount++;
                if(heartbeatcount>=20){
                    if(UDPComment.isSign){
                        HeatbeatUp heatbeatUp=new HeatbeatUp();
                        heatbeatUp.setHeartbeat(UDPComment.token,UDPComment.sendhsn);
                        sendMessage("VH2.0"+heatbeatUp.toString()+"\r\n");
                    }else{
                        SignUp sign=new SignUp();
                        sign.setSign(mContext,UDPComment.sendhsn);
                        sendMessage("VH2.0"+sign.toString()+"\r\n");
                    }
                    heartbeatcount=0;
                }
//                Log.d(TAG, "timer is onSchedule...");
//                long duration = System.currentTimeMillis() - lastReceiveTime;
//                Log.d(TAG, "duration:" + duration);
//                if (duration > TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
//                    Log.d(TAG, "超时，对方已经下线");
//                    // 刷新时间，重新进入下一个心跳周期
//                    lastReceiveTime = System.currentTimeMillis();
//                } else if (duration > HEARTBEAT_MESSAGE_DURATION) {//若超过十秒他没收到我的心跳包，则重新发一个。
//                    String string = "VH2.0{\"event\":\"sign\",\"eventkey\":1,\"kbox_sn\":\"B00010408\",\"device_sn\":\"6432760a68cad942\",\"os_version\":\"432\",\"version\":\"110\",\"hsn\":\"1\"}\r\n";
//                    sendMessage(string);
//                }
            }

        });
        timer.startTimer(1000, 1000);
    }

    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);

                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, SERVER_PORT);

                    client.send(packet);
                    UDPComment.sendhsn+=2;
                    // 数据发送事件
                    Log.d(TAG, "数据发送成功  "+message);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
