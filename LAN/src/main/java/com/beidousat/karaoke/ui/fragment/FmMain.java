package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.BannerPlayer;
import com.beidousat.karaoke.widget.WidgetMaterial;
import com.beidousat.libbns.amin.CubeAnimation;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.amin.PushPullAnimation;
import com.beidousat.libbns.util.FragmentUtil;


/**
 * Created by J Wong on 2015/12/16 16:13.
 */
public class FmMain extends BaseFragment implements RippleView.OnRippleCompleteListener {

    private View mRootView;
    //    private AdSupporterPlayer mAdSupporterPlayer;
    private WidgetMaterial wmPinYin, wmStar, wmShow, wmScore, wmTop, wmLove;
    private ImageView ivInverted;
    private BannerPlayer mBannerPlayer;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_main, null);

        wmPinYin = ((WidgetMaterial) mRootView.findViewById(R.id.wm_song));
        wmPinYin.setOnRippleCompleteListener(this);

        wmStar = ((WidgetMaterial) mRootView.findViewById(R.id.wm_singer));
        wmStar.setOnRippleCompleteListener(this);

        mBannerPlayer = ((BannerPlayer) mRootView.findViewById(R.id.ad_banner));

        mBannerPlayer.loadAds("B1");

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
//        //load image
//        mAdSupporterPlayer = (AdSupporterPlayer) mRootView.findViewById(R.id.ad_supporter);
//
//        mAdSupporterPlayer.loadAds();

        return mRootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mBannerPlayer != null)
            if (hidden) {
                //do when hidden
                mBannerPlayer.stopPlayer();
            } else {
                //do when show
                mBannerPlayer.startPlayer();
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBannerPlayer.startPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBannerPlayer.stopPlayer();
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


}
