package com.beidousat.karaoke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        PackageManager manager = context.getPackageManager();
//        String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
        Log.e("test","替换成功");
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String cmd = "su -c reboot";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
