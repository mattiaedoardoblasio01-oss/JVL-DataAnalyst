package it.unipv.JVL_DA.project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    // Parametri di connessione
    private static final String URL = "jdbc:mysql://localhost:3306/jvl";

    // Credenziali d'accesso (Utente: Blaise)
    private static final String USER = "root";
    private static final String PASSWORD = "Mtbl0512";

    // PATTERN: Singleton
    private static Connection connection = null;

    private DBConnector(){} // impedisce istanziazione diretta

    /*
    restituisce la connessione al DB (se non esiste ancora)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Carica il driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Per PostgreSQL: "org.postgresql.Driver"

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✔ Connessione al DB stabilita.");

            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver JDBC non trovato: " + e.getMessage());
            }
        }
        return connection;
    }
    /*
    chiude la connessione in modo sicuro
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✔ Connessione chiusa.");
            } catch (SQLException e) {
                System.err.println("Errore nella chiusura: " + e.getMessage());
            }
        }
    }
}
