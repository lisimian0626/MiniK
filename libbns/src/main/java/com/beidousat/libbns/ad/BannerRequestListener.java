package com.beidousat.libbns.ad;

import com.beidousat.libbns.model.BannerInfo;

import java.util.List;

/**
 * Created by J Wong on 2015/12/11 18:26.
 */
public interface BannerRequestListener {

    void onRequestSuccess(BannerInfo bannerInfo);

    void onRequestFail();

}
