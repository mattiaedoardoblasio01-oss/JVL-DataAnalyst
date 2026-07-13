package it.unipv.JVL_DA.project.POJO;

import java.time.LocalDateTime;

public class LogOperazioni {

    private int id;
    private Amministratore amministratore;
    private LocalDateTime timestamp;
    private String azione;
    private String dettagli;

    public LogOperazioni() {}

    public LogOperazioni(Amministratore amministratore, String azione, String dettagli) {
        this.amministratore = amministratore;
        this.azione = azione;
        this.dettagli = dettagli;
    }

    public LogOperazioni(int id, Amministratore amministratore, LocalDateTime timestamp, String azione, String dettagli) {
        this.id = id;
        this.amministratore = amministratore;
        this.timestamp = timestamp;
        this.azione = azione;
        this.dettagli = dettagli;
    }

    public int getId() { return id; }
    public Amministratore getAmministratore() { return amministratore; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAzione() { return azione; }
    public String getDettagli() { return dettagli; }

    public void setId(int id) { this.id = id; }
    public void setAmministratore(Amministratore amministratore) { this.amministratore = amministratore; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setAzione(String azione) { this.azione = azione; }
    public void setDettagli(String dettagli) { this.dettagli = dettagli; }

    @Override
    public String toString() {
        return "LogOperazioni{" +
                "id=" + id +
                ", amministratore=" + (amministratore != null ? amministratore.getAdminUser() : "null") +                ", timestamp=" + timestamp +
                ", azione='" + azione + '\'' +
                ", dettagli='" + dettagli + '\'' +
                '}';
    }
}