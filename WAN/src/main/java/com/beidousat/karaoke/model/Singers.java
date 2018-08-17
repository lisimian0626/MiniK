package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by J Wong on 2015/10/19 16:53.
 */
public class Singers extends BaseList implements Serializable {

    @Expose
    public List<StarInfo> list;

    @Expose
    public String NextWrod;
}
