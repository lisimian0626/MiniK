package com.beidousat.karaoke.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.beidousat.libbns.util.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J Wong on 2017/4/6.
 */

public class AudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private Context mContext;

    private static AudioPlayer mAudioPlayer;
    private boolean mIsPlaying;
    private MediaPlayer mediaPlayer;

    public static AudioPlayer getInstance(Context context) {
        if (mAudioPlayer == null) {
            mAudioPlayer = new AudioPlayer(context);
        }
        return mAudioPlayer;
    }


    private AudioPlayer(Context c) {
        mContext = c;
    }

    public void seekTo(int seek) {
        if (mIsPlaying && mediaPlayer != null) {
            mediaPlayer.seekTo(seek);
        }
    }

    public void play(final String pathPath) {
        if (mIsPlaying) {
            close();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Logger.d("AudioPlayer", "mp3 path:" + pathPath);
                    mediaPlayer.setDataSource(pathPath);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Logger.w("AudioPlayer", "mp3 IOException:" + e.toString());
                    close();
                } catch (Exception e) {
                    Logger.w("AudioPlayer", "mp3 Exception:" + e.toString());
                    close();
                }
            }
        }).start();
        mIsPlaying = true;
    }

    public void close() {
        stopTimer();
        try {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.setOnErrorListener(null);
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.release();
                Logger.d("AudioPlayer", "close ==================>");
                mediaPlayer = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mIsPlaying = false;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.start();
            Logger.d("AudioPlayer", "onPrepared");
            mIsPlaying = true;
            startTimer();
        } catch (Exception e) {
            Logger.w("AudioPlayer", "onPrepared ex:" + e.toString());
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (mAudioPlayerListener != null) {
            mAudioPlayerListener.onPlayCompletion();
        }

        stopTimer();
    }

    private ScheduledExecutorService mScheduledExecutorService;

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
                if (mIsPlaying && mediaPlayer != null && mAudioPlayerListener != null) {
                    try {
                        Logger.d("AudioPlayer", "getCurrentPosition :" + mediaPlayer.getCurrentPosition() + "  duration:" + mediaPlayer.getDuration());
                        mAudioPlayerListener.onPlayProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    private AudioPlayerListener mAudioPlayerListener;

    public void setAudioPlayerListener(AudioPlayerListener listener) {
        this.mAudioPlayerListener = listener;
    }

    public interface AudioPlayerListener {

        void onPlayProgress(int progress, int duration);

        void onPlayCompletion();
    }
}
