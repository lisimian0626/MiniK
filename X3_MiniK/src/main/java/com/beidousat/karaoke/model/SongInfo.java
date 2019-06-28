package com.beidousat.karaoke.model;

public class SongInfo {

    /**
     * ID : 19000
     * SimpName : 想念是一首愉快的诗
     * SongFileName : 10825932.mp4
     * SongFilePath : data/song/yc1/10825932.mp4
     * Volume : 97
     * AudioTrack : 1
     * SongVersion : 风景
     * LightingEffect : 13
     * IsGradeLib : null
     * SingerName : 洪小乔
     * SingerID : 9846
     * PreviewPath :
     * IsClear : 0
     * object_name : data1_to_cube/song/yc1/10825932.mp4
     * download_url : http://media.imtbox.com/data1_to_cube/song/yc1/10825932.mp4
     * IsAdSong : 0
     * ADID : 0
     */

    private String ID;
    private String SimpName;
    private String SongFileName;
    private String SongFilePath;
    private String Volume;
    private String AudioTrack;
    private String SongVersion;
    private String LightingEffect;
    private Object IsGradeLib;
    private String SingerName;
    private String SingerID;
    private String PreviewPath;
    private int IsClear;
    private String object_name;
    private String download_url;
    private int IsAdSong;
    private int ADID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSimpName() {
        return SimpName;
    }

    public void setSimpName(String SimpName) {
        this.SimpName = SimpName;
    }

    public String getSongFileName() {
        return SongFileName;
    }

    public void setSongFileName(String SongFileName) {
        this.SongFileName = SongFileName;
    }

    public String getSongFilePath() {
        return SongFilePath;
    }

    public void setSongFilePath(String SongFilePath) {
        this.SongFilePath = SongFilePath;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String Volume) {
        this.Volume = Volume;
    }

    public String getAudioTrack() {
        return AudioTrack;
    }

    public void setAudioTrack(String AudioTrack) {
        this.AudioTrack = AudioTrack;
    }

    public String getSongVersion() {
        return SongVersion;
    }

    public void setSongVersion(String SongVersion) {
        this.SongVersion = SongVersion;
    }

    public String getLightingEffect() {
        return LightingEffect;
    }

    public void setLightingEffect(String LightingEffect) {
        this.LightingEffect = LightingEffect;
    }

    public Object getIsGradeLib() {
        return IsGradeLib;
    }

    public void setIsGradeLib(Object IsGradeLib) {
        this.IsGradeLib = IsGradeLib;
    }

    public String getSingerName() {
        return SingerName;
    }

    public void setSingerName(String SingerName) {
        this.SingerName = SingerName;
    }

    public String getSingerID() {
        return SingerID;
    }

    public void setSingerID(String SingerID) {
        this.SingerID = SingerID;
    }

    public String getPreviewPath() {
        return PreviewPath;
    }

    public void setPreviewPath(String PreviewPath) {
        this.PreviewPath = PreviewPath;
    }

    public int getIsClear() {
        return IsClear;
    }

    public void setIsClear(int IsClear) {
        this.IsClear = IsClear;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getIsAdSong() {
        return IsAdSong;
    }

    public void setIsAdSong(int IsAdSong) {
        this.IsAdSong = IsAdSong;
    }

    public int getADID() {
        return ADID;
    }

    public void setADID(int ADID) {
        this.ADID = ADID;
    }

    public Song toSong(){
        Song song=new Song();
        song.SingerID=SingerID;
        song.SongFilePath=SongFilePath;
        song.Volume=Integer.valueOf(Volume);
        song.AudioTrack=Integer.valueOf(AudioTrack);
        song.IsAdSong=IsAdSong;
        song.ADID=String.valueOf(ADID);
        song.PreviewPath=PreviewPath;
        song.download_url=download_url;
        song.IsClear=IsClear;
        return song;
    }
}
