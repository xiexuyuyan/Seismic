package com.yuyan.driver.serialport;

public class Serialport {
    private native int nativeOpen();
    private native int nativeClose();
    private native int nativeRead(byte[] buff);

    public int open() {
        return nativeOpen();
    }

    public int close() {
        return nativeClose();
    }

    public int read(byte[] buff) {
        return nativeRead(buff);
    }

    static {
        System.loadLibrary("libSerialport");
    }
}
