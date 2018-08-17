package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.beidousat.karaoke.model.Singers;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.Songs;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.widget.WidgetKeyboard;
import com.beidousat.karaoke.widget.WidgetSingerPager;
import com.beidousat.karaoke.widget.WidgetSongPager;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2015/10/9 11:19.
 */
public class FmSearch extends BaseFragment implements KeyboardListener, OnPageScrollListener, OnPreviewSongListener, OnSongSelectListener {

    private int mTabPs = 0;
    private String mSearchKeyword;
    private View mRootView;
    private WidgetTopTabs mWidgetTopTabs;

    private WidgetKeyboard mWidgetKeyboard;

    private WidgetSongPager mWidgetSongPager;
    private WidgetSingerPager mWidgetSingerPager;
    private TextView mTvPages;
    private TextView mTvPre, mTvNext;


    private int mTotalSongPage;
    private int mTotalSingerPage;
    private int mCurPage = 0;

    private Map<String, String> mRequestSongParam;
    private Map<String, String> mRequestSingerParam;

    private int mWordCount = -1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fm_search, null);
        mWidgetSongPager = (WidgetSongPager) mRootView.findViewById(R.id.songPager);
        mWidgetSongPager.setOnPreviewSongListener(this);
        mWidgetSongPager.setOnSongSelectListener(this);

        mWidgetSingerPager = (WidgetSingerPager) mRootView.findViewById(R.id.singerPager);

        mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);

        mWidgetKeyboard = (WidgetKeyboard) mRootView.findViewById(R.id.keyboard);
        mWidgetKeyboard.setInputTextChangedListener(this);

        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);


        mWidgetTopTabs.setLeftTabs(R.array.search_types);
        mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);


        mWidgetSongPager.setOnPagerScrollListener(this);
        mWidgetSingerPager.setOnPagerScrollListener(this);

        init();

        requestSongs();


        mTvPre = (TextView) mRootView.findViewById(R.id.btn_pre);
        mTvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPage > 0) {
                    mCurPage--;
                    if (mTabPs == 0) {
                        mWidgetSongPager.setCurrentItem(mCurPage);
                    } else if (mTabPs == 1) {
                        mWidgetSingerPager.setCurrentItem(mCurPage);
                    }
                }
            }
        });

        mTvNext = (TextView) mRootView.findViewById(R.id.btn_next);
        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabPs == 0) {
                    if (mCurPage < mTotalSongPage - 1) {
                        mCurPage++;
                        mWidgetSongPager.setCurrentItem(mCurPage);
                    }
                } else if (mTabPs == 1) {
                    if (mCurPage < mTotalSingerPage - 1) {
                        mCurPage++;
                        mWidgetSingerPager.setCurrentItem(mCurPage);
                    }
                }
            }
        });

        return mRootView;
    }


    private void init() {
        requestSongs();
    }

    private void requestSongs() {
        HttpRequest r = initRequest(RequestMethod.GET_SONG);
        r.addParam("Nums", String.valueOf(8));
        boolean isFirstSong = true;
        if (!TextUtils.isEmpty(mSearchKeyword)) {
            r.addParam("Namesimplicity", mSearchKeyword);
            isFirstSong = false;
        }
        if (mWordCount > 0) {
            r.addParam("WordCount", String.valueOf(mWordCount));
            isFirstSong = false;
        }
        mRequestSongParam = r.getParams();
        r.setConvert2Class(Songs.class);
        r.doPost(1);
    }

    private void requestSingers() {
        HttpRequest r = initRequest(RequestMethod.GET_SINGER);
        r.addParam("Nums", String.valueOf(10));
        if (!TextUtils.isEmpty(mSearchKeyword)) {
            r.addParam("Namesimplicity", mSearchKeyword);
        }
        if (mWordCount > 0) {
            r.addParam("WordCount", String.valueOf(mWordCount));
        }
        mRequestSingerParam = r.getParams();
        r.setConvert2Class(Singers.class);
        r.doPost(1);
    }


    public void initSongPager(int totalPage, List<Song> firstPageSong, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalSongPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalSongPage);
        mWidgetSongPager.initPager(mTotalSongPage, firstPageSong, params);
    }

    public void initSingerPager(int totalPage, List<StarInfo> starInfos, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalSingerPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalSingerPage);
        mWidgetSingerPager.initPager(mTotalSingerPage, starInfos, params);
    }

    @Override
    public void onPagerSelected(int position) {
        mCurPage = position;
        if (mTabPs == 0) {
            mTvPages.setText((mCurPage + 1) + "/" + mTotalSongPage);
            mWidgetSongPager.notifyCurrentPage();
        } else if (mTabPs == 1) {
            mTvPages.setText((mCurPage + 1) + "/" + mTotalSingerPage);
        }
        mTvPre.setPressed(false);
        mTvNext.setPressed(false);
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

    private WidgetTopTabs.OnTabClickListener leftOnTabClickListener = new WidgetTopTabs.OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            onLeftTabClick(position);
        }
    };

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_SONG.equalsIgnoreCase(method)) {
            try {
                if (object != null) {
                    Songs songs = (Songs) object;
                    if (songs != null) {
                        if ((TextUtils.isEmpty(mSearchKeyword) && TextUtils.isEmpty(songs.Namesimplicity)) || songs.Namesimplicity.equals(mSearchKeyword)) {
                            initSongPager(songs.totalPages, songs.list, mRequestSongParam);
                            mWidgetKeyboard.setWords(songs.NextWrod);
                            mWidgetKeyboard.setKeyboardKeyEnableText(songs.NextWrod);
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), e.toString());
            }
        } else if (RequestMethod.GET_SINGER.equalsIgnoreCase(method)) {
            Singers singers = (Singers) object;
            if (singers != null) {
                initSingerPager(singers.totalPages, singers.list, mRequestSingerParam);
                mWidgetKeyboard.setWords(singers.NextWrod);
                mWidgetKeyboard.setKeyboardKeyEnableText(singers.NextWrod);

            }
        }

        super.onSuccess(method, object);
    }

    public void onLeftTabClick(int position) {
        mTabPs = position;
        switch (mTabPs) {
            case 0:
                mWidgetKeyboard.setCleanText(false);
                mWidgetKeyboard.setText("");
                mWidgetKeyboard.setHintText(getString(R.string.search_song));
                mWidgetKeyboard.showLackSongButton(true);
                mWidgetSongPager.setVisibility(View.VISIBLE);
                mWidgetSingerPager.setVisibility(View.GONE);
                requestSongs();
                break;
            case 1:
                mWidgetKeyboard.setCleanText(false);
                mWidgetKeyboard.setText("");
                mWidgetKeyboard.setHintText(getString(R.string.search_singer));
                mWidgetKeyboard.showLackSongButton(false);
                mWidgetSongPager.setVisibility(View.GONE);
                mWidgetSingerPager.setVisibility(View.VISIBLE);
                requestSingers();
                break;
        }
    }

    @Override
    public void onInputTextChanged(String text) {
        mSearchKeyword = text;
        switch (mTabPs) {
            case 0:
                requestSongs();
                break;
            case 1:
                requestSingers();
                break;
            default:
                break;
        }
    }

    @Override
    public void onWordCountChanged(int count) {
        mWordCount = count;
        switch (mTabPs) {
            case 0:
                requestSongs();
                break;
            case 1:
                requestSingers();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
    }

    @Override
    public void onSongSelectListener(Song song) {
        mWidgetKeyboard.setCleanText(true);
    }
}
