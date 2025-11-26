# Loyalty Points Service

A Vert.x-based service to calculate **loyalty points** for flight fares, considering FX rates, customer tiers, and promotional codes.

---

## Features

- Calculate points asynchronously with FX conversion and promo bonuses.
- Supports tier multipliers: Silver, Gold, Platinum.
- Caps total points at 50,000.
- Handles FX service retries and promo expiry warnings.
- REST API endpoints for FX, Promo, and Points Quote.
- Component tests using JUnit 5, AssertJ, and WireMock.

---

## API Endpoints

### Get FX Rate
GET /fx/:currency
- Example: `/fx/USD`
- Response:
  {
  "rate": 1.0
  }

### Get Promo Bonus
GET /promo/:code
- Example: `/promo/SUMMER25`
- Response:
  {
  "bonus": 0.25,
  "expiresInDays": 1
  }

### Quote Points
POST /v1/points/quote
- Request:
  {
  "fareAmount": 1000,
  "currency": "USD",
  "cabinClass": "ECONOMY",
  "customerTier": "SILVER",
  "promoCode": "SUMMER25"
  }
- Response:
  {
  "basePoints": 1000,
  "tierBonus": 150,
  "promoBonus": 250,
  "totalPoints": 1400,
  "effectiveFxRate": 1.0,
  "warnings": ["PROMO_EXPIRES_SOON"]
  }

---

## Setup & Run

### Requirements
- Java 11
- Maven 3.8

### Build
mvn clean install

### Run
mvn exec:java -Dexec.mainClass="org.example.loyalty.Launcher"
Server starts on a **random port** (displayed in console).

### Example Request
curl -X POST http://localhost:<port>/v1/points/quote \
-H "Content-Type: application/json" \
-d '{"fareAmount":1000,"currency":"USD","cabinClass":"ECONOMY"}'

---

## Testing

- Component tests spin up the Vert.x server on a random port.
- WireMock stubs simulate FX and Promo endpoints.
- Run all tests:
  mvn verify
- Covers edge-cases: negative fares, missing fields, promo expiry, FX failures.

---

## Logging & Observability

- Logs key events, errors, retries, and warnings.
- Helps debug points calculations and external service calls.

