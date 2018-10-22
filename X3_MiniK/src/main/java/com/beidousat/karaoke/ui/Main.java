package com.beidousat.karaoke.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.LanService;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.ad.AdDefault;
import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.RetrofitFactory;
import com.beidousat.karaoke.biz.SongHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.biz.base.BaseEntity;
import com.beidousat.karaoke.biz.base.BaseObserver;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.PublicSong;
import com.beidousat.karaoke.db.DatabaseHelper;
import com.beidousat.karaoke.model.CouponDetail;
import com.beidousat.karaoke.model.GiftDetail;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.model.PlayerStatus;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.UpLoadDataUtil;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.BnsPlayer;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.karaoke.player.chenxin.OriginPlayer;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.DeviceStore;
import com.beidousat.karaoke.ui.dlg.DlgAdScreen;
import com.beidousat.karaoke.ui.dlg.DlgAir;
import com.beidousat.karaoke.ui.dlg.DlgCoupon;
import com.beidousat.karaoke.ui.dlg.DlgGuide;
import com.beidousat.karaoke.ui.dlg.DlgInitLoading;
import com.beidousat.karaoke.ui.dlg.DlgService;
import com.beidousat.karaoke.ui.dlg.DlgTune;
import com.beidousat.karaoke.ui.dlg.FinishDialog;
import com.beidousat.karaoke.ui.dlg.FmDownloader;
import com.beidousat.karaoke.ui.dlg.FmFail;
import com.beidousat.karaoke.ui.dlg.FmPayMeal;
import com.beidousat.karaoke.ui.dlg.FmPayResult;
import com.beidousat.karaoke.ui.dlg.FmPaySevice;
import com.beidousat.karaoke.ui.dlg.FmRoomSet;
import com.beidousat.karaoke.ui.dlg.MngPwdDialog;
import com.beidousat.karaoke.ui.dlg.PopVersionInfo;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.ui.dlg.StepDialog;
import com.beidousat.karaoke.ui.fragment.FmChooseList;
import com.beidousat.karaoke.ui.fragment.FmMain;
import com.beidousat.karaoke.ui.fragment.FmSearch;
import com.beidousat.karaoke.ui.fragment.FmSetting;
import com.beidousat.karaoke.ui.fragment.FmSettingInfrared;
import com.beidousat.karaoke.ui.fragment.FmSettingSerail;
import com.beidousat.karaoke.ui.fragment.FmShows;
import com.beidousat.karaoke.ui.presentation.PlayerPresentation;
import com.beidousat.karaoke.util.AnimatorUtils;
import com.beidousat.karaoke.util.ChooseSongTimer;
import com.beidousat.karaoke.util.DownloadQueueHelper;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.karaoke.util.SystemBroadcastSender;
import com.beidousat.karaoke.util.TipsUtil;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.util.UIUtils;
import com.beidousat.karaoke.widget.ChooseSongTipView;
import com.beidousat.karaoke.widget.MealInfoTextView;
import com.beidousat.karaoke.widget.PauseTipView;
import com.beidousat.karaoke.widget.UserInfoLayout;
import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.DownloadBusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.FragmentModel;
import com.beidousat.libbns.model.KBoxStatus;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.NetChecker;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.net.download.FileDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.socket.KBoxSocketHeart;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.FileUtil;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libbns.util.UsbFileUtil;
import com.beidousat.score.KeyInfo;
import com.beidousat.score.NoteInfo;
import com.beidousat.score.OnKeyInfoListener;
import com.beidousat.score.OnScoreListener;
import com.czt.mp3recorder.AudioRecordFileUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.reactivex.Observable;


public class Main extends BaseActivity implements View.OnClickListener,
        BeidouPlayerListener, OnKeyInfoListener, OnScoreListener, SupportQueryOrder, View.OnLongClickListener {

    private static final String TAG = "Main";

    private LinkedList<FragmentModel> mFragments = new LinkedList<FragmentModel>();
    private FragmentManager mFragmentManager;
    private Button mBtnBack;
    private TextView mTvChooseCount, mTvCurrentSong;
    private TextView mTvPlayerPause, mTvPlayerOriAcc, mTvScore, mTvService, mTvSwitch;
    private LinearLayout ll_service;
    private UserInfoLayout mUserInfoLayout;
    private ToggleButton mTgScore;
    private MealInfoTextView mTvBuy;
    private TextView mTvProgress, mTvTips;
    private View mRoot;

    private KaraokeController mKaraokeController;
    private DisplayManager mDisplayManager;
    private AdBillHelper mAdBillHelper;

    //    private MarqueePlayer mMarqueePlayer;
    private TextView lable;
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
    private BnsPlayer player;
    private OriginPlayer player_cx;
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


    private SurfaceView main_surf;
    private HandlerSystem handler;
    private boolean windowsfocus;
    private boolean surf_show = false;
    private float touch_x = 0;
    private float touch_y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceUtil.getString(this, "mode", "zh").equals("en")) {
            switchLanguage("en");
        } else if (PreferenceUtil.getString(this, "mode", "zh").equals("zh")) {
            switchLanguage("zh");
        } else if (PreferenceUtil.getString(this, "mode", "zh").equals("en_zh")) {
            Common.isAuto = true;
        }
        setContentView(R.layout.act_main);
        mMainActivity = this;
        initView();
        init();
        EventBus.getDefault().register(this);
        if (!DiskFileUtil.is901()) {
            FileUtil.chmod777FileSu(KaraokeSdHelper.getSongSecurityKeyFileFor901());
            hideSystemUI(false);
        } else {
            hideSystemUI(true);
        }
        startMainPlayer();
        checkNetwork();
