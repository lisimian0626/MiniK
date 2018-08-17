package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class FmFail extends FmBaseDialog {
    CountDownTimer mFinishCountDownTimer;
    TextView tv_tips;
    public final static String FAIL_TIPS="CardCode";
    String tips;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_fail, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        tips=getArguments().getString(FAIL_TIPS);
        mFinishCountDownTimer = new CountDownTimer(3000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mAttached.dismiss();
            }
        };
        mFinishCountDownTimer.start();
    }

    @Override
    void initView() {
        if(TextUtils.isEmpty(tips))
        tv_tips=(TextView) mRootView.findViewById(R.id.fm_fail_tips);
    }

    @Override
    void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mAttached.registerCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFinishCountDownTimer.cancel();
                mAttached.dismiss();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mAttached.unregisterCloseListener(this);
    }

}
