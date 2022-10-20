package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.Root;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.local.CommandResolver;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.model.Command;
import com.yuyan.model.CommandRecv;
import com.yuyan.utils.Log;
import com.yuyan.web.model.SimpleStat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Function {
    private static final String TAG = "Function";


    public static void getCommandAll(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(CommandRepository.INSTANCE.commandList);
        response.getWriter().println(jsonStr);
    }

    public static void postCommandRemote(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Socket socket = SocketPlugin.INSTANCE.getSocket(Root.getThreadLocalSocket());
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] postCommand: \t" + s + ":" + Arrays.toString(values));
        }

        String valueCodeString = parameterMap.get("value")[0];
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(sendStringToByte(valueCodeString));

        InputStream inputStream = socket.getInputStream();
        byte[] buff = new byte[1024];
        int readLen = -1;
        try {
            readLen = inputStream.read(buff, 0, 1024);
        } catch (SocketTimeoutException e) {
            Log.i(TAG, "[Coder Wu] postCommandRemote: SocketTimeoutException " + e.getMessage());
            e.printStackTrace();

            String timeoutReason = e.getMessage();
            if (timeoutReason.equals("Read timed out")) {
                String commandDataName = parameterMap.get("command_data_name")[0];
                SimpleStat statMessage = new SimpleStat(commandDataName, "timeout");
                Gson gson = new Gson();
                String stateString = gson.toJson(statMessage);
                res.getWriter().println(stateString);

                return;
            }
        }

        if (readLen == -1) {
            Log.i(TAG, "[Coder Wu] postCommand: " +
                    "we received -1 in socket input, so closed the socket");
            socket.close();
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + receiveByteToString(buff, readLen));

        String reply = receiveByteToString(buff, readLen);
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        List<CommandRecv> commandRecvList = CommandResolver.checkUnitRecv(reply, commandList, true);
        if (commandRecvList.size() > 0) {
            CommandRecv commandRecv = commandRecvList.get(0);
            String replyValueString = CommandResolver.getValueString(commandRecv.commandData.replyHexCode, commandRecv.code);
            SimpleStat statMessage = new SimpleStat(commandRecv.commandData.name, replyValueString);
            Gson gson = new Gson();
            String stateString = gson.toJson(statMessage);
            res.getWriter().println(stateString);
        }
    }


    public static void postCommandLocal(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] postCommand: \t" + s + ":" + Arrays.toString(values));
        }

        String valueCodeString = parameterMap.get("value")[0];
        byte[] sendBytes = sendStringToByte(valueCodeString);
        Serialport serialport = Serialport.getInstance();
        serialport.write(sendBytes, sendBytes.length);

        byte[] buff = new byte[1024];
        int readLen = serialport.read(buff);

        if (readLen == -1) {
            String commandDataName = parameterMap.get("command_data_name")[0];
            SimpleStat statMessage = new SimpleStat(commandDataName, "timeout");
            Gson gson = new Gson();
            String stateString = gson.toJson(statMessage);
            res.getWriter().println(stateString);
            return;
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + receiveByteToString(buff, readLen));

        String reply = receiveByteToString(buff, readLen);
        List<Command> commandList = CommandRepository.INSTANCE.commandList.commands;
        List<CommandRecv> commandRecvList = CommandResolver.checkUnitRecv(reply, commandList, true);
        if (commandRecvList.size() > 0) {
            CommandRecv commandRecv = commandRecvList.get(0);
            String replyValueString = CommandResolver.getValueString(commandRecv.commandData.replyHexCode, commandRecv.code);
            SimpleStat statMessage = new SimpleStat(commandRecv.commandData.name, replyValueString);
            Gson gson = new Gson();
            String stateString = gson.toJson(statMessage);
            res.getWriter().println(stateString);
        }
    }

    public static void postSwitchSerialport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] postSwitchSerialport: \t" + s + ":" + Arrays.toString(values));
        }

        Serialport serialport = Serialport.getInstance();
        boolean status = parameterMap.get("status")[0].equals("true");
        boolean before = serialport.getStatus();

        if (status && !before) {
            serialport.open("COM6");
            serialport.setReadTimeout(3000);
        } else if (!status && before) {
            serialport.close();
        }

        boolean after = serialport.getStatus();
        Log.i(TAG, "[Coder Wu] postSwitchSerialport: " + before + " --> " + after);

        SimpleStat statMessage = new SimpleStat("POST_SWITCH_SERIALPORT", "" + status);
        Gson gson = new Gson();
        String stateString = gson.toJson(statMessage);
        response.getWriter().println(stateString);
    }


    public static void postUploadHDCPKey(HttpServletRequest request, HttpServletResponse response) {
        Log.i(TAG, "[Coder Wu] postUploadHDCPKey: ");
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] postSwitchSerialport: \t" + s + ":" + Arrays.toString(values));
        }

        try {
            int size = request.getParts().size();
            Part part = request.getPart("HDCPKeyFile");
            Log.i(TAG, "[Coder Wu] postUploadHDCPKey: part size = " + size);
            Log.i(TAG, "[Coder Wu] postUploadHDCPKey: part = " + part.getName());
            String fileName = part.getSubmittedFileName();
            String defaultFilePath = "seismic/main/res/" + fileName;
            saveSimpleFile(defaultFilePath, part.getInputStream());
            part.write(fileName);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "[Coder Wu] postUploadHDCPKey: end");
    }

    private static void saveSimpleFile(String filePath, InputStream inputStream) throws IOException {
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
    /*------------------------------------------------------------------*/
}
