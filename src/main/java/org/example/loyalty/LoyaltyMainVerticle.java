package org.example.loyalty;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.handler.BodyHandler;
import org.example.loyalty.api.QuoteHandler;
import org.example.loyalty.service.*;

public class LoyaltyMainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        // Dependency Injection (manual)
        FxService fxService = new FxServiceImpl(vertx);
        PromoService promoService = new PromoServiceImpl(vertx);
        PointsCalculator calculator = new PointsCalculator(fxService, promoService);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/v1/points/quote")
                .handler(new QuoteHandler(calculator));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(46043, http -> { // 0 = random port (needed for component tests)
                    if (http.succeeded()) {
                        System.out.println("Server started on port: " + http.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}

