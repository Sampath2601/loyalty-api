package org.example.loyalty.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class FxServiceImpl implements FxService {

    private final Vertx vertx;

    public FxServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Future<Double> getRate(String currency) {
        // Simulated: real implementation calls external service
        if (currency.equals("USD")) return Future.succeededFuture(1.0);
        if (currency.equals("EUR")) return Future.succeededFuture(1.1);

        return Future.failedFuture("Unknown currency");
    }
}
