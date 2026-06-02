package com.giftapp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AllModelsTest {

    @Test
    void testCandy() {
        LocalDate date = LocalDate.now();
        Candy c = new Candy("Name", 10.0, 50.0, "Manuf", date, "Jelly");

        // Перевірка логіки
        assertTrue(c.toString().contains("Jelly"), "toString має містити тип начинки");
        assertTrue(c.toCsv().contains("CANDY"), "toCsv має містити маркер CANDY");
        assertEquals(5.0, c.getTotalSugarWeight(), 0.001, "Розрахунок загальної ваги цукру неправильний");

        // Перевірка гетерів
        assertEquals("Name", c.getName());
        assertEquals(10.0, c.getWeight());
        assertEquals(50.0, c.getSugar());
        assertEquals("Manuf", c.getManufacturer());
        assertEquals(date, c.getExpirationDate());
    }

    @Test
    void testChocolate() {
        LocalDate date = LocalDate.now();
        Chocolate ch = new Chocolate("Choco", 100.0, 30.0, "M", date, 75);

        // Перевірка логіки
        assertTrue(ch.toString().contains("75%"), "toString має містити відсоток какао");
        assertTrue(ch.toCsv().contains("CHOCOLATE"), "toCsv має містити маркер CHOCOLATE");
        assertTrue(ch.toCsv().contains("75"), "toCsv має містити відсоток какао");

        // Перевірка гетерів
        assertEquals(100.0, ch.getWeight());
        assertEquals("Choco", ch.getName());
    }

    @Test
    void testWaffle() {
        LocalDate date = LocalDate.now();

        // Перевірка логіки (в глазурі)
        Waffle w = new Waffle("Waf", 50.0, 20.0, "M", date, true);
        assertTrue(w.toString().contains("в глазурі"));
        assertTrue(w.toCsv().contains("WAFFLE"));

        // Перевірка логіки (без глазурі)
        Waffle w2 = new Waffle("Waf2", 50.0, 20.0, "M", date, false);
        assertTrue(w2.toString().contains("без глазурі"));

        // Перевірка гетерів
        assertEquals("Waf", w.getName());
    }
}