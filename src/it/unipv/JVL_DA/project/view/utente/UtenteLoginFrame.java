package it.unipv.JVL_DA.project.view.utente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UtenteLoginFrame extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    private JTextField emailField;         // Cambiato da usernameField
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;        // Nuovo bottone aggiunto
    private JLabel errorLabel;

    // --- COSTRUTTORE ---
    public UtenteLoginFrame() {
        setTitle("LBA - Accesso Utente");  // Titolo aggiornato
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // --- INTESTAZIONE ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 51)); // Colore diverso per distinguere dall'admin (verde scuro)
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JLabel headerLabel = new JLabel("LEGA BASKET - AREA TIFOSI"); // Testo aggiornato
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

        // Label Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc); // Cambiato da Username

        // Campo Email
        gbc.gridx = 1;
        emailField = new JTextField(18);
        formPanel.add(emailField, gbc);

        // Label Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        // Campo Password
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        formPanel.add(passwordField, gbc);

        // Label errore
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(errorLabel, gbc);

        // Pannello per i Bottoni (li mettiamo sulla stessa riga)
        gbc.gridy = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        loginButton = new JButton("Accedi");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setFocusPainted(false);

        registerButton = new JButton("Registrati");
        registerButton.setBackground(new Color(240, 240, 240));
        registerButton.setForeground(new Color(50, 50, 50));
        registerButton.setFont(new Font("Arial", Font.BOLD, 13));
        registerButton.setFocusPainted(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    // --- FOOTER ---
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(230, 235, 242));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel footerLabel = new JLabel("© Lega Basket — Segui la tua squadra del cuore"); // Testo aggiornato
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    // -------------------------------------------------------------------------
    // METODI PUBBLICI PER IL CONTROLLER
    // -------------------------------------------------------------------------

    /** Restituisce l'email digitata. */
    public String getEmail() {
        return emailField.getText().trim();
    }

    /** Restituisce la password digitata come char[]. */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /** Listener per il bottone di Login */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /** Listener per il bottone di Registrazione */
    public void addRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    /** Mostra un messaggio di errore */
    public void showError(String messaggio) {
        errorLabel.setText(messaggio);
    }

    /** Pulisce il form */
    public void resetForm() {
        errorLabel.setText("");
        passwordField.setText("");
        passwordField.requestFocus();
    }
}