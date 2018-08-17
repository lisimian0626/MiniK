//package com.beidousat.karaoke.model;
//
//import com.google.gson.Gson;
//import com.google.gson.annotations.Expose;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Id;
//
//import java.io.Serializable;
//import org.greenrobot.greendao.annotation.Generated;
//
///**
// * Created by J Wong on 2015/10/10 11:13.
// */
//@Entity
//public class Newsong {
//
//    @Id(autoincrement = true)
//    private Long id;
//    public String SingerID;
//
//
//    public String SongFilePath;
//
//
//    public int Volume;
//
//
//    public int AudioTrack;
//
//
//
//    public int IsAdSong;
//
//
//    public String ADID;
//
//
//    public String PreviewPath;
//
//    public int Hot;
//
//    /***
//     * 0:song  1:movie 2:live
//     */
//    public int playType;
//
//    public int score;
//
//    public int IsClear;
//
//    public boolean isPrior;
//
//    public String downloadErro;
//
//    @Generated(hash = 1604070056)
//    public Newsong(Long id, String SingerID, String SongFilePath, int Volume,
//            int AudioTrack, int IsAdSong, String ADID, String PreviewPath, int Hot,
//            int playType, int score, int IsClear, boolean isPrior,
//            String downloadErro) {
//        this.id = id;
//        this.SingerID = SingerID;
//        this.SongFilePath = SongFilePath;
//        this.Volume = Volume;
//        this.AudioTrack = AudioTrack;
//        this.IsAdSong = IsAdSong;
//        this.ADID = ADID;
//        this.PreviewPath = PreviewPath;
//        this.Hot = Hot;
//        this.playType = playType;
//        this.score = score;
//        this.IsClear = IsClear;
//        this.isPrior = isPrior;
//        this.downloadErro = downloadErro;
//    }
//
//    @Generated(hash = 415870789)
//    public Newsong() {
//    }
//
//    public Long getId() {
//        return this.id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getSingerID() {
//        return this.SingerID;
//    }
//
//    public void setSingerID(String SingerID) {
//        this.SingerID = SingerID;
//    }
//
//    public String getSongFilePath() {
//        return this.SongFilePath;
//    }
//
//    public void setSongFilePath(String SongFilePath) {
//        this.SongFilePath = SongFilePath;
//    }
//
//    public int getVolume() {
//        return this.Volume;
//    }
//
//    public void setVolume(int Volume) {
//        this.Volume = Volume;
//    }
//
//    public int getAudioTrack() {
//        return this.AudioTrack;
//    }
//
//    public void setAudioTrack(int AudioTrack) {
//        this.AudioTrack = AudioTrack;
//    }
//
//    public int getIsAdSong() {
//        return this.IsAdSong;
//    }
//
//    public void setIsAdSong(int IsAdSong) {
//        this.IsAdSong = IsAdSong;
//    }
//
//    public String getADID() {
//        return this.ADID;
//    }
//
//    public void setADID(String ADID) {
//        this.ADID = ADID;
//    }
//
//    public String getPreviewPath() {
//        return this.PreviewPath;
//    }
//
//    public void setPreviewPath(String PreviewPath) {
//        this.PreviewPath = PreviewPath;
//    }
//
//    public int getHot() {
//        return this.Hot;
//    }
//
//    public void setHot(int Hot) {
//        this.Hot = Hot;
//    }
//
//    public int getPlayType() {
//        return this.playType;
//    }
//
//    public void setPlayType(int playType) {
//        this.playType = playType;
//    }
//
//    public int getScore() {
//        return this.score;
//    }
//
//    public void setScore(int score) {
//        this.score = score;
//    }
//
//    public int getIsClear() {
//        return this.IsClear;
//    }
//
//    public void setIsClear(int IsClear) {
//        this.IsClear = IsClear;
//    }
//
//    public boolean getIsPrior() {
//        return this.isPrior;
//    }
//
//    public void setIsPrior(boolean isPrior) {
//        this.isPrior = isPrior;
//    }
//
//    public String getDownloadErro() {
//        return this.downloadErro;
//    }
//
//    public void setDownloadErro(String downloadErro) {
//        this.downloadErro = downloadErro;
//    }
//
//
//}
