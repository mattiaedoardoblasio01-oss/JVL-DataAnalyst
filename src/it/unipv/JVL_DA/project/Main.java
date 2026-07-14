package it.unipv.JVL_DA.project;

import it.unipv.JVL_DA.project.controller.AuthController;
import it.unipv.JVL_DA.project.DAO.implementazioni.AmministratoreDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.UtenteDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.LogOperazioniDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Utente;
import it.unipv.JVL_DA.project.view.utente.UtenteLoginFrame;
import it.unipv.JVL_DA.project.view.admin.AdminLoginFrame;
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

            // 2. INIZIALIZZAZIONE DEL CONTROLLER
            AuthController authController = new AuthController(adminDAO, utenteDAO, logDAO);

            // 3. FINESTRA DI SCELTA INIZIALE PER IL TEST
            String[] opzioni = {"Area Amministratore", "Area Tifoso (Utente)"};
            int scelta = JOptionPane.showOptionDialog(null,
                    "Scegli quale area testare:",
                    "LBA - Launcher di Test",
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
                    // La view ora espone getEmail(): il login admin avviene per email,
                    // coerentemente con AuthController.loginAdmin() -> findByEmail()
                    String email = adminFrame.getEmail();
                    char[] password = adminFrame.getPassword();

                    Amministratore admin = authController.loginAdmin(email, password);

                    if (admin != null) {
                        JOptionPane.showMessageDialog(adminFrame, "Login Admin riuscito! Benvenuto " + admin.getEmail());
                        adminFrame.dispose();
                        // new AdminDashboardFrame(admin).setVisible(true);
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
                        JOptionPane.showMessageDialog(utenteFrame, "Login Utente riuscito! Benvenuto " + utente.getEmail());
                        utenteFrame.dispose();
                        // new UtentiFrame(utente).setVisible(true);
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

                    // Qualunque chiusura della registrazione (X, tasto "Indietro" o
                    // registrazione completata) riporta alla finestra di login.
                    // Il frame usa DISPOSE_ON_CLOSE, quindi windowClosed scatta sempre.
                    regFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent we) {
                            utenteFrame.setVisible(true);
                        }
                    });

                    // 5C. Tasto "Indietro": basta chiudere il frame, il WindowListener fa il resto
                    regFrame.addBackListener(backEvent -> regFrame.dispose());

                    // 5D. Logica di Conferma Registrazione
                    regFrame.addRegisterListener(regEvent -> {
                        String nome = regFrame.getNome();
                        String cognome = regFrame.getCognome();
                        String email = regFrame.getEmail();
                        String username = regFrame.getUsername();
                        char[] pwd = regFrame.getPassword();
                        char[] confirmPwd = regFrame.getConfirmPassword();

                        // Nome, cognome e data di nascita sono NOT NULL nella tabella `utenti`:
                        // vanno validati qui, prima di chiamare il controller
                        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()
                                || username.isEmpty() || pwd.length == 0) {
                            regFrame.showMessage("Compila tutti i campi.", true);
                            return;
                        }

                        if (!Arrays.equals(pwd, confirmPwd)) {
                            regFrame.showMessage("Le password non coincidono!", true);
                            return;
                        }

                        LocalDate dataNascita;
                        try {
                            dataNascita = LocalDate.parse(regFrame.getDataNascita(), FMT_DATA);
                        } catch (DateTimeParseException ex) {
                            regFrame.showMessage("Data di nascita non valida (usa gg/mm/aaaa).", true);
                            return;
                        }
                        if (dataNascita.isAfter(LocalDate.now())) {
                            regFrame.showMessage("La data di nascita non può essere futura.", true);
                            return;
                        }

                        // Costruttore per INSERT: passwordHash lo imposta AuthController
                        // dopo l'hashing; indirizzo, cap e provincia sono nullable nel DB
                        // e l'utente potrà completarli in seguito dal proprio profilo
                        Utente nuovoUtente = new Utente(nome, cognome, username, email,
                                null, null, null, null, dataNascita);

                        boolean successo = authController.registraUtente(nuovoUtente, pwd);

                        if (successo) {
                            JOptionPane.showMessageDialog(regFrame,
                                    "Registrazione completata! Ora puoi accedere.",
                                    "Benvenuto", JOptionPane.INFORMATION_MESSAGE);
                            regFrame.dispose(); // il WindowListener rimostra il login
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
}