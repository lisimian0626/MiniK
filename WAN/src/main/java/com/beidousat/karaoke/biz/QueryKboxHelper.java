package com.beidousat.karaoke.biz;

import android.content.Context;
import android.os.Handler;

import com.beidousat.karaoke.data.Common;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.libbns.model.ServerConfig;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class QueryKboxHelper implements StoreHttpRequestListener {
    private static final String TAG = "QueryKboxHelper";
    private Context mContext;
    private QueryKboxFeedback mQueryKboxFeedback;
    private String mCard_code;
    private static QueryKboxHelper mQueryKboxHelper;

    public static QueryKboxHelper getInstance(Context context, String card_code, QueryKboxFeedback cb) {
        if (mQueryKboxHelper == null) {
            mQueryKboxHelper = new QueryKboxHelper(context,card_code,cb);
        }
        return mQueryKboxHelper;
    }

    public QueryKboxHelper(Context context,String card_code, QueryKboxFeedback cb) {
        this.mContext = context;
        this.mQueryKboxFeedback = cb;
        this.mCard_code=card_code;
    }

    public void getBoxInfo(String kbox_sn) {
//        SSLHttpRequest request = new SSLHttpRequest(mContext, RequestMethod.GET_KBOX);
//        request.addParam(HttpParamsUtils.initKBoxParams(PrefData.getRoomCode(mContext)));
//        request.setHttpRequestListener(this);
//        request.setConvert2Class(KBox.class);
//        request.doPost(0);
        if(ServerConfigData.getInstance().getServerConfig()==null){
            return;
        }
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.STORE_KBOX);
        storeHttpRequest.addParam(HttpParamsUtils.initKBoxParams(kbox_sn,mCard_code));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KBox.class);
        storeHttpRequest.post();


    }
   public void getPayment(){
       StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_PAYMENT);
       storeHttpRequest.setStoreHttpRequestListener(this);
       storeHttpRequest.setConvert2Token(new TypeToken<List<PayMent>>(){});
       storeHttpRequest.post();
   }
    public void getConfig(){
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.GET_SERVER_CFG);
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KboxConfig.class);
        storeHttpRequest.post();
    }
    @Override
    public void onStoreStart(String method) {
        if (mQueryKboxFeedback != null) {
            mQueryKboxFeedback.onStart();
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        if (mQueryKboxFeedback != null) {
            mQueryKboxFeedback.onFeedback(false, error,null);
        }
//        if(!error.equals("K-box信息不存在！")){
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getBoxInfo();
//                }
//            }, 3000);
//        }

    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        Logger.d("QueryKboxHelper", "onSuccess :" + object);
        if (object != null && object instanceof KBox) {
            KBox kBox = (KBox) object;
            if(kBox.getAutocephalous()!=null&&kBox.getAutocephalous().toString().equals("1")){
                Common.isSingle=true;
            }else {
                Common.isSingle=false;
            }
//            PrefData.setAutocephalous(mContext,kBox.getAutocephalous());
            KBoxInfo.getInstance().setKBox(kBox);

        }else if(object != null && object instanceof KboxConfig) {
            KboxConfig kboxConfig=(KboxConfig) object;
            ServerConfig config = new ServerConfig();
            config.setAd_web(kboxConfig.getAd_web());
            config.setKbox_ip(kboxConfig.getKbox_ip());
            String kbox_url = kboxConfig.getStore_ip_port();
            String[] kbox_ipandport = kbox_url.split(":");
            if (kbox_ipandport != null) {
                config.setStore_ip(kbox_ipandport[0]);
                config.setStore_port(Integer.parseInt(kbox_ipandport[1]));
            } else {
                config.setStore_ip(kbox_url);
                config.setStore_port(1260);
            }
            config.setStore_web(KBoxInfo.STORE_WEB);
//            config.setVod_server(kboxConfig.getVod_server());
            ServerConfigData.getInstance().setConfigData(config);
            Logger.d(TAG,"kbox_ipandport:"+kboxConfig.getStore_ip_port()+"~~~~~~"+"kbox_url:"+kboxConfig.getStore_ip_port()+"~~~~~~~~~~~~"+"kbox_ip:"+kboxConfig.getKbox_ip());
        }else if(object != null && object instanceof ArrayList){
            List<PayMent> payMentList= (List<PayMent>) object;
            KBoxInfo.getInstance().setmPayMentlist(payMentList);
            Logger.d(TAG, "url :" + payMentList.get(0).getLogo_url());
        }
        if (mQueryKboxFeedback != null) {
            mQueryKboxFeedback.onFeedback(true, null,object);
        }
    }

//    @Override
//    public void onStart(String method) {
//        if (mQueryKboxFeedback != null) {
//            mQueryKboxFeedback.onStart();
//        }
//    }

    private Handler handler = new Handler();

//    public void loadKBoxInfo(Context context, final QueryKboxFeedback cb) {
//        SSLHttpRequest request = new SSLHttpRequest(context, RequestMethod.GET_KBOX);
//        //TODO 获取boxsn
////        request.addParam(HttpParamsUtils.initKBoxParams(KBoxInfo.getInstance().getKBox().getKBoxSn()));
//        request.addParam(HttpParamsUtils.initKBoxParams(PrefData.getRoomCode(context)));
//        request.setHttpRequestListener(new HttpRequestListener() {
//            @Override
//            public void onStart(String method) {
//                if (cb != null) {
//                    cb.onStart();
//                }
//            }
//
//            @Override
//            public void onSuccess(String method, Object object) {
//                if (object != null && object instanceof KBox) {
//                    KBoxInfo.getInstance().setKBox((KBox) object);
//                }
//                if (cb != null) {
//                    cb.onFeedback(true, null);
//                }
//            }
//
//            @Override
//            public void onFailed(String method, String error) {
//                if (cb != null) {
//                    cb.onFeedback(false, error);
//                }
//            }
//        });
//        request.setConvert2Class(KBox.class);
//        request.doPost(0);
//    }

    public interface QueryKboxFeedback {
        void onStart();

        void onFeedback(boolean suceed, String msg, Object obj);
    }
}
