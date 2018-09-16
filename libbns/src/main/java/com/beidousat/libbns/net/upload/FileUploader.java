package com.beidousat.libbns.net.upload;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.beidousat.libbns.model.BaseUpload;
import com.beidousat.libbns.util.Logger;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by J Wong on 2016/6/22.
 */
public class FileUploader {

    private final static String TAG = "FileUploader";


    public static OkHttpClient client;

    static {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();
    }

//    private static final OkHttpClient client = new OkHttpClient.Builder()
//            //设置超时，不设置可能会报异常
//            .connectTimeout(1, TimeUnit.MINUTES)
//            .readTimeout(1, TimeUnit.MINUTES)
//            .writeTimeout(1, TimeUnit.MINUTES)
//            .build();

    private FileUploadListener mFileUploadListener;
    private File mUploadFile;
    private Call mCurrentCall;

    public FileUploader() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2://success
                    Bundle bundle = msg.getData();
                    String fileUrl = bundle.getString("fileUrl");
                    if (mFileUploadListener != null) {
                        mFileUploadListener.onUploadCompletion(mUploadFile, fileUrl);
                    }
                    break;

                case 3://fail
                    bundle = msg.getData();
                    String err = bundle.getString("err");
                    if (mFileUploadListener != null) {
                        mFileUploadListener.onUploadFailure(mUploadFile, err);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void sendFail(String err) {
        Message message = new Message();
        message.what = 3;
        Bundle bundle = new Bundle();
        bundle.putString("err", err);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void sendSuccess(String fileUrl) {
        Message message = new Message();
        message.what = 2;
        Bundle bundle = new Bundle();
        bundle.putString("fileUrl", fileUrl);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    public void setFileUploadListener(FileUploadListener listener) {
        mFileUploadListener = listener;
    }

//    public void  cancel(){
//        client.dispatcher().cancelAll();
//    }

    /**
     * @param path 本地文件
     * @param url  上传文件地址
     * @param type 1头像 2 多媒体 3自制MV图片
     */
    public void upload(String path, String url, int type) throws Exception {
        if (TextUtils.isEmpty(path))
            throw new NullPointerException("path is empty!");

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("can not find file");
        }

        upload(file, url, type);
    }

    /**
     * @param file 本地文件
     * @param url  上传文件地址
     * @param type 1头像 2 多媒体 3自制MV图片
     */
    public void upload(File file, String url, int type) {
        mUploadFile = file;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("Type", String.valueOf(type))
                .addFormDataPart("FileName", mUploadFile.getName(), RequestBody.create(null, mUploadFile))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), mUploadFile))
                .build();

        Logger.d(TAG, "upload file path:" + file.getAbsolutePath());

        uploadFile(requestBody, url);
    }

    public void cancel() {
        mCurrentCall.cancel();
    }

    public void uploadFile(RequestBody requestBody, String url) {
        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
                Logger.d(TAG, "onUIProgress bytesWrite:" + bytesWrite + "  contentLength:" + contentLength + "  done:" + done);
                //ui层回调
                if (mFileUploadListener != null)
                    mFileUploadListener.onUploading(mUploadFile, (float) bytesWrite / contentLength);
            }

            @Override
            public void onUIStart(long bytesWrite, long contentLength, boolean done) {
                super.onUIStart(bytesWrite, contentLength, done);
                Logger.d(TAG, "onUIStart bytesWrite:" + bytesWrite + "  contentLength:" + contentLength + "  done:" + done);
                if (mFileUploadListener != null)
                    mFileUploadListener.onUploadStart(mUploadFile);
            }

            @Override
            public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
                super.onUIFinish(bytesWrite, contentLength, done);
                Logger.d(TAG, "onUIFinish bytesWrite:" + bytesWrite + "  contentLength:" + contentLength + "  done:" + done);
            }
        };


        //进行包装，使其支持进度回调
        final Request request = new Request.Builder().url(url).post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
        //开始请求
        mCurrentCall = client.newCall(request);
        mCurrentCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "onFailure ex:" + e.toString());
                sendFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String json = response.body().string();
                    Logger.d(TAG, "onResponse response:" + json);
                    if (response != null) {
//                        if (mConvert2Object != null) {
//                            Gson gson = new Gson();
//                          BaseUpload baseUpload = gson.fromJson(json, mConvert2Object);
//
//                        } else {
                        Gson gson = new Gson();
                        BaseUpload baseModel = gson.fromJson(json, BaseUpload.class);
                        if ("0".equalsIgnoreCase(baseModel.error)) {
                            String fileUrl = baseModel.data.getAsJsonObject().get("share_code").getAsString();
                            Logger.d(TAG, "onResponse fileUrl:" + fileUrl);
                            sendSuccess(fileUrl);
                            return;
                        } else {
                            sendFail(baseModel.error + ":" + baseModel.message);
                            return;
                        }
//                        }
                    }
                } catch (Exception e) {
                    Logger.w(TAG, "onResponse ex:" + e.toString());
                }
                sendFail(response.toString());
            }
        });
    }

//    public void setConvert2Class(Class<?> clazz) {
//        this.mConvert2Object = clazz;
//    }


    /**
     * /**
     *
     * @param file   本地文件
     * @param url    上传文件地址
     * @param type   1头像 2 多媒体 3自制MV图片
     * @param rename 指定生成文件名
     */
    public void upload(File file, String url, int type, String rename) {
        mUploadFile = file;
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("Type", String.valueOf(type))
                .addFormDataPart("FileName", mUploadFile.getName(), RequestBody.create(null, mUploadFile))
                .addFormDataPart("SaveName", rename)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), mUploadFile))
                .build();
        uploadFile(requestBody, url);
    }

    /**
     * /**
     *
     * @param file  本地文件
     * @param url   上传文件地址
     * @param parts 参数
     */
    public void upload(File file, String url, Map<String, String> parts) {
        mUploadFile = file;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (parts != null && parts.size() > 0) {
            Iterator iterator = parts.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                String val = entry.getValue() == null ? "" : entry.getValue().toString();
                builder.addFormDataPart(key, val);
            }
        }
        builder.addFormDataPart("FileName", mUploadFile.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), mUploadFile));
//        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), mUploadFile));
        MultipartBody requestBody = builder.build();
        uploadFile(requestBody, url);
    }

    /**
     * /**
     *
     * @param path  本地文件路径
     * @param url   上传文件地址
     * @param parts 参数
     */
    public void upload(String path, String url, Map<String, String> parts) {
        mUploadFile = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (parts != null && parts.size() > 0) {
            Iterator iterator = parts.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                String val = entry.getValue() == null ? "" : entry.getValue().toString();
                builder.addFormDataPart(key, val);
            }
        }
        builder.addFormDataPart("FileName", mUploadFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), mUploadFile));
//        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), mUploadFile));
        MultipartBody requestBody = builder.build();
        uploadFile(requestBody, url);
    }
}
