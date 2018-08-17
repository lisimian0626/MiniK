package com.hoho.android.usbserial.util;


import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.R.attr.port;
import static android.content.ContentValues.TAG;

public class TBManager {

    private UsbManager mUsbManager;
    private static TBManager Instance = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private UsbSerialPort mPort = null;
    private Timer mSendPortInfoTimer, mReadPortInfoTimer;
    private String mPortInfo;
    private TBManagerListener mTBListener;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                    error(e.getMessage());
                }

                @Override
                public void onNewData(final byte[] data) {
                    mPortInfo = mPortInfo + bytesToHexString(data);
                }
            };


    public TBManager() {
    }

    public static TBManager getInstance() {
        if (Instance == null) {
            synchronized (TBManager.class) {
                if (Instance == null) {
                    Instance = new TBManager();
                }
            }
        }
        return Instance;
    }


    public void start(Context context, TBManagerListener listener) {
        mTBListener = listener;
        boolean openResult = initDriverPort(context);
        if (openResult) {
            if (mPort != null) {
                mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
                mExecutor.submit(mSerialIoManager);
            }

            startSendPortTimer();
            startReadPortTimer();
        }
    }

    private void startReadPortTimer() {
        mReadPortInfoTimer = new Timer();
        mReadPortInfoTimer.schedule(new TimerTask() {
            public void run() {
                parsePortInfo();
            }
        }, 0, 300);
    }

    private void parsePortInfo() {
        if (TextUtils.isEmpty(mPortInfo)) {
            return;
        }
        if (mPortInfo.contains("020b") && mPortInfo.length() >= mPortInfo.indexOf("020b") + 22) {
            String newb = mPortInfo.substring(mPortInfo.indexOf("020b"), mPortInfo.indexOf("020b") + 22);
            String checkStr = newb.substring(6, 8);
            if (checkStr.equals("10")) {
                haveNewBi();
            }
            mPortInfo = mPortInfo.substring(mPortInfo.indexOf(newb) + newb.length(), mPortInfo.length());
        }
    }

    private void haveNewBi() {
        if (mTBListener != null) {
            mTBListener.onNewBi();
        }
    }

    private void error(String error) {
        if (mTBListener != null) {
            Log.d(TAG, "error :" + error);
            mTBListener.onError(error);
        }
    }

    private void startSendPortTimer() {
        mSendPortInfoTimer = new Timer();
        mSendPortInfoTimer.schedule(new TimerTask() {
            public void run() {
                mSerialIoManager.writeAsync(hexStringToBytes("0208107F10000377"));
            }
        }, 0, 1000);
    }

    private boolean initDriverPort(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> drivers = null;

        try {
            drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        } catch (Exception e) {
            Log.w(TAG, "findAllDrivers Exception ex:" + e.toString());
            error("未插投币器");
            return false;
        }

        List<UsbSerialPort> ports = new ArrayList<UsbSerialPort>();

        UsbSerialDriver usbSerialDriver = null;

        if (drivers != null && drivers.size() > 0)
            for (UsbSerialDriver driver : drivers) {
                Log.d(TAG, "initDriverPort driver ManufacturerName:" + driver.getDevice().getManufacturerName() + " DeviceName:" + driver.getDevice().getDeviceName());
                if ("FTDI".equals(driver.getDevice().getManufacturerName())) {
                    usbSerialDriver = driver;
                }
            }

        if (usbSerialDriver != null) {
            ports.addAll(usbSerialDriver.getPorts());
        } else {
            error("未插投币器");
            return false;
        }

//        if (drivers.size() > 0) {
//            ports.addAll(drivers.get(0).getPorts());
//        } else {
//            //识别不到usb端口
//            error("识别不到usb端口");
//        }

        if (ports.size() > 0) {
            mPort = ports.get(0);
        } else {
            //识别不到usb端口
            error("识别不到投币器端口");
            return false;
        }
        UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
        if (connection == null) {
            //打开端口连接异常
            error("连接投币器异常");
            return false;
        }
        try {
            mPort.open(connection);
            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            error("无法打开端口");
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
            return false;
        }

        return true;
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }


    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public void stop() {
        if (mSendPortInfoTimer != null) {
            mSendPortInfoTimer.cancel();
        }
        if (mReadPortInfoTimer != null)
            mReadPortInfoTimer.cancel();
        mTBListener = null;
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    public interface TBManagerListener {
        void onNewBi();

        void onError(String error);
    }

}
