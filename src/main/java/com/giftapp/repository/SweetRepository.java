package com.giftapp.repository;

import com.giftapp.model.Sweet;
import java.util.List;

public interface SweetRepository {
    // Отримати всі солодощі з бази
    List<Sweet> getAllSweets();

    // Додати нові солодощі в базу
    void addSweet(Sweet sweet);

    // Видалити солодощі за її назвою
    void deleteByName(String name);

}