package com.beidousat.karaoke.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DownloadQueueHelper {

    private static final String TAG = DownloadQueueHelper.class.getSimpleName();
    private static final String KEY_TASK_ID = "download.task.id";
    private static final String KEY_TASK_SOFA = "download.task.sofa";
    private static final String KEY_TASK_TOTAL = "download.task.total";
    private static final String KEY_TASK_MSG = "download.task.msg";

    private static DownloadQueueHelper mHelper;
    private OnDownloadListener mListener;
    private FileDownloadListener mDownloadListener;
    private FileDownloadQueueSet mQueueSet;

    private List<BaseDownloadTask> mAllDownloadTasks;
    private SparseArray<Throwable> mThrowable = new SparseArray<>();

    private static final int CTRL_PROGRESS = 1;
    private static final int CTRL_OVER = 2;
    private static final int CTRL_ERROR = 3;
    private static final int CTRL_COMPLETE = 4;


    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (mListener == null) {
                return false;
            }

            switch (message.what) {
                case CTRL_PROGRESS:
                    mListener.onDownloadProgress(getTask(message), getSofaBytes(message), getTotalBytes(message));
                    break;
                case CTRL_OVER:
                    mListener.onDownloadTaskOver();
                    break;
                case CTRL_ERROR:
                    BaseDownloadTask task = getTask(message);
                    if(task!=null)
                    mListener.onDownloadTaskError(task, mThrowable.get(task.getId()));
                    break;
                case CTRL_COMPLETE:
                    BaseDownloadTask task1 = getTask(message);
                    mListener.onDownloadComplete(task1);
                    break;
            }
            return true;
        }
    });

    private DownloadQueueHelper() {
        mAllDownloadTasks = Collections.synchronizedList(new LinkedList<BaseDownloadTask>());
    }

    public static DownloadQueueHelper getInstance() {
        if (mHelper == null) {
            synchronized (DownloadQueueHelper.class) {
                if (mHelper == null) {
                    mHelper = new DownloadQueueHelper();
                }
            }
        }
        return mHelper;
    }

    private void initDownloadListener() {
        mDownloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                // 之所以加这句判断，是因为有些异步任务在pause以后，会持续回调pause回来，而有些任务在pause之前已经完成，
                // 但是通知消息还在线程池中还未回调回来，这里可以优化
                // 后面所有在回调中加这句都是这个原因
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                     int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                mHandler.sendMessage(packageMessage(CTRL_PROGRESS, task, soFarBytes, totalBytes));
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
//                mHandler.sendMessage(packageMessage(CTRL_COMPLETE, task, ""));
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                mHandler.sendMessage(packageMessage(CTRL_COMPLETE, task, ""));
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                mThrowable.put(task.getId(), e);
                mHandler.sendMessage(packageMessage(CTRL_ERROR, task, ""));
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }
        };
    }

    private void initQueueSet() {
        mQueueSet = new FileDownloadQueueSet(mDownloadListener);
        //重试10次
        mQueueSet.setAutoRetryTimes(3);
        mQueueSet.addTaskFinishListener(new BaseDownloadTask.FinishListener() {
            @Override
            public void over(BaseDownloadTask task) {
                mAllDownloadTasks.remove(task);
                if (mAllDownloadTasks.isEmpty()) {
                    mHandler.sendEmptyMessage(CTRL_OVER);
                }

            }
        });
    }

    /**
     * 自定义一些设置的downloadQueueSet
     *
     * @param fileDownloadQueueSet
     */
    public void setFileDownloadQueueSet(FileDownloadQueueSet fileDownloadQueueSet) {
        mQueueSet = fileDownloadQueueSet;
    }

    public void downloadTogether(List<BaseDownloadTask> tasks) {
        //暂停之前的下载任务
        if (mDownloadListener != null) {
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.addAll(tasks);
        mQueueSet.downloadTogether(mAllDownloadTasks);
        mQueueSet.start();
    }


    //暂停所有的下载任务
    public void pauseAllDownload() {
        FileDownloader.getImpl().pauseAll();
    }

    public void downloadSequentially(List<BaseDownloadTask> tasks) {
        if (mDownloadListener != null) {
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.addAll(tasks);
        mQueueSet.downloadSequentially(mAllDownloadTasks);
        mQueueSet.start();
    }

    public void downloadSequentially(BaseDownloadTask task) {
        if (mDownloadListener != null) {
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.add(0, task);
        mQueueSet.downloadSequentially(mAllDownloadTasks);
        mQueueSet.start();
    }

    public Message packageMessage(int what, BaseDownloadTask task, int sofaBytes, int totalBytes) {
        Message message = new Message();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TASK_ID, task.getId());
        bundle.putInt(KEY_TASK_SOFA, sofaBytes);
        bundle.putInt(KEY_TASK_TOTAL, totalBytes);
        message.setData(bundle);

        return message;
    }

    public Message packageMessage(int what, BaseDownloadTask task, String errMsg) {
        Message message = new Message();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TASK_ID, task.getId());
        bundle.putString(KEY_TASK_MSG, errMsg);
        message.setData(bundle);

        return message;
    }

    public BaseDownloadTask getTask(Message message) {
        Bundle bundle = message.getData();
        int id = bundle.getInt(KEY_TASK_ID);
        BaseDownloadTask task = null;
        for (BaseDownloadTask item : mAllDownloadTasks) {
            if (item.getId() == id) {
                task = item;
                break;
            }
        }

        return task;
    }

    public int getTotalBytes(Message message) {
        Bundle bundle = message.getData();
        int total = bundle.getInt(KEY_TASK_TOTAL, 0);

        return total;
    }

    public int getSofaBytes(Message message) {
        Bundle bundle = message.getData();
        int total = bundle.getInt(KEY_TASK_SOFA, 0);

        return total;
    }

    public String getErrorMsg(Message message) {
        Bundle bundle = message.getData();
        String errorMsg = bundle.getString(KEY_TASK_MSG, "");

        return errorMsg;
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mListener = listener;
    }

    public interface OnDownloadListener {
        //在下载线程
        void onDownloadComplete(BaseDownloadTask task);

        void onDownloadTaskError(BaseDownloadTask task, Throwable e);

        void onDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes);

        //所有下载任务完成
        void onDownloadTaskOver();
    }

}