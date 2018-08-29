package com.beidousat.karaoke.model;

import android.graphics.drawable.Drawable;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.R;
import com.beidousat.libbns.model.Common;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.beidousat.karaoke.R.string.count;

/**
 * author: Hanson
 * date:   2017/3/31
 * describe:
 */

public class Meal implements Serializable {
    @Expose
    @SerializedName("create_id")
    private int ID;
    @Expose
    @SerializedName("pay_type")
    private int Type;
    @Expose
    @SerializedName("pay_count")
    private int Amount;
    @Expose
    @SerializedName("subtotal")
    private float Price;

    @Expose
    @SerializedName("qrcode_data")
    private String Url; //支付地址
    @Expose
    @SerializedName("order_sn")
    private String OrderSn;

    @Expose
    @SerializedName("real_total")
    private float realPrice;

    @Expose
    @SerializedName("use_online")
    private int use_online;

    @Expose
    @SerializedName("use_cion")
    private int use_cion;
    @Expose
    @SerializedName("use_gift_card")
    private int use_gift_card;
    @Expose
    @SerializedName("cion_exchange_rate")
    private float cion_exchange_rate;

    //是否支持该优惠券,1=是，0=否
    private int use_card;
    //优惠卷信息
    private String use_card_msg;
    public int getUser_card() {
        return use_card;
    }

    public void setUser_card(int user_card) {
        this.use_card = user_card;
    }

    public static final int SONG = 1;
    public static final int TIME = 2;

    public Meal() {

    }

    public Meal(int type, int amount, float price, float realPrice, int use_card,String use_card_msg,float rate) {
        Type = type;
        Amount = amount;
        Price = price;
        this.realPrice = realPrice;
        this.use_card=use_card;
        this.use_gift_card=use_gift_card;
        this.use_card_msg=use_card_msg;
//        setUse_cion(cion);
//        setUse_online(online);
        setCion_exchange_rate(rate);
    }

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

    public float getRealPrice() {
        return realPrice / 100; //分->元
    }

    public String getUrl() {
        return Url;
    }

    public String getOrderSn() {
        return OrderSn;
    }

    public void setOrderSn(String orderSn) {
        OrderSn = orderSn;
    }

    public String getUnit() {
        String unit=null;
        if(Common.isEn){
            if (Type == TIME) {
                unit = " Minutes";
            } else {
                unit = " Songs";
            }
        }else{
            if (Type == TIME) {
                unit = "分钟";
            } else {
                unit = "首";
            }

        }
        return unit;
    }

    public String getTitle() {
        String str=null;
        if(Common.isEn){
            str=String.format("%s￥%.2f", "original cost：", getPrice());
        }else{
            str=String.format("%s￥%.2f", "原价：", getPrice());
        }
        return str;

    }

    public int getBiCount() {
        int count = 0;
        if (realPrice % getCion_exchange_rate() == 0) {
            count = (int) (realPrice / getCion_exchange_rate());
        } else {
            count = (int) (realPrice / getCion_exchange_rate()) + 1;
        }
        return count;
    }

    public String getPriceTitle() {
        if(Common.isEn){
            if (getUse_cion() == 1) {
                return String.format("￥%.2f/%d coin", getRealPrice(), getBiCount());
            } else {
                return String.format("￥%.2f", getRealPrice());
            }
        }else{
            if (getUse_cion() == 1) {
                return String.format("￥%.2f/%d币", getRealPrice(), getBiCount());
            } else {
                return String.format("￥%.2f", getRealPrice());
            }
        }
    }

    public String getTime() {
        return String.format("%d%s", getAmount(), getUnit());
    }

    public String getRealPriceTitle() {
        return String.format("%d%s/￥%.2f", getAmount(), getUnit(), getRealPrice());
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

    public int getUse_online() {
        return use_online;
    }

    public void setUse_online(int use_online) {
        this.use_online = use_online;
    }

    public int getUse_cion() {
        return use_cion;
    }

    public void setUse_cion(int use_cion) {
        this.use_cion = use_cion;
    }


    public float getCion_exchange_rate() {
        return cion_exchange_rate;
    }

    public void setCion_exchange_rate(float cion_exchange_rate) {
        this.cion_exchange_rate = cion_exchange_rate;
    }

    public void setRealPrice(float realPrice) {
        this.realPrice = realPrice;
    }

    public int getUse_gift_card() {
        return use_gift_card;
    }

    public void setUse_gift_card(int use_gift_card) {
        this.use_gift_card = use_gift_card;
    }
}
