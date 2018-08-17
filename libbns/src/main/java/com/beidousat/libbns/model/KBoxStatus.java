package com.beidousat.libbns.model;

/**
 * Created by J Wong on 2017/5/11.
 */

public class KBoxStatus {

    public int status;

    public int code;


    public String msg;


    public KBoxStatus(int status, int code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

}
