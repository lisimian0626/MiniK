package com.beidousat.libbns.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by J Wong on 2016/1/4 19:06.
 */
public class ListUtil {

    public static List<String> array2List(String[] texts) {
        if (texts != null && texts.length > 0) {
            List<String> list = new ArrayList<String>();
            list = Arrays.asList(texts);
            return list;
        }
        return new ArrayList<String>();
    }

}
