package com.beidousat.karaoke.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libserial.ICTRecvHelper;
import com.beidousat.libserial.InfraredSerialSendRecvHelper;
import com.beidousat.libserial.McuRecvHelper;
import com.beidousat.libserial.OSTSendRecvHelper;
import com.beidousat.libserial.SerialSendRecvHelper;

/**
 * Created by J Wong on 2015/11/5 10:18.
 */
public class SerialController implements SerialSendRecvHelper.OnSerialReceiveListener, InfraredSerialSendRecvHelper.OnInfraredSerialReceiveListener, OSTSendRecvHelper.OnOstSerialReceiveListener, ICTRecvHelper.OnICTSerialReceiveListener, McuRecvHelper.OnMcuSerialReceiveListener {

    private static SerialController mSerialController;
    private Context mContext;
    private SerialSendRecvHelper mSerialHelper;
    private InfraredSerialSendRecvHelper mInfraredHelper;
    private OSTSendRecvHelper mOSTHelper;
    private ICTRecvHelper mICTHelper;
    private McuRecvHelper mcuRecvHelper;
    private final int Serial = 1;
    private final int InfraredSerial = 2;
    private final int ostSerial = 3;
    private final int ictSerial = 4;
    private final int McuSerial = 5;
    private String codeCache = "";
    private final static String TAG = "SerialController";

    public static SerialController getInstance(Context context) {
        if (mSerialController == null)
            mSerialController = new SerialController(context);
        return mSerialController;
    }


    private SerialController(Context context) {
        this.mContext = context;
    }

