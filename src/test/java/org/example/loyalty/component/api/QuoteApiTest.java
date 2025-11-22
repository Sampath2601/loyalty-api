package org.example.loyalty.component.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import org.assertj.core.api.Assertions;
import org.example.loyalty.component.base.TestBase;
import org.junit.jupiter.api.Test;

public class QuoteApiTest extends TestBase {

    @Test
    void testQuoteWithPromoSilver() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
        Assertions.assertThat(response.getDouble("totalPoints")).isEqualTo(1729.0);
        Assertions.assertThat(response.getDouble("tierBonus")).isEqualTo(185.25);
        Assertions.assertThat(response.getDouble("promoBonus")).isEqualTo(308.75);
    }


    @Test
    void testQuoteWithPromoGold() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "GOLD")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
        Assertions.assertThat(response.getDouble("totalPoints")).isLessThanOrEqualTo(50000);
        Assertions.assertThat(response.getDouble("tierBonus")).isGreaterThan(0);
        Assertions.assertThat(response.getDouble("promoBonus")).isGreaterThan(0);
    }

    @Test
    void testQuoteWithPromoDefault() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
        Assertions.assertThat(response.getDouble("totalPoints")).isLessThanOrEqualTo(50000);
        Assertions.assertThat(response.getDouble("tierBonus")).isEqualTo(0);
        Assertions.assertThat(response.getDouble("promoBonus")).isGreaterThan(0);
    }

    @Test
    void testQuoteWithPromoCurrencyEuro() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "EUR")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "PLATINUM")
                .put("promoCode", "SUMMER25");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings"))
                .contains("PROMO_EXPIRES_SOON");
        Assertions.assertThat(response.getDouble("totalPoints")).isLessThanOrEqualTo(50000);
        Assertions.assertThat(response.getDouble("tierBonus")).isGreaterThan(0);
        Assertions.assertThat(response.getDouble("promoBonus")).isGreaterThan(0);
    }

    @Test
    void testInvalidFareAmount() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", -5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(400);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getString("error"))
                .contains("Invalid fareAmount");
    }

    @Test
    void testInvalidCurrency() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 100)
                .put("currency", "")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(400);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getString("error"))
                .contains("Invalid currency");
    }

    @Test
    void testPointsCap() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 200000) // high fare to exceed cap
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "PLATINUM");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();
        Assertions.assertThat(response.getDouble("totalPoints")).isLessThanOrEqualTo(50000);
    }

    @Test
    void testQuoteWithNoPromo() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "ECONOMY")
                .put("customerTier", "SILVER");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(200);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getJsonArray("warnings")).isEmpty();
        Assertions.assertThat(response.getDouble("promoBonus")).isEqualTo(0);
    }

    @Test
    void testQuoteWithNoCabinClass() throws Exception {
        JsonObject request = new JsonObject()
                .put("fareAmount", 1234.5)
                .put("currency", "USD")
                .put("cabinClass", "")
                .put("customerTier", "SILVER");

        HttpResponse<Buffer> bufferHttpResponse = helper.executeQuote(request);

        Assertions.assertThat(bufferHttpResponse.statusCode()).isEqualTo(400);

        JsonObject response = bufferHttpResponse.bodyAsJsonObject();

        Assertions.assertThat(response.getString("error"))
                .contains("Invalid cabinClass");
    }


}




