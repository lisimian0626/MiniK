package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.Common;
import com.beidousat.karaoke.interf.KeyboardListener;
import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.karaoke.model.Singers;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.widget.WidgetKeyboard;
import com.beidousat.karaoke.widget.WidgetSingerPager;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2015/12/16 16:43.
 */
public class FmSinger extends BaseFragment implements KeyboardListener, OnPageScrollListener {

    private View mRootView;

    private WidgetSingerPager mSingerPager;
    private WidgetTopTabs mWidgetTopTabs;
    private WidgetKeyboard mWidgetKeyboard;
    private TextView mTvPre, mTvNext;

    private String[] mTabLeftArrays, mTabRightArrays;

    private int mTotalPage;
    private int mSex = 0;
    private String mArea;
    private int mIndex = 1;

    private boolean isLetter;

    private String mSearchKeyword;
    private Map<String, String> mRequestParam;

    private static final String[] mAreas = new String[]{"", "DALU", "GANGTAI", "OUMEI", "RIHAN", "QITA", ""};

    private int mWordCount = -1;

    private TextView mTvPages;
    private int mCurPage = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fm_singer, null);

//            mRootView.findViewById(R.id.keyboard).startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 300));
//            mRootView.findViewById(R.id.ll_content).startAnimation(MoveAnimation.create(MoveAnimation.RIGHT, true, 300));

            mSingerPager = (WidgetSingerPager) mRootView.findViewById(R.id.singerPager);
            mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);
            mWidgetKeyboard = (WidgetKeyboard) mRootView.findViewById(R.id.keyboard);
            mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);
            mWidgetTopTabs.setRightTabClickListener(rightOnTabClickListener);
            mWidgetKeyboard.setInputTextChangedListener(this);
            mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);

//        mWidgetTopTabs.setRightTabShow();
            mSingerPager.setOnPagerScrollListener(this);
            init();
            mTvPre = (TextView) mRootView.findViewById(R.id.btn_pre);
            mTvPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurPage > 0) {
                        mCurPage--;
                        mSingerPager.setCurrentItem(mCurPage);
                    }
                }
            });
            mTvNext = (TextView) mRootView.findViewById(R.id.btn_next);
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurPage < mTotalPage - 1) {
                        mCurPage++;
                        mSingerPager.setCurrentItem(mCurPage);
                    }
                }
            });
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }


    private void init() {
        if(Common.isEn){
            mTabLeftArrays = getResources().getStringArray(R.array.page_star_top_tabs_left_en);
            mTabRightArrays = getResources().getStringArray(R.array.page_star_top_tabs_rigth_en);
        }else{
            mTabLeftArrays = getResources().getStringArray(R.array.page_star_top_tabs_left);
            mTabRightArrays = getResources().getStringArray(R.array.page_star_top_tabs_rigth);
        }
        mWidgetTopTabs.setLeftTabs(mTabLeftArrays);
        mWidgetTopTabs.setRightTabs(mTabRightArrays);
        mWidgetTopTabs.setRightTabFocus(-1);
        requestSingers();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onInputTextChanged(String text) {
        mSearchKeyword = text;
        mIndex = 1;
        requestSingers();
    }

    private WidgetTopTabs.OnTabClickListener leftOnTabClickListener = new WidgetTopTabs.OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            onLeftTabClick(position);
        }
    };

    private WidgetTopTabs.OnTabClickListener rightOnTabClickListener = new WidgetTopTabs.OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            onRightTabClick(position);
        }
    };


    public void onLeftTabClick(int position) {
        isLetter = position >= mAreas.length - 1;
        mWidgetTopTabs.setRightTabFocus(-1);
        mSex = 0;
        mIndex = 1;
        mArea = mAreas[position];
        requestSingers();
    }

    public void onRightTabClick(int position) {
        mIndex = 1;
        mSex = position + 1;
        requestSingers();
    }

    private void requestSingers() {
        HttpRequest r = initRequest(RequestMethod.GET_SINGER);
        r.addParam("Nums", String.valueOf(10));
        if (mSex > 0) {
            r.addParam("Sex", String.valueOf(mSex));
        }
        if (!TextUtils.isEmpty(mArea)) {
            r.addParam("singerTypeID", mArea);
        }
        if (!TextUtils.isEmpty(mSearchKeyword)) {
            r.addParam("Namesimplicity", mSearchKeyword);
        }
        if (isLetter) {
            r.addParam("Letter", String.valueOf(1));
        }
        if (mWordCount > 0) {
            r.addParam("WordCount", String.valueOf(mWordCount));
        }
        mRequestParam = r.getParams();
        r.setConvert2Class(Singers.class);
        r.doPost(mIndex);
    }

    public void initSingerPager(int totalPage, List<StarInfo> starInfos, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mSingerPager.initPager(mTotalPage, starInfos, params);
    }


    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_SINGER.equalsIgnoreCase(method)) {
            Singers singers = (Singers) object;
            if (singers != null) {
                initSingerPager(singers.totalPages, singers.list, mRequestParam);
                mWidgetKeyboard.setWords(singers.NextWrod);
                mWidgetKeyboard.setKeyboardKeyEnableText(singers.NextWrod);
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {
        if(getContext()!=null){
            ToastUtils.toast(getContext(),error);
        }
        super.onFailed(method, error);
    }

    @Override
    public void onWordCountChanged(int count) {
        mWordCount = count;
        requestSingers();
    }

    @Override
    public void onPageScrollLeft() {
        mTvPre.setPressed(true);
        mTvNext.setPressed(false);
    }

    @Override
    public void onPageScrollRight() {
        mTvNext.setPressed(true);
        mTvPre.setPressed(false);
    }

    @Override
    public void onPagerSelected(int position) {
        mCurPage = position;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mTvPre.setPressed(false);
        mTvNext.setPressed(false);
    }
}
