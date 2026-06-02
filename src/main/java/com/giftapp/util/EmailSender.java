package com.giftapp.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    // Твоя пошта
    private static final String SENDER_EMAIL = "***";
    // Той самий спеціальний код, обов'язково :
    private static final String SENDER_PASSWORD = "***";

    private static String recipientEmail = "";

    public static void setRecipientEmail(String email) {
        recipientEmail = email;
    }

    public static void sendCriticalError(String context, String errorMessage) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Потрібна адреса");
                dialog.setHeaderText("Виникла критична помилка!");
                dialog.setContentText("Введіть E-mail для відправки звіту:");

                dialog.showAndWait().ifPresent(email -> {
                    recipientEmail = email.trim();
                    // Запускаємо в окремому потоці
                    new Thread(() -> performSending(context, errorMessage)).start();
                });
            });
        } else {
            new Thread(() -> performSending(context, errorMessage)).start();
        }
    }

    private static void performSending(String context, String errorMessage) {
        try {
            System.out.println("⏳ [КРОК 1] Підготовка налаштувань...");
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.quitwait", "false");

            // ВАЖЛИВО: Тайм-аути, щоб програма не зависала вічно
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            System.out.println("⏳ [КРОК 2] Авторизація...");
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            System.out.println("⏳ [КРОК 3] Формування листа...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("🚨 CRITICAL ERROR: " + context);
            message.setContent("<h3>Помилка в системі</h3><p>" + errorMessage + "</p>", "text/html; charset=utf-8");

            System.out.println("⏳ [КРОК 4] Відправка через інтернет...");
            Transport.send(message);

            System.out.println("✅ [УСПІХ] Лист відправлено!");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успіх");
                alert.setHeaderText("Відправлено");
                alert.setContentText("Звіт успішно надіслано на:\n" + recipientEmail);
                alert.show();
            });

        } catch (Exception e) {
            System.err.println("❌ [ПОМИЛКА] " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка відправки");
                alert.setHeaderText("Не вдалося надіслати лист!");
                alert.setContentText(e.getMessage());
                alert.show();
            });
            // Скидаємо пошту, щоб наступного разу знову запитало
            recipientEmail = "";
        }
    }
}