package org.example.loyalty;
import io.vertx.core.Vertx;

public class Launcher {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new LoyaltyMainVerticle(), ar -> {
            if (ar.succeeded()) {
                System.out.println("LoyaltyMainVerticle deployed successfully");
            } else {
                ar.cause().printStackTrace();
            }
        });
    }
}

