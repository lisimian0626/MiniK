package com.beidousat.karaoke.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class KBox {
    @SerializedName("kbox_sn") //房间编号
    String KBoxSn;
    @SerializedName("store_id") //店家ID
    int StoreID;
    @SerializedName("store_code") //店家编号
    String StoreCode;
    @SerializedName("label") //标签
    String Label;
    @SerializedName("province")  //省
    String Province;
    @SerializedName("city") //市
    String City;
    @SerializedName("area") //区域
    String Area;
    @SerializedName("address") //地址明细
    String Address;
    @SerializedName("use_online") //是否使用在线支付方案，1=是，0=否
    int use_online;

    @SerializedName("use_coin") //是否使用投币方案，1=是，0=否
    int use_coin;

    @SerializedName("coin_exchange_rate") //汇率，一个币的价格
    float coin_exchange_rate;

    @SerializedName("packages")
    List<Package> Packages;

    public String getKBoxSn() {
        return KBoxSn;
    }

    public int getStoreID() {
        return StoreID;
    }

    public String getStoreCode() {
        return StoreCode;
    }

    public String getLabel() {
        return Label;
    }

    public String getProvince() {
        return Province;
    }

    public String getCity() {
        return City;
    }

    public String getArea() {
        return Area;
    }

    public String getAddress() {
        return Address;
    }

    public List<Package> getPackages() {
        return Packages;
    }

    public int getUse_online() {
        return use_online;
    }

    public void setUse_online(int use_online) {
        this.use_online = use_online;
    }


    public int getUse_coin() {
        return use_coin;
    }

    public void setUse_coin(int use_coin) {
        this.use_coin = use_coin;
    }

    public float getCoin_exchange_rate() {
        return coin_exchange_rate;
    }

    public void setCoin_exchange_rate(float coin_exchange_rate) {
        this.coin_exchange_rate = coin_exchange_rate;
    }
}
