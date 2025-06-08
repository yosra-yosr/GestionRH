package entities;

public class Department {
    private String id;
    private String departmentName;
    private String roleDepartment;
    private double sumSalaries;
    private int nbrSalary;

    // Constructeur
    public Department(String id, String departmentName, String roleDepartment, double sumSalaries, int nbrSalary) {
        this.id = id;
        this.departmentName = departmentName;
        this.roleDepartment = roleDepartment;
        this.sumSalaries = sumSalaries;
        this.nbrSalary = nbrSalary;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleDepartment() {
        return roleDepartment;
    }

    public void setRoleDepartment(String roleDepartment) {
        this.roleDepartment = roleDepartment;
    }

    public double getSumSalaries() {
        return sumSalaries;
    }

    public void setSumSalaries(double sumSalaries) {
        this.sumSalaries = sumSalaries;
    }

    public int getNbrSalary() {
        return nbrSalary;
    }

    public void setNbrSalary(int nbrSalary) {
        this.nbrSalary = nbrSalary;
    }

    // Méthode pour générer la requête SQL d'insertion
    public String toInsertQuery() {
        return String.format("INSERT INTO `departments`(`id`, `department_name`, `role_department`, `sum_salaries`, `nbr_salary`) VALUES ('%s', '%s', '%s', '%f', '%d')",
                id, departmentName, roleDepartment, sumSalaries, nbrSalary);
    }
}
