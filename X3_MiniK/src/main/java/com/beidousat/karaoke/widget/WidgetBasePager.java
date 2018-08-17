package com.beidousat.karaoke.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.beidousat.karaoke.interf.OnPageScrollListener;
import com.beidousat.libwidget.viewpager.ViewPagerScroller;

/**
 * Created by J Wong on 2015/12/18 08:42.
 */
public class WidgetBasePager extends ViewPager {


    Context mContext;

    private static final int MOVE_LIMITATION = 100;// 触发移动的像素距离

    private float mLastMotionX; // 手指触碰屏幕的最后一次x坐标

    private Scroller mScroller; // 滑动控件

    private boolean isScrollable = true;

    private float mLastTocuhX;

    private OnPageScrollListener mOnPageScrollListener;


    public WidgetBasePager(Context context) {
        super(context);
        initView(context);
    }

    public WidgetBasePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mScroller = new Scroller(context);
        setPageMargin(20);
        ViewPagerScroller.setViewPagerScrollDuration(this, 500);
        setOffscreenPageLimit(3);
        this.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mLastTocuhX = 0;
                if (mOnPageScrollListener != null)
                    mOnPageScrollListener.onPagerSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void setScrollable(boolean scrollable) {
        this.isScrollable = scrollable;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isScrollable) {
            return false;
        }

        final int action = event.getAction();
        final float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLastTocuhX > 0) {
                    if (mLastTocuhX < x) {
                        if (mOnPageScrollListener != null)
                            mOnPageScrollListener.onPageScrollLeft();
                    } else if (mLastTocuhX > x) {
                        if (mOnPageScrollListener != null)
                            mOnPageScrollListener.onPageScrollRight();
                    }
                }
                mLastTocuhX = x;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x - mLastMotionX) >= MOVE_LIMITATION) {
                    final int curItem = getCurrentItem();
                    if (x - mLastMotionX > 0) {//
                        if (curItem > 0) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentItem(curItem - 1);
                                }
                            }, 100);
                        }
                    } else {
                        if (curItem < getAdapter().getCount() - 1) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentItem(curItem + 1);
                                }
                            }, 100);
                        }
                    }
                    return super.onTouchEvent(event);
                }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!isScrollable) {
            return false;
        }
        final int action = arg0.getAction();
        final float x = arg0.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(arg0);
    }

    public void setOnPagerScrollListener(OnPageScrollListener listener) {
        this.mOnPageScrollListener = listener;
    }
}