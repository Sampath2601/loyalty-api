package org.example.loyalty.service;

import io.vertx.core.Future;

public interface PromoService {
    Future<Double> getPromoBonus(String code, double basePoints);
    java.util.List<String> getWarnings();
}