//        if(DiskFileUtil.getDiskFileByUrl("data/song/kx/fengxian/44d3d201-922d-46d4-86ba-7a56bf031661.mp4")!=null) {
//            File file = new File(DiskFileUtil.getDiskFileByUrl("data/song/kx/fengxian/44d3d201-922d-46d4-86ba-7a56bf031661.mp4").getAbsolutePath());
//            if (file.exists()) {
//                file.delete();
//                Log.e(TAG, "delete:" + file.getAbsolutePath());
//            }
//        }
//        if(DiskFileUtil.getDiskFileByUrl("data/song/qg/c091f856-ecfc-4641-981c-d2762ce5a92d.mp4")!=null) {
//            File file1 = new File(DiskFileUtil.getDiskFileByUrl("data/song/qg/c091f856-ecfc-4641-981c-d2762ce5a92d.mp4").getAbsolutePath());
//            if (file1.exists()) {
//                file1.delete();
//                Log.e(TAG, "delete:" + file1.getAbsolutePath());
//            }
//        }
//        new QueryKboxHelper(getApplicationContext(), null, null).getBoxInfo();
//        newsongDao=LanApp.getInstance().getDaoSession().getNewsongDao();
//        copyDatabaseFile(this);
    }

    private void copyDatabaseFile(Context context) {
        File dbFile = context.getDatabasePath(Common.SongDb_Name);
        if (dbFile.exists()) {
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = context.getAssets().open(Common.SongDb_Name);
            out = new FileOutputStream(dbFile);

            byte buf[] = new byte[1024 * 1024];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
//            dbFile.delete();
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取kbox信息
    private void initAfterNetAvail() {
//        获取miniK配置文件（服务器接口地址，心跳地址，VOD地址等）
        new QueryKboxHelper(getApplicationContext(), null, new QueryKboxHelper.QueryKboxFeedback() {
            @Override
            public void onStart() {
                PrefData.setAuth(Main.this, false);
                showTips(getString(R.string.getting_config));
//                showCfgDialog(getString(R.string.getting_config),getString(R.string.getting_config_error));
            }

            @Override
            public void onFeedback(boolean suceed, String msg, Object obj) {
                //获取配置文件成功后检测外接硬盘，检测USB接入等
                hideTips();
                if (suceed) {
                    KboxConfig kboxConfig = (KboxConfig) obj;
                    String language = kboxConfig.getLanguage().toLowerCase();
                    if (!TextUtils.isEmpty(language)) {
                        if (!language.equals(PreferenceUtil.getString(Main.this, "mode", "zh"))) {
                            PromptDialog promptDialog = new PromptDialog(Main.this);
                            promptDialog.setMessage(R.string.language_different);
                            promptDialog.setPositiveButton(getString(R.string.reboot), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ChooseSongs.getInstance(Main.this).cleanChoose();
                                    BoughtMeal.getInstance().clearMealInfo();
                                    BoughtMeal.getInstance().clearMealInfoSharePreference();
                                    exitApp();
                                }
                            });
                            promptDialog.show();
                        }
                        PreferenceUtil.setString(Main.this, "mode", kboxConfig.getLanguage());
                    }


//                    dismissInitLoading();
//                    mMarqueePlayer.loadAds("Z1");
//                    mMarqueePlayer.startPlayer();
                    checkDeviceStore();
                    checkUsbKey();
                    //开启心跳服务
                    startService(new Intent(getApplicationContext(), LanService.class));
                    getKboxDetail();
                } else {
                    PromptCfgDialog(getString(R.string.getting_config_error));
//                    showCfgDialog(getString(R.string.getting_config), getString(R.string.getting_config_error));
                }
            }
        }).getConfig(DeviceUtil.getCupChipID());
    }

    private void getKboxDetail() {
        new QueryKboxHelper(getApplicationContext(), null, new QueryKboxHelper.QueryKboxFeedback() {
            @Override
            public void onStart() {
                showTips(getString(R.string.getting_box_info));
            }

            @Override
            public void onFeedback(boolean suceed, String msg, Object object) {
                hideTips();
                if (suceed) {
//                                dismissInitLoading();
                    if (object != null && object instanceof KBox) {
                        if (TextUtils.isEmpty(((KBox) object).getLabel())) {
                            lable.setVisibility(View.INVISIBLE);
                        } else {
                            lable.setText(((KBox) object).getLabel());
                            lable.setVisibility(View.VISIBLE);
                        }
                        if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                            ll_service.setVisibility(View.GONE);
                            mTvBuy.setVisibility(View.GONE);
                        } else {
                            ll_service.setVisibility(View.VISIBLE);
                            mTvBuy.setVisibility(View.VISIBLE);
                        }
                        getBoughtMeal();
                        restoreUserInfo();
                    }
                } else {
                    if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                        BoughtMeal.getInstance().clearMealInfoSharePreference();
                        BoughtMeal.getInstance().restoreMealInfoFromSharePreference();
                    }
                    if (getApplicationContext() != null) {
                        ToastUtils.toast(getApplicationContext(), msg);
                    }


                }
            }

        }).getBoxInfo(PrefData.getRoomCode(this.getApplicationContext()));
    }

    private void showTips(String tips) {
        mTvTips.setText(tips);
        mTvTips.setVisibility(View.VISIBLE);
    }

    private void hideTips() {
        mTvTips.setText("");
        mTvTips.setVisibility(View.GONE);
    }

    private boolean mIsInitAfterNetAvail = false;

    protected void checkNetwork() {
        showNetDialog(getString(R.string.checking_network), getString(R.string.checking_network_error));
        NetChecker.getInstance(Main.mMainActivity).setOnNetworkStatusListener(new NetChecker.OnNetworkStatusListener() {
            @Override
            public void onNetworkStatus(boolean status) {
                Logger.d(TAG, "checkNetwork onNetworkStatus:" + status);
                if (status) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsInitAfterNetAvail) {
                                mIsInitAfterNetAvail = true;
                                dismissInitLoading();
                                initAfterNetAvail();
                            }
                        }
                    });
                }
            }
        }).check();
    }

    private DlgInitLoading mDlgInitLoading;

    private void showNetDialog(final String text, final String error) {
        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
            mDlgInitLoading = new DlgInitLoading(this);
            mDlgInitLoading.setMessage(text);
            mDlgInitLoading.setError(error);
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

//    private void showCfgDialog(final String text, final String error) {
//        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
//            mDlgInitLoading = new DlgInitLoading(this);
//            mDlgInitLoading.setMessage(text);
//            mDlgInitLoading.setError(error);
//            mDlgInitLoading.setPositiveButton(getString(R.string.cfg_retry), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//            mDlgInitLoading.show();
//        } else {
//            mDlgInitLoading.setMessage(text);
//        }
//    }

    private void PromptCfgDialog(final String error) {
        PromptDialog promptDialog = new PromptDialog(Main.mMainActivity);
        promptDialog.setPositiveButton(getString(R.string.cfg_retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAfterNetAvail();
            }
        });
        promptDialog.setClose(true);
        promptDialog.setCanceledOnTouchOutside(false);
        promptDialog.setMessage(error);
        promptDialog.show();

    }

//    private void PromptKboxDialog(final String error) {
//        PromptDialog promptDialog = new PromptDialog(Main.mMainActivity);
//        promptDialog.setPositiveButton(getString(R.string.setting_room), new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showRoomSet();
//            }
//        });
//        promptDialog.setClose(true);
//        promptDialog.setCanceledOnTouchOutside(false);
//        promptDialog.setMessage(error);
//        promptDialog.show();
//
//    }

//    private void showKboxInitDialog(final String text, final String error) {
//        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
//            mDlgInitLoading = new DlgInitLoading(this);
//            mDlgInitLoading.setMessage(text);
//            mDlgInitLoading.setError(error);
//            mDlgInitLoading.setPositiveButton(getString(R.string.setting_network), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showRoomSet();
//                }
//            });
//            mDlgInitLoading.show();
//        } else {
//            mDlgInitLoading.setMessage(text);
//        }
//    }

