package com.beidousat.libbns.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by J Wong on 2017/5/9.
 */

public class Md5Util {

    public static String encode(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(string.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String s = Integer.toHexString(0xff & bytes[i]);
                if (s.length() == 1) {
                    sb.append("0" + s);
                } else {
                    sb.append(s);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return string;
    }
}
