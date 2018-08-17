package com.beidousat.karaoke.biz;

import android.content.Context;

import com.beidousat.karaoke.data.PrefData;
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

    public static SongHelper getInstance(Context context, SongFeedback cb) {
        if (mSongHelper == null) {
            mSongHelper = new SongHelper(context,cb);
        }
        return mSongHelper;
    }

    public SongHelper(Context context, SongFeedback cb) {
        this.mContext = context;
        this.songFeedback = cb;
    }

    public void upLoad(String songID,String orderSn,long playtime,long finishtime,int songlenght,int score){
        StoreHttpRequest storeHttpRequest = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.UPLOAD_SONG);
        storeHttpRequest.addParam(HttpParamsUtils.initUploadSongParams(PrefData.getRoomCode(mContext),songID,orderSn,playtime,finishtime,songlenght,score));
        storeHttpRequest.setStoreHttpRequestListener(this);
        storeHttpRequest.post();
    }
    @Override
    public void onStoreStart(String method) {
        if (songFeedback != null) {
            songFeedback.onStart();
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        Logger.d(TAG, "onFailed :" + error);
        if (songFeedback != null) {
            songFeedback.onFeedback(false, error,null);
        }
    }

    @Override
    public void onStoreSuccess(String method, Object object) {
        Logger.d(TAG, "onSuccess :" + object);

        if (songFeedback != null) {
            songFeedback.onFeedback(true, null,object);
        }
    }

    public interface SongFeedback {
        void onStart();

        void onFeedback(boolean suceed, String msg, Object obj);
    }
}
