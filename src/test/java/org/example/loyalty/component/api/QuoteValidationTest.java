package org.example.loyalty.component.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.ComponentTestBase;
import org.junit.jupiter.api.Test;

public class QuoteValidationTest extends ComponentTestBase {

    @Test
    void testNegativeFare() throws Exception {
        JsonObject req = new JsonObject()
                .put("fareAmount", -1)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);
        Assertions.assertThat(resp.statusCode()).isEqualTo(400);
        Assertions.assertThat(resp.bodyAsJsonObject().getString("error"))
                .contains("Invalid fareAmount");
    }

    @Test
    void testMissingCurrency() throws Exception {
        JsonObject req = new JsonObject()
                .put("fareAmount", 1000)
                .put("cabinClass", "ECONOMY");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);
        Assertions.assertThat(resp.statusCode()).isEqualTo(400);
        Assertions.assertThat(resp.bodyAsJsonObject().getString("error"))
                .contains("Invalid currency");
    }

    @Test
    void testMissingCabinClass() throws Exception {
        JsonObject req = new JsonObject()
                .put("fareAmount", 1000)
                .put("currency", "USD")
                .put("cabinClass", "");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);
        Assertions.assertThat(resp.statusCode()).isEqualTo(400);
        Assertions.assertThat(resp.bodyAsJsonObject().getString("error"))
                .contains("Invalid cabinClass");
    }
}

