package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.DAO.implementazioni.StatisticheDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.LogOperazioniDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Statistiche;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class StatisticheService {

    private final StatisticheDAO statisticheDAO;
    private final LogOperazioniDAO logDAO;

    public StatisticheService() {
        this.statisticheDAO = new StatisticheDAO();
        this.logDAO = new LogOperazioniDAO();
    }

    // Inserisce le statistiche di un giocatore e logga l'operazione
    public boolean inserisciStatistiche(Statistiche statistiche, Amministratore admin) throws SQLException {

        Statistiche esistenti = statisticheDAO.findByGiocatore(statistiche.getGiocatore().getId());

        boolean risultato;

        if (esistenti == null) {
            // Prima volta — crea il record
            risultato = statisticheDAO.insert(statistiche);
        } else {
            // Aggiunge i nuovi valori a quelli esistenti
            esistenti.setPunti(esistenti.getPunti() + statistiche.getPunti());
            esistenti.setRimbalzi(esistenti.getRimbalzi() + statistiche.getRimbalzi());
            esistenti.setAssist(esistenti.getAssist() + statistiche.getAssist());
            risultato = statisticheDAO.update(esistenti);
        }

        if (risultato) {
            logDAO.insert(new LogOperazioni(
                    admin,
                    "UPDATE_STATISTICHE",
                    "Aggiornate statistiche per giocatore: " + statistiche.getGiocatore().getId()
            ));
        }

        return risultato;
    }

    // Aggiorna le statistiche di un giocatore e logga l'operazione
    public boolean aggiornaStatistiche(Statistiche statistiche, Amministratore admin) throws SQLException {

        // Verifica che le statistiche esistano
        Statistiche esistenti = statisticheDAO.findById(statistiche.getId());
        if (esistenti == null) {
            throw new IllegalStateException("Statistiche non trovate — usa inserisci!");
        }

        boolean risultato = statisticheDAO.update(statistiche);

        if (risultato) {
            logDAO.insert(new LogOperazioni(
                    admin,
                    "UPDATE_STATISTICHE",
                    "Aggiornate statistiche per giocatore: " + statistiche.getGiocatore().getId()
            ));
        }

        return risultato;
    }

    // Restituisce la classifica marcatori ordinata per punti decrescenti
    public List<Statistiche> getClassificaMarcatori() throws SQLException {
        List<Statistiche> statistiche = statisticheDAO.findAll();
        statistiche.sort(Comparator.comparingInt(Statistiche::getPunti).reversed());
        return statistiche;
    }

    // Restituisce la classifica rimbalzisti ordinata per rimbalzi decrescenti
    public List<Statistiche> getClassificaRimbalzisti() throws SQLException {
        List<Statistiche> statistiche = statisticheDAO.findAll();
        statistiche.sort(Comparator.comparingInt(Statistiche::getRimbalzi).reversed());
        return statistiche;
    }

    // Restituisce la classifica assistman ordinata per assist decrescenti
    public List<Statistiche> getClassificaAssistman() throws SQLException {
        List<Statistiche> statistiche = statisticheDAO.findAll();
        statistiche.sort(Comparator.comparingInt(Statistiche::getAssist).reversed());
        return statistiche;
    }

    // Restituisce le statistiche di una squadra
    public List<Statistiche> getStatisticheSquadra(String squadraId) throws SQLException {
        return statisticheDAO.findBySquadra(squadraId);
    }

    // Restituisce le statistiche di un singolo giocatore
    public Statistiche getStatisticheGiocatore(String giocId) throws SQLException {
        return statisticheDAO.findByGiocatore(giocId);
    }
}