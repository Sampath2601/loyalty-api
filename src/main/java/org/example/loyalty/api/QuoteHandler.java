package org.example.loyalty.api;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.example.loyalty.model.FareRequest;
import org.example.loyalty.model.PointsResponse;
import org.example.loyalty.service.PointsCalculator;

public class QuoteHandler implements Handler<RoutingContext> {

    private final PointsCalculator calculator;

    public QuoteHandler(PointsCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void handle(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();

        if (body == null) {
            ctx.response()
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Invalid or empty JSON body").encode());
            return;
        }

        FareRequest request = new FareRequest(
                body.getDouble("fareAmount"),
                body.getString("currency"),
                body.getString("cabinClass"),
                body.getString("customerTier"),
                body.getString("promoCode")
        );

        calculator.calculate(request)
                .onSuccess(resp -> ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(toJson(resp).encode()))
                .onFailure(err -> ctx.response()
                        .setStatusCode(400)
                        .end(new JsonObject().put("error", err.getMessage()).encode()));
    }

    private JsonObject toJson(PointsResponse resp) {
        return new JsonObject()
                .put("basePoints", resp.getBasePoints())
                .put("tierBonus", resp.getTierBonus())
                .put("promoBonus", resp.getPromoBonus())
                .put("totalPoints", resp.getTotalPoints())
                .put("effectiveFxRate", resp.getEffectiveFxRate())
                .put("warnings", resp.getWarnings());
    }
}


