package com.beidousat.karaoke.player;

import android.content.Context;
import android.media.AudioManager;

import com.beidousat.karaoke.model.PlayerStatus;
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
    private final static int TONE_INTERVAL = 1;
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
//            serialController.onMicVolUp();
    }


    public void micVolDown() {
        EventBusUtil.postSticky(EventBusId.id.MIC_DOWN, "");
//            serialController.onMicVolDown();
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
        mPlayerStatus.tone = 0;
//            serialController.onToneDefault();
        EventBusUtil.postSticky(EventBusId.id.TONE_DEFAULT, mPlayerStatus.tone);
        return mPlayerStatus.tone;

    }

    public float toneUp() {
        int vol = mPlayerStatus.tone;
        mPlayerStatus.tone = vol + TONE_INTERVAL;
//            serialController.onToneUp();
        EventBusUtil.postSticky(EventBusId.id.TONE_UP, mPlayerStatus.tone);
        return mPlayerStatus.tone;
    }


    /**
     * @return
     */
    public float toneDown() {
        int vol = mPlayerStatus.tone;
        mPlayerStatus.tone = vol - TONE_INTERVAL;
//            serialController.onToneDown();
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

    /**
     * @param mode
     * @return light mode
     * 0:灯光-自动
     * 1:灯光-明亮
     * 2:灯光-柔和
     * 3:灯光-浪漫
     * 4:灯光-浪漫
     * 5:灯光-开关
     */
//    public int lightMode(int mode) {
//        switch (mode) {
//            case 0:
//                mPlayerStatus.lightMode = mode;
//                break;
//            case 1:
////                serialController.onLightBright();
//                mPlayerStatus.lightMode = mode;
//                break;
//            case 2:
////                serialController.onLightSoft();
//                mPlayerStatus.lightMode = mode;
//                break;
//            case 3:
////                serialController.onLightRomantic();
//                mPlayerStatus.lightMode = mode;
//                break;
//            case 4:
////                serialController.onLightMotion();
//                mPlayerStatus.lightMode = mode;
//                break;
//            case 5:
//                mPlayerStatus.isLightOn = !mPlayerStatus.isLightOn;
//                if (mPlayerStatus.isLightOn) {
//                    mPlayerStatus.lightMode = 5;
////                    serialController.onLightOn();
//                } else {
//                    mPlayerStatus.lightMode = 6;
////                    serialController.onLightOff();
//                }
//                break;
//        }
////        broadcastPlayerStatus();
//        EventBusUtil.postSticky(EventBusId.id.LIGHT_MODE, mPlayerStatus.lightMode);
//
//        return mPlayerStatus.lightMode;
//    }

    /**
     * 呼叫服务
     *
     * @param mode 0：呼叫服务员；1：DJ呼叫；2：清洁呼叫；3：保安呼叫；4：买单呼叫；5：催单呼叫；
     *             6：唛套呼叫；7：唛套呼叫；
     */
//    public void onService(int mode, boolean sendSerial) {
//        mPlayerStatus.serviceMode = mode;
//            if (sendSerial)
//                serialController.onService();
//            new ServiceCallHelper(mContext).callService(mode);
//        EventBusUtil.postSticky(EventBusId.id.SERVICE_MODE, mPlayerStatus.serviceMode);
//            broadcastPlayerStatus();
//    }

//    public void cancelService(boolean sendSerial) {
//        mPlayerStatus.serviceMode = -1;
//            if (sendSerial)
//                serialController.stopService();
//            new ServiceCallHelper(mContext).cancelService();
//        EventBusUtil.postSticky(EventBusId.id.SERVICE_MODE, mPlayerStatus.serviceMode);
//            broadcastPlayerStatus();
//    }

//    public void onSeekTo(float percent) {
//        boolean overTime = RoomInfo.getInstance().isOverTime();
//        Logger.i(getClass().getSimpleName(), "PlayerStatus.playingType:" + mPlayerStatus.playingType + " overTime: " + overTime);
//        EventBusUtil.postSticky(EventBusId.id.PLAYER_SEEK_TO, percent);
//        if (mPlayerStatus.playingType != 0 && !overTime && mPlayerStatus.playingType != 4) {
//            EventBusUtil.postSticky(EventBusId.id.PLAYER_SEEK_TO, percent);
//        } else {
//            Logger.e(getClass().getSimpleName(), "can not do onSeekTo");
//        }
//    }


//    public void broadcastPlayerStatus() {
//        ZmqOperationUtil.broadcastButtonStatus(mContext, new ButtonStatus(mPlayerStatus.originOn, !mPlayerStatus.isPlaying,
//                mPlayerStatus.isMute, mPlayerStatus.serviceMode >= 0, mPlayerStatus.scoreMode, mPlayerStatus.serviceMode, mPlayerStatus.lightMode));
//        SocketOperationUtil.broadcastPlayerStatus(mPlayerStatus);
//    }

//    public void onTimeOver() {
//        int playingType = mKaraokeController.getPlayerStatus().playingType;
//        if (playingType == 0 || playingType == 2 || playingType == 3) {//正在播放广告、电影、直播时直接停
//            ChooseSongs.getInstance(mContext.getApplicationContext()).cleanData();
//            if (playingType != 0)
//                next();
//        } else {//清除除第一首外所有已点歌曲
//            ChooseSongs.getInstance(mContext.getApplicationContext()).cleanDataNotTop();
//        }
//    }


//    public void onMusicLight(int type) {
//        serialController.onMusicLight(type);
//    }

//    public void sendInitToSecondary() {
//        ChooseSongs.getInstance(mContext).syncChoose();
//    }

//    public void getInitFromMain() {
//        sendOperationToMainVod(SocketCode.INIT_SECONDARY);
//    }


//    public void onHdmiBlack(boolean isHdmiBlack) {
//        if (mHdmiBlack != isHdmiBlack) {
//            this.mHdmiBlack = isHdmiBlack;
//            if (mDeviceHelper.isMainVod()) {
//                EventBusUtil.postSticky(EventBusId.id.HDMI_BLACK, mHdmiBlack);
//            } else {
//                SocketOperationUtil.sendHdmiBlack2Main(mHdmiBlack);
//            }
//        }
//    }
}
