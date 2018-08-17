package com.beidousat.libbns.util;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by J Wong on 2015/12/7 17:20.
 */
public class KaraokeSdHelper {

    private final static String ROOT = "/BNS/";
    private final static String APK = ROOT + "APK/";
    private final static String CRASH = ROOT + "CRASH/";
    private final static String NOTE = ROOT + "Note/";

    private final static String SINGER_IMAGE = "/SingerImg150";

    private final static String SCREEN_SHOT = ROOT + "ScreenShot/";
    private final static String SCREEN_SHOT_P = ROOT + "Compressor/";

    private final static String OTA = ROOT + "OTA/";
    private final static String SKIN = ROOT + "Skin/";

    private final static String DOOR_MINI = ROOT + "DoorAd/";


    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static final File getSingerImgDir() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + SINGER_IMAGE;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }

    public static final File getRoot() {
        if (!existSDCard())
            return null;
        String root = Environment.getExternalStorageDirectory() + ROOT;
        File file = new File(root);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public static final File getApk() {
        if (!existSDCard())
            return null;
//        String apk = Environment.getExternalStorageDirectory() + APK;

        String apk = "/mnt/sdcard/";
        File file = new File(apk);
        if (!file.exists())
            file.mkdirs();
        return file;
    }


    public static final File getCrash() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + CRASH;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }

    public static File getSdCard() {
        if (!existSDCard())
            return null;
        return Environment.getExternalStorageDirectory();
    }


    public static File getSongSecurityKeyFile() {
//        /mnt/private/SongSecurity.key
//        if (!existSDCard())
//            return null;
        String privateDir = "/data/private/";
        File file = new File(privateDir);
        if (!file.exists()) {
            boolean ret = file.mkdirs();
            Logger.i("KaraokeSdHelper", "privateDir create:" + ret);
        } else {
            Logger.i("KaraokeSdHelper", "privateDir exist");
        }
        File file1 = new File(file, "SongSecurity.key");
//        try {
//            file1.createNewFile();
//            file1.setExecutable(true);//设置可执行权限
//            file1.setReadable(true);//设置可读权限
//            file1.setWritable(true);//设置可写权限
//            Process p = Runtime.getRuntime().exec("chmod 666 " + file1);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return file1;
    }

    public static File getSongSecurityKeyFileFor901() {
        String privateDir = "/data/";
        File file = new File(privateDir);
        if (!file.exists()) {
            boolean ret = file.mkdirs();
            Logger.i("KaraokeSdHelper", "privateDir create getSongSecurityKeyFileFor901:" + ret);
        } else {
            Logger.i("KaraokeSdHelper", "privateDir exist getSongSecurityKeyFileFor901");
        }
        File file1 = new File(file, "bd.key");
        try {
            file1.createNewFile();
            file1.setExecutable(true);//设置可执行权限
            file1.setReadable(true);//设置可读权限
            file1.setWritable(true);//设置可写权限
            FileUtil.chmod777FileSu(file1);
        } catch (Exception ex) {
            Logger.e("KaraokeSdHelper", "su -c chmod 777 SongSecurityKey Exception");
            ex.printStackTrace();
        }
        return file1;
    }

    private static final File getNote() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + NOTE;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }


    public static final File getScreenShotDir() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + SCREEN_SHOT;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }

    public static final File getScreenShotCompressDir() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + SCREEN_SHOT_P;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }


    private static final File getOtaDir() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + OTA;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }

    public static final File getOtaDownloadFile() {
        File file = new File(getOtaDir(), "ota.zip");
        return file;
    }

    public static final File getOtaUpdateFile() {
        File file = new File(getOtaDir(), "update.zip");
        return file;
    }


    public static final File getSkinDir() {
        if (!existSDCard())
            return null;
        String crash = Environment.getExternalStorageDirectory() + SKIN;
        File file = new File(crash);
        if (!file.exists())
            file.mkdirs();

        return file;
    }


    public static File getAdDoorMiniDir() {
        if (!existSDCard())
            return null;
        String door = Environment.getExternalStorageDirectory() + DOOR_MINI;
        File file = new File(door);
        if (!file.exists())
            file.mkdirs();
        return file;
    }


    public static long getSdUsableSpace(Context context) {
        File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
        long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        long totalSpace = sdcard_filedir.getTotalSpace();
        //将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
        String usableSpace_str = Formatter.formatFileSize(context, usableSpace);
        String totalSpace_str = Formatter.formatFileSize(context, totalSpace);
        return usableSpace;
    }
}
