package com.yiwu.kotlinx.util;

/**
 *    author : doris
 *    date   : 2020/4/28 
 *    desc   :
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;
import android.widget.Toast;

import com.yiwu.kotlinx.application.KotlinxApplication;

public class InstallResultReceiver extends BroadcastReceiver {
    private static final String TAG = InstallResultReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            Log.i("DDDD", "[25]->onReceive: "+status);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                Log.i(TAG, "InstallResultReceiver[23]->onReceive: success");
                // success
            } else if(status == PackageInstaller.STATUS_PENDING_USER_ACTION){
                Intent confirmIntent = (Intent) intent.getExtras().get(Intent.EXTRA_INTENT);
                confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KotlinxApplication.getInstance().startActivity(confirmIntent);
            }else {
                Log.e(TAG, "install error "+intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE));
            }
        }
    }
}