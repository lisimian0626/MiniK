package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/11/3 17:26.
 */
public class Movie implements Serializable {

    @Expose
    public String ID;

    @Expose
    public String MovieCode;

    @Expose
    public String SimpName;

    @Expose
    public String Img;

    @Expose
    public String MovieType;

    @Expose
    public String score;


}