package com.yuyan.web;

import com.google.gson.Gson;
import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import com.yuyan.tcp.TcpClientRunnable;
import droid.message.Handler;
import droid.message.Message;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CommandHandler {
    public static void reloadCommandList(ServletRequest req, ServletResponse res) throws IOException {
        CommandHelper.read();
    }

    public static void getSize(ServletRequest req, ServletResponse res) {
        int size = CommandHelper.INSTANCE.commandList.commands.size();
        System.out.println("size = " + size);
    }

    public static void getCommandByIndex(ServletRequest req, ServletResponse res) {
        Command command = CommandHelper.INSTANCE.commandList.commands.get(0);
        System.out.println("command = " + command);
    }

    public static void getCommandAll(ServletRequest req, ServletResponse res) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(CommandHelper.INSTANCE.commandList);
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().println(jsonStr);
    }

    public static void handlePostCommand(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Map<String, String[]> argumentMap = request.getParameterMap();
        String[] argumentNames = new String[argumentMap.size()];
        int i = 0;
        for (String s : argumentMap.keySet()) {
            argumentNames[i++] = s;
            String[] value = argumentMap.get(s);
            System.out.println(argumentNames[i-1] + " : " + Arrays.toString(value));
        }

        String commandDataName = argumentMap.get("command_data_name")[0];
        Command command = CommandHelper.INSTANCE.commandMap.get(commandDataName);

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().println(command);

        Handler handler = TcpClientRunnable.INSTANCE.mH;
        Message message = new Message();
        message.what = 10086;
        handler.sendMessage(message);
    }
}
