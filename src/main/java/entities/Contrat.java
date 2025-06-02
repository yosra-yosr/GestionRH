package entities;

import java.time.LocalDate;

public class Contrat {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String matricule;
    private String role;
    private String cin;
    private String dateDebut;
    private String dateFin;

    public Contrat(int id, String name, String email, String phone, String matricule, String role, String cin, String dateDebut, String dateFin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.matricule = matricule;
        this.role = role;
        this.cin = cin;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Contrat(String name, String email, String phone, String matricule, String role, String cin, String dateDebut, String dateFin) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.matricule = matricule;
        this.role = role;
        this.cin = cin;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMatricule() { return matricule; }
    public String getRole() { return role; }
    public String getCin() { return cin; }
    public String getDateDebut() { return dateDebut; }
    public String getDateFin() { return dateFin; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setRole(String role) { this.role = role; }
    public void setCin(String cin) { this.cin = cin; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }
}
