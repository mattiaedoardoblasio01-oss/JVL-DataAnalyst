package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.interfacce.IGiocatoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IStatisticheDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Statistiche;
import it.unipv.JVL_DA.project.service.StatisticheService;
import it.unipv.JVL_DA.project.view.statistiche.StatisticheFrame;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller delle statistiche (solo Amministratore): inserimento dei
 * tabellini di giornata, correzione/eliminazione dei totali, consultazione
 * del log operazioni e classifiche individuali (marcatori, rimbalzisti,
 * assistman).
 * Segue lo stesso stile degli altri Controller: interfacce DAO iniettate
 * dal costruttore, Service istanziato internamente (come fanno i Service
 * con i propri DAO), listener agganciati tramite i metodi pubblici esposti
 * da StatisticheFrame.
 */
public class StatisticheController {

    private static final Logger logger = Logger.getLogger(StatisticheController.class.getName());

    // --- VIEW ---
    private final StatisticheFrame view;

    // --- MODEL ---
    private final IGiocatoreDAO giocatoreDAO;
    private final IStatisticheDAO statisticheDAO;
    private final ILogOperazioniDAO logDAO;
    private final StatisticheService statisticheService;

    // --- SESSIONE ---
    private final Amministratore adminLoggato;

    public StatisticheController(StatisticheFrame view, IGiocatoreDAO giocatoreDAO,
                                 IStatisticheDAO statisticheDAO, ILogOperazioniDAO logDAO,
                                 Amministratore adminLoggato) {
        this.view = view;
        this.giocatoreDAO = giocatoreDAO;
        this.statisticheDAO = statisticheDAO;
        this.logDAO = logDAO;
        this.adminLoggato = adminLoggato;
        this.statisticheService = new StatisticheService();

        initListeners();
        caricaDatiIniziali();
    }

    /**
     * Aggancia la logica ai bottoni della View.
     * Il bottone "Nuovo" pulisce già il form con un listener interno alla View,
     * quindi non viene agganciato qui.
     */
    private void initListeners() {
        view.addSalvaStatisticheListener(e -> salvaStatistiche());
        view.addEliminaStatisticheListener(e -> eliminaStatistiche());
        view.addAggiornaLogListener(e -> aggiornaLog());
    }

    /** Carica combo giocatori, tabella statistiche e log all'apertura. */
    private void caricaDatiIniziali() {
        try {
            view.populateGiocatoriComboBox(giocatoreDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei giocatori", e);
            view.showErrorStatistiche("Errore nel caricamento dei giocatori.");
        }
        refreshStatistiche();
        aggiornaLog();
    }

    private void refreshStatistiche() {
        try {
            view.populateStatisticheTable(statisticheDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento delle statistiche", e);
            view.showErrorStatistiche("Errore nel caricamento delle statistiche.");
        }
    }

    // =========================================================================
    // INSERIMENTO TABELLINI E CORREZIONI
    // =========================================================================

    /**
     * Salva i dati del form:
     * - nessuna riga selezionata (getSelectedStatisticheId() == -1) →
     *   INSERIMENTO TABELLINO: StatisticheService SOMMA i valori ai totali
     *   già registrati per il giocatore (o crea il record alla prima volta);
     * - riga selezionata → CORREZIONE: il record viene SOSTITUITO con i
     *   valori del form tramite aggiornaStatistiche.
     * In entrambi i casi il Service registra l'operazione nel log admin.
     */
    private void salvaStatistiche() {
        Giocatore giocatore = view.getGiocatoreSelezionato();
        if (giocatore == null) {
            view.showErrorStatistiche("Seleziona un giocatore.");
            return;
        }

        try {
            int selectedId = view.getSelectedStatisticheId();
            boolean ok;
            String messaggio;

            if (selectedId == -1) {
                Statistiche tabellino = new Statistiche(giocatore,
                        view.getPunti(), view.getRimbalzi(), view.getAssist());
                ok = statisticheService.inserisciStatistiche(tabellino, adminLoggato);
                messaggio = "Tabellino registrato: valori sommati ai totali del giocatore.";
            } else {
                // Guardia: l'UPDATE del DAO non modifica il giocatore associato
                // (gioc_id), quindi blocchiamo la correzione se nel combo è stato
                // scelto un giocatore diverso da quello della riga selezionata.
                Statistiche esistenti = statisticheDAO.findById(selectedId);
                if (esistenti != null && esistenti.getGiocatore() != null
                        && !esistenti.getGiocatore().getId().equals(giocatore.getId())) {
                    view.showErrorStatistiche(
                            "La riga selezionata appartiene a un altro giocatore: usa 'Nuovo' per un nuovo tabellino.");
                    return;
                }

                Statistiche corrette = new Statistiche(selectedId, giocatore,
                        view.getPunti(), view.getRimbalzi(), view.getAssist());
                ok = statisticheService.aggiornaStatistiche(corrette, adminLoggato);
                messaggio = "Statistiche corrette.";
            }

            if (ok) {
                refreshStatistiche();
                view.clearFormStatistiche();
                view.showSuccessStatistiche(messaggio);
                aggiornaLog(); // il Service ha appena scritto una riga di log
            } else {
                view.showErrorStatistiche("Salvataggio non riuscito.");
            }
        } catch (IllegalStateException e) {
            view.showErrorStatistiche(e.getMessage()); // es. "Statistiche non trovate — usa inserisci!"
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il salvataggio delle statistiche", e);
            view.showErrorStatistiche("Errore DB durante il salvataggio.");
        }
    }

    /** Elimina il record di statistiche selezionato in tabella (con conferma). */
    private void eliminaStatistiche() {
        int selectedId = view.getSelectedStatisticheId();
        if (selectedId == -1) {
            view.showErrorStatistiche("Seleziona una riga dalla tabella.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(view,
                "Eliminare le statistiche selezionate?", "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        try {
            if (statisticheDAO.delete(selectedId)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "DELETE_STATISTICHE",
                        "Eliminate statistiche id " + selectedId));
                refreshStatistiche();
                view.clearFormStatistiche();
                view.showSuccessStatistiche("Statistiche eliminate.");
                aggiornaLog();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'eliminazione delle statistiche", e);
            view.showErrorStatistiche("Errore DB durante l'eliminazione.");
        }
    }

    // =========================================================================
    // LOG OPERAZIONI (tab in sola lettura)
    // =========================================================================

    /** Ricarica la tabella del log operazioni (bottone "Aggiorna Log" e refresh interni). */
    private void aggiornaLog() {
        try {
            view.populateLogTable(logDAO.findAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento del log operazioni", e);
        }
    }

    // =========================================================================
    // CLASSIFICHE INDIVIDUALI (delegate al Service, riusabili da altre View)
    // =========================================================================

    /** Classifica marcatori ordinata per punti decrescenti. */
    public List<Statistiche> getClassificaMarcatori() throws SQLException {
        return statisticheService.getClassificaMarcatori();
    }

    /** Classifica rimbalzisti ordinata per rimbalzi decrescenti. */
    public List<Statistiche> getClassificaRimbalzisti() throws SQLException {
        return statisticheService.getClassificaRimbalzisti();
    }

    /** Classifica assistman ordinata per assist decrescenti. */
    public List<Statistiche> getClassificaAssistman() throws SQLException {
        return statisticheService.getClassificaAssistman();
    }
}