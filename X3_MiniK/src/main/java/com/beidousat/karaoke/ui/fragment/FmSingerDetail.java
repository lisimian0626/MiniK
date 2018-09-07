package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.KeyboardListener;
import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.Songs;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.widget.WidgetKeyboard;
import com.beidousat.karaoke.widget.WidgetSongPager;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by J Wong on 2015/12/19 14:06.
 */
public class FmSingerDetail extends BaseFragment implements OnPageScrollListener, OnPreviewSongListener, KeyboardListener, View.OnClickListener, OnSongSelectListener {

    private View mRootView;

    private WidgetSongPager mWidgetSongPager;
    private ImageView mIvSinger;
    private WidgetTopTabs mWidgetTopTabs;
    private TextView mTvPre, mTvNext, mTvSingerName;
    private WidgetKeyboard mWidgetKeyboard;

    private int mTotalSongPage;

    private StarInfo mStarInfo;
    private Map<String, String> mRequestSongParam;

    private TextView mTvPages, mTvName;
    private int mCurPage = 0;

    private String[] mLanguageIDs;
    private String mLanguageID = "";

    private int mLetter = 0;
    private String mSearchKeyword;
    private int mWordCount = -1;


    public static FmSingerDetail newInstance(StarInfo info) {
        FmSingerDetail fragment = new FmSingerDetail();
        Bundle args = new Bundle();
        args.putSerializable("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStarInfo = (StarInfo) getArguments().getSerializable("info");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fm_singer_detail, null);

        mTvSingerName = (TextView) mRootView.findViewById(R.id.tv_singer_name);
        mTvSingerName.setOnClickListener(this);
        mWidgetKeyboard = (WidgetKeyboard) mRootView.findViewById(R.id.keyboard);
        mWidgetKeyboard.setInputTextChangedListener(this);
        mWidgetKeyboard.showLackSongButton(true);
        mWidgetSongPager = (WidgetSongPager) mRootView.findViewById(R.id.songPager);
        mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);
        mWidgetTopTabs.setLeftTabs(R.array.api_singer_song_tab_text);
        mLanguageIDs = getResources().getStringArray(R.array.api_singer_song_tab_id);
        mWidgetTopTabs.setLeftTabClickListener(new WidgetTopTabs.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                mLanguageID = mLanguageIDs[position];
                mLetter = 0;
                if (position == mLanguageIDs.length - 2) {
                    mLetter = 1;
                } else if (position == mLanguageIDs.length - 1) {
                    mLetter = 2;
                }
                requestSingerSong();
            }
        });

        mIvSinger = (ImageView) mRootView.findViewById(R.id.iv_singer);
        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);
        mTvName = (TextView) mRootView.findViewById(R.id.tv_singer);
        mRootView.findViewById(R.id.tv_search_song).setOnClickListener(this);

        mWidgetSongPager.setOnPagerScrollListener(this);
        mWidgetSongPager.setOnPreviewSongListener(this);
        mWidgetSongPager.setOnSongSelectListener(this);

        if (mStarInfo != null) {
            mTvName.setText(mStarInfo.SimpName);
            mTvSingerName.setText(mStarInfo.SimpName);
            requestSingerSong();
            if (!TextUtils.isEmpty(mStarInfo.Img)) {
                Glide.with(this).load(DiskFileUtil.getSingerImg(mStarInfo.Img)).placeholder(R.drawable.star_default_l)
                        .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0, RoundedCornersTransformation.CornerType.ALL)).skipMemoryCache(true).into(mIvSinger);
            }
            requestSingerDetail(mStarInfo.ID);
