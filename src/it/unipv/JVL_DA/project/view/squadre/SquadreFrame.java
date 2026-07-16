package it.unipv.JVL_DA.project.view.squadre;

import it.unipv.JVL_DA.project.model.Squadra;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SquadreFrame extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    private JTextField nomeField;
    private JTextField sedeField;
    private JTextField logoURLField;
    private JTextField allenatoreField;
    private JButton nuovoButton;
    private JButton salvaButton;
    private JButton eliminaButton;
    private JLabel feedbackLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    // --- COSTRUTTORE ---
    public SquadreFrame() {
        setTitle("LBA - Gestione Squadre");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.SOUTH);

        // Listener interno: selezione riga → popola il form (comportamento puramente UI)
        // Colonne: 0=ID(nascosta), 1=Nome, 2=Sede, 3=Logo URL, 4=Allenatore
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    nomeField.setText((String) tableModel.getValueAt(row, 1));
                    sedeField.setText((String) tableModel.getValueAt(row, 2));
                    logoURLField.setText((String) tableModel.getValueAt(row, 3));
                    allenatoreField.setText((String) tableModel.getValueAt(row, 4));
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

        JLabel headerLabel = new JLabel("GESTIONE SQUADRE LBA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- TABELLA (Read) ---
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Colonna 0 = ID: presente nel modello ma resa invisibile
        String[] columns = {"ID", "Nome", "Sede", "Logo URL", "Allenatore"};
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
        scrollPane.setPreferredSize(new Dimension(780, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- FORM (Create / Update / Delete) ---
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Scheda Squadra"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(22);
        formPanel.add(nomeField, gbc);

        // Sede
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Sede:"), gbc);
        gbc.gridx = 1;
        sedeField = new JTextField(22);
        formPanel.add(sedeField, gbc);

        // Logo URL
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Logo URL:"), gbc);
        gbc.gridx = 1;
        logoURLField = new JTextField(22);
        formPanel.add(logoURLField, gbc);

        // Allenatore
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Allenatore:"), gbc);
        gbc.gridx = 1;
        allenatoreField = new JTextField(22);
        formPanel.add(allenatoreField, gbc);

        // Label feedback
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(feedbackLabel, gbc);

        // Pulsanti
        gbc.gridy = 5;
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
     * Popola la tabella con l'elenco squadre fornito dal Controller.
     * Da chiamare all'apertura della finestra e dopo ogni operazione CRUD.
     */
    public void populateTable(List<Squadra> squadre) {
        tableModel.setRowCount(0);
        for (Squadra s : squadre) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getNome(),
                    s.getSede(),
                    s.getLogoURL(),
                    s.getAllenatore()
            });
        }
    }

    /** Restituisce il valore del campo Nome. */
    public String getNome() {
        return nomeField.getText().trim();
    }

    /** Restituisce il valore del campo Sede. */
    public String getSede() {
        return sedeField.getText().trim();
    }

    /** Restituisce il valore del campo Logo URL. */
    public String getLogoURL() {
        return logoURLField.getText().trim();
    }

    /** Restituisce il valore del campo Allenatore. */
    public String getAllenatore() {
        return allenatoreField.getText().trim();
    }

    /**
     * Restituisce l'ID (String) della squadra selezionata nella tabella.
     * Il Controller usa questo valore per distinguere insert (null) da update/delete.
     * Ritorna null se nessuna riga è selezionata.
     */
    public String getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return (String) tableModel.getValueAt(row, 0);
    }

    /** Svuota il form e deseleziona la riga in tabella. */
    public void clearForm() {
        nomeField.setText("");
        sedeField.setText("");
        logoURLField.setText("");
        allenatoreField.setText("");
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