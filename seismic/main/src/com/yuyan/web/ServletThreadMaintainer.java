package com.yuyan.web;

import com.yuyan.driver.remote.Response;
import com.yuyan.driver.remote.SocketPlugin;
import com.yuyan.utils.Log;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.net.Socket;

public class ServletThreadMaintainer {
    private static final String TAG = "ServletThreadMaintainer";

    private final ServletRequest request;
    private final ServletResponse response;
    private final Socket socket;

    public ServletThreadMaintainer(ServletRequest req, ServletResponse res, ThreadLocal<Socket> socketThreadLocal) {
        this.request = req;
        this.response = res;
        this.socket = SocketPlugin.INSTANCE.getSocket(socketThreadLocal);
    }

    public void start() {
        String threadName = Thread.currentThread().getName();
        Log.i(TAG, "[Coder Wu] start: threadName = " + threadName);

        HttpServletResponse res = (HttpServletResponse) this.response;
        res.setHeader("Access-Control-Allow-Origin", "*");

        Response.dispatch(request, response, socket);
    }
}
