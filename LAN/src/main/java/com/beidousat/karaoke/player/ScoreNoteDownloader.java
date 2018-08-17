package com.beidousat.karaoke.player;

import android.text.TextUtils;

import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.net.download.SimpleDownloadListener;
import com.beidousat.libbns.net.download.SimpleDownloader;
import com.beidousat.libbns.util.KaraokeSdHelper;
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
                    String note2FileUrl = ServerFileUtil.getScoreNote2Url(songPath);

                    File file2 = new File(KaraokeSdHelper.getNote(), noteFileName2);

                    Logger.d("ScoreNoteDownloader", "downloadNote note2FileName:" + noteFileName2
                            + "  noteFileUrl:" + note2FileUrl + "  file path  " + file2.getAbsolutePath());

                    if (!file2.exists()) {
//                        SimpleDownloader simpleDownloader = new SimpleDownloader();
//                        simpleDownloader.download(file2, note2FileUrl, this);
                    } else {
                        Logger.d("ScoreNoteDownloader", "downloadNote 2 file exist:");
                        file2.delete();
                    }

                    new SimpleDownloader().download(file2, note2FileUrl, this);

                    String noteFileName = ServerFileUtil.getFileName(songPath) + ".txt";
                    String noteFileUrl = ServerFileUtil.getScoreNoteUrl(songPath);
                    File file = new File(KaraokeSdHelper.getNote(), noteFileName);
                    Logger.d("ScoreNoteDownloader", "downloadNote noteFileName:" + noteFileName
                            + "  noteFileUrl:" + noteFileUrl + "  file path  " + file.getAbsolutePath());
                    if (!file.exists()) {
//                        SimpleDownloader simpleDownloader = new SimpleDownloader();
//                        simpleDownloader.download(file, noteFileUrl, this);
                    } else {
                        file.delete();
                        Logger.d("ScoreNoteDownloader", "downloadNote file exist:");
                    }
                    SimpleDownloader simpleDownloader = new SimpleDownloader();
                    simpleDownloader.download(file, noteFileUrl, this);
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
