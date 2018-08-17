package com.beidousat.karaoke.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils {
    private static Toast toast = null;

    public static void toast(Context context,String text) {
        if (toast == null) {
            if(context!=null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
//    public static void toast(Context context, String content){
//            makeText = Toast.makeText(context, "", Toast.LENGTH_SHORT);
//            makeText.setText(content);
//            makeText.show();
//    }

    public static void toast2(Context context, String content){
        Toast makeText = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        makeText.show();
    }
}
