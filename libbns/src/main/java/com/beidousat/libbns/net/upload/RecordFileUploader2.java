package com.beidousat.libbns.net.upload;

import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J Wong on 2017/4/4.
 */

public class RecordFileUploader2 implements FileUploadListener {

    private static RecordFileUploader2 mRecordFileUploader;
    private FileUploader mFileUploader;
    private Map<String, FileUploadInfo> mUploadInfos = new HashMap<>();

    public static RecordFileUploader2 getInstance(String url) {
        if (mRecordFileUploader == null) {
            mRecordFileUploader = new RecordFileUploader2(url);
        }
        return mRecordFileUploader;
    }

    public FileUploadInfo getFileUploadInfo(String srcPath) {
        return mUploadInfos.get(srcPath);
    }

    private RecordFileUploader2(String url) {
        this.mUploaderUrl = url;
        mFileUploader = new FileUploader();
        mFileUploader.setFileUploadListener(this);
    }

    /**
     * 上传录音文件
     *
     * @param path       录音文件
     * @param orderSn    付款码订单号
     * @param songId     歌曲ID
     * @param songName   歌曲名
     * @param singerName 歌星
     * @param score      分数
     */
    public void uploadRecord(String path, String orderSn, String songId, String songName, String singerName, int score, String boxSn) {
        FileUploadInfo info = mUploadInfos.get(path);
        if (info != null && info.isSuccess)//已经上传成功了
            return;

        info = new FileUploadInfo();
        info.srcPath = path;
        info.orderSn = orderSn;
        info.songId = songId;
        info.songName = songName;
        info.singerName = singerName;
        info.score = score;
        info.boxSn = boxSn;

        upload(info);
    }

    private String mUploaderUrl;

    public void setUploadUrl(String url) {
        this.mUploaderUrl = url;
    }

    private void upload(FileUploadInfo info) {
        FileUploadInfo mapInfo = mUploadInfos.get(info.srcPath);
        if (mapInfo != null && (mapInfo.isSuccess || mapInfo.isUploading)) {
            return;
        }

        Map<String, String> p = new HashMap<>();
        p.put("order_sn", info.orderSn);
        p.put("song_id", info.songId);
        p.put("song_name", info.songName);
        p.put("singer_name", info.singerName);
        p.put("score", String.valueOf(info.score));
        p.put("kbox_sn", info.boxSn);

        info.isUploading = true;
        mUploadInfos.put(info.srcPath, info);

        Logger.d(getClass().getSimpleName(), "startUpload order_sn:" + info.orderSn + "  songId:" + info.songId + "  songName:" + info.songName + " singer_name:" + info.singerName);
        mFileUploader.upload(info.srcPath, mUploaderUrl, p);

    }


    @Override
    public void onUploadFailure(File file, String errInfo) {
        FileUploadInfo fileInfo = mUploadInfos.get(file.getAbsolutePath());
        fileInfo.srcPath = file.getAbsolutePath();
        fileInfo.decPath = null;
        fileInfo.isSuccess = false;
        fileInfo.progress = 0;
        fileInfo.isUploading = false;
        mUploadInfos.put(file.getAbsolutePath(), fileInfo);

        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
            for (RecordUploadListener listener : mRecordUploadListeners) {
                listener.onUploadFail(file.getAbsolutePath(), errInfo);
            }
        }
        Logger.d("RecordFileUploader", fileInfo.songName + ";error:" + errInfo);

    }

    @Override
    public void onUploadCompletion(File file, String desPath) {
        if (!desPath.startsWith("http")) {
            desPath = mShareDomain + desPath;
        }
        FileUploadInfo fileInfo = mUploadInfos.get(file.getAbsolutePath());
        fileInfo.srcPath = file.getAbsolutePath();
        fileInfo.decPath = desPath;
        fileInfo.isSuccess = true;
        fileInfo.progress = 1;
        fileInfo.isUploading = false;
        mUploadInfos.put(file.getAbsolutePath(), fileInfo);
        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
            List<RecordUploadListener> result = new ArrayList<>(Arrays.asList(new RecordUploadListener[mRecordUploadListeners.size()]));
            Collections.copy(result, mRecordUploadListeners);
            for (RecordUploadListener listener : result) {
                Logger.d("RecordFileUploader", "onUploadCompletion file:" + file.getAbsolutePath());
                listener.onUploadCompletion(file.getAbsolutePath(), desPath);
            }
        }
        Logger.d("RecordFileUploader", fileInfo.songName);
    }

    @Override
    public void onUploading(File file, float progress) {
        FileUploadInfo fileInfo = mUploadInfos.get(file.getAbsolutePath());
        fileInfo.srcPath = file.getAbsolutePath();
        fileInfo.decPath = null;
        fileInfo.isSuccess = false;
        fileInfo.progress = progress;
        fileInfo.isUploading = true;
        mUploadInfos.put(file.getAbsolutePath(), fileInfo);
        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
            for (RecordUploadListener listener : mRecordUploadListeners) {
                listener.onUploading(file.getAbsolutePath(), progress);
            }
        }
    }


    @Override
    public void onUploadStart(File file) {
        FileUploadInfo fileInfo = mUploadInfos.get(file.getAbsolutePath());
        fileInfo.srcPath = file.getAbsolutePath();
        fileInfo.decPath = null;
        fileInfo.isSuccess = false;
        fileInfo.progress = 0;
        fileInfo.isUploading = true;
        mUploadInfos.put(file.getAbsolutePath(), fileInfo);

        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
            for (RecordUploadListener listener : mRecordUploadListeners) {
                listener.onUploadStart(file.getAbsolutePath());
            }
        }
    }


    public class FileUploadInfo {
        public String srcPath;
        public String decPath;
        public float progress;
        public boolean isSuccess;

        public String orderSn;
        public String songId;
        public String songName;
        public String singerName;
        public int score;

        public String boxSn;

        public boolean isUploading = false;
    }

    private List<RecordUploadListener> mRecordUploadListeners = new ArrayList<>();

    public void addRecordUploadListener(RecordUploadListener listener) {
        mRecordUploadListeners.add(listener);
    }

    public void removeRecordUploadListener(RecordUploadListener listener) {
        mRecordUploadListeners.remove(listener);
    }

    private String mShareDomain;

    public void setShareDomain(String shareDomain) {
        mShareDomain = shareDomain;
    }

    public interface RecordUploadListener {

        void onUploadStart(String srcPath);

        void onUploading(String srcPath, float progress);

        void onUploadCompletion(String srcPath, String desPath);

        void onUploadFail(String srcPath, String errMsg);

    }
}

