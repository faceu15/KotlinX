package com.excellence.transfer.connect;


import com.excellence.transfer.entity.Command;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @ Author:yi wu
 * @ Date: Created in 9:54 2021/9/2
 * @ Description:
 */
public class ControlClient extends WebSocketClient {


    public ControlClient(URI serverUri) {
        super(serverUri);
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        Command parse = null;
        try {
            parse = Command.parse(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        parse.getCommand();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
