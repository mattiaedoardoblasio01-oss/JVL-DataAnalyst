package it.unipv.JVL_DA.project;

import it.unipv.JVL_DA.project.controller.AuthController;
import it.unipv.JVL_DA.project.controller.CalendarioController;
import it.unipv.JVL_DA.project.controller.GestioneLegaController;
import it.unipv.JVL_DA.project.controller.PublicController;
import it.unipv.JVL_DA.project.controller.StatisticheController;
import it.unipv.JVL_DA.project.DAO.implementazioni.AmministratoreDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.GiocatoreDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.LogOperazioniDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.StatisticheDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.UtenteDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Utente;
import it.unipv.JVL_DA.project.view.admin.AdminDashboard;
import it.unipv.JVL_DA.project.view.admin.AdminLoginFrame;
import it.unipv.JVL_DA.project.view.ricerca.RicercaFrame;
import it.unipv.JVL_DA.project.view.statistiche.StatisticheFrame;
import it.unipv.JVL_DA.project.view.utente.UtenteLoginFrame;
import it.unipv.JVL_DA.project.view.utente.UtenteRegistrazioneFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class Main {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // 1. INIZIALIZZAZIONE DEL MODELLO (DAO)
            AmministratoreDAO adminDAO = new AmministratoreDAO();
            UtenteDAO utenteDAO = new UtenteDAO();
            LogOperazioniDAO logDAO = new LogOperazioniDAO();
            SquadraDAO squadraDAO = new SquadraDAO();
            GiocatoreDAO giocatoreDAO = new GiocatoreDAO();
            CampionatoDAO campionatoDAO = new CampionatoDAO();
            PartitaDAO partitaDAO = new PartitaDAO();
            StatisticheDAO statisticheDAO = new StatisticheDAO();

            // 2. INIZIALIZZAZIONE DEL CONTROLLER DI AUTENTICAZIONE
            AuthController authController = new AuthController(adminDAO, utenteDAO, logDAO);

            // 3. FINESTRA DI SCELTA INIZIALE
            String[] opzioni = {"Area Amministratore", "Area Tifoso (Utente)"};
            int scelta = JOptionPane.showOptionDialog(null,
                    "Scegli quale area utilizzare:",
                    "LBA - Lega Basket",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, opzioni, opzioni[0]);

            // Se l'utente chiude la finestrella senza scegliere, fermiamo il programma
            if (scelta == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            // 4. FLUSSO AMMINISTRATORE
            if (scelta == 0) {
                AdminLoginFrame adminFrame = new AdminLoginFrame();

                adminFrame.addLoginListener(e -> {
                    // Il login admin avviene per email, coerentemente con
                    // AuthController.loginAdmin() -> findByEmail()
                    String email = adminFrame.getEmail();
                    char[] password = adminFrame.getPassword();

                    Amministratore admin = authController.loginAdmin(email, password);

                    if (admin != null) {
                        JOptionPane.showMessageDialog(adminFrame,
                                "Login Admin riuscito! Benvenuto " + admin.getEmail());
                        adminFrame.dispose();
                        apriDashboardAdmin(admin, squadraDAO, giocatoreDAO, campionatoDAO,
                                partitaDAO, statisticheDAO, logDAO);
                    } else {
                        adminFrame.showError("Credenziali amministratore non valide.");
                        adminFrame.resetForm();
                    }
                    Arrays.fill(password, '\0');
                });

                adminFrame.setVisible(true);
            }
            // 5. FLUSSO UTENTE (CON REGISTRAZIONE)
            else if (scelta == 1) {
                UtenteLoginFrame utenteFrame = new UtenteLoginFrame();

                // 5A. Logica di Login Utente
                utenteFrame.addLoginListener(e -> {
                    String email = utenteFrame.getEmail();
                    char[] password = utenteFrame.getPassword();

                    Utente utente = authController.loginUtente(email, password);

                    if (utente != null) {
                        JOptionPane.showMessageDialog(utenteFrame,
                                "Login Utente riuscito! Benvenuto " + utente.getEmail());
                        utenteFrame.dispose();

                        // Area pubblica (ricerca): PublicController aggancia i listener
                        // e carica i filtri nel proprio costruttore.
                        RicercaFrame ricercaFrame = new RicercaFrame();
                        new PublicController(ricercaFrame, utente);
                        ricercaFrame.setVisible(true);
                    } else {
                        utenteFrame.showError("Email o password errate.");
                        utenteFrame.resetForm();
                    }
                    Arrays.fill(password, '\0');
                });

                // 5B. Logica di Apertura Registrazione
                utenteFrame.addRegisterListener(e -> {
                    utenteFrame.setVisible(false);
                    UtenteRegistrazioneFrame regFrame = new UtenteRegistrazioneFrame();

                    // Qualunque chiusura della registrazione riporta al login.
                    regFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent we) {
                            utenteFrame.setVisible(true);
                        }
                    });

                    // 5C. Tasto "Indietro"
                    regFrame.addBackListener(backEvent -> regFrame.dispose());

                    // 5D. Logica di Registrazione
                    regFrame.addRegisterListener(regEvent -> {
                        String nome = regFrame.getNome();
                        String cognome = regFrame.getCognome();
                        String username = regFrame.getUsername();
                        String email = regFrame.getEmail();
                        char[] pwd = regFrame.getPassword();
                        char[] confirmPwd = regFrame.getConfirmPassword();

                        // Campi obbligatori (nome, cognome e data_nascita sono NOT NULL nel DB)
                        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty()
                                || email.isEmpty() || regFrame.getDataNascita().isEmpty()
                                || pwd.length == 0) {
                            regFrame.showMessage("Compila tutti i campi obbligatori.", true);
                            return;
                        }

                        // Conferma password
                        if (!Arrays.equals(pwd, confirmPwd)) {
                            regFrame.showMessage("Le due password non coincidono.", true);
                            return;
                        }

                        // Parsing e validazione della data di nascita (gg/mm/aaaa)
                        LocalDate dataNascita;
                        try {
                            dataNascita = LocalDate.parse(regFrame.getDataNascita(), FMT_DATA);
                        } catch (DateTimeParseException ex) {
                            regFrame.showMessage("Data di nascita non valida. Usa il formato gg/mm/aaaa.", true);
                            return;
                        }
                        if (dataNascita.isAfter(LocalDate.now())) {
                            regFrame.showMessage("La data di nascita non può essere futura.", true);
                            return;
                        }

                        // passwordHash lo imposta AuthController dopo l'hashing;
                        // indirizzo, cap e provincia sono nullable e completabili dopo.
                        Utente nuovoUtente = new Utente(nome, cognome, username, email,
                                null, null, null, null, dataNascita);

                        boolean successo = authController.registraUtente(nuovoUtente, pwd);

                        if (successo) {
                            JOptionPane.showMessageDialog(regFrame,
                                    "Registrazione completata! Ora puoi accedere.",
                                    "Benvenuto", JOptionPane.INFORMATION_MESSAGE);
                            regFrame.dispose();
                        } else {
                            regFrame.showMessage("Registrazione non riuscita: email/username già in uso o errore di connessione.", true);
                        }

                        Arrays.fill(pwd, '\0');
                        Arrays.fill(confirmPwd, '\0');
                    });

                    regFrame.setVisible(true);
                });

                utenteFrame.setVisible(true);
            }
        });
    }

    /**
     * Apre la dashboard amministratore e cabla OGNI pulsante al controller
     * competente, usando solo i metodi pubblici gia' esposti dai controller:
     *   - Squadre / Giocatori / Campionato    -> GestioneLegaController
     *   - Statistiche e Log                   -> StatisticheController
     *   - Calendario, Regular Season, Playoff -> CalendarioController
     */
    private static void apriDashboardAdmin(Amministratore admin,
                                           SquadraDAO squadraDAO, GiocatoreDAO giocatoreDAO,
                                           CampionatoDAO campionatoDAO, PartitaDAO partitaDAO,
                                           StatisticheDAO statisticheDAO, LogOperazioniDAO logDAO) {

        AdminDashboard dashboard = new AdminDashboard();

        // --- Aree CRUD di lega (squadre, giocatori, campionato) ---
        GestioneLegaController gestioneController = new GestioneLegaController(
                squadraDAO, giocatoreDAO, campionatoDAO, logDAO, admin);
        gestioneController.initDashboard(dashboard); // aggancia Squadre e Giocatori
        dashboard.addGestisciCampionatoListener(e -> gestioneController.apriGestioneCampionato());

        // --- Statistiche e Log ---
        // StatisticheController aggancia i listener e carica i dati nel costruttore,
        // quindi basta crearlo con la sua View e renderla visibile.
        dashboard.addGestisciStatisticheListener(e -> {
            StatisticheFrame statFrame = new StatisticheFrame();
            new StatisticheController(statFrame, giocatoreDAO, statisticheDAO, logDAO, admin);
            statFrame.setVisible(true);
        });

        // --- Calendario e transizioni di stagione ---
        CalendarioController calendarioController = new CalendarioController(
                campionatoDAO, squadraDAO, partitaDAO, logDAO, admin);
        dashboard.addGestisciCalendarioListener(e -> calendarioController.apriCalendario());
        dashboard.addGeneraRegularSeasonListener(e -> calendarioController.avviaRegularSeason());
        dashboard.addGeneraPlayoffListener(e -> {
            // avviaPlayoff() richiede la data di inizio: la chiediamo all'admin.
            String input = JOptionPane.showInputDialog(dashboard,
                    "Data di inizio Playoff (gg/mm/aaaa):",
                    LocalDate.now().plusWeeks(1).format(FMT_DATA));
            if (input == null || input.trim().isEmpty()) return;
            try {
                LocalDate data = LocalDate.parse(input.trim(), FMT_DATA);
                calendarioController.avviaPlayoff(data.atTime(20, 30));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dashboard,
                        "Data non valida. Usa il formato gg/mm/aaaa.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        dashboard.setVisible(true);
    }
}