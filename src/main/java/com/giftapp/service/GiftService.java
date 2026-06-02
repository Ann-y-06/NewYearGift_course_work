package com.giftapp.service;

import com.giftapp.model.Sweet;
import java.util.List;

public interface GiftService {
    List<Sweet> getAllSweets();
    void addSweetToGift(Sweet sweet);
    void removeSweetByName(String name);
    void removeExpiredSweets();
    List<Sweet> getSortedByWeight();
    List<Sweet> getSortedBySugar();
    List<Sweet> findByManufacturer(String manufacturer);
    List<Sweet> findBySugarRange(double min, double max);
}