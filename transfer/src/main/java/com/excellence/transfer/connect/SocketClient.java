package com.excellence.transfer.connect;

import android.util.Log;

import com.excellence.transfer.entity.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @ Author:yi wu
 * @ Date: Created in 10:06 2021/8/31
 * @ Description:
 */
public class SocketClient {


    private Socket mSocket;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private OnReceiveListener mOnReceiveListener;


    public SocketClient(String host, int port) {
        try {
            mSocket = new Socket(host, port);
            mInputStream = mSocket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceiveMessage() {
        if (mReadThread == null) {
            mReadThread = new ReadThread();
        }
        mReadThread.start();
    }

    public void sendCommand(Command cmd) {
        try {
            mSocket.getOutputStream().write(cmd.toBytes());
            mSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopReceiveMessage() {
        mReadThread.interrupt();
    }

    public void setOnReceiveListener(OnReceiveListener mOnReceiveListener) {
        this.mOnReceiveListener = mOnReceiveListener;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            String message;
            try {
                while (!isInterrupted()) {
                    message = bufferedReader.readLine();

                    if (message != null) {
                        Log.d("DDDD", "[60]->message: " + message);
                        mOnReceiveListener.onReceive(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
