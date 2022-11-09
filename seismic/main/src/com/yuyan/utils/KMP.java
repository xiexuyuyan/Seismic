package com.yuyan.utils;

public class KMP {
    private static native int nativeFind(String pat, String src);

    public static int find(String pat, String src) {
        return nativeFind(pat, src);
    }
    
    static {
        System.loadLibrary("libKMP");
    }
}
