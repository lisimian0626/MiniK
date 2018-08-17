package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.karaoke.model.PaySevice;
import com.beidousat.karaoke.util.GlideUtils;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/20.
 */

public class FmPayment extends FmBaseDialog {
    private TextView textView;
    private ImageView iv_qrcode,iv_logo;
    public final static String PAYMENT_TIPS="payment_tips";
    public final static String PAYMENT_TYPE="payment_type";
    private String tips;
    private String tpye;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_payment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        tips=getArguments().getString(PAYMENT_TIPS);
        tpye=getArguments().getString(PAYMENT_TYPE);
    }

    @Override
    void initView() {
        textView=(TextView)mRootView.findViewById(R.id.fm_payment_tv_tips);
        iv_qrcode=(ImageView)mRootView.findViewById(R.id.fm_payment_iv_qrcode);
        iv_logo=(ImageView)mRootView.findViewById(R.id.fm_payment_iv_logo);
        if(!TextUtils.isEmpty(tips)){
            textView.setText(tips);
        }
        if(!TextUtils.isEmpty(tpye)){
            if(tpye.equals("wechat")){
                iv_logo.setImageResource(R.drawable.wechat);
            }else if(tpye.equals("alipay")){
                iv_logo.setImageResource(R.drawable.alipay);
            }
        }
        initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_ACCOUNT).post();
    }

    @Override
    void setListener() {

    }

    @Override
    public StoreHttpRequest initStoreRequest(String urlHost, String method) {
        StoreHttpRequest request = new StoreHttpRequest(urlHost, method);
        if(!TextUtils.isEmpty(tpye)){
            if(tpye.equals("wechat")){
                request.addParam("payment", "wechat");
            }else if(tpye.equals("alipay")){
                request.addParam("payment", "alipay");
            }
        }
        request.addParam("kbox_sn", PrefData.getRoomCode(getActivity()));
        request.setConvert2Class(PaySevice.class);
        request.setStoreHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        Logger.d("Fm",object.toString());
        PaySevice paySevice= (PaySevice) object;
        if(!TextUtils.isEmpty(paySevice.getQrcode_str())){
            iv_qrcode.setImageBitmap(QrCodeUtil.createQRCode(paySevice.getQrcode_str()));
            iv_logo.setVisibility(View.VISIBLE);
        }else{
            iv_logo.setVisibility(View.GONE);
        }
        super.onStoreSuccess(url, object);
    }

    @Override
    public void onStoreFailed(String url, String error) {
        Logger.d("Fm",error.toString());
        iv_logo.setVisibility(View.GONE);
        super.onStoreFailed(url, error);
    }

    @Override
    public void onStoreStart(String method) {
        super.onStoreStart(method);
    }
}
