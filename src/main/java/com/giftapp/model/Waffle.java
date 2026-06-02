package com.giftapp.model;

import java.time.LocalDate;
import java.util.Locale;

public class Waffle extends Sweet {
    private boolean hasGlaze;

    public Waffle(String name, double weight, double sugar, String manufacturer, LocalDate expirationDate, boolean hasGlaze) {
        super(name, weight, sugar, manufacturer, expirationDate);
        this.hasGlaze = hasGlaze;
    }

    public boolean isHasGlaze() {
        return hasGlaze; // Переконайтеся, що назва поля збігається
    }

    @Override
    public String toCsv() {
        return String.format(Locale.US, "WAFFLE,%s,%.2f,%.2f,%s,%s,%s",
                getName(), getWeight(), getSugar(), getManufacturer(), getExpirationDate(), hasGlaze);
    }

    @Override
    public String toString() {
        return super.toString() + " | [Вафля: " + (hasGlaze ? "в глазурі" : "без глазурі") + "]";
    }
}