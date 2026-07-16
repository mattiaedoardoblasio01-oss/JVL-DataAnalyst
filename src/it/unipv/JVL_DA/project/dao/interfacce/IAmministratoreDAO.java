package it.unipv.JVL_DA.project.dao.interfacce;

import it.unipv.JVL_DA.project.model.Amministratore;

import java.sql.SQLException;
import java.util.List;

public interface IAmministratoreDAO {
    // insert
    boolean insert(Amministratore amministratore) throws SQLException;

    /* metodi di base*/
    Amministratore findById(int id) throws SQLException;
    Amministratore findByAdminUser(String adminUser) throws SQLException;
    Amministratore findByEmail(String email) throws SQLException;
    List<Amministratore> findAll() throws SQLException;

    /*update e delete */
    boolean update(Amministratore amministratore) throws SQLException;
    boolean delete(int id) throws SQLException;

    Amministratore login(String adminUser, String plainPassword) throws SQLException;
}
