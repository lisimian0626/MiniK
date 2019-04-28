package com.beidousat.libbns.util;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * Created by J Wong on 2016/11/21.
 */

public class FileUtil {
    public static final String APP_ROOT_DIR = "/MiniK/";
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

    public void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    public static void copyFile(File source, File target){
        if(target.length()>0)
            return;
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
    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int CopySdcardFile(String fromFile, String toFile)
    {

        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }


    /**
     * 根据文件路径拷贝文件
     * @param src 源文件
     * @param destPath 目标文件路径
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile_new(File src, String destPath) {
        boolean result = false;
        if ((src == null) || (destPath== null)) {
            return result;
        }
        File dest= new File(destPath);
        if (dest!= null && dest.exists()) {
            dest.delete(); // delete file
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
            Logger.d("FileUtil","deleteFile:"+file.getAbsolutePath());
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
    public static String getSongPath(String savePath) {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR+savePath;
        return filepath;
    }
    public static File getSongDir(String savePath) {
        String filepath = Environment.getExternalStorageDirectory()+APP_ROOT_DIR+savePath;
        try {
            File file = new File(filepath);
            if(file.exists()){
                return file;
            }else{
                file.getParentFile().mkdirs();
                file.createNewFile();
                return file;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static File getSongSaveDir(String savePath) {
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
    public static String getDeleteSongSavePath(String savePath) {
        try {
            int indexOf = savePath.indexOf("data/");
            String path = savePath.substring(indexOf, savePath.length());
            return path;
        }catch (Exception e){
            e.printStackTrace();
            return savePath;
        }
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
