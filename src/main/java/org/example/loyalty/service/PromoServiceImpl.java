package org.example.loyalty.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class PromoServiceImpl implements PromoService {

    private final List<String> warnings = new ArrayList<>();

    public PromoServiceImpl(Vertx vertx) {}

    @Override
    public Future<Double> getPromoBonus(String code, double basePoints) {

        warnings.clear();

        if (code == null) return Future.succeededFuture(0.0);

        if (code.equals("SUMMER25")) {
            warnings.add("PROMO_EXPIRES_SOON");
            return Future.succeededFuture(basePoints * 0.25);
        }

        return Future.succeededFuture(0.0);
    }

    @Override
    public List<String> getWarnings() {
        return warnings;
    }
}
