package com.beidousat.karaoke.biz;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.BasePlay;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.BasePlayFitter;
import com.beidousat.karaoke.util.DownloadQueueHelper;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.ServerConfig;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.FileUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
            mQueryKboxHelper = new QueryKboxHelper(context, card_code, cb);
        }
        return mQueryKboxHelper;
    }

    public QueryKboxHelper(Context context, String card_code, QueryKboxFeedback cb) {
        this.mContext = context;
        this.mQueryKboxFeedback = cb;
        this.mCard_code = card_code;
    }

    public void getBoxInfo(String kbox_sn, String chip) {
        if (ServerConfigData.getInstance().getServerConfig() == null) {
            return;
        }
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.STORE_KBOX);
        storeHttpRequest.addParam(HttpParamsUtils.initKBoxParams(kbox_sn, chip, mCard_code));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KBox.class);
        storeHttpRequest.post();


    }

    public void getPayment() {
        if (ServerConfigData.getInstance().getServerConfig() == null) {
            return;
        }
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_PAYMENT);
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Token(new TypeToken<List<PayMent>>() {
        });
        storeHttpRequest.post();
    }

    public void getConfig(String device_sn) {
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.GET_SERVER_CFG);
        storeHttpRequest.addParam(HttpParamsUtils.initConfigParams(device_sn));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KboxConfig.class);
        storeHttpRequest.post();
    }

    public void getBanner(String position,String sn) {
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.GET_BANNER);
        storeHttpRequest.addParam(HttpParamsUtils.initGetBannerParams(position,sn));
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
            mQueryKboxFeedback.onFeedback(false, error, null);
        }
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        Logger.d("QueryKboxHelper", "onSuccess :" + object);
        if (object != null && object instanceof KBox) {
            final KBox kBox = (KBox) object;
            if (kBox.getAutocephalous() != null && kBox.getAutocephalous().equals("1")) {
                PreferenceUtil.setBoolean(mContext, "isSingle", true);
            } else {
                PreferenceUtil.setBoolean(mContext, "isSingle", false);
            }
            Logger.d(TAG, "def_play:" + kBox.basePlaytoJsonStr(kBox.getBasePlayList()));
            PreferenceUtil.setString(mContext, "def_play", kBox.basePlaytoJsonStr(kBox.getBasePlayList()));
            KBoxInfo.getInstance().setKBox(kBox);
        } else if (object != null && object instanceof KboxConfig) {
            KboxConfig kboxConfig = (KboxConfig) object;
            ServerConfig config = new ServerConfig();
            PrefData.setNodisk(mContext,kboxConfig.noDisk);
            EventBusUtil.postSticky(EventBusId.id.NODISK,kboxConfig.noDisk);
            config.setAd_web(kboxConfig.getAd_web());
//            config.setKbox_ip(kboxConfig.getKbox_ip());
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
            config.setVod_server(kboxConfig.getVod_server());
            ServerConfigData.getInstance().setConfigData(config);
            KBoxInfo.getInstance().setKboxConfig(kboxConfig);

//            Logger.d(TAG,"kbox_ipandport:"+kboxConfig.getStore_ip_port()+"~~~~~~"+"kbox_url:"+kboxConfig.getStore_ip_port()+"~~~~~~~~~~~~"+"kbox_ip:"+kboxConfig.getKbox_ip());
        } else if (object != null && object instanceof ArrayList) {
            List<PayMent> payMentList = (List<PayMent>) object;
            KBoxInfo.getInstance().setmPayMentlist(payMentList);
            Logger.d(TAG, "url :" + payMentList.get(0).getLogo_url());
        }
        if (mQueryKboxFeedback != null) {
            mQueryKboxFeedback.onFeedback(true, null, object);
        }
    }

    public interface QueryKboxFeedback {
        void onStart();

        void onFeedback(boolean suceed, String msg, Object obj);
    }

    private List<BasePlay> getCurBasePlay(ArrayList<String> filelist) {
        List<BasePlay> basePlayList = new ArrayList<>();
        for (String path : filelist) {
            BasePlay basePlay = new BasePlay();
            basePlay.setSave_path(FileUtil.getDeleteSongSavePath(path));
            basePlay.setType("mp4");
            basePlayList.add(basePlay);
        }
        return basePlayList;
    }


    public ArrayList<String> refreshFileList(String strPath, ArrayList<String> filelist) {
        //遍历指定目录
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    filelist = refreshFileList(files[i].getAbsolutePath(), filelist);
                } else {
                    filelist.add(files[i].getAbsolutePath());
                    Logger.d(TAG, "filepath:" + files[i].getAbsolutePath());
                }
            }
        }
        return filelist;
    }
}
