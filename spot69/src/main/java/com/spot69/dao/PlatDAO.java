package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    public boolean isProduitAlreadyInMenu(int produitId) {
        String sql = "SELECT COUNT(*) FROM PLAT WHERE PRODUIT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean ajouterPlatsProduitsExistants(List<Plat> plats) {
        String checkSql = "SELECT COUNT(*) FROM PLAT WHERE PRODUIT_ID = ?";
        String insertSql = "INSERT INTO PLAT (NOM, DESCRIPTION, PRIX, IMAGE_URL, RAYON_ID, " +
                          "CATEGORIE_ID, SOUS_CATEGORIE_ID, STATUT, QTE_POINTS, CREATION_DATE, " +
                          "UPDATE_DATE, DELETED_BY, UTILISATEUR_ID, PRODUIT_ID) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Démarrer une transaction
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                
                for (Plat plat : plats) {
                    // Vérifier si le produit est déjà utilisé
                    checkStmt.setInt(1, plat.getProductId());
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        continue; // Passer au suivant si déjà présent
                    }
                    
                    // Préparer l'insertion
                    int index = 1;
                    insertStmt.setNull(index++, Types.VARCHAR);   // NOM
                    insertStmt.setNull(index++, Types.VARCHAR);   // DESCRIPTION
                    insertStmt.setNull(index++, Types.DECIMAL);   // PRIX
                    insertStmt.setNull(index++, Types.VARCHAR);   // IMAGE_URL
                    insertStmt.setNull(index++, Types.INTEGER);   // RAYON_ID
                    
                    // CATEGORIE_ID
                    if (plat.getCategorieId() != null && plat.getCategorieId() > 0) {
                        insertStmt.setInt(index++, plat.getCategorieId());
                    } else {
                        insertStmt.setNull(index++, Types.INTEGER);
                    }
                    
                    // SOUS_CATEGORIE_ID
                    if (plat.getSousCategorieId() != null && plat.getSousCategorieId() > 0) {
                        insertStmt.setInt(index++, plat.getSousCategorieId());
                    } else {
                        insertStmt.setNull(index++, Types.INTEGER);
                    }
                    
                    // STATUT
                    insertStmt.setString(index++, "VISIBLE");
                    
                    // QTE_POINTS
                    insertStmt.setNull(index++, Types.INTEGER);
                    
                    // DATES
                    LocalDateTime now = LocalDateTime.now();
                    insertStmt.setTimestamp(index++, Timestamp.valueOf(now));
                    insertStmt.setTimestamp(index++, Timestamp.valueOf(now));
                    
                    // DELETED_BY
                    insertStmt.setInt(index++, 0);
                    
                    // UTILISATEUR_ID
                    insertStmt.setInt(index++, plat.getUtilisateurId());
                    
                    // PRODUIT_ID
                    insertStmt.setInt(index++, plat.getProductId());
                    
                    insertStmt.addBatch();
                }
                
                // Exécuter tous les inserts en batch
                int[] results = insertStmt.executeBatch();
                conn.commit();
                
                // Vérifier si au moins un insert a réussi
                for (int result : results) {
                    if (result > 0) {
                        return true;
                    }
                }
                
                return false;
                
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Ajouter un plat OLD
    public boolean ajouterPlat(Plat plat) {
        String sql = "INSERT INTO PLAT (NOM, DESCRIPTION, PRIX, IMAGE_URL, RAYON_ID, CATEGORIE_ID, " +
                     "SOUS_CATEGORIE_ID, CREATION_DATE, UPDATE_DATE, UTILISATEUR_ID, STATUT, " +
                     "QTE_POINTS, PRODUIT_ID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plat.getNom());
            stmt.setString(2, plat.getDescription());
            stmt.setDouble(3, plat.getPrix());
            stmt.setString(4, plat.getImage());
            
            // Gestion des IDs (peuvent être null)
            if (plat.getRayonId() != null) {
                stmt.setInt(5, plat.getRayonId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (plat.getCategorieId() != null) {
                stmt.setInt(6, plat.getCategorieId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (plat.getSousCategorieId() != null) {
                stmt.setInt(7, plat.getSousCategorieId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setTimestamp(8, Timestamp.valueOf(plat.getCreationDate()));
            stmt.setTimestamp(9, Timestamp.valueOf(plat.getUpdateDate()));
            stmt.setInt(10, plat.getUtilisateurId());
            stmt.setString(11, "visible");
            stmt.setInt(12, plat.getQtePoints());
            
            if (plat.getProductId() != null) {
                stmt.setInt(13, plat.getProductId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Plat> searchPlats(String searchTerm) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT * FROM PLAT WHERE NOM LIKE ? OR description LIKE ? AND STATUT = 'VISIBLE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                plats.add(extrairePlatComplet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }
    // Ajouter un plat avec produit existant
    public boolean ajouterPlatProduitExistante(Plat plat) {
        // Validation des champs requis minimum
        if (plat.getProductId() <= 0 || plat.getUtilisateurId() <= 0) {
            System.out.println("Champs requis manquants: productId et utilisateurId");
            return false;
        }

        String checkSql = "SELECT COUNT(*) FROM PLAT WHERE PRODUIT_ID = ?";
        String insertSql = "INSERT INTO PLAT (NOM, DESCRIPTION, PRIX, IMAGE_URL, RAYON_ID, " +
                          "CATEGORIE_ID, SOUS_CATEGORIE_ID, STATUT, QTE_POINTS, CREATION_DATE, " +
                          "UPDATE_DATE, DELETED_BY, UTILISATEUR_ID, PRODUIT_ID) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // Vérifier si le produit est déjà utilisé dans un plat
            checkStmt.setInt(1, plat.getProductId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Ce produit est déjà associé à un plat. Insertion annulée.");
                return false;
            }

            // Insérer le nouveau plat
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                int index = 1;
                
                // Colonnes qui peuvent être NULL
                insertStmt.setNull(index++, Types.VARCHAR);   // NOM
                insertStmt.setNull(index++, Types.VARCHAR);   // DESCRIPTION
                insertStmt.setNull(index++, Types.DECIMAL);   // PRIX
                insertStmt.setNull(index++, Types.VARCHAR);   // IMAGE_URL
                insertStmt.setNull(index++, Types.INTEGER);   // RAYON_ID
                
                // CATEGORIE_ID - peut être NULL
                if (plat.getCategorieId() != null && plat.getCategorieId() > 0) {
                    insertStmt.setInt(index++, plat.getCategorieId());
                } else {
                    insertStmt.setNull(index++, Types.INTEGER);
                }
                
                // SOUS_CATEGORIE_ID - peut être NULL
                if (plat.getSousCategorieId() != null && plat.getSousCategorieId() > 0) {
                    insertStmt.setInt(index++, plat.getSousCategorieId());
                } else {
                    insertStmt.setNull(index++, Types.INTEGER);
                }
                
                // STATUT - valeur par défaut "VISIBLE" si null
                if (plat.getStatut() != null && !plat.getStatut().isEmpty()) {
                    insertStmt.setString(index++, plat.getStatut());
                } else {
                    insertStmt.setString(index++, "VISIBLE");
                }
                
                // QTE_POINTS - peut être NULL
                if (plat.getQtePoints() != 0) {
                    insertStmt.setInt(index++, plat.getQtePoints());
                } else {
                    insertStmt.setNull(index++, Types.INTEGER);
                }
                
                // DATES - date actuelle par défaut
                LocalDateTime now = LocalDateTime.now();
                insertStmt.setTimestamp(index++, Timestamp.valueOf(now));  // CREATION_DATE
                insertStmt.setTimestamp(index++, Timestamp.valueOf(now));  // UPDATE_DATE
                
                // DELETED_BY - toujours 0 par défaut
                insertStmt.setInt(index++, 0);
                
                // UTILISATEUR_ID - REQUIS
                insertStmt.setInt(index++, plat.getUtilisateurId());
                
                // PRODUIT_ID - REQUIS
                insertStmt.setInt(index++, plat.getProductId());

                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier un plat
    public boolean modifierPlat(Plat plat) {
        String sql = "UPDATE PLAT SET NOM = ?, DESCRIPTION = ?, PRIX = ?, IMAGE_URL = ?, " +
                     "RAYON_ID = ?, CATEGORIE_ID = ?, SOUS_CATEGORIE_ID = ?, UPDATE_DATE = ?, " +
                     "UTILISATEUR_ID = ?, QTE_POINTS = ?, PRODUIT_ID = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plat.getNom());
            stmt.setString(2, plat.getDescription());
            stmt.setDouble(3, plat.getPrix());
            stmt.setString(4, plat.getImage());
            
            // Gestion des IDs (peuvent être null)
            if (plat.getRayonId() != null) {
                stmt.setInt(5, plat.getRayonId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (plat.getCategorieId() != null) {
                stmt.setInt(6, plat.getCategorieId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (plat.getSousCategorieId() != null) {
                stmt.setInt(7, plat.getSousCategorieId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, plat.getUtilisateurId());
            stmt.setInt(10, plat.getQtePoints());
            
            if (plat.getProductId() != null) {
                stmt.setInt(11, plat.getProductId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            
            stmt.setInt(12, plat.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un plat (soft delete)
    public boolean supprimerPlat(int id, int utilisateurId) {
        String sql = "UPDATE PLAT SET STATUT = 'DELETED', UPDATE_DATE = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chercher un plat par ID avec toutes les relations
    public Plat chercherParId(int id) {
        String sql = "SELECT p.*, " +
                     "r.ID AS rayon_id, r.NOM AS rayon_nom, " +
                     "c.ID AS categorie_id, c.NOM AS categorie_nom, " +
                     "sc.ID AS sous_categorie_id, sc.NOM AS sous_categorie_nom, " +
                     "pr.ID AS produit_id, pr.IMAGE_URL AS produit_image_url, " +
                     "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, " +
                     "pr.QTE_POINTS AS produits_qte_points, pr.PRIX_VENTE AS produit_prix, " +
                     "pr.CATEGORIE_ID AS produit_categorie_id, pr.SOUS_CATEGORIE_ID AS produit_sous_categorie_id, " +
                     "pr.RAYON_ID AS produit_rayon_id " +
                     "FROM PLAT p " +
                     "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID " +
                     "LEFT JOIN MENU_CATEGORIE c ON p.CATEGORIE_ID = c.ID " +
                     "LEFT JOIN MENU_CATEGORIE sc ON p.SOUS_CATEGORIE_ID = sc.ID " +
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     "WHERE p.ID = ? AND p.STATUT = 'visible'";
        
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extrairePlatComplet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister tous les plats avec relations
 // Lister tous les plats avec relations - VERSION CORRIGÉE AVEC EXTRACTION ET LOGS DE DÉBOGAGE
    public List<Plat> listerPlats() {
        List<Plat> liste = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "r.ID AS rayon_id, r.NOM AS rayon_nom, " +
                     "c.ID AS categorie_id, c.NOM AS categorie_nom, " +
                     "sc.ID AS sous_categorie_id, sc.NOM AS sous_categorie_nom, " +
                     "pr.ID AS produit_id, pr.IMAGE_URL AS produit_image_url, " +
                     "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, " +
                     "pr.QTE_POINTS AS produit_qte_points, pr.PRIX_VENTE AS produit_prix, " +
                     
                     // Ajout des champs du produit pour la hiérarchie
                     "pr.CATEGORIE_ID AS produit_categorie_id, " +
                     "pr.SOUS_CATEGORIE_ID AS produit_sous_categorie_id, " +
                     "pr.RAYON_ID AS produit_rayon_id, " +
                     
                     // Jointures pour les infos de la hiérarchie du produit
                     "pr_c.ID AS pr_categorie_id, pr_c.NOM AS pr_categorie_nom, " +
                     "pr_sc.ID AS pr_sous_categorie_id, pr_sc.NOM AS pr_sous_categorie_nom, " +
                     "pr_r.ID AS pr_rayon_id, pr_r.NOM AS pr_rayon_nom, " +
                     
                     // Champs de date pour correction
                     "p.CREATION_DATE AS creation_date, " +
                     "p.UPDATE_DATE AS update_date " +
                     
                     "FROM PLAT p " +
                     
                     // Jointures pour la hiérarchie du plat (si le plat n'est pas lié à un produit)
                     "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID " +
                     "LEFT JOIN MENU_CATEGORIE c ON p.CATEGORIE_ID = c.ID " +
                     "LEFT JOIN MENU_CATEGORIE sc ON p.SOUS_CATEGORIE_ID = sc.ID " +
                     
                     // Jointure avec le produit (si le plat est lié à un produit)
                     "LEFT JOIN PRODUITS pr ON p.PRODUIT_ID = pr.ID " +
                     
                     // Jointures pour la hiérarchie du produit (si le plat est lié à un produit)
                     "LEFT JOIN RAYON pr_r ON pr.RAYON_ID = pr_r.ID " +
                     "LEFT JOIN MENU_CATEGORIE pr_c ON pr.CATEGORIE_ID = pr_c.ID " +
                     "LEFT JOIN MENU_CATEGORIE pr_sc ON pr.SOUS_CATEGORIE_ID = pr_sc.ID " +
                     
                     "WHERE p.STATUT = 'visible' " +
                     "ORDER BY p.CREATION_DATE DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== DÉBUT DE L'EXTRACTION DES PLATS ===");
            
            // Initialiser le DAO pour les mouvements de stock
            MouvementStockDAO msDao = new MouvementStockDAO();
            
            int totalPlatsLus = 0;
            int platsFiltresStock = 0;
            int platsAjoutes = 0;

            while (rs.next()) {
                totalPlatsLus++;
                
                // Extraire le plat de base avec ses relations directes
                Plat plat = extrairePlatComplet(rs);
                
                System.out.println("\n--- Traitement du plat ID: " + plat.getId() + " ---");
                System.out.println("Nom du plat: " + plat.getNom());
                System.out.println("Produit ID associé: " + plat.getProductId());
                
                // Vérifier si le plat est lié à un produit
                Integer produitId = plat.getProductId();
                boolean platEstLieAProduit = (produitId != null && produitId > 0);
                
                System.out.println("Type de plat: " + (platEstLieAProduit ? "LIÉ À UN PRODUIT" : "PLAT SIMPLE"));
                
                if (platEstLieAProduit) {
                    // Le plat est lié à un produit existant
                    System.out.println("Récupération du produit ID: " + produitId);
                    
                    Produit produit = extraireProduitFromResultSet(rs);
                    
                    // Compléter les informations manquantes du produit
                    int prRayonId = rs.getInt("produit_rayon_id");
                    if (!rs.wasNull() && prRayonId > 0) {
                        Rayon prRayon = new Rayon();
                        prRayon.setId(prRayonId);
                        prRayon.setNom(rs.getString("pr_rayon_nom"));
                        produit.setRayon(prRayon);
                        
                        // Mettre à jour le rayon du produit dans le produit
                        produit.setRayonId(prRayonId);
                    }
                    
                    int prCategorieId = rs.getInt("produit_categorie_id");
                    if (!rs.wasNull() && prCategorieId > 0) {
                        MenuCategorie prCategorie = new MenuCategorie();
                        prCategorie.setId(prCategorieId);
                        prCategorie.setNom(rs.getString("pr_categorie_nom"));
                        
                        // Associer le rayon à la catégorie si disponible
                        if (produit.getRayon() != null) {
                            prCategorie.setRayon(produit.getRayon());
                        }
                        
                        produit.setCategorie(prCategorie);
                        produit.setCategorieId(prCategorieId);
                    }
                    
                    int prSousCategorieId = rs.getInt("produit_sous_categorie_id");
                    if (!rs.wasNull() && prSousCategorieId > 0) {
                        MenuCategorie prSousCategorie = new MenuCategorie();
                        prSousCategorie.setId(prSousCategorieId);
                        prSousCategorie.setNom(rs.getString("pr_sous_categorie_nom"));
                        produit.setSousCategorie(prSousCategorie);
                        produit.setSousCategorieId(prSousCategorieId);
                    }
                    
                    // Récupérer le stock via MouvementStockDAO
//                    System.out.println("Vérification du stock du produit ID: " + produitId);
//                    List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produitId);
//                    int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
//                    produit.setQteEnStock(qteEnStock);
//                    
//                    System.out.println("Stock du produit '" + produit.getNom() + "': " + qteEnStock + " unités");
//                    
//                    // FILTRE IMPORTANT : Ne pas ajouter le plat si le stock du produit est ≤ 0
//                    if (qteEnStock <= 0) {
//                        platsFiltresStock++;
//                        System.out.println("❌ FILTRÉ - Stock insuffisant (stock = " + qteEnStock + ")");
//                        System.out.println("Produit '" + produit.getNom() + "' non disponible. Plat ID " + plat.getId() + " non ajouté à la liste.");
//                        continue; // Passer au plat suivant sans ajouter celui-ci à la liste
//                    } else {
//                        System.out.println("✅ Stock OK - Plat ajouté à la liste");
//                    }
                    
                    // Pour un plat lié à un produit, on doit OVERRIDER les IDs de hiérarchie
                    // avec ceux du produit, pas ceux du plat
                    plat.setRayonId(produit.getRayonId());
                    plat.setCategorieId(produit.getCategorieId());
                    plat.setSousCategorieId(produit.getSousCategorieId());
                    
                    // Mettre à jour les objets liés dans le plat avec ceux du produit
                    if (produit.getRayon() != null) {
                        plat.setRayon(produit.getRayon());
                    }
                    if (produit.getCategorie() != null) {
                        plat.setCategorie(produit.getCategorie());
                        plat.setCategorieMenu(produit.getCategorie());
                    }
                    if (produit.getSousCategorie() != null) {
                        plat.setSousCategorie(produit.getSousCategorie());
                        plat.setSousCategorieMenu(produit.getSousCategorie());
                    }
                    
                    // Associer le produit au plat
                    plat.setProduit(produit);
                } else {
                    // Le plat n'est pas lié à un produit
                    System.out.println("✅ Plat simple - Ajouté sans vérification de stock");
                    
                    // La hiérarchie est déjà extraite par extrairePlatComplet()
                    // Mais on s'assure que les IDs sont cohérents
                    
                    // Vérifier si les IDs de hiérarchie existent
                    Integer platRayonId = plat.getRayonId();
                    Integer platCategorieId = plat.getCategorieId();
                    Integer platSousCategorieId = plat.getSousCategorieId();
                    
                    // Si un ID est manquant dans le plat mais existe dans le produit (au cas où)
                    if ((platRayonId == null || platRayonId == 0) && rs.getInt("produit_rayon_id") > 0) {
                        plat.setRayonId(rs.getInt("produit_rayon_id"));
                    }
                    if ((platCategorieId == null || platCategorieId == 0) && rs.getInt("produit_categorie_id") > 0) {
                        plat.setCategorieId(rs.getInt("produit_categorie_id"));
                    }
                    if ((platSousCategorieId == null || platSousCategorieId == 0) && rs.getInt("produit_sous_categorie_id") > 0) {
                        plat.setSousCategorieId(rs.getInt("produit_sous_categorie_id"));
                    }
                }
                
                liste.add(plat);
                platsAjoutes++;
                
                System.out.println("Plat ID " + plat.getId() + " ajouté avec succès");
            }

            // Récapitulatif
            System.out.println("\n=== RÉCAPITULATIF DE L'EXTRACTION ===");
            System.out.println("Total de plats lus dans la base: " + totalPlatsLus);
            System.out.println("Plats filtrés (stock ≤ 0): " + platsFiltresStock);
            System.out.println("Plats ajoutés à la liste finale: " + platsAjoutes);
            System.out.println("Taux de filtrage: " + (totalPlatsLus > 0 ? 
                String.format("%.1f", (platsFiltresStock * 100.0 / totalPlatsLus)) + "%" : "0%"));
            System.out.println("=== FIN DE L'EXTRACTION DES PLATS ===\n");

        } catch (SQLException e) {
            System.err.println("❌ ERREUR SQL dans listerPlats(): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ ERREUR inattendue dans listerPlats(): " + e.getMessage());
            e.printStackTrace();
        }
        
        return liste;
    }
 public List<Plat> getAllPlatsWithoutRestrictions() {
    List<Plat> plats = new ArrayList<>();

    String sql = "SELECT p.*, " +
                "r.ID AS rayon_id, r.NOM AS rayon_nom, r.STATUT AS rayon_statut, " +
                "c.ID AS categorie_id, c.NOM AS categorie_nom, c.STATUT AS categorie_statut, " +
                "sc.ID AS sous_categorie_id, sc.NOM AS sous_categorie_nom, sc.STATUT AS sous_categorie_statut, " +
                "pr.ID AS produit_id_db, pr.IMAGE_URL AS produit_image_url, " +
                "pr.NOM AS produit_nom, pr.DESCRIPTION AS produit_description, " +
                "pr.QTE_POINTS AS produits_qte_points, pr.PRIX_VENTE AS produit_prix_vente, " +
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
                System.out.println("Colonne " + i + ": " + metaData.getColumnName(i));
            }
            System.out.println("==============================================");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Initialiser le DAO pour les mouvements de stock
        MouvementStockDAO msDao = new MouvementStockDAO();

        while (rs.next()) {
            Plat plat = new Plat();
            
            // Informations de base du plat
            plat.setId(rs.getInt("ID"));
            plat.setNom(rs.getString("NOM"));
            plat.setDescription(rs.getString("DESCRIPTION"));
            plat.setPrix(rs.getDouble("PRIX"));
            plat.setImage(rs.getString("IMAGE_URL"));
            plat.setQtePoints(rs.getInt("QTE_POINTS"));
            plat.setStatut(rs.getString("STATUT"));
            plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
            
            // IDs du plat (peuvent être NULL) - utiliser getObject et wasNull()
            Integer categorieId = rs.getObject("CATEGORIE_ID", Integer.class);
            plat.setCategorieId(rs.wasNull() ? null : categorieId);
            
            Integer sousCategorieId = rs.getObject("SOUS_CATEGORIE_ID", Integer.class);
            plat.setSousCategorieId(rs.wasNull() ? null : sousCategorieId);
            
            Integer rayonId = rs.getObject("RAYON_ID", Integer.class);
            plat.setRayonId(rs.wasNull() ? null : rayonId);
            
            Integer produitId = rs.getObject("PRODUIT_ID", Integer.class);
            plat.setProductId(rs.wasNull() ? null : produitId);

            // Dates
            Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
            if (creationDate != null) {
                plat.setCreationDate(creationDate.toLocalDateTime());
            }

            Timestamp updateDate = rs.getTimestamp("UPDATE_DATE");
            if (updateDate != null) {
                plat.setUpdateDate(updateDate.toLocalDateTime());
            }

            // Gestion du produit associé (si existant)
            // Utiliser produit_id_db au lieu de produit_id pour éviter conflit
            Integer produitIdFromJoin = rs.getObject("produit_id_db", Integer.class);
            
            // Variable pour stocker si on doit ajouter ce plat à la liste
            boolean ajouterPlat = true;
            
            if (produitIdFromJoin != null && !rs.wasNull()) {
                Produit produit = new Produit();
                produit.setId(produitIdFromJoin);
                produit.setNom(getStringOrNull(rs, "produit_nom"));
                produit.setDescription(getStringOrNull(rs, "produit_description"));
                produit.setImageUrl(getStringOrNull(rs, "produit_image_url"));
                
                // Prix du produit
                BigDecimal produitPrix = rs.getBigDecimal("produit_prix_vente");
                produit.setPrixVente(produitPrix != null ? produitPrix : BigDecimal.ZERO);
                
                produit.setQtePoints(rs.getInt("produits_qte_points"));
                
                // Catégories du PRODUIT (pas du plat)
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
                    produit.setRayon(produitRayon);
                }
                
                // IMPORTANT : Vérifier le stock du produit
                // Récupérer le stock via MouvementStockDAO
                List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produitIdFromJoin);
                int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
                produit.setQteEnStock(qteEnStock);
                
                // FILTRE CRITIQUE : Ne pas ajouter le plat si le stock du produit est ≤ 0
                if (qteEnStock <= 0) {
                    ajouterPlat = false;
                    System.out.println("Plat ID " + plat.getId() + " non ajouté - Produit ID " + 
                                     produitIdFromJoin + " en rupture de stock (stock = " + qteEnStock + ")");
                } else {
                    // Stock OK, on associe le produit au plat
                    plat.setProduit(produit);
                    plat.setTypePlat("PRODUIT");

                    // Si le plat n'a pas de nom, utiliser celui du produit
                    if (plat.getNom() == null || plat.getNom().trim().isEmpty()) {
                        plat.setNom(produit.getNom());
                    }
                }
            } else {
                plat.setTypePlat("PLAT_SIMPLE");
            }

            // Si le plat doit être ajouté (stock OK ou pas de produit associé)
            if (ajouterPlat) {
                // Créer l'objet Rayon pour le plat (avec gestion des NULL)
                Rayon rayon = new Rayon();
                rayon.setId(getIntOrZero(rs, "rayon_id"));
                rayon.setNom(getStringOrNull(rs, "rayon_nom"));
                plat.setRayon(rayon);

                // Créer l'objet Catégorie pour le plat (avec gestion des NULL)
                MenuCategorie categorie = new MenuCategorie();
                categorie.setId(getIntOrZero(rs, "categorie_id"));
                categorie.setNom(getStringOrNull(rs, "categorie_nom"));
                categorie.setStatut(getStringOrNull(rs, "categorie_statut"));
                plat.setCategorie(categorie);

                // Créer l'objet Sous-Catégorie pour le plat (avec gestion des NULL)
                MenuCategorie sousCategorieObj = new MenuCategorie();
                sousCategorieObj.setId(getIntOrZero(rs, "sous_categorie_id"));
                sousCategorieObj.setNom(getStringOrNull(rs, "sous_categorie_nom"));
                sousCategorieObj.setStatut(getStringOrNull(rs, "sous_categorie_statut"));
                plat.setSousCategorie(sousCategorieObj);

                plats.add(plat);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur dans getAllPlatsWithoutRestrictions: " + e.getMessage());
        
        // Afficher plus de détails pour le débogage
        System.err.println("SQL Query: " + sql);
    }

    return plats;
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

    // Lister plats par catégorie
//    OLD
//    public List<Plat> listePlats(int categorieId) {
//        List<Plat> liste = new ArrayList<>();
//        
//        String sql = "SELECT DISTINCT " +
//                "p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, " +
//                "p.RAYON_ID, p.CATEGORIE_ID, p.SOUS_CATEGORIE_ID, " +
//                "r.NOM AS rayon_nom, " +
//                "c.NOM AS categorie_nom, " +
//                "sc.NOM AS sous_categorie_nom " +
//                "FROM PLAT p " +
//                "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID " +
//                "LEFT JOIN MENU_CATEGORIE c ON p.CATEGORIE_ID = c.ID " +
//                "LEFT JOIN MENU_CATEGORIE sc ON p.SOUS_CATEGORIE_ID = sc.ID " +
//                "WHERE p.STATUT = 'VISIBLE' " +
//                "AND p.CATEGORIE_ID = ? " +
//                "AND p.NOM != '' " +
//                "UNION ALL " +
//                "SELECT " +
//                "pr.ID, pr.NOM, pr.DESCRIPTION, pr.PRIX_VENTE as PRIX, pr.IMAGE_URL, " +
//                "pr.RAYON_ID, pr.CATEGORIE_ID, pr.SOUS_CATEGORIE_ID, " +
//                "r2.NOM AS rayon_nom, " +
//                "c2.NOM AS categorie_nom, " +
//                "sc2.NOM AS sous_categorie_nom " +
//                "FROM PRODUITS pr " +
//                "LEFT JOIN RAYON r2 ON pr.RAYON_ID = r2.ID " +
//                "LEFT JOIN CATEGORIE c2 ON pr.CATEGORIE_ID = c2.ID " +
//                "LEFT JOIN SOUS_CATEGORIE sc2 ON pr.SOUS_CATEGORIE_ID = sc2.ID " +
//                "WHERE pr.STATUT = 'VISIBLE' " +
//                "AND pr.CATEGORIE_ID = ? " +
//                "ORDER BY NOM";
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, categorieId);
//            stmt.setInt(2, categorieId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Plat plat = extrairePlatApp(rs);
//                liste.add(plat);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return liste;
//    }
    
    public List<Plat> listePlats(int categorieId) {
        List<Plat> liste = new ArrayList<>();
        
        String sql = "SELECT DISTINCT " +
                "p.ID, p.NOM, p.DESCRIPTION, p.PRIX, p.IMAGE_URL, " +
                "p.RAYON_ID, p.CATEGORIE_ID, p.SOUS_CATEGORIE_ID, " +
                "r.NOM AS rayon_nom, " +
                "c.NOM AS categorie_nom, " +
                "sc.NOM AS sous_categorie_nom, " +
                "NULL AS produit_id, " + 
                "1 AS est_plat " + 
                "FROM PLAT p " +
                "LEFT JOIN RAYON r ON p.RAYON_ID = r.ID " +
                "LEFT JOIN MENU_CATEGORIE c ON p.CATEGORIE_ID = c.ID " +
                "LEFT JOIN MENU_CATEGORIE sc ON p.SOUS_CATEGORIE_ID = sc.ID " +
                "WHERE p.STATUT = 'VISIBLE' " +
                "AND p.CATEGORIE_ID = ? " +
                "AND p.NOM != '' " +
                "UNION ALL " +
                "SELECT " +
                "pr.ID, pr.NOM, pr.DESCRIPTION, pr.PRIX_VENTE as PRIX, pr.IMAGE_URL, " +
                "pr.RAYON_ID, pr.CATEGORIE_ID, pr.SOUS_CATEGORIE_ID, " +
                "r2.NOM AS rayon_nom, " +
                "c2.NOM AS categorie_nom, " +
                "sc2.NOM AS sous_categorie_nom, " +
                "pr.ID AS produit_id, " + 
                "0 AS est_plat " + 
                "FROM PRODUITS pr " +
                "LEFT JOIN RAYON r2 ON pr.RAYON_ID = r2.ID " +
                "LEFT JOIN MENU_CATEGORIE c2 ON pr.CATEGORIE_ID = c2.ID " +
                "LEFT JOIN MENU_CATEGORIE sc2 ON pr.SOUS_CATEGORIE_ID = sc2.ID " +
                "WHERE pr.STATUT = 'VISIBLE' " +
                "AND pr.CATEGORIE_ID = ? " +
                "ORDER BY NOM";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);
            stmt.setInt(2, categorieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Plat plat = extrairePlatApp(rs);
                
                // Vérifier si c'est un produit (pas un plat) pour ajouter le stock
                int estPlat = rs.getInt("est_plat");
                if (estPlat == 0) { // C'est un produit
                    int produitId = rs.getInt("produit_id");
                    if (produitId > 0) {
                        // Vérifier le stock via MouvementStockDAO
                        MouvementStockDAO msDao = new MouvementStockDAO();
                        List<MouvementStock> mouvements = msDao.mouvementStockParProduit(produitId);
                        int qteEnStock = mouvements.isEmpty() ? 0 : mouvements.get(0).getStockFin();
                        
                        // Créer un objet Produit pour le plat
                        Produit produit = new Produit();
                        produit.setId(produitId);
                        produit.setQteEnStock(qteEnStock);
                        produit.setNom(rs.getString("NOM"));
                        produit.setDescription(rs.getString("DESCRIPTION"));
                        produit.setPrixVente(rs.getBigDecimal("PRIX"));
                        produit.setImageUrl(rs.getString("IMAGE_URL"));
                        produit.setRayonId(rs.getInt("RAYON_ID"));
                        produit.setCategorieId(rs.getInt("CATEGORIE_ID"));
                        produit.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
                        
                        // Associer le produit au plat
                        plat.setProduit(produit);
                    }
                }
                
                liste.add(plat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Méthode pour extraire un plat complet avec toutes les relations
    private Plat extrairePlatComplet(ResultSet rs) throws SQLException {
        Plat plat = new Plat();
        plat.setId(rs.getInt("ID"));
        plat.setNom(rs.getString("NOM"));
        plat.setDescription(rs.getString("DESCRIPTION"));
        plat.setPrix(rs.getDouble("PRIX"));
        plat.setImage(rs.getString("IMAGE_URL"));
        
        // IDs
        plat.setRayonId(rs.getInt("RAYON_ID"));
        if (rs.wasNull()) plat.setRayonId(null);
        
        plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
        if (rs.wasNull()) plat.setCategorieId(null);
        
        plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
        if (rs.wasNull()) plat.setSousCategorieId(null);
        
        plat.setProductId(rs.getInt("PRODUIT_ID"));
        if (rs.wasNull()) plat.setProductId(null);
        
        // Dates
        plat.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime());
        plat.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime());
        
        // Autres champs
        plat.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        plat.setQtePoints(rs.getInt("QTE_POINTS"));
        
        // Objets liés - Rayon
        int rayonId = rs.getInt("rayon_id");
        if (!rs.wasNull()) {
            Rayon rayon = new Rayon();
            rayon.setId(rayonId);
            rayon.setNom(rs.getString("rayon_nom"));
            plat.setRayon(rayon);
        }
        
        // Objets liés - Catégorie
        int categorieId = rs.getInt("categorie_id");
        if (!rs.wasNull()) {
            MenuCategorie categorie = new MenuCategorie();
            categorie.setId(categorieId);
            categorie.setNom(rs.getString("categorie_nom"));
            plat.setCategorie(categorie);
            plat.setCategorieMenu(categorie);
        }
        
        // Objets liés - Sous-catégorie
        int sousCategorieId = rs.getInt("sous_categorie_id");
        if (!rs.wasNull()) {
            MenuCategorie sousCategorie = new MenuCategorie();
            sousCategorie.setId(sousCategorieId);
            sousCategorie.setNom(rs.getString("sous_categorie_nom"));
            plat.setSousCategorie(sousCategorie);
            plat.setSousCategorieMenu(sousCategorie);
        }
        
        return plat;
    }

    // Méthode pour extraire un produit depuis ResultSet
    private Produit extraireProduitFromResultSet(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("produit_id"));
        produit.setNom(rs.getString("produit_nom"));
        produit.setDescription(rs.getString("produit_description"));
        produit.setImageUrl(rs.getString("produit_image_url"));
        
        BigDecimal prixVente = rs.getBigDecimal("produit_prix");
        produit.setPrixVente(prixVente != null ? prixVente : BigDecimal.ZERO);
        
//        produit.setQtePoints(rs.getInt("produits_qte_points"));
        
        // Si le produit a sa propre catégorisation
        int produitCategorieId = rs.getInt("produit_categorie_id");
        if (!rs.wasNull()) {
            // Vous pouvez charger la catégorie complète si nécessaire
        }
        
        int produitSousCategorieId = rs.getInt("produit_sous_categorie_id");
        if (!rs.wasNull()) {
            // Vous pouvez charger la sous-catégorie complète si nécessaire
        }
        
        int produitRayonId = rs.getInt("produit_rayon_id");
        if (!rs.wasNull()) {
            // Vous pouvez charger le rayon complet si nécessaire
        }
        
        return produit;
    }

    // Méthode utilitaire pour extraire un plat pour l'application
    private Plat extrairePlatApp(ResultSet rs) throws SQLException {
        Plat plat = new Plat();
        plat.setId(rs.getInt("ID"));
        plat.setNom(rs.getString("NOM"));
        plat.setDescription(rs.getString("DESCRIPTION"));
        plat.setPrix(rs.getDouble("PRIX"));
        plat.setImage(rs.getString("IMAGE_URL"));
        
        plat.setRayonId(rs.getInt("RAYON_ID"));
        if (rs.wasNull()) plat.setRayonId(null);
        
        plat.setCategorieId(rs.getInt("CATEGORIE_ID"));
        if (rs.wasNull()) plat.setCategorieId(null);
        
        plat.setSousCategorieId(rs.getInt("SOUS_CATEGORIE_ID"));
        if (rs.wasNull()) plat.setSousCategorieId(null);
        
        return plat;
    }
    
    // Méthode supplémentaire pour charger les catégories d'un rayon
    public List<MenuCategorie> getCategoriesByRayon(int rayonId) {
        List<MenuCategorie> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT mc.* FROM MENU_CATEGORIE mc " +
                     "INNER JOIN RAYON_CATEGORIE rc ON mc.ID = rc.CATEGORIE_ID " +
                     "WHERE rc.RAYON_ID = ? AND mc.PARENT_ID IS NULL " +
                     "ORDER BY mc.NOM";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rayonId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MenuCategorie categorie = new MenuCategorie();
                categorie.setId(rs.getInt("ID"));
                categorie.setNom(rs.getString("NOM"));
                categorie.setDescription(rs.getString("DESCRIPTION"));
                categorie.setParentId(rs.getInt("PARENT_ID"));
                if (rs.wasNull()) categorie.setParentId(null);
                categorie.setImageUrl(rs.getString("IMAGE"));
                categorie.setStatut(rs.getString("STATUT"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}