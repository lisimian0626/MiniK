package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

/**
 * Created by J Wong on 2018/2/23.
 */

public class CouponDetail {

    @Expose
    public String title;

    @Expose
    public String notice;

    @Expose
    public String description;

    @Expose
    public String card_type;

    @Expose
    public String show_product;

    @Expose
    public String pre_product;

    @Expose
    public String limit;

    @Expose
    public String use_date_str;

    public String card_code;

    public String getCard_code() {
        return card_code;
    }

    public void setCard_code(String card_code) {
        this.card_code = card_code;
    }
}
