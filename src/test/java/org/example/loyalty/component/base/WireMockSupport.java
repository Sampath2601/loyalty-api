package org.example.loyalty.component.base;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockSupport {

    protected WireMockServer wireMock;

    public void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        configureFor("localhost", wireMock.port());
    }

    public void stop() {
        if (wireMock != null) wireMock.stop();
    }

    public int port() {
        return wireMock.port();
    }

    public void reset() {
        wireMock.resetAll();
    }
}

