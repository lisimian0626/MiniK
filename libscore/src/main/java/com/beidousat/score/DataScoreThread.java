//package com.beidousat.score;
//
//import android.media.AudioRecord;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class DataScoreThread extends HandlerThread {
//
//    private StopHandler mHandler;
//    private static final int PROCESS_STOP = 1;
//
//    private static class StopHandler extends Handler {
//
//        private DataScoreThread encodeThread;
//
//        public StopHandler(Looper looper, DataScoreThread encodeThread) {
//            super(looper);
//            this.encodeThread = encodeThread;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == PROCESS_STOP) {
//                //处理缓冲区中的数据
////                while (encodeThread.processData() > 0) ;
//                // Cancel any event left in the queue
//                removeCallbacksAndMessages(null);
////                encodeThread.flushAndRelease();
//                getLooper().quit();
//            }
//        }
//    }
//
//    private OnKeyInfoListener mOnKeyInfoListener;
//
//    public DataScoreThread(OnKeyInfoListener listener) {
//        super("DataScoreThread");
//        mOnKeyInfoListener = listener;
//    }
//
//    @Override
//    public synchronized void start() {
//        super.start();
//        mHandler = new StopHandler(getLooper(), this);
//    }
//
//    private void check() {
//        if (mHandler == null) {
//            throw new IllegalStateException();
//        }
//    }
//
//    public void sendStopMessage() {
//        check();
//        mHandler.sendEmptyMessage(PROCESS_STOP);
//        mIsRuning = false;
//    }
//
//    public Handler getHandler() {
//        check();
//        return mHandler;
//    }
//
//
//    public float getScore() {
//        return NdkJniUtil.getScore();
//    }
//
//    private void processData() {
//        while (mTasks.size() > 0) {
//            mIsRuning = true;
//            Task task = mTasks.remove(0);
//            double[] buffer = task.getData();
//            int position = task.getPosition();
//            KeyInfo[] infos = NdkJniUtil.getAnalyzeResult(buffer, position, buffer.length);
//            Log.d("DataScoreThread", "getAnalyzeResult :" + (infos == null ? " is  null" : "is not null"));
//            if (mOnKeyInfoListener != null)
//                mOnKeyInfoListener.onKeyInfoCallback(infos, 0);
//        }
//    }
//
//    /**
//     * Flush all data left in lame buffer to file
//     */
////    private void flushAndRelease() {
////        //将MP3结尾信息写入buffer中
////        final int flushResult = LameUtil.flush(mMp3Buffer);
////        if (flushResult > 0) {
////            try {
////                mFileOutputStream.write(mMp3Buffer, 0, flushResult);
////                int xframe = LameUtil.writeXingFrame(mMp3Buffer);
////                if (xframe > 0) {
////                    mFileOutputStream.seek(0);
////                    mFileOutputStream.write(mMp3Buffer, 0, xframe);
////                    //write header
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            } finally {
////                if (mFileOutputStream != null) {
////                    try {
////                        mFileOutputStream.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////                LameUtil.close();
////            }
////        }
////    }
//
//    private List<Task> mTasks = Collections.synchronizedList(new ArrayList<Task>());
//    private boolean mIsRuning;
//
//    public void addTask(double[] rawData, int position) {
//        mTasks.add(new Task(rawData, position));
//        if (!mIsRuning) {
//            processData();
//        }
//    }
//
//    private class Task {
//        private double[] rawData;
//        private int position;
//
//        public Task(double[] rawData, int position) {
//            this.rawData = rawData.clone();
//            this.position = position;
//        }
//
//        public double[] getData() {
//            return rawData;
//        }
//
//        public int getPosition() {
//            return position;
//        }
//    }
//
//
//
//}
