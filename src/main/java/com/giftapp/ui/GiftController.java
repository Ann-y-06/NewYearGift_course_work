package com.giftapp.ui;

import com.giftapp.model.Candy;
import com.giftapp.model.Sweet;
import com.giftapp.service.GiftService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GiftController {
    private GiftService giftService;
    private static final Logger logger = Logger.getLogger(GiftController.class.getName());

    @FXML private BorderPane rootPane;
    @FXML private ListView<Sweet> sweetsListView;
    @FXML private VBox menuContainer;
    @FXML private VBox centerContainer;

    private ObservableList<Sweet> currentGift = FXCollections.observableArrayList();
    private boolean isViewingWarehouse = true;

    public void setService(GiftService giftService) {
        this.giftService = giftService;
        try {
            rootPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            menuContainer.getStyleClass().add("glass-pane");
            if (centerContainer != null) {
                centerContainer.getStyleClass().add("glass-pane");
            }
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити стилі: " + e.getMessage());
        }
        setupListViewFormat();
        showMainMenu();
    }

    private void setupListViewFormat() {
        sweetsListView.setCellFactory(param -> new ListCell<Sweet>() {
            @Override
            protected void updateItem(Sweet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String text = String.format("🍬 %s | Виробник: %s | Вага: %.1fг | Цукор: %.1f%% | До: %s",
                            item.getName(), item.getManufacturer(), item.getWeight(), item.getSugar(), item.getExpirationDate());
                    setText(text);
                }
            }
        });
    }

    private void refreshList() {
        if (giftService != null) {
            sweetsListView.setItems(FXCollections.observableArrayList(giftService.getAllSweets()));
        }
    }

    private void showGiftList() {
        sweetsListView.setItems(currentGift);
    }

    private Button createMenuButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.getStyleClass().add("menu-button");
        btn.setOnAction(handler);
        return btn;
    }

    @FXML
    private void showMainMenu() {
        rootPane.setLeft(null);
        rootPane.setCenter(menuContainer);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.getChildren().clear();

        Label title = new Label("Головне меню");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        menuContainer.getChildren().addAll(
                title,
                createMenuButton("Робота зі складом", e -> showWarehouseMenu()),
                createMenuButton("Робота з подарунком", e -> showGiftMenu()),
                //createMenuButton("Симуляція критичного збою", e -> triggerCriticalErrorTest()),
                createMenuButton("Вихід", e -> System.exit(0))
        );
    }

    @FXML
    private void showWarehouseMenu() {
        isViewingWarehouse = true;
        rootPane.setCenter(centerContainer);
        rootPane.setLeft(menuContainer);
        menuContainer.setAlignment(Pos.TOP_CENTER);
        menuContainer.getChildren().clear();

        menuContainer.getChildren().addAll(
                new Label("=== СКЛАД ==="),
                createMenuButton("Створити нові солодощі", e -> openAddWindow()),
                createMenuButton("Видалити вибране", e -> handleDeleteWarehouse()),
                createMenuButton("Видалити прострочені", e -> handleRemoveExpired()),
                createMenuButton("Сортування ->", e -> showSortMenu()),
                createMenuButton("Пошук ->", e -> showSearchMenu()),
                createMenuButton("Імпорт з CSV", e -> importFromCSV()),
                createMenuButton("Назад", e -> showMainMenu())
        );
        refreshList();
    }

    @FXML
    private void showGiftMenu() {
        isViewingWarehouse = false;
        rootPane.setCenter(centerContainer);
        rootPane.setLeft(menuContainer);
        menuContainer.setAlignment(Pos.TOP_CENTER);
        menuContainer.getChildren().clear();

        menuContainer.getChildren().addAll(
                new Label("=== ПОДАРУНОК ==="),
                createMenuButton("Переглянути подарунок", e -> showGiftList()),
                createMenuButton("Додати зі списку всіх", e -> addToGift()),
                createMenuButton("Видалити з подарунка", e -> removeFromGift()),
                createMenuButton("Розрахувати вагу", e -> calculateWeight()),
                createMenuButton("Сортування ->", e -> showSortMenu()),
                createMenuButton("Пошук ->", e -> showSearchMenu()),
                createMenuButton("🎁 Завершити створення!", e -> finalizeGift()),
                createMenuButton("Назад", e -> showMainMenu())
        );
        showGiftList();
    }

    private void showSortMenu() {
        menuContainer.getChildren().clear();
        menuContainer.getChildren().addAll(
                new Label("=== СОРТУВАННЯ ==="),
                createMenuButton("За вагою", e -> handleSortByWeight()),
                createMenuButton("За цукром", e -> handleSortBySugar()),
                createMenuButton("Назад", e -> { if(isViewingWarehouse) showWarehouseMenu(); else showGiftMenu(); })
        );
    }

    private void showSearchMenu() {
        menuContainer.getChildren().clear();
        menuContainer.getChildren().add(new Label("=== ПОШУК ==="));

        if (isViewingWarehouse) {
            menuContainer.getChildren().add(createMenuButton("🎁 Додати вибране в подарунок", e -> addSelectedToGift()));
        }

        menuContainer.getChildren().addAll(
                createMenuButton("За назвою", e -> handleSearchName()),
                createMenuButton("За виробником", e -> handleSearchManufacturer()),
                createMenuButton("За діапазоном цукру", e -> handleSearchSugarRange()),
                createMenuButton("Скинути пошук", e -> { if(isViewingWarehouse) refreshList(); else showGiftList(); }),
                createMenuButton("Назад", e -> { if(isViewingWarehouse) showWarehouseMenu(); else showGiftMenu(); })
        );
    }

    private List<Sweet> getTargetList() {
        return isViewingWarehouse ? giftService.getAllSweets() : currentGift;
    }

    private void handleSortByWeight() {
        List<Sweet> sorted = getTargetList().stream().sorted((s1, s2) -> Double.compare(s1.getWeight(), s2.getWeight())).collect(Collectors.toList());
        sweetsListView.setItems(FXCollections.observableArrayList(sorted));
        logger.info("Користувач провів сортування за вагою.");
    }

    private void handleSortBySugar() {
        List<Sweet> sorted = getTargetList().stream().sorted((s1, s2) -> Double.compare(s1.getSugar(), s2.getSugar())).collect(Collectors.toList());
        sweetsListView.setItems(FXCollections.observableArrayList(sorted));
        logger.info("Користувач провів сортування за цукром.");
    }

    private void handleSearchName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Пошук");
        dialog.setHeaderText("Введіть назву:");
        dialog.showAndWait().ifPresent(n -> {
            List<Sweet> found = getTargetList().stream().filter(s -> s.getName().toLowerCase().contains(n.toLowerCase())).collect(Collectors.toList());
            sweetsListView.setItems(FXCollections.observableArrayList(found));
        });
        logger.info("Користувач провів пошук за назвою.");
    }

    private void handleSearchManufacturer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Пошук");
        dialog.setHeaderText("Введіть виробника:");
        dialog.showAndWait().ifPresent(m -> {
            List<Sweet> found = getTargetList().stream().filter(s -> s.getManufacturer().toLowerCase().contains(m.toLowerCase())).collect(Collectors.toList());
            sweetsListView.setItems(FXCollections.observableArrayList(found));
        });
        logger.info("Користувач провів пошук за виробником.");
    }

    private void handleSearchSugarRange() {
        Dialog<Double[]> dialog = new Dialog<>();
        dialog.setTitle("Фільтр цукру");
        dialog.setHeaderText("Оберіть діапазон (г)");

        ButtonType okButton = new ButtonType("Знайти", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Slider minSlider = new Slider(0, 100, 0); minSlider.setShowTickLabels(true); minSlider.setShowTickMarks(true);
        Slider maxSlider = new Slider(0, 100, 100); maxSlider.setShowTickLabels(true); maxSlider.setShowTickMarks(true);

        dialog.getDialogPane().setContent(new VBox(10, new Label("Від:"), minSlider, new Label("До:"), maxSlider));
        dialog.setResultConverter(btn -> btn == okButton ? new Double[]{minSlider.getValue(), maxSlider.getValue()} : null);

        dialog.showAndWait().ifPresent(range -> {
            List<Sweet> found = getTargetList().stream().filter(s -> s.getSugar() >= range[0] && s.getSugar() <= range[1]).collect(Collectors.toList());
            sweetsListView.setItems(FXCollections.observableArrayList(found));
        });
        logger.info("Користувач провів пошук за діапазоном цукру.");
    }

    @FXML
    private void openAddWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add-sweet-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            AddSweetController controller = loader.getController();
            controller.init(giftService, this::refreshList);
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Помилка відкриття: " + e.getMessage()).show();
        }
    }

    private void handleDeleteWarehouse() {
        Sweet selected = sweetsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            giftService.removeSweetByName(selected.getName());
            refreshList();
        } else {
            new Alert(Alert.AlertType.WARNING, "Виберіть елемент у списку для видалення!").showAndWait();
        }
        logger.info("Користувач видалив елемент.");
    }

    private void handleRemoveExpired() {
        giftService.removeExpiredSweets();
        refreshList();
        new Alert(Alert.AlertType.INFORMATION, "Прострочені солодощі видалено!").show();
        logger.info("Користувач видалив прострочені цукерки.");
    }

    private void addSelectedToGift() {
        Sweet selected = sweetsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Спочатку виберіть солодощі зі списку складу!").showAndWait();
            return;
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Додати у подарунок");
        dialog.setHeaderText("Скільки штук '" + selected.getName() + "' додати?");

        ButtonType okButton = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Slider quantitySlider = new Slider(1, 30, 1);
        quantitySlider.setShowTickLabels(true); quantitySlider.setShowTickMarks(true);
        quantitySlider.setMajorTickUnit(5); quantitySlider.setMinorTickCount(4); quantitySlider.setSnapToTicks(true);

        Label quantityLabel = new Label("Кількість: 1 шт.");
        quantitySlider.valueProperty().addListener((obs, oldVal, newVal) -> quantityLabel.setText("Кількість: " + newVal.intValue() + " шт."));

        dialog.getDialogPane().setContent(new VBox(10, quantityLabel, quantitySlider));
        dialog.setResultConverter(btn -> btn == okButton ? (int) quantitySlider.getValue() : null);

        dialog.showAndWait().ifPresent(count -> {
            for (int i = 0; i < count; i++) currentGift.add(selected);
            new Alert(Alert.AlertType.INFORMATION, "✅ Успішно додано " + count + " шт. '" + selected.getName() + "' у подарунок!").show();
        });
        logger.info("Користувач додав цукерку до подарунку.");
    }

    @FXML
    private void addToGift() {
        List<Sweet> allSweets = giftService.getAllSweets();
        if (allSweets.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Склад порожній! Додайте туди щось спочатку.").showAndWait();
            return;
        }

        Dialog<List<Sweet>> dialog = new Dialog<>();
        dialog.setTitle("Додати у подарунок");
        dialog.setHeaderText("Оберіть солодощі та кількість:");

        ButtonType okButton = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        ComboBox<Sweet> sweetComboBox = new ComboBox<>(FXCollections.observableArrayList(allSweets));
        sweetComboBox.getSelectionModel().selectFirst();
        sweetComboBox.setMaxWidth(Double.MAX_VALUE);

        sweetComboBox.setConverter(new javafx.util.StringConverter<Sweet>() {
            @Override public String toString(Sweet sweet) { return sweet == null ? "" : sweet.getName() + " (" + sweet.getManufacturer() + ") - " + sweet.getWeight() + "г"; }
            @Override public Sweet fromString(String string) { return null; }
        });

        Slider quantitySlider = new Slider(1, 30, 1);
        quantitySlider.setShowTickLabels(true); quantitySlider.setShowTickMarks(true);
        quantitySlider.setMajorTickUnit(5); quantitySlider.setMinorTickCount(4); quantitySlider.setSnapToTicks(true);

        Label quantityLabel = new Label("Кількість: 1 шт.");
        quantitySlider.valueProperty().addListener((obs, oldVal, newVal) -> quantityLabel.setText("Кількість: " + newVal.intValue() + " шт."));

        dialog.getDialogPane().setContent(new VBox(10, new Label("Що додаємо?"), sweetComboBox, quantityLabel, quantitySlider));

        dialog.setResultConverter(btn -> {
            if (btn == okButton) {
                Sweet selected = sweetComboBox.getValue();
                int count = (int) quantitySlider.getValue();
                List<Sweet> sweetsToAdd = new java.util.ArrayList<>();
                for (int i = 0; i < count; i++) sweetsToAdd.add(selected);
                return sweetsToAdd;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sweetsToAdd -> {
            currentGift.addAll(sweetsToAdd);
            showGiftList();
            new Alert(Alert.AlertType.INFORMATION, "✅ Успішно додано " + sweetsToAdd.size() + " шт. '" + sweetsToAdd.get(0).getName() + "' у подарунок!").show();
        });
        logger.info("Користувач додав цукерку до подарунку.");
    }

    private void removeFromGift() {
        Sweet selected = sweetsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentGift.remove(selected);
            showGiftList();
        } else {
            new Alert(Alert.AlertType.WARNING, "Виберіть елемент у подарунку для видалення!").showAndWait();
        }
        logger.info("Користувач видалив цукерку з подарунку.");
    }

    private void calculateWeight() {
        double total = currentGift.stream().mapToDouble(Sweet::getWeight).sum();
        new Alert(Alert.AlertType.INFORMATION, "⚖️ Загальна вага подарунка: " + String.format("%.2f", total) + " г").show();
        logger.info("Користувач обрахував вагу.");
    }

    @FXML
    private void finalizeGift() {
        if (currentGift.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Ваш подарунок порожній! Додайте хоча б одну цукерку, щоб завершити.").showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти чек подарунка");
        fileChooser.setInitialFileName("Gift_Receipt.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстові файли", "*.txt"));

        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            saveReceiptToFile(file); // ВИКЛИК НОВОГО МЕТОДУ
        }
    }

    // НОВИЙ МЕТОД: чиста логіка запису файлу без Windows-вікна
    void saveReceiptToFile(File file) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8);
            writer.println("=========================================");
            writer.println("       🎁 МАЙСТЕРНЯ ПОДАРУНКІВ 🎁       ");
            writer.println("=========================================");
            writer.println("Дата замовлення: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            writer.println("-----------------------------------------");

            double totalWeight = 0;
            double totalSugar = 0;

            for (Sweet s : currentGift) {
                writer.printf("- %-20s | %5.1f г\n", s.getName(), s.getWeight());
                totalWeight += s.getWeight();
                totalSugar += s.getSugar();
            }

            writer.println("-----------------------------------------");
            writer.printf("Загальна кількість:   %d шт.\n", currentGift.size());
            writer.printf("Загальна вага:        %.1f г\n", totalWeight);
            writer.printf("Загальний цукор:      %.1f г\n", totalSugar);
            writer.println("=========================================");
            writer.println("   Дякуємо, що обрали нашу систему!      ");
            writer.println("          Щасливих свят! 🎄✨            ");
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успішно!");
            alert.setHeaderText("🎉 Подарунок успішно зібрано та запаковано! 🎉");
            alert.setContentText(
                    "Ваш ідеальний новорічний подарунок готовий!\n\n" +
                            "• Загальна вага: " + String.format("%.1f", totalWeight) + " г\n" +
                            "• Кількість солодощів: " + currentGift.size() + " шт.\n\n" +
                            "Файл чека збережено як:\n" + file.getName() + "\n\n" +
                            "Коробка знову порожня. Можете збирати наступний подарунок!"
            );
            alert.showAndWait();

            currentGift.clear();
            showGiftList();
            logger.info("Користувач надрукував чек.");

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Не вдалося зберегти чек: " + e.getMessage()).showAndWait();
        }
    }

    private void importFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть файл sweets.csv");
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if (file != null) {
            processCSVFile(file); // ВИКЛИК НОВОГО МЕТОДУ
        }
    }

    // НОВИЙ МЕТОД: чиста логіка читання файлу без Windows-вікна
    void processCSVFile(File file) {
        try {
            List<String> existingNames = giftService.getAllSweets().stream()
                    .map(s -> s.getName().toLowerCase())
                    .collect(Collectors.toList());

            List<String> lines = Files.readAllLines(file.toPath());
            int count = 0, duplicates = 0, failed = 0;

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");

                if (parts.length >= 7) {
                    try {
                        String name = parts[1].trim();
                        if (existingNames.contains(name.toLowerCase())) {
                            duplicates++;
                            continue;
                        }

                        String type = parts[0].trim();
                        double weight = Double.parseDouble(parts[2].trim());
                        double sugar = Double.parseDouble(parts[3].trim());
                        String manufacturer = parts[4].trim();
                        LocalDate expirationDate = LocalDate.parse(parts[5].trim());
                        String rawProperty = parts[6].trim();

                        String specialProperty = "";
                        if ("CHOCOLATE".equals(type)) {
                            specialProperty = rawProperty + "% какао";
                        } else if ("WAFFLE".equals(type)) {
                            specialProperty = rawProperty.equalsIgnoreCase("true") ? "В глазурі" : "Без глазурі";
                        } else {
                            specialProperty = "Начинка: " + rawProperty;
                        }

                        Candy newSweet = new Candy(name, weight, sugar, manufacturer, expirationDate, specialProperty);
                        giftService.addSweetToGift(newSweet);
                        count++;
                        existingNames.add(name.toLowerCase());

                    } catch (Exception ex) {
                        failed++;
                    }
                } else {
                    failed++;
                }
            }
            refreshList();
            String report = "✅ Додано нових товарів: " + count;
            if (duplicates > 0) report += "\n⚠️ Пропущено дублікатів: " + duplicates;
            if (failed > 0) report += "\n❌ Пропущено рядків з помилками: " + failed;

            new Alert(Alert.AlertType.INFORMATION, report).show();
            logger.info("Користувач імпортував власні дані.");

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Помилка читання файлу! " + e.getMessage()).show();
        }
    }

    private void triggerCriticalErrorTest() {
        try {
            int test = 10 / 0;
        } catch (Exception e) {
            com.giftapp.util.EmailSender.sendCriticalError("Тест меню", "Штучна помилка: " + e.toString());
        }
    }
}