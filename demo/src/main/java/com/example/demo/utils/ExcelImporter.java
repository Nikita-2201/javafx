package com.example.demo.utils;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;
import com.example.demo.model.StringColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelImporter {
    public static Table importFromExcel(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        Table table = new Table();

        // Чтение заголовков
        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            table.addColumn(cell.getStringCellValue(), new StringColumn(cell.getStringCellValue()));
        }

        // Чтение строк
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            TableRow tableRow = new TableRow();

            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    StringColumn column = new StringColumn("");
                    column.parse(cell.toString());
                    tableRow.addColumn(column); // Добавляем столбец
                }
            }
            table.addRow(tableRow);
        }

        workbook.close();
        return table;
    }
}

