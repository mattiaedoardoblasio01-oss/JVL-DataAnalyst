package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.dao.implementazioni.AmministratoreDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.GiocatoreDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.StatisticheDAO;
import it.unipv.JVL_DA.project.model.Amministratore;
import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Statistiche;
import it.unipv.JVL_DA.project.service.StatisticheService;

import java.util.List;

/**
 * Test di INTEGRAZIONE di StatisticheService: la logica di ACCUMULO dei tabellini.
 *
 * ATTENZIONE: richiede il DATABASE DI PROVA attivo. Il Service istanzia da solo
 * i propri DAO e scrive su DB, quindi lo testiamo contro il database (fedeltà
 * MVC: il Service non viene modificato).
 *
 * Serve almeno UN amministratore già presente sul DB: inserisciStatistiche()
 * scrive una riga di log (FK verso amministratori). Se non ce n'è nessuno, il
 * test viene SALTATO con un avviso.
 *
 * NOTA: le poche righe di log (UPDATE_STATISTICHE) prodotte restano sul DB di
 * prova; non incidono sugli altri test. Il resto (squadra, giocatore, statistiche
 * temporanei) viene ripulito nel finally.
 *
 * Controlli:
 *   1. Primo tabellino {10,5,3} → il record viene creato con quei valori.
 *   2. Secondo tabellino {7,2,4} → i valori si SOMMANO: {17,7,7}.
 *   3. aggiornaStatistiche() su un id inesistente → IllegalStateException.
 */
public class StatisticheServiceIT {

    private static int passati = 0;
    private static int falliti = 0;

    private static final SquadraDAO squadraDAO = new SquadraDAO();
    private static final GiocatoreDAO giocatoreDAO = new GiocatoreDAO();
    private static final StatisticheDAO statisticheDAO = new StatisticheDAO();
    private static final AmministratoreDAO amministratoreDAO = new AmministratoreDAO();
    private static final StatisticheService statisticheService = new StatisticheService();

    private static final String SQUADRA_ID = "TSG1";
    private static final String GIOCATORE_ID = "TGST1";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST (IT) StatisticheService - Accumulo");
        System.out.println("========================================\n");

        Squadra squadra = null;
        Giocatore giocatore = null;

        try {
            // ── Serve un admin per il log (FK) ───────────────────────
            Amministratore admin = primoAdmin();
            if (admin == null) {
                System.out.println("  ⚠ SALTATO: nessun amministratore sul DB di prova "
                        + "(inserisciStatistiche scrive un log con FK admin).");
                stampaRiepilogo();
                return;
            }

            // ── Setup: squadra + giocatore temporanei ────────────────
            squadra = new Squadra(SQUADRA_ID, "TestStatSquadra", "Città", "", "Coach");
            squadraDAO.insert(squadra);

            giocatore = new Giocatore(GIOCATORE_ID, "Test", "Player", "Ala", 99, squadra);
            giocatoreDAO.insert(giocatore);

            // ── 1) Primo tabellino: crea il record ───────────────────
            statisticheService.inserisciStatistiche(
                    new Statistiche(giocatore, 10, 5, 3), admin);

            Statistiche dopoPrimo = statisticheDAO.findByGiocatore(GIOCATORE_ID);
            check("1) primo tabellino crea il record {10,5,3}",
                    dopoPrimo != null
                            && dopoPrimo.getPunti() == 10
                            && dopoPrimo.getRimbalzi() == 5
                            && dopoPrimo.getAssist() == 3);

            // ── 2) Secondo tabellino: i valori si sommano ────────────
            statisticheService.inserisciStatistiche(
                    new Statistiche(giocatore, 7, 2, 4), admin);

            Statistiche dopoSecondo = statisticheDAO.findByGiocatore(GIOCATORE_ID);
            check("2) secondo tabellino somma i valori → {17,7,7}",
                    dopoSecondo != null
                            && dopoSecondo.getPunti() == 17
                            && dopoSecondo.getRimbalzi() == 7
                            && dopoSecondo.getAssist() == 7);

            // ── 3) aggiornaStatistiche su id inesistente → eccezione ─
            Statistiche fantasma = new Statistiche(999_999_999, giocatore, 1, 1, 1);
            boolean lanciata;
            try {
                statisticheService.aggiornaStatistiche(fantasma, admin);
                lanciata = false;
            } catch (IllegalStateException e) {
                lanciata = true;
            }
            check("3) aggiornaStatistiche su record inesistente → IllegalStateException", lanciata);

        } catch (Exception e) {
            System.out.println("  ✘ Eccezione durante il test: " + e.getMessage());
            falliti++;
        } finally {
            pulisci();
        }

        stampaRiepilogo();
    }

    // ── SUPPORTO ─────────────────────────────────────────────────────

    private static Amministratore primoAdmin() {
        try {
            List<Amministratore> admin = amministratoreDAO.findAll();
            return (admin == null || admin.isEmpty()) ? null : admin.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private static void pulisci() {
        // 1. statistiche del giocatore di test
        try {
            Statistiche s = statisticheDAO.findByGiocatore(GIOCATORE_ID);
            if (s != null) statisticheDAO.delete(s.getId());
        } catch (Exception e) {
            System.out.println("  (pulizia) statistiche: " + e.getMessage());
        }
        // 2. giocatore
        try { giocatoreDAO.delete(GIOCATORE_ID); }
        catch (Exception e) { System.out.println("  (pulizia) giocatore: " + e.getMessage()); }
        // 3. squadra
        try { squadraDAO.delete(SQUADRA_ID); }
        catch (Exception e) { System.out.println("  (pulizia) squadra: " + e.getMessage()); }
    }

    private static void check(String descrizione, boolean condizione) {
        if (condizione) { System.out.println("  ✔ " + descrizione); passati++; }
        else { System.out.println("  ✘ " + descrizione); falliti++; }
    }

    private static void stampaRiepilogo() {
        System.out.println("\n----------------------------------------");
        System.out.println("  Passati: " + passati + " | Falliti: " + falliti);
        System.out.println(falliti == 0 ? "  ESITO: TUTTI I TEST OK" : "  ESITO: CI SONO FALLIMENTI");
        System.out.println("----------------------------------------");
    }
}