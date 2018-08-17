package com.beidousat.karaoke.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/11/6 09:10.
 */
public class PlayerStatus implements Serializable {

    /**
     * 评分模式：0：关闭  1：娱乐模式  2：专业模式
     */
    public int scoreMode = 1;

    public boolean isPlaying = true;

//    public boolean isAccom = true;
    /**
     * 0:伴唱 1：原唱 2：保持原唱
     */
    public boolean originOn;

//    public int micVol = 50;
//
//    public int musicVol = 50;

    public int tone;

    public boolean isMute;

    /**
     * 0:自动 1：明亮 ；2：柔和 ；3：抒情 4：动感
     */
    public int lightMode = 0;

//    public boolean isServing;

    public boolean isLightOn = true;

    /***
     * 0:广告：1：歌曲 2:电影 3:直播 4:贴片,5:转场
     */
    public int playingType;


    public int volMusic;


    public int serviceMode = -1;


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
