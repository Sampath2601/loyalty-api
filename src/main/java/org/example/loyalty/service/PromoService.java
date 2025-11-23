package org.example.loyalty.service;

import io.vertx.core.Future;

import java.util.List;

public interface PromoService {

    // Existing method to get promo bonus
    Future<Double> getPromoBonus(String code, double basePoints);

    // Add this method to return a list of warnings
    List<String> getWarnings();
}
