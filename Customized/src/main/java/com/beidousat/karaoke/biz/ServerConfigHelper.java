package com.beidousat.karaoke.biz;

import android.content.Context;
import android.os.Handler;

import com.beidousat.karaoke.data.ServerConfigData;
import com.beidousat.karaoke.model.ServerConfig;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.SSLHttpRequest;

/**
 * Created by J Wong on 2017/5/15.
 */

public class ServerConfigHelper implements HttpRequestListener {

    private static ServerConfigHelper mServerConfigHelper;
    private Context mContext;
    private OnServerCallback mOnServerCallback;
    private Handler handler = new Handler();

    public static ServerConfigHelper getInstance(Context context) {
        if (mServerConfigHelper == null) {
            mServerConfigHelper = new ServerConfigHelper(context);
        }
        return mServerConfigHelper;
    }

    public void setOnServerCallback(OnServerCallback callback) {
        this.mOnServerCallback = callback;
    }

    public void getConfig() {
        SSLHttpRequest request = new SSLHttpRequest(mContext, RequestMethod.GET_CONFIG);
        request.setHttpRequestListener(this);
        request.setConvert2Class(ServerConfig.class);
        request.doPost(0);
    }

    private ServerConfigHelper(Context context) {
        this.mContext = context;
    }


    @Override
    public void onStart(String method) {
    }

    @Override
    public void onSuccess(String method, Object object) {
        try {
            ServerConfig config = (ServerConfig) object;
            ServerConfigData.getInstance().setConfigData(config);
            if (mOnServerCallback != null) {
                mOnServerCallback.onSererCallback(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getConfig();
                }
            }, 3000);
        }
    }

    @Override
    public void onFailed(String method, String error) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getConfig();
            }
        }, 3000);
    }

    public interface OnServerCallback {
        void onSererCallback(ServerConfig config);
    }
}
