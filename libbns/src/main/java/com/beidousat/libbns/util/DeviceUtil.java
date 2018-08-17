package com.beidousat.libbns.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by J Wong on 2015/12/8 19:47.
 */
public class DeviceUtil {

    public static String getCupChipID() {
        String cupId = ProcCpuInfo.getChipIDHex().replace("\r\n", "").replace("\n", "");
        return cupId;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
