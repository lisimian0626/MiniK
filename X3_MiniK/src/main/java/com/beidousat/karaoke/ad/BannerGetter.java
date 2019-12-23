package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.libbns.ad.BannerRequestListener;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.google.gson.reflect.TypeToken;

import java.util.List;


/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class BannerGetter implements HttpRequestListener {
    Context mContext;
    BannerRequestListener mBannerRequestListener;

    public BannerGetter(Context mContext, BannerRequestListener mRequestListener) {
        this.mContext = mContext;
        this.mBannerRequestListener = mRequestListener;
    }

    HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }


    public void getBanner(String position, String sn) {
        HttpRequest r = initRequest(RequestMethod.GET_BANNER);
        r.addParam("device_sn", sn);
        r.addParam("pcode", position);
        r.setConvert2Class(BannerInfo.class);
        r.doGet();
    }

//    public void getBanner(String position, String sn) {
//        if(ServerConfigData.getInstance().getServerConfig()==null)
//            return;
//        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.GET_BANNER);
//        request.addParam("device_sn", sn);
//        request.addParam("pcode", position);
//        request.setConvert2Class(BannerInfo.class);
//        request.get();
//    }

    @Override
    public void onStart(String method) {

    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_BANNER.equals(method)) {
            BannerInfo bannerInfo = (BannerInfo) object;
            if (bannerInfo != null)
                mBannerRequestListener.onRequestSuccess(bannerInfo);
        }
    }

    @Override
    public void onFailed(String method, String error) {

    }
}