    public void open(String port, int baudrate) {
        try {
            mSerialHelper = SerialSendRecvHelper.getInstance();
//            if (mIsOpened) {
//                mSerialHelper.close();
//            }
            Logger.i(TAG, "open");
//            Toast.makeText(mContext,"open",Toast.LENGTH_SHORT);
            mSerialHelper.open(port, baudrate);
            mSerialHelper.setOnSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openInfrared(String port, int baudrate) {

        try {
            mInfraredHelper = InfraredSerialSendRecvHelper.getInstance();
//                        if (mIsfraredOpened) {
//                            mInfraredHelper.close();
//            }
            Logger.i(TAG, "Infrared open");
            mInfraredHelper.open(port, baudrate);
            mInfraredHelper.setOnInfraredSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openOst(String port, int baudrate) {

        try {
            mOSTHelper = OSTSendRecvHelper.getInstance().getInstance();
//                        if (mIsfraredOpened) {
//                            mInfraredHelper.close();
//            }
            Logger.i(TAG, "OST open");
            mOSTHelper.open(port, baudrate);
            mOSTHelper.setOnOstSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openICT(String port, int baudrate) {

        try {
            mICTHelper = ICTRecvHelper.getInstance().getInstance();
            Logger.i(TAG, "ICT open");
            mICTHelper.open(port, baudrate);
            mICTHelper.setOnICTSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMcu(String port, int baudrate) {

        try {
            mcuRecvHelper = McuRecvHelper.getInstance().getInstance();
            mcuRecvHelper.open(port, baudrate);
            mcuRecvHelper.setOnMcuSerialReceiveListener(this);
            Logger.i(TAG, "Mcu open");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnSerialReceive(String data) {
        Logger.d(TAG, "OnSerialReceive :" + data + "");
        Message msg = new Message();
        msg.what = Serial;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    @Override
    public void OnInfraredSerialReceive(String data) {
//        Logger.i(TAG, "OnInfraredSerialReceive :" + data + "");
        Message msg = new Message();
        msg.what = InfraredSerial;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    @Override
    public void OnOstReceive(String data) {
        Logger.d(TAG, "OnOstReceive :" + data + "");
        Message msg = new Message();
        msg.what = ostSerial;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    @Override
    public void OnICTReceive(String data) {
        Logger.d(TAG, "OnICTReceive :" + data + "");
        Message msg = new Message();
        msg.what = ictSerial;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            if (TextUtils.isEmpty(data))
                return;
            switch (msg.what) {
                case Serial:
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
                    break;
                case McuSerial:
                    data = data.replace(" ", "").toUpperCase();
                    codeCache += data;
                    Logger.d(TAG, "dealCode codeCache >>>>>>>>>> " + codeCache);
                    if (codeCache.contains("44BB0A")) {
                        try {
                            String str = codeCache.replace("44BB0A","");
                            String hex = str.substring(0, 2);
                            Logger.d(TAG, "OnSerialReceive handle mic hex :" + hex + "");
                            int micVol = Integer.parseInt(hex, 16);
                            EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_MIC_VOL, micVol);
                            codeCache = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (codeCache.contains("44BB08")) {
                        try {
                            String str = codeCache.replace("44BB08","");
                            String hex = str.substring(0, 2);
                            Logger.d(TAG, "OnSerialReceive handle eff hex :" + hex + "");
                            int effVol = Integer.parseInt(hex, 16);
                            EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_EFF_VOL, effVol);
                            codeCache = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case InfraredSerial:
                    EventBusUtil.postSticky(EventBusId.INFARAED.RECEIVE_CODE, data);
                    break;
                case ostSerial:
                    EventBusUtil.postOcto(EventBusId.Ost.RECEIVE_CODE, data);
                    break;
                case ictSerial:
                    Logger.d(TAG, "OnSerialReceive handler:" + data + "");
                    EventBusUtil.postICT(EventBusId.Ict.RECEIVE_CODE, data);
                    break;
                default:
                    break;
            }
        }
    };

    public void onMicUp() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_up);
        } else {
            try {
                String code = PrefData.getSerilMicUp(mContext);
                Logger.d(TAG, "onMicUp:" + code);
                if (!TextUtils.isEmpty(code))
                    mSerialHelper.send(code);
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }

    public void onMicDown() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_down);
        } else {
            try {
                String code = PrefData.getSerilMicDown(mContext);
                Logger.d(TAG, "onMicDown:" + code);
                if (!TextUtils.isEmpty(code))
                    mSerialHelper.send(code);
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }

    public void onReverbUp() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_effect_up);
        } else {
            try {
                String code = PrefData.getSerilReverbUp(mContext);
                if (!TextUtils.isEmpty(code))
                    mSerialHelper.send(code);
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }

    public void onReverbDown() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_effect_down);
        } else {
            try {
                String code = PrefData.getSerilReverbDown(mContext);
                if (!TextUtils.isEmpty(code))
                    mSerialHelper.send(code);
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
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
                if (PrefData.getSERIAL_RJ45(mContext) == 3) {
                    mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_mute);
                } else {
                    mSerialHelper.send("E0A206B70A0D0000AE");
                }
            } else {
                if (PrefData.getSERIAL_RJ45(mContext) == 3) {
                    mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_reset);
                } else {
                    mSerialHelper.send("E0A206B70A0D3200E0");
                }
            }
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void readMicVol() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_query);
        } else {
            try {
                mSerialHelper.send("E0A204B00A0DA7");
                //mSerialHelper.send("E0A204B00A0CA6");
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }

    public void readEffVol() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_effect_query);
        } else {
            try {
                mSerialHelper.send("E0A204B00A0EA8");
                //mSerialHelper.send("E0A204B00A0CA6");
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }

    public void resetMic() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_mic_reset);
        } else {
            try {
                mSerialHelper.send("E0A206B70A0D3200E0");
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }


    public void resetEff() {
        if (PrefData.getSERIAL_RJ45(mContext) == 3) {
            mcuRecvHelper.sendbyte(McuRecvHelper.byte_effect_reset);
        } else {
            try {
                mSerialHelper.send("E0A206B70A0E1600C5");
            } catch (Exception e) {
                Logger.w(TAG, e.toString());
            }
        }
    }


    public void close() {
        if (mInfraredHelper != null) {
            Logger.i(TAG, "close");
            Toast.makeText(mContext, "close", Toast.LENGTH_SHORT);
            mInfraredHelper.close();
        }
    }

    public void send(String msg) {
        if (mInfraredHelper != null) {
//            Logger.i(TAG, "send" );
            mInfraredHelper.send(msg);
        }
    }

    public void sendbyte(byte[] cmddata) {
        if (mInfraredHelper != null) {
//            Logger.i(TAG, "send" );
            mInfraredHelper.send(cmddata);
        }
    }

    public void sendOst(String msg) {
        if (mOSTHelper != null) {
//            Logger.i(TAG, "send" );
            mOSTHelper.send(msg);
        }
    }

    public void sendbyteOst(byte[] cmddata) {
        if (mOSTHelper != null) {
            Logger.i(TAG, "send");
            mOSTHelper.sendbyte(cmddata);
        }
    }

    public void sendICT(String msg) {
        if (mICTHelper != null) {
//            Logger.i(TAG, "send" );
            mICTHelper.send(msg);
        }
    }

    public void sendbyteICT(byte[] cmddata) {
        if (mICTHelper != null) {
            Logger.i(TAG, "send");
            mICTHelper.sendbyte(cmddata);
        }
    }

    public void sendMCU(String msg) {
        if (mcuRecvHelper != null) {
//            Logger.i(TAG, "send" );
            mcuRecvHelper.send(msg);
        }
    }

    public void sendbyteMCU(byte[] cmddata) {
        if (mcuRecvHelper != null) {
            Logger.i(TAG, " mcu send");
            mcuRecvHelper.sendbyte(cmddata);
        }
    }

    @Override
    public void OnMcuReceive(String data) {
        Logger.d(TAG, "OnMcuReceive :" + data + "");
        Message msg = new Message();
        msg.what = McuSerial;
        msg.obj = data;
        handler.sendMessage(msg);
    }
}
