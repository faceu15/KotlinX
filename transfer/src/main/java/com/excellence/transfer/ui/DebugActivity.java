package com.excellence.transfer.ui;

import static com.excellence.transfer.utils.RemoteDebugManager.DEBUG_STATE_CLOSE;
import static com.excellence.transfer.utils.RemoteDebugManager.DEBUG_STATE_OPEN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

import com.excellence.transfer.R;
import com.excellence.transfer.base.BaseActivity;
import com.excellence.transfer.databinding.ActivityDebugBinding;
import com.excellence.transfer.utils.RemoteDebugManager;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DebugActivity extends BaseActivity<ActivityDebugBinding> {

    private int mRemoteDebugState;
    private boolean mCatchingLog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_debug;
    }

    @Override
    protected void init() {
        RemoteDebugManager mRemoteDebugManager = RemoteDebugManager.getInstance();
        Observable.just(0)
                .map(integer -> {
                    mRemoteDebugState = mRemoteDebugManager.getDebugState();
                    Log.i("DDDD", "[44]->初始化state：" + mRemoteDebugState);
                    return mRemoteDebugState;
                })
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::modifyButtonState);

        mViewDataBinding.btnConnect.setOnClickListener(view -> {
            Flowable.just(0)
                    .map(state -> {
                        if (mRemoteDebugState == DEBUG_STATE_OPEN) {
                            mRemoteDebugManager.closeRemoteDebug();
                            mRemoteDebugState = DEBUG_STATE_CLOSE;
                        } else {
                            boolean openRemoteDebug = mRemoteDebugManager.openRemoteDebug();
                            Log.i("DDDD", "DebugActivity[53]->init连接: " + openRemoteDebug);
                            if (openRemoteDebug) {
                                mRemoteDebugState = DEBUG_STATE_OPEN;
                            } else {
                                Log.e("Error", "connect server failed!");
                                Toast.makeText(this, "connect server failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                        mRemoteDebugManager.restoreRemoteDebugState(mRemoteDebugState);
                        return mRemoteDebugState;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(this::modifyButtonState, throwable -> Log.e("DebugActivity[75]", "Error " + throwable.toString()));

        });


        int grant = checkSelfPermission(Manifest.permission.READ_LOGS);
        Log.i("DDDD", "DebugActivity[83]->权限: " + grant);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_LOGS}, 1);
            Log.i("DDDD", "DebugActivity[86]->init: 申请");
        }


        mViewDataBinding.btnSendCmd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                mCatchingLog = !mCatchingLog;
                Flowable.just(1)
                        .map(integer -> {
                            Process process = Runtime.getRuntime().exec(new String[]{"logcat -b system"});
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            Log.i("DDDD", "AndroidSocketClient[135]->catchLogcat: ");
                            while (mCatchingLog) {
                                Log.i("DDDD", "日志： " + bufferedReader.readLine());
                            }
                            return true;
                        }).subscribeOn(Schedulers.io())
                        .subscribe(aBoolean -> {

                        }, throwable -> Log.e("Error", ": " + throwable.toString()));

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("DDDD", "DebugActivity[121]->onRequestPermissionsResult: " + requestCode + " permissions :" + permissions.toString());
        Log.i("DDDD", "DebugActivity[122]->onRequestPermissionsResult: " + grantResults.toString());
    }

    private void modifyButtonState(int state) {
        if (state == DEBUG_STATE_OPEN) {
            mViewDataBinding.btnConnect.setText(R.string.btn_close);
        } else {
            mViewDataBinding.btnConnect.setText(R.string.btn_open);
        }
    }

}