package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;


/**
 * Created by J Wong on 2015/12/12 16:10.
 */
public class AdGetter implements HttpRequestListener {

    Context mContext;
    AdsRequestListener mAdsRequestListener;

    AdGetter(Context context, AdsRequestListener listener) {
        this.mContext = context;
        this.mAdsRequestListener = listener;
    }


    HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
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
        if (mAdsRequestListener != null)
            mAdsRequestListener.onAdsRequestFail();
    }
}
