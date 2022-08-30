package com.yuyan;

import cat.handler.ServletHandler;
import cat.server.TomcatServer;
import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import com.yuyan.web.CommandListReader;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Root {
    public static final String GET_COMMAND_LIST_SIZE = "GET_COMMAND_LIST_SIZE";
    public static final String GET_COMMAND_BY_INDEX = "GET_COMMAND_BY_INDEX";

    public static void main(String[] args) {
        try {
            new Root().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        CommandHelper.read();

        TomcatServer tomcatServer = new TomcatServer();
        ServletHandler handler = new ServletHandler("com.yuyan.seismic") {
            @Override
            public boolean handle(ServletRequest req, ServletResponse res) {
                HttpServletRequest request = (HttpServletRequest) req;
                String requestUrl = request.getRequestURI();
                String requestPackageName = parsePackageName(requestUrl);
                requestUrl = parseUrl(request.getRequestURI());
                System.out.println("requestPackageName = " + requestPackageName);
                System.out.println("requestUrl = " + requestUrl);
                dispatch(requestUrl, req, res);
                return true;
            }
        };
        tomcatServer.registerServletHandler(handler);
        tomcatServer.startServer();
    }

    void dispatch(String requestUrl, ServletRequest req, ServletResponse res) {
        HttpServletRequest request = (HttpServletRequest) req;
        Map<String, String[]> argumentMap = request.getParameterMap();
        String[] argumentNames = new String[argumentMap.size()];
        int i = 0;
        for (String s : argumentMap.keySet()) {
            argumentNames[i++] = s;
            System.out.println("s = " + s);
            String[] value = argumentMap.get(s);
            System.out.println("value = " + Arrays.toString(value));
        }
        switch (requestUrl) {
            case GET_COMMAND_LIST_SIZE:
                int size = CommandListReader.getSize();
                System.out.println("size = " + size);
                break;
            case GET_COMMAND_BY_INDEX:
                Command command = CommandListReader.getCommand(0);
                System.out.println("command = " + command);
                break;
        }
    }
}
