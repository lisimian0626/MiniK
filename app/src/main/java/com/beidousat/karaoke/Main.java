package com.beidousat.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.logging.Logger;

public class Main extends Activity {
    private final static String TAG="Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        String codeCache = "1144BB0A0C1242444";
        Log.d(TAG, "dealCode codeCache >>>>>>>>>> " + codeCache);
        try {
            if (codeCache.substring(codeCache.indexOf("44BB0A")).length() >= 8) {
                String str = codeCache.substring(codeCache.indexOf("44BB0A"));
                String hex = str.substring(6, 8);
                int micVol = Integer.parseInt(hex, 16);
//                EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_MIC_VOL, micVol);
                Log.d(TAG, "OnMcuReceive handle mic:" + "codeCache:"+codeCache+"       hex:"+hex+"    micVol:"+micVol + "");
            }else if(codeCache.substring(codeCache.indexOf("44BB08")).length() >= 8){
                String str = codeCache.substring(codeCache.indexOf("44BB08"));
                String hex = str.substring(6, 8);
                int micVol = Integer.parseInt(hex, 16);
                Log.d(TAG, "OnMcuReceive handle eff:" + "codeCache:"+codeCache+"    effVol:"+micVol + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
