package com.beidousat.libbns.model;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2017/5/15.
 */

public class ServerConfig implements Serializable {

    @Expose
    private String store_web;

    @Expose
    private String ad_web;

    @Expose
    private String store_ip;

    @Expose
    private int store_port;

    @Expose
    private String kbox_ip;

    private String vod_server;

    private String vod_url;

    private String download_server;


    public String getStore_web() {
        return store_web;
    }

    public String getAd_web() {

        return ad_web + "index.php/";
    }

    public String getStore_ip() {
        return store_ip;
    }

    public int getStore_port() {
        return store_port;
    }


    public String getKbox_ip() {
        return kbox_ip;
    }

    public void setStore_web(String store_web) {
        this.store_web = store_web;
    }

    public void setAd_web(String ad_web) {
        this.ad_web = ad_web;
    }

    public void setStore_ip(String store_ip) {
        this.store_ip = store_ip;
    }

    public void setStore_port(int store_port) {
        this.store_port = store_port;
    }

    public void setKbox_ip(String kbox_ip) {
        this.kbox_ip = kbox_ip;
    }

    public String getVod_server() {
        return vod_server;
    }

    public void setVod_server(String vod_server) {
        this.vod_server = vod_server;
    }


    public String getVod_url() {
        return getVod_server() + "?";
    }

    public String getDownload_server() {
        return download_server;
    }

    public void setDownload_server(String download_server) {
        this.download_server = download_server;
    }
}
