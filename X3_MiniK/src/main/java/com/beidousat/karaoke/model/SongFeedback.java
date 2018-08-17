package com.beidousat.karaoke.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by admin on 2016/7/27.
 */
public class SongFeedback implements Serializable {

    public String SongName;

    public String SingerName;

    public SongFeedback(String songName, String singerName) {
        this.SongName = songName;
        this.SingerName = singerName;
    }

    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        try {
            Gson gson = new Gson();
            return gson.toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
