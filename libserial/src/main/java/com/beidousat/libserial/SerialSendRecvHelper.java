package com.beidousat.libserial;

import android.util.Log;

/**
 * Created by J Wong on 2016/10/26.
 */

public class SerialSendRecvHelper {

    private StringBuffer data = new StringBuffer();

    private SerialHelper mSerialHelper;

    private static SerialSendRecvHelper mSerialSendRecvHelper;

    public static SerialSendRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new SerialSendRecvHelper();
        }
        return mSerialSendRecvHelper;
    }

    public void open(String port, int baudRate) {
        mSerialHelper = new SerialHelper(port, baudRate) {
            @Override
            protected void onReceive(final byte[] btData) {
                data = new StringBuffer();
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
                Log.i("SerialSendRecvHelper", "onReceive: " + data.toString());
                if (mOnSerialReceiveListener != null && data.length() > 0) {
                    mOnSerialReceiveListener.OnSerialReceive(data.toString().trim());
                }
            }
        };
        try {
            mSerialHelper.open();
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", "open error:" + ex.toString());
        }
    }

    public void close() {
        if (mSerialHelper==null) return;
        try {
            mSerialHelper.close();
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", " close error" + ex.toString());
        }
    }

    private OnSerialReceiveListener mOnSerialReceiveListener;

    public void setOnSerialReceiveListener(OnSerialReceiveListener listener) {
        mOnSerialReceiveListener = listener;
    }

    public void send(String code) {
        if (mSerialHelper==null) return;
        try {
            Log.d("SerialSendRecvHelper", "send :" + code);
            mSerialHelper.sendHex(code);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("SerialSendRecvHelper", " send string error" + ex.toString());
        }
    }

    public interface OnSerialReceiveListener {
        void OnSerialReceive(String data);
    }
}
