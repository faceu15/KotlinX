package com.excellence.transfer.connect;

/**
 * @ Author:yi wu
 * @ Date: Created in 14:35 2021/9/1
 * @ Description:
 */
interface OnReceiveListener {

    /**
     * @param message the server return string
     */
    void onReceive(String message);

}
