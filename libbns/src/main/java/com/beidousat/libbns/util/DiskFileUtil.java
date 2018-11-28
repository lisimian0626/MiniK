package com.beidousat.libbns.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.libbns.model.ServerConfigData;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by J Wong on 2017/5/3.
 * 硬盘文件
 */

public class DiskFileUtil {

    private final static String TAG = "DiskFileUtil";

    //晨芯硬盘根目录
    public final static String USB_PATH = "/mnt/usb_storage/USB_DISK1/udisk1/";

    //音诺恒硬盘根目录
    public final static String USB_PATH_901 = "/mnt/usb_storage/SATA/C/";

    //歌星大图目录
    private final static String SINGER_IMG_DIR = (is901()?USB_PATH_901:USB_PATH) + "data/Img/SingerImg/";


    //歌星缩略图目录
    private final static String SINGER_THUNB_IMG_DIR = (is901()?USB_PATH_901:USB_PATH) + "data/Img/SingerImg150/";

    //评分文件
    private final static String GRADE_DIR = (is901()?USB_PATH_901:USB_PATH) + "data/grade/";


    public static boolean is901() {
        String model = android.os.Build.MODEL;
        return "rk3288_box".equalsIgnoreCase(model);
    }
    /**
     * 根据URL获取硬盘中文件
     *
     * @param url
     * @return
     */
    public static File getDiskFileByUrl(String url) {
        Logger.d(TAG, TAG+"     "+"getDiskFileByUrl url==" + url);
        try {
            int indexOf = url.indexOf("data/");
            String path = url.substring(indexOf, url.length());
            Logger.d(TAG, TAG+"     "+"getDiskFileByUrl path==" + path);

            File file = new File((DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH) + path);
            Logger.d(TAG, TAG+"     "+"getDiskFileByUrl disk file==" + file.getAbsolutePath());

            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
      //获取文件相对路径
    public static String convertDiskpathToServerPath(String diskPath) {
        if (TextUtils.isEmpty(diskPath)) {
            return null;
        }
        String serverurl = null;
        if (diskPath.contains(DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH)) {
            serverurl = diskPath.replace(DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH, "");
        }
        return serverurl;
    }

    public static boolean hasDiskStorage() {
        File disk = new File(DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH);
        return disk.exists();
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
        String url="";
        try {
            int indexOf = path.indexOf("data/");
            url = path.substring(indexOf, path.length());
        }catch (Exception e){
            e.printStackTrace();
            url="";
        }

        return (DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH) + url;
    }

    public final static String getUsbDiskPath() {
        return  DiskFileUtil.is901() ? USB_PATH_901 : USB_PATH;
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
            Logger.d(TAG,TAG+"         "+"getSingerThumbnailImg:"+file.getAbsolutePath());
            return Uri.fromFile(file);
        }
        String url = (fileName.startsWith("http://") || fileName.startsWith("https://") ? fileName : ServerConfigData.getInstance().getServerConfig().getVod_server() + "data/Img/SingerImg150/" + fileName);
        Logger.d(TAG,TAG+"         "+"getSingerThumbnailImg:"+url);
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
            Logger.d(TAG,TAG+"         "+"getSingerImg:"+file.getAbsolutePath());
            return Uri.fromFile(file);
        }
        String url = (fileName.startsWith("http://") || fileName.startsWith("https://") ? fileName : ServerConfigData.getInstance().getServerConfig().getVod_server() + "data/Img/SingerImg/" + fileName);
        Logger.d(TAG,TAG+"         "+"getSingerImg:"+url);
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
        Logger.d(TAG,TAG+"         "+"getScoreNote:"+fileNote.getAbsolutePath());
        return fileNote;
    }

    public static File getScoreNoteSec(String songFilePath) {
        String note2 = ServerFileUtil.getFileName(songFilePath) + ".sec.txt";
        File fileNote2 = new File(getGradeDir(), note2);
        Logger.d(TAG,TAG+"         "+"getScoreNoteSec:"+fileNote2.getAbsolutePath());
        return fileNote2;
    }


    public static File getSdcardFileByUrl(String url) {
        Logger.d(TAG, "getSdcardFileByUrl url==" + url);
        try {
            int indexOf = url.indexOf("data/");
            String path = url.substring(indexOf, url.length());
            Logger.d(TAG, "getSdcardFileByUrl path==" + path);
            File sdCard = KaraokeSdHelper.getSdCard();
            File file = new File(sdCard, path);
            Logger.d(TAG, "getSdcardFileByUrl disk file==" + file.getAbsolutePath());
            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            Logger.d(TAG, TAG+"   "+"getDiskPathByHttpPath path==" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
