package com.beidousat.minidoor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.beidousat.libbns.upgrade.AppUpgrader;
import com.beidousat.libbns.upgrade.SystemUpgrader;

/**
 * Created by J Wong on 2017/6/28.
 */

public class MiniDoorService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        AppUpgrader appUpgrader = new AppUpgrader(getApplicationContext());
        appUpgrader.checkVersion(20);

        SystemUpgrader systemUpgrader = new SystemUpgrader(getApplicationContext());
        systemUpgrader.checkVersion(21);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
