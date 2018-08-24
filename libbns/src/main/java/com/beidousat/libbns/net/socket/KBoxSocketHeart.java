package com.beidousat.libbns.net.socket;

import android.text.TextUtils;
import android.util.Log;

import com.beidousat.libbns.R;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.KBoxStatus;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.Logger;

/**
 * Created by J Wong on 2017/5/9.
 */

public class KBoxSocketHeart implements BnsSocket.BnsSocketListener {
    /**
     * m2消息在后台改变套餐价格时，服务器会发送M2消息
     */
    @Deprecated
    private static final String M2 = "M2";

    private static KBoxSocketHeart mKBoxSocketHeart;
    public BnsSocket mBnsSocket;
    private boolean mIsRunning;
    private int mKBoxStatus = -1;
    private static final String PACKAGE_NO_SING = "M1";
    private static final String PACKAGE_SINGING = "U1";

    public static KBoxSocketHeart getInstance(String address, int port) {
        if (mKBoxSocketHeart == null) {
            mKBoxSocketHeart = new KBoxSocketHeart(address, port);
        }
        return mKBoxSocketHeart;
    }

    public KBoxSocketHeart(String address, int port) {
        Logger.d("KBoxSocketHeart", "KBoxSocketHeart host:" + address + "  port:" + port);
        mBnsSocket = new BnsSocket(address, port);
        mBnsSocket.setBnsSocketListener(this);
    }

    public void check() {
        start();
    }

    private void start() {
            EventBusUtil.postSticky(EventBusId.SOCKET.KBOX_STATUS_CHECKING, mKBoxStatus);
            mBnsSocket.start();
    }

    public void setKBoxId(String kBoxId) {
        mKBoxID = kBoxId;
        String heartPackage = "," + (mIsSinging ? PACKAGE_SINGING : PACKAGE_NO_SING) + "," + DeviceUtil.getCupChipID() + "," + mKBoxID;
        mBnsSocket.setHeartPackage(heartPackage);
    }

    public void setIsSinging(boolean isSinging) {
        mIsSinging = isSinging;
        String heartPackage = "," + (mIsSinging ? PACKAGE_SINGING : PACKAGE_NO_SING) + "," + DeviceUtil.getCupChipID() + "," + mKBoxID;
        mBnsSocket.setHeartPackage(heartPackage);
    }

    private String handSendPackage;
    private OnSetKBoxIDListener mOnSetKBoxIDListener;
    private boolean mIsSinging = false;
    private String mKBoxID;

    public void sendHeartPackage(String kBoxId, OnSetKBoxIDListener listener) {
        Logger.d("KBoxSocketHeart", "sendHeartPackage mKBoxID===" + mKBoxID+"    ||cupID:"+ DeviceUtil.getCupChipID());
        mKBoxID = kBoxId;
        handSendPackage = System.nanoTime() + "," + (mIsSinging ? PACKAGE_SINGING : PACKAGE_NO_SING) + "," + DeviceUtil.getCupChipID() + "," + mKBoxID;
        mOnSetKBoxIDListener = listener;
        mBnsSocket.sendMsg(handSendPackage);
    }

    @Override
    public void onSocketReceive(String msg) {
        try {
            Logger.d("KBoxSocketHeart", "onSocketReceive msg===" + msg+"||   cupID:"+ DeviceUtil.getCupChipID());
            String[] splits = msg.split(",");

            Logger.d("KBoxSocketHeart", msg + "---" + String.valueOf(splits[1].equals(M2)));
            if (splits[1].equals(M2)) {
                EventBusUtil.postRequestMeal();
            }

            String status = splits[4];
            mKBoxStatus = Integer.valueOf(status);

            int code = 0;
            if (splits.length >= 6) {
                String strCode = splits[5];
                code = Integer.valueOf(strCode);
            }


            Logger.d("KBoxSocketHeart", "onSocketReceive status===" + status);
            KBoxStatus kBoxStatus = new KBoxStatus(mKBoxStatus, code,getErrMsg(code));

            EventBusUtil.postSticky(EventBusId.SOCKET.KBOX_STATUS, kBoxStatus);

            if (mOnSetKBoxIDListener != null && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(handSendPackage) && msg.startsWith(handSendPackage)) {
                mOnSetKBoxIDListener.callback(kBoxStatus);
                Logger.d("KBoxSocketHeart", "onSocketReceive callback===");

            } else {
                Logger.d("KBoxSocketHeart", "onSocketReceive not callback===");
            }
//
            mBnsSocket.setHeartRate(mKBoxStatus == 1 ? 30 * 1000 : 5 * 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getErrMsg(int errCode) {
        if (errCode == 3001) {
            return "包房信息不存在";
        } else if (errCode == 2002) {
            return "包房已停止营业";
        } else if (errCode == 2003) {
            return "未交服务费";
        } else if (errCode == 2005) {
            return "设备出错，无法使用";
        } else if (errCode == 3002) {
            return "系统出错，查无店家信息";
        } else if (errCode == 2004) {
            return "店家已经停业";
        } else if (errCode == 4001) {
            return "权限不足";
        } else if (errCode == 1001) {
            return "无房间编号";
        } else if (errCode == 1002) {
            return "无房间编号";
        } else if (errCode == 2001) {
            return "房间编号冲突，或给占用";
        }
        return "未知错误";
    }

    public interface OnSetKBoxIDListener {
        void callback(KBoxStatus kBoxStatus);
    }

}
