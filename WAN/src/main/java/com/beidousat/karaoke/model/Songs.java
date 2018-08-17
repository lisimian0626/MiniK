package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by J Wong on 2015/10/19 16:53.
 */
public class    Songs extends BaseList {

    @Expose
    public List<Song> list;

    @Expose
    public String NextWrod;

    @Expose
    public String Namesimplicity = "";


    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
