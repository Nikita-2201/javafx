package com.example.demo.core;
import com.example.demo.core.TableRow;
import com.example.demo.model.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Table implements CustomSerializable {
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


    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeInt(columnNames.size()); // Количество столбцов
        out.writeInt(rows.size());        // Количество строк

        // Записываем дескрипторы столбцов
        for (int i = 0; i < columnNames.size(); i++) {
            out.writeUTF(columnNames.get(i)); // Имя столбца
            DataColumn<?> prototype = prototypeRow.getColumn(i);
            out.writeUTF(prototype.getClass().getSimpleName()); // Тип столбца
        }

        // Записываем данные строк
        for (TableRow row : rows) {
            for (int i = 0; i < columnNames.size(); i++) {
                DataColumn<?> column = row.getColumn(i);
                System.out.println("Запись строки, столбец " + (i + 1) + ": " + column.toString());
                column.writeToStream(out); // Записываем данные столбца
            }
        }
    }


    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        int columnCount = in.readInt(); // Читаем количество столбцов
        int rowCount = in.readInt();    // Читаем количество строк

        columnNames.clear();
        prototypeRow = new TableRow();

        // Читаем дескрипторы столбцов
        for (int i = 0; i < columnCount; i++) {
            String columnName = in.readUTF();
            String columnType = in.readUTF();
            DataColumn<?> columnPrototype;

            switch (columnType) {
                case "IntegerColumn":
                    columnPrototype = new IntegerColumn(columnName);
                    break;
                case "DoubleColumn":
                    columnPrototype = new DoubleColumn(columnName);
                    break;
                case "DateColumn":
                    columnPrototype = new DateColumn(columnName, "yyyy-MM-dd");
                    break;
                case "GpsCoordinatesColumn":
                    columnPrototype = new GpsCoordinatesColumn(columnName);
                    break;
                default:
                    columnPrototype = new StringColumn(columnName);
            }

            columnNames.add(columnName);
            prototypeRow.addColumn(columnPrototype);
        }

        // Читаем данные строк
        rows.clear();
        for (int i = 0; i < rowCount; i++) {
            TableRow row = new TableRow();
            for (int j = 0; j < columnCount; j++) {
                DataColumn<?> prototype = prototypeRow.getColumn(j);
                DataColumn<?> column = prototype.clone();
                System.out.println("Чтение строки " + (i + 1) + ", столбца " + (j + 1) + ": ожидается тип " + prototype.getClass().getSimpleName());
                column.readFromStream(in); // Читаем данные столбца
                System.out.println("Прочитано значение: " + column.toString());
                row.addColumn(column);
            }
            rows.add(row);
        }
    }

}
