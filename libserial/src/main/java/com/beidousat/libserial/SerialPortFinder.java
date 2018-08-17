/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.beidousat.libserial;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * 串口检索类
 *
 * @author LUOYE
 * @data 2015-07-05 10:45:02
 */
public class SerialPortFinder {

    /**
     * 串口驱动类
     *
     * @author LUOYE
     * @data 2015-07-05 10:47:36
     */
    public class Driver {
        /**
         * 构造函数
         *
         * @param name 串口名
         * @param root 串口根目录
         */
        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        /**
         * 驱动名
         */
        private String mDriverName;
        /**
         * 设备根目录
         */
        private String mDeviceRoot;
        /**
         * 设备列表
         */
        Vector<File> mDevices = null;

        /**
         * 获取设备列表
         *
         * @return 串口设备列表
         */
        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                        Log.d(TAG, "Found new device: " + files[i]);
                        mDevices.add(files[i]);
                    }
                }
            }
            return mDevices;
        }

        /**
         * 获取驱动名
         *
         * @return 驱动名
         */
        public String getName() {
            return mDriverName;
        }
    }

    /**
     * Log日志输出标识
     */
    private static final String TAG = "SerialPort";

    /**
     * 驱动列表
     */
    private Vector<Driver> mDrivers = null;

    /**
     * 获取驱动列表
     *
     * @return 驱动列表
     * @throws IOException
     */
    Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<Driver>();
            LineNumberReader r = new LineNumberReader(new FileReader(
                    "/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract
                // driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + drivername + " on "
                            + w[w.length - 4]);
                    mDrivers.add(new Driver(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    /**
     * 获取所有设备
     *
     * @return 设备列表
     */
    public String[] getAllDevices() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getName();
//                    String value = String.format("%s (%s)", device,
//                            driver.getName());
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * 获取所有设备路径
     *
     * @return 串口设备路径
     */
    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }
}
