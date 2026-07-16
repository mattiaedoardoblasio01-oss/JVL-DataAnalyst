package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.dao.interfacce.IAmministratoreDAO;
import it.unipv.JVL_DA.project.dao.interfacce.IUtenteDAO;
import it.unipv.JVL_DA.project.dao.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.model.Amministratore;
import it.unipv.JVL_DA.project.model.LogOperazioni;
import it.unipv.JVL_DA.project.model.Utente;
import it.unipv.JVL_DA.project.util.PasswordUtil;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final IAmministratoreDAO adminDAO;
    private final IUtenteDAO utenteDAO;
    private final ILogOperazioniDAO logDAO;

    public AuthController(IAmministratoreDAO adminDAO, IUtenteDAO utenteDAO, ILogOperazioniDAO logDAO) {
        this.adminDAO = adminDAO;
        this.utenteDAO = utenteDAO;
        this.logDAO = logDAO;
    }

    /**
     * Gestisce il login per gli Amministratori usando char[].
     * L'identificazione avviene tramite email.
     */
    public Amministratore loginAdmin(String email, char[] password) {
        try {
            Amministratore admin = adminDAO.findByEmail(email);

            if (admin != null) {
                // Conversione temporanea in String solo per BCrypt (jBCrypt accetta solo String)
                String passTemp = new String(password);

                if (PasswordUtil.verify(passTemp, admin.getPasswordHash())) {
                    registraLog(admin, "LOGIN", "Login amministratore effettuato con successo");
                    logger.info("Login admin riuscito per l'account: " + email);
                    return admin;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il login admin", e);
        }

        logger.warning("Tentativo di login admin fallito per: " + email);
        return null;
    }

    /**
     * Gestisce il login per gli Utenti base usando char[]
     */
    public Utente loginUtente(String email, char[] password) {
        try {
            Utente utente = utenteDAO.findByEmail(email);

            if (utente != null) {
                String passTemp = new String(password);
                if (PasswordUtil.verify(passTemp, utente.getPasswordHash())) {
                    logger.info("Login utente riuscito per: " + email);
                    return utente;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante il login utente", e);
        }

        logger.warning("Tentativo di login utente fallito per: " + email);
        return null;
    }

    /**
     * Gestisce la registrazione di un nuovo Utente.
     * L'hashing della password avviene QUI e solo qui: il DAO deve salvare
     * il valore di passwordHash così com'è, senza ulteriori trasformazioni
     * (altrimenti si otterrebbe un doppio hash e il login fallirebbe sempre).
     */
    public boolean registraUtente(Utente nuovoUtente, char[] password) {
        try {
            // 1. Controllo duplicati: email
            if (utenteDAO.findByEmail(nuovoUtente.getEmail()) != null) {
                logger.warning("Registrazione fallita: email già in uso (" + nuovoUtente.getEmail() + ")");
                return false;
            }

            // 2. Controllo duplicati: username
            if (utenteDAO.findByUsername(nuovoUtente.getUsername()) != null) {
                logger.warning("Registrazione fallita: username già in uso (" + nuovoUtente.getUsername() + ")");
                return false;
            }

            // 3. Hashing della password (PasswordUtil centralizza BCrypt con cost 12)
            String passTemp = new String(password);
            nuovoUtente.setPasswordHash(PasswordUtil.hash(passTemp));

            // 4. Inserimento
            if (utenteDAO.insert(nuovoUtente)) {
                logger.info("Nuovo utente registrato con successo: " + nuovoUtente.getEmail());
                return true;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la registrazione utente", e);
        }

        return false;
    }

    /**
     * Registra un'operazione nel log senza far fallire l'azione principale:
     * un errore di scrittura del log non deve impedire, ad esempio, un login valido.
     */
    private void registraLog(Amministratore admin, String azione, String dettagli) {
        try {
            logDAO.insert(new LogOperazioni(admin, azione, dettagli));
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Impossibile registrare il log dell'operazione: " + azione, e);
        }
    }
}