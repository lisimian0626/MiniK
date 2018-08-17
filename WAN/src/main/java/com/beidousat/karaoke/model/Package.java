package com.beidousat.karaoke.model;

import com.google.gson.annotations.SerializedName;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:套餐信息
 */

public class Package {
    //varchar packages->pack_type	套餐类型，1=包歌曲，2=包时
    //varchar packages->pack_count	套餐数
    //varchar packages->subtotal	套餐费用

    @SerializedName("pack_type")
    int PackType;

    @SerializedName("pack_count")
    int PackCount;

    @SerializedName("subtotal")
    int SubTotal;

    @SerializedName("real_total")
    int realPrice;

    @SerializedName("use_card")
    int usec_card;

    @SerializedName("use_card_errmsg")
    String user_card_msg;

    public int getUsec_card() {
        return usec_card;
    }

    public void setUsec_card(int usec_card) {
        this.usec_card = usec_card;
    }

    public String getUser_card_msg() {
        return user_card_msg;
    }

    public void setUser_card_msg(String user_card_msg) {
        this.user_card_msg = user_card_msg;
    }

    public int getPackType() {
        return PackType;
    }

    public int getPackCount() {
        return PackCount;
    }

    public int getSubTotal() {
        return SubTotal;
    }

    public int getRealPrice() {
        return realPrice;
    }

}
