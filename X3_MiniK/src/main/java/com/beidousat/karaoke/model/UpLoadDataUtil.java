package com.beidousat.karaoke.model;

public class UpLoadDataUtil {
    private static UpLoadDataUtil mSUpLoadDataUtil;
    private UploadSongData mUploadSongData;

    public static UpLoadDataUtil getInstance() {
        if (mSUpLoadDataUtil == null)
            mSUpLoadDataUtil = new UpLoadDataUtil();
        return mSUpLoadDataUtil;
    }

    public UploadSongData getmUploadSongData() {
        return mUploadSongData;
    }

    public void setmUploadSongData(UploadSongData mUploadSongData) {
        this.mUploadSongData = mUploadSongData;
    }
}
