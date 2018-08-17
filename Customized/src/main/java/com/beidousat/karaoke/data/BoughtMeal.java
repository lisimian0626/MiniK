package com.beidousat.karaoke.data;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.beidousat.libbns.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static com.beidousat.libbns.util.PreferenceUtil.setString;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:已买套餐
 */

public class BoughtMeal extends Observable {
    private final static BoughtMeal mInstance = new BoughtMeal();

    @Expose
    private List<Meal> mMealList;
    @Expose
    private List<PayStatus> mPayStatusList;
    @Expose
    private int mLeftSongs = 0;
    @Expose
    private long mLeftMls = 0L;
    @Expose
    private boolean isExpire = true;

    private boolean isRestore = false;
    private CountDownTimer mCountDownTimer;

    private static final long MIN_TO_MLS = 60 * 1000; //分钟转毫秒基数
    private static final int COUNTDOWN_INTERVAL = 1000; //倒数间隔

    /*SharePreference keys*/
    private static final String PREF_KEY_BOUGHT_MEAL = "pref_key_bought_meal";
    private static final String PREF_KEY_PAY_MEAL = "pref_key_pay_meal";
    private static final String PREF_KEY_PAY_STATUS = "pref_key_pay_status";
    private static final String PREF_KEY_LEFT_SONGS = "pref_key_left_songs";
    private static final String PREF_KEY_LEFT_MLS = "pref_key_left_mls";
    private static final String PREF_KEY_MEAL_EXPIRE = "pref_key_meal_expire";

    private static final String TAG = BoughtMeal.class.getSimpleName();

    private BoughtMeal() {
        mMealList = new ArrayList<>();
        mPayStatusList = new ArrayList<>();
    }

    public static BoughtMeal getInstance() {
        return mInstance;
    }

    /**
     * 设置当前购买的套餐；暂时不支持套餐叠加
     *
     * @param bought
     */
    public void setBoughtMeal(Meal bought, PayStatus payStatus) {
        if (bought == null || payStatus == null) return;

        if (!isExpire && getMealType() != bought.getType()) {
            Logger.d(TAG, "续费套餐类型不同");
        }
        Logger.d(TAG, "setBoughtMeal isExpire :" + isExpire);

        if (isExpire) {
            clearMealInfo();
            clearMealInfoSharePreference();
        }

        //如果没有买过则设置套餐信息
        mMealList.add(bought);
        mPayStatusList.add(payStatus);

        setupObservable(bought, false);
    }

