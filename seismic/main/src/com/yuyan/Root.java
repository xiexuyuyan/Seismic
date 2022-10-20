package com.yuyan;

import cat.handler.ServletHandler;
import cat.server.TomcatServer;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.web.ServletThreadMaintainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.Socket;

public class Root {
    private final static ContextThread contextThread = new ContextThread();

    public static ThreadLocal<Socket> getThreadLocalSocket() {
        return contextThread.threadLocalSocket;
    }

    private static class ContextThread {
        private final ThreadLocal<Socket> threadLocalSocket;
        ContextThread() {
            threadLocalSocket = new ThreadLocal<>();
        }
    }

    public static void main(String[] args) {
        try {
            new Root().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        CommandRepository.init();

        TomcatServer tomcatServer = new TomcatServer();

        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                new ServletThreadMaintainer(req, res).start();
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

}
