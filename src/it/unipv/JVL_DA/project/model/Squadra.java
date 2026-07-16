package it.unipv.JVL_DA.project.model;

public class Squadra {
    private String id;
    private String nome;
    private String sede;
    private String logoURL;
    private String allenatore;

    public Squadra(String id, String nome , String sede, String logoURL, String allenatore){
        this.id = id;
        this.nome = nome;
        this.sede = sede;
        this.logoURL = logoURL;
        this.allenatore = allenatore;
    }

    /* getter e setter*/
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSede() {
        return sede;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public String getAllenatore() {
        return allenatore;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public void setLogoURL(String logoUrl) {
        this.logoURL = logoUrl;
    }

    public void setAllenatore(String allenatore) {
        this.allenatore = allenatore;
    }

    @Override
    public String toString() {
        return "Squadra{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", sede='" + sede + '\'' +
                ", logoUrl='" + logoURL + '\'' +
                ", allenatore='" + allenatore + '\'' +
                '}';
    }
}
