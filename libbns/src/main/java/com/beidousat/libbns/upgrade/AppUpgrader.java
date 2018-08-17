package com.beidousat.libbns.upgrade;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.beidousat.libbns.model.VersionInfo;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PackageUtil;

import java.io.File;

/**
 * Created by J Wong on 2015/12/7 17:12.
 */
public class AppUpgrader implements HttpRequestListener, SimpleDownloadListener {

    private Context mContext;
    private int mType = 0;

    public AppUpgrader(Context context) {
        mContext = context;
    }

    /**
     * @param type 18:晨芯-3288
     *             20：迷你K门口屏
     *             24：音诺恒-3288
     */
    public void checkVersion(int type) {
        mType = type;
        getApkVersionInfo(type);
    }

    public void downloadApk(String url) {
        SimpleDownloader simpleDownloader = new SimpleDownloader();
        String fileName = "RK_Mini_KTV.apk";
        if (!TextUtils.isEmpty(fileName))
            simpleDownloader.download(new File(KaraokeSdHelper.getApk(), fileName), url, this);
    }

    private void getApkVersionInfo(int type) {
        HttpRequest r = initRequest(RequestMethod.GET_VERSION);
        r.addParam("device_code", DeviceUtil.getCupChipID());
        r.addParam("Type", String.valueOf(type));
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
            VersionInfo apkVersion = (VersionInfo) object;
            if (apkVersion != null && apkVersion.VersionNumber > PackageUtil.getVersionCode(mContext)) {
                downloadApk(apkVersion.FliePath);
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {
    }

    @Override
    public void onDownloadCompletion(File file, String url, long fileSize) {
        Logger.i(getClass().getSimpleName(), "download Completion");
        if (file != null && file.exists()) {
            if (file.length() == fileSize) {
                String packageName = mContext.getPackageName();
                Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
                String className = launchIntent.getComponent().getClassName();
                if (mType == 20) { //H8
                    PackageUtil.installApkH8(mContext, file, className);
                } else if (18 == mType) {//晨芯3288
                    PackageUtil.installApk(mContext, file, className);
                } else if (24 == mType) {//音诺恒-3288
                    PackageUtil.installApkFor901(mContext, file, className);
                }
            }
        }
    }

    @Override
    public void onDownloadFail(String url) {
    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {
    }
}
