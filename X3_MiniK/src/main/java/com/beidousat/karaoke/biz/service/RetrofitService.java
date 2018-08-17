package com.beidousat.karaoke.biz.service;

import com.beidousat.karaoke.biz.base.BaseEntity;
import com.beidousat.karaoke.model.KboxConfig;
import com.beidousat.karaoke.model.PayMent;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * For Retrofit
 * Created by jaycee on 2017/6/23.
 */
public interface RetrofitService {

//    @FormUrlEncoded
//    @POST("account/login")
//    Observable<BaseEntity<User>> login(
//            @Field("userId") String userId,
//            @Field("password") String password
//    );

    @GET("Pay/payment")
    Observable<BaseEntity<List<PayMent>>> getPayment();

    @GET("Index/getConfig")
    Observable<BaseEntity<KboxConfig>> getKboxConfig();
}
