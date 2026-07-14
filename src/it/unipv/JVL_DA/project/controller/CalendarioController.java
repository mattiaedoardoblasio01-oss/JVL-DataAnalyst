package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.interfacce.ICampionatoDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IPartitaDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ISquadraDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.service.CampionatoService;
import it.unipv.JVL_DA.project.service.PlayoffService;
import it.unipv.JVL_DA.project.view.calendario.CalendarioFrame;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller del ciclo di stagione (solo Amministratore):
 * generazione della Regular Season (round robin via CalendarioService),
 * generazione dei Playoff (quarti/semifinali/finale via PlayoffService)
 * e consultazione/aggiornamento risultati tramite CalendarioFrame.
 *
 * Tutta la logica di dominio resta nei Service (CampionatoService,
 * CalendarioService, PlayoffService): il Controller prepara i dati,
 * intercetta le eccezioni di validazione e aggiorna la View.
 */
public class CalendarioController {

    private static final Logger logger = Logger.getLogger(CalendarioController.class.getName());
    private static final DateTimeFormatter FMT_DATAORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // giornataBase per le fasi playoff: quarti 1-5, semifinali 6-10, finale 11-17
    private static final int BASE_QUARTI = 0;
    private static final int BASE_SEMIFINALI = 5;
    private static final int BASE_FINALE = 10;

    // --- MODEL ---
    private final ICampionatoDAO campionatoDAO;
    private final ISquadraDAO squadraDAO;
    private final IPartitaDAO partitaDAO;
    private final ILogOperazioniDAO logDAO;
    private final CampionatoService campionatoService;
    private final PlayoffService playoffService;

    // --- SESSIONE ---
    private final Amministratore adminLoggato;

    public CalendarioController(ICampionatoDAO campionatoDAO, ISquadraDAO squadraDAO,
                                IPartitaDAO partitaDAO, ILogOperazioniDAO logDAO,
                                Amministratore adminLoggato) {
        this.campionatoDAO = campionatoDAO;
        this.squadraDAO = squadraDAO;
        this.partitaDAO = partitaDAO;
        this.logDAO = logDAO;
        this.adminLoggato = adminLoggato;
        this.campionatoService = new CampionatoService();
        this.playoffService = new PlayoffService();
    }

    // =========================================================================
    // GENERAZIONE REGULAR SEASON
    // =========================================================================

