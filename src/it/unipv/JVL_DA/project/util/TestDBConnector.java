package it.unipv.JVL_DA.project.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe di test per verificare il funzionamento di DBConnector.
 * Eseguire con tasto destro → Run 'TestDBConnector.main()'
 */
public class TestDBConnector {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  TEST DBConnector");
        System.out.println("========================================\n");

        // ── Test 1: Apertura connessione ─────────────────────────────
        System.out.println("[ Test 1 ] Apertura connessione...");
        Connection conn1;
        try {
            conn1 = DBConnector.getConnection();
            if (conn1 != null && !conn1.isClosed()) {
                System.out.println("  ✔ Connessione aperta con successo!\n");
            } else {
                System.out.println("  ✘ Connessione nulla o già chiusa.\n");
                return;
            }
        } catch (SQLException e) {
            System.out.println("  ✘ Errore apertura connessione: " + e.getMessage() + "\n");
            return;
        }

        // ── Test 2: Verifica Singleton ────────────────────────────────
        System.out.println("[ Test 2 ] Verifica pattern Singleton...");
        try {
            Connection conn2 = DBConnector.getConnection();
            if (conn1 == conn2) {
                System.out.println("  ✔ Singleton confermato: conn1 == conn2 (stesso oggetto in memoria)\n");
            } else {
                System.out.println("  ✘ Singleton NON funziona: sono due istanze diverse!\n");
            }
        } catch (SQLException e) {
            System.out.println("  ✘ Errore: " + e.getMessage() + "\n");
        }

        // ── Test 3: Esecuzione query di prova ─────────────────────────
        System.out.println("[ Test 3 ] Esecuzione query di prova (SELECT 1)...");
        try {
            Statement stmt = conn1.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            if (rs.next()) {
                System.out.println("  ✔ Query eseguita correttamente. Risultato: " + rs.getInt(1) + "\n");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("  ✘ Errore esecuzione query: " + e.getMessage() + "\n");
        }

        // ── Test 4: Chiusura connessione ──────────────────────────────
        System.out.println("[ Test 4 ] Chiusura connessione...");
        DBConnector.closeConnection();
        try {
            if (conn1.isClosed()) {
                System.out.println("  ✔ Connessione chiusa correttamente.\n");
            } else {
                System.out.println("  ✘ La connessione risulta ancora aperta.\n");
            }
        } catch (SQLException e) {
            System.out.println("  ✘ Errore verifica chiusura: " + e.getMessage() + "\n");
        }

        // ── Test 5: Riapertura dopo chiusura ─────────────────────────
        System.out.println("[ Test 5 ] Riapertura connessione dopo chiusura...");
        try {
            Connection conn3 = DBConnector.getConnection();
            if (conn3 != null && !conn3.isClosed()) {
                System.out.println("  ✔ Riapertura riuscita: il Singleton si ricrea correttamente.\n");
            }
        } catch (SQLException e) {
            System.out.println("  ✘ Errore riapertura: " + e.getMessage() + "\n");
        }

        // ── Riepilogo ─────────────────────────────────────────────────
        System.out.println("========================================");
        System.out.println("  TEST COMPLETATI");
        System.out.println("========================================");

        // Chiusura finale
        DBConnector.closeConnection();
    }
}
