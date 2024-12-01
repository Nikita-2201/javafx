package com.example.demo.core;
import com.example.demo.core.TableRow;
import com.example.demo.model.DataColumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Table  {
    private List<String> columnNames;
    private TableRow prototypeRow;
    private List<TableRow> rows;

    public Table() {
        columnNames = new ArrayList<>();
        rows = new ArrayList<>();
        prototypeRow = new TableRow(); // Инициализация prototypeRow
    }

    public void addColumn(String name, DataColumn<?> prototype) {
        columnNames.add(name);
        prototypeRow.addColumn(prototype); // Добавляем столбец в строку-прототип
    }

    public void addRow(TableRow row) {
        rows.add(row);
    }

    public void sortByColumn(int columnIndex) {
        rows.sort(Comparator.comparing(row -> row.getColumn(columnIndex).toString()));
    }

    public TableRow getPrototypeRow() {
        return prototypeRow; // Возвращаем строку-прототип
    }


    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String name : columnNames) {
            builder.append(name).append("\t");
        }
        builder.append("\n");
        for (TableRow row : rows) {
            builder.append(row.toString()).append("\n");
        }
        return builder.toString();
    }
}
