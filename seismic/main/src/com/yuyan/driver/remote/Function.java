package com.yuyan.driver.remote;

import com.google.gson.Gson;
import com.yuyan.Root;
import org.yuyan.command.utils.ByteUtils;
import com.yuyan.driver.local.CommandRepository;
import com.yuyan.driver.serialport.Serialport;
import com.yuyan.driver.serialport.SerialportRepository;
import com.yuyan.utils.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Function {
    private static final String TAG = "Function";


    public static void getCommandAll(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(CommandRepository.INSTANCE.commandList);
        response.getWriter().println(jsonStr);
    }

    public static void postCommandRemote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postCommandRemote: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        Socket socket = SocketPlugin.INSTANCE.getSocket(Root.getThreadLocalSocket());
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(ByteUtils.hexStringToBytes(valueCodeString));

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
                FunctionCommon.sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_TIMEOUT, response);
                return;
            }
        }

        if (readLen == -1) {
            Log.i(TAG, "[Coder Wu] postCommand: " +
                    "we received -1 in socket input, so closed the socket");
            FunctionCommon.sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_NO_MATCH, response);
            socket.close();
            return;
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + ByteUtils.hexBytesToString(buff, readLen));

        FunctionCommon.sendCommandRevReply(commandDataName, buff, readLen, response);
    }


    public static void postCommandLocal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postCommandLocal: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        String commandDataName = request.getParameterMap().get("command_data_name")[0];
        String valueCodeString = request.getParameterMap().get("value")[0];

        byte[] sendBytes = ByteUtils.hexStringToBytes(valueCodeString);
        Serialport serialport = Serialport.getInstance();
        serialport.write(sendBytes, sendBytes.length);

        byte[] buff = new byte[1024];
        int readLen = serialport.read(buff);

        if (readLen == -1) {
            FunctionCommon.sendSimpleStatReply(commandDataName, Constant.SERIALPORT_READ_TIMEOUT, response);
            return;
        }

        Log.i(TAG, "[Coder Wu] postCommand: " +
                "readLen = " + readLen
                + ", " + ByteUtils.hexBytesToString(buff, readLen));
        FunctionCommon.sendCommandRevReply(commandDataName, buff, readLen, response);
    }

    public static void postSwitchSerialport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] postSwitchSerialport: ");
        FunctionCommon.printParametersFromHttpRequest(request);

        boolean status = request.getParameterMap().get("status")[0].equals("true");
        SerialportRepository.switchSerialport(status);

        FunctionCommon.sendSimpleStatReply(Constant.POST_SWITCH_SERIALPORT, status+"", response);
    }









    /*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*/
}
