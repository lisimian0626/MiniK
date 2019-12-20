package com.beidousat.libbns.model;

/**
 * Created by Administrator on 2018/3/27.
 */

public class Common {
    public static String SongDb_Name = "SongCache.db";
    public static String qrcode;
    public static long timelimit = 5 * 60 * 60 * 1000;
    //订单支付轮询时间间隔
    public static final int Order_query_limit = 2000;
    //效果器串口
    public static final String Rj45Port = "/dev/ttyS3";//网线口
    public static final String AudioTopPort = "/dev/ttyS4";//3.5音频口（上）
    public static final String AudioDownPort = "/dev/ttyS1";//3.5音频口（下）
    //红外串口
    public static final String mInfraredPort = "/dev/ttyS0";
    //红外波特率
    public static final int mInfraredBaudRate = 9600;

    public static int versioncode = -1;
    public static boolean isEn = false;
    public static boolean isAuto = false;
    public static boolean isPersonal = false;
    public static boolean isICT = false;
    public static boolean isOCT = false;

    public static int lastMoney;
    public static int TBcount;
    public static String curSongPath;
    //webviewinterface
    public static String INTERFACE_CLOSEWINDOWS = "closewindows";
    public static String INTERFACE_LOADSTART = "loadStart";
    public static String INTERFACE_LOADFINISH = "loadFinish";
    public static String INTERFACE_PROGRESSCHANGE = "progressChange";
}
