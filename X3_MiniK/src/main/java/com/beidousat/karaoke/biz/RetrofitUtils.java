package com.beidousat.karaoke.biz;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/4/16.
 */

public class RetrofitUtils {
    public final String Tab_Http="okhttp";
    public static final int DEFAULT_TIMEOUT = 5;
    private Retrofit mRetrofit;
    private static RetrofitUtils mInstance;
    private RetrofitUtils(String app_host){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(Tab_Http,message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BASIC));
// 添加通用的Header
//            .addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request.Builder builder = chain.request().newBuilder();
//                builder.addHeader("token", "123");
//                return chain.proceed(builder.build());
//            }
//        });

        mRetrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(app_host)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
    public static RetrofitUtils getInstance(String app_host){
        if (mInstance == null){
            synchronized (RetrofitUtils.class){
                mInstance = new RetrofitUtils(app_host);
            }
        }
        return mInstance;
    }
}
