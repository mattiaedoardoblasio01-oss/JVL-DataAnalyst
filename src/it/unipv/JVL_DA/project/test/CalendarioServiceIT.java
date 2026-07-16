package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.dao.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.service.CalendarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test di INTEGRAZIONE di CalendarioService (algoritmo Round Robin).
 *
 * ATTENZIONE: richiede il DATABASE DI PROVA attivo. CalendarioService istanzia
 * al suo interno PartitaDAO e scrive davvero le partite: per fedeltà al pattern
 * MVC NON modifichiamo il Service, quindi il modo corretto di testarlo è contro
 * il DB (come già fa TestDBConnector).
 *
 * Il test crea dati TEMPORANEI (1 campionato "Config" + 16 squadre), genera il
 * calendario, verifica le invarianti del round robin e infine ripulisce tutto.
 * La pulizia è nel blocco finally: se il test fallisce a metà, i dati temporanei
 * vengono comunque rimossi.
 *
 * Invarianti verificate:
 *   1. Esattamente 240 partite RS (16 squadre → 30 giornate × 8 partite).
 *   2. 30 giornate, ciascuna con esattamente 8 partite.
 *   3. Nessuna squadra gioca contro sé stessa.
 *   4. Ogni coppia di squadre si affronta esattamente 2 volte (andata + ritorno).
 *   5. Le 2 sfide di ogni coppia hanno casa e ospite invertiti.
 *   6. Ogni squadra gioca esattamente 30 partite (2 × 15 avversari).
 */
public class CalendarioServiceIT {

    private static int passati = 0;
    private static int falliti = 0;

    private static final SquadraDAO squadraDAO = new SquadraDAO();
    private static final CampionatoDAO campionatoDAO = new CampionatoDAO();
    private static final PartitaDAO partitaDAO = new PartitaDAO();
    private static final CalendarioService calendarioService = new CalendarioService();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST (IT) CalendarioService - Round Robin");
        System.out.println("========================================\n");

        List<Squadra> squadre = new ArrayList<>();
        Campionato campionato = null;

        try {
            // ── Setup: 16 squadre temporanee ─────────────────────────
            for (int i = 1; i <= 16; i++) {
                String id = String.format("TSQ%02d", i);
                Squadra s = new Squadra(id, "TestSquadra " + i, "Città " + i, "", "Coach " + i);
                squadraDAO.insert(s);
                squadre.add(s);
            }

            // ── Setup: campionato temporaneo in stato "Config" ───────
            campionato = new Campionato("TEST_RR_" + System.currentTimeMillis(),
                    2099, LocalDate.of(2099, 1, 1), LocalDate.of(2099, 6, 1), "Config");
            int campId = campionatoDAO.insertAndGetId(campionato);
            campionato.setId(campId);

            // ── Azione: genera il calendario (scrive le partite sul DB) ─
            LocalDateTime dataInizio = LocalDateTime.of(2099, 1, 1, 20, 30);
            calendarioService.generaCalendario(campionato, squadre, dataInizio);

            // ── Verifiche sulle partite generate ─────────────────────
            List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "RS");

            check("1) generate esattamente 240 partite RS", partite.size() == 240);

            // 2) 30 giornate, 8 partite ciascuna
            Map<Integer, Integer> perGiornata = new HashMap<>();
            for (Partita p : partite) {
                perGiornata.merge(p.getGiornata(), 1, Integer::sum);
            }
            boolean trenta = perGiornata.size() == 30;
            boolean ottoCiascuna = perGiornata.values().stream().allMatch(n -> n == 8);
            check("2) esattamente 30 giornate", trenta);
            check("2) ogni giornata ha 8 partite", ottoCiascuna);

            // 3) nessuna auto-sfida
            boolean autoSfida = partite.stream()
                    .anyMatch(p -> p.getCasa().getId().equals(p.getOspite().getId()));
            check("3) nessuna squadra gioca contro sé stessa", !autoSfida);

