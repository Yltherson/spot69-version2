package com.spot69.dao;

import com.spot69.model.Rayon;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RayonDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter un rayon
    public void ajouter(Rayon rayon) {
        String sql = "INSERT INTO RAYON (NOM, DESCRIPTION, IMAGE_URL, CREATION_DATE, UPDATE_DATE, UTILISATEUR_ID, STATUT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rayon.getNom());
            stmt.setString(2, rayon.getDescription());
            stmt.setString(3, rayon.getImageUrl());
            stmt.setTimestamp(4, Timestamp.valueOf(rayon.getCreationDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(rayon.getUpdateDate()));
            stmt.setInt(6, rayon.getUtilisateurId());
            stmt.setString(7, rayon.getStatut() != null ? rayon.getStatut() : "VISIBLE");

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un rayon
    public void modifier(Rayon rayon) {
        String sql = "UPDATE RAYON SET NOM = ?, DESCRIPTION = ?, IMAGE_URL = ?, UPDATE_DATE = ?, UTILISATEUR_ID = ?, STATUT = ? WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rayon.getNom());
            stmt.setString(2, rayon.getDescription());
            stmt.setString(3, rayon.getImageUrl());
            stmt.setTimestamp(4, Timestamp.valueOf(rayon.getUpdateDate()));
            stmt.setInt(5, rayon.getUtilisateurId());
            stmt.setString(6, rayon.getStatut());
            stmt.setInt(7, rayon.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un rayon (logique)
    public String supprimer(int rayonId) {
        try (Connection conn = getConnection()) {
            // Vérifie si le rayon contient des catégories avec sous-catégories ayant des produits
            String checkSql = "SELECT COUNT(*) AS nb " +
                              "FROM MENU_CATEGORIE mc " +
                              "JOIN PLAT p ON (p.CATEGORIE_ID = mc.ID OR p.SOUS_CATEGORIE_ID = mc.ID) " +
                              "WHERE mc.RAYON_ID = ? AND p.STATUT != 'DELETED' AND mc.STATUT != 'DELETED'";

            try (PreparedStatement stmtCheck = conn.prepareStatement(checkSql)) {
                stmtCheck.setInt(1, rayonId);
                ResultSet rs = stmtCheck.executeQuery();
                if (rs.next() && rs.getInt("nb") > 0) {
                    return "Impossible de supprimer le rayon : il contient des catégories avec des produits actifs.";
                }
            }

            // Supprimer logiquement le rayon
            String deleteSql = "UPDATE RAYON SET STATUT = 'DELETED', UPDATE_DATE = NOW() WHERE ID = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(deleteSql)) {
                stmtDelete.setInt(1, rayonId);
                int rowsAffected = stmtDelete.executeUpdate();
                if (rowsAffected == 0) return "Rayon non trouvé ou déjà supprimé.";
            }

            return "SUCCESS: Rayon supprimé avec succès !";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur technique lors de la suppression : " + e.getMessage();
        }
    }

    // Récupérer un rayon par ID
    public Rayon getById(int id) {
        String sql = "SELECT * FROM RAYON WHERE ID = ? AND STATUT = 'VISIBLE'";
        Rayon rayon = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rayon = new Rayon();
                rayon.setId(rs.getInt("ID"));
                rayon.setNom(rs.getString("NOM"));
                rayon.setDescription(rs.getString("DESCRIPTION"));
                rayon.setImageUrl(rs.getString("IMAGE_URL"));
                rayon.setStatut(rs.getString("STATUT"));
                rayon.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime());
                rayon.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime());
                rayon.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rayon;
    }

    // Récupérer tous les rayons visibles
    public List<Rayon> getAll() {
        List<Rayon> liste = new ArrayList<>();
        String sql = "SELECT * FROM RAYON WHERE STATUT = 'VISIBLE' ORDER BY CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rayon rayon = new Rayon();
                rayon.setId(rs.getInt("ID"));
                rayon.setNom(rs.getString("NOM"));
                rayon.setDescription(rs.getString("DESCRIPTION"));
                rayon.setImageUrl(rs.getString("IMAGE_URL"));
                rayon.setStatut(rs.getString("STATUT"));
                rayon.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime());
                rayon.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime());
                rayon.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

                liste.add(rayon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }
}
