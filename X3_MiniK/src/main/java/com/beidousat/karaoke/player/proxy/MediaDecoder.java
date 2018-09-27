package com.beidousat.karaoke.player.proxy;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * author: Hanson
 * date:   2017/4/26
 * describe:
 */

public class MediaDecoder {
    private static String SONG_HEAD = "BEIDOU  ";
    private static final String TAG = "MediaDecoder";

    public static boolean UnEncrypeFile(String strKeyFilePath, String strSrcPath, String strTagPath) {
        boolean blnResult = false;
        byte[] bytKeys;
        FileInputStream pKeyStream = null;
        FileInputStream pSrcStream = null;
        FileOutputStream pTagStream = null;

        try {
            File file = new File(strKeyFilePath);

            if (file.exists()) {
                pKeyStream = new FileInputStream(strKeyFilePath);
                bytKeys = new byte[(int) file.length()];
                int nKeyLen = pKeyStream.read(bytKeys);

                File srcFile = new File(strSrcPath);
                if (srcFile.exists()) {
                    byte[] bytHeadData = new byte[SONG_HEAD.length()];
                    pSrcStream = new FileInputStream(strSrcPath);
                    //SONG_HEAD
                    pSrcStream.read(bytHeadData, 0, SONG_HEAD.length());
                    String strHead = new String(bytHeadData);
                    if (strHead.equals(SONG_HEAD)) {
                        byte[] bytSrcData = new byte[1024];
                        //pSrcStream.Seek(SONG_HEAD.Length,SeekOrigin.Current);
                        int nDataLen = pSrcStream.read(bytSrcData);
                        byte[] bytTagData = Encrypt(bytSrcData, nDataLen, bytKeys);

                        pTagStream = new FileOutputStream(strTagPath);
                        pTagStream.write(bytTagData);
                        byte[] pBuffer = new byte[16384];

                        int rlen = 0;
                        while ((rlen = pSrcStream.read(pBuffer)) > 0) {
                            pTagStream.write(pBuffer, 0, rlen);
                        }
                        pTagStream.flush();
                        blnResult = true;
                    } else {
                        Log.e(TAG, "提示: " + strSrcPath + " 文件已解密!");
                    }
                } else {
                    Log.e(TAG, "提示: 请选择有效的原文件!");
                }
            } else {
                Log.e(TAG, "提示: 请选择有效的KEY文件!");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (pKeyStream != null) pKeyStream.close();
                if (pSrcStream != null) pSrcStream.close();
                if (pTagStream != null) pTagStream.close();
            } catch (Exception e) {

            }
        }
        return blnResult;
    }


    public static byte[] Encrypt(byte[] bytToBeEncs, int nBeEncSize, byte[] bytKeys) {
        int i, j;
        int nKeySize = bytKeys.length;
        byte[] bytToResults = new byte[nBeEncSize];
        for (i = 0; i < nBeEncSize; i = i + nKeySize) {
            for (j = 0; j < nKeySize; j++) {
                bytToResults[i + j] = Gen(bytToBeEncs[i + j], bytKeys[j], (byte)j);
            }
        }
        return bytToResults;
    }


    private static byte Gen(byte bytData, byte bytKey, byte index) {


        byte bytResult = (byte)(bytData ^ bytKey ^ (0xFF - index));
        return bytResult;
    }
}
