package org.example.loyalty.component.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.ComponentTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class QuoteFxParameterizedTest extends ComponentTestBase {

    @ParameterizedTest
    @MethodSource("fxProvider")
    void testFxConversion(String currency, double fxRate) throws Exception {

        JsonObject req = new JsonObject()
                .put("fareAmount", 1000)
                .put("currency", currency)
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> resp = http.postJson("/v1/points/quote", req);

        Assertions.assertThat(resp.statusCode()).isEqualTo(200);

        JsonObject json = resp.bodyAsJsonObject();

        Assertions.assertThat(json.getDouble("effectiveFxRate"))
                .isEqualTo(fxRate);

        Assertions.assertThat(json.getDouble("basePoints"))
                .isEqualTo(1000 * fxRate);
    }

    static Stream<Arguments> fxProvider() {
        return Stream.of(
                Arguments.of("USD", 1.0),
                Arguments.of("EUR", 1.1)
        );
    }
}
