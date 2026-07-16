package it.unipv.JVL_DA.project.view.ricerca;

import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Squadra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RicercaFrame extends JFrame {

    private JTabbedPane tabbedPane;

    // --- COMPONENTI SCHEDA: RICERCA GIOCATORI ---
    private JTextField txtCercaNomeCognomeGiocatore;
    private JComboBox<String> comboFiltroRuolo;
    private JComboBox<Squadra> comboFiltroSquadra; // filtra i giocatori per squadra
    private JButton btnCercaGiocatori;

    private JTable tabellaGiocatori;
    private DefaultTableModel modelGiocatori;

    // --- COMPONENTI SCHEDA: RICERCA SQUADRE ---
    private JTextField txtCercaNomeSquadra;
    private JTextField txtCercaSedeSquadra;
    private JButton btnCercaSquadre;

    private JTable tabellaSquadre;
    private DefaultTableModel modelSquadre;

    // --- COMPONENTI SCHEDA: CLASSIFICA ---
    private JComboBox<Campionato> comboCampionatoClassifica;
    private JButton btnMostraClassifica;
    private JTable tabellaClassifica;
    private DefaultTableModel modelClassifica;

    // --- COMPONENTI SCHEDA: CALENDARIO ---
    private JComboBox<Campionato> comboCampionatoCalendario;
    private JSpinner spinGiornataCalendario;
    private JButton btnMostraCalendario;
    private JTable tabellaCalendario;
    private DefaultTableModel modelCalendario;

    // --- COMPONENTI SCHEDA: PREFERITI ---
    private JButton btnAggiornaPreferiti;
    private JButton btnRimuoviGiocatorePreferito;
    private JButton btnRimuoviSquadraPreferita;
    private JTable tabellaGiocatoriPreferiti;
    private DefaultTableModel modelGiocatoriPreferiti;
    private JTable tabellaSquadrePreferite;
    private DefaultTableModel modelSquadrePreferite;

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
        tabbedPane.addTab("Ricerca Giocatori", creaPannelloRicercaGiocatori());
        tabbedPane.addTab("Ricerca Squadre", creaPannelloRicercaSquadre());
        tabbedPane.addTab("Classifica", creaPannelloClassifica());
        tabbedPane.addTab("Calendario", creaPannelloCalendario());
        tabbedPane.addTab("I Miei Preferiti", creaPannelloPreferiti());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // CREAZIONE PANNELLO GIOCATORI
    // =========================================================================
    private JPanel creaPannelloRicercaGiocatori() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area Filtri (Nord): GridBagLayout su una riga, per un allineamento
        // stabile che non "sballa" al variare della larghezza dei componenti.
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtri Ricerca Giocatore"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;

        gbc.gridx = 0;
        filterPanel.add(new JLabel("Nome/Cognome:"), gbc);
        gbc.gridx = 1;
        txtCercaNomeCognomeGiocatore = new JTextField(14);
        filterPanel.add(txtCercaNomeCognomeGiocatore, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Ruolo:"), gbc);
        gbc.gridx = 3;
        String[] ruoli = {"Tutti", "Playmaker", "Guardia", "Ala Piccola", "Ala Grande", "Centro"};
        comboFiltroRuolo = new JComboBox<>(ruoli);
        filterPanel.add(comboFiltroRuolo, gbc);

        gbc.gridx = 4;
        filterPanel.add(new JLabel("Squadra:"), gbc);
        gbc.gridx = 5;
        comboFiltroSquadra = new JComboBox<>();
        // FIX: senza renderer il combo mostrava il toString() completo di Squadra,
        // diventando larghissimo e rompendo il layout. Ora mostra solo il nome.
        comboFiltroSquadra.setRenderer(creaRendererSquadra());
        comboFiltroSquadra.setPreferredSize(
                new Dimension(160, comboFiltroRuolo.getPreferredSize().height));
        filterPanel.add(comboFiltroSquadra, gbc);

        gbc.gridx = 6;
        btnCercaGiocatori = new JButton("Cerca");
        filterPanel.add(btnCercaGiocatori, gbc);

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
        mainPanel.add(new JScrollPane(tabellaGiocatori), BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // CREAZIONE PANNELLO SQUADRE
    // =========================================================================
    private JPanel creaPannelloRicercaSquadre() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Area Filtri (Nord): stesso stile allineato della scheda giocatori
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtri Ricerca Squadra"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;

        gbc.gridx = 0;
        filterPanel.add(new JLabel("Nome Squadra:"), gbc);
        gbc.gridx = 1;
        txtCercaNomeSquadra = new JTextField(14);
        filterPanel.add(txtCercaNomeSquadra, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Sede:"), gbc);
        gbc.gridx = 3;
        txtCercaSedeSquadra = new JTextField(14);
        filterPanel.add(txtCercaSedeSquadra, gbc);

        gbc.gridx = 4;
        btnCercaSquadre = new JButton("Cerca");
        filterPanel.add(btnCercaSquadre, gbc);

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
        mainPanel.add(new JScrollPane(tabellaSquadre), BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // CREAZIONE PANNELLO CLASSIFICA (sola lettura)
    // =========================================================================
    private JPanel creaPannelloClassifica() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtri = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtri.setBorder(BorderFactory.createTitledBorder("Classifica Regular Season"));
        filtri.add(new JLabel("Campionato:"));
        comboCampionatoClassifica = new JComboBox<>();
        comboCampionatoClassifica.setRenderer(creaRendererCampionato());
        filtri.add(comboCampionatoClassifica);
        btnMostraClassifica = new JButton("Mostra Classifica");
        filtri.add(btnMostraClassifica);
        mainPanel.add(filtri, BorderLayout.NORTH);

        String[] colonne = {"Pos.", "Squadra", "Giocate", "Vittorie", "Sconfitte", "Diff. Canestri"};
        modelClassifica = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaClassifica = new JTable(modelClassifica);
        tabellaClassifica.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaClassifica.getTableHeader().setReorderingAllowed(false);
        mainPanel.add(new JScrollPane(tabellaClassifica), BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // CREAZIONE PANNELLO CALENDARIO (sola lettura)
    // =========================================================================
    private JPanel creaPannelloCalendario() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtri = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtri.setBorder(BorderFactory.createTitledBorder("Calendario Regular Season"));
        filtri.add(new JLabel("Campionato:"));
        comboCampionatoCalendario = new JComboBox<>();
        comboCampionatoCalendario.setRenderer(creaRendererCampionato());
        filtri.add(comboCampionatoCalendario);
        filtri.add(new JLabel("Giornata:"));
        spinGiornataCalendario = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        filtri.add(spinGiornataCalendario);
        btnMostraCalendario = new JButton("Carica Partite");
        filtri.add(btnMostraCalendario);
        mainPanel.add(filtri, BorderLayout.NORTH);

        String[] colonne = {"ID Partita", "Data e Ora", "Squadra Casa", "Squadra Ospite",
                "Punti Casa", "Punti Ospite", "Luogo", "Stato"};
        modelCalendario = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaCalendario = new JTable(modelCalendario);
        tabellaCalendario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaCalendario.getTableHeader().setReorderingAllowed(false);
        mainPanel.add(new JScrollPane(tabellaCalendario), BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================================
    // CREAZIONE PANNELLO PREFERITI (giocatori + squadre salvati)
    // =========================================================================
    private JPanel creaPannelloPreferiti() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel comandi = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnAggiornaPreferiti = new JButton("Aggiorna");
        btnRimuoviGiocatorePreferito = new JButton("Rimuovi Giocatore");
        btnRimuoviSquadraPreferita = new JButton("Rimuovi Squadra");
        comandi.add(btnAggiornaPreferiti);
        comandi.add(btnRimuoviGiocatorePreferito);
        comandi.add(btnRimuoviSquadraPreferita);
        mainPanel.add(comandi, BorderLayout.NORTH);

        // Tabella giocatori preferiti (stesse colonne della ricerca giocatori)
        String[] colGiocatori = {"ID", "Nome", "Cognome", "Ruolo", "N° Maglia", "Squadra"};
        modelGiocatoriPreferiti = new DefaultTableModel(colGiocatori, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaGiocatoriPreferiti = new JTable(modelGiocatoriPreferiti);
        tabellaGiocatoriPreferiti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollGiocatori = new JScrollPane(tabellaGiocatoriPreferiti);
        scrollGiocatori.setBorder(BorderFactory.createTitledBorder("Giocatori Preferiti"));

        // Tabella squadre preferite (stesse colonne della ricerca squadre)
        String[] colSquadre = {"ID Squadra", "Nome", "Sede", "Allenatore", "URL Logo"};
        modelSquadrePreferite = new DefaultTableModel(colSquadre, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaSquadrePreferite = new JTable(modelSquadrePreferite);
        tabellaSquadrePreferite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollSquadre = new JScrollPane(tabellaSquadrePreferite);
        scrollSquadre.setBorder(BorderFactory.createTitledBorder("Squadre Preferite"));

        JPanel tabelle = new JPanel(new GridLayout(2, 1, 0, 10));
        tabelle.add(scrollGiocatori);
        tabelle.add(scrollSquadre);
        mainPanel.add(tabelle, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Renderer che nel ComboBox mostra solo il nome della squadra invece del
     * toString() verboso di Squadra. Gestisce anche eventuali voci non-Squadra
     * (es. un placeholder "Tutte" aggiunto dal Controller), lasciate al default.
     */
    private DefaultListCellRenderer creaRendererSquadra() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Squadra) {
                    setText(((Squadra) value).getNome());
                }
                return this;
            }
        };
    }

    /** Renderer che mostra il nome del Campionato nei ComboBox (come per le Squadre). */
    private DefaultListCellRenderer creaRendererCampionato() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Campionato) {
                    setText(((Campionato) value).getNome());
                }
                return this;
            }
        };
    }

    // =========================================================================
    // GETTER COMPONENTI (invariati: il PublicController continua a funzionare)
    // =========================================================================

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

    // Getter per la scheda Classifica
    public JComboBox<Campionato> getComboCampionatoClassifica() { return comboCampionatoClassifica; }
    public JButton getBtnMostraClassifica() { return btnMostraClassifica; }
    public JTable getTabellaClassifica() { return tabellaClassifica; }
    public DefaultTableModel getModelClassifica() { return modelClassifica; }

    // Getter per la scheda Calendario
    public JComboBox<Campionato> getComboCampionatoCalendario() { return comboCampionatoCalendario; }
    public JSpinner getSpinGiornataCalendario() { return spinGiornataCalendario; }
    public JButton getBtnMostraCalendario() { return btnMostraCalendario; }
    public JTable getTabellaCalendario() { return tabellaCalendario; }
    public DefaultTableModel getModelCalendario() { return modelCalendario; }

    // Getter per la scheda Preferiti
    public JButton getBtnAggiornaPreferiti() { return btnAggiornaPreferiti; }
    public JButton getBtnRimuoviGiocatorePreferito() { return btnRimuoviGiocatorePreferito; }
    public JButton getBtnRimuoviSquadraPreferita() { return btnRimuoviSquadraPreferita; }
    public JTable getTabellaGiocatoriPreferiti() { return tabellaGiocatoriPreferiti; }
    public DefaultTableModel getModelGiocatoriPreferiti() { return modelGiocatoriPreferiti; }
    public JTable getTabellaSquadrePreferite() { return tabellaSquadrePreferite; }
    public DefaultTableModel getModelSquadrePreferite() { return modelSquadrePreferite; }
}