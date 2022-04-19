package com.excellence.transfer.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * author : doris
 * date   : 2020/4/28
 * desc   : Android 9.0 安装方法 是否适用Android 9.0+ 待验证
 */
public class PSilentInstaller {
    private static final String TAG = PSilentInstaller.class.getSimpleName();

    private static final String PACKAGE_INSTALLED_ACTION =
            "com.example.android.apis.content.SESSION_API_PACKAGE_INSTALLED";


    public static int installApk(Context context, String apkFilePath, PackageInstaller packageInstaller) {
        Log.i("DDDD", "[29]->installApk: " + Thread.currentThread().getName());
        File apkFile = new File(apkFilePath);
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(apkFile.length());

        int sessionId = createSession(packageInstaller, sessionParams);
        Log.i("DDDD", "[34]->installApk: " + sessionId);
        if (sessionId != -1) {
            boolean copySuccess = copyInstallFile(packageInstaller, sessionId, apkFilePath);
            if (copySuccess) {
                Log.i("DDDD", "[37]->installApk: copy Success");
                execInstallCommand(context, packageInstaller, sessionId);
            }
        }
        return sessionId;
    }

    public static void installApk(Context context, String apkFilePath, PackageManager packageManager) {
        File apkFile = new File(apkFilePath);
        PackageInstaller packageInstaller = packageManager.getPackageInstaller();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(apkFile.length());

        int sessionId = createSession(packageInstaller, sessionParams);
        if (sessionId != -1) {
            boolean copySuccess = copyInstallFile(packageInstaller, sessionId, apkFilePath);
            if (copySuccess) {
                execInstallCommand(context, packageInstaller, sessionId);
            }
        }
    }

    private static int createSession(PackageInstaller packageInstaller,
                                     PackageInstaller.SessionParams sessionParams) {
        int sessionId = -1;
        try {
            sessionId = packageInstaller.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    private static boolean copyInstallFile(PackageInstaller packageInstaller,
                                           int sessionId, String apkFilePath) {
        InputStream in = null;
        OutputStream out = null;
        PackageInstaller.Session session = null;
        boolean success = false;
        try {
            File apkFile = new File(apkFilePath);
            session = packageInstaller.openSession(sessionId);
            out = session.openWrite("base.apk", 0, apkFile.length());
            in = new FileInputStream(apkFile);
            int total = 0, c;
            byte[] buffer = new byte[65536];
            while ((c = in.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            Log.i(TAG, "streamed " + total + " bytes");
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(out);
            closeQuietly(in);
            closeQuietly(session);
        }
        return success;
    }

    private static void execInstallCommand(Context context, PackageInstaller packageInstaller, int sessionId) {
        PackageInstaller.Session session = null;
        try {
            session = packageInstaller.openSession(sessionId);
            Intent intent = new Intent(context, InstallResultReceiver.class);
            intent.setAction(PACKAGE_INSTALLED_ACTION);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("DDDD", "[106]->execInstallCommand: " + sessionId);
            session.commit(pendingIntent.getIntentSender());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(session);
        }
    }


//    public static void uninstall(String packageName, Context context, Intent broadcastIntent) {
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
//                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
//        packageInstaller.uninstall(packageName, pendingIntent.getIntentSender());
//    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }
}
