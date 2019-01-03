package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BasePlay {

    /**
     * save_path : data/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     * song_name : 公播0
     * object_name : data2_to_cube/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     * download_url : http://media.imtbox.com/data/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     */

    private String save_path;
    private String song_name;
    private String object_name;
    private String download_url;

    public static List<BasePlay> arrayBasePlayFromData(String str) {

        Type listType = new TypeToken<ArrayList<BasePlay>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}
