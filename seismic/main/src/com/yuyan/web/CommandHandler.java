package com.yuyan.web;

import com.google.gson.Gson;
import com.yuyan.model.Command;
import com.yuyan.repository.CommandHelper;
import droid.message.Message;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandHandler {
    public static void reloadCommandList(ServletRequest req, ServletResponse res) throws IOException {
        CommandHelper.init();
    }

    public static void getSize(ServletRequest req, ServletResponse res) {
        int size = CommandHelper.INSTANCE.commandList.commands.size();
    }

    public static void getCommandByIndex(ServletRequest req, ServletResponse res) {
        Command command = CommandHelper.INSTANCE.commandList.commands.get(0);
    }

    public static void getCommandAll(ServletRequest req, ServletResponse res) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(CommandHelper.INSTANCE.commandList);
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().println(jsonStr);
    }
}
