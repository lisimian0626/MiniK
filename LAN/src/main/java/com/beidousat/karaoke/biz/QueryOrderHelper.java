package com.beidousat.karaoke.biz;

import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.model.User;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.FmPayResult;
import com.beidousat.karaoke.ui.dlg.LoadingUtil;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.SSLHttpRequest;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:
 */

public class QueryOrderHelper implements HttpRequestListener {
    SupportQueryOrder mSupporter;
    Meal mMeal;

    public QueryOrderHelper(SupportQueryOrder supporter, Meal meal) {
        mSupporter = supporter;
        mMeal = meal;
    }

    public HttpRequest queryOrder() {
        SSLHttpRequest request = new SSLHttpRequest(mSupporter.getSupportedContext(), RequestMethod.QUERY_ORDER);
        request.setHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initQueryOrderParams(
                mMeal.getOrderSn(),
                DeviceUtil.getCupChipID(mSupporter.getSupportedContext()),
                ""
        ));
        request.setConvert2Class(PayStatus.class);
        return request;
    }

    public HttpRequest queryUser() {
        SSLHttpRequest request = new SSLHttpRequest(mSupporter.getSupportedContext(), RequestMethod.QUERY_USER);
        request.setHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initQueryUserParams(
                BoughtMeal.getInstance().getPayStatus().getPayUserID(),
                BoughtMeal.getInstance().getMeal().getOrderSn(),
                DeviceUtil.getCupChipID(mSupporter.getSupportedContext()),
                ""
        ));
        request.setConvert2Class(User.class);
        return request;
    }

    public HttpRequest cancelOrder() {
        SSLHttpRequest request = new SSLHttpRequest(mSupporter.getSupportedContext(), RequestMethod.CANCEL_ORDER);
        request.setHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initCancelOrderParams(mMeal.getOrderSn()));
        request.setConvert2Class(User.class);
        return request;
    }

    @Override
    public void onStart(String method) {
        switch (method) {
            case RequestMethod.CANCEL_ORDER:
                LoadingUtil.showLoadingDialog(mSupporter.getSupportedContext());
                break;
        }
    }

    @Override
    public void onSuccess(String method, Object object) {
        LoadingUtil.closeLoadingDialog();

        switch (method) {
            case RequestMethod.QUERY_ORDER:
                PayStatus payStatus = (PayStatus) object;
                if (payStatus.getPayStatus() == PayStatus.PAY_SUCESS) {
                    CommonDialog dialog = CommonDialog.getInstance();
                    dialog.setShowClose(true);
                    dialog.setContent(new FmPayResult());
                    if (!dialog.isAdded()) {
                        dialog.show(mSupporter.getSupportedFragmentManager(), "commonDialog");
                    }
                    //确保支付的套餐是正确的
                    mMeal.setAmount(payStatus.getAmount());
                    mMeal.setType(payStatus.getType());
                    //设置当前购买的套餐
                    BoughtMeal.getInstance().setBoughtMeal(mMeal);
                    BoughtMeal.getInstance().setPayStatus(payStatus);

                    EventBusUtil.postPaySucceed(null);
                }
                break;
            case RequestMethod.QUERY_USER:
                User user = (User) object;
                if (!User.isEmpty(user)) {
                    PayUserInfo.getInstance().addUser(user);
                }
                break;
            case RequestMethod.CANCEL_ORDER:
                mSupporter.sendRequestMessage(true, method, null);
                break;
        }
    }

    @Override
    public void onFailed(String method, String error) {
        LoadingUtil.closeLoadingDialog();
        mSupporter.sendRequestMessage(false, method, error);
    }
}
