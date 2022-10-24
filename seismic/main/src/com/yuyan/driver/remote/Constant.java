package com.yuyan.driver.remote;

public class Constant {
    public static final String OK = "OK";


    public static final String COMMAND_STRING_NO_MATCH = "COMMAND_STRING_NO_MATCH";
    public static final String COMMAND_STRING_ARGS_ERR = "COMMAND_STRING_ARGS_ERR";

    public static final String GET_COMMAND_ALL = "GET_COMMAND_ALL";
    public static final String POST_COMMAND_REMOTE = "POST_COMMAND_REMOTE";
    public static final String POST_COMMAND_LOCAL = "POST_COMMAND_LOCAL";
    public static final String POST_SWITCH_SERIALPORT = "POST_SWITCH_SERIALPORT";

    public static final String POST_UPLOAD_HDCP_KEY = "POST_UPLOAD_HDCP_KEY";
    public static final String GET_HDCP_KEY_LIST = "GET_HDCP_KEY_LIST";
    public static final String POST_COMMAND_BURN_HDCP_KEY_LOCAL = "POST_COMMAND_BURN_HDCP_KEY_LOCAL";
    public static final String POST_COMMAND_BURN_HDCP_KEY_REMOTE = "POST_COMMAND_BURN_HDCP_KEY_REMOTE";

    public static final String HDCP_KEY_UPLOAD_PATH = "D://TMP/HDCP/";
    public static final String[] HDCP_KEY_FILE_LIST = new String[] {
            "Hdcp14RX0.enc.factory-user.enc"
            , "Hdcp14TX0.enc.factory-user.enc"
            , "Hdcp22RX0.enc.factory-user.enc"
            , "Hdcp22TX0.enc.factory-user.enc"
    };

    public static final String SERIALPORT_READ_TIMEOUT = "timeout";
    public static final String SERIALPORT_READ_NO_MATCH = "no match";
}
