package com.excellence.transfer.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.excellence.transfer.connect.AndroidSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Author:yi wu
 * @ Date: Created in 10:56 2021/9/3
 * @ Description:
 */
public class RemoteDebugManager {

    private static final String REMOTE_URL = "ws://192.168.33.70:8088/websocket/0";

    public static final String REMOTE_DEBUG_CONFIG = "remote_debug_config";
    public static final String DEBUG_STATE = "debug_state";
    public static final int DEBUG_STATE_CLOSE = 0;
    public static final int DEBUG_STATE_OPEN = 1;

    private static volatile  RemoteDebugManager mRemoteDebugManager;
    private  Context mContext;
    private AndroidSocketClient mSocketClient;


    private RemoteDebugManager() {

    }

    public static RemoteDebugManager getInstance() {
        if (mRemoteDebugManager == null) {
            synchronized (RemoteDebugManager.class) {
                if (mRemoteDebugManager == null) {
                    mRemoteDebugManager = new RemoteDebugManager();
                }
            }
        }
        return mRemoteDebugManager;
    }

    @SuppressLint("CheckResult")
    public void initialize(Context context) {
        mContext = context;
        Observable.just(0)
                .map(id -> {
                    if (getDebugState() == DEBUG_STATE_OPEN) {

                        return openRemoteDebug();
                    }
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public boolean openRemoteDebug() {
        try {
            mSocketClient = new AndroidSocketClient(mContext, new URI(REMOTE_URL));
            if (mSocketClient.isClosed()) {
                mSocketClient.connectBlocking();
            }
            return mSocketClient.connectBlocking();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeRemoteDebug() {
        if (mSocketClient.isOpen()) {
            try {
                mSocketClient.closeBlocking();
                Log.i("DDDD", "[83]->closeRemoteDebug: " + mSocketClient.isClosed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDebugState() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(REMOTE_DEBUG_CONFIG,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(DEBUG_STATE, DEBUG_STATE_CLOSE);
    }

    public void restoreRemoteDebugState(int state) {
        mContext.getSharedPreferences(REMOTE_DEBUG_CONFIG, Context.MODE_PRIVATE)
                .edit()
                .putInt(DEBUG_STATE, state)
                .apply();

    }

    public AndroidSocketClient getSocketClient() {
        return mSocketClient;
    }
}
