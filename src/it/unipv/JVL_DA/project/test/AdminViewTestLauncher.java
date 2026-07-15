package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.view.admin.AdminDashboard;
import it.unipv.JVL_DA.project.view.admin.AdminLoginFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * Launcher di test PURAMENTE VISIVO per le view della cartella view/admin
 * (AdminLoginFrame e AdminDashboard).
 *
 * Scopo: verificare a schermo il layout delle finestre e il corretto aggancio
 * dei listener/getter esposti dalle view, senza alcun accesso a DAO, Service,
 * Controller o database.
 *
 * Nota MVC: le classi delle view NON vengono modificate. I listener agganciati
 * qui SIMULANO il ruolo del Controller (leggono i getter, mostrano un feedback).
 * L'unico adattamento è la sovrascrittura, dall'esterno e solo su queste istanze
 * di test, della close-operation da EXIT_ON_CLOSE a DISPOSE_ON_CLOSE, così da
 * poter chiudere una finestra e tornare al menu senza terminare la JVM.
 */
public class AdminViewTestLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminViewTestLauncher::mostraMenu);
    }

    /**
     * Menu di scelta della view admin da testare.
     * Si ripresenta automaticamente alla chiusura di ogni finestra.
     */
    private static void mostraMenu() {
        String[] opzioni = {"AdminLoginFrame", "AdminDashboard", "Esci"};
        int scelta = JOptionPane.showOptionDialog(null,
                "Quale view della cartella admin vuoi testare?",
                "LBA - Test View Admin",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opzioni, opzioni[0]);

        if (scelta == 0) {
            testAdminLoginFrame();
        } else if (scelta == 1) {
            testAdminDashboard();
        } else {
            // "Esci" oppure chiusura del dialog (CLOSED_OPTION)
            System.exit(0);
        }
    }

    // -------------------------------------------------------------------------
    // TEST AdminLoginFrame
    // -------------------------------------------------------------------------

    /**
     * Mostra AdminLoginFrame. Il listener di login simula il Controller:
     * legge getEmail()/getPassword() e restituisce un feedback tramite
     * showError() / resetForm(), senza alcuna verifica su DB.
     */
    private static void testAdminLoginFrame() {
        AdminLoginFrame frame = new AdminLoginFrame();

        // Le view usano EXIT_ON_CLOSE: lo sovrascriviamo solo su questa istanza
        // di test per non spegnere la JVM alla chiusura della finestra.
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tornaAlMenuAllaChiusura(frame);

        frame.addLoginListener(e -> {
            String email = frame.getEmail();
            char[] password = frame.getPassword();

            // Nessun AuthController/DB: verifichiamo solo che i getter leggano il form.
            if (email.isEmpty() || password.length == 0) {
                frame.showError("Compila email e password (test dei getter).");
            } else {
                frame.showError(""); // pulisce eventuali errori precedenti
                JOptionPane.showMessageDialog(frame,
                        "Listener OK.\n"
                                + "Email letta dalla view: " + email + "\n"
                                + "Caratteri password letti: " + password.length,
                        "Test AdminLoginFrame", JOptionPane.INFORMATION_MESSAGE);
                frame.resetForm(); // verifica anche resetForm()
            }

            // Igiene sulla password, coerentemente con Main.java
            Arrays.fill(password, '\0');
        });

        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // TEST AdminDashboard
    // -------------------------------------------------------------------------

    /**
     * Mostra AdminDashboard. I due listener di navigazione simulano il Controller:
     * confermano a schermo che il bottone corrispondente è stato agganciato e premuto.
     */
    private static void testAdminDashboard() {
        AdminDashboard frame = new AdminDashboard();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tornaAlMenuAllaChiusura(frame);

        frame.addGestisciSquadreListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Listener 'Gestione Squadre' agganciato e attivato.",
                        "Test AdminDashboard", JOptionPane.INFORMATION_MESSAGE));

        frame.addGestisciGiocatoriListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Listener 'Gestione Giocatori' agganciato e attivato.",
                        "Test AdminDashboard", JOptionPane.INFORMATION_MESSAGE));

        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // SUPPORTO
    // -------------------------------------------------------------------------

    /** Alla chiusura (dispose) della finestra ripresenta il menu di scelta. */
    private static void tornaAlMenuAllaChiusura(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                mostraMenu();
            }
        });
    }
}