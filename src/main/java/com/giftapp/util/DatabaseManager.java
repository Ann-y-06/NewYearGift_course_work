package com.giftapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Вказуємо відносний шлях до теки, яку ми щойно створили
    private static final String URL = "jdbc:sqlite:src/main/resources/database/database.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        // Універсальна таблиця для всіх типів солодощів
        String sql = "CREATE TABLE IF NOT EXISTS sweets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "weight REAL NOT NULL," +
                "sugar REAL NOT NULL," +
                "manufacturer TEXT," +
                "expiration_date TEXT," +
                "sweet_type TEXT NOT NULL," +      // CANDY, CHOCOLATE або WAFFLE
                "special_property TEXT NOT NULL" + // Начинка, % какао або наявність глазурі
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ База даних успішно підключена та ініціалізована.");

        } catch (SQLException e) {
            System.err.println("❌ Помилка ініціалізації бази даних: " + e.getMessage());
        }
    }
}