package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;

/**
 * Created by J Wong on 2015/12/12 16:08.
 */
public class CornerGetter extends AdGetter {


    public CornerGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    public void getCorner(String position, String songId, String area) {
        HttpRequest r = initRequest(RequestMethod.GET_CORNER);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        r.addParam("ADPosition", position);
        r.addParam("SongID", songId);
        if (!TextUtils.isEmpty(area))
            r.addParam("region", area);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
    }

    private void getCorner(String position, String area) {
        HttpRequest r = initRequest(RequestMethod.GET_CORNER);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        r.addParam("ADPosition", position);
        if (!TextUtils.isEmpty(area))
            r.addParam("region", area);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        if (mAdsRequestListener != null)
            mAdsRequestListener.onAdsRequestSuccess(AdDefault.getCornerDefaultAd());
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_CORNER.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null) {
                mAdsRequestListener.onAdsRequestSuccess(ad);
            } else {
                mAdsRequestListener.onAdsRequestSuccess(AdDefault.getCornerDefaultAd());
            }
        }
    }
}
