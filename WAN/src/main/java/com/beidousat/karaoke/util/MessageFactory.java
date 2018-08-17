package com.beidousat.karaoke.util;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * author: Hanson
 * date:   2016/9/5
 * describe:
 */
public class MessageFactory {
    public static String translate(Throwable e) {
        String result = "未知错误";
        if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
            result = "网络超时，请检查网络是否可用。";
        }  else if (e instanceof UnknownHostException) {
            result = "网络不可用，请检查网络是否已开启！";
        }
        return result;
    }
}
