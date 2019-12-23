package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.beidousat.karaoke.ad.MarqueeGetter;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libwidget.text.MarqueeView;
import com.beidousat.libwidget.text.OnMarqueeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 电视走马灯广告
 * <p>
 * Created by J Wong on 2015/12/12 13:50.
 */
public class MarqueePlayer extends MarqueeView implements MarqueeGetter.AdMarqueeRequestListener {

    private String mAdPosition;
    private MarqueeGetter mMarqueeGetter;
    private boolean mIsPlay;
    private List<Ad> mAds = new ArrayList<Ad>();

    private final static int INTERVAL = 60 * 1000;


    public MarqueePlayer(Context context) {
        super(context);
        init();
    }

    public MarqueePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnMarqueeListener(new OnMarqueeListener() {
            @Override
            public void onRollOver() {
                setText("");
                reset();
                if (mIsPlay) {
                    MarqueePlayer.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsPlay) {
                                playAd();
                            }
                        }
                    }, INTERVAL);
                }
                if (mIsPlay)
                    playAd();
            }
        });
        mMarqueeGetter = new MarqueeGetter(getContext(), this);
        setZOrderOnTop(true);
    }


    public void loadAds(String position) {
        mAdPosition = position;
    }


    /**
     * 查询走马灯信息
     */
    private void requestAds() {
        if (!TextUtils.isEmpty(mAdPosition)) {
            Song song = ChooseSongs.getInstance(getContext()).getFirstSong();
//            mMarqueeGetter.getMarquee(mAdPosition, song == null ? null : song.ID, KBoxInfo.getInstance().getKBox() != null
//                    ? KBoxInfo.getInstance().getKBox().getArea() : null);
            mMarqueeGetter.getMarquee(mAdPosition, DeviceUtil.getCupChipID());
        }
    }

    public void startPlayer() {
        if (mIsPlay)
            return;
        mIsPlay = true;
        requestAds();
    }

    public void stopPlayer() {
        setText("");
        reset();
        mIsPlay = false;
        stopScroll();
    }


    public void setText(int resId) {
        setText(getContext().getString(resId));
    }


    /**
     * 请求失败时进行的操作
     */
    @Override
    public void onAdMarqueeRequestFail() {
        if (mIsPlay) {
            mIsPlay = false;
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestAds();
                    mIsPlay = true;
                }
            }, INTERVAL);
        }
    }


    /**
     * 请求成功时的操作
     */
    @Override
    public void onAdMarqueeRequestSuccess(BannerInfo ads) {
        if (ads == null) return;
        if ("3".equals(ads.getMedia_type()) && "Z2".equals(ads.getPcode())) {
            playMsg(ads.getText());
        }
        return;
    }

    /**
     * 播放走马灯
     */
    public void playAd() {
        if (mAds != null && mAds.size() > 0) {
            String text = mAds.get(0).ADContent;
            if (mAds.get(0).IsChanged == 1) {
                setText(Color.YELLOW, text);
            } else {
                setText(Color.WHITE, text);
            }
            mAds.remove(0);
//            stopScroll();
            startScroll();
        } else {
            requestAds();
        }
    }

    public void playMsg(String msg) {
        Ad ad = new Ad();
        ad.ID = String.valueOf(-1);
        ad.ADContent = msg;
        ad.IsChanged = 1;
        if (mAds == null) {
            mAds = new ArrayList<>();
        }
        mAds.add(0, ad);
        setText("");
        playAd();
    }
}
