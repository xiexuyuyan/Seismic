package com.yuyan;

import cat.handler.ServletHandler;
import cat.server.TomcatServer;
import com.yuyan.repository.CommandHelper;
import com.yuyan.web.ServletThreadMaintainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class Root {

    public static void main(String[] args) {
        try {
            new Root().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        CommandHelper.init();

        TomcatServer tomcatServer = new TomcatServer();

        ThreadLocal<Socket> socketThreadLocal = new ThreadLocal<>();
        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                Socket oldSock = socketThreadLocal.get();
                if (oldSock == null || oldSock.isClosed()) {
                    System.out.println("[Coder Wu] prepare create socket.");
                    Socket socket = createSocket("192.168.18.235", 53705);
                    if (socket == null) {
                        try {
                            throw new Exception("Create a socket failed");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        socketThreadLocal.set(socket);
                        new ServletThreadMaintainer(req, res, socket).start();
                    }
                } else {
                    System.out.println("[Coder Wu] prepare use old socket.");
                    new ServletThreadMaintainer(req, res, oldSock).start();
                }
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

    private Socket createSocket(String ip, int port) {
        String[] ipStr = ip.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }

        try {
            System.out.println("[Coder Wu] create socket. at " + ip + ":" + port);
            return new Socket(InetAddress.getByAddress(ipBuf),port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
