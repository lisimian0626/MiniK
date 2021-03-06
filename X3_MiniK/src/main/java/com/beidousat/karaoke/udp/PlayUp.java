package com.beidousat.karaoke.udp;

import com.google.gson.Gson;

import java.io.Serializable;

public class PlayUp implements Serializable {
    public String event;
    public int eventkey;
    public int sound;
    public int music;
    public int status;
    public int mic;
    public int tone;
    public int reverberation;
    public int score;
    public String hsn;
    public String token;
    public int mute;
    public PlayUp(String event, int eventkey, int sound, int status,int music, int mic, int tone, int reverberation, int score,int mute,String hsn, String token) {
        this.event = event;
        this.eventkey = eventkey;
        this.sound = sound;
        this.status=status;
        this.music = music;
        this.mic = mic;
        this.tone = tone;
        this.reverberation = reverberation;
        this.score = score;
        this.mute=mute;
        this.hsn = hsn;
        this.token = token;
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
}
