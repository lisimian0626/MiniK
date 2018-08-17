package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.KeyboardListener;
import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.widget.WidgetKeyboard;
import com.beidousat.karaoke.widget.WidgetSongPager;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.amin.CubeAnimation;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.amin.PushPullAnimation;
import com.beidousat.libbns.util.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2017/3/29.
 */

public class FmSongBase extends BaseFragment implements KeyboardListener, OnPageScrollListener, OnSongSelectListener, OnPreviewSongListener {


    View mRootView;

    String mSearchKeyword;

    WidgetSongPager mSongPager;
    WidgetTopTabs mWidgetTopTabs;
    WidgetKeyboard mWidgetKeyboard;

    View mEmptyView;

    private TextView mTvPre, mTvNext;

    private int mTotalPage;
    private TextView mTvPages;
    private int mCurPage = 0;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_songs, null);
//        mRootView.findViewById(R.id.keyboard).startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 300));
//        mRootView.findViewById(R.id.ll_song).startAnimation(MoveAnimation.create(MoveAnimation.RIGHT, true, 300));

        mSongPager = (WidgetSongPager) mRootView.findViewById(R.id.songPager);

        mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);
        mWidgetKeyboard = (WidgetKeyboard) mRootView.findViewById(R.id.keyboard);
        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);
        mEmptyView = mRootView.findViewById(R.id.rl_empty);
        mEmptyView.findViewById(R.id.tv_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentUtil.addFragment(new FmSongFeedback(), false);
            }
        });

        mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);
        mWidgetTopTabs.setRightTabClickListener(rightOnTabClickListener);

        mWidgetKeyboard.setInputTextChangedListener(this);
        mWidgetKeyboard.showLackSongButton(true);

        mWidgetTopTabs.setRightTabShow(false);
        mSongPager.setOnPagerScrollListener(this);

        mSongPager.setOnPreviewSongListener(this);

        mSongPager.setOnSongSelectListener(this);

        mTvPre = (TextView) mRootView.findViewById(R.id.btn_pre);
        mTvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPage > 0) {
                    mCurPage--;
                    mSongPager.setCurrentItem(mCurPage);
                }
            }
        });
        mTvNext = (TextView) mRootView.findViewById(R.id.btn_next);
        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPage < mTotalPage - 1) {
                    mCurPage++;
                    mSongPager.setCurrentItem(mCurPage);
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onWordCountChanged(int count) {

    }

    @Override
    public void onInputTextChanged(String text) {
        mSearchKeyword = text;
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
    }

    public void onRightTabClick(int position) {
    }

    public void initSongPager(int totalPage, List<Song> firstPageSong, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalPage = totalPage;
//        mSeekBar.setTotalPage(mTotalPage);
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mSongPager.initPager(mTotalPage, firstPageSong, params);

        showEmptyView(firstPageSong == null || firstPageSong.isEmpty());
    }

    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
    }

    @Override
    public void onSongSelectListener(Song song) {
        mWidgetKeyboard.setCleanText(true);
    }

    public void showEmptyView(boolean show) {
        mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        mSongPager.setVisibility(show ? View.GONE : View.VISIBLE);
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
        mSongPager.notifyCurrentPage();
        mTvPre.setPressed(false);
        mTvNext.setPressed(false);
    }
}

