package com.beidousat.karaoke;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.beidousat.karaoke.ad.ScreenAdSyncTimer;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.ServerConfigData;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.DlgProgress;
import com.beidousat.libbns.net.socket.KBoxSocketHeart;
import com.beidousat.libbns.security.PrivateKeyUpdater;
import com.beidousat.libbns.upgrade.AppUpgrader;
import com.beidousat.libbns.upgrade.SystemUpgrader;
import com.beidousat.libbns.util.Logger;
import com.czt.mp3recorder.AudioRecordFileUtil;

/**
 * Created by J Wong on 2015/10/9 12:03.
 */
public class LanService extends Service {

    private DlgProgress mDlgSystemUpdate;

    @Override
    public void onCreate() {
        super.onCreate();

        PrivateKeyUpdater privateKeyUpdater = new PrivateKeyUpdater();
        privateKeyUpdater.updateKey();

        checkSystemUpdate();

        AppUpgrader appUpgrader = new AppUpgrader(getApplicationContext());
        appUpgrader.checkVersion(101);

        AudioRecordFileUtil.deleteRecordFiles();

//        ScreenAdSyncTimer.getInstance(getApplicationContext()).startTimer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!TextUtils.isEmpty(PrefData.getRoomCode(getApplicationContext())) && ServerConfigData.getInstance().getServerConfig() != null) {
            KBoxSocketHeart kBoxSocketHeart = KBoxSocketHeart.getInstance(ServerConfigData.getInstance().getServerConfig().getStore_ip(),
                    ServerConfigData.getInstance().getServerConfig().getKbox_port());
            kBoxSocketHeart.setKBoxId(PrefData.getRoomCode(getApplicationContext()));
            Song song = ChooseSongs.getInstance(getApplicationContext()).getFirstSong();
            kBoxSocketHeart.setIsSinging(song != null);
            kBoxSocketHeart.check();
        }

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
        systemUpgrader.checkVersion(100);
    }

}
