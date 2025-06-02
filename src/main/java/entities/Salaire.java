package entities;

public class Salaire {
    private String departement;
    private String roleDepartement;
    private double sumSalaries;
    private int nbrSalary;

    public Salaire(String departement, String roleDepartement, double sumSalaries, int nbrSalary) {
        this.departement = departement;
        this.roleDepartement = roleDepartement;
        this.sumSalaries = sumSalaries;
        this.nbrSalary = nbrSalary;
    }

    // Getters et Setters
    // ...
}
