package com.beidousat.karaoke.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.andexert.library.RippleView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.WidgetMaterial;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.util.FragmentUtil;

/**
 * Created by J Wong on 2017/4/1.
 */

public class FmShows extends BaseFragment implements RippleView.OnRippleCompleteListener {

    private WidgetMaterial mWmShow1, mWmShow2, mWmShow3, mWmShow4, mWmShow5, mWmShow6, mWmShow7, mWmShow8;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_shows, null);
        mWmShow1 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show1));
        mWmShow1.setOnRippleCompleteListener(this);

        mWmShow2 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show2));
        mWmShow2.setOnRippleCompleteListener(this);

        mWmShow3 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show3));
        mWmShow3.setOnRippleCompleteListener(this);

        mWmShow4 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show4));
        mWmShow4.setOnRippleCompleteListener(this);

        mWmShow5 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show5));
        mWmShow5.setOnRippleCompleteListener(this);

        mWmShow6 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show6));
        mWmShow6.setOnRippleCompleteListener(this);

        mWmShow7 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show7));
        mWmShow7.setOnRippleCompleteListener(this);

        mWmShow8 = ((WidgetMaterial) mRootView.findViewById(R.id.wm_show8));
        mWmShow8.setOnRippleCompleteListener(this);

        return mRootView;
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.wm_show1:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[0], R.drawable.show_varietyshow01, R.drawable.show_desc_bg, R.color.show_detail_right_bg));
                break;
            case R.id.wm_show2:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[1], R.drawable.show_varietyshow02, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show3:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[2], R.drawable.show_varietyshow03, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show4:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[3], R.drawable.show_varietyshow04, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show5:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[4], R.drawable.show_varietyshow05, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show6:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[5], R.drawable.show_varietyshow06, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show7:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[6], R.drawable.show_varietyshow07, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
            case R.id.wm_show8:
                FragmentUtil.addFragment(FmShowDetail.newInstance(getResources().getIntArray(R.array.show_id)[7], R.drawable.show_varietyshow08, R.drawable.show_desc_bg, R.color.show_detail_right_bg));

                break;
        }
    }
}
