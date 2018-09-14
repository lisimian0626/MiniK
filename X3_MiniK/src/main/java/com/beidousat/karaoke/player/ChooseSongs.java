package com.beidousat.karaoke.player;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.FmPayMeal;
import com.beidousat.karaoke.ui.dlg.FmPaySevice;
import com.beidousat.karaoke.ui.dlg.FmRoomSet;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.PreferenceUtil;
import com.beidousat.libbns.util.ServerFileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by J Wong on 2015/10/10 11:10.
 */
public class ChooseSongs {

    private final static String PREF_KEY_CHOOSE_SONGS = "pref_key_choose_songs2";

    private Context mContext;
    private static ChooseSongs mSelectedSongs;
    private List<Song> mSongInfos = new ArrayList<Song>();
    private List<Song> mSongHasSung = new ArrayList<>();

    private final static int MAX_SIZE = 50;

    public static ChooseSongs getInstance(Context context) {
        if (mSelectedSongs == null) {
            synchronized (ChooseSongs.class) {
                if (mSelectedSongs == null) {
                    mSelectedSongs = new ChooseSongs(context);
                }
            }
        }
        return mSelectedSongs;
    }

    private ChooseSongs(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        String string = PreferenceUtil.getString(mContext, PREF_KEY_CHOOSE_SONGS, "");
        Logger.d(getClass().getSimpleName(), "choose string:" + string);
        if (!TextUtils.isEmpty(string) && !"[]".equals(string)) {
            try {
                Gson gson = new Gson();
                List<Song> songs = gson.fromJson(string, new TypeToken<List<Song>>() {
                }.getType());
                if (songs != null && songs.size() > 0) {
                    mSongInfos = songs;
                    for (Song song : mSongInfos) {
                        if (TextUtils.isEmpty(song.SongFilePath) || TextUtils.isEmpty(song.SimpName)) {
                            mSongInfos.remove(song);
                        }
                    }
                    EventBusUtil.postSticky(EventBusId.id.CHOOSE_SONG_CHANGED, mSongInfos.size());
                }
            } catch (Exception e) {
                Logger.e(getClass().getSimpleName(), "init ex :" + e.toString());
            }
        }
    }

