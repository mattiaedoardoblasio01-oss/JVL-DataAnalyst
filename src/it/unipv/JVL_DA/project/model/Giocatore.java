package it.unipv.JVL_DA.project.model;

public class Giocatore extends Persona<String> {

    private String ruolo;
    private int nMaglia;
    private Squadra squadra;

    public Giocatore() {}

    public Giocatore(String id, String nome, String cognome, String ruolo, int nMaglia, Squadra squadra) {
        super(id, nome, cognome);
        this.ruolo = ruolo;
        this.nMaglia = nMaglia;
        this.squadra = squadra;
    }

    public String getRuolo() { return ruolo; }
    public int getNMaglia() { return nMaglia; }
    public Squadra getSquadra() { return squadra; }

    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    public void setNMaglia(int nMaglia) { this.nMaglia = nMaglia; }
    public void setSquadra(Squadra squadra) { this.squadra = squadra; }

    @Override
    public String toString() {
        return "Giocatore{" +
                "id='" + getId() + '\'' +
                ", nome='" + getNome() + '\'' +
                ", cognome='" + getCognome() + '\'' +
                ", ruolo='" + ruolo + '\'' +
                ", nMaglia=" + nMaglia +
                ", squadra=" + squadra +
                '}';
    }
}