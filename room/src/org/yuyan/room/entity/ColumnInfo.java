package org.yuyan.room.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @param name
 * it's interfacing to a default rule
 * that we indicate a SQL field to be named with
 * "database name" + "_" + "field name"
 * etc: user_name, user_mail, ...
 * */
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ColumnInfo {
    String name();
}
