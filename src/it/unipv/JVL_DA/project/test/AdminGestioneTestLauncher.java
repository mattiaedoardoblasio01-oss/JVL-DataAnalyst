package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.view.giocatori.GiocatoriFrame;
import it.unipv.JVL_DA.project.view.squadre.SquadreFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Launcher di test VISIVO per le view di gestione dell'area admin
 * (SquadreFrame e GiocatoriFrame), con verifica delle modifiche
 * "momentanee" effettuate dall'amministratore.
 *
 * Come funziona (fedeltà MVC):
 *   - Le classi delle view NON vengono modificate: vengono pilotate solo
 *     tramite i loro metodi pubblici, esattamente come farebbe il Controller.
 *   - Questa classe SIMULA il ruolo di GestioneLegaController: aggancia gli
 *     stessi listener (addNuovo/Salva/EliminaListener) e usa gli stessi getter
 *     e populateTable(), ma al posto dei DAO/DB usa DUE LISTE IN MEMORIA.
 *   - Le operazioni CRUD dell'admin restano quindi "momentanee": vivono per
 *     tutta la sessione (e si vedono aggiornarsi in tabella) e spariscono alla
 *     chiusura del programma, senza alcuna persistenza su database.
 *
 * Le validazioni riproducono quelle del Controller reale, così il
 * comportamento testato è quello effettivo dell'applicazione.
 */
public class AdminGestioneTestLauncher {

    // --- "DATABASE" IN MEMORIA (al posto dei DAO) ---
    private static final List<Squadra> squadreDB = new ArrayList<>();
    private static final List<Giocatore> giocatoriDB = new ArrayList<>();

    public static void main(String[] args) {
        seedDatiIniziali();
        SwingUtilities.invokeLater(AdminGestioneTestLauncher::mostraMenu);
    }

    /** Dati di partenza in memoria, così le tabelle non sono vuote all'avvio. */
    private static void seedDatiIniziali() {
        Squadra s1 = new Squadra(UUID.randomUUID().toString(), "Vigevano Basket", "Vigevano", "", "Coach Rossi");
        Squadra s2 = new Squadra(UUID.randomUUID().toString(), "Pavia Basket", "Pavia", "", "Coach Bianchi");
        squadreDB.add(s1);
        squadreDB.add(s2);

        giocatoriDB.add(new Giocatore(UUID.randomUUID().toString(), "Mario", "Verdi", "Playmaker", 7, s1));
        giocatoriDB.add(new Giocatore(UUID.randomUUID().toString(), "Luca", "Neri", "Ala", 10, s2));
    }

    /** Menu di scelta dell'area gestionale. Si ripresenta ad ogni chiusura. */
    private static void mostraMenu() {
        String[] opzioni = {"Gestione Squadre", "Gestione Giocatori", "Esci"};
        int scelta = JOptionPane.showOptionDialog(null,
                "Quale area di gestione vuoi testare?\n"
                        + "(le modifiche restano solo in memoria per questa sessione)",
                "LBA - Test Gestione Admin",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opzioni, opzioni[0]);

        if (scelta == 0) {
            apriGestioneSquadre();
        } else if (scelta == 1) {
            apriGestioneGiocatori();
        } else {
            System.exit(0); // "Esci" o chiusura del dialog
        }
    }

    // =========================================================================
    // GESTIONE SQUADRE  (simula GestioneLegaController.apriGestioneSquadre)
    // =========================================================================

    private static void apriGestioneSquadre() {
        SquadreFrame frame = new SquadreFrame();
        frame.addNuovoListener(e -> frame.clearForm());
        frame.addSalvaListener(e -> salvaSquadra(frame));
        frame.addEliminaListener(e -> eliminaSquadra(frame));
        tornaAlMenuAllaChiusura(frame);
        refreshSquadre(frame);
        frame.setVisible(true);
    }

    private static void refreshSquadre(SquadreFrame frame) {
        frame.populateTable(squadreDB);
    }

    private static void salvaSquadra(SquadreFrame frame) {
        String nome = frame.getNome();
        String sede = frame.getSede();

        if (nome.isEmpty() || sede.isEmpty()) {
            frame.showError("Nome e Sede sono obbligatori.");
            return;
        }

        String selectedId = frame.getSelectedId();

        // Vincolo di unicità sul nome (vale sia per insert sia per rinomina)
        for (Squadra s : squadreDB) {
            if (s.getNome().equalsIgnoreCase(nome) && !s.getId().equals(selectedId)) {
                frame.showError("Esiste già una squadra con questo nome.");
                return;
            }
        }

        if (selectedId == null) {
            // INSERT: id generato, come nel Controller reale
            squadreDB.add(new Squadra(UUID.randomUUID().toString(), nome, sede,
                    frame.getLogoURL(), frame.getAllenatore()));
            refreshSquadre(frame);
            frame.clearForm();
            frame.showSuccess("Squadra creata con successo (in memoria).");
        } else {
            // UPDATE: aggiorno l'oggetto esistente (l'identità resta la stessa,
            // così i giocatori che referenziano questa squadra restano coerenti)
            Squadra esistente = findSquadraById(selectedId);
            if (esistente != null) {
                esistente.setNome(nome);
                esistente.setSede(sede);
                esistente.setLogoURL(frame.getLogoURL());
                esistente.setAllenatore(frame.getAllenatore());
            }
            refreshSquadre(frame);
            frame.clearForm();
            frame.showSuccess("Squadra aggiornata con successo (in memoria).");
        }
    }

