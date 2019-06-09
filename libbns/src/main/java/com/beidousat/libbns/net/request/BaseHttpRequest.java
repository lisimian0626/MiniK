package com.beidousat.libbns.net.request;

import com.beidousat.libbns.R;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by J Wong on 2017/5/15.
 */

public class BaseHttpRequest implements BaseHttpRequestListener {

    private final static String TAG = BaseHttpRequest.class.getSimpleName();
    private BaseHttpRequestListener mListener;
    private Map<String, String> mParams;
    String mUrl;

    public void setBaseHttpRequestListener(BaseHttpRequestListener listener) {
        mListener = listener;
    }

    public BaseHttpRequest addParam(String key, String value) {
        if (mParams == null)
            mParams = new HashMap<String, String>();
        mParams.put(key, value);
        return this;
    }

    public BaseHttpRequest addParam(Map<String, String> params) {
        if (mParams == null)
            mParams = new HashMap<String, String>();

        mParams.putAll(params);
        return this;
    }

    public void postForm(String url) {
        mUrl = url;
        FormBody.Builder builder = new FormBody.Builder();
        if (mParams != null && mParams.size() > 0) {
            Iterator iterator = mParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                if (key == null) continue;
                String val = entry.getValue() == null ? "" : entry.getValue().toString();
                Logger.d(TAG, "postForm key:" + key + "  val:" + val);
                builder.add(key, val);
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(mUrl).post(body).build();
        post(mUrl, request);
    }

    public void httpGet(String url) {
        mUrl = getUrl(url);
        Request request = new Request.Builder().url(url).build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "onFailure:" + e.toString());
                onRequestFail(mUrl, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.isSuccessful()) {
                    String respBody = body.string();
                    Logger.d(TAG, "Url:" + mUrl + "  onResponse body :" + respBody);
                    onRequestCompletion(mUrl, respBody);
                } else {
                    onRequestFail(mUrl, response.message());
                }
                body.close();
            }
        });

    }

    public String getUrl(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        int i = 0;
        if (mParams != null && mParams.size() > 0) {
            Iterator iterator = mParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                String val = entry.getValue() == null ? "" : entry.getValue().toString();
                if (i > 0)
                    builder.append("&");
                builder.append(key).append("=").append(val);
                i++;
            }
        }
        return builder.toString();
    }

    void post(String url, Request request) {
        Logger.d(TAG, "post url :" + url);
        mUrl = url;
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "onFailure:" + e.toString());
                onRequestFail(mUrl, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.isSuccessful()) {
                    String respBody = body.string();
                    Logger.d(TAG, "Url:" + mUrl + "  onResponse body :" + respBody);
                    onRequestCompletion(mUrl, respBody);
                } else {
                    onRequestFail(mUrl, response.message());
                }
                body.close();
            }
        });
    }
    private void doGet(String url, Request request) {
        try {
            Logger.d(TAG, "get url :" + url);
            mUrl = url;
            OkHttpUtil.enqueue(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(TAG, "onFailure:" + e.toString());
                    onRequestFail(mUrl, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (response.isSuccessful()) {
                        String respBody = body.string();
                        Logger.d(TAG, "Url:" + mUrl + "  onResponse body :" + respBody);
                        onRequestCompletion(mUrl, respBody);
                    } else {
                        onRequestFail(mUrl, response.message());
                    }
                    body.close();
                }
            });
        } catch (Exception e) {
            Logger.e(TAG, "get Exception :" + e.toString());
        }
    }
    @Override
    public void onRequestCompletion(String url, String body) {
        if (mListener != null) {
            mListener.onRequestCompletion(url, body);
        }
    }

    @Override
    public void onRequestFail(String url, String err) {
        if (mListener != null) {
            mListener.onRequestFail(url, err);
        }
    }
}
