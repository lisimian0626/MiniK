package com.beidousat.karaoke.util;

import android.content.Context;

import com.beidousat.karaoke.R;

public class TipsUtil {
    Context mContext;

    public TipsUtil(Context mContext) {
        this.mContext = mContext;
    }

    public  String getErrMsg(int errCode) {
        if (errCode == 3001) {
            return mContext.getString(R.string.room_not_exist);
        } else if (errCode == 2002) {
            return mContext.getString(R.string.room_closed);
        } else if (errCode == 2003) {
            return mContext.getString(R.string.unpaid_service);
        } else if (errCode == 2005) {
            return mContext.getString(R.string.device_error);
        } else if (errCode == 3002) {
            return mContext.getString(R.string.no_information);
        } else if (errCode == 2004) {
            return mContext.getString(R.string.store_closed);
        } else if (errCode == 4001) {
            return mContext.getString(R.string.insufficient_permissions);
        } else if (errCode == 1001) {
            return mContext.getString(R.string.no_room_number);
        } else if (errCode == 1002) {
            return mContext.getString(R.string.no_room_number);
        } else if (errCode == 2001) {
            return mContext.getString(R.string.room_number_error);
        }
        return mContext.getString(R.string.unknow_error);
    }
}
