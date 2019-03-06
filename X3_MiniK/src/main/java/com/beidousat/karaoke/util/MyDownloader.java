package com.beidousat.karaoke.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.db.DatabaseHelper;
import com.beidousat.karaoke.db.service.SongDao;
import com.beidousat.karaoke.model.DownloadProgress;
import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.download.DownloaderHelper;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.StorageUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * author: Hanson
 * date:   2017/5/8
 * describe:
 */

public class MyDownloader {
    private static final String TAG = "MyDownloader";
    private static final MyDownloader mInstance = new MyDownloader();
    private Map<String, Song> mSongCache = new HashMap<>();

    private Map<String, Song> mSongErro = new HashMap<>();

    private DownloaderHelper mDownloadHelper;
    private boolean isFinishAll = false;

    private FileDownloadListener mFinishListener = new FileDownloadLargeFileListener() {
        @Override
        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            Logger.d(TAG, task.getFilename() + ";path=" + task.getPath());
            isFinishAll = false;
            // EventBusUtil.postDownloadStart(task.getUrl(), task.getPath());
            //检测磁盘空间是否充足

            if (!StorageUtil.isUsbDiskHasAvailableSpace()) {
                EventBusUtil.postDownloadSpaceNotEnough(task.getUrl(), task.getPath());
                Logger.d(TAG, "空间不足，删除歌曲");
                new freeDiskSpaceTask().execute();
            }
            EventBusUtil.postDownloadProgress(task.getUrl(), task.getPath(), 0);
        }

        @Override
        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            Logger.i(TAG, String.format("%.2f", (float) soFarBytes / totalBytes));
            if (totalBytes != 0) {
                EventBusUtil.postDownloadProgress(task.getUrl(), task.getPath(), (float) soFarBytes / totalBytes * 100);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
            Logger.d(TAG, "pause----" + String.format("%.2f", (float) soFarBytes / totalBytes));
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            //是否是最后一个任务
            isFinishAll = mSongCache.size() == 1;
            EventBusUtil.postDownloadProgress(task.getUrl(), task.getPath(), 100);
            EventBusUtil.postDownloadFinish(task.getUrl(), task.getPath());

            startNextDownload(task.getUrl());

            //将下载的文件记录保存到数据库
            addSongToSongCache(task.getPath());
            Logger.d(TAG, task.getFilename() + "finished---path=" + task.getPath());
            Logger.i(TAG, "songCacheSize = " + mSongCache.size());
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            String erroMsg = e.toString();
            Logger.d(TAG, "error Throwable:" + erroMsg);
            try {
                if (e instanceof SocketTimeoutException) {
                    erroMsg = "网络超时！";
                } else if (e instanceof FileDownloadHttpException) {
                    FileDownloadHttpException fileDownloadHttpException = (FileDownloadHttpException) e;
                    erroMsg = "连接异常！code：" + fileDownloadHttpException.getCode();
                } else if (e instanceof FileDownloadOutOfSpaceException) {
                    erroMsg = "磁盘空间不足！";
                }else if(e instanceof IOException){
                    erroMsg="读写文件失败";
                }else {
                    erroMsg="未知错误";
                }
            } catch (Exception ex) {
                ex.getLocalizedMessage();
                erroMsg="未知错误";
            }
            Logger.d(TAG, "error erroMsg:" + erroMsg);
            EventBusUtil.postDownloadError(task.getUrl(), task.getPath(), erroMsg);
            Song song = getSong(task.getUrl());
            if(song!=null) {
                song.downloadErro = erroMsg;
                mSongErro.put(task.getUrl(), song);
                startNextDownload(task.getUrl());
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Logger.d(TAG, String.format("%.2f", "warn"));
            startNextDownload(task.getUrl());
        }
    };

    /**
     * 将歌曲添加到本地数据库缓存
     *
     * @param path 文件路径，于服务器路径一致
     *             (完整的文件路径需要调用 {@link DiskFileUtil#getFileSavedPath(String)}})
     */
    private void addSongToSongCache(String path) {
        SongDao songDao = LanApp.getInstance().getDataBaseHelper().getSongDao();
        songDao.insertSong(DiskFileUtil.convertDiskpathToServerPath(path), 20);
    }

    /**
     * 删除磁盘文件以及数据库中缓存记录;只删除最少点播的20首
     */
    class freeDiskSpaceTask extends AsyncTask<String ,String ,String >{
        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            freeDiskSpace();
            return null;
        }
    }
    public void freeDiskSpace() {
        DatabaseHelper dbHelper = LanApp.getInstance().getDataBaseHelper();
        List<String> paths = dbHelper.getSongDao().deleteLessHotSongs();
        if (paths != null && paths.size() > 0) {
            for (String str : paths) {
                Logger.d(TAG,"要删除歌曲的初始path:"+str.toString());
                String diskPath = DiskFileUtil.getFileSavedPath(str);
                if (TextUtils.isEmpty(diskPath)) {
                    continue;
                }

                File file = new File(diskPath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                    Logger.d(TAG,"执行删除路径:"+str.toString());
                }
            }
            if(!StorageUtil.isUsbDiskHasAvailableSpace()){
                new freeDiskSpaceTask().execute();
            }else{
                return;
            }
        }
    }

    private MyDownloader() {
        mDownloadHelper = DownloaderHelper.getInstance();
        mDownloadHelper.setFileDownloaderListener(mFinishListener);
    }

    public static MyDownloader getInstance() {
        return mInstance;
    }

    public void startDownload(String url, String savedPath, Song song) throws Exception {
        if (mSongCache.size() > 10) {
            throw new Exception("下载队列不能超过10个任务.");
        }
        if(mSongCache.get(url)!=null){
            return;
        }
        EventBusUtil.postDownloadStart(url, savedPath);
        mSongCache.put(url, song);
        mSongErro.remove(url);
        mDownloadHelper.startDownload(url, savedPath);
    }

//    /**
//     * 优先下载
//     * @param url
//     * @param savedPath
//     */
//    public void priorDownload(String url, String savedPath, Song song) {
//        song.isPrior = true;
//        mSongCache.put(url, song);
//        mDownloadHelper.priorDownload(url, savedPath);
//    }

    public void startNextDownload(String url) {
        removeSongFromCache(url);
        mDownloadHelper.startNextDownload(url);
    }

    public Song getSong(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return mSongCache.get(url);
    }

    public int getTaskQueueSize() {
        return mSongCache.size();
    }

    public boolean isFinishAllTask() {
        return isFinishAll;
    }

    public List<DownloadProgress> getSongsProgress() {
        List<DownloadProgress> songProgress = new ArrayList<>();
        for (String url : mSongCache.keySet()) {
            Song song = mSongCache.get(url);
            BaseDownloadTask task = mDownloadHelper.getDownloadTask(url);
            int percent = 0;
            if (task != null) {
                percent = (int) (100 * (float) task.getSmallFileSoFarBytes() / task.getSmallFileTotalBytes());
            }
            DownloadProgress item = new DownloadProgress();
            item.song = song;
            item.percent = percent;
            songProgress.add(item);
        }

        for (String url : mSongErro.keySet()) {
            if (!mSongCache.containsKey(url)) {
                Song song = mSongErro.get(url);
                DownloadProgress item = new DownloadProgress();
                item.song = song;
                item.percent = 0;
                songProgress.add(item);
            }
        }

        return songProgress;
    }

    private void removeSongFromCache(String url) {
        if (TextUtils.isEmpty(url))
            return;
        mSongCache.remove(url);
    }
}
