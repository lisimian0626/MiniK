package com.beidousat.karaoke.biz;

import android.util.Log;

import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.model.User;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.FmPayResult;
import com.beidousat.karaoke.ui.dlg.LoadingUtil;
import com.beidousat.libbns.evenbus.EventBusUtil;
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

public class QueryOrderHelper implements StoreHttpRequestListener {
    SupportQueryOrder mSupporter;
    Meal mMeal;
    String order_sn;

    public QueryOrderHelper(SupportQueryOrder supporter) {
        mSupporter = supporter;
    }

    /**
     * 线上支付，查询支付情况
     * */
    public void queryOrder(Meal meal) {
        mMeal = meal;
//        SSLHttpRequest request = new SSLHttpRequest(mSupporter.getSupportedContext(), RequestMethod.QUERY_ORDER);
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_QUERY);
        request.setStoreHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initQueryOrderParams(
                mMeal.getOrderSn(),
                DeviceUtil.getCupChipID(),
                KBoxInfo.getInstance().getKBox().getKBoxSn()
        ));
        request.setConvert2Class(PayStatus.class);
        request.post();
    }

    /**
     * 查询订单支付成功后的用户信息
     */
    public void queryUser() {
        PayStatus payStatus = BoughtMeal.getInstance().getTheLastPaystatus();
        if (payStatus == null) {
            User tmp_user = new User();
            tmp_user.setNickName("未登陆");
            tmp_user.setAvatar("");
            PayUserInfo.getInstance().addUser(tmp_user);
            tmp_user = null;
            return;
        }
//        SSLHttpRequest request = new SSLHttpRequest(mSupporter.getSupportedContext(), RequestMethod.QUERY_USER);
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.USER_QUERY);
        request.setStoreHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initQueryUserParams(
                payStatus.getPayUserID(),
                "",
                "",
                ""
        ));
        request.setConvert2Class(User.class);
        request.post();
    }

    /**
     * 点击取消订单
     *
     * */
    public void cancelOrder(Meal meal) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CANCEL);
        request.setStoreHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initCancelOrderParams(meal.getOrderSn()));
        request.setConvert2Class(User.class);
        request.post();
    }

    /**
     * 线下支付完成，通知服务器
     * */
    public void reportCoinPayFinish(Meal meal) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_FINISH_PAY);
        request.setStoreHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initCancelOrderParams(meal.getOrderSn()));
        request.post();
    }

    @Override
    public void onStoreStart(String method) {
        switch (method) {
            case RequestMethod.ORDER_CANCEL:
//                LoadingUtil.showLoadingDialog(mSupporter.getSupportedContext());
                LoadingUtil.showLoadingDialog(Main.mMainActivity);
                break;
        }
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        LoadingUtil.closeLoadingDialog();
        switch (method) {
            case RequestMethod.ORDER_QUERY:
                PayStatus payStatus = (PayStatus) object;
                Logger.d("pay", "接口返回查询订单结果,payStatus=" + payStatus.getPayStatus());
                //防止多次触发导致套餐叠加
                if (order_sn == null && payStatus.getPayStatus() == PayStatus.PAY_SUCESS) {
                    order_sn = payStatus.getOrderSn();
                    boolean isMealExpire = BoughtMeal.getInstance().isMealExpire();
//                    if(!PreferenceUtil.getBoolean(Main.mMainActivity,"isSingle", false)){
                    if (!PrefData.getIsSingle(Main.mMainActivity)) {
                        CommonDialog dialog = CommonDialog.getInstance();
                        dialog.setShowClose(true);
                        dialog.setContent(new FmPayResult());
                        if (!dialog.isAdded()) {
                            dialog.show(mSupporter.getSupportedFragmentManager(), "commonDialog");
                        }
                    }
                    Logger.d("pay", "支付成功");
                    //确保支付的套餐是正确的
                    mMeal.setAmount(payStatus.getAmount());
                    mMeal.setType(payStatus.getType());
                    //设置当前购买的套餐
                    BoughtMeal.getInstance().setBoughtMeal(mMeal, payStatus);
                    EventBusUtil.postPaySucceed(isMealExpire);
                }
                break;
            case RequestMethod.USER_QUERY:
                User user = (User) object;
                PayUserInfo.getInstance().addUser(user);
                break;
            case RequestMethod.ORDER_CANCEL:
                mSupporter.sendRequestMessage(true, method, null);
                break;
            case RequestMethod.ORDER_FINISH_PAY:
//                Log.e("test",object.toString());
//                PayStatus payStatus1 = (PayStatus) object;
//                if (payStatus1.getPayStatus() == PayStatus.PAY_SUCESS) {
//                    boolean isMealExpire = BoughtMeal.getInstance().isMealExpire();
//                    Logger.d("QueryOrderHelper", "onStoreSuccess PAY_SUCESS isMealExpire:" + isMealExpire);
//                    if(!Common.isSingle){
//                        CommonDialog dialog = CommonDialog.getInstance();
//                        dialog.setShowClose(true);
//                        dialog.setContent(new FmPayResult());
//                        if (!dialog.isAdded()) {
//                            dialog.show(mSupporter.getSupportedFragmentManager(), "commonDialog");
//                        }
//                    }
//                    //确保支付的套餐是正确的
//                    mMeal.setAmount(payStatus1.getAmount());
//                    mMeal.setType(payStatus1.getType());
//                    //设置当前购买的套餐
//                    BoughtMeal.getInstance().setBoughtMeal(mMeal, payStatus1);
//                    EventBusUtil.postPaySucceed(isMealExpire);
//                }
                break;
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        LoadingUtil.closeLoadingDialog();
        mSupporter.sendRequestMessage(false, method, error);
    }
}
