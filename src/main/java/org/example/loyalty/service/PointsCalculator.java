package org.example.loyalty.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


import io.vertx.core.Future;
import org.example.loyalty.model.FareRequest;
import org.example.loyalty.model.PointsResponse;


import java.util.Collections;

public class PointsCalculator {

    private final FxService fxService;
    private final PromoService promoService;
    private static final long MAX_POINTS = 50000;

    public PointsCalculator(FxService fxService, PromoService promoService) {
        this.fxService = fxService;
        this.promoService = promoService;
    }

    public Future<PointsResponse> calculate(FareRequest request) {
        if (request.getFareAmount() <= 0) {
            return Future.failedFuture(new IllegalArgumentException("Invalid fareAmount"));
        }

        // Validate currency/cabin
        if (request.getCurrency() == null || request.getCurrency().isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Invalid currency"));
        }
        if (request.getCabinClass() == null || request.getCabinClass().isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Invalid cabinClass"));
        }

        return fxService.getRate(request.getCurrency())
                .compose(fxRate -> {
                    long basePoints = Math.round(request.getFareAmount() * fxRate);
                    double tierMultiplier = getTierMultiplier(request.getCustomerTier());
                    double tierBonus = basePoints * tierMultiplier;

                    return promoService.getPromoBonus(request.getPromoCode(), basePoints)
                            .map(promoBonus -> {
                                double total = basePoints + tierBonus + promoBonus;
                                if (total > MAX_POINTS) total = MAX_POINTS;

                                return new PointsResponse(
                                        basePoints,
                                        tierBonus,
                                        promoBonus,
                                        total,
                                        fxRate,
                                        promoService.getWarnings() != null ? promoService.getWarnings() : Collections.emptyList()
                                );
                            });
                });
    }

    private double getTierMultiplier(String tier) {
        if (tier == null) return 0.0;
        switch (tier.toUpperCase()) {
            case "SILVER":
                return 0.15;
            case "GOLD":
                return 0.30;
            case "PLATINUM":
                return 0.50;
            default:
                return 0.0;
        }
    }
}

