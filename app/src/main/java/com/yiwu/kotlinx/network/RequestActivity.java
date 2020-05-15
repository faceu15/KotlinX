package com.yiwu.kotlinx.network;

import com.yiwu.kotlinx.R;
import com.yiwu.kotlinx.base.BaseActivity;
import com.yiwu.kotlinx.databinding.ActivityRequestBinding;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RequestActivity extends BaseActivity<ActivityRequestBinding> {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_request;
    }

    @Override
    protected void init() {

    }

    public void okHttpRequest(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .callTimeout(1000, TimeUnit.MICROSECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("Get", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                    }
                })
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }

    public void retrofitRequest(){

    }
}
