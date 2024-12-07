package com.example.demo.model;

import com.example.demo.core.CustomSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringColumn implements DataColumn<String>, CustomSerializable {
    private String columnName;
    private String value;

    public StringColumn(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public void setColumnName(String name) {
        this.columnName = name;
    }

    @Override
    public String parse(String value) {
        this.value = value;
        return this.value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public StringColumn clone() {
        StringColumn copy = new StringColumn(this.columnName);
        copy.value = this.value;
        return copy;
    }

    @Override
    public int compareTo(DataColumn<String> other) {
        return this.value.compareTo(other.parse(""));
    }

    @Override
    public DataColumn<String> add(DataColumn<String> other) {
        StringColumn result = new StringColumn(this.columnName);
        result.value = this.value + other.parse("");
        return result;
    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeUTF(columnName); // Записываем имя столбца
        out.writeUTF(value);      // Записываем значение строки
    }

    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        columnName = in.readUTF(); // Читаем имя столбца
        value = in.readUTF();      // Читаем значение строки
    }
}

