package com.beidousat.libbns.util;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.ServerConfigData;

import java.io.File;

/**
 * Created by J Wong on 2015/10/20 09:57.
 */
public class ServerFileUtil {


    private final static String TAG = ServerFileUtil.class.getSimpleName();

    public static String getScoreNoteUrl(String songFilePath) {
        if (TextUtils.isEmpty(songFilePath)) {
            return null;
        }
        String url = convertHttps2Http(ServerConfigData.getInstance().getServerConfig().getVod_file() + "data/grade/" + getFileName(songFilePath) + ".txt");
        Log.e("test","getScoreNoteUrl:"+url);
        return url;
    }

    public static String getScoreNote2Url(String songFilePath) {
        if (TextUtils.isEmpty(songFilePath)) {
            return null;
        }
        String url = convertHttps2Http(ServerConfigData.getInstance().getServerConfig().getVod_file() + "data/grade/" + getFileName(songFilePath) + ".sec.txt");
        Log.e("test","getScoreNote2Url:"+url);
        return url;
    }


//    public static Uri getSingerImageUrl(String file) {
//        if (TextUtils.isEmpty(file)) {
//            return null;
//        }
//        String url = convertHttps2Http(file.startsWith("http://") || file.startsWith("https://") ? file : BnsConfig.DOMAIN_FILE + "data/Img/SingerImg/" + file);
//        return Uri.parse(url);
//    }
//
//
//    private static Uri getSingerThumbnailUrl(String file) {
//        if (TextUtils.isEmpty(file)) {
//            return null;
//        }
//        File dir = KaraokeSdHelper.getSingerImgDir();
//        File fileImage = new File(dir, file);
//        if (fileImage.exists()) {
//            return Uri.fromFile(fileImage);
//        }
//        String url = convertHttps2Http(file.startsWith("http://") || file.startsWith("https://") ? file : BnsConfig.DOMAIN_FILE + "data/Img/SingerImg150/" + file);
//        return Uri.parse(url);
//    }

    public static Uri getImageUrl(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String url = convertHttps2Http(filePath.startsWith("http://") || filePath.startsWith("https://") ? filePath : ServerConfigData.getInstance().getServerConfig().getVod_file() + filePath);
        Log.e("test","getImageUrl:"+url);
        return Uri.parse(url);
    }

    public static String getFileUrl(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String f;
        if(ServerConfigData.getInstance().getServerConfig()==null){
            f=filePath;
        }else{
            f = (filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("udp://")) ? filePath : (ServerConfigData.getInstance().getServerConfig().getVod_file() + filePath);
        }

//        Log.e("test","getFileUrl:"+convertHttps2Http(f));
        return convertHttps2Http(f);
    }
    public static String getPreviewUrl(String filePath){
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String f = (filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("udp://")) ? filePath : (ServerConfigData.getInstance().getServerConfig().getVod_file() + filePath);
        String toHttp = ServerConfigData.getInstance().getServerConfig().getVod_file().replace("https://","http://");
        toHttp = toHttp.substring(0, toHttp.length() - 1) + ":2800/";
        return f.replace(ServerConfigData.getInstance().getServerConfig().getVod_file(), toHttp);
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

//    private static File getScoreNote(String songFilePath) {
//        String noteFileName = ServerFileUtil.getFileName(songFilePath) + ".txt";
//        File fileNote = new File(KaraokeSdHelper.getNote(), noteFileName);
//        return fileNote;
//    }
//
//    private static File getScoreNoteSec(String songFilePath) {
//        String note2 = ServerFileUtil.getFileName(songFilePath) + ".sec.txt";
//        File fileNote2 = new File(KaraokeSdHelper.getNote(), note2);
//        return fileNote2;
//    }


    public static String convertHttps2Http(String https) {
        if(ServerConfigData.getInstance().getServerConfig()==null){
            return "";
        }
        String toHttp = ServerConfigData.getInstance().getServerConfig().getVod_file();
//        if(Common.isEn){
//            toHttp = toHttp.substring(0, toHttp.length() - 1);
//        }else{
//            toHttp = toHttp.substring(0, toHttp.length() - 1) + ":2800/";
//        }
        return https.replace(ServerConfigData.getInstance().getServerConfig().getVod_file(), toHttp);
    }

}
