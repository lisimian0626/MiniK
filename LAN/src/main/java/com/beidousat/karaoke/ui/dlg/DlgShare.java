package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.net.upload.RecordFileUploader;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.czt.mp3recorder.AudioRecordFileUtil;

/**
 * Created by J Wong on 2017/4/10.
 */

public class DlgShare extends Dialog implements View.OnClickListener, RecordFileUploader.RecordUploadListener {

    //    private ProgressBar mPgbProgress;
    private TextView mTvSongName, mTvSinger, mTvStatus;
    private RecyclerImageView mRivQrcode;

    private Song mSong;
    private RecordFileUploader.FileUploadInfo fileUploadInfo;

    public DlgShare(Context context, Song song) {
        super(context, R.style.MyDialog);
        mSong = song;
        initView();
        init();
    }

    private void initView() {
        this.setContentView(R.layout.dlg_share);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 600;
        lp.height = 440;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        findViewById(R.id.iv_close).setOnClickListener(this);
        mTvSongName = (TextView) findViewById(R.id.tv_title);
        mTvSinger = (TextView) findViewById(R.id.tv_singer);
        mRivQrcode = (RecyclerImageView) findViewById(R.id.iv_qrcode);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
    }

    private void init() {
        RecordFileUploader.getInstance().addRecordUploadListener(this);
        setRecordUploadStatus();
    }

    private String mRecordFilePath = "";

    private void setRecordUploadStatus() {
        mRecordFilePath = AudioRecordFileUtil.getRecordFile(mSong.RecordFile).getAbsolutePath();
        fileUploadInfo = RecordFileUploader.getInstance().getFileUploadInfo(mRecordFilePath);
        mTvSongName.setText(mSong.SimpName != null ? mSong.SimpName : "");
        mTvSinger.setText(mSong.SingerName != null ? mSong.SingerName : "");
        if (fileUploadInfo != null && fileUploadInfo.isSuccess && !TextUtils.isEmpty(fileUploadInfo.decPath)) {//已经上传
            mRivQrcode.setImageBitmap(QrCodeUtil.createQRCode(fileUploadInfo.decPath));
            mTvStatus.setText(R.string.wechat_scan_share);
        } else if (fileUploadInfo != null && TextUtils.isEmpty(fileUploadInfo.decPath)) {//正在上传
            mRivQrcode.setImageResource(R.drawable.ic_upload_success);
            mTvStatus.setText(R.string.upload_hard);
        } else {
            mRivQrcode.setImageResource(R.drawable.ic_upload_fail);
            mTvStatus.setText(R.string.upload_fail);
        }
    }

    @Override
    public void onUploadStart(String srcPath) {
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
            setRecordUploadStatus();
            Logger.d("DlgShare", "onUploadStart srcPath:" + srcPath);
        }
    }

    @Override
    public void onUploadFail(String srcPath, String errMsg) {
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
            setRecordUploadStatus();
            Logger.d("DlgShare", "onUploadFail srcPath:" + srcPath + " errMsg:" + errMsg);
        }
    }

    @Override
    public void onUploadCompletion(String srcPath, String desPath) {
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
            setRecordUploadStatus();
            Logger.d("DlgShare", "onUploadCompletion srcPath:" + srcPath + " desPath:" + desPath);
        }
    }

    @Override
    public void onUploading(String srcPath, float progress) {
        Logger.d("DlgShare", "onUploading srcPath:" + srcPath + " progress:" + progress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        RecordFileUploader.getInstance().removeRecordUploadListener(this);
        super.dismiss();
    }
}
