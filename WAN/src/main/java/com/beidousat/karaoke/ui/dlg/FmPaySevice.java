package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.StripViewPagerAdapter;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.OnDlgListener;
import com.beidousat.karaoke.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmPaySevice extends FmBaseDialog implements OnDlgListener {
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private PagerSlidingTabStrip mPagerStrip;
    private List<String> mPagerTitles = new ArrayList<>();
    public static FmPaySevice createPaySeviceFragment() {
        FmPaySevice fmRoomSet = new FmPaySevice();
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

        FmPayment payment_wechat=new FmPayment();
        Bundle bundle_wechat=new Bundle();
        bundle_wechat.putString(FmPayment.PAYMENT_TYPE,"wechat");
        bundle_wechat.putString(FmPayment.PAYMENT_TIPS,"请使用微信扫码充值，充值成功后系统将于2分钟内扣费并开通服务");
        payment_wechat.setArguments(bundle_wechat);

        FmPayment payment_zhifubao=new FmPayment();
        Bundle bundle_zhifubao=new Bundle();
        bundle_zhifubao.putString(FmPayment.PAYMENT_TYPE,"alipay");
        bundle_zhifubao.putString(FmPayment.PAYMENT_TIPS,"请使用支付宝扫码充值，充值成功后系统将于2分钟内扣费并开通服务");
        payment_zhifubao.setArguments(bundle_zhifubao);
        mPagerTitles.addAll(Arrays.asList(Main.mMainActivity.getResources().getStringArray(R.array.payment_tabs)));
        mFragments.add(payment_wechat);
        mFragments.add(payment_zhifubao);


    }

    @Override
    public void onTouch() {

    }

    @Override
    public void onDissmiss() {
              mAttached.dismiss();
    }
}
