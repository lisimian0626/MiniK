package com.beidousat.karaoke;

import android.app.Application;

import com.beidousat.karaoke.db.DatabaseHelper;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.libbns.net.download.OkHttp3Connection;
import com.beidousat.libbns.net.request.OkHttpUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;

/**
 * Created by J Wong on 2017/3/24.
 */

public class LanApp extends Application {
    public KaraokeController mKaraokeController;
    private DatabaseHelper mDbHelper;
    private static LanApp mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mKaraokeController = KaraokeController.getInstance(getApplicationContext());

        FileDownloadLog.NEED_LOG = true;
        FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new OkHttp3Connection.Creator(OkHttpUtil.mSSLOkHttpClient)));
        mDbHelper = new DatabaseHelper(this);

    }


    public static LanApp getInstance() {
        return mInstance;
    }

    public DatabaseHelper getDataBaseHelper() {
        return mDbHelper;
    }
}
