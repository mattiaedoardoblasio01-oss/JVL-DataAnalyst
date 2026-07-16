package it.unipv.JVL_DA.project.dao.implementazioni;

import it.unipv.JVL_DA.project.dao.interfacce.ISquadraDAO;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.util.DBConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class SquadraDAO implements ISquadraDAO {

    @Override
    public boolean insert(Squadra squadra) throws SQLException {
        String sql = "INSERT INTO squadre (id, nome, sede, logo_url, allenatore) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, squadra.getId());
            stmt.setString(2, squadra.getNome());
            stmt.setString(3, squadra.getSede());
            stmt.setString(4, squadra.getLogoURL());
            stmt.setString(5, squadra.getAllenatore());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Squadra findById(String id) throws SQLException {
        String sql = "SELECT * FROM squadre WHERE id = ?";

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
    public Squadra findByNome(String nome) throws SQLException {
        String sql = "SELECT * FROM squadre WHERE nome = ?";

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
    public Squadra findBySede(String sede) throws SQLException {
        String sql = "SELECT * FROM squadre WHERE sede = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, sede);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Squadra findByAllenatore(String allenatore) throws SQLException {
        String sql = "SELECT * FROM squadre WHERE allenatore = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, allenatore);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Squadra> findAll() throws SQLException {
        String sql = "SELECT * FROM squadre";
        List<Squadra> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Squadra squadra) throws SQLException {
        String sql = "UPDATE squadre SET nome = ?, sede = ?, logo_url = ?, allenatore = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, squadra.getNome());
            stmt.setString(2, squadra.getSede());
            stmt.setString(3, squadra.getLogoURL());
            stmt.setString(4, squadra.getAllenatore());
            stmt.setString(5, squadra.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM squadre WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Squadra mapRow(ResultSet rs) throws SQLException {
        return new Squadra(
                rs.getString("id"),
                rs.getString("nome"),
                rs.getString("sede"),
                rs.getString("logo_url"),
                rs.getString("allenatore")
        );
    }
}
