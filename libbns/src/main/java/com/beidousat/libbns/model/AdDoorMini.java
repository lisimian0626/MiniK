package com.beidousat.libbns.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by J Wong on 2017/7/10.
 */

public class AdDoorMini implements Serializable {

    @Expose
    public String Logo;

    @Expose
    public String Img;

    @Expose
    public List<Ad> List;

}
