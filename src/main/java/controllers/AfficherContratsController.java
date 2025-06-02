package controllers;

import entities.Contrat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import services.ContratService;

public class AfficherContratsController {

    private final ContratService contratService = new ContratService();
    private final ObservableList<Contrat> contratList = FXCollections.observableArrayList();
    private Contrat selectedContrat;

    @FXML private AnchorPane homePane;

    // TableView et ses colonnes
    @FXML private TableView<Contrat> tablec;
    @FXML private TableColumn<Contrat, String> matricule1;
    @FXML private TableColumn<Contrat, String> name1;
    @FXML private TableColumn<Contrat, String> cin1;
    @FXML private TableColumn<Contrat, String> role1;
    @FXML private TableColumn<Contrat, String> datef1;

    // TextFields pour les formulaires
    @FXML private TextField role;
    @FXML private TextField matricule;
    @FXML private TextField cin;
    @FXML private TextField dated;
    @FXML private TextField datef;
    @FXML private TextField name;
    @FXML private TextField email;
    @FXML private TextField phone;

    @FXML
    public void initialize() {
        // Liaison des colonnes aux attributs du modèle
        matricule1.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        name1.setCellValueFactory(new PropertyValueFactory<>("name"));
        cin1.setCellValueFactory(new PropertyValueFactory<>("cin"));
        role1.setCellValueFactory(new PropertyValueFactory<>("role"));
        datef1.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        loadContrats();

        tablec.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedContrat = newSelection;
                populateFields(selectedContrat);
            } else {
                selectedContrat = null;
                clearFields();
            }
        });
    }

    private void loadContrats() {
        contratList.clear();
        contratList.addAll(contratService.getAllContrats());
        tablec.setItems(contratList);
    }

    private void populateFields(Contrat contrat) {
        matricule.setText(contrat.getMatricule());
        name.setText(contrat.getName());
        cin.setText(contrat.getCin());
        role.setText(contrat.getRole());
        dated.setText(contrat.getDateDebut());
        datef.setText(contrat.getDateFin());
        email.setText(contrat.getEmail());
        phone.setText(contrat.getPhone());
    }

    private void clearFields() {
        matricule.clear();
        name.clear();
        cin.clear();
        role.clear();
        dated.clear();
        datef.clear();
        email.clear();
        phone.clear();
    }

    @FXML
    private void ajouterContrat() {
        try {
            Contrat newContrat = new Contrat(
                    name.getText(),
                    email.getText(),
                    phone.getText(),
                    matricule.getText(),
                    role.getText(),
                    cin.getText(),
                    dated.getText(),
                    datef.getText()
            );

            contratService.ajouterContrat(newContrat);
            loadContrats();
            clearFields();

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Contrat ajouté avec succès!");
            alert.showAndWait();

        } catch (Exception e) {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout du contrat: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleUpdateEmployee(ActionEvent actionEvent) {
        if (selectedContrat != null) {
            try {
                selectedContrat.setMatricule(matricule.getText());
                selectedContrat.setName(name.getText());
                selectedContrat.setCin(cin.getText());
                selectedContrat.setRole(role.getText());
                selectedContrat.setDateDebut(dated.getText());
                selectedContrat.setDateFin(datef.getText());
                selectedContrat.setEmail(email.getText());
                selectedContrat.setPhone(phone.getText());

                contratService.modifierContrat(selectedContrat);
                loadContrats();
                clearFields();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Contrat modifié avec succès!");
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la modification: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un contrat à modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleDeleteEmployee(ActionEvent actionEvent) {
        if (selectedContrat != null) {
            try {
                contratService.supprimerContrat(selectedContrat.getMatricule());
                loadContrats();
                clearFields();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Contrat supprimé avec succès!");
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la suppression: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un contrat à supprimer.");
            alert.showAndWait();
        }
    }

    // Méthodes de navigation
    @FXML
    public void showHome(ActionEvent actionEvent) {
        System.out.println("Accueil");
    }

    @FXML
    public void showDepartments(ActionEvent actionEvent) {
        System.out.println("Départements");
    }

    @FXML
    public void showEmployees(ActionEvent actionEvent) {
        System.out.println("Employés");
    }

    @FXML
    public void handleAddEmployee(ActionEvent actionEvent) {
        ajouterContrat();
    }

    // Nouvelle méthode pour gérer le clic sur la table
    @FXML
    public void handleEmployeeTableClick() {
        // Cette méthode est appelée automatiquement par le listener
    }
}