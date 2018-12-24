package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.PayResult;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.GlideUtils;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;
import com.bumptech.glide.Glide;

import java.util.List;


/**
 * author: Hanson
 * date:   2017/7/20
 * dscribe:
 */

public class FmChoosePay extends FmBaseDialog implements View.OnClickListener, SupportQueryOrder {

    private LinearLayout mZhiFuBaoBtn, mWeiXinBtn, mTouBiBtn, mCardBtn,mOctBtn;
    private ImageView mBackBtn, mIv_zhifubao, mIv_wechat, mIv_toubi;
    private String mPayment;
    public final static String MEAL_TAG = "SelectedMeal";
    public final static String MEAL_CARDCODE = "CardCode";
    private Meal mMeal;
    private String mCardcode;
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
        mCardcode = getArguments().getString(MEAL_CARDCODE);
        mQueryOrderHelper = new QueryOrderHelper(this);

    }

    @Override
    void initView() {

        mZhiFuBaoBtn = (LinearLayout) findViewById(R.id.zhifubao_btn);
        mWeiXinBtn = (LinearLayout) findViewById(R.id.weixin_btn);
        mBackBtn = (ImageView) findViewById(R.id.dlg_fm_pay_back);
        mTouBiBtn = (LinearLayout) findViewById(R.id.toubi_btn);
        mCardBtn = (LinearLayout) findViewById(R.id.card_btn);
        mIv_zhifubao = (ImageView) findViewById(R.id.iv_zhifubao);
        mIv_wechat = (ImageView) findViewById(R.id.iv_wechat);
        mIv_toubi = (ImageView) findViewById(R.id.iv_toubi);
        mOctBtn=(LinearLayout)findViewById(R.id.lin_ost);

        List<PayMent> payMentList = KBoxInfo.getInstance().getmPayMentlist();
        if (payMentList != null && payMentList.size() > 0) {
            for (PayMent payMent : payMentList) {
                switch (payMent.getName()) {
                    case "alipay":
                        GlideUtils.showImageView(getActivity(), R.drawable.pay_zhifubao, payMent.getLogo_url(), mIv_zhifubao);
                        break;
                    case "wechat":
                        GlideUtils.showImageView(getActivity(), R.drawable.pay_weixin, payMent.getLogo_url(), mIv_wechat);
                }
            }
        }
        if(KBoxInfo.getInstance().getKBox().getUse_online()==1){
            mZhiFuBaoBtn.setVisibility(View.VISIBLE);
            mWeiXinBtn.setVisibility(View.VISIBLE);
        }else{
            mZhiFuBaoBtn.setVisibility(View.GONE);
            mWeiXinBtn.setVisibility(View.GONE);
        }
        if (KBoxInfo.getInstance().getKBox().getUse_coin() == 1) {
            mTouBiBtn.setVisibility(View.VISIBLE);
        } else {
            mTouBiBtn.setVisibility(View.GONE);
        }
        if(KBoxInfo.getInstance().getKBox().getUse_gift_card() == 1){
            mCardBtn.setVisibility(View.VISIBLE);
        }else{
            mCardBtn.setVisibility(View.GONE);
        }
        if(KBoxInfo.getInstance().getKBox().getUse_pos() == 1){
            mOctBtn.setVisibility(View.VISIBLE);
        }else{
            mOctBtn.setVisibility(View.GONE);
        }
    }

    @Override
    void setListener() {
        mWeiXinBtn.setOnClickListener(this);
        mTouBiBtn.setOnClickListener(this);
        mZhiFuBaoBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mCardBtn.setOnClickListener(this);
        mOctBtn.setOnClickListener(this);
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
            case R.id.zhifubao_btn:
                mPayment = "alipay";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CREATE).post();
                break;
            case R.id.weixin_btn:
                mPayment = "wechat";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CREATE).post();
                break;
            case R.id.toubi_btn:
                mPayment = "coin";
                showTBPayNumber();
//                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CREATE).post();
                break;
            case R.id.card_btn:
                mPayment = "card";
                showCardPay();
