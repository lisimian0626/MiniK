package com.beidousat.karaoke.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.ad.AdPauseGetter;
import com.beidousat.karaoke.ad.CornerGetter;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.downLoadInfo;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.udp.UDPComment;
import com.beidousat.karaoke.widget.MarqueePlayer;
import com.beidousat.karaoke.widget.WidgetScore;
import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.amin.CubeAnimation;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.DownloadBusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.beidousat.libwidget.progress.NumberProgressBar;
import com.bumptech.glide.Glide;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import de.greenrobot.event.EventBus;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by J Wong on 2015/10/9 17:19.
 */
public class PlayerPresentation extends Presentation implements AdsRequestListener {

    private static final String TAG = "PlayerPresentation";
    private BridgeWebView mWebView;
    MyWebChromeClient mWebChromeClient;
    private SurfaceView surfaceView;
    private TextView mTvCenter;
    private Context mContext;
//    private MarqueePlayer mMarqueePlayer;
    private RecyclerImageView mIvAdCorner;

    public TextView mTvPasterTimer;

    private TextView mTvNextSong, mTvCurrent,mTvTips;

    private CornerGetter mCornerGetter;

    private static final int AD_CORNER_INTERVAL = 15 * 1000;

    private AdBillHelper mAdBillHelper;

    public Ad mAdCorner;

//    private VoiceGradeView voiceGradeView;
//    private ReceiveMicData receiveMicData;

    //    private TextView mTvScore;
    private TextView mTvResultScore, mTvResultSong, mTvResultPercent;
    //    private TextView mTvScoreMode;
    private RecyclerImageView ivImage;
    private View mViewVol;
    private NumberProgressBar mNpbVol;

    private int mScore;
    private View mViewAdCorner;
    private Animation mAnimCornerIn, mAnimCornerOut;

    private RecyclerImageView mIvAdPasue;
//            , mIvAdLast;

    private AdPauseGetter mAdPauseGetter;
    //    private AdStartEndGetter mAdStartGetter;
//    private AdStartEndGetter mAdEndGetter;
    private WidgetScore mWidgetScore;

    private int mHdmiW = 1920;
    private int mHdmiH = 1080;

    private RelativeLayout mRootView;
    private CountDownTimer countDownTimer;

    private ImageView qr_code;

    public PlayerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        mContext=outerContext;
        Point realSize = new Point();
        display.getRealSize(realSize);
        mHdmiW = realSize.x;
        mHdmiH = realSize.y;
//        PayUserInfo.getInstance().setHdmi_width(mHdmiW);
//        PayUserInfo.getInstance().setHdmi_width(mHdmiH);
    }
    public void setScreenSize(int with,int high){
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = with;
        params.height = high;
        surfaceView.setLayoutParams(params);
    }
    public SurfaceView getSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        return surfaceView;
    }

    public int getHdmiWidth() {
        return mHdmiW;
    }

    public int getHdmiHeight() {
        return mHdmiH;
    }

