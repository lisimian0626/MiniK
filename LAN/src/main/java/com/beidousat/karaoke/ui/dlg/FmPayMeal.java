package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.StripViewPagerAdapter;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.widget.CountDownTextView;
import com.beidousat.karaoke.widget.PagerSlidingTabStrip;
import com.beidousat.libbns.evenbus.EventBusUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmPayMeal extends FmBaseDialog {
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private PagerSlidingTabStrip mPagerStrip;
    private View mCountLayout;
    private CountDownTextView mCountDown;
    private List<String> mPagerTitles = new ArrayList<>();

    private int mPageType = TYPE_NORMAL;

    public static final String TAG_PAGE_TYPE = "page_type";
    /**
     * 普通类型页面
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 普通续费类型页面
     */
    public static final int TYPE_NORMAL_RENEW = 1;
    /**
     * 带倒计时的续费类型
     */
    public static final int TYPE_CNT_RENEW = 2;

    /**
     * 新建套餐支付页面
     *
     * @param type 页面类型:
     *             TYPE_NORMAL=普通类型；
     *             TYPE_NORMAL_RENEW=普通续费类型，只能选择当前已选套餐类型；
     *             TYPE_CNT_RENEW=带倒计时的续费类型,当倒计时结束后会发送ROOM_CLOSE消息；
     * @return
     */
    public static FmPayMeal createMealFragment(int type) {
        FmPayMeal payMeal = new FmPayMeal();
        Bundle bundle = new Bundle();
        //如果当前套餐未过期则创建续费套餐页面；否者创建普通套餐页面
        bundle.putInt(FmPayMeal.TAG_PAGE_TYPE, type);
        payMeal.setArguments(bundle);

        return payMeal;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_meal, container, false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mPageType = getArguments().getInt(TAG_PAGE_TYPE, TYPE_NORMAL);

        initRequest("PayMeal");
    }

    @Override
    void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mPagerStrip = findViewById(R.id.pager_strip);
        mCountLayout = findViewById(R.id.countdown_layout);
        mCountDown = findViewById(R.id.countdown);

        setupPager();
    }

    @Override
    void setListener() {
        mCountDown.setFinishedListener(new CountDownTextView.TimerFinished() {
            @Override
            public void onFinish() {
                handleWhenIsAutoCloseMode();
            }
        });
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
        setupCountDown();
        mAttached.registerCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleWhenIsAutoCloseMode();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mCountDown.stop();
        mAttached.unregisterCloseListener(this);
    }

    private void setupPager() {
        initFragmentAndTitle();

        StripViewPagerAdapter mAdapter = new StripViewPagerAdapter(
                getChildFragmentManager(), mFragments, mPagerTitles);
        mPagerStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.tab_font_size));
        mViewPager.setAdapter(mAdapter);
        mPagerStrip.setViewPager(mViewPager);
    }

    private void setupCountDown() {
        if (mPageType == TYPE_CNT_RENEW) {
            mCountLayout.setVisibility(View.VISIBLE);
            mCountDown.restart();
        } else {
            mCountLayout.setVisibility(View.INVISIBLE);
        }
    }


    private void initFragmentAndTitle() {
        mFragments = new ArrayList<>();

        FmMeal timeMeal = new FmMeal();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(FmMeal.FM_TAG, Meal.TIME);
        timeMeal.setArguments(bundle1);

        FmMeal songMeal = new FmMeal();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(FmMeal.FM_TAG, Meal.SONG);
        songMeal.setArguments(bundle2);

        if (mPageType != TYPE_NORMAL && !BoughtMeal.getInstance().isMealExpire()) {
            if (BoughtMeal.getInstance().getMeal().getType() == Meal.SONG) {
                mPagerTitles.add(getContext().getResources().getString(R.string.text_buy_songs));
                mFragments.add(songMeal);
            } else if (BoughtMeal.getInstance().getMeal().getType() == Meal.TIME) {
                mPagerTitles.add(getContext().getResources().getString(R.string.text_buy_time));
                mFragments.add(timeMeal);
            }
        } else {
            mPagerTitles.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array.paymeal_tabs)));
            mFragments.add(timeMeal);
            mFragments.add(songMeal);
        }

    }

    /**
     * 倒计时续费模式，倒计时结束后发送ROOM_CLOSE消息
     */
    private void handleWhenIsAutoCloseMode() {
        if (mPageType == TYPE_CNT_RENEW && BoughtMeal.getInstance().isMealExpire()) {
            EventBusUtil.postRoomClose(null);
        }
        mAttached.dismiss();
    }
}
