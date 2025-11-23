package org.example.loyalty.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.json.JsonObject;
import org.example.loyalty.util.PortUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PromoServiceImpl fetches promo bonuses for a given promo code asynchronously.
 * Maintains a list of warnings for expiring or unavailable promotions.
 */
public class PromoServiceImpl implements PromoService {

    private static final Logger logger = LoggerFactory.getLogger(PromoServiceImpl.class);

    private final WebClient client;
    private final List<String> warnings = new ArrayList<>();

    public PromoServiceImpl(Vertx vertx) {
        this.client = WebClient.create(vertx);
    }

    /**
     * Returns the promo bonus for a given code and base points.
     * Warnings are added for soon-to-expire or unavailable promotions.
     */
    @Override
    public Future<Double> getPromoBonus(String code, double basePoints) {

        int port = PortUtil.getPort();

        if (code == null || code.isEmpty()) {
            logger.warn("Invalid promo code");
            return Future.failedFuture("Invalid promo code");
        }

        return client.get(port, "localhost", "/promo/" + code) // Use port 8089 for Promo service
                .timeout(1000)
                .send()
                .map(resp -> {
                    warnings.clear();
                    JsonObject json = resp.bodyAsJsonObject();
                    double bonus = json.getDouble("bonus", 0.0);
                    if (bonus > 0) {
                        if (json.getInteger("expiresInDays") <= 1) {
                            warnings.add("PROMO_EXPIRES_SOON");
                        }
                    } else {
                        warnings.add("PROMO_UNAVAILABLE");
                    }
                    return basePoints * bonus;
                })
                .recover(err -> {
                    warnings.add("PROMO_UNAVAILABLE");
                    return Future.succeededFuture(0.0);
                });
    }

    /**
     * Returns an unmodifiable list of warnings from the last promo check.
     */
    @Override
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings); // Returns the warnings list
    }
}
