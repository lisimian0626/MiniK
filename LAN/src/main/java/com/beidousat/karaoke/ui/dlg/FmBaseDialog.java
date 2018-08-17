package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;

/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public abstract class FmBaseDialog extends Fragment implements HttpRequestListener {
    View mRootView;
    CommonDialog mAttached;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            initData();
            initView();
            setListener();
        }
        return mRootView;
    }

    abstract void initData();
    abstract void initView();
    abstract void setListener();

    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    public void attachDialog(CommonDialog dialog) {
        mAttached = dialog;
    }

    @Override
    public void onStart(String method) {

    }

    @Override
    public void onSuccess(String method, Object object) {
    }

    @Override
    public void onFailed(String method, String error) {

    }

    public <T extends View> T findViewById(@IdRes int id) {
        if (mRootView == null)
            return null;

        return (T) mRootView.findViewById(id);
    }
}
