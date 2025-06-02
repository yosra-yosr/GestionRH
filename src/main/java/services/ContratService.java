package services;

import entities.Contrat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratService {

    // Remplacez par vos informations de connexion
    private static final String URL = "jdbc:mysql://localhost:3306/gestionrh";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Contrat> getAllContrats() {
        List<Contrat> contrats = new ArrayList<>();

        // Modifiez cette requête selon votre structure de table
        String query = "SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone FROM contrats";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contrat contrat = new Contrat(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("matricule"),
                        rs.getString("role"),
                        rs.getString("cin"),
                        rs.getString("dateDebut"),
                        rs.getString("dateFin")
                );

                // Utilisez les noms exacts des colonnes de votre table
                contrat.setMatricule(rs.getString("matricule"));
                contrat.setName(rs.getString("name"));
                contrat.setCin(rs.getString("cin"));
                contrat.setRole(rs.getString("role"));
                contrat.setDateDebut(rs.getString("dateDebut"));
                contrat.setDateFin(rs.getString("dateFin"));
                contrat.setEmail(rs.getString("email"));
                contrat.setPhone(rs.getString("phone"));

                // Si vous avez une colonne id dans votre table, décommentez :
                // contrat.setId(rs.getInt("id"));

                contrats.add(contrat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des contrats: " + e.getMessage());
        }

        return contrats;
    }

    public void ajouterContrat(Contrat contrat) throws SQLException {
        String query = "INSERT INTO contrats (matricule, name, cin, role, dateDebut, dateFin, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, contrat.getMatricule());
            stmt.setString(2, contrat.getName());
            stmt.setString(3, contrat.getCin());
            stmt.setString(4, contrat.getRole());
            stmt.setString(5, contrat.getDateDebut());
            stmt.setString(6, contrat.getDateFin());
            stmt.setString(7, contrat.getEmail());
            stmt.setString(8, contrat.getPhone());

            stmt.executeUpdate();
        }
    }

    public void modifierContrat(Contrat contrat) throws SQLException {
        // Si vous n'avez pas de colonne id, utilisez un autre identifiant unique comme matricule
        String query = "UPDATE contrats SET name=?, cin=?, role=?, dateDebut=?, dateFin=?, email=?, phone=? WHERE matricule=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, contrat.getName());
            stmt.setString(2, contrat.getCin());
            stmt.setString(3, contrat.getRole());
            stmt.setString(4, contrat.getDateDebut());
            stmt.setString(5, contrat.getDateFin());
            stmt.setString(6, contrat.getEmail());
            stmt.setString(7, contrat.getPhone());
            stmt.setString(8, contrat.getMatricule()); // Utilise matricule comme identifiant

            stmt.executeUpdate();
        }
    }

    public void supprimerContrat(String matricule) throws SQLException {
        // Modifié pour utiliser matricule au lieu de id
        String query = "DELETE FROM contrats WHERE matricule=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricule);
            stmt.executeUpdate();
        }
    }

    // Méthode alternative si vous avez vraiment besoin de supprimer par ID
    public void supprimerContratParId(int id) throws SQLException {
        String query = "DELETE FROM contrats WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}