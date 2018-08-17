package com.beidousat.karaoke.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by J Wong on 2017/11/7.
 */

public class UIUtils {

    public static void hideNavibar(Context context, boolean hide) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", hide);
        context.sendBroadcast(intent);
    }


//    public static void openCalibrationApp(Context context) {
//        try {
//            Intent intent = new Intent("/");
//            ComponentName  cm= new ComponentName("org.zeroxlab.util.tscal",
//                    "org.zeroxlab.util.tscal.TSCalibration");
//            intent.setComponent(cm);
//            intent.setAction("android.intent.action.VIEW");
//            context.startActivity(intent);
//        } catch (Exception e) {
//            Logger.w("UIUtils", "open Calibration ex:" + e.toString());
//        }
//    }
}
