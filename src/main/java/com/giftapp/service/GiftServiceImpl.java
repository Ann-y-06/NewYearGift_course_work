package com.giftapp.service;

import com.giftapp.model.Sweet;
import com.giftapp.repository.SweetRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.logging.Logger;

public class GiftServiceImpl implements GiftService {
    private static final Logger logger = Logger.getLogger(GiftServiceImpl.class.getName());
    private final SweetRepository repository;
    private final List<Sweet> currentGift; // Подарунок, який зараз збирається

    public GiftServiceImpl(SweetRepository repository) {
        this.repository = repository;
        this.currentGift = new ArrayList<>();
    }

    // Сортування
    public List<Sweet> getSortedByWeight() {
        return getAllSweets().stream().sorted(Comparator.comparingDouble(Sweet::getWeight)).collect(Collectors.toList());
    }

    public List<Sweet> getSortedBySugar() {
        return getAllSweets().stream().sorted(Comparator.comparingDouble(Sweet::getSugar)).collect(Collectors.toList());
    }

    // Пошук та аналіз
    public List<Sweet> findByManufacturer(String manufacturer) {
        return getAllSweets().stream()
                .filter(s -> s.getManufacturer().equalsIgnoreCase(manufacturer))
                .collect(Collectors.toList());
    }

    public List<Sweet> findBySugarRange(double min, double max) {
        return getAllSweets().stream()
                .filter(s -> s.getSugar() >= min && s.getSugar() <= max)
                .collect(Collectors.toList());
    }

    public void removeExpiredSweets() {
        LocalDate today = LocalDate.now();
        List<Sweet> all = getAllSweets();
        for (Sweet s : all) {
            if (s.getExpirationDate().isBefore(today)) {
                repository.deleteByName(s.getName()); // Видаляємо з БД
            }
        }
    }

    @Override
    public List<Sweet> getAllSweets() {
        return repository.getAllSweets();
    }

    @Override
    public void addSweetToGift(Sweet sweet) {
        repository.addSweet(sweet);
        currentGift.add(sweet);
        // Фіксуємо успішну дію у файл gift-app.log
        logger.info("Додано цукерку до подарунка: " + sweet.getName());
    }

    @Override
    public void removeSweetByName(String name) {
        repository.deleteByName(name); // Викликаємо метод репозиторію
    }
}