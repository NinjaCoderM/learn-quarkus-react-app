package at.codecrafters.rate.controller;

import at.codecrafters.rate.service.RateCalculatorService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

/**
 * REST-Resource zur Berechnung von Zinsen.
 * Diese Klasse stellt einen Endpunkt bereit, um Zinsberechnungen basierend auf Benutzereingaben durchzuführen.
 */
@Path("/rate")
public class RateCalculatorResource {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RateCalculatorService rateCalculatorService;

    /**
     * Konstruktor für die Ressource.
     * @param rateCalculatorService Service für Zinsberechnungen.
     */
    @SuppressWarnings("unused")
    public RateCalculatorResource(RateCalculatorService rateCalculatorService) {
        this.rateCalculatorService = rateCalculatorService;
    }

    /**
     * POST-Endpunkt zur Berechnung des effektiven Zinssatzes.
     * Nimmt eine JSON-Anfrage entgegen, verarbeitet die Eingaben und gibt die Ergebnisse in einer JSON-Antwort zurück.
     *
     * @param request Die Eingabeparameter für die Zinsberechnung.
     * @return Antwort mit den berechneten Zinsen.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path ("/effZins")
    public Response calculateInterest(CalculationRequest request) {
        logger.info("Berechnung Zinsen für {}", request.toString());

        try {
            if (request.einzahlungsHoehe().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Einzahlungshöhe muss größer als null sein");
            }
            if (request.endBetrag().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Endbetrag muss größer als null sein");
            }
            if (request.laufzeit() < 1) {
                throw new IllegalArgumentException("Laufzeit muss größer als 0 sein");
            }
            BigDecimal zinssatz = rateCalculatorService.berechneZins(request.laufzeit(), request.zahlungenProJahr(), request.einzahlungsHoehe(), request.endBetrag());
            BigDecimal periodenZinssatz = request.zahlungenProJahr()==0?zinssatz:rateCalculatorService.berechnePeriodenZins(request.laufzeit(), request.zahlungenProJahr(), request.einzahlungsHoehe(), request.endBetrag());
            BigDecimal aktuellerZinssatz = request.zahlungenProJahr()==0?zinssatz:rateCalculatorService.berechneAktZins(request.laufzeit(), request.einzahlungsDauer(), request.zahlungenProJahr(), request.einzahlungsHoehe(), request.endBetrag());

            CalculationResponse calculationResponse = new CalculationResponse(aktuellerZinssatz, periodenZinssatz, zinssatz);

            logger.info("Berechnung Ergebnis: {}", calculationResponse);
            return Response.ok(calculationResponse).build();
        } catch (IllegalArgumentException e) {
            // Rückgabe einer BadRequest-Fehlermeldung
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            // Rückgabe einer generischen Fehlermeldung
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ein unerwarteter Fehler ist aufgetreten").build();
        }
    }

    /**
     * Datensatz zur Darstellung der Eingabeparameter für die Zinsberechnung.
     *
     * @param laufzeit Gesamtlaufzeit der Einzahlung in Jahren.
     * @param einzahlungsDauer Dauer der regelmäßigen Einzahlungen in Jahren.
     * @param zahlungenProJahr Anzahl der Zahlungen pro Jahr.
     * @param einzahlungsHoehe Höhe der einzelnen Einzahlung.
     * @param endBetrag Zielbetrag am Ende der Laufzeit.
     */
    public record CalculationRequest(
            int laufzeit,
            int einzahlungsDauer,
            int zahlungenProJahr,
            BigDecimal einzahlungsHoehe,
            BigDecimal  endBetrag
    ) {}

    /**
     * Datensatz zur Darstellung der Antwort der Zinsberechnung.
     *
     * @param aktuellerZinssatz Der aktuelle Zinssatz basierend auf der verbleibenden Laufzeit.
     * @param periodenZinssatz Der periodische Zinssatz.
     * @param zinssatz Der Gesamtzinssatz für die Laufzeit.
     */
    public record CalculationResponse(BigDecimal  aktuellerZinssatz, BigDecimal periodenZinssatz, BigDecimal zinssatz) {}
}