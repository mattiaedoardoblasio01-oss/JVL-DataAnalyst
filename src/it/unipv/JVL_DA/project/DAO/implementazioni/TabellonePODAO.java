package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.interfacce.ITabellonePODAO;
import it.unipv.JVL_DA.project.POJO.Partita;
import it.unipv.JVL_DA.project.POJO.TabellonePO;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TabellonePODAO implements ITabellonePODAO {

    private final PartitaDAO partitaDAO = new PartitaDAO();

    @Override
    public boolean insert(TabellonePO tabellonePO) throws SQLException {
        String sql = "INSERT INTO tabellone_po (partita_id, turno, serie_n) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, tabellonePO.getPartita().getId());
            stmt.setString(2, tabellonePO.getTurno());
            stmt.setInt(3, tabellonePO.getSerieN());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public TabellonePO findByPartita(int partitaId) throws SQLException {
        String sql = "SELECT * FROM tabellone_po WHERE partita_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, partitaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<TabellonePO> findByTurno(String turno) throws SQLException {
        String sql = "SELECT * FROM tabellone_po WHERE turno = ?";
        List<TabellonePO> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, turno);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<TabellonePO> findAll() throws SQLException {
        String sql = "SELECT * FROM tabellone_po";
        List<TabellonePO> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<TabellonePO> findBySerie(int serieN) throws SQLException {
        String sql = "SELECT * FROM tabellone_po WHERE serie_n = ?";
        List<TabellonePO> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, serieN);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(TabellonePO tabellonePO) throws SQLException {
        String sql = "UPDATE tabellone_po SET turno = ?, serie_n = ? WHERE partita_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, tabellonePO.getTurno());
            stmt.setInt(2, tabellonePO.getSerieN());
            stmt.setInt(3, tabellonePO.getPartita().getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int partitaId) throws SQLException {
        String sql = "DELETE FROM tabellone_po WHERE partita_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, partitaId);
            return stmt.executeUpdate() > 0;
        }
    }

    private TabellonePO mapRow(ResultSet rs) throws SQLException {
        Partita partita = partitaDAO.findById(rs.getInt("partita_id"));
        return new TabellonePO(
                partita,
                rs.getString("turno"),
                rs.getInt("serie_n")
        );
    }
}