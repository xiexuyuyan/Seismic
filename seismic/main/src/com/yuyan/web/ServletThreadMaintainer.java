package com.yuyan.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.net.Socket;

public class ServletThreadMaintainer {
    private final ServletRequest request;
    private final ServletResponse response;
    private final Socket socket;

    public ServletThreadMaintainer(ServletRequest req, ServletResponse res, Socket socket) {
        this.request = req;
        this.response = res;
        this.socket = socket;
    }

    public void start() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[Coder Wu] threadName = " + threadName);

        HttpServletResponse res = (HttpServletResponse) this.response;
        res.setHeader("Access-Control-Allow-Origin", "*");

        CommandHandler.dispatch(request, response, socket);
    }





}
