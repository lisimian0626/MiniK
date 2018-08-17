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

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
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

import java.util.List;


/**
 * author: Hanson
 * date:   2017/7/20
 * dscribe:
 */

public class FmChoosePay extends FmBaseDialog implements View.OnClickListener, SupportQueryOrder {

    private LinearLayout mZhiFuBaoBtn, mWeiXinBtn, mTouBiBtn;
    private ImageView mBackBtn,mIv_zhifubao,mIv_wechat,mIv_toubi;
    private String mPayment;
    public final static String MEAL_TAG = "SelectedMeal";
    public final static String MEAL_CARDCODE="CardCode";
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
        mCardcode=getArguments().getString(MEAL_CARDCODE);
        mQueryOrderHelper = new QueryOrderHelper(this);

    }

    @Override
    void initView() {

        mZhiFuBaoBtn = (LinearLayout) findViewById(R.id.zhifubao_btn);
        mWeiXinBtn = (LinearLayout) findViewById(R.id.weixin_btn);
        mBackBtn = (ImageView) findViewById(R.id.dlg_fm_pay_back);
        mTouBiBtn = (LinearLayout) findViewById(R.id.toubi_btn);
        mIv_zhifubao=(ImageView)findViewById(R.id.iv_zhifubao);
        mIv_wechat=(ImageView)findViewById(R.id.iv_wechat);
        mIv_toubi=(ImageView)findViewById(R.id.iv_toubi);
        List<PayMent> payMentList= KBoxInfo.getInstance().getmPayMentlist();
        if(payMentList!=null&&payMentList.size()>0){
            for (PayMent payMent:payMentList){
                switch (payMent.getName()){
                    case "alipay":
                        GlideUtils.showImageView(getActivity(), R.drawable.pay_zhifubao,payMent.getLogo_url(),mIv_zhifubao);
                        break;
                    case "wechat":
                        GlideUtils.showImageView(getActivity(), R.drawable.pay_weixin,payMent.getLogo_url(),mIv_wechat);
                }
            }
        }
        if (mMeal.getUse_cion() == 1) {
            mTouBiBtn.setVisibility(View.VISIBLE);
        } else {
            mTouBiBtn.setVisibility(View.GONE);
        }
    }

    @Override
    void setListener() {
        mWeiXinBtn.setOnClickListener(this);
        mTouBiBtn.setOnClickListener(this);
        mZhiFuBaoBtn.setOnClickListener(this);
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
            case R.id.zhifubao_btn:
                mPayment = "alipay";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE).post();
                break;
            case R.id.weixin_btn:
                mPayment = "wechat";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE).post();
                break;
            case R.id.toubi_btn:
                mPayment = "coin";
                initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE).post();
                break;
            case R.id.dlg_fm_pay_back:
                CommonDialog dialog = CommonDialog.getInstance();
                dialog.setShowClose(true);
                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                dialog.setContent(FmPayMeal.createMealFragment(pageType,mCardcode));
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
        if(TextUtils.isEmpty(mCardcode)){
            request.addParam(HttpParamsUtils.initCreateOrderParams(mMeal.getType(), mMeal.getAmount(),
                    mPayment, DeviceUtil.getCupChipID(), KBoxInfo.getInstance().getKBox().getKBoxSn(), "", 1));

        }else{
            request.addParam(HttpParamsUtils.initCreateOrderParams(mMeal.getType(), mMeal.getAmount(),
                    mPayment, DeviceUtil.getCupChipID(), KBoxInfo.getInstance().getKBox().getKBoxSn(), mCardcode, 1));
        }

        if ("coin".equals(mPayment))
            request.addParam("is_offline", 1 + "");

        request.setConvert2Class(Meal.class);

        return request;
    }

//    @Override
//    public StoreHttpRequest initRequest(String method) {
//        StoreHttpRequest request=new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE);
////        SSLHttpRequest request = new SSLHttpRequest(getActivity().getApplicationContext(), method);
//        request.setStoreHttpRequestListener(this);
//        request.addParam(HttpParamsUtils.initCreateOrderParams(
//                mMeal.getType(),
//                mMeal.getAmount(),
//                mPayment,
//                DeviceUtil.getCupChipID(),
//                KBoxInfo.getInstance().getKBox().getKBoxSn()));
//        request.setConvert2Class(Meal.class);
//        return request;
//    }

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
        if(getContext()!=null){
            ToastUtils.toast(getContext(),error);
        }

//        DialogFactory.showErrorDialog(getContext(), error, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
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

        if (nowPrice != preRealPrice) {
            showPriceChangeDialog();
        } else {
            if ("coin".equals(mPayment)) {
                showTBPayNumber(meal);
            } else {
                showQrCode(meal);
            }
        }
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

    private void showQrCode(Meal meal) {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmPayQrCode qrCode = new FmPayQrCode();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmPayQrCode.MEAL_TAG, meal);
        bundle.putString(FmPayQrCode.TYPE_TAG, mPayment);
        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }


    private void showTBPayNumber(Meal meal) {
        Logger.d(getClass().getSimpleName(), "showTBPayNumber  meal:" + meal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmTBPayNumber qrCode = new FmTBPayNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmTBPayNumber.MEAL_TAG, meal);

        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }


    private DlgPriceChange mDlgPriceChange;
    private DlgWebView dlgWebView;

    private void showPriceChangeDialog() {
        if (isAdded() && (mDlgPriceChange == null || !mDlgPriceChange.isShowing())) {
            mDlgPriceChange = new DlgPriceChange(Main.mMainActivity);
            mDlgPriceChange.setMessage(getString(R.string.price_changed));
            mDlgPriceChange.setLinkMessage(getString(R.string.check_dsc), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dlgWebView == null || !dlgWebView.isShowing()) {
                        dlgWebView = new DlgWebView(Main.mMainActivity,
                                ServerConfigData.getInstance().getServerConfig().getStore_web() + "package/?kbox_sn=" + PrefData.getRoomCode(getContext())
                        );
                        dlgWebView.show();
                    }
                }
            });
            mDlgPriceChange.setPositiveButton(getString(R.string.go_to_buy), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDlgPriceChange.dismiss();
                    if ("coin".equals(mPayment)) {
                        showTBPayNumber(mSelectedMeal);
                    } else {
                        showQrCode(mSelectedMeal);
                    }
                }
            });
            mDlgPriceChange.setCancelButton(getString(R.string.not_buy), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mQueryOrderHelper.cancelOrder(mSelectedMeal).post();
                    mDlgPriceChange.dismiss();
                }
            });
            mDlgPriceChange.show();
        }
    }

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


//    private Handler mRequestHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 10010:
//                    if (mConfirmDlg != null)
//                        mConfirmDlg.dismiss();
//                    mAttached.dismiss();
//                    break;
//                default:
//                    break;
//            }
//            return true;
//        }
//    });

}
