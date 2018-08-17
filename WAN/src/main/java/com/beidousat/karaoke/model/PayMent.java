package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/13.
 */

public class PayMent {

    /**
     * name : wechat
     * logo_url : http://www.s.local/Binary/show_img?model=Payment&fun=getlogoimage&api=kbox&path=Static&img_name=wechat.png
     */

    private String name;
    private String logo_url;

    public static List<PayMent> arrayPayMentFromData(String str) {

        Type listType = new TypeToken<ArrayList<PayMent>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }
}
