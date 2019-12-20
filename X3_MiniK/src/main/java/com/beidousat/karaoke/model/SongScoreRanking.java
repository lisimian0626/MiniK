package com.beidousat.karaoke.model;

import android.util.Log;

import com.beidousat.libbns.util.Logger;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/10/10 11:13.
 */
public class SongScoreRanking {

    private String Tag = "SongScoreRanking";

    public String mScorePercent = "";

    private Song Songinfo;//记录当前歌曲信息
    private int SongScore;//记录当前歌曲得分

    @Expose
    @SerializedName("percentage")
    public String percentage = "";

    private static SongScoreRanking save_getInstance;

    public static SongScoreRanking getInstance() {
        if (save_getInstance == null)
            save_getInstance = new SongScoreRanking();
        return save_getInstance;
    }

    public void setScorePercent(String val) {
        val = val.replace("\"", "");
        boolean strIsIntResult = val.matches("-?[0-9]+.*[0-9]*");
        if (!strIsIntResult) val = "0";
        mScorePercent = String.valueOf(Math.round(Float.parseFloat(val) * 100));
    }

    public String getScorePercent() {
        return mScorePercent + "%";
    }

    public void setPlaySonginfo(Song song, int score) {
        Songinfo = song;
        SongScore = score;
    }

    public Song getSong(){
        return Songinfo;
    }

    public int getSongScore(){
        return SongScore;
    }

    public String getPercentage() {
        return percentage;
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
