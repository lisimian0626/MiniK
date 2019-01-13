package com.beidousat.karaoke.player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.UpLoadDataUtil;
import com.beidousat.karaoke.model.UploadSongData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.DownloadQueueHelper;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.NetWorkUtils;
import com.beidousat.libbns.net.download.FileDownloader;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.score.KeyInfo;
import com.beidousat.score.NativeScoreRunner;
import com.beidousat.score.NoteInfo;
import com.beidousat.score.OnKeyInfoListener;
import com.beidousat.score.OnScoreListener;
import com.beidousat.score.ScoreFileUtil;
import com.beidousat.score.ScoreLineInfo;
import com.czt.mp3recorder.BnsAudioRecorder;
import com.czt.mp3recorder.IAudioRecordListener;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by J Wong on 2017/8/24.
 */

public class BnsPlayer implements IAudioRecordListener, OnKeyInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private SurfaceView mVideoSurfaceView;

    private SurfaceView mMinor;

    private int mWidth, mHeight;

    private final static String TAG = "BnsPlayer";

    private String mFilePath;
//    private String mGradeLibFile;
    private String savePath;
    private OnKeyInfoListener mOnKeyInfoListener;

    private OnScoreListener mOnScoreListener;

    private List<ScoreLineInfo> mScoreLineInfos;
    private int mScoreMode = 0;

    private final static float PROFESSIONAL_MODE = 3.0f;
    private final static float NORMAL_MODE = 5.0f;
    private float mCurrentVol = 0.5F;
    private Handler mHandlerVolInit = new Handler();
    private int mVolUpCount = 6;
    private final int VOL_UP_COUNT = 20;
    private ScheduledExecutorService mScheduledExecutorService;
    private long mPreProgress;
    private long mPreProgressCallback;
    private long mPreScoreCallBack;
    private final static long INTERVAL_PROGRESS = 1000;

    private List<Float> mShowScores = new ArrayList<Float>();
    private int mCurScoreLine = -1;
    private float mCurTotalScore;
    private String mRecordFileName;
    private BeidouPlayerListener mBeidouPlayerListener;

    private MediaPlayer mMediaPlayer;

    private boolean isPlaying = false;
    public static int PREVIEW = 1;
    public static final int NORMAL = 2;
    public BnsPlayer(SurfaceView videoSurfaceView, SurfaceView minor, int width, int height) {
        mVideoSurfaceView = videoSurfaceView;
        mMinor = minor;
        mWidth = width;
        mHeight = height;

        if (mMinor != null && mWidth > 0 && mHeight > 0) {
            mMinor.getHolder().setFixedSize(mWidth, mHeight);
        }

        createMediaPlayer();
    }

    private void createMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void playUrl(String videoUrl,String savePath,String recordFileName, int playmode) throws IOException {
        stop();
        initParameters();
        setIsRecord(recordFileName);
        mFilePath = videoUrl;
        this.savePath=savePath;
//        mGradeLibFile = gradeLibFile;
        Logger.d(TAG, "record file name:" + recordFileName + "   videoUrl:" + videoUrl+"   savaPath:"+savePath);
        if (mMediaPlayer == null) {
            createMediaPlayer();
        }
        open(videoUrl,savePath,playmode);
    }

    private void open(String uri,String savePath, int playmode) throws IOException {
        Logger.d(TAG, "uri:" + DiskFileUtil.getDiskFileByUrl(savePath));
        File file = DiskFileUtil.getDiskFileByUrl(savePath);
        if (file != null) {//存在本地文件
            Logger.d(TAG, "open local file:" + file.getAbsolutePath());
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            if (mMinor != null)
                mMediaPlayer.setMinorDisplay(mMinor.getHolder());
            mMediaPlayer.setDisplay(mVideoSurfaceView.getHolder());
            mMediaPlayer.prepare();
            UploadSongData uploadSongData = new UploadSongData();
            uploadSongData.setDuration(mMediaPlayer.getDuration());
            UpLoadDataUtil.getInstance().setmUploadSongData(uploadSongData);
//            mMediaPlayer.start();
            isPlaying = true;
            getTrack(mMediaPlayer);
        } else {//本地文件不存在
            if (playmode == PREVIEW) {
                if (!NetWorkUtils.isNetworkAvailable(Main.mMainActivity.getApplicationContext())) {
                    return;
                }
                Logger.d(TAG, "open net file:" + uri);

                mMediaPlayer.setDataSource(uri);
                if (mMinor != null)
                    mMediaPlayer.setMinorDisplay(mMinor.getHolder());
                mMediaPlayer.setDisplay(mVideoSurfaceView.getHolder());
                mMediaPlayer.prepare();
//                mMediaPlayer.start();
                isPlaying = true;
                getTrack(mMediaPlayer);
            } else if (playmode == NORMAL) {
                Log.e("test", "文件不存在");

//                EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT_DELAY, uri);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBusUtil.postSticky(EventBusId.id.PLAYER_NEXT, "");
                    }
                }, 10 * 1000);

                if (!DiskFileUtil.hasDiskStorage()) {
                    return;
                }
                List<BaseDownloadTask> mTaskList = new ArrayList<>();
                BaseDownloadTask task = com.liulishuo.filedownloader.FileDownloader.getImpl().create(uri)
                        .setPath(DiskFileUtil.getFileSavedPath(savePath));
                mTaskList.add(task);
                DownloadQueueHelper.getInstance().downloadSequentially(mTaskList);
                DownloadQueueHelper.getInstance().setOnDownloadListener(new DownloadQueueHelper.OnDownloadListener() {
                    @Override
                    public void onDownloadComplete(BaseDownloadTask task) {
                        Log.d(TAG, "download Commplete:" );
                    }

                    @Override
                    public void onDownloadTaskError(BaseDownloadTask task, Throwable e) {
                        Log.d(TAG, "download Error:" );
                    }

                    @Override
                    public void onDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "download:" + (int) ((float) soFarBytes / totalBytes * 100));
                    }

                    @Override
                    public void onDownloadTaskOver() {

                    }
                });

            }
        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.w(TAG, "onError  =========================> what:" + what + " extra:" + extra);
