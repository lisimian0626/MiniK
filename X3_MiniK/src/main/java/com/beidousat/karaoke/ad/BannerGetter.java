package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.libbns.ad.BannerRequestListener;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.google.gson.reflect.TypeToken;

import java.util.List;


/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class BannerGetter implements HttpRequestListener {
    Context mContext;
    BannerRequestListener mBannerRequestListener;

    public BannerGetter(Context mContext, BannerRequestListener mBannerRequestListener) {
        this.mContext = mContext;
        this.mBannerRequestListener = mBannerRequestListener;
    }

    public void getBanner(String position, String sn) {
        HttpRequest r = initRequest(RequestMethod.GET_BANNER);
        r.addParam("device_sn", sn);
        r.addParam("pcode", position);
        r.setConvert2Token(new TypeToken<List<BannerInfo>>() {
        });
        r.doGet();
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
        if (RequestMethod.GET_BANNER.equals(method)) {
            List<BannerInfo> bannerInfos = (List<BannerInfo>) object;
            if (bannerInfos != null&&bannerInfos.size()>0)
                mBannerRequestListener.onRequestSuccess(bannerInfos);
        }
    }

    @Override
    public void onFailed(String method, String error) {

    }
}
