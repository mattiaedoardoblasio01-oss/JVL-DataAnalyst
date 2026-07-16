package it.unipv.JVL_DA.project.dao.interfacce;

import it.unipv.JVL_DA.project.model.LogOperazioni;

import java.sql.SQLException;
import java.util.List;

public interface ILogOperazioniDAO {

    boolean insert(LogOperazioni log) throws SQLException;
    LogOperazioni findById(int id) throws SQLException;
    List<LogOperazioni> findByAmministratore(int adminId) throws SQLException;
    List<LogOperazioni> findByAzione(String azione) throws SQLException;
    List<LogOperazioni> findAll() throws SQLException;
    boolean delete(int id) throws SQLException;
}