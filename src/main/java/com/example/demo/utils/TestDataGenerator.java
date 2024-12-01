package com.example.demo.utils;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;
import com.example.demo.model.*;

import java.time.LocalDate;
import java.util.Random;

public class TestDataGenerator {

    public static Table generateRandomTable(int rowCount, int columnCount) {
        Table table = new Table();
        Random random = new Random();

        // Добавляем столбцы с разными типами данных
        for (int i = 0; i < columnCount; i++) {
            String columnName = "Column" + (i + 1);
            DataColumn<?> prototype;

            // Генерируем случайный тип данных для столбца
            int randomType = random.nextInt(5); // 0 = String, 1 = Integer, 2 = Double, 3 = Date, 4 = GPS Coordinates
            switch (randomType) {
                case 1:
                    prototype = new IntegerColumn(columnName);
                    break;
                case 2:
                    prototype = new DoubleColumn(columnName);
                    break;
                case 3:
                    prototype = new DateColumn(columnName, "yyyy-MM-dd");
                    break;
                case 4:
                    prototype = new GpsCoordinatesColumn(columnName);
                    break;
                default:
                    prototype = new StringColumn(columnName);
            }

            table.addColumn(columnName, prototype);
        }

        // Генерируем строки
        for (int i = 0; i < rowCount; i++) {
            TableRow row = new TableRow();

            for (int j = 0; j < columnCount; j++) {
                DataColumn<?> prototype = table.getPrototypeRow().getColumn(j);
                DataColumn<?> column = prototype.clone();

                // Заполняем случайными значениями в зависимости от типа данных
                if (column instanceof IntegerColumn) {
                    column.parse(String.valueOf(random.nextInt(1000)));
                } else if (column instanceof DoubleColumn) {
                    column.parse(String.format("%.2f", random.nextDouble() * 1000));
                } else if (column instanceof DateColumn) {
                    column.parse("2024-" + (random.nextInt(12) + 1) + "-" + (random.nextInt(28) + 1));
                } else if (column instanceof GpsCoordinatesColumn) {
                    double latitude = -90 + random.nextDouble() * 180; // Случайная широта
                    double longitude = -180 + random.nextDouble() * 360; // Случайная долгота
                    column.parse(latitude + ", " + longitude); // Формат GPS
                } else {
                    column.parse("Value" + random.nextInt(100));
                }

                row.addColumn(column);
            }

            table.addRow(row);
        }

        return table;
    }

}

