package com.giftapp.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class EmailLogHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        // Якщо помилка КРИТИЧНА (SEVERE) -> Відправляємо лист
        if (record.getLevel() == Level.SEVERE) {
            EmailSender.sendCriticalError(
                    record.getSourceClassName(), // Де сталася помилка
                    record.getMessage()          // Текст помилки
            );
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}