package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.BnsConfig;


/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class BannerGetter extends AdGetter {


    public BannerGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    public void getBanner(String position, String area) {
        HttpRequest r = initRequest(RequestMethod.GET_BANNER);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        r.addParam("ADPosition", position);
        if (!TextUtils.isEmpty(area))
            r.addParam("region", area);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
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
