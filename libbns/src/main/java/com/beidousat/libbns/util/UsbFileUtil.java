package com.beidousat.libbns.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;



/**
 * Created by J Wong on 2017/5/15.
 * <p>
 * U盘入库
 */
public class UsbFileUtil {

    public static boolean isUsbExitBoxCode() {
        File file = new File(DiskFileUtil.is901() ? DiskFileUtil.USB_PATH_901 : DiskFileUtil.USB_PATH, "BNS_KBox");
        if (file.exists()) {
            return true;
        }
        return false;
    }
    public static boolean isUsbExitBoxKey() {
        File file = new File(DiskFileUtil.is901() ? DiskFileUtil.USB_PATH_901 : DiskFileUtil.USB_PATH, "resetpassword");
        if (file.exists()) {
            return true;
        }
        return false;
    }
    public static File isUsbExitUpdataApp() {
        File file = new File(DiskFileUtil.is901() ? DiskFileUtil.USB_PATH_901 : DiskFileUtil.USB_PATH, "appupdate");
        return file;
    }

    public static String readKBoxCode() {
        BufferedReader br = null;
        String ret = "";
        try {
            StringBuilder sb = new StringBuilder();
            String s = "";
            File file = new File(DiskFileUtil.is901() ? DiskFileUtil.USB_PATH_901 : DiskFileUtil.USB_PATH, "BNS_KBox");
            br = new BufferedReader(new FileReader(file));
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            ret = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
        return ret;
    }

}
