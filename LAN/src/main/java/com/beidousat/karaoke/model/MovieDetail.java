package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/11/3 17:27.
 */
public class MovieDetail extends Movie implements Serializable {

    @Expose
    public String Director;

    @Expose
    public String Starring;

    @Expose
    public String Description;

    @Expose
    public String EnName;

    @Expose
    public String ScreenedDate;

    @Expose
    public long Duration;

    @Expose
    public String RegionName;

    @Expose
    public String MovieFilePath;

}
