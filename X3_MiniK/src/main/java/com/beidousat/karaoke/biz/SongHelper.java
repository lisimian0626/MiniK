package com.beidousat.karaoke.biz;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.model.SongScoreRanking;
import com.beidousat.karaoke.model.UploadSongData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.Logger;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class SongHelper implements StoreHttpRequestListener {
    private static final String TAG = "SongHelper";
    private Context mContext;
    private SongFeedback songFeedback;
    private static SongHelper mSongHelper;
    private UploadSongData mUploadSongData;
    private int uploadCount = 0;

    public static SongHelper getInstance(Context context, SongFeedback cb) {
        if (mSongHelper == null) {
            mSongHelper = new SongHelper(context, cb);
        }
        return mSongHelper;
    }

    public SongHelper(Context context, SongFeedback cb) {
        this.mContext = context;
        this.songFeedback = cb;
    }

    /**
     * 上报歌曲信息
     * uploadSongData 信息对像
     */
    public void upLoadSongData(UploadSongData uploadSongData) {
        mUploadSongData = uploadSongData;
        upLoad(uploadSongData.getSongId(), uploadSongData.getSN(), uploadSongData.getPayTime(), uploadSongData.getFinishTime(), uploadSongData.getDuration(), uploadSongData.getScore());
    }


    /**
     * 上报歌曲信息
     * songID 歌曲id
     * orderSn 订单编号
     * playtime 开始点唱时间
     * finishtime 唱完时间
     * songlenght 歌曲长度
     * score 得分
     */
    private void upLoad(String songID, String orderSn, long playtime, long finishtime, int songlenght, int score) {
        if (ServerConfigData.getInstance().getServerConfig() != null && ServerConfigData.getInstance().getServerConfig().getStore_web() != null && !TextUtils.isEmpty(ServerConfigData.getInstance().getServerConfig().getStore_web())) {
            StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.UPLOAD_SONG);
            storeHttpRequest.addParam(HttpParamsUtils.initUploadSongParams(PrefData.getRoomCode(mContext), songID, orderSn, playtime, finishtime, songlenght, score));
            storeHttpRequest.setStoreHttpRequestListener(this);
            storeHttpRequest.post();
        }
    }

    /**
     * 上报下载歌曲信息
     * sn 设备串号
     * savaPath 当前歌曲保存路径
     */
    public void sendDownLoad(String sn, String savaPath) {
        if (ServerConfigData.getInstance().getServerConfig() != null && ServerConfigData.getInstance().getServerConfig().getStore_web() != null && !TextUtils.isEmpty(ServerConfigData.getInstance().getServerConfig().getStore_web())) {
            StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.DOWNLOAD_TIMES);
            storeHttpRequest.addParam(HttpParamsUtils.initDownLoadParams(sn, savaPath));
            storeHttpRequest.setStoreHttpRequestListener(this);
            storeHttpRequest.post();
        }
    }

    /**
     * 通过得分，查询排名
     * SongID 歌曲ID
     * Score 当前演唱完后的得分
     */
    public void QueryScoreRanking(String SongID, int Score) {
        if (ServerConfigData.getInstance().getServerConfig() != null && ServerConfigData.getInstance().getServerConfig().getStore_web() != null && !TextUtils.isEmpty(ServerConfigData.getInstance().getServerConfig().getStore_web())) {
            StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.GET_GRADE_RECORD);
            storeHttpRequest.addParam("song_id", SongID);
            storeHttpRequest.addParam("score", Score + "");
            storeHttpRequest.setStoreHttpRequestListener(this);
            storeHttpRequest.setConvert2Class(SongScoreRanking.class);//加入本句的目的是为了收到数据后，通过gjson自动转换成相应的类对像
            storeHttpRequest.post();
        }
    }

    @Override
    public void onStoreStart(String method) {
        if (songFeedback != null) {
            songFeedback.onStart();
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        Logger.d(TAG, method + "=>onFailed :" + error);
        if (method.equals(RequestMethod.UPLOAD_SONG)) {
            if (uploadCount <= 3) {
                upLoadSongData(mUploadSongData);
                uploadCount++;
            } else {
                uploadCount = 0;
            }
        }
        if (songFeedback != null) {
            songFeedback.onFeedback(false, error, null);
        }
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        if (method.equals(RequestMethod.GET_GRADE_RECORD)) {
            SongScoreRanking SRank = (SongScoreRanking) object;
            SongScoreRanking.getInstance().setScorePercent(SRank.getPercentage());
            return;
        }
        if (songFeedback != null) {
            songFeedback.onFeedback(true, null, object);
        }
    }

    public interface SongFeedback {
        void onStart();

        void onFeedback(boolean suceed, String msg, Object obj);
    }
}
