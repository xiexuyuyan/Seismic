package com.yuyan.driver.remote;

import com.yuyan.utils.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public enum SocketPlugin {
    INSTANCE;

    private String remoteIpAddress = "192.168.18.235";
    private int remoteTcpPort = 53705;

    private final String TAG = "SocketRemoteManager";

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        INSTANCE.remoteIpAddress = remoteIpAddress;
    }

    public int getRemoteTcpPort() {
        return remoteTcpPort;
    }

    public void setRemoteTcpPort(int remoteTcpPort) {
        INSTANCE.remoteTcpPort = remoteTcpPort;
    }


    public Socket getSocket(ThreadLocal<Socket> socketThreadLocal) {
        Socket oldSock = socketThreadLocal.get();
        if (oldSock == null || oldSock.isClosed()) {
            Log.i(TAG, "[Coder Wu] getSocket: prepare create socket.");
            oldSock = createSocket(remoteIpAddress, remoteTcpPort);
            if (oldSock == null) {
                try {
                    throw new Exception("[Coder Wu] Create a socket failed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                socketThreadLocal.set(oldSock);
            }
        } else {
            Log.i(TAG, "[Coder Wu] getSocket: prepare use old socket.");
        }
        return oldSock;
    }


    private Socket createSocket(String ip, int port) {
        String[] ipStr = ip.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }

        try {
            Log.i(TAG, "[Coder Wu] createSocket: at " + ip + ":" + port);
            Socket socket = new Socket(InetAddress.getByAddress(ipBuf),port);
            socket.setSoTimeout(3000);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
