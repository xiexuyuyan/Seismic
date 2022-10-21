package com.yuyan.driver.remote;

import com.yuyan.utils.Log;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response {
    private static final String TAG = "Response";

    public static void dispatch(ServletRequest req, ServletResponse res) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        Log.i(TAG, "[Coder Wu] dispatch: req.getRequestURI() = " + request.getRequestURI());
        String[] uriArray = request.getRequestURI().split("/");

        String action = uriArray.length > 2 ? uriArray[2] : null;
        if (action != null) {
            try {
                consume(action, request, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void consume(
            String action
            , HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        Log.i(TAG, "[Coder Wu] consume: action = " + action);

        switch (action) {
            case Constant.GET_COMMAND_ALL:
                Function.getCommandAll(request, response);
                break;
            case Constant.POST_COMMAND_REMOTE:
                Function.postCommandRemote(request, response);
                break;
            case Constant.POST_COMMAND_LOCAL:
                Function.postCommandLocal(request, response);
                break;
            case Constant.POST_SWITCH_SERIALPORT:
                Function.postSwitchSerialport(request, response);
                break;
            case Constant.POST_UPLOAD_HDCP_KEY:
                Function.postUploadHDCPKey(request, response);
                break;
            case Constant.GET_HDCP_KEY_LIST:
                Function.getHDCPKeyList(request, response);
                break;
        }
    }
}
