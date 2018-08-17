package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2016/6/30.
 */
public class SongSimple implements Serializable {

    @Expose
    public String ID;
    @Expose
    public String SimpName;

    @Expose
    public String SongVersion;

    @Expose
    public String IsGradeLib;

    @Expose
    public String SingerName;

    public String RecordFile;

    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
