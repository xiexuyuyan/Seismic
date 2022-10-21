package com.yuyan.driver.serialport;

import com.yuyan.utils.Log;

public class SerialportRepository {
    private static final String TAG = "SerialportRepository";

    private static final String SERIALPORT_NAME = "COM6";

    public static void switchSerialport(boolean status) {
        Serialport serialport = Serialport.getInstance();

        boolean before = serialport.getStatus();
        if (status && !before) {
            serialport.open(SERIALPORT_NAME);
            serialport.setReadTimeout(3000);
        } else if (!status && before) {
            serialport.close();
        }
        boolean after = serialport.getStatus();

        Log.i(TAG, "[Coder Wu] switchSerialport: " + before + " --> " + after);
    }
}
