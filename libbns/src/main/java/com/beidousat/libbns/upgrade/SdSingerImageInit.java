package com.beidousat.libbns.upgrade;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.BnsZipUtil;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

import java.io.File;

/**
 * Created by J Wong on 2016/7/27.
 */
public class SdSingerImageInit implements SimpleDownloadListener {

    private File mZipFile = new File(KaraokeSdHelper.getSdCard(), "SingerImg150.zip");

    public SdSingerImageInit() {
    }

    private long mStartTime;

    public void init() {
        mStartTime = System.currentTimeMillis();
        File dir = KaraokeSdHelper.getSingerImgDir();
        if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length <= 0) {
            downloadZip();
        } else {
            Logger.d("SdSingerImageInit", "Singer img exists, not need to download :");
        }
    }

    private void downloadZip() {
        SimpleDownloader simpleDownloader = new SimpleDownloader();
        simpleDownloader.download(mZipFile, ServerFileUtil.convertHttps2Http(ServerConfigData.getInstance().getServerConfig().getVod_server() + "data/Img/SingerImg150.zip"), this);
        Log.d("test","downloadZip:"+ServerFileUtil.convertHttps2Http(ServerConfigData.getInstance().getServerConfig().getVod_server() + "data/Img/SingerImg150.zip"));
        if (mSingerImgInitListener != null) {
            mSingerImgInitListener.downloadStart();
        }
    }

    private void unZipSingerImage(final File file) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (file != null && file.exists() && file.length() > 1024 * 1024) {
                        if (mSingerImgInitListener != null)
                            mSingerImgInitListener.upzipStart();
                        long curTime = System.currentTimeMillis();
                        boolean success = BnsZipUtil.unzip(file.getAbsolutePath(), KaraokeSdHelper.getSingerImgDir(), handlerUpzip);
                        Logger.d("SdSingerImageInit", "unZipSingerImage zip is success:" + success + "  time:" + (System.currentTimeMillis() - curTime));
                        if (mSingerImgInitListener != null) {
                            if (success)
                                mSingerImgInitListener.upzipCompletion();
                            else {
                                mSingerImgInitListener.upzipFail("解压失败！");
                            }
                        }
                        if (success) {
                            file.delete();
                        }
                    } else {
                        Logger.d("SdSingerImageInit", "unZipSingerImage zip is not exist");
                        if (mSingerImgInitListener != null)
                            mSingerImgInitListener.upzipFail("文件不存在！");
                    }
                } catch (Exception e) {
                    Logger.w("SdSingerImageInit", "unZipSingerImage ex:" + e.toString());
                    if (mSingerImgInitListener != null)
                        mSingerImgInitListener.upzipFail(e.toString());
                }
                Logger.d("SdSingerImageInit", "total use time:" + (System.currentTimeMillis() - mStartTime));
                super.run();
            }
        };
        thread.start();
    }


    @Override
    public void onDownloadFail(String url) {
        try {
            mZipFile.delete();
        } catch (Exception e) {
            Logger.w("SdSingerImageInit", "onDownloadFail ex:" + e.toString());
        }

        if (mSingerImgInitListener != null) {
            mSingerImgInitListener.downloadFail();
        }
    }

    @Override
    public void onDownloadCompletion(File file, String url, long fileSize) {
        unZipSingerImage(file);
        if (mSingerImgInitListener != null) {
            mSingerImgInitListener.downloadCompletion();
        }
    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {
        if (mSingerImgInitListener != null) {
            mSingerImgInitListener.downloadProgress(progress, total);
        }
    }

    private SingerImgInitListener mSingerImgInitListener;

    public void setSingerImgInitListener(SingerImgInitListener listener) {
        this.mSingerImgInitListener = listener;
    }

    public interface SingerImgInitListener {
        void downloadStart();

        void downloadProgress(long progress, long total);

        void downloadCompletion();

        void downloadFail();

        void upzipStart();

        void upzipProgress(long progress, long total);

        void upzipCompletion();

        void upzipFail(String msg);
    }

    private Handler handlerUpzip = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                long total = bundle.getLong("total");
                long progress = bundle.getLong("progress");
                if (mSingerImgInitListener != null)
                    mSingerImgInitListener.upzipProgress(progress, total);
            }
            super.handleMessage(msg);
        }
    };
}
