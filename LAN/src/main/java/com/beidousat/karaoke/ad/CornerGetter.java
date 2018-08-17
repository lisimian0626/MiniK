package com.beidousat.karaoke.ad;

import android.content.Context;

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

    public void getCorner(String position, String songId) {
//        RoomDetail roomDetail = RoomInfo.getCommonDialog().getRoomDetail();
        HttpRequest r = initRequest(RequestMethod.GET_CORNER);
//        r.addParam("RoomCode", roomDetail.RoomCode);
        r.addParam("ADPosition", position);
        r.addParam("SongID", songId);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
    }

    public void getCorner(String position) {
//        if (OkConfig.AD_MODE) {
//            RoomDetail roomDetail = RoomInfo.getCommonDialog().getRoomDetail();
        HttpRequest r = initRequest(RequestMethod.GET_CORNER);
//            r.addParam("RoomCode", roomDetail.RoomCode);
        r.addParam("ADPosition", position);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
//        }
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
