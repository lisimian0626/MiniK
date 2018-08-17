package com.beidousat.karaoke.biz;

import android.content.Context;
import android.os.Handler;

import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.ServerConfigData;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.SSLHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class QueryKboxHelper implements StoreHttpRequestListener {

    private Context mContext;
    private QueryKboxFeedback mQueryKboxFeedback;
//    private static QueryKboxHelper mQueryKboxHelper;

//    public static QueryKboxHelper getInstance(Context context, QueryKboxFeedback cb) {
//        if (mQueryKboxHelper == null) {
//            mQueryKboxHelper = new QueryKboxHelper(context, cb);
//        }
//        return mQueryKboxHelper;
//    }

    public QueryKboxHelper(Context context, QueryKboxFeedback cb) {
        this.mContext = context;
        this.mQueryKboxFeedback = cb;
    }

    public void getBoxInfo() {
//        SSLHttpRequest request = new SSLHttpRequest(mContext, RequestMethod.GET_KBOX);
//        request.addParam(HttpParamsUtils.initKBoxParams(PrefData.getRoomCode(mContext)));
//        request.setHttpRequestListener(this);
//        request.setConvert2Class(KBox.class);
//        request.doPost(0);

        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.STORE_KBOX);
        storeHttpRequest.addParam(HttpParamsUtils.initKBoxParams((PrefData.getRoomCode(mContext)),null));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KBox.class);
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
            mQueryKboxFeedback.onFeedback(false, error);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBoxInfo();
            }
        }, 3000);
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        if (object != null && object instanceof KBox) {
            KBox kBox = (KBox) object;
            Logger.d("QueryKboxHelper", "onSuccess :" + kBox.getAddress());
            KBoxInfo.getInstance().setKBox(kBox);
        }
        if (mQueryKboxFeedback != null) {
            mQueryKboxFeedback.onFeedback(true, null);
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

        void onFeedback(boolean suceed, String msg);
    }
}
