package com.beidousat.karaoke.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by J Wong on 2018/1/10.
 */

public class SystemBroadcastSender {

//    private final static String PREF_KEY_SERIAL_SCREEN_BAUDRATE = "pref_key_serial_screen_baudrate";

    /**
     * @param context
     * @param mode     0:left 1:right other:both
     * @param progress 0-10
     */
    public static void setVol(Context context, int mode, int progress) {
        Intent intent = new Intent();
        intent.setAction("com.ynh.volume_control");
        Log.d("SystemBroadcastSender", "setVol progress:" + progress + "  mode:" + mode);
        if (progress > 10) {
            progress = 10;
        } else if (progress < 0) {
            progress = 0;
        }
        switch (mode) {
            case 0:
                intent.putExtra("command", 0);
                intent.putExtra("value", progress);
                context.sendBroadcast(intent);
                break;
            case 1:
                intent.putExtra("command", 1);
                intent.putExtra("value", progress);
                context.sendBroadcast(intent);
                break;
            default:
                intent.putExtra("command", 0);
                intent.putExtra("value", progress);
                context.sendBroadcast(intent);

                intent.putExtra("command", 1);
                intent.putExtra("value", progress);
                context.sendBroadcast(intent);
                break;
        }
    }
}
