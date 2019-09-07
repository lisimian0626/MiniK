package com.beidousat.karaoke;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.DlgProgress;
import com.beidousat.libbns.security.PrivateKeyUpdater;
import com.beidousat.libbns.upgrade.AppUpgrader;
import com.beidousat.libbns.upgrade.SystemUpgrader;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J Wong on 2015/10/9 12:03.
 */
public class LanService extends Service {

    private DlgProgress mDlgSystemUpdate;
    private ScheduledExecutorService mScheduledExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();

        PrivateKeyUpdater privateKeyUpdater = new PrivateKeyUpdater();
        privateKeyUpdater.updateKey();

        checkSystemUpdate();

        AppUpgrader appUpgrader = new AppUpgrader(getApplicationContext());

        appUpgrader.checkVersion(!DiskFileUtil.is901() ? 18 : 24);

        AudioRecordFileUtil.deleteRecordFiles();
//        startScreenTimer();
//        ScreenAdSyncTimer.getInstance(getApplicationContext()).startTimer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!TextUtils.isEmpty(PrefData.getRoomCode(getApplicationContext())) && ServerConfigData.getInstance().getServerConfig() != null) {
//            KBoxSocketHeart kBoxSocketHeart = KBoxSocketHeart.getInstance(ServerConfigData.getInstance().getServerConfig().getStore_ip(),
//                    ServerConfigData.getInstance().getServerConfig().getStore_port());
//            kBoxSocketHeart.setKBoxId(PrefData.getRoomCode(getApplicationContext()));
//            kBoxSocketHeart.check();
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
            mDlgSystemUpdate.dismiss();
            mDlgSystemUpdate = null;
        }
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                if (mCanShowAd && System.currentTimeMillis() - mLastTouchTime > 5 * 60 * 1000) {
//                    onEnterScreenAd();
//                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void checkSystemUpdate() {
        SystemUpgrader systemUpgrader = new SystemUpgrader(getApplicationContext());
        systemUpgrader.setOnSystemUpdateListener(new SystemUpgrader.OnSystemUpdateListener() {
            @Override
            public void onSystemUpdateStart() {
                try {
                    mDlgSystemUpdate = new DlgProgress(Main.mMainActivity);
                    mDlgSystemUpdate.setTitle("系统升级");
                    mDlgSystemUpdate.setTip("系统升级中，请勿关机...");
                    mDlgSystemUpdate.show();
                } catch (Exception ex) {
                    Logger.w("VODService", "onSystemUpdateStart ex:" + ex.toString());
                }
            }

            @Override
            public void onSystemUpdateProgress(long progress, long total) {
                try {
                    if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                        mDlgSystemUpdate.setProgress(progress, total);
                    }
                } catch (Exception ex) {
                    Logger.w("VODService", "onSystemUpdateProgress ex:" + ex.toString());
                }
            }

            @Override
            public void onSystemUpdateCompletion() {
                try {
                    if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                        mDlgSystemUpdate.dismiss();
                        mDlgSystemUpdate = null;
                    }
                } catch (Exception ex) {
                    Logger.w("VODService", "onSystemUpdateCompletion ex:" + ex.toString());
                }
            }

            @Override
            public void onSystemUpdateFail(String msg) {
                try {
                    if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                        mDlgSystemUpdate.dismiss();
                        mDlgSystemUpdate = null;
                    }
                    Toast.makeText(getApplicationContext(), "升级失败：" + msg, Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Logger.w("VODService", "onSystemUpdateFail ex:" + ex.toString());
                }

            }
        });
        systemUpgrader.checkVersion(!DiskFileUtil.is901() ? 19 : 23);
    }

    private void startScreenTimer() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);

    }

    private void stopScreenTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }
}
