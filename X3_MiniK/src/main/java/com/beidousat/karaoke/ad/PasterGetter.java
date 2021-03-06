package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;

/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class PasterGetter extends AdGetter {


    public PasterGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    public void getPaster(String position, String area) {
        HttpRequest r = initRequest(RequestMethod.GET_PASTER);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        if (!TextUtils.isEmpty(area))
            r.addParam("region", area);
        r.addParam("ADPosition", position);
        r.setConvert2Class(Ad.class);
        r.doPost(0);

    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        mAdsRequestListener.onAdsRequestFail();
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_PASTER.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null && !TextUtils.isEmpty(ad.ADContent)) {
                mAdsRequestListener.onAdsRequestSuccess(ad);
            } else {
                mAdsRequestListener.onAdsRequestFail();
            }
        }
    }
}
