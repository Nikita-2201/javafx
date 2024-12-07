package com.example.demo.model;

import com.example.demo.core.CustomSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerColumn implements DataColumn<Integer>, CustomSerializable {
    private String columnName;
    private Integer value;

    public IntegerColumn(String columnName) {
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
    public Integer parse(String value) {
        this.value = Integer.parseInt(value);
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public IntegerColumn clone() {
        IntegerColumn copy = new IntegerColumn(this.columnName);
        copy.value = this.value;
        return copy;
    }

    @Override
    public int compareTo(DataColumn<Integer> other) {
        return Integer.compare(this.value, other.parse(""));
    }

    @Override
    public DataColumn<Integer> add(DataColumn<Integer> other) {
        IntegerColumn result = new IntegerColumn(this.columnName);
        result.value = this.value + other.parse("");
        return result;
    }


    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeUTF(columnName); // Записываем имя столбца
        out.writeInt(value);     // Записываем значение
    }

    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        columnName = in.readUTF(); // Читаем имя столбца
        value = in.readInt();      // Читаем значение
    }
}