package com.yiwu.kotlinx.network;

import com.safframework.http.interceptor.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @Author:yiwu
 * @Date: Created in 22:21 2019/12/12
 * @Description:
 */
public class RetrofitManager {

    private static Retrofit mRetrofit;

    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.writeTimeout(30 * 1000, TimeUnit.MICROSECONDS);
            builder.readTimeout(20 * 1000, TimeUnit.MICROSECONDS);
            builder.connectTimeout(15 * 1000, TimeUnit.MICROSECONDS);

            LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
                    .loggable(true)
                    .request()
                    .requestTag("Request")
                    .response()
                    .responseTag("Response")
                    .build();
            //设置拦截器
            builder.addInterceptor(loggingInterceptor);

        }
        return mRetrofit;
    }


}
