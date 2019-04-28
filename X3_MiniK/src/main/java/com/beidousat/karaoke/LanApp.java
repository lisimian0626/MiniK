package com.beidousat.karaoke;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.beidousat.karaoke.db.DatabaseHelper;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.karaoke.util.RxAsyncTask;
import com.beidousat.karaoke.util.UnCeHandler;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.download.OkHttp3Connection;
import com.beidousat.libbns.net.request.OkHttpUtil;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.FileUtil;
import com.beidousat.libbns.util.Logger;
import com.facebook.stetho.Stetho;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * Created by J Wong on 2017/3/24.
 */

public class LanApp extends Application {
    public KaraokeController mKaraokeController;
    private DatabaseHelper mDbHelper;
    private static LanApp mInstance = null;

    //    private DaoMaster.DevOpenHelper mHelper;
//    private SQLiteDatabase db;
    public void onCreate() {
        super.onCreate();
        mInstance = this;

//        Beta.autoInit = true;//自动初始化开关,true表示app启动自动初始化升级模块; false不会自动初始化; 开发者如果担心sdk初始化影响app启动速度，可以设置为false，在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
//        Beta.autoCheckUpgrade = true;//true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
//        Beta.upgradeCheckPeriod = 60 * 1000;//设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
//        Beta.initDelay = 1 * 1000;//设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
////        Beta.largeIconId = R.drawable.ic_launcher;//设置通知栏大图标,largeIconId为项目中的图片资源;
////        Beta.smallIconId = R.drawable.ic_launcher;//设置状态栏小图标
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);//设置sd卡的Download为更新资源存储目录
//        Beta.canShowUpgradeActs.add(Main.class);//添加可显示弹窗的Activity,例如，只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗; 如果不设置默认所有activity都可以显示弹窗。
//        Beta.autoDownloadOnWifi = true;//设置Wifi下自动下载,默认false
        if (!DiskFileUtil.is901()) {
            CrashReport.initCrashReport(getApplicationContext(), "6e63a3d9f1", false);
        } else {
            CrashReport.initCrashReport(getApplicationContext(), "0d38972028", false);
            Bugly.init(this, "0d38972028", false);
        }
        mKaraokeController = KaraokeController.getInstance(getApplicationContext());

        FileDownloadLog.NEED_LOG = true;
        FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new OkHttp3Connection.Creator(OkHttpUtil.mSSLOkHttpClient)));
        mDbHelper = new DatabaseHelper(this);
        //Facebook stetho 初始化
        Stetho.initializeWithDefaults(this);
        //greenDao初始化
//        setDatabase();
    }


    public static LanApp getInstance() {
        return mInstance;
    }

    public DatabaseHelper getDataBaseHelper() {
        return mDbHelper;
    }

    public void init() {
        //设置该CrashHandler为程序的默认处理器
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    //    private void setDatabase() {
//
//        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
//        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
//        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
//        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        mHelper = new DaoMaster.DevOpenHelper(this, Common.SongDb_Name, null);
//        db =mHelper.getWritableDatabase();
//        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
//        mDaoMaster = new DaoMaster(db);
//        mDaoSession = mDaoMaster.newSession();
//
//    }
//
//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }
//
//
//    public SQLiteDatabase getDb() {
//        return db;
//    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);


//    // 安装tinker
//    Beta.installTinker();
    }
    public void copyFile(File souce,File desPath) {
        new RxAsyncTask<File,String,Boolean>() {
            @Override
            protected Boolean call(File... files) {
                return FileUtil.copyFile_new(files[0], files[1].getAbsolutePath());
            }

            @Override
            protected void onCompleted() {
                EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT, "");
                super.onCompleted();
            }
        }.execute(souce, desPath);
    }
//
//    private class mCopyFileTask extends AsyncTask<>
}