//    public FrameLayout getSurfaceParent() {
//        return null;
//    }

    public WidgetScore getWidgetScore() {
        return mWidgetScore;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdBillHelper = AdBillHelper.getInstance(getContext().getApplicationContext());
        mAnimCornerOut = MoveAnimation.create(MoveAnimation.RIGHT, false, 1000);
        mAnimCornerIn = MoveAnimation.create(MoveAnimation.LEFT, true, 1000);
        //角标
        mCornerGetter = new CornerGetter(getContext().getApplicationContext(), this);
        //暂停广告
        mAdPauseGetter = new AdPauseGetter(getContext().getApplicationContext(), mAdPauseListener);
        //片头广告
//        mAdStartGetter = new AdStartEndGetter(getContext().getApplicationContext(), mAdStartListener);
        //片尾广告
//        mAdEndGetter = new AdStartEndGetter(getContext().getApplicationContext(), mAdEndListener);
        setContentView(R.layout.player_presentation_stb);
        mRootView = (RelativeLayout) findViewById(R.id.root);
        //手机点歌二维码
        qr_code= (ImageView)findViewById(R.id.player_qr_code);
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
        linearParams.height = mHdmiH;
        linearParams.width = mHdmiW;
        mRootView.setLayoutParams(linearParams);
        mWebView= (BridgeWebView) findViewById(R.id.webview);
        mViewAdCorner = findViewById(R.id.ll_next);
        mTvResultScore = (TextView) findViewById(R.id.tv_result_score);
        mTvResultSong = (TextView) findViewById(R.id.tv_result_song_name);
        mTvResultPercent = (TextView) findViewById(R.id.tv_beat_percent);
        ivImage = (RecyclerImageView) findViewById(R.id.iv_image);
        mIvAdPasue = (RecyclerImageView) findViewById(R.id.iv_ad_pause);
//        mIvAdLast = (RecyclerImageView) findViewById(R.id.iv_ad_last);
//        mMarqueePlayer = (MarqueePlayer) findViewById(R.id.ads_marquee);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
//        mTvScore = (TextView) findViewById(R.id.tv_score);
//        mTvScoreMode = (TextView) findViewById(R.id.tv_score_mode);
//
//        voiceGradeView = (VoiceGradeView) findViewById(R.id.voicegrade);
//        receiveMicData = voiceGradeView;
        mWidgetScore = (WidgetScore) findViewById(R.id.visualizerView);


        mTvNextSong = (TextView) findViewById(R.id.tv_next_song);
        mTvCurrent = (TextView) findViewById(R.id.tv_current_song);
        mViewVol = findViewById(R.id.ll_vol);
        mNpbVol = (NumberProgressBar) findViewById(R.id.npb_vol);

        mIvAdCorner = (RecyclerImageView) findViewById(R.id.iv_ads);
        mTvCenter = (TextView) findViewById(R.id.tv_player_center);
        mTvPasterTimer = (TextView) findViewById(R.id.tv_timer);
        mTvTips= (TextView) findViewById(R.id.player_tv_tips);
        EventBus.getDefault().register(this);

//        mMarqueePlayer.loadAds("Z2");
        setSize();

        initWebView();
    }
    public void PlayWebView(String url){
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = 1;
        params.height = 1;
        surfaceView.setLayoutParams(params);
        initWebView();
        initJsBridge();
        mWebView.loadUrl(url);
        mWebView.setVisibility(View.VISIBLE);
        Function();
    }
    private void initWebView() {
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //适配网页宽高
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
    }
    private void initJsBridge() {
        mWebChromeClient = new MyWebChromeClient(mWebView) ;
        mWebView.setWebViewClient(mWebChromeClient);
        mWebView.setDefaultHandler(new DefaultHandler());
    }

    private void Function() {
        mWebView.registerHandler(Common.INTERFACE_CLOSEWINDOWS, new BridgeHandler() {
            @Override
            public void handler(String s, CallBackFunction callBackFunction) {
                Logger.d("test","close:"+s);
                EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT,"");
            }

        });

    }

    private void setSize() {
    }

    private boolean mIsPlayingMoving;

    public void showPauseAd(boolean show, boolean isMovie) {
        mIsPlayingMoving = isMovie;
        if (show && !mIvAdPasue.isShown()) {
            Song song = ChooseSongs.getInstance(getContext().getApplicationContext()).getFirstSong();
            if (isMovie) {
                mAdPauseGetter.getMovieStopAd(song != null ? song.ID : "", KBoxInfo.getInstance().getKBox() != null ? KBoxInfo.getInstance().getKBox().getArea() : null);
            } else {
                mAdPauseGetter.getSongStopAd(song != null ? song.ID : "", KBoxInfo.getInstance().getKBox() != null ? KBoxInfo.getInstance().getKBox().getArea() : null);
            }

        } else if (!show && mIvAdPasue.isShown()) {
            mIvAdPasue.setVisibility(View.GONE);
        }
    }

//    public void showLast5SecAd(boolean show) {
//        if (show && !mIvAdLast.isShown()) {
//            Song song = ChooseSongs.getInstance(getContext().getApplicationContext()).getFirstSong();
//            mAdEndGetter.getEnd(song != null ? song.ID : "", KBoxInfo.getInstance().getKBox() != null ? KBoxInfo.getInstance().getKBox().getArea() : null);
//        } else if (!show && mIvAdLast.isShown()) {
//            mIvAdLast.setVisibility(View.GONE);
//        }
//    }


    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
        mWidgetScore.setScore(score);
    }


    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mMarqueePlayer.stopPlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mTvCenter.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mMarqueePlayer.startPlayer();
