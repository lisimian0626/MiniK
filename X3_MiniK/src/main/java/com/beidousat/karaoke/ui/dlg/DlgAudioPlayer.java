package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.beidousat.karaoke.LanApp;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.player.AudioPlayer;
import com.beidousat.karaoke.player.KaraokeController;
import com.beidousat.libbns.util.DiskFileUtil;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libbns.util.TimeUtils;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.bumptech.glide.Glide;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DlgAudioPlayer extends Dialog implements OnClickListener, SeekBar.OnSeekBarChangeListener, HttpRequestListener, AudioPlayer.AudioPlayerListener {

    private final String TAG = DlgAudioPlayer.class.getSimpleName();

    private RecyclerImageView mIvAvatar;
    private TextView mTvName, mTvSinger, mTvCur, mTvDuration;
    private SeekBar mSeekBar;

    private Song mSong;
    private KaraokeController mKaraokeController;
    private boolean mTouching = false;
    private int mDuration;

    public DlgAudioPlayer(Context context, Song song) {
        super(context, R.style.MyDialog);
        mKaraokeController = ((LanApp) context.getApplicationContext()).mKaraokeController;
        mSong = song;
        init();

    }

    void init() {
        this.setContentView(R.layout.dlg_audio_player);
        setCanceledOnTouchOutside(false);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 400;
        lp.height = 188;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        mIvAvatar = (RecyclerImageView) findViewById(R.id.iv_singer);
        findViewById(R.id.iv_close).setOnClickListener(this);

        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvSinger = (TextView) findViewById(R.id.tv_singer);
        mTvCur = (TextView) findViewById(R.id.tv_cur);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);


        mTvName.setText(mSong.SimpName);
        mTvSinger.setText(mSong.SingerName);
        AudioPlayer.getInstance(getContext()).setAudioPlayerListener(this);

        requestSingerDetail(mSong.SingerID);

        play();
    }


    private void play() {
        mKaraokeController.pause();
        File fileRecord = AudioRecordFileUtil.getRecordFile(mSong.RecordFile);
        AudioPlayer.getInstance(getContext()).play(fileRecord.getAbsolutePath());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        AudioPlayer.getInstance(getContext()).close();
        mKaraokeController.play();
        super.dismiss();
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int seek = (int) (seekBar.getProgress() * mDuration / 100);
        AudioPlayer.getInstance(getContext()).seekTo(seek);
        mTouching = false;
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mTouching = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }


    private void requestSingerDetail(String id) {
        HttpRequest request = initRequest(RequestMethod.GET_SINGER_DETAIL);
        request.addParam("SingerID", id);
        request.setConvert2Class(StarInfo.class);
        request.doPost(0);
    }

    public HttpRequest initRequest(String method) {
        HttpRequest request = new HttpRequest(getContext().getApplicationContext(), method);
        request.setHttpRequestListener(this);
        return request;
    }

    @Override
    public void onStart(String method) {

    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_SINGER_DETAIL.equals(method)) {
            StarInfo starInfo = (StarInfo) object;
            if (starInfo != null) {
                mTvSinger.setText(starInfo.SimpName);
                Glide.with(getContext()).load(DiskFileUtil.getSingerThumbnailImg(starInfo.Img))
                        .bitmapTransform(new CropCircleTransformation(getContext())).error(R.drawable.ic_avatar).skipMemoryCache(true).into(mIvAvatar);
            }
        }
    }

    @Override
    public void onFailed(String method, String error) {

    }

    @Override
    public void onPlayCompletion() {
        dismiss();
    }


    @Override
    public void onPlayProgress(final int progress, final int duration) {
        mDuration = duration;
        mTvDuration.post(new Runnable() {
            @Override
            public void run() {
                mTvCur.setText(TimeUtils.convertLongString(progress));
                mTvDuration.setText(TimeUtils.convertLongString(duration));
                if (!mTouching) {
                    mSeekBar.setProgress(progress * 100 / duration);
                }
            }
        });
    }
}
