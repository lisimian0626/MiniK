package com.beidousat.karaoke.player.nocache;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.beidousat.karaoke.player.BnsPlayerListener;
import com.beidousat.karaoke.player.proxy.Utils;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.LogRecorder;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by J Wong on 2016/10/24.
 */

public class BnsPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SurfaceHolder.Callback, MediaPlayer.OnErrorListener {

    private final static String TAG = "BnsPlayer";
    private MediaPlayer player;
    private List<Integer> trackIndex = new ArrayList<Integer>();
    private SurfaceView mSurfaceView;
    private SurfaceView mMinorSurface;
    private HttpGetProxy proxy;
    private BnsPlayerListener mBnsPlayerListener;
    private String mUrl;
    private ExecutorService mExecutorService;
    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;
    private boolean mIsPlaying;

    public BnsPlayer(SurfaceView surfaceView, SurfaceView minorSurface, int width, int height) {
        this.mSurfaceView = surfaceView;
        this.mMinorSurface = minorSurface;
        this.mSurfaceWidth = width;
        this.mSurfaceHeight = height;
        initPlayer();
    }

    public void setBnsPlayerListener(BnsPlayerListener listener) {
        this.mBnsPlayerListener = listener;
    }

    public void initPlayer() {
        mExecutorService = Executors.newCachedThreadPool();

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(this);

        if (mSurfaceWidth > 0 && mSurfaceHeight > 0) {
            mSurfaceView.getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
        }
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        proxy = new HttpGetProxy();
        initMediaPlayer();
    }


    private void initMediaPlayer() {
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        if (mSurfaceView != null)
            player.setDisplay(mSurfaceView.getHolder());
        if (mMinorSurface != null)
            player.setMinorDisplay(mMinorSurface.getHolder());
    }

    public void open(String path) {

        mIsPlaying = false;
        mUrl = ServerFileUtil.convertHttps2Http(path);

        Logger.d(TAG, "open path url:" + mUrl);
        trackIndex.clear();
        if (player != null) {
            close();
        }
        initMediaPlayer();
        mExecutorService.execute(new Runnable() {
            public void run() {
                try {
                    String url;
                    if (mUrl.endsWith(".m3u8") || mUrl.startsWith("udp://")) {
                        url = mUrl;
                    } else {
                        String id = Utils.stringMD5(mUrl);
                        proxy.startDownload(id, mUrl);
                        url = proxy.getLocalURL(id);
                    }
                    Logger.d(TAG, "BNS play url ==" + url);
                    if (!TextUtils.isEmpty(url)) {
                        player.setDataSource(url);
                        player.prepare();
                        player.start();
                        mIsPlaying = true;
                    } else {
                        sendPlayCompletion();
                    }
//                    LogRecorder.addString2File("/sdcard/bns_play_log.txt", "\n" + "play open url===>" + url);
                } catch (IOException ex) {//无文件时直接跳过
                    Logger.d(TAG, "BNS play url IOException");
                    LogRecorder.addString2File("/sdcard/bns_play_log.txt", "\n" + "play open url IOException===>" + mUrl);
                    sendPlayCompletion();
                } catch (IllegalStateException e) {
                    Logger.d(TAG, "BNS play url IllegalStateException ex:" + e.toString());

                } catch (Exception e) {
                    Logger.d(TAG, "BNS play url Exception ex:" + e.toString());
                    LogRecorder.addString2File("/sdcard/bns_play_log.txt", "\n" + "play open url Exception===>" + mUrl + " ex:" + e.toString());
                    sendPlayCompletion();
                }
            }
        });

//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    String url;
//                    if (path.endsWith(".m3u8") || path.startsWith("udp://")) {
//                        url = path;
//                    } else {
//                        String id = Utils.stringMD5(path);
//                        proxy.startDownload(id, path, false);
//                        url = proxy.getLocalURL(id);
//                    }
//                    player.setDataSource(url);
//                    player.prepare();
//                    player.start();
////                    LogRecorder.addString2File("/sdcard/play_log2.txt", "\n" + "play open url===>" + mUrl);
//                } catch (IOException ex) {//无文件时直接跳过
//                    handler.sendEmptyMessage(1);
//                    LogRecorder.addString2File("/sdcard/bns_play_log.txt", "\n" + "play open url IOException===>" + mUrl);
//                } catch (Exception e) {
//                    LogRecorder.addString2File("/sdcard/bns_play_log.txt", "\n" + "play open url Exception===>" + mUrl + " ex:" + e.toString());
//                    mRetryTime++;
//                    if (mRetryTime < 2) {
//                        handler.sendEmptyMessage(0);
//                    } else {
//                        handler.sendEmptyMessage(1);
//                    }
//                }
//            }
//        }).start();
    }

    private void sendPlayCompletion() {
        handler.removeCallbacks(runCompletion);
        handler.postDelayed(runCompletion, 2000);
    }

    private Runnable runCompletion = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    close();
                    open(mUrl);
                    break;
                case 1:
                    close();
                    if (mBnsPlayerListener != null) {
                        mBnsPlayerListener.onPlayerCompletion();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public boolean isPlaying() {
        if (mIsPlaying) {
            try {
                if (player != null)
                    return player.isPlaying();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getCurrentPosition() {
        try {
            if (player != null)
                return player.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getDuration() {
        try {
            if (player != null)
                return player.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void seekTo(int position) {
        try {
            if (player != null)
                player.seekTo(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            if (player != null)
                player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        try {
            if (player != null)
                player.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPitch(int tone) {
        if (player != null) {
            player.setPitch(tone);
        }
    }

    public void setVolChannel(int channel) {
        if (player != null) {
            player.setParameter(1102, channel);
        }
    }

    public void setVolume(float volume) {
        Logger.d(TAG, "setVolume :" + volume);
        if (player != null)
            player.setVolume(volume, volume);
    }


    public int getTackNum() {
        trackIndex.clear();

        if (trackIndex.size() > 0)
            return trackIndex.size();
        if (player != null) {
            int total = 0;
            MediaPlayer.TrackInfo trackInfo[] = player.getTrackInfo();
            for (int i = 0; i < trackInfo.length; i++) {
                if (trackInfo[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    trackIndex.add(i);
                    total++;
                }
            }
            return total;
        }
        return 0;
    }

    public void selectTack(int index) {
        if (player != null) {
            try {
                player.selectTrack(trackIndex.get(index));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean haveMulTracks() {
        return trackIndex.size() > 0;
    }

    public void close() {
        try {
            if (player != null) {
                try {
                    if (player.isPlaying())
                        player.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(null);
                player.setOnErrorListener(null);
                player.setOnCompletionListener(null);
//                player.reset();
                player.release();
                player = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mIsPlaying = false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        getTackNum();
        if (mBnsPlayerListener != null) {
            mBnsPlayerListener.onPlayerPrepared();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mBnsPlayerListener != null) {
            mBnsPlayerListener.onPlayerCompletion();
        }
        mIsPlaying = false;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Logger.d(TAG, "MediaPlayer   onError : " + " code1:" + i + "  code2:" + i1);
        return false;
    }

    private boolean mSurfaceLoaded;

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceLoaded = false;
        Logger.d(TAG, "surface  Destroyed ");
        close();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Logger.d(TAG, "surface  Changed ");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceLoaded = true;
        Logger.d(TAG, "surface  Created ");
    }
}
