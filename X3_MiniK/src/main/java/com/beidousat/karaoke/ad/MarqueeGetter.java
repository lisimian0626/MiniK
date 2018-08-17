package com.beidousat.karaoke.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by J Wong on 2015/12/11 17:58.
 */
public class MarqueeGetter implements HttpRequestListener {

    public interface AdMarqueeRequestListener {
        void onAdMarqueeRequest(List<Ad> ads);

        void onAdMarqueeRequestFail();
    }

    private Context mContext;
    private AdMarqueeRequestListener mAdMarqueeRequestListener;

    public MarqueeGetter(Context context, AdMarqueeRequestListener listener) {
        mContext = context;
        mAdMarqueeRequestListener = listener;
    }

    HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    public void getMarquee(String position, String songId, String area) {
        HttpRequest r = initRequest(RequestMethod.GET_MARQUEE);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        r.addParam("ADPosition", position);
        if (!TextUtils.isEmpty(songId))
            r.addParam("SongID", songId);
        if (!TextUtils.isEmpty(area))
            r.addParam("region", area);
        r.setConvert2Token(new TypeToken<List<Ad>>() {
        });
        r.doPost(0);
    }


    private void getMarquee(String position) {
        HttpRequest r = initRequest(RequestMethod.GET_MARQUEE);
        r.addParam("RoomCode", PrefData.getRoomCode(mContext));
        r.addParam("ADPosition", position);
        r.setConvert2Token(new TypeToken<List<Ad>>() {
        });
        r.doPost(0);
    }

    @Override
    public void onStart(String method) {

    }

    @Override
    public void onFailed(String method, String error) {
        if (mAdMarqueeRequestListener != null) {
            mAdMarqueeRequestListener.onAdMarqueeRequestFail();
        }
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_MARQUEE.equals(method)) {
            List<Ad> ads = (List<Ad>) object;
            if (ads != null)
                mAdMarqueeRequestListener.onAdMarqueeRequest(ads);
        }
    }
}
