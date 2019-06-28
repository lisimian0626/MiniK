package com.beidousat.karaoke.udp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SignDown implements Serializable {

    /**
     * token : 9138a64cf29fd6a40128c53d92b99acd
     * qrcode : https://www.imtbox.com/uphconn?token=9138a64cf29fd6a40128c53d92b99acd
     * event : sign.ok
     * eventkey :
     * hsn : 2
     * status : OK
     * message :
     */

    private String token;
    private String qrcode;
    private String event;
    private int eventkey;
    private String code;
    private int hsn;
    private String status;
    private String message;

    public static List<SignDown> arraySignDownFromData(String str) {

        Type listType = new TypeToken<ArrayList<SignDown>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getEventkey() {
        return eventkey;
    }

    public void setEventkey(int eventkey) {
        this.eventkey = eventkey;
    }

    public int getHsn() {
        return hsn;
    }

    public void setHsn(int hsn) {
        this.hsn = hsn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
