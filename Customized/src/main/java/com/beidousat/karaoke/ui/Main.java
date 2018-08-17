package com.beidousat.karaoke.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.LanService;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.ad.AdDefault;
import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.ServerConfigHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.PublicSong;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PlayerStatus;
import com.beidousat.karaoke.model.ServerConfig;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.karaoke.player.OriginPlayer;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.DeviceStore;
import com.beidousat.karaoke.ui.dlg.DlgAdScreen;
import com.beidousat.karaoke.ui.dlg.DlgGuide;
import com.beidousat.karaoke.ui.dlg.DlgInitLoading;
import com.beidousat.karaoke.ui.dlg.DlgProgress;
import com.beidousat.karaoke.ui.dlg.DlgTune;
import com.beidousat.karaoke.ui.dlg.FinishDialog;
import com.beidousat.karaoke.ui.dlg.FmDownloader;
import com.beidousat.karaoke.ui.dlg.FmPayMeal;
import com.beidousat.karaoke.ui.dlg.MngPwdDialog;
import com.beidousat.karaoke.ui.dlg.PopVersionInfo;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.ui.dlg.StepDialog;
import com.beidousat.karaoke.ui.fragment.FmChooseList;
import com.beidousat.karaoke.ui.fragment.FmMain;
import com.beidousat.karaoke.ui.fragment.FmSearch;
import com.beidousat.karaoke.ui.fragment.FmSetting;
import com.beidousat.karaoke.ui.fragment.FmSettingSerail;
import com.beidousat.karaoke.ui.fragment.FmShows;
import com.beidousat.karaoke.ui.presentation.PlayerPresentation;
import com.beidousat.karaoke.util.AnimatorUtils;
import com.beidousat.karaoke.util.ChooseSongTimer;
import com.beidousat.karaoke.util.DiskFileUtil;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.karaoke.util.UsbFileUtil;
import com.beidousat.karaoke.widget.ChooseSongTipView;
import com.beidousat.karaoke.widget.MarqueePlayer;
import com.beidousat.karaoke.widget.MealInfoTextView;
import com.beidousat.karaoke.widget.PauseTipView;
import com.beidousat.karaoke.widget.UserInfoLayout;
import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.DownloadBusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.FragmentModel;
import com.beidousat.libbns.model.KBoxStatus;
import com.beidousat.libbns.net.NetChecker;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PackageUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libserial.SerialHelper;
import com.beidousat.score.KeyInfo;
import com.beidousat.score.NoteInfo;
import com.beidousat.score.OnKeyInfoListener;
import com.beidousat.score.OnScoreListener;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class Main extends BaseActivity implements View.OnClickListener,
        BeidouPlayerListener, OnKeyInfoListener, OnScoreListener, SupportQueryOrder, View.OnLongClickListener {

    private static final String TAG = "Main";

    private LinkedList<FragmentModel> mFragments = new LinkedList<FragmentModel>();
    private FragmentManager mFragmentManager;
    private Button mBtnBack;
    private TextView mTvChooseCount, mTvCurrentSong;
    private TextView mTvPlayerPause, mTvPlayerOriAcc, mTvScore;
    private UserInfoLayout mUserInfoLayout;
    private ToggleButton mTgScore;
    private MealInfoTextView mTvBuy;
    private TextView mTvProgress;
    private View mRoot;

    private KaraokeController mKaraokeController;
    private DisplayManager mDisplayManager;
    private AdBillHelper mAdBillHelper;

    private MarqueePlayer mMarqueePlayer;
    private String mCurPastAdPosition;

    private Song mPlayingSong;
    private int mAudioChannelFlag = 4;
    private Ad mAdVideo;
    private final static int TIMER_INTERVAL = 1000;
    private long mPasterBillProgress;
    private int mSongScore;
    private int mScorePercent;
    private Song mScoreSong;

    private final SparseArray<PlayerPresentation> mActivePresentations = new SparseArray<PlayerPresentation>();
    private PlayerPresentation mPresentation;
    private OriginPlayer player;
    private float mVolPercent;

    private View mControlBar;
    private PauseTipView mPauseTipView;
    private ChooseSongTipView mChooseSongTipView;

    private FrameLayout.LayoutParams mPauseTipViewParams;
    private FrameLayout.LayoutParams mChooseSongViewParams;

    private AudioManager mAudioManager;

    public QueryOrderHelper mQureyHelper;

    public static FragmentActivity mMainActivity;

    private View mViewTop, mViewBottom;

    private Ad mCurScreenAd;
    private DlgAdScreen mDlgAdScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        mMainActivity = this;
        initView();
        init();

        EventBus.getDefault().register(this);

        hideSystemUI(false);

        checkNetwork();

    }

    private void initAfterNetAvail() {
        ServerConfigHelper serverConfigHelper = ServerConfigHelper.getInstance(getApplicationContext());
        serverConfigHelper.setOnServerCallback(new ServerConfigHelper.OnServerCallback() {
            @Override
            public void onSererCallback(ServerConfig config) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startMainPlayer();
                        mMarqueePlayer.loadAds("Z1");
                        mMarqueePlayer.startPlayer();
                        startService(new Intent(getApplicationContext(), LanService.class));
                        checkDeviceStore();
                        dismissInitLoading();
                        restoreUserInfo();
                    }
                });
            }
        });
        serverConfigHelper.getConfig();
        showInitDialog(getString(R.string.getting_config));
        // LoadingUtil.showLoadingDialog(this, getString(R.string.getting_config));
    }

    private boolean mIsInitAfterNetAvail = false;

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

    private DlgInitLoading mDlgInitLoading;

    private void showInitDialog(String text) {
        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
            mDlgInitLoading = new DlgInitLoading(this);
            mDlgInitLoading.setMessage(text);
            mDlgInitLoading.setPositiveButton(getString(R.string.setting_network), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MngPwdDialog dialog = new MngPwdDialog(Main.this);
                    dialog.setOnMngPwdListener(new MngPwdDialog.OnMngPwdListener() {
                        @Override
                        public void onPass() {
                            toSetting();
                        }
                    });
                    dialog.show();
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
        hideSystemUI(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }


    @Override
    protected void onDestroy() {
        unregisterUsbReceiver();
        stopService(new Intent(getApplicationContext(), LanService.class));

        BoughtMeal.getInstance().deleteObservers();
        PayUserInfo.getInstance().deleteObservers();

        mMarqueePlayer.stopPlayer();
        stopMainPlayer();
        EventBus.getDefault().unregister(this);
        mMainActivity = null;
        System.exit(0);//直接结束程序
        super.onDestroy();
    }


    private void initView() {
        mRoot = findViewById(R.id.act_main);
        mViewTop = findViewById(R.id.rl_top);
        mViewBottom = findViewById(R.id.rl_bottom);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
        mTvCurrentSong = (TextView) findViewById(R.id.tv_playing);
        mTvPlayerPause = (TextView) findViewById(R.id.tv_pause);
        mTvPlayerOriAcc = (TextView) findViewById(R.id.tv_acc);
        mTgScore = (ToggleButton) findViewById(R.id.tg_score);
        mTgScore.setOnClickListener(this);
        mTvScore = (TextView) findViewById(R.id.tv_score);
        mTvScore.setOnClickListener(this);
        mTvScore.setSelected(true);
        mMarqueePlayer = (MarqueePlayer) findViewById(R.id.ads_marquee);
        mControlBar = findViewById(R.id.control_bar);
        mTvBuy = (MealInfoTextView) findViewById(R.id.tv_buy);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);

        mUserInfoLayout = (UserInfoLayout) findViewById(R.id.ll_user);
        findViewById(R.id.iv_logo).setOnLongClickListener(this);
        findViewById(R.id.iv_custom_logo).setOnLongClickListener(this);

        initPauseTipView();
        initChooseSongView();
    }

    private void init() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mDisplayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        mAdBillHelper = AdBillHelper.getInstance(getApplicationContext());
        mQureyHelper = new QueryOrderHelper(this);

        mFragmentManager = getSupportFragmentManager();
        traFragment(new FragmentModel(new FmMain()), true);

        mKaraokeController = ((LanApp) getApplicationContext()).mKaraokeController;

//        initSystemVol();

        initMealInfo();

        registerUsbReceiver();

        try {
            int baudrate = Integer.valueOf(PrefData.getSerilBaudrate(getApplicationContext()));
//            SerialController.getInstance(getApplicationContext()).open(baudrate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTvChooseCount.setText(String.valueOf(ChooseSongs.getInstance(getApplicationContext()).getChooseSize()));
    }

    private void checkMic() {
        Meal meal = BoughtMeal.getInstance().getTheFirstMeal();
        if (meal == null) {
            //未购买套餐
            Logger.d(TAG, "购买套餐:未购");
            mKaraokeController.setMicMute(true);
        } else {
            Logger.d(TAG, "购买套餐:已购");
            // mKaraokeController.setMicMute(false);
        }
        mKaraokeController.readMicVol(2000);

        mKaraokeController.readEffVol(4000);
    }

    private void initSystemVol() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int vol = maxVolume / 2;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
    }

    private void initMealInfo() {
        BoughtMeal.getInstance().addObserver(mTvBuy);
        PayUserInfo.getInstance().addObserver(mUserInfoLayout);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.id.ADD_FRAGMENT:
                FragmentModel fragment = (FragmentModel) event.data;
                traFragment(fragment, false);
                break;

            case EventBusId.id.PLAYER_PLAY_SONG:
                Song songInfo = (Song) event.data;
                playSong(songInfo);
                break;
            case EventBusId.id.CHOOSE_SONG_CHANGED:
                int count = Integer.valueOf(event.data.toString());
                mTvChooseCount.setText(String.valueOf(count));
                updatePlayingText();
                break;
            case EventBusId.id.PLAYER_NEXT:
                if (player != null) {
                    mKaraokeController.getPlayerStatus().isMute = false;
                    closePauseTipView();
                    next();
                    if (mPresentation != null)
                        mPresentation.tipOperation(R.drawable.tv_next, R.string.switch_song, true);
                }
                break;
            case EventBusId.id.PLAYER_STATUS_CHANGED:
                PlayerStatus status = (PlayerStatus) event.data;
                updatePlayerStatus(status);
                break;

            case EventBusId.id.PLAYER_PLAY:
                if (player != null) {
                    play();
                    if (mPresentation != null) {
                        mPresentation.tipOperation(R.drawable.tv_play, R.string.play, true);
                        mPresentation.showPauseAd(false, false);
                    }
                }
                break;

            case EventBusId.id.PLAYER_PAUSE2:
                pause();
                if (mPresentation != null) {
                    mPresentation.tipOperation(R.drawable.tv_pause, R.string.pause, true);
                    mPresentation.showPauseAd(true, mKaraokeController.getPlayerStatus().playingType == 2);
                }
                break;

            case EventBusId.id.PLAYER_REPLAY:
                replay();
                if (mPresentation != null) {
                    mPresentation.cleanScreen();
                    mPresentation.tipOperation(R.drawable.tv_replay, R.string.replay, true);
                }
                mTvPlayerPause.removeCallbacks(runShowScoreResult);
                break;
            case EventBusId.id.PLAYER_ORIGINAL:
                onOriginal(true);
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_original_on, R.string.original, true);
                break;
            case EventBusId.id.PLAYER_ACCOM:
                onAccom(true);
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_original_off, R.string.accompany, true);
                break;
            case EventBusId.id.PLAYER_SCORE_ON_OFF:
                updateScoreViews(true);
                break;

            case EventBusId.id.TONE_DEFAULT:
                setTone(Integer.valueOf(event.data.toString()));
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_tone_default, R.string.tone_default, true);
                break;
            case EventBusId.id.TONE_UP:
                setTone(Integer.valueOf(event.data.toString()));
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_tone_up, R.string.tone_up, true);
                break;
            case EventBusId.id.TONE_DOWN:
                setTone(Integer.valueOf(event.data.toString()));
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_tone_down, R.string.tone_down, true);
                break;

            case EventBusId.id.VOL_DOWN:
                if (mPresentation != null)
                    mPresentation.showMusicVol();
                break;
            case EventBusId.id.VOL_UP:
                if (mPresentation != null)
                    mPresentation.showMusicVol();
                break;

            case EventBusId.id.MIC_UP:
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_mic_up, getString(R.string.vol_mic), true);
                break;

            case EventBusId.id.MIC_DOWN:
                if (mPresentation != null)
                    mPresentation.tipOperation(R.drawable.tv_mic_down, getString(R.string.vol_mic), true);
                break;

            case EventBusId.id.SERIAL_REVERB_DOWN:
                break;

            case EventBusId.id.SERIAL_REVERB_UP:
                break;

            case EventBusId.SERIAL.SERIAL_EFF_VOL:
                int effVol = Integer.valueOf(event.data.toString());
                Logger.d(TAG, "SERIAL_EFF_VOL :" + effVol);
                mKaraokeController.getPlayerStatus().effVol = effVol;
                break;

            case EventBusId.SERIAL.SERIAL_MIC_VOL:
                int micVol = Integer.valueOf(event.data.toString());
                Logger.d(TAG, "SERIAL_MIC_VOL :" + micVol);
                mKaraokeController.getPlayerStatus().micVol = micVol;
                break;

            case EventBusId.id.PLAYER_VOL_OFF:
                volOff();
                break;
            case EventBusId.id.PLAYER_VOL_ON:
                volOn();
                break;
            case EventBusId.id.PAY_SUCCEED:
                boolean isExpire = true;
                try {
                    isExpire = Boolean.valueOf(event.data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Logger.d(TAG, "EventBusId  id PAY_SUCCEED isExpire:" + isExpire);

                //查询用户信息
                Meal meal = BoughtMeal.getInstance().getTheLastMeal();
                if (meal != null) {
                    mQureyHelper.queryUser().post();
                    if (isExpire) {//
                        mKaraokeController.setMicMute(false);//mic静音
                        showGuideDialog();//使用帮助指引
                    }
                    startChooseSongTimer();
                }
                break;
            case EventBusId.id.MEAL_EXPIRE:
                CommonDialog commonDialog = CommonDialog.getInstance();
                if (commonDialog.isAdded()) {
                    commonDialog.dismiss();
                }
                FinishDialog finishDialog = new FinishDialog();
                if (!finishDialog.isAdded()) {
                    finishDialog.show(getSupportFragmentManager(), "share");
                }
                break;
            case EventBusId.id.ROOM_CLOSE:
                //TODO 停止播放
                BoughtMeal.getInstance().clearMealInfo();
                PayUserInfo.getInstance().removeAllUser();
                //清空已点
                ChooseSongs.getInstance(getApplicationContext()).cleanChoose();
                //清空已已唱
                ChooseSongs.getInstance(getApplicationContext()).cleanSung();
                //删除录音文件
                AudioRecordFileUtil.deleteRecordFiles();
                //切歌到广告
                next();

                mKaraokeController.setMicMute(true);

                break;
            case EventBusId.Download.START:
                performDownloadStart((DownloadBusEvent) event);
                break;
            case EventBusId.Download.FINISH:
                performDownloadFinish((DownloadBusEvent) event);
                break;
            case EventBusId.Download.PROGRESS:
                performDownloadUpdate((DownloadBusEvent) event);
                break;

            case EventBusId.id.BACK_FRAGMENT:
                onBackPressed();
                break;

            case EventBusId.id.REQUEST_MEAL:
                KBoxInfo.getInstance().setKBox(null);
                new QueryKboxHelper(getApplicationContext(), null).getBoxInfo();
                break;

            case EventBusId.SOCKET.KBOX_STATUS_CHECKING:
                if (!mIsSetting && KBoxInfo.getInstance().getKBox() == null) {
//                    LoadingUtil.showLoadingDialog(this, getString(R.string.init_system));
                    showInitDialog(getString(R.string.device_auth));
                }
                break;

            case EventBusId.id.CURRENT_SCREEN_AD:
                mCurScreenAd = (Ad) event.data;
                if (mDlgAdScreen != null && mDlgAdScreen.isVisible() && mCurScreenAd != null && !TextUtils.isEmpty(mCurScreenAd.ADContent)) {
                    mDlgAdScreen.showAdScreen(mCurScreenAd);
                }
                break;

            case EventBusId.SOCKET.KBOX_STATUS:
                KBoxStatus kBoxStatus = (KBoxStatus) event.data;
                KBoxStatusInfo.getInstance().setKBoxStatus(kBoxStatus);
                dismissInitLoading();
                if (kBoxStatus.status != 1) {//授权未通过
                    if (!mIsSetting && (mDlgPass == null || !mDlgPass.isShowing())
                            && (mDlgSetBoxId == null || !mDlgSetBoxId.isShowing()) && mDialogAuth == null) {
                        mDialogAuth = new PromptDialog(Main.mMainActivity);
                        if (kBoxStatus.code == 3001 || kBoxStatus.code == 2001) {
                            mDialogAuth.setPositiveButton(getString(R.string.setting), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showMngPass(0);
                                }
                            });
                        }
                        mDialogAuth.setCanceledOnTouchOutside(false);
                        mDialogAuth.setMessage(kBoxStatus.msg);
                        mDialogAuth.show();
                    }
                }
                break;
        }
    }

    private PromptDialog mDialogAuth;
    private DlgTune dlgTune;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.iv_choose:
            case R.id.tv_choose_count:
                FragmentUtil.addFragment(FmChooseList.newInstance(0));
                break;
            case R.id.iv_search:
                FragmentUtil.addFragment(new FmSearch());
                break;
            case R.id.tv_next:
                mKaraokeController.next();
                break;
            case R.id.tv_acc:
                mKaraokeController.originalAccom();
                updateOriAccStatus(mKaraokeController.getPlayerStatus());
                break;
            case R.id.tv_pause:
                if (mPauseTipView.canPause()) {
                    mKaraokeController.playPause();
                }
                break;
            case R.id.tv_replay:
                mKaraokeController.replay();
                break;
            case R.id.tv_share:
                FragmentUtil.addFragment(FmChooseList.newInstance(1));
                break;

            case R.id.tv_score:
            case R.id.tg_score:
                int scoreMode = mKaraokeController.getPlayerStatus().scoreMode;
                if (scoreMode == 0) {
                    mKaraokeController.setScoreMode(1);
                } else {
                    mKaraokeController.setScoreMode(0);
                }
                break;

            case R.id.tv_rule:
                StepDialog dlgStep = StepDialog.getInstance();
                if (!dlgStep.isAdded()) {
                    dlgStep.show(getSupportFragmentManager(), "step");
                }
                break;
            case R.id.tv_buy:
                CommonDialog dialog = CommonDialog.getInstance();
                dialog.setShowClose(true);
                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                dialog.setContent(FmPayMeal.createMealFragment(pageType));
                if (!dialog.isAdded()) {
                    dialog.show(getSupportFragmentManager(), "commonDialog");
                }
                break;

            case R.id.tv_tune:
                dlgTune = new DlgTune(this);
                dlgTune.setOnTuneListener(new DlgTune.OnTuneListener() {
                    @Override
                    public void onMicDown() {
                        if (BoughtMeal.getInstance().getTheFirstMeal() == null) {
                            tipBuyMessage(R.string.tip_pay);
                        } else {
                            mKaraokeController.micVolDown();
                            mKaraokeController.readMicVol(200);
                        }
                    }

                    @Override
                    public void onMicUp() {
                        if (BoughtMeal.getInstance().getTheFirstMeal() == null) {
                            tipBuyMessage(R.string.tip_pay);
                        } else {
                            mKaraokeController.micVolUp();
                            mKaraokeController.readMicVol(200);
                        }
                    }

                    @Override
                    public void onMusicDown() {
                        mKaraokeController.musicVolDown();
                        dlgTune.setCurrentMusicVol(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    }

                    @Override
                    public void onMusicUp() {
                        mKaraokeController.musicVolUp();
                        dlgTune.setCurrentMusicVol(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    }

                    @Override
                    public void onToneDown() {
                        mKaraokeController.toneDown();
                        dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
                    }

                    @Override
                    public void onToneUp() {
                        mKaraokeController.toneUp();
                        dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
                    }

                    @Override
                    public void onReset() {
                        if (BoughtMeal.getInstance().getTheFirstMeal() == null) {
                            tipBuyMessage(R.string.tip_pay);
                        } else {
                            initSystemVol();
                            mKaraokeController.reset();
                            mKaraokeController.toneDefault();
                            dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
                            dlgTune.setCurrentMusicVol(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                        }
                    }

                    @Override
                    public void onReverbDown() {
                        mKaraokeController.reverbDown();
                        mKaraokeController.readEffVol(200);
                    }

                    @Override
                    public void onReverbUp() {
                        mKaraokeController.reverbUp();
                        mKaraokeController.readEffVol(200);
                    }
                });
                dlgTune.setCurrentMusicVol(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
                dlgTune.setCurrentEff(mKaraokeController.getPlayerStatus().effVol);
                dlgTune.setCurrentMic(mKaraokeController.getPlayerStatus().micVol);
                dlgTune.show();
                break;

            case R.id.iv_logo:
                if (mLogoHits == 0) {
                    mTvPlayerPause.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLogoHits = 0;
                        }
                    }, 3000);
                }
                mLogoHits++;
                if (mLogoHits >= 5) {
                    showMngPass(-1);
                }
                break;
            case R.id.iv_custom_logo:
                openCustomApp();
                break;
            case R.id.tv_progress:
                CommonDialog dlgProgress = CommonDialog.getInstance();
                dlgProgress.setShowClose(true);
                dlgProgress.setContent(new FmDownloader());
                if (!dlgProgress.isAdded()) {
                    dlgProgress.show(getSupportFragmentManager(), "commonDialog");
                }
                break;
        }
    }

    private int mLogoHits = 0;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            return;
        }
        super.onBackPressed();
        try {
            mFragments.remove(mFragments.size() - 1);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            FragmentModel fragmentModel = mFragments.get(mFragments.size() - 1);
            transaction.show(fragmentModel.fragment).commit();
            mBtnBack.setVisibility(mFragments.size() > 1 ? View.VISIBLE : View.GONE);
            checkFragment(fragmentModel);
        } catch (Exception e) {
            Logger.w(TAG, "onBackPressed ex:" + e.toString());
        }

        if (!checkMyStackBackStack()) {//出现重影了
            Logger.w(TAG, "onBackPressed checkMyStackBackStack false:");
            traFragment(new FragmentModel(new FmMain()), true);
        }
    }

    private synchronized void traFragment(FragmentModel targetFragment, boolean cleanStacks) {
        if (mFragments != null && mFragments.size() > 0 && mFragments.get(mFragments.size() - 1).tag.equals(targetFragment.tag)) {
            return;
        }
        if (cleanStacks) {
            cleanFragmentStacks();
            mFragments.clear();
            System.gc();
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (int i = mFragments.size() - 1; i > -1; i--) {
            FragmentModel fgm = mFragments.get(i);
            if (targetFragment.tag.equals(fgm.tag)) {
                transaction.remove(fgm.fragment);
                mFragments.remove(i);
            } else {
                transaction.hide(fgm.fragment);
            }
        }
        transaction.commit();
        mFragmentManager.beginTransaction().add(R.id.contentPanel, targetFragment.fragment, targetFragment.tag).addToBackStack("main_frag").commit();
        mFragments.add(targetFragment);

        mBtnBack.setVisibility(mFragments.size() > 1 ? View.VISIBLE : View.GONE);

        checkFragment(targetFragment);

    }


    private void cleanFragmentStacks() {
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
            mFragmentManager.popBackStack();
        }
    }

    private boolean checkMyStackBackStack() {
        try {
            return getSupportFragmentManager().getBackStackEntryCount() == mFragments.size();
        } catch (Exception e) {
            Logger.d(TAG, "checkMyStackBackStack e:" + e.toString());
        }
        return false;
    }


    private void updatePlayingText() {
        List<Song> songs = ChooseSongs.getInstance(getApplicationContext()).getSongs();
        Song currentSong = null;
        Song nextSong = ChooseSongs.getInstance(getApplicationContext()).getSecSong();
        if (songs != null && songs.size() > 0) {
            Song song0 = songs.get(0);
            currentSong = song0;
            mTvCurrentSong.setText(getString(R.string.playing_x, song0.SimpName));
        } else {
            mTvCurrentSong.setText(getString(R.string.playing_x, getString(R.string.ad)));
        }

        setTextMarquee(mTvCurrentSong);

        if (mPresentation != null)
            mPresentation.setNextSong(currentSong, nextSong);

        boolean isPlaying = mKaraokeController.getPlayerStatus().isPlaying;
        if (isPlaying) {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_pause, 0, 0);
        } else {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_play, 0, 0);
        }
        mTvPlayerPause.setText(isPlaying ? R.string.pause : R.string.play);

        startChooseSongTimer();

    }

    private void updateOriAccStatus(PlayerStatus playerStatus) {
        if (playerStatus.originOn) {
            mTvPlayerOriAcc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_original_on, 0, 0);
            mTvPlayerOriAcc.setText(R.string.accompany);
        } else {
            mTvPlayerOriAcc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_original_off, 0, 0);
            mTvPlayerOriAcc.setText(R.string.original);
        }
    }

    /**
     * 切歌
     **/
    private void next() {
        if (mPresentation != null)
            mPresentation.cleanScreen();
        mTvPlayerPause.removeCallbacks(runShowScoreResult);
        ChooseSongs chooseSongs = ChooseSongs.getInstance(getApplicationContext());
        if (chooseSongs.getChooseSize() > 0) {
            ChooseSongs.getInstance(getApplicationContext()).add2Sungs(chooseSongs.getFirstSong());
            ChooseSongs.getInstance(getApplicationContext()).remove(0);
        }
        if (BoughtMeal.getInstance().getTheFirstMeal() != null) {
            if (BoughtMeal.getInstance().isBuySong()) {
                BoughtMeal.getInstance().checkLeftSong();
            }
            if (chooseSongs.getChooseSize() > 0 && BoughtMeal.getInstance().isBuySong() && BoughtMeal.getInstance().getLeftSongs() > 0) {//包曲
                playSong(chooseSongs.getFirstSong());
            } else if (chooseSongs.getChooseSize() > 0 && BoughtMeal.getInstance().isBuyTime() && BoughtMeal.getInstance().getLeftMillSeconds() > 0) {//包时
                playSong(chooseSongs.getFirstSong());
            } else {
                playVideoAdRandom();
            }
        } else {
            playVideoAdRandom();
        }
    }


    private void playSong(Song song) {
        Logger.d(TAG, "playSong playSong playSong playSong" + song.SongFilePath);
        try {
            //重置暂停次数
            mPauseTipView.resetLeftTimes();
            mPlayingSong = song;
            mAudioChannelFlag = song.AudioTrack;
            mKaraokeController.getPlayerStatus().playingType = 1;
            float vol = song.Volume > 0 ? ((float) song.Volume / 100) : 0.8f;
            playUrl(ServerFileUtil.getFileUrl(song.SongFilePath), vol);

            if (song.IsAdSong == 1 && !TextUtils.isEmpty(song.ADID)) {
                mAdBillHelper.billAd(song.ADID, "R1", PrefData.getRoomCode(getApplicationContext()));
            }
            //减掉一首
            BoughtMeal.getInstance().updateLeftSongs();
        } catch (Exception ex) {
            Logger.w(TAG, "playSong ex:" + ex.toString());
        }
    }


    private void playUrl(String url, float volPercent) {
        mVolPercent = volPercent;
        if (mPresentation != null)
            mPresentation.mAdCorner = null;
        if (player == null)
            return;
        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.id.PLAYER_PLAY_BEGIN));
        if (mPresentation != null)
            mPresentation.cleanScreen();
        if (player != null) {
            Song secSong = ChooseSongs.getInstance(getApplicationContext()).getSecSong();
            player.playUrl(url, mKaraokeController.getPlayerStatus().playingType == 1 ? mPlayingSong.RecordFile : null, secSong == null ? url : secSong.SongFilePath);
        }
        mKaraokeController.getPlayerStatus().isPlaying = true;
        if (mPresentation != null)
            mPresentation.tipOperation(0, 0, true);
    }


    private void startMainPlayer() {
        Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if (displays != null) {
            for (int i = 0; i < displays.length; i++) {
                final Display display = displays[i];
                if (display != null && display.isValid() && display.getName().toLowerCase().contains("hdmi")) {
                    long time = System.currentTimeMillis();
                    showPresentation(display);
                    Logger.d(TAG, "showPresentation use time:" + (System.currentTimeMillis() - time));
                    break;
                }
            }
            mDisplayManager.registerDisplayListener(mDisplayListener, null);
        }
        mTvPlayerPause.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer();
            }
        }, 3000);
    }

    private void stopMainPlayer() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (player != null) {
            player.stop();
            player = null;
        }
        mActivePresentations.clear();
    }

    private void showPresentation(Display display) {
        final int displayId = display.getDisplayId();
        if (mActivePresentations.get(displayId) != null) {
            return;
        }
        mPresentation = new PlayerPresentation(this, display);
        mPresentation.show();
        mPresentation.setOnDismissListener(mOnDismissListener);
        mActivePresentations.put(displayId, mPresentation);

    }

    private void initPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
        if (mPresentation != null) {
            int hdmiHeight = mPresentation.getHdmiHeight();
            int hdmiWidth = mPresentation.getHdmiWidth();

            player = new OriginPlayer(mPresentation.getSurfaceView(), Main.this, hdmiWidth, hdmiHeight);
            player.setOnKeyInfoListener(this);
            player.setOnScoreListener(this);
            Logger.i(TAG, "initPlayer");
            mTvPlayerPause.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ChooseSongs chooseSongs = ChooseSongs.getInstance(getApplicationContext());
                    if (chooseSongs.getChooseSize() > 0) {
                        playSong(chooseSongs.getFirstSong());
                    } else {
                        Logger.d(TAG, " play public  ad  initPlayer");
                        playVideoAdRandom();
                    }

                    int count = Integer.valueOf(chooseSongs.getChooseSize());
                    mTvChooseCount.setText(String.valueOf(count));
                    updatePlayingText();
                }
            }, 1000);
        }

    }


    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        public void onDisplayAdded(int displayId) {
            Toast.makeText(getApplicationContext(), "HDMI 已连接", Toast.LENGTH_SHORT).show();
            stopMainPlayer();
            startMainPlayer();
        }

        public void onDisplayChanged(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
            Toast.makeText(getApplicationContext(), "HDMI 已断开", Toast.LENGTH_SHORT).show();
        }
    };

    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        public void onDismiss(DialogInterface dialog) {
        }
    };


    @Override
    public void onPlayerCompletion() {
        if (mKaraokeController.getPlayerStatus().playingType == 1) {//歌曲播完
            if (mKaraokeController.getPlayerStatus().scoreMode != 0 && mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib)) {//播放转场
                if (mPresentation != null) {
                    mPresentation.showScoreView(0);
                    playScoreResult(mPlayingSong, mPresentation.getScore());
                } else {
                    next();
                }
            } else {
                next();
            }
        } else if (mKaraokeController.getPlayerStatus().playingType != 3) {
            next();
        }
    }

    @Override
    public void onPlayerProgress(final long progress, final long duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dealProgress(progress, duration);
            }
        });
    }

    @Override
    public void onPlayerPrepared() {
//        mTvPlayerPause.removeCallbacks(runAdRequest);
        if (mPresentation != null) {
            mPresentation.getWidgetScore().setVisualizerData(0);
        }

        mPasterBillProgress = 0;
        if (mKaraokeController.getPlayerStatus().playingType == 1 || mKaraokeController.getPlayerStatus().playingType == 2) {//歌曲、电影
            initCurrentMode();
        } else if (mKaraokeController.getPlayerStatus().playingType == 0) {//广告
            EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.id.PLAYER_ADS_BEIGIN));
        }
        updateScoreViews(false);

        initMute();

    }

    @Override
    public void onScoreCallback(final int score) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPresentation != null)
                    mPresentation.setScore(score);
            }
        });
    }

    @Override
    public void onUpdateTime(long msTime) {
//        if (mPresentation != null && mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib))
//            mPresentation.onCurrentTimeChange(msTime);
    }


    @Override
    public void onKeyInfoCallback(KeyInfo[] infos, int totalScore) {
//        if (mPresentation != null)
//            mPresentation.setKeyInfos(infos);
    }

    @Override
    public void onOriginNotes(ArrayList<NoteInfo> noteInfos) {
//        if (mPresentation != null && mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib))
//            mPresentation.setScoreNotes(noteInfos);
    }


    private void playVideoAdRandom() {
        mAdVideo = new Ad();
        mAdVideo.ADMovie = PublicSong.getAdVideo();
        mAdVideo.ADContent = PublicSong.getAdVideo();
        mAudioChannelFlag = 4;
        mKaraokeController.getPlayerStatus().playingType = 0;
        mPlayingSong = null;
        playUrl(ServerFileUtil.getFileUrl(mAdVideo.ADContent), 0.5f);
    }

    private Runnable runShowScoreResult = new Runnable() {
        @Override
        public void run() {
            if (mPresentation != null) {
                try {
                    mPresentation.showScoreResult(mScoreSong, mSongScore, (mScorePercent <= 0 ? 0 : mScorePercent) + "%");
                } catch (Exception e) {
                    Logger.w(TAG, "requestResult ex:" + e.toString());
                }
            }
        }
    };

    private void playScoreResult(final Song song, final int score) {
        try {
            Logger.d(TAG, "mark score :" + score);
            ChooseSongs.getInstance(getApplicationContext()).getFirstSong().score = score;
        } catch (Exception e) {
            Logger.d(TAG, "mark score ex:" + e.toString());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mKaraokeController.getPlayerStatus().playingType = 5;
                if (player != null) {
                    Song secSong = ChooseSongs.getInstance(getApplicationContext()).getSecSong();
                    player.playUrl(AdDefault.getScoreResultVideo(), null, secSong == null ? AdDefault.getScoreResultVideo() : secSong.SongFilePath);
                }
            }
        });
        mSongScore = score;
        mScorePercent = score;
        mScoreSong = song;
        requestResult(song, score);

        mTvPlayerPause.postDelayed(runShowScoreResult, 5500);

        mPlayingSong = null;
    }

    private void requestResult(final Song song, final int score) {
        HttpRequest request = new HttpRequest(getApplicationContext(), RequestMethod.GET_GRADE_RECORD);
        request.addParam("SongID", song.ID);
        request.addParam("Score", score + "");
        request.setHttpRequestListener(new HttpRequestListener() {
            @Override
            public void onStart(String method) {
            }

            @Override
            public void onSuccess(String method, Object object) {
                try {
                    mScorePercent = (int) (Float.valueOf(object.toString()) * 100);
                } catch (Exception e) {
                    Logger.d(getClass().getSimpleName(), "requestResult ex:" + e.toString());
                }
            }

            @Override
            public void onFailed(String method, String error) {
            }
        });
        request.doPost(0);
    }


    private void dealProgress(long progress, long duration) {
        if (mKaraokeController.getPlayerStatus().playingType == 1) {//歌曲电影
            if (mPresentation != null)
                mPresentation.mTvPasterTimer.setVisibility(View.GONE);
            if (progress > 0) {
                /**
                 * 角标显示
                 */
                boolean isStart = Math.abs(progress - 10 * 1000) <= 2 * TIMER_INTERVAL;//开始10秒后
                boolean isCenter = duration > 0 && progress > 0 && Math.abs(duration / 2 - progress) <= 2 * TIMER_INTERVAL;//播放一半
                boolean isEnd = Math.abs(duration - progress - 25 * 1000) <= 2 * TIMER_INTERVAL;//结束前15秒
                boolean isShowCurNext = isStart || isCenter || isEnd;

                if (isShowCurNext && mKaraokeController.getPlayerStatus().playingType == 1 && mPresentation != null) {
                    mPresentation.showCurNextSong();
                }
            }
        } else if (mKaraokeController.getPlayerStatus().playingType == 0 || mKaraokeController.getPlayerStatus().playingType == 4) {//广告
            try {
                if (progress > 0 && progress - mPasterBillProgress >= 5 * 1000 && (progress / 1000) % 5 == 0) {
                    mPasterBillProgress = progress;
                    if (mAdVideo != null && !TextUtils.isEmpty(mCurPastAdPosition))
                        mAdBillHelper.billAd(mAdVideo, mCurPastAdPosition, PrefData.getRoomCode(getApplicationContext()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initCurrentMode() {
        if (player != null && player.getCurrentPosition() < 5 * 1000) {
            mTvPlayerPause.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean originOn = mKaraokeController.getPlayerStatus().originOn;
                    if (originOn) {
                        onOriginal(false);
                    } else {
                        onAccom(false);
                    }
                }
            }, 1000);
            initVol();
        }
    }

    private void onOriginal(boolean showOnScreen) {
        if (player != null)
            player.onOriginal(mAudioChannelFlag);
        if (showOnScreen)
            if (mPresentation != null)
                mPresentation.tipOperation(R.drawable.tv_original_on, R.string.original, true);
    }

    private void onAccom(boolean showOnScreen) {
        if (player != null)
            player.onAccom(mAudioChannelFlag);
        if (showOnScreen)
            if (mPresentation != null)
                mPresentation.tipOperation(R.drawable.tv_original_off, R.string.accompany, true);
    }

    private void initVol() {
        if (player != null)
            player.setVol(mVolPercent);
    }


    private void updateScoreViews(boolean showTvTip) {
        int mode = mKaraokeController.getPlayerStatus().scoreMode;
        if (mPresentation != null) {
            if (mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib)) {
                if (player != null)
                    player.setScoreOn(mode);
                mPresentation.showScoreView(mode);
            } else {
                mPresentation.showScoreView(0);
                if (player != null)
                    player.setScoreOn(0);
            }
        }

        mTgScore.setChecked(mode != 0);
        mTvScore.setSelected(mode != 0);

        if (mPresentation != null && showTvTip) {
            //电视屏显示
            int resId = 0;
            int textResId = 0;
            if (mode == 0) {
                resId = R.drawable.tv_score_closed;
                textResId = R.string.score_closed;
            } else if (mode == 1) {
                resId = R.drawable.tv_score_mode_normal;
                textResId = R.string.score_mode_normal;
            } else if (mode == 2) {
                resId = R.drawable.tv_score_mode_professional;
                textResId = R.string.score_mode_professional;
            }
            if (resId > 0)
                mPresentation.tipOperation(resId, textResId, true);
        }
    }

    private void initMute() {
        boolean isMute = mKaraokeController.getPlayerStatus().isMute;
        initVol();
        if (isMute) {
            volOff();
        } else {
            volOn();
        }
    }

    private void volOn() {
        if (player != null)
            player.volOn();
    }

    private void volOff() {
        if (player != null)
            player.volOff();
    }

    private void play() {
        if (player != null) {
            player.play();
            mKaraokeController.getPlayerStatus().isPlaying = true;
            if (mPresentation != null) {
                mPresentation.tipOperation(0, 0, true);
            }
        }
    }

    private void pause() {
        if (player != null) {
            player.pause();
            mKaraokeController.getPlayerStatus().isPlaying = false;
            if (mPresentation != null) {
                mPresentation.tipOperation(R.drawable.main_bottom_bar_pause_p, R.string.pause, false);
            }
        }
    }

    /**
     * 重播
     */
    private void replay() {
        if (player != null) {
            Song song = ChooseSongs.getInstance(getApplicationContext()).getFirstSong();
            if (song != null) {
                if (BoughtMeal.getInstance().isBuySong()) {
                    Logger.d(TAG, "next 2>>>>>>>");
                    BoughtMeal.getInstance().checkLeftSong();
                }
                playSong(song);
            } else {
                player.replay();
            }
        }
    }

    private void setTone(int tone) {
        if (player != null) {
            player.setTone(tone);
        }
    }

    private void updatePlayerStatus(PlayerStatus playerStatus) {
        boolean isPlaying = playerStatus.isPlaying;
        if (isPlaying) {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_pause, 0, 0);
            closePauseTipView();
        } else {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_play, 0, 0);
            popPauseTipView();
        }
        mTvPlayerPause.setText(isPlaying ? R.string.pause : R.string.play);
        updateOriAccStatus(playerStatus);
        updateScoreViews(false);
    }

    private void initPauseTipView() {
        mPauseTipView = new PauseTipView(this);
        mPauseTipView.setDettachListener(new PauseTipView.DettachWindownListener() {
            @Override
            public void onViewDettachWindow() {
                closePauseTipView();
            }
        });
    }

    private void initChooseSongView() {
        mChooseSongTipView = new ChooseSongTipView(this);
    }

    private void initChooseSongViewLayoutParams() {
        if (mChooseSongViewParams == null) {
            int[] ctrlBarLoc = new int[2];
            findViewById(R.id.ll_search_choose).getLocationInWindow(ctrlBarLoc);
            int[] pauseLoc = new int[2];
            mTvChooseCount.getLocationInWindow(pauseLoc);
            int pauseViewWidth = getResources().getDimensionPixelSize(R.dimen.choose_tip_view_width);
            int pauseViewHeight = getResources().getDimensionPixelSize(R.dimen.choose_tip_view_height);
            mChooseSongViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mChooseSongViewParams.topMargin = ctrlBarLoc[1] - pauseViewHeight;
            mChooseSongViewParams.leftMargin = pauseLoc[0] + mTvChooseCount.getWidth() / 2 - pauseViewWidth / 2;
        }
    }

    private void initPauseTipViewLayoutParams() {
        if (mPauseTipViewParams == null) {
            int[] ctrlBarLoc = new int[2];
            mControlBar.getLocationInWindow(ctrlBarLoc);
            int[] pauseLoc = new int[2];
            mTvPlayerPause.getLocationInWindow(pauseLoc);

            int pauseViewWidth = getResources().getDimensionPixelSize(R.dimen.pause_tip_view_width);
            int pauseViewHeight = getResources().getDimensionPixelSize(R.dimen.pause_tip_view_height);

            mPauseTipViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPauseTipViewParams.topMargin = ctrlBarLoc[1] - pauseViewHeight;
            mPauseTipViewParams.leftMargin = pauseLoc[0] + mTvPlayerPause.getWidth() / 2 - pauseViewWidth / 2;
        }
    }

    private void popPauseTipView() {
        if (mPauseTipView.canPause() && BoughtMeal.getInstance().isBuySong()) {
            initPauseTipViewLayoutParams();
            ((ViewGroup) getWindow().getDecorView().getRootView()).addView(mPauseTipView, mPauseTipViewParams);
            mPauseTipView.showPauseTipView();
        }
    }

    private void closePauseTipView() {
        if (mPauseTipView.isAttachedToWindow()) {
            mPauseTipView.hidePauseTipView();
            ((ViewGroup) getWindow().getDecorView().getRootView()).removeView(mPauseTipView);
        }
    }

    private void hideSystemUI(boolean hide) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", hide);
        sendBroadcast(intent);
    }

    @Override
    public Context getSupportedContext() {
        return this;
    }

    @Override
    public FragmentManager getSupportedFragmentManager() {
        return super.getSupportFragmentManager();
    }

    @Override
    public void sendRequestMessage(boolean isSucced, String method, Object data) {

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_logo:
                PopVersionInfo versionInfo = new PopVersionInfo(Main.this);
                versionInfo.show();

//                openCustomApp();

                break;
        }
        return false;
    }


    private void checkFragment(FragmentModel fragment) {
        if (FmMain.class.getName().equals(fragment.tag) || FmShows.class.getName().equals(fragment.tag)) {
            mIsSetting = false;
            mRoot.setBackgroundResource(R.drawable.bg);
            mViewBottom.setVisibility(View.VISIBLE);
            mViewTop.setVisibility(View.VISIBLE);
            mMarqueePlayer.setVisibility(View.VISIBLE);
        } else if (FmSetting.class.getName().equals(fragment.tag) || FmSettingSerail.class.getName().equals(fragment.tag)) {
            mIsSetting = true;
            mViewBottom.setVisibility(View.GONE);
            mViewTop.setVisibility(View.GONE);
            mRoot.setBackgroundResource(R.drawable.bg_setting);
            mMarqueePlayer.setVisibility(View.INVISIBLE);

        } else {
            mIsSetting = false;
            mViewBottom.setVisibility(View.VISIBLE);
            mViewTop.setVisibility(View.VISIBLE);
            mRoot.setBackgroundResource(R.drawable.bg_content);
            mMarqueePlayer.setVisibility(View.VISIBLE);

        }
        if (!mIsSetting) {
            checkAvailable();
        }
    }


    private void performDownloadUpdate(DownloadBusEvent event) {
        int hisProgress = 0;
        if (mTvProgress.getTag() != null) {
            hisProgress = (int) mTvProgress.getTag();
            hisProgress = hisProgress == 100 ? 0 : hisProgress;
        }
        int curPorgress = Math.max(hisProgress, (int) event.percent);
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setTag(curPorgress);
        mTvProgress.setText(String.valueOf(curPorgress) + "%");
    }

    private void performDownloadFinish(DownloadBusEvent event) {
        Song song = MyDownloader.getInstance().getSong(event.url);
        if (song.isPrior) {
            song.isPrior = false;
            ChooseSongs.getInstance(this).add2Top(song);
        } else {
            ChooseSongs.getInstance(this).addSong(song);
        }

        if (MyDownloader.getInstance().isFinishAllTask()) {
            mTvProgress.setVisibility(View.INVISIBLE);
        } else {
            mTvProgress.setVisibility(View.VISIBLE);
        }
    }

    private void performDownloadStart(DownloadBusEvent event) {
        AnimatorUtils.playParabolaAnimator((ViewGroup) mRoot, mTvProgress);
    }


    @Override
    protected void onResume() {
        checkAvailable();
        super.onResume();
    }

    private void restoreUserInfo() {
        BoughtMeal.getInstance().restoreMealInfoFromSharePreference();
        mQureyHelper.queryUser().post();
        checkMic();
    }

    private void checkAvailable() {
        checkDisk();
        checkBoxId();
    }

    private PromptDialog mDlgDiskNotExit;

    private boolean checkDisk() {
        if (!DiskFileUtil.isDiskExit()) {
            if (mDlgDiskNotExit == null || !mDlgDiskNotExit.isShowing()) {
                mDlgDiskNotExit = new PromptDialog(this);
                mDlgDiskNotExit.setMessage("未连接硬盘或硬盘已损坏！");
//                mDlgDiskNotExit.setNotClose();
                mDlgDiskNotExit.show();
            }
            return false;
        } else {
            if (mDlgDiskNotExit != null && mDlgDiskNotExit.isShowing()) {
                mDlgDiskNotExit.dismiss();
            }
        }
        return true;
    }

    private PromptDialog mDlgSetBoxId;

    private boolean checkBoxId() {
        if (TextUtils.isEmpty(PrefData.getRoomCode(getApplicationContext()))) {
            showSetBoxId();
            return false;
        } else {
            return true;
        }
    }

    private void showSetBoxId() {
        if (mDlgSetBoxId == null || !mDlgSetBoxId.isShowing()) {
            mDlgSetBoxId = new PromptDialog(this);
            mDlgSetBoxId.setMessage("请设置房间编号");
            mDlgSetBoxId.setPositiveButton("设置", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMngPass(0);
                }
            });
            mDlgSetBoxId.setCanceledOnTouchOutside(false);
            mDlgSetBoxId.show();
        }
    }


    private MngPwdDialog mDlgPass;

    private void showMngPass(final int open) {
        if (mDlgPass == null || !mDlgPass.isShowing()) {
            mDlgPass = new MngPwdDialog(this);
            mDlgPass.setOnMngPwdListener(new MngPwdDialog.OnMngPwdListener() {
                @Override
                public void onPass() {
                    FragmentUtil.addFragment(FmSetting.newInstance(open));
                }
            });
            mDlgPass.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (!mIsSetting) {
                        checkBoxId();
                    }
                }
            });
            mDlgPass.show();
        }
    }

    private boolean mIsSetting;


    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        registerReceiver(usbStateReceiver, filter);
