package org.example.loyalty.component.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import org.example.loyalty.LoyaltyMainVerticle;
import org.example.loyalty.component.util.TestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public abstract class TestBase {

    protected static Vertx vertx;
    protected static WebClient webClient;
    protected static int port;
    protected static TestHelper helper;
    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() throws Exception {
        // Start WireMock
        wireMockServer = new WireMockServer(0); // random port
        wireMockServer.start();

        // Configure stubs for FX/Promo services
        wireMockServer.stubFor(get(urlPathMatching("/fx/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"rate\":1.0}"))); // can customize per currency
        wireMockServer.stubFor(get(urlPathMatching("/promo/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"bonus\":0.25,\"warning\":\"PROMO_EXPIRES_SOON\"}")));

        int wireMockPort = wireMockServer.port();
        System.out.println("WireMock started at port: " + wireMockPort);

        // Spin up Vert.x on random port
        vertx = Vertx.vertx();
        webClient = WebClient.create(vertx);

        CompletableFuture<Void> deployFuture = new CompletableFuture<>();
        vertx.deployVerticle(new LoyaltyMainVerticle(), new DeploymentOptions(), ar -> {
            if (ar.succeeded()) deployFuture.complete(null);
            else deployFuture.completeExceptionally(ar.cause());
        });

        deployFuture.get(5, TimeUnit.SECONDS);

        // For simplicity, using hardcoded port from verticle (could be 0 for random)
        port = 46043;

        helper = new TestHelper(webClient, port);
    }

    @AfterAll
    static void teardown() throws Exception {
        if (vertx != null)
            vertx.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS);

        if (wireMockServer != null)
            wireMockServer.stop();
    }
}



