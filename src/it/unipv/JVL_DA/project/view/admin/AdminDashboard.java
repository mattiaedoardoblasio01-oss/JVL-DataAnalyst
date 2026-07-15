package it.unipv.JVL_DA.project.view.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    // Aree gestionali (aprono una finestra CRUD)
    private JButton gestisciSquadreButton;
    private JButton gestisciGiocatoriButton;
    private JButton gestisciCampionatoButton;
    private JButton gestisciStatisticheButton;
    private JButton gestisciCalendarioButton;
    // Operazioni di stagione (azioni dirette)
    private JButton generaRegularSeasonButton;
    private JButton generaPlayoffButton;

    // --- COSTRUTTORE ---
    public AdminDashboard() {
        setTitle("LBA - Dashboard Amministratore");
        setSize(760, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        JLabel headerLabel = new JLabel("PANNELLO CONTROLLO STAGIONE LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- PANNELLO NAVIGAZIONE ---
    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBorder(BorderFactory.createEmptyBorder(24, 50, 24, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Titolo sezione
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        JLabel sezioneLabel = new JLabel("Seleziona un'area gestionale o un'operazione di stagione:");
        sezioneLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        sezioneLabel.setForeground(new Color(80, 80, 80));
        navPanel.add(sezioneLabel, gbc);

        // Blu per le aree gestionali, verde per le operazioni di stagione
        Color gestione = new Color(0, 102, 204);
        Color stagione = new Color(0, 140, 70);

        // Riga 1: Squadre | Giocatori
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 1;
        gestisciSquadreButton = createNavButton("Gestione Squadre", gestione);
        navPanel.add(gestisciSquadreButton, gbc);
        gbc.gridx = 1;
        gestisciGiocatoriButton = createNavButton("Gestione Giocatori", gestione);
        navPanel.add(gestisciGiocatoriButton, gbc);

        // Riga 2: Campionato | Statistiche
        gbc.gridx = 0; gbc.gridy = 2;
        gestisciCampionatoButton = createNavButton("Gestione Campionato", gestione);
        navPanel.add(gestisciCampionatoButton, gbc);
        gbc.gridx = 1;
        gestisciStatisticheButton = createNavButton("Statistiche e Log", gestione);
        navPanel.add(gestisciStatisticheButton, gbc);

        // Riga 3: Calendario e Risultati | Genera Regular Season
        gbc.gridx = 0; gbc.gridy = 3;
        gestisciCalendarioButton = createNavButton("Calendario e Risultati", gestione);
        navPanel.add(gestisciCalendarioButton, gbc);
        gbc.gridx = 1;
        generaRegularSeasonButton = createNavButton("Genera Regular Season", stagione);
        navPanel.add(generaRegularSeasonButton, gbc);

        // Riga 4: Genera Playoff (a tutta larghezza)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        generaPlayoffButton = createNavButton("Genera Playoff", stagione);
        navPanel.add(generaPlayoffButton, gbc);

        return navPanel;
    }

    // --- FOOTER ---
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(230, 235, 242));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel footerLabel = new JLabel("© Lega Basket Amministrazione — Accesso consentito solo agli amministratori");
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    // --- FACTORY BOTTONE DI NAVIGAZIONE ---
    private JButton createNavButton(String testo, Color colore) {
        JButton button = new JButton(testo);
        button.setBackground(colore);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return button;
    }

    // -------------------------------------------------------------------------
    // METODI PUBBLICI PER IL CONTROLLER
    // Ogni bottone espone il proprio addListener: il Controller aggancia la
    // logica senza dover mai modificare questa classe.
    // -------------------------------------------------------------------------

    public void addGestisciSquadreListener(ActionListener listener) {
        gestisciSquadreButton.addActionListener(listener);
    }

    public void addGestisciGiocatoriListener(ActionListener listener) {
        gestisciGiocatoriButton.addActionListener(listener);
    }

    public void addGestisciCampionatoListener(ActionListener listener) {
        gestisciCampionatoButton.addActionListener(listener);
    }

    public void addGestisciStatisticheListener(ActionListener listener) {
        gestisciStatisticheButton.addActionListener(listener);
    }

    public void addGestisciCalendarioListener(ActionListener listener) {
        gestisciCalendarioButton.addActionListener(listener);
    }

    public void addGeneraRegularSeasonListener(ActionListener listener) {
        generaRegularSeasonButton.addActionListener(listener);
    }

    public void addGeneraPlayoffListener(ActionListener listener) {
        generaPlayoffButton.addActionListener(listener);
    }
}