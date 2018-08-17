package com.beidousat.karaoke.player;

/**
 * Created by J Wong on 2015/11/23 08:57.
 */
public interface BeidouPlayerListener {

    void onPlayerPrepared();

    void onPlayerProgress(long progress, long duration);

    void onPlayerCompletion();

}
