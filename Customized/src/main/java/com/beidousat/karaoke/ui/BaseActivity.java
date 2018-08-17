package com.beidousat.karaoke.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import com.beidousat.libbns.net.request.HttpRequestListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by J Wong on 2015/10/9 08:56.
 */
public class BaseActivity extends FragmentActivity implements OnScreenAdListener, HttpRequestListener {

    private final static int SYS_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    public static long mLastTouchTime;
    public static boolean mCanShowAd = true;

    private ScheduledExecutorService mScheduledExecutorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastTouchTime = 0;
        getWindow().getDecorView().setSystemUiVisibility(SYS_UI);
        startScreenTimer();
    }

    @Override
    protected void onDestroy() {
        stopScreenTimer();
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }


    private void startScreenTimer() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);

    }

    private void stopScreenTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mCanShowAd && System.currentTimeMillis() - mLastTouchTime > 5 * 60 * 1000) {//进入屏保5min
                    onEnterScreenAd();
                }
            }
        }, 60, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onEnterScreenAd() {
    }


    @Override
    public void onStart(String method) {

    }

    @Override
    public void onSuccess(String method, Object object) {

    }

    @Override
    public void onFailed(String method, String error) {

    }
}
