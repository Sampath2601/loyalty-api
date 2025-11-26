package org.example.loyalty.component.util;

import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

public class HttpClientHelper {

    private final WebClient client;
    private final int port;

    public HttpClientHelper(WebClient client, int port) {
        this.client = client;
        this.port = port;
    }

    public HttpResponse<Buffer> postJson(String path, JsonObject body) throws Exception {
        return client.post(port, "localhost", path)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(body)
                .toCompletionStage().toCompletableFuture()
                .get(5, TimeUnit.SECONDS);
    }

    public HttpResponse<Buffer> get(String path) throws Exception {
        return client.get(port, "localhost", path)
                .send()
                .toCompletionStage().toCompletableFuture()
                .get(5, TimeUnit.SECONDS);
    }
}

