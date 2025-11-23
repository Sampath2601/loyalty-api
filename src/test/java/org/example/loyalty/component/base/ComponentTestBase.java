package org.example.loyalty.component.base;

import io.vertx.ext.web.client.WebClient;
import org.example.loyalty.component.util.HttpClientHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ComponentTestBase {

    protected static WireMockSupport mock;
    protected static VertxAppRunner app;
    protected static HttpClientHelper http;

    @BeforeAll
    static void init() throws Exception {
        mock = new WireMockSupport();
        mock.start();

        app = new VertxAppRunner();
        var vertx = app.startApp();

        http = new HttpClientHelper(WebClient.create(vertx), app.appPort());
    }

    @AfterAll
    static void cleanup() throws Exception {
        mock.stop();
        app.stopApp();
    }
}

