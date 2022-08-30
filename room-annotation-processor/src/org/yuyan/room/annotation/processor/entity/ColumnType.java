package org.yuyan.room.annotation.processor.entity;

import javax.lang.model.type.TypeMirror;

public class ColumnType {
    String columnName;// annotation value in columnInfo
    String varName;
    TypeMirror varType;

    public String getColumnName() {
        return columnName;
    }

    public String getVarName() {
        return varName;
    }

    public TypeMirror getVarType() {
        return varType;
    }

    public ColumnType(String columnName, String varName, TypeMirror varType) {
        this.columnName = columnName;
        this.varName = varName;
        this.varType = varType;
    }

    @Override
    public String toString() {
        return "columnName: " + columnName
                + ", varName: " + varName
                + ", varClassName: " + varType;
    }
}