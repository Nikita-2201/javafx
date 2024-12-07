package com.example.demo.core;

import com.example.demo.model.DataColumn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableRow implements CustomSerializable {
    private List<DataColumn<?>> columns; // Список столбцов в строке

    public TableRow() {
        columns = new ArrayList<>();
    }

    // Добавление столбца
    public void addColumn(DataColumn<?> column) {
        columns.add(column);
    }

    // Новый метод для получения всех столбцов
    public List<DataColumn<?>> getColumns() {
        return columns;
    }

    // Получение столбца по индексу
    public DataColumn<?> getColumn(int index) {
        return columns.get(index);
    }

    // Преобразование строки в текст
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (DataColumn<?> column : columns) {
            builder.append(column.toString()).append("\t");
        }
        return builder.toString();
    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        for (DataColumn<?> column : columns) {
            column.writeToStream(out); // Записываем каждую колонку
        }
    }

    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        for (DataColumn<?> column : columns) {
            column.readFromStream(in); // Читаем каждую колонку
        }
    }
}

