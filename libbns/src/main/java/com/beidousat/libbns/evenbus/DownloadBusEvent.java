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
    public String songName;
    public static DownloadBusEvent getEvent(int id, String url, String path,String songName, float percent) {
        return new DownloadBusEvent(id, url, path, songName,percent);
    }

    public static DownloadBusEvent getEvent(int id, String url, String path, String msg) {
        return new DownloadBusEvent(id, url, path, msg);
    }

    private DownloadBusEvent() {

    }

    private DownloadBusEvent(int id, String url, String path,String SongName,float percent) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.songName=SongName;
        this.percent = percent;
    }

    private DownloadBusEvent(int id, String url, String path, String msg) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.msg = msg;
    }
}
