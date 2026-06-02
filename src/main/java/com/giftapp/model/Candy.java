package com.giftapp.model;

import java.time.LocalDate;
import java.util.Locale;

public class Candy extends Sweet {
    private String filling;

    public Candy(String name, double weight, double sugar, String manufacturer, LocalDate expirationDate, String filling) {
        super(name, weight, sugar, manufacturer, expirationDate);
        this.filling = filling;
    }

    public String getFillingType() {
        return filling;
    }

    @Override
    public String toCsv() {
        return String.format(Locale.US, "CANDY,%s,%.2f,%.2f,%s,%s,%s",
                getName(), getWeight(), getSugar(), getManufacturer(), getExpirationDate(), filling);
    }

    @Override
    public String toString() {
        return super.toString() + " | [Цукерка: " + filling + "]";
    }
}