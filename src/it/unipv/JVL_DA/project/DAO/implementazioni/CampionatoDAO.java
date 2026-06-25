package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.interfacce.ICampionatoDAO;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CampionatoDAO implements ICampionatoDAO{

    @Override
    public boolean insert(Campionato campionato) throws SQLException {
        String sql = "INSERT INTO campionati (nome, anno, data_inizio, data_fine, stato) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, campionato.getNome());
            stmt.setInt(2, campionato.getAnno());
            stmt.setDate(3, Date.valueOf(campionato.getDataInizio()));
            stmt.setDate(4, Date.valueOf(campionato.getDataFine()));
            stmt.setString(5, campionato.getStato());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int insertAndGetId(Campionato campionato) throws SQLException {
        String sql = "INSERT INTO campionati (nome, anno, data_inizio, data_fine, stato) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, campionato.getNome());
            stmt.setInt(2, campionato.getAnno());
            stmt.setDate(3, Date.valueOf(campionato.getDataInizio()));
            stmt.setDate(4, Date.valueOf(campionato.getDataFine()));
            stmt.setString(5, campionato.getStato());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Errore nel recupero dell'id generato!");
        }
    }
    @Override
    public Campionato findById(int id) throws SQLException {
        String sql = "SELECT * FROM campionati WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Campionato findByNome(String nome) throws SQLException {
        String sql = "SELECT * FROM campionati WHERE nome = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Campionato findByAnno(int anno) throws SQLException {
        String sql = "SELECT * FROM campionati WHERE anno = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, anno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Campionato findByStato(String stato) throws SQLException {
        String sql = "SELECT * FROM campionati WHERE stato = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, stato);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Campionato> findAll() throws SQLException {
        String sql = "SELECT * FROM campionati";
        List<Campionato> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Campionato campionato) throws SQLException {
        String sql = "UPDATE campionati SET nome = ?, anno = ?, data_inizio = ?, data_fine = ?, stato = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, campionato.getNome());
            stmt.setInt(2, campionato.getAnno());
            stmt.setDate(3, Date.valueOf(campionato.getDataInizio()));
            stmt.setDate(4, Date.valueOf(campionato.getDataFine()));
            stmt.setString(5, campionato.getStato());
            stmt.setInt(6, campionato.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM campionati WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Campionato mapRow(ResultSet rs) throws SQLException {
        return new Campionato(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("anno"),
                rs.getDate("data_inizio").toLocalDate(),
                rs.getDate("data_fine").toLocalDate(),
                rs.getString("stato")
        );
    }
}
