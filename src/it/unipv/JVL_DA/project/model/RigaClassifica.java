package it.unipv.JVL_DA.project.model;

/**
 * Riga aggregata della classifica di Regular Season di una singola squadra.
 *
 * E' un semplice oggetto di trasporto del Model (stesso stile di TabellonePO):
 * viene calcolato dal Service a partire dalle Partite "Conclusa" e mappato in
 * tabella dal Controller, senza contenere logica di calcolo o di presentazione.
 *
 * Le sconfitte non sono un campo a se': si ricavano come giocate - vittorie
 * (nel basket non esistono pareggi).
 */
public class RigaClassifica {

    private int posizione;
    private Squadra squadra;
    private int giocate;
    private int vittorie;
    private int diffCanestri;

    public RigaClassifica() {}

    public RigaClassifica(int posizione, Squadra squadra, int giocate, int vittorie, int diffCanestri) {
        this.posizione = posizione;
        this.squadra = squadra;
        this.giocate = giocate;
        this.vittorie = vittorie;
        this.diffCanestri = diffCanestri;
    }

    /* Getter e setter */
    public int getPosizione()      { return posizione; }
    public Squadra getSquadra()    { return squadra; }
    public int getGiocate()        { return giocate; }
    public int getVittorie()       { return vittorie; }
    public int getSconfitte()      { return giocate - vittorie; }
    public int getDiffCanestri()   { return diffCanestri; }

    public void setPosizione(int posizione)       { this.posizione = posizione; }
    public void setSquadra(Squadra squadra)       { this.squadra = squadra; }
    public void setGiocate(int giocate)           { this.giocate = giocate; }
    public void setVittorie(int vittorie)         { this.vittorie = vittorie; }
    public void setDiffCanestri(int diffCanestri) { this.diffCanestri = diffCanestri; }

    @Override
    public String toString() {
        return "RigaClassifica{" +
                "posizione=" + posizione +
                ", squadra=" + (squadra != null ? squadra.getNome() : "null") +
                ", giocate=" + giocate +
                ", vittorie=" + vittorie +
                ", sconfitte=" + getSconfitte() +
                ", diffCanestri=" + diffCanestri +
                '}';
    }
}