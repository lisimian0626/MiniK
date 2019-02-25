package com.beidousat.karaoke.util;

import com.beidousat.karaoke.model.BasePlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/14.
 */

public class BasePlayFitter {
    public static List<BasePlay> getDiffrent(List<BasePlay> basePlayList1, List<BasePlay> basePlayList2) {
        List<BasePlay> diffrent = new ArrayList<>();
        List<BasePlay> maxlist = basePlayList1;
        List<BasePlay> minlist = basePlayList2;
        if (basePlayList1.size() > basePlayList2.size()) {
            maxlist = basePlayList2;
            minlist = basePlayList1;
        }
        Map<BasePlay, Integer> map = new HashMap<>(maxlist.size());
        for (BasePlay p : maxlist) {
            map.put(p, 1);
        }
        for (BasePlay p : minlist) {
            if (map.get(p) != null) {
                map.put(p, 2);
                continue;
            }
            diffrent.add(p);
        }
        for (Map.Entry<BasePlay, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diffrent.add(entry.getKey());
            }
        }
        return diffrent;
    }
}
