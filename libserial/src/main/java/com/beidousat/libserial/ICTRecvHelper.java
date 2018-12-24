package com.beidousat.libserial;

import android.util.Log;


/**
 * Created by J Wong on 2016/10/26.
 */

public class ICTRecvHelper {
    private String Tag="ICT";
    private StringBuffer data = new StringBuffer();
    private SerialHelper mSerialHelper;
    private static ICTRecvHelper mSerialSendRecvHelper;
    private final byte cmdAccept= (byte)0x02;
    private final byte cmdReject=(byte)0x0f;
   
    public static ICTRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new ICTRecvHelper();
        }
        return mSerialSendRecvHelper;
    }

    public void open(String port,int baudRate) {
        mSerialHelper = new SerialHelper(port, baudRate) {
            @Override
            protected void onReceive(final byte[] btData) {
                data = new StringBuffer();
//                Log.i(Tag, new String(btData));
                // int i=0;
                for (byte b : btData) {
                    data.append(DataTransition.byte2Hex(b) + " ");
                }
//                Log.i(Tag, data.tICTring());
                if (mOnICTReceiveListener != null && data.length() > 0) {
                    mOnICTReceiveListener.OnICTReceive(data.toString().trim());
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

    private OnICTSerialReceiveListener mOnICTReceiveListener;

    public void setOnICTSerialReceiveListener(OnICTSerialReceiveListener listener) {
        mOnICTReceiveListener = listener;
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
   
    public interface OnICTSerialReceiveListener {
        void OnICTReceive(String data);
    }

}
