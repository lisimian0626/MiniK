package com.beidousat.karaoke.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;

import java.util.List;

/**
 * Created by J Wong on 2015/12/18 08:42.
 */
public class WidgetChoosePagerList extends WidgetBasePager implements OnPreviewSongListener {


    private SongPagerAdapter mAdapter;
    private OnPreviewSongListener mOnPreviewSongListener;

    public WidgetChoosePagerList(Context context) {
        super(context);
    }

    public WidgetChoosePagerList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public int initPager() {
        List<Song> songs = ChooseSongs.getInstance(mContext).getSongs();

        int pages = songs.size() / 8;
        if (songs.size() % 8 > 0) {
            pages = pages + 1;
        }
        mAdapter = new SongPagerAdapter(mContext, pages, songs);
        setAdapter(mAdapter);
        return pages;
    }


    public void setOnPreviewSongListener(OnPreviewSongListener l) {
        this.mOnPreviewSongListener = l;
    }


    @Override
    public void onPreviewSong(Song song) {
        if (mOnPreviewSongListener != null)
            mOnPreviewSongListener.onPreviewSong(song);
    }

    private class SongPagerAdapter extends PagerAdapter {

        private Context mContext;
        private SparseArray<WidgetChoosePageList> sparseArray = new SparseArray<WidgetChoosePageList>();
        private int mPageCount;
        private SparseArray<List<Song>> mSongs = new SparseArray<List<Song>>();

        public SongPagerAdapter(Context context, int pages, List<Song> songs) {
            mContext = context;
            for (int i = 0; i < pages; i++) {
                int e = 8 * (i + 1) > songs.size() ? songs.size() : 8 * (i + 1);
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
            WidgetChoosePageList page;
            if (sparseArray.get(position) == null) {
                page = new WidgetChoosePageList(mContext);
                page.setOnPreviewSongListener(WidgetChoosePagerList.this);
                page.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                if (mSongs.get(position) != null) {
                    page.setSong(position, mSongs.get(position));
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
            WidgetChoosePageList view = (WidgetChoosePageList) object;
            container.removeView(view);
            sparseArray.delete(position);
        }
    }
}