package com.yiwu.kotlinx.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.yiwu.kotlinx.binder.BinderPool;

/**
 * @Author:yiwu
 * @Date: Created in 10:42 2020/3/31
 * @Description:
 */
public class BinderPoolService extends Service {

    private Binder mBinderPool = new BinderPool.BinderPoolImp();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
