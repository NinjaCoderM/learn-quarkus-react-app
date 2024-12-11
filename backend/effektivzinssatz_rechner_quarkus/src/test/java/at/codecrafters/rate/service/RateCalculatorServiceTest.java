package at.codecrafters.rate.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class RateCalculatorServiceTest {

    private final RateCalculatorService rateCalculatorService = new RateCalculatorService();

    @Test
    void testBerechneZins() {
        int laufzeit = 10;
        int zahlungenProJahr = 12;
        BigDecimal einzahlungsHoehe = BigDecimal.valueOf(150);
        BigDecimal endBetrag = BigDecimal.valueOf(20000);

        BigDecimal result = rateCalculatorService.berechneZins(laufzeit, zahlungenProJahr, einzahlungsHoehe, endBetrag);

        assertNotNull(result, "Das Ergebnis sollte nicht null sein.");
        assertTrue(result.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0, "Der Zins sollte positiv sein.");
    }

    @Test
    void testBerechnePeriodenZins() {
        int laufzeit = 10;
        int zahlungenProJahr = 12;
        BigDecimal einzahlungsHoehe = BigDecimal.valueOf(150);
        BigDecimal endBetrag = BigDecimal.valueOf(20000);

        BigDecimal result = rateCalculatorService.berechnePeriodenZins(laufzeit, zahlungenProJahr, einzahlungsHoehe, endBetrag);

        assertNotNull(result, "Das Ergebnis sollte nicht null sein.");
        assertTrue(result.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0, "Der Periodenzins sollte positiv sein.");
    }

    @Test
    void testBerechneAktZins() {
        int laufzeit = 10;
        int einzahlungsDauer = 5;
        int zahlungenProJahr = 12;
        BigDecimal einzahlungsHoehe = BigDecimal.valueOf(150);
        BigDecimal endBetrag = BigDecimal.valueOf(20000);

        BigDecimal result = rateCalculatorService.berechneAktZins(laufzeit, einzahlungsDauer, zahlungenProJahr, einzahlungsHoehe, endBetrag);

        assertNotNull(result, "Das Ergebnis sollte nicht null sein.");
        assertTrue(result.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0, "Der aktuelle Zins sollte positiv sein.");
    }
}