    private boolean isContainsSong(Song song) {
        try {
            if (mSongInfos != null && mSongInfos.size() > 0)
                for (Song song1 : mSongInfos) {
                    if (song1.ID.equals(song.ID)) {
                        return true;
                    }
                }
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "isContainsSong ex:" + e.toString());
        }
        return false;
    }

    private boolean canAddSong() {
        if (PreferenceUtil.getBoolean(Main.mMainActivity,"isSingle", false)) {
            if (PrefData.getLastAuth(mContext)) {
                return true;
            } else {
                if (KBoxStatusInfo.getInstance().getKBoxStatus() != null) {
                    if (KBoxStatusInfo.getInstance().getKBoxStatus().code == 2001 || KBoxStatusInfo.getInstance().getKBoxStatus().code == 3001) {
                        tipMessage(R.string.room_num_error);
                    } else if (KBoxStatusInfo.getInstance().getKBoxStatus().code == 2003) {
                        tipMessage(R.string.no_pay_service);
                    } else {
                        if(mContext!=null){
                            ToastUtils.toast(mContext, KBoxStatusInfo.getInstance().getKBoxStatus().msg);
                        }
                    }
                } else {
                    if(mContext!=null) {
                        ToastUtils.toast(mContext, mContext.getApplicationContext().getString(R.string.device_auth_fail));
                    }
                }
                return false;
            }
        } else {
            if (PrefData.getLastAuth(mContext)) {
                if (!DiskFileUtil.hasDiskStorage()) {
                    tipMessage(R.string.hand_disk);
                    return false;
                }
                if (BoughtMeal.getInstance().getTheFirstMeal() == null) {
                    tipMessage(R.string.tip_pay);
                    return false;
                }

                if (BoughtMeal.getInstance().isBuySong()) {
                    if (BoughtMeal.getInstance().getLeftSongs() > 0) {//
                        return true;
                    } else {
                        tipMessage(R.string.tip_used_up);
                        return false;
                    }
                } else if (BoughtMeal.getInstance().isBuyTime()) {
                    if (BoughtMeal.getInstance().getLeftMillSeconds() > 0) {
                        return true;
                    } else {
                        tipMessage(R.string.tip_used_up);
                        return false;
                    }
                }
                else {
                    tipMessage(R.string.tip_pay);
                    return false;
                }
            } else {
                if(mContext!=null) {
                    ToastUtils.toast(mContext, mContext.getApplicationContext().getString(R.string.device_auth_fail));
                }
            }
        }
        return false;
    }

    private PromptDialog mPromptDialog;

    public void tipMessage(int resId) {
        try {
            if (mPromptDialog == null || !mPromptDialog.isShowing()) {
                mPromptDialog = new PromptDialog(Main.mMainActivity);
            }
            if(PreferenceUtil.getBoolean(Main.mMainActivity,"isSingle", false)){
                     if(R.string.no_pay_service==resId){
                         mPromptDialog.setPositiveButton(mContext.getString(R.string.pay_for_service), new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 showPayService();
                             }
                         });
                         mPromptDialog.setMessage(mContext.getString(R.string.no_pay_service));
                         mPromptDialog.setClose(true);
                     }
            }else{
                mPromptDialog.setMessage(mContext.getString(resId));
                    if(R.string.hand_disk==resId){
                        mPromptDialog.setPositiveButton(mContext.getString(R.string.close), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                             mPromptDialog.dismiss();
                            }
                        });
                    }else{
                        mPromptDialog.setPositiveButton(mContext.getString(R.string.buy), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CommonDialog dialog = CommonDialog.getInstance();
                                dialog.setShowClose(true);
                                int pageType = BoughtMeal.getInstance().isMealExpire() ?
                                        FmPayMeal.TYPE_NORMAL : FmPayMeal.TYPE_NORMAL_RENEW;
                                dialog.setContent(FmPayMeal.createMealFragment(pageType,null));
                                if (!dialog.isAdded()) {
                                    dialog.show(Main.mMainActivity.getSupportFragmentManager(), "commonDialog");
                                }
                            }
                        });
                    }


            }

            mPromptDialog.show();
        } catch (Exception e) {
            Logger.d(getClass().getSimpleName(), "tipMessage ex:" + e.toString());
        }
    }

    private PromptDialog mDlgDownload;

    private void download(final Song songInfo) {
        Logger.d("ChooseSongs", "onSongClick  file  not exist:");
        if (DiskFileUtil.hasDiskStorage()) {
            try {
                MyDownloader.getInstance().startDownload(
                        ServerFileUtil.getFileUrl(songInfo.download_url),
                        DiskFileUtil.getFileSavedPath(songInfo.SongFilePath), songInfo);
            } catch (Exception e) {
//                PromptDialog promptDialog = new PromptDialog(Main.mMainActivity);
//                promptDialog.setMessage(e.getMessage());
//                promptDialog.show();
                if(mContext!=null) {
                    ToastUtils.toast(mContext, mContext.getString(R.string.download_fail));
                }

/*
                            DialogFactory.showErrorDialog(mContext, e.getMessage(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            */

            }
        } else {
//            Toast.makeText(mContext, "检测到没有硬盘，请插入硬盘！", Toast.LENGTH_SHORT).show();
            if(mContext!=null) {
                ToastUtils.toast(mContext.getApplicationContext(), mContext.getApplicationContext().getString(R.string.hand_disk));
            }
        }
//        if (mDlgDownload == null || !mDlgDownload.isShowing()) {
//            mDlgDownload = new PromptDialog(Main.mMainActivity);
//            mDlgDownload.setMessage(R.string.download_prompt);
//            mDlgDownload.setPositiveButton(mContext.getString(R.string.download_background), new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (DiskFileUtil.hasDiskStorage()) {
//                        try {
//                            MyDownloader.getInstance().startDownload(
//                                    ServerFileUtil.getFileUrl(songInfo.SongFilePath),
//                                    DiskFileUtil.getFileSavedPath(songInfo.SongFilePath), songInfo);
//                        } catch (Exception e) {
//                            PromptDialog promptDialog = new PromptDialog(Main.mMainActivity);
//                            promptDialog.setMessage(e.getMessage());
//                            promptDialog.show();
///*
//                            DialogFactory.showErrorDialog(mContext, e.getMessage(), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            */
//
//                        }
//                    } else {
//                        Toast.makeText(mContext, "检测到没有硬盘，请插入硬盘！", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            mDlgDownload.setShowButton2(true);
//            mDlgDownload.show();
//        }

//        DialogFactory.showDownloadDialog(mContext, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                try {
//                    MyDownloader.getInstance().startDownload(
//                            ServerFileUtil.getFileUrl(songInfo.SongFilePath),
//                            DiskFileUtil.getFileSavedPath(songInfo.SongFilePath), songInfo);
//                } catch (Exception e) {
//                    DialogFactory.showErrorDialog(mContext, e.getMessage(), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            }
//        });
    }
    private void showPayService() {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setContent(FmPaySevice.createPaySeviceFragment());
        if (!dialog.isAdded()) {
            dialog.show(Main.mMainActivity.getSupportFragmentManager(), "pay_service");
        }
    }
    private void showRoomSet() {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        dialog.setContent(FmRoomSet.createRoomSetFragment());
        if (!dialog.isAdded()) {
            dialog.show(Main.mMainActivity.getSupportFragmentManager(), "commonDialog");
        }
    }
    public boolean addSong(final Song songInfo) {
        try {
            if (canAddSong()) {

                if (isContainsSong(songInfo)) {
                    return false;
                }

                if (mSongInfos.isEmpty() && BoughtMeal.getInstance().isBuySong()) {
                    Logger.d("ChooseSongs", "addSong isBuySong isEmpty");
                    final File file = DiskFileUtil.getDiskFileByUrl(songInfo.SongFilePath);
                    if (file == null) {//下载
                        download(songInfo);
                        return false;
                    } else {
                        //包曲第一首播放提示
                        songInfo.RecordFile = songInfo.ID + "_" + System.currentTimeMillis();
                        mSongInfos.add(songInfo);
                        Logger.d(getClass().getSimpleName(), "add song IsGradeLib:" + songInfo.IsGradeLib + "");
                        new ScoreNoteDownloader().downloadNote(songInfo);
                        if (mSongInfos.size() == 1) {
                            EventBusUtil.postSticky(EventBusId.id.PLAYER_PLAY_SONG, songInfo);
                        }
                        sendChooseChangedMsg();
//                        if (mPromptDialog == null || !mPromptDialog.isShowing()) {
//                            mPromptDialog = new PromptDialog(Main.mMainActivity);
//                            mPromptDialog.setMessage(mContext.getString(R.string.tip_add_first_song));
//                            mPromptDialog.setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    songInfo.RecordFile = songInfo.ID + "_" + System.currentTimeMillis();
//                                    mSongInfos.add(songInfo);
//                                    Logger.d(getClass().getSimpleName(), "add song IsGradeLib:" + songInfo.IsGradeLib + "");
//                                    new ScoreNoteDownloader().downloadNote(songInfo);
//                                    if (mSongInfos.size() == 1) {
//                                        EventBusUtil.postSticky(EventBusId.id.PLAYER_PLAY_SONG, songInfo);
//                                    }
//                                    sendChooseChangedMsg();
//                                }
//                            });
//                            mPromptDialog.setShowButton2(true);
//                            mPromptDialog.show();
//                        }
                        return true;
                    }
                } else {
                    Logger.d("ChooseSongs", "addSong isBuySong not empty");
                    final File file = DiskFileUtil.getDiskFileByUrl(songInfo.SongFilePath);
                    if (file == null) {
                        Logger.d("ChooseSongs", "addSong isBuySong not empty disk file not exit");
                        download(songInfo);
                    } else {
                        Logger.d("ChooseSongs", "addSong isBuySong not empty disk file is exit");
                        songInfo.RecordFile = songInfo.ID + "_" + System.currentTimeMillis();
                        mSongInfos.add(songInfo);
                        Logger.d(getClass().getSimpleName(), "add song IsGradeLib:" + songInfo.IsGradeLib + "");
                        new ScoreNoteDownloader().downloadNote(songInfo);
                        if (mSongInfos.size() == 1) {
                            EventBusUtil.postSticky(EventBusId.id.PLAYER_PLAY_SONG, songInfo);
                        }
                        sendChooseChangedMsg();
                    }
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "addSong ex:" + e.toString());
            return false;
        }
    }


    public boolean add2Top(final Song songInfo) {
        try {
            if (canAddSong()) {
                if (TextUtils.isEmpty(songInfo.SongFilePath) || TextUtils.isEmpty(songInfo.SimpName)) {
                    return false;
                }
                if (mSongInfos.isEmpty()) {
                    return addSong(songInfo);
                } else {
                    if (mSongInfos.get(0).ID.equals(songInfo.ID)) {
                        return false;
                    }

                    songInfo.isPrior = true;
                    final File file = DiskFileUtil.getDiskFileByUrl(songInfo.SongFilePath);
                    if (file == null) {
                        Logger.d("AdtSong", "onSongClick  file  not exist:");
                        download(songInfo);
                        return false;
                    } else {
                        if (mSongInfos.get(0).ID.equals(songInfo.ID)) {
                            return false;
                        }
                        for (Song song1 : mSongInfos) {
                            if (song1.ID.equals(songInfo.ID)) {
                                mSongInfos.remove(song1);
                                break;
                            }
                        }
                        songInfo.RecordFile = songInfo.ID + "_" + System.currentTimeMillis();
                        mSongInfos.add(1, songInfo);
                        new ScoreNoteDownloader().downloadNote(songInfo);
                        sendChooseChangedMsg();
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "add2Top ex:" + e.toString());
            return false;
        }
    }


    public void remove(int position) {
        try {
            mSongInfos.remove(position);
            sendChooseChangedMsg();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "remove ex:" + e.toString());
        }
    }

    public void removeSongById(String id) {
        try {
            if (mSongInfos != null && mSongInfos.size() > 0 && !getFirstSong().ID.equals(id)) {
                for (int i = 0; i < mSongInfos.size(); i++) {
                    Song song = mSongInfos.get(i);
                    if (song.ID.equals(id)) {
                        mSongInfos.remove(i);
                        sendChooseChangedMsg();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "removeSongById ex:" + e.toString());
        }
    }

    public void remove(Song song) {
        try {
            if (mSongInfos != null && mSongInfos.size() > 0) {
                mSongInfos.remove(song);
                sendChooseChangedMsg();
            }
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "remove ex:" + e.toString());
        }
    }

    public void cleanSung() {
        try {
            mSongHasSung.clear();
            sendChooseChangedMsg();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "cleanData ex:" + e.toString());
        }
    }


    public void cleanChoose() {
        try {
            mSongInfos.clear();
            sendChooseChangedMsg();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "cleanData ex:" + e.toString());
        }
    }

    private void cleanDataNotTop() {
        try {
            if (mSongInfos != null && mSongInfos.size() > 1) {
                for (int i = mSongInfos.size() - 1; i > 0; i--) {
                    mSongInfos.remove(i);
                }
            }
            sendChooseChangedMsg();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "cleanDataNotTop ex:" + e.toString());
        }
    }

    public int getChooseSize() {
        try {
            return mSongInfos.size();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "getChooseSize ex:" + e.toString());
        }
        return 0;
    }

    public Song getFirstSong() {
        return getChooseSize() > 0 ? mSongInfos.get(0) : null;
    }

    public Song getSecSong() {
        return getChooseSize() > 1 ? mSongInfos.get(1) : null;
    }

    public void removeSung(Song song) {
        try {
            mSongHasSung.remove(song);
            broadcastSungChanged();
        } catch (Exception e) {
            Logger.w(getClass().getSimpleName(), "removeSung ex:" + e.toString());
        }
    }


    public List<Song> getSongs() {
        return mSongInfos;
    }

    public void setSongs(List<Song> songs) {
        try {
            mSongInfos = songs;
            EventBusUtil.postSticky(EventBusId.id.CHOOSE_SONG_CHANGED, mSongInfos.size());
        } catch (Exception e) {
            Logger.i(getClass().getSimpleName(), "setSongs ex :" + e.toString());
        }
    }


    private void broadcastSungChanged() {
        EventBusUtil.postSticky(EventBusId.id.SUNG_SONG_CHANGED, "");
    }

    public List<Song> getHasSungSons() {
        return mSongHasSung;
    }

    public void add2Sungs(final Song songInfo) {
        try {
            if (TextUtils.isEmpty(songInfo.SongFilePath) || TextUtils.isEmpty(songInfo.SimpName)) {
                return;
            }
            mSongHasSung.add(0, songInfo);

            broadcastSungChanged();
            //upload record
//            new Handler().postDelayed(new Runnable() {//延时，避免录音文件没写完就上传
//                @Override
//                public void run() {
//                    Meal meal = BoughtMeal.getInstance().getTheFirstMeal();
//                    String orderSn = "";
//                    if (meal != null) {
//                        orderSn = meal.getOrderSn();
//                    }
//                    String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.RECORD_UPLOAD;
//                    String shareDomain = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.SHARE_HTML_URL;
//                    RecordFileUploader.getInstance(url).setShareDomain(shareDomain);
//                    RecordFileUploader.getInstance(url).addRecordUploadListener(ChooseSongs.this);
//
//                    RecordFileUploader.getInstance(url).addFile(AudioRecordFileUtil.getRecordFile(songInfo.RecordFile).getAbsolutePath(),
//                            orderSn, songInfo.ID, songInfo.SimpName, songInfo.SingerName, songInfo.score, PrefData.getRoomCode(mContext));
//                }
//            }, 2000);
        } catch (Exception e) {
            Logger.i(getClass().getSimpleName(), "add2Sungs ex :" + e.toString());
        }
    }


    private void sendChooseChangedMsg() {
        try {
            EventBusUtil.postSticky(EventBusId.id.CHOOSE_SONG_CHANGED, mSongInfos.size());
            updateChoosePreference();
        } catch (Exception e) {
            Logger.i(getClass().getSimpleName(), "sendChooseChangedMsg ex :" + e.toString());
        }
    }

    public String getSongPriorities(String id) {
        try {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Song info : mSongInfos) {
                if (info.ID.equals(id)) {
                    if (i == 0) {
                        builder.append(mContext.getString(R.string.playing)).append("  ");
                    } else if (i == 1) {
                        builder.append(mContext.getString(R.string.next_song)).append("  ");
                    } else {
                        builder.append(mContext.getString(R.string.priorities_x, i + 1)).append("  ");
                    }
                    break;
                }
                i++;
            }
            return builder.toString();
        } catch (Exception e) {
            Logger.i(getClass().getSimpleName(), "getSongPriorities ex :" + e.toString());
        }
        return "";
    }


    public String getSongPriorities(Song song) {
        try {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (Song info : mSongInfos) {
                if (info.ID.equals(song.ID)) {
                    if (i == 0) {
                        builder.append(mContext.getString(R.string.playing)).append("  ");
                    } else if (i == 1) {
                        builder.append(mContext.getString(R.string.next_song)).append("  ");
                    } else {
                        builder.append(mContext.getString(R.string.priorities_x, i + 1)).append("  ");
                    }
                    break;
                }
                i++;
            }
            return builder.toString();
        } catch (Exception e) {
            Logger.i(getClass().getSimpleName(), "getSongPriorities ex :" + e.toString());
        }
        return "";
    }

    public synchronized void updateChoosePreference() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    String str = gson.toJson(mSongInfos);
                    PreferenceUtil.setString(mContext, PREF_KEY_CHOOSE_SONGS, str);
                } catch (Exception e) {
                    Logger.e(getClass().getSimpleName(), "updateChoosePreference ex:" + e.toString());
                }
            }
        };
        thread.start();
    }


    public void shuffle() {
        try {
            if (mSongInfos != null && mSongInfos.size() >= 3) {
                Song topSong = mSongInfos.get(0);
                List<Song> list = new ArrayList<Song>();
                list.addAll(mSongInfos.subList(1, mSongInfos.size()));
                Collections.shuffle(list);
                mSongInfos.clear();
                mSongInfos.add(0, topSong);
                mSongInfos.addAll(list);
                sendChooseChangedMsg();
            }
        } catch (Exception e) {
            Logger.e(getClass().getSimpleName(), "shuffle ex :" + e.toString());

        }
    }


    public Song getSungByRecordFile(String file) {
        if (mSongHasSung != null && mSongHasSung.size() > 0)
            for (Song song : mSongHasSung) {
                if (song.RecordFile != null && song.RecordFile.equals(file)) {
                    return song;
                }
            }
        return null;
    }

    private Song getSongByRecordPath(String recordPath) {
        Logger.d("ChooseSongs", "getSongByRecordPath recordPath:" + recordPath);
        if (mSongHasSung != null && mSongHasSung.size() > 0) {
            for (Song song : mSongHasSung) {
                Logger.d("ChooseSongs", "getSongByRecordPath RecordFile:" + song.RecordFile);
                if (recordPath.endsWith(song.RecordFile + ".mp3")) {
                    return song;
                }
            }
        }
        return null;
    }
}
