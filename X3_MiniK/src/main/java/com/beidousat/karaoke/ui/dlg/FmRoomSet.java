package com.beidousat.karaoke.ui.dlg;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.StripViewPagerAdapter;
import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.OnDlgListener;
import com.beidousat.karaoke.widget.CountDownTextView;
import com.beidousat.karaoke.widget.PagerSlidingTabStrip;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmRoomSet extends FmBaseDialog implements OnDlgListener{
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private PagerSlidingTabStrip mPagerStrip;
    private List<String> mPagerTitles = new ArrayList<>();
    public static FmRoomSet createRoomSetFragment() {
        FmRoomSet fmRoomSet = new FmRoomSet();
        return fmRoomSet;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_room_set, container, false);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(450,500);
        params.gravity=Gravity.CENTER;
        mAttached.getView().setLayoutParams(params);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {

    }

    @Override
    void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mPagerStrip = findViewById(R.id.pager_strip);
        setupPager();

    }

    @Override
    void setListener() {
    }

    @Override
    public void onSuccess(String method, Object object) {
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupPager() {
        if (isAdded()) {
            initFragmentAndTitle();

            StripViewPagerAdapter mAdapter = new StripViewPagerAdapter(
                    getChildFragmentManager(), mFragments, mPagerTitles);
            mPagerStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.tab_font_size));
            mViewPager.setAdapter(mAdapter);
            mPagerStrip.setViewPager(mViewPager);
        }
    }

    private void initFragmentAndTitle() {
        mFragments = new ArrayList<>();

        FmDlgRoom fmDlgRoom = new FmDlgRoom();
        fmDlgRoom.setOnDlgListener(this);
        FmDlgRoomCode fmDlgRoomCode = new FmDlgRoomCode();
        fmDlgRoomCode.setOnDlgListener(this);
        mPagerTitles.addAll(Arrays.asList(Main.mMainActivity.getResources().getStringArray(R.array.room_set_tabs)));
        mFragments.add(fmDlgRoom);
        mFragments.add(fmDlgRoomCode);


    }

    @Override
    public void onTouch() {

    }

    @Override
    public void onDissmiss() {
              mAttached.dismiss();
    }
}
