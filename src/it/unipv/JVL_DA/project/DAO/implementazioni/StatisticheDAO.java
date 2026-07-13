package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.interfacce.IStatisticheDAO;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.Statistiche;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticheDAO implements IStatisticheDAO {

    private final GiocatoreDAO giocatoreDAO = new GiocatoreDAO();

    @Override
    public boolean insert(Statistiche statistiche) throws SQLException {
        String sql = "INSERT INTO statistiche (gioc_id, punti, rimbalzi, assist) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, statistiche.getGiocatore().getId());
            stmt.setInt(2, statistiche.getPunti());
            stmt.setInt(3, statistiche.getRimbalzi());
            stmt.setInt(4, statistiche.getAssist());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Statistiche findById(int id) throws SQLException {
        String sql = "SELECT * FROM statistiche WHERE id = ?";

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
    public Statistiche findByGiocatore(String giocId) throws SQLException {
        String sql = "SELECT * FROM statistiche WHERE gioc_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, giocId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Statistiche> findBySquadra(String squadraId) throws SQLException {
        String sql = "SELECT s.* FROM statistiche s " +
                "JOIN giocatori g ON s.gioc_id = g.id " +
                "WHERE g.squadra_id = ?";
        List<Statistiche> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, squadraId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Statistiche> findAll() throws SQLException {
        String sql = "SELECT * FROM statistiche";
        List<Statistiche> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Statistiche statistiche) throws SQLException {
        String sql = "UPDATE statistiche SET punti = ?, rimbalzi = ?, assist = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, statistiche.getPunti());
            stmt.setInt(2, statistiche.getRimbalzi());
            stmt.setInt(3, statistiche.getAssist());
            stmt.setInt(4, statistiche.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM statistiche WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Statistiche mapRow(ResultSet rs) throws SQLException {
        Giocatore giocatore = giocatoreDAO.findById(rs.getString("gioc_id"));
        return new Statistiche(
                rs.getInt("id"),
                giocatore,
                rs.getInt("punti"),
                rs.getInt("rimbalzi"),
                rs.getInt("assist")
        );
    }
}