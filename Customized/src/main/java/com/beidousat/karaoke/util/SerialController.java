package com.beidousat.karaoke.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libserial.SerialSendRecvHelper;

/**
 * Created by J Wong on 2015/11/5 10:18.
 */
public class SerialController implements SerialSendRecvHelper.OnSerialReceiveListener {

    private static SerialController mSerialController;
    private Context mContext;
    private SerialSendRecvHelper mSerialHelper;
    private boolean mIsOpened;

    private final static String TAG = "SerialController";

    public static SerialController getInstance(Context context) {
        if (mSerialController == null)
            mSerialController = new SerialController(context);
        return mSerialController;
    }

    private SerialController(Context context) {
        this.mContext = context;
    }

    public void open(String port,int baudrate) {
        try {
            mSerialHelper = SerialSendRecvHelper.getInstance();
//            if (mIsOpened) {
//                mSerialHelper.close();
//            }
            mSerialHelper.open(port,baudrate);
            mIsOpened = true;
            mSerialHelper.setOnSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnSerialReceive(String data) {
        Logger.d(TAG, "OnSerialReceive :" + data + "");
        Message msg = new Message();
        msg.obj = data;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            Logger.d(TAG, "OnSerialReceive handler:" + data + "");
            if (!TextUtils.isEmpty(data)) {
                if (data.startsWith("A2 06 B0 0A 0D ")) {//mic音量
                    try {
                        String str = data.replace("A2 06 B0 0A 0D ", "");
                        String hex = str.substring(0, 2);
                        Logger.d(TAG, "OnSerialReceive handle mic hex :" + hex + "");
                        int micVol = Integer.parseInt(hex, 16);
                        Logger.d(TAG, "OnSerialReceive handle mic vol :" + micVol + "");
                        EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_MIC_VOL, micVol);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (data.startsWith("A2 06 B0 0A 0E ")) {//效果音量
                    try {
                        String str = data.replace("A2 06 B0 0A 0E ", "");
                        String hex = str.substring(0, 2);
                        Logger.d(TAG, "OnSerialReceive handle eff hex :" + hex + "");
                        int effVol = Integer.parseInt(hex, 16);
                        Logger.d(TAG, "OnSerialReceive handle eff vol :" + effVol + "");
                        EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_EFF_VOL, effVol);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            super.handleMessage(msg);
        }
    };

    public void onMicUp() {
        try {
            String code = PrefData.getSerilMicUp(mContext);
            if (!TextUtils.isEmpty(code))
                mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void onMicDown() {
        try {
            String code = PrefData.getSerilMicDown(mContext);
            if (!TextUtils.isEmpty(code))
                mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void onReverbUp() {
        try {
            String code = PrefData.getSerilReverbUp(mContext);
            if (!TextUtils.isEmpty(code))
                mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void onReverbDown() {
        try {
            String code = PrefData.getSerilReverbDown(mContext);
            if (!TextUtils.isEmpty(code))
                mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void onReset() {
        try {
            String code = PrefData.getSerilReset(mContext);
            if (!TextUtils.isEmpty(code))
                mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void setMicMute(boolean mute) {
        try {
            if (mute) {
                mSerialHelper.send("E0A206B70A0D0000AE");
            } else {
                mSerialHelper.send("E0A206B70A0D3200E0");
            }
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void readMicVol() {
        try {
            mSerialHelper.send("E0A204B00A0DA7");
            //mSerialHelper.send("E0A204B00A0CA6");
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void readEffVol() {
        try {
            mSerialHelper.send("E0A204B00A0EA8");
            //mSerialHelper.send("E0A204B00A0CA6");
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void resetMic() {
        try {
            mSerialHelper.send("E0A206B70A0D3200E0");
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }


    public void resetEff() {
        try {
            mSerialHelper.send("E0A206B70A0E1600C5");
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }
}
