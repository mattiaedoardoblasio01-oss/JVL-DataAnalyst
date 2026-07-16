package it.unipv.JVL_DA.project.model;

public class Statistiche {

    private int id;
    private Giocatore giocatore;
    private int punti;
    private int rimbalzi;
    private int assist;

    public Statistiche() {}

    public Statistiche(Giocatore giocatore, int punti, int rimbalzi, int assist) {
        this.giocatore = giocatore;
        this.punti = punti;
        this.rimbalzi = rimbalzi;
        this.assist = assist;
    }

    public Statistiche(int id, Giocatore giocatore, int punti, int rimbalzi, int assist) {
        this.id = id;
        this.giocatore = giocatore;
        this.punti = punti;
        this.rimbalzi = rimbalzi;
        this.assist = assist;
    }

    public int getId() { return id; }
    public Giocatore getGiocatore() { return giocatore; }
    public int getPunti() { return punti; }
    public int getRimbalzi() { return rimbalzi; }
    public int getAssist() { return assist; }

    public void setId(int id) { this.id = id; }
    public void setGiocatore(Giocatore giocatore) { this.giocatore = giocatore; }
    public void setPunti(int punti) { this.punti = punti; }
    public void setRimbalzi(int rimbalzi) { this.rimbalzi = rimbalzi; }
    public void setAssist(int assist) { this.assist = assist; }

    @Override
    public String toString() {
        return "Statistiche{" +
                "id=" + id +
                ", giocatore=" + (giocatore != null ? giocatore.getNome() + " " + giocatore.getCognome() : "null") +
                ", punti=" + punti +
                ", rimbalzi=" + rimbalzi +
                ", assist=" + assist +
                '}';
    }
}