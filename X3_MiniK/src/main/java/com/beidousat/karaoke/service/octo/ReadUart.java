
package com.beidousat.karaoke.service.octo;

import java.io. File;
import java.io. IOException;
import java.io. InputStream;
import java.io. OutputStream;
import android.util.Log;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import android_serialport_api.SerialPort;

public


class ReadUart
{
    private static final String[] ttys_list =
            {
                    "/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4"
            };


    private static final String[] baut_list =
            {
                    "9600"
            };
    private static byte cmdPollStauts[] = {(byte)0x02, (byte)0x01, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x3};
    private static int ackNormalStauts[] = {2, 1, 0xaa, 0, 0, 3};
    //private byte buff[16] ;

    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    protected			byte[] mUartBuffer = new byte[1024];
    private static final String TAG = "OCT";
    int[] integer_buffer = new int[10];
    private int fd = -1;
    int blocksize = 0;
    int print_flag = -4;


    public int initUart (int portIndex, int baudrate)
    {

        Log.d (TAG, " initUart portIndex = " + portIndex + ", baud = " + baudrate);

        //baud,N,8,1
        try
        {
            mSerialPort 				= new SerialPort (new File (ttys_list[portIndex]), baudrate);
        }

        catch (Exception e)
        {
            Log.d (TAG, e.getMessage ());
        }

        mInputStream				= mSerialPort.getInputStream ();
        mOutputStream			= mSerialPort.getOutputStream ();


        return 0;

    }


    public void closeUart ()
    {
        int 						ret = 0;

        try
        {
            mInputStream.close ();
            mOutputStream.close ();
        }

        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            Log.d (TAG, e1.getMessage ());
        }

        mSerialPort.close ();

        //close(fd);
    }




    private void sleepMills(int m)
    {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch(Exception e) {

        }
    }

    private int[] byte2Int(byte b[])
    {	int val[] = new int[b.length];

        for(int i = 0; i<b.length; i++)
            val[i] = b[i]&0xFF;
        return val;
    }

    private boolean arrayCompare(int a[], int b[])
    {
		/*
		if(a.length != b.length){
			Log.d(TAG, "a.length != b.length");
			return false;
		}
		*/
        for(int i = 0; i<a.length; i++){
            if(a[i] != b[i]){
                Log.d(TAG, "a[i]:"+a[i] +" b[i]="+b[i]);
                return false;
            }
            else
                continue;
        }
        return true;

    }

    public int pollStatus()
    {
        int size = 0;
        try
        {
            mOutputStream.write (cmdPollStauts);
            sleepMills(70);
            size = mInputStream.read(mUartBuffer, 0, 6);
            int tmp[] = byte2Int(mUartBuffer);
				/*
				Log.d (TAG, "size:" + size + ", " + Integer.toHexString (tmp[0]) + " " + Integer.toHexString (tmp[1])
										+ " " + Integer.toHexString (tmp[2]) + " " + Integer.toHexString (tmp[3])
										+ " " + Integer.toHexString (tmp[4]) + " " + Integer.toHexString (tmp[5]));
				Log.d (TAG, "size:" + size + ", " + Integer.toHexString (ackNormalStauts[0]) + " " + Integer.toHexString (ackNormalStauts[1])
										+ " " + Integer.toHexString (ackNormalStauts[2]) + " " + Integer.toHexString (ackNormalStauts[3])
										+ " " + Integer.toHexString (ackNormalStauts[4]) + " " + Integer.toHexString (ackNormalStauts[5]));
				*/
            if(6 == size){
                if(arrayCompare(ackNormalStauts, tmp)){
                    Log.d(TAG, "normal status");
                    return 0;
                }
                else
                {
                    Log.d(TAG, "unnormal status");
                    //替他状态比较
                    return 1;
                }
            }
            else
            {
                //ack不正常
                return -1;
            }

        }
        catch (IOException e)
        {
            Log.d (TAG, e.getMessage ());

        }
        return -9;
    }


}


