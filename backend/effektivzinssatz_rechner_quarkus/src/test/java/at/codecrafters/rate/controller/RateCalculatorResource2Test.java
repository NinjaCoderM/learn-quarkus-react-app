package at.codecrafters.rate.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class RateCalculatorResource2Test {

    @Test
    void testCalculateInterest() {
        RateCalculatorResource.CalculationRequest request = new RateCalculatorResource.CalculationRequest(
                10, // Laufzeit
                5,  // EinzahlungsDauer
                12, // Zahlungen pro Jahr
                BigDecimal.valueOf(100), // EinzahlungsHöhe
                BigDecimal.valueOf(20000) // EndBetrag
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/rate/effZins")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("aktuellerZinssatz", notNullValue())
                .body("periodenZinssatz", notNullValue())
                .body("zinssatz", notNullValue());
    }

    @Test
    void testCalculateInterestBadRequest() {
        RateCalculatorResource.CalculationRequest invalidRequest = new RateCalculatorResource.CalculationRequest(
                10, // Laufzeit
                0,  // EinzahlungsDauer
                12, // Zahlungen pro Jahr
                BigDecimal.valueOf(0), // Ungültige EinzahlungsHöhe
                BigDecimal.valueOf(20000) // EndBetrag
        );

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/rate/effZins")
                .then()
                .statusCode(400);
    }
}