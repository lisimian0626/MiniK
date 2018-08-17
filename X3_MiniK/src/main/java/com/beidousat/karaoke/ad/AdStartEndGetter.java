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
 * 片头片尾广告
 */
public class AdStartEndGetter extends AdGetter {

    private String mPosition;

    public AdStartEndGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }

    /**
     * 片头广告
     */
    public void getStart(String songId, String area) {
        getAd("W1", songId == null ? "" : songId, area);
    }

    /**
     * 片尾广告
     */
    public void getEnd(String songId, String area) {
        getAd("W2", songId == null ? "" : songId, area);
    }

    private void getAd(String position, String songId, String area) {
        try {
            mPosition = position;
            HttpRequest r = initRequest(RequestMethod.GET_AD_START_END);
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
        if (RequestMethod.GET_AD_START_END.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null) {
                mAdsRequestListener.onAdsRequestSuccess(ad);
            } else {
                mAdsRequestListener.onAdsRequestFail();
            }
        }
    }


    private boolean isVideoFile(String path) {
        String lowPath = path.toLowerCase();
        return (lowPath.endsWith(".mp4") || lowPath.endsWith(".m3u8") || path.endsWith(".mpg") || path.endsWith(".mpeg")
                || path.endsWith(".avi") || path.endsWith(".rm") || path.endsWith(".rmvb") || lowPath.endsWith(".m3u8"));
    }

    private boolean isImage(String path) {
        String lowPath = path.toLowerCase();
        return (lowPath.endsWith(".png") || lowPath.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".bmp")
                || path.endsWith(".webp") || path.endsWith(".gif"));
    }
}