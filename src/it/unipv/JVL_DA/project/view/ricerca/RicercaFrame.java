package it.unipv.JVL_DA.project.view.ricerca;

import it.unipv.JVL_DA.project.POJO.Squadra;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RicercaFrame extends JFrame {

    private JTabbedPane tabbedPane;

    // --- COMPONENTI SCHEDA: RICERCA GIOCATORI ---
    private JTextField txtCercaNomeCognomeGiocatore;
    private JComboBox<String> comboFiltroRuolo;
    private JComboBox<Squadra> comboFiltroSquadra; // Permette di filtrare i giocatori per squadra
    private JButton btnCercaGiocatori;

    private JTable tabellaGiocatori;
    private DefaultTableModel modelGiocatori;

    // --- COMPONENTI SCHEDA: RICERCA SQUADRE ---
    private JTextField txtCercaNomeSquadra;
    private JTextField txtCercaSedeSquadra;
    private JButton btnCercaSquadre;

    private JTable tabellaSquadre;
    private DefaultTableModel modelSquadre;

    public RicercaFrame() {
        super("Pannello di Ricerca Generale");
        inizializzaUI();
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void inizializzaUI() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Costruzione delle due schede (Tab)
        JPanel panelGiocatori = creaPannelloRicercaGiocatori();
        JPanel panelSquadre = creaPannelloRicercaSquadre();

        tabbedPane.addTab("Ricerca Giocatori", panelGiocatori);
        tabbedPane.addTab("Ricerca Squadre", panelSquadre);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // CREAZIONE PANNELLO GIOCATORI
    // =========================================================================
    private JPanel creaPannelloRicercaGiocatori() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area Filtri (Nord)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtri Ricerca Giocatore"));

        filterPanel.add(new JLabel("Nome/Cognome:"));
        txtCercaNomeCognomeGiocatore = new JTextField(15);
        filterPanel.add(txtCercaNomeCognomeGiocatore);

        filterPanel.add(new JLabel("Ruolo:"));
        // ComboBox pre-popolata con ruoli standard del basket
        String[] ruoli = {"Tutti", "Playmaker", "Guardia", "Ala Piccola", "Ala Grande", "Centro"};
        comboFiltroRuolo = new JComboBox<>(ruoli);
        filterPanel.add(comboFiltroRuolo);

        filterPanel.add(new JLabel("Squadra:"));
        comboFiltroSquadra = new JComboBox<>(); // Verrà popolata dal Controller con gli oggetti Squadra dal DB
        filterPanel.add(comboFiltroSquadra);

        btnCercaGiocatori = new JButton("Cerca");
        filterPanel.add(btnCercaGiocatori);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Area Tabella (Centro)
        String[] colonneGiocatori = {"ID", "Nome", "Cognome", "Ruolo", "N° Maglia", "Squadra"};
        modelGiocatori = new DefaultTableModel(colonneGiocatori, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaGiocatori = new JTable(modelGiocatori);
        tabellaGiocatori.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabellaGiocatori);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // CREAZIONE PANNELLO SQUADRE
    // =========================================================================
    private JPanel creaPannelloRicercaSquadre() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area Filtri (Nord)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtri Ricerca Squadra"));

        filterPanel.add(new JLabel("Nome Squadra:"));
        txtCercaNomeSquadra = new JTextField(15);
        filterPanel.add(txtCercaNomeSquadra);

        filterPanel.add(new JLabel("Sede:"));
        txtCercaSedeSquadra = new JTextField(15);
        filterPanel.add(txtCercaSedeSquadra);

        btnCercaSquadre = new JButton("Cerca");
        filterPanel.add(btnCercaSquadre);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Area Tabella (Centro)
        String[] colonneSquadre = {"ID Squadra", "Nome", "Sede", "Allenatore", "URL Logo"};
        modelSquadre = new DefaultTableModel(colonneSquadre, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaSquadre = new JTable(modelSquadre);
        tabellaSquadre.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabellaSquadre);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // GETTER COMPONENTI (Per permettere al Controller di agganciare i Listener
    // e leggere/scrivere i dati dai campi di testo e dalle tabelle)
    // =========================================================================

    // Getter Generali
    public JTabbedPane getTabbedPane() { return tabbedPane; }

    // Getter per la scheda Giocatori
    public JTextField getTxtCercaNomeCognomeGiocatore() { return txtCercaNomeCognomeGiocatore; }
    public JComboBox<String> getComboFiltroRuolo() { return comboFiltroRuolo; }
    public JComboBox<Squadra> getComboFiltroSquadra() { return comboFiltroSquadra; }
    public JButton getBtnCercaGiocatori() { return btnCercaGiocatori; }
    public JTable getTabellaGiocatori() { return tabellaGiocatori; }
    public DefaultTableModel getModelGiocatori() { return modelGiocatori; }

    // Getter per la scheda Squadre
    public JTextField getTxtCercaNomeSquadra() { return txtCercaNomeSquadra; }
    public JTextField getTxtCercaSedeSquadra() { return txtCercaSedeSquadra; }
    public JButton getBtnCercaSquadre() { return btnCercaSquadre; }
    public JTable getTabellaSquadre() { return tabellaSquadre; }
    public DefaultTableModel getModelSquadre() { return modelSquadre; }
}