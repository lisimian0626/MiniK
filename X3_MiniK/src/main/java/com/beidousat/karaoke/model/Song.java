package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/10/10 11:13.
 */
public class Song extends SongSimple implements Serializable {

    @Expose
    public String SingerID;

    @Expose
    public String SongFilePath;

    @Expose
    public int Volume;

    @Expose
    public int AudioTrack;


    @Expose
    public int IsAdSong;

    @Expose
    public String ADID;

    @Expose
    public String PreviewPath;

    @Expose
    public String download_url;

    public int Hot;

    /***
     * 0:song  1:movie 2:live
     */
    public int playType;

    public int score;

    public int IsClear;

    public boolean isPrior;

    public String downloadErro;

    private boolean isAD;

    public boolean isAD() {
        return isAD;
    }

    public void setAD(boolean AD) {
        isAD = AD;
    }

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
