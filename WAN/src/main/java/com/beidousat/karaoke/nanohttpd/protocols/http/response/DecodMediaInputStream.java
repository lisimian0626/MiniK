package com.beidousat.karaoke.nanohttpd.protocols.http.response;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.beidousat.karaoke.player.proxy.MediaDecoder.Encrypt;

/**
 * author: Hanson
 * date:   2017/4/27
 * describe:MediaPlayer(简称MP)播放本地加密后Mp4的流程如下：
 *          MP---(http)---->NanoHttpd(本地代理服务器)---(file)---->本地文件--
 *          ----(加密文件流)---->DecodMediaInputStream----(解密文件流)----->MP;
 *          至此完成整个MP访问SD卡中加密Mp4文件流程。
 * 当前MP4文件解密后生成的新文件会比加密文件少8个字节（加密算法规定，换加密算法，需要此类）
 * DecodMediaInputStream作用就是进行文件解密，虚拟一个解密文件。因此在收到带Range Http
 * 请求时skip字节并不是加密文件，而是解密文件即重写skip方法。
 */

public class DecodMediaInputStream extends FilterInputStream {
    private int encodeLen = 1024 + SONG_HEAD.length();
    private boolean isDecoded = false;
    private File mFile;
    private long mLessEncodeLen = 0L;

    private static String SONG_HEAD = "BEIDOU  ";
    private static final String TAG = "DecodMediaInputStream";

    /**
     * Constructs a new {@code FilterInputStream} with the specified input
     * stream as source.
     * <p>
     * <p><strong>Warning:</strong> passing a null source creates an invalid
     * {@code FilterInputStream}, that fails on every method that is not
     * overridden. Subclasses should check for null in their constructors.
     *
     * @param in the input stream to filter reads on.
     */
    public DecodMediaInputStream(InputStream in) {
        super(in);
    }

    public DecodMediaInputStream(InputStream in, File file) {
        this(in);
        mFile = file;
    }

    @Override
    public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int realReadLen = 0;

        if (byteOffset != 0) {
            throw new IOException("the first reading must start at 0");
        }
        if (byteCount < encodeLen) {
            throw new IOException("read lenght must large than 1032 byte");
        }

        byte[] encodeByte = new byte[encodeLen];
        if (in.read(encodeByte, 0, encodeLen) > 0) {
            byte[] head = unEncrypeFile(encodeByte);

            if (head != null) {
                isDecoded = true;
                System.arraycopy(head, (int)mLessEncodeLen, buffer, 0, head.length - (int)mLessEncodeLen);
                realReadLen = head.length - (int)mLessEncodeLen;
                realReadLen += in.read(buffer, byteOffset + head.length - (int)mLessEncodeLen, byteCount - encodeLen);
            } else {
                System.arraycopy(encodeByte, 0, buffer, 0, encodeByte.length);
                realReadLen = encodeByte.length;
                realReadLen += in.read(buffer, byteOffset + encodeByte.length, byteCount - encodeLen);
            }
        }

        return realReadLen;
    }

    private void readHeader() throws IOException {
        if (mFile != null && mFile.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(mFile);
                byte[] bytHead = new byte[8];
                fileInputStream.skip(0);
                int rlen = fileInputStream.read(bytHead, 0, 8);
                String strHead = new String(bytHead);
                if (rlen == 8 && strHead.equals(SONG_HEAD)) {
                    isDecoded = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean isDecoded() {
        try {
            readHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isDecoded;
    }


    @Override
    public long skip(long n) throws IOException {
        long pos = n;

        if (isDecoded() && n >= encodeLen) {
            if (n < encodeLen)
                throw new IOException("加密文件不能seek");
            pos += 8;
        } else if (isDecoded() && n < encodeLen) {
            mLessEncodeLen = n;
            pos = 0;
        }

        return super.skip(pos);
    }

    private byte[] unEncrypeFile(byte[] head) {
        byte[] bytKeys = null;
        byte[] bytTagData = null;
        FileInputStream pKeyStream = null;
        String strKeyFilePath = "/sdcard/BNS/SongSecurity.key";
        try {
            File file = new File(strKeyFilePath);

            if (file.exists()) {
                pKeyStream = new FileInputStream(strKeyFilePath);
                bytKeys = new byte[(int) file.length()];
                int nKeyLen = pKeyStream.read(bytKeys);

                byte[] bytHeadData = new byte[SONG_HEAD.length()];
                //SONG_HEAD
                System.arraycopy(head, 0, bytHeadData, 0, SONG_HEAD.length());
                String strHead = new String(bytHeadData);
                if (strHead.equals(SONG_HEAD)) {
                    byte[] bytSrcData = new byte[1024];
                    System.arraycopy(head, SONG_HEAD.length(), bytSrcData, 0, bytSrcData.length);
                    int nDataLen = bytSrcData.length;
                    bytTagData = Encrypt(bytSrcData, nDataLen, bytKeys);
                    Log.e(TAG, "解密成功");

                    encodeLen = nDataLen + SONG_HEAD.length();
                } else {
//                    Log.e(TAG, "不需要解密");
                }

            } else {
//                Log.e(TAG, "提示: 请选择有效的KEY文件!");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (pKeyStream != null) pKeyStream.close();
            } catch (Exception e) {

            }
        }

        return bytTagData;
    }
}
