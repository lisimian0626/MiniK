package com.beidousat.karaoke.player.online;//package com.beidousat.karaoke.player.online;
//
//import android.media.MediaPlayer;
//import android.view.SurfaceView;
//
//import com.beidousat.karaoke.player.BnsPlayerListener;
//import com.beidousat.karaoke.player.MPlayer;
//import com.beidousat.karaoke.player.proxy.Utils;
//
///**
// * Created by J Wong on 2017/5/3.
// */
//
//public class LinePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
//
//
//    private MPlayer mPlayer;
//    private BnsPlayerListener mBnsPlayerListener;
//
//    public LinePlayer(SurfaceView surfaceView) {
//        mPlayer = new MPlayer();
//        mPlayer.init(surfaceView);
//    }
//
//    public void open(String uri, BnsPlayerListener listener) {
//        this.mBnsPlayerListener = listener;
//        try {
//            CacheFile.getInstance().add(uri, uri);
//            HttpGetProxy proxy = new HttpGetProxy();
//            proxy.startDownload(uri);
//            String url = proxy.getLocalURL();
//            mPlayer.open(url, this, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//        mBnsPlayerListener.onPlayerCompletion();
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        mBnsPlayerListener.onPlayerPrepared();
//    }
//
//    public void setVolume(float volume) {
//        mPlayer.setVolume(volume);
//    }
//
//    public void close() {
//        try {
//            if (mPlayer != null) {
//                mPlayer.close();
//                mPlayer = null;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//}
