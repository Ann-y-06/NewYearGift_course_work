package com.giftapp.ui;

import com.giftapp.model.Candy; // Обов'язково свій імпорт
import com.giftapp.service.GiftService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class AddSweetController {
    @FXML private TextField nameField;
    @FXML private TextField weightField;
    @FXML private TextField sugarField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private VBox dynamicFieldContainer;

    // Динамічні поля
    private TextField cacaoField;
    private TextField fillingField;
    private CheckBox glazeCheckBox;

    private GiftService service;
    private Runnable onSaveCallback;

    public void init(GiftService service, Runnable onSaveCallback) {
        this.service = service;
        this.onSaveCallback = onSaveCallback;
    }

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("Шоколад", "Цукерка", "Вафля");
        datePicker.setValue(LocalDate.now().plusMonths(6));

        // Надійний спосіб відслідковувати вибір
        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDynamicFields(newValue);
        });
    }

    private void updateDynamicFields(String type) {
        dynamicFieldContainer.getChildren().clear();

        if ("Шоколад".equals(type)) {
            cacaoField = new TextField();
            cacaoField.setPromptText("Відсоток какао (наприклад, 56)");
            dynamicFieldContainer.getChildren().add(cacaoField);

        } else if ("Цукерка".equals(type)) {
            fillingField = new TextField();
            fillingField.setPromptText("Введіть начинку (наприклад, помадка)");
            dynamicFieldContainer.getChildren().add(fillingField);

        } else if ("Вафля".equals(type)) {
            glazeCheckBox = new CheckBox("Є шоколадна глазур?");
            dynamicFieldContainer.getChildren().add(glazeCheckBox);
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (nameField.getText().isEmpty() || typeComboBox.getValue() == null) {
                throw new Exception("Заповніть назву та оберіть тип!");
            }

            String name = nameField.getText();
            double weight = Double.parseDouble(weightField.getText());
            double sugar = Double.parseDouble(sugarField.getText());
            LocalDate expirationDate = datePicker.getValue();
            String type = typeComboBox.getValue();

            String specialProperty = "";
            if ("Шоколад".equals(type) && cacaoField != null) {
                specialProperty = cacaoField.getText() + "% какао";
            } else if ("Цукерка".equals(type) && fillingField != null) {
                specialProperty = "Начинка: " + fillingField.getText();
            } else if ("Вафля".equals(type) && glazeCheckBox != null) {
                specialProperty = glazeCheckBox.isSelected() ? "В глазурі" : "Без глазурі";
            }

            Candy newSweet = new Candy(name, weight, sugar, "Власний виробник", expirationDate, specialProperty);
            service.addSweetToGift(newSweet);

            onSaveCallback.run();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Вага та цукор мають бути числами!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}