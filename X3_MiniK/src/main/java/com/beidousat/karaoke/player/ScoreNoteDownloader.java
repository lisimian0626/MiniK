package com.beidousat.karaoke.player;

import android.text.TextUtils;

import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

import java.io.File;

/**
 * Created by J Wong on 2016/7/7.
 */
public class ScoreNoteDownloader implements SimpleDownloadListener {


    public void downloadNote(Song song) {
        try {
            if (song != null && song.IsGradeLib != null && Integer.valueOf(song.IsGradeLib) == 1) {
                String songPath = song.SongFilePath;
                Logger.d("ScoreNoteDownloader", "downloadNote songPath:" + songPath);
                if (!TextUtils.isEmpty(songPath)) {

                    String noteFileName2 = ServerFileUtil.getFileName(songPath) + ".sec.txt";
                    String note2FileUrl = ServerFileUtil.getScoreNote2Url(song.download_url);

                    File file2 = new File(DiskFileUtil.getGradeDir(), noteFileName2);



                    if (!file2.exists()) {
//                        new SimpleDownloader().download(file2, note2FileUrl, this);
                        new SimpleDownloader().downloadFile(file2,note2FileUrl);
                    }else{
                        Logger.d("ScoreNoteDownloader", "file2："+file2.getAbsolutePath()+"   exists");
                    }

                    String noteFileName = ServerFileUtil.getFileName(songPath) + ".txt";
                    String noteFileUrl = ServerFileUtil.getScoreNoteUrl(song.download_url);
                    File file = new File(DiskFileUtil.getGradeDir(), noteFileName);
                    if (!file.exists()) {
                        new SimpleDownloader().downloadFile(file,noteFileUrl);
                    }else{
                        Logger.d("ScoreNoteDownloader", "file："+file.getAbsolutePath()+"  exists");
                    }
                }
            }
        } catch (Exception e) {
            Logger.d("ScoreNoteDownloader", "downloadNote ex:" + e.toString());
        }
    }

    @Override
    public void onDownloadFail(String url) {

    }

    @Override
    public void onDownloadCompletion(File file, String url, long fileSize) {

    }

    @Override
    public void onUpdateProgress(File mDesFile, long progress, long total) {

    }
}
