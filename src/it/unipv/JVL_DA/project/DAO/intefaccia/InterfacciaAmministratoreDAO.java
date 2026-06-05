package it.unipv.JVL_DA.project.DAO.intefaccia;

import it.unipv.JVL_DA.project.POJO.Amministratore;

import java.sql.SQLException;
import java.util.List;

public interface InterfacciaAmministratoreDAO {
    boolean insert(Amministratore amministratore) throws SQLException;

    Amministratore cercaID(int id) throws SQLException;

    Amministratore cercaAdmin_user(String adminUser);

    Amministratore cercaEmail(String email) throws SQLException;

    List<Amministratore> cercaTutti() throws SQLException;

    boolean update(Amministratore amministratore) throws SQLException;

    boolean delete(int id) throws SQLException;

    Amministratore login(String adminUser, String plainPassword) throws SQLException;
}

