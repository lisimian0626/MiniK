package com.beidousat.karaoke.ad;

import android.content.Context;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libbns.ad.AdsRequestListener;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

/**
 * Created by J Wong on 2017/08/16.
 * 屏保广告
 */
public class AdScreenGetter extends AdGetter {


    public AdScreenGetter(Context context, AdsRequestListener listener) {
        super(context, listener);
    }


    /**
     * 屏保广告
     */
    private void getScreenAd() {
        try {
            HttpRequest r = initRequest(RequestMethod.GET_AD_SCREEN);
            r.addParam("RoomCode", PrefData.getRoomCode(mContext));
            r.addParam("ADPosition", "P2");
            Song song = ChooseSongs.getInstance(mContext).getFirstSong();
            if (song != null) {
                r.addParam("SongID", song.ID);
            }
            r.doPost(0);
        } catch (Exception e) {
            Logger.d("AdScreenGetter", "getScreenAd ex:" + e.toString());
        }
    }


    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_AD_SCREEN.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null)
                mAdsRequestListener.onAdsRequestSuccess(ad);
        }
    }

}