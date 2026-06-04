package it.unipv.JVL_DA.project.POJO;

public class Amministratore extends UtenteAutenticato {

    private String adminUser;

    public Amministratore() {}

    public Amministratore(String adminUser, String email, String passwordHash) {
        super(email, passwordHash);
        this.adminUser = adminUser;
    }

    public Amministratore(int id, String adminUser, String email, String passwordHash) {
        super(id, email, passwordHash);
        this.adminUser = adminUser;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    @Override
    public String toString() {
        return "Amministratore{" +
                "id=" + getId() +
                ", adminUser='" + adminUser + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
