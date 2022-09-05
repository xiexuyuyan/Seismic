package com.yuyan.repository;

public class DecodeUtils {

    // input-format     String["00", "FF"] | char-int:[00, FF]
    // send-format      int:[0xFF_FF_FF_00, 0xFF_FF_FF_FF] 32bit
    //                  int:[-0x80, 0x7F]
    //                  int[-0x80, 0x7F] == byte[-0x80, 0x7F]
    // transport-format byte:[-0x80, 0x7F]
    //                  byte[-0x80, 0x7F] == int[-0x80, 0x7F]
    //                  int:[-0x80, 0x7F]
    // receive-format   int:[0xFF_FF_FF_00, 0xFF_FF_FF_FF] 32bit
    // output-format    String["00", "FF"] | char-int:[00, FF]

    public static int inputFormat(int d) {
        return d & 0x00_00_00_FF;
    }

    public static int inputFormat(String s) {
        if (s.matches("[0-9A-Fa-f]{1,2}")) {
            return (Integer.parseInt(s, 16) & 0x00_00_00_FF);
        } else {
            return 0x00_00_00_00;
        }
    }

    public static byte sendFormat(int d) {
        return (byte) ((0x00_00_00_FF & d) - 0x80);
    }

    public static int receiveFormat(byte b) {
        return (b + 0x80) & 0x00_00_00_FF;
    }

    public static int outputIntFormat(int d) {
        return (0x00_00_FF_FF & d);
    }

    public static String outputStringFormat(int d) {
        int high = (0x00_00_00_F0 & d) >> 4;
        int low = 0x00_00_00_0F & d;

        char[] HEX_CODE = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7'
                , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        return HEX_CODE[high] + "" + HEX_CODE[low];
    }
}
