package com.giftapp.model;

import java.time.LocalDate;
import java.util.Locale;

public class Chocolate extends Sweet {
    private int cocoaPercent;

    public Chocolate(String name, double weight, double sugar, String manufacturer, LocalDate expirationDate, int cocoaPercent) {
        super(name, weight, sugar, manufacturer, expirationDate);
        this.cocoaPercent = cocoaPercent;
    }

    public int getCocoaPercentage() {
        return cocoaPercent; // Переконайтеся, що назва поля збігається
    }

    @Override
    public String toCsv() {
        return String.format(Locale.US, "CHOCOLATE,%s,%.2f,%.2f,%s,%s,%d",
                getName(), getWeight(), getSugar(), getManufacturer(), getExpirationDate(), cocoaPercent);
    }

    @Override
    public String toString() {
        return super.toString() + " | [Шоколад: " + cocoaPercent + "% какао]";
    }
}