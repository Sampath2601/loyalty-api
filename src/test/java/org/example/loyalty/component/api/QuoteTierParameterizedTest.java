package org.example.loyalty.component.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.TestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class QuoteTierParameterizedTest extends TestBase {

    @ParameterizedTest
    @MethodSource("tierDataProvider")
    void testTierMultiplier(String tier, double expectedMultiplier) throws Exception {

        JsonObject req = new JsonObject()
                .put("fareAmount", 1000)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", tier)
                .put("promoCode", "NONE");

        HttpResponse<Buffer> resp = helper.executeQuote(req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);

        JsonObject json = resp.bodyAsJsonObject();
        double tierBonus = json.getDouble("tierBonus");

        Assertions.assertThat(tierBonus)
                .isEqualTo(1000 * expectedMultiplier);
    }

    static Stream<Arguments> tierDataProvider() {
        return Stream.of(
                Arguments.of("NONE", 0.0),
                Arguments.of("SILVER", 0.15),
                Arguments.of("GOLD", 0.30),
                Arguments.of("PLATINUM", 0.50)
        );
    }

}
