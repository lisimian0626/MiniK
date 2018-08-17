package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class PayStatus {
    @Expose
    @SerializedName("pay_status")
    int PayStatus;
    @Expose
    @SerializedName("pay_time")
    int PayTime;
    @Expose
    @SerializedName("pay_user_id")
    int PayUserID;
    @Expose
    @SerializedName("order_sn")
    String OrderSn;
    @Expose
    @SerializedName("device_sn")
    String DeviceSn;
    @Expose
    @SerializedName("pay_type")
    private int Type;

    @Expose@SerializedName("pay_count")
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

    public void setType(int type) {
        Type = type;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public void setPayStatus(int payStatus) {
        PayStatus = payStatus;
    }

    public void setPayTime(int payTime) {
        PayTime = payTime;
    }

    public void setPayUserID(int payUserID) {
        PayUserID = payUserID;
    }

    public void setOrderSn(String orderSn) {
        OrderSn = orderSn;
    }

    public void setDeviceSn(String deviceSn) {
        DeviceSn = deviceSn;
    }
}
