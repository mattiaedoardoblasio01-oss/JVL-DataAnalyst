package it.unipv.JVL_DA.project.view.giocatori;

import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.Squadra;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GiocatoriFrame extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField ruoloField;
    private JSpinner nMagliaSpinner;
    private JComboBox<Squadra> squadraComboBox;
    private JButton nuovoButton;
    private JButton salvaButton;
    private JButton eliminaButton;
    private JLabel feedbackLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    // --- COSTRUTTORE ---
    public GiocatoriFrame() {
        setTitle("LBA - Gestione Giocatori");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.SOUTH);

        // Listener interno: selezione riga → popola il form (comportamento puramente UI)
        // Colonne: 0=ID(nascosta), 1=Nome, 2=Cognome, 3=Ruolo, 4=N°Maglia, 5=Squadra
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    nomeField.setText((String) tableModel.getValueAt(row, 1));
                    cognomeField.setText((String) tableModel.getValueAt(row, 2));
                    ruoloField.setText((String) tableModel.getValueAt(row, 3));
                    nMagliaSpinner.setValue((Integer) tableModel.getValueAt(row, 4));

                    // Seleziona la squadra corrispondente nel ComboBox confrontando per nome
                    String nomeSquadra = (String) tableModel.getValueAt(row, 5);
                    for (int i = 0; i < squadraComboBox.getItemCount(); i++) {
                        if (squadraComboBox.getItemAt(i).getNome().equals(nomeSquadra)) {
                            squadraComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    feedbackLabel.setText(" ");
                }
            }
        });

        // Listener interno del bottone "Nuovo": svuota il form senza logica di business
        nuovoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel headerLabel = new JLabel("GESTIONE GIOCATORI LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- TABELLA (Read) ---
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Colonna 0 = ID: presente nel modello ma resa invisibile tramite larghezza 0
        String[] columns = {"ID", "Nome", "Cognome", "Ruolo", "N° Maglia", "Squadra"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);

        // Nascondi colonna ID mantenendola nel modello
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(860, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- FORM (Create / Update / Delete) ---
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Scheda Giocatore"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Riga 0: Nome | Cognome (layout 2x2: 5 campi in 3 righe invece di 5)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(18);
        formPanel.add(nomeField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Cognome:"), gbc);
        gbc.gridx = 3;
        cognomeField = new JTextField(18);
        formPanel.add(cognomeField, gbc);

        // Riga 1: Ruolo | N° Maglia
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Ruolo:"), gbc);
        gbc.gridx = 1;
        ruoloField = new JTextField(18);
        formPanel.add(ruoloField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("N° Maglia:"), gbc);
        gbc.gridx = 3;
        // SpinnerNumberModel(valore iniziale, min, max, step)
        nMagliaSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        formPanel.add(nMagliaSpinner, gbc);

        // Riga 2: Squadra (occupa le colonne 1-3 per avere più spazio)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Squadra:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        squadraComboBox = new JComboBox<>();
        // Renderer custom: mostra solo getNome() invece del toString() verboso di Squadra
        squadraComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Squadra) {
                    setText(((Squadra) value).getNome());
                }
                return this;
            }
        });
        formPanel.add(squadraComboBox, gbc);

        // Riga 3: feedback
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(feedbackLabel, gbc);

        // Riga 4: pulsanti
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        nuovoButton = new JButton("  Nuovo");
        nuovoButton.setBackground(new Color(85, 85, 85));
        nuovoButton.setForeground(Color.WHITE);
        nuovoButton.setFocusPainted(false);

        salvaButton = new JButton("  Salva");
        salvaButton.setBackground(new Color(0, 102, 204));
        salvaButton.setForeground(Color.WHITE);
        salvaButton.setFocusPainted(false);

        eliminaButton = new JButton("  Elimina");
        eliminaButton.setBackground(new Color(185, 30, 30));
        eliminaButton.setForeground(Color.WHITE);
        eliminaButton.setFocusPainted(false);

        buttonPanel.add(nuovoButton);
        buttonPanel.add(salvaButton);
        buttonPanel.add(eliminaButton);
        formPanel.add(buttonPanel, gbc);

        outerPanel.add(formPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    // -------------------------------------------------------------------------
    // METODI PUBBLICI PER IL CONTROLLER
    // -------------------------------------------------------------------------

    /**
     * Popola la tabella con l'elenco giocatori fornito dal Controller.
     * Da chiamare all'apertura della finestra e dopo ogni operazione CRUD.
     * Gestisce il caso in cui squadra sia null.
     */
    public void populateTable(List<Giocatore> giocatori) {
        tableModel.setRowCount(0);
        for (Giocatore g : giocatori) {
            String nomeSquadra = (g.getSquadra() != null) ? g.getSquadra().getNome() : "";
            tableModel.addRow(new Object[]{
                    g.getId(),
                    g.getNome(),
                    g.getCognome(),
                    g.getRuolo(),
                    g.getNMaglia(),
                    nomeSquadra
            });
        }
    }

    /**
     * Popola il ComboBox con le squadre disponibili.
     * Da chiamare all'apertura della finestra tramite SquadraDAO.findAll().
     */
    public void populateSquadreComboBox(List<Squadra> squadre) {
        squadraComboBox.removeAllItems();
        for (Squadra s : squadre) {
            squadraComboBox.addItem(s);
        }
    }

    /** Restituisce il valore del campo Nome. */
    public String getNome() {
        return nomeField.getText().trim();
    }

    /** Restituisce il valore del campo Cognome. */
    public String getCognome() {
        return cognomeField.getText().trim();
    }

    /** Restituisce il valore del campo Ruolo. */
    public String getRuolo() {
        return ruoloField.getText().trim();
    }

    /** Restituisce il numero di maglia selezionato nello Spinner. */
    public int getNumeroMaglia() {
        return (Integer) nMagliaSpinner.getValue();
    }

    /**
     * Restituisce l'oggetto Squadra selezionato nel ComboBox.
     * Il Controller lo usa direttamente per costruire il Giocatore da salvare.
     */
    public Squadra getSelectedSquadra() {
        return (Squadra) squadraComboBox.getSelectedItem();
    }

    /**
     * Restituisce l'ID (String) del giocatore selezionato nella tabella.
     * Ritorna null se nessuna riga è selezionata (modalità inserimento nuovo).
     */
    public String getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return (String) tableModel.getValueAt(row, 0);
    }

    /** Svuota il form e deseleziona la riga in tabella. */
    public void clearForm() {
        nomeField.setText("");
        cognomeField.setText("");
        ruoloField.setText("");
        nMagliaSpinner.setValue(0);
        if (squadraComboBox.getItemCount() > 0) {
            squadraComboBox.setSelectedIndex(0);
        }
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

    public void addNuovoListener(ActionListener listener) {
        nuovoButton.addActionListener(listener);
    }

    public void addSalvaListener(ActionListener listener) {
        salvaButton.addActionListener(listener);
    }

    public void addEliminaListener(ActionListener listener) {
        eliminaButton.addActionListener(listener);
    }
}