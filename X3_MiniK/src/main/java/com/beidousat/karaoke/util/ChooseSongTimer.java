package com.beidousat.karaoke.util;

import android.os.Handler;

import com.beidousat.libbns.util.Logger;

/**
 * Created by J Wong on 2017/10/11.
 */

public class ChooseSongTimer {

    private int TIME = 1000;
    private int i = 0;

    private static ChooseSongTimer mChooseSongTimer;
    private ChooseSongTimerListener mChooseSongTimerListener;

    public static ChooseSongTimer getInstance() {
        if (mChooseSongTimer == null) {
            mChooseSongTimer = new ChooseSongTimer();
        }

        return mChooseSongTimer;
    }

    public void resetCount() {
        i = 0;
    }

    public ChooseSongTimer startTimer() {
        stopTimer();
        resetCount();
        Logger.d("ChooseSongTimer", "startTimer");
        handler.postDelayed(runnable, TIME); //每隔1s执行

        return mChooseSongTimer;
    }

    public ChooseSongTimer stopTimer() {
        Logger.d("ChooseSongTimer", "stopTimer");
        resetCount();
        handler.removeCallbacks(runnable);
        return mChooseSongTimer;
    }

    public ChooseSongTimer setChooseSongTimerListener(ChooseSongTimerListener listener) {
        this.mChooseSongTimerListener = listener;
        return mChooseSongTimer;
    }


    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                handler.postDelayed(this, TIME);
                i++;
                Logger.d("ChooseSongTimer", "runnable :" + i);

                if (mChooseSongTimerListener != null) {
                    mChooseSongTimerListener.onChooseSongTimer(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public interface ChooseSongTimerListener {
        void onChooseSongTimer(int count);
    }
}
