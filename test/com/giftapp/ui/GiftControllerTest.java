package com.giftapp.ui;

import com.giftapp.model.Candy;
import com.giftapp.model.Sweet;
import com.giftapp.service.GiftService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GiftControllerTest extends ApplicationTest {

    private GiftController controller;

    @Override
    public void start(Stage stage) throws Exception {
        GiftService mockService = mock(GiftService.class);
        List<Sweet> dummyList = new ArrayList<>();
        dummyList.add(new Candy("Ромашка", 15.0, 10.0, "Roshen", LocalDate.now().plusMonths(1), "Начинка: помадка"));
        when(mockService.getAllSweets()).thenReturn(dummyList);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setService(mockService);

        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    private void clickMenu(String buttonText) {
        interact(() -> lookup(buttonText).queryButton().fire());
    }

    private void clickDialog(String buttonText) throws InterruptedException {
        Platform.runLater(() -> lookup(buttonText).queryButton().fire());
        Thread.sleep(400);
        type(KeyCode.ENTER);
        Thread.sleep(200);
    }

    // 🤖 НОВИЙ РОБОТ: Програмно виділяє перший елемент у списку
    private void selectFirstItem() {
        Platform.runLater(() -> {
            ListView<?> list = lookup(".list-view").queryListView();
            if (!list.getItems().isEmpty()) {
                list.getSelectionModel().selectFirst();
            }
        });
    }

    @Test
    void testWarehouseFullCoverage() throws InterruptedException {
        clickMenu("Робота зі складом");

        clickMenu("Сортування ->");
        clickMenu("За вагою");
        clickMenu("За цукром");
        clickMenu("Назад");

        clickMenu("Пошук ->");
        clickDialog("За назвою");
        clickDialog("За виробником");
        clickDialog("За діапазоном цукру");

        // ГІЛКА 1: Додаємо БЕЗ вибору (викличе попередження)
        clickDialog("🎁 Додати вибране в подарунок");

        // ГІЛКА 2: Додаємо З ВИБОРОМ (успішне додавання)
        selectFirstItem();
        clickDialog("🎁 Додати вибране в подарунок");

        clickMenu("Скинути пошук");
        clickMenu("Назад");

        clickMenu("Створити нові солодощі");

        // ГІЛКА 3: Видаляємо БЕЗ вибору
        clickDialog("Видалити вибране");

        // ГІЛКА 4: Видаляємо З ВИБОРОМ
        selectFirstItem();
        clickDialog("Видалити вибране");

        clickDialog("Видалити прострочені");
        clickMenu("Назад");
    }

    @Test
    void testGiftFullCoverage() throws InterruptedException {
        clickMenu("Робота з подарунком");

        // ГІЛКА 5: Спроба зберегти порожній подарунок
        clickDialog("🎁 Завершити створення!");

        // ГІЛКА 6: Спроба видалити з порожнього подарунка
        clickDialog("Видалити з подарунка");

        // Наповнюємо подарунок для подальших тестів
        clickMenu("Переглянути подарунок");
        clickDialog("Додати зі списку всіх");

        // ГІЛКА 7: Успішне видалення з подарунка
        selectFirstItem();
        clickDialog("Видалити з подарунка");

        clickDialog("Розрахувати вагу");

        clickMenu("Сортування ->");
        clickMenu("За вагою");
        clickMenu("Назад");

        clickMenu("Пошук ->");
        clickDialog("За назвою");
        clickMenu("Скинути пошук");
        clickMenu("Назад");
        clickMenu("Назад");
    }

    @Test
    void testExtremeExceptionsAndBranches() throws Exception {
        // --- 1. ЗЛАМУЄМО ІМПОРТ (передаємо файл, якого не існує) ---
        java.io.File ghostFile = new java.io.File("неіснуючий_файл_123.csv");
        Platform.runLater(() -> controller.processCSVFile(ghostFile));
        Thread.sleep(500);
        type(KeyCode.ENTER); // Закриваємо Alert помилки читання

        // --- 2. ЗЛАМУЄМО ЗАПИС ЧЕКА (передаємо папку замість файлу, щоб заборонити запис) ---
        java.io.File readOnlyDir = java.nio.file.Files.createTempDirectory("fake_dir").toFile();
        readOnlyDir.deleteOnExit();
        Platform.runLater(() -> controller.saveReceiptToFile(readOnlyDir));
        Thread.sleep(500);
        type(KeyCode.ENTER); // Закриваємо Alert помилки запису

        // --- 3. ЗГОДОВУЄМО МАКСИМАЛЬНО "БИТИЙ" CSV ---
        // Цей файл пройдеться по абсолютно всіх прихованих гілках if/else
        java.io.File tempCsv = java.io.File.createTempFile("sweets_test_extreme", ".csv");
        tempCsv.deleteOnExit();
        String maliciousCsv = "\n" +                                      // Гілка: порожній рядок
                "CHOCOLATE,Шоко,100,50,Світоч,2026-12-31,80\n" +  // Гілка: Шоколад
                "WAFFLE,Артек,50,20,Світоч,2026-12-31,true\n" +   // Гілка: Вафля
                "CANDY,Корівка,15,60,Рошен,2026-12-31,Молочна\n" +// Гілка: Цукерка
                "CHOCOLATE,Шоко,100,50,Світоч,2026-12-31,80\n" +  // Гілка: Дублікат
                "SHORT_LINE_ERROR\n" +                            // Гілка: Менше 7 колонок
                "CANDY,Помилка,АБВГД,50,Бренд,2026-12-31,Власт\n";// Гілка: inner catch (помилка парсингу)
        java.nio.file.Files.writeString(tempCsv.toPath(), maliciousCsv);

        Platform.runLater(() -> controller.processCSVFile(tempCsv));
        Thread.sleep(500);
        type(KeyCode.ENTER); // Закриваємо фінальний Alert імпорту
    }

    @Test
    void testCriticalErrorSimulation() throws InterruptedException {
        com.giftapp.util.EmailSender.setRecipientEmail("");
        Platform.runLater(() -> lookup("Симуляція критичного збою").queryButton().fire());
        Thread.sleep(500);
        type(KeyCode.ESCAPE);
        Thread.sleep(200);
    }
}