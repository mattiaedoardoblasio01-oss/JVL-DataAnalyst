package it.unipv.JVL_DA.project.model;

public class TabellonePO {
    private Partita partita;
    private String turno;
    private int serieN; // numero di gara nella serie

    public TabellonePO() {}

    public TabellonePO(Partita partita, String turno, int serieN) {
        this.partita = partita;
        this.turno = turno;
        this.serieN = serieN;
    }

    public Partita getPartita() { return partita; }
    public String getTurno() { return turno; }
    public int getSerieN() { return serieN; }

    public void setPartita(Partita partita) { this.partita = partita; }
    public void setTurno(String turno) { this.turno = turno; }
    public void setSerieN(int serieN) { this.serieN = serieN; }

    @Override
    public String toString() {
        return "TabellonePO{" +
                "partita=" + partita.getId() +
                ", turno='" + turno + '\'' +
                ", serieN=" + serieN +
                '}';
    }
}
