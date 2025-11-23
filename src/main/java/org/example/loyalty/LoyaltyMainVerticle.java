package org.example.loyalty;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.loyalty.api.QuoteHandler;
import org.example.loyalty.service.*;
import org.example.loyalty.util.PortUtil;

/**
 * Main Vert.x verticle for Loyalty service.
 * Sets up FX/Promo endpoints and the main quote API.
 * Starts server on a random port and stores it for service use.
 */
public class LoyaltyMainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(LoyaltyMainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {

        logger.info("Starting LoyaltyMainVerticle...");

        // First create router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Start HTTP server on a random port
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(0, http -> {

                    if (!http.succeeded()) {
                        logger.error("Failed to start HTTP server", http.cause());
                        startPromise.fail(http.cause());
                        return;
                    }

                    // Save the actual port so services can use it
                    int actualPort = http.result().actualPort();
                    PortUtil.setPort(actualPort);
                    logger.info("Server running on port "+  actualPort);
                    System.out.println("Server running on port = " + actualPort);

                    // Now we can build services safely
                    FxService fxService = new FxServiceImpl(vertx, actualPort);
                    PromoService promoService = new PromoServiceImpl(vertx);
                    PointsCalculator calculator = new PointsCalculator(fxService, promoService);

                    // ------------------- FX endpoint -------------------
                    router.get("/fx/:currency").handler(ctx -> {
                        String currency = ctx.pathParam("currency");
                        JsonObject response = new JsonObject();

                        if ("USD".equalsIgnoreCase(currency)) {
                            response.put("rate", 1.0);
                        } else if ("EUR".equalsIgnoreCase(currency)) {
                            response.put("rate", 1.1);
                        } else {
                            ctx.response()
                                    .setStatusCode(400)
                                    .end(new JsonObject().put("error", "Unknown currency").encode());
                            return;
                        }

                        ctx.json(response);
                    });

                    // ------------------- Promo endpoint -------------------
                    router.get("/promo/:code").handler(ctx -> {
                        String code = ctx.pathParam("code");

                        vertx.setTimer(80, id -> {
                            JsonObject response = new JsonObject();
                            if ("SUMMER25".equalsIgnoreCase(code)) {
                                response.put("bonus", 0.25)
                                        .put("expiresInDays", 1);
                            } else {
                                response.put("bonus", 0.0)
                                        .put("expiresInDays", 10);
                            }

                            ctx.response()
                                    .putHeader("Content-Type", "application/json")
                                    .end(response.encode());
                        });
                    });

                    // ------------------- Main Quote API -------------------
                    router.post("/v1/points/quote")
                            .handler(new QuoteHandler(calculator));

                    // Now fully started
                    startPromise.complete();
                    logger.info("Api started successfully");
                });
    }
}