            // 4) ogni coppia si affronta 2 volte
            Map<String, Integer> coppie = new HashMap<>();
            for (Partita p : partite) {
                coppie.merge(chiaveCoppia(p.getCasa().getId(), p.getOspite().getId()), 1, Integer::sum);
            }
            int coppieAttese = 16 * 15 / 2; // 120 coppie
            boolean tutteDue = coppie.size() == coppieAttese
                    && coppie.values().stream().allMatch(n -> n == 2);
            check("4) ogni coppia si affronta esattamente 2 volte", tutteDue);

            // 5) casa/ospite invertiti nelle 2 sfide della stessa coppia
            Map<String, Set<String>> caseDellaCoppia = new HashMap<>();
            for (Partita p : partite) {
                String k = chiaveCoppia(p.getCasa().getId(), p.getOspite().getId());
                caseDellaCoppia.computeIfAbsent(k, x -> new HashSet<>()).add(p.getCasa().getId());
            }
            boolean inversione = caseDellaCoppia.values().stream().allMatch(set -> set.size() == 2);
            check("5) le 2 sfide di ogni coppia hanno casa/ospite invertiti", inversione);

            // 6) ogni squadra gioca 30 partite
            Map<String, Integer> perSquadra = new HashMap<>();
            for (Partita p : partite) {
                perSquadra.merge(p.getCasa().getId(), 1, Integer::sum);
                perSquadra.merge(p.getOspite().getId(), 1, Integer::sum);
            }
            boolean trentaCiascuna = perSquadra.size() == 16
                    && perSquadra.values().stream().allMatch(n -> n == 30);
            check("6) ogni squadra gioca esattamente 30 partite", trentaCiascuna);

        } catch (Exception e) {
            System.out.println("  ✘ Eccezione durante il test: " + e.getMessage());
            falliti++;
        } finally {
            // ── Pulizia (best-effort) ────────────────────────────────
            pulisci(campionato, squadre);
        }

        stampaRiepilogo();
    }

    // ── SUPPORTO ─────────────────────────────────────────────────────

    /** Chiave simmetrica per una coppia di squadre, indipendente dall'ordine. */
    private static String chiaveCoppia(String a, String b) {
        return (a.compareTo(b) <= 0) ? a + "|" + b : b + "|" + a;
    }

    private static void pulisci(Campionato campionato, List<Squadra> squadre) {
        // 1. partite (via il metodo del Service, che elimina le RS del campionato)
        if (campionato != null && campionato.getId() != 0) {
            try {
                calendarioService.eliminaCalendario(campionato.getId());
            } catch (Exception e) {
                System.out.println("  (pulizia) impossibile eliminare le partite: " + e.getMessage());
            }
        }
        // 2. squadre
        for (Squadra s : squadre) {
            try {
                squadraDAO.delete(s.getId());
            } catch (Exception e) {
                System.out.println("  (pulizia) impossibile eliminare la squadra " + s.getId() + ": " + e.getMessage());
            }
        }
        // 3. campionato
        if (campionato != null && campionato.getId() != 0) {
            try {
                campionatoDAO.delete(campionato.getId());
            } catch (Exception e) {
                System.out.println("  (pulizia) impossibile eliminare il campionato: " + e.getMessage());
            }
        }
    }

    private static void check(String descrizione, boolean condizione) {
        if (condizione) {
            System.out.println("  ✔ " + descrizione);
            passati++;
        } else {
            System.out.println("  ✘ " + descrizione);
            falliti++;
        }
    }

    private static void stampaRiepilogo() {
        System.out.println("\n----------------------------------------");
        System.out.println("  Passati: " + passati + " | Falliti: " + falliti);
        System.out.println(falliti == 0 ? "  ESITO: TUTTI I TEST OK" : "  ESITO: CI SONO FALLIMENTI");
        System.out.println("----------------------------------------");
    }
}