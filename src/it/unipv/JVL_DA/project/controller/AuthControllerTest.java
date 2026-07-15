package it.unipv.JVL_DA.project.controller;

import it.unipv.JVL_DA.project.DAO.interfacce.IAmministratoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IUtenteDAO;
import it.unipv.JVL_DA.project.POJO.Amministratore;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.POJO.Utente;
import it.unipv.JVL_DA.project.util.PasswordUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test dei casi d'uso di Autenticazione gestiti da {@link AuthController}:
 *  - Registrazione nuovo Utente (con controllo duplicati email/username);
 *  - Login Utente (email + password);
 *  - Login Amministratore (email + password, con tracciamento su log).
 *
 * Strategia (fedele al pattern MVC del progetto): il Controller viene testato
 * contro il Model tramite le interfacce DAO (IUtenteDAO, IAmministratoreDAO,
 * ILogOperazioniDAO), usando implementazioni "fake" in-memory. Non è coinvolta
 * alcuna View né alcun database reale. Viene invece usato il vero PasswordUtil,
 * così da verificare anche il round-trip reale dell'hash BCrypt (la password
 * salvata in registrazione deve essere verificabile al login, senza doppio hash).
 */
@DisplayName("AuthController — Casi d'uso di Autenticazione (Registrazione e Login)")
class AuthControllerTest {

