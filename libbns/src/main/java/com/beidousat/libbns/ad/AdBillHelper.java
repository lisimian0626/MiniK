package com.beidousat.libbns.ad;

import android.content.Context;
import android.text.TextUtils;

import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;


/**
 * Created by J Wong on 2015/12/27 09:24.
 */
public class AdBillHelper implements HttpRequestListener {

    private Context mContext;

    public static AdBillHelper mAdBillHelper;

    public static AdBillHelper getInstance(Context context) {
        if (mAdBillHelper == null) {
            mAdBillHelper = new AdBillHelper(context);
        }
        return mAdBillHelper;
    }

    private AdBillHelper(Context context) {
        this.mContext = context;
    }

    public void billAd(Ad ad, String adPosition, String boxId) {
        billAd(String.valueOf(ad.ID), adPosition, boxId);
    }

    public void billAd(String adId, String adPosition, String boxId) {
        if (!TextUtils.isEmpty(adId) && !"null".equals(adId)) {
            HttpRequest r = initRequest(RequestMethod.AD_BILL);
            r.addParam("PlayID", adId);
            r.addParam("ADPosition", adPosition);
            r.addParam("RoomCode", boxId);
            r.doPost(0);
        } else {
            Logger.d("AdBillHelper", "not bill ad id is null");
        }
    }

//    public void billAd(String adId, String adPosition, String roomCode) {
//        if (!TextUtils.isEmpty(adId) && !"null".equals(adId)) {
//            HttpRequest r = initRequest(RequestMethod.AD_BILL);
//            r.addParam("PlayID", adId);
//            r.addParam("RoomCode", roomCode);
//            r.addParam("ADPosition", adPosition);
//            r.doPost(0);
//        } else {
//            Logger.d("AdBillHelper", "not bill ad id is null");
//        }
//    }

    HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStart(String method) {
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.AD_BILL.equals(method)) {
            Logger.i("AdBillHelper", "bill ad success");
        }
    }

    @Override
    public void onFailed(String method, String error) {
        if (RequestMethod.AD_BILL.equals(method))
            Logger.w("AdBillHelper", "bill ad fail");
    }
}
