package it.unipv.JVL_DA.project.view.playoff;

import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.Squadra;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlayoffFrame extends JFrame {

    private static final DateTimeFormatter FMT_DATAORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Fasi fisse dei playoff: valori noti a priori, quindi JComboBox invece di JTextField
    private static final String[] FASI_PLAYOFF = {
            "Quarti di Finale", "Semifinale", "Finale"
    };

    // --- COMPONENTI DELLA VIEW ---
    // Col tabella: 0=ID(nascosta), 1=Campionato, 2=Fase, 3=Giornata,
    //              4=Casa, 5=Ospite, 6=Data/Ora, 7=Luogo,
    //              8=Score Casa, 9=Score Osp, 10=Stato
    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<Campionato> campionatoComboBox;
    private JComboBox<String>     faseComboBox;
    private JSpinner              giornataSpinner;
    private JTextField            statoField;
    private JComboBox<Squadra>    casaComboBox;
    private JComboBox<Squadra>    ospiteComboBox;
    private JTextField            dataOraField;
    private JTextField            luogoField;
    private JSpinner              scoreCasaSpinner;
    private JSpinner              scoreOspSpinner;
    private JButton               nuovoButton;
    private JButton               salvaButton;
    private JButton               eliminaButton;
    private JLabel                feedbackLabel;

    // --- COSTRUTTORE ---
    public PlayoffFrame() {
        setTitle("LBA - Tabellone Playoff");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(),  BorderLayout.CENTER);
        add(createFormPanel(),   BorderLayout.SOUTH);

        // Listener interno: selezione riga → popola il form (comportamento puramente UI)
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();

                    // Riseleziona il Campionato nel ComboBox confrontando per nome
                    String nomeCamp = (String) tableModel.getValueAt(row, 1);
                    for (int i = 0; i < campionatoComboBox.getItemCount(); i++) {
                        if (campionatoComboBox.getItemAt(i).getNome().equals(nomeCamp)) {
                            campionatoComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    // La fase è un valore fisso: setSelectedItem trova direttamente la voce
                    faseComboBox.setSelectedItem(tableModel.getValueAt(row, 2));
                    giornataSpinner.setValue((Integer) tableModel.getValueAt(row, 3));

                    // Riseleziona la Squadra Casa confrontando per nome
                    String nomeCasa = (String) tableModel.getValueAt(row, 4);
                    for (int i = 0; i < casaComboBox.getItemCount(); i++) {
                        if (casaComboBox.getItemAt(i).getNome().equals(nomeCasa)) {
                            casaComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Riseleziona la Squadra Ospite confrontando per nome
                    String nomeOspite = (String) tableModel.getValueAt(row, 5);
                    for (int i = 0; i < ospiteComboBox.getItemCount(); i++) {
                        if (ospiteComboBox.getItemAt(i).getNome().equals(nomeOspite)) {
                            ospiteComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    dataOraField.setText((String)   tableModel.getValueAt(row, 6));
                    luogoField.setText((String)     tableModel.getValueAt(row, 7));
                    scoreCasaSpinner.setValue((Integer) tableModel.getValueAt(row, 8));
                    scoreOspSpinner.setValue((Integer)  tableModel.getValueAt(row, 9));
                    statoField.setText((String)     tableModel.getValueAt(row, 10));
                    feedbackLabel.setText(" ");
                }
            }
        });

        // Listener interno del bottone "Nuovo": svuota il form senza logica di business
        nuovoButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { clearForm(); }
        });
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel headerLabel = new JLabel("TABELLONE PLAYOFF LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- TABELLA (Read) ---
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        String[] columns = {
                "ID", "Campionato", "Fase", "Giornata",
                "Casa", "Ospite", "Data/Ora", "Luogo",
                "Score Casa", "Score Osp", "Stato"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Colonna ID nascosta: necessaria per il Controller, invisibile all'utente
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- FORM (Create / Update / Delete) ---
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Scheda Partita Playoff"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Riga 0: Campionato (piena larghezza)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Campionato:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        campionatoComboBox = new JComboBox<>();
        campionatoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Campionato) {
                    setText(((Campionato) value).getNome() + " (" + ((Campionato) value).getAnno() + ")");
                }
                return this;
            }
        });
        formPanel.add(campionatoComboBox, gbc);

        // Riga 1: Fase (piena larghezza) — JComboBox con valori fissi, non campo libero
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Fase:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        faseComboBox = new JComboBox<>(FASI_PLAYOFF);
        formPanel.add(faseComboBox, gbc);

        // Riga 2: Giornata | Stato
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Giornata:"), gbc);
        gbc.gridx = 1;
        giornataSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        formPanel.add(giornataSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Stato:"), gbc);
        gbc.gridx = 3;
        statoField = new JTextField(18);
        formPanel.add(statoField, gbc);

        // Riga 3: Squadra Casa | Squadra Ospite
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Squadra Casa:"), gbc);
        gbc.gridx = 1;
        casaComboBox = new JComboBox<>();
        casaComboBox.setRenderer(createSquadraRenderer());
        formPanel.add(casaComboBox, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Squadra Ospite:"), gbc);
        gbc.gridx = 3;
        ospiteComboBox = new JComboBox<>();
        ospiteComboBox.setRenderer(createSquadraRenderer());
        formPanel.add(ospiteComboBox, gbc);

        // Riga 4: Data/Ora | Luogo
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Data/Ora (gg/mm/aaaa hh:mm):"), gbc);
        gbc.gridx = 1;
        dataOraField = new JTextField(18);
        formPanel.add(dataOraField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Luogo:"), gbc);
        gbc.gridx = 3;
        luogoField = new JTextField(18);
        formPanel.add(luogoField, gbc);

        // Riga 5: Score Casa | Score Ospite
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Score Casa:"), gbc);
        gbc.gridx = 1;
        scoreCasaSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        formPanel.add(scoreCasaSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Score Ospite:"), gbc);
        gbc.gridx = 3;
        scoreOspSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        formPanel.add(scoreOspSpinner, gbc);

        // Riga 6: Feedback
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(feedbackLabel, gbc);

        // Riga 7: Bottoni
        gbc.gridy = 7;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        nuovoButton   = createButton("  Nuovo",   new Color(85, 85, 85));
        salvaButton   = createButton("  Salva",   new Color(0, 102, 204));
        eliminaButton = createButton("  Elimina", new Color(185, 30, 30));

        buttonPanel.add(nuovoButton);
        buttonPanel.add(salvaButton);
        buttonPanel.add(eliminaButton);
        formPanel.add(buttonPanel, gbc);

        outerPanel.add(formPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    private JButton createButton(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setBackground(colore);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private DefaultListCellRenderer createSquadraRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Squadra) setText(((Squadra) value).getNome());
                return this;
            }
        };
    }

    // =========================================================================
    // METODI PUBBLICI PER IL CONTROLLER
    // =========================================================================

    /**
     * Popola la tabella con le partite playoff fornite dal Controller.
     * Da chiamare all'apertura della finestra e dopo ogni operazione CRUD.
     */
    public void populateTable(List<Partita> partite) {
        tableModel.setRowCount(0);
        for (Partita p : partite) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getCampionato() != null ? p.getCampionato().getNome() : "",
                    p.getFase(),
                    p.getGiornata(),
                    p.getCasa()    != null ? p.getCasa().getNome()    : "",
                    p.getOspite()  != null ? p.getOspite().getNome()  : "",
                    p.getDataOra() != null ? FMT_DATAORA.format(p.getDataOra()) : "",
                    p.getLuogo(),
                    p.getScoreCasa(),
                    p.getScoreOsp(),
                    p.getStato()
            });
        }
    }

    /** Popola il ComboBox dei campionati. Da chiamare all'apertura. */
    public void populateCampionatoComboBox(List<Campionato> campionati) {
        campionatoComboBox.removeAllItems();
        for (Campionato c : campionati) campionatoComboBox.addItem(c);
    }

    /** Popola entrambi i ComboBox delle squadre (casa e ospite). Da chiamare all'apertura. */
    public void populateSquadreComboBox(List<Squadra> squadre) {
        casaComboBox.removeAllItems();
        ospiteComboBox.removeAllItems();
        for (Squadra s : squadre) {
            casaComboBox.addItem(s);
            ospiteComboBox.addItem(s);
        }
    }

    public Campionato getCampionatoSelezionato() { return (Campionato) campionatoComboBox.getSelectedItem(); }
    public String     getFase()                  { return (String) faseComboBox.getSelectedItem(); }
    public int        getGiornata()              { return (Integer) giornataSpinner.getValue(); }
    public Squadra    getCasaSelezionata()        { return (Squadra) casaComboBox.getSelectedItem(); }
    public Squadra    getOspiteSelezionata()      { return (Squadra) ospiteComboBox.getSelectedItem(); }
    public String     getDataOra()               { return dataOraField.getText().trim(); }
    public String     getLuogo()                 { return luogoField.getText().trim(); }
    public int        getScoreCasa()             { return (Integer) scoreCasaSpinner.getValue(); }
    public int        getScoreOsp()              { return (Integer) scoreOspSpinner.getValue(); }
    public String     getStatoPartita()          { return statoField.getText().trim(); }

    /**
     * Restituisce l'ID della partita selezionata in tabella.
     * Ritorna -1 se nessuna riga è selezionata (modalità inserimento nuovo).
     */
    public int getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        return (Integer) tableModel.getValueAt(row, 0);
    }

    /** Svuota il form e deseleziona la riga in tabella. */
    public void clearForm() {
        if (campionatoComboBox.getItemCount() > 0) campionatoComboBox.setSelectedIndex(0);
        faseComboBox.setSelectedIndex(0);
        giornataSpinner.setValue(1);
        statoField.setText("");
        if (casaComboBox.getItemCount() > 0)   casaComboBox.setSelectedIndex(0);
        if (ospiteComboBox.getItemCount() > 0) ospiteComboBox.setSelectedIndex(0);
        dataOraField.setText("");
        luogoField.setText("");
        scoreCasaSpinner.setValue(0);
        scoreOspSpinner.setValue(0);
        table.clearSelection();
        feedbackLabel.setText(" ");
    }

    /** Mostra un messaggio di errore in rosso. */
    public void showError(String messaggio) {
        feedbackLabel.setForeground(Color.RED);
        feedbackLabel.setText(messaggio);
    }

    /** Mostra un messaggio di successo in verde. */
    public void showSuccess(String messaggio) {
        feedbackLabel.setForeground(new Color(0, 140, 0));
        feedbackLabel.setText(messaggio);
    }

    // --- AGGANCIO LISTENER DAL CONTROLLER ---

    public void addNuovoListener(ActionListener listener)   { nuovoButton.addActionListener(listener); }
    public void addSalvaListener(ActionListener listener)   { salvaButton.addActionListener(listener); }
    public void addEliminaListener(ActionListener listener) { eliminaButton.addActionListener(listener); }
}