package com.beidousat.karaoke.service.octo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import android.content.Context;
import android.os.PowerManager;
import java.io.File;
import java.util.concurrent.TimeUnit;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OctopusService extends Service implements Runnable{
    private boolean mFinished = true;
    Thread mThread = null;
    ReadUart mReadUart = null;
    int port=1;
    int baut=9600;
    private final static String TAG = "OCT";

    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "leow service on create");
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart....");

        {
            mFinished = true;
            mReadUart = new ReadUart();
            //getSavedValue();

            mReadUart.initUart(port, baut);

            mThread = new Thread(this);
            mThread.start();
        }
    }

    public IBinder onBind(Intent intent){
        Log.d(TAG, "onBind....");
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        mFinished = false;
        if(mThread != null)
            mThread.interrupt();
        if(mReadUart != null)
            mReadUart.closeUart();
    }

    @Override
    public void run() {
        while(mFinished)
        {	try{
            //查询状态
            if(mReadUart.pollStatus() > 0){
                Log.d(TAG, "run....");
                Thread.sleep(200);
                //
                // 其他命令下发
                //
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        }
    }
}
