package com.example.demo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateColumn implements DataColumn<Date> {
    private String columnName;        // Имя столбца
    private Date value;               // Значение даты
    private SimpleDateFormat format; // Формат даты

    // Конструктор
    public DateColumn(String columnName, String dateFormat) {
        this.columnName = columnName;
        this.format = new SimpleDateFormat(dateFormat);
        this.value = new Date(); // Значение по умолчанию - текущая дата
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
    public Date parse(String value) {
        try {
            this.value = format.parse(value); // Парсинг строки в дату
        } catch (ParseException e) {
            this.value = new Date(); // Если парсинг не удался, устанавливается текущая дата
        }
        return this.value;
    }

    @Override
    public String toString() {
        return format.format(value); // Преобразование даты в строку
    }

    @Override
    public DateColumn clone() {
        DateColumn copy = new DateColumn(this.columnName, format.toPattern());
        copy.value = this.value; // Клонирование значения
        return copy;
    }

    @Override
    public int compareTo(DataColumn<Date> other) {
        return this.value.compareTo(other.parse("")); // Сравнение дат
    }

    @Override
    public DataColumn<Date> add(DataColumn<Date> other) {
        // Сложение дат не имеет смысла, можно выбросить исключение
        throw new UnsupportedOperationException("Addition is not supported for DateColumn.");
    }

    // Дополнительно: Getter для значения
    public Date getValue() {
        return value;
    }

    // Дополнительно: Setter для значения
    public void setValue(Date value) {
        this.value = value;
    }
}
