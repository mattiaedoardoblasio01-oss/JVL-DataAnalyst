package it.unipv.JVL_DA.project.POJO;

public abstract class UtenteAutenticato extends Persona {

    private String email;
    private String passwordHash;

    public UtenteAutenticato() {}

    public UtenteAutenticato(String email, String passwordHash) {
        super();
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UtenteAutenticato(int id, String email, String passwordHash) {
        super();
        setId(id);
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UtenteAutenticato(String nome, String cognome, String email, String passwordHash) {
        super(nome, cognome);
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UtenteAutenticato(int id, String nome, String cognome, String email, String passwordHash) {
        super(id, nome, cognome);
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "UtenteAutenticato{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", cognome='" + getCognome() + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
