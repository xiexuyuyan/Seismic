package com.yuyan.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

public class Log {

    public static final int INFO = 0;
    public static final int DEBUG = 1;
    public static final int WARN= 2;
    public static final int ERROR = 3;

    public static int level = 0;

    private Log(){ }

    public static void i(String TAG, String msg){ if (INFO  >= level) { System.out.println("[" + TAG + "] " + msg); } }
    public static void d(String TAG, String msg){ if (DEBUG >= level) { System.out.println("[" + TAG + "] " + msg); } }
    public static void w(String TAG, String msg){ if (WARN  >= level) { System.out.println("[" + TAG + "] " + msg); } }
    public static void w(String TAG, String msg, Exception e){ if (WARN  >= level) { System.out.println("[" + TAG + "] " + msg + ", " + e + "."); } }
    public static void e(String TAG, String msg){ if (ERROR >= level) { System.out.println("[" + TAG + "] " + msg); } }

    static Throwable tr = new Throwable();

    public static String getStackTraceString() {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

}