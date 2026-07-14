package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.interfacce.IAmministratoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IUtenteDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Utente;
import org.mindrot.jbcrypt.BCrypt;

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
     * Gestisce il login per gli Amministratori usando char[]
     */
    public Amministratore loginAdmin(String email, char[] password) {
        try {
            Amministratore admin = adminDAO.findByEmail(email);

            if (admin != null) {
                // Convertiamo temporaneamente in String solo per BCrypt
                String passTemp = new String(password);
                boolean isPasswordValid = BCrypt.checkpw(passTemp, admin.getPasswordHash());

                if (isPasswordValid) {
                    logDAO.insert(new LogOperazioni(admin, "LOGIN", "Login amministratore effettuato con successo"));
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
                if (BCrypt.checkpw(passTemp, utente.getPasswordHash())) {
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
     * Gestisce la registrazione di un nuovo Utente
     */
    public boolean registraUtente(Utente nuovoUtente, char[] password) {
        try {
            // 1. Controllo duplicati: email
            if (utenteDAO.findByEmail(nuovoUtente.getEmail()) != null) {
                logger.warning("Registrazione fallita: email già in uso (" + nuovoUtente.getEmail() + ")");
                return false;
            }

            // 2. Controllo duplicati: username (richiede un metodo findByUsername nell'interfaccia IUtenteDAO)
            if (utenteDAO.findByUsername(nuovoUtente.getUsername()) != null) {
                logger.warning("Registrazione fallita: username già in uso (" + nuovoUtente.getUsername() + ")");
                return false;
            }

            // 3. Hashing della password
            String passTemp = new String(password);
            String hashSicuro = BCrypt.hashpw(passTemp, BCrypt.gensalt());
            nuovoUtente.setPasswordHash(hashSicuro);

            // 4. Inserimento
            boolean risultato = utenteDAO.insert(nuovoUtente);

            if (risultato) {
                logger.info("Nuovo utente registrato con successo: " + nuovoUtente.getEmail());
                return true;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore DB durante la registrazione utente", e);
        }

        return false;
    }
}