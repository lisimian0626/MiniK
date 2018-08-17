package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/20.
 */

public class PaySevice {

    /**
     * qrcode_str : https://qr.alipay.com/bax04772xbcfub5awoc06012
     */
    private String qrcode_str;

    public String getQrcode_str() {
        return qrcode_str;
    }

    public void setQrcode_str(String qrcode_str) {
        this.qrcode_str = qrcode_str;
    }
}
