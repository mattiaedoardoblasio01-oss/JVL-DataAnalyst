package it.unipv.JVL_DA.project.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminLoginFrame extends JFrame {

    // --- COMPONENTI DELLA VIEW (privati, esposti ai Controller tramite metodi pubblici) ---
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    // --- COSTRUTTORE ---
    public AdminLoginFrame() {
        setTitle("LBA - Accesso Amministratore");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // --- INTESTAZIONE (stesso stile di AdminDashboard) ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JLabel headerLabel = new JLabel("LEGA BASKET - AREA RISERVATA");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- FORM CENTRALE ---
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        // Campo Username
        gbc.gridx = 1;
        usernameField = new JTextField(18);
        formPanel.add(usernameField, gbc);

        // Label Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        // Campo Password
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        formPanel.add(passwordField, gbc);

        // Label errore (vuota di default, valorizzata dal Controller in caso di login fallito)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(errorLabel, gbc);

        // Bottone Login
        gbc.gridy = 3;
        loginButton = new JButton("Accedi");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton, gbc);

        return formPanel;
    }

    // --- FOOTER ---
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(230, 235, 242));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel footerLabel = new JLabel("© Lega Basket Amministrazione — Accesso consentito solo agli amministratori");
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    // -------------------------------------------------------------------------
    // METODI PUBBLICI PER IL CONTROLLER
    // Il Controller legge i valori inseriti e aggancia la sua logica al bottone
    // senza mai dover modificare questa classe.
    // -------------------------------------------------------------------------

    /** Restituisce il testo digitato nel campo username. */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /** Restituisce la password digitata (come char[] per non tenerla in memoria come String). */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Permette al Controller di agganciare la propria logica al bottone "Accedi".
     * Uso: loginForm.addLoginListener(e -> controller.eseguiLogin());
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /**
     * Mostra un messaggio di errore sotto i campi (es. "Credenziali non valide.").
     * Chiamato dal Controller quando il login fallisce.
     */
    public void showError(String messaggio) {
        errorLabel.setText(messaggio);
    }

    /** Pulisce il messaggio di errore e svuota il campo password dopo un login fallito. */
    public void resetForm() {
        errorLabel.setText("");
        passwordField.setText("");
        passwordField.requestFocus();
    }
}