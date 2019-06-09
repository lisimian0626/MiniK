package com.beidousat.libbns.net.request;

import android.os.Handler;
import android.os.Message;

import com.beidousat.libbns.model.StoreBaseModel;
import com.beidousat.libbns.util.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by J Wong on 2017/8/24.
 */

public class StoreHttpRequest extends BaseHttpRequest {

    private Class<?> mConvert2Object;
    private TypeToken<?> mConvert2Token;
    private final static String TAG = "StoreHttpRequest";
    private StoreHttpRequestListener mStoreHttpRequestListener;

    private String mMethod;
    private String mUrlHost;

    public StoreHttpRequest(String urlHost, String method) {
        mMethod = method;
        mUrlHost = urlHost;
    }

    public void post() {
        postForm(mUrlHost + mMethod);
        if (mStoreHttpRequestListener != null) {
            mStoreHttpRequestListener.onStoreStart(mMethod);
        }
    }
    public void get(){
         httpGet(mUrlHost + mMethod);
        if (mStoreHttpRequestListener != null) {
            mStoreHttpRequestListener.onStoreStart(mMethod);
        }
    }
    public StoreHttpRequest setUrlHost(String urlHost) {
        mUrlHost = urlHost;
        return this;
    }

    public StoreHttpRequest setMethod(String method) {
        mMethod = method;
        return this;
    }

    public StoreHttpRequest setStoreHttpRequestListener(StoreHttpRequestListener listener) {
        mStoreHttpRequestListener = listener;
        return this;
    }

    public StoreHttpRequest setConvert2Class(Class<?> clazz) {
        this.mConvert2Object = clazz;
        return this;
    }

    public StoreHttpRequest setConvert2Token(TypeToken<?> token) {
        this.mConvert2Token = token;
        return this;
    }

    @Override
    public void onRequestCompletion(String url, String body) {
        doResolve(body);
//        super.onRequestCompletion(url, body);
    }

    @Override
    public void onRequestFail(String url, String err) {
        sendFailMessage(err);
//        super.onRequestFail(url, err);
    }


    private void doResolve(String response) {
        if (response != null) {
            Logger.d(TAG, "doResolve response:" + response);
            StoreBaseModel baseModel = convert2BaseModel(response);
            if (baseModel != null) {
                if ("0".equals(baseModel.error)) {
                    Logger.d(TAG, "baseModel.error == 0");
                    Object result = baseModel.data;
                    if (mConvert2Object != null) {
                        result = convert2Object(baseModel.data);
                    } else if (mConvert2Token != null) {
                        result = convert2Token(baseModel.data);
                    }
                    sendSuccessMessage(result);
                } else {
                    sendFailMessage(baseModel.message);
                }
            } else {
                Logger.d(TAG, "baseModel == null");
                sendFailMessage("数据错误");
            }
        } else {
            sendFailMessage("返回空数据");
        }
    }


    private StoreBaseModel convert2BaseModel(String response) {
        StoreBaseModel baseModel = null;
        try {
            Gson gson = new Gson();
            baseModel = gson.fromJson(response, StoreBaseModel.class);
        } catch (Exception e) {
            Logger.e(TAG, "convert2BaseModel ex:" + e.toString());
        }
        return baseModel;
    }

    private Object convert2Token(Object object) {
        Object obj = null;
        try {
            String json = object.toString();
            Gson gson = new Gson();
            obj = gson.fromJson(json, mConvert2Token.getType());
        } catch (Exception e) {
            Logger.w(TAG, "convert2Token Exception :" + e.toString());
        }
        return obj;
    }

    private Object convert2Object(Object object) {
        Object obj = null;
        if (object != null) {
            try {
                Gson gson = new Gson();
                obj = gson.fromJson(object.toString(), mConvert2Object);
            } catch (Exception e) {
                Logger.w(TAG, "convert2Object Exception :" + e.toString());
            }
        }
        return obj;
    }

    private void sendFailMessage(String msg) {
        Message message = new Message();
        message.what = 1;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private void sendSuccessMessage(Object obj) {
        Message message = new Message();
        message.what = 2;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mStoreHttpRequestListener != null) {
                        mStoreHttpRequestListener.onStoreFailed(mMethod, msg.obj == null ? "" : msg.obj.toString());
                    }
                    break;
                case 2:
                    if (mStoreHttpRequestListener != null) {
                        mStoreHttpRequestListener.onStoreSuccess(mMethod, msg.obj);
                    }
                    break;
            }
        }
    };
}
