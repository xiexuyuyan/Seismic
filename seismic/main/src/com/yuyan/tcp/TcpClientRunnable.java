package com.yuyan.tcp;

import droid.message.Handler;
import droid.message.Looper;
import droid.message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public enum TcpClientRunnable implements Runnable {
    INSTANCE;

    public H mH;

    private final Socket socketClient = createSocket("192.168.18.143", 53705);

    @Override
    public void run() {
        Looper.prepare();
        mH = new H();

        Looper.loop();
        try {
            throw new Exception("ContextThread looper stop");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Socket socket = INSTANCE.socketClient;
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Socket createSocket(String ip, int port) {
        String[] ipStr = ip.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }

        try {
            return new Socket(InetAddress.getByAddress(ipBuf),port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    final class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String threadName = Thread.currentThread().getName();
            System.out.println("Thread[" + threadName + "]: handleMessage():msg.what = " + msg.what + ", msg.obj = " + msg.obj);

            Socket socket = INSTANCE.socketClient;
            if (socket != null) {
                try {
                    handleSocketMessage(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void handleSocketMessage(Socket socket) throws IOException {
        String remoteAddr = socket.getRemoteSocketAddress().toString();
        OutputStream os = socket.getOutputStream();
        PrintStream p = new PrintStream(os);
        p.println("from client");
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        String strTemp = bufferReader.readLine();
        System.out.println("strTemp = " + strTemp);
        os.flush();
    }
}
