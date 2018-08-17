package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.KBoxInfo;

import java.lang.ref.WeakReference;

/**
 * author: Hanson
 * date:   2017/3/29
 * describe:
 */

public class StepDialog extends DialogFragment {
    private static StepDialog mInstance;
    private WeakReference<Activity> mAttached;
    private ViewGroup mRootView;
    private TextView mClose;
    private TextView mLocation;

    /**
     * 获取操作指引对话框
     *
     * @return
     */
    public static StepDialog getInstance() {
        if (mInstance == null) {
            mInstance = new StepDialog();
        }

        return mInstance;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mAttached = new WeakReference<>((Activity) context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CommonDialog);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //保证对话框使用自定义圆角背景
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRootView = (ViewGroup) inflater.inflate(R.layout.dlg_step, container, false);
        initData();
        initView();
        initListener();
        return mRootView;
    }

    private void initView() {
        mClose = (TextView) mRootView.findViewById(R.id.close);
        mLocation = (TextView) mRootView.findViewById(R.id.tv_location);

        if (KBoxInfo.getInstance().getKBox() != null) {
            mLocation.setText(KBoxInfo.getInstance().getKBox().getAddress());
        }
    }

    private void initData() {
        mInstance = this;
    }

    private void initListener() {
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = getResources().getDimensionPixelSize(R.dimen.dlg_step_width);
        int height = getResources().getDimensionPixelSize(R.dimen.dlg_step_height);
        WindowManager.LayoutParams window = getDialog().getWindow().getAttributes();
        window.width = width;
        window.height = height;
        window.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(window);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mInstance = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mAttached.clear();
        mAttached = null;
    }
}
