package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.local.CommandResolver;
import com.yuyan.model.Command;
import com.yuyan.model.CommandRecv;
import com.yuyan.utils.Log;
import com.yuyan.web.model.SimpleStat;

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
        String reply = receiveByteToString(buff, readLen);
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        List<CommandRecv> commandRecvList = CommandResolver.checkUnitRecv(reply, commandList, true);

        for (int i = 0; i < commandRecvList.size(); i++) {
            CommandRecv commandRecv = commandRecvList.get(i);
            String replyValueString = CommandResolver.getValueString(commandRecv.commandData.replyHexCode, commandRecv.code);
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

    public static byte[] sendStringToByte(String commandHexCode) {
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

    public static String receiveByteToString(final byte[] buff, int len) {
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
    /*------------------------------------------------------------------*/
}
