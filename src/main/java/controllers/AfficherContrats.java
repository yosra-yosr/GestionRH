
package controllers;
import java.sql.*;
import entities.Contrat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import services.ContratService;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.fxml.FXML;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import services.ContratSearchService;
import javafx.scene.input.KeyEvent;

public class AfficherContrats {

    private final ContratSearchService searchService = new ContratSearchService();
    private final ContratService contratService = new ContratService();
    private final ObservableList<Contrat> contratList = FXCollections.observableArrayList();
    private Contrat selectedContrat;

    @FXML private TableColumn<Contrat, String> role1;
    @FXML private TextField role;
    @FXML private TextField matricule;
    @FXML private TableColumn<Contrat, String> cin1;
    @FXML private Button department_add_btn1;
    @FXML private TextField cin;
    @FXML private TableColumn<Contrat, String> dated1;
    @FXML private TextField nombre;
    @FXML private TextField search;
    @FXML private TextField datef;
    @FXML private TextField dated;
    @FXML private TextField depart;
    @FXML private TextField email;
    @FXML private TextField somme;
    @FXML private TableView<Contrat> tablec;
    @FXML private AnchorPane departmentsPane;
    @FXML private AnchorPane homePane;
    @FXML private Button department_delete_btn;
    @FXML private AnchorPane employeesPane;
    @FXML private Button department_add_btn;
    @FXML private TextField phone;
    @FXML private TableColumn<Contrat, String> matricule1;
    @FXML private TextField roledepart;
    @FXML private TextField name;
    @FXML private TableColumn<Contrat, String> name1;
    @FXML private TableColumn<Contrat, String> datef1;
    @FXML private LineChart<String, Number> departmentChart;

    // NOUVEAU : Labels pour les statistiques de la page d'accueil
    @FXML private Label contractCountLabel;
    @FXML private Label departmentCountLabel;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du controller...");

        // Configuration des colonnes de la TableView
        if (matricule1 != null) {
            matricule1.setCellValueFactory(new PropertyValueFactory<>("matricule"));
            System.out.println("Colonne matricule configurée");
        }
        if (name1 != null) {
            name1.setCellValueFactory(new PropertyValueFactory<>("name"));
            System.out.println("Colonne name configurée");
        }
        if (cin1 != null) {
            cin1.setCellValueFactory(new PropertyValueFactory<>("cin"));
            System.out.println("Colonne cin configurée");
        }
        if (role1 != null) {
            role1.setCellValueFactory(new PropertyValueFactory<>("role"));
            System.out.println("Colonne role configurée");
        }
        if (dated1 != null) {
            dated1.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
            System.out.println("Colonne dateDebut configurée");
        }
        if (datef1 != null) {
            datef1.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
            System.out.println("Colonne dateFin configurée");
        }

        // Initialiser l'interface (afficher Contract par défaut)
        initializeInterface();

        // Charger les contrats
        loadContrats();

        // Charger les données du graphique au démarrage
        loadExistingDepartments();

        // NOUVEAU : Charger les statistiques au démarrage
        updateHomeStatistics();

