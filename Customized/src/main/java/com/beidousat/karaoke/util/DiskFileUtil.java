package com.beidousat.karaoke.util;

import android.net.Uri;
import android.os.StatFs;
import android.text.TextUtils;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.db.DatabaseHelper;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by J Wong on 2017/5/3.
 * 硬盘文件
 */

public class DiskFileUtil {

    private final static String TAG = "DiskFileUtil";

    //硬盘根目录
    private final static String USB_PATH = "/mnt/usb_storage/USB_DISK1/udisk1/";

    //歌星大图目录
    private final static String SINGER_IMG_DIR = USB_PATH + "data/Img/SingerImg/";

    //歌星缩略图目录
    private final static String SINGER_THUNB_IMG_DIR = USB_PATH + "data/Img/SingerImg150/";

    //歌星缩略图目录
    private final static String GRADE_DIR = USB_PATH + "data/grade/";


    /**
     * 根据URL获取硬盘中文件
     *
     * @param url
     * @return
     */
    public static File getDiskFileByUrl(String url) {
        Logger.d(TAG, "getDiskFileByUrl url==" + url);
        try {
            int indexOf = url.indexOf("data/");
            String path = url.substring(indexOf, url.length());
            Logger.d(TAG, "getDiskFileByUrl path==" + path);
            File file = new File(USB_PATH + path);
            Logger.d(TAG, "getDiskFileByUrl disk file==" + file.getAbsolutePath());
            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDiskpathToServerPath(String diskPath) {
        if (TextUtils.isEmpty(diskPath)) {
            return null;
        }
        String serverurl = null;
        if (diskPath.contains(USB_PATH)) {
            serverurl = diskPath.replace(USB_PATH, "");
        }

        return serverurl;
    }

    /**
     * 获取文件在disk上的全路径
     *
     * @param path 服务器返回的相对路径
     * @return
     */
    public static String getFileSavedPath(String path) {
        if (TextUtils.isEmpty(path))
            return null;

        return USB_PATH + path;
    }

    /**
     * @param fileName
     * @return
     */
    public static Uri getSingerThumbnailImg(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(SINGER_THUNB_IMG_DIR, fileName);
        if (file != null && file.exists()) {
            return Uri.fromFile(file);
        }
        String url = ServerFileUtil.convertHttps2Http(fileName.startsWith("http://") || fileName.startsWith("https://") ? fileName : BnsConfig.DOMAIN_FILE + "data/Img/SingerImg150/" + fileName);
        return Uri.parse(url);
    }

    /**
     * @param fileName
     * @return
     */
    public static Uri getSingerImg(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(SINGER_IMG_DIR, fileName);
        if (file != null && file.exists()) {
            return Uri.fromFile(file);
        }
        String url = ServerFileUtil.convertHttps2Http(fileName.startsWith("http://") || fileName.startsWith("https://") ? fileName : BnsConfig.DOMAIN_FILE + "data/Img/SingerImg/" + fileName);
        return Uri.parse(url);
    }

    public static File getGradeDir() {
        File file = new File(GRADE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getScoreNote(String songFilePath) {
        String noteFileName = ServerFileUtil.getFileName(songFilePath) + ".txt";
        File fileNote = new File(getGradeDir(), noteFileName);
        return fileNote;
    }

    public static File getScoreNoteSec(String songFilePath) {
        String note2 = ServerFileUtil.getFileName(songFilePath) + ".sec.txt";
        File fileNote2 = new File(getGradeDir(), note2);
        return fileNote2;
    }

    /**
     * 根据http url 转为硬盘文件路径
     *
     * @param httpPath
     * @return
     */
    public static String getDiskPathByHttpPath(String httpPath) {
        if (TextUtils.isEmpty(httpPath)) {
            return "";
        }
        try {
            int indexOf = httpPath.indexOf("data/");
            String path = httpPath.substring(indexOf, httpPath.length());
            Logger.d(TAG, "getDiskFileByUrl path==" + path);
            return (USB_PATH + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否连接硬盘
     *
     * @return
     */
    public static boolean isDiskExit() {
        File file = new File(USB_PATH);
        return file.exists();
    }

    /**
     * 硬盘剩余空间
     *
     * @return 单位MB
     */
    public static long getDiskAvailableSpace() {
        if (isDiskExit()) {
            StatFs sf = new StatFs(USB_PATH);
            long blockSize = sf.getBlockSizeLong();
//            long blockCount = sf.getBlockCountLong();
            long availCount = sf.getAvailableBlocksLong();
            long availMB = availCount * blockSize / (1024 * 1024);
            Logger.d(TAG, "剩余空间:" + availMB + "MB");
            return availMB;
        }
        return 0;
    }
}