    private static void eliminaSquadra(SquadreFrame frame) {
        String selectedId = frame.getSelectedId();
        if (selectedId == null) {
            frame.showError("Seleziona una squadra dalla tabella.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare la squadra selezionata?", "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        // Integrità referenziale: nel DB reale è una FK; qui la simuliamo a mano
        for (Giocatore g : giocatoriDB) {
            if (g.getSquadra() != null && g.getSquadra().getId().equals(selectedId)) {
                frame.showError("Impossibile eliminare: la squadra ha giocatori collegati.");
                return;
            }
        }

        squadreDB.removeIf(s -> s.getId().equals(selectedId));
        refreshSquadre(frame);
        frame.clearForm();
        frame.showSuccess("Squadra eliminata (in memoria).");
    }

    // =========================================================================
    // GESTIONE GIOCATORI  (simula GestioneLegaController.apriGestioneGiocatori)
    // =========================================================================

    private static void apriGestioneGiocatori() {
        GiocatoriFrame frame = new GiocatoriFrame();
        frame.addNuovoListener(e -> frame.clearForm());
        frame.addSalvaListener(e -> salvaGiocatore(frame));
        frame.addEliminaListener(e -> eliminaGiocatore(frame));
        tornaAlMenuAllaChiusura(frame);

        frame.populateSquadreComboBox(squadreDB); // stesse squadre in memoria
        refreshGiocatori(frame);
        frame.setVisible(true);
    }

    private static void refreshGiocatori(GiocatoriFrame frame) {
        frame.populateTable(giocatoriDB);
    }

    private static void salvaGiocatore(GiocatoriFrame frame) {
        String nome = frame.getNome();
        String cognome = frame.getCognome();
        String ruolo = frame.getRuolo();
        Squadra squadra = frame.getSelectedSquadra();
        int nMaglia = frame.getNumeroMaglia();

        if (nome.isEmpty() || cognome.isEmpty() || ruolo.isEmpty()) {
            frame.showError("Nome, Cognome e Ruolo sono obbligatori.");
            return;
        }
        if (squadra == null) {
            frame.showError("Seleziona una squadra per il giocatore.");
            return;
        }

        String selectedId = frame.getSelectedId();

        // Roster: numero di maglia unico nella squadra + tetto di 15 giocatori
        int giocatoriNellaSquadra = 0;
        for (Giocatore g : giocatoriDB) {
            if (g.getSquadra() != null && g.getSquadra().getId().equals(squadra.getId())) {
                giocatoriNellaSquadra++;
                if (g.getNMaglia() == nMaglia && !g.getId().equals(selectedId)) {
                    frame.showError("Numero " + nMaglia + " già assegnato a "
                            + g.getNome() + " " + g.getCognome() + ".");
                    return;
                }
            }
        }
        if (selectedId == null && giocatoriNellaSquadra >= 15) {
            frame.showError("Roster completo: massimo 15 giocatori per squadra.");
            return;
        }

        if (selectedId == null) {
            giocatoriDB.add(new Giocatore(UUID.randomUUID().toString(),
                    nome, cognome, ruolo, nMaglia, squadra));
            refreshGiocatori(frame);
            frame.clearForm();
            frame.showSuccess("Giocatore inserito nel roster (in memoria).");
        } else {
            Giocatore esistente = findGiocatoreById(selectedId);
            if (esistente != null) {
                esistente.setNome(nome);
                esistente.setCognome(cognome);
                esistente.setRuolo(ruolo);
                esistente.setNMaglia(nMaglia);
                esistente.setSquadra(squadra);
            }
            refreshGiocatori(frame);
            frame.clearForm();
            frame.showSuccess("Giocatore aggiornato (in memoria).");
        }
    }

    private static void eliminaGiocatore(GiocatoriFrame frame) {
        String selectedId = frame.getSelectedId();
        if (selectedId == null) {
            frame.showError("Seleziona un giocatore dalla tabella.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare il giocatore selezionato?", "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);
        if (conferma != JOptionPane.YES_OPTION) return;

        giocatoriDB.removeIf(g -> g.getId().equals(selectedId));
        refreshGiocatori(frame);
        frame.clearForm();
        frame.showSuccess("Giocatore eliminato dal roster (in memoria).");
    }

    // =========================================================================
    // SUPPORTO
    // =========================================================================

    private static Squadra findSquadraById(String id) {
        for (Squadra s : squadreDB) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    private static Giocatore findGiocatoreById(String id) {
        for (Giocatore g : giocatoriDB) {
            if (g.getId().equals(id)) return g;
        }
        return null;
    }

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