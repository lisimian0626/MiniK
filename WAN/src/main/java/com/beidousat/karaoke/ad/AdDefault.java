package com.beidousat.karaoke.ad;


import com.beidousat.libbns.model.Ad;

/**
 * Created by J Wong on 2016/1/12 10:58.
 */
public class AdDefault {

    public static Ad getCornerDefaultAd() {
        Ad ad = new Ad();
        ad.ADContent = "";
        return ad;
    }

    public static Ad getDoorDefaultAd() {
        Ad ad = new Ad();
        ad.ADMovie = "data/ad/movie/ad_default.mp4";
        ad.ADPics = new String[]{"data/ad/img/ad_m1_default.webp"};
        return ad;
    }

    public static Ad getPatchDefaultAd() {
        Ad ad = new Ad();
        ad.ADMovie = "data/ad/movie/ad_default.mp4";
        ad.ADContent = "data/ad/movie/ad_default.mp4";
        return ad;
    }

    public static Ad getScreenDefaultAd() {
        Ad ad = new Ad();
        ad.ADContent = "data/ad/img/ad_default.webp";
        return ad;
    }


    public static Ad getPublicServiceAd() {
        Ad ad = new Ad();
        ad.ADMovie = "data/ad/movie/ad_starting.mp4";
        ad.ADContent = "data/ad/movie/ad_starting.mp4";
        return ad;
    }


    public static String getScoreResultVideo() {
        return "data/ad/movie/score_result.mp4";
    }
}
