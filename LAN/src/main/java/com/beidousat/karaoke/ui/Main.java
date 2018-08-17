package com.beidousat.karaoke.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
import com.beidousat.karaoke.ad.AdBenefitGetter;
import com.beidousat.karaoke.ad.AdDefault;
import com.beidousat.karaoke.ad.PasterGetter;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PlayerStatus;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.karaoke.player.OriginPlayer;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.DlgTune;
import com.beidousat.karaoke.ui.dlg.FinishDialog;
import com.beidousat.karaoke.ui.dlg.FmPayMeal;
import com.beidousat.karaoke.ui.dlg.MngPwdDialog;
import com.beidousat.karaoke.ui.dlg.PopVersionInfo;
import com.beidousat.karaoke.ui.dlg.StepDialog;
import com.beidousat.karaoke.ui.dlg.TestDataFactory;
import com.beidousat.karaoke.ui.fragment.FmChooseList;
import com.beidousat.karaoke.ui.fragment.FmMain;
import com.beidousat.karaoke.ui.fragment.FmSearch;
import com.beidousat.karaoke.ui.presentation.PlayerPresentation;
import com.beidousat.karaoke.widget.MarqueePlayer;
import com.beidousat.karaoke.widget.MealInfoTextView;
import com.beidousat.karaoke.widget.PauseTipView;
import com.beidousat.karaoke.widget.UserInfoLayout;
import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.FragmentModel;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.score.KeyInfo;
import com.beidousat.score.NoteInfo;
import com.beidousat.score.OnKeyInfoListener;
import com.beidousat.score.OnScoreListener;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class Main extends FragmentActivity implements View.OnClickListener,
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
    private FrameLayout.LayoutParams mPauseTipViewParams;
    private AudioManager mAudioManager;

    public QueryOrderHelper mQureyHelper;

    public static Activity mMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        mMainActivity = this;
        initView();
        init();

        EventBus.getDefault().register(this);

        hideSystemUI(false);


        startMainPlayer();
        mMarqueePlayer.loadAds("Z1");
        mMarqueePlayer.startPlayer();

        startService(new Intent(getApplicationContext(), LanService.class));
    }


    @Override
    protected void onDestroy() {

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
        mUserInfoLayout = (UserInfoLayout) findViewById(R.id.ll_user);
        findViewById(R.id.iv_logo).setOnLongClickListener(this);
        initPauseTipView();
    }

    private void init() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mDisplayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        mAdBillHelper = AdBillHelper.getInstance(getApplicationContext());

        mFragmentManager = getSupportFragmentManager();
        traFragment(new FragmentModel(new FmMain()), true);

        mKaraokeController = ((LanApp) getApplicationContext()).mKaraokeController;

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

            case EventBusId.id.PLAYER_VOL_OFF:
                volOff();

                break;
            case EventBusId.id.PLAYER_VOL_ON:
                volOn();
                break;
            case EventBusId.id.PAY_SUCCEED:
                //清空已点
                ChooseSongs.getInstance(getApplicationContext()).cleanChoose();
                //清空已唱
                ChooseSongs.getInstance(getApplicationContext()).cleanSung();
                AudioRecordFileUtil.deleteRecordFiles();

                //TODO 购买套餐成功
                //查询用户信息
                mQureyHelper = new QueryOrderHelper(this, BoughtMeal.getInstance().getMeal());
                mQureyHelper.queryUser().doPost(0);


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
                PayUserInfo.getInstance().removeAllUser();
                //清空已点
                ChooseSongs.getInstance(getApplicationContext()).cleanChoose();
                //清空已已唱
                ChooseSongs.getInstance(getApplicationContext()).cleanSung();
                //删除录音文件
                AudioRecordFileUtil.deleteRecordFiles();
                //切歌到广告
                next();
                break;
        }
    }

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
                mKaraokeController.playPause();
                break;
            case R.id.tv_replay:
                mKaraokeController.replay();
                mPauseTipView.resetLeftTimes();
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
                final DlgTune dlgTune = new DlgTune(this);
                dlgTune.setOnTuneListener(new DlgTune.OnTuneListener() {
                    @Override
                    public void onMicDown() {
                        mKaraokeController.micVolDown();
                    }

                    @Override
                    public void onMicUp() {
                        mKaraokeController.micVolUp();
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
                        mKaraokeController.toneDefault();
                        dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
                    }
                });
                dlgTune.setCurrentMusicVol(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                dlgTune.setCurrentTone(mKaraokeController.getPlayerStatus().tone);
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
                    MngPwdDialog dialog2 = new MngPwdDialog(this);
                    dialog2.setOnMngPwdListener(new MngPwdDialog.OnMngPwdListener() {
                        @Override
                        public void onPass() {
                            gotoSetting();
                        }
                    });
                    dialog2.show();
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
        if (mPresentation != null)
            mPresentation.setNextSong(currentSong, nextSong);

        boolean isPlaying = mKaraokeController.getPlayerStatus().isPlaying;
        if (isPlaying) {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_pause, 0, 0);
        } else {
            mTvPlayerPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_main_play, 0, 0);
        }
        mTvPlayerPause.setText(isPlaying ? R.string.pause : R.string.play);
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

        if (BoughtMeal.getInstance().getMeal() != null) {
            if (BoughtMeal.getInstance().isBuySong()) {
                BoughtMeal.getInstance().checkLeftSong();
            }

            if (chooseSongs.getChooseSize() > 0 && BoughtMeal.getInstance().isBuySong() && BoughtMeal.getInstance().getLeftSongs() > 0) {//包曲
                playSong(chooseSongs.getFirstSong());
            } else if (chooseSongs.getChooseSize() > 0 && BoughtMeal.getInstance().isBuyTime() && BoughtMeal.getInstance().getLeftMillSeconds() > 0) {//包时
                playSong(chooseSongs.getFirstSong());
            } else {
                mTvPlayerPause.post(runAdRequest);
            }
        } else {
            mTvPlayerPause.post(runAdRequest);
        }
    }


    private void playSong(Song song) {
        try {
            mPlayingSong = song;
            mAudioChannelFlag = song.AudioTrack;
            mKaraokeController.getPlayerStatus().playingType = 1;
            float vol = song.Volume > 0 ? ((float) song.Volume / 100) : 0.8f;
            playUrl(ServerFileUtil.getFileUrl(song.SongFilePath), vol);

            if (song.IsAdSong == 1 && !TextUtils.isEmpty(song.ADID)) {
                mAdBillHelper.billAd(song.ADID, "R1");
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
            int width = getResources().getDimensionPixelOffset(R.dimen.hdmi_video_width);
            int height = getResources().getDimensionPixelOffset(R.dimen.hdmi_video_height);
            player = new OriginPlayer(mPresentation.getSurfaceView(), Main.this, width, height);

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
                        playPublicServiceAd();
                    }
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
        mTvPlayerPause.removeCallbacks(runAdRequest);
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
        if (mPresentation != null && mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib))
            mPresentation.onCurrentTimeChange(msTime);
    }


    @Override
    public void onKeyInfoCallback(KeyInfo[] infos, int totalScore) {
        if (mPresentation != null)
            mPresentation.setKeyInfos(infos);
    }

    @Override
    public void onOriginNotes(ArrayList<NoteInfo> noteInfos) {
        if (mPresentation != null && mPlayingSong != null && "1".equals(mPlayingSong.IsGradeLib))
            mPresentation.setScoreNotes(noteInfos);
    }


    private void playPublicServiceAd() {
        AdBenefitGetter adBenefitGetter = new AdBenefitGetter(getApplicationContext(), new AdsRequestListener() {
            @Override
            public void onAdsRequestSuccess(Ad ad) {
                if (ad != null && !TextUtils.isEmpty(ad.ADContent)) {
                    mAdVideo = ad;
                    mAudioChannelFlag = 4;
                    mKaraokeController.getPlayerStatus().playingType = 0;
                    playUrl(ServerFileUtil.getFileUrl(mAdVideo.ADContent), 0.5f);
                } else {
                    playStaticPublicServiceAd();
                }
            }

            @Override
            public void onAdsRequestFail() {
                playStaticPublicServiceAd();
            }
        });
        adBenefitGetter.getBenefitVideo();
    }

    //播放固定的默认公益广告
    private void playStaticPublicServiceAd() {
        mAdVideo = AdDefault.getPublicServiceAd();
        mAudioChannelFlag = 4;
        mKaraokeController.getPlayerStatus().playingType = 0;
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
    private Runnable runAdRequest = new Runnable() {
        @Override
        public void run() {
            playAds();
        }
    };


    /**
     * 无歌曲时播放的广告
     */
    private void playAds() {
        mCurPastAdPosition = "T1";

        mPlayingSong = null;
        if (mPresentation != null)
            mPresentation.cleanScreen();
        PasterGetter pasterGetter = new PasterGetter(getApplicationContext(), new AdsRequestListener() {
            @Override
            public void onAdsRequestFail() {
                int size = ChooseSongs.getInstance(getApplicationContext()).getChooseSize();
                if (size <= 0) {
                    mTvPlayerPause.removeCallbacks(runAdRequest);
                    mAdVideo = AdDefault.getPatchDefaultAd();
                    mAudioChannelFlag = 4;
                    mKaraokeController.getPlayerStatus().playingType = 0;
                    playUrl(ServerFileUtil.getFileUrl(mAdVideo.ADContent), 0.5f);
                }
            }

            @Override
            public void onAdsRequestSuccess(Ad ad) {
                int size = ChooseSongs.getInstance(getApplicationContext()).getChooseSize();
                if (size <= 0 && ad != null) {
                    mAdVideo = ad;
                    mAudioChannelFlag = 4;
                    mKaraokeController.getPlayerStatus().playingType = 0;
                    playUrl(ServerFileUtil.getFileUrl(mAdVideo.ADContent), 0.5f);
                }
            }
        });
        pasterGetter.getPaster(mCurPastAdPosition);

    }


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
                boolean isStart1 = Math.abs(progress - 1 * 1000) <= 2 * TIMER_INTERVAL;//开始1秒后
                if (mPresentation != null && isStart1) {
                    mPresentation.showStartAd(true);
                }
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

                boolean isLas5s = Math.abs(duration - progress - 5 * 1000) <= 2 * TIMER_INTERVAL;//结束前5秒
                if (mPresentation != null && isLas5s) {
                    mPresentation.showLast5SecAd(true);
                }
            }

        } else if (mKaraokeController.getPlayerStatus().playingType == 0 || mKaraokeController.getPlayerStatus().playingType == 4) {//广告
            try {
                if (mPresentation != null) {
                    mPresentation.mTvPasterTimer.setVisibility(View.VISIBLE);
                    mPresentation.mTvPasterTimer.setText(Html.fromHtml(getString(R.string.ad_past_time, (duration - progress) / 1000)));
                }
                if (progress > 0 && progress - mPasterBillProgress >= 5 * 1000 && (progress / 1000) % 5 == 0) {
                    mPasterBillProgress = progress;
                    if (mAdVideo != null && !TextUtils.isEmpty(mCurPastAdPosition))
                        mAdBillHelper.billAd(mAdVideo, mCurPastAdPosition);
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
        if (mPauseTipView.canShow() && BoughtMeal.getInstance().isBuySong()) {
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


    private void gotoSetting() {

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
                break;
        }
        return false;
    }
}
