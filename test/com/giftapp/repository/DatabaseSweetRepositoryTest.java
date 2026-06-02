package com.giftapp.repository;

import com.giftapp.model.Candy;
import com.giftapp.model.Chocolate;
import com.giftapp.model.Sweet;
import com.giftapp.model.Waffle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseSweetRepositoryTest {

    private DatabaseSweetRepository repository;
    private final String testPrefix = "TEST_DB_";

    @BeforeEach
    void setUp() {
        // Конструктор автоматично викличе createTableIfNotExists()
        repository = new DatabaseSweetRepository();
    }

    @AfterEach
    void tearDown() {
        // Очищення бази після тестів
        repository.deleteByName(testPrefix + "Candy");
        repository.deleteByName(testPrefix + "Choco");
        repository.deleteByName(testPrefix + "Waffle");
        repository.deleteByName(testPrefix + "ToDelete");
    }

    @Test
    void testAddAndGetAllSweets() {
        // 1. Створюємо всі 3 типи (щоб покрити весь switch case в репозиторії)
        Candy candy = new Candy(testPrefix + "Candy", 10.0, 5.0, "Roshen", LocalDate.now(), "Jelly");
        Chocolate choco = new Chocolate(testPrefix + "Choco", 20.0, 10.0, "Milka", LocalDate.now(), 75);
        Waffle waffle = new Waffle(testPrefix + "Waffle", 15.0, 8.0, "Artek", LocalDate.now(), true);

        // 2. Додаємо в базу
        assertDoesNotThrow(() -> {
            repository.addSweet(candy);
            repository.addSweet(choco);
            repository.addSweet(waffle);
        }, "Помилка SQL при додаванні в БД");

        // 3. Витягуємо всі і перевіряємо, чи є наші
        List<Sweet> allSweets = repository.getAllSweets();

        boolean foundCandy = false;
        boolean foundChoco = false;
        boolean foundWaffle = false;

        for (Sweet s : allSweets) {
            if (s.getName().equals(testPrefix + "Candy")) foundCandy = true;
            if (s.getName().equals(testPrefix + "Choco")) foundChoco = true;
            if (s.getName().equals(testPrefix + "Waffle")) foundWaffle = true;
        }

        assertTrue(foundCandy, "Цукерка не збереглася в базу");
        assertTrue(foundChoco, "Шоколад не зберігся в базу");
        assertTrue(foundWaffle, "Вафля не збереглася в базу");
    }

    @Test
    void testDeleteByName() {
        Candy candy = new Candy(testPrefix + "ToDelete", 10.0, 5.0, "M", LocalDate.now(), "F");
        repository.addSweet(candy);

        assertDoesNotThrow(() -> repository.deleteByName(testPrefix + "ToDelete"));

        List<Sweet> allSweets = repository.getAllSweets();
        boolean stillExists = allSweets.stream().anyMatch(s -> s.getName().equals(testPrefix + "ToDelete"));

        assertFalse(stillExists, "Цукерка мала бути видалена з БД");
    }
}