//            }
//        }, 10 * 1000);
    }

//    public void onCurrentTimeChange(long time) {
//        voiceGradeView.currentTime = (float) time / 1000;
//        if (voiceGradeView.isShown())
//            voiceGradeView.drawView();
//    }

    public void tipOperation(int resId, int resText, boolean autoDismiss) {
        tipOperation(resId, resText <= 0 ? "" : getContext().getString(resText), autoDismiss);
    }

    public void tipOperation(int resId, String text, boolean autoDismiss) {
        mTvCenter.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        mTvCenter.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
        if (TextUtils.isEmpty(text)) {
            mTvCenter.setText("");
        } else {
            mTvCenter.setText(text);
        }
        mTvCenter.removeCallbacks(runOperation);
        if (autoDismiss) {
            mTvCenter.postDelayed(runOperation, 2000);
        }
    }

    public void showMusicVol() {
        mViewVol.setVisibility(View.VISIBLE);
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        //音量控制,初始化定义
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mNpbVol.setMax(maxVolume);
        mNpbVol.setProgress(current);

        mNpbVol.removeCallbacks(runnableVolDismiss);
        mNpbVol.postDelayed(runnableVolDismiss, 5000);
    }

    private void showImage(String url) {
        Glide.with(getContext()).load(ServerFileUtil.getImageUrl(url)).override(500, 650).centerCrop().skipMemoryCache(true).into(ivImage);
        ivImage.setVisibility(View.VISIBLE);
        ivImage.removeCallbacks(runnableImageDismiss);
        ivImage.postDelayed(runnableImageDismiss, 10 * 1000);
    }

    public void showQrCode(){
        if(!TextUtils.isEmpty(UDPComment.QRcode)){
            qr_code.setVisibility(View.VISIBLE);
            qr_code.setImageBitmap(QrCodeUtil.createQRCode(UDPComment.QRcode));
            mTvTips.setText(R.string.qrcode_tips);
        }else{
            qr_code.setVisibility(View.GONE);
            mTvTips.setText("");
        }

    }

    private Runnable runnableVolDismiss = new Runnable() {
        @Override
        public void run() {
            mViewVol.setVisibility(View.GONE);
        }
    };


    private Runnable runnableImageDismiss = new Runnable() {
        @Override
        public void run() {
            ivImage.setImageResource(0);
            ivImage.setVisibility(View.GONE);
        }
    };

    public Runnable runOperation = new Runnable() {
        @Override
        public void run() {
            mTvCenter.setVisibility(View.GONE);
        }
    };

    public void playMarquee(String text, boolean isRepeat) {
//        mMarqueePlayer.playMsg(text);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.id.SHOW_QR_CODE:
                showImage(event.data.toString());
                break;
            case EventBusId.id.TV_AD_CORNER_SHOW:
                mViewAdCorner.setVisibility(Boolean.valueOf(event.data.toString()).booleanValue() ? View.VISIBLE : View.GONE);
                break;
            case EventBusId.Download.FINISH:
                mTvTips.setText(mContext.getString(R.string.qrcode_tips));
                break;
            case EventBusId.Download.PROGRESS:
                 if(event instanceof DownloadBusEvent){
                     DownloadBusEvent downloadBusEvent= (DownloadBusEvent) event;
                     mTvTips.setText(mContext.getString(R.string.download_tips,downloadBusEvent.songName)+"   "+(int)(downloadBusEvent.percent)+"%");
                 }

                break;
        }
    }


    public void setNextSong(Song curSong, Song nextSong) {
        if (curSong != null) {
            mTvCurrent.setText(getContext().getString(R.string.current_x, curSong.SimpName));
        } else {
            mTvCurrent.setText("");
        }
        if (nextSong != null) {
            mTvNextSong.setText(getContext().getString(R.string.next_x, nextSong.SimpName));
        } else {
            mTvNextSong.setText("");
        }
    }

    public void showCurNextSong() {
        if (mViewAdCorner.isShown())
            return;
        showAds(true);
    }

    private void showAds(final boolean show) {
        if ((mViewAdCorner.isShown() && show) || !show && !mViewAdCorner.isShown())
            return;
        if (show) {
            if (mAdCorner != null) {
                showCorner();
            } else {
                Song song = ChooseSongs.getInstance(getContext().getApplicationContext()).getFirstSong();
                mCornerGetter.getCorner("J1", song != null ? song.ID : "", KBoxInfo.getInstance().getKBox() != null ?
                        KBoxInfo.getInstance().getKBox().getArea() : null);
            }
        } else {
            mViewAdCorner.setVisibility(View.GONE);
            mViewAdCorner.startAnimation(mAnimCornerOut);
        }
    }


    public void cleanScreen() {
        if(mWebView.getVisibility()==View.VISIBLE){
            mWebView.setVisibility(View.GONE);
            mWebView.registerHandler(Common.INTERFACE_CLOSEWINDOWS, new BridgeHandler() {
                @Override
                public void handler(String s, CallBackFunction callBackFunction) {
                    Logger.d("test","close:"+s);
                }
            });
            ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            params.width = mHdmiW;
            params.height = mHdmiH;
            surfaceView.setLayoutParams(params);
        }
        findViewById(R.id.rl_score_result).setVisibility(View.GONE);
        mTvCenter.setVisibility(View.GONE);
        tipOperation(0, 0, false);
        mViewAdCorner.setVisibility(View.GONE);
//        showLast5SecAd(false);
//        showPauseAd(false, false);
//        mIvAdLast.setVisibility(View.GONE);
        showScoreView(0);
    }

    private void showCorner() {
        mViewAdCorner.setVisibility(View.VISIBLE);
        mViewAdCorner.startAnimation(mAnimCornerIn);
        if (mAdCorner == null) {
            return;
        }
        Uri uri = ServerFileUtil.getImageUrl(mAdCorner.ADContent);
        if(Common.isEn){
            Glide.with(getContext()).load(uri)
                    .override(300, 500).centerCrop().error(R.drawable.ad_corner_default_en)
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 0, RoundedCornersTransformation.CornerType.ALL)).skipMemoryCache(true).into(mIvAdCorner);
        }else{
            Glide.with(getContext()).load(uri)
                    .override(300, 500).centerCrop().error(R.drawable.ad_corner_default)
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 0, RoundedCornersTransformation.CornerType.ALL)).skipMemoryCache(true).into(mIvAdCorner);
        }


        mViewAdCorner.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAds(false);
            }
        }, AD_CORNER_INTERVAL);
        mAdBillHelper.billAd(mAdCorner, "J1", PrefData.getRoomCode(getContext().getApplicationContext()));
    }

    @Override
    public void onAdsRequestSuccess(Ad ad) {
        if (ad != null) {
            mAdCorner = ad;
            showCorner();
        }
    }

    @Override
    public void onAdsRequestFail() {
    }


