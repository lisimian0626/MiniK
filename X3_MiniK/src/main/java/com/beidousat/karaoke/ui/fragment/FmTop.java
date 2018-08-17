package com.beidousat.karaoke.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.AdtTopType;
import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.Songs;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.widget.WidgetSongPager;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;

import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2017/4/6.
 */

public class FmTop extends BaseFragment implements AdtTopType.OnTopTypeSelectListener, OnPageScrollListener, OnSongSelectListener, OnPreviewSongListener {

    private RecyclerView mRvTopType;
    private AdtTopType mAdtTopType;
    private WidgetSongPager mSongPager;
    private TextView mTvPre, mTvNext;

    private int mTotalPage;
    private TextView mTvPages;
    private int mCurPage = 0;
    private Map<String, String> mRequestParam;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_top, null);
        mRvTopType = (RecyclerView) mRootView.findViewById(R.id.rv_top_type);
        mSongPager = (WidgetSongPager) mRootView.findViewById(R.id.songPager);
        mSongPager.setOnPagerScrollListener(this);
        mSongPager.setOnPreviewSongListener(this);
        mSongPager.setOnSongSelectListener(this);
        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);

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
        init();
        return mRootView;
    }

    private void init() {
        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext())
                .color(Color.TRANSPARENT).size(2).margin(2).build();

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        layoutManager3.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTopType.setLayoutManager(layoutManager3);
        mRvTopType.addItemDecoration(horDivider);

        mAdtTopType = new AdtTopType(getActivity().getApplication());
        mAdtTopType.setOnTopTypeSelectListener(this);
        int[] resId;
        if(Common.isEn){
            resId = new int[]{R.drawable.selector_top_total, R.drawable.selector_top_month, R.drawable.selector_top_day};
        }else{
            resId = new int[]{R.drawable.selector_top_total, R.drawable.selector_top_month, R.drawable.selector_top_day, R.drawable.selector_top_mandarin, R.drawable.selector_top_cantonese};
        }

        mAdtTopType.setData(resId);

        mRvTopType.setAdapter(mAdtTopType);

        requestSongs(1);
    }

    private void requestSongs(int topType) {
        HttpRequest r = initRequest(RequestMethod.SONG_RANKING);
        r.addParam("Type", String.valueOf(topType));
        r.addParam("Nums", String.valueOf(8));
        mRequestParam = r.getParams();
        r.setConvert2Class(Songs.class);
        r.doPost(1);
    }


    public void initSongPager(int totalPage, List<Song> firstPageSong, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mSongPager.initPager(RequestMethod.SONG_RANKING, mTotalPage, firstPageSong, params);
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.SONG_RANKING.equalsIgnoreCase(method)) {
            Songs songs = (Songs) object;
            if (songs != null) {
                initSongPager(songs.totalPages, songs.list, mRequestParam);
            }
        }
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        if (RequestMethod.SONG_RANKING.equalsIgnoreCase(method)) {
            if(getContext()!=null) {
                ToastUtils.toast(getContext().getApplicationContext(), error);
            }
//            initSongPager(0, null, mRequestParam);
        }
    }

    @Override
    public void onTopSelect(int position) {
        int type = position + 1;
        if (position >= 2) {
            type = position + 2;
        }
        requestSongs(type);
    }


    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
    }

    @Override
    public void onSongSelectListener(Song song) {
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
