package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class FmPayResult extends FmBaseDialog {
    CountDownTimer mFinishCountDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_result, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
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
