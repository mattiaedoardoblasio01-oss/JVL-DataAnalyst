package it.unipv.JVL_DA.project.dao.implementazioni;

import it.unipv.JVL_DA.project.dao.interfacce.IAmministratoreDAO;
import it.unipv.JVL_DA.project.model.Amministratore;
import it.unipv.JVL_DA.project.util.DBConnector;
import it.unipv.JVL_DA.project.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmministratoreDAO implements IAmministratoreDAO {

    @Override
    public boolean insert(Amministratore amministratore) throws SQLException {
        String sql = "INSERT INTO amministratori (admin_user, email, password_hash) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, amministratore.getAdminUser());
            stmt.setString(2, amministratore.getEmail());
            stmt.setString(3, PasswordUtil.hash(amministratore.getPasswordHash()));
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Amministratore findById(int id) throws SQLException {
        String sql = "SELECT * FROM amministratori WHERE id = ?";

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
    public Amministratore findByAdminUser(String adminUser) throws SQLException {
        String sql = "SELECT * FROM amministratori WHERE admin_user = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, adminUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Amministratore findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM amministratori WHERE email = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Amministratore> findAll() throws SQLException {
        String sql = "SELECT * FROM amministratori";
        List<Amministratore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Amministratore amministratore) throws SQLException {
        String sql = "UPDATE amministratori SET admin_user = ?, email = ?, password_hash = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, amministratore.getAdminUser());
            stmt.setString(2, amministratore.getEmail());
            stmt.setString(3, amministratore.getPasswordHash());
            stmt.setInt(4, amministratore.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM amministratori WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Amministratore login(String adminUser, String plainPassword) throws SQLException {
        Amministratore amministratore = findByAdminUser(adminUser);

        if (amministratore != null && PasswordUtil.verify(plainPassword, amministratore.getPasswordHash())) {
            return amministratore;
        }
        return null;
    }

    private Amministratore mapRow(ResultSet rs) throws SQLException {
        return new Amministratore(
                rs.getInt("id"),
                rs.getString("admin_user"),
                rs.getString("email"),
                rs.getString("password_hash")
        );
    }
}
