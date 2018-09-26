package com.beidousat.karaoke.player.local;


import com.beidousat.karaoke.player.proxy.Config;
import com.beidousat.karaoke.player.proxy.Decoder;
import com.beidousat.karaoke.player.proxy.IDownState;
import com.beidousat.karaoke.player.proxy.IDownState.DownState;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileHeadLoadThread extends Thread {

    static private final String TAG = "DownloadThread";
    private String mUrl;
    private String mPath;
    private int mDownloadSize;
    private int mTargetSize;
    private boolean mStop;
    private boolean mDownloading;
    private boolean mStarted;
    private boolean mError;
    private final static int BUF_LEN = 1024 * 512;
    private IDownState state;

    public FileHeadLoadThread(String url, String savePath, IDownState state, int tsize) {
        mUrl = url;
        mPath = savePath;
        this.state = state;
        mDownloadSize = 0;
        mTargetSize = tsize;
        mStop = false;
        mDownloading = false;
        mStarted = false;
        mError = false;
    }

    @Override
    public void run() {
        mDownloading = true;
        download();
    }

    /**
     * 启动下载线程
     */
    public void startThread() {
        if (!mStarted) {
            this.start();
            // 只能启动一次
            mStarted = true;
        }
    }

    /**
     * 停止下载线程
     */
    public void stopThread() {
        mStop = true;
    }

    /**
     * 是否正在下载
     */
    public boolean isDownloading() {
        return mDownloading;
    }

    /**
     * 是否下载异常
     *
     * @return
     */
    public boolean isError() {
        return mError;
    }

    public long getDownloadedSize() {
        return mDownloadSize;
    }

    /**
     * 是否下载成功
     */
    public boolean isDownloadSuccessed() {
        return (mDownloadSize != 0 && mDownloadSize >= mTargetSize);
    }

    public boolean checkStop() {
        return mStop;
    }

    private void download() {
        InputStream is = null;
        if (mStop) {
            return;
        }
        int clen;
        try {
            File file = DiskFileUtil.getDiskFileByUrl(mUrl);
            if (file == null || !file.exists()) {
                Logger.d(TAG, "本地文件不存在");
                state.onDown(mUrl, 0, DownState.ERROREXIT, null, 0);
                return;
            }
            Logger.d(TAG, "本地文件：" + file.getAbsolutePath());
            is = new FileInputStream(file);
            clen = (int) file.length();
            if (mStop) {
                return;
            }
            int len = 0;
            byte[] bs = new byte[BUF_LEN];
            byte[] hbuf = new byte[Config.ENCODE_HEAD_LEN * 4];
            int hlen = 0;
            while (!mStop) {
                Logger.d(TAG, "读取文件" + mUrl);
                len = is.read(bs, 0, Config.ENCODE_HEAD_LEN * 2);
                if (len > 0) {
                    System.arraycopy(bs, 0, hbuf, hlen, len);
                    hlen += len;
                }
                if (hlen >= Config.ENCODE_HEAD_LEN)
                    break;
            }
            if (hlen != -1) {
                if (Decoder.getinstance().checkEncryt(bs)) {
                    Logger.d(TAG, "读取文件为加密文件" + mUrl);
                    state.onInit(mUrl, clen - 8, true);
                } else {
                    Logger.d(TAG, "读取文件不是加密文件" + mUrl);
                    state.onInit(mUrl, clen, false);
                }
                byte[] dec = Decoder.getinstance().decoder(bs, len);
                mDownloadSize += dec.length;
                state.onDown(mUrl, mDownloadSize, DownState.DOWNING, dec, dec.length);
                Logger.d(TAG, "读取文件 mDownloadSize： " + mDownloadSize);

            }
            state.onDown(mUrl, (int) mDownloadSize, DownState.SUCCESS, null, 0);
        } catch (Exception e) {
            Logger.d(TAG, "读取文件 Exception： " + e.getMessage());
            state.onDown(mUrl, (int) mDownloadSize, DownState.ERROR, null, 0);
            mError = true;
            e.printStackTrace();
        } finally {
            state = null;
            mDownloading = false;
            mStop = true;
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
