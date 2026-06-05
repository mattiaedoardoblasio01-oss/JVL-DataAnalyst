package it.unipv.JVL_DA.project.DAO.interfacce;

import it.unipv.JVL_DA.project.POJO.Amministratore;

import java.sql.SQLException;
import java.util.List;

public interface IAmministratoreDAO {

    boolean insert(Amministratore amministratore) throws SQLException;
    Amministratore findById(int id) throws SQLException;
    Amministratore findByUsername(String adminUser) throws SQLException;
    Amministratore findByEmail(String email) throws SQLException;
    List<Amministratore> findAll() throws SQLException;
    boolean update(Amministratore amministratore) throws SQLException;
    boolean delete(int id) throws SQLException;
    Amministratore login(String adminUser, String plainPassword) throws SQLException;
}
