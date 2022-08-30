package org.yuyan.room.annotation.processor.utils;

/**
 * Created by: As10970 2021/7/2 10:45.
 * Project: Vicar.
 */

public class StringUtils {

    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}