    /**
     * Variante di comodo: usa come prima giornata la data di inizio
     * del campionato in stato "Config" (ore 20:30).
     */
    public void avviaRegularSeason() {
        try {
            Campionato campionato = campionatoDAO.findByStato("Config");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Config' da avviare.");
            }
            avviaRegularSeason(campionato.getDataInizio().atTime(20, 30));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'avvio della Regular Season", e);
            mostraErrore("Errore DB durante l'avvio della Regular Season.");
        } catch (IllegalStateException e) {
            mostraErrore(e.getMessage());
        }
    }

    /**
     * Genera il calendario della Regular Season (round robin, 16 squadre,
     * 30 giornate) per il campionato in stato "Config" e lo porta ad "Attivo".
     * I vincoli (16 squadre, calendario non già esistente, stato corretto)
     * sono verificati da CampionatoService/CalendarioService.
     */
    public void avviaRegularSeason(LocalDateTime dataInizio) {
        try {
            Campionato campionato = campionatoDAO.findByStato("Config");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Config' da avviare.");
            }

            List<Squadra> squadre = squadraDAO.findAll();
            campionatoService.avviaRegularSeason(campionato, squadre, dataInizio);

            logDAO.insert(new LogOperazioni(adminLoggato, "GENERA_CALENDARIO_RS",
                    "Generata Regular Season per campionato: " + campionato.getNome()));
            JOptionPane.showMessageDialog(null,
                    "Regular Season generata (30 giornate). Il campionato è ora 'Attivo'.",
                    "Calendario", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException e) {
            mostraErrore(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la generazione della Regular Season", e);
            mostraErrore("Errore DB durante la generazione del calendario.");
        }
    }

    // =========================================================================
    // GENERAZIONE PLAYOFF
    // =========================================================================

    /**
     * Avvia i Playoff del campionato "Attivo": calcola la classifica della
     * Regular Season, seleziona le prime 8 e genera i quarti di finale
     * (serie al meglio delle 5) tramite CampionatoService, che verifica
     * che la Regular Season sia completata.
     */
    public void avviaPlayoff(LocalDateTime dataInizio) {
        try {
            Campionato campionato = campionatoDAO.findByStato("Attivo");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Attivo'.");
            }
            if (playoffService.esistePlayoff(campionato.getId())) {
                throw new IllegalStateException("I Playoff per questo campionato esistono già!");
            }

            List<Squadra> classifica = calcolaClassificaRS(campionato.getId());
            if (classifica.size() < 8) {
                throw new IllegalStateException("Classifica incompleta: impossibile determinare le prime 8.");
            }
            List<Squadra> primeOtto = new ArrayList<>(classifica.subList(0, 8));

            campionatoService.avviaPlayoff(campionato, primeOtto, dataInizio);

            logDAO.insert(new LogOperazioni(adminLoggato, "GENERA_PLAYOFF",
                    "Generati quarti di finale per campionato: " + campionato.getNome()));
            JOptionPane.showMessageDialog(null,
                    "Quarti di finale generati per le prime 8 della Regular Season.",
                    "Playoff", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException e) {
            mostraErrore(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la generazione dei Playoff", e);
            mostraErrore("Errore DB durante la generazione dei Playoff.");
        }
    }

    /** Genera le semifinali (giornate 6-10) per le 4 squadre vincenti dei quarti. */
    public void avviaSemifinali(List<Squadra> vincentiQuarti, LocalDateTime dataInizio) {
        try {
            Campionato campionato = campionatoDAO.findByStato("Attivo");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Attivo'.");
            }
            playoffService.generaSemifinali(campionato, vincentiQuarti, dataInizio, BASE_SEMIFINALI);

            logDAO.insert(new LogOperazioni(adminLoggato, "GENERA_SEMIFINALI",
                    "Generate semifinali per campionato: " + campionato.getNome()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            mostraErrore(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la generazione delle semifinali", e);
            mostraErrore("Errore DB durante la generazione delle semifinali.");
        }
    }

    /** Genera la finale (serie al meglio delle 7, giornate 11-17) per le 2 finaliste. */
    public void avviaFinale(List<Squadra> finaliste, LocalDateTime dataInizio) {
        try {
            Campionato campionato = campionatoDAO.findByStato("Attivo");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Attivo'.");
            }
            playoffService.generaFinale(campionato, finaliste, dataInizio, BASE_FINALE);

            logDAO.insert(new LogOperazioni(adminLoggato, "GENERA_FINALE",
                    "Generata finale per campionato: " + campionato.getNome()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            mostraErrore(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la generazione della finale", e);
            mostraErrore("Errore DB durante la generazione della finale.");
        }
    }

    /**
     * Chiude il campionato "Attivo" a fine stagione. CampionatoService
     * verifica che non restino partite in stato "Programmata".
     */
    public void chiudiCampionato() {
        try {
            Campionato campionato = campionatoDAO.findByStato("Attivo");
            if (campionato == null) {
                throw new IllegalStateException("Nessun campionato in stato 'Attivo' da chiudere.");
            }
            campionatoService.chiudiCampionato(campionato);

            logDAO.insert(new LogOperazioni(adminLoggato, "CHIUDI_CAMPIONATO",
                    "Chiuso campionato: " + campionato.getNome()));
            JOptionPane.showMessageDialog(null,
                    "Campionato '" + campionato.getNome() + "' chiuso.",
                    "Campionato", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            mostraErrore(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la chiusura del campionato", e);
            mostraErrore("Errore DB durante la chiusura del campionato.");
        }
    }

    /**
     * Calcola la classifica della Regular Season contando le vittorie nelle
     * partite in stato "Terminata"; a parità di vittorie ordina per
     * differenza canestri. Restituisce le squadre dalla prima all'ultima.
     */
    private List<Squadra> calcolaClassificaRS(int campId) throws SQLException {
        List<Partita> partite = partitaDAO.findByCampionatoAndFase(campId, "RS");

        Map<String, Squadra> squadre = new LinkedHashMap<>();
        Map<String, Integer> vittorie = new HashMap<>();
        Map<String, Integer> diffCanestri = new HashMap<>();

        for (Partita p : partite) {
            squadre.putIfAbsent(p.getCasa().getId(), p.getCasa());
            squadre.putIfAbsent(p.getOspite().getId(), p.getOspite());

            if (!"Terminata".equals(p.getStato())) continue;

            int diff = p.getScoreCasa() - p.getScoreOsp();
            diffCanestri.merge(p.getCasa().getId(), diff, Integer::sum);
            diffCanestri.merge(p.getOspite().getId(), -diff, Integer::sum);

            if (diff > 0) {
                vittorie.merge(p.getCasa().getId(), 1, Integer::sum);
            } else if (diff < 0) {
                vittorie.merge(p.getOspite().getId(), 1, Integer::sum);
            }
        }

        List<Squadra> classifica = new ArrayList<>(squadre.values());
        classifica.sort(
                Comparator.comparing((Squadra s) -> vittorie.getOrDefault(s.getId(), 0),
                                Comparator.reverseOrder())
                        .thenComparing(s -> diffCanestri.getOrDefault(s.getId(), 0),
                                Comparator.reverseOrder()));
        return classifica;
    }

    // =========================================================================
    // CONSULTAZIONE E RISULTATI (CalendarioFrame)
    // =========================================================================

    /**
     * Apre CalendarioFrame, popola il filtro campionati e aggancia i listener
     * ai componenti esposti dalla View tramite i getter.
     */
    public void apriCalendario() {
        CalendarioFrame frame = new CalendarioFrame();

        try {
            for (Campionato c : campionatoDAO.findAll()) {
                frame.getComboCampionato().addItem(c.getNome());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB nel caricamento dei campionati", e);
        }

        frame.getBtnFiltra().addActionListener(e -> caricaPartite(frame));
        frame.getBtnAggiornaRisultato().addActionListener(e -> aggiornaRisultato(frame));

        // Selezione riga in tabella → riempie l'area "Aggiorna Risultato"
        // Colonne: 0=ID, 1=Data e Ora, 2=Casa, 3=Ospite, 4=Punti Casa, 5=Punti Ospite, 6=Luogo, 7=Stato
        frame.getTabellaPartite().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && frame.getTabellaPartite().getSelectedRow() != -1) {
                int row = frame.getTabellaPartite().getSelectedRow();
                DefaultTableModel model = frame.getTableModel();
                frame.getTxtIdPartita().setText(model.getValueAt(row, 0).toString());
                frame.getSpinScoreCasa().setValue(model.getValueAt(row, 4));
                frame.getSpinScoreOspite().setValue(model.getValueAt(row, 5));
                frame.getComboStatoPartita().setSelectedItem(model.getValueAt(row, 7).toString());
            }
        });

        frame.setVisible(true);
    }

    /** Carica in tabella le partite RS della giornata selezionata nei filtri. */
    private void caricaPartite(CalendarioFrame frame) {
        String nomeCampionato = (String) frame.getComboCampionato().getSelectedItem();
        if (nomeCampionato == null) {
            JOptionPane.showMessageDialog(frame, "Seleziona un campionato.",
                    "Calendario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int giornata = (Integer) frame.getSpinGiornata().getValue();

        try {
            Campionato campionato = campionatoDAO.findByNome(nomeCampionato);
            if (campionato == null) return;

            DefaultTableModel model = frame.getTableModel();
            model.setRowCount(0);

            for (Partita p : partitaDAO.findByCampionatoAndFase(campionato.getId(), "RS")) {
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
            JOptionPane.showMessageDialog(frame, "Errore nel caricamento delle partite.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Aggiorna punteggio e stato della partita selezionata nell'area sud della View. */
    private void aggiornaRisultato(CalendarioFrame frame) {
        String idText = frame.getTxtIdPartita().getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Seleziona prima una partita dalla tabella.",
                    "Calendario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Partita partita = partitaDAO.findById(id);
            if (partita == null) {
                JOptionPane.showMessageDialog(frame, "Partita non trovata.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            partita.setScoreCasa((Integer) frame.getSpinScoreCasa().getValue());
            partita.setScoreOsp((Integer) frame.getSpinScoreOspite().getValue());
            partita.setStato((String) frame.getComboStatoPartita().getSelectedItem());

            if (partitaDAO.update(partita)) {
                logDAO.insert(new LogOperazioni(adminLoggato, "UPDATE_RISULTATO",
                        "Aggiornato risultato partita id " + id + ": "
                                + partita.getScoreCasa() + "-" + partita.getScoreOsp()
                                + " (" + partita.getStato() + ")"));
                caricaPartite(frame); // ricarica la giornata visualizzata
                JOptionPane.showMessageDialog(frame, "Risultato aggiornato.",
                        "Calendario", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "ID partita non valido.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante l'aggiornamento del risultato", e);
            JOptionPane.showMessageDialog(frame, "Errore DB durante l'aggiornamento.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Supporto per le finestre di dialogo di errore delle operazioni di stagione ---
    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(null, messaggio, "Operazione non consentita",
                JOptionPane.ERROR_MESSAGE);
    }
}