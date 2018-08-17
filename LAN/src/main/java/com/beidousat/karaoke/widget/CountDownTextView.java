package com.beidousat.karaoke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.util.BnsConfig;

/**
 * author: Hanson
 * date:   2017/4/1
 * describe:
 */

public class CountDownTextView extends TextView {
    private CountDownTimer mTimer;
    private int mMaxTimerSes = BnsConfig.CNT_FINISH; //单位秒
    private TimerFinished mFinishedListener;


    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView);
        a.getInt(R.styleable.CountDownTextView_maxCountDownSec, mMaxTimerSes);
        a.recycle();

        init();
    }

    private void init() {
        //加2s是为了能显示出30s和0s
        mTimer = new CountDownTimer((mMaxTimerSes+2)*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int)millisUntilFinished/1000 -1;
                if (sec < 10) {
                    setText(" " + sec + " s");
                } else {
                    setText(sec + " s");
                }

            }

            @Override
            public void onFinish() {
                setText("0" + " s");
                mTimer.cancel();
                mTimer = null;

                if (mFinishedListener != null) {
                    mFinishedListener.onFinish();
                }
            }
        };

        setText(mMaxTimerSes+" s");
    }

    public void setFinishedListener(TimerFinished listener) {
        mFinishedListener = listener;
    }

    public void start() {
        if (mTimer != null) {
            mTimer.start();
        }
    }

    public void restart() {
        stop();
        init();
        start();
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public interface TimerFinished {
        void onFinish();
    }
}
