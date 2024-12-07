package com.example.demo.model;

import com.example.demo.core.CustomSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GpsCoordinatesColumn implements DataColumn<GpsCoordinatesColumn.GpsCoordinates>, CustomSerializable {
    private String columnName; // Имя столбца
    private GpsCoordinates value; // Координаты

    // Вложенный класс для представления координат
    public static class GpsCoordinates {
        private double latitude;  // Широта
        private double longitude; // Долгота

        public GpsCoordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return latitude + ", " + longitude;
        }

        // Рассчет расстояния между двумя координатами (в километрах)
        public double distanceTo(GpsCoordinates other) {
            final int EARTH_RADIUS = 6371; // Радиус Земли в километрах
            double latDistance = Math.toRadians(other.latitude - this.latitude);
            double lonDistance = Math.toRadians(other.longitude - this.longitude);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return EARTH_RADIUS * c;
        }
    }

    // Конструктор
    public GpsCoordinatesColumn(String columnName) {
        this.columnName = columnName;
        this.value = new GpsCoordinates(0.0, 0.0); // Значение по умолчанию
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
    public GpsCoordinates parse(String value) {
        try {
            String[] parts = value.split(","); // Ожидается формат "широта, долгота"
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            this.value = new GpsCoordinates(latitude, longitude);
        } catch (Exception e) {
            this.value = new GpsCoordinates(0.0, 0.0); // Значение по умолчанию в случае ошибки
        }
        return this.value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public GpsCoordinatesColumn clone() {
        GpsCoordinatesColumn copy = new GpsCoordinatesColumn(this.columnName);
        copy.value = new GpsCoordinates(this.value.latitude, this.value.longitude); // Клонируем координаты
        return copy;
    }

    @Override
    public int compareTo(DataColumn<GpsCoordinates> other) {
        // Сравниваем по расстоянию до точки (0, 0)
        GpsCoordinates zeroPoint = new GpsCoordinates(0.0, 0.0);
        double thisDistance = this.value.distanceTo(zeroPoint);
        double otherDistance = other.parse("").distanceTo(zeroPoint);
        return Double.compare(thisDistance, otherDistance);
    }

    @Override
    public DataColumn<GpsCoordinates> add(DataColumn<GpsCoordinates> other) {
        // Сложение координат возвращает новую точку с суммой широты и долготы
        GpsCoordinatesColumn result = new GpsCoordinatesColumn(this.columnName);
        result.value = new GpsCoordinates(
                this.value.latitude + other.parse("").latitude,
                this.value.longitude + other.parse("").longitude
        );
        return result;
    }

    // Дополнительно: Getter для значения
    public GpsCoordinates getValue() {
        return value;
    }

    // Дополнительно: Setter для значения
    public void setValue(GpsCoordinates value) {
        this.value = value;
    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeUTF(columnName); // Записываем имя столбца
        out.writeDouble(value.latitude); // Записываем широту
        out.writeDouble(value.longitude); // Записываем долготу
    }

    @Override
    public void readFromStream(DataInputStream in) throws IOException {
        columnName = in.readUTF(); // Читаем имя столбца
        double latitude = in.readDouble(); // Читаем широту
        double longitude = in.readDouble(); // Читаем долготу
        value = new GpsCoordinates(latitude, longitude); // Восстанавливаем объект координат
    }
}

