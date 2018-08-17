package com.beidousat.score;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2016/6/12.
 */
public class ScoreFileUtil {

    /**
     * 读取评分文件
     *
     * @param strFilePath
     * @return
     */
    public static ArrayList<NoteInfo> readNoteFile(String strFilePath) {
        ArrayList<NoteInfo> noteList = new ArrayList<NoteInfo>();
        String path = strFilePath;
        Log.w("ScoreFileUtil", "readNoteFile file :" + strFilePath);
        //打开文件
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            Log.w("ScoreFileUtil", "readNoteFile exist file :" + file.getAbsolutePath());
            InputStream instream = null;
            InputStreamReader inputreader = null;
            BufferedReader buffreader = null;
            try {
                instream = new FileInputStream(file);
                if (instream != null) {
                    inputreader = new InputStreamReader(instream);
                    buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        if (line.length() > 0) {
//                            Logger.w("ScoreFileUtil", "readNoteFile line :" + line);
                            line = line.replace(";", "");
                            String[] segs = line.split(",");
                            float pos = Float.parseFloat(segs[0]);
                            float len = Float.parseFloat(segs[1]);
                            float key = Float.parseFloat(segs[2]);
                            int score = Integer.valueOf(segs[3]);
                            NoteInfo n = new NoteInfo(pos, len, key, score);
                            noteList.add(n);
//                            content += line + "\n";
                        }
                    }
                }

            } catch (Exception e) {
                Log.w("ScoreFileUtil", "readNoteFile ex:" + e.toString());
            } finally {
                if (buffreader != null) {
                    try {
                        buffreader.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNoteFile close buffreader ex:" + e.toString());
                    }
                }
                if (inputreader != null) {
                    try {
                        inputreader.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNoteFile close inputreader ex:" + e.toString());
                    }
                }
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNoteFile close instream ex:" + e.toString());
                    }
                }
            }
        }
        return noteList;
    }


    public static List<ScoreLineInfo> readNote2File(String strFilePath) {
        List<ScoreLineInfo> scoreLineInfos = new ArrayList<ScoreLineInfo>();
        String path = strFilePath;
        Log.w("ScoreFileUtil", "readNote2File file :" + strFilePath);
        //打开文件
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            Log.w("ScoreFileUtil", "readNote2File exist file :" + file.getAbsolutePath());
            InputStream instream = null;
            InputStreamReader inputreader = null;
            BufferedReader buffreader = null;
            try {
                instream = new FileInputStream(file);
                if (instream != null) {
                    inputreader = new InputStreamReader(instream);
                    buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        if (line.length() > 0) {
                            line = line.replace(";", "");
                            String[] segs = line.split(",");
                            float time = Float.parseFloat(segs[0]);
                            float score = Float.parseFloat(segs[1]);
                            ScoreLineInfo scoreLineInfo = new ScoreLineInfo(time, score);
                            scoreLineInfos.add(scoreLineInfo);
                        }
                    }
                }

            } catch (Exception e) {
                Log.w("ScoreFileUtil", "readNote2File ex:" + e.toString());
            } finally {
                if (buffreader != null) {
                    try {
                        buffreader.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNote2File close buffreader ex:" + e.toString());
                    }
                }
                if (inputreader != null) {
                    try {
                        inputreader.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNote2File close inputreader ex:" + e.toString());
                    }
                }
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception e) {
                        Log.w("ScoreFileUtil", "readNote2File close instream ex:" + e.toString());
                    }
                }
            }
        }
        return scoreLineInfos;
    }
}