//        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SHARED);//如果SDCard未安装,并通过USB大容量存储共享返回
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd对象是存在并具有读/写权限
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但没有被安装
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //表明对象正在磁盘检查
        filter.addAction(Intent.ACTION_MEDIA_EJECT);  //物理的拔出 SDCARD
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
        filter.addDataScheme("file"); // 必须要有此行，否则无法收到广播
        registerReceiver(usbStateReceiver, filter);

    }

    private void unregisterUsbReceiver() {
        unregisterReceiver(usbStateReceiver);
    }

    private final BroadcastReceiver usbStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(TAG, "usbStateReceiver action:" + action);
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                checkDisk();
                checkDeviceStore();

            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                checkDisk();
                checkDeviceStore();
                Uri uri = intent.getData();
                String path = uri.getPath();
                Logger.d(TAG, "usb path===" + path + "  ");
            } else if (action.equals("android.hardware.usb.action.USB_STATE") | action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {//判断其中一个就可以了

            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {//USB被拔出

            }
        }
    };

    private DeviceStore mDlgDeviceStore;

    private void checkDeviceStore() {
        boolean isStore = UsbFileUtil.isUsbExitBoxCode();
        if (isStore) {
            if (mDlgDeviceStore == null || !mDlgDeviceStore.isShowing()) {
                mDlgDeviceStore = new DeviceStore(this, UsbFileUtil.readKBoxCode());
                mDlgDeviceStore.show();
            }
        } else {
            if (mDlgDeviceStore != null && mDlgDeviceStore.isShowing()) {
                mDlgDeviceStore.dismiss();
            }
        }
    }

    private long preCall = 0;

    @Override
    public void onRecordData(byte[] data, int bufSize) {
//        Logger.d(TAG, "onRecordData len:" + data.length + "  bufSize:" + bufSize);
        if (mPresentation != null) {
            if (mPresentation.getWidgetScore().isShown()) {
                mPresentation.getWidgetScore().stopFlake();
                long cur = System.currentTimeMillis();
                if (cur - preCall >= 200) {
                    byte[] bytes = new byte[bufSize];
                    System.arraycopy(data, 0, bytes, 0, bufSize);
                    int decibel = calculateDecibel(bytes, bufSize);
//                    Logger.d(TAG, "onRecordData calculateDecibel decibel:" + decibel);
                    mPresentation.getWidgetScore().setVisualizerData(decibel);
                    preCall = cur;
                }
            } else {
                mPresentation.getWidgetScore().stopFlake();
            }
        }
    }

    private int calculateDecibel(byte[] buf, int mBufSize) {
        int sum = 0;
        for (int i = 0; i < mBufSize; i++) {
            sum += Math.abs(buf[i]);
        }
        // avg 10-50
        return sum / mBufSize;
    }


    private PromptDialog mDlgTipBuy;

    public void tipBuyMessage(int resId) {
        try {
            if (mDlgTipBuy == null || !mDlgTipBuy.isShowing()) {
                mDlgTipBuy = new PromptDialog(Main.mMainActivity);
                mDlgTipBuy.setMessage(getString(resId));
                mDlgTipBuy.setPositiveButton(getString(R.string.buy), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonDialog dialog = CommonDialog.getInstance();
                        dialog.setShowClose(true);
                        int pageType = BoughtMeal.getInstance().isMealExpire() ?
                                FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                        dialog.setContent(FmPayMeal.createMealFragment(pageType));
                        if (!dialog.isAdded()) {
                            dialog.show(Main.mMainActivity.getSupportFragmentManager(), "commonDialog");
                        }
                    }
                });
                mDlgTipBuy.show();
            }
        } catch (Exception e) {
            Logger.d(getClass().getSimpleName(), "tipMessage ex:" + e.toString());
        }
    }


    @Override
    public void onEnterScreenAd() {
        if ((mDlgAdScreen == null || !mDlgAdScreen.isVisible()) && mCurScreenAd != null && !TextUtils.isEmpty(mCurScreenAd.ADContent)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDlgAdScreen = new DlgAdScreen();
                    mDlgAdScreen.showAdScreen(mCurScreenAd);
                    mDlgAdScreen.show(getFragmentManager(), "dialogAdScreen");
                }
            });
        } else {
            Logger.d(TAG, "onEnterScreenAd  no enter");
        }
        super.onEnterScreenAd();
    }

    private DlgGuide mDlgGuide;

    private PromptDialog mDlgChooseSong;

    private void showGuideDialog() {
        if (mDlgGuide == null || !mDlgGuide.isVisible()) {
            mDlgGuide = new DlgGuide();
            mDlgGuide.setOnDismissListener(new DlgGuide.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Meal boughtMeal = BoughtMeal.getInstance().getTheFirstMeal();
                    if (boughtMeal != null && boughtMeal.getType() == Meal.SONG && BoughtMeal.getInstance().getLeftSongs() > 0 && ChooseSongs.getInstance(getApplicationContext()).getChooseSize() <= 0) {
                        try {
                            if (mDlgChooseSong == null || !mDlgChooseSong.isShowing()) {
                                mDlgChooseSong = new PromptDialog(Main.mMainActivity);
                                mDlgChooseSong.setMessage(getString(R.string.choose_song_tip_x));
                                mDlgChooseSong.setPositiveButton(getString(R.string.known), null);
                                mDlgChooseSong.setCanceledOnTouchOutside(false);
                                mDlgChooseSong.show();
                            }
                        } catch (Exception e) {
                            Logger.d(getClass().getSimpleName(), "tipMessage ex:" + e.toString());
                        }
                    }

                }
            });
            mDlgGuide.show(getFragmentManager(), "dialogGuide");
        }
    }

    private static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

    private void startChooseSongTimer() {
        Meal boughtMeal = BoughtMeal.getInstance().getTheFirstMeal();
        if (boughtMeal != null && boughtMeal.getType() == Meal.SONG && BoughtMeal.getInstance().getLeftSongs() > 0
                && ChooseSongs.getInstance(getApplicationContext()).getChooseSize() <= 0) {
            ChooseSongTimer.getInstance().setChooseSongTimerListener(new ChooseSongTimer.ChooseSongTimerListener() {
                @Override
                public void onChooseSongTimer(int count) {
                    Logger.d(TAG, "startChooseSongTime onChooseSongTimer:" + count);
                    showChooseSongTipView(BnsConfig.CHOOSE_SONG_TIME - count);
                    if ((BnsConfig.CHOOSE_SONG_TIME - count) == 0) {
                        //减去一首歌
                        ChooseSongTimer.getInstance().resetCount();
                        //重置暂停次数                        mPauseTipView.resetLeftTimes();
                        //减掉一首
                        BoughtMeal.getInstance().updateLeftSongs();
                        Logger.d(TAG, "startChooseSongTime onChooseSongTimer sub song");
                    }
                }
            }).startTimer();
        } else {
            ChooseSongTimer.getInstance().stopTimer();
            closeChooseSongTipView();
        }
    }


    private void showChooseSongTipView(int time) {
        if (!mChooseSongTipView.isAttachedToWindow()) {
            initChooseSongViewLayoutParams();
            ((ViewGroup) getWindow().getDecorView().getRootView()).addView(mChooseSongTipView, mChooseSongViewParams);
            mChooseSongTipView.showView();
        }
        if (mChooseSongTipView.isAttachedToWindow() && mChooseSongTipView.isShown()) {
            mChooseSongTipView.setTime(time);
        }
    }

    private void closeChooseSongTipView() {
        if (mChooseSongTipView.isAttachedToWindow()) {
            mChooseSongTipView.hideView();
            ((ViewGroup) getWindow().getDecorView().getRootView()).removeView(mChooseSongTipView);
        }
    }

    private DlgProgress mDlgSystemUpdate;


    private void openCustomApp() {
        final String apkPackage = "com.sunplusit.usbcameraapp2";
        if (checkApkExist(apkPackage)) {
            PromptDialog promptDialog = new PromptDialog(this);
            promptDialog.setShowButton2(true);
            promptDialog.setMessage("是否前往“XXX APP”");
            promptDialog.setPositiveButton("前往", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doStartApplicationWithPackageName(apkPackage);
                }
            });
            promptDialog.show();
        } else {
            PromptDialog promptDialog = new PromptDialog(this);
            promptDialog.setShowButton2(true);
            promptDialog.setMessage("是否下载“XXX APP”");
            promptDialog.setPositiveButton("下载", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File fileDes = new File(KaraokeSdHelper.getSdCard(), "custom.apk");
                    SimpleDownloader simpleDownloader = new SimpleDownloader();
                    simpleDownloader.download(fileDes, "http://p.gdown.baidu.com/2ba5e9d03fc223d62b3cf6f660067629bf4d7304eaebd951ec18303f29c01a7247cbd7bba259408c6ef94fe653dd9bbd1edb02ae8b858d247ccdd1b3bc5ba58b7f86027d36ce975d97299d4657568ee134ab571b5de4828bc1085f7c5af70e12ce66f4c2e258aab79f2f07d25a46c66c2b68f828321d8cebc6b7990a8b7b7f851704e3eb35df5665507445864e2e7c8a224ed008a767485b6b495208cd8287d48f83a0ee1f2fb3a09df2cd20c9ddc5d774422610eb905f9d6ea53ee4e74cb1e980414cb1356e9b437cecbc7f5a26e76cc87a94b055b5fff5c3250434f073d04a193b6901bd77ed0d8ed938aab27f3a676fa7fa39113b9e0d1538926fee90cd00cb293aacc712022b3140356ae192bba43a6a26a0f9df381df26cb708d751db06d0841036bdcdbeb6788b975abf42e93f61586a20090b126743d218dc1e6225aca906c450bd84485a9303a8383beba488", new SimpleDownloadListener() {
                        @Override
                        public void onDownloadCompletion(File file, String url, long size) {
                            if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                                mDlgSystemUpdate.dismiss();
                                mDlgSystemUpdate = null;
                            }
                            hideSystemUI(true);
                            PackageUtil.installApkByApi(getApplicationContext(), file);
                        }

                        @Override
                        public void onDownloadFail(String url) {
                            if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                                mDlgSystemUpdate.dismiss();
                                mDlgSystemUpdate = null;
                            }
                        }

                        @Override
                        public void onUpdateProgress(File mDesFile, long progress, long total) {
                            try {
                                if (mDlgSystemUpdate != null && mDlgSystemUpdate.isShowing()) {
                                    mDlgSystemUpdate.setProgress(progress, total);
                                }
                            } catch (Exception ex) {
                                Logger.w("Main", "custom apk download ex:" + ex.toString());
                            }
                        }
                    });

                    try {
                        mDlgSystemUpdate = new DlgProgress(Main.mMainActivity);
                        mDlgSystemUpdate.setTitle("文件下载");
                        mDlgSystemUpdate.setTip("文件下载中，请勿关机...");
                        mDlgSystemUpdate.show();
                    } catch (Exception ex) {
                        Logger.w("Main", "custom apk download ex:" + ex.toString());
                    }
                }
            });
            promptDialog.show();
        }
    }

    private boolean checkApkExist(String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void doStartApplicationWithPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);

            hideSystemUI(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());//获取PID
            System.exit(0);//直接结束程序
        }
    }
}
