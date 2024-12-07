package com.example.demo.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface DataColumn<T> extends Cloneable {
    String getColumnName();
    void setColumnName(String name);
    T parse(String value);
    String toString();
    DataColumn<T> clone();
    int compareTo(DataColumn<T> other);
    DataColumn<T> add(DataColumn<T> other);

    // Методы для записи и чтения данных в/из двоичного потока
    void writeToStream(DataOutputStream out) throws IOException;
    void readFromStream(DataInputStream in) throws IOException;
}

