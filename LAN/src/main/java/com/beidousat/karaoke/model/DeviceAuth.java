package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

/**
 * Created by J Wong on 2015/12/28 15:17.
 */
public class DeviceAuth {

    @Expose
    public String KTVNetCode;

    @Expose
    public String Random;

    @Expose
    public String AuthCheckCode;

}
