package com.beidousat.libbns.model;

/**
 * Created by Administrator on 2018/3/27.
 */

public class Common {
    public static String SongDb_Name="SongCache.db";
    public static String qrcode;
    public static long timelimit=5*60*60*1000;
    //订单支付轮询时间间隔
    public static final int Order_query_limit=2000;
    //单机版
    public static boolean isSingle;
   //效果器串口
    public static final String mPort = "/dev/ttyS3";
    //红外串口
    public static final String mInfraredPort = "/dev/ttyS0";
    //效果器波特率
    public static final int mBaudRate = 4800;
    //红外波特率
    public static final int mInfraredBaudRate = 9600;


    public static  boolean isEn=false;


}
