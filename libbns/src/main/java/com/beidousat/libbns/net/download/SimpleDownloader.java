package com.beidousat.libbns.net.download;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.util.BnsConfig;
import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by J Wong on 2015/12/7 18:41.
 */
public class SimpleDownloader {

    private final static String TAG = SimpleDownloader.class.getSimpleName();
    public static long intervalTime;
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

    public void download(final File desFile, final String url, final SimpleDownloadListener listener) {
        if (desFile == null || TextUtils.isEmpty(url)) {
            return;
        }
        Logger.d(TAG, "download url:" + url);

        String fileUrl = url;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            fileUrl = ServerConfigData.getInstance().getServerConfig().getVod_server() + url;
        }
        Logger.d(TAG, "download fileUrl:" + fileUrl);
        new AsyncDownloader(desFile, fileUrl, listener).execute();
    }

    private long mTotalSize = 0L;
    public void downloadFile(final File desFile,String url){
        final long startTime = System.currentTimeMillis();
        Logger.d("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Logger.d("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    sink = Okio.sink(desFile);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Logger.d("DOWNLOAD","download success");
                    Logger.d("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });
    }


    private class AsyncDownloader extends AsyncTask<Void, Long, Boolean> {

        private String mUrl;
        private File mDesFile;
        private SimpleDownloadListener mSimpleDownloadListener;

        public AsyncDownloader(File desFile, String url, SimpleDownloadListener listener) {
            mUrl = url;
            mDesFile = desFile;
            mSimpleDownloadListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Logger.d(TAG, "download url doInBackground:" + mUrl);
            Call call = client.newCall(new Request.Builder().url(mUrl).get().build());
            try {
                Response response = call.execute();
                if (response.code() == 200) {
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        String fileName = mDesFile.getName();
                        File fileTemp = new File(mDesFile.getParentFile(), fileName + ".temp");
                        Logger.d(TAG, "fileName :" + fileName + "  fileTemp:" + fileTemp.getAbsolutePath());
                        fileOutputStream = new FileOutputStream(fileTemp);
                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();
                        mTotalSize = target;
                        publishProgress(0L, target);
                        long startTime = System.currentTimeMillis();
                        while (true) {
                            int readed = inputStream.read(buff);
                            if (readed == -1) {

                                break;
                            }

                            //write buff
                            fileOutputStream.write(buff, 0, readed);
                            downloaded += readed;
                            publishProgress(downloaded, target);
                            if (isCancelled()) {
                                return false;
                            }
                        }
                        boolean ret = false;
                        intervalTime=System.currentTimeMillis()-startTime;
                        Logger.d(TAG, " downloaded intervalTime:" + intervalTime);
                        Logger.d(TAG, " downloaded speed:" + ((  downloaded / intervalTime ) * 1000)/1024+"kb/s");
                        if (downloaded == target) {
                            ret = fileTemp.renameTo(mDesFile);
                            Logger.d(TAG, " downloaded == target:" + (downloaded == target) + "  ret:" + ret);

                        }

                        Logger.d(TAG, " downloaded == target:" + (downloaded == target) + "  ret:" + ret);
                        return ret;
                    } catch (IOException ignore) {
                        return false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                Logger.e(TAG, "IOException :" + e.toString());
                return false;
            } catch (Exception e) {
                Logger.e(TAG, "Exception :" + e.toString());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            if (mSimpleDownloadListener != null)
                mSimpleDownloadListener.onUpdateProgress(mDesFile, values[0], values[1]);
            Logger.d(TAG, "download progress:" + String.format("%d / %d", values[0], values[1]));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                Logger.d(TAG, "File :" + mDesFile.getAbsolutePath() + " exist :" + mDesFile.exists() + "  result:" + result);
                if (result) {
                    mSimpleDownloadListener.onDownloadCompletion(mDesFile, mUrl, mTotalSize);
                } else {
                    mSimpleDownloadListener.onDownloadFail(mUrl);
                }
            } catch (Exception e) {
                Logger.e(TAG, "onPostExecute Exception :" + e.toString());
            }
        }
    }
}
