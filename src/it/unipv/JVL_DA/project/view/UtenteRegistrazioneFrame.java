package it.unipv.JVL_DA.project.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class UtenteRegistrazioneFrame extends JFrame {

    // --- COMPONENTI DELLA VIEW ---
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;

    // --- COSTRUTTORE ---
    public UtenteRegistrazioneFrame() {
        setTitle("LBA - Registrazione Nuovo Tifoso");
        setSize(450, 420);
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
        headerPanel.setBackground(new Color(0, 102, 51)); // Stesso verde del login utente
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JLabel headerLabel = new JLabel("CREA IL TUO ACCOUNT");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        return headerPanel;
    }

    // --- FORM CENTRALE ---
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(18);
        formPanel.add(emailField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(18);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        formPanel.add(passwordField, gbc);

        // Conferma Password
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Conferma Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(18);
        formPanel.add(confirmPasswordField, gbc);

        // Label Messaggi (Errori/Successo)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(messageLabel, gbc);

        // Pannello Bottoni
        gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        backButton = new JButton("Indietro");
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setForeground(new Color(50, 50, 50));
        backButton.setFocusPainted(false);

        registerButton = new JButton("Registrati");
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 13));
        registerButton.setFocusPainted(false);

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    // --- FOOTER ---
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(230, 235, 242));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel footerLabel = new JLabel("© Lega Basket — Unisciti alla community");
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    // -------------------------------------------------------------------------
    // METODI PUBBLICI PER IL CONTROLLER
    // -------------------------------------------------------------------------

    public String getEmail() { return emailField.getText().trim(); }
    public String getUsername() { return usernameField.getText().trim(); }
    public char[] getPassword() { return passwordField.getPassword(); }
    public char[] getConfirmPassword() { return confirmPasswordField.getPassword(); }

    public void addRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void showMessage(String text, boolean isError) {
        messageLabel.setForeground(isError ? Color.RED : new Color(0, 153, 0));
        messageLabel.setText(text);
    }
}