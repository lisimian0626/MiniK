package com.beidousat.karaoke.player;

import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by J Wong on 2017/4/26.
 */

public class FileDecoder {

    private static final String SONG_HEAD = "BEIDOU  ";

    /**
     * 修改文件中的某一部分的数据测试:将字定位置的字母改为大写
     *
     * @param fName        :要修改的文件名字
     * @param start:起始字节
     * @param len:要修改多少个字节
     * @return :是否修改成功
     * @throws Exception:文件读写中可能出的错
     * @author javaFound
     */
    public boolean changeFile(String fName) {
        try {
//            int len = 8 + 1024;
            //创建一个随机读写文件对象
            RandomAccessFile raf = new RandomAccessFile(fName, "rw");
            long totalLen = raf.length();
            System.out.println("文件总长字节是: " + totalLen);
//打开一个文件通道
            FileChannel channel = raf.getChannel();
//映射文件中的某一部分数据以读写模式到内存中
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8 + 1024);
//示例修改字节
            byte[] bytes = new byte[1024];

            for (int i = 7; i < 1024; i++) {
                byte src = buffer.get(i);
//                buffer.put(i, (byte) (src - 31));//修改Buffer中映射的字节的值
                bytes[i - 7] = src;
                System.out.println("被改为大写的原始字节是:" + src);
            }
            buffer.put(Encrypt(bytes, 1024, getKeyByte()), 0, 1024);

            buffer.force();//强制输出,在buffer中的改动生效到文件
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public static byte[] Encrypt(byte[] bytToBeEncs, int nBeEncSize, byte[] bytKeys) {
        int i, j;
        int nKeySize = bytKeys.length;
        byte[] bytToResults = new byte[nBeEncSize];
        for (i = 0; i < nBeEncSize; i = i + nKeySize) {
            for (j = 0; j < nKeySize; j++) {
                bytToResults[i + j] = Gen(bytToBeEncs[i + j], bytKeys[j], intToByte(j));
            }
        }
        return bytToResults;
    }


    private static byte Gen(byte bytData, byte bytKey, byte index) {
        byte bytResult = intToByte(bytData ^ bytKey ^ (0xFF - index));
        return bytResult;
    }

    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static byte[] getKeyByte() throws IOException {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile("/sdcard/SongSecurity.key", "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
            System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public void doRead() throws IOException {
//        RandomAccessFile  aFile = new RandomAccessFile("C:/goods.txt", "rw");
//        FileChannel   inChannel = aFile.getChannel();
//        int bytesRead = inChannel.read(buf);
//        while (bytesRead != -1) {
//            System.out.println("Read " + bytesRead);
//            buf.flip();
//            while (buf.hasRemaining())
//                System.out.print((char) buf.get());
//
//            buf.clear();
//            bytesRead = inChannel.read(buf);
//        }
//
//        aFile.close();
//    }


//    public static void doCopy() {
//        RandomAccessFile aFile=null;
//        try {
//             aFile = new RandomAccessFile("C:/goods.txt", "rw");
//            FileChannel inChannel = aFile.getChannel();
//            RandomAccessFile bFile = new RandomAccessFile("C:/22.log", "rw");
//            FileChannel outChannel = bFile.getChannel();
//            inChannel.transferTo(0, inChannel.size(), outChannel);
//            System.out.println("Copy over");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void readFileByBybeBuffer(String filePath, String desFile) {
        long timeMillis = System.currentTimeMillis();
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
//            fileInputStream = new FileInputStream(filePath);
//            fileOutputStream = new FileOutputStream(desFile);
//            byte[] mark = new byte[8];
//            fileInputStream.read(mark, 0, SONG_HEAD.length());
//            String strMark = new String(mark);
//            if (SONG_HEAD.equals(strMark)) {//加密视频
//                Logger.d("FileDecoder", "加密视频 ==============>");
//                byte[] bytSrcData = new byte[1024];
//                int nDataLen = fileInputStream.read(bytSrcData, 0, bytSrcData.length);
//                Logger.d("FileDecoder", "加密视频 ==============>");
//                byte[] bytTagData = Encrypt(bytSrcData, nDataLen, getKeyByte());
//                fileOutputStream.write(bytTagData);
//
//            } else {
//                Logger.d("FileDecoder", "未加密视频 ==============>");
//            }

//            RandomAccessFile randomFile = new RandomAccessFile(desFile, "rw");
//            long fileLength = randomFile.length();
//            randomFile.seek(fileLength);
//            randomFile.writeBytes();
//            randomFile.close();
//
            // 获取源文件和目标文件的输入输出流
            fileInputStream = new FileInputStream(filePath);
            fileOutputStream = new FileOutputStream(desFile);
            // 获取输入输出通道
            FileChannel fcIn = fileInputStream.getChannel();
            FileChannel fcOut = fileOutputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                // clear方法重设缓冲区，使它可以接受读入的数据
                buffer.clear();
                // 从输入通道中将数据读到缓冲区
                int r = fcIn.read(buffer);
                if (r == -1) {
                    break;
                }
                // flip方法让缓冲区可以将新读入的数据写入另一个通道
                buffer.flip();
                fcOut.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger.d("FileDecoder", "decode time:" + (System.currentTimeMillis() - timeMillis) + " ret:");
    }

    public static void decode(File src, File des) {
        long timeMillis = System.currentTimeMillis();
        try {
            FileInputStream fileInputStream = new FileInputStream(src);
            FileOutputStream fileOutputStream = new FileOutputStream(des);
            byte[] mark = new byte[8];
            fileInputStream.read(mark, 0, SONG_HEAD.length());
            String strMark = new String(mark);
            if (SONG_HEAD.equals(strMark)) {// 加密视频
                Logger.d("FileDecoder", "加密视频 ==============>");
                byte[] bytSrcData = new byte[1024];
                int nDataLen = fileInputStream.read(bytSrcData, 0, bytSrcData.length);
                Logger.d("FileDecoder", "加密视频 ==============>");
                byte[] bytTagData = Encrypt(bytSrcData, nDataLen, getKeyByte());
                fileOutputStream.write(bytTagData);
                byte[] buffer = new byte[1024];
                int byteread = 0; // 读取的字节数
                while ((byteread = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteread);
                }
            } else {
                Logger.d("FileDecoder", "未加密视频 ==============>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("FileDecoder", "decode time:" + (System.currentTimeMillis() - timeMillis) + " ret:");
//        try {
//            FileChannel inputChannel = null;
//            FileChannel outputChannel = null;
//            try {
//                inputChannel = new FileInputStream(source).getChannel();
//                outputChannel = new FileOutputStream(dest).getChannel();
//
//                //创建一个随机读写文件对象
//                java.io.RandomAccessFile raf = new java.io.RandomAccessFile(source, "rw");
//                long totalLen = raf.length();
//                System.out.println("文件总长字节是: " + totalLen);
//                java.nio.channels.FileChannel channel = raf.getChannel();
//                java.nio.MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8 + 1024);
//                byte[] bytes = new byte[1024];
//                for (int i = 7; i < 1024; i++) {
//                    byte src = buffer.get(i);
//                    bytes[i - 7] = src;
//                }
//                ByteBuffer buf = ByteBuffer.allocate(1024);
//                buf.clear();
//                byte[] decodes = Encrypt(bytes, 1024, getKeyByte());
//                buf.put(decodes);
//                buf.flip();
//
//                while (buf.hasRemaining())
//                    outputChannel.write(buf);
////                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//                inputChannel.transferTo(8 + 1024, inputChannel.size() - (8 + 1024), outputChannel);
//
//            } finally {
//                inputChannel.close();
//                outputChannel.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
