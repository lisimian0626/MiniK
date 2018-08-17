package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2017/1/18.
 */

public class SkinInfo implements Serializable {

    @Expose
    public String ID;
    @Expose
    public int SkinType;
    @Expose
    public String FilePath;
    @Expose
    public String SkinName;
    @Expose
    public String PreviewImg;
}