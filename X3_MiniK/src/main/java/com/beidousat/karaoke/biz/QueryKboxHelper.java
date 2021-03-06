package com.beidousat.karaoke.biz;

import android.content.Context;
import android.util.Log;

import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.BasePlay;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.ServerConfig;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.FileUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
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
            mQueryKboxHelper = new QueryKboxHelper(context, card_code, cb);
        }
        return mQueryKboxHelper;
    }

    public QueryKboxHelper(Context context, String card_code, QueryKboxFeedback cb) {
        this.mContext = context;
        this.mQueryKboxFeedback = cb;
        this.mCard_code = card_code;
    }

    /**
     * 读取包厢信息
     */
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

    /**
     * 读取支付方式信息
     */
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

    /**
     * 读取设备配置信息
     */
    public void getConfig(String device_sn) {
        Log.d(TAG, "读取配置信息");
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.GET_SERVER_CFG);
        storeHttpRequest.addParam(HttpParamsUtils.initConfigParams(device_sn));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.setConvert2Class(KboxConfig.class);
        storeHttpRequest.post();
    }

    /**
     * 读取广告信息（用不上）
     *
     * */
    public void getBanner(String position, String sn) {
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.GET_BANNER);
        storeHttpRequest.addParam(HttpParamsUtils.initGetBannerParams(position, sn));
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


    /**
     * 查询成功，响应结果处理
     */
    @Override
    public void onStoreSuccess(String method, Object object) {
        Logger.d(TAG, "onSuccess :" + object);
        if (object != null && object instanceof KBox) {
            //收到包房配置信息
            final KBox kBox = (KBox) object;
            //把不用计费的标记为单机版本
            if (kBox.getAutocephalous() != null && kBox.getAutocephalous().equals("1")) {
                PrefData.setIsSingle(mContext, true);
            } else {
                PrefData.setIsSingle(mContext, false);
            }
            //设置公播信息
            BasePlay.setBasePlay(mContext, kBox.basePlaytoJsonStr(kBox.getBasePlayList()));
            //设置公播的方式
            BasePlay.setPlayPlan(mContext, kBox.getBaseplay_type());
            //设置单曲播放的序号
            BasePlay.setSingle_index(mContext, kBox.getSingle_index());
            //保存包房信息到变量
            KBoxInfo.getInstance().setKBox(kBox);
        } else if (object != null && object instanceof KboxConfig) {
            //收到配置基本配置信息
            KboxConfig kboxConfig = (KboxConfig) object;
            ServerConfig config = new ServerConfig();
            PrefData.setNodisk(mContext, kboxConfig.noDisk);
            EventBusUtil.postSticky(EventBusId.id.NODISK, kboxConfig.noDisk);
            config.setAd_web(kboxConfig.getAd_web());
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
            config.setDownload_server(kboxConfig.downloadServer);
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
