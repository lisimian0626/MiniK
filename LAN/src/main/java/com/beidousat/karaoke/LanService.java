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
import com.beidousat.libbns.upgrade.SdSingerImageInit;
import com.beidousat.libbns.upgrade.SystemUpgrader;
import com.beidousat.libbns.util.Logger;
import com.czt.mp3recorder.AudioRecordFileUtil;

/**
 * Created by J Wong on 2015/10/9 12:03.
 */
public class LanService extends Service {

    private DlgProgress mDlgSingerInit;
    private DlgProgress mDlgSystemUpdate;

    @Override
    public void onCreate() {
        super.onCreate();

        PrivateKeyUpdater privateKeyUpdater = new PrivateKeyUpdater();
        privateKeyUpdater.updateKey();

        checkSystemUpdate();

        AppUpgrader appUpgrader = new AppUpgrader(getApplicationContext());
        appUpgrader.checkVersion(18);

        initSingerImg();


        AudioRecordFileUtil.deleteRecordFiles();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
            mDlgSystemUpdate.dismiss();
            mDlgSystemUpdate = null;
        }
        if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
            mDlgSingerInit.dismiss();
            mDlgSingerInit = null;
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
        systemUpgrader.checkVersion(19);
    }

    private void initSingerImg() {
        SdSingerImageInit sdSingerImageInit = new SdSingerImageInit();
        sdSingerImageInit.setSingerImgInitListener(new SdSingerImageInit.SingerImgInitListener() {
            @Override
            public void downloadStart() {
                try {
                    mDlgSingerInit = new DlgProgress(Main.mMainActivity);
                    mDlgSingerInit.setTitle("系统初始化");
                    mDlgSingerInit.setTip("系统初始化中，请勿关机...");
                    mDlgSingerInit.show();
                } catch (Exception ex) {
                    Logger.w("VODService", "downloadStart ex:" + ex.toString());
                }
            }

            @Override
            public void downloadProgress(long progress, long total) {
                try {
                    if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                        mDlgSingerInit.setProgress(progress, total);
                    }
                } catch (Exception ex) {
                    Logger.w("VODService", "downloadProgress ex:" + ex.toString());
                }
            }

            @Override
            public void downloadCompletion() {
            }

            @Override
            public void downloadFail() {
                if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                    try {
                        Toast.makeText(getApplicationContext(), "系统初始化失败:下载失败！", Toast.LENGTH_SHORT).show();
                        mDlgSingerInit.dismiss();
                        mDlgSingerInit = null;
                    } catch (Exception ex) {
                        Logger.w("VODService", "downloadFail ex:" + ex.toString());
                    }
                }
            }

            @Override
            public void upzipStart() {
                if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                    Main.mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDlgSingerInit.setTip("正在解压必要文件，请勿关机...");
                            } catch (Exception ex) {
                                Logger.w("VODService", "upzipStart1 ex:" + ex.toString());
                            }
                        }
                    });
                } else {
                    Main.mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDlgSingerInit = new DlgProgress(Main.mMainActivity);
                                mDlgSingerInit.setTitle("系统初始化");
                                mDlgSingerInit.setTip("正在解压必要文件，请勿关机...");
                                mDlgSingerInit.show();
                            } catch (Exception ex) {
                                Logger.w("VODService", "upzipStart2 ex:" + ex.toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void upzipProgress(final long progress, final long total) {
                if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                    try {
                        Main.mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDlgSingerInit.setProgress(progress, total);
                            }
                        });
                    } catch (Exception ex) {
                        Logger.w("VODService", "upzipProgress ex:" + ex.toString());
                    }
                }
            }

            @Override
            public void upzipCompletion() {
                if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                    Main.mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDlgSingerInit.dismiss();
                                mDlgSingerInit = null;
                                Toast.makeText(getApplicationContext(), "系统初始化成功！", Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                Logger.w("VODService", "upzipCompletion ex:" + ex.toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void upzipFail(final String msg) {
                if (mDlgSingerInit != null && mDlgSingerInit.isShowing()) {
                    Main.mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDlgSingerInit.dismiss();
                                mDlgSingerInit = null;
                            } catch (Exception ex) {
                                Logger.w("VODService", "upzipFail ex:" + ex.toString());
                            }

                        }
                    });
                }
            }
        });
        sdSingerImageInit.init();
    }
}