//        if (mBeidouPlayerListener != null) {
//            mBeidouPlayerListener.onPlayerCompletion();
//        }
//        BnsAudioRecorder.getInstance().release();
//        NativeScoreRunner.getInstance().stop();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        Logger.w(TAG, "onCompletion  =========================>");
        if (mBeidouPlayerListener != null) {
            mBeidouPlayerListener.onPlayerCompletion();
        }
        BnsAudioRecorder.getInstance().release();
        NativeScoreRunner.getInstance().stop();
    }

    private Handler handlerRecord = new Handler();
    private Runnable runnableRecord = new Runnable() {
        @Override
        public void run() {
            initScore();
            initRecord();
        }
    };


    private void initRecord() {
        if (!TextUtils.isEmpty(mRecordFileName)) {
            BnsAudioRecorder bnsAudioRecorder = BnsAudioRecorder.getInstance();
            bnsAudioRecorder.setAudioRecordListener(this);
            bnsAudioRecorder.start(mRecordFileName);
        }
    }

    private void initScore() {
        NativeScoreRunner.getInstance().setOnKeyInfoListener(this);
        NativeScoreRunner.getInstance().setScoreMode(mScoreMode);
        NativeScoreRunner.getInstance().start();
        if (mScoreLineInfos != null)
            mScoreLineInfos.clear();

        //  File fileNote = ServerFileUtil.getScoreNote(mGradeLibFile);
        File fileNote = DiskFileUtil.getScoreNote(savePath);
        Logger.d(TAG, "savePath:" + fileNote.getAbsolutePath());
        if (fileNote != null && fileNote.exists()) {
            Logger.d(TAG, "read score note file:" + fileNote.getAbsolutePath());
            ArrayList<NoteInfo> notes = ScoreFileUtil.readNoteFile(fileNote.getAbsolutePath());
            if (notes != null) {
                try {
                    NativeScoreRunner.getInstance().setNotes(notes);
                } catch (Exception e) {
                    Logger.w(TAG, "NdkJniUtil.setNotes ex:" + e.toString());
                }
            } else {
                Logger.d(TAG, "Score note notes null:");
            }
        } else {
            Logger.d(TAG, "Score note not exist:");
        }

        // File fileNote2 = ServerFileUtil.getScoreNoteSec(mGradeLibFile);
        File fileNote2 = DiskFileUtil.getScoreNoteSec(savePath);

        if (fileNote2 != null && fileNote2.exists()) {
            Logger.d(TAG, "read score note2 file:" + fileNote2.getAbsolutePath());
            mScoreLineInfos = ScoreFileUtil.readNote2File(fileNote2.getAbsolutePath());
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Logger.w(TAG, "onPrepared  =========================>");
        mp.start();
        try {
            if (mBeidouPlayerListener != null)
                mBeidouPlayerListener.onPlayerPrepared();
        } catch (Exception e) {
            Logger.w(TAG, "onPrepared ex:" + e.toString());
        }
        NativeScoreRunner.getInstance().stop();
        startTimer();
        handlerRecord.removeCallbacks(runnableRecord);
        handlerRecord.postDelayed(runnableRecord, 1000);

    }

    @Override
    public void audioByte(double[] buffer) {
        try {
            if (mScoreLineInfos != null && mScoreLineInfos.size() > 0) {
                int currentPosition = getCurrentPosition();
                NativeScoreRunner.getInstance().addData(buffer, currentPosition);
            }
        } catch (Exception e) {
            Logger.w(TAG, "audioByte ex:" + e.toString());
        }
    }

    @Override
    public void onOriginNotes(ArrayList<NoteInfo> noteInfos) {
    }

    @Override
    public void audioBytes(byte[] buffer, int bufSize) {
        if (mOnKeyInfoListener != null) {
            mOnKeyInfoListener.onRecordData(buffer, bufSize);
        }
    }


    @Override
    public void onKeyInfoCallback(KeyInfo[] infos, int totalScore) {
        if (mOnKeyInfoListener != null && (mScoreMode != 0)) {
            mOnKeyInfoListener.onKeyInfoCallback(infos, totalScore);
        }
    }

    @Override
    public void onUpdateTime(long msTime) {

    }

    @Override
    public void onRecordData(byte[] data, int bufSize) {
        if (mOnKeyInfoListener != null) {
            mOnKeyInfoListener.onRecordData(data, bufSize);
        }
    }


    public void setOnKeyInfoListener(OnKeyInfoListener l) {
        mOnKeyInfoListener = l;
    }

    public void setOnScoreListener(OnScoreListener scoreListener) {
        this.mOnScoreListener = scoreListener;
    }

    public int getCurrentPosition() {
        if (isPlaying())
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if (isPlaying())
            return mMediaPlayer.getDuration();
        return 0;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && isPlaying)
            return mMediaPlayer.isPlaying();
        return false;
    }


    private float getScoreX() {
        return mScoreMode == 2 ? PROFESSIONAL_MODE : NORMAL_MODE;
    }


    public void setScoreOn(int mode) {
        mScoreMode = mode;
        NativeScoreRunner.getInstance().setScoreMode(mScoreMode);
    }


    /**
     * 0:立体声 1：左声道 2：右声道
     *
     * @param channel
     */
    private void setVolChannel(int channel) {
        if (mMediaPlayer != null)
            mMediaPlayer.setAudioChannel(channel);
    }

    public void onAccom(int flag) {
        Logger.i(TAG, "onAccom flag:" + flag);
        try {
            if (flag == 0) {//单音轨原唱在左
                setVolChannel(2);
            } else if (flag == 1) {//单音轨原唱在右
                setVolChannel(1);
            } else if (flag == 2) {//双音轨第一轨原唱
                if (haveMulTracks())
                    mMediaPlayer.selectTrack(mTrackAudioIndex.get(1));
            } else if (flag == 3) {//双音轨第一轨伴奏
                if (haveMulTracks())
                    mMediaPlayer.selectTrack(mTrackAudioIndex.get(0));
            }
        } catch (Exception e) {
            Logger.w(TAG, "onAccom ex:" + e.toString());
        }
    }

    public void onOriginal(int flag) {
        try {
            if (flag == 0) {//单音轨原唱在左
                setVolChannel(1);
            } else if (flag == 1) {//单音轨原唱在右
                setVolChannel(2);
            } else if (flag == 2) {//双音轨第一轨原唱
                mMediaPlayer.selectTrack(mTrackAudioIndex.get(0));
            } else if (flag == 3) {//双音轨第一轨伴奏
                if (haveMulTracks())
                    mMediaPlayer.selectTrack(mTrackAudioIndex.get(1));
            }
        } catch (Exception e) {
            Logger.w(TAG, "onOriginal ex:" + e.toString());
        }
    }


    public void seekTo(int seek) {
        try {
            if (mMediaPlayer != null)
                mMediaPlayer.seekTo(seek);
        } catch (Exception e) {
            Logger.d(TAG, "seekTo ex:" + e.toString());
        }
    }


    public void setTone(int tone) {
        try {
            Logger.d(TAG, "setTone :" + tone);
            if (mMediaPlayer != null)
                mMediaPlayer.setAudioPitch(tone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setVol(float vol) {
        mCurrentVol = vol;
        startVolInit();
    }

    public void replay() throws IOException {
        String url = mFilePath;
        mFilePath = "";
        playUrl(url,savePath, mRecordFileName, BnsPlayer.NORMAL);
    }


    private void startVolInit() {
        mHandlerVolInit.removeCallbacks(runVolInit);
        mVolUpCount = 6;
        mHandlerVolInit.post(runVolInit);
    }

    private Runnable runVolInit = new Runnable() {
        @Override
        public void run() {
            if (mVolUpCount <= VOL_UP_COUNT) {
                float vol = mVolUpCount == VOL_UP_COUNT ? mCurrentVol : (mCurrentVol * mVolUpCount / VOL_UP_COUNT);
                mMediaPlayer.setVolume(vol, vol);
                mHandlerVolInit.postDelayed(this, 250);
                mVolUpCount++;
            }
        }
    };


    private void startTimer() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);
    }

    private void stopTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }


    public void setBeidouPlayerListener(BeidouPlayerListener listener) {
        mBeidouPlayerListener = listener;
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    long cur = getCurrentPosition();
                    long dur = getDuration();
                    checkAddScore(cur);
                    if (mOnKeyInfoListener != null) {
                        mOnKeyInfoListener.onUpdateTime(cur);
                    }
                    long curMilTime;
                    if (mBeidouPlayerListener != null && mPreProgress != cur
                            && ((curMilTime = System.currentTimeMillis()) - mPreProgressCallback) >= INTERVAL_PROGRESS) {
                        if (mOnScoreListener != null && curMilTime - mPreScoreCallBack >= 5000) {//每5秒回调分数
                            int showScore = getCurScore();
                            mOnScoreListener.onScoreCallback(showScore);
                            mPreScoreCallBack = curMilTime;
                        }
                        //每秒回调一次播放进度
                        mBeidouPlayerListener.onPlayerProgress(cur, dur);
                        mPreProgressCallback = curMilTime;
                        mPreProgress = cur;
                    }
                } else {
                    Logger.d(TAG, "scheduleAtFixedRate checkAddScore is not playing!");
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }


    private void checkAddScore(long playPosition) {
        try {
            if (mScoreLineInfos != null && mScoreLineInfos.size() > mCurScoreLine + 1) {
                ScoreLineInfo info = mScoreLineInfos.get(mCurScoreLine + 1);
                if (playPosition >= info.time * 1000) {
                    mCurScoreLine = mCurScoreLine + 1;
                    float oScore = info.socre;
                    float curScore = NativeScoreRunner.getInstance().getScore();
                    float deltaScore = getScoreX() * (curScore - mCurTotalScore);
                    mCurTotalScore = curScore;
                    if (oScore > 0 && deltaScore >= 0) {
                        float percentScore = (100 * deltaScore / oScore);
                        if (percentScore > 0 && percentScore < 60) {
                            percentScore = percentScore * getScoreX();
                            if (percentScore < 60) {
                                percentScore = 60;
                            }
                        }
                        float gotScore = deltaScore > oScore ? 100 : (percentScore > 100 ? 100 : percentScore);
                        mShowScores.add(gotScore);
                    }
                }
            }
        } catch (Exception e) {
            Logger.w(TAG, "NdkJniUtil.setNotes ex:" + e.toString());
        }
    }


    public synchronized int getCurScore() {
        int last = mShowScores.size();
        if (last > 0) {
            float total = 0;
            for (float s : mShowScores) {
                total = total + s;
            }
            int size = mShowScores.size();
            float average = total / size;
            return (int) average;
        }
        return 0;
    }


    private void setIsRecord(String recordFileName) {
        mRecordFileName = recordFileName;
    }

    public void stop() {
        isPlaying = false;
        mMediaPlayer.release();
        mMediaPlayer = null;
        Logger.i(TAG, "release player");
        BnsAudioRecorder.getInstance().release();
        stopTimer();
    }

    public void play() {
        BnsAudioRecorder.getInstance().pause(false);
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    public void pause() {
        try {
            BnsAudioRecorder.getInstance().pause(true);
            if (mMediaPlayer != null)
                mMediaPlayer.pause();
        } catch (Exception e) {
            Logger.d(TAG, "pause ex:" + e.toString());
        }
    }

    public void volOn() {
        startVolInit();
    }

    public void volOff() {
        mHandlerVolInit.removeCallbacks(runVolInit);
        try {
            mMediaPlayer.setVolume(0.0F, 0.0F);
        } catch (Exception e) {
            Logger.d(TAG, "volOff ex:" + e.toString());
        }
    }

    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    private Vector<Integer> mTrackAudioIndex = new Vector<Integer>();

    public boolean haveMulTracks() {
        return mTrackAudioIndex.size() > 1;
    }

    public int getTrack(MediaPlayer player) {
        mTrackAudioIndex.clear();
        MediaPlayer.TrackInfo[] trackInfos = player.getTrackInfo();
        if (trackInfos != null && trackInfos.length > 0) {
            for (int j = 0; j < trackInfos.length; j++) {
                MediaPlayer.TrackInfo info = trackInfos[j];
                if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    Logger.d(TAG, "add  mTrackAudioIndex :" + j);
                    mTrackAudioIndex.add(j);
                }
            }
        } else {
            if (trackInfos == null)
                Logger.w(TAG, "trackInfos = null");
            else
                Logger.w(TAG, "trackInfos.length = " + trackInfos.length);
        }
        return mTrackAudioIndex.size();
    }


    private void initParameters() {
        mPreScoreCallBack = 0;
        mPreProgress = 0;
        mCurTotalScore = 0;
        mPreProgressCallback = 0;
        mCurScoreLine = -1;
        if (mShowScores != null)
            mShowScores.clear();

        if (mScoreLineInfos != null)
            mScoreLineInfos.clear();

        if (mOnScoreListener != null) {
            mOnScoreListener.onScoreCallback(0);
        }
    }


}
