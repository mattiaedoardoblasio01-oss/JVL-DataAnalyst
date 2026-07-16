package it.unipv.JVL_DA.project.model;

import java.time.LocalDate;

public class Campionato {
    private int id;
    private String nome;
    private int anno;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato; /*Config, Attivo, Chiuso*/

    public Campionato(){}

    /* Costruttore senza ID per INSERT*/
    public Campionato (String nome, int anno, LocalDate dataInizio, LocalDate dataFine, String stato){
        this.nome = nome;
        this.anno = anno;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.stato = stato;
    }

    /* Costruttore completo per SELECT*/
    public Campionato (int id, String nome, int anno, LocalDate dataInizio, LocalDate dataFine, String stato){
        this.id = id;
        this.nome = nome;
        this.anno = anno;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.stato = stato;
    }

    /* Getter e Setter*/
    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getAnno() { return anno; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public String getStato() { return stato; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setAnno(int anno) { this.anno = anno; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    public void setStato(String stato) { this.stato = stato; }

    @Override
    public String toString() {
        return "Campionato{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", anno=" + anno +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", stato='" + stato + '\'' +
                '}';
    }
}