//    private void showAuthorizeDialog(final String text, final String error) {
//        if (mDlgInitLoading == null || !mDlgInitLoading.isShowing()) {
//            mDlgInitLoading = new DlgInitLoading(this);
//            mDlgInitLoading.setMessage(text);
//            mDlgInitLoading.setError(error);
//            mDlgInitLoading.setPositiveButton(getString(R.string.setting_network), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showRoomSet();
//                }
//            });
//            mDlgInitLoading.show();
//        } else {
//            mDlgInitLoading.setMessage(text);
//        }
//    }

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
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }


    @Override
    protected void onDestroy() {
        unregisterUsbReceiver();
        stopService(new Intent(getApplicationContext(), LanService.class));
        BoughtMeal.getInstance().deleteObservers();
        PayUserInfo.getInstance().deleteObservers();
//        mMarqueePlayer.stopPlayer();
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
        mTvService = (TextView) findViewById(R.id.tv_service);
        mTvSwitch = (TextView) findViewById(R.id.tv_switch);
        mTvSwitch.setVisibility(Common.isAuto ? View.VISIBLE : View.GONE);
        mTvSwitch.setOnClickListener(this);
        mTgScore = (ToggleButton) findViewById(R.id.tg_score);
        mTgScore.setOnClickListener(this);
        mTvScore = (TextView) findViewById(R.id.tv_score);
        mTvScore.setOnClickListener(this);
        mTvScore.setSelected(true);
        lable = (TextView) findViewById(R.id.main_lable);
//        mMarqueePlayer = (MarqueePlayer) findViewById(R.id.ads_marquee);
        mControlBar = findViewById(R.id.control_bar);
        mTvBuy = (MealInfoTextView) findViewById(R.id.tv_buy);
        mTvTips = (TextView) findViewById(R.id.main_process_tips);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        main_surf = (SurfaceView) findViewById(R.id.main_surf);
        main_surf.setZOrderOnTop(true);
        mUserInfoLayout = (UserInfoLayout) findViewById(R.id.ll_user);
        ll_service = (LinearLayout) findViewById(R.id.ll_service);
        ImageView iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(this);
        iv_logo.setOnLongClickListener(this);
        mTvService.setVisibility(Common.isAuto ? View.GONE : View.VISIBLE);
        if (Common.isEn) {
            mTvBuy.setBackgroundResource(R.drawable.selector_main_buy_en);
            mBtnBack.setBackgroundResource(R.drawable.selector_main_back_en);
            iv_logo.setImageResource(R.drawable.logo_en);
        }
        initPauseTipView();
        initChooseSongView();
    }

    private void init() {
        handler = new HandlerSystem();
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
            SerialController.getInstance(getApplicationContext()).open(Common.mPort, baudrate);
//            SerialController.getInstance(getApplicationContext()).openInfrared(Common.mInfraredPort, Common.mInfraredBaudRate);
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
                if (player != null || player_cx != null) {
                    mKaraokeController.getPlayerStatus().isMute = false;
                    closePauseTipView();
                    next();
                    if (mPresentation != null)
                        mPresentation.tipOperation(R.drawable.tv_next, R.string.switch_song, true);
                }
                break;
            case EventBusId.id.PLAYER_NEXT_DELAY:
                String downpath = event.data.toString();
                Log.d(TAG,"download:"+ServerFileUtil.getFileUrl(downpath)+"   "+"savepath:"+DiskFileUtil.getFileSavedPath(downpath));
                List<BaseDownloadTask> mTaskList = new ArrayList<>();
                BaseDownloadTask task = FileDownloader.getImpl().create(ServerFileUtil.getFileUrl(downpath))
                        .setPath(DiskFileUtil.getFileSavedPath(downpath));
                mTaskList.add(task);
                DownloadQueueHelper.getInstance().downloadSequentially(mTaskList);
                final String finalUri = downpath;
                DownloadQueueHelper.getInstance().setOnDownloadListener(new DownloadQueueHelper.OnDownloadListener() {
                    @Override
                    public void onDownloadComplete(BaseDownloadTask task) {
                        Log.d(TAG,"download Commplete:"+ServerFileUtil.getFileUrl(finalUri));
                    }

                    @Override
                    public void onDownloadTaskError(BaseDownloadTask task, Throwable e) {
                        Log.d(TAG,"download Error:"+ServerFileUtil.getFileUrl(finalUri));
                    }

                    @Override
                    public void onDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG,"download:"+(int) ((float) soFarBytes / totalBytes * 100));
                    }

                    @Override
                    public void onDownloadTaskOver() {

                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (player != null || player_cx != null) {
                            mKaraokeController.getPlayerStatus().isMute = false;
                            closePauseTipView();
                            next();
                            if (mPresentation != null)
                                mPresentation.tipOperation(R.drawable.tv_next, R.string.switch_song, true);
                        }
                    }
                },10*1000);

                break;
            case EventBusId.id.PLAYER_STATUS_CHANGED:
                PlayerStatus status = (PlayerStatus) event.data;
                updatePlayerStatus(status);
                break;

            case EventBusId.id.PLAYER_PLAY:
                if (player != null || player_cx != null) {
                    play();
                    if (mPresentation != null) {
                        mPresentation.tipOperation(R.drawable.tv_play, R.string.play, true);
//                        mPresentation.showPauseAd(false, false);
                    }
                }
                break;

            case EventBusId.id.PLAYER_PAUSE2:
                pause();
                if (mPresentation != null) {
                    mPresentation.tipOperation(R.drawable.tv_pause, R.string.pause, true);
//                    mPresentation.showPauseAd(true, mKaraokeController.getPlayerStatus().playingType == 2);
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
                pay_sucessed(event);
                break;
            case EventBusId.id.MEAL_EXPIRE:
                if (!PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                    CommonDialog commonDialog = CommonDialog.getInstance();
                    if (commonDialog.isAdded()) {
                        commonDialog.dismiss();
                    }
                    FinishDialog finishDialog = new FinishDialog();
                    if (!finishDialog.isAdded()) {
                        finishDialog.show(getSupportFragmentManager(), "share");
                    }
                }
                break;
            case EventBusId.id.ROOM_CLOSE:
                //TODO 停止播放
                Log.e("test", "停止播放");
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
                performDownloadStart(touch_x, touch_y);
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
                new QueryKboxHelper(getApplicationContext(), null, null).getBoxInfo(PrefData.getRoomCode(Main.this.getApplicationContext()));
                break;

            case EventBusId.SOCKET.KBOX_STATUS_CHECKING:
                if (!mIsSetting && KBoxInfo.getInstance().getKBox() == null) {
//                    LoadingUtil.showLoadingDialog(this, getString(R.string.init_system));
//                    showKboxInitDialog(getString(R.string.getting_box_info), getString(R.string.checking_kbox_error));
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
                Logger.d(TAG, "onEventMainThread: " + "kbox staut:" + kBoxStatus.status + "kbox code:" + kBoxStatus.code);
                if (kBoxStatus.status != 1) {//授权未通过
                    PrefData.setAuth(Main.this, false);
                    TipsUtil tipsUtil = new TipsUtil(Main.this);
                    if (!mIsSetting && (mDlgPass == null || !mDlgPass.isShowing())
                            && (mDialogAuth == null || !mDialogAuth.isShowing())) {
                        if (kBoxStatus.code == 2003) {
                            if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
//                                Log.e("test", "心跳检测没交服务费，清空套餐");
                                BoughtMeal.getInstance().clearMealInfoSharePreference();
                                BoughtMeal.getInstance().restoreMealInfoFromSharePreference();
                            }
                            mDialogAuth = new PromptDialog(Main.mMainActivity);
                            mDialogAuth.setPositiveButton(getString(R.string.pay_for_service), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showPayService();
                                }
                            });
                            mDialogAuth.setCanceledOnTouchOutside(false);
                            mDialogAuth.setClose(true);
                            mDialogAuth.setMessage(tipsUtil.getErrMsg(kBoxStatus.code));
                            mDialogAuth.show();
                        } else if (kBoxStatus.code == 2001) {
                            mDialogAuth = new PromptDialog(Main.mMainActivity);
                            mDialogAuth.setPositiveButton(getString(R.string.setting), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showMngPass(0);
                                }
                            });
                            mDialogAuth.setCanceledOnTouchOutside(false);
                            mDialogAuth.setClose(true);
                            mDialogAuth.setMessage(tipsUtil.getErrMsg(kBoxStatus.code));
                            mDialogAuth.show();
                        } else if(kBoxStatus.code == 00301){
                            if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                                BoughtMeal.getInstance().clearMealInfoSharePreference();
                                BoughtMeal.getInstance().restoreMealInfoFromSharePreference();
                            }
                        }else {
                            if (getApplicationContext() != null) {
                                ToastUtils.toast(getApplicationContext(), tipsUtil.getErrMsg(kBoxStatus.code));
                            }
                        }
                    }
                } else {
                    PrefData.setAuth(Main.this, true);
                    PrefData.setLastTime(this.getApplicationContext(), System.currentTimeMillis());
                    if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                        if (BoughtMeal.getInstance().isMealExpire()) {
//                            Log.e("test", "心跳检测，初始化套餐");
                            Meal meal = new Meal();
                            meal.setType(1);
                            meal.setAmount(10000);
                            PayStatus payStatus = new PayStatus();
                            payStatus.setPayStatus(1);
                            payStatus.setPayTime((int) System.currentTimeMillis());
                            payStatus.setOrderSn(meal.getOrderSn());
                            payStatus.setDeviceSn(DeviceUtil.getCupChipID());
                            payStatus.setType(meal.getType());
                            payStatus.setAmount(meal.getAmount());
                            //设置当前购买的套餐
                            BoughtMeal.getInstance().setBoughtMeal(meal, payStatus);
                        }

                    }
                    if (KBoxInfo.getInstance().getmPayMentlist() == null) {
                        new QueryKboxHelper(getApplicationContext(), null, null).getPayment();
                    }
                    if (CommonDialog.mInstance != null && CommonDialog.mInstance.getTag() != null && CommonDialog.mInstance.getTag().equals("pay_service")) {
                        CommonDialog.mInstance.dismiss();
                        if (getApplicationContext() != null) {
                            ToastUtils.toast(getApplicationContext(), getString(R.string.pay_service_succed));
                        }
                    }
                }
                break;
            case EventBusId.id.GIFT_DETAIL:
                CouponDetail couponDetail = (CouponDetail) event.data;
