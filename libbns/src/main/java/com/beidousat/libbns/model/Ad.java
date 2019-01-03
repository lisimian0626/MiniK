package com.beidousat.libbns.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/12/11 18:00.
 */
public class Ad implements Serializable {

    @Expose
    public String ID;

    @Expose
    public String ADContent;

    @Expose
    public String ADMovie;

    @Expose
    public String[] ADPics;

    public String brand;

    public String ADPosition;

    public String SongListID;

    public String DownLoadUrl;
    /**
     * Banner 大图
     */
    public String ADContent1;


    public int IsChanged;


    public int Type;
}
