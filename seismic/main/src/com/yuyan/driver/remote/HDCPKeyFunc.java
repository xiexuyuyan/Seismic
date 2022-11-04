package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.Root;
import org.yuyan.command.utils.ByteUtils;
import com.yuyan.driver.local.CommandRepository;
import org.yuyan.command.utils.CommandResolver;
import com.yuyan.driver.serialport.Serialport;
import org.yuyan.command.model.Command;
import org.yuyan.command.model.CommandHexCode;
import com.yuyan.web.model.SimpleArrayStat;
import org.yuyan.command.utils.CommandUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HDCPKeyFunc {
    private static final String TAG = "HDCPKeyFunc";

    private static final int MAX_HDCP_KEY_LEN = 2048; // 2KB, tomcat default format data is 1M.

    public static void postUploadHDCPKey(HttpServletRequest request, HttpServletResponse response) {
        com.yuyan.utils.Log.i(TAG, "[Coder Wu] postUploadHDCPKey: ");

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
                FunctionCommon.saveSimpleFile(defaultFilePath, part.getInputStream());
            }
            /* part.write(fileName); */
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }


        com.yuyan.utils.Log.i(TAG, "[Coder Wu] postUploadHDCPKey: end");
    }

    public static void getHDCPKeyList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        com.yuyan.utils.Log.i(TAG, "[Coder Wu] getHDCPKeyList: ");
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

    public static void postCommandBurnHDCPKeyLocal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        com.yuyan.utils.Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        PostBurnHDCPKeyPreparation preparation = preHandlePostCommandBurnHDCPKey(request, response);
        String commandDataName = preparation.commandDataName;
        String valueCodeString = preparation.valueCodeString;
        byte[] keyPayload = preparation.keyPayload;
        int keyPayloadLen = preparation.keyPayloadLen;
        if (!preparation.isOk) {
            String reason = preparation.reason;
            FunctionCommon.sendSimpleStatReply(commandDataName, reason, response);
            com.yuyan.utils.Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: reason = " + reason);
            return;
        }

        byte[] sendBytes = ByteUtils.hexStringToBytes(valueCodeString);
        Serialport serialport = Serialport.getInstance();
        serialport.write(sendBytes, sendBytes.length);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serialport.write(keyPayload, keyPayloadLen);
        FunctionCommon.sendSimpleStatReply(commandDataName, Constant.OK, response);
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: keyPayload = " + ByteUtils.hexBytesToString(keyPayload, keyPayloadLen));
    }

    public static void postCommandBurnHDCPKeyRemote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        com.yuyan.utils.Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyRemote: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        PostBurnHDCPKeyPreparation preparation = preHandlePostCommandBurnHDCPKey(request, response);
        String commandDataName = preparation.commandDataName;
        String valueCodeString = preparation.valueCodeString;
        byte[] keyPayload = preparation.keyPayload;
        int keyPayloadLen = preparation.keyPayloadLen;
        if (!preparation.isOk) {
            String reason = preparation.reason;
            FunctionCommon.sendSimpleStatReply(commandDataName, reason, response);
            com.yuyan.utils.Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyRemote: reason = " + reason);
            return;
        }

        Socket socket = SocketPlugin.INSTANCE.getSocket(Root.getThreadLocalSocket());
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(ByteUtils.hexStringToBytes(valueCodeString));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        outputStream.write(keyPayload, 0, keyPayloadLen);
        FunctionCommon.sendSimpleStatReply(commandDataName, Constant.OK, response);
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyRemote: keyPayload = " + ByteUtils.hexBytesToString(keyPayload, keyPayloadLen));
    }






    /*------------------------------------------------------------------*/
    static class PostBurnHDCPKeyPreparation {
        boolean isOk = false;
        String reason;
        String commandDataName;
        String valueCodeString;
        byte[] keyPayload;
        int keyPayloadLen;

        public PostBurnHDCPKeyPreparation(boolean isOk, String reason, String commandDataName, String valueCodeString, byte[] keyPayload, int keyPayloadLen) {
            this.isOk = isOk;
            this.reason = reason;
            this.commandDataName = commandDataName;
            this.valueCodeString = valueCodeString;
            this.keyPayload = keyPayload;
            this.keyPayloadLen = keyPayloadLen;
        }
    }

    private static PostBurnHDCPKeyPreparation preHandlePostCommandBurnHDCPKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        CommandHexCode commandHexCode = null;
        for (Command command : CommandRepository.INSTANCE.commandList.commands) {
            if (command.commandData.name.equals(commandDataName)) {
                commandHexCode = command.commandData.commandHexCode;
                break;
            }
        }

        if (commandHexCode == null) {
            return new PostBurnHDCPKeyPreparation(false, Constant.COMMAND_STRING_NO_MATCH, commandDataName, valueCodeString, null, 0);
        }

        String valueStr = CommandUtils.getValueString(commandHexCode, valueCodeString);
        int valueInt = Integer.parseInt(valueStr);
        if (valueInt < 0 || valueInt >= Constant.HDCP_KEY_FILE_LIST.length) {
            return new PostBurnHDCPKeyPreparation(false, Constant.COMMAND_STRING_ARGS_ERR, commandDataName, valueCodeString, null, 0);
        }

        String fileName = Constant.HDCP_KEY_UPLOAD_PATH + Constant.HDCP_KEY_FILE_LIST[valueInt];
        byte[] keyPayload = readHDCPKeyFile_lock(fileName);
        int keyPayloadLen = ((keyPayload[0] & 0x00FF)<<8) | (keyPayload[1] & 0x00FF);
        if (keyPayloadLen <= 0 || keyPayloadLen >= MAX_HDCP_KEY_LEN) {
            Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: keyPayloadLen = " + keyPayloadLen);
            return new PostBurnHDCPKeyPreparation(false, Constant.COMMAND_STRING_ARGS_ERR, commandDataName, valueCodeString, null, 0);
        }

        return new PostBurnHDCPKeyPreparation(true, "", commandDataName, valueCodeString, keyPayload, keyPayloadLen);
    }


    private static byte[] readHDCPKeyFile_lock(String fillNameFullPath) throws IOException {
        com.yuyan.utils.Log.i(TAG, "[Coder Wu] readHDCPKeyFile_lock: fillNameFullPath = " + fillNameFullPath);

        byte[] keyPayload = new byte[MAX_HDCP_KEY_LEN];
        keyPayload[0] = 0x00;
        keyPayload[1] = 0x00;

        synchronized (HDCPKeyFunc.class) {
            File file = new File(fillNameFullPath);
            if (!file.exists()) {
                Log.i(TAG, "[Coder Wu] readHDCPKeyFile_lock: file not exist " + fillNameFullPath);
                return keyPayload;
            }


            int readLen = 0;
            byte[] buff = new byte[MAX_HDCP_KEY_LEN];
            int keyPayloadLen = 2;

            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                readLen = fileInputStream.read(buff);
                if (readLen == -1) {
                    break;
                }
                System.arraycopy(buff, 0, keyPayload, keyPayloadLen, readLen);

                keyPayloadLen += readLen;
            }

            byte high = (byte) (keyPayloadLen >> 8);
            byte low = (byte) (keyPayloadLen & 0x00FF);

            String highLowStr = ByteUtils.hexBytesToString(new byte[]{high, low}, 2);
            Log.i(TAG, "[Coder Wu] readHDCPKeyFile_lock: highLowStr = " + highLowStr);

            keyPayload[0] = high;
            keyPayload[1] = low;

            return keyPayload;
        }

    }

    /*------------------------------------------------------------------*/

    static class Log {
        static boolean DEBUG = true;

        public static void i(String TAG, String msg) {
            if (DEBUG) {
                com.yuyan.utils.Log.i(TAG, msg);
            }
        }
    }

    /*------------------------------------------------------------------*/
}
