package it.unipv.JVL_DA.project.test;

import it.unipv.JVL_DA.project.util.PasswordUtil;

/**
 * Test UNIT (puro, senza DB) di PasswordUtil.
 *
 * Stile coerente con TestDBConnector: nessun framework, un main() che stampa
 * ✔/✘ per ogni controllo e un riepilogo finale.
 *
 * Cosa verifica:
 *   1. hash() non restituisce mai la password in chiaro (deve essere un hash BCrypt).
 *   2. verify() accetta la password corretta.
 *   3. verify() rifiuta una password errata.
 *   4. Due hash della stessa password sono diversi (salt casuale), ma entrambi validi.
 *   5. Robustezza: password vuota gestita senza eccezioni.
 *
 * Nota MVC: PasswordUtil è una classe di util del Model; qui non tocchiamo
 * né View né Controller.
 */
public class PasswordUtilTest {

    private static int passati = 0;
    private static int falliti = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST PasswordUtil");
        System.out.println("========================================\n");

        final String password = "Str0ngPass!";

        // ── Test 1: l'hash non è la password in chiaro ───────────────
        String hash = PasswordUtil.hash(password);
        check("hash() non restituisce la password in chiaro",
                hash != null && !hash.equals(password));
        check("hash() ha il formato BCrypt (prefisso $2a/$2b/$2y)",
                hash != null && hash.matches("^\\$2[aby]\\$.*"));

        // ── Test 2: verify() accetta la password corretta ────────────
        check("verify() accetta la password corretta",
                PasswordUtil.verify(password, hash));

        // ── Test 3: verify() rifiuta la password errata ──────────────
        check("verify() rifiuta una password errata",
                !PasswordUtil.verify("passwordSbagliata", hash));

        // ── Test 4: hash diversi per la stessa password, entrambi validi ─
        String hash2 = PasswordUtil.hash(password);
        check("due hash della stessa password sono diversi (salt casuale)",
                !hash.equals(hash2));
        check("anche il secondo hash è verificabile",
                PasswordUtil.verify(password, hash2));

        // ── Test 5: password vuota gestita senza eccezioni ───────────
        try {
            String hashVuota = PasswordUtil.hash("");
            check("hash/verify di password vuota non lancia eccezioni",
                    PasswordUtil.verify("", hashVuota));
        } catch (Exception e) {
            check("hash/verify di password vuota non lancia eccezioni", false);
        }

        stampaRiepilogo();
    }

    // ── SUPPORTO ─────────────────────────────────────────────────────
    private static void check(String descrizione, boolean condizione) {
        if (condizione) {
            System.out.println("  ✔ " + descrizione);
            passati++;
        } else {
            System.out.println("  ✘ " + descrizione);
            falliti++;
        }
    }

    private static void stampaRiepilogo() {
        System.out.println("\n----------------------------------------");
        System.out.println("  Passati: " + passati + " | Falliti: " + falliti);
        System.out.println(falliti == 0 ? "  ESITO: TUTTI I TEST OK" : "  ESITO: CI SONO FALLIMENTI");
        System.out.println("----------------------------------------");
    }
}