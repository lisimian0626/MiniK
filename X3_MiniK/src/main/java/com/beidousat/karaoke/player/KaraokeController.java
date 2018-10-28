package com.beidousat.karaoke.player;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;

import com.beidousat.karaoke.model.PlayerStatus;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;

/**
 * Created by J Wong on 2015/11/5 19:49.
 */
public class KaraokeController {

    private static KaraokeController mKaraokeController;

    private PlayerStatus mPlayerStatus;
    private Context mContext;
    //    private SerialController serialController;
    private final static int TONE_INTERVAL = 5;
    private long mPreNextTime = 0;

    public static KaraokeController getInstance(Context context) {
        if (mKaraokeController == null) {
            mKaraokeController = new KaraokeController(context);
        }
        return mKaraokeController;
    }

    private KaraokeController(Context context) {
        mContext = context;
        mPlayerStatus = new PlayerStatus();
        mPlayerStatus.tone = 100;//901方案100为原调

//        serialController = SerialController.getInstance(mContext.getApplicationContext());
    }


    public PlayerStatus getPlayerStatus() {
        return mPlayerStatus;
    }

    public void setPlayerStatus(PlayerStatus status) {
        mPlayerStatus = status;
    }

    public void next() {
        long curTime;
        if ((curTime = System.currentTimeMillis()) - mPreNextTime >= 2500) {//限制快速切歌
            mPlayerStatus.isMute = false;
            EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT, "");
//            broadcastPlayerStatus();
            EventBusUtil.postSticky(EventBusId.id.PLAYER_STATUS_CHANGED, mPlayerStatus);
            mPreNextTime = curTime;
        }
    }


    /**
     * @return isPlaying
     */
    public boolean playPause() {
        if (mPlayerStatus.playingType == 1 || mPlayerStatus.playingType == 2 || mPlayerStatus.playingType == 4) {//歌曲、电影、贴片
            boolean isPlaying = mPlayerStatus.isPlaying;
            if (isPlaying) {
                pause();
            } else {
                play();
            }
            EventBusUtil.postSticky(EventBusId.id.PLAYER_STATUS_CHANGED, mPlayerStatus);

        }
        return mPlayerStatus.isPlaying;
    }

    public boolean play() {
        mPlayerStatus.isPlaying = true;
        EventBusUtil.postSticky(EventBusId.id.PLAYER_PLAY, "");
//        broadcastPlayerStatus();
        return true;
    }

    public boolean pause() {
        mPlayerStatus.isPlaying = false;
        EventBusUtil.postSticky(EventBusId.id.PLAYER_PAUSE2, "");
//        broadcastPlayerStatus();
        return false;
    }


    public void replay() {
        long curTime;
        if ((curTime = System.currentTimeMillis()) - mPreNextTime >= 2500) {//限制快速切歌
            EventBusUtil.postSticky(EventBusId.id.PLAYER_REPLAY, "");
            mPlayerStatus.isPlaying = true;
            mPlayerStatus.isMute = false;
//            broadcastPlayerStatus();
            EventBusUtil.postSticky(EventBusId.id.PLAYER_STATUS_CHANGED, mPlayerStatus);
            mPreNextTime = curTime;
        }
    }

    public int setScoreMode(int mode) {
        mPlayerStatus.scoreMode = mode;
        EventBusUtil.postSticky(EventBusId.id.PLAYER_SCORE_ON_OFF, mPlayerStatus.scoreMode);
//        broadcastPlayerStatus();
        return mPlayerStatus.scoreMode;
    }

    public boolean originalAccom() {
        if (mPlayerStatus.playingType == 1) {//歌曲才可以切换原伴唱
            mPlayerStatus.originOn = !mPlayerStatus.originOn;
            if (mPlayerStatus.originOn) {
                EventBusUtil.postSticky(EventBusId.id.PLAYER_ORIGINAL, "");
            } else {
                EventBusUtil.postSticky(EventBusId.id.PLAYER_ACCOM, "");
            }
//            broadcastPlayerStatus();
            EventBusUtil.postSticky(EventBusId.id.PLAYER_STATUS_CHANGED, mPlayerStatus);
        }
        return mPlayerStatus.originOn;
    }


    public void micVolUp() {
        EventBusUtil.postSticky(EventBusId.id.MIC_UP, "");
        SerialController.getInstance(mContext).onMicUp();
    }


    public void micVolDown() {
        EventBusUtil.postSticky(EventBusId.id.MIC_DOWN, "");
        SerialController.getInstance(mContext).onMicDown();
    }

    public void reverbUp() {
        EventBusUtil.postSticky(EventBusId.id.SERIAL_REVERB_UP, "");
        SerialController.getInstance(mContext).onReverbUp();
    }

    public void reverbDown() {
        EventBusUtil.postSticky(EventBusId.id.SERIAL_REVERB_DOWN, "");
        SerialController.getInstance(mContext).onReverbDown();
    }

    public long reset() {
//        SerialController.getInstance(mContext).onReset();
        SerialController.getInstance(mContext).resetMic();

        handlerReadSerial.postDelayed(new Runnable() {
            @Override
            public void run() {
                SerialController.getInstance(mContext).resetEff();
            }
        }, 1500);

        readMicVol(3000);

        readEffVol(4500);

        return 4500;
    }

    public void readMicVol(long delay) {
        handlerReadSerial.removeCallbacks(runnableReadMic);
        handlerReadSerial.postDelayed(runnableReadMic, delay);
    }

    public void readEffVol(long delay) {
        handlerReadSerial.removeCallbacks(runnableReadEff);
        handlerReadSerial.postDelayed(runnableReadEff, delay);
    }

    public void setMicMute(boolean mute) {
        SerialController.getInstance(mContext).setMicMute(mute);

        readMicVol(1000);
    }

    public int musicVolUp() {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //音量控制,初始化定义
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (current < maxVolume) {
            current++;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        }
        EventBusUtil.postSticky(EventBusId.id.VOL_UP, "");
        mPlayerStatus.volMusic = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        broadcastPlayerStatus();

        return mPlayerStatus.volMusic;
    }

    public void mute(){
        //音量控制,初始化定义
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(current!=0){
             mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            EventBusUtil.postSticky(EventBusId.id.TONE_MUTE, "");
            mPlayerStatus.volMusic = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

    }
    /**
     * @return
     */
    public int musicVolDown() {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //最大音量
        if (current > 0) {
            current--;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        }
        EventBusUtil.postSticky(EventBusId.id.VOL_DOWN, "");
        mPlayerStatus.volMusic = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        broadcastPlayerStatus();
        return mPlayerStatus.volMusic;
    }


    public float toneDefault() {
        mPlayerStatus.tone = 100;
//            serialController.onToneDefault();
        EventBusUtil.postSticky(EventBusId.id.TONE_DEFAULT, mPlayerStatus.tone);
        return mPlayerStatus.tone;

    }

    public float toneUp() {
        int vol = mPlayerStatus.tone;
        if (vol < 200) {
            mPlayerStatus.tone = vol + TONE_INTERVAL;
            EventBusUtil.postSticky(EventBusId.id.TONE_UP, mPlayerStatus.tone);
        }
        mPlayerStatus.tone = vol + TONE_INTERVAL;
        EventBusUtil.postSticky(EventBusId.id.TONE_UP, mPlayerStatus.tone);
        return mPlayerStatus.tone;
    }


    /**
     * @return
     */
    public float toneDown() {
        int vol = mPlayerStatus.tone;
        if (vol > 50) {
            mPlayerStatus.tone = vol - TONE_INTERVAL;
            EventBusUtil.postSticky(EventBusId.id.TONE_DOWN, mPlayerStatus.tone);
        }
        mPlayerStatus.tone = vol - TONE_INTERVAL;
        EventBusUtil.postSticky(EventBusId.id.TONE_DOWN, mPlayerStatus.tone);
        return mPlayerStatus.tone;
    }

    /**
     * @return isMute
     */

    public boolean mute(boolean mute) {
        mPlayerStatus.isMute = mute;
        EventBusUtil.postSticky(mPlayerStatus.isMute ? EventBusId.id.PLAYER_VOL_OFF : EventBusId.id.PLAYER_VOL_ON, "");
        EventBusUtil.postSticky(EventBusId.id.PLAYER_STATUS_CHANGED, mPlayerStatus);
        return mPlayerStatus.isMute;
    }

    private Handler handlerReadSerial = new Handler() {

    };

    private Runnable runnableReadMic = new Runnable() {
        @Override
        public void run() {
            SerialController.getInstance(mContext).readMicVol();
        }
    };

    private Runnable runnableReadEff = new Runnable() {
        @Override
        public void run() {
            SerialController.getInstance(mContext).readEffVol();

        }
    };

}
