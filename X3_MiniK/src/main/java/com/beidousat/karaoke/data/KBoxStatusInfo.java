package com.beidousat.karaoke.data;

import android.util.Log;

import com.beidousat.libbns.model.KBoxStatus;

/**
 * Created by J Wong on 2017/5/11.
 */

public class KBoxStatusInfo {
    private static KBoxStatusInfo mKBoxStatusInfo;

    private KBoxStatus mKBoxStatus;

    public static KBoxStatusInfo getInstance() {
        if (mKBoxStatusInfo == null) {
            mKBoxStatusInfo = new KBoxStatusInfo();
        }
        return mKBoxStatusInfo;
    }

    public void setKBoxStatus(KBoxStatus status) {
        mKBoxStatus = status;
    }

    public KBoxStatus getKBoxStatus() {
        return mKBoxStatus;
    }
}
