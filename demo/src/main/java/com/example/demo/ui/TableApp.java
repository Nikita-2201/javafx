package com.example.demo.ui;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;
import com.example.demo.model.DataColumn;
import com.example.demo.utils.ExcelExporter;
import com.example.demo.utils.ExcelImporter;
import com.example.demo.utils.TestDataGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Table table = TestDataGenerator.generateTestTable(10, 5);

        TableView<TableRow> tableView = new TableView<>();
        for (int i = 0; i < table.getColumnNames().size(); i++) {
            final int colIndex = i;
            TableColumn<TableRow, String> column = new TableColumn<>(table.getColumnNames().get(i));
            column.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getColumn(colIndex).toString()));
            tableView.getColumns().add(column);
        }
        tableView.getItems().addAll(table.getRows());

        // Панель управления
        HBox controlPanel = new HBox();
        controlPanel.setSpacing(10);
        controlPanel.setPadding(new Insets(10));

        Button addRowButton = new Button("Добавить строку");
        Button deleteRowButton = new Button("Удалить строку");
        Button saveButton = new Button("Сохранить");
        Button loadButton = new Button("Загрузить");
        TextField filterField = new TextField();
        filterField.setPromptText("Введите значение для фильтрации");
        Button filterButton = new Button("Фильтровать");

        controlPanel.getChildren().addAll(addRowButton, deleteRowButton, saveButton, loadButton, filterField, filterButton);

        addRowButton.setOnAction(e -> {
            Dialog<TableRow> dialog = new Dialog<>();
            dialog.setTitle("Добавить строку");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            List<TextField> fields = new ArrayList<>();
            for (String columnName : table.getColumnNames()) {
                TextField field = new TextField();
                field.setPromptText(columnName);
                fields.add(field);
                grid.add(new Label(columnName), 0, fields.size() - 1);
                grid.add(field, 1, fields.size() - 1);
            }

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    TableRow newRow = new TableRow();
                    for (int i = 0; i < fields.size(); i++) {
                        String value = fields.get(i).getText();
                        DataColumn<?> prototype = table.getPrototypeRow().getColumn(i); // Получаем столбец из строки-прототипа
                        DataColumn<?> column = prototype.clone();
                        column.parse(value);
                        newRow.addColumn(column);
                    }
                    return newRow;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newRow -> {
                table.addRow(newRow);
                tableView.getItems().add(newRow); // Обновляем таблицу
            });
        });

        deleteRowButton.setOnAction(e -> {
            TableRow selectedRow = tableView.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                table.getRows().remove(selectedRow);
                tableView.getItems().remove(selectedRow); // Обновляем таблицу
            }
        });

        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить таблицу");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    ExcelExporter.exportToExcel(table, file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Загрузить таблицу");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Table loadedTable = ExcelImporter.importFromExcel(file.getAbsolutePath());
                    tableView.getItems().clear();
                    table.getRows().clear();
                    table.getRows().addAll(loadedTable.getRows());
                    tableView.getItems().addAll(loadedTable.getRows());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        filterButton.setOnAction(e -> {
            String filterValue = filterField.getText();
            if (!filterValue.isEmpty()) {
                tableView.getItems().clear();
                for (TableRow row : table.getRows()) {
                    if (row.toString().contains(filterValue)) {
                        tableView.getItems().add(row);
                    }
                }
            } else {
                tableView.getItems().setAll(table.getRows()); // Сброс фильтра
            }
        });

        VBox root = new VBox();
        root.getChildren().addAll(controlPanel, tableView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Table Application");
        primaryStage.show();





    }

    public static void main(String[] args) {
        launch(args);
    }
}