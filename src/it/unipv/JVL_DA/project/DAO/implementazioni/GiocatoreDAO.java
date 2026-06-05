package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.interfacce.IGiocatoreDAO;
import it.unipv.JVL_DA.project.POJO.Giocatore;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiocatoreDAO implements IGiocatoreDAO {

    @Override
    public boolean insert(Giocatore giocatore) throws SQLException {
        String sql = "INSERT INTO giocatori (id, nome, cognome, ruolo, n_maglia, squadra_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, giocatore.getId());
            stmt.setString(2, giocatore.getNome());
            stmt.setString(3, giocatore.getCognome());
            stmt.setString(4, giocatore.getRuolo());
            stmt.setInt(5, giocatore.getNMaglia());
            stmt.setString(6, giocatore.getSquadra().getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Giocatore findById(String id) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Giocatore> findByNome(String nome) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE nome = ?";
        List<Giocatore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Giocatore> findByCognome(String cognome) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE cognome = ?";
        List<Giocatore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cognome);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Giocatore> findByRuolo(String ruolo) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE ruolo = ?";
        List<Giocatore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, ruolo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public Giocatore findByNMaglia(int nMaglia) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE n_maglia = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nMaglia);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Giocatore> findBySquadra(String squadraId) throws SQLException {
        String sql = "SELECT * FROM giocatori WHERE squadra_id = ?";
        List<Giocatore> lista = new ArrayList<>();

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
    public List<Giocatore> findAll() throws SQLException {
        String sql = "SELECT * FROM giocatori";
        List<Giocatore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Giocatore giocatore) throws SQLException {
        String sql = "UPDATE giocatori SET nome = ?, cognome = ?, ruolo = ?, n_maglia = ?, squadra_id = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, giocatore.getNome());
            stmt.setString(2, giocatore.getCognome());
            stmt.setString(3, giocatore.getRuolo());
            stmt.setInt(4, giocatore.getNMaglia());
            stmt.setString(5, giocatore.getSquadra().getId());
            stmt.setString(6, giocatore.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM giocatori WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Giocatore mapRow(ResultSet rs) throws SQLException {
        SquadraDAO squadraDAO = new SquadraDAO();
        Squadra squadra = squadraDAO.findById(rs.getString("squadra_id"));
        return new Giocatore(
                rs.getString("id"),
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("ruolo"),
                rs.getInt("n_maglia"),
                squadra
        );
    }
}