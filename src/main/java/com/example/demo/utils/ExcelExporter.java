package com.example.demo.utils;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {
    public static void exportToExcel(Table table, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Table");

        // Запись заголовков
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < table.getColumnNames().size(); i++) {
            headerRow.createCell(i).setCellValue(table.getColumnNames().get(i));
        }

        // Запись данных
        for (int i = 0; i < table.getRows().size(); i++) {
            Row row = sheet.createRow(i + 1);
            TableRow tableRow = table.getRows().get(i);
            for (int j = 0; j < tableRow.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableRow.getColumn(j).toString());
            }
        }

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        workbook.close();
    }
}

