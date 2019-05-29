package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.google.gson.reflect.TypeToken;

import java.util.List;


/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class BannerGetter extends AdGetter {


    public BannerGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    public void getBanner(String position, String sn) {
        HttpRequest r = initRequest(RequestMethod.GET_BANNER);
        r.addParam("device_sn", sn);
        r.addParam("pcode", position);
        r.setConvert2Token(new TypeToken<List<BannerInfo>>() {
        });
        r.doGet();
    }


    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_BANNER.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null)
                mAdsRequestListener.onAdsRequestSuccess(ad);
        }
    }
}