//                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CREATE).post();
                break;
            case R.id.lin_ost:
                mPayment="Oct";
                showOctPayNumber();
                break;
            case R.id.dlg_fm_pay_back:
                CommonDialog dialog = CommonDialog.getInstance();
                dialog.setShowClose(true);
                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                dialog.setContent(FmPayMeal.createMealFragment(pageType, mCardcode));
                if (!dialog.isAdded()) {
                    dialog.show(Main.mMainActivity.getSupportFragmentManager(), "commonDialog");
                }
                break;
        }

    }

    @Override
    public StoreHttpRequest initStoreRequest(String urlHost, String method) {
        StoreHttpRequest request = new StoreHttpRequest(urlHost,method);
        request.setStoreHttpRequestListener(this);
        switch (mPayment){
            case "alipay":
                request.addParam(HttpParamsUtils.initPayCreateParams(mMeal.getOrderSn(),"alipay"));
                break;
            case "wechat":
                request.addParam(HttpParamsUtils.initPayCreateParams(mMeal.getOrderSn(),"wechat"));
                break;

        }
        request.setConvert2Class(PayResult.class);
        return request;
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        super.onStoreSuccess(url, object);
        LoadingUtil.closeLoadingDialog();
        switch (url) {
            case RequestMethod.PAY_CREATE:
                if (object instanceof PayResult) {
                    PayResult payResult = (PayResult) object;
                    switch (mPayment){
                        case "alipay":
                            showQrCode(payResult);
                            break;
                        case "wechat":
                            showQrCode(payResult);
                            break;
                        case "coin":
                            break;
                        case "card":
                            break;
                    }
                }
                break;
        }
    }


//    private void dealAfterCreateOrder(Meal meal) {
//        float nowPrice = meal.getPrice();
//        float preRealPrice = mMeal.getRealPrice();
//        mSelectedMeal = meal;
//
//        Logger.d("FmChoosePay", "nowRealPrice:" + nowPrice + "  preRealPrice:" + preRealPrice);
//
//        if (nowPrice != preRealPrice) {
//            showPriceChangeDialog();
//        } else {
//            if ("coin".equals(mPayment)) {
//                showTBPayNumber(meal);
//            } else if ("card".equals(mPayment)) {
//                showCardPay(meal);
//            } else {
//                showQrCode(meal);
//            }
//        }
//    }

    @Override
    public void onStoreFailed(String url, String error) {
        LoadingUtil.closeLoadingDialog();
        DialogFactory.showErrorDialog(Main.mMainActivity, error, new DialogInterface.OnClickListener() {
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

    private void showQrCode(PayResult payResult) {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmPayQrCode qrCode = new FmPayQrCode();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmPayQrCode.MEAL_TAG, mMeal);
        bundle.putString(FmPayQrCode.TYPE_TAG, mPayment);
        bundle.putString(FmPayQrCode.QR_CODE,payResult.getQrcode_data());
        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }


    private void showTBPayNumber() {
        Logger.d(getClass().getSimpleName(), "showTBPayNumber  meal:" + mMeal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmTBPayNumber qrCode = new FmTBPayNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmTBPayNumber.MEAL_TAG, mMeal);
        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }
    private void showOctPayNumber() {
//        Logger.d(getClass().getSimpleName(), "showTBPayNumber  meal:" + mMeal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmOctoNumber fmOctoNumber = new FmOctoNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmOctoNumber.MEAL_TAG, mMeal);
        fmOctoNumber.setArguments(bundle);
        dialog.setContent(fmOctoNumber);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }
    private void showCardPay() {
        Logger.d(getClass().getSimpleName(), "showPayCard  meal:" + mMeal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmPayCard payCard = new FmPayCard();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmPayCard.MEAL_TAG, mMeal);
        payCard.setArguments(bundle);
        dialog.setContent(payCard);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }

    private DlgPriceChange mDlgPriceChange;
    private DlgWebView dlgWebView;

//    private void showPriceChangeDialog() {
//        if (isAdded() && (mDlgPriceChange == null || !mDlgPriceChange.isShowing())) {
//            mDlgPriceChange = new DlgPriceChange(Main.mMainActivity);
//            mDlgPriceChange.setMessage(getString(R.string.price_changed));
//            mDlgPriceChange.setLinkMessage(getString(R.string.check_dsc), new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (dlgWebView == null || !dlgWebView.isShowing()) {
//                        dlgWebView = new DlgWebView(Main.mMainActivity,
//                                ServerConfigData.getInstance().getServerConfig().getStore_web() + "package/?kbox_sn=" + PrefData.getRoomCode(getContext())
//                        );
//                        dlgWebView.show();
//                    }
//                }
//            });
//            mDlgPriceChange.setPositiveButton(getString(R.string.go_to_buy), new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mDlgPriceChange.dismiss();
//                    if ("coin".equals(mPayment)) {
//                        showTBPayNumber(mSelectedMeal);
//                    } else if ("card".equals(mPayment)) {
//                        showCardPay(mSelectedMeal);
//                    } else {
//                        showQrCode(mSelectedMeal);
//                    }
//                }
//            });
//            mDlgPriceChange.setCancelButton(getString(R.string.not_buy), new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mQueryOrderHelper.cancelOrder(mSelectedMeal).post();
//                    mDlgPriceChange.dismiss();
//                }
//            });
//            mDlgPriceChange.show();
//        }
//    }

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
