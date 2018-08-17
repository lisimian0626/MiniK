package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;

/**
 * Created by J Wong on 2016/12/19.
 * 公益广告
 */
public class AdBenefitGetter extends AdGetter {

    public AdBenefitGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    /**
     * 获取公益视频广告
     */
    public void getBenefitVideo() {
        getBenefit("Y1", null);

    }

    /**
     * 获取屏保公益广告
     */
    public void getBenefitScreen(String roomCode) {
        getBenefit("Y2", roomCode);
    }

    /**
     * @param position Y1:播歌屏，未开房前一直播放，开房后播放一次
     *                 Y2:墙面板屏保：未开房前一直播放
     */
    private void getBenefit(String position, String roomCode) {
//        if (OkConfig.AD_MODE) {
        HttpRequest r = initRequest(RequestMethod.GET_AD_BENEFIT);
        r.addParam("ADPosition", position);
        if (!TextUtils.isEmpty(roomCode))
            r.addParam("RoomCode", roomCode);
        r.setConvert2Class(Ad.class);
        r.doPost(0);
//        }
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_AD_BENEFIT.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null && mAdsRequestListener != null)
                mAdsRequestListener.onAdsRequestSuccess(ad);
        }
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        if (mAdsRequestListener != null)
            mAdsRequestListener.onAdsRequestFail();
    }
}