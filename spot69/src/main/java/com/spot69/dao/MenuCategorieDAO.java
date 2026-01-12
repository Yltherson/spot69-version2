package com.spot69.dao;

import com.spot69.model.MenuCategorie;
import com.spot69.model.MouvementStock;
import com.spot69.model.Plat;
import com.spot69.model.Produit;
import com.spot69.model.Rayon;
import com.spot69.model.RayonHierarchique;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuCategorieDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    public void ajouter(MenuCategorie menuCategorie) {
        String sql = "INSERT INTO MENU_CATEGORIE (RAYON_ID, NOM, DESCRIPTION, CREATION_DATE, UPDATE_DATE, UTILISATEUR_ID, STATUT, PARENT_ID, IMAGE_URL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // RAYON_ID est obligatoire seulement pour les catégories parentes (PARENT_ID null)
            if (menuCategorie.getParentId() == null) {
                if (menuCategorie.getRayon() == null || menuCategorie.getRayon().getId() == 0) {
                    throw new IllegalArgumentException("Le rayon est obligatoire pour les catégories parentes.");
                }
                stmt.setInt(1, menuCategorie.getRayon().getId());
            } else {
                // Pour les sous-catégories, RAYON_ID peut être null
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, menuCategorie.getNom());
            stmt.setString(3, menuCategorie.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(menuCategorie.getCreationDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(menuCategorie.getUpdateDate()));
            stmt.setInt(6, menuCategorie.getUtilisateurId());
            stmt.setString(7, menuCategorie.getStatut());

            // Si parentId est null, on passe null dans le statement
            if (menuCategorie.getParentId() != null) {
                stmt.setInt(8, menuCategorie.getParentId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, menuCategorie.getImageUrl());

            stmt.executeUpdate();

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void modifier(MenuCategorie menuCategorie) {
        String sql = "UPDATE MENU_CATEGORIE SET RAYON_ID = ?, NOM = ?, DESCRIPTION = ?, UPDATE_DATE = ?, UTILISATEUR_ID = ?, STATUT = ?, PARENT_ID = ?, IMAGE_URL = ? WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // RAYON_ID est obligatoire seulement pour les catégories parentes (PARENT_ID null)
            if (menuCategorie.getParentId() == null) {
                if (menuCategorie.getRayon() == null || menuCategorie.getRayon().getId() == 0) {
                    throw new IllegalArgumentException("Le rayon est obligatoire pour les catégories parentes.");
                }
                stmt.setInt(1, menuCategorie.getRayon().getId());
            } else {
                // Pour les sous-catégories, RAYON_ID peut être null
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, menuCategorie.getNom());
            stmt.setString(3, menuCategorie.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(menuCategorie.getUpdateDate()));
            stmt.setInt(5, menuCategorie.getUtilisateurId());
            stmt.setString(6, menuCategorie.getStatut());

            if (menuCategorie.getParentId() != null) {
                stmt.setInt(7, menuCategorie.getParentId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setString(8, menuCategorie.getImageUrl());
            stmt.setInt(9, menuCategorie.getId());

            stmt.executeUpdate();

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
 // Ajoutez ces méthodes dans MenuCategorieDAO

 // Méthode pour récupérer toute la hiérarchie des rayons
 public List<RayonHierarchique> getHierarchieCompleteRayons() {
     List<RayonHierarchique> hierarchie = new ArrayList<>();
     
     // 1. Récupérer tous les rayons actifs
     List<Rayon> rayons = getAllRayonsActifs();
     
     for (Rayon rayon : rayons) {
         RayonHierarchique rayonHierarchique = new RayonHierarchique();
         rayonHierarchique.setRayon(rayon);
         
         // 2. Récupérer les catégories parentes de ce rayon
         List<MenuCategorie> categories = getCategoriesByRayonId(rayon.getId());
         rayonHierarchique.setCategories(categories);
         
         // 3. Pour chaque catégorie, récupérer ses sous-catégories
         Map<Integer, List<MenuCategorie>> sousCategoriesMap = new HashMap<>();
         for (MenuCategorie categorie : categories) {
             List<MenuCategorie> sousCategories = getSousCategoriesByParentId(categorie.getId());
             sousCategoriesMap.put(categorie.getId(), sousCategories);
         }
         rayonHierarchique.setSousCategoriesParCategorie(sousCategoriesMap);
         
         hierarchie.add(rayonHierarchique);
     }
     
     return hierarchie;
 }

 // Méthode pour récupérer les plats par sous-catégorie (optimisée)
 public List<Plat> getPlatsBySousCategorieId(int sousCategorieId) {
     return getPlatsBySubCategoryId(sousCategorieId);
 }

 // Méthode pour récupérer tous les plats avec leur hiérarchie complète
 public List<Plat> getAllPlatsWithHierarchy() {
     List<Plat> plats = getAllPlatsWithoutRestrictions();
     
     // Pour chaque plat, récupérer les détails de sa hiérarchie
     for (Plat plat : plats) {
         if (plat.getCategorieId() != null && plat.getCategorieId() > 0) {
             MenuCategorie categorie = getById(plat.getCategorieId());
             plat.setCategorie(categorie);
         }
         
         if (plat.getSousCategorieId() != null && plat.getSousCategorieId() > 0) {
             MenuCategorie sousCategorie = getById(plat.getSousCategorieId());
             plat.setSousCategorie(sousCategorie);
         }
         
         if (plat.getRayonId() != null && plat.getRayonId() > 0) {
             // Pour le rayon, on utilise un objet simple
             // (vous devriez avoir un RayonDAO pour getById)
         }
     }
     
     return plats;
 }

 // Méthode pour récupérer les catégories avec leurs sous-catégories pour un rayon
 public Map<MenuCategorie, List<MenuCategorie>> getCategoriesAvecSousCategories(int rayonId) {
     Map<MenuCategorie, List<MenuCategorie>> result = new HashMap<>();
     
     // Récupérer les catégories parentes du rayon
     List<MenuCategorie> categories = getCategoriesByRayonId(rayonId);
     
     for (MenuCategorie categorie : categories) {
         List<MenuCategorie> sousCategories = getSousCategoriesByParentId(categorie.getId());
         result.put(categorie, sousCategories);
     }
     
     return result;
 }

    public String supprimer(int id) {
        try (Connection conn = getConnection()) {
            // Vérifie si c'est une catégorie parente ou une sous-catégorie
            String checkSql = "SELECT PARENT_ID, NOM FROM MENU_CATEGORIE WHERE ID = ? AND STATUT != 'DELETED'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        Integer parentId = rs.getObject("PARENT_ID", Integer.class);
                        String nomCategorie = rs.getString("NOM");
                        
                        boolean isCategorieParente = (parentId == null);

                        // Vérifie si la catégorie contient des plats actifs
                        String checkPlats;
                        if (isCategorieParente) {
                            checkPlats = "SELECT COUNT(*) AS nb FROM PLAT WHERE STATUT != 'DELETED' AND " +
                                         "(CATEGORIE_ID = ? OR SOUS_CATEGORIE_ID IN (SELECT ID FROM MENU_CATEGORIE WHERE PARENT_ID = ?))";
                        } else {
                            checkPlats = "SELECT COUNT(*) AS nb FROM PLAT WHERE STATUT != 'DELETED' AND SOUS_CATEGORIE_ID = ?";
                        }

                        try (PreparedStatement stmtCheckPlats = conn.prepareStatement(checkPlats)) {
                            stmtCheckPlats.setInt(1, id);
                            if (isCategorieParente) {
                                stmtCheckPlats.setInt(2, id);
                            }

                            try (ResultSet rsPlats = stmtCheckPlats.executeQuery()) {
                                if (rsPlats.next() && rsPlats.getInt("nb") > 0) {
                                    if (isCategorieParente) {
                                        return "Impossible de supprimer la catégorie '" + nomCategorie + "' car elle contient des plats actifs. Veuillez d'abord supprimer ou déplacer les plats associés.";
                                    } else {
                                        return "Impossible de supprimer la sous-catégorie '" + nomCategorie + "' car elle contient des plats actifs. Veuillez d'abord supprimer ou déplacer les plats associés.";
                                    }
                                }
                            }
                        }

                        // Si aucun plat n'est présent, on peut supprimer
                        String deleteCategorie = "UPDATE MENU_CATEGORIE SET STATUT = 'DELETED', UPDATE_DATE = NOW() WHERE ID = ?";
                        try (PreparedStatement stmtDelete = conn.prepareStatement(deleteCategorie)) {
                            stmtDelete.setInt(1, id);
                            int rowsAffected = stmtDelete.executeUpdate();
                            
                            if (rowsAffected == 0) {
                                return "Catégorie non trouvée ou déjà supprimée.";
                            }
                        }

                        // Si c'est une catégorie parente, on supprime aussi ses sous-catégories
                        if (isCategorieParente) {
                            String deleteSousCat = "UPDATE MENU_CATEGORIE SET STATUT = 'DELETED', UPDATE_DATE = NOW() WHERE PARENT_ID = ?";
                            try (PreparedStatement stmtDeleteSous = conn.prepareStatement(deleteSousCat)) {
                                stmtDeleteSous.setInt(1, id);
                                stmtDeleteSous.executeUpdate();
                            }
                        }

                        return "SUCCESS:" + (isCategorieParente ? "Catégorie" : "Sous-catégorie") + " '" + nomCategorie + "' supprimée avec succès !";
                    } else {
                        return "Catégorie non trouvée ou déjà supprimée.";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur technique lors de la suppression : " + e.getMessage();
        }
    }

    public MenuCategorie getById(int id) {
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.ID = ? AND mc.STATUT = 'VISIBLE'";
        MenuCategorie menuCategorie = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                menuCategorie = mapResultSetToMenuCategorie(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menuCategorie;
    }

    public List<MenuCategorie> getAll() {
        List<MenuCategorie> liste = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.STATUT = 'VISIBLE' ORDER BY mc.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                liste.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }
    
    public List<MenuCategorie> getCategoriesParentes() {
        List<MenuCategorie> categoriesParentes = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.PARENT_ID IS NULL AND mc.STATUT = 'VISIBLE' ORDER BY mc.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                categoriesParentes.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoriesParentes;
    }
    
    public List<MenuCategorie> getSousCategoriesByParentId(int parentId) {
        List<MenuCategorie> sousCategories = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.PARENT_ID = ? AND mc.STATUT = 'VISIBLE' ORDER BY mc.CREATION_DATE ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                sousCategories.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sousCategories;
    }

    
    public List<MenuCategorie> getAllSubCategory() {
        List<MenuCategorie> sousCategories = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.STATUT = 'VISIBLE' AND mc.PARENT_ID IS NOT NULL ORDER BY mc.CREATION_DATE ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                sousCategories.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sousCategories;
    }

    // Méthode utilitaire pour mapper le ResultSet vers MenuCategorie
//    private MenuCategorie mapResultSetToMenuCategorie(ResultSet rs) throws SQLException {
//        MenuCategorie menuCategorie = new MenuCategorie();
//        menuCategorie.setId(rs.getInt("ID"));
//        menuCategorie.setNom(rs.getString("NOM"));
//        menuCategorie.setDescription(rs.getString("DESCRIPTION"));
//        menuCategorie.setStatut(rs.getString("STATUT"));
//        menuCategorie.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime());
//        menuCategorie.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime());
//        menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//        menuCategorie.setImageUrl(rs.getString("IMAGE_URL"));
//
//        // Gestion du parentId
//        Integer parentId = (Integer) rs.getObject("PARENT_ID");
//        menuCategorie.setParentId(parentId);
//
//        // Gestion du Rayon - seulement si RAYON_ID n'est pas null
//        Integer rayonId = (Integer) rs.getObject("RAYON_ID");
//        if (rayonId != null) {
//            Rayon rayon = new Rayon();
//            rayon.setId(rs.getInt("rayon_id"));
//            rayon.setNom(rs.getString("rayon_nom"));
//            rayon.setDescription(rs.getString("rayon_description"));
//            rayon.setStatut(rs.getString("rayon_statut"));
//            rayon.setImageUrl(rs.getString("rayon_image_url"));
//            rayon.setCreationDate(rs.getTimestamp("rayon_creation_date").toLocalDateTime());
//            rayon.setUpdateDate(rs.getTimestamp("rayon_update_date").toLocalDateTime());
//            rayon.setUtilisateurId(rs.getInt("rayon_utilisateur_id"));
//            menuCategorie.setRayon(rayon);
//        }
//
//        return menuCategorie;
//    }
 // Méthode utilitaire pour mapper le ResultSet vers MenuCategorie
    private MenuCategorie mapResultSetToMenuCategorie(ResultSet rs) throws SQLException {
        MenuCategorie menuCategorie = new MenuCategorie();
        
        try {
            // ID
            if (hasColumn(rs, "ID")) {
                menuCategorie.setId(rs.getInt("ID"));
            }
            
            // Nom
            if (hasColumn(rs, "NOM")) {
                menuCategorie.setNom(getSafeString(rs, "NOM"));
            }
            
            // Description
            if (hasColumn(rs, "DESCRIPTION")) {
                menuCategorie.setDescription(getSafeString(rs, "DESCRIPTION"));
            }
            
            // Statut
            if (hasColumn(rs, "STATUT")) {
                menuCategorie.setStatut(getSafeString(rs, "STATUT", "VISIBLE"));
            }
            
            // Dates
            if (hasColumn(rs, "CREATION_DATE")) {
                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                menuCategorie.setCreationDate(creationDate != null ? creationDate.toLocalDateTime() : LocalDateTime.now());
            } else {
                menuCategorie.setCreationDate(LocalDateTime.now());
            }
            
            if (hasColumn(rs, "UPDATE_DATE")) {
                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                menuCategorie.setUpdateDate(updateDate != null ? updateDate.toLocalDateTime() : LocalDateTime.now());
            } else {
                menuCategorie.setUpdateDate(LocalDateTime.now());
            }
            
            // Utilisateur ID
            if (hasColumn(rs, "UTILISATEUR_ID")) {
                int utilisateurId = rs.getInt("UTILISATEUR_ID");
                menuCategorie.setUtilisateurId(rs.wasNull() ? 0 : utilisateurId);
            }
            
            // Image URL
            if (hasColumn(rs, "IMAGE_URL")) {
                menuCategorie.setImageUrl(getSafeString(rs, "IMAGE_URL"));
            }

            // Parent ID
            if (hasColumn(rs, "PARENT_ID")) {
                Integer parentId = (Integer) rs.getObject("PARENT_ID");
                menuCategorie.setParentId(parentId);
            }

            // Rayon
            if (hasColumn(rs, "RAYON_ID")) {
                Integer rayonId = (Integer) rs.getObject("RAYON_ID");
                if (rayonId != null && !rs.wasNull() && hasColumn(rs, "rayon_id")) {
                    Rayon rayon = new Rayon();
                    rayon.setId(rs.getInt("rayon_id"));
                    rayon.setNom(getSafeString(rs, "rayon_nom"));
                    rayon.setDescription(getSafeString(rs, "rayon_description"));
                    rayon.setStatut(getSafeString(rs, "rayon_statut", "VISIBLE"));
                    rayon.setImageUrl(getSafeString(rs, "rayon_image_url"));
                    
                    Timestamp rayonCreationDate = rs.getTimestamp("rayon_creation_date");
                    rayon.setCreationDate(rayonCreationDate != null ? rayonCreationDate.toLocalDateTime() : LocalDateTime.now());
                    
                    Timestamp rayonUpdateDate = rs.getTimestamp("rayon_update_date");
                    rayon.setUpdateDate(rayonUpdateDate != null ? rayonUpdateDate.toLocalDateTime() : LocalDateTime.now());
                    
                    int rayonUtilisateurId = rs.getInt("rayon_utilisateur_id");
                    rayon.setUtilisateurId(rs.wasNull() ? 0 : rayonUtilisateurId);
                    
                    menuCategorie.setRayon(rayon);
                } else {
                    menuCategorie.setRayon(null);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du mapping ResultSet vers MenuCategorie: " + e.getMessage());
            // Vous pouvez logger l'erreur mais continuer avec un objet partiellement rempli
        }

        return menuCategorie;
    }

    // Méthodes utilitaires pour une gestion sécurisée
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String getSafeString(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : value;
    }

    private String getSafeString(ResultSet rs, String columnName, String defaultValue) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? defaultValue : value;
    }

    // Méthode pour récupérer les catégories par rayon
    public List<MenuCategorie> getCategoriesByRayonId(int rayonId) {
        List<MenuCategorie> categories = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.RAYON_ID = ? AND mc.STATUT = 'VISIBLE' ORDER BY mc.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rayonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                categories.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<MenuCategorie> getCategoriesSansRayon() {
        List<MenuCategorie> categories = new ArrayList<>();
        String sql = "SELECT mc.*, r.ID as rayon_id, r.NOM as rayon_nom, r.DESCRIPTION as rayon_description, " +
                     "r.STATUT as rayon_statut, r.IMAGE_URL as rayon_image_url, r.CREATION_DATE as rayon_creation_date, " +
                     "r.UPDATE_DATE as rayon_update_date, r.UTILISATEUR_ID as rayon_utilisateur_id " +
                     "FROM MENU_CATEGORIE mc " +
                     "LEFT JOIN RAYON r ON mc.RAYON_ID = r.ID " +
                     "WHERE mc.STATUT = 'VISIBLE' AND mc.PARENT_ID IS NULL AND mc.RAYON_ID IS NULL " +
                     "ORDER BY mc.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MenuCategorie menuCategorie = mapResultSetToMenuCategorie(rs);
                categories.add(menuCategorie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
	/*
	 * public void ajouter(MenuCategorie menuCategorie) { String sql =
	 * "INSERT INTO MENU_CATEGORIE (NOM, DESCRIPTION, CREATION_DATE, UPDATE_DATE, UTILISATEUR_ID, STATUT, PARENT_ID, IMAGE_URL) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql)) {
	 * 
	 * stmt.setString(1, menuCategorie.getNom()); stmt.setString(2,
	 * menuCategorie.getDescription()); stmt.setTimestamp(3,
	 * Timestamp.valueOf(menuCategorie.getCreationDate())); stmt.setTimestamp(4,
	 * Timestamp.valueOf(menuCategorie.getUpdateDate())); stmt.setInt(5,
	 * menuCategorie.getUtilisateurId()); stmt.setString(6,
	 * menuCategorie.getStatut());
	 * 
	 * // Si parentId est null, on passe null dans le statement if
	 * (menuCategorie.getParentId() != null) { stmt.setInt(7,
	 * menuCategorie.getParentId());
	 * 
	 * // Validation pour s'assurer que l'imageUrl est obligatoire pour les
	 * catégories parentes // if (categorie.getImageUrl() == null ||
	 * categorie.getImageUrl().trim().isEmpty()) { // throw new
	 * IllegalArgumentException("L'imageUrl est obligatoire pour les catégories parentes."
	 * ); // } } else { stmt.setNull(7, Types.INTEGER); // Pas de validation
	 * nécessaire pour l'imageUrl si parentId est null }
	 * 
	 * stmt.setString(8, menuCategorie.getImageUrl()); // Ajout de l'imageUrl dans
	 * la requête
	 * 
	 * stmt.executeUpdate();
	 * 
	 * } catch (SQLException | IllegalArgumentException e) { e.printStackTrace(); }
	 * }
	 * 
	 * 
	 * public void modifier(MenuCategorie menuCategorie) { String sql =
	 * "UPDATE MENU_CATEGORIE SET NOM = ?, DESCRIPTION = ?, UPDATE_DATE = ?, UTILISATEUR_ID = ?, STATUT = ?, PARENT_ID = ?, IMAGE_URL = ? WHERE ID = ?"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql)) {
	 * 
	 * stmt.setString(1, menuCategorie.getNom()); stmt.setString(2,
	 * menuCategorie.getDescription()); stmt.setTimestamp(3,
	 * Timestamp.valueOf(menuCategorie.getUpdateDate())); stmt.setInt(4,
	 * menuCategorie.getUtilisateurId()); stmt.setString(5,
	 * menuCategorie.getStatut());
	 * 
	 * // Si parentId est null, on passe null dans le statement if
	 * (menuCategorie.getParentId() != null) { stmt.setInt(6,
	 * menuCategorie.getParentId());
	 * 
	 * // Validation pour s'assurer que l'imageUrl est obligatoire pour les
	 * catégories parentes // if (categorie.getImageUrl() == null ||
	 * categorie.getImageUrl().trim().isEmpty()) { // throw new
	 * IllegalArgumentException("L'imageUrl est obligatoire pour les catégories parentes."
	 * ); // } } else { stmt.setNull(6, Types.INTEGER); // Pas de validation
	 * nécessaire pour l'imageUrl si parentId est null }
	 * 
	 * stmt.setString(7, menuCategorie.getImageUrl()); // Ajout de l'imageUrl dans
	 * la requête
	 * 
	 * stmt.setInt(8, menuCategorie.getId());
	 * 
	 * stmt.executeUpdate();
	 * 
	 * } catch (SQLException | IllegalArgumentException e) { e.printStackTrace(); }
	 * }
	 * 
	 * 
	 * 
	 * public String supprimer(int id) { try (Connection conn = getConnection()) {
	 * 
	 * // Vérifie si c'est une catégorie parente ou une sous-catégorie String
	 * checkSql =
	 * "SELECT PARENT_ID, NOM FROM MENU_CATEGORIE WHERE ID = ? AND STATUT != 'DELETED'"
	 * ; try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	 * checkStmt.setInt(1, id); try (ResultSet rs = checkStmt.executeQuery()) { if
	 * (rs.next()) { // CORRECTION ICI : Utiliser rs.wasNull() pour vérifier les
	 * NULL Integer parentId = rs.getObject("PARENT_ID", Integer.class); String
	 * nomCategorie = rs.getString("NOM");
	 * 
	 * boolean isCategorieParente = (parentId == null); // Catégorie parente si
	 * PARENT_ID est NULL
	 * 
	 * // Vérifie si la catégorie (ou sous-catégorie) contient des plats actifs
	 * String checkPlats; if (isCategorieParente) { // Catégorie parente : on
	 * vérifie tous les plats dans la catégorie et ses sous-catégories checkPlats =
	 * "SELECT COUNT(*) AS nb FROM PLAT WHERE STATUT != 'DELETED' AND " +
	 * "(CATEGORIE_ID = ? OR SOUS_CATEGORIE_ID IN (SELECT ID FROM MENU_CATEGORIE WHERE PARENT_ID = ?))"
	 * ; } else { // Sous-catégorie : on vérifie seulement les plats liés à cette
	 * sous-catégorie checkPlats =
	 * "SELECT COUNT(*) AS nb FROM PLAT WHERE STATUT != 'DELETED' AND SOUS_CATEGORIE_ID = ?"
	 * ; }
	 * 
	 * try (PreparedStatement stmtCheckPlats = conn.prepareStatement(checkPlats)) {
	 * stmtCheckPlats.setInt(1, id); if (isCategorieParente) {
	 * stmtCheckPlats.setInt(2, id); // Deuxième paramètre pour la sous-requête }
	 * 
	 * try (ResultSet rsPlats = stmtCheckPlats.executeQuery()) { if (rsPlats.next()
	 * && rsPlats.getInt("nb") > 0) { // La catégorie contient des plats → on
	 * empêche la suppression if (isCategorieParente) { return
	 * "Impossible de supprimer la catégorie '" + nomCategorie +
	 * "' car elle contient des plats actifs. Veuillez d'abord supprimer ou déplacer les plats associés."
	 * ; } else { return "Impossible de supprimer la sous-catégorie '" +
	 * nomCategorie +
	 * "' car elle contient des plats actifs. Veuillez d'abord supprimer ou déplacer les plats associés."
	 * ; } } } }
	 * 
	 * // Si aucun plat n'est présent, on peut supprimer la catégorie ou
	 * sous-catégorie String deleteCategorie =
	 * "UPDATE MENU_CATEGORIE SET STATUT = 'DELETED', UPDATE_DATE = NOW() WHERE ID = ?"
	 * ; try (PreparedStatement stmtDelete = conn.prepareStatement(deleteCategorie))
	 * { stmtDelete.setInt(1, id); int rowsAffected = stmtDelete.executeUpdate();
	 * 
	 * if (rowsAffected == 0) { return "Catégorie non trouvée ou déjà supprimée."; }
	 * }
	 * 
	 * // Si c'est une catégorie parente, on peut aussi supprimer ses
	 * sous-catégories if (isCategorieParente) { String deleteSousCat =
	 * "UPDATE MENU_CATEGORIE SET STATUT = 'DELETED', UPDATE_DATE = NOW() WHERE PARENT_ID = ?"
	 * ; try (PreparedStatement stmtDeleteSous =
	 * conn.prepareStatement(deleteSousCat)) { stmtDeleteSous.setInt(1, id);
	 * stmtDeleteSous.executeUpdate(); } }
	 * 
	 * return "SUCCESS:" + (isCategorieParente ? "Catégorie" : "Sous-catégorie") +
	 * " '" + nomCategorie + "' supprimée avec succès !"; } else { return
	 * "Catégorie non trouvée ou déjà supprimée."; } } }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); return
	 * "Erreur technique lors de la suppression : " + e.getMessage(); } }
	 * 
	 * 
	 * public MenuCategorie getById(int id) { String sql =
	 * "SELECT * FROM MENU_CATEGORIE WHERE ID = ? AND STATUT = 'visible'";
	 * MenuCategorie menuCategorie = null;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql)) {
	 * 
	 * stmt.setInt(1, id); ResultSet rs = stmt.executeQuery();
	 * 
	 * if (rs.next()) { menuCategorie = new MenuCategorie();
	 * menuCategorie.setId(rs.getInt("ID"));
	 * menuCategorie.setNom(rs.getString("NOM"));
	 * menuCategorie.setDescription(rs.getString("DESCRIPTION"));
	 * menuCategorie.setCreationDate(rs.getTimestamp("CREATION_DATE").
	 * toLocalDateTime());
	 * menuCategorie.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime())
	 * ; menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	 * menuCategorie.setStatut(rs.getString("STATUT"));
	 * menuCategorie.setParentId((Integer) rs.getObject("PARENT_ID")); // peut être
	 * null menuCategorie.setImageUrl(rs.getString("IMAGE_URL")); // Récupérer l'URL
	 * de l'image }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); }
	 * 
	 * return menuCategorie; }
	 * 
	 * public List<MenuCategorie> getAll() { List<MenuCategorie> liste = new
	 * ArrayList<>(); String sql =
	 * "SELECT * FROM MENU_CATEGORIE WHERE STATUT = 'VISIBLE' ORDER BY CREATION_DATE DESC"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
	 * 
	 * while (rs.next()) { MenuCategorie menuCategorie = new MenuCategorie();
	 * menuCategorie.setId(rs.getInt("ID"));
	 * menuCategorie.setNom(rs.getString("NOM"));
	 * menuCategorie.setDescription(rs.getString("DESCRIPTION"));
	 * menuCategorie.setCreationDate(rs.getTimestamp("CREATION_DATE").
	 * toLocalDateTime());
	 * menuCategorie.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime())
	 * ; menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	 * menuCategorie.setStatut(rs.getString("STATUT"));
	 * menuCategorie.setParentId((Integer) rs.getObject("PARENT_ID")); // nullable
	 * menuCategorie.setImageUrl(rs.getString("IMAGE_URL")); // Récupérer l'URL de
	 * l'image liste.add(menuCategorie); }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); }
	 * 
	 * return liste; }
	 * 
	 * public List<MenuCategorie> getCategoriesParentes() { List<MenuCategorie>
	 * categoriesParentes = new ArrayList<>(); String sql =
	 * "SELECT * FROM MENU_CATEGORIE WHERE PARENT_ID IS NULL AND STATUT = 'VISIBLE' ORDER BY CREATION_DATE DESC"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
	 * 
	 * while (rs.next()) { MenuCategorie menuCategorie = new MenuCategorie();
	 * menuCategorie.setId(rs.getInt("ID"));
	 * menuCategorie.setNom(rs.getString("NOM"));
	 * menuCategorie.setDescription(rs.getString("DESCRIPTION"));
	 * menuCategorie.setStatut(rs.getString("STATUT"));
	 * menuCategorie.setCreationDate(rs.getTimestamp("CREATION_DATE").
	 * toLocalDateTime());
	 * menuCategorie.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime())
	 * ; menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	 * menuCategorie.setImageUrl(rs.getString("IMAGE_URL")); // Récupérer l'URL de
	 * l'image
	 * 
	 * int parentId = rs.getInt("PARENT_ID"); if (!rs.wasNull()) {
	 * menuCategorie.setParentId(parentId); } else {
	 * menuCategorie.setParentId(null); }
	 * 
	 * categoriesParentes.add(menuCategorie); }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); }
	 * 
	 * return categoriesParentes; }
	 * 
	 * public List<MenuCategorie> getSousCategoriesByParentId(int parentId) {
	 * List<MenuCategorie> sousCategories = new ArrayList<>(); String sql =
	 * "SELECT * FROM MENU_CATEGORIE WHERE PARENT_ID = ? AND STATUT = 'VISIBLE' ORDER BY CREATION_DATE ASC"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql)) {
	 * 
	 * stmt.setInt(1, parentId); System.out.println("SQL : " + stmt.toString()); //
	 * Log de la requête SQL
	 * 
	 * ResultSet rs = stmt.executeQuery();
	 * 
	 * while (rs.next()) { MenuCategorie menuCategorie = new MenuCategorie();
	 * menuCategorie.setId(rs.getInt("ID"));
	 * menuCategorie.setNom(rs.getString("NOM"));
	 * menuCategorie.setDescription(rs.getString("DESCRIPTION"));
	 * menuCategorie.setStatut(rs.getString("STATUT"));
	 * menuCategorie.setCreationDate(rs.getTimestamp("CREATION_DATE").
	 * toLocalDateTime());
	 * menuCategorie.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime())
	 * ; menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	 * menuCategorie.setImageUrl(rs.getString("IMAGE_URL")); // Récupérer l'URL de
	 * l'image
	 * 
	 * int parentIdResult = rs.getInt("PARENT_ID"); if (!rs.wasNull()) {
	 * menuCategorie.setParentId(parentIdResult); }
	 * 
	 * sousCategories.add(menuCategorie); }
	 * System.out.println("Sous-catégories récupérées pour parentId " + parentId +
	 * ": " + sousCategories);
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); } return sousCategories; }
	 * 
	 * 
	 * public List<MenuCategorie> getAllSubCategory() { List<MenuCategorie>
	 * sousCategories = new ArrayList<>(); String sql =
	 * "SELECT * FROM MENU_CATEGORIE WHERE STATUT = 'VISIBLE' AND PARENT_ID IS NOT NULL ORDER BY CREATION_DATE ASC"
	 * ;
	 * 
	 * try (Connection conn = getConnection(); PreparedStatement stmt =
	 * conn.prepareStatement(sql)) {
	 * 
	 * System.out.println("SQL : " + sql);
	 * 
	 * ResultSet rs = stmt.executeQuery();
	 * 
	 * while (rs.next()) { MenuCategorie menuCategorie = new MenuCategorie();
	 * menuCategorie.setId(rs.getInt("ID"));
	 * menuCategorie.setNom(rs.getString("NOM"));
	 * menuCategorie.setDescription(rs.getString("DESCRIPTION"));
	 * menuCategorie.setStatut(rs.getString("STATUT"));
	 * 
	 * // Correction des dates Timestamp creationDate =
	 * rs.getTimestamp("CREATION_DATE"); if (creationDate != null) {
	 * menuCategorie.setCreationDate(creationDate.toLocalDateTime()); }
	 * 
	 * Timestamp updateDate = rs.getTimestamp("UPDATE_DATE"); if (updateDate !=
	 * null) { menuCategorie.setUpdateDate(updateDate.toLocalDateTime()); }
	 * 
	 * menuCategorie.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	 * menuCategorie.setImageUrl(rs.getString("IMAGE_URL"));
	 * 
	 * int parentIdResult = rs.getInt("PARENT_ID"); if (!rs.wasNull()) {
	 * menuCategorie.setParentId(parentIdResult); }
	 * 
	 * sousCategories.add(menuCategorie); }
	 * 
	 * System.out.println("Sous-catégories récupérées: " + sousCategories.size());
	 * 
	 * } catch (SQLException e) { System.err.println("Erreur SQL: " +
	 * e.getMessage()); e.printStackTrace(); } return sousCategories; }
	 */

  
//    public List<Plat> getThePlatsByCategoryId(int categorieId) {
//        List<Plat> plats = new ArrayList<>();
//
//        String sql = "SELECT p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, p.CATEGORIE_ID, p.QTE_POINTS, " +
//                     "p.SOUS_CATEGORIE_ID, p.STATUT, p.CREATION_DATE, p.UPDATE_DATE, p.UTILISATEUR_ID, " +
//                     "p.PRODUIT_ID, " +
//                     "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, pr.QTE_POINTS AS produit_qtePoints, " +
//                     "pr.IMAGE_URL AS produit_image, pr.PRIX_VENTE AS produit_prix " +
//                     "FROM PLAT p " +
//                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
//                     "WHERE p.STATUT = 'VISIBLE' AND p.CATEGORIE_ID = ? " +
//                     "ORDER BY p.NOM";
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, categorieId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Plat plat = new Plat();
//                plat.setId(rs.getInt("ID"));
//                plat.setNom(rs.getString("NOM"));
//                plat.setDescription(rs.getString("DESCRIPTION"));
//                plat.setPrix(rs.getDouble("PRIX"));
//                plat.setImage(rs.getString("IMAGE_URL"));
//                plat.setQtePoints(rs.getInt("QTE_POINTS"));
//                plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
//                plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
//                plat.setStatut(rs.getString("STATUT"));
//
//                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
//                if (creationDate != null) {
//                    plat.setCreationDate(creationDate.toLocalDateTime());
//                }
//
//                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
//                if (updateDate != null) {
//                    plat.setUpdateDate(updateDate.toLocalDateTime());
//                }
//
//                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//
//                int produitId = rs.getInt("PRODUIT_ID");
//                if (!rs.wasNull()) {
//                    Produit produit = new Produit();
//                    produit.setId(produitId);
//                    produit.setNom(rs.getString("produit_nom"));
//                    produit.setDescription(rs.getString("produit_description"));
//                    produit.setImageUrl(rs.getString("produit_image"));
//                    produit.setPrixVente(rs.getBigDecimal("produit_prix"));
//                    produit.setQtePoints(rs.getInt("produit_qtePoints"));
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    if (plat.getNom() == null) {
//                        plat.setNom(produit.getNom());
//                    }
//                } else {
//                    plat.setProduit(null);
//                }
//
//                plats.add(plat);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return plats;
//    }
    
    public List<Plat> getPlatsByRayonId(int rayonId) {
        List<Plat> plats = new ArrayList<>();

        String sql = "SELECT p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, p.CATEGORIE_ID, " +
                "p.QTE_POINTS, p.SOUS_CATEGORIE_ID, p.STATUT, p.CREATION_DATE, p.UPDATE_DATE, " +
                "p.UTILISATEUR_ID, p.PRODUIT_ID, " +
                "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, " +
                "pr.QTE_POINTS AS produit_qtePoints, pr.IMAGE_URL AS produit_image, pr.PRIX_VENTE AS produit_prix " +
                "FROM PLAT p " +
                "INNER JOIN MENU_CATEGORIE mc ON mc.ID = p.CATEGORIE_ID " +
                "INNER JOIN RAYON r ON r.ID = mc.RAYON_ID " +
                "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                "WHERE p.STATUT = 'VISIBLE' AND mc.STATUT = 'VISIBLE' AND r.STATUT = 'VISIBLE' " +
                "AND r.ID = ? " +
                "ORDER BY p.NOM";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rayonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setId(rs.getInt("ID"));
                plat.setNom(rs.getString("NOM"));
                plat.setDescription(rs.getString("DESCRIPTION"));
                plat.setPrix(rs.getDouble("PRIX"));
                plat.setImage(rs.getString("IMAGE_URL"));
                plat.setQtePoints(rs.getInt("QTE_POINTS"));
                plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
                plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
                plat.setStatut(rs.getString("STATUT"));

                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) {
                    plat.setCreationDate(creationDate.toLocalDateTime());
                }

                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) {
                    plat.setUpdateDate(updateDate.toLocalDateTime());
                }

                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

                int produitId = rs.getInt("PRODUIT_ID");
                if (!rs.wasNull()) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom(rs.getString("produit_nom"));
                    produit.setDescription(rs.getString("produit_description"));
                    produit.setImageUrl(rs.getString("produit_image"));
                    produit.setPrixVente(rs.getBigDecimal("produit_prix"));
                    produit.setQtePoints(rs.getInt("produit_qtePoints"));
                    plat.setProduit(produit);
                    plat.setTypePlat("PRODUIT");

                    if (plat.getNom() == null) {
                        plat.setNom(produit.getNom());
                    }
                }

                plats.add(plat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plats;
    }
    
    public List<Plat> getAllPlatsWithoutRestrictions() {
        List<Plat> plats = new ArrayList<>();

        String sql = "SELECT p.ID, p.NOM AS plat_nom, p.DESCRIPTION AS plat_description, p.PRIX AS plat_prix, " +
                    "p.IMAGE_URL AS plat_image, p.RAYON_ID AS plat_rayon_id, p.CATEGORIE_ID AS plat_categorie_id, " +
                    "p.SOUS_CATEGORIE_ID AS plat_sous_categorie_id, p.STATUT, p.QTE_POINTS AS plat_qte_points, " +
                    "p.CREATION_DATE, p.UPDATE_DATE, p.UTILISATEUR_ID, p.PRODUIT_ID AS plat_produit_id, " +
                    "r.ID AS rayon_id, r.NOM AS rayon_nom, r.STATUT AS rayon_statut, " +
                    "c.ID AS categorie_id, c.NOM AS categorie_nom, c.STATUT AS categorie_statut, " +
                    "sc.ID AS sous_categorie_id, sc.NOM AS sous_categorie_nom, sc.STATUT AS sous_categorie_statut, " +
                    "pr.ID AS produit_id, pr.IMAGE_URL AS produit_image, " +
                    "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, " +
                    "pr.QTE_POINTS AS produit_qte_points, pr.PRIX_VENTE AS produit_prix, " +
                    "pr.CATEGORIE_ID AS produit_categorie_id, " +
                    "pr.SOUS_CATEGORIE_ID AS produit_sous_categorie_id, " +
                    "pr.RAYON_ID AS produit_rayon_id, " +
                    "pr_cat.NOM AS produit_categorie_nom, pr_cat.STATUT AS produit_categorie_statut, " +
                    "pr_sous.NOM AS produit_sous_categorie_nom, pr_sous.STATUT AS produit_sous_categorie_statut, " +
                    "r_pr.ID AS produit_rayon_id_full, r_pr.NOM AS produit_rayon_nom, r_pr.STATUT AS produit_rayon_statut " +
                    "FROM PLAT p " +
                    "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID " +
                    "LEFT JOIN MENU_CATEGORIE c ON p.CATEGORIE_ID = c.ID " +
                    "LEFT JOIN MENU_CATEGORIE sc ON p.SOUS_CATEGORIE_ID = sc.ID " +
                    "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                    "LEFT JOIN MENU_CATEGORIE pr_cat ON pr.CATEGORIE_ID = pr_cat.ID " +
                    "LEFT JOIN MENU_CATEGORIE pr_sous ON pr.SOUS_CATEGORIE_ID = pr_sous.ID " +
                    "LEFT JOIN RAYON r_pr ON pr.RAYON_ID = r_pr.ID " +
                    "WHERE (p.STATUT = 'visible' OR p.STATUT = 'VISIBLE') " +
                    "ORDER BY p.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // DEBUG: Afficher les colonnes disponibles
            try {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                System.out.println("=== COLONNES DISPONIBLES DANS LE RESULTSET ===");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("Colonne " + i + ": " + metaData.getColumnName(i) + 
                                     " - Type: " + metaData.getColumnTypeName(i));
                }
                System.out.println("==============================================");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            while (rs.next()) {
                Plat plat = new Plat();
                
                // Informations de base du plat
                plat.setId(rs.getInt("ID"));
                plat.setNom(getStringOrNull(rs, "plat_nom"));
                plat.setDescription(getStringOrNull(rs, "plat_description"));
                plat.setPrix(rs.getDouble("plat_prix"));
                plat.setImage(getStringOrNull(rs, "plat_image"));
                plat.setQtePoints(rs.getInt("plat_qte_points"));
                plat.setStatut(rs.getString("STATUT"));
                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                
                // IDs du plat
                plat.setCategorieId(getIntOrNull(rs, "plat_categorie_id"));
                plat.setSousCategorieId(getIntOrNull(rs, "plat_sous_categorie_id"));
                plat.setRayonId(getIntOrNull(rs, "plat_rayon_id"));
                plat.setProductId(getIntOrNull(rs, "plat_produit_id"));

                // Dates
                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) {
                    plat.setCreationDate(creationDate.toLocalDateTime());
                }

                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) {
                    plat.setUpdateDate(updateDate.toLocalDateTime());
                }

                // Gestion du produit associé
                Integer produitId = getIntOrNull(rs, "produit_id");
                if (produitId != null && produitId > 0) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom(getStringOrNull(rs, "produit_nom"));
                    produit.setDescription(getStringOrNull(rs, "produit_description"));
                    produit.setImageUrl(getStringOrNull(rs, "produit_image"));
                    
                    BigDecimal produitPrix = rs.getBigDecimal("produit_prix");
                    produit.setPrixVente(produitPrix != null ? produitPrix : BigDecimal.ZERO);
                    
                    produit.setQtePoints(rs.getInt("produit_qte_points"));
                    
                    // Catégories du PRODUIT
                    produit.setCategorieId(rs.getInt("produit_categorie_id"));
                    produit.setSousCategorieId(rs.getInt("produit_sous_categorie_id"));
                    produit.setRayonId(rs.getInt("produit_rayon_id"));
                    
                    // Informations complètes des catégories du produit
                    if (produit.getCategorieId() > 0) {
                        MenuCategorie produitCategorie = new MenuCategorie();
                        produitCategorie.setId(produit.getCategorieId());
                        produitCategorie.setNom(getStringOrNull(rs, "produit_categorie_nom"));
                        produitCategorie.setStatut(getStringOrNull(rs, "produit_categorie_statut"));
                        produit.setCategorie(produitCategorie);
                    }
                    
                    if (produit.getSousCategorieId() > 0) {
                        MenuCategorie produitSousCategorie = new MenuCategorie();
                        produitSousCategorie.setId(produit.getSousCategorieId());
                        produitSousCategorie.setNom(getStringOrNull(rs, "produit_sous_categorie_nom"));
                        produitSousCategorie.setStatut(getStringOrNull(rs, "produit_sous_categorie_statut"));
                        produit.setSousCategorie(produitSousCategorie);
                    }
                    
                    if (produit.getRayonId() > 0) {
                        Rayon produitRayon = new Rayon();
                        produitRayon.setId(produit.getRayonId());
                        produitRayon.setNom(getStringOrNull(rs, "produit_rayon_nom"));
                        produitRayon.setStatut(getStringOrNull(rs, "produit_rayon_statut"));
                        produit.setRayon(produitRayon);
                    }
                    
                    plat.setProduit(produit);
                    plat.setTypePlat("PRODUIT");

                    // Si le plat n'a pas de nom, utiliser celui du produit
                    if (plat.getNom() == null || plat.getNom().trim().isEmpty()) {
                        plat.setNom(produit.getNom());
                    }
                } else {
                    plat.setTypePlat("PLAT_SIMPLE");
                }

                // Créer les objets pour le plat (sans produit ou en complément)
                Rayon rayon = new Rayon();
                rayon.setId(getIntOrZero(rs, "rayon_id"));
                rayon.setNom(getStringOrNull(rs, "rayon_nom"));
                rayon.setStatut(getStringOrNull(rs, "rayon_statut"));
                plat.setRayon(rayon);

                MenuCategorie categorie = new MenuCategorie();
                categorie.setId(getIntOrZero(rs, "categorie_id"));
                categorie.setNom(getStringOrNull(rs, "categorie_nom"));
                categorie.setStatut(getStringOrNull(rs, "categorie_statut"));
                plat.setCategorie(categorie);

                MenuCategorie sousCategorieObj = new MenuCategorie();
                sousCategorieObj.setId(getIntOrZero(rs, "sous_categorie_id"));
                sousCategorieObj.setNom(getStringOrNull(rs, "sous_categorie_nom"));
                sousCategorieObj.setStatut(getStringOrNull(rs, "sous_categorie_statut"));
                plat.setSousCategorie(sousCategorieObj);

                // LOG IMPORTANT: Vérifier ce que contient le produit
                if (plat.getProduit() != null) {
                    System.out.println("DEBUG Plat " + plat.getId() + " - Produit: " + plat.getProduit().getNom());
                    System.out.println("  Produit catégorie ID: " + plat.getProduit().getCategorieId());
                    System.out.println("  Produit catégorie obj: " + (plat.getProduit().getCategorie() != null ? plat.getProduit().getCategorie().getNom() : "NULL"));
                    System.out.println("  Produit sous-catégorie obj: " + (plat.getProduit().getSousCategorie() != null ? plat.getProduit().getSousCategorie().getNom() : "NULL"));
                }

                plats.add(plat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur dans getAllPlatsWithoutRestrictions: " + e.getMessage());
            System.err.println("SQL Query: " + sql);
        }

        return plats;
    }

    // Ajoutez ces méthodes utilitaires
    private Integer getIntOrNull(ResultSet rs, String columnName) {
        try {
            Integer value = rs.getObject(columnName, Integer.class);
            return rs.wasNull() ? null : value;
        } catch (SQLException e) {
            System.err.println("Erreur sur colonne " + columnName + ": " + e.getMessage());
            return null;
        }
    }


// Méthodes utilitaires pour gérer les NULL
private String getStringOrNull(ResultSet rs, String columnName) {
    try {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : value;
    } catch (SQLException e) {
        // Si la colonne n'existe pas, retourner null
        System.err.println("Colonne non trouvée: " + columnName);
        return null;
    }
}

private int getIntOrZero(ResultSet rs, String columnName) {
    try {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? 0 : value;
    } catch (SQLException e) {
        // Si la colonne n'existe pas, retourner 0
        System.err.println("Colonne non trouvée: " + columnName);
        return 0;
    }
}

    // Méthode utilitaire pour gérer les String NULL
    private String getNullableString(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : value;
    }

    // Méthode pour afficher les colonnes (débogage)
    private void printResultSetColumns(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        System.out.println("\n=== NOMS DE COLONNES DANS LE RESULTSET ===");
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String columnType = metaData.getColumnTypeName(i);
            System.out.printf("%-30s %-15s\n", columnName, columnType);
        }
        System.out.println("===========================================\n");
    }
    
    public List<Plat> getPlatsBySubCategoryId(int categorieId) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, p.CATEGORIE_ID, p.QTE_POINTS, " +
                     "p.SOUS_CATEGORIE_ID, p.STATUT, p.CREATION_DATE, p.UPDATE_DATE, p.UTILISATEUR_ID, " +
                     "pr.ID AS produit_id, pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, pr.QTE_POINTS AS produit_qtePoints, " +
                     "pr.IMAGE_URL AS produit_image, pr.PRIX_VENTE AS produit_prix " +
                     "FROM PLAT p " +
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     "WHERE p.STATUT = 'VISIBLE' " +
                     "AND p.SOUS_CATEGORIE_ID = ? " +
                     "ORDER BY p.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setId(rs.getInt("ID"));
                plat.setNom(rs.getString("NOM"));
                plat.setDescription(rs.getString("DESCRIPTION"));
                plat.setPrix(rs.getDouble("PRIX"));
                plat.setImage(rs.getString("IMAGE_URL"));
                plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
                plat.setQtePoints(rs.getInt("QTE_POINTS"));
                plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
                plat.setStatut(rs.getString("STATUT"));

                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) {
                    plat.setCreationDate(creationDate.toLocalDateTime());
                }
                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) {
                    plat.setUpdateDate(updateDate.toLocalDateTime());
                }

                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

                int produitId = rs.getInt("produit_id");
                if (!rs.wasNull()) {
                    Produit produit = new Produit();
                    produit.setId(produitId);
                    produit.setNom(rs.getString("produit_nom"));
                    produit.setDescription(rs.getString("produit_description"));
                    produit.setImageUrl(rs.getString("produit_image"));
                    produit.setPrixVente(rs.getBigDecimal("produit_prix"));
                    produit.setQtePoints(rs.getInt("produit_qtePoints"));

//                    // Récupération du stock réel via MouvementStockDAO
//                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
//                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
//                    produit.setQteEnStock(qteEnStock);
//                   // produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));
//
//                    // On ne garde pas le plat si plus de stock
//                    if (qteEnStock <= 0) {
//                        continue;
//                    }

                    plat.setProduit(produit);
                } else {
                    plat.setProduit(null);
                }

                plats.add(plat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plats;
    }



    public List<Plat> searchPlatsByName(String query) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT p.ID as plat_id, p.NOM as plat_nom, p.DESCRIPTION as plat_description,  p.QTE_POINTS as plat_qtePoints, " +
                     "p.PRIX as plat_prix, p.IMAGE_URL as plat_image, p.CATEGORIE_ID as plat_categorie_id, " +
                     "p.PRODUIT_ID as produit_id, " +
                     "pr.ID as prod_id, pr.NOM as prod_nom, pr.PRIX_VENTE as prod_prix, pr.IMAGE_URL as prod_image, pr.QTE_POINTS as prod_qtePoints " +
                     "FROM PLAT p " +
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     "WHERE p.STATUT='VISIBLE' " +
                     "AND (p.NOM LIKE ? OR pr.NOM LIKE ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeQuery = "%" + query + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            ResultSet rs = stmt.executeQuery();

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setId(rs.getInt("plat_id"));
                plat.setNom(rs.getString("plat_nom"));
                plat.setDescription(rs.getString("plat_description"));
                plat.setPrix(rs.getDouble("plat_prix"));
                plat.setImage(rs.getString("plat_image"));
                plat.setCategorieId(rs.getInt("plat_categorie_id"));
                plat.setQtePoints(rs.getInt("plat_qtePoints"));

                int produitId = rs.getInt("produit_id");
                if (produitId > 0) {
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("prod_id"));
                    produit.setNom(rs.getString("prod_nom"));
                    produit.setPrixVente(rs.getBigDecimal("prod_prix"));
                    produit.setImageUrl(rs.getString("prod_image"));
                    produit.setQtePoints(rs.getInt("prod_qtePoints"));

                    // Récupération du stock réel via MouvementStockDAO
//                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
//                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
//                    produit.setQteEnStock(qteEnStock);
//                    //produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));
//
//                    // Ne pas ajouter si stock vide
//                    if (qteEnStock <= 0) {
//                        continue;
//                    }

                    plat.setProduit(produit);
                }

                plats.add(plat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plats;
    }
    
//    public List<Plat> searchPlatsByName(String query) { 
//        List<Plat> plats = new ArrayList<>();
//        String sql = "SELECT p.ID as plat_id, p.NOM as plat_nom, p.DESCRIPTION as plat_description, " +
//                     "p.QTE_POINTS as plat_qtePoints, p.PRIX as plat_prix, p.IMAGE_URL as plat_image, " +
//                     "p.CATEGORIE_ID as plat_categorie_id, p.PRODUIT_ID as produit_id, " +
//                     "pr.ID as prod_id, pr.NOM as prod_nom, pr.PRIX_VENTE as prod_prix, " +
//                     "pr.IMAGE_URL as prod_image, pr.QTE_POINTS as prod_qtePoints " +
//                     "FROM PLAT p " +
//                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
//                     "WHERE p.STATUT='VISIBLE' " +  // Supprimé le doublon
//                     "AND (p.NOM LIKE ? OR pr.NOM LIKE ? OR p.DESCRIPTION LIKE ?)";  // Ajouté la description
//        
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            String likeQuery = "%" + query + "%";
//            stmt.setString(1, likeQuery);
//            stmt.setString(2, likeQuery);
//            stmt.setString(3, likeQuery);  // Pour la recherche dans la description
//            ResultSet rs = stmt.executeQuery();
//
//            MouvementStockDAO msDao = new MouvementStockDAO();
//
//            while (rs.next()) {
//                Plat plat = new Plat();
//                plat.setId(rs.getInt("plat_id"));
//                plat.setNom(rs.getString("plat_nom"));
//                plat.setDescription(rs.getString("plat_description"));
//                plat.setPrix(rs.getDouble("plat_prix"));
//                plat.setImage(rs.getString("plat_image"));
//                plat.setCategorieId(rs.getInt("plat_categorie_id"));
//                plat.setQtePoints(rs.getInt("plat_qtePoints"));
//
//                int produitId = rs.getInt("produit_id");
//                if (produitId > 0 && !rs.wasNull()) {
//                    Produit produit = new Produit();
//                    produit.setId(rs.getInt("prod_id"));
//                    produit.setNom(rs.getString("prod_nom"));
//                    produit.setPrixVente(rs.getBigDecimal("prod_prix"));
//                    produit.setImageUrl(rs.getString("prod_image"));
//                    produit.setQtePoints(rs.getInt("prod_qtePoints"));
//
//                    // Récupération du stock réel via MouvementStockDAO
//                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
//                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
//                    produit.setQteEnStock(qteEnStock);
//
//                    // Ne pas ajouter si stock vide
//                    if (qteEnStock <= 0) {
//                        continue;
//                    }
//
//                    plat.setProduit(produit);
//                }
//
//                plats.add(plat);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return plats;
//    }

    public List<Plat> getAllPlats() {
        List<Plat> plats = new ArrayList<>();

        String sql = "SELECT p.ID as plat_id, p.NOM as plat_nom, p.DESCRIPTION as plat_description, p.QTE_POINTS as plat_qtePoints, " +
                     "p.PRIX as plat_prix, p.IMAGE_URL as plat_image, p.CATEGORIE_ID as plat_categorie_id, " +
                     "p.SOUS_CATEGORIE_ID as plat_sous_categorie_id, p.STATUT as plat_statut, " +
                     "p.CREATION_DATE, p.UPDATE_DATE, p.UTILISATEUR_ID, " +
                     "p.PRODUIT_ID as produit_id, " +
                     "pr.ID as prod_id, pr.NOM as prod_nom, pr.PRIX_VENTE as prod_prix, pr.IMAGE_URL as prod_image, pr.QTE_POINTS as prod_qtePoints  " +
                     "FROM PLAT p " +
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     "WHERE p.STATUT = 'VISIBLE' " +
                     "ORDER BY p.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setId(rs.getInt("plat_id"));
                plat.setQtePoints(rs.getInt("plat_qtePoints"));
                plat.setNom(rs.getString("plat_nom"));
                plat.setDescription(rs.getString("plat_description"));
                plat.setPrix(rs.getDouble("plat_prix"));
                plat.setImage(rs.getString("plat_image"));
                plat.setCategorieId(rs.getInt("plat_categorie_id"));
                plat.setSousCategorieId(rs.getInt("plat_sous_categorie_id"));
                plat.setStatut(rs.getString("plat_statut"));
                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) plat.setCreationDate(creationDate.toLocalDateTime());

                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) plat.setUpdateDate(updateDate.toLocalDateTime());

                int produitId = rs.getInt("produit_id");
                if (produitId > 0) {
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("prod_id"));
                    produit.setQtePoints(rs.getInt("prod_qtePoints"));
                    produit.setNom(rs.getString("prod_nom"));
                    produit.setPrixVente(rs.getBigDecimal("prod_prix"));
                    produit.setImageUrl(rs.getString("prod_image"));

                    // Récupération du stock réel
                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
                    produit.setQteEnStock(qteEnStock);
                    //produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));

                    if (qteEnStock <= 0) {
                        continue; // on ignore ce plat
                    }

                    plat.setProduit(produit);
                } else {
                    continue; // on ignore les plats sans produit associé
                }

                plats.add(plat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plats;
    }




    public List<Plat> getPlatsByCategoryId(int categorieId) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, " +
                     "p.CATEGORIE_ID, p.SOUS_CATEGORIE_ID, p.STATUT, p.CREATION_DATE, p.UPDATE_DATE, p.UTILISATEUR_ID, " +
                     "p.PRODUIT_ID, " +
                     "pr.ID as prod_id, pr.NOM as prod_nom, pr.PRIX_VENTE as prod_prix, pr.IMAGE_URL as prod_image " +
                     "FROM PLAT p " +
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     "WHERE p.CATEGORIE_ID = ? AND p.STATUT = 'VISIBLE' " +
                     "ORDER BY p.NOM";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setId(rs.getInt("ID"));
                plat.setNom(rs.getString("NOM"));
                plat.setDescription(rs.getString("DESCRIPTION"));
                plat.setPrix(rs.getDouble("PRIX"));
                plat.setImage(rs.getString("IMAGE_URL"));
                plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
                plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
                plat.setStatut(rs.getString("STATUT"));
                plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) plat.setCreationDate(creationDate.toLocalDateTime());

                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) plat.setUpdateDate(updateDate.toLocalDateTime());

                int produitId = rs.getInt("PRODUIT_ID");
                if (produitId > 0) {
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("prod_id"));
                    produit.setNom(rs.getString("prod_nom"));
                    produit.setPrixVente(rs.getBigDecimal("prod_prix"));
                    produit.setImageUrl(rs.getString("prod_image"));

                    // Récupération du stock réel
                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
                    produit.setQteEnStock(qteEnStock);
                    //produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));

                    if (qteEnStock <= 0) {
                        continue; // pas de stock -> on saute
                    }

                    plat.setProduit(produit);
                } else {
                    continue; // on saute les plats sans produit associé
                }

                plats.add(plat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plats;
    }
    
 // Dans MenuCategorieDAO.java
    public List<Rayon> getAllRayonsActifs() {
        List<Rayon> rayons = new ArrayList<>();
        String sql = "SELECT * FROM RAYON WHERE STATUT = 'VISIBLE' ORDER BY ID";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rayon rayon = new Rayon();
                rayon.setId(rs.getInt("ID"));
                rayon.setNom(rs.getString("NOM"));
                rayon.setDescription(rs.getString("DESCRIPTION"));
                rayon.setStatut(rs.getString("STATUT"));
                rayon.setImageUrl(rs.getString("IMAGE_URL"));
                
                Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
                if (creationDate != null) {
                    rayon.setCreationDate(creationDate.toLocalDateTime());
                }
                
                Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
                if (updateDate != null) {
                    rayon.setUpdateDate(updateDate.toLocalDateTime());
                }
                
                rayon.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                rayons.add(rayon);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rayons;
    }

}
