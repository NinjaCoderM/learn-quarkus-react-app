package at.codecrafters.rate.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@ApplicationScoped
public class RateCalculatorService {

    /**
     * Berechnet die n-te Wurzel eines Betrags.
     *
     * @param betrag Der Betrag, dessen Wurzel berechnet werden soll
     * @param root Die Wurzel, die berechnet werden soll
     * @return Der Wert der n-ten Wurzel des Betrags als BigDecimal
     * @throws ArithmeticException Wenn der Betrag negativ ist, wird eine Ausnahme ausgelöst, da keine reelle Wurzel existiert
     */
    private static BigDecimal calculateRoot(BigDecimal betrag, int root) {
        double number = betrag.doubleValue();
        if (number < 0) {
            throw new ArithmeticException("Negative values cannot have real roots");
        }
        return BigDecimal.valueOf(Math.pow(number, 1.0 / root));
    }
    /**
     * Berechnet den Zinssatz basierend auf der Laufzeit, der Anzahl der Zahlungen pro Jahr, der Höhe der Einzahlung und dem Endbetrag.
     *
     * @param laufzeit Die Laufzeit des Investments in Jahren
     * @param zahlungenProJahr Die Anzahl der Zahlungen pro Jahr
     * @param einzahlungsHoehe Die Höhe der Einzahlung pro Periode
     * @param endBetrag Der Endbetrag des Investments
     * @return Der berechnete Zinssatz als BigDecimal
     */
    public BigDecimal berechneZins(int laufzeit, int zahlungenProJahr, BigDecimal einzahlungsHoehe, BigDecimal  endBetrag){
        BigDecimal gesamtEinzahlung = zahlungenProJahr==0?einzahlungsHoehe:BigDecimal.valueOf((long) laufzeit *zahlungenProJahr).multiply(einzahlungsHoehe);
        return calculateRoot(endBetrag.divide(gesamtEinzahlung, MathContext.DECIMAL128), laufzeit).setScale(5, RoundingMode.HALF_UP);
    }
    /**
     * Berechnet den Periodenzinssatz basierend auf der Laufzeit, der Anzahl der Zahlungen pro Jahr und dem Endbetrag.
     *
     * @param laufzeit Die Laufzeit des Investments in Jahren
     * @param zahlungenProJahr Die Anzahl der Zahlungen pro Jahr
     * @param einzahlungsHoehe Die Höhe der Einzahlung pro Periode
     * @param endBetrag Der Endbetrag des Investments
     * @return Der berechnete Periodenzinssatz als BigDecimal
     */
    public BigDecimal berechnePeriodenZins(int laufzeit, int zahlungenProJahr, BigDecimal einzahlungsHoehe, BigDecimal  endBetrag){
        BigDecimal gesamtEinzahlung = BigDecimal.valueOf((long) laufzeit *zahlungenProJahr).multiply(einzahlungsHoehe);
        return calculateRoot(endBetrag.divide(gesamtEinzahlung, MathContext.DECIMAL128), laufzeit*zahlungenProJahr).setScale(5, RoundingMode.HALF_UP);
    }
    /**
     * Berechnet den aktuellen Zinssatz unter Berücksichtigung der offenen Laufzeit.
     *
     * @param laufzeit Die gesamte Laufzeit des Investments in Jahren
     * @param einzahlungsDauer Die Anzahl der Jahre, in denen Einzahlungen getätigt wurden
     * @param zahlungenProJahr Die Anzahl der Zahlungen pro Jahr
     * @param einzahlungsHoehe Die Höhe der Einzahlung pro Periode
     * @param endBetrag Der Endbetrag des Investments
     * @return Der berechnete aktuelle Zinssatz als BigDecimal
     */
    public BigDecimal berechneAktZins(int laufzeit, int einzahlungsDauer, int zahlungenProJahr, BigDecimal einzahlungsHoehe, BigDecimal  endBetrag){
        BigDecimal gesamtEinzahlung = BigDecimal.valueOf((long) laufzeit *zahlungenProJahr).multiply(einzahlungsHoehe);
        int offeneLaufzeit = laufzeit*zahlungenProJahr-einzahlungsDauer*zahlungenProJahr;
        BigDecimal periodenZinssatz =  calculateRoot(endBetrag.divide(gesamtEinzahlung, MathContext.DECIMAL128), laufzeit*zahlungenProJahr);
        return BigDecimal.valueOf(Math.pow(periodenZinssatz.doubleValue(), offeneLaufzeit+1)).setScale(5, RoundingMode.HALF_UP);
    }
}
