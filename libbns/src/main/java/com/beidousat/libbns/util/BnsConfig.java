package com.beidousat.libbns.util;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

/**
 * Created by J Wong on 2015/9/30.
 */

public class BnsConfig {
      public final static String ProxyHost="http://media.imtbox.com/";
    public final static String LocalAddress = "http://127.0.0.1/";
//    public final static String LocalAddress = "http://vl.imbox.com/";
    /**
     * VOD api
     */
//    public static String SERVER_ADDRESS = "";

//    public static String SERVER_ADDRESS = "minik.beidousat.com";
//    public static String SERVER_ADDRESS = "192.168.1.148:8080";
    /**
     * test
     */

//     public final static String SERVER_ADDRESS = "172.30.4.230";
//
//    public static final String DOMAIN_FILE = "https://" + SERVER_ADDRESS + "/";
//
//    public static final String DOMAIN_IP = DOMAIN_FILE + "?";

    /**
     * 是否开启LOG
     */
    public static final boolean DEBUG = false;

    /**
     * 普通倒计时(结束、续费)
     */
    public static final int CNT_FINISH = 30;

    /**
     * 最多可暂停次数
     */
    public static final int MAX_PAUSE_TIME = 1;

    /**
     * 购买包曲情况下，多久未点歌自动扣减一首歌
     */
    public static final int CHOOSE_SONG_TIME = 60 * 10;


    public static boolean is901() {
        String model = android.os.Build.MODEL;
        return "rk3288_box".equalsIgnoreCase(model);
    }
    public static final int PREVIEW = 1;
    public static final int NORMAL = 2;
    public static final int PUBLIC = 3;

    public static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