//                Log.e("test", "card_code:" + couponDetail.getCard_code());
                if (!couponDetail.card_type.toUpperCase().equals("GIFT")) {
                    showbuyDlg(couponDetail.getCard_code());
                }
                break;
            case EventBusId.id.GIFT_SUCCESSED:
                GiftDetail giftDetail = (GiftDetail) event.data;
                Meal meal = new Meal();
                meal.setType(giftDetail.pay_type);
                meal.setAmount(giftDetail.pay_count);
                meal.setOrderSn(giftDetail.order_sn);
                PayStatus payStatus = new PayStatus();
                payStatus.setPayStatus(1);
                payStatus.setPayTime((int) System.currentTimeMillis());
                payStatus.setOrderSn(meal.getOrderSn());
                payStatus.setDeviceSn(DeviceUtil.getCupChipID());
                payStatus.setType(meal.getType());
                payStatus.setAmount(meal.getAmount());
                boolean isMealExpire = BoughtMeal.getInstance().isMealExpire();
                CommonDialog dialog1 = CommonDialog.getInstance();
                dialog1.setShowClose(true);
                dialog1.setContent(new FmPayResult());
                if (!dialog1.isAdded()) {
                    dialog1.show(getSupportedFragmentManager(), "commonDialog");
                }
                //确保支付的套餐是正确的
                meal.setAmount(payStatus.getAmount());
                meal.setType(payStatus.getType());
                mQureyHelper.reportCoinPayFinish(meal).post();
                //设置当前购买的套餐
                BoughtMeal.getInstance().setBoughtMeal(meal, payStatus);
                EventBusUtil.postPaySucceed(isMealExpire);
                break;
            case EventBusId.id.GIFT_FAIL:
                String tips = (String) event.data;
                CommonDialog dialog = CommonDialog.getInstance();
                FmFail fmFail = new FmFail();
                Bundle bundle = new Bundle();
                bundle.putString(FmFail.FAIL_TIPS, tips);
                fmFail.setArguments(bundle);
                dialog.setShowClose(true);
                dialog.setContent(fmFail);
                if (!dialog.isAdded()) {
                    dialog.show(getSupportedFragmentManager(), "commonDialog");
                }
                break;
            case EventBusId.Dialog.CHECKROOM:
                checkBoxRoom();
