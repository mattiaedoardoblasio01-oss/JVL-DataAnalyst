package it.unipv.JVL_DA.project.POJO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Utente extends UtenteAutenticato {

    private String username;
    private String indirizzo;
    private String cap;
    private String provincia;
    private LocalDate dataNascita;
    private LocalDateTime createdAt;

    public Utente() {}

    // Costruttore senza id e senza createdAt (per INSERT — id e createdAt li gestisce il DB)
    public Utente(String nome, String cognome, String username, String email,
                  String passwordHash, String indirizzo, String cap,
                  String provincia, LocalDate dataNascita) {
        super(nome, cognome, email, passwordHash);
        this.username = username;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.provincia = provincia;
        this.dataNascita = dataNascita;
    }

    // Costruttore completo (per SELECT)
    public Utente(Integer id, String nome, String cognome, String username, String email,
                  String passwordHash, String indirizzo, String cap,
                  String provincia, LocalDate dataNascita, LocalDateTime createdAt) {
        super(id, nome, cognome, email, passwordHash);
        this.username = username;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.provincia = provincia;
        this.dataNascita = dataNascita;
        this.createdAt = createdAt;
    }

    public String getUsername() { return username; }
    public String getIndirizzo() { return indirizzo; }
    public String getCap() { return cap; }
    public String getProvincia() { return provincia; }
    public LocalDate getDataNascita() { return dataNascita; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUsername(String username) { this.username = username; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public void setCap(String cap) { this.cap = cap; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", cognome='" + getCognome() + '\'' +
                ", username='" + username + '\'' +
                ", email='" + getEmail() + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", cap='" + cap + '\'' +
                ", provincia='" + provincia + '\'' +
                ", dataNascita=" + dataNascita +
                ", createdAt=" + createdAt +
                '}';
    }
}