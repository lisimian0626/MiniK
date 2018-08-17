package com.beidousat.libbns.net.request;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.libbns.R;
import com.beidousat.libbns.model.BaseModel;
import com.beidousat.libbns.model.ServerConfig;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.OkHttpUtil;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.LogRecorder;
import com.beidousat.libbns.util.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by J Wong on 2015/10/9 17:58.
 * 网络请求
 */
public class HttpRequest {

    private final static int TIME_OUT = 5000;
    private final static int REQUEST_SUCCESS_CODE = 1001;
    private final static String TAG = HttpRequest.class.getSimpleName();
    public final static int TIMEOUT_STATUS = 666;

    //    private RequestQueue mQueue;
    private String mMethod;
    private HttpRequestListener mHttpRequestListener;
    private Map<String, String> mParams;
    //    private StringRequest mJsonObjectRequest;
    private Class<?> mConvert2Object;
    private TypeToken<?> mConvert2Token;
    private Context mContext;
    private String mDomainUrl;

    public HttpRequest(Context context, String method) {
        this.mContext = context.getApplicationContext();
        this.mMethod = method;
    }

    public HttpRequest setHttpRequestListener(HttpRequestListener listener) {
        this.mHttpRequestListener = listener;
        return this;
    }

    public HttpRequest addParam(String key, String value) {
        if (mParams == null)
            mParams = new HashMap<String, String>();
        mParams.put(key, value);
        return this;
    }

    public HttpRequest addParam(Map<String, String> params) {
        if (mParams == null)
            mParams = new HashMap<String, String>();

        mParams.putAll(params);
        return this;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public HttpRequest setConvert2Class(Class<?> clazz) {
        this.mConvert2Object = clazz;
        return this;
    }

    public HttpRequest setConvert2Token(TypeToken<?> token) {
        this.mConvert2Token = token;
        return this;
    }

    public void setDomainUrl(String domainUrl) {
        mDomainUrl = domainUrl;
    }

    public void doPost(int index) {
        if (NetWorkUtils.isNetworkAvailable(mContext)) {
            doPosOk(index);
        } else {
            String url = getUrl(index, mMethod);
            LogRecorder.addString2File("/sdcard/net_conn.txt", "网络不可用：" + url);
            if (mHttpRequestListener != null) {
                mHttpRequestListener.onFailed(mMethod, mContext.getString(R.string.network_failure));
            }
        }
    }

    private String getUrl(int index, String urlMethod) {
        StringBuilder builder = new StringBuilder();

        setRequestModel(builder);
        builder.append("&").append("a=").append(urlMethod);
        if (index > 0)
            builder.append("&").append("page=").append(index);
        if (mParams != null && mParams.size() > 0) {
            Iterator iterator = mParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                String val = entry.getValue() == null ? "" : entry.getValue().toString();
                builder.append("&").append(key).append("=").append(val);
            }
        }
        String url = ServerConfigData.getInstance().getServerConfig().getVod_url() + builder.toString();
//        Log.e("test","mDomainUrl:"+mDomainUrl);
//        Log.e("test","BnsConfig:"+BnsConfig.DOMAIN_IP);
        return url;
    }

    public void setRequestModel(StringBuilder builder) {
        builder.append("m=index");
    }

    private void doResolve(String response) {
        if (response != null) {
            Logger.i(TAG, mMethod + " : " + response);
            BaseModel baseModel = convert2BaseModel(response);
            if (baseModel != null) {
                if (baseModel.status == REQUEST_SUCCESS_CODE) {
                    Object result = baseModel.data;
                    if (mConvert2Object != null) {
                        result = convert2Object(baseModel.data);
                    } else if (mConvert2Token != null) {
                        result = convert2Token(baseModel.data);
                    }
                    sendSuccessMessage(result);
                } else {
                    sendFailMessage(baseModel.info);
                }
            } else {
                sendFailMessage("数据错误");
            }
        } else {
            sendFailMessage("返回空数据");
        }
    }

    private BaseModel convert2BaseModel(String response) {
        BaseModel baseModel = null;
        try {
            Gson gson = new Gson();
            baseModel = gson.fromJson(response, BaseModel.class);
        } catch (Exception e) {
            Logger.e(TAG, "convert2BaseModel ex:" + e.toString());
        }
        return baseModel;
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mHttpRequestListener != null) {
                        mHttpRequestListener.onFailed(mMethod, msg.obj == null ? "" : msg.obj.toString());
                    }
                    break;
                case 2:
                    if (mHttpRequestListener != null)
                        mHttpRequestListener.onSuccess(mMethod, msg.obj);
                    break;
            }
        }
    };


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

    private void doPosOk(int index) {
        try {
            String url = getUrl(index, mMethod);
            Logger.i(TAG, "url:" + url);
            Request request = new Request.Builder().url(url).build();

            if (mHttpRequestListener != null) {
                mHttpRequestListener.onStart(mMethod);
            }
            OkHttpUtil.enqueue(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    sendFailMessage(mContext.getString(R.string.request_fail, parseErrorMsg(e)));
                    Logger.e(TAG, "onFailure:" + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody body = response.body();
                        if (response.isSuccessful()) {
                            String responseUrl = body.string();
                            doResolve(responseUrl);
                        } else {
                            sendFailMessage(mContext.getString(R.string.request_fail, response.message()));
//                        throw new IOException("Unexpected code " + response);
                        }
                        body.close();
                    } catch (Exception e) {
                        Logger.e(TAG, "onResponse Exception :" + e.toString());
                        sendFailMessage(mContext.getString(R.string.network_failure));
                    }
                }
            });
        } catch (Exception e) {
            Logger.e(TAG, "doPosOk Exception :" + e.toString());
        }
    }

    private String parseErrorMsg(Exception e) {
        String error = mContext.getResources().getString(R.string.network_failure);
        if (e instanceof SocketTimeoutException) {
            error = mContext.getResources().getString(R.string.network_timeout);
        }
        return error;
    }
}
