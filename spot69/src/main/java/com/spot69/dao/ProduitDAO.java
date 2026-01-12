package com.spot69.dao;

import com.spot69.model.Facture;
import com.spot69.model.FactureDetail;
import com.spot69.model.MenuCategorie;
import com.spot69.model.MouvementStock;
import com.spot69.model.Produit;
import com.spot69.model.Rayon;
import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProduitDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

//    public boolean ajouterProduit(Produit produit) {
//        String sql = "INSERT INTO PRODUITS (NOM, CODE_PRODUIT, DESCRIPTION, IMAGE_URL, EMPLACEMENT, RAYON_ID, " +
//                     "CATEGORIE_ID, SOUS_CATEGORIE_ID, UNITE_VENTE, PRIX_ACHAT_PAR_UNITE_VENTE, CONTENU_PAR_UNITE, " +
//                     "SEUIL_ALERTE, PRIX_VENTE, UTILISATEUR_ID, CREATED_AT, UPDATED_AT, QTE_POINTS, STATUT) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE')"; // 18 valeurs maintenant
//
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, produit.getNom());
//            stmt.setString(2, produit.getCodeProduit());
//            stmt.setString(3, produit.getDescription() != null ? produit.getDescription() : "");
//            stmt.setString(4, produit.getImageUrl() != null ? produit.getImageUrl() : "");
//            stmt.setString(5, produit.getEmplacement() != null ? produit.getEmplacement() : "");
//            stmt.setInt(6, produit.getRayonId()); // RAYON_ID ajouté ici
//            stmt.setInt(7, produit.getCategorieId());
//            stmt.setInt(8, produit.getSousCategorieId());
//            stmt.setString(9, produit.getUniteVente() != null ? produit.getUniteVente() : "");
//            stmt.setInt(10, produit.getPrixAchatParUniteVente());
//            stmt.setString(11, produit.getContenuParUnite() != null ? produit.getContenuParUnite() : "");
//            stmt.setInt(12, produit.getSeuilAlerte());
//            stmt.setBigDecimal(13, produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO);
//            stmt.setInt(14, produit.getUtilisateurId());
//
//            Timestamp now = new Timestamp(new Date().getTime());
//            stmt.setTimestamp(15, now); // CREATED_AT
//            stmt.setTimestamp(16, now); // UPDATED_AT
//            stmt.setInt(17, produit.getQtePoints());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public boolean ajouterProduit(Produit produit) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Désactiver l'auto-commit pour gérer la transaction
            
            // Étape 1: Insérer le produit
            String sqlProduit = "INSERT INTO PRODUITS (NOM, CODE_PRODUIT, DESCRIPTION, IMAGE_URL, EMPLACEMENT, RAYON_ID, " +
                               "CATEGORIE_ID, SOUS_CATEGORIE_ID, UNITE_VENTE, PRIX_ACHAT_PAR_UNITE_VENTE, CONTENU_PAR_UNITE, " +
                               "SEUIL_ALERTE, PRIX_VENTE, UTILISATEUR_ID, CREATED_AT, UPDATED_AT, QTE_POINTS, STATUT) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE')";
            
            try (PreparedStatement stmtProduit = conn.prepareStatement(sqlProduit, Statement.RETURN_GENERATED_KEYS)) {
                // Configuration des paramètres du produit
                int index = 1;
                stmtProduit.setString(index++, produit.getNom());
                stmtProduit.setString(index++, produit.getCodeProduit());
                stmtProduit.setString(index++, produit.getDescription() != null ? produit.getDescription() : "");
                stmtProduit.setString(index++, produit.getImageUrl() != null ? produit.getImageUrl() : "");
                stmtProduit.setString(index++, produit.getEmplacement() != null ? produit.getEmplacement() : "");
                stmtProduit.setInt(index++, produit.getRayonId());
                stmtProduit.setInt(index++, produit.getCategorieId());
                stmtProduit.setInt(index++, produit.getSousCategorieId());
                stmtProduit.setString(index++, produit.getUniteVente() != null ? produit.getUniteVente() : "");
                stmtProduit.setInt(index++, produit.getPrixAchatParUniteVente());
                stmtProduit.setString(index++, produit.getContenuParUnite() != null ? produit.getContenuParUnite() : "");
                stmtProduit.setInt(index++, produit.getSeuilAlerte());
                stmtProduit.setBigDecimal(index++, produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO);
                stmtProduit.setInt(index++, produit.getUtilisateurId());

                Timestamp now = new Timestamp(new Date().getTime());
                stmtProduit.setTimestamp(index++, now);
                stmtProduit.setTimestamp(index++, now);
                stmtProduit.setInt(index++, produit.getQtePoints());

                // Exécution de l'insertion
                int rowsAffected = stmtProduit.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
                
                // Récupérer l'ID du produit inséré
                ResultSet generatedKeys = stmtProduit.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    conn.rollback();
                    return false;
                }
                
                int productId = generatedKeys.getInt(1);
                
                // Étape 2: Vérifier si le produit est déjà dans un plat
                String checkSql = "SELECT COUNT(*) FROM PLAT WHERE PRODUIT_ID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, productId);
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Le produit est déjà dans un plat, annuler la transaction
                        conn.rollback();
                        System.out.println("Ce produit est déjà associé à un plat.");
                        return false;
                    }
                }
                
                // Étape 3: Ajouter le produit au menu (table PLAT)
                String sqlPlat = "INSERT INTO PLAT (NOM, DESCRIPTION, PRIX, IMAGE_URL, RAYON_ID, " +
                               "CATEGORIE_ID, SOUS_CATEGORIE_ID, STATUT, QTE_POINTS, CREATION_DATE, " +
                               "UPDATE_DATE, DELETED_BY, UTILISATEUR_ID, PRODUIT_ID) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmtPlat = conn.prepareStatement(sqlPlat)) {
                    index = 1;
                    
                    // NOM
                    if (produit.getNom() != null && !produit.getNom().isEmpty()) {
                        stmtPlat.setString(index++, produit.getNom());
                    } else {
                        stmtPlat.setNull(index++, Types.VARCHAR);
                    }
                    
                    // DESCRIPTION
                    if (produit.getDescription() != null && !produit.getDescription().isEmpty()) {
                        stmtPlat.setString(index++, produit.getDescription());
                    } else {
                        stmtPlat.setNull(index++, Types.VARCHAR);
                    }
                    
                    // PRIX
                    if (produit.getPrixVente() != null) {
                        stmtPlat.setBigDecimal(index++, produit.getPrixVente());
                    } else {
                        stmtPlat.setNull(index++, Types.DECIMAL);
                    }
                    
                    // IMAGE_URL
                    if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
                        stmtPlat.setString(index++, produit.getImageUrl());
                    } else {
                        stmtPlat.setNull(index++, Types.VARCHAR);
                    }
                    
                    // RAYON_ID
                    stmtPlat.setInt(index++, produit.getRayonId());
                    
                    // CATEGORIE_ID
                    if (produit.getCategorieId() > 0) {
                        stmtPlat.setInt(index++, produit.getCategorieId());
                    } else {
                        stmtPlat.setNull(index++, Types.INTEGER);
                    }
                    
                    // SOUS_CATEGORIE_ID
                    if (produit.getSousCategorieId() > 0) {
                        stmtPlat.setInt(index++, produit.getSousCategorieId());
                    } else {
                        stmtPlat.setNull(index++, Types.INTEGER);
                    }
                    
                    // STATUT - valeur par défaut "VISIBLE"
                    stmtPlat.setString(index++, "VISIBLE");
                    
                    // QTE_POINTS
                    if (produit.getQtePoints() != 0) {
                        stmtPlat.setInt(index++, produit.getQtePoints());
                    } else {
                        stmtPlat.setNull(index++, Types.INTEGER);
                    }
                    
                    // DATES - date actuelle
                    stmtPlat.setTimestamp(index++, now);  // CREATION_DATE
                    stmtPlat.setTimestamp(index++, now);  // UPDATE_DATE
                    
                    // DELETED_BY - toujours 0 par défaut
                    stmtPlat.setInt(index++, 0);
                    
                    // UTILISATEUR_ID
                    stmtPlat.setInt(index++, produit.getUtilisateurId());
                    
                    // PRODUIT_ID
                    stmtPlat.setInt(index++, productId);

                    int rowsPlat = stmtPlat.executeUpdate();
                    
                    if (rowsPlat <= 0) {
                        conn.rollback();
                        return false;
                    }
                    
                    // Tout s'est bien passé, valider la transaction
                    conn.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Restaurer l'auto-commit et fermer la connexion
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // Modifier un produit
//    public boolean modifierProduit(Produit produit) {
//        String sql = "UPDATE PRODUITS SET NOM = ?, DESCRIPTION = ?, IMAGE_URL = ?, EMPLACEMENT = ?, CATEGORIE_ID = ?, SOUS_CATEGORIE_ID = ?, UNITE_VENTE = ?, PRIX_ACHAT_PAR_UNITE_VENTE = ?, CONTENU_PAR_UNITE = ?, SEUIL_ALERTE = ?, PRIX_VENTE = ?, UTILISATEUR_ID = ?, UPDATED_AT = ?, QTE_POINTS = ? " +
//                     "WHERE ID = ?";
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, produit.getNom());
//            stmt.setString(2, produit.getDescription());
//            stmt.setString(3, produit.getImageUrl());
//            stmt.setString(4, produit.getEmplacement());
//            stmt.setInt(5, produit.getCategorieId());
//            stmt.setInt(6, produit.getSousCategorieId());
//            stmt.setString(7, produit.getUniteVente());
//            stmt.setInt(8, produit.getPrixAchatParUniteVente());
//            stmt.setString(9, produit.getContenuParUnite());
//            stmt.setInt(10, produit.getSeuilAlerte());
//            stmt.setBigDecimal(11, produit.getPrixVente());
//            stmt.setInt(12, produit.getUtilisateurId());
//            stmt.setTimestamp(13, new Timestamp(new Date().getTime()));
//            stmt.setInt(14, produit.getQtePoints());
//            stmt.setInt(15, produit.getId());
//
//            int rows = stmt.executeUpdate();
//            System.out.println("[INFO] Produit mis à jour : " + produit.getId() + " | Lignes affectées = " + rows);
//
//            return rows > 0;
//        } catch (SQLException e) {
//            System.out.println("[ERREUR] Échec lors de la mise à jour du produit ID=" + produit.getId());
//            System.out.println("[SQL] " + sql);
//            e.printStackTrace();
//            return false;
//        }
//    }
    public boolean modifierProduit(Produit produit) {
        String sql = "UPDATE PRODUITS SET NOM = ?, DESCRIPTION = ?, IMAGE_URL = ?, EMPLACEMENT = ?, "
                   + "CATEGORIE_ID = ?, SOUS_CATEGORIE_ID = ?, UNITE_VENTE = ?, "
                   + "PRIX_ACHAT_PAR_UNITE_VENTE = ?, CONTENU_PAR_UNITE = ?, "
                   + "SEUIL_ALERTE = ?, PRIX_VENTE = ?, UTILISATEUR_ID = ?, "
                   + "UPDATED_AT = ?, QTE_POINTS = ? "
                   // Si vous avez un champ rayon_id dans la table PRODUITS, ajoutez-le ici:
                    + ", RAYON_ID = ? "
                   + "WHERE ID = ?";
        
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setString(3, produit.getImageUrl());
            stmt.setString(4, produit.getEmplacement());
            stmt.setInt(5, produit.getCategorieId());
            stmt.setInt(6, produit.getSousCategorieId());
            stmt.setString(7, produit.getUniteVente());
            stmt.setInt(8, produit.getPrixAchatParUniteVente());
            stmt.setString(9, produit.getContenuParUnite());
            stmt.setInt(10, produit.getSeuilAlerte());
            stmt.setBigDecimal(11, produit.getPrixVente());
            stmt.setInt(12, produit.getUtilisateurId());
            stmt.setTimestamp(13, new Timestamp(new Date().getTime()));
            stmt.setInt(14, produit.getQtePoints());
            
            // Si vous avez un champ rayon_id dans la table:
             stmt.setInt(15, produit.getRayonId());
             stmt.setInt(16, produit.getId());
            
//            stmt.setInt(15, produit.getId()); // Sans rayon_id
            
            int rows = stmt.executeUpdate();
            System.out.println("[INFO] Produit mis à jour : " + produit.getId() + " | Lignes affectées = " + rows);

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("[ERREUR] Échec lors de la mise à jour du produit ID=" + produit.getId());
            System.out.println("[SQL] " + sql);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean existeCodeProduit(String codeProduit) {
        String sql = "SELECT COUNT(*) FROM PRODUITS WHERE CODE_PRODUIT = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codeProduit);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer un produit (statut = 'DELETED' + deleted_by)
    public boolean supprimerProduit(int id, int deletedBy) {
        String sql = "UPDATE PRODUITS SET STATUT = 'DELETED', UPDATED_AT = ?, DELETED_BY = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.setInt(2, deletedBy);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Rechercher un produit par ID
//    public Produit chercherParId(int id) {
//        String sql = "SELECT p.*, " +
//                     "mc_parent.NOM AS categorie_nom, mc_parent.DESCRIPTION AS categorie_description, " +
//                     "mc_sous.NOM AS sous_categorie_nom, mc_sous.DESCRIPTION AS sous_categorie_description " +
//                     "FROM PRODUITS p " +
//                     "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' " +
//                     "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' " +
//                     "WHERE p.ID = ? AND p.STATUT = 'VISIBLE'";
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return extraireProduit(rs);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public Produit chercherParId(int id) {
        String sql = "SELECT p.*, "
                   + "mc_parent.NOM AS categorie_nom, mc_parent.DESCRIPTION AS categorie_description, "
                   + "mc_sous.NOM AS sous_categorie_nom, mc_sous.DESCRIPTION AS sous_categorie_description, "
                   + "r.ID AS rayon_id, r.NOM AS rayon_nom, r.DESCRIPTION AS rayon_description "
                   + "FROM PRODUITS p "
                   + "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' "
                   + "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' "
                   + "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID AND r.STATUT = 'VISIBLE' "
                   + "WHERE p.ID = ? AND p.STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireProduit(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Produit> getBoissonsDisponibles() {
        List<Produit> produits = new ArrayList<>();

        String sql = "SELECT " +
                "p.ID, p.NOM, p.PRIX_VENTE, p.IMAGE_URL, p.QTE_POINTS " +
                "FROM PRODUITS p " +
                "WHERE p.CATEGORIE_ID = ? " +
                "AND p.STATUT = 'VISIBLE' " +
                "ORDER BY p.NOM ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 6); // ID de la catégorie boissons
            ResultSet rs = stmt.executeQuery();

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("ID"));
                produit.setNom(rs.getString("NOM"));
                produit.setImageUrl(rs.getString("IMAGE_URL"));
                produit.setPrixVente(rs.getBigDecimal("PRIX_VENTE"));
                produit.setQtePoints(rs.getInt("QTE_POINTS"));

                // Récupération du stock réel via MouvementStockDAO
                List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
                int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
                produit.setQteEnStock(qteEnStock);

                // Calcul du prix total
                produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));

                // On ajoute uniquement si dispo
                if (qteEnStock > 0) {
                    produits.add(produit);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }

    // Rechercher par nom (LIKE)
    public List<Produit> chercherParNom(String motCle) {
        List<Produit> liste = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "mc_parent.NOM AS categorie_nom, mc_parent.DESCRIPTION AS categorie_description, " +
                     "mc_sous.NOM AS sous_categorie_nom, mc_sous.DESCRIPTION AS sous_categorie_description " +
                     "FROM PRODUITS p " +
                     "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' " +
                     "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' " +
                     "WHERE p.NOM LIKE ? AND p.STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireProduit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    
    public int compterProduitsEnRupture() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS NB_RUPTURE FROM (" +
                     "  SELECT p.ID " +
                     "  FROM PRODUITS p " +
                     "  LEFT JOIN FACTURE_DETAIL fd ON fd.PRODUIT_ID = p.ID AND fd.STATUT = 'VISIBLE' " +
                     "  LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' " +
                     "  LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' " +
                     "  WHERE p.STATUT = 'VISIBLE' " +
                     "  GROUP BY p.ID " +
                     "  HAVING COALESCE(SUM(fd.QUANTITE), 0) = 0" +
                     ") AS produits_en_rupture";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("NB_RUPTURE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    public int compterQuantiteTotaleProduits() {
        int totalQuantite = 0;
        String sql = "SELECT COUNT(*) AS TOTAL_QTE " +
                "FROM PRODUITS p " +
                "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' " +
                "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' " +
                "WHERE p.STATUT = 'VISIBLE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalQuantite = rs.getInt("TOTAL_QTE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalQuantite;
    }

 // Alternative si vous voulez aussi avoir un champ rayon dans Produit
  public List<Produit> listerProduits() {
    List<Produit> liste = new ArrayList<>();
    String sql = "SELECT " +
            "p.ID, p.NOM, p.CODE_PRODUIT, p.DESCRIPTION, p.QTE_POINTS, p.IMAGE_URL, p.EMPLACEMENT, " +
            "p.CATEGORIE_ID, p.SOUS_CATEGORIE_ID, p.UNITE_VENTE, p.CONTENU_PAR_UNITE, " +
            "p.PRIX_ACHAT_PAR_UNITE_VENTE, p.SEUIL_ALERTE, p.PRIX_VENTE, p.RAYON_ID, " +
            "p.UTILISATEUR_ID, p.CREATED_AT, p.UPDATED_AT, " +
            "mc_parent.NOM AS categorie_nom, mc_parent.DESCRIPTION AS categorie_description, " +
            "mc_sous.NOM AS sous_categorie_nom, mc_sous.DESCRIPTION AS sous_categorie_description, " + // Ajouté
            "r.ID AS rayon_id, r.NOM AS rayon_nom, r.DESCRIPTION AS rayon_description, r.STATUT AS rayon_statut " +
            "FROM PRODUITS p " +
            "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID AND mc_parent.STATUT = 'VISIBLE' " + // AJOUTÉ
            "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID AND mc_sous.STATUT = 'VISIBLE' " +
            "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID AND r.STATUT = 'VISIBLE' " +
            "WHERE p.STATUT = 'VISIBLE' " +
            "ORDER BY p.NOM ASC";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        MouvementStockDAO msDao = new MouvementStockDAO();

        while (rs.next()) {
            Produit produit = new Produit();

            produit.setId(rs.getInt("ID"));
            produit.setNom(rs.getString("NOM"));
            produit.setCodeProduit(rs.getString("CODE_PRODUIT"));
            produit.setDescription(rs.getString("DESCRIPTION"));
            produit.setQtePoints(rs.getInt("QTE_POINTS"));
            produit.setImageUrl(rs.getString("IMAGE_URL"));
            produit.setEmplacement(rs.getString("EMPLACEMENT"));
            produit.setCategorieId(rs.getInt("CATEGORIE_ID"));
            produit.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
            produit.setUniteVente(rs.getString("UNITE_VENTE"));
            produit.setPrixAchatParUniteVente(rs.getInt("PRIX_ACHAT_PAR_UNITE_VENTE"));
            produit.setContenuParUnite(rs.getString("CONTENU_PAR_UNITE"));
            produit.setSeuilAlerte(rs.getInt("SEUIL_ALERTE"));
            produit.setPrixVente(rs.getBigDecimal("PRIX_VENTE"));
            produit.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
            produit.setCreatedAt(rs.getTimestamp("CREATED_AT"));
            produit.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
            produit.setRayonId(rs.getInt("RAYON_ID"));

            // Récupération du stock réel via MouvementStockDAO
            List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produit.getId());
            int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
            produit.setQteEnStock(qteEnStock);
            produit.setPrixTotal(produit.getPrixVente().multiply(BigDecimal.valueOf(qteEnStock)));

            // Création de l'objet rayon
            int rayonId = rs.getInt("RAYON_ID");
            if (!rs.wasNull() && rayonId > 0) {
                Rayon rayon = new Rayon();
                rayon.setId(rayonId);
                rayon.setNom(rs.getString("rayon_nom"));
                rayon.setDescription(rs.getString("rayon_description"));
                rayon.setStatut(rs.getString("rayon_statut"));
                produit.setRayon(rayon);
            }

            // Création de l'objet catégorie parent
            if (produit.getCategorieId() > 0) {
                MenuCategorie categorie = new MenuCategorie();
                categorie.setId(produit.getCategorieId());
                categorie.setNom(rs.getString("categorie_nom"));
                categorie.setDescription(rs.getString("categorie_description"));
                
                // Associer le rayon à la catégorie si disponible
                if (produit.getRayon() != null) {
                    categorie.setRayon(produit.getRayon());
                }
                
                produit.setCategorie(categorie);
            }

            // Création de l'objet sous-catégorie
            if (produit.getSousCategorieId() > 0) {
                MenuCategorie sousCategorie = new MenuCategorie();
                sousCategorie.setId(produit.getSousCategorieId());
                sousCategorie.setNom(rs.getString("sous_categorie_nom"));
                sousCategorie.setDescription(rs.getString("sous_categorie_description"));
                produit.setSousCategorie(sousCategorie);
            }

            liste.add(produit);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return liste;
}
    public Map<String, Object> listerInventaires() {
    	 String sql = "SELECT " +
    	            "p.*, " +
    	            "mc_parent.NOM AS categorie_nom, mc_parent.DESCRIPTION AS categorie_description, mc_parent.ID AS cat_id, " +
    	            "mc_sous.NOM AS sous_categorie_nom, mc_sous.DESCRIPTION AS sous_categorie_description, mc_sous.ID AS sous_id, " +
    	            "r.ID AS rayon_id, r.NOM AS rayon_nom, r.DESCRIPTION AS rayon_description, r.IMAGE_URL AS rayon_image_url " +
    	            "FROM PRODUITS p " +
    	            "LEFT JOIN MENU_CATEGORIE mc_parent ON p.CATEGORIE_ID = mc_parent.ID " +
    	            "LEFT JOIN MENU_CATEGORIE mc_sous ON p.SOUS_CATEGORIE_ID = mc_sous.ID " +
    	            "LEFT JOIN RAYON r ON mc_parent.RAYON_ID = r.ID " +
    	            "WHERE p.STATUT = 'VISIBLE' " +
    	            "ORDER BY r.ID, mc_parent.ID, mc_sous.ID";

    	    Map<Integer, Map<String, Object>> rayonsMap = new LinkedHashMap<>();

    	    try (Connection conn = getConnection();
    	         Statement stmt = conn.createStatement();
    	         ResultSet rs = stmt.executeQuery(sql)) {

    	        MouvementStockDAO msDao = new MouvementStockDAO();

    	        while (rs.next()) {

    	            // ====== RAYON ======
    	            int rayonId = rs.getInt("rayon_id");
    	            if (!rayonsMap.containsKey(rayonId)) {
    	                Map<String, Object> rayon = new LinkedHashMap<>();
    	                rayon.put("id", rayonId);
    	                rayon.put("nom", rs.getString("rayon_nom"));
    	                rayon.put("description", rs.getString("rayon_description"));
    	                rayon.put("imageUrl", rs.getString("rayon_image_url"));
    	                rayon.put("categories", new ArrayList<>());
    	                rayonsMap.put(rayonId, rayon);
    	            }

    	            Map<String, Object> rayon = rayonsMap.get(rayonId);
    	            List<Map<String, Object>> categories = (List<Map<String, Object>>) rayon.get("categories");

    	            // ====== CATÉGORIE ======
    	            int catId = rs.getInt("cat_id");
    	            Map<String, Object> categorie = categories.stream()
    	                    .filter(c -> (int) c.get("id") == catId)
    	                    .findFirst()
    	                    .orElse(null);

    	            if (categorie == null) {
    	                categorie = new LinkedHashMap<>();
    	                categorie.put("id", catId);
    	                categorie.put("nom", rs.getString("categorie_nom"));
    	                categorie.put("description", rs.getString("categorie_description"));
    	                categorie.put("imageUrl", "/spot69/images/categories/default.png");
    	                categorie.put("sousCategories", new ArrayList<>());
    	                categories.add(categorie);
    	            }

    	            List<Map<String, Object>> sousCategories =
    	                    (List<Map<String, Object>>) categorie.get("sousCategories");

    	            // ====== SOUS-CATÉGORIE ======
    	            int sousId = rs.getInt("sous_id");
    	            Map<String, Object> sousCat = sousCategories.stream()
    	                    .filter(sc -> (int) sc.get("id") == sousId)
    	                    .findFirst()
    	                    .orElse(null);

    	            if (sousCat == null) {
    	                sousCat = new LinkedHashMap<>();
    	                sousCat.put("id", sousId);
    	                sousCat.put("nom", rs.getString("sous_categorie_nom"));
    	                sousCat.put("description", rs.getString("sous_categorie_description"));
    	                sousCat.put("imageUrl", "/spot69/images/categories/default.png");
    	                sousCat.put("produits", new ArrayList<>());
    	                sousCategories.add(sousCat);
    	            }

    	            List<Map<String, Object>> produits =
    	                    (List<Map<String, Object>>) sousCat.get("produits");

    	            // ====== PRODUIT ======
    	            Map<String, Object> produit = new LinkedHashMap<>();
    	            produit.put("id", rs.getInt("ID"));
    	            produit.put("nom", rs.getString("NOM"));
    	            produit.put("description", rs.getString("DESCRIPTION"));
    	            produit.put("prix", rs.getBigDecimal("PRIX_VENTE"));
    	            produit.put("qtePoints", rs.getInt("QTE_POINTS"));
    	            produit.put("imageUrl", rs.getString("IMAGE_URL"));

    	            // Stock réel
    	            List<MouvementStock> mvmts = msDao.mouvementStockParProduit(rs.getInt("ID"));
    	            int stock = mvmts.isEmpty() ? 0 : mvmts.get(0).getStockFin();
    	            produit.put("qteEnStock", stock);

    	            produits.add(produit);
    	        }

    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    // Construction JSON final
    	    Map<String, Object> response = new LinkedHashMap<>();
    	    response.put("status", "ok");
    	    Map<String, Object> data = new LinkedHashMap<>();
    	    data.put("rayons", rayonsMap.values());

    	    response.put("data", data);


    	    return response;
    }

    // Les autres méthodes (mouvementStockParProduit, recupererGrandTotal) restent inchangées
    // car elles n'utilisent pas directement les tables de catégories

    public List<MouvementStock> mouvementStockParProduit(int idProduit) {
        // Cette méthode reste inchangée car elle ne référence pas les tables de catégories
        List<MouvementStock> mouvements = new ArrayList<>();
        List<FactureDetail> details = new ArrayList<>();

        String sql = "SELECT " +
                "fd.ID AS fd_id, fd.FACTURE_ID AS fd_facture_id, fd.PRODUIT_ID AS fd_produit_id, " +
                "fd.QUANTITE AS fd_quantite, fd.PRIX_ACHAT_PAR_UNITE_MESURE AS fd_prix_unite, " +
                "fd.PRIX_ACHAT_TOTAL AS fd_prix_total, fd.QTE_UNITE AS fd_qte_unite, fd.QTE_VENDU AS fd_qte_vendu, " +
                "fd.STATUT AS fd_statut, fd.PRIX_REVIENT_UNITE AS fd_prix_revient, " +
                "fd.DELETED_BY AS fd_deleted_by, fd.CREATED_AT AS fd_created_at, fd.UPDATED_AT AS fd_updated_at, " +

                "p.ID AS p_id, p.NOM AS p_nom, p.DESCRIPTION AS p_description,  p.QTE_POINTS AS p_qtePoints, p.IMAGE_URL AS p_image, " +
                "p.CATEGORIE_ID AS p_categorie, p.SOUS_CATEGORIE_ID AS p_sous_categorie, p.EMPLACEMENT AS p_emplacement, " +
                "p.UNITE_VENTE AS p_unite, p.CONTENU_PAR_UNITE AS p_contenu, p.SEUIL_ALERTE AS p_seuil, " +
                "p.PRIX_VENTE AS p_prix_vente, p.QTE_EN_STOCK AS p_stock, p.UTILISATEUR_ID AS p_utilisateur_id, " +
                "p.STATUT AS p_statut, p.CREATED_AT AS p_created_at, p.UPDATED_AT AS p_updated_at, p.DELETED_BY AS p_deleted_by, " +
                "p.CODE_PRODUIT AS p_code, " +

                "f.ID AS f_id, f.NO_FACTURE AS f_no_facture, f.CREATED_AT AS f_created_at, f.UPDATED_AT AS f_updated_at " +

                "FROM FACTURE_DETAIL fd " +
                "LEFT JOIN PRODUITS p ON fd.PRODUIT_ID = p.ID " +
                "LEFT JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "ORDER BY fd.CREATED_AT ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FactureDetail detail = new FactureDetail();

                // Produit
                Produit produit = new Produit();
                produit.setId(rs.getInt("p_id"));
                produit.setNom(rs.getString("p_nom"));
                produit.setDescription(rs.getString("p_description"));
                produit.setQtePoints(rs.getInt("p_qtePoints"));
                produit.setImageUrl(rs.getString("p_image"));
                produit.setEmplacement(rs.getString("p_emplacement"));
                produit.setCodeProduit(rs.getString("p_code"));
                produit.setCategorieId(rs.getInt("p_categorie"));
                produit.setSousCategorieId(rs.getInt("p_sous_categorie"));
                produit.setUniteVente(rs.getString("p_unite"));
                produit.setContenuParUnite(rs.getString("p_contenu"));
                produit.setSeuilAlerte(rs.getInt("p_seuil"));
                produit.setPrixVente(rs.getBigDecimal("p_prix_vente"));
                produit.setUtilisateurId(rs.getInt("p_utilisateur_id"));
                produit.setQteEnStock(rs.getInt("p_stock"));
                produit.setCreatedAt(rs.getTimestamp("p_created_at"));
                produit.setUpdatedAt(rs.getTimestamp("p_updated_at"));
                produit.setDeletedBy(rs.getObject("p_deleted_by") != null ? rs.getInt("p_deleted_by") : null);

                // Facture
                Facture facture = new Facture();
                facture.setId(rs.getInt("f_id"));
                facture.setNoFacture(rs.getString("f_no_facture"));
                facture.setCreatedAt(rs.getTimestamp("f_created_at"));
                facture.setUpdatedAt(rs.getTimestamp("f_updated_at"));

                // Détail
                detail.setId(rs.getInt("fd_id"));
                detail.setFacture(facture);
                detail.setProduit(produit);
                detail.setQuantite(rs.getInt("fd_quantite"));
                detail.setPrixAchatParUniteMesure(rs.getInt("fd_prix_unite"));
                detail.setPrixAchatTotal(rs.getInt("fd_prix_total"));
                detail.setQteUnite(rs.getInt("fd_qte_unite"));
                detail.setQteVendu(rs.getInt("fd_qte_vendu"));
                detail.setPrixRevientUnite(rs.getInt("fd_prix_revient"));
                detail.setCreatedAt(rs.getTimestamp("fd_created_at"));
                detail.setUpdatedAt(rs.getTimestamp("fd_updated_at"));

                if (rs.getObject("fd_deleted_by") != null) {
                    Utilisateur deletedBy = new Utilisateur();
                    deletedBy.setId(rs.getInt("fd_deleted_by"));
                    detail.setDeletedBy(deletedBy);
                }

                details.add(detail);
            }

            // Calcul des mouvements de stock seulement si on a des données
            if (!details.isEmpty()) {
                int stockActuel = details.get(0).getProduit().getQteEnStock();
                
                for (FactureDetail detail : details) {
                    MouvementStock mvt = new MouvementStock();
                    mvt.setDate(detail.getCreatedAt());
                    mvt.setStockDebut(stockActuel);
                    
                    if (detail.getQuantite() > 0) { // Achat
                        mvt.setQteIn(detail.getQuantite());
                        mvt.setQteOut(0);
                        stockActuel += detail.getQuantite();
                    } else { // Vente
                        mvt.setQteIn(0);
                        mvt.setQteOut(detail.getQteVendu());
                        stockActuel -= detail.getQteVendu();
                    }
                    
                    mvt.setStockFin(stockActuel);
                    mvt.setFactureDetail(detail);
                    mouvements.add(mvt);
                }
                
                // Inverser pour avoir du plus récent au plus ancien
                Collections.reverse(mouvements);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mouvements;
    }

    public BigDecimal recupererGrandTotal() {
        BigDecimal grandTotal = BigDecimal.ZERO;

        String sql = "SELECT ID, PRIX_VENTE FROM PRODUITS WHERE STATUT = 'VISIBLE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            MouvementStockDAO msDao = new MouvementStockDAO();

            while (rs.next()) {
                int produitId = rs.getInt("ID");
                BigDecimal prixVente = rs.getBigDecimal("PRIX_VENTE");

                // Récupération de la quantité réelle en stock via MouvementStockDAO
                List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produitId);
                int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();

                if (qteEnStock > 0) {
                    BigDecimal totalProduit = prixVente.multiply(BigDecimal.valueOf(qteEnStock));
                    grandTotal = grandTotal.add(totalProduit);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Optionnel : arrondi à 2 décimales
        grandTotal = grandTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

        return grandTotal;
    }

    // Méthode utilitaire pour extraire un produit depuis ResultSet
   private Produit extraireProduit(ResultSet rs) throws SQLException {
    Produit produit = new Produit();
    produit.setId(rs.getInt("ID"));
    produit.setNom(rs.getString("NOM"));
    produit.setCodeProduit(rs.getString("CODE_PRODUIT"));
    produit.setDescription(rs.getString("DESCRIPTION"));
    produit.setImageUrl(rs.getString("IMAGE_URL"));
    produit.setEmplacement(rs.getString("EMPLACEMENT"));
    produit.setCategorieId(rs.getInt("CATEGORIE_ID"));
    produit.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
    produit.setUniteVente(rs.getString("UNITE_VENTE"));
    produit.setContenuParUnite(rs.getString("CONTENU_PAR_UNITE"));
    produit.setSeuilAlerte(rs.getInt("SEUIL_ALERTE"));
    produit.setPrixVente(rs.getBigDecimal("PRIX_VENTE"));
    produit.setQteEnStock(rs.getInt("QTE_EN_STOCK"));
    produit.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
    produit.setCreatedAt(rs.getTimestamp("CREATED_AT"));
    produit.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
    produit.setDeletedBy(rs.getObject("DELETED_BY") != null ? rs.getInt("DELETED_BY") : null);
    produit.setQtePoints(rs.getInt("QTE_POINTS"));
    
    // Récupérer RAYON_ID
    if (rs.getObject("RAYON_ID") != null) {
        produit.setRayonId(rs.getInt("RAYON_ID"));
        
        // Créer l'objet Rayon
        Rayon rayon = new Rayon();
        rayon.setId(rs.getInt("RAYON_ID"));
        rayon.setNom(rs.getString("rayon_nom"));
        rayon.setDescription(rs.getString("rayon_description"));
        produit.setRayon(rayon);
    }

    // Charger les objets catégorie et sous-catégorie
    MenuCategorieDAO menuCatDAO = new MenuCategorieDAO();

    if (produit.getCategorieId() > 0) {
        MenuCategorie categorie = new MenuCategorie();
        categorie.setId(produit.getCategorieId());
        categorie.setNom(rs.getString("categorie_nom"));
        categorie.setDescription(rs.getString("categorie_description"));
        
        // Associer le rayon à la catégorie
        if (produit.getRayon() != null) {
            categorie.setRayon(produit.getRayon());
        }
        
        produit.setCategorie(categorie);
    }

    if (produit.getSousCategorieId() > 0) {
        MenuCategorie sousCategorie = new MenuCategorie();
        sousCategorie.setId(produit.getSousCategorieId());
        sousCategorie.setNom(rs.getString("sous_categorie_nom"));
        sousCategorie.setDescription(rs.getString("sous_categorie_description"));
        produit.setSousCategorie(sousCategorie);
    }

    return produit;
}
}