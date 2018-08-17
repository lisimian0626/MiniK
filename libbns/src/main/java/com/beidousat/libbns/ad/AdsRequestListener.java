package com.beidousat.libbns.ad;

import com.beidousat.libbns.model.Ad;

/**
 * Created by J Wong on 2015/12/11 18:26.
 */
public interface AdsRequestListener {

    void onAdsRequestSuccess(Ad ad);

    void onAdsRequestFail();

}