        // Listener pour la sélection dans la table
        if (tablec != null) {
            tablec.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedContrat = newSelection;
                    populateFields(selectedContrat);
                    System.out.println("Contrat sélectionné: " + newSelection.getName());
                } else {
                    selectedContrat = null;
                    clearFields();
                }
            });
        }
    }

    /**
     * NOUVEAU : Calcule le nombre total de contrats depuis la base de données
     */
    private int getTotalContracts() {
        String sql = "SELECT COUNT(*) as total FROM contrats";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du calcul du nombre de contrats: " + e.getMessage());
        }

        return 0;
    }

    /**
     * NOUVEAU : Calcule le nombre total de départements depuis la base de données
     */
    private int getTotalDepartments() {
        String sql = "SELECT COUNT(*) as total FROM departments";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du calcul du nombre de départements: " + e.getMessage());
        }

        return 0;
    }

    /**
     * NOUVEAU : Met à jour les statistiques affichées sur la page d'accueil
     */
    private void updateHomeStatistics() {
        try {
            int contractCount = getTotalContracts();
            int departmentCount = getTotalDepartments();

            // Mettre à jour les labels dans l'interface
            if (contractCountLabel != null) {
                contractCountLabel.setText(String.valueOf(contractCount));
            }

            if (departmentCountLabel != null) {
                departmentCountLabel.setText(String.valueOf(departmentCount));
            }

            System.out.println("Statistiques mises à jour - Contrats: " + contractCount + ", Départements: " + departmentCount);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour des statistiques: " + e.getMessage());
        }
    }

    private void loadContrats() {
        try {
            System.out.println("Chargement des contrats...");
            contratList.clear();
            contratList.addAll(contratService.getAllContrats());

            if (tablec != null) {
                tablec.setItems(contratList);
                System.out.println("Nombre de contrats chargés: " + contratList.size());
            } else {
                System.out.println("ERREUR: tablec est null!");
            }

            // Actualiser l'affichage
            if (tablec != null) {
                tablec.refresh();
            }

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des contrats: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void populateFields(Contrat contrat) {
        if (matricule != null) matricule.setText(contrat.getMatricule());
        if (name != null) name.setText(contrat.getName());
        if (cin != null) cin.setText(contrat.getCin());
        if (role != null) role.setText(contrat.getRole());
        if (dated != null) dated.setText(contrat.getDateDebut());
        if (datef != null) datef.setText(contrat.getDateFin());
        if (email != null) email.setText(contrat.getEmail());
        if (phone != null) phone.setText(contrat.getPhone());
    }

    private void clearFields() {
        if (matricule != null) matricule.clear();
        if (name != null) name.clear();
        if (cin != null) cin.clear();
        if (role != null) role.clear();
        if (dated != null) dated.clear();
        if (datef != null) datef.clear();
        if (email != null) email.clear();
        if (phone != null) phone.clear();
    }

    private boolean validateFields() {
        if (name == null || name.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire");
            return false;
        }
        if (matricule == null || matricule.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le matricule est obligatoire");
            return false;
        }
        if (cin == null || cin.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le CIN est obligatoire");
            return false;
        }
        return true;
    }

    private void ajouterContrat() {
        if (!validateFields()) {
            return;
        }

        try {
            Contrat newContrat = new Contrat(
                    name.getText().trim(),
                    email.getText().trim(),
                    phone.getText().trim(),
                    matricule.getText().trim(),
                    role.getText().trim(),
                    cin.getText().trim(),
                    dated.getText().trim(),
                    datef.getText().trim()
            );

            contratService.ajouterContrat(newContrat);

            // Recharger les données APRÈS l'ajout
            loadContrats();
            clearFields();

            // NOUVEAU : Mettre à jour les statistiques
            updateHomeStatistics();

            // Afficher un message de succès
            showAlert("Succès", "Contrat ajouté avec succès!");
            System.out.println("Contrat ajouté: " + newContrat.getName());

        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du contrat: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleUpdateEmployee(ActionEvent actionEvent) {
        if (selectedContrat != null) {
            if (!validateFields()) {
                return;
            }

            try {
                selectedContrat.setMatricule(matricule.getText().trim());
                selectedContrat.setName(name.getText().trim());
                selectedContrat.setCin(cin.getText().trim());
                selectedContrat.setRole(role.getText().trim());
                selectedContrat.setDateDebut(dated.getText().trim());
                selectedContrat.setDateFin(datef.getText().trim());
                selectedContrat.setEmail(email.getText().trim());
                selectedContrat.setPhone(phone.getText().trim());

                contratService.modifierContrat(selectedContrat);
                loadContrats();
                clearFields();

                // NOUVEAU : Mettre à jour les statistiques
                updateHomeStatistics();

                showAlert("Succès", "Contrat modifié avec succès!");

            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage());
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un contrat à modifier.");
        }
    }

    @FXML
    public void handleDeleteEmployee(ActionEvent actionEvent) {
        if (selectedContrat != null) {
            // Demander confirmation
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce contrat ?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                try {
                    contratService.supprimerContrat(selectedContrat.getMatricule());
                    loadContrats();
                    clearFields();

                    // NOUVEAU : Mettre à jour les statistiques
                    updateHomeStatistics();

                    showAlert("Succès", "Contrat supprimé avec succès!");

                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un contrat à supprimer.");
        }
    }



    @FXML
    public void handleAddEmployee(ActionEvent actionEvent) {
        ajouterContrat();
    }

    @FXML
    public void handleEmployeeTableClick(Event event) {
        // Géré automatiquement par le listener dans initialize()
    }

    // Méthode pour actualiser manuellement les données
    @FXML
    public void refreshData(ActionEvent actionEvent) {
        loadContrats();
        updateHomeStatistics(); // NOUVEAU : Actualiser aussi les statistiques
        showAlert("Info", "Données actualisées!");
    }

    // Méthodes de navigation entre les interfaces
    @FXML
    public void showHome(ActionEvent actionEvent) {
        System.out.println("Navigation vers Home");

        // Masquer tous les panneaux
        hideAllPanes();

        // Afficher uniquement le panneau Home
        if (homePane != null) {
            homePane.setVisible(true);
        }

        // NOUVEAU : Mettre à jour les statistiques
        updateHomeStatistics();
    }

    @FXML
    public void showDepartments(ActionEvent actionEvent) {
        System.out.println("Navigation vers Contract");

        // Masquer tous les panneaux
        hideAllPanes();

        // Afficher uniquement le panneau des contrats (employeesPane)
        if (employeesPane != null) {
            employeesPane.setVisible(true);
        }

        // Recharger les données des contrats
        loadContrats();
    }

    @FXML
    public void showEmployees(ActionEvent actionEvent) {
        System.out.println("Navigation vers Salary");

        // Masquer tous les panneaux
        hideAllPanes();

        // Afficher uniquement le panneau des départements (qui sera utilisé pour Salary)
        if (departmentsPane != null) {
            departmentsPane.setVisible(true);
        }

        // Charger/actualiser le graphique quand on navigue vers Salary
        updateDepartmentChart();
    }

    private void debugChart() {
        if (departmentChart == null) {
            System.out.println("ERREUR: departmentChart est null dans debugChart()");
            return;
        }

        System.out.println("departmentChart trouvé et initialisé");
        System.out.println("Titre du graphique: " + departmentChart.getTitle());
        System.out.println("Nombre de séries: " + departmentChart.getData().size());
    }

    /**
     * Méthode utilitaire pour masquer tous les panneaux
     */
    private void hideAllPanes() {
        if (homePane != null) {
            homePane.setVisible(false);
        }
        if (employeesPane != null) {
            employeesPane.setVisible(false);
        }
        if (departmentsPane != null) {
            departmentsPane.setVisible(false);
        }
    }

    /**
     * Initialiser l'interface au démarrage - afficher Contract par défaut
     */
    private void initializeInterface() {
        // Masquer tous les panneaux au démarrage
        hideAllPanes();

        // Afficher le panneau Contract par défaut
        if (employeesPane != null) {
            employeesPane.setVisible(true);
        }
    }

    @FXML
    public void handleAddDepartment(ActionEvent actionEvent) {
        System.out.println("Add Department/Salary");

        try {
            // Récupérer les valeurs des champs
            String departmentName = depart.getText().trim();
            String roleDepartmentValue = roledepart.getText().trim();
            String sumSalariesText = somme.getText().trim();
            String nbrSalaryText = nombre.getText().trim();

            // Validation des champs
            if (departmentName.isEmpty() || roleDepartmentValue.isEmpty() ||
                    sumSalariesText.isEmpty() || nbrSalaryText.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs du département.");
                return;
            }

            double sumSalaries;
            int nbrSalary;

            try {
                sumSalaries = Double.parseDouble(sumSalariesText);
                nbrSalary = Integer.parseInt(nbrSalaryText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour la somme et le nombre.");
                return;
            }

            // Ajouter le département à la base de données
            addDepartmentToDatabase(departmentName, roleDepartmentValue, sumSalaries, nbrSalary);

            // Forcer la mise à jour de la courbe
            System.out.println("Mise à jour forcée du graphique après ajout...");
            updateDepartmentChart();

            // NOUVEAU : Mettre à jour les statistiques
            updateHomeStatistics();

            // Vider les champs du département
            clearDepartmentFields();

            showAlert("Succès", "Département ajouté avec succès!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du département: " + e.getMessage());
        }
    }

    private void addDepartmentToDatabase(String departmentName, String roleDepartment, double sumSalaries, int nbrSalary) {
        String sql = "INSERT INTO departments (department_name, role_department, sum_salaries, nbr_salary) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, departmentName);
            pstmt.setString(2, roleDepartment);
            pstmt.setDouble(3, sumSalaries);
            pstmt.setInt(4, nbrSalary);

            pstmt.executeUpdate();
            System.out.println("Département ajouté en base de données avec succès");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'insertion en base de données: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout en base de données", e);
        }
    }

    private void updateDepartmentChart() {
        if (departmentChart == null) {
            System.out.println("ATTENTION: departmentChart est null!");
            return;
        }

        // Effacer les données existantes
        departmentChart.getData().clear();

        // Récupérer les données depuis la base de données
        Map<String, Double> departmentData = getDepartmentDataFromDatabase();

        // Créer une nouvelle série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Somme des salaires par département");

        // Ajouter les données à la série
        for (Map.Entry<String, Double> entry : departmentData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Ajouter la série au graphique
        departmentChart.getData().add(series);

        // Personnaliser le graphique
        departmentChart.setTitle("Somme des salaires en fonction du département");
        departmentChart.setAnimated(true);
        departmentChart.setCreateSymbols(true);

        System.out.println("Graphique mis à jour avec " + departmentData.size() + " départements");
    }

    private Map<String, Double> getDepartmentDataFromDatabase() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT department_name, sum_salaries FROM departments ORDER BY department_name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String deptName = rs.getString("department_name");
                double sumSalaries = rs.getDouble("sum_salaries");
                data.put(deptName, sumSalaries);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des données du graphique: " + e.getMessage());
        }

        return data;
    }

    @FXML
    public void handleUpdateDepartment(ActionEvent actionEvent) {
        System.out.println("Update Department/Salary");
        // Logique pour mettre à jour un département ou salaire
        showAlert("Info", "Fonctionnalité de mise à jour à implémenter");
    }

    @FXML
    public void handleDeleteDepartment(ActionEvent actionEvent) {
        System.out.println("Delete Department/Salary");

        // Supprimer le dernier département ajouté
        String sql = "DELETE FROM departments ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                updateDepartmentChart();
                clearDepartmentFields();

                // NOUVEAU : Mettre à jour les statistiques
                updateHomeStatistics();

                showAlert("Succès", "Dernier département supprimé!");
            } else {
                showAlert("Erreur", "Aucun département à supprimer!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
        }
    }

    private void clearDepartmentFields() {
        if (depart != null) depart.clear();
        if (roledepart != null) roledepart.clear();
        if (somme != null) somme.clear();
        if (nombre != null) nombre.clear();
    }

    private Connection getConnection() throws SQLException {
        // Utilise les mêmes paramètres que votre ContratService
        String URL = "jdbc:mysql://localhost:3306/gestionrh";
        String USER = "root";
        String PASSWORD = "";
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Charger les données existantes des départements au démarrage
    private void loadExistingDepartments() {
        // Charger le graphique au démarrage s'il y a des données
        updateDepartmentChart();
        System.out.println("Données des départements chargées pour le graphique");
    }



@FXML
public void handleSearch(KeyEvent event) {
    String searchTerm = search.getText();
    
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
        // Si la recherche est vide, afficher tous les contrats
        loadContrats();
        return;
    }
    
    performSearch(searchTerm.trim());
}

/**
 * Gestionnaire pour le bouton de recherche (si vous en ajoutez un)
 */
@FXML
public void handleSearchButton(ActionEvent actionEvent) {
    String searchTerm = search.getText();
    
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
        showAlert("Attention", "Veuillez saisir un terme de recherche.");
        return;
    }
    
    performSearch(searchTerm.trim());
}

/**
 * Effectue la recherche et met à jour la table
 */
private void performSearch(String searchTerm) {
    try {
        System.out.println("Recherche pour: " + searchTerm);
        
        // Effacer la liste actuelle
        contratList.clear();
        
        // Effectuer la recherche globale
        List<Contrat> searchResults = searchService.searchGlobal(searchTerm);
        
        // Ajouter les résultats à la liste observable
        contratList.addAll(searchResults);
        
        // Mettre à jour la table
        if (tablec != null) {
            tablec.setItems(contratList);
            tablec.refresh();
        }
        
        // Afficher le nombre de résultats
        System.out.println("Nombre de résultats trouvés: " + searchResults.size());
        
        // Optionnel: afficher un message si aucun résultat
        if (searchResults.isEmpty()) {
            showAlert("Information", "Aucun contrat trouvé pour: " + searchTerm);
        }
        
    } catch (Exception e) {
        System.out.println("Erreur lors de la recherche: " + e.getMessage());
        e.printStackTrace();
        showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage());
    }
}

/**
 * Recherche avancée par critères spécifiques
 */
@FXML
public void handleAdvancedSearch(ActionEvent actionEvent) {
    try {
        // Récupérer les valeurs des champs de recherche
        String nameSearch = name != null ? name.getText() : null;
        String roleSearch = role != null ? role.getText() : null;
        String cinSearch = cin != null ? cin.getText() : null;
        String matriculeSearch = matricule != null ? matricule.getText() : null;
        String emailSearch = email != null ? email.getText() : null;
        String phoneSearch = phone != null ? phone.getText() : null;
        
        // Vérifier qu'au moins un critère est rempli
        if (isAllEmpty(nameSearch, roleSearch, cinSearch, matriculeSearch, emailSearch, phoneSearch)) {
            showAlert("Attention", "Veuillez remplir au moins un critère de recherche.");
            return;
        }
        
        // Effectuer la recherche avancée
        List<Contrat> searchResults = searchService.searchAdvanced(
            nameSearch, roleSearch, cinSearch, matriculeSearch, emailSearch, phoneSearch
        );
        
        // Mettre à jour la table
        contratList.clear();
        contratList.addAll(searchResults);
        
        if (tablec != null) {
            tablec.setItems(contratList);
            tablec.refresh();
        }
        
        System.out.println("Recherche avancée - Nombre de résultats: " + searchResults.size());
        
        if (searchResults.isEmpty()) {
            showAlert("Information", "Aucun contrat trouvé avec les critères spécifiés.");
        }
        
    } catch (Exception e) {
        System.out.println("Erreur lors de la recherche avancée: " + e.getMessage());
        e.printStackTrace();
        showAlert("Erreur", "Erreur lors de la recherche avancée: " + e.getMessage());
    }
}

/**
 * Recherche par nom uniquement
 */
@FXML
public void searchByName(ActionEvent actionEvent) {
    String nameToSearch = name != null ? name.getText() : "";
    if (nameToSearch.trim().isEmpty()) {
        showAlert("Attention", "Veuillez saisir un nom à rechercher.");
        return;
    }
    
    List<Contrat> results = searchService.searchByName(nameToSearch);
    updateTableWithResults(results, "nom: " + nameToSearch);
}

/**
 * Recherche par rôle uniquement
 */
@FXML 
public void searchByRole(ActionEvent actionEvent) {
    String roleToSearch = role != null ? role.getText() : "";
    if (roleToSearch.trim().isEmpty()) {
        showAlert("Attention", "Veuillez saisir un rôle à rechercher.");
        return;
    }
    
    List<Contrat> results = searchService.searchByRole(roleToSearch);
    updateTableWithResults(results, "rôle: " + roleToSearch);
}

/**
 * Recherche par CIN uniquement
 */
@FXML
public void searchByCin(ActionEvent actionEvent) {
    String cinToSearch = cin != null ? cin.getText() : "";
    if (cinToSearch.trim().isEmpty()) {
        showAlert("Attention", "Veuillez saisir un CIN à rechercher.");
        return;
    }
    
    List<Contrat> results = searchService.searchByCin(cinToSearch);
    updateTableWithResults(results, "CIN: " + cinToSearch);
}

/**
 * Recherche par matricule uniquement
 */
@FXML
public void searchByMatricule(ActionEvent actionEvent) {
    String matriculeToSearch = matricule != null ? matricule.getText() : "";
    if (matriculeToSearch.trim().isEmpty()) {
        showAlert("Attention", "Veuillez saisir un matricule à rechercher.");
        return;
    }
    
    List<Contrat> results = searchService.searchByMatricule(matriculeToSearch);
    updateTableWithResults(results, "matricule: " + matriculeToSearch);
}

/**
 * Afficher tous les contrats actifs
 */
@FXML
public void showActiveContracts(ActionEvent actionEvent) {
    List<Contrat> results = searchService.searchActiveContracts();
    updateTableWithResults(results, "contrats actifs");
}

/**
 * Afficher tous les contrats expirés
 */
@FXML
public void showExpiredContracts(ActionEvent actionEvent) {
    List<Contrat> results = searchService.searchExpiredContracts();
    updateTableWithResults(results, "contrats expirés");
}

/**
 * Réinitialiser la recherche et afficher tous les contrats
 */
@FXML
public void resetSearch(ActionEvent actionEvent) {
    // Vider le champ de recherche
    if (search != null) {
        search.clear();
    }
    
    // Recharger tous les contrats
    loadContrats();
    
    showAlert("Information", "Recherche réinitialisée. Tous les contrats sont affichés.");
}

/**
 * Méthode utilitaire pour mettre à jour la table avec les résultats
 */
private void updateTableWithResults(List<Contrat> results, String searchType) {
    try {
        contratList.clear();
        contratList.addAll(results);
        
        if (tablec != null) {
            tablec.setItems(contratList);
            tablec.refresh();
        }
        
        System.out.println("Recherche par " + searchType + " - Résultats: " + results.size());
        
        if (results.isEmpty()) {
            showAlert("Information", "Aucun contrat trouvé pour " + searchType + ".");
        }
        
    } catch (Exception e) {
        System.out.println("Erreur lors de la mise à jour des résultats: " + e.getMessage());
        e.printStackTrace();
        showAlert("Erreur", "Erreur lors de l'affichage des résultats: " + e.getMessage());
    }
}

/**
 * Vérifie si tous les paramètres sont vides
 */
private boolean isAllEmpty(String... values) {
    for (String value : values) {
        if (value != null && !value.trim().isEmpty()) {
            return false;
        }
    }
    return true;
}

/**
 * Obtenir le nombre de résultats pour le terme de recherche actuel
 */
public int getCurrentSearchResultCount() {
    String searchTerm = search != null ? search.getText() : "";
    if (searchTerm.trim().isEmpty()) {
        return contratList.size();
    }
    return searchService.countSearchResults(searchTerm);
}

@FXML
private void clearSearch(ActionEvent event) {
    // Add your search clearing logic here
    // For example:
    // searchField.clear(); // if you have a search TextField
    // Or refresh your table/list view
}


}

