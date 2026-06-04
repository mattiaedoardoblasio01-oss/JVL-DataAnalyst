package it.unipv.JVL_DA.project.POJO;

public class Amministratore {

    // Campi (corrispondono alle colonne del DB)
    private int id;               // id INT (auto increment)
    private String adminUser;     // admin_user VARCHAR(50)
    private String email;         // email VARCHAR(100)
    private String passwordHash;  // password_hash VARCHAR(255)

    // costruttore vuoto (usato da JDBC per creare l'oggetto)
    public Amministratore() {}

    // Costruttore senza id (usato per INSERT, l'id lo assegna il DB)
    public Amministratore(String adminUser, String email, String passwordHash) {
        this.adminUser = adminUser;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Costruttore completo (usato per SELECT, l'id viene dal DB)
    public Amministratore(int id, String adminUser, String email, String passwordHash) {
        this.id = id;
        this.adminUser = adminUser;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // getter
    public int getId() {
        return id;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // toString (utile per stampe e debug)
    @Override
    public String toString() {
        return "Amministratore{" +
                "id=" + id +
                ", adminUser='" + adminUser + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}