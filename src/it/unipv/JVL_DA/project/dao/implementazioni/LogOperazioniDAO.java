package it.unipv.JVL_DA.project.dao.implementazioni;

import it.unipv.JVL_DA.project.dao.interfacce.ILogOperazioniDAO;
import it.unipv.JVL_DA.project.model.Amministratore;
import it.unipv.JVL_DA.project.model.LogOperazioni;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogOperazioniDAO implements ILogOperazioniDAO {

    private final AmministratoreDAO amministratoreDAO = new AmministratoreDAO();

    @Override
    public boolean insert(LogOperazioni log) throws SQLException {
        // timestamp non viene passato — lo gestisce il DB con CURRENT_TIMESTAMP
        String sql = "INSERT INTO log_operazioni (admin_id, azione, dettagli) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, log.getAmministratore().getId());
            stmt.setString(2, log.getAzione());
            stmt.setString(3, log.getDettagli());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public LogOperazioni findById(int id) throws SQLException {
        String sql = "SELECT * FROM log_operazioni WHERE id = ?";

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
    public List<LogOperazioni> findByAmministratore(int adminId) throws SQLException {
        String sql = "SELECT * FROM log_operazioni WHERE admin_id = ?";
        List<LogOperazioni> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<LogOperazioni> findByAzione(String azione) throws SQLException {
        String sql = "SELECT * FROM log_operazioni WHERE azione = ?";
        List<LogOperazioni> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, azione);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<LogOperazioni> findAll() throws SQLException {
        String sql = "SELECT * FROM log_operazioni ORDER BY timestamp DESC";
        List<LogOperazioni> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM log_operazioni WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private LogOperazioni mapRow(ResultSet rs) throws SQLException {
        Amministratore amministratore = amministratoreDAO.findById(rs.getInt("admin_id"));
        return new LogOperazioni(
                rs.getInt("id"),
                amministratore,
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getString("azione"),
                rs.getString("dettagli")
        );
    }
}