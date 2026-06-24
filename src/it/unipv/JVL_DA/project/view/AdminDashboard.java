package it.unipv.JVL_DA.project.view;

gitimport javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    // --- COMPONENTI DELLA VIEW (Resi globali per essere letti dai Controller) ---
    private JTextField teamNameField, cityField, arenaField;
    private JTextField firstNameField, lastNameField;
    private JComboBox<String> teamComboBox;
    private JSpinner numberSpinner;

    // --- MOCK DEI DAO (I metodi messi a disposizione dallo Sviluppatore 1) ---
    // Nota: Qui simuliamo le chiamate che interagiranno con il database del tuo compagno
    private void invocaSalvataggioSquadra(String nome, String citta) {
        // Qui Sviluppatore 1 avrà una classe tipo SquadraDAO
        // Esempio: squadraDAO.inserisciSquadra(new Squadra(nome, citta, palazzetto));
        System.out.println("LOG CONTROLLER: Invocato Model/DAO per Squadra -> " + nome + " (" + citta + ")");
    }

    private void invocaSalvataggioGiocatore(String nome, String cognome, String squadra, int numero) {
        // Qui Sviluppatore 1 avrà una classe tipo GiocatoreDAO
        // Esempio: giocatoreDAO.inserisciGiocatore(new Giocatore(nome, cognome, squadra, numero));
        System.out.println("LOG CONTROLLER: Invocato Model/DAO per Giocatore -> " + nome + " " + cognome + ", n°" + numero);
    }

    // --- COSTRUTTORE ---
    public AdminDashboard() {
        setTitle("LBA - Dashboard Amministratore");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Intestazione Superiore
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        JLabel headerLabel = new JLabel("PANNELLO CONTROLLO STAGIONE LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Contenitore a Schede (Garantisce RNF 8: Navigazione fluida in 1 click)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("➕ Nuova Squadra", createTeamFormPanel());
        tabbedPane.addTab("👤 Inserimento Giocatori", createPlayerFormPanel());
        tabbedPane.addTab("📋 Dettaglio e Classifica", createDetailPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- PANEL 1: FORM SQUADRA (UC4) + CONTROLLER ---
    private JPanel createTeamFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrazione Nuova Società"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome Squadra:"), gbc);
        gbc.gridx = 1;
        teamNameField = new JTextField(20);
        panel.add(teamNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Città Sede:"), gbc);
        gbc.gridx = 1;
        cityField = new JTextField(20);
        panel.add(cityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nome Palazzetto:"), gbc);
        gbc.gridx = 1;
        arenaField = new JTextField(20);
        panel.add(arenaField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton saveTeamButton = new JButton("Salva Squadra");
        saveTeamButton.setBackground(new Color(0, 102, 204));
        panel.add(saveTeamButton, gbc);

        // IMPLEMENTAZIONE CONTROLLER (Classe Anonima) per Bottone Squadra
        saveTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Estrazione dati dalla View
                String nomeSquadra = teamNameField.getText().trim();
                String citta = cityField.getText().trim();
                String palazzetto = arenaField.getText().trim();

                // Validazione base dell'input
                if (nomeSquadra.isEmpty() || citta.isEmpty() || palazzetto.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Tutti i campi della squadra sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Invocazione dei metodi del Model/DAO dello Sviluppatore 1
                invocaSalvataggioSquadra(nomeSquadra, citta, palazzetto);

                // Feedback visivo e svuotamento campi
                JOptionPane.showMessageDialog(AdminDashboard.this, "Squadra salvata con successo nel Database LBA!");
                teamNameField.setText("");
                cityField.setText("");
                arenaField.setText("");
            }
        });

        return panel;
    }

    // --- PANEL 2: FORM GIOCATORE (UC4) + CONTROLLER ---
    private JPanel createPlayerFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tesseramento Giocatore nel Roster"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Cognome:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        panel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Squadra di Appartenenza:"), gbc);
        gbc.gridx = 1;

        teamComboBox = new JComboBox<>();
        teamComboBox.addItem("Seleziona squadra... ");
        // Qui andranno inseriti i nomi delle squadre della stagione 2025/2026
        teamComboBox.addItem("Olimpia Milano");
        teamComboBox.addItem("Virtus Bologna");
        teamComboBox.addItem("Germani Brescia");
        teamComboBox.addItem("Reyer Venezia");
        teamComboBox.addItem("Bertram Derthona");
        teamComboBox.addItem("Pallacanestro Trieste");
        teamComboBox.addItem("UNAHOTELS Reggiana");
        teamComboBox.addItem("Dolomiti Energia Trentino");
        teamComboBox.addItem("Guerri Napoli");
        teamComboBox.addItem("Openjobmetis Varese");
        teamComboBox.addItem("OWW Udine");
        teamComboBox.addItem("Vanoli Cremona");
        teamComboBox.addItem("Nutribullet Treviso");
        teamComboBox.addItem("Acqua S.Bernardo Cantù");
        teamComboBox.addItem("Dinamo Sassari");
        panel.add(teamComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Numero Maglia:"), gbc);
        gbc.gridx = 1;
        numberSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        panel.add(numberSpinner, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JButton savePlayerButton = new JButton("Assegna al Roster");
        savePlayerButton.setBackground(new Color(0, 102, 204));
        panel.add(savePlayerButton, gbc);

        // IMPLEMENTAZIONE CONTROLLER (Classe Anonima) per Bottone Giocatore
        savePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Estrazione dati dalla View
                String nome = firstNameField.getText().trim();
                String cognome = lastNameField.getText().trim();
                String squadraSelezionata = (String) teamComboBox.getSelectedItem();
                int numeroMaglia = (Integer) numberSpinner.getValue();

                // Validazione dell'input
                if (nome.isEmpty() || cognome.isEmpty() || squadraSelezionata.equals("Seleziona squadra... ")) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Compila tutti i campi e seleziona una squadra valida!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Invocazione del Model/DAO
                invocaSalvataggioGiocatore(nome, cognome, squadraSelezionata, numeroMaglia);

                // Feedback visivo e reset dei campi
                JOptionPane.showMessageDialog(AdminDashboard.this, "Giocatore aggiunto correttamente al Roster!");
                firstNameField.setText("");
                lastNameField.setText("");
                teamComboBox.setSelectedIndex(0);
                numberSpinner.setValue(0);
            }
        });

        return panel;
    }

    // --- PANEL 3: DETTAGLIO ---
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel infoLabel = new JLabel("Riepilogo Generale e Info Squadre registrate nel Database");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        panel.add(infoLabel, BorderLayout.NORTH);

        String[] columns = {"Squadra", "Città"};
        Object[][] data = {
                {"Olimpia Milano", "Milano"},
                {"Virtus Bologna", "Bologna"},
                {"Germani Brescia", "Brescia"},
                {"Reyer Venezia", "Venezia"},
                {"Bertram Derthona", "Tortona"},
                {"UNAHOTELS Reggiana", "Reggio Emilia"},
                {"Pallacanestro Trieste", "Trieste"},
                {"Dolomiti Energia Trentino", "Trento"},
                {"Openjobmetis Varese", "Varese"},
                {"Guerri Napoli", "Napoli"},
                {"OWW Udine", "Udine"},
                {"Vanoli Cremona", "Cremona"},
                {"Nutribullet Treviso", "Treviso"},
                {"Acqua S. Bernardo Cantù", "Cantù"},
                {"Dinamo Sassari", "Sassari"},


        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}