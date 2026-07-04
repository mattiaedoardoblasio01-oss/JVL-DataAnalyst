package it.unipv.JVL_DA.project.view.calendario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CalendarioFrame extends JFrame {

    // Componenti Pannello Superiore (Filtri)
    private JComboBox<String> comboCampionato;
    private JSpinner spinGiornata;
    private JButton btnFiltra;

    // Componenti Pannello Centrale (Tabella)
    private JTable tabellaPartite;
    private DefaultTableModel tableModel;

    // Componenti Pannello Inferiore (Aggiornamento)
    private JTextField txtIdPartita;
    private JSpinner spinScoreCasa;
    private JSpinner spinScoreOspite;
    private JComboBox<String> comboStatoPartita;
    private JButton btnAggiornaRisultato;

    public CalendarioFrame() {
        super("Calendario Regular Season");
        inizializzaUI();
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void inizializzaUI() {
        setLayout(new BorderLayout(10, 10));

        // ==========================================
        // NORD: Area di ricerca e filtro
        // ==========================================
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtra Giornata"));

        filterPanel.add(new JLabel("Campionato:"));
        comboCampionato = new JComboBox<>();
        filterPanel.add(comboCampionato);

        filterPanel.add(new JLabel("Giornata:"));
        spinGiornata = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        filterPanel.add(spinGiornata);

        btnFiltra = new JButton("Carica Partite");
        filterPanel.add(btnFiltra);

        add(filterPanel, BorderLayout.NORTH);

        // ==========================================
        // CENTRO: Tabella riepilogativa
        // ==========================================
        String[] colonne = {"ID Partita", "Data e Ora", "Squadra Casa", "Squadra Ospite", "Punti Casa", "Punti Ospite", "Luogo", "Stato"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende le celle non modificabili direttamente col doppio clic
            }
        };
        tabellaPartite = new JTable(tableModel);
        tabellaPartite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabellaPartite);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // SUD: Area di aggiornamento risultato
        // ==========================================
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Aggiorna Risultato"));

        updatePanel.add(new JLabel("ID Partita:"));
        txtIdPartita = new JTextField(5);
        txtIdPartita.setEditable(false); // L'ID non deve essere modificato a mano
        updatePanel.add(txtIdPartita);

        updatePanel.add(new JLabel("Punti Casa:"));
        spinScoreCasa = new JSpinner(new SpinnerNumberModel(0, 0, 300, 1));
        updatePanel.add(spinScoreCasa);

        updatePanel.add(new JLabel("Punti Ospite:"));
        spinScoreOspite = new JSpinner(new SpinnerNumberModel(0, 0, 300, 1));
        updatePanel.add(spinScoreOspite);

        updatePanel.add(new JLabel("Stato:"));
        String[] stati = {"Programmata", "In Corso", "Terminata", "Rinviata"};
        comboStatoPartita = new JComboBox<>(stati);
        updatePanel.add(comboStatoPartita);

        btnAggiornaRisultato = new JButton("Salva Modifiche");
        updatePanel.add(btnAggiornaRisultato);

        add(updatePanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // GETTER DEI COMPONENTI
    // (Serviranno al Controller per aggiungere i Listener e leggere/scrivere i dati)
    // ==========================================

    public JButton getBtnFiltra() { return btnFiltra; }
    public JButton getBtnAggiornaRisultato() { return btnAggiornaRisultato; }
    public JComboBox<String> getComboCampionato() { return comboCampionato; }
    public JSpinner getSpinGiornata() { return spinGiornata; }
    public JTable getTabellaPartite() { return tabellaPartite; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtIdPartita() { return txtIdPartita; }
    public JSpinner getSpinScoreCasa() { return spinScoreCasa; }
    public JSpinner getSpinScoreOspite() { return spinScoreOspite; }
    public JComboBox<String> getComboStatoPartita() { return comboStatoPartita; }
}
