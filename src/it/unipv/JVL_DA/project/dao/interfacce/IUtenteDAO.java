package it.unipv.JVL_DA.project.dao.interfacce;

import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Utente;

import java.sql.SQLException;
import java.util.List;

public interface IUtenteDAO {

    // CRUD base
    boolean insert(Utente utente) throws SQLException;
    Utente findById(int id) throws SQLException;
    Utente findByUsername(String username) throws SQLException;
    Utente findByEmail(String email) throws SQLException;
    List<Utente> findAll() throws SQLException;
    boolean update(Utente utente) throws SQLException;
    boolean delete(int id) throws SQLException;

    // Login
    Utente login(String username, String password) throws SQLException;

    // Preferenze giocatori
    boolean aggiungiGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException;
    boolean rimuoviGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException;
    List<Giocatore> getGiocatoriPreferiti(int utenteId) throws SQLException;

    // Preferenze squadre
    boolean aggiungiSquadraPreferita(int utenteId, String squadraId) throws SQLException;
    boolean rimuoviSquadraPreferita(int utenteId, String squadraId) throws SQLException;
    List<Squadra> getSquadrePreferite(int utenteId) throws SQLException;
}