    /**
     * 设置被观察对象；如果是本地恢复模式，则不重新计算剩余歌曲或者时间数量
     *
     * @param bought    当前购买的套餐信息
     * @param isRestore 是否是本地恢复模式
     */
    private void setupObservable(Meal bought, boolean isRestore) {
        if (bought == null)
            return;

        saveMealInfoToSharePreference();
        Logger.d(TAG, "setupObservable isExpire :" + isExpire + "  getType:" + bought.getType());

        if (bought.getType() == Meal.SONG) {
            Logger.d(TAG, "setupObservable ");
            if (!isRestore) {
                //如果是续费，则剩余歌曲=当前剩余(首)+续费套餐歌曲(首)
                mLeftSongs = isExpire ? bought.getAmount() : (bought.getAmount() + mLeftSongs);
                //设置套餐有效
                isExpire = false;

                Logger.d(TAG, "setupObservable mLeftSongs :" + mLeftSongs + "  getAmount:" + bought.getAmount());

            }
        } else if (bought.getType() == Meal.TIME) {
            if (!isRestore) {
                //如果是续费，则剩余时间=当前剩余时间+续费套餐歌曲时间
                //bought.setAmount(1);
                mLeftMls = isExpire ? bought.getAmount() * MIN_TO_MLS : (bought.getAmount() * MIN_TO_MLS + mLeftMls);
                //设置套餐有效
                isExpire = false;
            }

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
                    mLeftMls = 0;
                    setMealExpire(true);
                    notifyMealObervers();
                }
            };
            mCountDownTimer.start();
        }
        notifyMealObervers();
    }

    /**
     * 获取当前购买的第一个套餐（续费时会有多个套餐，但套餐类型都一样）
     *
     * @return
     */
    public Meal getTheFirstMeal() {
        Meal meal = null;
        if (mMealList != null && mMealList.size() > 0) {
            meal = mMealList.get(0);
        }

        return meal;
    }

    /**
     * 获取当前购买的最后一个套餐（续费时会有多个套餐，但套餐类型都一样）
     *
     * @return
     */
    public Meal getTheLastMeal() {
        Meal meal = null;
        if (mMealList != null && mMealList.size() > 0) {
            meal = mMealList.get(mMealList.size() - 1);
        }

        return meal;
    }

    public PayStatus getTheFirstPayStatus() {
        PayStatus status = null;
        if (mPayStatusList != null && mPayStatusList.size() > 0) {
            status = mPayStatusList.get(0);
        }

        return status;
    }

    public PayStatus getTheLastPaystatus() {
        PayStatus status = null;
        if (mPayStatusList != null && mPayStatusList.size() > 0) {
            status = mPayStatusList.get(mPayStatusList.size() - 1);
        }

        return status;
    }

    public int getMealType() {
        Meal meal = null;
        if (mMealList != null && mMealList.size() > 0) {
            meal = mMealList.get(0);
        }

        return meal == null ? Meal.SONG : meal.getType();
    }

    /**
     * 如果是包曲模式，则每切歌一次都需调用该方法更新剩余几首
     */
    public void updateLeftSongs() {
        if (mMealList != null && getMealType() == Meal.SONG) {
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
            EventBusUtil.postMealExpire(null);
        }
    }

    public void clearMealInfo() {
        mMealList.clear();
        mPayStatusList.clear();
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
        return mMealList != null && getMealType() == Meal.TIME;
    }

    /**
     * 是否购买包曲套餐；默认为包时套餐(用于控制暂停次数，未购买时可以操作
     * 页面，避免在未购买时点击暂停弹出暂停次数弹出)
     *
     * @return
     */
    public boolean isBuySong() {
        return mMealList != null && getMealType() == Meal.SONG;
    }

    @Deprecated
    public void addPayStatus(PayStatus payStatus) {
        mPayStatusList.add(payStatus);
    }

    private void notifyMealObervers() {
        setChanged();
        notifyObservers();
    }

    public boolean checkLeftSong() {
        Logger.d("BoughtMeal", "checkLeftSong =====>" + mLeftSongs);
        if (mMealList != null && getMealType() == Meal.SONG) {
            if (mLeftSongs <= 0) {
                setMealExpire(true);
                notifyMealObervers();
            }
        }
        return mLeftSongs > 0;
    }

    public void saveMealInfoToSharePreference() {
        try {
            if (BoughtMeal.getInstance() != null) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                String json = gson.getAdapter(new TypeToken<List<Meal>>() {
                }).toJson(mMealList);
                setString(LanApp.getInstance(), PREF_KEY_PAY_MEAL, json);
//                Logger.d(TAG, "save---"+json);

                json = gson.getAdapter(new TypeToken<List<PayStatus>>() {
                }).toJson(mPayStatusList);
                setString(LanApp.getInstance(), PREF_KEY_PAY_STATUS, json);
//                Logger.d(TAG, json);

                if (getMealType() == Meal.SONG) {
                    PreferenceUtil.setInt(LanApp.getInstance(), PREF_KEY_LEFT_SONGS, mLeftSongs);
//                    Logger.d(TAG, "save---song="+String.valueOf(mLeftSongs));
                } else {
                    PreferenceUtil.setLong(LanApp.getInstance(), PREF_KEY_LEFT_MLS, mLeftMls);
//                    Logger.d(TAG, "save---mls="+String.valueOf(mLeftMls));
                }
                PreferenceUtil.setBoolean(LanApp.getInstance(), PREF_KEY_MEAL_EXPIRE, isExpire);
            }
        } catch (Exception e) {
            Logger.d(TAG, e.toString());
        }
        Log.d(TAG, "saveMealInfoToSharePreference");
    }

    public void clearMealInfoSharePreference() {
        PreferenceUtil.setString(LanApp.getInstance(), PREF_KEY_PAY_MEAL, new JsonArray().toString());
        PreferenceUtil.setString(LanApp.getInstance(), PREF_KEY_PAY_STATUS, new JsonArray().toString());
        PreferenceUtil.setInt(LanApp.getInstance(), PREF_KEY_LEFT_SONGS, 0);
        PreferenceUtil.setLong(LanApp.getInstance(), PREF_KEY_LEFT_MLS, 0);
        PreferenceUtil.setLong(LanApp.getInstance(), PREF_KEY_LEFT_MLS, 0);
        PreferenceUtil.setBoolean(LanApp.getInstance(), PREF_KEY_MEAL_EXPIRE, true);

        Log.d(TAG, "clearMealInfoSharePreference");
    }

    public void restoreMealInfoFromSharePreference() {
        try {
            if (!isRestore) {
                isRestore = true;
                String jsonMeal, jsonStatus;
                Gson gson = new Gson();

                jsonMeal = PreferenceUtil.getString(LanApp.getInstance(), PREF_KEY_PAY_MEAL, "");
                Logger.d(TAG, "restore---" + jsonMeal);
                if (!TextUtils.isEmpty(jsonMeal)) {
                    mMealList = gson.fromJson(jsonMeal, new TypeToken<List<Meal>>() {
                    }.getType());
                }

                jsonStatus = PreferenceUtil.getString(LanApp.getInstance(), PREF_KEY_PAY_STATUS, "");
                if (!TextUtils.isEmpty(jsonStatus)) {
                    mPayStatusList = gson.fromJson(jsonStatus, new TypeToken<List<PayStatus>>() {
                    }.getType());
                }
                Logger.d(TAG, "restore---" + jsonStatus);

                isExpire = PreferenceUtil.getBoolean(LanApp.getInstance(), PREF_KEY_MEAL_EXPIRE, true);
                if (getMealType() == Meal.SONG) {
                    mLeftSongs = PreferenceUtil.getInt(LanApp.getInstance(), PREF_KEY_LEFT_SONGS, 0);
                    //leftsong数量分两种情况；1、当前正在播放已点歌曲时，mLeftSongs += 1;
                    //                      2、当前没有播放已点歌曲，mLeftSongs = mLeftSongs;
                    // 当ChooseSongs.getSong().size() > 0 表示有已点歌曲，那么当前播放器正在播放已点歌曲
                    if (!isExpire && ChooseSongs.getInstance(LanApp.getInstance()).getSongs().size() > 0) {
                        mLeftSongs += 1;
                    }
                    Logger.d(TAG, "restore---song=" + String.valueOf(mLeftSongs));
                } else {
                    mLeftMls = PreferenceUtil.getLong(LanApp.getInstance(), PREF_KEY_LEFT_MLS, 0);
                    Logger.d(TAG, "save---mls=" + String.valueOf(mLeftMls));
                }

                if (!isExpire) {
                    setupObservable(getTheLastMeal(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
