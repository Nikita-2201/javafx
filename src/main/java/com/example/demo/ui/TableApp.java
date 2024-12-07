package com.example.demo.ui;

import com.example.demo.core.Table;
import com.example.demo.core.TableRow;
import com.example.demo.model.*;
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

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableApp extends Application {

    private Table table; // Таблица как поле класса
    private TableView<TableRow> tableView; // TableView как поле класса

    @Override
    public void start(Stage primaryStage) {
        //Кол. столбцов и строк при начальной генерации
        table = TestDataGenerator.generateRandomTable(10, 5);
        tableView = new TableView<>();

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

        Button showTypesButton = new Button("Показать типы столбцов");
        Button addRowButton = new Button("Добавить строку");
        Button deleteRowButton = new Button("Удалить строку");
        Button addColumnButton = new Button("Добавить столбец");
        Button deleteColumnButton = new Button("Удалить столбец");
        Button saveButton = new Button("Сохранить");
        Button loadButton = new Button("Загрузить");
        TextField filterField = new TextField();
        filterField.setPromptText("Введите значение для фильтрации");
        Button filterButton = new Button("Фильтровать");
        Button saveBinaryButton = new Button("Сохранить в бинарный файл");
        Button loadBinaryButton = new Button("Загрузить из бинарного файла");

        controlPanel.getChildren().addAll(showTypesButton, addRowButton, deleteRowButton, addColumnButton , deleteColumnButton,saveButton, loadButton, saveBinaryButton, loadBinaryButton, filterField, filterButton);

        showTypesButton.setOnAction(e -> {
            StringBuilder typesInfo = new StringBuilder("Типы столбцов:\n");
            for (int i = 0; i < table.getColumnNames().size(); i++) {
                DataColumn<?> prototype = table.getPrototypeRow().getColumn(i);
                typesInfo.append(table.getColumnNames().get(i))
                        .append(" -> ")
                        .append(prototype.getClass().getSimpleName())
                        .append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Типы данных столбцов");
            alert.setHeaderText(null);
            alert.setContentText(typesInfo.toString());
            alert.showAndWait();
        });

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

        addColumnButton.setOnAction(e -> {
            // Диалог для ввода имени столбца и выбора типа
            Dialog<String[]> dialog = new Dialog<>();
            dialog.setTitle("Добавить новый столбец");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Поле для имени столбца
            TextField columnNameField = new TextField();
            columnNameField.setPromptText("Имя столбца");

            // Выбор типа данных
            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll("String", "Integer", "Double", "Date", "GPS Coordinates");
            typeComboBox.setValue("String");

            grid.add(new Label("Имя столбца:"), 0, 0);
            grid.add(columnNameField, 1, 0);
            grid.add(new Label("Тип данных:"), 0, 1);
            grid.add(typeComboBox, 1, 1);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return new String[]{columnNameField.getText(), typeComboBox.getValue()};
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result -> {
                String columnName = result[0];
                String columnType = result[1];

                // Создаем прототип столбца на основе выбранного типа
                DataColumn<?> prototype;
                switch (columnType) {
                    case "Integer":
                        prototype = new IntegerColumn(columnName);
                        break;
                    case "Double":
                        prototype = new DoubleColumn(columnName);
                        break;
                    case "Date":
                        prototype = new DateColumn(columnName, "yyyy-MM-dd");
                        break;
                    case "GPS Coordinates":
                        prototype = new GpsCoordinatesColumn(columnName);
                        break;
                    default:
                        prototype = new StringColumn(columnName);
                }

                // Добавляем столбец в таблицу
                table.addColumn(columnName, prototype);

                // Добавляем новый столбец в интерфейс
                TableColumn<TableRow, String> column = new TableColumn<>(columnName);
                final int colIndex = table.getColumnNames().size() - 1;
                column.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getColumn(colIndex).toString()));
                tableView.getColumns().add(column);

                // Добавляем пустое значение для всех строк
                for (TableRow row : table.getRows()) {
                    DataColumn<?> newColumn = prototype.clone();
                    row.addColumn(newColumn);
                }

                tableView.refresh();
            });
        });

        deleteColumnButton.setOnAction(e -> {
            // Список для выбора столбца
            ChoiceDialog<String> dialog = new ChoiceDialog<>(table.getColumnNames().get(0), table.getColumnNames());
            dialog.setTitle("Удалить столбец");
            dialog.setHeaderText("Выберите столбец для удаления");
            dialog.setContentText("Столбец:");

            dialog.showAndWait().ifPresent(selectedColumn -> {
                int columnIndex = table.getColumnNames().indexOf(selectedColumn);

                // Удаляем столбец из модели данных
                table.getColumnNames().remove(columnIndex);
                table.getPrototypeRow().getColumns().remove(columnIndex);
                for (TableRow row : table.getRows()) {
                    row.getColumns().remove(columnIndex); // Удаляем данные столбца из каждой строки
                }

                // Удаляем столбец из интерфейса
                tableView.getColumns().remove(columnIndex);

                // Обновляем индексы столбцов в `TableView`
                for (int i = 0; i < tableView.getColumns().size(); i++) {
                    final int colIndex = i; // Обновленный индекс
                    TableColumn<TableRow, String> column = (TableColumn<TableRow, String>) tableView.getColumns().get(i);
                    column.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                            data.getValue().getColumn(colIndex).toString()
                    ));
                }

                // Обновляем таблицу
                tableView.refresh();
            });
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

        saveBinaryButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить в бинарный файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.bin"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    saveTableToBinary(table, file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadBinaryButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Загрузить из бинарного файла");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.bin"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                try {
                    Table loadedTable = loadTableFromBinary(file.getAbsolutePath());
                    table = loadedTable;

                    // Обновляем TableView
                    tableView.getColumns().clear();
                    for (int i = 0; i < table.getColumnNames().size(); i++) {
                        final int colIndex = i;
                        TableColumn<TableRow, String> column = new TableColumn<>(table.getColumnNames().get(i));
                        column.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getColumn(colIndex).toString()));
                        tableView.getColumns().add(column);
                    }

                    tableView.getItems().clear();
                    tableView.getItems().addAll(table.getRows());
                    tableView.refresh();
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


    public void saveTableToBinary(Table table, String filePath) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath))) {
            table.writeToStream(out);
        }
    }

    public Table loadTableFromBinary(String filePath) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            Table table = new Table();
            table.readFromStream(in);
            return table;
        }
    }
}