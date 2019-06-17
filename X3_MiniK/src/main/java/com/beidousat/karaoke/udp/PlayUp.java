package com.beidousat.karaoke.udp;

import com.google.gson.Gson;

public class PlayUp {
    public String event;
    public int eventkey;
    public int sound;
    public int music;
    public int mic;
    public int tone;
    public int reverberation;
    public int score;
    public String hsn;
    public String token;

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
}
