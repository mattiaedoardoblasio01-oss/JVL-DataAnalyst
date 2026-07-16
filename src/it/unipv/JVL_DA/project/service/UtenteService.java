package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.dao.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.GiocatoreDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.LogOperazioniDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.dao.implementazioni.UtenteDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.LogOperazioni;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.RigaClassifica;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Utente;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtenteService {

    private final UtenteDAO utenteDAO;
    private final GiocatoreDAO giocatoreDAO;
    private final SquadraDAO squadraDAO;
    private final LogOperazioniDAO logDAO;
    private final CampionatoDAO campionatoDAO;
    private final PartitaDAO partitaDAO;

    public UtenteService() {
        this.utenteDAO = new UtenteDAO();
        this.giocatoreDAO = new GiocatoreDAO();
        this.squadraDAO = new SquadraDAO();
        this.logDAO = new LogOperazioniDAO();
        this.campionatoDAO = new CampionatoDAO();
        this.partitaDAO = new PartitaDAO();
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

    // ── Consultazione pubblica: campionati, calendario e classifica ────

    /** Tutti i campionati, per popolare i filtri delle schede Classifica e Calendario. */
    public List<Campionato> getCampionati() throws SQLException {
        return campionatoDAO.findAll();
    }

    /** Partite di Regular Season di un campionato (sola lettura). */
    public List<Partita> getPartiteRegularSeason(int campId) throws SQLException {
        return partitaDAO.findByCampionatoAndFase(campId, "RS");
    }

    /**
     * Calcola la classifica di Regular Season contando solo le partite in stato
     * "Conclusa"; a parità di vittorie ordina per differenza canestri.
     * Stesso algoritmo usato lato admin, qui esposto come metodo di Service
     * così il Controller non deve conoscere la logica di calcolo: riceve
     * righe già pronte (RigaClassifica) e le mappa in tabella.
     */
    public List<RigaClassifica> getClassificaRegularSeason(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "RS");

        Map<String, Squadra> squadre = new LinkedHashMap<>();
        Map<String, Integer> giocate = new HashMap<>();
        Map<String, Integer> vittorie = new HashMap<>();
        Map<String, Integer> diff = new HashMap<>();

        for (Partita p : partite) {
            squadre.putIfAbsent(p.getCasa().getId(), p.getCasa());
            squadre.putIfAbsent(p.getOspite().getId(), p.getOspite());
            if (!"Conclusa".equals(p.getStato())) continue;

            giocate.merge(p.getCasa().getId(), 1, Integer::sum);
            giocate.merge(p.getOspite().getId(), 1, Integer::sum);

            int d = p.getScoreCasa() - p.getScoreOsp();
            diff.merge(p.getCasa().getId(), d, Integer::sum);
            diff.merge(p.getOspite().getId(), -d, Integer::sum);

            if (d > 0) vittorie.merge(p.getCasa().getId(), 1, Integer::sum);
            else if (d < 0) vittorie.merge(p.getOspite().getId(), 1, Integer::sum);
        }

        List<Squadra> ordinata = new ArrayList<>(squadre.values());
        ordinata.sort(
                Comparator.comparing((Squadra s) -> vittorie.getOrDefault(s.getId(), 0), Comparator.reverseOrder())
                        .thenComparing(s -> diff.getOrDefault(s.getId(), 0), Comparator.reverseOrder()));

        List<RigaClassifica> classifica = new ArrayList<>();
        int pos = 1;
        for (Squadra s : ordinata) {
            int g = giocate.getOrDefault(s.getId(), 0);
            int v = vittorie.getOrDefault(s.getId(), 0);
            classifica.add(new RigaClassifica(pos++, s, g, v, diff.getOrDefault(s.getId(), 0)));
        }
        return classifica;
    }

    // ── Cancellazione account (GDPR) ───────────────────────────────────

    public boolean cancellaAccount(int utenteId) throws SQLException {
        return utenteDAO.delete(utenteId);
    }
}