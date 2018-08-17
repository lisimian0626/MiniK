package com.beidousat.libbns.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2017/7/31.
 */

public class BaseUpload implements Serializable {

    @Expose
    public String error;

    @Expose
    public String message;

    @Expose
    public JsonElement data;

}