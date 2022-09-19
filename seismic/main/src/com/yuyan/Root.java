package com.yuyan;

import cat.handler.ServletHandler;
import cat.server.TomcatServer;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.web.ServletThreadMaintainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.Socket;

public class Root {

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

        ThreadLocal<Socket> socketThreadLocal = new ThreadLocal<>();
        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                new ServletThreadMaintainer(req, res, socketThreadLocal).start();
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

}
