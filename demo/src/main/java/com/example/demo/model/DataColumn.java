package com.example.demo.model;

public interface DataColumn<T> extends Cloneable {
    String getColumnName();
    void setColumnName(String name);
    T parse(String value);
    String toString();
    DataColumn<T> clone();
    int compareTo(DataColumn<T> other);
    DataColumn<T> add(DataColumn<T> other);
}

