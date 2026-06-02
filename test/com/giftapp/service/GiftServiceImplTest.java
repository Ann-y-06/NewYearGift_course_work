package com.giftapp.service;

import com.giftapp.model.Candy;
import com.giftapp.model.Sweet;
import com.giftapp.repository.SweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GiftServiceImplTest {

    private SweetRepository mockRepository;
    private GiftServiceImpl giftService;

    @BeforeEach
    void setUp() {
        mockRepository = mock(SweetRepository.class);
        giftService = new GiftServiceImpl(mockRepository);
    }

    @Test
    void testGetSortedByWeight() {
        Candy c1 = new Candy("Важка", 50.0, 10.0, "M", LocalDate.now(), "F");
        Candy c2 = new Candy("Легка", 10.0, 10.0, "M", LocalDate.now(), "F");
        when(mockRepository.getAllSweets()).thenReturn(Arrays.asList(c1, c2));

        List<Sweet> sorted = giftService.getSortedByWeight();
        assertEquals("Легка", sorted.get(0).getName(), "Сортування за вагою не працює");
        assertEquals("Важка", sorted.get(1).getName());
    }

    @Test
    void testGetSortedBySugar() {
        Candy c1 = new Candy("Солодка", 10.0, 45.0, "M", LocalDate.now(), "F");
        Candy c2 = new Candy("Менш солодка", 10.0, 15.0, "M", LocalDate.now(), "F");
        when(mockRepository.getAllSweets()).thenReturn(Arrays.asList(c1, c2));

        List<Sweet> sorted = giftService.getSortedBySugar();
        assertEquals("Менш солодка", sorted.get(0).getName(), "Сортування за цукром не працює");
        assertEquals("Солодка", sorted.get(1).getName());
    }

    @Test
    void testFindByManufacturer() {
        Candy c1 = new Candy("A", 10.0, 10.0, "Roshen", LocalDate.now(), "F");
        Candy c2 = new Candy("B", 10.0, 10.0, "AVK", LocalDate.now(), "F");
        when(mockRepository.getAllSweets()).thenReturn(Arrays.asList(c1, c2));

        // Перевіряємо, що ігнорує регістр (roshEN)
        List<Sweet> found = giftService.findByManufacturer("roshEN");
        assertEquals(1, found.size(), "Пошук за виробником не працює");
        assertEquals("A", found.get(0).getName());
    }

    @Test
    void testFindBySugarRange() {
        // Додаємо 3 варіанти, щоб перевірити всі можливі гілки оператора &&
        Candy c1 = new Candy("Норма", 10.0, 15.0, "M", LocalDate.now(), "F");    // Цукор 15 (входить у 10-20)
        Candy c2 = new Candy("Забагато", 10.0, 80.0, "M", LocalDate.now(), "F"); // Цукор 80 (> 20)
        Candy c3 = new Candy("Замало", 10.0, 5.0, "M", LocalDate.now(), "F");    // Цукор 5 (< 10)

        when(mockRepository.getAllSweets()).thenReturn(Arrays.asList(c1, c2, c3));

        List<Sweet> found = giftService.findBySugarRange(10.0, 20.0);

        assertEquals(1, found.size(), "Пошук за діапазоном цукру має знайти лише 1 цукерку");
        assertEquals("Норма", found.get(0).getName());
    }

    @Test
    void testRemoveExpiredSweets() {
        Candy expired = new Candy("Прострочена", 10.0, 10.0, "M", LocalDate.now().minusDays(1), "F");
        Candy fresh = new Candy("Свіжа", 10.0, 10.0, "M", LocalDate.now().plusDays(1), "F");
        when(mockRepository.getAllSweets()).thenReturn(Arrays.asList(expired, fresh));

        giftService.removeExpiredSweets();

        // Перевіряємо, що БД отримала команду видалити тільки прострочену
        verify(mockRepository, times(1)).deleteByName("Прострочена");
        verify(mockRepository, never()).deleteByName("Свіжа");
    }

    @Test
    void testAddSweetToGift() {
        Candy c = new Candy("Тест", 10.0, 10.0, "M", LocalDate.now(), "F");
        giftService.addSweetToGift(c);
        verify(mockRepository, times(1)).addSweet(c);
    }

    @Test
    void testRemoveSweetByName() {
        giftService.removeSweetByName("ТестоваВидалення");
        verify(mockRepository, times(1)).deleteByName("ТестоваВидалення");
    }
}