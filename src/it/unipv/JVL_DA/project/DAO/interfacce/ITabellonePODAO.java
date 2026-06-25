package it.unipv.JVL_DA.project.DAO.interfacce;

import it.unipv.JVL_DA.project.POJO.TabellonePO;
import java.sql.SQLException;
import java.util.List;

    public interface ITabellonePODAO {

        boolean insert(TabellonePO tabellonePO) throws SQLException;
        TabellonePO findByPartita(int partitaId) throws SQLException;
        List<TabellonePO> findByTurno(String turno) throws SQLException;
        List<TabellonePO> findAll() throws SQLException;
        List<TabellonePO> findBySerie(int serieN) throws SQLException; // per trovare il numero di gara nella serie
        boolean update(TabellonePO tabellonePO) throws SQLException;
        boolean delete(int partitaId) throws SQLException;
    }
