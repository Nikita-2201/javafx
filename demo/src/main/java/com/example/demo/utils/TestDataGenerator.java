package com.example.demo.utils;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;
import com.example.demo.model.DataColumn;
import com.example.demo.model.DateColumn;
import com.example.demo.model.IntegerColumn;
import com.example.demo.model.StringColumn;

import java.time.LocalDate;
import java.util.Random;

public class TestDataGenerator {

    public static Table generateTestTable(int rows, int columns) {
        Table table = new Table();

        // Добавляем столбцы с прототипами
        for (int i = 0; i < columns; i++) {
            String columnName = "Column" + (i + 1);
            //TODO: Пример: первый столбец - строки, остальные - числа
            if (i == 0) {
                table.addColumn(columnName, new StringColumn(columnName));
            } else {
                table.addColumn(columnName, new IntegerColumn(columnName));
            }
        }

        Random random = new Random();

        // Генерируем строки
        for (int i = 0; i < rows; i++) {
            TableRow row = new TableRow();

            for (int j = 0; j < columns; j++) {
                if (j == 0) {
                    // Столбец строк
                    StringColumn stringColumn = new StringColumn("");
                    stringColumn.parse("Value" + random.nextInt(100));
                    row.addColumn(stringColumn); // Добавляем объект столбца
                } else {
                    // Столбцы чисел
                    IntegerColumn integerColumn = new IntegerColumn("");
                    integerColumn.parse(String.valueOf(random.nextInt(1000)));
                    row.addColumn(integerColumn); // Добавляем объект столбца
                }
            }

            table.addRow(row);
        }

        return table;
    }
}

