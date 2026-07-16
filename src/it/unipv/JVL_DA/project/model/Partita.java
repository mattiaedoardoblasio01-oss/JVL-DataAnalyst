package it.unipv.JVL_DA.project.model;

import java.time.LocalDateTime;
public class Partita {
    private int id;
    private Campionato campionato;
    private String fase;
    private int giornata;
    private Squadra casa;
    private Squadra ospite;
    private LocalDateTime dataOra;
    private String luogo;
    private int scoreCasa;
    private int scoreOsp;
    private String stato;

    public Partita() {}

    // Senza ID per INSERT
    public Partita(Campionato campionato, String fase, int giornata, Squadra casa, Squadra ospite, LocalDateTime dataOra, String luogo, int scoreCasa, int scoreOsp, String stato) {
        this.campionato = campionato;
        this.fase = fase;
        this.giornata = giornata;
        this.casa = casa;
        this.ospite = ospite;
        this.dataOra = dataOra;
        this.luogo = luogo;
        this.scoreCasa = scoreCasa;
        this.scoreOsp = scoreOsp;
        this.stato = stato;
    }

    // Con ID per SELECT
    public Partita(int id, Campionato campionato, String fase, int giornata, Squadra casa, Squadra ospite, LocalDateTime dataOra, String luogo, int scoreCasa, int scoreOsp, String stato) {
        this.id = id;
        this.campionato = campionato;
        this.fase = fase;
        this.giornata = giornata;
        this.casa = casa;
        this.ospite = ospite;
        this.dataOra = dataOra;
        this.luogo = luogo;
        this.scoreCasa = scoreCasa;
        this.scoreOsp = scoreOsp;
        this.stato = stato;
    }

    /* Getter e setter*/
    public int getId() { return id; }
    public Campionato getCampionato() { return campionato; }
    public String getFase() { return fase; }
    public int getGiornata() { return giornata; }
    public Squadra getCasa() { return casa; }
    public Squadra getOspite() { return ospite; }
    public LocalDateTime getDataOra() { return dataOra; }
    public String getLuogo() { return luogo; }
    public int getScoreCasa() { return scoreCasa; }
    public int getScoreOsp() { return scoreOsp; }
    public String getStato() { return stato; }

    public void setId(int id) { this.id = id; }
    public void setCampionato(Campionato campionato) { this.campionato = campionato; }
    public void setFase(String fase) { this.fase = fase; }
    public void setGiornata(int giornata) { this.giornata = giornata; }
    public void setCasa(Squadra casa) { this.casa = casa; }
    public void setOspite(Squadra ospite) { this.ospite = ospite; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public void setScoreCasa(int scoreCasa) { this.scoreCasa = scoreCasa; }
    public void setScoreOsp(int scoreOsp) { this.scoreOsp = scoreOsp; }
    public void setStato(String stato) { this.stato = stato; }

    @Override
    public String toString() {
        return "Partita{" +
                "id=" + id +
                ", campionato=" + campionato +
                ", fase='" + fase + '\'' +
                ", giornata=" + giornata +
                ", casa=" + casa +
                ", ospite=" + ospite +
                ", dataOra=" + dataOra +
                ", luogo='" + luogo + '\'' +
                ", scoreCasa=" + scoreCasa +
                ", scoreOsp=" + scoreOsp +
                ", stato='" + stato + '\'' +
                '}';
    }
}
