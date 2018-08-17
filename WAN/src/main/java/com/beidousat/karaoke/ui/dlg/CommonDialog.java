package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.beidousat.karaoke.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * author: Hanson
 * date:   2017/3/29
 * describe:
 */

public class CommonDialog extends DialogFragment {
    public static CommonDialog mInstance;
    private WeakReference<FragmentActivity> mAttached;
    private ViewGroup mRootView;
    private TextView mClose;
    private FmBaseDialog mContent;
    /***需重写对话框关闭事件的页面****/
    private Map<String, View.OnClickListener> mCloseListeners = new HashMap<>();

    private boolean mShowClose = true;

    public void setContent(FmBaseDialog fragment) {
        if (mContent != null && mContent.isAdded()) {
            FragmentTransaction trans = getChildFragmentManager().beginTransaction();
            trans.replace(R.id.main_content, fragment);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commitAllowingStateLoss();
        }

        mContent = fragment;
        setupCloseView();
        fragment.attachDialog(this);
    }

    private void setupCloseView() {
        if (mClose != null) {
            mClose.setVisibility(mShowClose ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取公共对话框
     *
     * @return
     */
    public static CommonDialog getInstance() {
        if (mInstance == null) {
            mInstance = new CommonDialog();
        }

        mInstance.setCancelable(false);

        return mInstance;
    }

    /**
     * 是否显示“关闭”按钮
     *
     * @param isShow
     */
    public void setShowClose(boolean isShow) {
        mShowClose = isShow;
    }

    public void registerCloseListener(View.OnClickListener closeListener) {
        if (closeListener != null) {
            mCloseListeners.put(closeListener.getClass().getSimpleName(), closeListener);
        }
    }

    public void unregisterCloseListener(Object object) {
        if (object != null) {
            mCloseListeners.remove(object.getClass().getSimpleName());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mAttached = new WeakReference<>((FragmentActivity) context);
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

        mRootView = (ViewGroup) inflater.inflate(R.layout.dlg_common, container, false);

        initData();
        initView();
        initListener();
        return mRootView;
    }

    private void initView() {
        mClose = (TextView) mRootView.findViewById(R.id.close);
    }

    private void initData() {
        mInstance = this;

        if (mContent != null) {
            FragmentTransaction trans = getChildFragmentManager().beginTransaction();
            trans.replace(R.id.main_content, mContent);
            trans.commitAllowingStateLoss();
        }
    }

    private void initListener() {
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloseListeners != null && mCloseListeners.size() != 0) {
                    for (String key : mCloseListeners.keySet()) {
                        mCloseListeners.get(key).onClick(v);
                    }
                } else {
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = getResources().getDimensionPixelSize(R.dimen.dlg_width);
        int height = getResources().getDimensionPixelSize(R.dimen.dlg_height);

        WindowManager.LayoutParams window = getDialog().getWindow().getAttributes();
        window.width = width;
        window.height = height;
        window.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(window);

        setupCloseView();
    }

    @Override
    public void onPause() {
        super.onPause();

        mRootView.setBackgroundResource(R.drawable.bg_dlg_round_white);
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

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void onBackPressed() {
        FragmentManager fmManager = getChildFragmentManager();
        //获取最后一个fragment的index
        int lastIndex = fmManager.getFragments().size() - 1;
        //获取当前fragment上一级fragment
        if (lastIndex - 1 >= 0) {
            mContent = (FmBaseDialog) fmManager.getFragments().get(lastIndex - 1);
        } else {
            mContent = null;
        }

        if (fmManager.getFragments().size() - 1 < 0) {
            dismiss();
        } else {
            fmManager.popBackStack();
        }
    }
}
