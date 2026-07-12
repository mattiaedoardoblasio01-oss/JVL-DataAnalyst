package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ICampionatoDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IPartitaDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ISquadraDAO;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.view.campionato.CampionatoFrame;
import it.unipv.JVL_DA.project.view.playoff.PlayoffFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CampionatoController {

    // Stessi formattatori della View: il Controller riceve stringhe "dd/MM/yyyy"
    // dai getter e le converte in LocalDate/LocalDateTime prima di passarle al DAO
    private static final DateTimeFormatter FMT_DATA    = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATAORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Le fasi playoff corrispondono esattamente ai valori del JComboBox in PlayoffFrame
    private static final List<String> FASI_PLAYOFF = List.of(
            "Quarti di Finale", "Semifinale", "Finale"
    );

    // Il Controller dipende dalle interfacce, non dalle implementazioni concrete
    private final ICampionatoDAO campionatoDAO;
    private final IPartitaDAO    partitaDAO;
    private final ISquadraDAO    squadraDAO;

    private final CampionatoFrame campionatoFrame;
    private final PlayoffFrame    playoffFrame;

    // --- COSTRUTTORE ---
    public CampionatoController(CampionatoFrame campionatoFrame, PlayoffFrame playoffFrame) {
        this.campionatoDAO  = new CampionatoDAO();
        this.partitaDAO     = new PartitaDAO();
        this.squadraDAO     = new SquadraDAO();
        this.campionatoFrame = campionatoFrame;
        this.playoffFrame    = playoffFrame;

        inizializzaView();
        agganciaListener();
    }

    // =========================================================================
    // INIZIALIZZAZIONE
    // =========================================================================

    /**
     * Carica i dati iniziali in entrambe le view.
     * Le partite vengono separate in calendario (fasi non-playoff)
     * e playoff (Quarti di Finale, Semifinale, Finale) filtrando la lista completa,
     * evitando query multiple verso il DB.
     */
    private void inizializzaView() {
        try {
            List<Campionato> campionati = campionatoDAO.findAll();
            List<Squadra>    squadre    = squadraDAO.findAll();
            List<Partita>    tutte      = partitaDAO.findAll();

            // CampionatoFrame: tabella campionati, tabella calendario, combobox partite
            campionatoFrame.populateCampionatoTable(campionati);
            campionatoFrame.populatePartiteTable(filtraCalendario(tutte));
            campionatoFrame.populateCampionatoComboBox(campionati);
            campionatoFrame.populateSquadreComboBox(squadre);

            // PlayoffFrame: tabella playoff, combobox
            playoffFrame.populateTable(filtraPlayoff(tutte));
            playoffFrame.populateCampionatoComboBox(campionati);
            playoffFrame.populateSquadreComboBox(squadre);

        } catch (SQLException ex) {
            campionatoFrame.showErrorCampionato("Errore durante il caricamento: " + ex.getMessage());
        }
    }

    /**
     * Aggancia la logica del Controller ai bottoni esposti dalle due View.
     * "Nuovo" è già gestito internamente dalla View (clearForm),
     * quindi qui si aggiungono solo Salva ed Elimina.
     */
    private void agganciaListener() {

        // --- Tab Campionato ---
        campionatoFrame.addSalvaCampionatoListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { salvaCampionato(); }
        });
        campionatoFrame.addEliminaCampionatoListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminaCampionato(); }
        });

        // --- Tab Calendario ---
        campionatoFrame.addSalvaPartitaListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { salvaPartitaCalendario(); }
        });
        campionatoFrame.addEliminaPartitaListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminaPartitaCalendario(); }
        });

        // --- PlayoffFrame ---
        playoffFrame.addSalvaListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { salvaPartitaPlayoff(); }
        });
        playoffFrame.addEliminaListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminaPartitaPlayoff(); }
        });
    }

    // =========================================================================
    // CAMPIONATO — CRUD
    // =========================================================================

    private void salvaCampionato() {
        String nome        = campionatoFrame.getNomeCampionato();
        int    anno        = campionatoFrame.getAnnoCampionato();
        String inizioStr   = campionatoFrame.getDataInizioCampionato();
        String fineStr     = campionatoFrame.getDataFineCampionato();
        String stato       = campionatoFrame.getStatoCampionato();

        if (nome.isEmpty() || inizioStr.isEmpty() || fineStr.isEmpty()) {
            campionatoFrame.showErrorCampionato("Nome, data inizio e data fine sono obbligatori.");
            return;
        }

        LocalDate dataInizio, dataFine;
        try {
            dataInizio = LocalDate.parse(inizioStr, FMT_DATA);
            dataFine   = LocalDate.parse(fineStr,   FMT_DATA);
        } catch (DateTimeParseException ex) {
            campionatoFrame.showErrorCampionato("Formato data non valido. Usa gg/mm/aaaa.");
            return;
        }

        if (dataFine.isBefore(dataInizio)) {
            campionatoFrame.showErrorCampionato("La data fine non può precedere la data inizio.");
            return;
        }

        try {
            int id = campionatoFrame.getSelectedCampionatoId();

            if (id == -1) {
                boolean ok = campionatoDAO.insert(
                        new Campionato(nome, anno, dataInizio, dataFine, stato)
                );
                if (ok) campionatoFrame.showSuccessCampionato("Campionato inserito correttamente.");
                else  { campionatoFrame.showErrorCampionato("Inserimento non riuscito. Riprova."); return; }
            } else {
                boolean ok = campionatoDAO.update(
                        new Campionato(id, nome, anno, dataInizio, dataFine, stato)
                );
                if (ok) campionatoFrame.showSuccessCampionato("Campionato aggiornato correttamente.");
                else  { campionatoFrame.showErrorCampionato("Aggiornamento non riuscito. Riprova."); return; }
            }

            aggiornaCampionatoTable();
            campionatoFrame.clearFormCampionato();

        } catch (SQLException ex) {
            campionatoFrame.showErrorCampionato("Errore database: " + ex.getMessage());
        }
    }

    private void eliminaCampionato() {
        int id = campionatoFrame.getSelectedCampionatoId();
        if (id == -1) {
            campionatoFrame.showErrorCampionato("Seleziona un campionato da eliminare.");
            return;
        }

        try {
            boolean ok = campionatoDAO.delete(id);
            if (ok) {
                campionatoFrame.showSuccessCampionato("Campionato eliminato correttamente.");
                aggiornaCampionatoTable();
                campionatoFrame.clearFormCampionato();
            } else {
                campionatoFrame.showErrorCampionato("Eliminazione non riuscita. Riprova.");
            }
        } catch (SQLException ex) {
            campionatoFrame.showErrorCampionato("Errore database: " + ex.getMessage());
        }
    }

    // =========================================================================
    // CALENDARIO — CRUD
    // =========================================================================

    private void salvaPartitaCalendario() {
        Campionato campionato = campionatoFrame.getCampionatoSelezionato();
        String     fase       = campionatoFrame.getFase();
        int        giornata   = campionatoFrame.getGiornata();
        Squadra    casa       = campionatoFrame.getCasaSelezionata();
        Squadra    ospite     = campionatoFrame.getOspiteSelezionata();
        String     dataOraStr = campionatoFrame.getDataOra();
        String     luogo      = campionatoFrame.getLuogo();
        int        scoreCasa  = campionatoFrame.getScoreCasa();
        int        scoreOsp   = campionatoFrame.getScoreOsp();
        String     stato      = campionatoFrame.getStatoPartita();

        String errore = validaPartita(campionato, casa, ospite, dataOraStr, stato);
        if (errore != null) { campionatoFrame.showErrorPartita(errore); return; }

        LocalDateTime dataOra;
        try {
            dataOra = LocalDateTime.parse(dataOraStr, FMT_DATAORA);
        } catch (DateTimeParseException ex) {
            campionatoFrame.showErrorPartita("Formato data/ora non valido. Usa gg/mm/aaaa hh:mm.");
            return;
        }

        try {
            int id = campionatoFrame.getSelectedPartitaId();

            if (id == -1) {
                boolean ok = partitaDAO.insert(
                        new Partita(campionato, fase, giornata, casa, ospite, dataOra, luogo, scoreCasa, scoreOsp, stato)
                );
                if (ok) campionatoFrame.showSuccessPartita("Partita inserita correttamente.");
                else  { campionatoFrame.showErrorPartita("Inserimento non riuscito. Riprova."); return; }
            } else {
                boolean ok = partitaDAO.update(
                        new Partita(id, campionato, fase, giornata, casa, ospite, dataOra, luogo, scoreCasa, scoreOsp, stato)
                );
                if (ok) campionatoFrame.showSuccessPartita("Partita aggiornata correttamente.");
                else  { campionatoFrame.showErrorPartita("Aggiornamento non riuscito. Riprova."); return; }
            }

            aggiornaCalendarioTable();
            campionatoFrame.clearFormPartita();

        } catch (SQLException ex) {
            campionatoFrame.showErrorPartita("Errore database: " + ex.getMessage());
        }
    }

    private void eliminaPartitaCalendario() {
        int id = campionatoFrame.getSelectedPartitaId();
        if (id == -1) {
            campionatoFrame.showErrorPartita("Seleziona una partita da eliminare.");
            return;
        }

        try {
            boolean ok = partitaDAO.delete(id);
            if (ok) {
                campionatoFrame.showSuccessPartita("Partita eliminata correttamente.");
                aggiornaCalendarioTable();
                campionatoFrame.clearFormPartita();
            } else {
                campionatoFrame.showErrorPartita("Eliminazione non riuscita. Riprova.");
            }
        } catch (SQLException ex) {
            campionatoFrame.showErrorPartita("Errore database: " + ex.getMessage());
        }
    }

    // =========================================================================
    // PLAYOFF — CRUD
    // =========================================================================

    private void salvaPartitaPlayoff() {
        Campionato campionato = playoffFrame.getCampionatoSelezionato();
        String     fase       = playoffFrame.getFase();
        int        giornata   = playoffFrame.getGiornata();
        Squadra    casa       = playoffFrame.getCasaSelezionata();
        Squadra    ospite     = playoffFrame.getOspiteSelezionata();
        String     dataOraStr = playoffFrame.getDataOra();
        String     luogo      = playoffFrame.getLuogo();
        int        scoreCasa  = playoffFrame.getScoreCasa();
        int        scoreOsp   = playoffFrame.getScoreOsp();
        String     stato      = playoffFrame.getStatoPartita();

        String errore = validaPartita(campionato, casa, ospite, dataOraStr, stato);
        if (errore != null) { playoffFrame.showError(errore); return; }

        LocalDateTime dataOra;
        try {
            dataOra = LocalDateTime.parse(dataOraStr, FMT_DATAORA);
        } catch (DateTimeParseException ex) {
            playoffFrame.showError("Formato data/ora non valido. Usa gg/mm/aaaa hh:mm.");
            return;
        }

        try {
            int id = playoffFrame.getSelectedId();

            if (id == -1) {
                boolean ok = partitaDAO.insert(
                        new Partita(campionato, fase, giornata, casa, ospite, dataOra, luogo, scoreCasa, scoreOsp, stato)
                );
                if (ok) playoffFrame.showSuccess("Partita playoff inserita correttamente.");
                else  { playoffFrame.showError("Inserimento non riuscito. Riprova."); return; }
            } else {
                boolean ok = partitaDAO.update(
                        new Partita(id, campionato, fase, giornata, casa, ospite, dataOra, luogo, scoreCasa, scoreOsp, stato)
                );
                if (ok) playoffFrame.showSuccess("Partita playoff aggiornata correttamente.");
                else  { playoffFrame.showError("Aggiornamento non riuscito. Riprova."); return; }
            }

            aggiornaPlayoffTable();
            playoffFrame.clearForm();

        } catch (SQLException ex) {
            playoffFrame.showError("Errore database: " + ex.getMessage());
        }
    }

    private void eliminaPartitaPlayoff() {
        int id = playoffFrame.getSelectedId();
        if (id == -1) {
            playoffFrame.showError("Seleziona una partita playoff da eliminare.");
            return;
        }

        try {
            boolean ok = partitaDAO.delete(id);
            if (ok) {
                playoffFrame.showSuccess("Partita playoff eliminata correttamente.");
                aggiornaPlayoffTable();
                playoffFrame.clearForm();
            } else {
                playoffFrame.showError("Eliminazione non riuscita. Riprova.");
            }
        } catch (SQLException ex) {
            playoffFrame.showError("Errore database: " + ex.getMessage());
        }
    }

    // =========================================================================
    // METODI DI SUPPORTO
    // =========================================================================

    /**
     * Ricarica la tabella campionati e aggiorna i combobox campionato
     * in entrambe le view, che dipendono dagli stessi dati.
     */
    private void aggiornaCampionatoTable() {
        try {
            List<Campionato> campionati = campionatoDAO.findAll();
            campionatoFrame.populateCampionatoTable(campionati);
            campionatoFrame.populateCampionatoComboBox(campionati);
            playoffFrame.populateCampionatoComboBox(campionati);
        } catch (SQLException ex) {
            campionatoFrame.showErrorCampionato("Errore nel refresh campionati: " + ex.getMessage());
        }
    }

    /** Ricarica la tabella calendario filtrando le partite non-playoff. */
    private void aggiornaCalendarioTable() {
        try {
            campionatoFrame.populatePartiteTable(filtraCalendario(partitaDAO.findAll()));
        } catch (SQLException ex) {
            campionatoFrame.showErrorPartita("Errore nel refresh calendario: " + ex.getMessage());
        }
    }

    /** Ricarica la tabella playoff filtrando solo le fasi playoff. */
    private void aggiornaPlayoffTable() {
        try {
            playoffFrame.populateTable(filtraPlayoff(partitaDAO.findAll()));
        } catch (SQLException ex) {
            playoffFrame.showError("Errore nel refresh playoff: " + ex.getMessage());
        }
    }

    /**
     * Filtra le partite che appartengono al calendario della regular season,
     * escludendo le fasi playoff.
     */
    private List<Partita> filtraCalendario(List<Partita> partite) {
        List<Partita> risultato = new ArrayList<>();
        for (Partita p : partite) {
            if (!FASI_PLAYOFF.contains(p.getFase())) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    /**
     * Filtra le partite che appartengono al tabellone playoff,
     * selezionando solo le fasi: Quarti di Finale, Semifinale, Finale.
     */
    private List<Partita> filtraPlayoff(List<Partita> partite) {
        List<Partita> risultato = new ArrayList<>();
        for (Partita p : partite) {
            if (FASI_PLAYOFF.contains(p.getFase())) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    /**
     * Validazione comune per partite di calendario e playoff.
     * Ritorna il messaggio di errore se la validazione fallisce, null se tutto è ok.
     */
    private String validaPartita(Campionato campionato, Squadra casa,
                                 Squadra ospite, String dataOraStr, String stato) {
        if (campionato == null)
            return "Seleziona un campionato.";
        if (casa == null || ospite == null)
            return "Seleziona entrambe le squadre.";
        // Squadra.getId() è String: confronto con equals()
        if (casa.getId().equals(ospite.getId()))
            return "La squadra casa e la squadra ospite non possono coincidere.";
        if (dataOraStr.isEmpty())
            return "Inserisci la data e l'ora della partita.";
        if (stato.isEmpty())
            return "Inserisci lo stato della partita.";
        return null;
    }
}
