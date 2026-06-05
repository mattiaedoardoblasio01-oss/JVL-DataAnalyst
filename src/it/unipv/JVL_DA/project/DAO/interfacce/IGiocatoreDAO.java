package it.unipv.JVL_DA.project.DAO.interfacce;

import it.unipv.JVL_DA.project.POJO.Giocatore;

import java.sql.SQLException;
import java.util.List;

public interface IGiocatoreDAO {

    boolean insert(Giocatore giocatore) throws SQLException;

    /*metodi base*/
    Giocatore findById(String id) throws SQLException;

    List<Giocatore> findByNome(String nome) throws SQLException;

    List<Giocatore> findByCognome(String cognome) throws SQLException;

    List<Giocatore> findByRuolo(String ruolo) throws SQLException;

    Giocatore findByNMaglia(int nMaglia) throws SQLException;

    List<Giocatore> findBySquadra(String squadraId) throws SQLException;

    List<Giocatore> findAll() throws SQLException;

    /*update e delete*/
    boolean update(Giocatore giocatore) throws SQLException;
    boolean delete(String id) throws SQLException;


}