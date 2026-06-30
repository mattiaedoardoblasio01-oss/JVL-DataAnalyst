package it.unipv.JVL_DA.project.view;

import it.unipv.JVL_DA.project.DAO.implementazioni.GiocatoreDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IGiocatoreDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.ISquadraDAO;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.Squadra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboard extends JFrame {

    // --- COMPONENTI DELLA VIEW (Resi globali per essere letti dai Controller) ---
    private JTextField teamNameField, cityField, arenaField;
    private JTextField firstNameField, lastNameField;
    private JComboBox<Squadra> teamComboBox;
    private JSpinner numberSpinner;
    private DefaultTableModel teamTableModel;

    // --- DAO REALI (stessa struttura di AmministratoreDAO: si dipende dall'interfaccia, non dall'implementazione) ---
    private final ISquadraDAO squadraDAO = new SquadraDAO();
    private final IGiocatoreDAO giocatoreDAO = new GiocatoreDAO();

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
        // Invoca realmente SquadraDAO.insert(...), costruito esattamente come AmministratoreDAO.insert(...)
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

                // 2. Invocazione del DAO reale: insert(...) dichiara "throws SQLException", va quindi gestita qui nel Controller
                try {
                    boolean salvata = squadraDAO.insert(new Squadra(nomeSquadra, citta, palazzetto));

                    if (salvata) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Squadra salvata con successo nel Database LBA!");
                        teamNameField.setText("");
                        cityField.setText("");
                        arenaField.setText("");
                        aggiornaDatiSquadre();
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Salvataggio non riuscito. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Errore di connessione al Database: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
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

        // La combo box ora contiene oggetti Squadra letti dal database tramite SquadraDAO.findAll(),
        // non più una lista statica di nomi scritta a mano
        teamComboBox = new JComboBox<>();
        caricaSquadreNelComboBox();
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
        // Invoca realmente GiocatoreDAO.insert(...), stessa struttura di AmministratoreDAO.insert(...)
        savePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Estrazione dati dalla View
                String nome = firstNameField.getText().trim();
                String cognome = lastNameField.getText().trim();
                Squadra squadraSelezionata = (Squadra) teamComboBox.getSelectedItem();
                int numeroMaglia = (Integer) numberSpinner.getValue();

                // Validazione dell'input
                if (nome.isEmpty() || cognome.isEmpty() || squadraSelezionata == null) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Compila tutti i campi e seleziona una squadra valida!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Invocazione del DAO reale
                try {
                    boolean salvato = giocatoreDAO.insert(new Giocatore(nome, cognome, numeroMaglia, squadraSelezionata));

                    if (salvato) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Giocatore aggiunto correttamente al Roster!");
                        firstNameField.setText("");
                        lastNameField.setText("");
                        teamComboBox.setSelectedIndex(0);
                        numberSpinner.setValue(0);
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Salvataggio non riuscito. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Errore di connessione al Database: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
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

        // La tabella ora è alimentata dal database tramite SquadraDAO.findAll(), non più da dati scritti a mano
        String[] columns = {"Squadra", "Città"};
        teamTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(teamTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        aggiornaDatiSquadre();

        return panel;
    }

    // --- METODI DI SUPPORTO: LETTURA DATI TRAMITE DAO (stesso pattern try/catch usato per le insert) ---

    private void caricaSquadreNelComboBox() {
        try {
            List<Squadra> squadre = squadraDAO.findAll();
            teamComboBox.removeAllItems();
            for (Squadra squadra : squadre) {
                teamComboBox.addItem(squadra);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Impossibile caricare le squadre dal Database: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aggiornaDatiSquadre() {
        try {
            List<Squadra> squadre = squadraDAO.findAll();

            if (teamTableModel != null) {
                teamTableModel.setRowCount(0);
                for (Squadra squadra : squadre) {
                    teamTableModel.addRow(new Object[]{squadra.getNomeSquadra(), squadra.getCitta()});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Impossibile aggiornare l'elenco squadre: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        // Tiene allineata anche la combo box del form Giocatore, se già creata
        if (teamComboBox != null) {
            caricaSquadreNelComboBox();
        }
    }
}}