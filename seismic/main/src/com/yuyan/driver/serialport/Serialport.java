package com.yuyan.driver.serialport;

public class Serialport {
    private native int nativeOpen();
    private native int nativeClose();
    private native int nativeRead(byte[] buff);
    private native int nativeWrite(byte[] buff, int len);

    public int open() {
        return nativeOpen();
    }

    public int close() {
        return nativeClose();
    }

    public int read(byte[] buff) {
        return nativeRead(buff);
    }

    public int write(byte[] buff, int len) {
        return nativeWrite(buff, len);
    }

    static {
        System.loadLibrary("libSerialport");
    }
}
