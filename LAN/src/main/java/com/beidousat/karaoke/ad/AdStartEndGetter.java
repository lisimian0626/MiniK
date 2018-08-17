package com.beidousat.karaoke.ad;

import android.content.Context;

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
    public void getStart(String songId) {
        getAd("W1", songId == null ? "" : songId);
    }

    /**
     * 片尾广告
     */
    public void getEnd(String songId) {
        getAd("W2", songId == null ? "" : songId);
    }

    private void getAd(String position, String songId) {
        try {
            mPosition = position;
//                RoomDetail roomDetail = RoomInfo.getInstance().getRoomDetail();
            HttpRequest r = initRequest(RequestMethod.GET_AD_START_END);
//                r.addParam("RoomCode", roomDetail == null || roomDetail.RoomCode == null ? "" : roomDetail.RoomCode);
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