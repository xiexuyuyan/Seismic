package com.yuyan.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
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
        HttpServletResponse response = (HttpServletResponse) this.response;
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            response.getWriter().println("sssss");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String threadName = Thread.currentThread().getName();
        System.out.println("[Coder Wu] threadName = " + threadName);

        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println("from " + threadName);


        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            String line = bufferedReader.readLine();
            System.out.println("[Coder Wu] line = " + line);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



}
