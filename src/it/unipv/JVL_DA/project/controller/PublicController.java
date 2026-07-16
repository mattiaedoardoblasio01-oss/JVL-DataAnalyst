package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.RigaClassifica;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Utente;
import it.unipv.JVL_DA.project.service.UtenteService;
import it.unipv.JVL_DA.project.view.ricerca.RicercaFrame;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller dell'area pubblica (Utente/tifoso): ricerca e filtri su
 * giocatori e squadre tramite RicercaFrame, consultazione di classifica e
 * calendario di Regular Season, gestione delle preferenze dell'utente
 * loggato e cancellazione account (GDPR).
 *
 * Tutta la logica di ricerca, calcolo classifica, preferenze e cancellazione
 * è delegata a UtenteService; il Controller legge i componenti esposti dai
 * getter della View, applica i filtri combinati e riempie le tabelle.
 */
public class PublicController {

    private static final Logger logger = Logger.getLogger(PublicController.class.getName());

    /** Voce del ComboBox ruoli (pre-popolato dalla View) che disattiva il filtro. */
    private static final String FILTRO_TUTTI = "Tutti";

    /** Formato di visualizzazione di data e ora nella scheda Calendario. */
    private static final DateTimeFormatter FMT_DATAORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // --- VIEW ---
    private final RicercaFrame view;

    // --- MODEL ---
    private final UtenteService utenteService;

    // --- SESSIONE (può essere null: la ricerca resta consultabile anche senza login) ---
    private final Utente utenteLoggato;

    // Ultimi risultati mostrati: la riga i-esima della tabella corrisponde
    // all'elemento i-esimo della lista (usati per i preferiti col doppio click)
    private List<Giocatore> ultimiGiocatoriTrovati = new ArrayList<>();
    private List<Squadra> ultimeSquadreTrovate = new ArrayList<>();

    // Preferiti attualmente mostrati: riga i-esima ↔ elemento i-esimo (per la rimozione)
    private List<Giocatore> giocatoriPreferitiVisualizzati = new ArrayList<>();
    private List<Squadra> squadrePreferiteVisualizzate = new ArrayList<>();

    public PublicController(RicercaFrame view, Utente utenteLoggato) {
        this.view = view;
        this.utenteLoggato = utenteLoggato;
        // Il Service (come gli altri Service del progetto) istanzia internamente i propri DAO
        this.utenteService = new UtenteService();

        initListeners();
        caricaFiltri();
        caricaCampionati();
    }

