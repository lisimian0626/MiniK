package com.beidousat.libserial;

import android.util.Log;


/**
 * Created by J Wong on 2016/10/26.
 */

public class McuRecvHelper {
    private String Tag = "McuRecvHelper";
    private StringBuffer data = new StringBuffer();
    private SerialHelper mSerialHelper;
    private static McuRecvHelper mSerialSendRecvHelper;
    public static final byte byte_mic_up[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x13, (byte) 0x02};//55AA1302
    public static final byte byte_mic_down[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x13, (byte) 0x03};//55AA1303
    public static final byte byte_effect_up[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x13, (byte) 0x04};//55AA1304
    public static final byte byte_effect_down[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x13, (byte) 0x05};//55AA1305
    public static final byte byte_effect_query[] = {(byte) 0x55, (byte) 0xAA, (byte) 0xFF, (byte) 0x08};//55AAFF08
    public static final byte byte_mic_query[] = {(byte) 0x55, (byte) 0xAA, (byte) 0xFF, (byte) 0x0A};//55AAFF0A
    public static final byte byte_effect_reset[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x05};//55AA0805
    public static final byte byte_mic_reset[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x0A, (byte) 0x07};//55AA0A07
    public static final byte byte_mic_mute[] = {(byte) 0x55, (byte) 0xAA, (byte) 0x0A, (byte) 0x00};//55AA0A00

    public static McuRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new McuRecvHelper();
        }
        return mSerialSendRecvHelper;
    }

    public void open(String port, int baudRate) {
        mSerialHelper = new SerialHelper(port, baudRate) {
            @Override
            protected void onReceive(final byte[] btData) {
                data = new StringBuffer();
                Log.i(Tag, "onReceive");
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
                if (mOnMcuReceiveListener != null) {
                    mOnMcuReceiveListener.OnMcuReceive(data.toString());
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

    /**
     * 通过串口发送字符串（自动转换成字节）
     */
    public void send(String code) {
        try {
            Log.d(Tag, "send String:" + code);
            mSerialHelper.sendHex(code);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }

    /**
     * 通过串口发送字节
     */
    public void sendbyte(byte[] code) {
        try {
            Log.d(Tag, "send byte:" + DataTransition.byteArrayToHexStr(code));
            mSerialHelper.send(code);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }

    public interface OnMcuSerialReceiveListener {
        void OnMcuReceive(String data);
    }
}
