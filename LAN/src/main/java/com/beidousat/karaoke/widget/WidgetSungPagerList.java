package com.beidousat.karaoke.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;

import java.util.List;

/**
 * Created by J Wong on 2015/12/18 08:42.
 */
public class WidgetSungPagerList extends WidgetBasePager {


    private SongPagerAdapter mAdapter;

    public WidgetSungPagerList(Context context) {
        super(context);
    }

    public WidgetSungPagerList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public int initPager() {
        List<Song> songs = ChooseSongs.getInstance(mContext).getHasSungSons();
        int pages = songs.size() / 8;
        if (songs.size() % 8 > 0) {
            pages = pages + 1;
        }
        mAdapter = new SongPagerAdapter(mContext, pages, songs);
        setAdapter(mAdapter);
        return pages;
    }


    private class SongPagerAdapter extends PagerAdapter {

        private Context mContext;
        private SparseArray<WidgetSungPageList> sparseArray = new SparseArray<WidgetSungPageList>();
        private int mPageCount;
        private SparseArray<List<Song>> mSongs = new SparseArray<List<Song>>();

        public SongPagerAdapter(Context context, int pages, List<Song> songs) {
            mContext = context;
            for (int i = 0; i < pages; i++) {
                int e = 8 * (i + 1) > songs.size() ? songs.size() : 8* (i + 1);
                List<Song> subList = songs.subList(i * 8, e);
                mSongs.put(i, subList);
            }
            this.mPageCount = pages;
        }

        @Override
        public int getCount() {
            return mPageCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            WidgetSungPageList page;
            if (sparseArray.get(position) == null) {
                page = new WidgetSungPageList(mContext);
                page.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                if (mSongs.get(position) != null) {
                    page.setSong(mSongs.get(position));
                }
                sparseArray.put(position, page);
            } else {
                page = sparseArray.get(position);
            }
            container.addView(page);
            return page;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            WidgetSungPageList view = (WidgetSungPageList) object;
            container.removeView(view);
            sparseArray.delete(position);
        }

    }
}