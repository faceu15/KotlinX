package com.yiwu.kotlinx.network;

import com.safframework.http.interceptor.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author:yiwu
 * @Date: Created in 22:21 2019/12/12
 * @Description:
 */
public class RetrofitManager {

    private static final String BASE_URL = "https://www.wanandroid.com";

    private static Retrofit mRetrofit;

    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            //设置拦截器
            LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
                    .loggable(true)
                    .request()
                    .requestTag("Request")
                    .response()
                    .responseTag("Response")
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .writeTimeout(30 * 1000, TimeUnit.MICROSECONDS)
                    .readTimeout(20 * 1000, TimeUnit.MICROSECONDS)
                    .connectTimeout(15 * 1000, TimeUnit.MICROSECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();

            mRetrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();

        }
        return mRetrofit;
    }


}
