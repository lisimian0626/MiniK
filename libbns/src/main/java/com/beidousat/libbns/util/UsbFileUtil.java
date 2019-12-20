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

    //U盘根目录
    private final static String USB_PATH = "/mnt/usb_storage/USB_DISK0/udisk1/";

    private final static String USB_PATH_901 = "/mnt/usb_storage/USB_DISK0/C/";

    public static String DevPath() {
        return BnsConfig.is901() ? USB_PATH_901 : USB_PATH;
    }

    public static boolean isUsbExitBoxCode() {
        File file = new File(DevPath(), "BNS_KBox");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 是否接入U盘，并且U盘中带有重量密码的文件
     */
    public static boolean isUsbExitBoxKey() {
        File file = new File(DevPath(), "resetpassword");
        if (file.exists()) {
            return true;
        }
        return false;
    }


    public static File isUsbExitUpdataApp() {
        File file = new File(DevPath(), "appupdate");
        return file;
    }

    /**
     * 入库U盘
     */
    public static String readKBoxCode() {
        BufferedReader br = null;
        String ret = "";
        try {
            StringBuilder sb = new StringBuilder();
            String s = "";
            File file = new File(DevPath(), "BNS_KBox");
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
