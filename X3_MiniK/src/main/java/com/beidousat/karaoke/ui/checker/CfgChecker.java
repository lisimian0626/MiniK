package com.beidousat.karaoke.ui.checker;

import android.content.Context;

import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.libbns.net.NetWorkUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J Wong on 2017/5/12.
 */

public class CfgChecker {

    public static CfgChecker mNetChecker;
    private Context mConext;
    private ScheduledExecutorService mScheduledExecutorService;
    private OnRequstListener onRequstListener;
    public static CfgChecker getInstance(Context context) {
        if (mNetChecker == null) {
            mNetChecker = new CfgChecker(context);
        }
        return mNetChecker;
    }

    private CfgChecker(Context context) {
        this.mConext = context;
    }

    public void check() {
        if (mScheduledExecutorService == null || mScheduledExecutorService.isShutdown()) {
            mScheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduleAtFixedRate(mScheduledExecutorService);
        }
    }

    private void stop() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
              if(onRequstListener!=null){
                  onRequstListener.Requst();
              }
            }
        }, 0, 3, TimeUnit.SECONDS);
    }



    public CfgChecker setOnRequstListener(OnRequstListener listenner) {
        this.onRequstListener = listenner;
        return this;
    }

    public interface OnRequstListener {
        void Requst();
    }
}
