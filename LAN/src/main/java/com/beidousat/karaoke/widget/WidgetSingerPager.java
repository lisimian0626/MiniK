package com.beidousat.karaoke.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beidousat.karaoke.model.StarInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2015/12/18 08:42.
 */
public class WidgetSingerPager extends WidgetBasePager {


    private SingerPagerAdapter mAdapter;
    private Map<String, String> mRequestParams = new HashMap<String, String>();
    private Map<Integer, List<StarInfo>> mIndexPage;


    public WidgetSingerPager(Context context) {
        super(context);
    }

    public WidgetSingerPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void initPager(int totalPage, List<StarInfo> firstPageSinger, Map<String, String> requestParams) {
        mIndexPage = new HashMap<Integer, List<StarInfo>>();
        mIndexPage.put(1, firstPageSinger);
        mRequestParams = requestParams;
        mAdapter = new SingerPagerAdapter(mContext, totalPage);
        setOffscreenPageLimit(3);
        setAdapter(mAdapter);
    }

    private class SingerPagerAdapter extends PagerAdapter {

        private Context mContext;
        private SparseArray<WidgetSingerPage> sparseArray = new SparseArray<WidgetSingerPage>();
        private int mPageCount;

        public SingerPagerAdapter(Context context, int pageCount) {
            mContext = context;
            this.mPageCount = pageCount;
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

            WidgetSingerPage widgetSingerPage;
            if (sparseArray.get(position) == null) {
                widgetSingerPage = new WidgetSingerPage(mContext);
                widgetSingerPage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                if (mIndexPage.containsKey(position + 1)) {
                    widgetSingerPage.setSinger(mIndexPage.get(position + 1));
                } else {
                    widgetSingerPage.loadSinger(position + 1, mRequestParams);
                }
                sparseArray.put(position, widgetSingerPage);
            } else {
                widgetSingerPage = sparseArray.get(position);
            }
            container.addView(widgetSingerPage);

            return widgetSingerPage;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            WidgetSingerPage view = (WidgetSingerPage) object;
            container.removeView(view);
//            sparseArray.put(position, view);
            sparseArray.delete(position);
        }
    }
}