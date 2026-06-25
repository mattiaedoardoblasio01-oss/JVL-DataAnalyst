package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.Squadra;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarioService {
    private final PartitaDAO partitaDAO;

    public CalendarioService() {
        this.partitaDAO = new PartitaDAO();
    }

    /*Algoritmo di tipo Round Robin*/
    public void generaCalendario(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio) throws SQLException {

        if (squadre.size() != 16) {
            throw new IllegalArgumentException("Servono esattamente 16 squadre per generare il calendario");
        }

        int nSquadre = squadre.size();
        int nGiornate = nSquadre - 1; // 15 giornate di andata
        int nPartitePerGiornata = nSquadre / 2; // 8 partite per giornata

        List<Squadra> lista = new ArrayList<>(squadre);
        List<Partita> andata = new ArrayList<>();

        // Genera andata (15 giornate)
        for (int turno = 0; turno < nGiornate; turno++) {
            int giornata = turno + 1;
            LocalDateTime dataGiornata = dataInizio.plusWeeks(turno);

            for (int i = 0; i < nPartitePerGiornata; i++) {
                Squadra casa = lista.get(i);
                Squadra ospite = lista.get(nSquadre - 1 - i);

                Partita partita = new Partita(
                        campionato, "RS", giornata, casa, ospite,
                        dataGiornata, casa.getSede(), 0, 0, "Programmata"
                );

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

                // Inverti casa e ospite
                Partita ritorno = new Partita(
                        campionato, "RS", giornata,
                        partitaAndata.getOspite(), // casa diventa ospite
                        partitaAndata.getCasa(),   // ospite diventa casa
                        dataGiornata,
                        partitaAndata.getOspite().getSede(),
                        0, 0, "Programmata"
                );

                partitaDAO.insert(ritorno);
            }
        }
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
            throw new IllegalStateException("Il calendario per questo campionato esiste già!");
        }
        generaCalendario(campionato, squadre, dataInizio);
    }
}