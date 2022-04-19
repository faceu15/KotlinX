package com.excellence.transfer.connect;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @ Author:yi wu
 * @ Date: Created in 9:20 2021/9/2
 * @ Description:
 */
public class ReactionClient {

    private Socket mSocket;
    private InputStream mInputStream;

    public ReactionClient(String host, int port) {
        try {
            mSocket = new Socket(host, port);
            mInputStream = mSocket.getInputStream();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
