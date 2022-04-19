package com.excellence.transfer.entity;


/**
 * @ Author:yi wu
 * @ Date: Created in 10:04 2021/9/1
 * @ Description:
 */
public class Command {

    public static final byte CMD_SHELL = 0x0A;

    public static final byte CMD_SESSION_ID = 0x11;
    public static final byte CMD_SERVER_MSG = 0x12;
    public static final byte CMD_CALLBACK_MSG = 0x13;

    public static final byte CMD_SENT_APK_START = 0x1A;
    public static final byte CMD_SENT_APK_PAUSE = 0x1B;
    public static final byte CMD_SENT_APK_FINISH = 0x1C;
    public static final byte CMD_SENT_APK_DISCARD = 0x1D;
    public static final byte CMD_INSTALL_APK = 0x1E;


    public static final byte CMD_LOGCAT_START = 0x2A;
    public static final byte CMD_LOGCAT_STOP = 0x2B;

    private static final int COMMAND_INDEX = 2;
    private static final int LENGTH_INDEX = 3;
    private static final int DATA_OFFSET = 4;

    public static final byte[] HEAD = {(byte) 0x7F, (byte) 0x7F};
    private byte command;

    /**
     * length 是data的长度加上CRC校验位的长度
     */
    private byte length = 2;

    private byte[] data;

    private byte CRC;

    public Command() {
    }

    public Command(byte command, byte[] data) {
        this.command = command;
        this.data = data;
        if (data != null) {
            this.length = (byte) (data.length + 1);
        }
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[length + DATA_OFFSET];
        bytes[0] = HEAD[0];
        bytes[1] = HEAD[1];
        bytes[COMMAND_INDEX] = command;
        bytes[LENGTH_INDEX] = length;
        CRC = 0;
        for (int j = 0; j < DATA_OFFSET; j++) {
            CRC ^= bytes[j];
        }
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                bytes[i + DATA_OFFSET] = data[i];
                CRC ^= data[i];
            }
        }

        bytes[bytes.length - 1] = CRC;

        return bytes;
    }

    public static Command parse(byte[] buff) throws Exception {
        Command command = new Command();
        if (buff[0] != HEAD[0] || buff[1] != HEAD[1]) {
            System.out.println("parse error head");
            return null;
        }
        command.setCommand(buff[COMMAND_INDEX]);
        command.setLength(buff[LENGTH_INDEX]);
        command.setCRC(buff[buff.length - 1]);

        byte[] data = new byte[command.length - 1];
        byte crc = 0;
        //parse head 4 byte
        for (int j = 0; j < DATA_OFFSET; j++) {
            crc ^= buff[j];
        }
        //parse data[]
        for (int i = 0; i < command.length - 1; i++) {
            data[i] = buff[i + DATA_OFFSET];
            crc ^= data[i];
        }
        if (crc != command.getCRC()) {
            throw new Exception("parse error CRC Command.CRC = " + command.getCRC() + " parse crc= " + crc);
        }
        command.setData(data);

        return command;
    }

    public byte[] getHeader() {
        return HEAD;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        length = (byte) (data.length + 1);
    }

    public byte getCRC() {
        return CRC;
    }

    public void setCRC(byte CRC) {
        this.CRC = CRC;
    }
}
