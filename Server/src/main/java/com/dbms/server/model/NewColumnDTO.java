package com.dbms.server.model;

import java.io.Serializable;

public class NewColumnDTO implements Serializable {
    String columnName;
    String dataType;
    boolean isPrimaryKey;
    boolean isUniqueKey;
    boolean isNull;

    public NewColumnDTO(String columnName, String dataType, boolean isPrimaryKey, boolean isUniqueKey, boolean isNull) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.isUniqueKey = isUniqueKey;
        this.isNull = isNull;
    }

    public NewColumnDTO() {
    }


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isUniqueKey() {
        return isUniqueKey;
    }

    public void setUniqueKey(boolean uniqueKey) {
        isUniqueKey = uniqueKey;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    @Override
    public String toString() {
        return "NewColumnDTO{" +
                "columnName='" + columnName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isUniqueKey=" + isUniqueKey +
                ", isNull=" + isNull +
                '}';
    }
}
