package com.beidousat.karaoke;

import android.app.Application;

import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.libbns.net.download.OkHttp3Connection;
import com.beidousat.libbns.net.request.OkHttpUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;

import java.io.File;

import okio.Buffer;

/**
 * Created by J Wong on 2017/3/24.
 */

public class LanApp extends Application {
    public KaraokeController mKaraokeController;
    private static LanApp mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mKaraokeController = KaraokeController.getInstance(getApplicationContext());
    }

    public static LanApp getInstance() {
        return mInstance;
    }
}
