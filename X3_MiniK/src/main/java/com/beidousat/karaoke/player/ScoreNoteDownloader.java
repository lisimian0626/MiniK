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

                    Logger.d("ScoreNoteDownloader", "downloadNote note2FileName:" + noteFileName2
                            + "  noteFileUrl:" + note2FileUrl + "  file path  " + file2.getAbsolutePath());

                    if (!file2.exists()) {
//                        new SimpleDownloader().download(file2, note2FileUrl, this);
                        new SimpleDownloader().downloadFile(file2,note2FileUrl);
                    }

                    String noteFileName = ServerFileUtil.getFileName(songPath) + ".txt";
                    String noteFileUrl = ServerFileUtil.getScoreNoteUrl(song.download_url);
                    File file = new File(DiskFileUtil.getGradeDir(), noteFileName);
                    Logger.d("ScoreNoteDownloader", "downloadNote noteFileName:" + noteFileName
                            + "  noteFileUrl:" + noteFileUrl + "  file path  " + file.getAbsolutePath());
                    if (!file.exists()) {
                        new SimpleDownloader().downloadFile(file2,note2FileUrl);
//                        new SimpleDownloader().download(file, noteFileUrl, this);
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
