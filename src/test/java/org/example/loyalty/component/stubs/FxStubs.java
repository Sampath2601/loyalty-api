package org.example.loyalty.component.stubs;

import com.github.tomakehurst.wiremock.stubbing.Scenario;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FxStubs {

    public static void usdOK(double rate) {
        stubFor(get("/fx/USD")
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"rate\":" + rate + "}")));
    }

    public static void failUSD() {
        stubFor(get("/fx/USD")
                .willReturn(aResponse().withStatus(400)));
    }

}
