package com.beidousat.karaoke.model;

import android.graphics.drawable.Drawable;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author: Hanson
 * date:   2017/3/31
 * describe:
 */

public class Meal implements Serializable {

    @SerializedName("create_id")
    private int ID;
    @SerializedName("pay_type")
    private int Type;
    @SerializedName("pay_count")
    private int Amount;
    @SerializedName("subtotal")
    private float Price;
    @SerializedName("qrcode_data")
    private String Url; //支付地址
    @SerializedName("order_sn")
    private String OrderSn;

    public static final int SONG = 1;
    public static final int TIME = 2;

    public int getID() {
        return ID;
    }

    public int getType() {
        return Type;
    }

    public int getAmount() {
        return Amount;
    }

    public float getPrice() {
        return Price / 100; //分->元
    }

    public String getUrl() {
        return Url;
    }

    public String getOrderSn() {
        return OrderSn;
    }

    public String getUnit() {
        String unit = "分钟";
        if (Type == TIME) {
            unit = "分钟";
        } else {
            unit = "首";
        }

        return unit;
    }

    public String getTitle() {
        return String.format("%d%s/￥%.2f", getAmount(), getUnit(), getPrice());
    }

    public Drawable getDrawable() {
        Drawable drawable = null;
        if (Type == TIME) {
            drawable = LanApp.getInstance().getResources().getDrawable(R.drawable.dlg_buy_time);
        } else {
            drawable = LanApp.getInstance().getResources().getDrawable(R.drawable.dlg_buy_song);
        }

        return drawable;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
