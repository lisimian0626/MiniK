package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.ad.BannerGetter;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.Common;
import com.beidousat.karaoke.widget.BannerPlayer;
import com.beidousat.karaoke.widget.WidgetMaterial;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.FragmentUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;


/**
 * Created by J Wong on 2015/12/16 16:13.
 */
public class FmMain extends BaseFragment implements RippleView.OnRippleCompleteListener,AdsRequestListener{

    private View mRootView;
    //    private AdSupporterPlayer mAdSupporterPlayer;
    private WidgetMaterial wmPinYin, wmStar, wmShow, wmScore, wmTop, wmLove;
    private ImageView ivInverted;
    private BannerGetter mBannerGetter;
    private BannerPlayer mBannerPlayer;
    private SurfaceView surfaceView;
    private boolean mIsPlaying = false;
    private ScheduledExecutorService mScheduledExecutorService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_main, null);

        wmPinYin = ((WidgetMaterial) mRootView.findViewById(R.id.wm_song));
        wmPinYin.setOnRippleCompleteListener(this);

        wmStar = ((WidgetMaterial) mRootView.findViewById(R.id.wm_singer));
        wmStar.setOnRippleCompleteListener(this);

        mBannerPlayer = ((BannerPlayer) mRootView.findViewById(R.id.ad_banner));

        wmShow = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show));
        wmShow.setOnRippleCompleteListener(this);
//
        wmTop = ((WidgetMaterial) mRootView.findViewById(R.id.wm_top));
        wmTop.setOnRippleCompleteListener(this);
//
        wmScore = ((WidgetMaterial) mRootView.findViewById(R.id.wm_score));
        wmScore.setOnRippleCompleteListener(this);
//
        wmLove = ((WidgetMaterial) mRootView.findViewById(R.id.wm_love));
        wmLove.setOnRippleCompleteListener(this);
        if (Common.isEn) {
            wmPinYin.setBackgroundResource(R.drawable.song_song_en);
            wmStar.setBackgroundResource(R.drawable.song_star_en);
            wmShow.setBackgroundResource(R.drawable.song_show_en);
            wmTop.setBackgroundResource(R.drawable.song_top_en);
            wmScore.setBackgroundResource(R.drawable.song_score_en);
            wmLove.setBackgroundResource(R.drawable.song_love_en);
            wmLove.setVisibility(View.GONE);
            wmShow.setVisibility(View.GONE);
        }

        mBannerGetter = new BannerGetter(getActivity(), this);
        mBannerGetter.getBanner("B1", DeviceUtil.getCupChipID());
//        //load image
//        mAdSupporterPlayer = (AdSupporterPlayer) mRootView.findViewById(R.id.ad_supporter);
//
//        mAdSupporterPlayer.loadAds();

        return mRootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (NetWorkUtils.isNetworkAvailable(getContext())&&mBannerPlayer != null)
//            if (hidden) {
//                //do when hidden
//                mBannerPlayer.stopBannerGetter();
//            } else {
//                //do when show
//                mBannerPlayer.startBannerGetter();
//            }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mBannerGetter = new BannerGetter(getActivity(), this);
//        if(NetWorkUtils.isNetworkAvailable(getContext()))
//        mBannerPlayer.startBannerGetter();
    }

    @Override
    public void onStop() {
        super.onStop();

//        mBannerPlayer.stopBannerGetter();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopBannerGetter();
        super.onDestroy();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.wm_song:
                FmSongCommon fmSongCommon = FmSongCommon.newInstance(new String[]{}, new String[]{});
                FragmentUtil.addFragment(fmSongCommon);
                break;
            case R.id.wm_singer:
                FragmentUtil.addFragment(new FmSinger());
                break;
            case R.id.wm_show:
                FragmentUtil.addFragment(new FmShows());
                break;
            case R.id.wm_top:
                FragmentUtil.addFragment(new FmTop());
                break;
            case R.id.wm_love:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[8], 0, 0, R.drawable.ic_love_desc));
//                fmSongCommon = FmSongCommon.newInstance(new String[]{"Type"}, new String[]{getResources().getIntArray(R.array.show_id)[8] + ""});
//                FragmentUtil.addFragment(fmSongCommon);
                break;
            case R.id.wm_score:
                fmSongCommon = FmSongCommon.newInstance(new String[]{"IsGradeLib"}, new String[]{"1"});
                FragmentUtil.addFragment(fmSongCommon);
                break;
        }
    }
    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.id.GET_CONFIG_SUCCESS:
//                Logger.d(getClass().getSimpleName(),"收到消息");
                startBannerGetter();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAdsRequestSuccess(Ad ad) {

    }

    @Override
    public void onAdsRequestFail() {
             mBannerPlayer.setVisibility(View.VISIBLE);
             surfaceView.setVisibility(View.GONE);
    }


    private void startBannerGetter() {
        if (mIsPlaying || (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown()))
            return;
        if (!mIsPlaying) {
            mScheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduleAtFixedRate(mScheduledExecutorService);
            mIsPlaying = true;
        }
    }

    private void stopBannerGetter() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
            mIsPlaying = false;
        }
    }
    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mBannerGetter.getBanner("B1", DeviceUtil.getCupChipID());
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}
