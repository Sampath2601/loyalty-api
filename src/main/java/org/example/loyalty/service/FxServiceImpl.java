package org.example.loyalty.service;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * FxServiceImpl fetches foreign exchange rates from an FX endpoint.
 * It supports retries, timeout handling, and returns rates asynchronously.
 */
public class FxServiceImpl implements FxService {

    private static final Logger logger = LoggerFactory.getLogger(FxServiceImpl.class);

    private final WebClient webClient;
    private final Vertx vertx;
    private final int fxPort;
    private static final int MAX_RETRIES = 2;
    private static final long TIMEOUT_MS = 1500; // 1.5s timeout

    public FxServiceImpl(Vertx vertx, int fxPort) {
        this.vertx = vertx;
        this.fxPort = fxPort;
        this.webClient = WebClient.create(vertx);
    }

    /**
     * Fetch FX rate for the given currency asynchronously, with retry logic.
     */
    @Override
    public Future<Double> getRate(String currency) {
        logger.info("Fetching FX rate for currency "+ currency);
        Promise<Double> promise = Promise.promise();
        attempt(currency, 0, promise);
        return promise.future();
    }

    /**
     *  attempt to fetch FX rate, retrying up to MAX_RETRIES.
     */
    private void attempt(String currency, int retry, Promise<Double> promise) {
        HttpRequest<Buffer> request = webClient.get(fxPort, "localhost", "/fx/" + currency);

        withTimeout(request.send(), TIMEOUT_MS)
                .onSuccess(resp -> {
                    if (resp.statusCode() == 200) {
                        JsonObject json = resp.bodyAsJsonObject();
                        promise.tryComplete(json.getDouble("rate"));
                    } else {
                        logger.warn("Failed to get FX rate for "+ currency + "status" + resp.statusCode());
                        promise.tryFail("Failed to get FX rate");
                    }
                })
                .onFailure(err -> {
                    if (retry < MAX_RETRIES) {
                        attempt(currency, retry + 1, promise);
                    } else {
                        logger.error("FX service unavailable after "+ MAX_RETRIES+1 + " retries for "+ currency);
                        promise.tryFail("FX service unavailable after retries");
                    }
                });
    }

    /**
     * Wraps a Future with a timeout, failing if it exceeds the specified duration.
     */
    private <T> Future<T> withTimeout(Future<T> future, long timeoutMs) {
        Promise<T> timeoutPromise = Promise.promise();
        long timerId = vertx.setTimer(timeoutMs, id -> timeoutPromise.tryFail("Operation timed out"));
        future.onComplete(ar -> {
            vertx.cancelTimer(timerId);
            timeoutPromise.handle(ar);
        });
        return timeoutPromise.future();
    }
}
