package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.dao.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.service.PlayoffService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test di INTEGRAZIONE di PlayoffService: generazione dei QUARTI di finale.
 *
 * ATTENZIONE: richiede il DATABASE DI PROVA attivo. Come gli altri Service,
 * PlayoffService istanzia da solo i propri DAO e scrive su DB, quindi lo
 * testiamo contro il database (fedeltà MVC: il Service non viene toccato).
 *
 * NOTA SULLA PULIZIA: generaQuarti() inserisce anche righe in tabellone_po che
 * referenziano le partite. Se il vincolo FK NON è "ON DELETE CASCADE", la
 * cancellazione delle partite di test può fallire: in quel caso il test lo
 * segnala e le poche righe temporanee vanno rimosse a mano (o si aggiunge la
 * cascade sullo schema di prova). Le partite di test usano un campionato
 * temporaneo dedicato, quindi sono facilmente individuabili.
 *
 * Invarianti verificate (giornataBase = 0 → gare 1..5):
 *   1. Esattamente 20 partite PO (4 serie × 5 gare).
 *   2. 5 giornate (1..5), ciascuna con 4 partite.
 *   3. Esattamente 4 serie (coppie), ciascuna ripetuta 5 volte.
 *   4. Accoppiamento per testa di serie: 1v8, 2v7, 3v6, 4v5.
 *   5. Alternanza casa/ospite: gare dispari in casa della testa di serie più alta.
 */
public class PlayoffServiceIT {

    private static int passati = 0;
    private static int falliti = 0;

    private static final SquadraDAO squadraDAO = new SquadraDAO();
    private static final CampionatoDAO campionatoDAO = new CampionatoDAO();
    private static final PartitaDAO partitaDAO = new PartitaDAO();
    private static final PlayoffService playoffService = new PlayoffService();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST (IT) PlayoffService - Quarti");
        System.out.println("========================================\n");

        List<Squadra> squadre = new ArrayList<>();
        Campionato campionato = null;

        try {
            // ── Setup: 8 squadre temporanee (ordine = teste di serie) ─
            for (int i = 1; i <= 8; i++) {
                String id = String.format("TPO%02d", i);
                Squadra s = new Squadra(id, "PoSquadra " + i, "Città " + i, "", "Coach " + i);
                squadraDAO.insert(s);
                squadre.add(s);
            }
            // seedIndex: posizione in classifica (0 = migliore)
            Map<String, Integer> seedIndex = new HashMap<>();
            for (int i = 0; i < squadre.size(); i++) {
                seedIndex.put(squadre.get(i).getId(), i);
            }

            // ── Setup: campionato temporaneo ─────────────────────────
            campionato = new Campionato("TEST_PO_" + System.currentTimeMillis(),
                    2099, LocalDate.of(2099, 1, 1), LocalDate.of(2099, 6, 1), "Attivo");
            int campId = campionatoDAO.insertAndGetId(campionato);
            campionato.setId(campId);

            // ── Azione: genera i quarti (giornataBase 0 → gare 1..5) ──
            playoffService.generaQuarti(campionato, squadre,
                    LocalDateTime.of(2099, 5, 1, 20, 30), 0);

            // ── Verifiche ────────────────────────────────────────────
            List<Partita> po = partitaDAO.findByCampionatoAndFase(campId, "PO");

            check("1) generate esattamente 20 partite PO", po.size() == 20);

            // 2) 5 giornate, 4 partite ciascuna
            Map<Integer, Integer> perGiornata = new HashMap<>();
            for (Partita p : po) perGiornata.merge(p.getGiornata(), 1, Integer::sum);
            check("2) esattamente 5 giornate", perGiornata.size() == 5);
            check("2) ogni giornata ha 4 partite",
                    perGiornata.values().stream().allMatch(n -> n == 4));

            // 3) 4 serie, ciascuna con 5 gare
            Map<String, Integer> serie = new HashMap<>();
            for (Partita p : po) {
                serie.merge(chiaveCoppia(p.getCasa().getId(), p.getOspite().getId()), 1, Integer::sum);
            }
            check("3) esattamente 4 serie", serie.size() == 4);
            check("3) ogni serie ha 5 gare", serie.values().stream().allMatch(n -> n == 5));

            // 4) accoppiamento per testa di serie: (0,7)(1,6)(2,5)(3,4)
            Set<String> attese = new HashSet<>();
            for (int i = 0; i < 4; i++) {
                attese.add(chiaveCoppia(squadre.get(i).getId(), squadre.get(7 - i).getId()));
            }
            check("4) accoppiamento 1v8, 2v7, 3v6, 4v5", serie.keySet().equals(attese));

            // 5) alternanza: gare dispari in casa della testa di serie più alta
            //    (seedIndex più basso = squadra più forte)
            boolean alternanzaOk = true;
            for (Partita p : po) {
                Integer seedCasa = seedIndex.get(p.getCasa().getId());
                Integer seedOsp = seedIndex.get(p.getOspite().getId());
                if (seedCasa == null || seedOsp == null) { alternanzaOk = false; break; }
                boolean garaDispari = (p.getGiornata() % 2 == 1);
                boolean casaEAlta = seedCasa < seedOsp; // seed più basso = più alta in classifica
                if (garaDispari != casaEAlta) { alternanzaOk = false; break; }
            }
            check("5) alternanza casa/ospite corretta nelle serie", alternanzaOk);

        } catch (Exception e) {
            System.out.println("  ✘ Eccezione durante il test: " + e.getMessage());
            falliti++;
        } finally {
            pulisci(campionato, squadre);
        }

        stampaRiepilogo();
    }

    // ── SUPPORTO ─────────────────────────────────────────────────────

    private static String chiaveCoppia(String a, String b) {
        return (a.compareTo(b) <= 0) ? a + "|" + b : b + "|" + a;
    }

    private static void pulisci(Campionato campionato, List<Squadra> squadre) {
        // 1. partite PO (attenzione: possibile FK da tabellone_po → vedi nota in testa)
        if (campionato != null && campionato.getId() != 0) {
            try {
                for (Partita p : partitaDAO.findByCampionatoAndFase(campionato.getId(), "PO")) {
                    partitaDAO.delete(p.getId());
                }
            } catch (Exception e) {
                System.out.println("  (pulizia) partite PO non eliminate (probabile FK tabellone_po senza CASCADE): "
                        + e.getMessage());
            }
        }
        // 2. squadre
        for (Squadra s : squadre) {
            try { squadraDAO.delete(s.getId()); }
            catch (Exception e) { System.out.println("  (pulizia) squadra " + s.getId() + ": " + e.getMessage()); }
        }
        // 3. campionato
        if (campionato != null && campionato.getId() != 0) {
            try { campionatoDAO.delete(campionato.getId()); }
            catch (Exception e) { System.out.println("  (pulizia) campionato: " + e.getMessage()); }
        }
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