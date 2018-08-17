package com.beidousat.libbns.util;

import android.util.Log;

/**
 * Created by J Wong on 2016/2/4 11:04.
 */
public class Logger {

    public static void e(String tag, String msg) {
        if (BnsConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        if (BnsConfig.DEBUG)
            Log.e(tag, msg, e);
    }

    public static void w(String tag, String msg) {
        if (BnsConfig.DEBUG)
            Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (BnsConfig.DEBUG)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (BnsConfig.DEBUG)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BnsConfig.DEBUG)
            Log.v(tag, msg);
    }
}