//            ClickRecorder.getCommonDialog(getActivity().getApplicationContext()).addSingerClick(mStarInfo.ID);
        }
        mTvPre = (TextView) mRootView.findViewById(R.id.btn_pre);

        mTvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPage > 0) {
                    mCurPage--;
                    mWidgetSongPager.setCurrentItem(mCurPage);
                }
            }
        });
        mTvNext = (TextView) mRootView.findViewById(R.id.btn_next);
        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPage < mTotalSongPage - 1) {
                    mCurPage++;
                    mWidgetSongPager.setCurrentItem(mCurPage);
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (isAdded()) {
            if (RequestMethod.GET_SONG.equalsIgnoreCase(method)) {
                Songs songs = (Songs) object;
                if (songs != null) {
                    if ((TextUtils.isEmpty(mSearchKeyword) && TextUtils.isEmpty(songs.Namesimplicity)) || songs.Namesimplicity.equals(mSearchKeyword)) {
                        initSingerSongPager(songs.totalPages, songs.list, mRequestSongParam);
                        mWidgetKeyboard.setWords(songs.NextWrod);
                        mWidgetKeyboard.setKeyboardKeyEnableText(songs.NextWrod);
                    }
                }
            } else if (RequestMethod.GET_SINGER_DETAIL.equals(method)) {
                StarInfo starInfo = (StarInfo) object;
                if (starInfo != null) {
                    mStarInfo = starInfo;
                    mTvName.setText(mStarInfo.SimpName);
                    mTvSingerName.setText(mStarInfo.SimpName);
                    Glide.with(this).load(DiskFileUtil.getSingerImg(mStarInfo.Img)).placeholder(R.drawable.star_default_l)
                            .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0, RoundedCornersTransformation.CornerType.ALL)).skipMemoryCache(true).into(mIvSinger);
                }
            }
        }
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        if(getContext()!=null) {
            ToastUtils.toast(getContext(), error);
        }
        super.onFailed(method, error);
    }

    public void initSingerSongPager(int totalPage, List<Song> firstPageSong, Map<String, String> params) {
        mTotalSongPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalSongPage);
        mWidgetSongPager.initPager(mTotalSongPage, firstPageSong, params);
    }


    private void requestSingerSong() {
        HttpRequest r = initRequest(RequestMethod.GET_SONG);
        r.addParam("SingerID", mStarInfo.ID);
        if (!TextUtils.isEmpty(mLanguageID)) {
            r.addParam("LanguageID", mLanguageID);
        }
        if (mLetter > 0) {
            r.addParam("Letter", String.valueOf(mLetter));
        }
        if (!TextUtils.isEmpty(mSearchKeyword)) {
            r.addParam("Namesimplicity", mSearchKeyword);
        }
        if (mWordCount > 0) {
            r.addParam("WordCount", String.valueOf(mWordCount));
        }
        r.addParam("Nums", String.valueOf(8));
        mRequestSongParam = r.getParams();
        r.setConvert2Class(Songs.class);
        r.doPost(1);
    }


    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
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
        mTvPages.setText((mCurPage + 1) + "/" + mTotalSongPage);
        mWidgetSongPager.notifyCurrentPage();
        mWidgetSongPager.notifyCurrentPage();
        mTvPre.setPressed(false);
        mTvNext.setPressed(false);
    }

    @Override
    public void onWordCountChanged(int count) {
        mWordCount = count;
        requestSingerSong();
    }

    @Override
    public void onInputTextChanged(String text) {
        mSearchKeyword = text;
        requestSingerSong();
    }

    @Override
    public void onSongSelectListener(Song song) {
        mWidgetKeyboard.setCleanText(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_song:
                mRootView.findViewById(R.id.tv_search_song).setVisibility(View.GONE);
                mRootView.findViewById(R.id.ll_signer).startAnimation(MoveAnimation.create(MoveAnimation.LEFT, false, 300));
                mRootView.findViewById(R.id.rl_content).startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 300));
                mRootView.findViewById(R.id.ll_signer).setVisibility(View.GONE);

                mTvSingerName.setVisibility(View.VISIBLE);
                mWidgetKeyboard.setVisibility(View.VISIBLE);
                mWidgetKeyboard.startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 300));
                break;
            case R.id.tv_singer_name:

                mRootView.findViewById(R.id.ll_signer).startAnimation(MoveAnimation.create(MoveAnimation.RIGHT, true, 300));
                mRootView.findViewById(R.id.rl_content).startAnimation(MoveAnimation.create(MoveAnimation.RIGHT, true, 300));
                mWidgetKeyboard.startAnimation(MoveAnimation.create(MoveAnimation.RIGHT, false, 300));

                mTvSingerName.setVisibility(View.GONE);
                mWidgetKeyboard.setVisibility(View.GONE);
                mRootView.findViewById(R.id.ll_signer).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.tv_search_song).setVisibility(View.VISIBLE);
                break;

        }
    }


    private void requestSingerDetail(String id) {
        HttpRequest request = initRequest(RequestMethod.GET_SINGER_DETAIL);
        request.addParam("SingerID", id);
        request.setConvert2Class(StarInfo.class);
        request.doPost(0);
    }
}
