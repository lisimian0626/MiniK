//package com.beidousat.karaoke.oss.base.service;
//
//import android.graphics.Bitmap;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.alibaba.sdk.android.oss.ClientException;
//import com.alibaba.sdk.android.oss.OSS;
//import com.alibaba.sdk.android.oss.ServiceException;
//import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
//import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
//import com.alibaba.sdk.android.oss.common.OSSLog;
//import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
//import com.alibaba.sdk.android.oss.model.GetObjectRequest;
//import com.alibaba.sdk.android.oss.model.GetObjectResult;
//import com.alibaba.sdk.android.oss.model.OSSRequest;
//import com.beidousat.libbns.util.Logger;
//import com.liulishuo.filedownloader.BaseDownloadTask;
//import com.liulishuo.filedownloader.FileDownloadListener;
//import com.liulishuo.filedownloader.FileDownloader;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
///**
// * author: Hanson
// * date:   2017/4/20
// * describe:
// */
//
//public class OssDownloaderHelper {
//
//    private static final String TAG = "OssDownloaderHelper";
//    private static final OssDownloaderHelper mInstance = new OssDownloaderHelper();
//    public OSS mOss;
//    private ConcurrentLinkedDeque<OSSAsyncTask> mTaskPool;
//    private ConcurrentLinkedDeque<DownloadInfo> mDownloadQueue;
//
//    private static final int MAX_DOWNLOAD_TASK = 1;
//
//    private FileDownloadListener mFinishListener;
//
//
//    private OssDownloaderHelper() {
//        mTaskPool = new ConcurrentLinkedDeque<>();
//        mDownloadQueue = new ConcurrentLinkedDeque<>();
//
//        /*mFinishListener = new FileDownloadLargeFileListener() {
//            @Override
//            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                EventBusUtil.postDownloadStart(task.getUrl(), task.getPath());
//                //检测磁盘空间是否充足
//                if (!StorageUtil.isExternalStorageHasAvailableSpace(totalBytes-soFarBytes)) {
//                    //TODO 通知APP磁盘空间不足
//                    EventBusUtil.postDownloadSpaceNotEnough(task.getUrl(), task.getPath());
//                }
//            }
//
//            @Override
//            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                Logger.i(TAG, String.format("%.2f", (float)soFarBytes/totalBytes));
//                if (totalBytes != 0) {
//                    EventBusUtil.postDownloadProgress(task.getUrl(), task.getPath(), soFarBytes/totalBytes);
//                }
//            }
//
//            @Override
//            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
//                Logger.i(TAG, "pause----"+String.format("%.2f", (float)soFarBytes/totalBytes));
//            }
//
//            @Override
//            protected void completed(BaseDownloadTask task) {
//                EventBusUtil.postDownloadFinish(task.getUrl(), task.getPath());
//                startNextDownload(task);
//                Logger.i(TAG, task.getFilename()+"finished---taskSize="+mTaskPool.size());
//            }
//
//            @Override
//            protected void error(BaseDownloadTask task, Throwable e) {
//                Logger.i(TAG, e.toString());
//                startNextDownload(task);
//                EventBusUtil.postDownloadError(task.getUrl(), task.getPath(), e.toString());
//            }
//
//            @Override
//            protected void warn(BaseDownloadTask task) {
//                Logger.i(TAG, String.format("%.2f", "warn"));
//                startNextDownload(task);
//            }
//        };*/
//    }
//
//    public static OssDownloaderHelper getInstance() {
//        return mInstance;
//    }
//
//    public void setFileDownloaderListener(FileDownloadListener listener) {
//        mFinishListener = listener;
//    }
//
//    /**
//     * 开始一个下载任务；如果队列中有排队的任务，则自动下载队首的任务
//     *
//     * @param complete 当前完成的任务，用于删除完成任务队列
//     */
//    public void startNextDownload(BaseDownloadTask complete) {
//        removeDownload(complete.getUrl());
//
//        //下载完成后找出最优先加入下载池
//        DownloadInfo info = mDownloadQueue.pollFirst();
//        if (info != null) {
//            startDownload(info.mUrl, info.mPath);
//        }
//    }
//
//    public void startNextDownload(String url) {
//        removeDownload(url);
//
//        //下载完成后找出最优先加入下载池
//        DownloadInfo info = mDownloadQueue.pollFirst();
//        if (info != null) {
//            startDownload(info.mUrl, info.mPath);
//        }
//    }
//
//    /**
//     * 开始下载，如果超过MAX_DOWNLOAD_TASK，则加入队列等待下载
//     *
//     * @param url
//     * @param savedPath
//     */
//    public void startDownload(String url, String savedPath) throws IllegalArgumentException {
//        Logger.d(TAG, "startDownload url:" + url + "  savedPath:" + savedPath);
//
//        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savedPath))
//            throw new IllegalArgumentException("url or savedPath can't be null");
//        GetObjectRequest get = new GetObjectRequest(mBucket, object);
//        get.setCRC64(OSSRequest.CRC64Config.YES);
//        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
//            @Override
//            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
//                Log.d("GetObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                int progress = (int) (100 * currentSize / totalSize);
//                mDisplayer.updateProgress(progress);
//                mDisplayer.displayInfo("下载进度: " + String.valueOf(progress) + "%");
//            }
//        });
//        OSSAsyncTask task = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
//            @Override
//            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
//                // 请求成功
//                InputStream inputStream = result.getObjectContent();
//                //Bitmap bm = BitmapFactory.decodeStream(inputStream);
//                try {
//                    //需要根据对应的View大小来自适应缩放
//                    Bitmap bm = mDisplayer.autoResizeFromStream(inputStream);
//                    long get_end = System.currentTimeMillis();
//                    OSSLog.logDebug("get cost: " + (get_end - get_start) / 1000f);
//                    mDisplayer.downloadComplete(bm);
//                    mDisplayer.displayInfo("Bucket: " + mBucket + "\nObject: " + request.getObjectKey() + "\nRequestId: " + result.getRequestId());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                String info = "";
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                    info = clientExcepion.toString();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                    info = serviceException.toString();
//                }
//                mDisplayer.downloadFail(info);
//                mDisplayer.displayInfo(info);
//            }
//        });
//        BaseDownloadTask downloading = findTask(url);
//        //如果任务正在下载，直接返回
//        if (downloading != null) return;
//
//        //当下载线程小于MAX_DOWNLOAD_TASK直接，开启下载线程；否者加入待下载列表
//        if (mTaskPool.size() < MAX_DOWNLOAD_TASK) {
//            BaseDownloadTask task = FileDownloader.getImpl().create(url)
//                    .setPath(savedPath).setListener(mFinishListener);
//            mTaskPool.add(task);
//
//            task.start();
//        } else {
//            DownloadInfo info = new DownloadInfo(url, savedPath);
//            mDownloadQueue.add(info);
//        }
//    }
//
//    private BaseDownloadTask findTask(String url) {
//        for (BaseDownloadTask task : mTaskPool) {
//            if (task.getUrl().equals(url))
//                return task;
//        }
//
//        return null;
//    }
//
//    /**
//     * 优先下载
//     *
//     * @param url
//     * @param savedPath
//     */
//    public void priorDownload(String url, String savedPath) {
//        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savedPath))
//            throw new IllegalArgumentException("url or savedPath can't be null");
//
//        if (mTaskPool.size() >= MAX_DOWNLOAD_TASK) {
//            //暂停一项任务
//            BaseDownloadTask lastTask = mTaskPool.pollLast();
//            lastTask.pause();
//            //将暂停的任务加入到下载队列
//            DownloadInfo paused = new DownloadInfo(lastTask.getUrl(), lastTask.getPath());
//            mDownloadQueue.addFirst(paused);
//        }
//
//        //下载优先下载的任务
//        startDownload(url, savedPath);
//    }
//
//    public void removeDownload(String url) {
//        BaseDownloadTask task = null;
//        if ((task = findTask(url)) != null) {
//            if (task.isRunning()) {
//                task.pause();
//            }
//            mTaskPool.remove(task);
//        }
//
//        for (DownloadInfo info : mDownloadQueue) {
//            if (info.mUrl.equals(url)) {
//                mDownloadQueue.remove(info);
//                break;
//            }
//        }
//    }
//
//    public BaseDownloadTask getDownloadTask(String url) {
//        return findTask(url);
//    }
//
//    public int getTaskPoolSize() {
//        return mTaskPool.size();
//    }
//
//    static class DownloadInfo {
//        String mUrl;
//        String mPath;
//
//        public DownloadInfo(String mUrl, String mPath) {
//            this.mUrl = mUrl;
//            this.mPath = mPath;
//        }
//    }
//}
