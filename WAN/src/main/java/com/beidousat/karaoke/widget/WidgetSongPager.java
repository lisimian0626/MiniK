package com.beidousat.karaoke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by J Wong on 2015/12/18 08:42.
 */
public class WidgetSongPager extends WidgetBasePager implements OnPreviewSongListener, OnSongSelectListener {


    private SongPagerAdapter mAdapter;

    private Map<String, String> mRequestParams = new HashMap<String, String>();

    private OnPreviewSongListener mOnPreviewSongListener;
    private OnSongSelectListener mOnSongSelectListener;

    private String mMethod;

    private int mPageSize = 8;

    private int horizontalMargin, verticalMargin;
    private boolean mIsShowSingerButton;
    private Map<Integer, List<Song>> mIndexPage;


    public WidgetSongPager(Context context) {
        super(context);
        init();
    }


    public WidgetSongPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        readAttr(attrs);
    }


    private void readAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetSongPage);
            this.horizontalMargin = a.getDimensionPixelSize(R.styleable.WidgetSongPage_horizontalMargin, 6);
            this.verticalMargin = a.getDimensionPixelSize(R.styleable.WidgetSongPage_verticalMargin, 6);
            this.mIsShowSingerButton = a.getBoolean(R.styleable.WidgetSongPage_showSingerButton, true);
        }
    }

    private void init() {
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.id.CHOOSE_SONG_CHANGED:
                notifyCurrentPage();
                break;
        }
    }

//    public void initPager(int totalPage, Map<String, String> requestParams) {
//        mRequestParams = requestParams;
//        if (mRequestParams.containsKey("Nums")) {
//            mPageSize = Integer.valueOf(mRequestParams.get("Nums"));
//        }
//        mAdapter = new SongPagerAdapter(mContext, totalPage);
//        setAdapter(mAdapter);
//    }


    public void initPager(int totalPage, List<Song> firstPageSongs, Map<String, String> requestParams) {
        mIndexPage = new HashMap<Integer, List<Song>>();
        mIndexPage.put(1, firstPageSongs);
        mRequestParams = requestParams;
        if (mRequestParams.containsKey("Nums")) {
            mPageSize = Integer.valueOf(mRequestParams.get("Nums"));
        }
        mAdapter = new SongPagerAdapter(mContext, totalPage);
        setAdapter(mAdapter);
    }

    public void initPager(String method, int totalPage, List<Song> firstPageSongs, Map<String, String> requestParams) {
        mIndexPage = new HashMap<Integer, List<Song>>();
        mIndexPage.put(1, firstPageSongs);
        mRequestParams = requestParams;
        if (mRequestParams.containsKey("Nums")) {
            mPageSize = Integer.valueOf(mRequestParams.get("Nums"));
        }
        mMethod = method;
        mAdapter = new SongPagerAdapter(mContext, totalPage);
        setAdapter(mAdapter);
    }

    public void initPager(List<Song> songs) {
        mAdapter = new SongPagerAdapter(mContext, songs);
        setAdapter(mAdapter);
    }


    public void setOnPreviewSongListener(OnPreviewSongListener listener) {
        this.mOnPreviewSongListener = listener;
    }

    public void setOnSongSelectListener(OnSongSelectListener listener) {
        mOnSongSelectListener = listener;
    }

    public void notifyCurrentPage() {
        if (mAdapter != null) {
            WidgetSongPage view = mAdapter.getCurrentView();
            if (view != null)
                view.notifyAdapter();
        }
    }

    @Override
    public void onSongSelectListener(Song song) {
        if (mOnSongSelectListener != null)
            mOnSongSelectListener.onSongSelectListener(song);
    }

    @Override
    public void onPreviewSong(Song song) {
        if (mOnPreviewSongListener != null) {
            mOnPreviewSongListener.onPreviewSong(song);
        }
    }

    private class SongPagerAdapter extends PagerAdapter {

        private Context mContext;
        private SparseArray<WidgetSongPage> sparseArray = new SparseArray<WidgetSongPage>();
        private int mPageCount;
        private SparseArray<List<Song>> mSongs = new SparseArray<List<Song>>();

        public WidgetSongPage getCurrentView() {
            return sparseArray.get(WidgetSongPager.this.getCurrentItem());
        }


        public SongPagerAdapter(Context context, int pageCount) {
            mContext = context;
            this.mPageCount = pageCount;
        }

        public SongPagerAdapter(Context context, List<Song> songs) {
            mContext = context;
            int pages = songs.size() / mPageSize;
            if (songs.size() % mPageSize > 0) {
                pages = pages + 1;
            }

            for (int i = 0; i < pages; i++) {
                int e = mPageSize * (i + 1) > songs.size() ? songs.size() : mPageSize * (i + 1);
                List<Song> subList = songs.subList(i * mPageSize, e);
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
            WidgetSongPage imageView;
            if (sparseArray.get(position) == null) {
                imageView = new WidgetSongPage(mContext, verticalMargin, horizontalMargin, mIsShowSingerButton);
                imageView.setOnPreviewSongListener(WidgetSongPager.this);
                imageView.setOnSongSelectListener(WidgetSongPager.this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                if (mSongs.get(position) != null) {
                    imageView.setSong(mSongs.get(position));
                } else if (mIndexPage.containsKey(position + 1)) {
                    imageView.setSong(mIndexPage.get(position + 1));
                } else if (TextUtils.isEmpty(mMethod)) {
                    imageView.loadSong(position + 1, mRequestParams);
                } else {
                    imageView.loadSong(mMethod, position + 1, mRequestParams);
                }
                sparseArray.put(position, imageView);
            } else {
                imageView = sparseArray.get(position);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            mCurrentView = (WidgetSongPage) object;
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            WidgetSongPage view = (WidgetSongPage) object;
            container.removeView(view);
//            sparseArray.put(position, view);
            sparseArray.delete(position);
        }

    }
}