package it.unipv.JVL_DA.project.DAO.interfacce;

import it.unipv.JVL_DA.project.POJO.Partita;

import java.sql.SQLException;
import java.util.List;

public interface IPartitaDAO {
    /*metodi di base*/
    boolean insert(Partita partita) throws SQLException;

    Partita findById(int id) throws SQLException;
    List<Partita> findByCampionato(int campId) throws SQLException;
    List<Partita> findByFase(String fase) throws SQLException;
    List<Partita> findByGiornata(int giornata) throws SQLException;
    List<Partita> findBySquadra(String squadraId) throws SQLException;
    List<Partita> findByStato(String stato) throws SQLException;
    List<Partita> findAll() throws SQLException;

    /*update e delete*/
    boolean update(Partita partita) throws SQLException;
    boolean delete(int id) throws SQLException;
}
