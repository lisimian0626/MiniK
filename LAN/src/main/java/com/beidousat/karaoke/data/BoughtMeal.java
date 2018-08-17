package com.beidousat.karaoke.data;

import android.os.CountDownTimer;

import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.TimeUtils;

import java.util.Observable;

import de.greenrobot.event.EventBus;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:已买套餐
 */

public class BoughtMeal extends Observable {
    private static final BoughtMeal mInstance = new BoughtMeal();
    private Meal mMeal;
    private PayStatus mPayStatus;
    private int mLeftSongs = 0;
    private long mLeftMls = 0L;
    private boolean isExpire = true;

    private CountDownTimer mCountDownTimer;

    private static final long MIN_TO_MLS = 60 * 1000; //分钟转毫秒基数
    private static final int COUNTDOWN_INTERVAL = 1000; //倒数间隔

    private BoughtMeal() {

    }

    public static BoughtMeal getInstance() {
        return mInstance;
    }

    /**
     * 设置当前购买的套餐；暂时不支持套餐叠加
     *
     * @param bought
     */
    public void setBoughtMeal(Meal bought) {
        if (bought == null) return;

        if (!isExpire && mMeal.getType() != bought.getType()) {
            throw new IllegalArgumentException("续费套餐类型不同");
        }
        //如果没有买过则设置套餐信息
        if (mMeal == null) {
            mMeal = bought;
        }

        if (bought.getType() == Meal.SONG) {
            //如果是续费，则剩余歌曲=当前剩余(首)+续费套餐歌曲(首)
            mLeftSongs = isExpire ? bought.getAmount() : (bought.getAmount() + mLeftSongs);
            //设置套餐有效
            isExpire = false;
        } else if (bought.getType() == Meal.TIME) {
            //如果是续费，则剩余时间=当前剩余时间+续费套餐歌曲时间
            mLeftMls = isExpire ? mMeal.getAmount() * MIN_TO_MLS : (bought.getAmount() * MIN_TO_MLS + mLeftMls);
            //设置套餐有效
            isExpire = false;

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }

            mCountDownTimer = new CountDownTimer(mLeftMls, COUNTDOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mLeftMls = millisUntilFinished;
                    notifyMealObervers();
                }

                @Override
                public void onFinish() {
                    setMealExpire(true);
                    notifyMealObervers();
                }
            };
            mCountDownTimer.start();
        }

        notifyMealObervers();
    }

    /**
     * 获取当前购买的套餐
     *
     * @return
     */
    public Meal getMeal() {
        return mMeal;
    }

    /**
     * 如果是包曲模式，则每切歌一次都需调用该方法更新剩余几首
     */
    public void updateLeftSongs() {
        if (mMeal != null && mMeal.getType() == Meal.SONG) {
            --mLeftSongs;
            notifyMealObervers();
        }
    }

    /**
     * 设置套餐过期；包曲模式剩余歌曲为零，或者包时模式时间到了
     *
     * @param expire
     */
    public void setMealExpire(boolean expire) {
        isExpire = expire;
        if (isExpire) {
            mMeal = null;
            mPayStatus = null;
            EventBusUtil.postMealExpire(null);
        }
    }

    /**
     * 套餐是否过期
     *
     * @return
     */
    public boolean isMealExpire() {
        return isExpire;
    }

    /**
     * 剩余多少首
     *
     * @return
     */
    public int getLeftSongs() {
        return mLeftSongs;
    }

    /**
     * 剩余多少毫秒
     *
     * @return
     */
    public long getLeftMillSeconds() {
        return mLeftMls;
    }

    /**
     * 剩余多少分钟
     *
     * @return
     */
    public String getLeftMinite() {
        return TimeUtils.convertLongString(mLeftMls);
    }

    /**
     * 是否购买包时套餐；默认为包时套餐(用于控制暂停次数，未购买时可以操作
     * 页面，避免在未购买时点击暂停弹出暂停次数弹出)
     *
     * @return
     */
    public boolean isBuyTime() {
        return mMeal != null && mMeal.getType() == Meal.TIME;
    }

    /**
     * 是否购买包曲套餐；默认为包时套餐(用于控制暂停次数，未购买时可以操作
     * 页面，避免在未购买时点击暂停弹出暂停次数弹出)
     *
     * @return
     */
    public boolean isBuySong() {
        return mMeal != null && mMeal.getType() == Meal.SONG;
    }

    public void setPayStatus(PayStatus payStatus) {
        mPayStatus = payStatus;
    }

    public PayStatus getPayStatus() {
        return mPayStatus;
    }

    private void notifyMealObervers() {
        setChanged();
        notifyObservers();
    }

    public boolean checkLeftSong() {
        Logger.d("BoughtMeal", "checkLeftSong =====>" + mLeftSongs);
        if (mMeal != null && mMeal.getType() == Meal.SONG) {
            if (mLeftSongs <= 0) {
                setMealExpire(true);
                notifyMealObervers();
            }
        }
        return mLeftSongs > 0;
    }
}
