package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginForm() {
        // Configurazione della finestra
        setTitle("LBA Gestionale - Login Admin");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo
        setLayout(new BorderLayout());

        // Pannello del Titolo
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 51, 102)); // Blu LBA
        JLabel titleLabel = new JLabel("ACCESSO AMMINISTRATORE LBA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Pannello del Form (Campi Input)
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passField = new JPasswordField();

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        add(formPanel, BorderLayout.CENTER);

        // Pannello del Pulsante
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Accedi al Gestionale");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 51, 102));
        loginButton.setForeground(Color.BLACK); // Colore testo bottone

        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Gestione dell'evento Click (Il "Cervello" del Controller)
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                // Controllo simulato (UC1)
                if (username.equals("admin") && password.equals("LBA2026")) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Accesso consentito!");

                    // Chiude la finestra di login e apre la Dashboard
                    dispose();
                    SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Credenziali errate!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        // Avvia l'applicazione partendo dal Login
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
