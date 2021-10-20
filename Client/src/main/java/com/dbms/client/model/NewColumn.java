package com.dbms.client.model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.Objects;

public class NewColumn {
    TextField columnName;
    ComboBox<String> dataTypes;
    CheckBox isPrimaryKey;
    CheckBox isUniqueKey;
    CheckBox isNull;


    public NewColumn() {
        columnName=new TextField();

        dataTypes=new ComboBox<>();
        dataTypes.getItems().add("int");
        dataTypes.getItems().add("varchar(255)");
//        dataTypes.setConverter(
//                new StringConverter<>() {
//                    @Override
//                    public String toString(String string) {
//                        return Objects.requireNonNullElse(string, "");
//                    }
//
//                    @Override
//                    public String fromString(String s) {
//                        try {
//                            return s;
//                        } catch (NumberFormatException e) {
//                            return null;
//                        }
//                    }
//                });
//        dataTypes.setEditable(true);

        isPrimaryKey=new CheckBox();
        isUniqueKey =new CheckBox();
        isNull=new CheckBox();
    }

    public TextField getColumnName() {
        return columnName;
    }

    public void setColumnName(TextField columnName) {
        this.columnName = columnName;
    }

    public ComboBox<String> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(ComboBox<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public CheckBox getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(CheckBox isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public CheckBox getIsUniqueKey() {
        return isUniqueKey;
    }

    public void setIsUniqueKey(CheckBox isUniqueKey) {
        this.isUniqueKey = isUniqueKey;
    }

    public CheckBox getIsNull() {
        return isNull;
    }

    public void setIsNull(CheckBox isNull) {
        this.isNull = isNull;
    }
}
