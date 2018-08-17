package com.beidousat.karaoke.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/20.
 */

public class DateUtil {
    public static SimpleDateFormat getDateFormat1() {
//11/28/2017 17:26:16
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }
    public static SimpleDateFormat getDateFormat() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    public  static boolean DateCompare(String start,String end) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date data_start = sdf.parse(start);
        Date data_end = sdf.parse(end);
        Date currentdata=new Date(System.currentTimeMillis());
        if(currentdata.getTime()>=data_start.getTime()&&currentdata.getTime()<=data_end.getTime()){
            return true;
        }else{
            return false;
        }
    }
}
