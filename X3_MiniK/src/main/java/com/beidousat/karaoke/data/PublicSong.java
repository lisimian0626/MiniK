package com.beidousat.karaoke.data;

import android.util.Log;

import java.util.Random;

/**
 * Created by J Wong on 2017/6/23.
 */

public class PublicSong {

    private static final String videoAdDir = "/data/PublicSong/";


    public static String getAdVideo() {
        int m = getNum(5);
        return videoAdDir + String.valueOf(m+1) + ".mp4";

    }
    public static int getNum(int endNum){
        if(endNum > 0){
            Random random = new Random();
            return random.nextInt(endNum);
        }
        return 0;
    }
}
