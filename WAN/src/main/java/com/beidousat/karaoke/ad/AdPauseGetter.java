package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
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
    public void getSongStopAd(String songId, String area) {
        getStopAd("S1", songId == null ? "" : songId, area);
    }

    /**
     * 电影暂停广告
     */
    public void getMovieStopAd(String songId, String area) {
        getStopAd("S2", songId == null ? "" : songId, area);
    }

    /**
     * 暂停广告
     */
    private void getStopAd(String position, String songId, String area) {
        try {
            HttpRequest r = initRequest(RequestMethod.GET_AD_PAUSE);
            r.addParam("RoomCode", PrefData.getRoomCode(mContext));
            r.addParam("ADPosition", position);
            r.addParam("SongID", songId);
            if (!TextUtils.isEmpty(area))
                r.addParam("region", area);
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