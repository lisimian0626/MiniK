package com.beidousat.karaoke.ui.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;

/**
 * Created by J Wong on 2017/3/24.
 */

public class BaseFragment extends Fragment implements HttpRequestListener {

    View mRootView;

    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
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

//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        return MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
//    }
}
