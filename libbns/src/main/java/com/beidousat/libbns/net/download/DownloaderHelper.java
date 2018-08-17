package com.beidousat.libbns.net.download;

import android.text.TextUtils;

import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.StorageUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * author: Hanson
 * date:   2017/4/20
 * describe:
 */

public class DownloaderHelper {

    private static final String TAG = "DownloaderHelper";
    private static final DownloaderHelper mInstance = new DownloaderHelper();

    private ConcurrentLinkedDeque<BaseDownloadTask> mTaskPool;
    private ConcurrentLinkedDeque<DownloadInfo> mDownloadQueue;

    private static final int MAX_DOWNLOAD_TASK = 1;

    private FileDownloadListener mFinishListener;


    private DownloaderHelper() {
        mTaskPool = new ConcurrentLinkedDeque<>();
        mDownloadQueue = new ConcurrentLinkedDeque<>();

        /*mFinishListener = new FileDownloadLargeFileListener() {
            @Override
            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                EventBusUtil.postDownloadStart(task.getUrl(), task.getPath());
                //检测磁盘空间是否充足
                if (!StorageUtil.isExternalStorageHasAvailableSpace(totalBytes-soFarBytes)) {
                    //TODO 通知APP磁盘空间不足
                    EventBusUtil.postDownloadSpaceNotEnough(task.getUrl(), task.getPath());
                }
            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                Logger.i(TAG, String.format("%.2f", (float)soFarBytes/totalBytes));
                if (totalBytes != 0) {
                    EventBusUtil.postDownloadProgress(task.getUrl(), task.getPath(), soFarBytes/totalBytes);
                }
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                Logger.i(TAG, "pause----"+String.format("%.2f", (float)soFarBytes/totalBytes));
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                EventBusUtil.postDownloadFinish(task.getUrl(), task.getPath());
                startNextDownload(task);
                Logger.i(TAG, task.getFilename()+"finished---taskSize="+mTaskPool.size());
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Logger.i(TAG, e.toString());
                startNextDownload(task);
                EventBusUtil.postDownloadError(task.getUrl(), task.getPath(), e.toString());
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                Logger.i(TAG, String.format("%.2f", "warn"));
                startNextDownload(task);
            }
        };*/
    }

    public static DownloaderHelper getInstance() {
        return mInstance;
    }

    public void setFileDownloaderListener(FileDownloadListener listener) {
        mFinishListener = listener;
    }

    /**
     * 开始一个下载任务；如果队列中有排队的任务，则自动下载队首的任务
     *
     * @param complete 当前完成的任务，用于删除完成任务队列
     */
    public void startNextDownload(BaseDownloadTask complete) {
        removeDownload(complete.getUrl());

        //下载完成后找出最优先加入下载池
        DownloadInfo info = mDownloadQueue.pollFirst();
        if (info != null) {
            startDownload(info.mUrl, info.mPath);
        }
    }

    public void startNextDownload(String url) {
        removeDownload(url);

        //下载完成后找出最优先加入下载池
        DownloadInfo info = mDownloadQueue.pollFirst();
        if (info != null) {
            startDownload(info.mUrl, info.mPath);
        }
    }

    /**
     * 开始下载，如果超过MAX_DOWNLOAD_TASK，则加入队列等待下载
     *
     * @param url
     * @param savedPath
     */
    public void startDownload(String url, String savedPath) throws IllegalArgumentException {
        Logger.d(TAG, "startDownload url:" + url + "  savedPath:" + savedPath);

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savedPath))
            throw new IllegalArgumentException("url or savedPath can't be null");

        BaseDownloadTask downloading = findTask(url);
        //如果任务正在下载，直接返回
        if (downloading != null) return;

        //当下载线程小于MAX_DOWNLOAD_TASK直接，开启下载线程；否者加入待下载列表
        if (mTaskPool.size() < MAX_DOWNLOAD_TASK) {
            BaseDownloadTask task = FileDownloader.getImpl().create(url)
                    .setPath(savedPath).setListener(mFinishListener);
            mTaskPool.add(task);

            task.start();
        } else {
            DownloadInfo info = new DownloadInfo(url, savedPath);
            mDownloadQueue.add(info);
        }
    }

    private BaseDownloadTask findTask(String url) {
        for (BaseDownloadTask task : mTaskPool) {
            if (task.getUrl().equals(url))
                return task;
        }

        return null;
    }

    /**
     * 优先下载
     *
     * @param url
     * @param savedPath
     */
    public void priorDownload(String url, String savedPath) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savedPath))
            throw new IllegalArgumentException("url or savedPath can't be null");

        if (mTaskPool.size() >= MAX_DOWNLOAD_TASK) {
            //暂停一项任务
            BaseDownloadTask lastTask = mTaskPool.pollLast();
            lastTask.pause();
            //将暂停的任务加入到下载队列
            DownloadInfo paused = new DownloadInfo(lastTask.getUrl(), lastTask.getPath());
            mDownloadQueue.addFirst(paused);
        }

        //下载优先下载的任务
        startDownload(url, savedPath);
    }

    public void removeDownload(String url) {
        BaseDownloadTask task = null;
        if ((task = findTask(url)) != null) {
            if (task.isRunning()) {
                task.pause();
            }
            mTaskPool.remove(task);
        }

        for (DownloadInfo info : mDownloadQueue) {
            if (info.mUrl.equals(url)) {
                mDownloadQueue.remove(info);
                break;
            }
        }
    }

    public BaseDownloadTask getDownloadTask(String url) {
        return findTask(url);
    }

    public int getTaskPoolSize() {
        return mTaskPool.size();
    }

    static class DownloadInfo {
        String mUrl;
        String mPath;

        public DownloadInfo(String mUrl, String mPath) {
            this.mUrl = mUrl;
            this.mPath = mPath;
        }
    }
}
