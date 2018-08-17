package com.beidousat.karaoke.data;

/**
 * Created by J Wong on 2017/6/23.
 */

public class PublicSong {

    private static final String videoAdDir = "/data/PublicSong/";


    public static String getAdVideo() {
        int m = (int) ((Math.random() * 5));
        return videoAdDir + String.valueOf(m + 1) + ".mp4";
    }
}
