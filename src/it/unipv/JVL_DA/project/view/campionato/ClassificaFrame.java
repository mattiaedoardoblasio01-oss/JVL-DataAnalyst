package it.unipv.JVL_DA.project.view.campionato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * View di sola consultazione della classifica della Regular Season.
 * Come CalendarioFrame, espone il proprio DefaultTableModel così il
 * Controller (CalendarioController) può riempirlo con le righe calcolate.
 */
public class ClassificaFrame extends JFrame {

    private JTable tabella;
    private DefaultTableModel tableModel;

    public ClassificaFrame(String nomeCampionato) {
        setTitle("LBA - Classifica Regular Season"
                + (nomeCampionato != null ? " (" + nomeCampionato + ")" : ""));
        setSize(560, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Intestazione
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 51, 102));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        JLabel titolo = new JLabel("CLASSIFICA REGULAR SEASON");
        titolo.setForeground(Color.WHITE);
        titolo.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(titolo);
        add(header, BorderLayout.NORTH);

        // Tabella (read-only): posizione, squadra, giocate, vittorie, sconfitte, differenza canestri
        String[] colonne = {"Pos.", "Squadra", "Giocate", "Vittorie", "Sconfitte", "Diff. Canestri"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabella = new JTable(tableModel);
        tabella.setRowHeight(24);
        tabella.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabella), BorderLayout.CENTER);
    }

    /** Espone il model così il Controller può riempire le righe della classifica. */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}