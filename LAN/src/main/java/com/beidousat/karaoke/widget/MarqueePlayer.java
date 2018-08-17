package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.beidousat.karaoke.ad.MarqueeGetter;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libwidget.text.MarqueeView;
import com.beidousat.libwidget.text.OnMarqueeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2015/12/12 13:50.
 */
public class MarqueePlayer extends MarqueeView implements MarqueeGetter.AdMarqueeRequestListener {

    private String mAdPosition;
    private MarqueeGetter mMarqueeGetter;
    private boolean mIsPlay;
    private List<Ad> mAds = new ArrayList<Ad>();


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

    private void requestAds() {
        if (!TextUtils.isEmpty(mAdPosition)) {
            Song song = ChooseSongs.getInstance(getContext()).getFirstSong();
            if (song != null) {
                mMarqueeGetter.getMarquee(mAdPosition, song.ID);
            } else {
                mMarqueeGetter.getMarquee(mAdPosition);
            }
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
            }, 5000);
        }
    }


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


    @Override
    public void onAdMarqueeRequest(List<Ad> ads) {
        mAds = ads;
        if (mAds != null && mAds.size() > 0) {
            playAd();
        } else {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestAds();
                    mIsPlay = true;
                }
            }, 5000);
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
