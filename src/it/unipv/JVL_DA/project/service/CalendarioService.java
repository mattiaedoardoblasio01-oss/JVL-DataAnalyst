package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.Squadra;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CalendarioService {
    private final PartitaDAO partitaDAO;

    // Range realistico di punti per una partita di basket simulata
    private static final int PUNTI_MIN = 65;
    private static final int PUNTI_MAX = 105;

    public CalendarioService() {
        this.partitaDAO = new PartitaDAO();
    }

    /*Algoritmo di tipo Round Robin*/
    public void generaCalendario(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio) throws SQLException {

        if (squadre.size() != 16) {
            throw new IllegalArgumentException("Servono esattamente 16 squadre per generare il calendario");
        }

        int nSquadre = squadre.size();
        int nGiornate = nSquadre - 1;          // 15 giornate di andata
        int nPartitePerGiornata = nSquadre / 2; // 8 partite per giornata

        List<Squadra> lista = new ArrayList<>(squadre);
        List<Partita> andata = new ArrayList<>();

        // Genera andata (15 giornate) — risultati casuali, partite gia' "Conclusa"
        for (int turno = 0; turno < nGiornate; turno++) {
            int giornata = turno + 1;
            LocalDateTime dataGiornata = dataInizio.plusWeeks(turno);

            for (int i = 0; i < nPartitePerGiornata; i++) {
                Squadra casa = lista.get(i);
                Squadra ospite = lista.get(nSquadre - 1 - i);

                Partita partita = creaPartitaConRisultatoCasuale(
                        campionato, giornata, casa, ospite, dataGiornata, casa.getSede());

                andata.add(partita);
                partitaDAO.insert(partita);
            }

            // Ruota le squadre mantenendo la prima fissa
            lista.add(1, lista.remove(nSquadre - 1));
        }

        // Genera ritorno (15 giornate) invertendo casa e ospite dell'andata
        for (int turno = 0; turno < nGiornate; turno++) {
            int giornata = nGiornate + turno + 1; // giornate 16-30
            LocalDateTime dataGiornata = dataInizio.plusWeeks(nGiornate + turno);

            // Prende le 8 partite della giornata corrispondente dell'andata
            for (int i = turno * nPartitePerGiornata; i < (turno + 1) * nPartitePerGiornata; i++) {
                Partita partitaAndata = andata.get(i);

                // Inverti casa e ospite, nuovo risultato casuale
                Partita ritorno = creaPartitaConRisultatoCasuale(
                        campionato, giornata,
                        partitaAndata.getOspite(), // casa diventa ospite
                        partitaAndata.getCasa(),   // ospite diventa casa
                        dataGiornata,
                        partitaAndata.getOspite().getSede());

                partitaDAO.insert(ritorno);
            }
        }
    }

    /**
     * Crea una Partita di Regular Season con punteggio casuale e stato "Conclusa".
     * Nel basket non esistono pareggi: si garantisce scoreCasa != scoreOsp.
     * "Conclusa" e' l'unico valore di partita conclusa ammesso dall'enum del DB
     * (partite.stato = enum('Programmata','Conclusa')), ed e' quello che la
     * classifica conta come partita giocata.
     */
    private Partita creaPartitaConRisultatoCasuale(Campionato campionato, int giornata,
                                                   Squadra casa, Squadra ospite,
                                                   LocalDateTime dataOra, String luogo) {
        int scoreCasa = punteggioCasuale();
        int scoreOsp = punteggioCasuale();
        while (scoreOsp == scoreCasa) {
            scoreOsp = punteggioCasuale(); // niente pareggi
        }
        return new Partita(campionato, "RS", giornata, casa, ospite,
                dataOra, luogo, scoreCasa, scoreOsp, "Conclusa");
    }

    private int punteggioCasuale() {
        return ThreadLocalRandom.current().nextInt(PUNTI_MIN, PUNTI_MAX + 1);
    }

    public boolean esisteCalendario(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "RS");
        return !partite.isEmpty();
    }

    public void eliminaCalendario(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "RS");
        for (Partita partita : partite) {
            partitaDAO.delete(partita.getId());
        }
    }

    public void generaCalendarioSafe(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio) throws SQLException {
        if (esisteCalendario(campionato.getId())) {
            throw new IllegalStateException("Il calendario per questo campionato esiste gia'!");
        }
        generaCalendario(campionato, squadre, dataInizio);
    }
}