package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2016/11/8.
 */

public class UpdateLog implements Serializable {

    @Expose
    public String Date;

    @Expose
    public String Content;

}
