package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.Root;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.local.CommandResolver;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.model.Command;
import com.yuyan.model.CommandHexCode;
import com.yuyan.utils.Log;
import com.yuyan.web.model.SimpleArrayStat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.Socket;

public class HDCPKeyFunc {
    private static final String TAG = "HDCPKeyFunc";


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
                FunctionCommon.saveSimpleFile(defaultFilePath, part.getInputStream());
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

    public static void postCommandBurnHDCPKeyLocal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        byte[] sendBytes = FunctionCommon.sendStringToByte(valueCodeString);
        Serialport serialport = Serialport.getInstance();
        serialport.write(sendBytes, sendBytes.length);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CommandHexCode commandHexCode = null;
        for (Command command : CommandRepository.INSTANCE.commandList.commands) {
            if (command.commandData.name.equals(commandDataName)) {
                commandHexCode = command.commandData.commandHexCode;
            }
        }

        if (commandHexCode == null) {
            return;
        }

        String valueStr = CommandResolver.getValueString(commandHexCode, valueCodeString);
        int valueInt = Integer.parseInt(valueStr);
        String fileName = "";
        switch (valueInt) {
            case 0:
                fileName = "Hdcp14RX0.enc.factory-user.enc";
            case 1:
                fileName = "Hdcp14TX0.enc.factory-user.enc";
                break;
            case 2:
                fileName = "Hdcp22RX0.enc.factory-user.enc";
                break;
            case 3:
                fileName = "Hdcp22TX0.enc.factory-user.enc";
                break;
        }
        File file = new File(Constant.HDCP_KEY_UPLOAD_PATH + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        int readLen = 0;
        byte[] buff = new byte[1024];
        int keyPayloadLen = 2;
        byte[] keyPayload = new byte[2048];
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

        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: " + CommandResolver.receiveByteToString(new byte[]{high}, 1));
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: " + CommandResolver.receiveByteToString(new byte[]{low}, 1));

        keyPayload[0] = high;
        keyPayload[1] = low;


        int keyPayloadLenT = ((keyPayload[0] & 0x00FF)<<8) | (keyPayload[1] & 0x00FF);
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: keyPayloadLen = " + keyPayloadLen);
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: keyPayloadLenT = " + keyPayloadLenT);
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyLocal: " + CommandResolver.receiveByteToString(keyPayload, keyPayloadLen));

        serialport.write(keyPayload, keyPayloadLen);
    }

    public static void postCommandBurnHDCPKeyRemote(HttpServletRequest request, HttpServletResponse response) {
        Log.i(TAG, "[Coder Wu] postCommandBurnHDCPKeyRemote: ");
        FunctionCommon.printParametersFromHttpRequest(request);

    }






    /*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*/
}
