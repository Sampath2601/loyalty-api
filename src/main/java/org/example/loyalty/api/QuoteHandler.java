package org.example.loyalty.api;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.example.loyalty.model.FareRequest;
import org.example.loyalty.model.PointsResponse;
import org.example.loyalty.service.PointsCalculator;

/**
 * QuoteHandler is responsible for handling requests to calculate loyalty points.
 * It validates the incoming JSON, invokes the PointsCalculator service, and returns
 * a structured JSON response with points details and warnings.
 */
public class QuoteHandler implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(QuoteHandler.class);
    private final PointsCalculator calculator;

    public QuoteHandler(PointsCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void handle(RoutingContext ctx) {

        // Extract JSON body from request
        JsonObject body = ctx.getBodyAsJson();

        // Validate JSON body
        if (body == null) {
            ctx.response()
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Invalid or empty JSON body").encode());
            logger.warn("Empty JSON body received");
            return;
        }

        // Map JSON to FareRequest model
        FareRequest request = new FareRequest(
                body.getDouble("fareAmount"),
                body.getString("currency"),
                body.getString("cabinClass"),
                body.getString("customerTier"),
                body.getString("promoCode")
        );

        // Call PointsCalculator to compute points asynchronously
        calculator.calculate(request)
                .onSuccess(resp -> ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(toJson(resp).encode()))
                .onFailure(err -> ctx.response()
                        .setStatusCode(400)
                        .end(new JsonObject().put("error", err.getMessage()).encode()));
    }

    /**
     * Converts PointsResponse model to a structured JsonObject for HTTP response.
     */
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


