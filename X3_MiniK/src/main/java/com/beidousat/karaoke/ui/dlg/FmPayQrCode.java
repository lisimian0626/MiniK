package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.libbns.model.Common;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayResult;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.util.QrCodeUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author: Hanson
 * date:   2017/3/30
 * dscribe:
 */

public class FmPayQrCode extends FmBaseDialog implements SupportQueryOrder {
    private TextView mBtnBack;
    private TextView mTvMoney;
    private TextView mTvMeal;
    private TextView mTvPrompt;
    private ImageView mIvQrCode;
    private AlertDialog mConfirmDlg;
    private Meal mSelectedMeal;
    private QueryOrderHelper mQueryOrderHelper;

    private Timer mQueryTimer = new Timer();

    private final static int HTTP_REQUEST_MSG = 1;
    private final static int CLOSE_DIALOG = 2;

    private Handler mRequestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HTTP_REQUEST_MSG:
                    mQueryOrderHelper.queryOrder(mSelectedMeal).post();
                    break;
                case CLOSE_DIALOG:
                    if (mConfirmDlg != null)
                        mConfirmDlg.dismiss();
                    mAttached.dismiss();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private TimerTask mQueryTask = new TimerTask() {
        @Override
        public void run() {
            mRequestHandler.sendEmptyMessage(HTTP_REQUEST_MSG);
        }
    };

    public final static String MEAL_TAG = "SelectedMeal";
    public final static String TYPE_TAG = "type";
    public final static String QR_CODE="Qr_code";

    private String mType;
    private String qr_code;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_qrcode, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mSelectedMeal = (Meal) getArguments().getSerializable(MEAL_TAG);
        qr_code=getArguments().getString(QR_CODE);
        mType = getArguments().getString(TYPE_TAG);
        mQueryOrderHelper = new QueryOrderHelper(this);
    }

    @Override
    void initView() {
        mBtnBack = findViewById(R.id.btn_back);
        mTvMoney = findViewById(R.id.tv_money);
        mTvMeal = findViewById(R.id.tv_selected_meal);
        mIvQrCode = findViewById(R.id.iv_pay_qrcode);
        mTvPrompt = findViewById(R.id.pay_prompt);
        if (mType.equals("alipay")) {
            mTvPrompt.setText(getResources().getString(R.string.text_pay_prompt_zhifubao));
        } else {
            mTvPrompt.setText(getResources().getString(R.string.text_pay_prompt));
        }
        mTvMeal.setText(getResources().getString(R.string.text_selected_pay_meal,
                mSelectedMeal.getAmount(), mSelectedMeal.getUnit()));
        mTvMoney.setText(String.format("%.2f", mSelectedMeal.getPrice()));
        if(!TextUtils.isEmpty(qr_code)){
            mIvQrCode.setImageBitmap(QrCodeUtil.createQRCode(qr_code));
        }
//        Glide.with(getContext()).load(mSelectedMeal.getUrl()).centerCrop().into(mIvQrCode);

//        if (mIsPriceChange) {
//            showPriceChangeDialog();
//        }
    }

    @Override
    void setListener() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }



    private void showConfirmDialog() {
        mConfirmDlg = DialogFactory.showCancelOrderDialog(getContext(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mQueryOrderHelper.cancelOrder(mSelectedMeal).post();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(mType.equals("alipay")){
//            pay(mSelectedMeal.getOrderSn(),"alipay");
//        }else{
//            pay(mSelectedMeal.getOrderSn(),"wechat");
//        }

        mQueryTimer.schedule(mQueryTask, 100, Common.Order_query_limit); //请求超时为5s
        //注册对话框关闭事件
        mAttached.registerCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        mQueryTimer.cancel();
        mQueryTask.cancel();
        //反注册对话框关闭事件
        mAttached.unregisterCloseListener(this);
        mRequestHandler.removeMessages(HTTP_REQUEST_MSG);
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
                    mRequestHandler.sendEmptyMessage(CLOSE_DIALOG);
                }
          /*      else {
                    if (data != null && TextUtils.isEmpty(data.toString())) {
                        DialogFactory.showErrorDialog(getContext(), data.toString(),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mAttached.dismiss();
                            }
                        });
                    }
                }*/
                break;
        }
    }
    private void pay(String orderSn,String payment) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CREATE);
        request.setStoreHttpRequestListener(this);
        request.addParam("order_sn", orderSn);
        request.addParam("payment", payment);
        request.setConvert2Class(PayResult.class);
        request.post();
    }

    @Override
    public void onStoreStart(String method) {
        super.onStoreStart(method);
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        PayResult payResult= (PayResult) object;
        if(!TextUtils.isEmpty(payResult.getQrcode_data())){
            mIvQrCode.setImageBitmap(QrCodeUtil.createQRCode(payResult.getQrcode_data()));
        }
        super.onStoreSuccess(url, object);
    }

    @Override
    public void onStoreFailed(String url, String error) {
        if(getContext()!=null){
            ToastUtils.toast(getContext(),error);
        }
        super.onStoreFailed(url, error);
    }
}
