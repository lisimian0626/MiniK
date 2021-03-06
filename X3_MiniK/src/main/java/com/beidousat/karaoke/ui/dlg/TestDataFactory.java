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

    public static PayStatus createPayStatus() {
        PayStatus payStatus = new PayStatus();

        payStatus.setAmount(15);
        payStatus.setType(2);

        return payStatus;
    }

//    public static PayStatus createTestPayStatus() {
//        PayStatus status = new PayStatus();
//        status.addPayStatus(1);
//        status.setPayUserID(1);
//
//        return status;
//    }
}
