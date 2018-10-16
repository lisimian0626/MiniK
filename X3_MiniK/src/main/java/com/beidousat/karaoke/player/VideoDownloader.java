package com.beidousat.karaoke.player;

import android.text.TextUtils;

import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
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
        if (DiskFileUtil.hasDiskStorage()) {
            mUrl.add(ServerFileUtil.getFileUrl(url));
            Logger.d(TAG, "addDownloadUrl ==" + url);
            startDownload();
        }
    }

    private void startDownload() {
        if (DiskFileUtil.hasDiskStorage() && !mIsDownloading && mUrl.size() > 0) {
            String url = mUrl.remove(0);
            String savePath;
            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(savePath = DiskFileUtil.getFileSavedPath(url))) {
                mIsDownloading = true;
                Logger.d(TAG, "startDownload url ==" + url + " savePath==" + savePath);
                mSimpleDownloader.download(new File(savePath), url, this);
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
