package com.example.demo.model;

public class StringColumn implements DataColumn<String> {
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
}

