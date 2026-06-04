package it.unipv.JVL_DA.project.POJO;

public class Amministratore {

    private int id;
    private String adminUser;
    private String email;
    private String passwordHash;

    public Amministratore() {}

    public Amministratore(String adminUser, String email, String passwordHash) {
        this.adminUser = adminUser;
        this.email = email;
        this.passwordHash = passwordHash;
    }

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