package com.beidousat.minidoor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.AdDoorMini;
import com.beidousat.libbns.net.NetChecker;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends FragmentActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        HttpRequestListener, SurfaceHolder.Callback, View.OnClickListener, View.OnLongClickListener {

    private final static int SYS_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private SurfaceView mSurfaceView;
    private RecyclerImageView mIvAd;

    private boolean mIsInitAfterNetAvail;
    private DlgInitLoading mDlgInitLoading;
    private MediaPlayer mMediaPlayer;

    private boolean mSurfaceChanged = false;

    private ScheduledExecutorService mScheduledExecutorService;

    private int mVideoPosition = 0;
    private int mImgPosition = 0;

    private Ad mPlayingAd;
    private AdDoorMini mAdDoorMini;

    private int mLogoHits = 0;

    public final static String PREF_KEY_BOXID = "pref_key_boxid";

    private final static String TAG = "Main";

    private BnsDbHelper mBnsDbHelper;

    /**
     * 硬盘可用空间小于此值，进行本地文件清理
     */
    private final static long MIN_SPACE = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI(true);
        setContentView(R.layout.act_main);
        initView();

        mBnsDbHelper = new BnsDbHelper(this);

        checkNetwork();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mIvAd = (RecyclerImageView) findViewById(R.id.riv_ad);

        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceView.setOnClickListener(this);
        mIvAd.setOnClickListener(this);

        mSurfaceView.setOnLongClickListener(this);
        mIvAd.setOnLongClickListener(this);

        if (TextUtils.isEmpty(PreferenceUtil.getString(getApplicationContext(), PREF_KEY_BOXID))) {
            DlgBoxIdInput dlgBoxIdInput = new DlgBoxIdInput(this);
            dlgBoxIdInput.show();
        }
    }

    private DlgBoxIdInput mDlgBoxIdInput;

    private void showBoxIdInput() {
        mDlgBoxIdInput = new DlgBoxIdInput(this);
        mDlgBoxIdInput.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        mDlgBoxIdInput.show();
    }

    @Override
    public boolean onLongClick(View view) {
        DlgBoxIdInput dlgBoxIdInput = new DlgBoxIdInput(this);
        dlgBoxIdInput.show();
        return false;
    }

    @Override
    public void onClick(View view) {
        if (mLogoHits == 0) {
            mSurfaceView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLogoHits = 0;
                }
            }, 3000);
        }
        mLogoHits++;
        if (mLogoHits >= 5) {
            toSetting();
        }
    }

    private void initAfterNetAvail() {

        Intent intent = new Intent(getApplicationContext(), MiniDoorService.class);
        startService(intent);

        showInitDialog(getString(R.string.connecting_server));

        requestAd();
    }

    private void hideSystemUI(boolean hide) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", hide);
        sendBroadcast(intent);
        if (hide)
            getWindow().getDecorView().setSystemUiVisibility(SYS_UI);
    }


    private void checkNetwork() {
        showInitDialog(getString(R.string.checking_network));
        NetChecker.getInstance(getApplicationContext()).setOnNetworkStatusListener(new NetChecker.OnNetworkStatusListener() {
            @Override
            public void onNetworkStatus(boolean status) {
                Logger.d(TAG, "checkNetwork onNetworkStatus:" + status);
                if (status) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsInitAfterNetAvail) {
                                mIsInitAfterNetAvail = true;
                                initAfterNetAvail();
                            }
                        }
                    });
                }
            }
        }).check();
    }


    private void showInitDialog(String text) {
        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
            mDlgInitLoading = new DlgInitLoading(this);
            mDlgInitLoading.setMessage(text);
            mDlgInitLoading.setPositiveButton(getString(R.string.setting_network), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toSetting();
                }
            });
            mDlgInitLoading.show();
        } else {
            mDlgInitLoading.setMessage(text);
        }
    }

    private void dismissInitLoading() {
        if (mDlgInitLoading != null && mDlgInitLoading.isShowing()) {
            mDlgInitLoading.dismiss();
        }
    }

    private void toSetting() {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        startActivity(intent);
        hideSystemUI(false);
        finish();
    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    private void playVideo(Ad ad) {
        mPlayingAd = ad;
        mIvAd.setVisibility(View.GONE);

        String fileName = ServerFileUtil.getFileName(ad.ADContent);
        File file = new File(KaraokeSdHelper.getAdDoorMiniDir(), fileName);
        Cursor cursor = null;
        try {
            cursor = mBnsDbHelper.getByFilePath(file.getAbsolutePath());
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String filePath = cursor.getString(1);
                    int times = cursor.getInt(2);
                    mBnsDbHelper.update(String.valueOf(id), filePath, times + 1);
                }
            } else {
                mBnsDbHelper.insert(file.getAbsolutePath(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (file.exists() && file.length() > 0) {//本地文件
            Logger.i(TAG, "play url:" + file.getAbsolutePath());
            try {
                releasePlayer();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setDataSource(file.getAbsolutePath());
                mMediaPlayer.setDisplay(mSurfaceView.getHolder());
                mMediaPlayer.prepareAsync();
            } catch (Exception ex) {
                Logger.w(TAG, "Exception :" + ex.toString());
            }
        } else {
            checkSdUsable();//检查SD卡可用空间

            String url = ServerFileUtil.getFileUrl(ad.ADContent);
            VideoDownloader.getInstance().addDownloadUrl(url);
            Logger.i(TAG, "play url:" + url);
            try {
                releasePlayer();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setDisplay(mSurfaceView.getHolder());
                mMediaPlayer.prepareAsync();
            } catch (Exception ex) {
                Logger.w(TAG, "Exception :" + ex.toString());
            }
        }
    }

    private void playImage() {
        if (mAdDoorMini != null) {
            try {
                mIvAd.setVisibility(View.VISIBLE);
                releasePlayer();
                String imgUrl = mImgPosition % 2 == 0 ? mAdDoorMini.Logo : mAdDoorMini.Img;
                mImgPosition++;

                Glide.with(this).load(ServerFileUtil.getImageUrl(imgUrl)).into(mIvAd);
                mIvAd.removeCallbacks(runnable);
                mIvAd.postDelayed(runnable, 30 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mIvAd.removeCallbacks(runnable);
        super.onDestroy();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mImgPosition % 2 == 1) {
                playImage();
            } else {
                if (mAdDoorMini != null && mAdDoorMini.List != null && mAdDoorMini.List.size() > 0) {
                    mVideoPosition = 0;
                    playVideo(mAdDoorMini.List.get(mVideoPosition));
                } else {
                    requestAd();
                }
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopTimer();
        mVideoPosition++;
        if (mAdDoorMini != null && mAdDoorMini.List != null && mAdDoorMini.List.size() > mVideoPosition) {
            playVideo(mAdDoorMini.List.get(mVideoPosition));
        } else {
            requestAd();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Logger.w(TAG, "MediaPlayer onError i:" + i + " i1:" + i1);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.start();
            startTimer();
        } catch (Exception e) {
            Logger.w(TAG, "onPrepared ex:" + e.toString());
        }
    }

    private void requestAd() {
        mVideoPosition = 0;
        mImgPosition = 0;

        HttpRequest httpRequest = initRequest(RequestMethod.GET_AD_DOOR_MINI);
        httpRequest.addParam("ADPosition", "K1");
        httpRequest.addParam("RoomCode", PreferenceUtil.getString(getApplicationContext(), PREF_KEY_BOXID));
        httpRequest.setConvert2Class(AdDoorMini.class);
        httpRequest.doPost(0);
    }


    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStart(String method) {

    }


    @Override
    public void onSuccess(String method, Object object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissInitLoading();
            }
        });

        if (RequestMethod.GET_AD_DOOR_MINI.equalsIgnoreCase(method)) {
            mAdDoorMini = (AdDoorMini) object;
            if (mAdDoorMini != null) {
                playImage();
            } else {
                mIvAd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestAd();
                    }
                }, 3000);
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {
        if (RequestMethod.GET_AD_DOOR_MINI.equalsIgnoreCase(method)) {
            mIvAd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestAd();
                }
            }, 3000);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceChanged = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceChanged = false;
    }


    private void startTimer() {
        if ((mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown()))
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);

    }

    private void stopTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mMediaPlayer != null && mPlayingAd != null && mMediaPlayer.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AdBillHelper.getInstance(getApplicationContext()).billAd(mPlayingAd.ID, "K1", PreferenceUtil.getString(getApplicationContext(), PREF_KEY_BOXID));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }


    private synchronized void checkSdUsable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                try {
                    long usableSpace = KaraokeSdHelper.getSdUsableSpace(getApplicationContext()) / (1024 * 1024);
                    Logger.d(TAG, "checkSdUsable usableSpace :" + usableSpace);
                    if (usableSpace < MIN_SPACE) {
                        Logger.d(TAG, "checkSdUsable usableSpace is small !");
                        cursor = mBnsDbHelper.getAllTimeAsc();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                Logger.d(TAG, "checkSdUsable cursor is not null:");
                                String filePath = cursor.getString(1);
                                int times = cursor.getInt(2);
                                Logger.d(TAG, "checkSdUsable filePath:" + filePath + " times:" + times);
                                try {
                                    usableSpace = KaraokeSdHelper.getSdUsableSpace(getApplicationContext()) / (1024 * 1024);
                                    if (usableSpace < MIN_SPACE) {
                                        File file = new File(filePath);
                                        if (file.exists()) {//删除缓存文件
                                            boolean ret = file.delete();
                                            Logger.d(TAG, "checkSdUsable delete file:" + file.getAbsolutePath() + " ret:" + ret);
                                        }
                                        mBnsDbHelper.delete(filePath);
                                    } else {
                                        return;
                                    }
                                } catch (Exception e) {
                                    Logger.d(TAG, "checkSdUsable Exception :" + e.toString());
                                }
                            }
                        } else {
                            Logger.d(TAG, "checkSdUsable cursor is null !");
                        }
                    } else {
                        Logger.d(TAG, "checkSdUsable space is big !");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d(TAG, "checkSdUsable Exception e2:" + e.toString());

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).start();
    }
}
