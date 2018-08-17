package com.beidousat.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.TimeUtils;

import static com.beidousat.libbns.util.BnsConfig.MAX_PAUSE_TIME;

/**
 * author: Hanson
 * date:   2017/4/11
 * describe:
 */

public class ChooseSongTipView extends RelativeLayout {

    private ViewGroup mRootView;
    private TextView mCountDown;
    private TextView mTvPauseTimes;

    public ChooseSongTipView(Context context) {
        this(context, null);
    }

    public ChooseSongTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChooseSongTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.popup_choose_song, this, true);
        mCountDown = (TextView) mRootView.findViewById(R.id.tv_countdown);
        mTvPauseTimes = (TextView) mRootView.findViewById(R.id.tv_pause_times);

        mTvPauseTimes.setText(R.string.choose_song_tip);
    }

    public void showView() {
        setVisibility(VISIBLE);
    }

    public void hideView() {
        setVisibility(GONE);
    }

    public void setTime(int time) {
        mCountDown.setText(TimeUtils.convertLongSecString(time));
    }

}
