package com.beidousat.karaoke.player.chenxin;

import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.VideoDownloader;
import com.beidousat.karaoke.player.local.LocalFileCache;
import com.beidousat.karaoke.player.local.LocalFileProxy;
import com.beidousat.karaoke.player.online.CacheFile;
import com.beidousat.karaoke.player.online.HttpGetProxy;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.DownloadQueueHelper;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.beidousat.karaoke.player.BnsPlayer.NORMAL;
import static com.beidousat.karaoke.player.BnsPlayer.PREVIEW;

/**
 * Created by J Wong on 2017/5/3.
 */

public class BNSPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private String TAG="BNSPlayer";
    private MPlayer mPlayer;
    private BeidouPlayerListener mBnsPlayerListener;

    public BNSPlayer(SurfaceView surfaceView,SurfaceView minor) {
        mPlayer = new MPlayer();
        mPlayer.init(surfaceView);
        if(minor!=null)
        mPlayer.setMinorDisplay(minor.getHolder());
    }

    public void open(Song curSong, Song nextSong, int playmode, BeidouPlayerListener listener) {
        this.mBnsPlayerListener = listener;
        File file = DiskFileUtil.getDiskFileByUrl(curSong.SongFilePath);
        String curSongpath = ServerFileUtil.getFileUrl(curSong.download_url);
        String nextSongPath = ServerFileUtil.getFileUrl(nextSong.download_url);
        Logger.d("BNSPlayer", "savaPath:" + file.getAbsolutePath()+"   curSongpath:"+curSongpath+"    nextSongPath:"+nextSongPath);
        String playUrl = null;
        try {
            if (file != null) {//存在本地文件
                Logger.d("BNSPlayer", "open 本地视频 ：" + file.getAbsolutePath());
                LocalFileCache.getInstance().add(curSongpath, nextSongPath);
                LocalFileProxy proxy = new LocalFileProxy();
                proxy.startDownload(curSong.download_url);
                playUrl = proxy.getLocalURL();
            } else {//本地文件不存在
                if (!NetWorkUtils.isNetworkAvailable(Main.mMainActivity.getApplicationContext())) {
                    return;
                }
                if (playmode == PREVIEW) {
                    Logger.d("BNSPlayer", "open 网络视频 ：" + curSongpath);
                    CacheFile.getInstance().add(curSong.download_url, nextSong.download_url);
                    HttpGetProxy proxy = new HttpGetProxy();
                    proxy.startDownload(curSong.download_url);
                    playUrl = proxy.getLocalURL();
                }else if(playmode==NORMAL){
                    Logger.d("BNSPlayer", "open 网络视频 ：" + curSongpath);
                    if (!DiskFileUtil.hasDiskStorage()) {
                        return;
                    }
                        EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT_DELAY, curSong.download_url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("BNSPlayer", "open play url ：" + playUrl);
        if (!TextUtils.isEmpty(playUrl)) {
            mPlayer.open(playUrl, this, this);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mBnsPlayerListener.onPlayerCompletion();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mBnsPlayerListener.onPlayerPrepared();
    }

    public void close() {
        try {
            if (mPlayer != null) {
                mPlayer.close();
                mPlayer = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isPlaying() {
        try {
            return mPlayer.isPlaying();
        } catch (Exception e) {
        }
        return false;
    }

    public void start() {
        try {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }
        } catch (Exception ex) {
        }
    }

    public void pause() {
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        } catch (Exception ex) {
        }
    }

    public int getCurrentPosition() {
        try {
            if (mPlayer != null)
                return mPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getDuration() {
        try {
            if (mPlayer != null)
                return mPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setVolume(float volume) {
        if (mPlayer != null)
            mPlayer.setVolume(volume);
    }

    public void seekTo(int seek) {
        try {
            if (mPlayer != null)
                mPlayer.seekTo(seek);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectTack(int index) {
        if (mPlayer != null) {
            mPlayer.selectTack(index);
        }
    }

    public boolean haveMulTracks() {
        if (mPlayer != null) {
            return mPlayer.getTackNum() > 0;
        }
        return false;
    }

    public void setVolChannel(int channel) {
        if (mPlayer != null) {
            mPlayer.setVolChannel(channel);
        }
    }

    public void setPitch(int tone) {
        if (mPlayer != null) {
            mPlayer.setPitch(tone);
        }
    }
}
