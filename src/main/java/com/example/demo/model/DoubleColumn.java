package com.example.demo.model;


import com.example.demo.core.CustomSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleColumn implements DataColumn<Double>, CustomSerializable {
    private String columnName; // Имя столбца
    private Double value;      // Значение в столбце

    // Конструктор
    public DoubleColumn(String columnName) {
        this.columnName = columnName;
        this.value = 0.0; // Значение по умолчанию
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
    public Double parse(String value) {
        try {
            this.value = Double.parseDouble(value); // Парсинг строки в double
        } catch (NumberFormatException e) {
            this.value = 0.0; // Если парсинг не удался, устанавливаем 0.0
        }
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value); // Преобразование значения в строку
    }

    @Override
    public DoubleColumn clone() {
        DoubleColumn copy = new DoubleColumn(this.columnName);
        copy.value = this.value; // Клонируем значение
        return copy;
    }

    @Override
    public int compareTo(DataColumn<Double> other) {
        return Double.compare(this.value, other.parse("")); // Сравнение значений
    }

    @Override
    public DataColumn<Double> add(DataColumn<Double> other) {
        DoubleColumn result = new DoubleColumn(this.columnName);
        result.value = this.value + other.parse(""); // Сложение значений
        return result;
    }

    // Дополнительно: Getter для значения
    public Double getValue() {
        return value;
    }

    // Дополнительно: Setter для значения
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeUTF(columnName); // Записываем имя столбца
        out.writeDouble(value);   // Записываем значение как double
    }

    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        columnName = in.readUTF(); // Читаем имя столбца
        value = in.readDouble();   // Читаем значение как double
    }
}