    private FakeUtenteDAO utenteDAO;
    private FakeAmministratoreDAO adminDAO;
    private FakeLogOperazioniDAO logDAO;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        utenteDAO = new FakeUtenteDAO();
        adminDAO = new FakeAmministratoreDAO();
        logDAO = new FakeLogOperazioniDAO();
        authController = new AuthController(adminDAO, utenteDAO, logDAO);
    }

    // ---------------------------------------------------------------------
    // Helper di costruzione dati
    // ---------------------------------------------------------------------

    /** Utente pronto per l'INSERT: passwordHash null, lo imposta AuthController. */
    private Utente nuovoUtenteBase(String username, String email) {
        return new Utente("Mario", "Rossi", username, email,
                null, null, null, null, LocalDate.of(1990, 1, 1));
    }

    /** Utente già "persistito" con hash reale, per i test di login. */
    private Utente utenteConHash(String username, String email, String passwordInChiaro) {
        Utente u = nuovoUtenteBase(username, email);
        u.setPasswordHash(PasswordUtil.hash(passwordInChiaro));
        return u;
    }

    /** Amministratore già "persistito" con hash reale, per i test di login. */
    private Amministratore adminConHash(String adminUser, String email, String passwordInChiaro) {
        return new Amministratore(1, adminUser, email, PasswordUtil.hash(passwordInChiaro));
    }

    // =====================================================================
    // CASO D'USO: REGISTRAZIONE UTENTE
    // =====================================================================

    @Nested
    @DisplayName("Registrazione Utente")
    class Registrazione {

        @Test
        @DisplayName("Con dati validi: l'utente viene creato e la password è salvata come hash (mai in chiaro)")
        void registrazione_conDatiValidi_creaUtenteConPasswordHashata() {
            char[] password = "SecretPwd123".toCharArray();
            Utente nuovo = nuovoUtenteBase("mrossi", "mario.rossi@mail.it");

            boolean esito = authController.registraUtente(nuovo, password);

            assertTrue(esito, "La registrazione con dati validi deve riuscire");
            assertEquals(1, utenteDAO.count(), "L'utente deve risultare inserito una sola volta");

            Utente salvato = utenteDAO.getByEmail("mario.rossi@mail.it");
            assertNotNull(salvato, "L'utente inserito deve essere recuperabile per email");
            assertNotNull(salvato.getPasswordHash(), "L'hash della password deve essere valorizzato");
            assertNotEquals("SecretPwd123", salvato.getPasswordHash(),
                    "La password non deve mai essere salvata in chiaro");
            assertTrue(PasswordUtil.verify("SecretPwd123", salvato.getPasswordHash()),
                    "L'hash salvato deve corrispondere alla password originale");
        }

        @Test
        @DisplayName("Con email già in uso: la registrazione fallisce e non inserisce un nuovo utente")
        void registrazione_conEmailDuplicata_fallisce() {
            utenteDAO.seed(utenteConHash("utente_esistente", "duplicata@mail.it", "qualcosa"));
            Utente nuovo = nuovoUtenteBase("mrossi", "duplicata@mail.it"); // stessa email

            boolean esito = authController.registraUtente(nuovo, "pwd".toCharArray());

            assertFalse(esito, "Con email duplicata la registrazione deve fallire");
            assertEquals(1, utenteDAO.count(), "Non deve essere inserito un secondo utente");
        }

        @Test
        @DisplayName("Con username già in uso: la registrazione fallisce e non inserisce un nuovo utente")
        void registrazione_conUsernameDuplicato_fallisce() {
            utenteDAO.seed(utenteConHash("mrossi", "primo@mail.it", "qualcosa"));
            Utente nuovo = nuovoUtenteBase("mrossi", "secondo@mail.it"); // stesso username, email diversa

            boolean esito = authController.registraUtente(nuovo, "pwd".toCharArray());

            assertFalse(esito, "Con username duplicato la registrazione deve fallire");
            assertEquals(1, utenteDAO.count(), "Non deve essere inserito un secondo utente");
        }
    }

    // =====================================================================
    // CASO D'USO: LOGIN UTENTE
    // =====================================================================

    @Nested
    @DisplayName("Login Utente")
    class LoginUtente {

        @Test
        @DisplayName("Con credenziali corrette: restituisce l'utente autenticato")
        void loginUtente_conCredenzialiCorrette_restituisceUtente() {
            utenteDAO.seed(utenteConHash("mrossi", "mario.rossi@mail.it", "SecretPwd123"));

            Utente loggato = authController.loginUtente(
                    "mario.rossi@mail.it", "SecretPwd123".toCharArray());

            assertNotNull(loggato, "Con credenziali corrette il login deve restituire l'utente");
            assertEquals("mario.rossi@mail.it", loggato.getEmail());
        }

        @Test
        @DisplayName("Con password errata: restituisce null")
        void loginUtente_conPasswordErrata_restituisceNull() {
            utenteDAO.seed(utenteConHash("mrossi", "mario.rossi@mail.it", "SecretPwd123"));

            Utente loggato = authController.loginUtente(
                    "mario.rossi@mail.it", "PasswordSbagliata".toCharArray());

            assertNull(loggato, "Con password errata il login non deve autenticare");
        }

        @Test
        @DisplayName("Con email inesistente: restituisce null")
        void loginUtente_conEmailInesistente_restituisceNull() {
            Utente loggato = authController.loginUtente(
                    "nessuno@mail.it", "qualsiasi".toCharArray());

            assertNull(loggato, "Un'email non registrata non deve autenticare");
        }

        @Test
        @DisplayName("Integrazione registrazione → login: la stessa password consente l'accesso")
        void registrazionePoiLogin_conStessaPassword_autentica() {
            char[] password = "Passw0rd!".toCharArray();
            authController.registraUtente(nuovoUtenteBase("mrossi", "mario.rossi@mail.it"), password);

            Utente loggato = authController.loginUtente(
                    "mario.rossi@mail.it", "Passw0rd!".toCharArray());

            assertNotNull(loggato,
                    "Un utente appena registrato deve poter effettuare il login con la stessa password");
        }
    }

    // =====================================================================
    // CASO D'USO: LOGIN AMMINISTRATORE
    // =====================================================================

    @Nested
    @DisplayName("Login Amministratore")
    class LoginAdmin {

        @Test
        @DisplayName("Con credenziali corrette: restituisce l'admin e registra l'operazione sul log")
        void loginAdmin_conCredenzialiCorrette_restituisceAdminEScriveLog() {
            Amministratore atteso = adminConHash("admin", "admin@lba.it", "AdminPwd!");
            adminDAO.seed(atteso);

            Amministratore loggato = authController.loginAdmin(
                    "admin@lba.it", "AdminPwd!".toCharArray());

            assertNotNull(loggato, "Con credenziali corrette il login admin deve riuscire");
            assertSame(atteso, loggato, "Deve essere restituito l'amministratore trovato per email");

            assertEquals(1, logDAO.count(), "Un login admin riuscito deve scrivere una riga di log");
            LogOperazioni riga = logDAO.getInseriti().get(0);
            assertEquals("LOGIN", riga.getAzione(), "L'azione registrata deve essere 'LOGIN'");
        }

        @Test
        @DisplayName("Con password errata: restituisce null e non scrive alcun log")
        void loginAdmin_conPasswordErrata_restituisceNullSenzaLog() {
            adminDAO.seed(adminConHash("admin", "admin@lba.it", "AdminPwd!"));

            Amministratore loggato = authController.loginAdmin(
                    "admin@lba.it", "Sbagliata".toCharArray());

            assertNull(loggato, "Con password errata il login admin non deve autenticare");
            assertEquals(0, logDAO.count(), "Un tentativo fallito non deve produrre righe di log");
        }

        @Test
        @DisplayName("Con email inesistente: restituisce null e non scrive alcun log")
        void loginAdmin_conEmailInesistente_restituisceNullSenzaLog() {
            Amministratore loggato = authController.loginAdmin(
                    "sconosciuto@lba.it", "qualsiasi".toCharArray());

            assertNull(loggato, "Un'email admin non registrata non deve autenticare");
            assertEquals(0, logDAO.count(), "Un tentativo fallito non deve produrre righe di log");
        }
    }

    // =====================================================================
    // FAKE DAO IN-MEMORY (implementano le interfacce reali del Model)
    // =====================================================================

    /**
     * Fake di IUtenteDAO. Implementa solo i metodi usati da AuthController
     * (insert, findByEmail, findByUsername) più alcuni read-only innocui;
     * i metodi non pertinenti a questi casi d'uso lanciano
     * UnsupportedOperationException per segnalare usi inattesi.
     */
    private static class FakeUtenteDAO implements IUtenteDAO {
        private final List<Utente> store = new ArrayList<>();

        // --- helper di test (non lanciano SQLException) ---
        void seed(Utente u) { store.add(u); }
        int count() { return store.size(); }
        Utente getByEmail(String email) {
            for (Utente u : store) {
                if (email.equals(u.getEmail())) return u;
            }
            return null;
        }

        // --- metodi usati dal Controller ---
        @Override
        public boolean insert(Utente utente) {
            store.add(utente);
            return true;
        }

        @Override
        public Utente findByEmail(String email) {
            return getByEmail(email);
        }

        @Override
        public Utente findByUsername(String username) {
            for (Utente u : store) {
                if (username != null && username.equals(u.getUsername())) return u;
            }
            return null;
        }

        // --- read-only non usati dai casi d'uso, ma innocui ---
        @Override
        public Utente findById(int id) {
            for (Utente u : store) {
                if (u.getId() != null && u.getId() == id) return u;
            }
            return null;
        }

        @Override
        public List<Utente> findAll() {
            return new ArrayList<>(store);
        }

        // --- non pertinenti a questi casi d'uso ---
        @Override
        public boolean update(Utente utente) { throw new UnsupportedOperationException(); }
        @Override
        public boolean delete(int id) { throw new UnsupportedOperationException(); }
        @Override
        public Utente login(String username, String password) { throw new UnsupportedOperationException(); }
        @Override
        public boolean aggiungiGiocatorePreferito(int utenteId, String giocatoreId) { throw new UnsupportedOperationException(); }
        @Override
        public boolean rimuoviGiocatorePreferito(int utenteId, String giocatoreId) { throw new UnsupportedOperationException(); }
        @Override
        public List<Giocatore> getGiocatoriPreferiti(int utenteId) { throw new UnsupportedOperationException(); }
        @Override
        public boolean aggiungiSquadraPreferita(int utenteId, String squadraId) { throw new UnsupportedOperationException(); }
        @Override
        public boolean rimuoviSquadraPreferita(int utenteId, String squadraId) { throw new UnsupportedOperationException(); }
        @Override
        public List<Squadra> getSquadrePreferite(int utenteId) { throw new UnsupportedOperationException(); }
    }

    /**
     * Fake di IAmministratoreDAO. Implementa solo ciò che serve a loginAdmin
     * (findByEmail) più alcuni read-only; il resto lancia
     * UnsupportedOperationException.
     */
    private static class FakeAmministratoreDAO implements IAmministratoreDAO {
        private final List<Amministratore> store = new ArrayList<>();

        void seed(Amministratore a) { store.add(a); }

        @Override
        public Amministratore findByEmail(String email) {
            for (Amministratore a : store) {
                if (email.equals(a.getEmail())) return a;
            }
            return null;
        }

        @Override
        public boolean insert(Amministratore amministratore) {
            store.add(amministratore);
            return true;
        }

        @Override
        public Amministratore findById(int id) {
            for (Amministratore a : store) {
                if (a.getId() != null && a.getId() == id) return a;
            }
            return null;
        }

        @Override
        public Amministratore findByAdminUser(String adminUser) {
            for (Amministratore a : store) {
                if (adminUser != null && adminUser.equals(a.getAdminUser())) return a;
            }
            return null;
        }

        @Override
        public List<Amministratore> findAll() { return new ArrayList<>(store); }

        @Override
        public boolean update(Amministratore amministratore) { throw new UnsupportedOperationException(); }
        @Override
        public boolean delete(int id) { throw new UnsupportedOperationException(); }
        @Override
        public Amministratore login(String adminUser, String plainPassword) { throw new UnsupportedOperationException(); }
    }

    /**
     * Fake di ILogOperazioniDAO: registra in memoria le righe inserite,
     * così i test possono verificare che (e quando) il log viene scritto.
     */
    private static class FakeLogOperazioniDAO implements ILogOperazioniDAO {
        private final List<LogOperazioni> inseriti = new ArrayList<>();

        List<LogOperazioni> getInseriti() { return inseriti; }
        int count() { return inseriti.size(); }

        @Override
        public boolean insert(LogOperazioni log) {
            inseriti.add(log);
            return true;
        }

        @Override
        public List<LogOperazioni> findAll() { return new ArrayList<>(inseriti); }

        @Override
        public LogOperazioni findById(int id) { throw new UnsupportedOperationException(); }
        @Override
        public List<LogOperazioni> findByAmministratore(int adminId) { throw new UnsupportedOperationException(); }
        @Override
        public List<LogOperazioni> findByAzione(String azione) { throw new UnsupportedOperationException(); }
        @Override
        public boolean delete(int id) { throw new UnsupportedOperationException(); }
    }
}