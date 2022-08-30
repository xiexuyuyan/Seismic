package org.yuyan.room.annotation.processor.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityType {
    String tableName;
    String primaryKey;
    List<ColumnType> columns;


    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public List<ColumnType> getColumns() {
        return columns;
    }

    public EntityType() {
        columns = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tableName: ").append(tableName)
                    .append(", primaryKey: ").append(primaryKey)
                .append(", columns: {");
        for (ColumnType column : columns) {
            stringBuilder.append(column.toString());
            stringBuilder.append(", ");
        }
        String ret = stringBuilder.toString();
        ret = stringBuilder.substring(0, ret.length() - 2);
        return ret + "}";
    }
}