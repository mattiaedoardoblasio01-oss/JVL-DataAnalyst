package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.TabellonePODAO;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.POJO.TabellonePO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PlayoffService {

    private final PartitaDAO partitaDAO;
    private final TabellonePODAO tabellonePODAO;

    public PlayoffService() {
        this.partitaDAO = new PartitaDAO();
        this.tabellonePODAO = new TabellonePODAO();
    }

    public void generaQuarti(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio, int giornataBase) throws SQLException {

        if (squadre.size() != 8) {
            throw new IllegalArgumentException("Servono esattamente 8 squadre per i quarti di finale!");
        }

        for (int i = 0; i < 4; i++) {
            Squadra squadraAlta = squadre.get(i);
            Squadra squadraBassa = squadre.get(7 - i);

            for (int gara = 1; gara <= 5; gara++) {
                Squadra casa = gara % 2 == 1 ? squadraAlta : squadraBassa;
                Squadra ospite = gara % 2 == 1 ? squadraBassa : squadraAlta;

                Partita partita = new Partita(
                        campionato, "PO", giornataBase + gara, casa, ospite,
                        dataInizio.plusWeeks(gara - 1),
                        casa.getSede(), 0, 0, "Programmata"
                );

                int idGenerato = partitaDAO.insertAndGetId(partita);
                partita.setId(idGenerato);

                TabellonePO tabellone = new TabellonePO(partita, "Quarti", gara);
                tabellonePODAO.insert(tabellone);
            }
        }
    }

    public void generaSemifinali(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio, int giornataBase) throws SQLException {

        if (squadre.size() != 4) {
            throw new IllegalArgumentException("Servono esattamente 4 squadre per le semifinali!");
        }

        for (int i = 0; i < 2; i++) {
            Squadra squadraAlta = squadre.get(i);
            Squadra squadraBassa = squadre.get(3 - i);

            for (int gara = 1; gara <= 5; gara++) {
                Squadra casa = gara % 2 == 1 ? squadraAlta : squadraBassa;
                Squadra ospite = gara % 2 == 1 ? squadraBassa : squadraAlta;

                Partita partita = new Partita(
                        campionato, "PO", giornataBase + gara, casa, ospite,
                        dataInizio.plusWeeks(gara - 1),
                        casa.getSede(), 0, 0, "Programmata"
                );

                int idGenerato = partitaDAO.insertAndGetId(partita);
                partita.setId(idGenerato);

                TabellonePO tabellone = new TabellonePO(partita, "Semi", gara);
                tabellonePODAO.insert(tabellone);
            }
        }
    }

    public void generaFinale(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio, int giornataBase) throws SQLException {

        if (squadre.size() != 2) {
            throw new IllegalArgumentException("Servono esattamente 2 squadre per la finale!");
        }

        Squadra squadraAlta = squadre.get(0);
        Squadra squadraBassa = squadre.get(1);

        for (int gara = 1; gara <= 7; gara++) {
            Squadra casa = gara % 2 == 1 ? squadraAlta : squadraBassa;
            Squadra ospite = gara % 2 == 1 ? squadraBassa : squadraAlta;

            Partita partita = new Partita(
                    campionato, "PO", giornataBase + gara, casa, ospite,
                    dataInizio.plusWeeks(gara - 1),
                    casa.getSede(), 0, 0, "Programmata"
            );

            int idGenerato = partitaDAO.insertAndGetId(partita);
            partita.setId(idGenerato);

            TabellonePO tabellone = new TabellonePO(partita, "Finale", gara);
            tabellonePODAO.insert(tabellone);
        }
    }

    public boolean esistePlayoff(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "PO");
        return !partite.isEmpty();
    }

    public void eliminaPlayoff(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "PO");
        for (Partita partita : partite) {
            partitaDAO.delete(partita.getId());
        }
    }
}