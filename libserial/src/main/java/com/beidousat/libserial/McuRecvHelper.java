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
                // int i=0;
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
                Log.d(Tag, data.toString());
                if (mOnMcuReceiveListener != null && data.length() > 0) {
                    mOnMcuReceiveListener.OnMcuReceive(data.toString().trim());
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
            mSerialHelper.send(code);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }
   
    public interface OnMcuSerialReceiveListener {
        void OnMcuReceive(String data);
    }

}