//                restartHeat();
                break;
            case EventBusId.Dialog.PAYSERVICE:
                showPayService();
                break;
            case EventBusId.INFARAED.RECEIVE_CODE:
                Logger.i(TAG, "OnSerialReceive Main:" + event.data + "");
                break;
        }
    }

    private void pay_sucessed(BusEvent event) {
        boolean isExpire = true;
        try {
            isExpire = Boolean.valueOf(event.data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d(TAG, "EventBusId  id PAY_SUCCEED isExpire:" + isExpire);
        //查询用户信息
        Meal meal = BoughtMeal.getInstance().getTheLastMeal();
        if (!PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
            if (meal != null) {
                mQureyHelper.queryUser().post();
                if (isExpire) {//
                    mKaraokeController.setMicMute(false);//mic静音
                    showGuideDialog();//使用帮助指引
                }
                startChooseSongTimer();
            }
        }
    }

    private PromptDialog mDialogAuth;
    private CommonDialog dialog;
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

                showbuyDlg(null);

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
//                getPayMent();
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
            case R.id.tv_progress:
                CommonDialog dlgProgress = CommonDialog.getInstance();
                dlgProgress.setShowClose(true);
                dlgProgress.setContent(new FmDownloader());
                if (!dlgProgress.isAdded()) {
                    dlgProgress.show(getSupportFragmentManager(), "commonDialog");
                }
                break;
            case R.id.tv_service:
                DlgService dlgService = new DlgService(this);
                dlgService.show();
                break;
            case R.id.tv_coupon:
                DlgCoupon dlgCoupon = new DlgCoupon(this);
//                dlgCoupon.setOnDlgTouchListener(this);
                dlgCoupon.show();
                break;
            case R.id.tv_switch:
                if (Common.isEn) {
                    switchLanguage("zh");
                } else {
                    switchLanguage("en");
                }
                PromptDialog promptDialog = new PromptDialog(Main.this);
                promptDialog.setMessage(R.string.language_different);
                promptDialog.setPositiveButton(getString(R.string.reboot), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ChooseSongs.getInstance(Main.this).cleanChoose();
//                        BoughtMeal.getInstance().clearMealInfo();
//                        BoughtMeal.getInstance().clearMealInfoSharePreference();
                        exitApp();
                    }
                });
                promptDialog.show();
                break;
            case R.id.main_tv_infrared:
                DlgAir dlgAir = new DlgAir(this);
                dlgAir.show();
                break;
        }
    }

    private void showbuyDlg(String card_code) {
        if (!NetWorkUtils.isNetworkAvailable(getApplicationContext())) {
            if (getApplicationContext() != null) {
                ToastUtils.toast(getApplicationContext(), getApplicationContext().getString(R.string.checking_network_error));
            }
        } else {
            if (PrefData.getLastAuth(getApplicationContext())) {
                CommonDialog dialog = CommonDialog.getInstance();
                dialog.setShowClose(true);
                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                dialog.setContent(FmPayMeal.createMealFragment(pageType, card_code));
                if (!dialog.isAdded()) {
                    dialog.show(getSupportFragmentManager(), "commonDialog");
                }
            } else {
                if (KBoxStatusInfo.getInstance().getKBoxStatus() != null) {
                    if (KBoxStatusInfo.getInstance().getKBoxStatus().code == 2003) {
                        PromptDialog mPromptDialog = new PromptDialog(Main.mMainActivity);
                        mPromptDialog.setPositiveButton(getString(R.string.pay_for_service), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EventBusUtil.postPayService();
                            }
                        });
                        mPromptDialog.setClose(true);
                        mPromptDialog.setCanceledOnTouchOutside(false);
                        mPromptDialog.setMessage(KBoxStatusInfo.getInstance().getKBoxStatus().msg);
                        mPromptDialog.show();
                    } else {
                        ToastUtils.toast(getApplicationContext(), KBoxStatusInfo.getInstance().getKBoxStatus().msg);
                    }
                } else {
                    ToastUtils.toast(getApplicationContext(), getString(R.string.device_auth_fail));
                }
            }

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
            transaction.show(fragmentModel.fragment).commitAllowingStateLoss();
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
        transaction.commitAllowingStateLoss();
        if (!targetFragment.fragment.isAdded()) {
            mFragmentManager.beginTransaction().add(R.id.contentPanel, targetFragment.fragment, targetFragment.tag).addToBackStack("main_frag").commitAllowingStateLoss();
            mFragments.add(targetFragment);

            mBtnBack.setVisibility(mFragments.size() > 1 ? View.VISIBLE : View.GONE);

            checkFragment(targetFragment);
        }
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

    public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
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
        if (!PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
            startChooseSongTimer();
        }
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
        if (UpLoadDataUtil.getInstance().getmUploadSongData() != null && !TextUtils.isEmpty(UpLoadDataUtil.getInstance().getmUploadSongData().getSongId())) {
            String order_sn = "";
            if (BoughtMeal.getInstance().getTheLastPaystatus() != null) {
                order_sn = BoughtMeal.getInstance().getTheLastPaystatus().getOrderSn();
            }
            SongHelper.getInstance(Main.this, null).upLoad(UpLoadDataUtil.getInstance().getmUploadSongData().getSongId(), order_sn, UpLoadDataUtil.getInstance().getmUploadSongData().getPayTime(), System.currentTimeMillis(), UpLoadDataUtil.getInstance().getmUploadSongData().getDuration(), mPresentation.getScore());
            UpLoadDataUtil.getInstance().setmUploadSongData(null);
        }
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

