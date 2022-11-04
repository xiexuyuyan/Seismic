package com.yuyan.driver.remote;

import com.google.gson.Gson;
import org.yuyan.command.utils.ByteUtils;
import com.yuyan.driver.local.CommandRepository;
import org.yuyan.command.utils.CommandResolver;
import org.yuyan.command.model.Command;
import org.yuyan.command.model.CommandRecv;
import com.yuyan.utils.Log;
import com.yuyan.web.model.SimpleStat;
import org.yuyan.command.utils.CommandUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FunctionCommon {
    private static final String TAG = "FunctionCommon";


    /*------------------------------------------------------------------*/
    public static void sendCommandRevReply(String commandDataName, byte[] buff, int readLen, HttpServletResponse response) {
        String reply = ByteUtils.hexBytesToString(buff, readLen);
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        List<CommandRecv> commandRecvList = CommandResolver.parse(reply, commandList, true);

        for (int i = 0; i < commandRecvList.size(); i++) {
            CommandRecv commandRecv = commandRecvList.get(i);
            String replyValueString = CommandUtils.getValueString(commandRecv.commandData.replyHexCode, commandRecv.code);
            SimpleStat statMessage = new SimpleStat(commandRecv.commandData.name, replyValueString);
            Gson gson = new Gson();
            String stateString = gson.toJson(statMessage);
            try {
                response.getWriter().println(stateString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (commandRecvList.size() <= 0) {
            SimpleStat statMessage = new SimpleStat(commandDataName, Constant.SERIALPORT_READ_NO_MATCH);
            Gson gson = new Gson();
            String stateString = gson.toJson(statMessage);
            try {
                response.getWriter().println(stateString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendSimpleStatReply(String name, String value, HttpServletResponse response) {
        SimpleStat statMessage = new SimpleStat(name, value);
        Gson gson = new Gson();
        String stateString = gson.toJson(statMessage);
        try {
            response.getWriter().println(stateString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printParametersFromHttpRequest(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] printParametersFromHttpRequest: \t" + s + ":" + Arrays.toString(values));
        }
    }

    public static void saveSimpleFile(String filePath, InputStream inputStream) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){
            boolean createFileRet = file.createNewFile();
            Log.i(TAG, "[Coder Wu] saveSimpleFile: createFileRet " + createFileRet);
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        in = new BufferedInputStream(inputStream);
        out = new BufferedOutputStream(new FileOutputStream(file));
        int len = -1;
        byte[] b = new byte[1024];
        while((len = in.read(b)) != -1){
            out.write(b,0,len);
        }
        in.close();
        out.close();
    }

    /*------------------------------------------------------------------*/
}
