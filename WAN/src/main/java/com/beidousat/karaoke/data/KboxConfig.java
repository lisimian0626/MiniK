package com.beidousat.karaoke.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by J Wong on 2017/5/15.
 */

public class KboxConfig implements Serializable {
    @SerializedName("vod_thumbnail_server")
    private String kbox_ip;
    @SerializedName("ad_server")
    private String ad_web;
    @SerializedName("socket_server")
    private String store_ip_port;
    @SerializedName("vod_http_server")
    private String  vod_server;

    public String getKbox_ip() {
        return kbox_ip;
    }

    public void setKbox_ip(String kbox_ip) {
        this.kbox_ip = kbox_ip;
    }

    public String getAd_web() {
        return ad_web;
    }

    public void setAd_web(String ad_web) {
        this.ad_web = ad_web;
    }

    public String getStore_ip_port() {
        return store_ip_port;
    }

    public void setStore_ip_port(String store_ip_port) {
        this.store_ip_port = store_ip_port;
    }

    public String getVod_server() {
        return vod_server;
    }

    public void setVod_server(String vod_server) {
        this.vod_server = vod_server;
    }
}
