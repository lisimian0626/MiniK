package com.beidousat.libbns.net.download;

import android.os.AsyncTask;
import android.text.TextUtils;


import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by J Wong on 2015/12/7 18:41.
 */
public class FileDownloader {

//    private static final OkHttpClient client = new OkHttpClient();
//    private String fileUrl;
//    private File mDesFile;
//    private String mFileUrl;
//    private FileDownloadListener mFileDownloadListener;

    public void download(final File desFile, final String url, final FileDownloadListener listener) {
        if (desFile == null || TextUtils.isEmpty(url)) {
            return;
        }

        String fileUrl = url;
        Logger.d(getClass().getSimpleName(), "download url:" + fileUrl);

//        mDesFile = desFile;
//        mFileUrl = fileUrl;
//        mFileDownloadListener = listener;

        new AsyncDownloader(desFile, fileUrl, listener).execute();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                download();
//            }
//        }).start();
    }

    private long mTotalSize = 0L;

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    Logger.d(getClass().getSimpleName(), "download onDownloadFail:");
//                    if (mFileDownloadListener != null)
//                        mFileDownloadListener.onDownloadFail(mFileUrl);
//                    break;
//                case 1:
//                    Bundle bundle = msg.getData();
//                    long progress = bundle.getLong("progress");
//                    long total = bundle.getLong("total");
//                    Logger.d(getClass().getSimpleName(), "download progress:" + String.format("%d / %d", progress, total));
//                    mTotalSize = total;
//                    if (mFileDownloadListener != null)
//                        mFileDownloadListener.onUpdateProgress(mDesFile, progress, total);
//                    break;
//                case 2:
//                    Logger.d(getClass().getSimpleName(), "download onDownloadCompletion:");
//
//                    if (mFileDownloadListener != null)
//                        mFileDownloadListener.onDownloadCompletion(mDesFile, mFileUrl, mTotalSize);
//                    break;
//            }
//        }
//    };


//    private void publishProgress(long progress, long total) {
//        Message message = new Message();
//        message.what = 1;
//        Bundle bundle = new Bundle();
//        bundle.putLong("progress", progress);
//        bundle.putLong("total", total);
//        message.setData(bundle);
//        mHandler.sendMessage(message);
//    }


    //    private void download() {
//        Logger.d(getClass().getSimpleName(), "download url doInBackground:" + mFileUrl);
//        OkHttpClient httpClient = new OkHttpClient();
//        Call call = httpClient.newCall(new Request.Builder().url(mFileUrl).get().build());
//        try {
//            Response response = call.execute();
//            if (response.code() == 200) {
//                InputStream inputStream = null;
//                FileOutputStream fileOutputStream = null;
//                try {
//                    inputStream = response.body().byteStream();
//                    fileOutputStream = new FileOutputStream(mDesFile);
//                    byte[] buff = new byte[1024 * 4];
//                    long downloaded = 0;
//                    long target = response.body().contentLength();
//                    publishProgress(0L, target);
//                    Logger.d("FileDownloader", "SDCARD MOUNTED:" + KaraokeSdHelper.existSDCard());
//                    while (true) {
//                        int readed = inputStream.read(buff);
//                        if (readed == -1) {
//                            break;
//                        }
//                        //write buff
//                        fileOutputStream.write(buff, 0, readed);
//                        downloaded += readed;
//                        publishProgress(downloaded, target);
////                        if (isCancelled()) {
////                            return false;
////                        }
//                    }
//                    Logger.d("FileDownloader", "download finish downloaded :" + downloaded + " target :" + target);
//
//                    try {
//                        inputStream.close();
//                        inputStream = null;
//                        fileOutputStream.flush();
//                        fileOutputStream.close();
//                        fileOutputStream = null;
//                    } catch (Exception e) {
//                        Logger.d("FileDownloader", "download close() ex:" + e.toString());
//                    }
//
//                    mHandler.sendEmptyMessage(downloaded >= target ? 2 : 0);
//
//                } catch (IOException ignore) {
//                    mHandler.sendEmptyMessage(0);
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    if (fileOutputStream != null)
//                        fileOutputStream.close();
//                }
//            } else {
//                mHandler.sendEmptyMessage(0);
//            }
//        } catch (IOException e) {
//            Logger.e(getClass().getSimpleName(), "download IOException :" + e.toString());
//            mHandler.sendEmptyMessage(0);
//        } catch (Exception e) {
//            Logger.e(getClass().getSimpleName(), "download Exception :" + e.toString());
//            mHandler.sendEmptyMessage(0);
//        }
//    }
    private boolean IgnoreByFileLen = false;

    public void setIgnoreByFileLen(boolean ignoreFileLen) {
        this.IgnoreByFileLen = ignoreFileLen;
    }

    private class AsyncDownloader extends AsyncTask<Void, Long, Boolean> {

        private String mUrl;
        private File mDesFile;
        private FileDownloadListener mFileDownloadListener;
        private File desFileTemp;

        public AsyncDownloader(File desFile, String url, FileDownloadListener listener) {
            mUrl = url;
            mDesFile = desFile;
            desFileTemp = new File(desFile.getAbsolutePath() + ".temp");
            mFileDownloadListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File parentFile = mDesFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            Logger.d(getClass().getSimpleName(), "download url doInBackground:" + mUrl);
            OkHttpClient httpClient = new OkHttpClient();
            Call call = httpClient.newCall(new Request.Builder().url(mUrl).get().build());
            try {
                Response response = call.execute();
                if (response.code() == 200) {
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(desFileTemp);
                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();
                        mTotalSize = target;
                        if (mDesFile.exists() && IgnoreByFileLen && mTotalSize == mDesFile.length()) {//通过文件大小判断是否需要下载
                            Logger.d(getClass().getSimpleName(), "download not need to down:" + mUrl);
                            publishProgress(target, target);
                            return true;
                        } else {
                            Logger.d(getClass().getSimpleName(), "download  need to down:" + mUrl);
                            publishProgress(0L, target);
                            while (true) {
                                int readed = inputStream.read(buff);
                                if (readed == -1) {
                                    break;
                                }
                                //write buff
                                fileOutputStream.write(buff, 0, readed);
                                downloaded += readed;
                                publishProgress(downloaded, target);
                                if (isCancelled()) {
                                    return false;
                                }
                            }
                            if (downloaded == target) {
                                if (mDesFile.exists()) {
                                    mDesFile.delete();
                                }
                                boolean rename = desFileTemp.renameTo(mDesFile);
                                Logger.d(getClass().getSimpleName(), "renameTo ret:" + rename + " desfile:" + mDesFile.getAbsolutePath());
                                return rename;
                            } else {
                                Logger.e(getClass().getSimpleName(), "downloaded != target");
                                return false;
                            }
                        }
//                        return downloaded == target;
                    } catch (IOException ignore) {
                        return false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                Logger.e(getClass().getSimpleName(), "IOException :" + e.toString());
                return false;
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), "Exception :" + e.toString());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            if (mFileDownloadListener != null)
                mFileDownloadListener.onUpdateProgress(mDesFile, values[0], values[1]);
            Logger.d(getClass().getSimpleName(), "download progress:" + String.format("%d / %d", values[0], values[1]));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                Logger.d(getClass().getSimpleName(), "File :" + mDesFile.getAbsolutePath() + " exist :" + mDesFile.exists() + "  result:" + result);
                if (result) {
                    mFileDownloadListener.onDownloadCompletion(mDesFile, mUrl, mTotalSize);
                } else {
                    mFileDownloadListener.onDownloadFail(mUrl);
                }
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), "onPostExecute Exception :" + e.toString());
            }
        }
    }
}
