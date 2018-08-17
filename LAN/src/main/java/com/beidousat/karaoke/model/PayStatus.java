package com.beidousat.karaoke.model;

import com.google.gson.annotations.SerializedName;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class PayStatus {
    @SerializedName("pay_status")
    int PayStatus;
    @SerializedName("pay_time")
    int PayTime;
    @SerializedName("pay_user_id")
    int PayUserID;
    @SerializedName("order_sn")
    String OrderSn;
    @SerializedName("device_sn")
    String DeviceSn;
    @SerializedName("pay_type")
    private int Type;
    @SerializedName("pay_count")
    private int Amount;

    public static final int PAY_SUCESS = 1;


    public int getPayStatus() {
        return PayStatus;
    }

    public int getPayTime() {
        return PayTime;
    }

    public int getPayUserID() {
        return PayUserID;
    }

    public String getOrderSn() {
        return OrderSn;
    }

    public String getDeviceSn() {
        return DeviceSn;
    }

    public int getType() {
        return Type;
    }

    public int getAmount() {
        return Amount;
    }
}
