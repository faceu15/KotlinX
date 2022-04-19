package com.yiwu.kotlinx.network;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.excellence.transfer.CommandService;
import com.permissionx.guolindev.PermissionX;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.ScopeProvider;
import com.yiwu.kotlinx.R;
import com.yiwu.kotlinx.base.BaseActivity;
import com.yiwu.kotlinx.databinding.ActivityRequestBinding;

import org.apache.http.client.AuthenticationHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RequestActivity extends BaseActivity<ActivityRequestBinding> implements View.OnClickListener {
    Observable<Integer> mClickStream;

    private WHandler mHandler = new WHandler(this);

    @Override
    protected int getContentViewId() {
        return R.layout.activity_request;
    }

    @Override
    protected void init() {
        mViewDataBinding.btnClick.setOnClickListener(this);
        mClickStream = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        });

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

    @Override
    public void onClick(View v) {
        mClickStream.buffer(mClickStream.throttleWithTimeout(250, TimeUnit.MICROSECONDS))
                .map(List::size)
                .as(AutoDispose.autoDisposable(ScopeProvider.UNBOUND))
                .subscribe(times -> {
                    Log.i("DDDD", "RequestActivity[100]->onClick: " + times + "击");
                });

    }

    private static class WHandler extends Handler {

        WeakReference<Activity> mActivity;

        public WHandler(Activity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    break;
            }
        }
    }
}
