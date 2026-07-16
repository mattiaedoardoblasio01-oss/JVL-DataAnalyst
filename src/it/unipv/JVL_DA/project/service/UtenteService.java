package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.dao.implementazioni.GiocatoreDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.LogOperazioniDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.UtenteDAO;
import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.LogOperazioni;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Utente;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UtenteService {

    private final UtenteDAO utenteDAO;
    private final GiocatoreDAO giocatoreDAO;
    private final SquadraDAO squadraDAO;
    private final LogOperazioniDAO logDAO;

    public UtenteService() {
        this.utenteDAO = new UtenteDAO();
        this.giocatoreDAO = new GiocatoreDAO();
        this.squadraDAO = new SquadraDAO();
        this.logDAO = new LogOperazioniDAO();
    }

    // ── Registrazione ──────────────────────────────────────────────────

    public boolean registra(Utente utente) throws SQLException {
        // ... controlli ...
        boolean risultato = utenteDAO.insert(utente);
        if (risultato) {
            logDAO.insert(new LogOperazioni(null, "REGISTRAZIONE_UTENTE",
                    "Nuovo utente registrato: " + utente.getUsername()));
        }
        return risultato;
    }

    // ── Login ──────────────────────────────────────────────────────────

    public Utente login(String username, String password) throws SQLException {
        return utenteDAO.login(username, password);
    }

    // ── Modifica profilo ───────────────────────────────────────────────

    public boolean modificaProfilo(Utente utente) throws SQLException {

        // Verifica che il nuovo username non sia già in uso da un altro utente
        Utente esistente = utenteDAO.findByUsername(utente.getUsername());
        if (esistente != null && esistente.getId() != utente.getId()) {
            throw new IllegalStateException("Username già in uso!");
        }

        // Verifica che la nuova email non sia già in uso da un altro utente
        Utente esistenteEmail = utenteDAO.findByEmail(utente.getEmail());
        if (esistenteEmail != null && esistenteEmail.getId() != utente.getId()) {
            throw new IllegalStateException("Email già in uso!");
        }

        return utenteDAO.update(utente);
    }

    // ── Ricerca e filtri ───────────────────────────────────────────────

    public List<Giocatore> cercaGiocatori(String query) throws SQLException {
        List<Giocatore> tutti = giocatoreDAO.findAll();
        String queryLower = query.toLowerCase();

        return tutti.stream()
                .filter(g -> g.getNome().toLowerCase().contains(queryLower) ||
                        g.getCognome().toLowerCase().contains(queryLower) ||
                        g.getRuolo().toLowerCase().contains(queryLower))
                .collect(Collectors.toList());
    }

    public List<Squadra> cercaSquadre(String query) throws SQLException {
        List<Squadra> tutte = squadraDAO.findAll();
        String queryLower = query.toLowerCase();

        return tutte.stream()
                .filter(s -> s.getNome().toLowerCase().contains(queryLower) ||
                        s.getSede().toLowerCase().contains(queryLower))
                .collect(Collectors.toList());
    }

    public List<Giocatore> filtraGiocatoriPerRuolo(String ruolo) throws SQLException {
        return giocatoreDAO.findByRuolo(ruolo);
    }

    public List<Giocatore> filtraGiocatoriPerSquadra(String squadraId) throws SQLException {
        return giocatoreDAO.findBySquadra(squadraId);
    }

    // ── Preferenze ─────────────────────────────────────────────────────

    public boolean aggiungiGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException {
        return utenteDAO.aggiungiGiocatorePreferito(utenteId, giocatoreId);
    }

    public boolean rimuoviGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException {
        return utenteDAO.rimuoviGiocatorePreferito(utenteId, giocatoreId);
    }

    public List<Giocatore> getGiocatoriPreferiti(int utenteId) throws SQLException {
        return utenteDAO.getGiocatoriPreferiti(utenteId);
    }

    public boolean aggiungiSquadraPreferita(int utenteId, String squadraId) throws SQLException {
        return utenteDAO.aggiungiSquadraPreferita(utenteId, squadraId);
    }

    public boolean rimuoviSquadraPreferita(int utenteId, String squadraId) throws SQLException {
        return utenteDAO.rimuoviSquadraPreferita(utenteId, squadraId);
    }

    public List<Squadra> getSquadrePreferite(int utenteId) throws SQLException {
        return utenteDAO.getSquadrePreferite(utenteId);
    }

    // ── Cancellazione account (GDPR) ───────────────────────────────────

    public boolean cancellaAccount(int utenteId) throws SQLException {
        return utenteDAO.delete(utenteId);
    }
}