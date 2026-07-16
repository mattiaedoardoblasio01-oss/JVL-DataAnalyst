package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.dao.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.service.CampionatoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Test di INTEGRAZIONE di CampionatoService: le GUARDIE della macchina a stati
 * (Config → Attivo → Chiuso).
 *
 * ATTENZIONE: richiede il DATABASE DI PROVA attivo. Il Service istanzia da solo
 * i propri DAO, quindi (per fedeltà MVC, senza modificarlo) lo testiamo contro
 * il DB. La mutazione è minima: un solo campionato temporaneo "Config", rimosso
 * nel finally.
 *
 * Controlli:
 *   1. creaCampionato() rifiuta la creazione se esiste già un campionato
 *      "Config" (o "Attivo") → IllegalStateException.
 *   2. avviaRegularSeason() con un numero di squadre diverso da 16
 *      → IllegalArgumentException (verificata PRIMA di scrivere sul DB).
 *   3. chiudiCampionato() su un campionato non "Attivo" → IllegalStateException.
 */
public class CampionatoServiceIT {

    private static int passati = 0;
    private static int falliti = 0;

    private static final CampionatoDAO campionatoDAO = new CampionatoDAO();
    private static final CampionatoService campionatoService = new CampionatoService();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST (IT) CampionatoService - Stati");
        System.out.println("========================================\n");

        Campionato tempConfig = null;

        try {
            // ── Setup: un campionato temporaneo in stato "Config" ────
            tempConfig = new Campionato("TEST_STATO_" + System.currentTimeMillis(),
                    2099, LocalDate.of(2099, 1, 1), LocalDate.of(2099, 6, 1), "Config");
            int id = campionatoDAO.insertAndGetId(tempConfig);
            tempConfig.setId(id);

            // 1) creaCampionato deve fallire: esiste già un "Config" (o un "Attivo")
            checkThrows("1) creaCampionato con Config/Attivo già presente → IllegalStateException",
                    IllegalStateException.class,
                    () -> campionatoService.creaCampionato(
                            "NuovoDoppione", 2099,
                            LocalDate.of(2099, 1, 1), LocalDate.of(2099, 6, 1)));

            // 2) avviaRegularSeason con ≠ 16 squadre → IllegalArgumentException
            //    (il controllo sulla dimensione avviene prima di scrivere sul DB,
            //     quindi bastano squadre non persistite)
            List<Squadra> quindici = new ArrayList<>();
            for (int i = 1; i <= 15; i++) {
                quindici.add(new Squadra("X" + i, "S" + i, "C" + i, "", "A" + i));
            }
            final Campionato campConfig = tempConfig;
            checkThrows("2) avviaRegularSeason con 15 squadre → IllegalArgumentException",
                    IllegalArgumentException.class,
                    () -> campionatoService.avviaRegularSeason(
                            campConfig, quindici, LocalDateTime.of(2099, 1, 1, 20, 30)));

            // 3) chiudiCampionato su stato "Config" → IllegalStateException
            checkThrows("3) chiudiCampionato su campionato non 'Attivo' → IllegalStateException",
                    IllegalStateException.class,
                    () -> campionatoService.chiudiCampionato(campConfig));

        } catch (Exception e) {
            System.out.println("  ✘ Eccezione inattesa nel setup: " + e.getMessage());
            falliti++;
        } finally {
            if (tempConfig != null && tempConfig.getId() != 0) {
                try {
                    campionatoDAO.delete(tempConfig.getId());
                } catch (Exception e) {
                    System.out.println("  (pulizia) impossibile eliminare il campionato temporaneo: " + e.getMessage());
                }
            }
        }

        stampaRiepilogo();
    }

    // ── SUPPORTO ─────────────────────────────────────────────────────

    @FunctionalInterface
    private interface Azione { void esegui() throws Exception; }

    /** Verifica che l'azione lanci un'eccezione del tipo atteso (o sottotipo). */
    private static void checkThrows(String descrizione, Class<? extends Exception> atteso, Azione azione) {
        try {
            azione.esegui();
            System.out.println("  ✘ " + descrizione + "  (nessuna eccezione lanciata)");
            falliti++;
        } catch (Exception e) {
            if (atteso.isInstance(e)) {
                System.out.println("  ✔ " + descrizione);
                passati++;
            } else {
                System.out.println("  ✘ " + descrizione + "  (atteso " + atteso.getSimpleName()
                        + ", ottenuto " + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
                falliti++;
            }
        }
    }

    private static void stampaRiepilogo() {
        System.out.println("\n----------------------------------------");
        System.out.println("  Passati: " + passati + " | Falliti: " + falliti);
        System.out.println(falliti == 0 ? "  ESITO: TUTTI I TEST OK" : "  ESITO: CI SONO FALLIMENTI");
        System.out.println("----------------------------------------");
    }
}