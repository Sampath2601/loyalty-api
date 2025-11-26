package org.example.loyalty.component.base;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.example.loyalty.LoyaltyMainVerticle;
import org.example.loyalty.util.PortUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VertxAppRunner {

    private Vertx vertx;

    public Vertx startApp() throws Exception {
        vertx = Vertx.vertx();

        CompletableFuture<Void> future = new CompletableFuture<>();

        vertx.deployVerticle(new LoyaltyMainVerticle(), new DeploymentOptions(), ar -> {
            if (ar.succeeded()) future.complete(null);
            else future.completeExceptionally(ar.cause());
        });

        future.get(6, TimeUnit.SECONDS);
        return vertx;
    }

    public void stopApp() throws Exception {
        if (vertx != null) {
            vertx.close()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(5, TimeUnit.SECONDS);
        }
    }

    public int appPort() {
        return PortUtil.getPort();
    }
}

