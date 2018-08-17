package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by J Wong on 2015/11/3 17:30.
 */
public class Topics extends BaseList implements Serializable {

    @Expose
    public List<Topic> list;

}
