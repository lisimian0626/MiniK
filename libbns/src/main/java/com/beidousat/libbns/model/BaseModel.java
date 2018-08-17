package com.beidousat.libbns.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2015/10/9 18:44.
 */
public class BaseModel implements Serializable {

    @Expose
    public int status;

    @Expose
    public String info;

    @Expose
    public JsonElement data;

}
