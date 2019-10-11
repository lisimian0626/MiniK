package com.beidousat.libbns.evenbus;

/**
 * author: Hanson
 * date:   2017/5/8
 * describe:
 */

public class DownloadBusEvent extends BusEvent {
    public String url;
    public String path;
    public float percent;
    public String msg;
    public static DownloadBusEvent getEvent(int id, String url, String path, float percent) {
        return new DownloadBusEvent(id, url, path,percent);
    }

    public static DownloadBusEvent getEvent(int id, String url, String path, String msg) {
        return new DownloadBusEvent(id, url, path, msg);
    }

    private DownloadBusEvent() {

    }

    private DownloadBusEvent(int id, String url, String path,float percent) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.percent = percent;
    }

    private DownloadBusEvent(int id, String url, String path, String msg) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.msg = msg;
    }
}
