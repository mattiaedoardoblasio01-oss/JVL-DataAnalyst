package it.unipv.JVL_DA.project.view.statistiche;

import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.LogOperazioni;
import it.unipv.JVL_DA.project.POJO.Statistiche;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatisticheFrame extends JFrame {

    // Formatter per il timestamp del log
    private static final DateTimeFormatter FMT_TIMESTAMP =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // -------------------------------------------------------------------------
    // COMPONENTI TAB STATISTICHE
    // Col tabella: 0=ID stat(nascosta), 1=Giocatore, 2=Squadra,
    //              3=Punti, 4=Rimbalzi, 5=Assist, 6=GiocatoreID(nascosta)
    //
    // La colonna 6 è nascosta come la 0: serve al listener di selezione
    // per trovare il Giocatore corretto nel comboBox tramite getId()
    // -------------------------------------------------------------------------
    private JTable             statisticheTable;
    private DefaultTableModel  statisticheTableModel;
    private JComboBox<Giocatore> giocatoreComboBox;
    private JSpinner           puntiSpinner;
    private JSpinner           rimbalziSpinner;
    private JSpinner           assistSpinner;
    private JButton            nuovoStatisticheButton;
    private JButton            salvaStatisticheButton;
    private JButton            eliminaStatisticheButton;
    private JLabel             feedbackStatisticheLabel;

    // -------------------------------------------------------------------------
    // COMPONENTI TAB LOG OPERAZIONI (sola lettura)
    // Col tabella: 0=ID(nascosta), 1=Amministratore, 2=Timestamp, 3=Azione, 4=Dettagli
    // -------------------------------------------------------------------------
    private JTable            logTable;
    private DefaultTableModel logTableModel;
    private JButton           aggiornaLogButton;

    // --- COSTRUTTORE ---
    public StatisticheFrame() {
        setTitle("LBA - Statistiche e Log Operazioni");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📊 Statistiche",     createStatisticheTab());
        tabbedPane.addTab("📋 Log Operazioni",  createLogTab());
        add(tabbedPane, BorderLayout.CENTER);

        // Listener interno: selezione riga → popola il form (comportamento puramente UI)
        // Usa la col 6 (GiocatoreID nascosta) per trovare il Giocatore nel ComboBox
        statisticheTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && statisticheTable.getSelectedRow() != -1) {
                    int row = statisticheTable.getSelectedRow();

                    // Riseleziona il Giocatore confrontando per ID (String)
                    String giocId = (String) statisticheTableModel.getValueAt(row, 6);
                    for (int i = 0; i < giocatoreComboBox.getItemCount(); i++) {
                        if (giocatoreComboBox.getItemAt(i).getId().equals(giocId)) {
                            giocatoreComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    puntiSpinner.setValue((Integer)    statisticheTableModel.getValueAt(row, 3));
                    rimbalziSpinner.setValue((Integer) statisticheTableModel.getValueAt(row, 4));
                    assistSpinner.setValue((Integer)   statisticheTableModel.getValueAt(row, 5));
                    feedbackStatisticheLabel.setText(" ");
                }
            }
        });

        // Listener interno del bottone "Nuovo"
        nuovoStatisticheButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { clearFormStatistiche(); }
        });
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel headerLabel = new JLabel("STATISTICHE E LOG OPERAZIONI LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // =========================================================================
    // TAB 1 — STATISTICHE
    // =========================================================================

    private JPanel createStatisticheTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.add(createStatisticheTablePanel(), BorderLayout.CENTER);
        tab.add(createStatisticheFormPanel(),  BorderLayout.SOUTH);
        return tab;
    }

    private JPanel createStatisticheTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        String[] columns = {"ID", "Giocatore", "Squadra", "Punti", "Rimbalzi", "Assist", "GiocID"};
        statisticheTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        statisticheTable = new JTable(statisticheTableModel);
        statisticheTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statisticheTable.setRowHeight(22);
        statisticheTable.getTableHeader().setReorderingAllowed(false);

        // Nasconde colonna 0 (ID statistiche)
        statisticheTable.getColumnModel().getColumn(0).setMinWidth(0);
        statisticheTable.getColumnModel().getColumn(0).setMaxWidth(0);
        statisticheTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Nasconde colonna 6 (GiocatoreID): usata solo per il matching nel ComboBox
        statisticheTable.getColumnModel().getColumn(6).setMinWidth(0);
        statisticheTable.getColumnModel().getColumn(6).setMaxWidth(0);
        statisticheTable.getColumnModel().getColumn(6).setPreferredWidth(0);

        panel.add(new JScrollPane(statisticheTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatisticheFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Scheda Statistiche"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Riga 0: Giocatore (piena larghezza)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Giocatore:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        giocatoreComboBox = new JComboBox<>();
        // Renderer custom: mostra "Nome Cognome" invece del toString() verboso di Giocatore
        giocatoreComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Giocatore) {
                    setText(((Giocatore) value).getNome() + " " + ((Giocatore) value).getCognome());
                }
                return this;
            }
        });
        formPanel.add(giocatoreComboBox, gbc);

        // Riga 1: Punti | Rimbalzi
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Punti:"), gbc);
        gbc.gridx = 1;
        puntiSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(puntiSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Rimbalzi:"), gbc);
        gbc.gridx = 3;
        rimbalziSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(rimbalziSpinner, gbc);

        // Riga 2: Assist (affiancato a spazio libero per simmetria visiva)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Assist:"), gbc);
        gbc.gridx = 1;
        assistSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(assistSpinner, gbc);

        // Riga 3: Feedback
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackStatisticheLabel = new JLabel(" ");
        feedbackStatisticheLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(feedbackStatisticheLabel, gbc);

        // Riga 4: Bottoni
        gbc.gridy = 4;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        nuovoStatisticheButton   = createButton("  Nuovo",   new Color(85, 85, 85));
        salvaStatisticheButton   = createButton("  Salva",   new Color(0, 102, 204));
        eliminaStatisticheButton = createButton("  Elimina", new Color(185, 30, 30));
        bp.add(nuovoStatisticheButton);
        bp.add(salvaStatisticheButton);
        bp.add(eliminaStatisticheButton);
        formPanel.add(bp, gbc);

        outerPanel.add(formPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    // =========================================================================
    // TAB 2 — LOG OPERAZIONI (sola lettura)
    // =========================================================================

    private JPanel createLogTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Amministratore", "Timestamp", "Azione", "Dettagli"};
        logTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        logTable = new JTable(logTableModel);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setRowHeight(22);
        logTable.getTableHeader().setReorderingAllowed(false);
        // AUTO_RESIZE_OFF con scroll orizzontale: la colonna Dettagli può essere lunga
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Nascondi colonna ID
        logTable.getColumnModel().getColumn(0).setMinWidth(0);
        logTable.getColumnModel().getColumn(0).setMaxWidth(0);
        logTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tab.add(scrollPane, BorderLayout.CENTER);

        // Bottone Aggiorna (unica interazione disponibile nel tab log)
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        aggiornaLogButton = createButton("  Aggiorna Log", new Color(0, 102, 204));
        southPanel.add(aggiornaLogButton);
        tab.add(southPanel, BorderLayout.SOUTH);

        return tab;
    }

    // Factory privata condivisa tra i due tab
    private JButton createButton(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setBackground(colore);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // =========================================================================
    // METODI PUBBLICI PER IL CONTROLLER — TAB STATISTICHE
    // =========================================================================

    /**
     * Popola la tabella statistiche. Da chiamare all'apertura e dopo ogni CRUD.
     * Scrive anche la colonna 6 (GiocatoreID nascosta) per il matching nel ComboBox.
     */
    public void populateStatisticheTable(List<Statistiche> statistiche) {
        statisticheTableModel.setRowCount(0);
        for (Statistiche s : statistiche) {
            String nomeGiocatore = (s.getGiocatore() != null)
                    ? s.getGiocatore().getNome() + " " + s.getGiocatore().getCognome() : "";
            String nomeSquadra = (s.getGiocatore() != null && s.getGiocatore().getSquadra() != null)
                    ? s.getGiocatore().getSquadra().getNome() : "";
            String giocId = (s.getGiocatore() != null) ? s.getGiocatore().getId() : "";
            statisticheTableModel.addRow(new Object[]{
                    s.getId(),
                    nomeGiocatore,
                    nomeSquadra,
                    s.getPunti(),
                    s.getRimbalzi(),
                    s.getAssist(),
                    giocId
            });
        }
    }

    /** Popola il ComboBox dei giocatori. Da chiamare all'apertura. */
    public void populateGiocatoriComboBox(List<Giocatore> giocatori) {
        giocatoreComboBox.removeAllItems();
        for (Giocatore g : giocatori) giocatoreComboBox.addItem(g);
    }

    public Giocatore getGiocatoreSelezionato() { return (Giocatore) giocatoreComboBox.getSelectedItem(); }
    public int       getPunti()                { return (Integer) puntiSpinner.getValue(); }
    public int       getRimbalzi()             { return (Integer) rimbalziSpinner.getValue(); }
    public int       getAssist()               { return (Integer) assistSpinner.getValue(); }

    /**
     * Restituisce l'ID delle statistiche selezionate in tabella.
     * Ritorna -1 se nessuna riga è selezionata (modalità inserimento nuovo).
     */
    public int getSelectedStatisticheId() {
        int row = statisticheTable.getSelectedRow();
        if (row == -1) return -1;
        return (Integer) statisticheTableModel.getValueAt(row, 0);
    }

    /** Svuota il form e deseleziona la riga in tabella. */
    public void clearFormStatistiche() {
        if (giocatoreComboBox.getItemCount() > 0) giocatoreComboBox.setSelectedIndex(0);
        puntiSpinner.setValue(0);
        rimbalziSpinner.setValue(0);
        assistSpinner.setValue(0);
        statisticheTable.clearSelection();
        feedbackStatisticheLabel.setText(" ");
    }

    /** Mostra un messaggio di errore in rosso. */
    public void showErrorStatistiche(String messaggio) {
        feedbackStatisticheLabel.setForeground(Color.RED);
        feedbackStatisticheLabel.setText(messaggio);
    }

    /** Mostra un messaggio di successo in verde. */
    public void showSuccessStatistiche(String messaggio) {
        feedbackStatisticheLabel.setForeground(new Color(0, 140, 0));
        feedbackStatisticheLabel.setText(messaggio);
    }

    public void addNuovoStatisticheListener(ActionListener l)   { nuovoStatisticheButton.addActionListener(l); }
    public void addSalvaStatisticheListener(ActionListener l)   { salvaStatisticheButton.addActionListener(l); }
    public void addEliminaStatisticheListener(ActionListener l) { eliminaStatisticheButton.addActionListener(l); }

    // =========================================================================
    // METODI PUBBLICI PER IL CONTROLLER — TAB LOG OPERAZIONI
    // =========================================================================

    /**
     * Popola la tabella log. Il timestamp è formattato come "dd/MM/yyyy HH:mm:ss".
     * Da chiamare all'apertura e quando l'utente preme "Aggiorna Log".
     */
    public void populateLogTable(List<LogOperazioni> operazioni) {
        logTableModel.setRowCount(0);
        for (LogOperazioni log : operazioni) {
            String adminUser  = (log.getAmministratore() != null)
                    ? log.getAmministratore().getAdminUser() : "";
            String timestamp  = (log.getTimestamp() != null)
                    ? FMT_TIMESTAMP.format(log.getTimestamp()) : "";
            logTableModel.addRow(new Object[]{
                    log.getId(),
                    adminUser,
                    timestamp,
                    log.getAzione(),
                    log.getDettagli()
            });
        }
    }

    /** Permette al Controller di agganciare il refresh del log al bottone "Aggiorna Log". */
    public void addAggiornaLogListener(ActionListener l) { aggiornaLogButton.addActionListener(l); }
}
