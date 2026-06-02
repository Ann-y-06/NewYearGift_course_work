package com.giftapp.model;

import java.time.LocalDate;

public abstract class Sweet {
    private String name;
    private double weight;
    private double sugar;
    private String manufacturer;
    private LocalDate expirationDate;

    public Sweet(String name, double weight, double sugar, String manufacturer, LocalDate expirationDate) {
        this.name = name;
        this.weight = weight;
        this.sugar = sugar;
        this.manufacturer = manufacturer;
        this.expirationDate = expirationDate;
    }

    public String getName() { return name; }
    public double getWeight() { return weight; }
    public double getSugar() { return sugar; }
    public String getManufacturer() { return manufacturer; }
    public LocalDate getExpirationDate() { return expirationDate; }

    public double getTotalSugarWeight() {
        return (weight * sugar) / 100.0;
    }

    public abstract String toCsv();

    @Override
    public String toString() {
        return name + " | " + manufacturer + " | " + weight + "г";
    }
}