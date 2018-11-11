package com.beidousat.libserial;

import android.util.Log;




/**
 * Created by J Wong on 2016/10/26.
 */

public class OSTSendRecvHelper {
    private String Tag="OST";
    private StringBuffer data = new StringBuffer();
    private SerialHelper mSerialHelper;
    private static OSTSendRecvHelper mSerialSendRecvHelper;
    private final byte cmdCheck[] = {(byte)0x02, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03};

    public static OSTSendRecvHelper getInstance() {
        if (mSerialSendRecvHelper == null) {
            mSerialSendRecvHelper = new OSTSendRecvHelper();
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
//                Log.i(Tag, data.toString());
                if (mOnOstReceiveListener != null && data.length() > 0) {
                    mOnOstReceiveListener.OnOstReceive(data.toString().trim());
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

    private OnOstSerialReceiveListener mOnOstReceiveListener;

    public void setOnOstSerialReceiveListener(OnOstSerialReceiveListener listener) {
        mOnOstReceiveListener = listener;
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
    public void sendcmdCheck() {
        try {
            mSerialHelper.send(cmdCheck);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }
    public void sendcmdPay(byte value) {
        byte[] cmd = new byte[6];
        cmd[0] = (byte) 0x88;
        cmd[1] = (byte) 0x01;
        cmd[2] = (byte) 0x00;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) 0x89;
        try {
            mSerialHelper.send(cmdCheck);
        } catch (Exception ex) {
            Log.e(Tag, ex.toString());
        }
    }
    public interface OnOstSerialReceiveListener {
        void OnOstReceive(String data);
    }

}
