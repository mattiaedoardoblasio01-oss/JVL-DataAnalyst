package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.interfacce.ICampionatoDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IGiocatoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ISquadraDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.service.CampionatoService;
import it.unipv.JVL_DA.project.view.admin.AdminDashboard;
import it.unipv.JVL_DA.project.view.campionato.CampionatoFrame;
import it.unipv.JVL_DA.project.view.giocatori.GiocatoriFrame;
import it.unipv.JVL_DA.project.view.squadre.SquadreFrame;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller dell'area gestionale (solo Amministratore):
 * CRUD Squadre, CRUD Giocatori (con gestione roster) e CRUD Campionato.
 *
 * Segue lo stesso stile di AuthController: dipendenze dal Model iniettate
 * tramite le interfacce DAO, logging con java.util.logging e tracciamento
 * delle operazioni admin su LogOperazioni.
 */
public class GestioneLegaController {

    private static final Logger logger = Logger.getLogger(GestioneLegaController.class.getName());

    // --- MODEL ---
    private final ISquadraDAO squadraDAO;
    private final IGiocatoreDAO giocatoreDAO;
    private final ICampionatoDAO campionatoDAO;
    private final ILogOperazioniDAO logDAO;
    private final CampionatoService campionatoService;

    // --- SESSIONE ---
    private final Amministratore adminLoggato;

    public GestioneLegaController(ISquadraDAO squadraDAO, IGiocatoreDAO giocatoreDAO,
                              ICampionatoDAO campionatoDAO, ILogOperazioniDAO logDAO,
                              Amministratore adminLoggato) {
        this.squadraDAO = squadraDAO;
        this.giocatoreDAO = giocatoreDAO;
        this.campionatoDAO = campionatoDAO;
        this.logDAO = logDAO;
        this.adminLoggato = adminLoggato;
        // Il Service (come gli altri Service del progetto) istanzia internamente i propri DAO
        this.campionatoService = new CampionatoService();
    }

    /**
     * Aggancia i bottoni di navigazione della dashboard admin
     * ai metodi di apertura delle finestre gestionali.
     */
    public void initDashboard(AdminDashboard dashboard) {
        dashboard.addGestisciSquadreListener(e -> apriGestioneSquadre());
        dashboard.addGestisciGiocatoriListener(e -> apriGestioneGiocatori());
    }

    // =========================================================================
    // GESTIONE SQUADRE (SquadreFrame)
    // =========================================================================

    /** Apre SquadreFrame, aggancia i listener e carica la tabella. */
    public void apriGestioneSquadre() {
        SquadreFrame frame = new SquadreFrame();
        frame.addNuovoListener(e -> frame.clearForm());
        frame.addSalvaListener(e -> salvaSquadra(frame));
        frame.addEliminaListener(e -> eliminaSquadra(frame));
        refreshSquadre(frame);
        frame.setVisible(true);
    }

