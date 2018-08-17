package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

/**
 * Created by J Wong on 2018/2/23.
 */

public class GiftDetail {
    @Expose
    public int create_id;

    @Expose
    public String order_sn;

    @Expose
    public String qrcode_data;

    @Expose
    public int subtotal;

    @Expose
    public int pay_type;

    @Expose
    public int pay_count;

    @Expose
    public int is_pay;
}
