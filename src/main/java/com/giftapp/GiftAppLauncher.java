package com.giftapp;

import com.giftapp.repository.DatabaseSweetRepository;
import com.giftapp.service.GiftServiceImpl;
import com.giftapp.ui.GiftController;
import com.giftapp.util.DatabaseManager;
import com.giftapp.util.LoggerConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.giftapp.model.Candy;
import java.time.LocalDate;

public class GiftAppLauncher extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 0. Ініціалізуємо логування найпершим
        LoggerConfig.setup();

        // 1. Ініціалізуємо БД
        DatabaseManager.initializeDatabase();

        // 2. Створюємо наші об'єкти
        DatabaseSweetRepository repository = new DatabaseSweetRepository();
        GiftServiceImpl service = new GiftServiceImpl(repository);

        // 3. ЗМІНЕНО ШЛЯХ: тепер він веде у /views/main-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Parent root = loader.load();

        // 4. Передаємо сервіс у контролер
        GiftController controller = loader.getController();
        controller.setService(service);

        // 5. Показуємо вікно з заданими розмірами
        primaryStage.setTitle("NewYear Gift System");
        primaryStage.setScene(new Scene(root, 1000, 700)); // Ось тут ми задаємо ширину і висоту
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}