    private void refreshSquadre(SquadreFrame frame) {
        try {
            frame.populateTable(squadraDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento delle squadre", e);
            frame.showError("Errore nel caricamento delle squadre.");
        }
    }

    /**
     * Salva la squadra: se nessuna riga è selezionata (getSelectedId() == null)
     * esegue un INSERT con id generato, altrimenti un UPDATE della riga selezionata.
     */
    private void salvaSquadra(SquadreFrame frame) {
        String nome = frame.getNome();
        String sede = frame.getSede();

        if (nome.isEmpty() || sede.isEmpty()) {
            frame.showError("Nome e Sede sono obbligatori.");
            return;
        }

        try {
            String selectedId = frame.getSelectedId();

            // Vincolo di unicità sul nome (vale sia per insert sia per rinomina)
            Squadra omonima = squadraDAO.findByNome(nome);
            if (omonima != null && !omonima.getId().equals(selectedId)) {
                frame.showError("Esiste già una squadra con questo nome.");
                return;
            }

            boolean ok;
            String messaggio;

            if (selectedId == null) {
                Squadra nuova = new Squadra(UUID.randomUUID().toString(), nome, sede,
                        frame.getLogoURL(), frame.getAllenatore());
                ok = squadraDAO.insert(nuova);
                messaggio = "Squadra creata con successo.";
                if (ok) {
                    logDAO.insert(new LogOperazioni(adminLoggato, "INSERT_SQUADRA",
                            "Creata squadra: " + nome));
                }
            } else {
                Squadra modificata = new Squadra(selectedId, nome, sede,
                        frame.getLogoURL(), frame.getAllenatore());
                ok = squadraDAO.update(modificata);
                messaggio = "Squadra aggiornata con successo.";
                if (ok) {
                    logDAO.insert(new LogOperazioni(adminLoggato, "UPDATE_SQUADRA",
                            "Aggiornata squadra: " + selectedId));
                }
            }

            if (ok) {
                refreshSquadre(frame);
                frame.clearForm();
                frame.showSuccess(messaggio);
            } else {
                frame.showError("Salvataggio non riuscito.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il salvataggio della squadra", e);
            frame.showError("Errore DB durante il salvataggio.");
        }
    }

    private void eliminaSquadra(SquadreFrame frame) {
        String selectedId = frame.getSelectedId();
        if (selectedId == null) {
            frame.showError("Seleziona una squadra dalla tabella.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare la squadra selezionata?", "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        try {
            if (squadraDAO.delete(selectedId)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "DELETE_SQUADRA",
                        "Eliminata squadra: " + selectedId));
                refreshSquadre(frame);
                frame.clearForm();
                frame.showSuccess("Squadra eliminata.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'eliminazione della squadra", e);
            frame.showError("Impossibile eliminare: la squadra ha giocatori o partite collegate.");
        }
    }

    // =========================================================================
    // GESTIONE GIOCATORI E ROSTER (GiocatoriFrame)
    // =========================================================================

    /** Apre GiocatoriFrame, aggancia i listener e carica tabella + combo squadre. */
    public void apriGestioneGiocatori() {
        GiocatoriFrame frame = new GiocatoriFrame();
        frame.addNuovoListener(e -> frame.clearForm());
        frame.addSalvaListener(e -> salvaGiocatore(frame));
        frame.addEliminaListener(e -> eliminaGiocatore(frame));

        try {
            frame.populateSquadreComboBox(squadraDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento delle squadre", e);
            frame.showError("Errore nel caricamento delle squadre.");
        }
        refreshGiocatori(frame);
        frame.setVisible(true);
    }

    private void refreshGiocatori(GiocatoriFrame frame) {
        try {
            frame.populateTable(giocatoreDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei giocatori", e);
            frame.showError("Errore nel caricamento dei giocatori.");
        }
    }

    /**
     * Salva il giocatore gestendo i vincoli di roster:
     * - numero di maglia unico all'interno della stessa squadra (controllo nel Controller);
     * - massimo 15 giocatori per squadra (vincolo già applicato da GiocatoreDAO.insert,
     *   la cui SQLException viene mostrata all'utente).
     */
    private void salvaGiocatore(GiocatoriFrame frame) {
        String nome = frame.getNome();
        String cognome = frame.getCognome();
        String ruolo = frame.getRuolo();
        Squadra squadra = frame.getSelectedSquadra();
        int nMaglia = frame.getNumeroMaglia();

        if (nome.isEmpty() || cognome.isEmpty() || ruolo.isEmpty()) {
            frame.showError("Nome, Cognome e Ruolo sono obbligatori.");
            return;
        }
        if (squadra == null) {
            frame.showError("Seleziona una squadra per il giocatore.");
            return;
        }

        try {
            String selectedId = frame.getSelectedId();

            // Gestione roster: numero di maglia unico nella squadra scelta
            for (Giocatore g : giocatoreDAO.findBySquadra(squadra.getId())) {
                if (g.getNMaglia() == nMaglia && !g.getId().equals(selectedId)) {
                    frame.showError("Numero " + nMaglia + " già assegnato a "
                            + g.getNome() + " " + g.getCognome() + ".");
                    return;
                }
            }

            boolean ok;
            String messaggio;

            if (selectedId == null) {
                Giocatore nuovo = new Giocatore(UUID.randomUUID().toString(),
                        nome, cognome, ruolo, nMaglia, squadra);
                ok = giocatoreDAO.insert(nuovo); // blocca i roster oltre i 15 giocatori
                messaggio = "Giocatore inserito nel roster.";
                if (ok) {
                    logDAO.insert(new LogOperazioni(adminLoggato, "INSERT_GIOCATORE",
                            "Inserito giocatore: " + nome + " " + cognome
                                    + " (" + squadra.getNome() + ")"));
                }
            } else {
                Giocatore modificato = new Giocatore(selectedId,
                        nome, cognome, ruolo, nMaglia, squadra);
                ok = giocatoreDAO.update(modificato);
                messaggio = "Giocatore aggiornato.";
                if (ok) {
                    logDAO.insert(new LogOperazioni(adminLoggato, "UPDATE_GIOCATORE",
                            "Aggiornato giocatore: " + selectedId));
                }
            }

            if (ok) {
                refreshGiocatori(frame);
                frame.clearForm();
                frame.showSuccess(messaggio);
            } else {
                frame.showError("Salvataggio non riuscito.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il salvataggio del giocatore", e);
            // Mostra anche il messaggio "Roster completo: massimo 15 giocatori per squadra"
            frame.showError(e.getMessage());
        }
    }

    private void eliminaGiocatore(GiocatoriFrame frame) {
        String selectedId = frame.getSelectedId();
        if (selectedId == null) {
            frame.showError("Seleziona un giocatore dalla tabella.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare il giocatore selezionato?", "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        try {
            if (giocatoreDAO.delete(selectedId)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "DELETE_GIOCATORE",
                        "Eliminato giocatore: " + selectedId));
                refreshGiocatori(frame);
                frame.clearForm();
                frame.showSuccess("Giocatore eliminato dal roster.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'eliminazione del giocatore", e);
            frame.showError("Impossibile eliminare: il giocatore ha statistiche collegate.");
        }
    }

    // =========================================================================
    // GESTIONE CAMPIONATO (CampionatoFrame)
    // =========================================================================

    /** Apre CampionatoFrame, aggancia i listener e carica la tabella. */
    public void apriGestioneCampionato() {
        CampionatoFrame frame = new CampionatoFrame();
        frame.addAggiungiListener(e -> aggiungiCampionato(frame));
        frame.addModificaListener(e -> modificaCampionato(frame));
        frame.addEliminaListener(e -> eliminaCampionato(frame));
        refreshCampionati(frame);
        frame.setVisible(true);
    }

    private void refreshCampionati(CampionatoFrame frame) {
        try {
            frame.popolaTabella(campionatoDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei campionati", e);
            frame.mostraErrore("Errore nel caricamento dei campionati.");
        }
    }

    /**
     * Crea un nuovo campionato delegando a CampionatoService, che forza lo
     * stato iniziale a "Config" e impedisce di avere più campionati
     * contemporaneamente in stato Config/Attivo.
     */
    private void aggiungiCampionato(CampionatoFrame frame) {
        try {
            Campionato dalForm = frame.getCampionatoDalForm(); // valida il form (può lanciare Exception)
            Campionato creato = campionatoService.creaCampionato(
                    dalForm.getNome(), dalForm.getAnno(),
                    dalForm.getDataInizio(), dalForm.getDataFine());

            logDAO.insert(new LogOperazioni(adminLoggato, "INSERT_CAMPIONATO",
                    "Creato campionato: " + creato.getNome() + " (id " + creato.getId() + ")"));
            refreshCampionati(frame);
            frame.mostraSuccesso("Campionato creato in stato 'Config'.");
        } catch (IllegalStateException e) {
            frame.mostraErrore(e.getMessage()); // es. esiste già un campionato Attivo/Config
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la creazione del campionato", e);
            frame.mostraErrore("Errore DB durante la creazione del campionato.");
        } catch (Exception e) {
            frame.mostraErrore(e.getMessage()); // messaggi di validazione del form
        }
    }

    /**
     * Aggiorna il campionato selezionato con i dati del form.
     * Nota: le transizioni di stagione (avvio Regular Season, Playoff, chiusura)
     * restano gestite da CalendarioController tramite CampionatoService.
     */
    private void modificaCampionato(CampionatoFrame frame) {
        try {
            int id = frame.getIdSelezionato(); // lancia Exception se nessuna riga è selezionata
            Campionato modificato = frame.getCampionatoDalForm();
            modificato.setId(id);

            if (campionatoDAO.update(modificato)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "UPDATE_CAMPIONATO",
                        "Aggiornato campionato id " + id));
                refreshCampionati(frame);
                frame.mostraSuccesso("Campionato aggiornato.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'aggiornamento del campionato", e);
            frame.mostraErrore("Errore DB durante l'aggiornamento del campionato.");
        } catch (Exception e) {
            frame.mostraErrore(e.getMessage());
        }
    }

    private void eliminaCampionato(CampionatoFrame frame) {
        try {
            int id = frame.getIdSelezionato(); // lancia Exception se nessuna riga è selezionata

            int conferma = JOptionPane.showConfirmDialog(frame,
                    "Eliminare il campionato selezionato?", "Conferma eliminazione",
                    JOptionPane.YES_NO_OPTION);
            if (conferma != JOptionPane.YES_OPTION) return;

            if (campionatoDAO.delete(id)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "DELETE_CAMPIONATO",
                        "Eliminato campionato id " + id));
                refreshCampionati(frame);
                frame.mostraSuccesso("Campionato eliminato.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'eliminazione del campionato", e);
            frame.mostraErrore("Impossibile eliminare: il campionato ha partite collegate.");
        } catch (Exception e) {
            frame.mostraErrore(e.getMessage());
        }
    }
}
