package com.yuyan;

import cat.handler.ServletHandler;
import cat.server.TomcatServer;
import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import com.yuyan.tcp.TcpClientService;
import com.yuyan.web.CommandHandler;
import droid.message.Looper;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Root {
    public static final String GET_COMMAND_LIST_SIZE = "GET_COMMAND_LIST_SIZE";
    public static final String GET_COMMAND_BY_INDEX = "GET_COMMAND_BY_INDEX";
    public static final String GET_COMMAND_ALL = "GET_COMMAND_ALL";
    public static final String HANDLE_POST_COMMAND = "HANDLE_POST_COMMAND";

    public static void main(String[] args) {
        try {
            new Root().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        Looper.prepareMainLooper();

        new TcpClientService().start();
        CommandHelper.read();

        TomcatServer tomcatServer = new TomcatServer();
        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                HttpServletRequest request = (HttpServletRequest) req;
                String requestUrl = request.getRequestURI();
                String[] args = requestUrl.split("/");
                System.out.println("args = " + Arrays.toString(args));
                String requestPackageName = parsePackageName(requestUrl);
                requestUrl = parseUrl(request.getRequestURI());
                System.out.println("requestPackageName = " + requestPackageName);
                System.out.println("requestUrl = " + requestUrl);
                try {
                    dispatch(requestUrl, req, res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

    void dispatch(String requestUrl, ServletRequest req, ServletResponse res) throws IOException {
        switch (requestUrl) {
            case GET_COMMAND_LIST_SIZE:
                CommandHandler.getSize(req, res);
                break;
            case GET_COMMAND_BY_INDEX:
                CommandHandler.getCommandByIndex(req, res);
                break;
            case GET_COMMAND_ALL:
                CommandHandler.getCommandAll(req, res);
                break;
            case HANDLE_POST_COMMAND:
                CommandHandler.handlePostCommand(req, res);
                break;
        }
    }
}
