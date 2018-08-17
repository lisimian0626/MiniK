package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.beidousat.karaoke.adapter.AdtSinger;
import com.beidousat.karaoke.interf.OnSingerClickListener;
import com.beidousat.karaoke.model.Singers;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.ui.fragment.FmSingerDetail;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2015/12/17 18:01.
 */
public class WidgetSingerPage extends RecyclerView implements HttpRequestListener, OnSingerClickListener {

    private AdtSinger mAdapter;

    public WidgetSingerPage(Context context) {
        super(context);
        init();
    }

    public WidgetSingerPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WidgetSingerPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);

        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(DensityUtil.dip2px(getContext(), 20)).build();

        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(DensityUtil.dip2px(getContext(), 12)).build();

        setLayoutManager(new GridLayoutManager(getContext(), 5));

        addItemDecoration(horDivider);
        addItemDecoration(verDivider);

        mAdapter = new AdtSinger(getContext());
        mAdapter.setOnSingerClickListener(this);
        setVerticalScrollBarEnabled(false);
        setAdapter(mAdapter);
    }


//    private void init() {
//        mAdapter = new AdtSinger(getContext());
//        setNumColumns(4);
//        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
//        setHorizontalSpacing(spacing);
//        setVerticalSpacing(spacing);
//        setSelector(R.drawable.selector_list_item_song);
//        setAdapter(mAdapter);
//    }

    @Override
    public void onSingerClick(StarInfo starInfo) {
        FmSingerDetail fmSingerDetail = FmSingerDetail.newInstance(starInfo);
        FragmentUtil.addFragment(fmSingerDetail);
    }

    public void setSinger(List<StarInfo> starInfos) {
        mAdapter.setData(starInfos);
    }

    public void loadSinger(int page, Map<String, String> map) {
        requestSingers(page, map);
    }

    private void requestSingers(int page, Map<String, String> map) {
        HttpRequest r = initRequest(RequestMethod.GET_SINGER);
        if (map != null) {
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                r.addParam(entry.getKey(), entry.getValue());
            }
        }
        r.setConvert2Class(Singers.class);
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
        if (RequestMethod.GET_SINGER.equalsIgnoreCase(method)) {
            try {
                Singers result = (Singers) object;
                if (result != null) {
                    if (result.list != null && result.list.size() > 0) {
                        mAdapter.setData(result.list);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), e.toString());
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {
    }
}
