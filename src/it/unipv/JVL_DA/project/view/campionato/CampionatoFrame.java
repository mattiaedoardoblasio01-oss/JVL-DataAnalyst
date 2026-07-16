package it.unipv.JVL_DA.project.view.campionato;

import it.unipv.JVL_DA.project.model.Campionato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CampionatoFrame extends JFrame {

    private JTable tabellaCampionati;
    private DefaultTableModel tableModel;

    // Componenti del form
    private JTextField txtId;
    private JTextField txtNome;
    private JSpinner spinAnno;
    private JTextField txtDataInizio;
    private JTextField txtDataFine;
    private JComboBox<String> comboStato;

    // Bottoni per il Controller
    private JButton btnAggiungi;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton btnPulisci;

    // Formatter per le date
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CampionatoFrame() {
        super("Gestione Campionato");
        inizializzaUI();
        pack();
        setLocationRelativeTo(null); // Centra la finestra
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void inizializzaUI() {
        setLayout(new BorderLayout(10, 10));

        // --- NORD: Tabella dei Campionati ---
        String[] colonne = {"ID", "Nome", "Anno", "Data Inizio", "Data Fine", "Stato"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabella in sola lettura
            }
        };
        tabellaCampionati = new JTable(tableModel);
        tabellaCampionati.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabellaCampionati);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        add(scrollPane, BorderLayout.NORTH);

        // --- CENTRO: Form di Inserimento / Dettaglio ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dettagli Campionato"));
        formPanel.setPreferredSize(new Dimension(650, 200));

        formPanel.add(new JLabel(" ID (Autogenerato):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        formPanel.add(txtId);

        formPanel.add(new JLabel(" Nome Campionato:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel(" Anno Sportivo:"));
        spinAnno = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinAnno, "#"); // Rimuove i punti delle migliaia
        spinAnno.setEditor(editor);
        formPanel.add(spinAnno);

        formPanel.add(new JLabel(" Data Inizio (yyyy-MM-dd):"));
        txtDataInizio = new JTextField();
        formPanel.add(txtDataInizio);

        formPanel.add(new JLabel(" Data Fine (yyyy-MM-dd):"));
        txtDataFine = new JTextField();
        formPanel.add(txtDataFine);

        formPanel.add(new JLabel(" Stato:"));
        String[] stati = {"Config", "Attivo", "Chiuso"};
        comboStato = new JComboBox<>(stati);
        formPanel.add(comboStato);

        add(formPanel, BorderLayout.CENTER);

        // --- SUD: Bottoni di Azione ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAggiungi = new JButton("Aggiungi");
        btnModifica = new JButton("Modifica");
        btnElimina = new JButton("Elimina");
        btnPulisci = new JButton("Pulisci Form");

        btnPanel.add(btnAggiungi);
        btnPanel.add(btnModifica);
        btnPanel.add(btnElimina);
        btnPanel.add(btnPulisci);

        add(btnPanel, BorderLayout.SOUTH);

        // --- LISTENER INTERNI (Solo logica View) ---
        // Selezionando una riga della tabella, i dati riempiono in automatico il form
        tabellaCampionati.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabellaCampionati.getSelectedRow() != -1) {
                riempiFormDaTabella();
            }
        });

        // Tasto pulisci gestito internamente dalla View
        btnPulisci.addActionListener(e -> pulisciForm());
    }

    // =========================================================================
    // METODI PUBBLICI PER IL CONTROLLER
    // =========================================================================

    /**
     * Popola la tabella con una lista di oggetti Campionato provenienti dal DB.
     */
    public void popolaTabella(List<Campionato> campionati) {
        tableModel.setRowCount(0); // Svuota la tabella
        for (Campionato c : campionati) {
            Object[] row = {
                    c.getId(),
                    c.getNome(),
                    c.getAnno(),
                    c.getDataInizio(),
                    c.getDataFine(),
                    c.getStato()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Crea un oggetto Campionato leggendo i dati attualmente presenti nel form.
     * Gestisce e solleva eccezioni se i dati non sono validi.
     */
    public Campionato getCampionatoDalForm() throws Exception {
        String nome = txtNome.getText().trim();
        if(nome.isEmpty()) throw new Exception("Il campo Nome non può essere vuoto.");

        int anno = (int) spinAnno.getValue();

        LocalDate dataInizio;
        LocalDate dataFine;
        try {
            dataInizio = LocalDate.parse(txtDataInizio.getText().trim(), dateFormatter);
            dataFine = LocalDate.parse(txtDataFine.getText().trim(), dateFormatter);
        } catch (DateTimeParseException ex) {
            throw new Exception("Formato data non valido. Assicurati di usare il formato yyyy-MM-dd.");
        }

        String stato = (String) comboStato.getSelectedItem();
        String idText = txtId.getText().trim();

        // Se c'è un ID, usa il costruttore completo (Modifica/Select), altrimenti quello senza ID (Inserimento)
        if (!idText.isEmpty()) {
            int id = Integer.parseInt(idText);
            return new Campionato(id, nome, anno, dataInizio, dataFine, stato);
        } else {
            return new Campionato(nome, anno, dataInizio, dataFine, stato);
        }
    }

    /**
     * Restituisce l'ID selezionato nella tabella. Utile per l'eliminazione.
     */
    public int getIdSelezionato() throws Exception {
        int riga = tabellaCampionati.getSelectedRow();
        if (riga == -1) throw new Exception("Seleziona un campionato dalla tabella.");
        return (int) tableModel.getValueAt(riga, 0);
    }

    /**
     * Mostra un popup di errore all'utente.
     */
    public void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra un popup di successo.
     */
    public void mostraSuccesso(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Esposizione dei listener per i bottoni (Il Controller li utilizzerà) ---
    public void addAggiungiListener(ActionListener listener) { btnAggiungi.addActionListener(listener); }
    public void addModificaListener(ActionListener listener) { btnModifica.addActionListener(listener); }
    public void addEliminaListener(ActionListener listener) { btnElimina.addActionListener(listener); }

    // =========================================================================
    // METODI PRIVATI DI SUPPORTO (View)
    // =========================================================================

    private void riempiFormDaTabella() {
        int riga = tabellaCampionati.getSelectedRow();
        txtId.setText(tableModel.getValueAt(riga, 0).toString());
        txtNome.setText(tableModel.getValueAt(riga, 1).toString());
        spinAnno.setValue(tableModel.getValueAt(riga, 2));
        txtDataInizio.setText(tableModel.getValueAt(riga, 3).toString());
        txtDataFine.setText(tableModel.getValueAt(riga, 4).toString());
        comboStato.setSelectedItem(tableModel.getValueAt(riga, 5).toString());
    }

    private void pulisciForm() {
        txtId.setText("");
        txtNome.setText("");
        spinAnno.setValue(LocalDate.now().getYear());
        txtDataInizio.setText("");
        txtDataFine.setText("");
        comboStato.setSelectedIndex(0);
        tabellaCampionati.clearSelection();
    }
}