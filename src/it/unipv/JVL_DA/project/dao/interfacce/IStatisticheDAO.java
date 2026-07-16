package it.unipv.JVL_DA.project.dao.interfacce;

import it.unipv.JVL_DA.project.model.Statistiche;

import java.sql.SQLException;
import java.util.List;

public interface IStatisticheDAO {

    boolean insert(Statistiche statistiche) throws SQLException;
    Statistiche findById(int id) throws SQLException;
    Statistiche findByGiocatore(String giocId) throws SQLException;
    List<Statistiche> findBySquadra(String squadraId) throws SQLException;
    List<Statistiche> findAll() throws SQLException;
    boolean update(Statistiche statistiche) throws SQLException;
    boolean delete(int id) throws SQLException;
}