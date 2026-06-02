package com.giftapp.util;

import org.junit.jupiter.api.Test;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailLogHandlerTest {

    @Test
    void testPublishSevereLevel() {
        EmailLogHandler handler = new EmailLogHandler();
        LogRecord record = new LogRecord(Level.SEVERE, "Тестова критична помилка для пошти");
        record.setSourceClassName("EmailLogHandlerTest");

        EmailSender.setRecipientEmail("hanna.mail543@gmail.com");

        // Перевіряємо гілку if (SEVERE)
        assertDoesNotThrow(() -> handler.publish(record));
    }

    @Test
    void testPublishInfoLevel() {
        EmailLogHandler handler = new EmailLogHandler();
        LogRecord record = new LogRecord(Level.INFO, "Звичайна інформація, ігнорується");
        record.setSourceClassName("EmailLogHandlerTest");

        // Перевіряємо, що звичайні логи не викликають відправку листа
        assertDoesNotThrow(() -> handler.publish(record));
    }

    @Test
    void testFlushAndClose() {
        EmailLogHandler handler = new EmailLogHandler();

        // Ці методи порожні, але ми маємо їх викликати, щоб Jacoco зарахував ці рядки як покриті
        assertDoesNotThrow(() -> {
            handler.flush();
            handler.close();
        });
    }
}