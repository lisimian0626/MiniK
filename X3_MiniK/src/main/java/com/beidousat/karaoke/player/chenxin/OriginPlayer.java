package com.beidousat.karaoke.player.chenxin;

/**
 * Created by J Wong on 2015/11/21 15:15.
 */

import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.BnsPlayer;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.ServerConfigData;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OriginPlayer implements IAudioRecordListener, OnKeyInfoListener, BeidouPlayerListener {

    private final static String TAG = OriginPlayer.class.getSimpleName();

    private BNSPlayer mMediaPlayer;
    private BeidouPlayerListener mBeidouPlayerListener;
    private SurfaceView mSurfaceView;
    private SurfaceView mSurface_minor;
    private String mFilePath;
    private String savePath;
    private String mNextPath;
    private boolean isPlayerReset;

    private final static long INTERVAL_PROGRESS = 1000;

    private float mCurrentVol = 0.5F;

    private int mScoreMode = 0;
    private String mRecordFileName;
    private OnKeyInfoListener mOnKeyInfoListener;
    private OnScoreListener mOnScoreListener;
    private int playMode;
    private long mPreScoreCallBack;
    private int mCurScoreLine = -1;
    private float mCurTotalScore;
    private List<Float> mShowScores = new ArrayList<Float>();
    private List<ScoreLineInfo> mScoreLineInfos;
    private long mPreProgress;
    private long mPreProgressCallback;
    private final static float PROFESSIONAL_MODE = 1.5f;
    private final static float NORMAL_MODE = 2.0f;
    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;
    private Thread mThreadPlayer = null;
    private final int VOL_UP_COUNT = 20;
    private int mVolUpCount = 6;
    private Handler mHandlerVolInit = new Handler();
    private ScheduledExecutorService mScheduledExecutorService;


    public OriginPlayer(SurfaceView surfaceView, SurfaceView minor, BeidouPlayerListener l, int surfaceWidth, int surfaceHeight) {
        mBeidouPlayerListener = l;
        mSurfaceView = surfaceView;
        this.mSurfaceWidth = surfaceWidth;
        this.mSurfaceHeight = surfaceHeight;
        mSurface_minor=minor;
        initPlayer();
    }

    private float getScoreX() {
        return mScoreMode == 2 ? PROFESSIONAL_MODE : NORMAL_MODE;
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

    private void checkAddScore(long playPosition) {
        try {
            if (mScoreLineInfos != null && mScoreLineInfos.size() > mCurScoreLine + 1) {
                ScoreLineInfo info = mScoreLineInfos.get(mCurScoreLine + 1);
                if (playPosition >= info.time * 1000) {
                    mCurScoreLine = mCurScoreLine + 1;
                    float oScore = info.socre;
                    float curScore = NativeScoreRunner.getInstance().getScore();

                    float deltaScore = getScoreX() * (curScore - mCurTotalScore);
                    Logger.d(TAG, "checkAddScore oScore:" + oScore + " deltaScore:" + deltaScore + " curScore:" + curScore);

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
//                        float gotScore = (100 * deltaScore / oScore);
                        Logger.d(TAG, "checkAddScore gotScore:" + gotScore);
                        mShowScores.add(gotScore);
                    }
                }
            }
        } catch (Exception e) {
            Logger.w(TAG, "NdkJniUtil.setNotes ex:" + e.toString());
        }
    }


    private void initPlayer() {

        if (mSurfaceView != null && mSurfaceWidth > 0 && mSurfaceHeight > 0) {
            mSurfaceView.getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
        }
        mMediaPlayer = new BNSPlayer(mSurfaceView,mSurface_minor);

//        if (mSurfaceMinor != null) {
//            mMediaPlayer.setMinorDisplay(mSurfaceMinor.getHolder());
//        } else {
//            mMediaPlayer.setMinorDisplay(null);
//        }
    }


    public void play() {
        if (mMediaPlayer != null && !isPlayerReset) {
            BnsAudioRecorder.getInstance().pause(false);
            mMediaPlayer.start();
        }
    }

    public void playUrl(String videoUrl,String savePath,String nexturi,String recordFileName,int playMode) {
        Common.curSongPath=savePath;
        Logger.d(TAG, "record file name:" + recordFileName + "   videoUrl:" + videoUrl+"    savePath:"+savePath+"   nexturi:"+nexturi);
        setIsRecord(recordFileName);
        playUri(videoUrl,savePath,nexturi,recordFileName,playMode);
    }

    private void playUri(String uri,String savePath,String nexturi,String recordFileName,int playMode) {
        BnsAudioRecorder.getInstance().release();
        initParameters();
        setIsRecord(recordFileName);
        this.playMode=playMode;
        mFilePath = uri;
        mNextPath=nexturi;
        this.savePath=savePath;
        try {
            Logger.i(TAG, "play url :" + mFilePath);
            if (mMediaPlayer == null)
                initPlayer();
            openHttp(ServerFileUtil.getFileUrl(uri),savePath,nexturi,playMode);
        } catch (Exception e) {
            Logger.w(TAG, "Exception :" + e.toString());
        }
    }


    private void openHttp(String url, final String savePath, final String nextUrl, final int playMode) {

        if (null != mThreadPlayer) {
            try {
                mThreadPlayer.join();
                mThreadPlayer.interrupt();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//        final String filepath = ServerFileUtil.getFileUrl(path).replace(ServerConfigData.getInstance().getServerConfig().getVod_server().toString(),ServerConfigData.getInstance().getServerConfig().getVod_server().toString()+"%20");
//        final String nextPath = ServerFileUtil.getFileUrl(next).replace(ServerConfigData.getInstance().getServerConfig().getVod_server().toString(),ServerConfigData.getInstance().getServerConfig().getVod_server().toString()+"%20");

        final String downurl = ServerFileUtil.getFileUrl(url);
        Logger.d(TAG, "downurl:" + downurl );
        mThreadPlayer = new Thread(new Runnable() {
            public void run() {
//                mMediaPlayer.close();
                mMediaPlayer.open(downurl, savePath,nextUrl,playMode,OriginPlayer.this);
                mThreadPlayer = null;
            }
        });
        mThreadPlayer.start();
    }

    public void pause() {
        if (mMediaPlayer != null && !isPlayerReset)
            try {
                BnsAudioRecorder.getInstance().pause(true);
                mMediaPlayer.pause();
            } catch (Exception e) {
                Logger.d(TAG, "pause ex:" + e.toString());
            }
    }


    public boolean isPlaying() {
        if (mMediaPlayer != null && !isPlayerReset)
            try {
                return mMediaPlayer.isPlaying();
            } catch (Exception e) {
                Logger.d(TAG, "isPlaying ex:" + e.toString());
            }
        return false;
    }

    public void replay() {
        String url = mFilePath;
        mFilePath = "";
        playUrl(url,savePath,mNextPath,mRecordFileName,playMode);
    }

    public void seekTo(int seek) {
        if (mMediaPlayer != null && !isPlayerReset)
            try {
                mMediaPlayer.seekTo(seek);
            } catch (Exception e) {
                Logger.d(TAG, "seekTo ex:" + e.toString());
            }
    }


    public int getCurrentPosition() {
        if (mMediaPlayer != null && !isPlayerReset && isPlaying())
            try {
                return mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                Logger.e(TAG, "getCurrentPosition ex:" + e.toString());
            }
        return 0;
    }

    public int getDuration() {
        if (mMediaPlayer != null && !isPlayerReset && isPlaying())
            try {
                return mMediaPlayer.getDuration();
            } catch (Exception e) {
                Logger.e(TAG, "getDuration ex:" + e.toString());
            }
        return 0;
    }

    public void onOriginal(int flag) {
        if (mMediaPlayer == null || flag == 4 || isPlayerReset) {// DISCO、单音轨不切换声道类
            return;
        }
        try {
            if (flag == 0) {//单音轨原唱在左
                setVolChannel(1);
            } else if (flag == 1) {//单音轨原唱在右
                setVolChannel(2);
            } else if (flag == 2) {//双音轨第一轨原唱
                mMediaPlayer.selectTack(0);
            } else if (flag == 3) {//双音轨第一轨伴奏
                if (mMediaPlayer.haveMulTracks())
                    mMediaPlayer.selectTack(1);
            }
        } catch (Exception e) {
            Logger.w(TAG, "onOriginal ex:" + e.toString());
        }
    }

    public void onAccom(int flag) {
        Logger.i(TAG, "onAccom flag:" + flag);
        if (mMediaPlayer == null || flag == 4 || isPlayerReset) {// DISCO、单音轨不切换声道类
            return;
        }
        try {
            if (flag == 0) {//单音轨原唱在左
                setVolChannel(2);
            } else if (flag == 1) {//单音轨原唱在右
                setVolChannel(1);
            } else if (flag == 2) {//双音轨第一轨原唱
                if (mMediaPlayer.haveMulTracks())
                    mMediaPlayer.selectTack(1);
            } else if (flag == 3) {//双音轨第一轨伴奏
                mMediaPlayer.selectTack(0);
            }
        } catch (Exception e) {
            Logger.w(TAG, "onAccom ex:" + e.toString());
        }

    }

    /**
     * 0:立体声 1：左声道 2：右声道
     *
     * @param channel
     */
    private void setVolChannel(int channel) {
        if (mMediaPlayer != null && !isPlayerReset)
            mMediaPlayer.setVolChannel(channel);
    }


    public void stop() {
        if (null != mThreadPlayer) {
            try {
                mThreadPlayer.join();
                mThreadPlayer.interrupt();
            } catch (Exception ex) {
            }
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.close();
            mMediaPlayer = null;
            Logger.i(TAG, "release player");
            BnsAudioRecorder.getInstance().release();
            stopTimer();
        }
    }


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
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void setTone(int tone) {
        if (mMediaPlayer != null && !isPlayerReset)
            mMediaPlayer.setPitch(tone);
    }


    public void setVol(float vol) {
        mCurrentVol = vol;
        startVolInit();
    }


    private void startVolInit() {
        mHandlerVolInit.removeCallbacks(runVolInit);
        mVolUpCount = 6;
        mHandlerVolInit.post(runVolInit);
    }

    private Runnable runVolInit = new Runnable() {
        @Override
        public void run() {
            if (mVolUpCount <= VOL_UP_COUNT && mMediaPlayer != null && !isPlayerReset) {
                float vol = mVolUpCount == VOL_UP_COUNT ? mCurrentVol : (mCurrentVol * mVolUpCount / VOL_UP_COUNT);
                mMediaPlayer.setVolume(vol);
                mHandlerVolInit.postDelayed(this, 250);
                mVolUpCount++;
            }
        }
    };

    public void volOn() {
        startVolInit();
    }

    public void volOff() {
        mHandlerVolInit.removeCallbacks(runVolInit);
        if (mMediaPlayer != null)
            try {
                mMediaPlayer.setVolume(0.0F);
            } catch (Exception e) {
                Logger.d(TAG, "volOff ex:" + e.toString());
            }
    }


    public void setScoreOn(int mode) {
        mScoreMode = mode;
        NativeScoreRunner.getInstance().setScoreMode(mScoreMode);
    }


    private void initScore() {
        NativeScoreRunner.getInstance().setOnKeyInfoListener(this);
        NativeScoreRunner.getInstance().setScoreMode(mScoreMode);
        NativeScoreRunner.getInstance().start();
        if (mScoreLineInfos != null)
            mScoreLineInfos.clear();

        File fileNote2 = DiskFileUtil.getScoreNoteSec(mFilePath);


        if (fileNote2 != null && fileNote2.exists())
            mScoreLineInfos = ScoreFileUtil.readNote2File(fileNote2.getAbsolutePath());

        File fileNote = DiskFileUtil.getScoreNote(mFilePath);
        if (fileNote != null && fileNote.exists()) {
            ArrayList<NoteInfo> notes = ScoreFileUtil.readNoteFile(fileNote.getAbsolutePath());
            Logger.d(TAG, "Score note line:" + notes);
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
    }


    private void setIsRecord(String recordFileName) {
        mRecordFileName = recordFileName;
    }

    private void initRecord() {
        if (!TextUtils.isEmpty(mRecordFileName)) {
            BnsAudioRecorder bnsAudioRecorder = BnsAudioRecorder.getInstance();
            bnsAudioRecorder.setAudioRecordListener(this);
            bnsAudioRecorder.start(mRecordFileName);
        }
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


    public void setOnKeyInfoListener(OnKeyInfoListener l) {
        mOnKeyInfoListener = l;
    }


    public void setOnScoreListener(OnScoreListener scoreListener) {
        this.mOnScoreListener = scoreListener;
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

    private Handler handlerRecord = new Handler();

    private Runnable runnableRecord = new Runnable() {
        @Override
        public void run() {
            initScore();
            initRecord();
        }
    };

    @Override
    public void onUpdateTime(long msTime) {
    }

    @Override
    public void onKeyInfoCallback(KeyInfo[] infos, int totalScore) {
        if (mOnKeyInfoListener != null && (mScoreMode != 0)) {
            mOnKeyInfoListener.onKeyInfoCallback(infos, totalScore);
        }
    }

    @Override
    public void onOriginNotes(ArrayList<NoteInfo> noteInfos) {
        if (mOnKeyInfoListener != null)
            mOnKeyInfoListener.onOriginNotes(noteInfos);
    }

    @Override
    public void onPlayerCompletion() {
        Logger.i(TAG, "Player onCompletion");
        if (mBeidouPlayerListener != null)
            mBeidouPlayerListener.onPlayerCompletion();
        BnsAudioRecorder.getInstance().release();
        NativeScoreRunner.getInstance().stop();
    }

    @Override
    public void onPlayerPrepared() {
        try {
            isPlayerReset = false;
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
    public void onPlayerProgress(long progress, long duration) {

    }


    @Override
    public void audioBytes(byte[] buffer, int bufSize) {
        if (mOnKeyInfoListener != null) {
            mOnKeyInfoListener.onRecordData(buffer, bufSize);
        }
    }

    @Override
    public void onRecordData(byte[] data, int bufSize) {
        if (mOnKeyInfoListener != null) {
            mOnKeyInfoListener.onRecordData(data, bufSize);
        }
    }
}
