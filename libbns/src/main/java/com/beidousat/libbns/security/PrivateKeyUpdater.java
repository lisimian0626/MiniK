package com.beidousat.libbns.security;


import android.util.Log;

import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;

import java.io.File;

/**
 * Created by J Wong on 2015/12/15 10:30.
 */
public class PrivateKeyUpdater implements SimpleDownloadListener {

    private int mDownloadRetryTimes;

    private final static String KEY_URL = "data/version_update/key/SongSecurity.key";

    public PrivateKeyUpdater() {
    }

    public void updateKey() {
        File keyFile = BnsConfig.is901() ? KaraokeSdHelper.getSongSecurityKeyFileFor901() : KaraokeSdHelper.getSongSecurityKeyFile();
        if (keyFile != null && keyFile.exists() && keyFile.length() == 32) {//不需要更新密钥
            Log.d("PrivateKeyUpdater", "updateKey keyFile key exist , not need to update !!");
            return;
        }
        SimpleDownloader simpleDownloader = new SimpleDownloader();
//        File keyFile = KaraokeSdHelper.getSongSecurityKeyFile();
        simpleDownloader.download(keyFile, KEY_URL, this);

//        File keyFile = is901? KaraokeSdHelper.getSongSecurityKeyFileFor901() : KaraokeSdHelper.getSongSecurityKeyFile();
//        if (keyFile != null && keyFile.exists() && keyFile.length() == 32) {//不需要更新密钥
//            Log.d("PrivateKeyUpdater", "updateKey keyFile key exist , not need to update !!");
//            return;
//        }
//        Log.d("PrivateKeyUpdater", "updateKey keyFile !!");
//
//        updateKey(keyFile);
    }


    @Override
    public void onDownloadCompletion(File file, String url, long fileSize) {
        Logger.d("PrivateKeyUpdater", "Update private key success !");
    }

    @Override
    public void onDownloadFail(String url) {
        Logger.d("PrivateKeyUpdater", "Update private key fail time:" + mDownloadRetryTimes);
        if (mDownloadRetryTimes < 5) {//retry
            updateKey();
            mDownloadRetryTimes++;
        }
    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {
    }
}
