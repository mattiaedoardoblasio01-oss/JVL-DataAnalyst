package it.unipv.JVL_DA.project.service;

import it.unipv.JVL_DA.project.DAO.implementazioni.CampionatoDAO;
import it.unipv.JVL_DA.project.DAO.implementazioni.PartitaDAO;
import it.unipv.JVL_DA.project.model.Campionato;
import it.unipv.JVL_DA.project.model.Partita;
import it.unipv.JVL_DA.project.model.Squadra;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CampionatoService {

    private final CampionatoDAO campionatoDAO;
    private final CalendarioService calendarioService;
    private final PartitaDAO partitaDAO;
    private final PlayoffService playoffService;

    public CampionatoService() {
        this.campionatoDAO = new CampionatoDAO();
        this.calendarioService = new CalendarioService();
        this.partitaDAO = new PartitaDAO();
        this.playoffService = new PlayoffService();
    }

    public Campionato creaCampionato(String nome, int anno, LocalDate dataInizio, LocalDate dataFine) throws SQLException {

        Campionato campionatoAttivo = campionatoDAO.findByStato("Attivo");
        if (campionatoAttivo != null) {
            throw new IllegalStateException("Esiste già un campionato attivo!");
        }

        Campionato campionatoConfig = campionatoDAO.findByStato("Config");
        if (campionatoConfig != null) {
            throw new IllegalStateException("Esiste già un campionato in configurazione!");
        }

        Campionato campionato = new Campionato(
                nome, anno, dataInizio, dataFine, "Config"
        );

        int idGenerato = campionatoDAO.insertAndGetId(campionato);
        campionato.setId(idGenerato);
        return campionato;
    }

    public void avviaRegularSeason(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio) throws SQLException {

        // Verifica che il campionato sia in stato Config
        if (!campionato.getStato().equals("Config")) {
            throw new IllegalStateException("Il campionato deve essere in stato 'Config' per avviare la Regular Season!");
        }

        // Verifica che ci siano esattamente 16 squadre
        if (squadre.size() != 16) {
            throw new IllegalArgumentException("Servono esattamente 16 squadre per avviare la Regular Season!");
        }

        // Genera il calendario
        calendarioService.generaCalendarioSafe(campionato, squadre, dataInizio);

        // Cambia stato a Attivo
        campionato.setStato("Attivo");
        campionatoDAO.update(campionato);
    }

    public void chiudiCampionato(Campionato campionato) throws SQLException {

        // Verifica che il campionato sia in stato Attivo
        if (!campionato.getStato().equals("Attivo")) {
            throw new IllegalStateException("Il campionato deve essere in stato 'Attivo' per essere chiuso!");
        }

        // Verifica che non ci siano partite ancora da giocare per questo campionato
        List<Partita> partite = partitaDAO.findByCampionato(campionato.getId());
        long partiteProgrammate = partite.stream()
                .filter(p -> p.getStato().equals("Programmata"))
                .count();

        if (partiteProgrammate > 0) {
            throw new IllegalStateException("Ci sono ancora " + partiteProgrammate + " partite da giocare!");
        }

        // Cambia stato a Chiuso
        campionato.setStato("Chiuso");
        campionatoDAO.update(campionato);
    }

    public void avviaPlayoff(Campionato campionato, List<Squadra> squadre, LocalDateTime dataInizio) throws SQLException {

        // Verifica che il campionato sia in stato Attivo
        if (!campionato.getStato().equals("Attivo")) {
            throw new IllegalStateException("Il campionato deve essere in stato 'Attivo' per avviare i Playoff!");
        }

        // Verifica che la Regular Season sia completata
        List<Partita> partiteRS = partitaDAO.findByCampionatoAndFase(campionato.getId(), "RS");
        long partiteProgrammate = partiteRS.stream()
                .filter(p -> p.getStato().equals("Programmata"))
                .count();

        if (partiteProgrammate > 0) {
            throw new IllegalStateException("La Regular Season non è ancora completata — ci sono ancora " + partiteProgrammate + " partite da giocare!");
        }

        // Verifica che ci siano esattamente 8 squadre
        if (squadre.size() != 8) {
            throw new IllegalArgumentException("Servono esattamente 8 squadre per avviare i Playoff!");
        }

        // Genera i quarti di finale
        playoffService.generaQuarti(campionato, squadre, dataInizio, 0);
    }

    public Campionato getCampionatoAttivo() throws SQLException {
        Campionato campionato = campionatoDAO.findByStato("Attivo");
        if (campionato == null) {
            campionato = campionatoDAO.findByStato("Config");
        }
        return campionato;
    }

}