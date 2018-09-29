package com.beidousat.karaoke.player.chenxin;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.beidousat.karaoke.player.VideoDownloader;
import com.beidousat.karaoke.player.local.LocalFileCache;
import com.beidousat.karaoke.player.local.LocalFileProxy;
import com.beidousat.karaoke.player.online.CacheFile;
import com.beidousat.karaoke.player.online.HttpGetProxy;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;

/**
 * Created by J Wong on 2017/5/3.
 */

public class BNSPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {


    private MPlayer mPlayer;
    private BnsPlayerListener mBnsPlayerListener;

    public BNSPlayer(SurfaceView surfaceView,SurfaceView minor) {
        mPlayer = new MPlayer();
        mPlayer.init(surfaceView);
        if(minor!=null)
        mPlayer.setMinorDisplay(minor.getHolder());
    }

    public void open(String uri, BnsPlayerListener listener, String nextUri) {
        if (ServerConfigData.getInstance().getServerConfig() != null && !TextUtils.isEmpty(ServerConfigData.getInstance().getServerConfig().getKbox_ip())) {
            uri = uri.replace(ServerConfigData.getInstance().getServerConfig().getVod_server(), ServerConfigData.getInstance().getServerConfig().getKbox_ip());
            nextUri = nextUri.replace(ServerConfigData.getInstance().getServerConfig().getVod_server(), ServerConfigData.getInstance().getServerConfig().getKbox_ip());
        }

        this.mBnsPlayerListener = listener;
        File file = DiskFileUtil.getDiskFileByUrl(uri);
       // if (DiskFileUtil.getSdcardFileByUrl(uri) != null) {
        //    file = DiskFileUtil.getSdcardFileByUrl(uri);
      //  }
        String playUrl = null;
        try {
            if (file != null) {//存在本地文件
                Logger.d("BNSPlayer", "open 本地视频 ：" + file.getAbsolutePath());
                LocalFileCache.getInstance().add(uri, nextUri);
                LocalFileProxy proxy = new LocalFileProxy();
                proxy.startDownload(uri);
                playUrl = proxy.getLocalURL();
            } else {//本地文件不存在

                if (!NetWorkUtils.isNetworkAvailable(Main.mMainActivity.getApplicationContext())) {
                    return;
                }
                Logger.d("BNSPlayer", "open 网络视频 ：" + uri);

                CacheFile.getInstance().add(uri, nextUri);
                //下载到硬盘
                VideoDownloader.getInstance().addDownloadUrl(uri);
                HttpGetProxy proxy = new HttpGetProxy();
                proxy.startDownload(uri);
                playUrl = proxy.getLocalURL();
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