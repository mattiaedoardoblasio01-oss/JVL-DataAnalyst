import it.unipv.JVL_DA.project.controller.LoginController;
import it.unipv.JVL_DA.project.view.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Punto di avvio dell'applicazione LBA.
 * Mostra la schermata di login e le collega il {@link LoginController}.
 */
public class Main {

    public static void main(String[] args) {
        // L'interfaccia Swing va costruita e mostrata sull'Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame); // si aggancia da solo al bottone "Accedi"
            loginFrame.setVisible(true);
        });
    }
}
