package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;


/**
 * author: Hanson
 * date:   2017/7/20
 * dscribe:
 */

public class FmChoosePay extends FmBaseDialog implements View.OnClickListener, SupportQueryOrder {

    private LinearLayout mTouBiBtn;
    private TextView mBackBtn;
    private String mPayment;
    public final static String MEAL_TAG = "SelectedMeal";
    private Meal mMeal;

    private QueryOrderHelper mQueryOrderHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_choose_pay, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mMeal = (Meal) getArguments().getSerializable(MEAL_TAG);
        mQueryOrderHelper = new QueryOrderHelper(this);

    }

    @Override
    void initView() {
        mBackBtn = (TextView) findViewById(R.id.btn_back);
        mTouBiBtn = (LinearLayout) findViewById(R.id.toubi_btn);
        if (mMeal.getUse_cion() == 1) {
            mTouBiBtn.setVisibility(View.VISIBLE);
        } else {
            mTouBiBtn.setVisibility(View.GONE);
        }
    }

    @Override
    void setListener() {
        mTouBiBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart(String method) {
        super.onStart(method);
        LoadingUtil.showLoadingDialog(Main.mMainActivity);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toubi_btn:
                mPayment = "coin";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE).post();
                break;
            case R.id.btn_back:
                CommonDialog dialog = CommonDialog.getInstance();
                dialog.setShowClose(true);
                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                dialog.setContent(FmPayMeal.createMealFragment(pageType));
                if (!dialog.isAdded()) {
                    dialog.show(Main.mMainActivity.getSupportFragmentManager(), "commonDialog");
                }
                break;
        }

    }

    @Override
    public StoreHttpRequest initStoreRequest(String urlHost, String method) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE);
        request.setStoreHttpRequestListener(this);
//        request.addParam(HttpParamsUtils.initCreateOrderParams(mMeal.getType(), mMeal.getAmount(),
//                mPayment, DeviceUtil.getCupChipID(), KBoxInfo.getInstance().getKBox().getKBoxSn()));

        if ("coin".equals(mPayment))
            request.addParam("is_offline", 1 + "");

        request.setConvert2Class(Meal.class);
        return request;
    }

    @Override
    public void onSuccess(String method, Object object) {
        super.onSuccess(method, object);
        LoadingUtil.closeLoadingDialog();
        switch (method) {
            case RequestMethod.ORDER_CREATE:
                if (object instanceof Meal) {
                    Meal meal = (Meal) object;
                    if (meal != null) {
                        dealAfterCreateOrder(meal);
                    }
                }
                break;
        }
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        LoadingUtil.closeLoadingDialog();
        DialogFactory.showErrorDialog(getContext(), error, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        super.onStoreSuccess(url, object);
        LoadingUtil.closeLoadingDialog();
        switch (url) {
            case RequestMethod.ORDER_CREATE:
                if (object instanceof Meal) {
                    Meal meal = (Meal) object;
                    if (meal != null) {
                        dealAfterCreateOrder(meal);
                    }
                }
                break;
        }
    }


    private void dealAfterCreateOrder(Meal meal) {
        float nowPrice = meal.getPrice();
        float preRealPrice = mMeal.getRealPrice();
        mSelectedMeal = meal;
        Logger.d("FmChoosePay", "nowRealPrice:" + nowPrice + "  preRealPrice:" + preRealPrice);
        paySuccess();
    }


    private void paySuccess() {
        PayStatus payStatus = new PayStatus();
        payStatus.setPayStatus(1);
        payStatus.setPayTime((int) System.currentTimeMillis());
        payStatus.setOrderSn(mSelectedMeal.getOrderSn());
        payStatus.setDeviceSn(DeviceUtil.getCupChipID());
        payStatus.setType(mSelectedMeal.getType());
        payStatus.setAmount(mSelectedMeal.getAmount());

        boolean isMealExpire = BoughtMeal.getInstance().isMealExpire();
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        dialog.setContent(new FmPayResult());
        if (!dialog.isAdded()) {
            dialog.show(getFragmentManager(), "commonDialog");
        }
        //确保支付的套餐是正确的
        mSelectedMeal.setAmount(payStatus.getAmount());
        mSelectedMeal.setType(payStatus.getType());

        mQueryOrderHelper.reportCoinPayFinish(mSelectedMeal).post();

        //设置当前购买的套餐
        BoughtMeal.getInstance().setBoughtMeal(mSelectedMeal, payStatus);
        EventBusUtil.postPaySucceed(isMealExpire);

    }

    @Override
    public void onStoreFailed(String url, String error) {
        LoadingUtil.closeLoadingDialog();
        DialogFactory.showErrorDialog(getContext(), error, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        super.onStoreFailed(url, error);
    }

    @Override
    public void onStoreStart(String method) {
        super.onStart(method);
        LoadingUtil.showLoadingDialog(Main.mMainActivity);
    }

    private Meal mSelectedMeal;


    @Override
    public Context getSupportedContext() {
        return getContext();
    }

    @Override
    public FragmentManager getSupportedFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void sendRequestMessage(boolean isSucced, String method, Object data) {
        switch (method) {
            case RequestMethod.ORDER_CANCEL:
                if (isSucced) {
//                    mRequestHandler.sendEmptyMessage(10010);
                }
                break;
        }
    }
}
