package com.giftapp.util;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class LoggerConfigTest {

    @Test
    void testSetupCreatesLogFile() {
        // Перевіряємо, що ініціалізація проходить без помилок
        assertDoesNotThrow(() -> LoggerConfig.setup());

        // Перевіряємо, що логер дійсно створив файл на диску
        File logFile = new File("gift-app.log");
        assertTrue(logFile.exists(), "Файл app.log має створитися після налаштування логера");
    }

    @Test
    void testLoggerConfigInstantiation() {
        // Покриваємо дефолтний конструктор
        LoggerConfig config = new LoggerConfig();
        assertNotNull(config);
    }
}