package com.beidousat.karaoke.udp;

import com.google.gson.Gson;

public class HeatbeatUp {
    public String event;
    public String eventkey;
    public String token;
    public String hsn;


    public HeatbeatUp() {
    }

    public HeatbeatUp(String event, String eventkey, String token, String hsn) {
        this.event = event;
        this.eventkey = eventkey;
        this.token = token;
        this.hsn = hsn;
    }

    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setHeartbeat(String token,long hsn){
        event="hearbeat";
        eventkey="1";
        this.token=token;
        this.hsn=String.valueOf(hsn);
    }
}
