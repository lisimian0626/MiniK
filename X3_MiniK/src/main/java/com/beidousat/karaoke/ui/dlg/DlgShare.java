package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.widget.ProgressWebView;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.Song;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.upload.RecordFileUploader2;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by J Wong on 2017/4/10.
 */

public class DlgShare extends BaseDialog implements View.OnClickListener, RecordFileUploader2.RecordUploadListener {

    //    private ProgressBar mPgbProgress;
    private TextView mTvSongName, mTvSinger, mTvStatus;
    private RecyclerImageView mRivQrcode;
    private Button btnRetry;
    ProgressWebView mWebView;

    private Map<String, String> Headers;

    private Song mSong;
    private RecordFileUploader2.FileUploadInfo fileUploadInfo;

    public DlgShare(Context context, Song song) {
        super(context, R.style.MyDialog);
        initView();
        setSong(song);
    }

    public void setSong(Song song) {
        mSong = song;
        init();
    }

    private void initView() {
        this.setContentView(R.layout.dlg_share);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        mWebView  = (ProgressWebView) findViewById(R.id.share_webview);
        lp.width = 600;
        lp.height = 460;
        if(Common.isEn) {
            lp.width = 960;
            lp.height = 600;
            mWebView.setVisibility(View.INVISIBLE);
        }else{
            mWebView.setVisibility(View.GONE);
        }
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        btnRetry = (Button) findViewById(android.R.id.button1);
        btnRetry.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mTvSongName = (TextView) findViewById(R.id.tv_title);
        mTvSinger = (TextView) findViewById(R.id.tv_singer);
        mRivQrcode = (RecyclerImageView) findViewById(R.id.iv_qrcode);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
    }

    private void init() {
        if(ServerConfigData.getInstance().getServerConfig()!=null&&ServerConfigData.getInstance().getServerConfig().getStore_web()!=null){
            String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.RECORD_UPLOAD;
            RecordFileUploader2.getInstance(url).addRecordUploadListener(this);
            setRecordUploadStatus();
        }
    }

    private String mRecordFilePath = "";

    private void setRecordUploadStatus() {
        mRecordFilePath = AudioRecordFileUtil.getRecordFile(mSong.RecordFile).getAbsolutePath();
        String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.RECORD_UPLOAD;

        fileUploadInfo = RecordFileUploader2.getInstance(url).getFileUploadInfo(mRecordFilePath);

        mTvSongName.setText(mSong.SimpName != null ? mSong.SimpName : "");
        mTvSinger.setText(mSong.SingerName != null ? mSong.SingerName : "");
        if (fileUploadInfo != null && fileUploadInfo.isSuccess && !TextUtils.isEmpty(fileUploadInfo.decPath)) {//已经上传
            if(Common.isEn){
                mTvSongName.setVisibility(View.GONE);
                mTvSinger.setVisibility(View.GONE);
                mTvStatus.setVisibility(View.GONE);
                mRivQrcode.setVisibility(View.GONE);
                btnRetry.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                //打开网页
//                Log.d("setRecordUploadStatus", fileUploadInfo.decPath);
//                String mUrl = "http://192.168.0.4:8089/";
                Headers = new HashMap<String, String>();
                WebSettings settings = mWebView.getSettings();
                settings.setJavaScriptEnabled(true);//开启支持javascript
                settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                settings.setUserAgentString("minkbox");
                settings.setAllowFileAccess(true);//支持文件流
                settings.setSupportZoom(false);//不支持缩放
                settings.setBuiltInZoomControls(false);//不支持缩放
                settings.setUseWideViewPort(false);// 调整到适合webview大小
                settings.setLoadWithOverviewMode(false);//  调整到适合webview大小
                settings.setBlockNetworkImage(true);////提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
                //settings.setAppCacheEnabled(true);//开启缓存机制
                settings.setDomStorageEnabled(true);//开启DOM
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub
                        //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                        view.loadUrl(url,Headers);
                        return true;
                    }
                });
                mWebView.loadUrl(fileUploadInfo.decPath,Headers);
            }else{
                mRivQrcode.setImageBitmap(QrCodeUtil.createQRCode(fileUploadInfo.decPath));
                mTvStatus.setText(R.string.wechat_scan_share);
            }
        } else if (fileUploadInfo != null && TextUtils.isEmpty(fileUploadInfo.decPath) && fileUploadInfo.isUploading) {//正在上传
            mRivQrcode.setImageResource(R.drawable.ic_upload_success);
            mTvStatus.setText(R.string.upload_hard);
        } else {
            upload(mSong);
        }
    }

    @Override
    public void onUploadStart(String srcPath) {
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
            btnRetry.setVisibility(View.INVISIBLE);

            setRecordUploadStatus();
            Logger.d("DlgShare", "onUploadStart srcPath:" + srcPath);
            mRivQrcode.setImageResource(R.drawable.ic_upload_success);
            mTvStatus.setText(R.string.upload_hard);
        }
    }

    @Override
    public void onUploadFail(String srcPath, String errMsg) {
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
//            setRecordUploadStatus();
            Logger.d("DlgShare", "onUploadFail srcPath:" + srcPath + " errMsg:" + errMsg);
            mRivQrcode.setImageResource(R.drawable.ic_upload_fail);
            mTvStatus.setText("上传失败!");
            btnRetry.setVisibility(View.VISIBLE);
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
        if (!TextUtils.isEmpty(mRecordFilePath) && mRecordFilePath.equals(srcPath)) {
            mTvStatus.setText(getContext().getString(R.string.uploading) + "" + (int) (100 * progress) + "%");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case android.R.id.button1:
                upload(mSong);
                break;
        }
    }

    @Override
    public void dismiss() {
        String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.RECORD_UPLOAD;
        RecordFileUploader2.getInstance(url).removeRecordUploadListener(this);
        super.dismiss();
    }

    private void upload(Song songInfo) {  //upload record
        btnRetry.setVisibility(View.INVISIBLE);
        try {
            Meal meal = BoughtMeal.getInstance().getTheFirstMeal();
            String orderSn = "";
            if (meal != null) {
                orderSn = meal.getOrderSn();
            }
            String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.RECORD_UPLOAD;
            RecordFileUploader2.getInstance(url).uploadRecord(AudioRecordFileUtil.getRecordFile(songInfo.RecordFile).getAbsolutePath(),
                    orderSn, songInfo.ID, songInfo.SimpName, songInfo.SingerName, songInfo.score, PrefData.getRoomCode(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
