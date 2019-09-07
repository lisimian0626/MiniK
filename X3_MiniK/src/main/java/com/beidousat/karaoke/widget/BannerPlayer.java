package com.beidousat.karaoke.widget;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.ad.BannerGetter;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.DlgWebView;
import com.beidousat.karaoke.ui.fragment.FmBannerDetail;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.beidousat.libwidget.viewpager.ViewPagerScroller;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by J Wong on 2015/12/11 19:59.
 */
public class BannerPlayer extends JazzyViewPager {

    private static final int VIEW_PAGER_SCROLL_DURATION = 1000;

    private Context mContext;
//    private BannerGetter mBannerGetter;
    private String mAdPosition;
    private int mPlaceholder;
    private MainAdapter mAdapter;
    private boolean mIsPlaying = false;
    private static final int MAX_IMG_COUNT = 5;
    private List<Uri> mImageUrls = new ArrayList<Uri>();
    private BannerInfo bannerInfo;
    public BannerPlayer(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public BannerPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        if(Common.isEn){
            this.mPlaceholder = R.drawable.ad_banner_default_en;
        }else{
            this.mPlaceholder = R.drawable.ad_banner_default;
        }
        ViewPagerScroller.setViewPagerScrollDuration(this, VIEW_PAGER_SCROLL_DURATION);
        String[] effects = getResources().getStringArray(R.array.jazzy_effects);
        TransitionEffect effect = TransitionEffect.valueOf(effects[effects.length - 1]);
        setTransitionEffect(effect);

        mAdapter = new MainAdapter(mContext);
        setAdapter(mAdapter);
//        mBannerGetter = new BannerGetter(mContext, this);

    }

//    public void loadAds(String position) {
//        mAdPosition = position;
//        if(Common.isEn){
//            this.mPlaceholder = R.drawable.ad_banner_default_en;
//        }else{
//            this.mPlaceholder = R.drawable.ad_banner_default;
//        }
//
//    }


//    private void requestAds() {
//            mBannerGetter.getBanner(mAdPosition, DeviceUtil.getCupChipID());
//    }

    public void setBannerInfo(BannerInfo bannerInfo) {
        this.bannerInfo=bannerInfo;
        loadImage(ServerFileUtil.getImageUrl(bannerInfo.getImg_url()));
    }

    public void loadImage(Uri uri) {
        mImageUrls.add(uri);
        mAdapter.setAds(mImageUrls);
    }

//    @Override
//    public void onAdsRequestFail() {
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                setBackgroundResource(mPlaceholder);
//            }
//        });
//    }
//
//    @Override
//    public void onAdsRequestSuccess(final Ad ad) {
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                if (ad != null) {
//                    if (mLsAds.size() > MAX_IMG_COUNT) {
//                        mLsAds.clear();
//                        mImageUrls.clear();
//                    }
//                    mLsAds.add(ad);
//                    Uri uri = ServerFileUtil.getImageUrl(ad.ADContent);
//                    mImageUrls.add(uri);
//                    mAdapter.setAds(mImageUrls);
//                    setCurrentItem(mAdapter.getCount() - 1);
//                }
//            }
//        });
//    }


    private ScheduledExecutorService mScheduledExecutorService;

//    public void startPlayer() {
//        if (mIsPlaying || (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown()))
//            return;
//        if (!mIsPlaying) {
//            mScheduledExecutorService = Executors.newScheduledThreadPool(1);
//            scheduleAtFixedRate(mScheduledExecutorService);
//            mIsPlaying = true;
//        }
//    }

//    public void stopPlayer() {
//        if (mScheduledExecutorService != null) {
//            mScheduledExecutorService.shutdownNow();
//            mScheduledExecutorService = null;
//            mIsPlaying = false;
//        }
//    }

//    private void scheduleAtFixedRate(ScheduledExecutorService service) {
//        service.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                BannerPlayer.this.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        requestAds();
//                    }
//                });
//            }
//        }, 0, 1, TimeUnit.HOURS);
//    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            stopPlayer();
//        } else {
//            startPlayer();
//        }
        return true;
    }
    private boolean mIsCorner = true;


    public void setCorner(boolean isCorner) {
        mIsCorner = isCorner;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }

    private class MainAdapter extends PagerAdapter {

        private List<Uri> mImgUrls = new ArrayList<Uri>();

        private Context mContext;

        public MainAdapter(Context context) {
            mContext = context;
        }

        public void setAds(List<Uri> ulrs) {
            mImgUrls = ulrs;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImgUrls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            RecyclerImageView imageView = new RecyclerImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setObjectForPosition(imageView, position);

            Uri imageUrl = mImgUrls.get(position);
            if (imageUrl == null) {
                imageView.setImageResource(mPlaceholder);
            } else {
                if (mIsCorner) {
                    Glide.with(mContext).load(imageUrl).override(380, 380).fitCenter().error(mPlaceholder)
                            .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0, RoundedCornersTransformation.CornerType.ALL)).into(imageView);
                } else {
                    Glide.with(mContext).load(imageUrl).override(380, 380).fitCenter().placeholder(mPlaceholder).into(imageView);
                }
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (bannerInfo!=null&&!TextUtils.isEmpty(bannerInfo.getAction_type())) {
                            switch (bannerInfo.getAction_type()){
                                case "1":
                                    //打开网页
                                    DlgWebView dlgWebView=new DlgWebView(mContext,bannerInfo.getAction_url());
                                    dlgWebView.show();
                                    break;
                                case "2":
                                    //打开歌单
//                                    FragmentUtil.addFragment(FmBannerDetail.newInstance(ad));
                                    break;
                            }

                        }
                    } catch (Exception e) {
                        Logger.w(getClass().getSimpleName(), "setOnClickListener ex:" + e.toString());
                    }
                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView(findViewFromObject(position));
        }


        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }
    }
}
