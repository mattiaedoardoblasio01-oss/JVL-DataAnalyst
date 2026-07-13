package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.implementazioni.AmministratoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IAmministratoreDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.view.AdminDashboard;
import it.unipv.JVL_DA.project.view.LoginFrame;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Controller dell'Iterazione 1 per il login amministratore.
 *
 * Fa da ponte tra {@link LoginFrame} (View) e {@link AmministratoreDAO} (persistenza):
 * legge le credenziali dalla view, le verifica tramite il DAO e, in caso di successo,
 * apre {@link AdminDashboard}. La View non conosce mai il DAO — tutta la logica sta qui,
 * come previsto dal pattern MVC.
 */
public class LoginController {

    private final LoginFrame view;
    private final IAmministratoreDAO amministratoreDAO;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    /** Costruttore di uso normale: crea internamente il DAO concreto. */
    public LoginController(LoginFrame view) {
        this(view, new AmministratoreDAO());
    }

    /**
     * Costruttore con iniezione del DAO: utile per i test unitari (Iterazione 5),
     * dove al posto del DAO reale si passa un mock di {@link IAmministratoreDAO}.
     */
    public LoginController(LoginFrame view, IAmministratoreDAO amministratoreDAO) {
        this.view = view;
        this.amministratoreDAO = amministratoreDAO;
        // Il controller si aggancia da solo al bottone "Accedi" della view.
        this.view.addLoginListener(e -> eseguiLogin());
    }

    /** Logica eseguita al click su "Accedi". */
    private void eseguiLogin() {
        String username = view.getUsername();

        // La view restituisce la password come char[]; la convertiamo per il DAO
        // e ripuliamo subito l'array in memoria per non tenerla più del necessario.
        char[] passwordChars = view.getPassword();
        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Inserisci username e password.");
            return;
        }

        try {
            Amministratore admin = amministratoreDAO.login(username, password);
            if (admin != null) {
                apriDashboard(admin);
            } else {
                view.showError("Credenziali non valide.");
                view.resetForm();
            }
        }
            catch (SQLException ex) {
                view.showError("Errore di connessione al database. Riprova.");
                logger.severe("Errore SQL durante il login: " + ex.getMessage());
            }
    }

    /** Chiude la schermata di login e apre la dashboard amministratore. */
    private void apriDashboard(Amministratore admin) {
        view.dispose();
        AdminDashboard dashboard = new AdminDashboard();
        dashboard.setVisible(true);
    }
}
