package com.beidousat.libwidget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * @desc ViewPager 自动播放setCurrentItem滑动太快，看不到动画效果，用这个类设置即可改变滑动时间
 * Created by ZZB on 2015/7/16 10:21
 */
public class ViewPagerScroller extends Scroller {

    /**ViewPager滑动时间*/
    private int mScrollDuration = 0;

    public ViewPagerScroller(Context context, int scrollDuration){
        super(context);
        mScrollDuration = scrollDuration;
    }

    public static void setViewPagerScrollDuration(ViewPager viewPager, int scrollDuration){
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(viewPager.getContext( ), scrollDuration);
            mScroller.set(viewPager, scroller);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
}
