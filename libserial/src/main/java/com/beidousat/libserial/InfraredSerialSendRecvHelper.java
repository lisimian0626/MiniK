package com.beidousat.libserial;

import android.util.Log;

/**
 * Created by J Wong on 2016/10/26.
 */

public class InfraredSerialSendRecvHelper {

    private StringBuffer data = new StringBuffer();

    private SerialHelper mSerialHelper;

    private static InfraredSerialSendRecvHelper mSerialSendRecvHelper;

    public static InfraredSerialSendRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new InfraredSerialSendRecvHelper();
        }
        return mSerialSendRecvHelper;
    }

    public void open(String port,int baudRate) {
        mSerialHelper = new SerialHelper(port, baudRate) {
            @Override
            protected void onReceive(final byte[] btData) {
                data = new StringBuffer();
                Log.i("SerialSendRecvHelper", new String(btData));
                // int i=0;
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
                Log.i("SerialSendRecvHelper", data.toString());
                if (mOnInfraredSerialReceiveListener != null && data.length() > 0) {
                    mOnInfraredSerialReceiveListener.OnInfraredSerialReceive(data.toString().trim());
                }
            }
        };
        try {
            mSerialHelper.open();
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", ex.toString());
        }
    }

    public void close() {
        try {
            mSerialHelper.close();
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", ex.toString());
        }
    }

    private OnInfraredSerialReceiveListener mOnInfraredSerialReceiveListener;

    public void setOnInfraredSerialReceiveListener(OnInfraredSerialReceiveListener listener) {
        mOnInfraredSerialReceiveListener = listener;
    }

    public void send(String code) {
        try {
            Log.d("SerialSendRecvHelper", "send :" + code);
            mSerialHelper.sendTxt(code);
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", ex.toString());
        }
    }
    public void send(byte[] code) {
        try {
            Log.i("SerialSendRecvHelper", "send :" + code.toString());
            mSerialHelper.send(code);
        } catch (Exception ex) {
            Log.e("SerialSendRecvHelper", ex.toString());
        }
    }
    public interface OnInfraredSerialReceiveListener {
        void OnInfraredSerialReceive(String data);
    }

}
