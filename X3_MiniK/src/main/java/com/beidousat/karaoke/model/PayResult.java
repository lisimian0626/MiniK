package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PayResult {

    /**
     * create_id : 0
     * order_sn : D0006600000021180809
     * qrcode_data :
     * subtotal : 5007
     * pay_type : 2
     * pay_count : 100
     * is_pay : 0
     */

    private int create_id;
    private String order_sn;
    private String qrcode_data;
    private int subtotal;
    private String pay_type;
    private String pay_count;
    private int is_pay;

    public static List<PayResult> arrayPayResultFromData(String str) {

        Type listType = new TypeToken<ArrayList<PayResult>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public int getCreate_id() {
        return create_id;
    }

    public void setCreate_id(int create_id) {
        this.create_id = create_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getQrcode_data() {
        return qrcode_data;
    }

    public void setQrcode_data(String qrcode_data) {
        this.qrcode_data = qrcode_data;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_count() {
        return pay_count;
    }

    public void setPay_count(String pay_count) {
        this.pay_count = pay_count;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }
}
