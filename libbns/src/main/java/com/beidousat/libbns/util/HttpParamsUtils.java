package com.beidousat.libbns.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class HttpParamsUtils {

    public static Map<String, String> initCardDetailParams(String card_code, int static_code) {
        Map<String, String> params = new HashMap<>();
        params.put("card_code", card_code);
        params.put("static_code", static_code + "");
        return params;
    }
    public static Map<String, String> initGiftParams(String device_sn,String kbox_sn,String card_code, int static_code) {
        Map<String, String> params = new HashMap<>();
        params.put("device_sn", device_sn);
        params.put("kbox_sn", kbox_sn);
        params.put("card_code", card_code);
        params.put("static_code", static_code + "");
        return params;
    }
    public static Map<String, String> initConfigParams(String device_sn) {
        Map<String, String> params = new HashMap<>();
        params.put("device_sn", device_sn);
        return params;
    }
    public static Map<String, String> initCreateOrderParams(int payType, int payCount, String payment, String deviceSn, String kboxSn, String card_code, int static_code) {
        Map<String, String> params = new HashMap<>();
        params.put("pay_type", payType + "");
        params.put("pay_count", payCount + "");
        params.put("device_sn", deviceSn);
        params.put("kbox_sn", kboxSn);
        params.put("payment", payment);
        if (!TextUtils.isEmpty(card_code)) {
            params.put("card_code", card_code);
            params.put("static_code", static_code + "");
        }
        return params;
    }
    public static Map<String, String> initCreateOrder2Params(int payType, int payCount,String deviceSn, String kboxSn, String card_code, int static_code) {
        Map<String, String> params = new HashMap<>();
        params.put("pay_type", payType + "");
        params.put("pay_count", payCount + "");
        params.put("device_sn", deviceSn);
        params.put("kbox_sn", kboxSn);
        if (!TextUtils.isEmpty(card_code)) {
            params.put("card_code", card_code);
            params.put("static_code", static_code + "");
        }
        return params;
    }
    public static Map<String, String> initPayCreateParams(String orderSn,String payment) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", orderSn);
        params.put("payment", payment);
        return params;
    }
    public static Map<String, String> initQueryOrderParams(String orderSn, String deviceSn, String kboxSn) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", orderSn);
        params.put("device_sn", deviceSn);
        params.put("kbox_sn", kboxSn);

        return params;
    }

    public static Map<String, String> initQueryUserParams(int userId, String orderSn, String deviceSn, String kboxSn) {
        Map<String, String> params = new HashMap<>();

        params.put("user_id", userId + "");
        params.put("order_sn", orderSn);
        params.put("device_sn", deviceSn);
        params.put("kbox_sn", kboxSn);

        return params;
    }

    public static Map<String, String> initCancelOrderParams(String orderSn) {
        Map<String, String> params = new HashMap<>();

        params.put("order_sn", orderSn);

        return params;
    }

    public static Map<String, String> initKBoxParams(String kboxSn,String chip,String card_code) {
        Map<String, String> params = new HashMap<>();

        params.put("kbox_sn", kboxSn);
        params.put("device_sn", chip);
        if(!TextUtils.isEmpty(card_code)){
            params.put("card_code",card_code);
        }
        params.put("static_code","1");
        return params;
    }
    public static Map<String, String> initUploadSongParams(String kboxSn,String SongID,String orderSn,long playtime,long finishtime,int songlenght,int score) {
        Map<String, String> params = new HashMap<>();
        params.put("kbox_sn", kboxSn);
        params.put("song_id",SongID);
        if(!TextUtils.isEmpty(orderSn)){
            params.put("order_sn",orderSn);
        }
        params.put("play_time",String.valueOf(playtime));
        params.put("finish_time",String.valueOf(finishtime));
        params.put("song_time_len",String.valueOf(songlenght));
        params.put("score",String.valueOf(score));
        return params;
    }
}
