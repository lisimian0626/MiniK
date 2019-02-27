package com.beidousat.karaoke.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by J Wong on 2018/2/1.
 * ANR异常捕获
 */

public class ANRCacheHelper {

    private static MyReceiver myReceiver;

    private static final String ACTION_ANR = "android.intent.action.ANR";

    public static void registerANRReceiver(Context context) {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_ANR);
        intentFilter.setPriority(Integer.MAX_VALUE);
        context.registerReceiver(myReceiver, intentFilter);
    }

    public static void unregisterANRReceiver(Context context) {
        if (myReceiver == null) {
            return;
        }
        context.unregisterReceiver(myReceiver);
    }


    private static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ANR)) {
                // to do
                Log.e("ANRCacheHelper", "ANR happen !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                String anr = "/data/anr/traces.txt";
                Toast.makeText(context, "程序无响应！", Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }
    }
}
