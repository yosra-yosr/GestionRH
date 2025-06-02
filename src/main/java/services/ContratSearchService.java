package services;

import entities.Contrat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ContratSearchService {

    // Paramètres de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/gestionrh";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Établit une connexion à la base de données
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Recherche globale dans tous les champs avec un seul terme de recherche
     * @param searchTerm terme de recherche
     * @return liste des contrats correspondants
     */
    public List<Contrat> searchGlobal(String searchTerm) {
        List<Contrat> contrats = new ArrayList<>();
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return contrats;
        }

        String query = """
            SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone 
            FROM contrats 
            WHERE name LIKE ? 
               OR role LIKE ? 
               OR cin LIKE ? 
               OR matricule LIKE ? 
               OR email LIKE ? 
               OR phone LIKE ?
            ORDER BY name ASC
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchParam = "%" + searchTerm.trim() + "%";
            
            // Remplir tous les paramètres avec le même terme de recherche
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchParam);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contrats.add(createContratFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche globale: " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Recherche par nom
     */
    public List<Contrat> searchByName(String name) {
        return searchByField("name", name);
    }

    /**
     * Recherche par rôle
     */
    public List<Contrat> searchByRole(String role) {
        return searchByField("role", role);
    }

    /**
     * Recherche par CIN
     */
    public List<Contrat> searchByCin(String cin) {
        return searchByField("cin", cin);
    }

    /**
     * Recherche par matricule
     */
    public List<Contrat> searchByMatricule(String matricule) {
        return searchByField("matricule", matricule);
    }

    /**
     * Recherche par email
     */
    public List<Contrat> searchByEmail(String email) {
        return searchByField("email", email);
    }

    /**
     * Recherche par téléphone
     */
    public List<Contrat> searchByPhone(String phone) {
        return searchByField("phone", phone);
    }

    /**
     * Méthode générique pour rechercher par un champ spécifique
     */
    private List<Contrat> searchByField(String fieldName, String value) {
        List<Contrat> contrats = new ArrayList<>();
        
        if (value == null || value.trim().isEmpty()) {
            return contrats;
        }

        String query = String.format("""
            SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone 
            FROM contrats 
            WHERE %s LIKE ? 
            ORDER BY name ASC
            """, fieldName);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + value.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contrats.add(createContratFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche par " + fieldName + ": " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Recherche avancée avec plusieurs critères (ET logique)
     */
    public List<Contrat> searchAdvanced(String name, String role, String cin, String matricule, String email, String phone) {
        List<Contrat> contrats = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        List<String> parameters = new ArrayList<>();

        queryBuilder.append("SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone FROM contrats WHERE 1=1");

        // Ajouter les conditions selon les paramètres fournis
        if (name != null && !name.trim().isEmpty()) {
            queryBuilder.append(" AND name LIKE ?");
            parameters.add("%" + name.trim() + "%");
        }
        if (role != null && !role.trim().isEmpty()) {
            queryBuilder.append(" AND role LIKE ?");
            parameters.add("%" + role.trim() + "%");
        }
        if (cin != null && !cin.trim().isEmpty()) {
            queryBuilder.append(" AND cin LIKE ?");
            parameters.add("%" + cin.trim() + "%");
        }
        if (matricule != null && !matricule.trim().isEmpty()) {
            queryBuilder.append(" AND matricule LIKE ?");
            parameters.add("%" + matricule.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            queryBuilder.append(" AND email LIKE ?");
            parameters.add("%" + email.trim() + "%");
        }
        if (phone != null && !phone.trim().isEmpty()) {
            queryBuilder.append(" AND phone LIKE ?");
            parameters.add("%" + phone.trim() + "%");
        }

        queryBuilder.append(" ORDER BY name ASC");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            // Définir les paramètres
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setString(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contrats.add(createContratFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche avancée: " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Recherche par période de dates (contrats actifs entre deux dates)
     */
    public List<Contrat> searchByDateRange(String startDate, String endDate) {
        List<Contrat> contrats = new ArrayList<>();
        
        if ((startDate == null || startDate.trim().isEmpty()) && 
            (endDate == null || endDate.trim().isEmpty())) {
            return contrats;
        }

        StringBuilder queryBuilder = new StringBuilder();
        List<String> parameters = new ArrayList<>();

        queryBuilder.append("SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone FROM contrats WHERE 1=1");

        if (startDate != null && !startDate.trim().isEmpty()) {
            queryBuilder.append(" AND dateDebut >= ?");
            parameters.add(startDate.trim());
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryBuilder.append(" AND (dateFin <= ? OR dateFin IS NULL OR dateFin = '')");
            parameters.add(endDate.trim());
        }

        queryBuilder.append(" ORDER BY dateDebut ASC");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setString(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contrats.add(createContratFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche par dates: " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Recherche les contrats expirés
     */
    public List<Contrat> searchExpiredContracts() {
        List<Contrat> contrats = new ArrayList<>();
        
        String query = """
            SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone 
            FROM contrats 
            WHERE dateFin IS NOT NULL 
              AND dateFin != '' 
              AND dateFin < CURDATE()
            ORDER BY dateFin DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contrats.add(createContratFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche des contrats expirés: " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Recherche les contrats actifs (sans date de fin ou date de fin future)
     */
    public List<Contrat> searchActiveContracts() {
        List<Contrat> contrats = new ArrayList<>();
        
        String query = """
            SELECT matricule, name, cin, role, dateDebut, dateFin, email, phone 
            FROM contrats 
            WHERE (dateFin IS NULL OR dateFin = '' OR dateFin >= CURDATE())
            ORDER BY dateDebut DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contrats.add(createContratFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche des contrats actifs: " + e.getMessage());
        }

        return contrats;
    }

    /**
     * Compte le nombre de résultats pour une recherche globale
     */
    public int countSearchResults(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return 0;
        }

        String query = """
            SELECT COUNT(*) as total 
            FROM contrats 
            WHERE name LIKE ? 
               OR role LIKE ? 
               OR cin LIKE ? 
               OR matricule LIKE ? 
               OR email LIKE ? 
               OR phone LIKE ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchParam = "%" + searchTerm.trim() + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchParam);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du comptage: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Méthode utilitaire pour créer un objet Contrat à partir d'un ResultSet
     */
    private Contrat createContratFromResultSet(ResultSet rs) throws SQLException {
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
        
        return contrat;
    }

    /**
     * Méthode pour tester la connexion
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            return false;
        }
    }
}
