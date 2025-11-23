package org.example.loyalty.component.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.ComponentTestBase;
import org.junit.jupiter.api.Test;

public class QuoteMaxPointsTest extends ComponentTestBase {

    @Test
    void testPointsAreCappedAt50000() throws Exception {
        JsonObject req = new JsonObject()
                .put("fareAmount", 100000)   // ensure > 50,000 after fx & promo
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "PLATINUM")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);

        Assertions.assertThat(resp.bodyAsJsonObject().getDouble("totalPoints"))
                .isEqualTo(50000.0);
    }
}

