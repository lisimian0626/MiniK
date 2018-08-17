package com.beidousat.libbns.upgrade;

import android.content.Context;


import com.beidousat.libbns.model.VersionInfo;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PackageUtil;

import java.io.File;

/**
 * Created by J Wong on 2015/12/7 17:12.
 */
public class SystemUpgrader implements HttpRequestListener, SimpleDownloadListener {

//    这个功能我要发新的升级包给你们才行
//    升级包放到/mnt/sdcard/，命名为update.zip
//    调用方法如下：
//    Intent intent = new Intent();
//    intent = new Intent("softwinner.intent.action.autoupdate");
//    startActivity(intent);


    private Context mContext;
    private VersionInfo mApkVersion;

    /***
     *
     */
    private int mType = 0;

    public SystemUpgrader(Context context) {
        mContext = context;
    }

    public void checkVersion(int type) {
        this.mType = type;
        getSystemVersionInfo();
    }

    public void downloadZip(String url) {
        SimpleDownloader simpleDownloader = new SimpleDownloader();
        simpleDownloader.download(KaraokeSdHelper.getOtaDownloadFile(), url, this);
        if (mOnSystemUpdateListener != null)
            mOnSystemUpdateListener.onSystemUpdateStart();
    }

    private void getSystemVersionInfo() {
        HttpRequest r = initRequest(RequestMethod.GET_VERSION);
        r.addParam("Type", String.valueOf(mType));
        r.setConvert2Class(VersionInfo.class);
        r.doPost(0);
    }

    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStart(String method) {
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_VERSION.equals(method)) {
            mApkVersion = (VersionInfo) object;
            if (mApkVersion != null && mApkVersion.VersionNumber > PackageUtil.getSystemVersionCode()) {
                downloadZip(mApkVersion.FliePath);
                Logger.i("SystemUpgrader", "update.zip not exists");
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {
    }

    @Override
    public void onDownloadCompletion(File file, String url, long fileSize) {
        if (file != null && file.exists()) {
            Logger.i("SystemUpgrader", "file len:" + file.length() + "  download len:" + fileSize);
            if (file.length() == fileSize) {
                File file1 = KaraokeSdHelper.getOtaUpdateFile();
                if (file.renameTo(file1)) {
                    Logger.d("SystemUpgrader", "RK 3288 update.zip file size==" + file1.length());
                    if (file1.exists()) {
                        if (mType == 23) {
                            PackageUtil.rk3288upgradeSystemFor901(mContext, file1.getAbsolutePath());
                        } else if (mType == 19) {
                            PackageUtil.rk3288upgradeSystem(mContext, file1);
                        }
                        if (mOnSystemUpdateListener != null)
                            mOnSystemUpdateListener.onSystemUpdateCompletion();
                    } else {
                        if (mOnSystemUpdateListener != null)
                            mOnSystemUpdateListener.onSystemUpdateFail("文件不存在！");
                    }
                } else {
                    if (mOnSystemUpdateListener != null)
                        mOnSystemUpdateListener.onSystemUpdateFail("文件命名失败！");
                }
            } else {
                if (mOnSystemUpdateListener != null)
                    mOnSystemUpdateListener.onSystemUpdateFail("文件被损坏！");
            }
        }
    }

    @Override
    public void onDownloadFail(String url) {
        if (mOnSystemUpdateListener != null)
            mOnSystemUpdateListener.onSystemUpdateFail("下载失败");
    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {
        if (mOnSystemUpdateListener != null)
            mOnSystemUpdateListener.onSystemUpdateProgress(progress, total);
    }

    private OnSystemUpdateListener mOnSystemUpdateListener;

    public void setOnSystemUpdateListener(OnSystemUpdateListener listener) {
        this.mOnSystemUpdateListener = listener;
    }

    public interface OnSystemUpdateListener {
        void onSystemUpdateStart();

        void onSystemUpdateProgress(long progress, long total);

        void onSystemUpdateCompletion();

        void onSystemUpdateFail(String msg);
    }
}
