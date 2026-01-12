package com.spot69.dao;

import com.spot69.model.MenuCategorie;
import com.spot69.model.Panier;
import com.spot69.model.Plat;
import com.spot69.model.Produit;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

//    public boolean ajouterAuPanier(int utilisateurId, int elementId, String type) {
//        String selectSql = "SELECT QUANTITE FROM PANIER WHERE UTILISATEUR_ID = ? AND " +
//                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + " = ?";
//
//        String updateSql = "UPDATE PANIER SET QUANTITE = QUANTITE + 1 WHERE UTILISATEUR_ID = ? AND " +
//                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + " = ?";
//
//        String insertSql = "INSERT INTO PANIER (UTILISATEUR_ID, " +
//                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + ", QUANTITE) VALUES (?, ?, 1)";
//
//        try (Connection conn = getConnection()) {
//            // Vérifier si l’élément est déjà dans le panier
//            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
//                selectStmt.setInt(1, utilisateurId);
//                selectStmt.setInt(2, elementId);
//
//                ResultSet rs = selectStmt.executeQuery();
//
//                if (rs.next()) {
//                    // L’élément existe déjà, on incrémente la quantité
//                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
//                        updateStmt.setInt(1, utilisateurId);
//                        updateStmt.setInt(2, elementId);
//                        return updateStmt.executeUpdate() > 0;
//                    }
//                } else {
//                    // L’élément n’existe pas encore, on l’insère
//                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
//                        insertStmt.setInt(1, utilisateurId);
//                        insertStmt.setInt(2, elementId);
//                        return insertStmt.executeUpdate() > 0;
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    
    
    public boolean setQuantity(int utilisateurId, int panierId, String type, int newQuantity) {
        // Vérifier d'abord si l'article existe et appartient à l'utilisateur
        String checkSql = "SELECT QUANTITE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";
        String updateSql = "UPDATE PANIER SET QUANTITE = ? WHERE ID = ? AND UTILISATEUR_ID = ?";
        String deleteSql = "DELETE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";

        try (Connection conn = getConnection()) {
            // Vérifier l'existence
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, panierId);
                checkStmt.setInt(2, utilisateurId);
                
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    // Article non trouvé
                    return false;
                }
                
                int currentQuantity = rs.getInt("QUANTITE");
                
                if (newQuantity <= 0) {
                    // Supprimer si quantité <= 0
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, panierId);
                        deleteStmt.setInt(2, utilisateurId);
                        return deleteStmt.executeUpdate() > 0;
                    }
                } else if (newQuantity == currentQuantity) {
                    // Pas de changement
                    return true;
                } else {
                    // Mettre à jour la quantité
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, panierId);
                        updateStmt.setInt(3, utilisateurId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String ajouterAuPanier(int utilisateurId, int elementId, String type) {
        String selectSql = "SELECT QUANTITE FROM PANIER WHERE UTILISATEUR_ID = ? AND " +
                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + " = ?";

        String updateSql = "UPDATE PANIER SET QUANTITE = QUANTITE + 1 WHERE UTILISATEUR_ID = ? AND " +
                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + " = ?";

        String insertSql = "INSERT INTO PANIER (UTILISATEUR_ID, " +
                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + ", QUANTITE) VALUES (?, ?, 1)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, utilisateurId);
                selectStmt.setInt(2, elementId);

                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, utilisateurId);
                        updateStmt.setInt(2, elementId);
                        int rows = updateStmt.executeUpdate();
                        return rows > 0 ? "Ajouté au panier." : "Échec de la mise à jour du panier.";
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, utilisateurId);
                        insertStmt.setInt(2, elementId);
                        int rows = insertStmt.executeUpdate();
                        return rows > 0 ? "Ajouté au panier." : "Échec de l'insertion dans le panier.";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur SQL : " + e.getMessage();
        }
    }

    public boolean augmenterQteAuPanier(int utilisateurId, int panierId, String type) {
        String updateSql = "UPDATE PANIER SET QUANTITE = QUANTITE + 1 WHERE ID = ? AND UTILISATEUR_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, panierId);
            updateStmt.setInt(2, utilisateurId);
            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean diminuerQteAuPanier(int utilisateurId, int panierId, String type) {
        String selectSql = "SELECT QUANTITE FROM panier WHERE ID = ? AND UTILISATEUR_ID = ?";
        String updateSql = "UPDATE PANIER SET QUANTITE = QUANTITE - 1 WHERE ID = ? AND UTILISATEUR_ID = ?";
        String deleteSql = "DELETE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";

        try (Connection conn = getConnection()) {
            // Lire la quantité actuelle
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, panierId);
                selectStmt.setInt(2, utilisateurId);

                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int quantite = rs.getInt("QUANTITE");
                    if (quantite > 1) {
                        // Diminuer la quantité
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, panierId);
                            updateStmt.setInt(2, utilisateurId);
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        // Quantité = 1, supprimer l'élément
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, panierId);
                            deleteStmt.setInt(2, utilisateurId);
                            return deleteStmt.executeUpdate() > 0;
                        }
                    }
                } else {
                    // Pas trouvé dans le panier
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean supprimerDuPanier(int utilisateurId, int panierId, String type) {
        // On va supprimer la ligne correspondant à l'utilisateur + id + type
        // La colonne dépend du type : "plat_id" ou "produit_id"
        String colonneType;

        if ("plat".equalsIgnoreCase(type)) {
            colonneType = "plat_id";
        } else if ("produit".equalsIgnoreCase(type)) {
            colonneType = "produit_id";
        } else {
            // type inconnu, on refuse
            return false;
        }

        String deleteSql = "DELETE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            deleteStmt.setInt(1, panierId);
            deleteStmt.setInt(2, utilisateurId);

            int rowsDeleted = deleteStmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    
    public boolean diminuerQuantite(int utilisateurId, int panierId, String type) {
        String selectSql = "SELECT QUANTITE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";
        
        String deleteSql = "DELETE FROM PANIER WHERE ID = ? AND UTILISATEUR_ID = ?";
        
        String updateSql = "UPDATE PANIER SET QUANTITE = QUANTITE - 1 WHERE ID = ? AND UTILISATEUR_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, panierId);
            selectStmt.setInt(2, utilisateurId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int quantite = rs.getInt("QUANTITE");
                if (quantite > 1) {
                    // Diminuer la quantité
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, panierId);
                        updateStmt.setInt(2, utilisateurId);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Supprimer l'article du panier
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, panierId);
                        deleteStmt.setInt(2, utilisateurId);
                        return deleteStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public List<Panier> listerPanier(int utilisateurId) {
        List<Panier> liste = new ArrayList<>();
        String sql = "SELECT p.ID AS panier_id, p.UTILISATEUR_ID, p.PRODUIT_ID, p.PLAT_ID, p.QUANTITE, p.DATE_AJOUT, "
                + "p.CREATED_AT, p.UPDATED_AT, p.DELETED_BY, "
                + "pr.NOM AS produit_nom, pr.IMAGE_URL AS produit_image, pr.PRIX_VENTE AS produit_prix, "
                + "pl.NOM AS plat_nom, pl.IMAGE_URL AS plat_image, pl.PRIX AS plat_prix, "
                + "cat_pl.ID AS plat_categorie_id, souscat_pl.ID AS plat_souscategorie_id, cat_pl.NOM AS plat_categorie_nom, souscat_pl.NOM AS plat_souscategorie_nom, "
                + "cat_pr.ID AS produit_categorie_id, souscat_pr.ID AS produit_souscategorie_id, cat_pr.NOM AS produit_categorie_nom, souscat_pr.NOM AS produit_souscategorie_nom "
                + "FROM PANIER p "
                + "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID AND pr.STATUT = 'VISIBLE' "
                + "LEFT JOIN MENU_CATEGORIE cat_pr ON pr.CATEGORIE_ID = cat_pr.ID AND cat_pr.PARENT_ID IS NULL "
                + "LEFT JOIN MENU_CATEGORIE souscat_pr ON pr.CATEGORIE_ID = souscat_pr.ID AND souscat_pr.PARENT_ID IS NOT NULL "
                + "LEFT JOIN PLAT pl ON p.PLAT_ID = pl.ID AND pl.STATUT = 'VISIBLE' "
                + "LEFT JOIN MENU_CATEGORIE cat_pl ON pl.CATEGORIE_ID = cat_pl.ID AND cat_pl.PARENT_ID IS NULL "
                + "LEFT JOIN MENU_CATEGORIE souscat_pl ON pl.CATEGORIE_ID = souscat_pl.ID AND souscat_pl.PARENT_ID IS NOT NULL "
                + "WHERE p.UTILISATEUR_ID = ?";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Panier panier = new Panier();
                    panier.setId(rs.getInt("panier_id"));
                    panier.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                    panier.setProduitId((Integer) rs.getObject("PRODUIT_ID"));
                    panier.setPlatId((Integer) rs.getObject("PLAT_ID"));
                    panier.setQuantite(rs.getInt("QUANTITE"));
                    panier.setDateAjout(rs.getTimestamp("DATE_AJOUT"));
                    panier.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    panier.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                    panier.setDeletedBy((Integer) rs.getObject("DELETED_BY"));
                    

                    BigDecimal prixUnitaire = BigDecimal.ZERO;
                    System.out.println("lalal");
                    // Produit
                    if (panier.getProduitId() != null) {
                        Produit produit = new Produit();
                        produit.setId(panier.getProduitId());
                        produit.setNom(rs.getString("produit_nom"));
                        produit.setImageUrl(rs.getString("produit_image"));
                        produit.setPrixVente(rs.getBigDecimal("produit_prix"));

                        MenuCategorie cat = new MenuCategorie();
                        cat.setId(rs.getInt("produit_categorie_id"));
                        cat.setNom(rs.getString("produit_categorie_nom"));
                        produit.setCategorieMenu(cat);

                        MenuCategorie sousCat = new MenuCategorie();
                        sousCat.setId(rs.getInt("produit_souscategorie_id"));
                        sousCat.setNom(rs.getString("produit_souscategorie_nom"));
                        produit.setSousCategorieMenu(sousCat);

                        panier.setProduit(produit);
                        prixUnitaire = produit.getPrixVente();
                    }
                    	else if (panier.getPlatId() != null) {
                        Plat plat = new Plat();
                        plat.setId(panier.getPlatId());
                        plat.setNom(rs.getString("plat_nom"));
                        plat.setImage(rs.getString("plat_image"));
                        plat.setPrix(rs.getDouble("plat_prix"));

                        MenuCategorie cat = new MenuCategorie();
                        cat.setId(rs.getInt("plat_categorie_id"));
                        cat.setNom(rs.getString("plat_categorie_nom"));
                        plat.setCategorieMenu(cat);

                        MenuCategorie sousCat = new MenuCategorie();
                        sousCat.setId(rs.getInt("plat_souscategorie_id"));
                        sousCat.setNom(rs.getString("plat_souscategorie_nom"));
                        plat.setSousCategorieMenu(sousCat);

                        panier.setPlat(plat);
                        prixUnitaire = BigDecimal.valueOf(plat.getPrix());
                    }


                    // ➕ Calcul du total pour cet item
                    BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(panier.getQuantite()));
                    panier.setTotal(sousTotal);

                    liste.add(panier);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }

    public List<Panier> getPanierComplet(int utilisateurId) {
        List<Panier> panierList = new ArrayList<>();
        
        String sql = "SELECT " +
                    "p.ID AS panier_id, " +
                    "p.UTILISATEUR_ID, " +
                    "p.PRODUIT_ID, " +
                    "p.PLAT_ID, " +
                    "p.QUANTITE, " +
                    "p.DATE_AJOUT, " +
                    // Informations du plat
                    "pl.ID AS plat_id, " +
                    "pl.NOM AS plat_nom, " +
                    "pl.IMAGE_URL AS plat_image, " +
                    "pl.PRIX AS plat_prix, " +
                    "pl.PRODUIT_ID AS plat_product_id, " +
                    // Informations du produit associé au plat
                    "pr_plat.ID AS plat_, " +
                    "pr_plat.NOM AS plat_produit_nom, " +
                    "pr_plat.PRIX_VENTE AS plat_produit_prix_vente, " +
                    // Informations du produproduit_idit (si produit direct)
                    "pr.ID AS produit_id, " +
                    "pr.NOM AS produit_nom, " +
                    "pr.PRIX_VENTE AS produit_prix_vente, " +
                    "pr.IMAGE_URL AS produit_image_url " +
                    "FROM PANIER p " +
                    "LEFT JOIN PLAT pl ON p.PLAT_ID = pl.ID AND pl.STATUT = 'VISIBLE' " +
                    "LEFT JOIN PRODUITS pr_plat ON pl.PRODUIT_ID = pr_plat.ID AND pr_plat.STATUT = 'VISIBLE' " +
                    "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID AND pr.STATUT = 'VISIBLE' " +
                    "WHERE p.UTILISATEUR_ID = ? " +
                    "ORDER BY p.DATE_AJOUT DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Panier panier = new Panier();
                    
                    // Informations de base du panier
                    panier.setId(rs.getInt("panier_id"));
                    panier.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                    panier.setQuantite(rs.getInt("QUANTITE"));
                    panier.setDateAjout(rs.getTimestamp("DATE_AJOUT"));
                    
                    // Vérifier si c'est un plat ou un produit
                    Integer platId = rs.getInt("plat_id");
                    Integer produitId = rs.getInt("produit_id");
                    
                    if (platId != null && platId > 0) {
                        panier.setPlatId(platId);
                        
                        // Créer l'objet Plat
                        Plat plat = new Plat();
                        plat.setId(platId);
                        plat.setNom(rs.getString("plat_nom"));
                        plat.setImage(rs.getString("plat_image"));
                        
                        // Vérifier le prix
                        Double platPrix = rs.getDouble("plat_prix");
                        if (!rs.wasNull()) {
                            plat.setPrix(platPrix);
                        }
                        
                        // Vérifier si le plat a un produit associé
                        Integer platProduitId = rs.getInt("plat_product_id");
                        if (platProduitId != null && platProduitId > 0) {
                            Produit produitAssocie = new Produit();
                            produitAssocie.setId(platProduitId);
                            produitAssocie.setNom(rs.getString("plat_produit_nom"));
                            
                            BigDecimal prixVente = rs.getBigDecimal("plat_produit_prix_vente");
                            if (!rs.wasNull()) {
                                produitAssocie.setPrixVente(prixVente);
                                // Utiliser le prix du produit associé pour le plat
                                plat.setPrix(prixVente.doubleValue());
                            }
                            
                            plat.setProduit(produitAssocie);
                        }
                        
                        panier.setPlat(plat);
                        
                    } else if (produitId != null && produitId > 0) {
                        panier.setProduitId(produitId);
                        
                        // Créer l'objet Produit
                        Produit produit = new Produit();
                        produit.setId(produitId);
                        produit.setNom(rs.getString("produit_nom"));
                        
                        BigDecimal prixVente = rs.getBigDecimal("produit_prix_vente");
                        if (!rs.wasNull()) {
                            produit.setPrixVente(prixVente);
                        }
                        
                        produit.setImageUrl(rs.getString("produit_image_url"));
                        panier.setProduit(produit);
                    }
                    
                    // Calculer le total pour cet item
                    BigDecimal prixUnitaire = BigDecimal.ZERO;
                    if (panier.getPlat() != null && panier.getPlat().getPrix() != 0) {
                        prixUnitaire = BigDecimal.valueOf(panier.getPlat().getPrix());
                    } else if (panier.getProduit() != null && panier.getProduit().getPrixVente() != null) {
                        prixUnitaire = panier.getProduit().getPrixVente();
                    }
                    
                    if (prixUnitaire.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(panier.getQuantite()));
                        panier.setTotal(sousTotal);
                    }
                    
                    panierList.add(panier);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return panierList;
    }

    public Panier rechercherDansPanierParId(int utilisateurId, int elementId, String type) {
        String sql = "SELECT * FROM PANIER WHERE UTILISATEUR_ID = ? AND " +
                (type.equalsIgnoreCase("plat") ? "PLAT_ID" : "PRODUIT_ID") + " = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            stmt.setInt(2, elementId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Panier panier = new Panier();
                    panier.setId(rs.getInt("ID"));
                    panier.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                    panier.setProduitId((Integer) rs.getObject("PRODUIT_ID"));
                    panier.setPlatId((Integer) rs.getObject("PLAT_ID"));
                    panier.setQuantite(rs.getInt("QUANTITE"));
                    panier.setDateAjout(rs.getTimestamp("DATE_AJOUT"));
                    panier.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    panier.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                    panier.setDeletedBy((Integer) rs.getObject("DELETED_BY"));

                    if (type.equalsIgnoreCase("produit")) {
                        panier.setProduit(chercherProduitParId(elementId));
                    } else if (type.equalsIgnoreCase("plat")) {
                        panier.setPlat(chercherPlatParId(elementId));
                    }

                    return panier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Utilitaires internes

    private Produit chercherProduitParId(int id) {
        String sql = "SELECT * FROM PRODUITS WHERE ID = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produit p = new Produit();
                    p.setId(rs.getInt("ID"));
                    p.setNom(rs.getString("NOM"));
                    p.setDescription(rs.getString("DESCRIPTION"));
                    p.setImageUrl(rs.getString("IMAGE_URL"));
                    p.setPrixVente(rs.getBigDecimal("PRIX_VENTE"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Plat chercherPlatParId(int id) {
        String sql = "SELECT * FROM PLAT WHERE ID = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Plat p = new Plat();
                    p.setId(rs.getInt("ID"));
                    p.setNom(rs.getString("NOM"));
                    p.setDescription(rs.getString("DESCRIPTION"));
                    p.setImage(rs.getString("IMAGE_URL"));
                    p.setPrix(rs.getDouble("PRIX"));
                    p.setCategorieId(rs.getInt("CATEGORIE_ID"));
                    p.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
