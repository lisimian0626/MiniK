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
import com.beidousat.karaoke.model.Songs;
import com.beidousat.karaoke.model.VarietyShowMenu;
import com.beidousat.karaoke.ui.dlg.DlgPreview;
import com.beidousat.karaoke.widget.WidgetSongPager;
import com.beidousat.karaoke.widget.WidgetTopTabs;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2017/4/1.
 */

public class FmShowDetail extends BaseFragment implements OnPageScrollListener, OnPreviewSongListener {

    private int mRankingType;
    private int mResId;
    private int mBgResId;
    private int mTopId;

    private RecyclerImageView mRivShow, mRivBg, mRivTop;
    private WidgetSongPager mSongPager;
    private WidgetTopTabs mWidgetTopTabs;
    private TextView mTvPre, mTvNext;
    private TextView mTvPages;

    private int mCurPage = 0;
    private int mTotalPage;

    private Map<String, String> mRequestParam;
    private List<VarietyShowMenu> mVarietyShowMenus = new ArrayList<>();


    public static FmShowDetail newInstance(int type, int resId, int resTop, int bgResId) {
        FmShowDetail fragment = new FmShowDetail();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("resId", resId);
        args.putInt("resTop", resTop);
        args.putInt("bgResId", bgResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRankingType = getArguments().getInt("type");
            mResId = getArguments().getInt("resId");
            mBgResId = getArguments().getInt("bgResId");
            mTopId = getArguments().getInt("resTop");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_show_detail, null);

        mSongPager = (WidgetSongPager) mRootView.findViewById(R.id.songPager);
        mWidgetTopTabs = (WidgetTopTabs) mRootView.findViewById(R.id.topTab);
        mTvPages = (TextView) mRootView.findViewById(R.id.tv_pages);
        mSongPager.setOnPagerScrollListener(this);
        mSongPager.setOnPreviewSongListener(this);

        mRivBg = (RecyclerImageView) mRootView.findViewById(R.id.iv_bg);
        mRivBg.setImageResource(mBgResId);

        mRivTop = (RecyclerImageView) mRootView.findViewById(R.id.iv_show_bg);
        mRivTop.setImageResource(mTopId);

        mRivShow = (RecyclerImageView) mRootView.findViewById(R.id.iv_show);
        mRivShow.setImageResource(mResId);

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

        mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);


        requestTabs();
        requestSongs(mRankingType + "");

        return mRootView;
    }


    private WidgetTopTabs.OnTabClickListener leftOnTabClickListener = new WidgetTopTabs.OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            onLeftTabClick(position);
        }
    };

    private void onLeftTabClick(int position) {
        mWidgetTopTabs.setRightTabFocus(-1);
        requestSongs(mVarietyShowMenus.get(position).ID);
    }


    private void initSongPager(int totalPage, List<Song> firstPageSong, Map<String, String> params) {
        Logger.i(getClass().getSimpleName(), "Current total page:" + totalPage);
        mTotalPage = totalPage;
        mCurPage = 0;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mSongPager.initPager(RequestMethod.GET_SONG_RANKING, mTotalPage, firstPageSong, params);
    }


    @Override
    public void onPreviewSong(Song song) {
        new DlgPreview(getActivity(), song).show();
    }

    @Override
    public void onPagerSelected(int position) {
        mCurPage = position;
        mTvPages.setText((mCurPage + 1) + "/" + mTotalPage);
        mSongPager.notifyCurrentPage();
        mTvPre.setPressed(false);
        mTvNext.setPressed(false);
    }

    @Override
    public void onPageScrollRight() {
        mTvNext.setPressed(true);
        mTvPre.setPressed(false);
    }

    @Override
    public void onPageScrollLeft() {
        mTvPre.setPressed(true);
        mTvNext.setPressed(false);
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_SONG_RANKING.equalsIgnoreCase(method)) {
            Songs songs = (Songs) object;
            if (songs != null) {
                initSongPager(songs.totalPages, songs.list, mRequestParam);
            }
        } else if (RequestMethod.MUZIKLAND_LEVEL.equals(method)) {
            List<VarietyShowMenu> menuList = (List<VarietyShowMenu>) object;
            if (menuList != null && menuList.size() > 0) {
                mVarietyShowMenus.addAll(menuList);
                setTabs(mVarietyShowMenus);
            }
        }
        super.onSuccess(method, object);
    }

    private void setTabs(List<VarietyShowMenu> list) {
        String[] texts = new String[list.size()];
        int ps = 0;
        for (VarietyShowMenu menu : list) {
            texts[ps] = menu.MuziklandName;
            ps++;
        }
        mWidgetTopTabs.setLeftTabs(texts);
        mWidgetTopTabs.setLeftTabClickListener(leftOnTabClickListener);
        mWidgetTopTabs.setLeftTabFocus(0);
    }

    public void requestTabs() {
        HttpRequest r = initRequest(RequestMethod.MUZIKLAND_LEVEL);
        r.addParam("ID", String.valueOf(mRankingType));
        r.setConvert2Token(new TypeToken<List<VarietyShowMenu>>() {
        });
        r.doPost(0);
    }

    private void requestSongs(String type) {
        HttpRequest r = initRequest(RequestMethod.GET_SONG_RANKING);
        r.addParam("Nums", String.valueOf(8));
        r.addParam("Type", String.valueOf(type));
        mRequestParam = r.getParams();
        r.setConvert2Class(Songs.class);
        r.doPost(1);
    }

}