//        String orderSn = null;
//        if (BoughtMeal.getInstance().getTheLastPaystatus() != null) {
//            orderSn = BoughtMeal.getInstance().getTheLastPaystatus().getOrderSn();
//        }
//
        if (mPresentation == null) {
            ToastUtils.toast(Main.mMainActivity, getString(R.string.play_error2));
            return;
        }
        try {
            //重置暂停次数
            mPauseTipView.resetLeftTimes();
            mPlayingSong = song;
            mAudioChannelFlag = song.AudioTrack;
            mKaraokeController.getPlayerStatus().playingType = 1;
            float vol = song.Volume > 0 ? ((float) song.Volume / 100) : 0.8f;
//            Logger.d(TAG, "playSong" + song.SongFilePath+"|ID:"+mPlayingSong.ID);
            playUrl(ServerFileUtil.getFileUrl(song.SongFilePath), vol,0);
            BoughtMeal.getInstance().updateLeftSongs();
            if (song.IsAdSong == 1 && !TextUtils.isEmpty(song.ADID)) {
                mAdBillHelper.billAd(song.ADID, "R1", PrefData.getRoomCode(getApplicationContext()));
            }
            Logger.d(TAG, "playSong" + song.SongFilePath + "|ID:" + song.ID);
            if (UpLoadDataUtil.getInstance().getmUploadSongData() != null) {
                UpLoadDataUtil.getInstance().getmUploadSongData().setPayTime(System.currentTimeMillis());
                UpLoadDataUtil.getInstance().getmUploadSongData().setSongId(song.ID);
            }
            //减掉一首

            new increaseSongHotTask().execute(song.SongFilePath);
            handler.removeMessages(HandlerSystem.MSG_UPDATE_TIME);
            handler.sendEmptyMessage(HandlerSystem.MSG_RESET);
            handler.sendEmptyMessageDelayed(HandlerSystem.MSG_UPDATE_TIME, 100);
        } catch (Exception ex) {
            ToastUtils.toast(Main.mMainActivity, getString(R.string.play_error));
            Logger.w(TAG, "playSong ex:" + ex.toString());
//            final String path = song.SongFilePath;
            next();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    String diskPath = DiskFileUtil.getFileSavedPath(path);
//                    File file = new File(diskPath);
//                    if (file.exists() && file.isFile()) {
//                        file.delete();
//                        Logger.d(TAG, "执行删除路径:" + diskPath.toString());
//                        EventBusUtil.postSticky(EventBusId.id.CHOOSE_SONG_CHANGED, "");
//                    }
//                }
//            }, 2000);

        }
    }


    private void playUrl(String url, float volPercent,int random) throws IOException {
//        url= "http://minik.beidousat.com:2800/data/song/yyzx/fa49e8ea-8918-49f1-8ac0-917942e4cb84.mp4";
        mVolPercent = volPercent;
        if (mPresentation != null)
            mPresentation.mAdCorner = null;
        if (mPresentation != null)
            mPresentation.cleanScreen();
        if (DiskFileUtil.is901()) {
            if (player == null)
                return;
//        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.id.PLAYER_PLAY_BEGIN));
            if (player != null) {
                player.playUrl(url, mKaraokeController.getPlayerStatus().playingType == 1 ? mPlayingSong.RecordFile : null, BnsPlayer.NORMAL);
                player.setRandom(random);
            }
        } else {
            if (player_cx == null)
                return;
//        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.id.PLAYER_PLAY_BEGIN));
            if (player_cx != null) {
                Song secSong = ChooseSongs.getInstance(getApplicationContext()).getSecSong();
                player_cx.playUrl(url, mKaraokeController.getPlayerStatus().playingType == 1 ? mPlayingSong.RecordFile : null, secSong == null ? url : secSong.SongFilePath);
            }
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
        stopPlay();
        mActivePresentations.clear();
    }

    private void stopPlay() {
        if (DiskFileUtil.is901()) {
            if (player != null) {
                player.stop();
                player = null;
            }
        } else {
            if (player_cx != null) {
                player_cx.stop();
                player_cx = null;
            }
        }
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
        stopPlay();
        if (mPresentation != null) {
            if (DiskFileUtil.is901()) {
                player = new BnsPlayer(mPresentation.getSurfaceView(), main_surf, mPresentation.getHdmiWidth(), mPresentation.getHdmiHeight());
                player.setBeidouPlayerListener(this);
                player.setOnKeyInfoListener(this);
                player.setOnScoreListener(this);
            } else {
                player_cx = new OriginPlayer(mPresentation.getSurfaceView(), main_surf, this, mPresentation.getHdmiWidth(), mPresentation.getHdmiHeight());
                player_cx.setOnKeyInfoListener(this);
                player_cx.setOnScoreListener(this);
            }

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
            if (Common.isEn) {
                Toast.makeText(getApplicationContext(), "HDMI Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "HDMI 已连接", Toast.LENGTH_SHORT).show();
            }

            stopMainPlayer();
            startMainPlayer();
        }

        public void onDisplayChanged(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
            if (Common.isEn) {
                Toast.makeText(getApplicationContext(), "HDMI DisConnected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "HDMI 已断开", Toast.LENGTH_SHORT).show();
            }

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
//                Log.e("test", "score:" + mPresentation.getScore());
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
        Logger.d(TAG, "playAD");
        int random=0;
        handler.removeMessages(HandlerSystem.MSG_UPDATE_TIME);
        hideSurf();
        mAdVideo = new Ad();
        String str = PreferenceUtil.getString(this, "def_play");
        if (TextUtils.isEmpty(str) || str.equals("[]")) {
            mAdVideo.ADMovie = PublicSong.getAdVideo();
            mAdVideo.ADContent = PublicSong.getAdVideo();
        } else {
            try {
                String[] def_play = str.substring(1, str.length() - 1).split(",");
                random=PublicSong.getNum(5);
                mAdVideo.ADMovie = def_play[random].trim();
                mAdVideo.ADContent = def_play[random].trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mAudioChannelFlag = 4;
        mKaraokeController.getPlayerStatus().playingType = 0;
        mPlayingSong = null;
        Logger.d(TAG, "playUrl:" + ServerFileUtil.getFileUrl(mAdVideo.ADContent) + "|AdVideo:" + mAdVideo.ADContent);
        try {
            playUrl(mAdVideo.ADContent, 0.5f,random);
        } catch (IOException e) {
            ToastUtils.toast(getApplicationContext(), getString(R.string.play_error));
            Logger.w(TAG, "playSong ex:" + e.toString());
        }
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
        if (UpLoadDataUtil.getInstance().getmUploadSongData() != null && !TextUtils.isEmpty(UpLoadDataUtil.getInstance().getmUploadSongData().getSongId())) {
            String order_sn = "";
            if (BoughtMeal.getInstance().getTheLastPaystatus() != null) {
                order_sn = BoughtMeal.getInstance().getTheLastPaystatus().getOrderSn();
            }
            SongHelper.getInstance(Main.this, null).upLoad(UpLoadDataUtil.getInstance().getmUploadSongData().getSongId(), order_sn, UpLoadDataUtil.getInstance().getmUploadSongData().getPayTime(), System.currentTimeMillis(), UpLoadDataUtil.getInstance().getmUploadSongData().getDuration(), mPresentation.getScore());
            UpLoadDataUtil.getInstance().setmUploadSongData(null);
        }
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
                if (DiskFileUtil.is901()) {
                    if (player != null) {
                        try {
                            player.playUrl(AdDefault.getScoreResultVideo(), null, BnsPlayer.NORMAL);
                        } catch (IOException e) {
                            ToastUtils.toast(Main.mMainActivity, getString(R.string.play_error));
                            Logger.w(TAG, "playSong ex:" + e.toString());
                        }
                    }
                } else {
                    if (player_cx != null) {
                        Song secSong = ChooseSongs.getInstance(getApplicationContext()).getSecSong();
                        player_cx.playUrl(AdDefault.getScoreResultVideo(), null, secSong == null ? AdDefault.getScoreResultVideo() : secSong.SongFilePath);
                    }
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
        if(DiskFileUtil.is901()){
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
        }else{
            if (player_cx != null && player_cx.getCurrentPosition() < 5 * 1000) {
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
    }

    private void onOriginal(boolean showOnScreen) {
        if(DiskFileUtil.is901()){
            if (player != null)
                player.onOriginal(mAudioChannelFlag);
        }else{
            if (player_cx != null)
                player_cx.onOriginal(mAudioChannelFlag);
        }
        if (showOnScreen)
            if (mPresentation != null)
                mPresentation.tipOperation(R.drawable.tv_original_on, R.string.original, true);
    }

    private void onAccom(boolean showOnScreen) {
        if(DiskFileUtil.is901()){
            if (player != null)
                player.onAccom(mAudioChannelFlag);
        }else{
            if (player_cx != null)
                player_cx.onAccom(mAudioChannelFlag);
        }
        if (showOnScreen)
            if (mPresentation != null)
                mPresentation.tipOperation(R.drawable.tv_original_off, R.string.accompany, true);
    }

    private void initVol() {
        if(DiskFileUtil.is901()){
            if (player != null)
                player.setVol(mVolPercent);
        }else{
            if (player_cx != null)
                player_cx.setVol(mVolPercent);
        }
    }


    private void updateScoreViews(boolean showTvTip) {
        int mode = mKaraokeController.getPlayerStatus().scoreMode;
        if (mPresentation != null) {
            if (mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib)) {
                if(DiskFileUtil.is901()){
                    if (player != null)
                        player.setScoreOn(mode);
                }else{
                    if (player_cx != null)
                        player_cx.setScoreOn(mode);
                }
                mPresentation.showScoreView(mode);
            } else {
                mPresentation.showScoreView(0);
                if(DiskFileUtil.is901()){
                    if (player != null)
                        player.setScoreOn(0);
                }else{
                    if (player_cx != null)
                        player_cx.setScoreOn(0);
                }
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
        if(DiskFileUtil.is901()){
            if (player != null)
                player.volOn();
        }else{
            if (player_cx != null)
                player_cx.volOn();
        }
    }

    private void volOff() {
        if(DiskFileUtil.is901()){
            if (player != null)
                player.volOff();
        }else{
            if (player_cx != null)
                player_cx.volOff();
        }
    }

    private void play() {
        if(DiskFileUtil.is901()){
            if (player != null) {
                player.play();
                mKaraokeController.getPlayerStatus().isPlaying = true;
                if (mPresentation != null) {
                    mPresentation.tipOperation(0, 0, true);
                }
            }
        }else{
            if (player_cx != null) {
                player_cx.play();
                mKaraokeController.getPlayerStatus().isPlaying = true;
                if (mPresentation != null) {
                    mPresentation.tipOperation(0, 0, true);
                }
            }
        }

    }

    private void pause() {
        if(DiskFileUtil.is901()){
            if (player != null) {
                player.pause();
                mKaraokeController.getPlayerStatus().isPlaying = false;
                if (mPresentation != null) {
                    mPresentation.tipOperation(R.drawable.main_bottom_bar_pause_p, R.string.pause, false);
                }
            }
        }else{
            if (player_cx != null) {
                player_cx.pause();
                mKaraokeController.getPlayerStatus().isPlaying = false;
                if (mPresentation != null) {
                    mPresentation.tipOperation(R.drawable.main_bottom_bar_pause_p, R.string.pause, false);
                }
            }
        }
    }

    /**
     * 重播
     */
    private void replay() {
        if(DiskFileUtil.is901()){
            if (player != null) {
                Song song = ChooseSongs.getInstance(getApplicationContext()).getFirstSong();
                if (song != null) {
                    if (BoughtMeal.getInstance().isBuySong()) {
                        Logger.d(TAG, "next 2>>>>>>>");
                        BoughtMeal.getInstance().checkLeftSong();
                    }
                    playSong(song);
                } else {
                    try {
                        player.replay();
                    } catch (IOException e) {
                        ToastUtils.toast(Main.mMainActivity, getString(R.string.play_error));
                        Logger.w(TAG, "playSong ex:" + e.toString());
                    }
                }
            }
        }else{
            if (player_cx != null) {
                Song song = ChooseSongs.getInstance(getApplicationContext()).getFirstSong();
                if (song != null) {
                    if (BoughtMeal.getInstance().isBuySong()) {
                        Logger.d(TAG, "next 2>>>>>>>");
                        BoughtMeal.getInstance().checkLeftSong();
                    }
                    playSong(song);
                } else {
                    player_cx.replay();
                }
            }
        }

    }

    private void setTone(int tone) {
        if(DiskFileUtil.is901()){
            if (player != null) {
                player.setTone(tone);
            }
        }else{
            if (player_cx != null) {
                player_cx.setTone(tone);
            }
        }
    }

    private void updatePlayerStatus(PlayerStatus playerStatus) {
        boolean isPlaying = playerStatus.isPlaying;
        if (isPlaying) {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_pause, 0, 0);
            closePauseTipView();
        } else {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_play, 0, 0);
            //单机版不限制暂停次数
            if (!PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                popPauseTipView();
            }
        }
        mTvPlayerPause.setText(isPlaying ? R.string.pause : R.string.play);
        updateOriAccStatus(playerStatus);
        updateScoreViews(false);
    }

    //加载剩余次数，时间的悬浮框
    private void initPauseTipView() {
        mPauseTipView = new PauseTipView(this);
        mPauseTipView.setDettachListener(new PauseTipView.DettachWindownListener() {
            @Override
            public void onViewDettachWindow() {
                closePauseTipView();
            }
        });
        mPauseTipView.hidePauseTipView();
    }

    private void initChooseSongView() {
        mChooseSongTipView = new ChooseSongTipView(this);
        mChooseSongTipView.hideView();
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
        UIUtils.hideNavibar(getApplicationContext(), hide);
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
//            mMarqueePlayer.setVisibility(View.VISIBLE);
        } else if (FmSetting.class.getName().equals(fragment.tag) || FmSettingSerail.class.getName().equals(fragment.tag) || FmSettingInfrared.class.getName().equals(fragment.tag)) {
            mIsSetting = true;
            mViewBottom.setVisibility(View.GONE);
            mViewTop.setVisibility(View.GONE);
            mRoot.setBackgroundResource(R.drawable.bg_setting);
//            mMarqueePlayer.setVisibility(View.INVISIBLE);

        } else {
            mIsSetting = false;
            mViewBottom.setVisibility(View.VISIBLE);
            mViewTop.setVisibility(View.VISIBLE);
            mRoot.setBackgroundResource(R.drawable.bg_content);
//            mMarqueePlayer.setVisibility(View.VISIBLE);

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
        if (song == null) {
            return;
        }
        if (!song.isAD()) {
            if (song.isPrior) {
                song.isPrior = false;
                ChooseSongs.getInstance(this).add2Top(song);
            } else {
                ChooseSongs.getInstance(this).addSong(song);
            }
        }
        if (MyDownloader.getInstance().isFinishAllTask()) {
            mTvProgress.setVisibility(View.INVISIBLE);
        } else {
            mTvProgress.setVisibility(View.VISIBLE);
        }
    }

    private void performDownloadStart(float touch_x, float touch_y) {
        AnimatorUtils.playParabolaAnimator((ViewGroup) mRoot, mTvProgress, touch_x, touch_y);
    }


    @Override
    protected void onResume() {
        checkAvailable();
        SystemBroadcastSender.setVol(this, 1, 1);
        SystemBroadcastSender.setVol(this, 0, 1);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void restoreUserInfo() {
        mQureyHelper.queryUser().post();
        checkMic();
    }

    private void getBoughtMeal() {
        //关机超过5小时后，套餐清0
        long lasttime = PrefData.getLastTime(Main.this);
        long currenttime = System.currentTimeMillis();
//        Log.e("test", "lasttime:" + lasttime / (60 * 1000) + "分钟" + "   " + "currenttime：" + currenttime / (60 * 1000) + "分钟");
        if (!PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false) && currenttime - lasttime > Common.timelimit) {
//            Log.e("test", "关机超过5小时后，套餐清0");
            ChooseSongs.getInstance(getApplication()).cleanChoose();
            BoughtMeal.getInstance().clearMealInfo();
            BoughtMeal.getInstance().clearMealInfoSharePreference();
            BoughtMeal.getInstance().notifyMealObervers();
        }
        BoughtMeal.getInstance().restoreMealInfoFromSharePreference();
    }

    private void checkAvailable() {
        checkDisk();
        checkBoxId();
    }

    private PromptDialog mDlgDiskNotExit;

    private boolean checkDisk() {
        if (!DiskFileUtil.hasDiskStorage()) {
            if (mDlgDiskNotExit == null || !mDlgDiskNotExit.isShowing()) {
                mDlgDiskNotExit = new PromptDialog(this);
                mDlgDiskNotExit.setMessage(getResources().getString(R.string.hand_disk));
//                mDlgDiskNotExit.setNotClose();
                mDlgDiskNotExit.show();
            }
            return false;
        } else {
//            DiskFileUtil.getDiskAvailableSpace();
            if (mDlgDiskNotExit != null && mDlgDiskNotExit.isShowing()) {
                mDlgDiskNotExit.dismiss();
            }
        }
        return true;
    }

    private PromptDialog mDlgSetBoxId;

    private boolean checkBoxId() {
        if (TextUtils.isEmpty(PrefData.getRoomCode(getApplicationContext()))) {
            showRoomSet();
            return false;
        } else {
            return true;
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

    private void showPayService() {
        CommonDialog dialog = CommonDialog.getInstance();
        if (!dialog.isAdded()) {
            dialog.setShowClose(true);
            dialog.setContent(FmPaySevice.createPaySeviceFragment());
            dialog.show(getSupportFragmentManager(), "pay_service");
        }
    }

    private void showRoomSet() {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        dialog.setContent(FmRoomSet.createRoomSetFragment());
        if (!dialog.isAdded()) {
            dialog.show(getSupportFragmentManager(), "commonDialog");
        }
    }

    private boolean mIsSetting;

    private void restartHeat() {
        if (!TextUtils.isEmpty(PrefData.getRoomCode(this)) && ServerConfigData.getInstance().getServerConfig() != null) {
            KBoxSocketHeart kBoxSocketHeart = KBoxSocketHeart.getInstance(ServerConfigData.getInstance().getServerConfig().getStore_ip(),
                    ServerConfigData.getInstance().getServerConfig().getStore_port());
            kBoxSocketHeart.setKBoxId(PrefData.getRoomCode(this));
            kBoxSocketHeart.check();
//            Log.e("test","restart");
        }
    }

    private void checkBoxRoom() {

        new QueryKboxHelper(getApplicationContext(), null, new QueryKboxHelper.QueryKboxFeedback() {
            @Override
            public void onStart() {
                showTips(getString(R.string.getting_box_info));
            }

            @Override
            public void onFeedback(boolean suceed, String msg, Object obj) {
                hideTips();
                if (suceed) {
                    if (obj != null && obj instanceof KBox) {
                        if (PreferenceUtil.getBoolean(Main.mMainActivity, "isSingle", false)) {
                            ll_service.setVisibility(View.GONE);
                            mTvBuy.setVisibility(View.GONE);
                        } else {
                            ll_service.setVisibility(View.VISIBLE);
                            mTvBuy.setVisibility(View.VISIBLE);
                        }
                        ToastUtils.toast(getApplicationContext(), msg);
                    }
                } else {
                    ToastUtils.toast(getApplicationContext(), msg);
                }
            }
        }).getBoxInfo(PrefData.getRoomCode(getApplicationContext()));
    }

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
                checkUsbKey();
            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                checkDisk();
                checkDeviceStore();
                checkUsbKey();
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

    private void checkUsbKey() {
        boolean isStore = UsbFileUtil.isUsbExitBoxKey();
        if (isStore) {
            PrefData.setPassword(this, "666888");
            Toast.makeText(this, getString(R.string.kbox_reset_psw), Toast.LENGTH_LONG).show();
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
                        dialog.setContent(FmPayMeal.createMealFragment(pageType, null));
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


    class increaseSongHotTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            DatabaseHelper dbHelper = LanApp.getInstance().getDataBaseHelper();
            dbHelper.getSongDao().increaseSongHot(strings[0]);
            return null;
        }
    }

    class HandlerSystem extends Handler {
        private static final int MSG_RESET = 1;
        private static final int MSG_UPDATE_TIME = 2;
        private int time;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 计时器重置
                case MSG_RESET:
                    time = 0;
                    // Log.i(TAG, "handleMessage: MSG_RESET time = " + time);
                    break;
                case MSG_UPDATE_TIME:
                    // 计时器更新
                    if (mPresentation == null) {
                        return;
                    }
                    if (time > 10 && windowsfocus&&DiskFileUtil.is901()) {
                        showSurf();
                    } else {
                        time++;
                        sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                    }
                    break;

                default:
                    return;
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case KeyEvent.ACTION_DOWN:
                TouchScreen();
                break;
            case KeyEvent.ACTION_UP:
                touch_x = ev.getX();
                touch_y = ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void TouchScreen() {
        ChooseSongs chooseSongs = ChooseSongs.getInstance(getApplicationContext());
        hideSurf();
        if (BoughtMeal.getInstance().getTheFirstMeal() != null && chooseSongs.getChooseSize() > 0) {
            handler.removeMessages(HandlerSystem.MSG_UPDATE_TIME);
            handler.sendEmptyMessage(HandlerSystem.MSG_RESET);
            handler.sendEmptyMessageDelayed(HandlerSystem.MSG_UPDATE_TIME, 100);
        }
        if (!PrefData.getLastAuth(Main.this)) {
            restartHeat();
        }
//        Log.e("test","curTime:"+System.currentTimeMillis()+"|lastTime:"+PrefData.getLastTime(Main.this)+"|相差:"+String.valueOf(System.currentTimeMillis()-PrefData.getLastTime(Main.this)));
        if (NetWorkUtils.isNetworkAvailable(Main.this)) {
            if (System.currentTimeMillis() - PrefData.getLastTime(Main.this) > 10 * 60 * 60 * 1000) {
                PrefData.setAuth(Main.this, false);
                KBoxStatusInfo.getInstance().setKBoxStatus(null);
                ToastUtils.toast(getApplicationContext(), getString(R.string.device_auth_fail));
            }
        } else {
            if (System.currentTimeMillis() - PrefData.getLastTime(Main.this) > 10 * 60 * 1000) {
                PrefData.setAuth(Main.this, false);
                KBoxStatusInfo.getInstance().setKBoxStatus(null);
                ToastUtils.toast(getApplicationContext(), getString(R.string.device_auth_fail));
            }
        }

    }

    private void hideSurf() {
        if (surf_show) {
            ViewGroup.LayoutParams params = main_surf.getLayoutParams();
            params.width = 1;
            params.height = 1;
            main_surf.setLayoutParams(params);
            surf_show = false;
        }
    }

    private void showSurf() {
        if (!surf_show) {
            WindowManager manager = this.getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            int width = outMetrics.widthPixels;
            int height = outMetrics.heightPixels;
            ViewGroup.LayoutParams params = main_surf.getLayoutParams();
            params.width = width;
            params.height = height;
            main_surf.setLayoutParams(params);
            surf_show = true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ChooseSongs chooseSongs = ChooseSongs.getInstance(getApplicationContext());
        if (!windowsfocus && chooseSongs.getChooseSize() > 0) {
            handler.removeMessages(HandlerSystem.MSG_UPDATE_TIME);
            handler.sendEmptyMessage(HandlerSystem.MSG_RESET);
            handler.sendEmptyMessageDelayed(HandlerSystem.MSG_UPDATE_TIME, 100);
        }
        windowsfocus = hasFocus;
        super.onWindowFocusChanged(hasFocus);
    }

    private void getPayMent() {
        Observable<BaseEntity<List<PayMent>>> observable = RetrofitFactory.getInstance().getPayment();
        observable.compose(compose(this.<BaseEntity<List<PayMent>>>bindToLifecycle())).subscribe(new BaseObserver<List<PayMent>>(this) {
            @Override
            protected void onHandleSuccess(List<PayMent> payMentList) {
                Log.e("test", payMentList.get(0).getLogo_url());
            }
        });
    }

    private void getKboxConfig() {
        Observable<BaseEntity<KboxConfig>> observable = RetrofitFactory.getInstance().getKboxConfig();
        observable.compose(compose(this.<BaseEntity<KboxConfig>>bindToLifecycle())).subscribe(new BaseObserver<KboxConfig>(this) {
            @Override
            protected void onHandleSuccess(KboxConfig kboxConfig) {
//                Log.e("test", kboxConfig.getKbox_ip());

            }

        });

    }

    public void download() {
        File fileDes = new File(KaraokeSdHelper.getSdCard(), "testspeed.png");
        String url = "http://minorder.beidousat.com/Binary/show_img?model=Payment&fun=getlogoimage&api=kbox&path=Static&img_name=wechat.png";
        SimpleDownloader simpleDownloader = new SimpleDownloader();
        simpleDownloader.download(fileDes, url, new SimpleDownloadListener() {
            @Override
            public void onDownloadCompletion(File file, String url, long size) {
                Logger.d(TAG, "Completion:" + String.valueOf(SimpleDownloader.intervalTime));
            }

            @Override
            public void onDownloadFail(String url) {
                Logger.d(TAG, "Fail:" + String.valueOf(SimpleDownloader.intervalTime));
            }

            @Override
            public void onUpdateProgress(File mDesFile, long progress, long total) {

            }
        });

    }

}
