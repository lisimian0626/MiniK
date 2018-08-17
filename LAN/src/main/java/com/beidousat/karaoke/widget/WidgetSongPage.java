package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.beidousat.karaoke.adapter.AdtSong;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.Songs;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration2;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2015/12/17 18:01.
 */
public class WidgetSongPage extends RecyclerView implements HttpRequestListener, OnPreviewSongListener, OnSongSelectListener {


    private String mMethod;
    private AdtSong mAdapter;
    private OnPreviewSongListener mOnPreviewSongListener;
    private boolean mIsShowSingerButton;

    private int dividerV = DensityUtil.dip2px(getContext(), 6);

    private int dividerH = DensityUtil.dip2px(getContext(), 6);

    public WidgetSongPage(Context context) {
        super(context);
        init();
    }

    public WidgetSongPage(Context context, int marginVertical, int horizontalMargin, boolean showSingerButton) {
        super(context);
        dividerV = marginVertical;
        dividerH = horizontalMargin;
        mIsShowSingerButton = showSingerButton;
        init();
    }

//    public WidgetSongPage(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }

//    public WidgetSongPage(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }

    public void notifyAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);


        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(dividerH).margin(0, 0).build();

        VerticalDividerItemDecoration2 verDivider = new VerticalDividerItemDecoration2.Builder(getContext())
                .color(Color.TRANSPARENT).size(dividerV).margin(dividerV).build();

        setLayoutManager(new GridLayoutManager(getContext(), 2));

        addItemDecoration(horDivider);
        addItemDecoration(verDivider);

        mAdapter = new AdtSong(getContext(), mIsShowSingerButton);
        mAdapter.setOnPreviewSongListener(this);
        mAdapter.setOnSongSelectListener(this);
        setVerticalScrollBarEnabled(false);
        setAdapter(mAdapter);
    }

    @Override
    public void onSongSelectListener(Song song) {
        if (mOnSongSelectListener != null)
            mOnSongSelectListener.onSongSelectListener(song);
    }

    private OnSongSelectListener mOnSongSelectListener;

    public void setOnSongSelectListener(OnSongSelectListener listener) {
        mOnSongSelectListener = listener;
    }

    public void setOnPreviewSongListener(OnPreviewSongListener listener) {
        this.mOnPreviewSongListener = listener;
    }

    @Override
    public void onPreviewSong(Song song) {
        if (mOnPreviewSongListener != null) {
            mOnPreviewSongListener.onPreviewSong(song);
        }
    }

    public void loadSong(int page, Map<String, String> map) {
        requestSongs(page, map);
    }

    public void loadSong(String method, int page, Map<String, String> map) {
        requestSongs(method, page, map);
    }

    public void setSong(List<Song> songs) {
        mAdapter.setData(songs);
    }

    private void requestSongs(String method, int page, Map<String, String> map) {
        mMethod = method;
        HttpRequest r = initRequest(mMethod);
        if (map != null) {
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                r.addParam(entry.getKey(), entry.getValue());
            }
        }
//        r.addParam("Nums", String.valueOf(Constant.PAGE_SIZE));
        r.setConvert2Class(Songs.class);
        r.doPost(page);
    }

    private void requestSongs(int page, Map<String, String> map) {
        mMethod = RequestMethod.GET_SONG;
        HttpRequest r = initRequest(RequestMethod.GET_SONG);
        if (map != null) {
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                r.addParam(entry.getKey(), entry.getValue());
            }
        }
//        r.addParam("Nums", String.valueOf(Constant.PAGE_SIZE));
        r.setConvert2Class(Songs.class);
        r.doPost(page);
    }

    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(getContext().getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStart(String method) {
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (method.equalsIgnoreCase(mMethod)) {
            try {
                if (object != null) {
                    Songs songs = (Songs) object;
                    if (songs != null && songs.list != null && songs.list.size() > 0) {
                        mAdapter.setData(songs.list);
                    }
                }
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), e.toString());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailed(String method, String error) {
    }
}