package com.giftapp.util;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    private static final Logger ROOT_LOGGER = Logger.getLogger("com.giftapp");

    public static void setup() {
        try {
            // 1. Налаштування запису у файл gift-app.log
            FileHandler fileHandler = new FileHandler("gift-app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL); // Пишемо все: Інфо, помилки, попередження

            // 2. Налаштування відправки на пошту (тільки критичні)
            EmailLogHandler emailHandler = new EmailLogHandler();
            emailHandler.setLevel(Level.SEVERE);

            ROOT_LOGGER.addHandler(fileHandler);
            ROOT_LOGGER.addHandler(emailHandler);
            ROOT_LOGGER.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("Не вдалося ініціалізувати систему логування: " + e.getMessage());
        }
    }
}