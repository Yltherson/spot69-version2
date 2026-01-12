package com.spot69.dao;

import com.spot69.model.InventaireCategorie;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventaireCategorieDAO {

    public boolean insert(InventaireCategorie c) {
        String sql;
        if ("CATEGORIE".equalsIgnoreCase(c.getType())) {
            sql = "INSERT INTO CATEGORIES (NOM, DESCRIPTION, STATUT, DELETED_BY) VALUES (?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO SOUS_CATEGORIES (NOM, CATEGORIE_ID, DESCRIPTION, STATUT, DELETED_BY) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNom());
            if ("CATEGORIE".equalsIgnoreCase(c.getType())) {
                stmt.setString(2, c.getDescription());
                stmt.setString(3, c.getStatut());
                stmt.setObject(4, c.getDeletedBy());
            } else {
                stmt.setInt(2, c.getCategorieId());
                stmt.setString(3, c.getDescription());
                stmt.setString(4, c.getStatut());
                stmt.setObject(5, c.getDeletedBy());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(InventaireCategorie c) {
        String sql;
        if ("CATEGORIE".equalsIgnoreCase(c.getType())) {
            sql = "UPDATE CATEGORIES SET NOM = ?, DESCRIPTION = ?, STATUT = ?, DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        } else {
            sql = "UPDATE SOUS_CATEGORIES SET NOM = ?, CATEGORIE_ID = ?, DESCRIPTION = ?, STATUT = ?, DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNom());
            if ("CATEGORIE".equalsIgnoreCase(c.getType())) {
                stmt.setString(2, c.getDescription());
                stmt.setString(3, c.getStatut());
                stmt.setObject(4, c.getDeletedBy());
                stmt.setInt(5, c.getId());
            } else {
                stmt.setInt(2, c.getCategorieId());
                stmt.setString(3, c.getDescription());
                stmt.setString(4, c.getStatut());
                stmt.setObject(5, c.getDeletedBy());
                stmt.setInt(6, c.getId());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public boolean deleteSousCategorie(int id, int deletedBy) {
        String sql = "UPDATE SOUS_CATEGORIES SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deletedBy);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


 // Supprimer une catégorie + toutes ses sous-catégories
    public boolean supprimerCategorieEtSousCategories(int idCategorie, int deletedBy) {
        String sqlSousCat = "UPDATE SOUS_CATEGORIES SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE CATEGORIE_ID = ?";
        String sqlCat = "UPDATE CATEGORIES SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                PreparedStatement stmtSous = conn.prepareStatement(sqlSousCat);
                PreparedStatement stmtCat = conn.prepareStatement(sqlCat)
            ) {
                // Supprimer sous-catégories
                stmtSous.setInt(1, deletedBy);
                stmtSous.setInt(2, idCategorie);
                stmtSous.executeUpdate();

                // Supprimer catégorie
                stmtCat.setInt(1, deletedBy);
                stmtCat.setInt(2, idCategorie);
                stmtCat.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Obtenir toutes les sous-catégories d'une catégorie
    public List<InventaireCategorie> getSousCategoriesByCategorie(int categorieId) {
        List<InventaireCategorie> sousCategories = new ArrayList<>();
        String sql = "SELECT * FROM SOUS_CATEGORIES WHERE CATEGORIE_ID = ? AND STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sousCategories.add(extractFromResultSet(rs, "SOUS_CATEGORIE"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sousCategories;
    }
    public InventaireCategorie findById(int id, String type) {
        String table = "CATEGORIE".equalsIgnoreCase(type) ? "CATEGORIES" : "SOUS_CATEGORIES";
        String sql = "SELECT * FROM " + table + " WHERE ID = ? AND STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs, type);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<InventaireCategorie> findAllVisibleCategorie() {
        List<InventaireCategorie> list = new ArrayList<>();

        // Charger les catégories
        String sqlCat = "SELECT * FROM CATEGORIES WHERE STATUT = 'VISIBLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCat);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs, "CATEGORIE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<InventaireCategorie> getAllCategoriesWithSousCategories() {
        List<InventaireCategorie> categoriesAvecSous = new ArrayList<>();

        String sqlCat = "SELECT * FROM CATEGORIES WHERE STATUT = 'VISIBLE'";
        String sqlSous = "SELECT * FROM SOUS_CATEGORIES WHERE CATEGORIE_ID = ? AND STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtCat = conn.prepareStatement(sqlCat);
             ResultSet rsCat = stmtCat.executeQuery()) {

            while (rsCat.next()) {
                InventaireCategorie categorie = extractFromResultSet(rsCat, "CATEGORIE");

                // Charger les sous-catégories
                try (PreparedStatement stmtSous = conn.prepareStatement(sqlSous)) {
                    stmtSous.setInt(1, categorie.getId());
                    try (ResultSet rsSous = stmtSous.executeQuery()) {
                        List<InventaireCategorie> sousCategories = new ArrayList<>();
                        while (rsSous.next()) {
                            sousCategories.add(extractFromResultSet(rsSous, "SOUS_CATEGORIE"));
                        }
                        categorie.setSousCategories(sousCategories); // 🔁 Assurez-vous que votre modèle a ce champ
                    }
                }

                categoriesAvecSous.add(categorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoriesAvecSous;
    }


    public List<InventaireCategorie> findAllVisibleSousCategorie() {
        List<InventaireCategorie> list = new ArrayList<>();

        // Charger les sous-catégories
        String sqlSous = "SELECT * FROM SOUS_CATEGORIES WHERE STATUT = 'VISIBLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSous);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs, "SOUS_CATEGORIE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private InventaireCategorie extractFromResultSet(ResultSet rs, String type) throws SQLException {
        InventaireCategorie c = new InventaireCategorie();
        c.setId(rs.getInt("ID"));
        c.setNom(rs.getString("NOM"));
        c.setDescription(rs.getString("DESCRIPTION"));
        c.setStatut(rs.getString("STATUT"));
        c.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        c.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        c.setDeletedBy(rs.getObject("DELETED_BY") != null ? rs.getInt("DELETED_BY") : null);
        c.setType(type);

        if ("SOUS_CATEGORIE".equalsIgnoreCase(type)) {
            c.setCategorieId(rs.getInt("CATEGORIE_ID"));
        }

        return c;
    }
}
