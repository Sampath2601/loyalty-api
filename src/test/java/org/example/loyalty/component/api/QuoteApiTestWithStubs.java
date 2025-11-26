package org.example.loyalty.component.api;

import io.vertx.core.json.JsonObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.ComponentTestBase;
import org.example.loyalty.component.stubs.FxStubs;
import org.example.loyalty.component.stubs.PromoStubs;
import org.junit.jupiter.api.Test;

public class QuoteApiTestWithStubs extends ComponentTestBase {

    @Test
    void testSilverWithPromo() throws Exception {

        FxStubs.usdOK(1.0);
        PromoStubs.summer25();

        JsonObject req = new JsonObject()
                .put("fareAmount", 500)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);
        JsonObject body = resp.bodyAsJsonObject();

        Assertions.assertThat(body.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
    }


    @Test
    void testPromoUnavailable() throws Exception {

        FxStubs.usdOK(1.0);
        PromoStubs.promoUnavailable("FAILME");

        JsonObject req = new JsonObject()
                .put("fareAmount", 400)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("promoCode", "FAILME");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);
        Assertions.assertThat(resp.bodyAsJsonObject()
                        .getJsonArray("warnings"))
                .contains("PROMO_UNAVAILABLE");
    }

    @Test
    void testPointsCap() throws Exception {

        FxStubs.usdOK(1.0);

        JsonObject req = new JsonObject()
                .put("fareAmount", 200000)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "PLATINUM")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);
        Assertions.assertThat(resp.bodyAsJsonObject()
                        .getDouble("totalPoints"))
                .isLessThanOrEqualTo(50000);
    }

    @Test
    void testQuoteWithPromoSilver() throws Exception {
        JsonObject req = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> bufferHttpResponse = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
        Assertions.assertThat(response.getDouble("totalPoints")).isEqualTo(1729.0);
        Assertions.assertThat(response.getDouble("tierBonus")).isEqualTo(185.25);
        Assertions.assertThat(response.getDouble("promoBonus")).isEqualTo(308.75);
    }

}

