package com.czt.mp3recorder;

import java.io.File;

/**
 * Created by J Wong on 2016/9/12.
 */
public class BnsAudioRecorder implements IAudioRecordListener {

    private static BnsAudioRecorder mBnsVoiceRecorder;
    private IAudioRecordListener mIAudioRecordListener;

    private MP3Recorder mRecorder;

    public static BnsAudioRecorder getInstance() {
        if (mBnsVoiceRecorder == null) {
            mBnsVoiceRecorder = new BnsAudioRecorder();
        }
        return mBnsVoiceRecorder;
    }


    public void setAudioRecordListener(IAudioRecordListener listener) {
        this.mIAudioRecordListener = listener;
    }

    public void start(String recordName) {
        release();
        String filePath = AudioRecordFileUtil.getRecordFilePath(recordName);
        try {
            mRecorder = new MP3Recorder(new File(filePath));
            mRecorder.setAudioRecordListener(this);
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void start(File file) {
//        release();
//        try {
//            mRecorder = new MP3Recorder(file,AudioFormat.CHANNEL_IN_MONO);
//            mRecorder.setAudioRecordListener(this);
//            mRecorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void pause(boolean isPause) {
        if (mRecorder != null)
            mRecorder.setPause(isPause);
    }

    public void release() {
        if (mRecorder != null && mRecorder.isRecording())
            mRecorder.stop();
    }

    @Override
    public void audioByte(double[] bytes) {
        if (mIAudioRecordListener != null) {
            mIAudioRecordListener.audioByte(bytes);
        }
    }

    @Override
    public void audioBytes(byte[] bytes, int bufSize) {
        if (mIAudioRecordListener != null) {
            mIAudioRecordListener.audioBytes(bytes, bufSize);
        }
    }
}
