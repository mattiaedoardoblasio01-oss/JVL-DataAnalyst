package it.unipv.JVL_DA.project.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    private JButton gestisciSquadreButton;
    private JButton gestisciGiocatoriButton;

    // --- COSTRUTTORE ---
    public AdminDashboard() {
        setTitle("LBA - Dashboard Amministratore");
        setSize(700, 460);
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
        navPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 20, 14, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Titolo sezione
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        JLabel sezioneLabel = new JLabel("Seleziona un'area gestionale:");
        sezioneLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        sezioneLabel.setForeground(new Color(80, 80, 80));
        navPanel.add(sezioneLabel, gbc);

        // Bottone Squadre
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gestisciSquadreButton = createNavButton("Gestione Squadre", new Color(0, 102, 204));
        navPanel.add(gestisciSquadreButton, gbc);

        // Bottone Giocatori
        gbc.gridx = 1;
        gestisciGiocatoriButton = createNavButton("Gestione Giocatori", new Color(0, 102, 204));
        navPanel.add(gestisciGiocatoriButton, gbc);

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
    // -------------------------------------------------------------------------

    /**
     * Permette al Controller di agganciare l'apertura di SquadreFrame
     * al bottone "Gestione Squadre".
     */
    public void addGestisciSquadreListener(ActionListener listener) {
        gestisciSquadreButton.addActionListener(listener);
    }

    /**
     * Permette al Controller di agganciare l'apertura di GiocatoriFrame
     * al bottone "Gestione Giocatori".
     */
    public void addGestisciGiocatoriListener(ActionListener listener) {
        gestisciGiocatoriButton.addActionListener(listener);
    }
}