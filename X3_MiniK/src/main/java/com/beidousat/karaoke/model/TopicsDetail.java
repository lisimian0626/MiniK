package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by J Wong on 2015/11/3 17:30.
 */
public class TopicsDetail extends BaseList implements Serializable {
    @Expose
    public String TopicsName;
    @Expose
    public String Img;
    @Expose
    public int Hot;
    @Expose
    public List<Song> SongList;

    @Expose
    public String RecommendImg;

    @Expose
    public String Brand;
}
