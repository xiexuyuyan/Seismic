package com.yuyan.tcp;

public class TcpClientService {
    public void start() {
        new Thread(TcpClientRunnable.INSTANCE).start();
    }
}
