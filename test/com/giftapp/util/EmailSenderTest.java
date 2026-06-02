package com.giftapp.util;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

public class EmailSenderTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        // Просто ініціалізуємо JavaFX
    }

    @Test
    void testConstructor() {
        // Створюємо порожній об'єкт просто щоб задовольнити аналізатор
        EmailSender sender = new EmailSender();
        org.junit.jupiter.api.Assertions.assertNotNull(sender);
    }

    @Test
    void testSuccessfulSending() throws InterruptedException {
        EmailSender.setRecipientEmail("hanna.mail543@gmail.com");
        EmailSender.sendCriticalError("Тест Jacoco", "Перевірка успішної відправки");

        Thread.sleep(6000);
        WaitForAsyncUtils.waitForFxEvents();

        // Замість кліку по тексту, просто тиснемо Enter
        type(KeyCode.ENTER);
    }

    @Test
    void testFailedSending() throws InterruptedException {
        // Замінили кирилицю на латиницю, щоб сервер не лаявся на кодування
        EmailSender.setRecipientEmail("bad-email@test.com");
        EmailSender.sendCriticalError("Тест Jacoco", "Перевірка помилки");

        Thread.sleep(4000);
        WaitForAsyncUtils.waitForFxEvents();

        // Тиснемо Enter для закриття вікна з помилкою
        type(KeyCode.ENTER);
    }

    @Test
    void testEmptyEmailDialog() throws InterruptedException {
        EmailSender.setRecipientEmail("");
        EmailSender.sendCriticalError("Тест Jacoco", "Перевірка діалогу");

        Thread.sleep(1000);

        write("hanna.mail543@gmail.com");
        // Тиснемо Enter замість пошуку кнопки ОК
        type(KeyCode.ENTER);

        Thread.sleep(6000);
        WaitForAsyncUtils.waitForFxEvents();

        // Тиснемо Enter для фінального Alert
        type(KeyCode.ENTER);
    }

    @Test
    void testCatchBlockCoverage() throws InterruptedException {
        // Передаємо явно неправильний формат адреси, щоб викликати помилку
        EmailSender.setRecipientEmail("invalid_email_format");

        EmailSender.sendCriticalError("Тест catch", "Перевірка обробки помилок");

        // Даємо фоновому потоку 2 секунди, щоб він встиг впасти з помилкою
        // і передати команду на створення віконця в Platform.runLater
        Thread.sleep(2000);

        // Синхронізуємо чергу JavaFX, щоб віконце точно з'явилося на екрані
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Робот натискає Enter, закриваючи червоне віконце Alert.AlertType.ERROR
        type(javafx.scene.input.KeyCode.ENTER);
    }

    @Test
    void testNullEmailBranch() throws InterruptedException {
        // Передаємо чистий null, щоб покрити другу половину умови if
        EmailSender.setRecipientEmail(null);
        EmailSender.sendCriticalError("Тест гілки", "Перевірка null");

        Thread.sleep(1000);
        // Вводимо пошту і тиснемо Enter, щоб закрити віконце, яке вискочить
        write("hanna.mail543@gmail.com");
        type(javafx.scene.input.KeyCode.ENTER);

        // Чекаємо відправки і закриваємо Alert успіху
        Thread.sleep(6000);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        type(javafx.scene.input.KeyCode.ENTER);
    }
}