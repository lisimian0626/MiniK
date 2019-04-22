package com.beidousat.karaoke.player.chenxin;

import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.beidousat.karaoke.model.downLoadInfo;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.local.LocalFileCache;
import com.beidousat.karaoke.player.local.LocalFileProxy;
import com.beidousat.karaoke.player.proxy.CacheFile;
import com.beidousat.karaoke.player.proxy.HttpGetProxy;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.FileUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;

/**
 * Created by J Wong on 2017/5/3.
 */

public class BNSPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private String TAG = "BNSPlayer";
    private MPlayer mPlayer;
    private BeidouPlayerListener mBnsPlayerListener;

    public BNSPlayer(SurfaceView surfaceView, SurfaceView minor) {
        mPlayer = new MPlayer();
        mPlayer.init(surfaceView);

        if (minor != null)
            mPlayer.setMinorDisplay(minor.getHolder());
    }

    public void open(String uri, String savePath, String next_uri, int playmode, BeidouPlayerListener listener) {
        switch (playmode) {
            case BnsConfig.PREVIEW:
            case BnsConfig.NORMAL:
            case BnsConfig.PUBLIC:
                this.mBnsPlayerListener = listener;
                File file = DiskFileUtil.getDiskFileByUrl(savePath);
                String n_uri = ServerConfigData.getInstance().getServerConfig()==null?uri:ServerConfigData.getInstance().getServerConfig().getVod_server() + savePath;
                String n_next_uri =ServerConfigData.getInstance().getServerConfig()==null?next_uri:ServerConfigData.getInstance().getServerConfig().getVod_server() + savePath;
                String playUrl = null;
                try {
                    if (file != null) {//存在本地文件
                        Logger.d("BNSPlayer", "file length：" + String.valueOf(file.length()));
                        if (file.length() < 1024 * 1024) {
                            file.delete();
                            EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT, "");
                            return;
                        }
                        Logger.d("BNSPlayer", "open 本地视频 ：" + file.getAbsolutePath());
                        LocalFileCache.getInstance().add(n_uri, n_next_uri);
                        LocalFileProxy proxy = new LocalFileProxy(savePath);
                        proxy.startDownload(n_uri);
                        playUrl = proxy.getLocalURL();
                    } else {//本地文件不存在
                        if (!NetWorkUtils.isNetworkAvailable(Main.mMainActivity.getApplicationContext())) {
                            return;
                        }
                        if (playmode == BnsConfig.PREVIEW) {
                            Logger.d("BNSPlayer", "open 网络视频 ：" + uri);
                            CacheFile.getInstance().add(uri, uri);
                            HttpGetProxy proxy = new HttpGetProxy();
                            proxy.startDownload(uri);
                            playUrl = proxy.getLocalURL();
                        } else if (playmode == BnsConfig.NORMAL) {
                            Log.e("test", "文件不存在");
                            if (!DiskFileUtil.hasDiskStorage()) {
                                return;
                            }
                            try {
                                downLoadInfo downLoadInfo = new downLoadInfo();
                                downLoadInfo.setDownUrl(uri);
                                downLoadInfo.setSavePath(savePath);
                                downLoadInfo.setPlayMode(playmode);
                                EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT_DELAY, downLoadInfo);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("test", "下载失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Logger.d("BNSPlayer", "open play url ：" + playUrl);
                if (!TextUtils.isEmpty(playUrl)) {
                    mPlayer.open(playUrl, this, this, playmode);
                }
                break;
//            case BnsConfig.PUBLIC:
//                File mFile = FileUtil.getSongSaveDir(savePath);
//                if (mFile != null) {
//                    Logger.d(TAG, "open public file:" + mFile.getAbsolutePath());
//                    mPlayer.open(mFile.getAbsolutePath(), this, this, playmode);
//                } else {
//                    Logger.d(TAG, "public file:" + "不存在");
//                    try {
//                        downLoadInfo downLoadInfo = new downLoadInfo();
//                        downLoadInfo.setDownUrl(uri);
//                        downLoadInfo.setSavePath(savePath);
//                        downLoadInfo.setPlayMode(playmode);
//                        EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT_DELAY, downLoadInfo);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("test", "下载失败");
//                    }
//                }
//                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mBnsPlayerListener != null)
            mBnsPlayerListener.onPlayerCompletion();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mBnsPlayerListener != null)
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

    private String getBufferDir() {
        String bufferDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ProxyBuffer/files";
        return bufferDir;
    }
}