//    public void setScoreNotes(ArrayList<NoteInfo> infos) {
//        if (infos != null) {
//            VoiceDataSources dataSources = new VoiceDataSources();
//            for (NoteInfo info : infos) {
//                dataSources.add(info);
//                dataSources.setMinY(dataSources.getMinY() > info.key ? info.key : dataSources.getMinY());
//                dataSources.setMaxY(dataSources.getMaxY() < info.key ? info.key : dataSources.getMaxY());
//            }
//            voiceGradeView.setDataSources(dataSources);
//        }
//    }

//    public void setKeyInfos(KeyInfo[] infos) {
//        receiveMicData.onReceiveMicData(infos);
//    }


    public void showScoreView(int mode) {
        boolean show = mode != 0;
        mWidgetScore.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        if (show) {
            mWidgetScore.startFlake();
        } else {
            mWidgetScore.release();
        }

    }

    public void showScoreResult(Song song, int score, String beatPercent) {
        findViewById(R.id.rl_score_result).setVisibility(View.VISIBLE);
        mTvResultScore.setText(String.valueOf(score));
        mTvResultPercent.setText(String.valueOf(beatPercent));
        mTvResultSong.setText(song.SimpName);
    }


    public void setDeviceStatus(String text) {
        TextView textView = (TextView) findViewById(R.id.tv_device_status);
        textView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(text))
            textView.setText(text);
    }
