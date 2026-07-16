package it.unipv.JVL_DA.project.view.campionato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * View di sola consultazione: mostra in due schede la classifica della
 * Regular Season e il tabellone dei Playoff. Come le altre view, espone i
 * propri DefaultTableModel così il Controller (CalendarioController) può
 * riempirli con le righe calcolate, senza logica di business nella View.
 */
public class ClassificaFrame extends JFrame {

    private JTable tabellaClassifica;
    private DefaultTableModel tableModel;

    private JTable tabellaPlayoff;
    private DefaultTableModel playoffTableModel;

    public ClassificaFrame(String nomeCampionato) {
        setTitle("LBA - Classifica & Playoff"
                + (nomeCampionato != null ? " (" + nomeCampionato + ")" : ""));
        setSize(620, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Intestazione
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 51, 102));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        JLabel titolo = new JLabel("REGULAR SEASON & PLAYOFF");
        titolo.setForeground(Color.WHITE);
        titolo.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(titolo);
        add(header, BorderLayout.NORTH);

        // Schede: Classifica RS + Tabellone Playoff
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Classifica Regular Season", creaPannelloClassifica());
        tabs.addTab("Tabellone Playoff", creaPannelloPlayoff());
        add(tabs, BorderLayout.CENTER);
    }

    // Tabella classifica (read-only):
    // posizione, squadra, giocate, vittorie, sconfitte, differenza canestri
    private JScrollPane creaPannelloClassifica() {
        String[] colonne = {"Pos.", "Squadra", "Giocate", "Vittorie", "Sconfitte", "Diff. Canestri"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaClassifica = new JTable(tableModel);
        tabellaClassifica.setRowHeight(24);
        tabellaClassifica.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(tabellaClassifica);
    }

    // Tabella tabellone playoff (read-only):
    // fase, gara, casa, ospite, risultato, stato
    private JScrollPane creaPannelloPlayoff() {
        String[] colonne = {"Fase", "Gara", "Casa", "Ospite", "Risultato", "Stato"};
        playoffTableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaPlayoff = new JTable(playoffTableModel);
        tabellaPlayoff.setRowHeight(24);
        tabellaPlayoff.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(tabellaPlayoff);
    }

    /** Espone il model della classifica RS così il Controller può riempirlo. */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /** Espone il model del tabellone Playoff così il Controller può riempirlo. */
    public DefaultTableModel getPlayoffTableModel() {
        return playoffTableModel;
    }
}