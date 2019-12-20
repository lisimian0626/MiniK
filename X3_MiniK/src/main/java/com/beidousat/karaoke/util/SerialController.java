package com.beidousat.karaoke.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libserial.ICTRecvHelper;
import com.beidousat.libserial.InfraredSerialSendRecvHelper;
import com.beidousat.libserial.OSTSendRecvHelper;
import com.beidousat.libserial.SerialSendRecvHelper;

/**
 * Created by J Wong on 2015/11/5 10:18.
 */
public class SerialController implements SerialSendRecvHelper.OnSerialReceiveListener, InfraredSerialSendRecvHelper.OnInfraredSerialReceiveListener, OSTSendRecvHelper.OnOstSerialReceiveListener, ICTRecvHelper.OnICTSerialReceiveListener {

    private static SerialController mSerialController;
    private Context mContext;
    private SerialSendRecvHelper mSerialHelper;
    private InfraredSerialSendRecvHelper mInfraredHelper;
    private OSTSendRecvHelper mOSTHelper;
    private ICTRecvHelper mICTHelper;
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

    /**
     * 建立效果器监听
     */
    public void open(String port, int baudrate) {
        Logger.i(TAG, "Sound effects opened：port=>" + port + ",baudrate=>" + baudrate);
        if (TextUtils.isEmpty(port))
            return;
        try {
            mSerialHelper = SerialSendRecvHelper.getInstance();
            mSerialHelper.open(port, baudrate);
            mSerialHelper.setOnSerialReceiveListener(this);
            Logger.i(TAG, "Sound effects opened：port=>" + port + ",baudrate=>" + baudrate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立红外线串口监听
     */
    public void openInfrared(String port, int baudrate) {
        if (TextUtils.isEmpty(port))
            return;
        try {
            mInfraredHelper = InfraredSerialSendRecvHelper.getInstance();
            Logger.i(TAG, "Infrared opened：port=>" + port + ",baudrate=>" + baudrate);
            mInfraredHelper.open(port, baudrate);
            mInfraredHelper.setOnInfraredSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立 pos机 串口监听
     * */
    public void openOst(String port, int baudrate) {
        if (TextUtils.isEmpty(port))
            return;
        try {
            mOSTHelper = OSTSendRecvHelper.getInstance().getInstance();
            Logger.i(TAG, "OST opened：port=>" + port + ",baudrate=>" + baudrate);
            mOSTHelper.open(port, baudrate);
            mOSTHelper.setOnOstSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立投币机监听
     */
    public void openICT(String port, int baudrate) {
        if (TextUtils.isEmpty(port))
            return;
        try {
            mICTHelper = ICTRecvHelper.getInstance().getInstance();
            Logger.i(TAG, "ICT opened：port=>" + port + ",baudrate=>" + baudrate);
            mICTHelper.open(port, baudrate);
            mICTHelper.setOnICTSerialReceiveListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 收到效果器数据后进行处理
     */
    @Override
    public void OnSerialReceive(String data) {
        Logger.d(TAG, "OnSerialReceive :" + data + "");
        if (PrefData.getSoundEffectsBrand(mContext) == 1) {
            codeCache += data;
            int start_num = codeCache.indexOf("44");
            int end_num = start_num + 11;
            Log.d(TAG, "mcu " + codeCache);
            if (start_num >= 0 && codeCache.length() >= end_num) {
                String use_string = codeCache.substring(start_num, end_num);
                Log.d(TAG, " use mcu str:" + use_string);
                use_string = use_string.replace(" ", "").toUpperCase();
                //以下为提交到消息处理
                toMsg(McuSerial, use_string);
                //提交完毕
                codeCache = codeCache.substring(end_num);
            }
        } else {
            toMsg(Serial, data);
        }
    }

    @Override
    public void OnInfraredSerialReceive(String data) {
        Logger.i(TAG, "OnInfraredSerialReceive :" + data + "");
        toMsg(InfraredSerial, data);
    }

    @Override
    public void OnOstReceive(String data) {
        Logger.d(TAG, "OnOstReceive :" + data + "");
        toMsg(ostSerial, data);
    }

    /**
     * 接收投币机的数据
     */
    @Override
    public void OnICTReceive(String data) {
        Logger.d(TAG, "OnICTReceive :" + data + "");
        toMsg(ictSerial, data);
    }

    private void toMsg(int setwhat, String data) {
        Message msg = new Message();
        msg.what = setwhat;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    /**
     * 跟据收取效果器返回的值做处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            if (TextUtils.isEmpty(data))
                return;
            switch (msg.what) {
                case Serial:
                    Logger.d(TAG, "OnSerialReceive handler:" + data);
                    boolean ismic = (data.startsWith("A2 06 B0 0A 0D ") ? true : false);
                    boolean iseff = (data.startsWith("A2 06 B0 0A 0E ") ? true : false);
                    if ((ismic || iseff) && data.length() >= 17) {
                        try {
                            String hex = data.substring(15, 17);
                            int useVol = Integer.parseInt(hex, 16);
                            if (ismic) {//mic音量
                                Logger.d(TAG, "OnSerialReceive handle mic hex :" + hex + ", vol :" + useVol);
                                EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_MIC_VOL, useVol);
                            } else if (iseff) {//效果音量
                                Logger.d(TAG, "OnSerialReceive handle eff hex :" + hex + ",vol :" + useVol);
                                EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_EFF_VOL, useVol);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    super.handleMessage(msg);
                    break;
                case McuSerial:
                    try {
                        String hex = data.substring(6, 8);
                        int useVol = Integer.parseInt(hex, 16);
                        if (data.contains("44BB0A")) {
                            Logger.d(TAG, "OnSerialReceive handle mic hex :" + hex + ", vol :" + useVol);
                            EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_MIC_VOL, useVol);
                        } else if (data.contains("44BB08")) {
                            Logger.d(TAG, "OnSerialReceive handle eff hex :" + hex + ", vol :" + useVol);
                            EventBusUtil.postSticky(EventBusId.SERIAL.SERIAL_EFF_VOL, useVol);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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


    /**
     * 麦克风增加
     */
    public void onMicUp() {
        String code = PrefData.getSerilMicUp(mContext);
        Logger.d(TAG, "onMicUp:" + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }


    /**
     * 麦克风减小
     */

    public void onMicDown() {
        String code = PrefData.getSerilMicDown(mContext);
        Logger.d(TAG, "onMicDown:" + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 混响增加
     */

    public void onReverbUp() {
        String code = PrefData.getSerilReverbUp(mContext);
        Logger.d(TAG, "onReverbUp:" + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 混响减小
     */
    public void onReverbDown() {
        String code = PrefData.getSerilReverbDown(mContext);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    public void onReset() {
        String code = PrefData.getSerilReset(mContext);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 设置麦克风为静音
     */
    public void setMicMute(boolean mute) {
        String code = mute ? PrefData.getSerilMicMute(mContext) : PrefData.getSerilMicUnmute(mContext);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 查询麦克风
     */
    public void readMicVol() {
        String code = PrefData.getSerilQMicVol(mContext);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 查询混响
     */
    public void readEffVol() {
        String code = PrefData.getSerilQueryErrect(mContext);
        Log.d(TAG, "readEffVol: " + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, "readEffVol error:"+e.toString());
        }
    }

    /**
     * 重置麦克风
     */
    public void resetMic() {
        String code = PrefData.getSerilResetMic(mContext);
        Log.d(TAG, "resetMic:" + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
        }
    }

    /**
     * 重置混响
     */
    public void resetEff() {
        String code = PrefData.getSerilResetErrect(mContext);
        Log.d(TAG, "resetMic:" + code);
        if (TextUtils.isEmpty(code))
            return;
        try {
            mSerialHelper.send(code);
        } catch (Exception e) {
            Logger.w(TAG, e.toString());
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
}
