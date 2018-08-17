package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

/**
 * Created by J Wong on 2016/12/19.
 * 暂停广告
 */
public class AdPauseGetter extends AdGetter {


    public AdPauseGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    /**
     * 歌曲暂停广告
     */
    public void getSongStopAd(String songId) {
        getStopAd("S1", songId == null ? "" : songId);
    }

    /**
     * 电影暂停广告
     */
    public void getMovieStopAd(String songId) {
        getStopAd("S2", songId == null ? "" : songId);
    }

    /**
     * 暂停广告
     */
    private void getStopAd(String position, String songId) {
        try {
//            RoomDetail roomDetail = RoomInfo.getInstance().getRoomDetail();
            HttpRequest r = initRequest(RequestMethod.GET_AD_PAUSE);
//            r.addParam("RoomCode", roomDetail == null || roomDetail.RoomCode == null ? "" : roomDetail.RoomCode);
            r.addParam("ADPosition", position);
            r.addParam("SongID", songId);
            r.setConvert2Class(Ad.class);
            r.doPost(0);
        } catch (Exception e) {
            Logger.d("AdStopGetter", "getStop ex:" + e.toString());
        }
    }


    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_AD_PAUSE.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null)
                mAdsRequestListener.onAdsRequestSuccess(ad);
        }
    }

}