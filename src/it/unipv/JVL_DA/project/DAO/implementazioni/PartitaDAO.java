package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.SquadraDAO;
import it.unipv.JVL_DA.project.DAO.interfacce.IPartitaDAO;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.Campionato;
import it.unipv.JVL_DA.project.POJO.Squadra;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartitaDAO implements IPartitaDAO {
    private final CampionatoDAO campionatoDAO = new CampionatoDAO();
    private final SquadraDAO squadraDAO = new SquadraDAO();

    @Override
    public boolean insert(Partita partita) throws SQLException {
        String sql = "INSERT INTO partite (camp_id, fase, giornata, casa_id, ospite_id, data_ora, luogo, score_casa, score_osp, stato) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, partita.getCampionato().getId());
            stmt.setString(2, partita.getFase());
            stmt.setInt(3, partita.getGiornata());
            stmt.setString(4, partita.getCasa().getId());
            stmt.setString(5, partita.getOspite().getId());
            stmt.setTimestamp(6, Timestamp.valueOf(partita.getDataOra()));
            stmt.setString(7, partita.getLuogo());
            stmt.setInt(8, partita.getScoreCasa());
            stmt.setInt(9, partita.getScoreOsp());
            stmt.setString(10, partita.getStato());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Partita findById(int id) throws SQLException {
        String sql = "SELECT * FROM partite WHERE id = ?";

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
    public List<Partita> findByCampionato(int campId) throws SQLException {
        String sql = "SELECT * FROM partite WHERE camp_id = ?";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, campId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Partita> findByFase(String fase) throws SQLException {
        String sql = "SELECT * FROM partite WHERE fase = ?";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, fase);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Partita> findByGiornata(int giornata) throws SQLException {
        String sql = "SELECT * FROM partite WHERE giornata = ?";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, giornata);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Partita> findBySquadra(String squadraId) throws SQLException {
        String sql = "SELECT * FROM partite WHERE casa_id = ? OR ospite_id = ?";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, squadraId);
            stmt.setString(2, squadraId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Partita> findByStato(String stato) throws SQLException {
        String sql = "SELECT * FROM partite WHERE stato = ?";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, stato);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Partita> findAll() throws SQLException {
        String sql = "SELECT * FROM partite";
        List<Partita> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Partita partita) throws SQLException {
        String sql = "UPDATE partite SET camp_id = ?, fase = ?, giornata = ?, casa_id = ?, ospite_id = ?, data_ora = ?, luogo = ?, score_casa = ?, score_osp = ?, stato = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, partita.getCampionato().getId());
            stmt.setString(2, partita.getFase());
            stmt.setInt(3, partita.getGiornata());
            stmt.setString(4, partita.getCasa().getId());
            stmt.setString(5, partita.getOspite().getId());
            stmt.setTimestamp(6, Timestamp.valueOf(partita.getDataOra()));
            stmt.setString(7, partita.getLuogo());
            stmt.setInt(8, partita.getScoreCasa());
            stmt.setInt(9, partita.getScoreOsp());
            stmt.setString(10, partita.getStato());
            stmt.setInt(11, partita.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateScore(int id, int scoreCasa, int scoreOsp) throws SQLException {
        String sql = "UPDATE partite SET score_casa = ?, score_osp = ?, stato = 'Conclusa' WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, scoreCasa);
            stmt.setInt(2, scoreOsp);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM partite WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Partita mapRow(ResultSet rs) throws SQLException {
            Campionato campionato = campionatoDAO.findById(rs.getInt("camp_id"));
            Squadra casa = squadraDAO.findById(rs.getString("casa_id"));
            Squadra ospite = squadraDAO.findById(rs.getString("ospite_id"));

            return new Partita(
                rs.getInt("id"),
                campionato,
                rs.getString("fase"),
                rs.getInt("giornata"),
                casa,
                ospite,
                rs.getTimestamp("data_ora").toLocalDateTime(),
                rs.getString("luogo"),
                rs.getInt("score_casa"),
                rs.getInt("score_osp"),
                rs.getString("stato")
        );
    }
}