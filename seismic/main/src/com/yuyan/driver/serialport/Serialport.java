package com.yuyan.driver.serialport;

public class Serialport {
    private native int nativeOpen(String com);
    private native int nativeClose();
    private native boolean nativeGetStatus();
    private native int nativeRead(byte[] buff);
    private native int nativeWrite(byte[] buff, int len);

    private static volatile Serialport mSerialport;
    private Serialport() {}

    public static Serialport getInstance() {
        if (mSerialport == null) {
            synchronized (Serialport.class) {
                mSerialport = new Serialport();
            }
        }

        return mSerialport;
    }

    public int open(String com) {
        return nativeOpen(com);
    }

    public int close() {
        return nativeClose();
    }

    public boolean getStatus() {
        return nativeGetStatus();
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
