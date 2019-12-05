package com.beidousat.libserial;

import android.util.Log;


/**
 * Created by J Wong on 2016/10/26.
 */

public class McuRecvHelper {
    private String Tag="Mcu";
    private StringBuffer data = new StringBuffer();
    private SerialHelper mSerialHelper;
    private static McuRecvHelper mSerialSendRecvHelper;
    public  static final byte byte_mic_up[] = {(byte)0x55, (byte)0xAA, (byte)0x13, (byte)0x02};
    public  static final byte byte_mic_down[] = {(byte)0x55, (byte)0xAA, (byte)0x13, (byte)0x03};
    public  static final byte byte_effect_up[] = {(byte)0x55, (byte)0xAA, (byte)0x13, (byte)0x04};
    public  static final byte byte_effect_down[] = {(byte)0x55, (byte)0xAA, (byte)0x13, (byte)0x05};
    public  static final byte byte_effect_query[] = {(byte)0x55, (byte)0xAA, (byte)0xFF, (byte)0x08};
    public  static final byte byte_mic_query[] = {(byte)0x55, (byte)0xAA, (byte)0xFF, (byte)0x0A};
    public  static final byte byte_effect_reset[] = {(byte)0x55, (byte)0xAA, (byte)0x08, (byte)0x05};
    public  static final byte byte_mic_reset[] = {(byte)0x55, (byte)0xAA, (byte)0x0A, (byte)0x07};
    public  static final byte byte_mic_mute[] = {(byte)0x55, (byte)0xAA, (byte)0x0A, (byte)0x00};
    private String rec_str;
    public static McuRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new McuRecvHelper();
        }
        return mSerialSendRecvHelper;
    }

    public void open(String port,int baudRate) {
        mSerialHelper = new SerialHelper(port, baudRate) {
            @Override
            protected void onReceive(final byte[] btData) {
                data = new StringBuffer();
                Log.i(Tag, "onReceive");
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
                rec_str += data.toString();
                int start_num = rec_str.indexOf("44");
                int end_num=start_num + 11;
                Log.d(Tag, "mcu " + rec_str);
                if (start_num >= 0 && rec_str.length() >= end_num) {
                    String use_string = rec_str.substring(start_num, end_num);
                    Log.d(Tag, " use mcu str:" + use_string );
                    if (mOnMcuReceiveListener != null) {
                        mOnMcuReceiveListener.OnMcuReceive(use_string.trim());
                    }
                    rec_str = rec_str.substring(end_num);
                }
            }
        };
        try {
            mSerialHelper.open();
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }

    public void close() {
        try {
            mSerialHelper.close();
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }

    private OnMcuSerialReceiveListener mOnMcuReceiveListener;

    public void setOnMcuSerialReceiveListener(OnMcuSerialReceiveListener listener) {
        mOnMcuReceiveListener = listener;
    }

    public void send(String code) {
        try {
            Log.d(Tag, "send :" + code);
            mSerialHelper.sendTxt(code);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }
    public void sendbyte(byte[] code) {
        try {
            Log.d(Tag, "send :" + code.toString());
            mSerialHelper.send(code);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }
   
    public interface OnMcuSerialReceiveListener {
        void OnMcuReceive(String data);
    }

}
