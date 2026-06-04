package it.unipv.JVL_DA.project.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Fattore di costo: più è alto, più è lento (e sicuro). 12 è il valore consigliato.
    private static final int COST = 12;

    // Costruttore privato: classe di sola utilità, non va istanziata
    private PasswordUtil() {}

    /**
     * Genera l'hash BCrypt di una password in chiaro.
     * Da usare prima di salvare la password nel DB.
     *
     * @param plainPassword la password in chiaro inserita dall'utente
     * @return il hash BCrypt da salvare nel campo password_hash
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
    }

    /**
     * Verifica che una password in chiaro corrisponda all'hash salvato nel DB.
     * Da usare al momento del login.
     *
     * @param plainPassword  la password inserita dall'utente al login
     * @param hashedPassword il hash salvato nel DB
     * @return true se la password è corretta, false altrimenti
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
