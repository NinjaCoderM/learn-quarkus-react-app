package at.codecrafters.rate.controller;

import at.codecrafters.rate.service.RateCalculatorService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RateCalculatorResourceTest {

    @SuppressWarnings("unused")
    @InjectMock
    RateCalculatorService rateCalculatorService;

    @Test
    void testCalculateInterest() {
        RateCalculatorResource.CalculationRequest request = new RateCalculatorResource.CalculationRequest(
                10, // Laufzeit
                5,  // EinzahlungsDauer
                12, // Zahlungen pro Jahr
                BigDecimal.valueOf(100), // EinzahlungsHöhe
                BigDecimal.valueOf(20000) // EndBetrag
        );

        Mockito.when(rateCalculatorService.berechneZins(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class))).thenReturn(new BigDecimal("1"));
        Mockito.when(rateCalculatorService.berechnePeriodenZins(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class))).thenReturn(new BigDecimal("1"));
        Mockito.when(rateCalculatorService.berechneAktZins(Mockito.any(Integer.class),Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class))).thenReturn(new BigDecimal("1"));
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
    void testCalculateInternalServiceError() {
        RateCalculatorResource.CalculationRequest invalidRequest = new RateCalculatorResource.CalculationRequest(
                10, // Laufzeit
                3,  // EinzahlungsDauer
                12, // Zahlungen pro Jahr
                BigDecimal.valueOf(150), // Ungültige EinzahlungsHöhe
                BigDecimal.valueOf(20000) // EndBetrag
        );

        Mockito.when(rateCalculatorService.berechneZins(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class))).thenThrow(new NullPointerException("Test Error"));

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/rate/effZins")
                .then()
                .statusCode(500);
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