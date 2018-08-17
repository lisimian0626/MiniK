package com.beidousat.karaoke.model;

import com.google.gson.annotations.Expose;

/**
 * Created by J Wong on 2016/1/8 17:35.
 */
public class SerialPortCode {
    /**
     * ID
     */
    @Expose
    public String ID;

    /**
     * 房号
     */
    @Expose
    public String RoomCode;

    /**
     * 波特率
     */
    @Expose
    public String baudrate;

    /**
     * 服务端闪烁（下行）
     */
    @Expose
    public String ServiceLightsFlashing;
    /**
     * 服务端闪烁（上行）
     */
    @Expose
    public String ServiceLightsFlashingN;

    /**
     * 取消服务端闪烁（下行）
     */
    @Expose
    public String ServiceLightsOff;
    /**
     * 取消服务端闪烁（上行）
     */
    @Expose
    public String ServiceLightsOffN;

    /**
     * 音乐音量+（上行）
     */
    @Expose
    public String MusicVolumePlus;
    /**
     * 音乐音量-（上行）
     */
    @Expose
    public String MusicVolumeLose;

    /**
     * Mic音量+（下行）
     */
    @Expose
    public String MicrophoneVolumePlus;
    /**
     * Mic音量+（上行）
     */
    @Expose
    public String MicrophoneVolumePlusN;

    /**
     * Mic音量-（下行）
     */
    @Expose
    public String MicrophoneVolumeLose;
    /**
     * Mic音量-（下行）
     */
    @Expose
    public String MicrophoneVolumeLoseN;

    /**
     * 升调（上行）
     */
    @Expose
    public String PitchPlus;

    /**
     * 降调（上行）
     */
    @Expose
    public String PitchLose;

    /**
     * 原调（上行）
     */
    @Expose
    public String OriginalPitch;

    /**
     * 灯光-明亮（下行）
     */
    @Expose
    public String LightsBright;
    /**
     * 灯光-明亮（上行）
     */
    @Expose
    public String LightsBrightN;

    /**
     * 灯光-柔和（下行）
     */
    @Expose
    public String LightsSoft;

    /**
     * 灯光-柔和（上行）
     */
    @Expose
    public String LightsSoftN;

    /**
     * 灯光-抒情（下行）
     */
    @Expose
    public String LightsLyric;

    /**
     * 灯光-抒情（上行）
     */
    @Expose
    public String LightsLyricN;

    /**
     * 灯光-动感（下行）
     */
    @Expose
    public String LightsDynamic;

    /**
     * 灯光-动感（上行）
     */
    @Expose
    public String LightsDynamicN;

    /**
     * 音乐灯光-摇滚（慢歌）（下行）
     */
    @Expose
    public String MusicLightsRockSlow;
    /**
     * 音乐灯光-摇滚（快歌）（下行）
     */
    @Expose
    public String MusicLightsRockFast;
    /**
     * 音乐灯光-流行（慢歌）（下行）
     */
    @Expose
    public String MusicLightsFashionSlow;
    /**
     * 音乐灯光-流行（快歌）（下行）
     */
    @Expose
    public String MusicLightsFashionFast;
    /**
     * 音乐灯光-民谣（下行）
     */
    @Expose
    public String MusicLightsBallad;
    /**
     * 音乐灯光-嘻哈（下行）
     */
    @Expose
    public String MusicLightsHipHop;
    /**
     * 音乐灯光-儿歌（下行）
     */
    @Expose
    public String MusicLightsRhymes;
    /**
     * 音乐灯光-舞曲（迪斯科）（下行）
     */
    @Expose
    public String MusicLightsDanceDisco;
    /**
     * 音乐灯光-舞曲（慢摇）（下行）
     */
    @Expose
    public String MusicLightsDanceRoll;
    /**
     * 音乐灯光-革命歌曲（下行）
     */
    @Expose
    public String MusicLightsRevolution;
    /**
     * 音乐灯光-安静的独唱（下行）
     */
    @Expose
    public String MusicLightsQuietSolo;
    /**
     * 音乐灯光-大合唱（下行）
     */
    @Expose
    public String MusicLightsCantata;
    /**
     * 灯光-全开（下行）
     */
    public String LightsFullOpen;
    /**
     * 灯光-全开（上行）
     */
    @Expose
    public String LightsFullOpenN;
    /**
     * 灯光-全关（下行）
     */
    @Expose
    public String LightsFullClose;
    /**
     * 灯光-全关（上行）
     */
    @Expose
    public String LightsFullCloseN;

    /**
     * 原伴唱（上行）
     */
    @Expose
    public String OriginalAccompany;

    /**
     * 切歌（上行）
     */
    @Expose
    public String CutSongs;
    /**
     * 静音（上行）
     */
    @Expose
    public String Mute;
    /**
     * 重唱（上行）
     */
    @Expose
    public String Again;
    /**
     * 暂停/播放（上行）
     */
    @Expose
    public String PauseBegin;


}
