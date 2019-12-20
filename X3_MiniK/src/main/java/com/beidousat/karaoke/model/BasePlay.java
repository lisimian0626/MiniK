package com.beidousat.karaoke.model;

import android.content.Context;

import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BasePlay {

    /**
     * save_path : data/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     * song_name : 公播0
     * object_name : data2_to_cube/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     * download_url : http://media.imtbox.com/data/song/yyzx/76096824-0a14-4f56-b49e-871660109697.mp4
     */
    private String type;    //url,mp4
    private String save_path;
    private String song_name;
    private String object_name;
    private String download_url;

    public static int index = -1;//当前播放的歌曲序号

    private static String Tag = "BasePlay";


    /**
     * 设置公播的播放方案
     */
    public static void setPlayPlan(Context mContext, String playplan) {
        PreferenceUtil.setString(mContext, "PlayPlan", playplan);
    }

    /**
     * 读取公播的播放方案
     */
    public static String getPlayPlan(Context mContext) {
        return PreferenceUtil.getString(mContext, "PlayPlan", "random");//默认为随机的
    }


    /**
     * 设置公播中中固定歌曲
     */
    public static void setSingle_index(Context mContext, int single) {
        PreferenceUtil.setInt(mContext, "Single_index", single);
    }

    /**
     * 设置公播中中固定歌曲
     */
    public static int getSingle_index(Context mContext, int max_size) {
        int Single_index = PreferenceUtil.getInt(mContext, "Single_index", 0);
        if (max_size < Single_index) Single_index = max_size;
        return Single_index;
    }

    /**
     * 保存公播放信息
     */
    public static void setBasePlay(Context mContext, String basePlay) {
        PreferenceUtil.setString(mContext, "BasePlaySong", basePlay);
    }

    /**
     * 读取公播放信息
     */
    public static List<BasePlay> getBasePlay(Context mContext) {
        String BasePlaySong = PreferenceUtil.getString(mContext, "BasePlaySong", null);
        if (BasePlaySong == null) {
            return null;
        }
        return arrayBasePlayFromData(BasePlaySong);
    }


    /**
     * 取得当前可以播放的歌曲序号
     */
    public static int getCycleNum(Context mContext) {
        List<BasePlay> basePlaylist = getBasePlay(mContext);
        int max_size = basePlaylist.size();
        if (index < max_size - 1) {
            index++;
        } else {
            index = 0;
        }
        Logger.d("Main", "index:" + index);
        return index;
    }

    /**
     * 取得随机的合法序号
     */
    public static int getRandIndex(Context mContext) {
        List<BasePlay> basePlaylist = getBasePlay(mContext);
        int max_size = basePlaylist.size();
        if (max_size > 0) {
            Random random = new Random();
            return random.nextInt(max_size);
        }
        return 0;
    }


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePlay basePlay = (BasePlay) o;
        return Objects.equals(save_path, basePlay.save_path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(save_path);
    }
}
