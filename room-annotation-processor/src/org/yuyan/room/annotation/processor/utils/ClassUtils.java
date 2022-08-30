package org.yuyan.room.annotation.processor.utils;

import com.squareup.javapoet.ClassName;

/**
 * Created by: As10970 2021/7/5 10:04.
 * Project: Vicar.
 */

public class ClassUtils {
    public static String classGetter(String classTypeName){
        String[] s = classTypeName.split("\\.");
        return "get" + StringUtils.upperCase(s[s.length - 1]);
    }
}