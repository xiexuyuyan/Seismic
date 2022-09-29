package com.yuyan.driver.serialport;

import java.io.*;

public class Serialport {
    private InputStream inputStream;
    private OutputStream outputStream;

    private native FileDescriptor nativeOpen();
    private native int nativeReadByte();

    public void open() {
        FileDescriptor mFd = nativeOpen();
        inputStream = new SerialportInputStream() {
            @Override
            public int read() throws IOException {
                return nativeReadByte();
            }
        };
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    static {
        System.loadLibrary("libSerialport");
    }
}
