package it.unipv.JVL_DA.project.DAO.implementazioni;

import it.unipv.JVL_DA.project.DAO.interfacce.IUtenteDAO;
import it.unipv.JVL_DA.project.model.Giocatore;
import it.unipv.JVL_DA.project.model.Squadra;
import it.unipv.JVL_DA.project.model.Utente;
import it.unipv.JVL_DA.project.util.DBConnector;
import it.unipv.JVL_DA.project.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements IUtenteDAO {

    private final GiocatoreDAO giocatoreDAO = new GiocatoreDAO();
    private final SquadraDAO squadraDAO = new SquadraDAO();

    @Override
    public boolean insert(Utente utente) throws SQLException {
        // created_at non viene passato — lo gestisce il DB con CURRENT_TIMESTAMP
        String sql = "INSERT INTO utenti (nome, cognome, username, email, password_hash, " +
                "indirizzo, cap, provincia, data_nascita) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getUsername());
            stmt.setString(4, utente.getEmail());
            stmt.setString(5, utente.getPasswordHash());
            stmt.setString(6, utente.getIndirizzo());
            stmt.setString(7, utente.getCap());
            stmt.setString(8, utente.getProvincia());
            stmt.setDate(9, Date.valueOf(utente.getDataNascita()));
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Utente findById(int id) throws SQLException {
        String sql = "SELECT * FROM utenti WHERE id = ?";

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
    public Utente findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM utenti WHERE username = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Utente findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utenti WHERE email = ?";

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
    public List<Utente> findAll() throws SQLException {
        String sql = "SELECT * FROM utenti";
        List<Utente> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Utente utente) throws SQLException {
        // Non aggiorniamo created_at — è impostato una volta sola alla registrazione
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, username = ?, email = ?, " +
                "indirizzo = ?, cap = ?, provincia = ?, data_nascita = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getUsername());
            stmt.setString(4, utente.getEmail());
            stmt.setString(5, utente.getIndirizzo());
            stmt.setString(6, utente.getCap());
            stmt.setString(7, utente.getProvincia());
            stmt.setDate(8, Date.valueOf(utente.getDataNascita()));
            stmt.setInt(9, utente.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM utenti WHERE id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Utente login(String username, String password) throws SQLException {
        Utente utente = findByUsername(username);
        if (utente != null && PasswordUtil.verify(password, utente.getPasswordHash())) {
            return utente;
        }
        return null;
    }

    // ── Preferenze Giocatori ───────────────────────────────────────────

    @Override
    public boolean aggiungiGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException {
        String sql = "INSERT INTO preferenze_giocatori (utente_id, giocatore_id) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            stmt.setString(2, giocatoreId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean rimuoviGiocatorePreferito(int utenteId, String giocatoreId) throws SQLException {
        String sql = "DELETE FROM preferenze_giocatori WHERE utente_id = ? AND giocatore_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            stmt.setString(2, giocatoreId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Giocatore> getGiocatoriPreferiti(int utenteId) throws SQLException {
        String sql = "SELECT g.* FROM giocatori g " +
                "JOIN preferenze_giocatori pg ON g.id = pg.giocatore_id " +
                "WHERE pg.utente_id = ?";
        List<Giocatore> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(giocatoreDAO.findById(rs.getString("id")));
            }
        }
        return lista;
    }

    // ── Preferenze Squadre ─────────────────────────────────────────────

    @Override
    public boolean aggiungiSquadraPreferita(int utenteId, String squadraId) throws SQLException {
        String sql = "INSERT INTO preferenze_squadre (utente_id, squadra_id) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            stmt.setString(2, squadraId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean rimuoviSquadraPreferita(int utenteId, String squadraId) throws SQLException {
        String sql = "DELETE FROM preferenze_squadre WHERE utente_id = ? AND squadra_id = ?";

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            stmt.setString(2, squadraId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Squadra> getSquadrePreferite(int utenteId) throws SQLException {
        String sql = "SELECT s.* FROM squadre s " +
                "JOIN preferenze_squadre ps ON s.id = ps.squadra_id " +
                "WHERE ps.utente_id = ?";
        List<Squadra> lista = new ArrayList<>();

        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(squadraDAO.findById(rs.getString("id")));
            }
        }
        return lista;
    }

    // ── mapRow ─────────────────────────────────────────────────────────

    private Utente mapRow(ResultSet rs) throws SQLException {
        return new Utente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("indirizzo"),
                rs.getString("cap"),
                rs.getString("provincia"),
                rs.getDate("data_nascita").toLocalDate(),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}