package com.giftapp.repository;

import com.giftapp.model.*;
import com.giftapp.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSweetRepository implements SweetRepository {

    // У DatabaseSweetRepository.java
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/database.db";

    public DatabaseSweetRepository() {
        createTableIfNotExists(); // Викликаємо ініціалізацію при кожному створенні репозиторію
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS sweets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "weight REAL, " +
                "sugar REAL, " +
                "manufacturer TEXT, " +
                "expiration_date TEXT, " +
                "sweet_type TEXT, " +
                "special_property TEXT);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Sweet> getAllSweets() {
        List<Sweet> sweets = new ArrayList<>();
        String sql = "SELECT * FROM sweets";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double sugar = rs.getDouble("sugar");
                String manufacturer = rs.getString("manufacturer");
                LocalDate expirationDate = LocalDate.parse(rs.getString("expiration_date"));
                String type = rs.getString("sweet_type");
                String special = rs.getString("special_property");

                // Використовуємо твої класи моделей
                switch (type) {
                    case "CANDY" -> sweets.add(new Candy(name, weight, sugar, manufacturer, expirationDate, special));
                    case "CHOCOLATE" -> sweets.add(new Chocolate(name, weight, sugar, manufacturer, expirationDate, Integer.parseInt(special)));
                    case "WAFFLE" -> sweets.add(new Waffle(name, weight, sugar, manufacturer, expirationDate, Boolean.parseBoolean(special)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sweets;
    }

    @Override
    public void addSweet(Sweet sweet) {
        String sql = "INSERT INTO sweets(name, weight, sugar, manufacturer, expiration_date, sweet_type, special_property) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sweet.getName());
            pstmt.setDouble(2, sweet.getWeight());
            pstmt.setDouble(3, sweet.getSugar());
            pstmt.setString(4, sweet.getManufacturer());
            pstmt.setString(5, sweet.getExpirationDate().toString());

            // Твої оригінальні назви типів
            if (sweet instanceof Candy candy) {
                pstmt.setString(6, "CANDY");
                pstmt.setString(7, candy.getFillingType()); // Додай цей метод у Candy
            } else if (sweet instanceof Chocolate chocolate) {
                pstmt.setString(6, "CHOCOLATE");
                pstmt.setString(7, String.valueOf(chocolate.getCocoaPercentage())); // Додай цей метод у Chocolate
            } else if (sweet instanceof Waffle waffle) {
                pstmt.setString(6, "WAFFLE");
                pstmt.setString(7, String.valueOf(waffle.isHasGlaze())); // Додай цей метод у Waffle
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByName(String name) {
        // Trim() прибирає випадкові пробіли, якщо вони є в назві
        String sql = "DELETE FROM sweets WHERE TRIM(name) = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Видалено рядків: " + affectedRows); // Це допоможе зрозуміти, чи знайшла база запис
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}