package com.excellence.transfer.connect;

import static com.excellence.transfer.CommandService.CODE;
import static com.excellence.transfer.CommandService.SHELL_CMD;
import static com.excellence.transfer.CommandService.UPDATE;
import static com.excellence.transfer.utils.Constant.APK_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.excellence.transfer.CommandService;
import com.excellence.transfer.entity.Command;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Author:yi wu
 * @ Date: Created in 11:13 2021/9/3
 * @ Description:
 */
public class AndroidSocketClient extends WebSocketClient {
    private static final String TAG = "AndroidSocketClient";

    private static final String LOGCAT = "logcat";

    private Context mContext;
    private FileChannel mFileChannel;
    private File mUpdateFile;
    private HashMap<String, Process> mCommandMap = new HashMap<>();
    private boolean mCatchingLog = false;

    public AndroidSocketClient(Context context, URI serverUri) {
        super(serverUri);
        mContext = context;
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        Log.d(TAG, "onOpen: " + handShakeData.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Log.i("DDDD", "[47]->onMessage: " + message);
        byte[] bytes = message.getBytes();
        if (bytes[0] == Command.HEAD[0] && bytes[1] == Command.HEAD[1]) {
            try {
                Command command = Command.parse(bytes);
                switch (Objects.requireNonNull(command).getCommand()) {
                    case Command.CMD_SHELL:
                        execShellCommand(command);
                        break;
                    case Command.CMD_SENT_APK_START:
                        prepareApkReceiver();
                        break;
                    case Command.CMD_SENT_APK_FINISH:
                        receiveApkFinish();
                        break;


                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "onMessage: receive unknown command");
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        if (mUpdateFile != null) {
            Flowable.just(bytes)
                    .map(data -> {
                        try {
                            mFileChannel.write(data);
                        } catch (IOException e) {
                            Log.e(TAG, "onMessage: " + e.toString());
                        }
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();

        }
    }

    private void execShellCommand(Command command) {
        String shell = new String(command.getData());
        Log.d(TAG, "execShellCommand: " + shell);
        if (shell.equals("logcat stop")) {
            stopLogcat();
        } else if (shell.contains("logcat")) {

            catchLogcat(shell);
        }
    }


    @SuppressLint("CheckResult")
    public void catchLogcat(String shell) {
        Log.i("DDDD", "AndroidSocketClient[132]->catchLogcat: ");

        mCatchingLog = true;
        Flowable.just(shell)
                .map(cmd -> {
                    Process process = Runtime.getRuntime().exec(new String[]{shell});
                    mCommandMap.put(LOGCAT, process);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    Log.i("DDDD", "AndroidSocketClient[135]->catchLogcat: ");
                    while (mCatchingLog) {
                        send(bufferedReader.readLine());
                    }
                    return process;
                }).subscribeOn(Schedulers.io())
                .subscribe(process -> {

                }, throwable -> {
                    stopLogcat();
                    sendCommand(Command.CMD_CALLBACK_MSG, throwable.toString());
                    Log.e(TAG, "catchLogcat: " + throwable.toString());
                });
    }

    private void stopLogcat() {
        mCatchingLog = false;
        Process process = mCommandMap.get("logcat");
        if (process != null) {
            try {
                process.getInputStream().close();
                process.getErrorStream().close();
                process.destroy();
                Log.i("DDDD", "AndroidSocketClient[120]->execShellCommand: 销毁");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void sendCommand(byte cmd, String data) {
        Flowable.just(cmd)
                .map(c -> {
                    Command command = new Command();
                    command.setCommand(cmd);
                    if (data != null) {
                        command.setData(data.getBytes());
                    }
                    send(new String(command.toBytes()));
                    return c;
                }).subscribeOn(Schedulers.io())
                .subscribe(command -> Log.d(TAG, "sendCommand : "), throwable -> {
                    Log.e(TAG, "sendCommand: " + throwable.toString());
                });
    }

    private void prepareApkReceiver() {
        if (mFileChannel == null || !mFileChannel.isOpen()) {
            String path = mContext.getFilesDir().getAbsolutePath();
            mUpdateFile = new File(path, APK_NAME);
            Log.d(TAG, "apk update file :" + mUpdateFile.getAbsolutePath());
            try {
                if (!mUpdateFile.exists()) {
                    mUpdateFile.createNewFile();
                }
                mFileChannel = new FileOutputStream(mUpdateFile).getChannel();
                sendCommand(Command.CMD_SENT_APK_START, null);
                Log.d(TAG, "Prepare finish.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sendCommand(Command.CMD_SENT_APK_START, null);
        }
    }

    private void receiveApkFinish() {
        sendCommand(Command.CMD_SENT_APK_FINISH, "start to install");
        Intent intent = new Intent(mContext, CommandService.class);
        intent.putExtra(CODE, UPDATE);
        mContext.startService(intent);
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: " + code + "  reason:" + reason + " remote:" + remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "onError: " + ex.toString());
    }
}
