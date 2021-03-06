package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.BeidouPlayerListener;
import com.beidousat.karaoke.player.BnsPlayer;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.player.chenxin.BNSPlayer;
import com.beidousat.karaoke.player.chenxin.OriginPlayer;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;

public class DlgPreview extends BaseDialog implements OnClickListener, BeidouPlayerListener {

    private final String TAG = DlgPreview.class.getSimpleName();
    private BnsPlayer mMediaPlayer;
    private BNSPlayer mMediaPlayer_cx;
    private SurfaceView mSurfaceView;
    private ImageView mIvClose;
    private TextView mTvName, mTvTime;
    private long prepareBegin;
    private Song mSong;
    private String mFileUrl;
    private Thread mThreadPlayer = null;

    public DlgPreview(Context context, Song song) {
        super(context, R.style.MyDialog);
        mSong = song;
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_preview);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getInteger(R.integer.preview_w);
        lp.height = getContext().getResources().getInteger(R.integer.preview_h);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        mTvTime = (TextView) findViewById(R.id.tv_timer);
        mTvTime.setVisibility(View.INVISIBLE);
        mSurfaceView = (SurfaceView) findViewById(R.id.surf_preview);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(this);
        findViewById(R.id.iv_top).setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);


        mTvName.setText(mSong.SimpName);


        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, 500);
    }


    private void play() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (BnsConfig.is901()) {
                    try {
                        mMediaPlayer = new BnsPlayer(mSurfaceView, null, 0, 0);
                        String filePath = TextUtils.isEmpty(mSong.download_url) ? mSong.SongFilePath : mSong.download_url;
                        mFileUrl = ServerFileUtil.getPreviewUrl(filePath);
                        Logger.i(TAG, "play url:" + mFileUrl);
                        prepareBegin = System.currentTimeMillis();
                        mMediaPlayer.setBeidouPlayerListener(DlgPreview.this);
                        mMediaPlayer.playUrl(mFileUrl,mSong.SongFilePath, null, BnsConfig.PREVIEW);
                    } catch (Exception e) {
                        Logger.e(TAG, "Exception:" + e.toString());
                    }
                } else {
                    mMediaPlayer_cx = new BNSPlayer(mSurfaceView, null);
                    String filePath = TextUtils.isEmpty(mSong.download_url) ? mSong.SongFilePath : mSong.download_url;
                    mFileUrl = ServerFileUtil.getPreviewUrl(filePath);
                    Logger.i(TAG, "play url:" + mFileUrl);
                    prepareBegin = System.currentTimeMillis();
                    mThreadPlayer = new Thread(new Runnable() {
                        public void run() {
                            mMediaPlayer_cx.open(mFileUrl, mSong.SongFilePath,mSong.SongFilePath,BnsConfig.PREVIEW, DlgPreview.this);
                            mThreadPlayer = null;
                        }
                    });
                    mThreadPlayer.start();
                }

            }
        };
        mSurfaceView.post(runnable);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                close();
                break;
            case R.id.iv_add:
                if (mSong != null)
                    ChooseSongs.getInstance(getContext().getApplicationContext()).addSong(mSong);
                break;
            case R.id.iv_top:
                if (mSong != null)
                    ChooseSongs.getInstance(getContext().getApplicationContext()).add2Top(mSong);
                break;
        }
    }

    @Override
    public void dismiss() {
        if (mMediaPlayer != null) {
            Logger.e(TAG, "mMediaPlayer.release");
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }else if(mMediaPlayer_cx!=null){
            mMediaPlayer_cx.close();
            mMediaPlayer_cx = null;
        }
        super.dismiss();
    }

    private void close() {
        if(this.isShowing()){
            this.dismiss();
        }
    }

    @Override
    public void onPlayerCompletion() {
        close();
    }

    @Override
    public void onPlayerPrepared() {
        Logger.i(TAG, "onPrepared time" + (System.currentTimeMillis() - prepareBegin));
        findViewById(R.id.ll_loading).setVisibility(View.GONE);
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(0.0F);
    }

    @Override
    public void onPlayerProgress(long progress, long duration) {

    }
}
