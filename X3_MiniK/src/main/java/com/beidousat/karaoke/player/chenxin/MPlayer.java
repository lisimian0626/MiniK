package com.beidousat.karaoke.player.chenxin;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.TrackInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class MPlayer {
    MediaPlayer player = null;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    //    Context context;
    SurfaceHolder subsurfaceHolder = null;
    private boolean iscreated = false;
    private final static String MUSIC = ".mp3;.wav;.pcm;";
    private boolean ispread = false;
    int totaltime = 0;

    public MPlayer() {
//        this.context = context;
    }

    class SurfaceBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            iscreated = true;
            System.out.println("surfaceCreated ");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            iscreated = false;
            System.out.println("surfaceDestroyed");
        }

    }

    private MediaPlayer.OnErrorListener errlen = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
            System.out.println("OnErrorListener  " + arg0);
            return false;
        }

    };
    private OnPreparedListener listener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            // TODO Auto-generated method stub
//			player.start();
            System.out.println("onPrepared  " + mp);
            ispread = true;
            System.out.println(" ispread " + ispread);
            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mp);
        }

    };

    public void init(SurfaceView view) {
        surface = view;
        surfaceHolder = surface.getHolder();
    }

    private OnPreparedListener mOnPreparedListener;

    public boolean open(String path, MediaPlayer.OnCompletionListener comlisten, OnPreparedListener preparedListener) {
        this.mOnPreparedListener = preparedListener;
        boolean ret = false;
        System.out.println("open url " + path);
        trackIndex.clear();
        if (player != null) {
            close();
            player.setOnPreparedListener(null);
            player.release();
            player = null;
        }
        player = new MediaPlayer();
        System.out.println("open player " + player);
        player.setOnPreparedListener(listener);
        player.setOnCompletionListener(comlisten);
        player.setOnErrorListener(errlen);
        String lc = path.toLowerCase();
        if (!(lc.contains(".mp3") || lc.contains(".wav"))) {
            try {
                player.setDisplay(surfaceHolder);
                System.out.println("subsurfaceHolder " + subsurfaceHolder);
                if (null != subsurfaceHolder)
                    player.setMinorDisplay(subsurfaceHolder);
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void setPitch(int tone) {
        try {
            if (player != null)
                player.setAudioPitch(tone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setMinorDisplay(SurfaceHolder h) {
        subsurfaceHolder = h;//player.setSurface(h.getSurface());
    }

    public void setVolChannel(int channel) {
        if (player != null) {
            System.out.println("channel " + channel);
            player.setParameter(1102, channel);
        }
    }

    List<Integer> trackIndex = new ArrayList<Integer>();

    @SuppressLint("NewApi")
    public int getTackNum() {
        if (trackIndex.size() > 0)
            return trackIndex.size();
        if (player != null) {
            int total = 0;
//			trackIndex.clear();
            TrackInfo trackInfo[] = player.getTrackInfo();
            for (int i = 0; i < trackInfo.length; i++) {
                if (trackInfo[i].getTrackType() == TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    trackIndex.add(i);
                    total++;
                }
            }
            return total;
        }
        return 0;
    }

    public boolean haveMulTracks() {
        return getTackNum() > 0;
    }
//    private void mute() {
//        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
//    }
//
//    public void unmute() {
//        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamMute(AudioManager.STREAM_MUSIC, false);
//    }

    public void selectTack(int index) {
        try {
            if (player != null) {
//            mute();
                player.selectTrack(trackIndex.get(index));
//            unmute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        try {
            return player.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void start() {
        try {
            if (!player.isPlaying()) {
                player.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void pause() {
        try {
            if (player.isPlaying()) {
                player.pause();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        ispread = false;
        totaltime = 0;
        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public void setVolume(float volume) {
        try {
            if (player != null && ispread)
                player.setVolume(volume, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int seek) {
        try {
            if (player != null && ispread)
                player.seekTo(seek);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
