package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/10/20 10:10.
 */
public class BaseList implements Serializable {


    @Expose
    public int total;

    @Expose
    public int nowPage;

    @Expose
    public int totalPages;
}
