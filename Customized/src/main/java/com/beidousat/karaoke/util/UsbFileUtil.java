package com.beidousat.karaoke.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by J Wong on 2017/5/15.
 */

public class UsbFileUtil {

    //硬盘根目录
    private final static String USB_PATH = "/mnt/usb_storage/USB_DISK0/udisk1/";

    public static boolean isUsbExitBoxCode() {
        File file = new File(USB_PATH, "BNS_KBox");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static String readKBoxCode() {
        BufferedReader br = null;
        String ret = "";
        try {
            StringBuilder sb = new StringBuilder();
            String s = "";
            File file = new File(USB_PATH, "BNS_KBox");
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
