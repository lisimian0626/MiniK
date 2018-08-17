//package com.beidousat.libbns.net.upload;
//
//import com.beidousat.libbns.util.Logger;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by J Wong on 2017/4/4.
// */
//
//public class RecordFileUploader implements FileUploadListener {
//
//    private static RecordFileUploader mRecordFileUploader;
//    private List<FileUploadInfo> filePaths = new ArrayList<>();
//    private FileUploader mFileUploader;
////    private static final String FILE_UPLOAD_URL = RequestMethod.UPLOAD_RECORD.replace("https://", "http://");
//
//    private boolean mIsUploading;
//    private Map<String, FileUploadInfo> mUploadInfos = new HashMap<>();
//    private String mUploadingPath;
//
//    public static RecordFileUploader getInstance(String url) {
//        if (mRecordFileUploader == null) {
//            mRecordFileUploader = new RecordFileUploader(url);
//        }
//        return mRecordFileUploader;
//    }
//
//    public FileUploadInfo getFileUploadInfo(String srcPath) {
//        return mUploadInfos.get(srcPath);
//    }
//
//    private RecordFileUploader(String url) {
//        this.mUploaderUrl = url;
//        mFileUploader = new FileUploader();
//        mFileUploader.setFileUploadListener(this);
//        mIsUploading = false;
//    }
//
//    /**
//     * 上传录音文件
//     *
//     * @param path       录音文件
//     * @param orderSn    付款码订单号
//     * @param songId     歌曲ID
//     * @param songName   歌曲名
//     * @param singerName 歌星
//     * @param score      分数
//     */
//    public void addFile(String path, String orderSn, String songId, String songName, String singerName, int score, String boxSn) {
//        FileUploadInfo info = mUploadInfos.get(path);
//        if (info != null && info.isSuccess)//已经上传成功了
//            return;
//
//        if (info != null) {
//            info.retryTimes = 0;
//        } else {
//            info = new FileUploadInfo();
//            info.srcPath = path;
//            info.orderSn = orderSn;
//            info.songId = songId;
//            info.songName = songName;
//            info.singerName = singerName;
//            info.score = score;
//            info.retryTimes = 0;
//            info.boxSn = boxSn;
//        }
////        if ((info = mUploadInfos.get(path)) != null && !info.isSuccess) {
////            info.retryTimes = 0;
////        } else {
////            info = new FileUploadInfo();
////            info.srcPath = path;
////            info.orderSn = orderSn;
////            info.songId = songId;
////            info.songName = songName;
////            info.singerName = singerName;
////            info.score = score;
////            info.retryTimes = 0;
////            info.boxSn = boxSn;
////        }
//        filePaths.add(info);
//        startUpload();
//    }
//
//    private String mUploaderUrl;
//
//    public void setUploadUrl(String url) {
//        this.mUploaderUrl = url;
//    }
//
//    private void startUpload() {
//        if (!mIsUploading && filePaths.size() > 0) {
//            FileUploadInfo info = filePaths.remove(0);
//
//            FileUploadInfo mapInfo = mUploadInfos.get(info.srcPath);
//            if ((mapInfo != null && mapInfo.isSuccess) || mIsUploading) {
//                return;
//            }
//
//            try {
//                mUploadingPath = info.srcPath;
//                mIsUploading = true;
//                mUploadInfos.put(mUploadingPath, info);
//
//                Map<String, String> p = new HashMap<>();
//                p.put("order_sn", info.orderSn);
//                p.put("song_id", info.songId);
//                p.put("song_name", info.songName);
//                p.put("singer_name", info.singerName);
//                p.put("score", String.valueOf(info.score));
//                p.put("kbox_sn", info.boxSn);
//
//                Logger.d(getClass().getSimpleName(), "startUpload order_sn:" + info.orderSn + "  songId:" + info.songId + "  songName:" + info.songName + " singer_name:" + info.singerName);
//
//                mFileUploader.upload(mUploadingPath, mUploaderUrl, p);
//            } catch (Exception e) {
//                Logger.d("RecordFileUploader", "startUpload ex:" + e.toString());
//                FileUploadInfo fileInfo = mUploadInfos.get(mUploadingPath);
//                fileInfo.srcPath = mUploadingPath;
//                fileInfo.decPath = null;
//                fileInfo.isSuccess = false;
//                fileInfo.progress = 0;
//                mUploadInfos.put(mUploadingPath, fileInfo);
//
//                mIsUploading = false;
//                startUpload();
//            }
//        }
//    }
//
//
//    @Override
//    public void onUploadFailure(File file, String errInfo) {
//        FileUploadInfo fileInfo = mUploadInfos.get(mUploadingPath);
//        fileInfo.srcPath = mUploadingPath;
//        fileInfo.decPath = null;
//        fileInfo.isSuccess = false;
//        fileInfo.progress = 0;
//        mUploadInfos.put(mUploadingPath, fileInfo);
//
//        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
//            for (RecordUploadListener listener : mRecordUploadListeners) {
//                listener.onUploadFail(file.getAbsolutePath(), errInfo);
//            }
//        }
//        Logger.d("RecordFileUploader", fileInfo.songName + ";error:" + errInfo);
//        mIsUploading = false;
//        startUpload();
//
//        //retry
////        if (fileInfo.retryTimes < 3) {
////            addFile(fileInfo.srcPath, fileInfo.orderSn, fileInfo.songId, fileInfo.songName, fileInfo.singerName, fileInfo.score, fileInfo.boxSn);
////        }
//
//    }
//
//    @Override
//    public void onUploadCompletion(File file, String desPath) {
//
//        if (!desPath.startsWith("http")) {
//            desPath = mShareDomain + desPath;
//        }
//
//        FileUploadInfo fileInfo = mUploadInfos.get(mUploadingPath);
//        fileInfo.srcPath = mUploadingPath;
//        fileInfo.decPath = desPath;
//        fileInfo.isSuccess = true;
//        fileInfo.progress = 1;
//        mUploadInfos.put(mUploadingPath, fileInfo);
//
//        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
//            List<RecordUploadListener> result = new ArrayList<>(Arrays.asList(new RecordUploadListener[mRecordUploadListeners.size()]));
//            Collections.copy(result, mRecordUploadListeners);
//            for (RecordUploadListener listener : result) {
//                Logger.d("RecordFileUploader", "onUploadCompletion file:" + file.getAbsolutePath());
//                listener.onUploadCompletion(file.getAbsolutePath(), desPath);
//            }
//        }
//        Logger.d("RecordFileUploader", fileInfo.songName);
//        mIsUploading = false;
//        startUpload();
//    }
//
//    @Override
//    public void onUploading(File file, float progress) {
//        mIsUploading = true;
//        FileUploadInfo fileInfo = mUploadInfos.get(mUploadingPath);
//        fileInfo.srcPath = mUploadingPath;
//        fileInfo.decPath = null;
//        fileInfo.isSuccess = true;
//        fileInfo.progress = progress;
//        mUploadInfos.put(mUploadingPath, fileInfo);
//
//        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
//            for (RecordUploadListener listener : mRecordUploadListeners) {
//                listener.onUploading(file.getAbsolutePath(), progress);
//            }
//        }
//    }
//
//
//    @Override
//    public void onUploadStart(File file) {
//        mIsUploading = true;
//        if (mRecordUploadListeners != null && mRecordUploadListeners.size() > 0) {
//            for (RecordUploadListener listener : mRecordUploadListeners) {
//                listener.onUploadStart(file.getAbsolutePath());
//            }
//        }
//    }
//
//
//    public class FileUploadInfo {
//        public String srcPath;
//        public String decPath;
//        public float progress;
//        public boolean isSuccess;
//
//        public int retryTimes;
//
//        public String orderSn;
//        public String songId;
//        public String songName;
//        public String singerName;
//        public int score;
//
//        public String boxSn;
//    }
//
//    private List<RecordUploadListener> mRecordUploadListeners = new ArrayList<>();
//
//    public void addRecordUploadListener(RecordUploadListener listener) {
//        mRecordUploadListeners.add(listener);
//    }
//
//    public void removeRecordUploadListener(RecordUploadListener listener) {
//        mRecordUploadListeners.remove(listener);
//    }
//
//    private String mShareDomain;
//
//    public void setShareDomain(String shareDomain) {
//        mShareDomain = shareDomain;
//    }
//
//    public interface RecordUploadListener {
//
//        void onUploadStart(String srcPath);
//
//        void onUploading(String srcPath, float progress);
//
//        void onUploadCompletion(String srcPath, String desPath);
//
//        void onUploadFail(String srcPath, String errMsg);
//
//    }
//}
//
