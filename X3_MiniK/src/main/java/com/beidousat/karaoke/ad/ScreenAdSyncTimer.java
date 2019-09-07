package com.beidousat.karaoke.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J Wong on 2017/08/16 15:09.
 */
public class ScreenAdSyncTimer implements HttpRequestListener {

    private Context mContext;
    private static ScreenAdSyncTimer mRomDetailTimer;
    private ScheduledExecutorService mScheduledExecutorService;

    public static ScreenAdSyncTimer getInstance(Context context) {
        if (mRomDetailTimer == null)
            mRomDetailTimer = new ScreenAdSyncTimer(context);
        return mRomDetailTimer;
    }

    public ScreenAdSyncTimer(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void syncAd() {
        try {
            HttpRequest r = initRequest(RequestMethod.GET_AD_SCREEN);
            r.addParam("RoomCode", PrefData.getRoomCode(mContext));
            r.addParam("ADPosition", "P2");
            Song song = ChooseSongs.getInstance(mContext).getFirstSong();
            if (song != null) {
                r.addParam("SongID", song.ID);
            }
            r.setConvert2Class(Ad.class);
            r.doPost(0);
        } catch (Exception e) {
            Logger.d("AdScreenGetter", "getScreenAd ex:" + e.toString());
        }
    }

    public void startTimer() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);
    }

    public void stopTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                syncAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(mContext.getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }


    @Override
    public void onFailed(String method, String error) {

    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_AD_SCREEN.equals(method)) {
            Ad ad = (Ad) object;
            if (ad != null) {
                EventBusUtil.postSticky(EventBusId.id.CURRENT_SCREEN_AD, ad);
            }
        }
    }


    @Override
    public void onStart(String method) {
    }

}