//
//    private AdsRequestListener mAdStartListener = new AdsRequestListener() {
//        @Override
//        public void onAdsRequestSuccess(Ad ad) {
//            if (ad != null && !TextUtils.isEmpty(ad.ADContent)) {
//                mIvAdLast.setVisibility(View.VISIBLE);
//                Glide.with(getContext()).load(ServerFileUtil.getImageUrl(ad.ADContent)).skipMemoryCache(true).into(mIvAdLast);
//                mAdBillHelper.billAd(ad, "W1", PrefData.getRoomCode(getContext().getApplicationContext()));
//            } else {
//                Logger.d(TAG, "mAdStartListener onAdsRequestSuccess ad is null:");
//            }
//            mIvAdLast.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mIvAdLast.setVisibility(View.GONE);
//                }
//            }, 5000);
//        }
//
//        @Override
//        public void onAdsRequestFail() {
//            mIvAdLast.setVisibility(View.VISIBLE);
//            Glide.with(getContext()).load(R.drawable.tv_ad_last).skipMemoryCache(true).into(mIvAdLast);
//            mIvAdLast.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mIvAdLast.setVisibility(View.GONE);
//                }
//            }, 5000);
//        }
//    };

//    private AdsRequestListener mAdEndListener = new AdsRequestListener() {
//        @Override
//        public void onAdsRequestSuccess(Ad ad) {
//            if (ad != null && !TextUtils.isEmpty(ad.ADContent)) {
//                mIvAdLast.setVisibility(View.VISIBLE);
//                Glide.with(getContext().getApplicationContext()).load(ServerFileUtil.getImageUrl(ad.ADContent)).skipMemoryCache(true).into(mIvAdLast);
//                mAdBillHelper.billAd(ad, "W2", PrefData.getRoomCode(getContext().getApplicationContext()));
//            } else {
//                Logger.d(TAG, "mAdEndListener onAdsRequestSuccess ad is null:");
//            }
//            mIvAdLast.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mIvAdLast.setVisibility(View.GONE);
//                }
//            }, 5000);
//        }
//
//        @Override
//        public void onAdsRequestFail() {
//            mIvAdLast.setVisibility(View.VISIBLE);
//            Glide.with(getContext()).load(R.drawable.tv_ad_last).skipMemoryCache(true).into(mIvAdLast);
//            mIvAdLast.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mIvAdLast.setVisibility(View.GONE);
//                }
//            }, 5000);
//        }
//    };

    private AdsRequestListener mAdPauseListener = new AdsRequestListener() {
        @Override
        public void onAdsRequestSuccess(Ad ad) {
            if (ad != null && !TextUtils.isEmpty(ad.ADContent)) {
                mIvAdPasue.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(ServerFileUtil.getImageUrl(ad.ADContent)).skipMemoryCache(true).into(mIvAdPasue);
                mAdBillHelper.billAd(ad, mIsPlayingMoving ? "S2" : "S1", PrefData.getRoomCode(getContext().getApplicationContext()));
            }
        }

        @Override
        public void onAdsRequestFail() {
            mIvAdPasue.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.tv_ad_pause).skipMemoryCache(true).into(mIvAdPasue);
            Animation anim = CubeAnimation.create(CubeAnimation.LEFT, true, 300);
            mIvAdPasue.startAnimation(anim);
        }
    };


//    public void showStartAd(boolean show) {
//        if (show && !mIvAdLast.isShown()) {
//            Song song = ChooseSongs.getInstance(getContext().getApplicationContext()).getFirstSong();
//            mAdStartGetter.getStart(song != null ? song.ID : "", KBoxInfo.getInstance().getKBox() != null ? KBoxInfo.getInstance().getKBox().getArea() : null);
//        } else if (!show && mIvAdLast.isShown()) {
//            mIvAdLast.setVisibility(View.GONE);
//        }
//    }

}

