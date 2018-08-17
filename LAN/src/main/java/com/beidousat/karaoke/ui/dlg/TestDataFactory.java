package com.beidousat.karaoke.ui.dlg;

import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Hanson
 * date:   2017/4/1
 * describe:
 */

public class TestDataFactory {
    public static List<Song> createSongList() {
        List<Song> songs = new ArrayList<>();
        Song s = new Song();
        s.SimpName = "一千个伤心的理由";
        s.SingerName = "张学友";
        for (int i=0; i<50; i++) {
            songs.add(s);
        }

        return songs;
    }

    public static Meal createTestMeal() {
        Meal meal = new Meal();
        meal.setUrl("https://172.30.4.230/?m=netbar&a=CreateOrder&store_sn=&pay_count=30&device_sn=1829ce617d8587d6%20&pay_type=1");
        meal.setPrice(3000f);
        meal.setAmount(15);
        meal.setID(121350);
        meal.setType(2);

        return meal;
    }

//    public static PayStatus createTestPayStatus() {
//        PayStatus status = new PayStatus();
//        status.setPayStatus(1);
//        status.setPayUserID(1);
//
//        return status;
//    }
}
