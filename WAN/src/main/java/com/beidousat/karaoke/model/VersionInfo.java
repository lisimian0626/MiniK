package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by J Wong on 2016/11/8.
 */

public class VersionInfo implements Serializable {
    @Expose
    public String SongTotal;
    @Expose
    public String NewTotal;
    @Expose
    public String ZGXGS;
    @Expose
    public String ZMHS;
}
