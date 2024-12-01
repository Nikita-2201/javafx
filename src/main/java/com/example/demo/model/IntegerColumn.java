package com.example.demo.model;

public class IntegerColumn implements DataColumn<Integer> {
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
}