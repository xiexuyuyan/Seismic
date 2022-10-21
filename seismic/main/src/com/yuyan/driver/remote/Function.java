package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.Root;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.local.CommandResolver;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.driver.serialport.SerialportRepository;
import com.yuyan.model.Command;
import com.yuyan.model.CommandRecv;
import com.yuyan.utils.Log;
import com.yuyan.web.model.SimpleArrayStat;
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

    public static void postCommandRemote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postCommandRemote: ");
        printParametersFromHttpRequest(request);

        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        Socket socket = SocketPlugin.INSTANCE.getSocket(Root.getThreadLocalSocket());
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
                sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_TIMEOUT, response);
                return;
            }
        }

        if (readLen == -1) {
            Log.i(TAG, "[Coder Wu] postCommand: " +
                    "we received -1 in socket input, so closed the socket");
            sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_NO_MATCH, response);
            socket.close();
            return;
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + receiveByteToString(buff, readLen));

        sendCommandRevReply(commandDataName, buff, readLen, response);
    }


    public static void postCommandLocal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postCommandLocal: ");
        printParametersFromHttpRequest(request);

        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        byte[] sendBytes = sendStringToByte(valueCodeString);
        Serialport serialport = Serialport.getInstance();
        serialport.write(sendBytes, sendBytes.length);

        byte[] buff = new byte[1024];
        int readLen = serialport.read(buff);

        if (readLen == -1) {
            sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_TIMEOUT, response);
            return;
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + receiveByteToString(buff, readLen));
        sendCommandRevReply(commandDataName, buff, readLen, response);
    }

    public static void postSwitchSerialport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postSwitchSerialport: ");
        printParametersFromHttpRequest(request);

        boolean status = request.getParameterMap().get("status")[0].equals("true");
        SerialportRepository.switchSerialport(status);

        sendSimpleStatReply(Constant.POST_SWITCH_SERIALPORT, status+"", response);
    }


    public static void postUploadHDCPKey(HttpServletRequest request, HttpServletResponse response) {
        Log.i(TAG, "[Coder Wu] postUploadHDCPKey: ");

        try {
            int size = request.getParts().size();
            if (size <= 0 ) {
                return;
            }
            Part part = request.getPart("HDCPKeyFile");
            Log.i(TAG, "[Coder Wu] postUploadHDCPKey: part = " + part.getName());
            String fileName = part.getSubmittedFileName();
            Log.i(TAG, "[Coder Wu] postUploadHDCPKey: fileName = " + fileName);
            // TODO: 2022/10/21 to create FileDirectory avoid access denied
            File file = new File(Constant.HDCP_KEY_UPLOAD_PATH);
            if (file.exists() && file.isDirectory()) {
                String defaultFilePath = Constant.HDCP_KEY_UPLOAD_PATH + fileName;
                saveSimpleFile(defaultFilePath, part.getInputStream());
            }
            /* part.write(fileName); */
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "[Coder Wu] postUploadHDCPKey: end");
    }

    public static void getHDCPKeyList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = new File(Constant.HDCP_KEY_UPLOAD_PATH);
        SimpleArrayStat statMessage = new SimpleArrayStat(Constant.GET_HDCP_KEY_LIST, new String[]{});
        if (file.exists() && file.isDirectory()) {
            String[] fileList = file.list();
            for (int i = 0; fileList != null && i < fileList.length; i++) {
                Log.i(TAG, "[Coder Wu] getHDCPKeyList: [" + i + "]: " + fileList[i]);
            }
            
            statMessage = new SimpleArrayStat(Constant.GET_HDCP_KEY_LIST, fileList);
        }

        Gson gson = new Gson();
        String stateString = gson.toJson(statMessage);
        response.getWriter().println(stateString);
    }









    /*------------------------------------------------------------------*/
    private static void sendCommandRevReply(String commandDataName, byte[] buff, int readLen, HttpServletResponse response) {
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

    private static void sendSimpleStatReply(String name, String value, HttpServletResponse response) {
        SimpleStat statMessage = new SimpleStat(name, value);
        Gson gson = new Gson();
        String stateString = gson.toJson(statMessage);
        try {
            response.getWriter().println(stateString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printParametersFromHttpRequest(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] values = parameterMap.get(s);
            Log.i(TAG, "[Coder Wu] printParametersFromHttpRequest: \t" + s + ":" + Arrays.toString(values));
        }
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
