package it.unipv.JVL_DA.project.dao.interfacce;

import it.unipv.JVL_DA.project.model.Campionato;

import java.sql.SQLException;
import java.util.List;

public interface ICampionatoDAO {
    /* creare un nuovo campionato quando l'admin inizia una nuova stagione sportiva*/
    boolean insert(Campionato campionato) throws SQLException;
    int insertAndGetId(Campionato campionato) throws SQLException;
    /*metodi di base*/
    Campionato findById(int id) throws SQLException;

    Campionato findByNome(String nome) throws SQLException;

    Campionato findByAnno(int anno) throws SQLException;

    Campionato findByStato(String stato) throws SQLException;

    List<Campionato> findAll() throws SQLException;

    /*Update e delete*/
    boolean update(Campionato campionato) throws SQLException;
    boolean delete(int id) throws SQLException;
}
