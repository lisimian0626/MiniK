package com.beidousat.libbns.util;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.beidousat.libbns.util.BnsConfig;

import com.beidousat.libbns.model.ServerConfigData;

import java.io.File;

/**
 * Created by J Wong on 2015/10/20 09:57.
 */
public class ServerFileUtil {


    private final static String TAG = "ServerFileUtil";

    private static String downloadserver() {
        String url = null;
        if (ServerConfigData.getInstance().getServerConfig() != null && !TextUtils.isEmpty(ServerConfigData.getInstance().getServerConfig().getDownload_server())) {
            url = ServerConfigData.getInstance().getServerConfig().getDownload_server();
        }
        return url;
    }

    public static String SongSecurity(String path) {
        String px_url = downloadserver();
        if (!TextUtils.isEmpty(path) || px_url == null) return null;
        String url = px_url + path;
        Logger.d(TAG, TAG + "   " + "SongSecurity:" + url);
        return url;
    }

    public static String getScoreNoteUrl(String songFilePath) {
        String px_url = downloadserver();
        if (TextUtils.isEmpty(songFilePath) || px_url == null) return null;
        String url = px_url + "data/grade/" + getFileName(songFilePath) + ".txt";
        Logger.d(TAG, TAG + "   " + "getScoreNoteUrl:" + url);
        return url;
    }

    public static String getScoreNote2Url(String songFilePath) {
        String px_url = downloadserver();
        if (TextUtils.isEmpty(songFilePath) || px_url == null) return null;
        String url = px_url + "data/grade/" + getFileName(songFilePath) + ".sec.txt";
        Logger.d(TAG, TAG + "   " + "getScoreNote2Url:" + url);
        return url;
    }


    public static Uri getImageUrl(String filePath) {
        String px_url = downloadserver();
        if (TextUtils.isEmpty(filePath) || px_url == null) return null;
        String url = filePath.startsWith("http://") || filePath.startsWith("https://") ? filePath : px_url + filePath;
        Logger.d(TAG, TAG + "      " + "getImageUrl:" + url);
        return Uri.parse(url);
    }

    public static String getFileUrl(String filePath) {
        String px_url = downloadserver();
        if (TextUtils.isEmpty(filePath) || px_url == null) return null;
        String url = filePath.startsWith("http://") || filePath.startsWith("https://") ? filePath : px_url + filePath;
        url = url + ((url.contains("?")) ? "&" : "?") + "dev=" + (BnsConfig.is901() ? "YNH" : "X3");
        Logger.d(TAG, "getFileUrl:" + url);
        return url;
    }

    public static String getPreviewUrl(String filePath) {
        String px_url = downloadserver();
        if (TextUtils.isEmpty(filePath) || px_url == null) return null;
        String url = filePath.startsWith("http://") || filePath.startsWith("https://") ? filePath : px_url + filePath;
        Logger.d(TAG, TAG + "      " + "getPreviewUrl:" + url);
        return url;
    }

    private static String getSdFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String fileName = getFileName(filePath);
        String path = "file://" + Environment.getExternalStorageDirectory() + "/BNS/";
        if (filePath.contains("ad/")) {
            path = path + "ad/" + fileName;
        } else if (filePath.contains("song/")) {
            path = path + "song/" + fileName;
        } else if (filePath.contains("movie/")) {
            path = path + "movie/" + fileName;
        }
        return path;
    }

    private static Uri getSdImgFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String fileName = getFileName(filePath);
        String path = Environment.getExternalStorageDirectory() + "/BNS/Img/";
        if (filePath.contains("AlbumImg/")) {
            path = path + "AlbumImg/" + fileName;
        } else if (filePath.contains("LiveProgram/")) {
            path = path + "LiveProgram/" + fileName;
        } else if (filePath.contains("MovieHomePage/")) {
            path = path + "MovieHomePage/" + fileName;
        } else if (filePath.contains("SingerImg/")) {
            path = path + "SingerImg/" + fileName;
        } else if (filePath.contains("Topics/")) {
            path = path + "Topics/" + fileName;
        }
        return Uri.fromFile(new File(path));
    }

    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        if (url.contains("/")) {
            int lastIndex = url.lastIndexOf("/");
            String fileName = url.substring(lastIndex + 1);
            return fileName;
        } else {
            return url;
        }
    }

    private static String getFileNameExceptSuffix(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        if (url.contains("/")) {
            int lastIndex = url.lastIndexOf("/");
            int index = url.lastIndexOf(".");
            String fileName = url.substring(lastIndex + 1, index);
            return fileName;
        } else {
            return url;
        }
    }

    public static String getImageName(String url) {
        if (TextUtils.isEmpty(url)) return null;
        int pos = url.lastIndexOf("/");
        if (pos < 0) return null;
        return url.substring(pos + 1);
    }

}
