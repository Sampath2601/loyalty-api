package org.example.loyalty.service;

import io.vertx.core.Future;

public interface FxService {
    Future<Double> getRate(String currency);
}
