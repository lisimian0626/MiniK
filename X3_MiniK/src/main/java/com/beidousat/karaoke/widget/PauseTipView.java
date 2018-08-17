package com.beidousat.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.BnsConfig;

import de.greenrobot.event.EventBus;

import static com.beidousat.libbns.util.BnsConfig.MAX_PAUSE_TIME;

/**
 * author: Hanson
 * date:   2017/4/11
 * describe:
 */

public class PauseTipView extends RelativeLayout {
    private ViewGroup mRootView;
    private CountDownTextView mCountDown;
    private TextView mTvPauseTimes;

    private DettachWindownListener mDettachListener;

    private int mLeftPauseTimes = BnsConfig.MAX_PAUSE_TIME - 1;


    public PauseTipView(Context context) {
        this(context, null);
    }

    public PauseTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PauseTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.popup_pause, this, true);

        mCountDown = (CountDownTextView) mRootView.findViewById(R.id.tv_countdown);
        mTvPauseTimes = (TextView) mRootView.findViewById(R.id.tv_pause_times);

        mTvPauseTimes.setText(context.getResources().getString(
                R.string.text_pause_residue_degree, mLeftPauseTimes));
        mCountDown.setFinishedListener(new CountDownTextView.TimerFinished() {
            @Override
            public void onFinish() {
                if (mDettachListener != null) {
                    mDettachListener.onViewDettachWindow();
                }
                EventBusUtil.postSticky(EventBusId.id.PLAYER_PLAY, null);
            }
        });
        setVisibility(GONE);
    }

    public void showPauseTipView() {
        if (mLeftPauseTimes >= 0) {
            mTvPauseTimes.setText(getContext().getResources().getString(
                    R.string.text_pause_residue_degree, mLeftPauseTimes));
            setVisibility(VISIBLE);
            mCountDown.restart();
        }
    }

    public void setDettachListener(DettachWindownListener dettachListener) {
        mDettachListener = dettachListener;
    }

    public void hidePauseTipView() {
        mLeftPauseTimes--;
        setVisibility(GONE);
        mCountDown.stop();
    }

    public boolean canPause() {
        return mLeftPauseTimes >= 0;
    }

    public void resetLeftTimes() {
        mLeftPauseTimes = MAX_PAUSE_TIME - 1;
    }

    public interface DettachWindownListener {
        void onViewDettachWindow();
    }
}
