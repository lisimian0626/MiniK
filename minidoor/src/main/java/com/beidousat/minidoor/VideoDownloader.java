package com.beidousat.minidoor;

import android.text.TextUtils;

import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.util.KaraokeSdHelper;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2017/5/10.
 */

public class VideoDownloader implements SimpleDownloadListener {

    private static final String TAG = "VideoDownloader";
    private static VideoDownloader mDownloader;
    private List<String> mUrl = new ArrayList<>();
    private SimpleDownloader mSimpleDownloader;
    private boolean mIsDownloading;

    public static VideoDownloader getInstance() {
        if (mDownloader == null) {
            mDownloader = new VideoDownloader();
        }
        return mDownloader;
    }

    private VideoDownloader() {
        mSimpleDownloader = new SimpleDownloader();
    }

    public void addDownloadUrl(String url) {
        if (TextUtils.isEmpty(url) || !KaraokeSdHelper.existSDCard()) {
            return;
        }
        String fileName = ServerFileUtil.getFileName(url);
        File file = new File(KaraokeSdHelper.getAdDoorMiniDir(), fileName);
        if (file.exists() && file.length() > 0) {
            return;
        }

        mUrl.add(ServerFileUtil.getFileUrl(url));
        Logger.d(TAG, "addDownloadUrl ==" + url);
        startDownload();

    }

    private void startDownload() {
        if (KaraokeSdHelper.existSDCard() && !mIsDownloading && mUrl.size() > 0) {
            String url = mUrl.remove(0);
            if (!TextUtils.isEmpty(url)) {
                String fileName = ServerFileUtil.getFileName(url);
                File file = new File(KaraokeSdHelper.getAdDoorMiniDir(), fileName);
                mIsDownloading = true;
                Logger.d(TAG, "startDownload url ==" + url + " savePath==" + file);
                mSimpleDownloader.download(file, url, this);
            }
        }
    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {
        Logger.d(TAG, "onUpdateProgress desFile ==" + mDesFile.getAbsolutePath() + " progress==" + progress + " total: " + total);

    }

    @Override
    public void onDownloadFail(String url) {
        Logger.d(TAG, "onDownloadFail url ==" + url);
        mIsDownloading = false;
    }

    @Override
    public void onDownloadCompletion(File file, String url, long size) {
        Logger.d(TAG, "onDownloadCompletion file ==" + file.getAbsolutePath());
        mIsDownloading = false;
        startDownload();
    }
}