    /** Aggancia la logica ai componenti esposti dai getter della View. */
    private void initListeners() {
        view.getBtnCercaGiocatori().addActionListener(e -> cercaGiocatori());
        view.getBtnCercaSquadre().addActionListener(e -> cercaSquadre());

        // Doppio click su una riga → aggiunta ai preferiti dell'utente loggato
        view.getTabellaGiocatori().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) aggiungiGiocatorePreferitoDaTabella();
            }
        });
        view.getTabellaSquadre().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) aggiungiSquadraPreferitaDaTabella();
            }
        });

        // Schede Classifica, Calendario e Preferiti
        view.getBtnMostraClassifica().addActionListener(e -> mostraClassifica());
        view.getBtnMostraCalendario().addActionListener(e -> mostraCalendario());
        view.getBtnAggiornaPreferiti().addActionListener(e -> aggiornaPreferiti());
        view.getBtnRimuoviGiocatorePreferito()
                .addActionListener(e -> rimuoviGiocatorePreferitoSelezionato());
        view.getBtnRimuoviSquadraPreferita()
                .addActionListener(e -> rimuoviSquadraPreferitaSelezionata());
    }

    // =========================================================================
    // RICERCA E FILTRI
    // =========================================================================

    /**
     * Popola il ComboBox delle squadre, che la View lascia volutamente vuoto
     * ("Verrà popolata dal Controller con gli oggetti Squadra dal DB").
     * Nessuna selezione = filtro squadra disattivato; il ComboBox dei ruoli
     * è invece già pre-popolato dalla View con "Tutti" + ruoli standard.
     */
    private void caricaFiltri() {
        try {
            JComboBox<Squadra> comboSquadre = view.getComboFiltroSquadra();
            comboSquadre.removeAllItems();
            for (Squadra s : utenteService.cercaSquadre("")) { // query vuota → tutte
                comboSquadre.addItem(s);
            }
            comboSquadre.setSelectedIndex(-1); // di default nessun filtro squadra
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei filtri di ricerca", e);
        }
    }

    /**
     * Ricerca giocatori per nome/cognome/ruolo tramite UtenteService e applica
     * in AND i filtri scelti nei ComboBox (ruolo diverso da "Tutti", squadra
     * selezionata). Con campo di testo vuoto la ricerca restituisce tutti.
     */
    private void cercaGiocatori() {
        try {
            String query = view.getTxtCercaNomeCognomeGiocatore().getText().trim();
            List<Giocatore> risultati = new ArrayList<>(utenteService.cercaGiocatori(query));

            String ruolo = (String) view.getComboFiltroRuolo().getSelectedItem();
            if (ruolo != null && !FILTRO_TUTTI.equalsIgnoreCase(ruolo)) {
                risultati.removeIf(g -> !ruolo.equalsIgnoreCase(g.getRuolo()));
            }

            Squadra squadra = (Squadra) view.getComboFiltroSquadra().getSelectedItem();
            if (squadra != null) {
                risultati.removeIf(g -> g.getSquadra() == null
                        || !g.getSquadra().getId().equals(squadra.getId()));
            }

            ultimiGiocatoriTrovati = risultati;

            // Ordine dei valori allineato alle colonne di modelGiocatori:
            // {ID, Nome, Cognome, Ruolo, N° Maglia, Squadra}
            DefaultTableModel model = view.getModelGiocatori();
            model.setRowCount(0);
            for (Giocatore g : risultati) {
                model.addRow(new Object[]{
                        g.getId(),
                        g.getNome(),
                        g.getCognome(),
                        g.getRuolo(),
                        g.getNMaglia(),
                        g.getSquadra() != null ? g.getSquadra().getNome() : ""
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la ricerca dei giocatori", e);
            JOptionPane.showMessageDialog(view, "Errore durante la ricerca dei giocatori.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ricerca squadre applicando insieme i filtri Nome e Sede
     * (entrambi vuoti → tutte le squadre).
     */
    private void cercaSquadre() {
        try {
            String nomeQuery = view.getTxtCercaNomeSquadra().getText().trim().toLowerCase();
            String sedeQuery = view.getTxtCercaSedeSquadra().getText().trim().toLowerCase();

            List<Squadra> risultati = new ArrayList<>(utenteService.cercaSquadre(""));
            risultati.removeIf(s -> !s.getNome().toLowerCase().contains(nomeQuery)
                    || !s.getSede().toLowerCase().contains(sedeQuery));

            ultimeSquadreTrovate = risultati;

            // Ordine dei valori allineato alle colonne di modelSquadre:
            // {ID Squadra, Nome, Sede, Allenatore, URL Logo}
            DefaultTableModel model = view.getModelSquadre();
            model.setRowCount(0);
            for (Squadra s : risultati) {
                model.addRow(new Object[]{
                        s.getId(),
                        s.getNome(),
                        s.getSede(),
                        s.getAllenatore(),
                        s.getLogoURL()
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la ricerca delle squadre", e);
            JOptionPane.showMessageDialog(view, "Errore durante la ricerca delle squadre.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // CLASSIFICA E CALENDARIO (sola consultazione)
    // =========================================================================

    /** Popola i ComboBox campionato delle schede Classifica e Calendario. */
    private void caricaCampionati() {
        try {
            JComboBox<Campionato> comboClass = view.getComboCampionatoClassifica();
            JComboBox<Campionato> comboCal = view.getComboCampionatoCalendario();
            comboClass.removeAllItems();
            comboCal.removeAllItems();
            for (Campionato c : utenteService.getCampionati()) {
                comboClass.addItem(c);
                comboCal.addItem(c);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei campionati", e);
        }
    }

    /** Calcola e mostra la classifica RS del campionato selezionato. */
    private void mostraClassifica() {
        Campionato campionato = (Campionato) view.getComboCampionatoClassifica().getSelectedItem();
        if (campionato == null) {
            JOptionPane.showMessageDialog(view, "Seleziona un campionato.",
                    "Classifica", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<RigaClassifica> classifica =
                    utenteService.getClassificaRegularSeason(campionato.getId());

            // Ordine dei valori allineato alle colonne di modelClassifica:
            // {Pos., Squadra, Giocate, Vittorie, Sconfitte, Diff. Canestri}
            DefaultTableModel model = view.getModelClassifica();
            model.setRowCount(0);
            for (RigaClassifica r : classifica) {
                model.addRow(new Object[]{
                        r.getPosizione(),
                        r.getSquadra() != null ? r.getSquadra().getNome() : "",
                        r.getGiocate(),
                        r.getVittorie(),
                        r.getSconfitte(),
                        r.getDiffCanestri()
                });
            }
            if (classifica.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Nessuna partita di Regular Season per questo campionato.",
                        "Classifica", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il calcolo della classifica", e);
            JOptionPane.showMessageDialog(view, "Errore durante il calcolo della classifica.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Mostra le partite RS della giornata selezionata per il campionato scelto. */
    private void mostraCalendario() {
        Campionato campionato = (Campionato) view.getComboCampionatoCalendario().getSelectedItem();
        if (campionato == null) {
            JOptionPane.showMessageDialog(view, "Seleziona un campionato.",
                    "Calendario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int giornata = (Integer) view.getSpinGiornataCalendario().getValue();

        try {
            // Ordine dei valori allineato alle colonne di modelCalendario:
            // {ID Partita, Data e Ora, Casa, Ospite, Punti Casa, Punti Ospite, Luogo, Stato}
            DefaultTableModel model = view.getModelCalendario();
            model.setRowCount(0);
            for (Partita p : utenteService.getPartiteRegularSeason(campionato.getId())) {
                if (p.getGiornata() != giornata) continue;
                model.addRow(new Object[]{
                        p.getId(),
                        p.getDataOra() != null ? FMT_DATAORA.format(p.getDataOra()) : "",
                        p.getCasa() != null ? p.getCasa().getNome() : "",
                        p.getOspite() != null ? p.getOspite().getNome() : "",
                        p.getScoreCasa(),
                        p.getScoreOsp(),
                        p.getLuogo(),
                        p.getStato()
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento delle partite", e);
            JOptionPane.showMessageDialog(view, "Errore nel caricamento delle partite.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // PREFERENZE UTENTE
    // =========================================================================

    /** Doppio click sulla tabella giocatori → aggiunta ai preferiti (con conferma). */
    private void aggiungiGiocatorePreferitoDaTabella() {
        if (richiedeLogin()) return;

        int row = view.getTabellaGiocatori().getSelectedRow();
        if (row == -1) return;
        int modelRow = view.getTabellaGiocatori().convertRowIndexToModel(row);
        if (modelRow >= ultimiGiocatoriTrovati.size()) return;

        Giocatore giocatore = ultimiGiocatoriTrovati.get(modelRow);
        int conferma = JOptionPane.showConfirmDialog(view,
                "Aggiungere " + giocatore.getNome() + " " + giocatore.getCognome()
                        + " ai tuoi preferiti?",
                "Preferiti", JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        if (aggiungiGiocatorePreferito(giocatore)) {
            JOptionPane.showMessageDialog(view, "Giocatore aggiunto ai preferiti.");
        } else {
            JOptionPane.showMessageDialog(view, "Il giocatore è già nei tuoi preferiti.",
                    "Preferiti", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Doppio click sulla tabella squadre → aggiunta ai preferiti (con conferma). */
    private void aggiungiSquadraPreferitaDaTabella() {
        if (richiedeLogin()) return;

        int row = view.getTabellaSquadre().getSelectedRow();
        if (row == -1) return;
        int modelRow = view.getTabellaSquadre().convertRowIndexToModel(row);
        if (modelRow >= ultimeSquadreTrovate.size()) return;

        Squadra squadra = ultimeSquadreTrovate.get(modelRow);
        int conferma = JOptionPane.showConfirmDialog(view,
                "Aggiungere " + squadra.getNome() + " alle tue squadre preferite?",
                "Preferiti", JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        if (aggiungiSquadraPreferita(squadra)) {
            JOptionPane.showMessageDialog(view, "Squadra aggiunta ai preferiti.");
        } else {
            JOptionPane.showMessageDialog(view, "La squadra è già nei tuoi preferiti.",
                    "Preferiti", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Aggiunge un giocatore ai preferiti dell'utente loggato. */
    public boolean aggiungiGiocatorePreferito(Giocatore giocatore) {
        if (richiedeLogin()) return false;
        try {
            return utenteService.aggiungiGiocatorePreferito(utenteLoggato.getId(), giocatore.getId());
        } catch (SQLException e) {
            // Tipicamente violazione di chiave primaria: giocatore già nei preferiti
            logger.log(Level.WARNING, "Impossibile aggiungere il giocatore ai preferiti", e);
            return false;
        }
    }

    /** Rimuove un giocatore dai preferiti dell'utente loggato. */
    public boolean rimuoviGiocatorePreferito(Giocatore giocatore) {
        if (richiedeLogin()) return false;
        try {
            return utenteService.rimuoviGiocatorePreferito(utenteLoggato.getId(), giocatore.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nella rimozione del giocatore dai preferiti", e);
            return false;
        }
    }

    /** Restituisce i giocatori preferiti dell'utente loggato (lista vuota se non loggato o in errore). */
    public List<Giocatore> getGiocatoriPreferiti() {
        if (utenteLoggato == null) return new ArrayList<>();
        try {
            return utenteService.getGiocatoriPreferiti(utenteLoggato.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei giocatori preferiti", e);
            return new ArrayList<>();
        }
    }

    /** Aggiunge una squadra alle preferite dell'utente loggato. */
    public boolean aggiungiSquadraPreferita(Squadra squadra) {
        if (richiedeLogin()) return false;
        try {
            return utenteService.aggiungiSquadraPreferita(utenteLoggato.getId(), squadra.getId());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Impossibile aggiungere la squadra ai preferiti", e);
            return false;
        }
    }

    /** Rimuove una squadra dalle preferite dell'utente loggato. */
    public boolean rimuoviSquadraPreferita(Squadra squadra) {
        if (richiedeLogin()) return false;
        try {
            return utenteService.rimuoviSquadraPreferita(utenteLoggato.getId(), squadra.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nella rimozione della squadra dai preferiti", e);
            return false;
        }
    }

    /** Restituisce le squadre preferite dell'utente loggato (lista vuota se non loggato o in errore). */
    public List<Squadra> getSquadrePreferite() {
        if (utenteLoggato == null) return new ArrayList<>();
        try {
            return utenteService.getSquadrePreferite(utenteLoggato.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento delle squadre preferite", e);
            return new ArrayList<>();
        }
    }

    // =========================================================================
    // PREFERITI SALVATI (visualizzazione e rimozione dalla scheda dedicata)
    // =========================================================================

    /** Ricarica in tabella i giocatori e le squadre preferite dell'utente loggato. */
    private void aggiornaPreferiti() {
        if (utenteLoggato == null) {
            JOptionPane.showMessageDialog(view,
                    "Devi effettuare il login per vedere i tuoi preferiti.",
                    "Preferiti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Giocatori preferiti — colonne: {ID, Nome, Cognome, Ruolo, N° Maglia, Squadra}
        giocatoriPreferitiVisualizzati = getGiocatoriPreferiti();
        DefaultTableModel modelG = view.getModelGiocatoriPreferiti();
        modelG.setRowCount(0);
        for (Giocatore g : giocatoriPreferitiVisualizzati) {
            modelG.addRow(new Object[]{
                    g.getId(),
                    g.getNome(),
                    g.getCognome(),
                    g.getRuolo(),
                    g.getNMaglia(),
                    g.getSquadra() != null ? g.getSquadra().getNome() : ""
            });
        }

        // Squadre preferite — colonne: {ID Squadra, Nome, Sede, Allenatore, URL Logo}
        squadrePreferiteVisualizzate = getSquadrePreferite();
        DefaultTableModel modelS = view.getModelSquadrePreferite();
        modelS.setRowCount(0);
        for (Squadra s : squadrePreferiteVisualizzate) {
            modelS.addRow(new Object[]{
                    s.getId(),
                    s.getNome(),
                    s.getSede(),
                    s.getAllenatore(),
                    s.getLogoURL()
            });
        }
    }

    /** Rimuove dai preferiti il giocatore selezionato nella tabella. */
    private void rimuoviGiocatorePreferitoSelezionato() {
        if (richiedeLogin()) return;

        int row = view.getTabellaGiocatoriPreferiti().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Seleziona un giocatore da rimuovere.",
                    "Preferiti", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = view.getTabellaGiocatoriPreferiti().convertRowIndexToModel(row);
        if (modelRow >= giocatoriPreferitiVisualizzati.size()) return;

        Giocatore giocatore = giocatoriPreferitiVisualizzati.get(modelRow);
        if (rimuoviGiocatorePreferito(giocatore)) {
            aggiornaPreferiti();
            JOptionPane.showMessageDialog(view, "Giocatore rimosso dai preferiti.");
        }
    }

    /** Rimuove dai preferiti la squadra selezionata nella tabella. */
    private void rimuoviSquadraPreferitaSelezionata() {
        if (richiedeLogin()) return;

        int row = view.getTabellaSquadrePreferite().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Seleziona una squadra da rimuovere.",
                    "Preferiti", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = view.getTabellaSquadrePreferite().convertRowIndexToModel(row);
        if (modelRow >= squadrePreferiteVisualizzate.size()) return;

        Squadra squadra = squadrePreferiteVisualizzate.get(modelRow);
        if (rimuoviSquadraPreferita(squadra)) {
            aggiornaPreferiti();
            JOptionPane.showMessageDialog(view, "Squadra rimossa dai preferiti.");
        }
    }

    // =========================================================================
    // GDPR — CANCELLAZIONE ACCOUNT (diritto all'oblio)
    // =========================================================================

    /**
     * Elimina definitivamente l'account dell'utente loggato dopo conferma
     * esplicita, delegando a UtenteService.cancellaAccount.
     * Restituisce true se l'account è stato eliminato: in tal caso il
     * chiamante deve chiudere la sessione e tornare alla schermata di login.
     */
    public boolean cancellaAccount() {
        if (richiedeLogin()) return false;

        int conferma = JOptionPane.showConfirmDialog(view,
                "Vuoi eliminare definitivamente il tuo account e i dati associati?\n"
                        + "L'operazione non è reversibile.",
                "Cancellazione account (GDPR)",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conferma != JOptionPane.YES_OPTION) return false;

        try {
            boolean eliminato = utenteService.cancellaAccount(utenteLoggato.getId());
            if (eliminato) {
                logger.info("Account eliminato (GDPR) per l'utente id " + utenteLoggato.getId());
                JOptionPane.showMessageDialog(view, "Account eliminato correttamente.");
            }
            return eliminato;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la cancellazione dell'account", e);
            JOptionPane.showMessageDialog(view, "Errore durante la cancellazione dell'account.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // =========================================================================
    // SUPPORTO
    // =========================================================================

    /**
     * Vero (e mostra un avviso) se non c'è un utente loggato:
     * preferenze e cancellazione account richiedono il login,
     * la ricerca resta invece libera.
     */
    private boolean richiedeLogin() {
        if (utenteLoggato == null) {
            JOptionPane.showMessageDialog(view,
                    "Effettua l'accesso per usare questa funzione.",
                    "Accesso richiesto", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
}