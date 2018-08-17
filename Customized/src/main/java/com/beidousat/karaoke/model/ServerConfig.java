package com.beidousat.karaoke.model;

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
    private int kbox_port;

    @Expose
    private String kbox_ip;


    public String getStore_web() {
        return store_web;
    }

    public String getAd_web() {
//        ad_web = "http://192.168.1.99/";
//        return ad_web;
        return ad_web + "index.php/";
    }

    public String getStore_ip() {
        return store_ip;
    }

    public int getKbox_port() {
        return kbox_port;
    }


    public String getKbox_ip() {
        return kbox_ip;
    }


}
