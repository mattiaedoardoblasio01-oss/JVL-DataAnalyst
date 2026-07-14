package it.unipv.JVL_DA.project.view.utente;

import it.unipv.JVL_DA.project.POJO.Utente;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UtenteFrame extends JFrame {

    private static final DateTimeFormatter FMT_DATA    = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATAORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // --- COMPONENTI DELLA VIEW ---
    // Col tabella: 0=ID(nascosta), 1=Username, 2=Nome, 3=Cognome, 4=Email,
    //              5=Provincia, 6=Data Nascita, 7=Creato il,
    //              8=Indirizzo(nascosta), 9=CAP(nascosta)
    //
    // Le colonne 8 e 9 sono nascoste: servono al listener di selezione per
    // popolare il form completo senza richiedere ulteriori query al Controller.
    private JTable            table;
    private DefaultTableModel tableModel;

    private JTextField     usernameField;
    private JTextField     nomeField;
    private JTextField     cognomeField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JTextField     indirizzoField;
    private JTextField     capField;
    private JTextField     provinciaField;
    private JTextField     dataNascitaField;
    private JButton        nuovoButton;
    private JButton        salvaButton;
    private JButton        eliminaButton;
    private JLabel         feedbackLabel;

    // --- COSTRUTTORE ---
    public UtenteFrame() {
        setTitle("LBA - Gestione Utenti");
        setSize(920, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(),  BorderLayout.CENTER);
        add(createFormPanel(),   BorderLayout.SOUTH);

        // Listener interno: selezione riga → popola il form (comportamento puramente UI)
        // La password NON viene mai pre-popolata per ragioni di sicurezza:
        // il Controller la aggiorna solo se getPassword() restituisce un array non vuoto.
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    usernameField.setText((String)    tableModel.getValueAt(row, 1));
                    nomeField.setText((String)         tableModel.getValueAt(row, 2));
                    cognomeField.setText((String)      tableModel.getValueAt(row, 3));
                    emailField.setText((String)        tableModel.getValueAt(row, 4));
                    provinciaField.setText((String)    tableModel.getValueAt(row, 5));
                    dataNascitaField.setText((String)  tableModel.getValueAt(row, 6));
                    // Col 7 (Creato il): sola lettura, non va nel form
                    indirizzoField.setText((String)    tableModel.getValueAt(row, 8));
                    capField.setText((String)          tableModel.getValueAt(row, 9));
                    passwordField.setText("");   // mai pre-popolata
                    feedbackLabel.setText(" ");
                }
            }
        });

        // Listener interno del bottone "Nuovo"
        nuovoButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { clearForm(); }
        });
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        JLabel headerLabel = new JLabel("GESTIONE UTENTI LBA");
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
                "ID", "Username", "Nome", "Cognome", "Email",
                "Provincia", "Data Nascita", "Creato il",
                "Indirizzo", "CAP"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);

        // Nascondi colonna ID (0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Nascondi colonna Indirizzo (8): nel form ma non in tabella
        table.getColumnModel().getColumn(8).setMinWidth(0);
        table.getColumnModel().getColumn(8).setMaxWidth(0);
        table.getColumnModel().getColumn(8).setPreferredWidth(0);

        // Nascondi colonna CAP (9): nel form ma non in tabella
        table.getColumnModel().getColumn(9).setMinWidth(0);
        table.getColumnModel().getColumn(9).setMaxWidth(0);
        table.getColumnModel().getColumn(9).setPreferredWidth(0);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- FORM (Create / Update / Delete) ---
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Scheda Utente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Riga 0: Username | Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(18);
        formPanel.add(usernameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 3;
        nomeField = new JTextField(18);
        formPanel.add(nomeField, gbc);

        // Riga 1: Cognome | Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeField = new JTextField(18);
        formPanel.add(cognomeField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        emailField = new JTextField(18);
        formPanel.add(emailField, gbc);

        // Riga 2: Password | Data Nascita
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        formPanel.add(passwordField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Data Nascita (gg/mm/aaaa):"), gbc);
        gbc.gridx = 3;
        dataNascitaField = new JTextField(18);
        formPanel.add(dataNascitaField, gbc);

        // Riga 3: Indirizzo (piena larghezza)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Indirizzo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        indirizzoField = new JTextField(18);
        formPanel.add(indirizzoField, gbc);
        gbc.gridwidth = 1;

        // Riga 4: CAP | Provincia
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("CAP:"), gbc);
        gbc.gridx = 1;
        capField = new JTextField(18);
        formPanel.add(capField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Provincia:"), gbc);
        gbc.gridx = 3;
        provinciaField = new JTextField(18);
        formPanel.add(provinciaField, gbc);

        // Riga 5: Feedback
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(feedbackLabel, gbc);

        // Riga 6: Bottoni
        gbc.gridy = 6;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        nuovoButton   = createButton("  Nuovo",   new Color(85, 85, 85));
        salvaButton   = createButton("  Salva",   new Color(0, 102, 204));
        eliminaButton = createButton("  Elimina", new Color(185, 30, 30));
        bp.add(nuovoButton);
        bp.add(salvaButton);
        bp.add(eliminaButton);
        formPanel.add(bp, gbc);

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

    // =========================================================================
    // METODI PUBBLICI PER IL CONTROLLER
    // =========================================================================

    /**
     * Popola la tabella con la lista utenti fornita dal Controller.
     * Scrive anche le colonne nascoste 8 (indirizzo) e 9 (cap)
     * in modo che la selezione di una riga popoli il form completo.
     * Il campo createdAt è formattato e mostrato in tabella (col 7) ma
     * non esposto come getter perché gestito interamente dal DB.
     */
    public void populateTable(List<Utente> utenti) {
        tableModel.setRowCount(0);
        for (Utente u : utenti) {
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getNome(),
                    u.getCognome(),
                    u.getEmail(),
                    u.getProvincia(),
                    u.getDataNascita()  != null ? FMT_DATA.format(u.getDataNascita())    : "",
                    u.getCreatedAt()    != null ? FMT_DATAORA.format(u.getCreatedAt())   : "",
                    u.getIndirizzo(),
                    u.getCap()
            });
        }
    }

    public String  getUsername()     { return usernameField.getText().trim(); }
    public String  getNome()         { return nomeField.getText().trim(); }
    public String  getCognome()      { return cognomeField.getText().trim(); }
    public String  getEmail()        { return emailField.getText().trim(); }
    public String  getIndirizzo()    { return indirizzoField.getText().trim(); }
    public String  getCap()          { return capField.getText().trim(); }
    public String  getProvincia()    { return provinciaField.getText().trim(); }
    public String  getDataNascita()  { return dataNascitaField.getText().trim(); }

    /**
     * Restituisce la password digitata come char[] (mai come String,
     * per non lasciarla in memoria nel pool JVM).
     * Il Controller aggiorna la password solo se l'array non è vuoto:
     * in fase di modifica, un array vuoto significa "non cambiare la password".
     */
    public char[] getPassword() { return passwordField.getPassword(); }

    /**
     * Restituisce l'ID (Integer) dell'utente selezionato in tabella.
     * Ritorna null se nessuna riga è selezionata (modalità inserimento nuovo).
     */
    public Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return (Integer) tableModel.getValueAt(row, 0);
    }

    /** Svuota tutti i campi del form e deseleziona la riga in tabella. */
    public void clearForm() {
        usernameField.setText("");
        nomeField.setText("");
        cognomeField.setText("");
        emailField.setText("");
        passwordField.setText("");
        indirizzoField.setText("");
        capField.setText("");
        provinciaField.setText("");
        dataNascitaField.setText("");
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

    public void addNuovoListener(ActionListener l)   { nuovoButton.addActionListener(l); }
    public void addSalvaListener(ActionListener l)   { salvaButton.addActionListener(l); }
    public void addEliminaListener(ActionListener l) { eliminaButton.addActionListener(l); }
}