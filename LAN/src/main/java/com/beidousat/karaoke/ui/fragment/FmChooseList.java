package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.widget.WidgetChoosePagerList;
import com.beidousat.karaoke.widget.WidgetSungPagerList;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;

import de.greenrobot.event.EventBus;

/**
 * Created by J Wong on 2015/12/17 17:34.
 */
public class FmChooseList extends BaseFragment implements OnPageScrollListener, View.OnClickListener
        , OnPreviewSongListener {

    private View mRootView;
    private WidgetChoosePagerList mSongPager;
    private WidgetSungPagerList mSungPager;
    private WidgetTopTabs mWidgetTopTabs;
    private TextView mTvShuffle;
    private TextView mTvPages;
    private TextView mTvPre, mTvNext;

    private int mTotalPage;

    private int mCurTab = 0;

    private int mCurPage = 0;

    public static FmChooseList newInstance(int focusTab) {
        FmChooseList fragment = new FmChooseList();
        Bundle args = new Bundle();
        args.putInt("focusTab", focusTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurTab = getArguments().getInt("focusTab", 0);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_choose_list, null);
        mTvShuffle = (TextView) mRootView.findViewById(R.id.tv_shuffle);
        mTvShuffle.setOnClickListener(this);
        mSongPager = (WidgetChoosePagerList) mRootView.findViewById(R.id.choosePager);
        mSongPager.setOnPreviewSongListener(this);
        mSungPager = (WidgetSungPagerList) mRootView.findViewById(R.id.sungPager);

        mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);

        mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);
        mWidgetTopTabs.setRightTabShow(false);
        mSongPager.setOnPagerScrollListener(this);
        mSungPager.setOnPagerScrollListener(this);

        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);

        mWidgetTopTabs.setLeftTabs(R.array.choose_tabs);


//        initSongPager();
        setTab(mCurTab);

        /**
         * 广告
         //         */
//        mBannerPlayer.loadAds("B1");

        EventBus.getDefault().register(this);

        mTvPre = (TextView) mRootView.findViewById(R.id.btn_pre);
        mTvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurTab == 0) {
                    if (mCurPage > 0) {
                        mCurPage--;
                        mSongPager.setCurrentItem(mCurPage);
                    }
                } else if (mCurTab == 1) {
                    if (mCurPage > 0) {
                        mCurPage--;
                        mSungPager.setCurrentItem(mCurPage);
                    }
                }

            }
        });
        mTvNext = (TextView) mRootView.findViewById(R.id.btn_next);
        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurTab == 0) {
                    if (mCurPage < mTotalPage - 1) {
                        mCurPage++;
                        mSongPager.setCurrentItem(mCurPage);
                    }
                } else if (mCurTab == 1) {
                    if (mCurPage < mTotalPage - 1) {
                        mCurPage++;
                        mSungPager.setCurrentItem(mCurPage);
                    }
                }
            }
        });

        return mRootView;
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void setTab(int tab) {
        mCurTab = tab;
        mWidgetTopTabs.setLeftTabFocus(mCurTab);
        switch (mCurTab) {
            case 0:
                mSongPager.setVisibility(View.VISIBLE);
                mSungPager.setVisibility(View.GONE);
                mTvShuffle.setVisibility(View.VISIBLE);
                initSongPager();
                break;
            case 1:
                mSongPager.setVisibility(View.GONE);
                mSungPager.setVisibility(View.VISIBLE);
                mTvShuffle.setVisibility(View.GONE);
                initSungPager();
                break;
        }
    }

    private WidgetTopTabs.OnTabClickListener leftOnTabClickListener = new WidgetTopTabs.OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            setTab(position);
        }
    };

    public int initSongPager() {
        mTotalPage = mSongPager.initPager();
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        return mTotalPage;
    }

    public int initSungPager() {
        mTotalPage = mSungPager.initPager();
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        return mTotalPage;
    }

    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_shuffle:
                int curItem = mSongPager.getCurrentItem();
                ChooseSongs.getInstance(getActivity().getApplicationContext()).shuffle();
                int pages = initSongPager();
                mSongPager.setCurrentItem(curItem < pages - 1 ? curItem : pages - 1);
                break;
        }
    }


    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.id.CHOOSE_SONG_CHANGED:
                if (mCurTab == 0) {
                    int curItem = mSongPager.getCurrentItem();
                    int pages = initSongPager();
                    mSongPager.setCurrentItem(curItem < pages - 1 ? curItem : pages - 1);
                } else if (mCurTab == 1) {
                    int curItem = mSungPager.getCurrentItem();
                    int pages = initSungPager();
                    mSungPager.setCurrentItem(curItem < pages - 1 ? curItem : pages - 1);
                }
                break;
            case EventBusId.id.SUNG_SONG_CHANGED:
                int curItem = mSungPager.getCurrentItem();
                int pages = initSungPager();
                mSungPager.setCurrentItem(curItem < pages - 1 ? curItem : pages - 1);
                break;
        }
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
