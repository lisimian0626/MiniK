package com.beidousat.libbns.util;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by J Wong on 2016/11/21.
 */

public class FileUtil {
    private static final String APP_ROOT_DIR = "/MiniK/";
    public static String readFileContent(String strFilePath) {
        StringBuilder builder = new StringBuilder();
        Log.w("FileUtil", "readFileContent file :" + strFilePath);
        //打开文件
        File file = new File(strFilePath);
        if (file.exists() && file.isFile()) {
            InputStream instream = null;
            InputStreamReader inputreader = null;
            BufferedReader buffreader = null;
            try {
                instream = new FileInputStream(file);
                if (instream != null) {
                    inputreader = new InputStreamReader(instream);
                    buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line);
                    }
                }
            } catch (Exception e) {
                Log.w("FileUtil", "readFileContent ex:" + e.toString());
            } finally {
                if (buffreader != null) {
                    try {
                        buffreader.close();
                    } catch (Exception e) {
                        Log.w("FileUtil", "readFileContent close buffreader ex:" + e.toString());
                    }
                }
                if (inputreader != null) {
                    try {
                        inputreader.close();
                    } catch (Exception e) {
                        Log.w("FileUtil", "readFileContent close inputreader ex:" + e.toString());
                    }
                }
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception e) {
                        Log.w("FileUtil", "readFileContent close instream ex:" + e.toString());
                    }
                }
            }
        }
        return builder.toString();
    }

    public static String getFileMD5String(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            Logger.w("FileUtil", "getFileMD5String ex:" + e.toString());
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 = bigInt.toString(16);
        Logger.d("FileUtil", "getFileMD5String file md5==" + md5);
        return md5;
    }

    public static boolean chmod777File(File file) {
        try {
            file.setExecutable(true);//设置可执行权限
            file.setReadable(true);//设置可读权限
            file.setWritable(true);//设置可写权限
            Process p = Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("KaraokeSdHelper", "su -c chmod 777 SongSecurityKey Exception");
        }
        return false;
    }

    public static boolean chmod777FileSu(File file) {
        try {
            file.setExecutable(true);//设置可执行权限
            file.setReadable(true);//设置可读权限
            file.setWritable(true);//设置可写权限
//            Process p = Runtime.getRuntime().exec("su");
//            p = Runtime.getRuntime().exec("su -c chmod 777 " + file.getAbsolutePath());
//            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c", cmd});
            String cmd = "chmod 777 " + file.getAbsolutePath();
            Logger.d("KaraokeSdHelper", "chmod777FileSu cmd :" + cmd);
            Runtime.getRuntime().exec(new String[]{"/system/xbin/su", "-c", cmd});
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("KaraokeSdHelper", "su -c chmod 777 SongSecurityKey Exception");
        }
        return false;
    }

//    public void copy(File sourceLocation, File targetLocation) throws IOException {
//        if (sourceLocation.isDirectory()) {
//            copyDirectory(sourceLocation, targetLocation);
//        } else {
//            copyFile(sourceLocation, targetLocation);
//        }
//    }
//
//    private void copyDirectory(File source, File target) throws IOException {
//        if (!target.exists()) {
//            target.mkdir();
//        }
//
//        for (String f : source.list()) {
//            copy(new File(source, f), new File(target, f));
//        }
//    }

    public static void copyFile(File source, File target){
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);

            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                deleteFile(item);
            }
        } else {
            file.delete();
        }
    }

    public static File getApkDir() {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR;
        File file = new File(filepath);

        if (!file.exists()) {
            boolean mkdirs=file.mkdirs();
            Logger.d("FileUtil","mkdirs:"+mkdirs);
        }

        return file;
    }
    public static File getSongDir(String savePath) {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR+savePath;
        try {
            File file = new File(filepath);
            if(file.exists()){
                return file;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return null;
    }
    public static File getSongSaveDir(String savePath) {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR+savePath;
        File file = new File(filepath);
        if (!file.exists()) {
            boolean mkdirs=file.mkdir();
            Logger.d("FileUtil","mkdirs:"+mkdirs);
        }
        return file;
    }
    public static String getApkRootPath() {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR;
        File file = new File(filepath);

        if (!file.exists()) {
            boolean mkdirs=file.mkdirs();
            Logger.d("FileUtil","mkdirs:"+mkdirs);
        }

        return filepath;
    }
}
