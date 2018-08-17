package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/10/15 14:59.
 */
public class StarInfo implements Serializable {
    @Expose
    public String ID;

    @Expose
    public String SimpName;

    @Expose
    public String Img;

//    @Expose
//    public String Description;

//    @Expose
//    public String SingerCode;

}
