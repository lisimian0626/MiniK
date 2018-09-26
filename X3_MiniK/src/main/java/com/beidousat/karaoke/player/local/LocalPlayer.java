package com.beidousat.karaoke.player.local;//package com.beidousat.karaoke.player.local;
//
//import android.media.MediaPlayer;
//import android.view.SurfaceView;
//
//import com.beidousat.karaoke.player.BnsPlayerListener;
//import com.beidousat.karaoke.player.MPlayer;
//
///**
// * Created by J Wong on 2017/5/3.
// */
//
//public class LocalPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
//
//
//    private MPlayer mPlayer;
//    private BnsPlayerListener mBnsPlayerListener;
//
//    public LocalPlayer(SurfaceView surfaceView) {
//        mPlayer = new MPlayer();
//        mPlayer.init(surfaceView);
//    }
//
//    public void open(String uri, BnsPlayerListener listener) {
//        this.mBnsPlayerListener = listener;
//        try {
//            LocalFileCache.getInstance().add(uri, uri);
//            LocalFileProxy proxy = new LocalFileProxy();
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
