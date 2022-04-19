package com.excellence.transfer;

import static com.excellence.transfer.utils.Constant.APK_NAME;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.excellence.transfer.utils.PSilentInstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CommandService extends Service {
    private static final String TAG = "CommandService";

    public static final String CODE = "code";
    public static final int UPDATE = 1;
    public static final int SHELL_CMD = 4;

    public static final int START_LOG = 2;
    public static final int STOP_LOG = 3;

    private final ExecutorService mThreadPoolExecutor = new ThreadPoolExecutor(3,
            4, 1000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(2));

    public CommandService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int code = intent.getIntExtra(CODE, -1);
        Log.i("DDDD", "CommandService[59]->onStartCommand: " + code);
        switch (code) {
            case UPDATE:
                updateApk();
                break;

            case SHELL_CMD:
                executeShellCmd();
                break;

            case START_LOG:
                startCatchLog();
                break;

            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void executeShellCmd() {


    }

    private void startCatchLog() {
        mThreadPoolExecutor.submit(() -> {
            try {
                String path = getFilesDir().getAbsolutePath();
                Log.i("DDDD", "[77]->startCatchLog: " + path);
                File file = new File(path, "logcat.txt");
                if (!file.exists()) {
                    Log.i("DDDD", "[78]->创建");
                    boolean newFile = file.createNewFile();
                    if (newFile) {
                        Runtime.getRuntime().exec(new String[]{"logcat -f " + file.getAbsolutePath()});
                    }
                } else {
                    Process process = Runtime.getRuntime().exec(new String[]{"logcat"});
                    BufferedReader buffReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    FileOutputStream fos = new FileOutputStream(file, false);

                    String str;
                    while ((str = buffReader.readLine()) != null) {
                        fos.write(str.getBytes());
                        Log.i("DDDD", "[97]->输出log: " + str);
                    }
                    fos.close();
                    buffReader.close();
                }
                Log.i("DDDD", "[85]->startCatchLog: " + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateApk() {
        String path = getFilesDir().getAbsolutePath() + "/" + APK_NAME;
        File file = new File(path);
        Log.d(TAG, "updateApk: " + path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(this, "com.excellence.transfer.fileprovider", file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void updateSilence() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/app-debug.apk";
        Log.i("DDDD", "[118]->update: " + path);

        PackageInstaller packageInstaller = this.getPackageManager()
                .getPackageInstaller();

        packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
            @Override
            public void onCreated(int sessionId) {
                Log.i("DDDD", "[123]->onCreated: " + sessionId);
            }

            @Override
            public void onBadgingChanged(int sessionId) {
                Log.i("DDDD", "[128]->onBadgingChanged: " + sessionId);
            }

            @Override
            public void onActiveChanged(int sessionId, boolean active) {
                Log.i("DDDD", "[133]->onActiveChanged: " + active);
            }

            @Override
            public void onProgressChanged(int sessionId, float progress) {
                Log.i("DDDD", "[138]->onProgressChanged: " + progress);
            }

            @Override
            public void onFinished(int sessionId, boolean success) {
                Log.i("DDDD", "[143]->onFinished: " + success);
            }
        });

        Observable.just(path)
                .map(s -> PSilentInstaller.installApk(this, path, packageInstaller))
                .subscribeOn(Schedulers.io())
                .subscribe();

    }
}