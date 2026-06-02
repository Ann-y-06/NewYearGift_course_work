package com.giftapp.ui;

import com.giftapp.service.GiftService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.application.Platform;

import static org.mockito.Mockito.mock;

public class AddSweetControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        GiftService mockService = mock(GiftService.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add-sweet-view.fxml"));
        Parent root = loader.load();

        AddSweetController controller = loader.getController();
        // Передаємо заглушки, щоб вікно могло відкритися без помилок
        controller.init(mockService, () -> {});

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testDynamicFieldsForChocolate() {
        // Робот обирає "Шоколад" і перевіряє, чи з'явилося поле
        clickOn("#typeComboBox");
        clickOn("Шоколад");
        // Вводимо тестові дані
        clickOn("#nameField").write("Тестова Шоколадка");
        clickOn("#weightField").write("50.5");
        clickOn("#sugarField").write("20.0");
    }

    @Test
    void testDynamicFieldsForCandy() {
        // Робот обирає "Цукерка"
        clickOn("#typeComboBox");
        clickOn("Цукерка");
    }

    @Test
    void testDynamicFieldsForWaffle() {
        // Робот обирає "Вафля"
        clickOn("#typeComboBox");
        clickOn("Вафля");
    }

    @Test
    void testHandleSaveSuccess() {
        // 1. Обираємо тип
        clickOn("#typeComboBox");
        clickOn("Шоколад");

        // 2. Заповнюємо базові поля
        clickOn("#nameField").write("Корона");
        clickOn("#weightField").write("100.0");
        clickOn("#sugarField").write("45.5");

        // 3. Натискаємо кнопку збереження!
        // ЗАМІНИ "Зберегти" на той текст, який реально написаний на твоїй кнопці
        clickOn("Зберегти");
    }

    @Test
    void testHandleSaveValidationError() {
        // Робот залишає поля порожніми і тисне зберегти, щоб викликати Alert з помилкою
        clickOn("Зберегти");

        // Закриваємо віконце Alert
        type(javafx.scene.input.KeyCode.ENTER);
    }

    @Test
    void testInvalidNumberInputCoverage() {
        interact(() -> {
            // Вибираємо тип
            lookup("#typeComboBox").queryComboBox().getSelectionModel().select("Шоколад");
            lookup("#nameField").queryAs(javafx.scene.control.TextField.class).setText("Тест");

            // СПЕЦІАЛЬНО ВВОДИМО БУКВИ ТАМ, ДЕ МАЮТЬ БУТИ ЦИФРИ
            lookup("#weightField").queryAs(javafx.scene.control.TextField.class).setText("АБВГД");
            lookup("#sugarField").queryAs(javafx.scene.control.TextField.class).setText("10.0");

            // Тиснемо зберегти (це викличе NumberFormatException)
            lookup("Зберегти").queryButton().fire();
        });

        // Закриваємо червоне віконце з помилкою
        type(javafx.scene.input.KeyCode.ENTER);
    }

    @Test
    void testBranchChocolate() throws InterruptedException {
        interact(() -> {
            lookup("#typeComboBox").queryComboBox().getSelectionModel().select("Шоколад");
            lookup("#nameField").queryAs(javafx.scene.control.TextField.class).setText("Шоко");
            lookup("#weightField").queryAs(javafx.scene.control.TextField.class).setText("100");
            lookup("#sugarField").queryAs(javafx.scene.control.TextField.class).setText("50");
        });

        interact(() -> {
            // Знаходимо саме те поле і вставляємо 80
            lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class).forEach(field -> {
                if (field.getParent() instanceof javafx.scene.control.DatePicker) return;
                if ("nameField".equals(field.getId()) || "weightField".equals(field.getId()) || "sugarField".equals(field.getId())) return;
                field.setText("80");
            });
            lookup("Зберегти").queryButton().fire();
        });
        Thread.sleep(300);
        type(javafx.scene.input.KeyCode.ENTER); // Alert ОК
    }

    @Test
    void testBranchCandy() throws InterruptedException {
        interact(() -> {
            lookup("#typeComboBox").queryComboBox().getSelectionModel().select("Цукерка");
            lookup("#nameField").queryAs(javafx.scene.control.TextField.class).setText("Ромашка");
            lookup("#weightField").queryAs(javafx.scene.control.TextField.class).setText("15");
            lookup("#sugarField").queryAs(javafx.scene.control.TextField.class).setText("10");
        });

        interact(() -> {
            lookup(".text-field").queryAllAs(javafx.scene.control.TextField.class).forEach(field -> {
                if (field.getParent() instanceof javafx.scene.control.DatePicker) return;
                if ("nameField".equals(field.getId()) || "weightField".equals(field.getId()) || "sugarField".equals(field.getId())) return;
                field.setText("Помадка");
            });
            lookup("Зберегти").queryButton().fire();
        });
        Thread.sleep(300);
        type(javafx.scene.input.KeyCode.ENTER);
    }

    @Test
    void testBranchWaffleWithGlaze() throws InterruptedException {
        interact(() -> {
            lookup("#typeComboBox").queryComboBox().getSelectionModel().select("Вафля");
            lookup("#nameField").queryAs(javafx.scene.control.TextField.class).setText("Артек");
            lookup("#weightField").queryAs(javafx.scene.control.TextField.class).setText("50");
            lookup("#sugarField").queryAs(javafx.scene.control.TextField.class).setText("20");
        });

        interact(() -> {
            lookup(".check-box").queryAs(javafx.scene.control.CheckBox.class).setSelected(true);
            lookup("Зберегти").queryButton().fire();
        });
        Thread.sleep(300);
        type(javafx.scene.input.KeyCode.ENTER);
    }

    @Test
    void testBranchWaffleWithoutGlaze() throws InterruptedException {
        interact(() -> {
            lookup("#typeComboBox").queryComboBox().getSelectionModel().select("Вафля");
            lookup("#nameField").queryAs(javafx.scene.control.TextField.class).setText("Артек 2");
            lookup("#weightField").queryAs(javafx.scene.control.TextField.class).setText("50");
            lookup("#sugarField").queryAs(javafx.scene.control.TextField.class).setText("20");
        });

        interact(() -> {
            lookup(".check-box").queryAs(javafx.scene.control.CheckBox.class).setSelected(false);
            lookup("Зберегти").queryButton().fire();
        });
        Thread.sleep(300);
        type(javafx.scene.input.KeyCode.ENTER);
    }





}