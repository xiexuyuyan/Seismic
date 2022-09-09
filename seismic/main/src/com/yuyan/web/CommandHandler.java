package com.yuyan.web;

import com.google.gson.Gson;
import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import droid.message.Message;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class CommandHandler {
    public static final String GET_COMMAND_ALL = "GET_COMMAND_ALL";
    public static final String POST_COMMAND = "POST_COMMAND";



    public static void dispatch(ServletRequest req, ServletResponse res, Socket socket) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        System.out.println("[Coder Wu] req.getRequestURI() = " + request.getRequestURI());
        String[] uriArray = request.getRequestURI().split("/");
        String action = uriArray.length > 2 ? uriArray[2] : null;
        System.out.println("[Coder Wu] action = " + action);
        if (action != null) {
            try {
                switch (action) {
                    case GET_COMMAND_ALL:
                        CommandHandler.getCommandAll(request, response);
                        break;
                    case POST_COMMAND:
                        CommandHandler.postCommand(request, response, socket);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void reloadCommandList(HttpServletRequest req, HttpServletResponse res) throws IOException {
        CommandHelper.init();
    }

    public static void getSize(HttpServletRequest req, HttpServletResponse res) {
        int size = CommandHelper.INSTANCE.commandList.commands.size();
    }

    public static void getCommandByIndex(HttpServletRequest req, HttpServletResponse res) {
        Command command = CommandHelper.INSTANCE.commandList.commands.get(0);
    }

    public static void getCommandAll(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(CommandHelper.INSTANCE.commandList);
        response.getWriter().println(jsonStr);
    }

    public static void postCommand(HttpServletRequest req, HttpServletResponse res, Socket socket) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            System.out.println("\t" + s + ":" + Arrays.toString(values));
        }

        String valueCodeString = parameterMap.get("value")[0];
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(sendStringToByte(valueCodeString));

        InputStream inputStream = socket.getInputStream();
        byte[] buff = new byte[1024];
        int readLen = inputStream.read(buff, 0, 1024);

        if (readLen == -1) {
            System.out.println("[Coder Wu] " +
                    "we received -1 in socket input, so closed the socket");
            socket.close();
        }

        System.out.println("[Coder Wu] readLen = " + readLen
                + ", " + receiveByteToString(buff, readLen));
    }

    private static byte[] sendStringToByte(String commandHexCode) {
        if ((commandHexCode.length() % 2) == 1) {
            commandHexCode = commandHexCode.substring(0, commandHexCode.length()-1);
        }

        byte[] re = new byte[commandHexCode.length()/2];

        for (int i = 0; i < commandHexCode.length(); i+=2) {
            String s = commandHexCode.substring(i, i+2);
            int d = Integer.parseInt(s, 16);
            byte b = (byte) d;
            re[i/2] = b;
        }

        return re;
    }

    private static String receiveByteToString(final byte[] buff, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = buff[i];
            int d = (b & 0x00_00_00_FF);
            String s = Integer.toString(d, 16);
            if (s.length() == 1) {
                s = "0" + s;
            }
            builder.append(s.toUpperCase());
        }
        return builder.toString();
    }
}
