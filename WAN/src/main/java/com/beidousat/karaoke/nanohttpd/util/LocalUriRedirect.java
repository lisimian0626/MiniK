package com.beidousat.karaoke.nanohttpd.util;

import com.beidousat.karaoke.player.proxy.Config;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * author: Hanson
 * date:   2017/5/3
 * describe:
 */

public class LocalUriRedirect {
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String LOCAL_PORT = "2800";

    public static String getLocalURL(String url) {
        // ----获取对应本地代理服务器的链接----//
        String localUrl = "";
        URI originalURI = URI.create(url);
        String remoteHost = originalURI.getHost();

        if (originalURI.getPort() != -1) {
            localUrl = url.replace(remoteHost + ":" + originalURI.getPort(), LOCAL_HOST + ":" + LOCAL_PORT);
        } else {// URL不带Port
            localUrl = url.replace(remoteHost, LOCAL_HOST + ":" + LOCAL_PORT);
        }
        return localUrl;
    }
}
