package com.beidousat.karaoke.biz;

import android.util.Log;

import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.model.CouponDetail;
import com.beidousat.karaoke.model.GiftDetail;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.model.User;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.FmPayResult;
import com.beidousat.karaoke.ui.dlg.LoadingUtil;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.ServerConfig;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:
 */

public class CouponHelper implements StoreHttpRequestListener {
    SupportQueryOrder mSupporter;

    public CouponHelper(SupportQueryOrder supporter) {
        mSupporter = supporter;
    }

    private String getServerUlr() {
        ServerConfig sconfig = ServerConfigData.getInstance().getServerConfig();
        if (sconfig == null) return null;
        return sconfig.getStore_web();
    }

    /**
     * 查询卡券信息
     */
    public void qCardDetail(String cardCode) {
        String serUrl = getServerUlr();
        if (serUrl==null) return;
        StoreHttpRequest request = new StoreHttpRequest(serUrl, RequestMethod.CARD_DETAIL);
        request.setStoreHttpRequestListener(this);
        request.addParam("card_code", cardCode);
        request.addParam("static_code", String.valueOf(1));
        request.setConvert2Class(CouponDetail.class);
        request.post();
    }

    @Override
    public void onStoreStart(String method) {
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        switch (method) {
            case RequestMethod.ORDER_QUERY:

                break;
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        LoadingUtil.closeLoadingDialog();
        mSupporter.sendRequestMessage(false, method, error);
    }
}
