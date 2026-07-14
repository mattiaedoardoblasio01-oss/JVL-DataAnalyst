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
import java.util.Arrays;

public class Main {
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
                    String email = adminFrame.getUsername(); // Usiamo getUsername() come da tuo frame originale
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
                    regFrame.setVisible(true);

                    // 5C. Logica per tornare indietro
                    regFrame.addBackListener(backEvent -> {
                        regFrame.dispose();
                        utenteFrame.setVisible(true);
                    });

                    // 5D. Logica di Conferma Registrazione
                    regFrame.addRegisterListener(regEvent -> {
                        String email = regFrame.getEmail();
                        String username = regFrame.getUsername();
                        char[] pwd = regFrame.getPassword();
                        char[] confirmPwd = regFrame.getConfirmPassword();

                        if (email.isEmpty() || username.isEmpty() || pwd.length == 0) {
                            regFrame.showMessage("Compila tutti i campi.", true);
                            return;
                        }

                        if (!Arrays.equals(pwd, confirmPwd)) {
                            regFrame.showMessage("Le password non coincidono!", true);
                            return;
                        }

                        Utente nuovoUtente = new Utente();
                        nuovoUtente.setEmail(email);
                        nuovoUtente.setUsername(username);

                        boolean successo = authController.registraUtente(nuovoUtente, pwd);

                        if (successo) {
                            JOptionPane.showMessageDialog(regFrame,
                                    "Registrazione completata! Ora puoi accedere.",
                                    "Benvenuto", JOptionPane.INFORMATION_MESSAGE);
                            regFrame.dispose();
                            utenteFrame.setVisible(true);
                        } else {
                            regFrame.showMessage("Errore: Email o Username già in uso.", true);
                        }

                        Arrays.fill(pwd, '\0');
                        Arrays.fill(confirmPwd, '\0');
                    });
                });

                utenteFrame.setVisible(true);
            }
        });
    }
}