package org.example.loyalty.component.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PromoStubs {

    public static void summer25() {
        stubFor(get("/promo/SUMMER25")
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"bonus\":0.25,\"expiresInDays\":1}")));
    }

    public static void promoUnavailable(String code) {
        stubFor(get("/promo/" + code)
                .willReturn(aResponse().withStatus(400)));
    }
}
