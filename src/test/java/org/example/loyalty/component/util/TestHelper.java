package org.example.loyalty.component.util;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.concurrent.TimeUnit;

public class TestHelper {

    private final WebClient webClient;
    private final int port;

    public TestHelper(WebClient webClient, int port) {
        this.webClient = webClient;
        this.port = port;
    }

    public Future<HttpResponse<Buffer>> postQuote(JsonObject request) {
        return webClient.post(port, "localhost", "/v1/points/quote")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(request);
    }

    public HttpResponse<Buffer> executeQuote(JsonObject request) throws Exception {
        return postQuote(request)
                .toCompletionStage().toCompletableFuture()
                .get(5, TimeUnit.SECONDS);
    }
}

