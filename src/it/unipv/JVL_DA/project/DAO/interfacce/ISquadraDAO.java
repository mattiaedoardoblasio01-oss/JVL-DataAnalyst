package it.unipv.JVL_DA.project.DAO.interfacce;

import it.unipv.JVL_DA.project.POJO.Squadra;
import java.sql.SQLException;
import java.util.List;

public interface ISquadraDAO {
    // insert
    boolean insert(Squadra squadra) throws SQLException;

    /* metodi base */
    Squadra findById(String id) throws SQLException;
    Squadra findByNome(String nome) throws SQLException;
    Squadra findBySede(String sede) throws SQLException;
    Squadra findByAllenatore(String allenatore) throws SQLException;
    List<Squadra> findAll() throws SQLException;

    /* update e delete */
    boolean update(Squadra squadra) throws SQLException;
    boolean delete(String id) throws SQLException;
}
