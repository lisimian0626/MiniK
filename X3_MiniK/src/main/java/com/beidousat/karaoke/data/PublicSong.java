package com.beidousat.karaoke.data;

import android.util.Log;

import com.beidousat.libbns.util.Logger;

import java.util.Random;

/**
 * Created by J Wong on 2017/6/23.
 */

public class PublicSong {
    public static int index=-1;
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
    public static int getCycleNum(int endNum){
        if(index<endNum-1){
            index++;
        }else{
            index=0;
        }
        Logger.d("Main","index:"+index);
        return index;
    }
}
