package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PointManagerDAO {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    private PointHistoriqueManager historiqueManager;
    
    public PointManagerDAO() {
        this.historiqueManager = new PointHistoriqueManager();
    }
    
    // Méthode principale pour attribuer des points lors d'une commande
    public Map<String, Object> attribuerPointsPourCommande(Commande commande, List<CommandeDetail> details, String ipAdresse) {
        Map<String, Object> result = new HashMap<>();
        List<Point> pointsCrees = new ArrayList<>();
        int totalPoints = 0;
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Points par produit/plat
            for (CommandeDetail detail : details) {
                int pointsDetail = 0;
                PointConfig config = null;
                String sourceType = "COMMANDE";
                
                if (detail.getProduitId() != null) {
                    config = getConfigForProduit(conn, detail.getProduitId());
                    if (config != null) {
                        pointsDetail = config.getPoints() * detail.getQuantite();
                        sourceType = "PRODUIT";
                    }
                } else if (detail.getPlatId() != null) {
                    config = getConfigForPlat(conn, detail.getPlatId());
                    if (config != null) {
                        pointsDetail = config.getPoints() * detail.getQuantite();
                        sourceType = "PLAT";
                    }
                }
                
                if (pointsDetail > 0 && config != null) {
                    Point point = creerPoint(conn, 
                        commande.getClientId(),
                        commande.getId(),
                        sourceType,
                        config.getId(),
                        pointsDetail,
                        "Points pour achat de " + detail.getQuantite() + " unité(s)"
                    );
                    
                    if (point != null) {
                        pointsCrees.add(point);
                        totalPoints += pointsDetail;
                        
                        // Créer l'historique détaillé
                        historiqueManager.historiqueAcquisitionCommande(
                            conn, point, detail, 
                            "Config: " + config.getTypeConfig() + " (ID: " + config.getId() + ") - " + config.getPoints() + " pts/unité",
                            ipAdresse
                        );
                    }
                }
            }   
            
            // 2. Points par quantité de commandes journalières
            int pointsQteCommandes = calculerPointsQteCommandesJournalieres(conn, commande);
            if (pointsQteCommandes > 0) {
                Point point = creerPoint(conn,
                    commande.getClientId(),
                    commande.getId(),
                    "QTE_COMMANDES",
                    null,
                    pointsQteCommandes,
                    "Points bonus pour nombre de commandes journalières"
                );
                
                if (point != null) {
                    pointsCrees.add(point);
                    totalPoints += pointsQteCommandes;
                    
                    // Récupérer le nombre de commandes
                    int nbCommandes = getNombreCommandesJournalieres(conn, commande.getClientId());
                    
                    // Créer l'historique
                    historiqueManager.historiqueBonusQteCommandes(
                        conn, point, nbCommandes, ipAdresse
                    );
                }
            }
            
            // 3. Points par montant total
            int pointsMontant = calculerPointsMontantTotal(conn, commande);
            if (pointsMontant > 0) {
                Point point = creerPoint(conn,
                    commande.getClientId(),
                    commande.getId(),
                    "BONUS_MONTANT",
                    null,
                    pointsMontant,
                    "Points bonus pour montant total de commande"
                );
                
                if (point != null) {
                    pointsCrees.add(point);
                    totalPoints += pointsMontant;
                    
                    // Créer l'historique
                    historiqueManager.historiqueBonusMontant(
                        conn, point, commande.getMontantTotal().doubleValue(), ipAdresse
                    );
                }
            }
            
            // 4. Mettre à jour le total de points de l'utilisateur
            if (totalPoints > 0) {
                mettreAJourTotalPointsUtilisateur(conn, commande.getClientId(), totalPoints);
            }
            
            conn.commit();
            
            result.put("success", true);
            result.put("totalPoints", totalPoints);
            result.put("pointsCrees", pointsCrees);
            result.put("details", details.size() + " article(s) traités");
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de l'attribution des points: " + e.getMessage());
        }
        
        return result;
    }
 // NOUVELLES MÉTHODES POUR RÉSERVATIONS
public Map<String, Object> attribuerPointsPourReservation_v2(
        int utilisateurId, 
        int reservationId, 
        BigDecimal montantTotal, 
        String moyenPaiement, 
        String ipAdresse) {
    
    Map<String, Object> result = new HashMap<>();
    
    System.out.println("=== DÉBUT ATTRIBUTION POINTS RÉSERVATION ===");
    System.out.println("Réservation #" + reservationId);
    System.out.println("Utilisateur #" + utilisateurId);
    System.out.println("Montant: " + String.format("%.2f HTG", montantTotal.doubleValue()));
    System.out.println("Moyen paiement: " + moyenPaiement);
    
    try (Connection conn = getConnection()) {
        if ("SOLDE".equals(moyenPaiement)) {
            String sqlCompte = "SELECT solde FROM COMPTE_CLIENT WHERE client_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCompte)) {
                stmt.setInt(1, utilisateurId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal solde = rs.getBigDecimal("solde");
                        if (solde.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("❌ Pas de points - solde négatif");
                            result.put("success", false);
                            result.put("error", "Pas de points attribués - solde négatif (crédit utilisé)");
                            return result;
                        }
                    }
                }
            }
        }
        
        conn.setAutoCommit(false);
        List<Point> pointsCrees = new ArrayList<>();
        int totalPoints = 0;
        
        double montant = montantTotal.doubleValue();
        int pointsMontant = calculerPointsMontantTotalReservation(conn, montant);
        
        System.out.println("Points calculés: " + pointsMontant);
        
     // Dans PointManagerDAO, modifiez la partie où vous créez l'historique :
        if (pointsMontant > 0) {
            Point point = creerPointPourReservation(conn,
                utilisateurId,
                reservationId,
                "RES. EVENEMENT", // Changer le sourceType
                null,
                pointsMontant,
                "Points bonus pour montant total de réservation événement"
            );
            
            if (point != null) {
                pointsCrees.add(point);
                totalPoints += pointsMontant;
                
                // CORRECTION : Utiliser la nouvelle méthode spécifique aux réservations
                historiqueManager.historiqueAcquisitionReservation(
                    conn, point, montant, "Réservation événement", ipAdresse
                );
                
                System.out.println("✅ Point créé - ID: " + point.getId() + 
                                 ", Reservation: " + reservationId + 
                                 ", Points: " + pointsMontant);
            }
        } else {
            System.out.println("ℹ️ Aucun point à attribuer (montant insuffisant)");
        }
        
        if (totalPoints > 0) {
            mettreAJourTotalPointsUtilisateur(conn, utilisateurId, totalPoints);
            System.out.println("✅ Total points attribués: " + totalPoints);
        }
        
        conn.commit();
        
        result.put("success", true);
        result.put("totalPoints", totalPoints);
        result.put("pointsCrees", pointsCrees);
        result.put("montant", montantTotal);
        
        System.out.println("=== FIN ATTRIBUTION POINTS RÉSERVATION ===");
        
    } catch (SQLException e) {
        e.printStackTrace();
        result.put("success", false);
        result.put("error", "Erreur lors de l'attribution des points: " + e.getMessage());
        System.err.println("❌ Erreur SQL: " + e.getMessage());
    }
    
    return result;
}

//NOUVELLE MÉTHODE POUR RÉSERVATIONS DE CHAMBRE
// NOUVELLE MÉTHODE POUR RÉSERVATIONS DE CHAMBRE
public Map<String, Object> attribuerPointsPourReservationChambre_v2(
        int utilisateurId, 
        int reservationId, 
        BigDecimal montantTotal, 
        String moyenPaiement, 
        String ipAdresse) {
    
    Map<String, Object> result = new HashMap<>();
    
    System.out.println("=== DÉBUT ATTRIBUTION POINTS RÉSERVATION CHAMBRE ===");
    System.out.println("Réservation chambre #" + reservationId);
    System.out.println("Utilisateur #" + utilisateurId);
    System.out.println("Montant: " + String.format("%.2f HTG", montantTotal.doubleValue()));
    System.out.println("Moyen paiement: " + moyenPaiement);
    System.out.println("IP: " + ipAdresse);
    
    try (Connection conn = getConnection()) {
        // Vérifier que le solde n'est pas négatif (pas de crédit)
        if ("SOLDE".equals(moyenPaiement)) {
            String sqlCompte = "SELECT solde FROM COMPTE_CLIENT WHERE client_id = ?";
            System.out.println("Vérification solde pour utilisateur #" + utilisateurId);
            try (PreparedStatement stmt = conn.prepareStatement(sqlCompte)) {
                stmt.setInt(1, utilisateurId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal solde = rs.getBigDecimal("solde");
                        System.out.println("Solde trouvé: " + solde);
                        if (solde.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("❌ Pas de points - solde négatif");
                            result.put("success", false);
                            result.put("error", "Pas de points attribués - solde négatif (crédit utilisé)");
                            return result;
                        }
                    } else {
                        System.out.println("❌ Aucun compte trouvé pour l'utilisateur #" + utilisateurId);
                        result.put("success", false);
                        result.put("error", "Compte client introuvable");
                        return result;
                    }
                }
            }
        }
        
        conn.setAutoCommit(false);
        List<Point> pointsCrees = new ArrayList<>();
        int totalPoints = 0;
        
        double montant = montantTotal.doubleValue();
        System.out.println("Calcul des points pour montant: " + montant);
        int pointsMontant = calculerPointsMontantTotalReservation(conn, montant);
        
        System.out.println("Points calculés pour chambre: " + pointsMontant);
        
        if (pointsMontant > 0) {
            System.out.println("Création du point pour réservation chambre...");
            Point point = creerPointPourReservation(conn,
                utilisateurId,
                reservationId,
                "RES. CHAMBRE", // Type spécifique pour chambre
                null,
                pointsMontant,
                "Points bonus pour réservation de chambre"
            );
            
            if (point != null) {
                System.out.println("Point créé avec ID: " + point.getId());
                pointsCrees.add(point);
                totalPoints += pointsMontant;
                
                // Utiliser la méthode spécifique aux réservations
                System.out.println("Création historique...");
                historiqueManager.historiqueAcquisitionReservation(
                    conn, point, montant, "Réservation chambre", ipAdresse
                );
                
                System.out.println("✅ Point créé pour chambre - ID: " + point.getId() + 
                                 ", Reservation: " + reservationId + 
                                 ", Points: " + pointsMontant);
            } else {
                System.out.println("❌ Échec création du point");
            }
        } else {
            System.out.println("ℹ️ Aucun point à attribuer (montant insuffisant ou règles non remplies)");
            result.put("success", false);
            result.put("error", "Montant insuffisant pour l'attribution de points");
            conn.rollback();
            return result;
        }
        
        if (totalPoints > 0) {
            System.out.println("Mise à jour total points utilisateur...");
            mettreAJourTotalPointsUtilisateur(conn, utilisateurId, totalPoints);
            System.out.println("✅ Total points attribués pour chambre: " + totalPoints);
        }
        
        conn.commit();
        
        result.put("success", true);
        result.put("totalPoints", totalPoints);
        result.put("pointsCrees", pointsCrees);
        result.put("montant", montantTotal);
        
        System.out.println("=== FIN ATTRIBUTION POINTS RÉSERVATION CHAMBRE - SUCCÈS ===");
        
    } catch (SQLException e) {
        System.err.println("❌ ERREUR SQL dans attribuerPointsPourReservationChambre_v2:");
        System.err.println("Message: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        e.printStackTrace();
        
        result.put("success", false);
        result.put("error", "Erreur SQL: " + e.getMessage() + " (Code: " + e.getErrorCode() + ")");
        
    } catch (Exception e) {
        System.err.println("❌ ERREUR GÉNÉRIQUE dans attribuerPointsPourReservationChambre_v2:");
        System.err.println("Message: " + e.getMessage());
        e.printStackTrace();
        
        result.put("success", false);
        result.put("error", "Erreur: " + e.getMessage());
    }
    
    return result;
}

//Méthode utilitaire pour déboguer
private void debugResultMap(Map<String, Object> map, String mapName) {
 System.out.println("=== DEBUG MAP: " + mapName + " ===");
 if (map == null) {
     System.out.println("La map est NULL");
     return;
 }
 
 if (map.isEmpty()) {
     System.out.println("La map est VIDE");
     return;
 }
 
 for (Map.Entry<String, Object> entry : map.entrySet()) {
     System.out.println("Key: '" + entry.getKey() + "'");
     System.out.println("  Type: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "NULL"));
     System.out.println("  Value: " + entry.getValue());
 }
 System.out.println("=== FIN DEBUG ===");
}

// Méthode pour créer l'historique des points de réservation
private void creerHistoriquePointsReservation(Connection conn, Point point, BigDecimal montant, 
                                            String ipAdresse) throws SQLException {
    // Créez un objet PointHistorique avec les bonnes informations
    PointHistorique historique = new PointHistorique();
    historique.setPointId(point.getId());
    historique.setUtilisateurId(point.getUtilisateurId());
    historique.setTypeAction("ACQUISITION");
    historique.setSourceType("RESERVATION_EVENEMENT");
    historique.setSourceRefId(point.getReservationId());
    historique.setTotalPoints(point.getPointsObtenus());
    
    // Récupérer l'ancien total
    int ancienTotal = getTotalPointsUtilisateurDirect(conn, point.getUtilisateurId()) - point.getPointsObtenus();
    historique.setAncienPoint(ancienTotal);
    historique.setNouveauPoint(ancienTotal + point.getPointsObtenus());
    historique.setDifference(point.getPointsObtenus());
    historique.setNotes("Points pour réservation événement #" + point.getReservationId() + 
                       " - Montant: " + String.format("%.2f HTG", montant.doubleValue()));
    historique.setIpAdresse(ipAdresse);
    historique.setUtilisateurModif(point.getUtilisateurId());
    
    // Utiliser la méthode existante de historiqueManager
    historiqueManager.creerHistorique(conn, historique);
}

// Calculer les points pour réservation
private int calculerPointsMontantTotalReservation(Connection conn, double montant) throws SQLException {
    String sql = "SELECT CONDITION_VALEUR, POINTS FROM POINT_CONFIG " +
                 "WHERE TYPE_CONFIG = 'MONTANT_TOTAL' " +
                 "AND STATUT = 'ACTIF' " +
                 "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                 "ORDER BY CONDITION_VALEUR ASC LIMIT 1";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double palier = rs.getDouble("CONDITION_VALEUR");
                int pointsParPalier = rs.getInt("POINTS");
                
                int nbTranches = (int) Math.floor(montant / palier);
                return nbTranches * pointsParPalier;
            }
        }
    }
    return 0;
}

// Créer un point pour réservation
private Point creerPointPourReservation(Connection conn, int utilisateurId, int reservationId,
                                      String sourceType, Integer sourceConfigId, int points,
                                      String notes) throws SQLException {
    String sql = "INSERT INTO POINT (UTILISATEUR_ID, COMMANDE_ID, RESERVATION_ID, SOURCE_TYPE, " +
                 "SOURCE_CONFIG_ID, POINTS_OBTENUS, NOTES, DATE_OBTENTION, DATE_EXPIRATION, STATUT) " +
                 "VALUES (?, NULL, ?, ?, ?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 'VALIDE')";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, utilisateurId);
        stmt.setInt(2, reservationId);
        stmt.setString(3, sourceType);
        
        if (sourceConfigId != null) {
            stmt.setInt(4, sourceConfigId);
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
        
        stmt.setInt(5, points);
        stmt.setString(6, notes);
        
        stmt.executeUpdate();
        
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                int pointId = rs.getInt(1);
                Point point = getPointById(conn, pointId);
                if (point != null) {
                    System.out.println("✅ Point créé - ID: " + pointId + 
                                     ", Reservation: " + reservationId + 
                                     ", Points: " + points);
                    return point;
                }
            }
        }
    }
    
    System.err.println("❌ Échec création point pour réservation #" + reservationId);
    return null;
}

    // Calculer les points pour réservation
//   

    // Créer l'historique des points de réservation

    
    
    // Méthode pour ajuster manuellement les points (admin)
    public Map<String, Object> ajusterPointsManuellement(int utilisateurId, int difference, 
                                                        int adminId, String raison, String ipAdresse) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer l'ancien total
            int ancienTotal = getTotalPointsUtilisateurDirect(conn, utilisateurId);
            int nouveauTotal = ancienTotal + difference;
            
            // Valider que le total ne devienne pas négatif
            if (nouveauTotal < 0) {
                result.put("success", false);
                result.put("error", "Le total des points ne peut pas être négatif. Ancien total: " + ancienTotal + ", Différence demandée: " + difference);
                return result;
            }
            
            // Mettre à jour le total
            String sql = "UPDATE UTILISATEUR SET POINTS_TOTAL = ?, POINT = ?, UPDATE_DATE = NOW() WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, nouveauTotal);
                stmt.setInt(2, nouveauTotal);
                stmt.setInt(3, utilisateurId);
                int rowsUpdated = stmt.executeUpdate();
                
                if (rowsUpdated == 0) {
                    result.put("success", false);
                    result.put("error", "Utilisateur non trouvé avec ID: " + utilisateurId);
                    return result;
                }
            }
            
            // Créer l'historique
            historiqueManager.historiqueAjustementManuel(
                conn, utilisateurId, ancienTotal, nouveauTotal, difference, 
                adminId, raison, ipAdresse
            );
            
            conn.commit();
            
            result.put("success", true);
            result.put("ancienTotal", ancienTotal);
            result.put("nouveauTotal", nouveauTotal);
            result.put("difference", difference);
            result.put("message", "Points ajustés avec succès. " + 
                        (difference > 0 ? "Ajout de " + difference + " points" : 
                         "Retrait de " + Math.abs(difference) + " points"));
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de l'ajustement des points: " + e.getMessage());
        }
        
        return result;
    }
    
    // Méthode pour utiliser des points (réduction, échange)
    public Map<String, Object> utiliserPoints(int utilisateurId, int pointsAUtiliser, 
                                             String typeUtilisation, String reference, 
                                             int adminId, String notes, String ipAdresse) {
        Map<String, Object> result = new HashMap<>();
        
        if (pointsAUtiliser <= 0) {
            result.put("success", false);
            result.put("error", "Le nombre de points à utiliser doit être positif");
            return result;
        }
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer le total actuel
            int totalActuel = getTotalPointsUtilisateurDirect(conn, utilisateurId);
            
            // Vérifier si l'utilisateur a assez de points
            if (totalActuel < pointsAUtiliser) {
                result.put("success", false);
                result.put("error", "Points insuffisants. Disponible: " + totalActuel + ", Requis: " + pointsAUtiliser);
                result.put("soldeActuel", totalActuel);
                return result;
            }
            
            // Mettre à jour le total
            int nouveauTotal = totalActuel - pointsAUtiliser;
            String sql = "UPDATE UTILISATEUR SET POINTS_TOTAL = ?, POINT = ?, UPDATE_DATE = NOW() WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, nouveauTotal);
                stmt.setInt(2, nouveauTotal);
                stmt.setInt(3, utilisateurId);
                stmt.executeUpdate();
            }
            
            // Créer l'historique
            historiqueManager.historiqueUtilisationPoints(
                conn, utilisateurId, pointsAUtiliser, typeUtilisation, 
                reference, adminId, notes, ipAdresse
            );
            
            conn.commit();
            
            result.put("success", true);
            result.put("ancienTotal", totalActuel);
            result.put("nouveauTotal", nouveauTotal);
            result.put("pointsUtilises", pointsAUtiliser);
            result.put("message", pointsAUtiliser + " points utilisés pour " + typeUtilisation + " (" + reference + ")");
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de l'utilisation des points: " + e.getMessage());
        }
        
        return result;
    }
    
    // Méthode pour gérer l'expiration des points
    public Map<String, Object> traiterExpirationPoints() {
        Map<String, Object> result = new HashMap<>();
        List<Integer> pointsExpires = new ArrayList<>();
        int totalPointsExpires = 0;
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer les points expirés
            String sqlSelect = "SELECT p.*, u.LOGIN FROM POINT p " +
                              "JOIN UTILISATEUR u ON p.UTILISATEUR_ID = u.ID " +
                              "WHERE p.DATE_EXPIRATION < NOW() AND p.STATUT = 'VALIDE' " +
                              "ORDER BY p.DATE_EXPIRATION ASC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Point point = mapResultSetToPoint(rs);
                        pointsExpires.add(point.getId());
                        
                        // Marquer le point comme expiré
                        String sqlUpdate = "UPDATE POINT SET STATUT = 'EXPIRE', UPDATED_AT = NOW() WHERE ID = ?";
                        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                            stmtUpdate.setInt(1, point.getId());
                            stmtUpdate.executeUpdate();
                        }
                        
                        // Mettre à jour le total de l'utilisateur
                        mettreAJourTotalPointsUtilisateur(conn, point.getUtilisateurId(), -point.getPointsObtenus());
                        totalPointsExpires += point.getPointsObtenus();
                        
                        // Créer l'historique (IP null pour traitements automatiques)
                        historiqueManager.historiqueExpirationPoints(conn, point, null);
                    }
                }
            }
            
            conn.commit();
            
            result.put("success", true);
            result.put("pointsExpires", pointsExpires.size());
            result.put("totalPointsExpires", totalPointsExpires);
            result.put("message", "Expiration traitée: " + pointsExpires.size() + " points pour " + totalPointsExpires + " unités");
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors du traitement de l'expiration: " + e.getMessage());
        }
        
        return result;
    }
    
    // Méthodes auxiliaires privées
    private PointConfig getConfigForProduit(Connection conn, int produitId) throws SQLException {
        String sql = "SELECT pc.* FROM POINT_CONFIG pc " +
                     "WHERE pc.TYPE_CONFIG = 'PRODUIT' " +
                     "AND pc.REF_ID = ? " +
                     "AND pc.STATUT = 'ACTIF' " +
                     "AND (pc.DATE_FIN IS NULL OR pc.DATE_FIN >= NOW()) " +
                     "ORDER BY pc.CREATED_AT DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
        }
        
        return null;
    }
    
    private PointConfig getConfigForPlat(Connection conn, int platId) throws SQLException {
        // Récupérer la catégorie du plat
        String sql = "SELECT mc.ID as categorie_id FROM PLAT p " +
                     "LEFT JOIN MENU_CATEGORIE mc ON p.CATEGORIE_ID = mc.ID " +
                     "WHERE p.ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, platId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int categorieId = rs.getInt("categorie_id");
                    if (categorieId > 0) {
                        return getConfigForCategorie(conn, categorieId);
                    }
                }
            }
        }
        
        // Si pas de catégorie, chercher une config pour plat spécifique
        String sqlPlat = "SELECT pc.* FROM POINT_CONFIG pc " +
                        "WHERE pc.TYPE_CONFIG = 'PLAT' " +
                        "AND pc.REF_ID = ? " +
                        "AND pc.STATUT = 'ACTIF' " +
                        "AND (pc.DATE_FIN IS NULL OR pc.DATE_FIN >= NOW()) " +
                        "ORDER BY pc.CREATED_AT DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlPlat)) {
            stmt.setInt(1, platId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
        }
        
        return null;
    }
    
    private PointConfig getConfigForCategorie(Connection conn, int categorieId) throws SQLException {
        String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = 'CATEGORIE' " +
                     "AND REF_ID = ? AND STATUT = 'ACTIF' " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY CREATED_AT DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categorieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
        }
        
        return null;
    }
    
    private Point creerPoint(Connection conn, int utilisateurId, Integer commandeId,
                           String sourceType, Integer sourceConfigId, int points,
                           String notes) throws SQLException {
        String sql = "INSERT INTO POINT (UTILISATEUR_ID, COMMANDE_ID, SOURCE_TYPE, " +
                     "SOURCE_CONFIG_ID, POINTS_OBTENUS, NOTES, DATE_OBTENTION, DATE_EXPIRATION, STATUT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 'VALIDE')";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, utilisateurId);
            
            if (commandeId != null) {
                stmt.setInt(2, commandeId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setString(3, sourceType);
            
            if (sourceConfigId != null) {
                stmt.setInt(4, sourceConfigId);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, points);
            stmt.setString(6, notes);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int pointId = rs.getInt(1);
                    return getPointById(conn, pointId);
                }
            }
        }
        
        return null;
    }
    
    private int calculerPointsQteCommandesJournalieres(Connection conn, Commande commande) 
            throws SQLException {
        // Mettre à jour ou créer l'entrée journalière
        mettreAJourCommandeJournaliere(conn, commande);
        
        // Récupérer le nombre de commandes aujourd'hui
        String sql = "SELECT NOMBRE_COMMANDES FROM COMMANDE_JOURNALIERE " +
                     "WHERE UTILISATEUR_ID = ? AND DATE_JOUR = CURDATE()";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commande.getClientId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int nbCommandes = rs.getInt("NOMBRE_COMMANDES");
                    
                    // Vérifier s'il y a une configuration pour ce nombre de commandes
                    String sqlConfig = "SELECT * FROM POINT_CONFIG " +
                                      "WHERE TYPE_CONFIG = 'QTE_COMMANDE_JOURNALIERE' " +
                                      "AND CONDITION_VALEUR <= ? " +
                                      "AND STATUT = 'ACTIF' " +
                                      "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                                      "ORDER BY CONDITION_VALEUR DESC LIMIT 1";
                    
                    try (PreparedStatement stmtConfig = conn.prepareStatement(sqlConfig)) {
                        stmtConfig.setInt(1, nbCommandes);
                        
                        try (ResultSet rsConfig = stmtConfig.executeQuery()) {
                            if (rsConfig.next()) {
                                return rsConfig.getInt("POINTS");
                            }
                        }
                    }
                }
            }
        }
        
        return 0;
    }
    
    private void mettreAJourCommandeJournaliere(Connection conn, Commande commande) 
            throws SQLException {
        String sql = "INSERT INTO COMMANDE_JOURNALIERE (UTILISATEUR_ID, DATE_JOUR, " +
                     "NOMBRE_COMMANDES, MONTANT_TOTAL, DERNIERE_MAJ) " +
                     "VALUES (?, CURDATE(), 1, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "NOMBRE_COMMANDES = NOMBRE_COMMANDES + 1, " +
                     "MONTANT_TOTAL = MONTANT_TOTAL + ?, " +
                     "DERNIERE_MAJ = NOW()";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commande.getClientId());
            stmt.setDouble(2, commande.getMontantTotal().doubleValue());
            stmt.setDouble(3, commande.getMontantTotal().doubleValue());
            
            stmt.executeUpdate();
        }
    }
    
    private int getNombreCommandesJournalieres(Connection conn, int utilisateurId) throws SQLException {
        String sql = "SELECT NOMBRE_COMMANDES FROM COMMANDE_JOURNALIERE " +
                     "WHERE UTILISATEUR_ID = ? AND DATE_JOUR = CURDATE()";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("NOMBRE_COMMANDES");
                }
            }
        }
        
        return 0;
    }
    
    private int calculerPointsMontantTotal(Connection conn, Commande commande) throws SQLException {
        double montant = commande.getMontantTotal().doubleValue();
        
        String sql = "SELECT CONDITION_VALEUR, POINTS FROM POINT_CONFIG " +
                     "WHERE TYPE_CONFIG = 'MONTANT_TOTAL' " +
                     "AND STATUT = 'ACTIF' " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY CONDITION_VALEUR ASC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double palier = rs.getDouble("CONDITION_VALEUR");
                    int pointsParPalier = rs.getInt("POINTS");
                    
                    // Calcul des tranches (arrondi à l'inférieur)
                    int nbTranches = (int) Math.floor(montant / palier);
                    return nbTranches * pointsParPalier;
                }
            }
        }

        return 0;
    }
    
    private void mettreAJourTotalPointsUtilisateur(Connection conn, int utilisateurId, int points) 
            throws SQLException {
        String sql = "UPDATE UTILISATEUR SET POINTS_TOTAL = POINTS_TOTAL + ?, POINT = POINT + ?, UPDATE_DATE = NOW() " +
                     "WHERE ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, points);
            stmt.setInt(3, utilisateurId);
            stmt.executeUpdate();
        }
    }
    
    private Point getPointById(Connection conn, int pointId) throws SQLException {
        String sql = "SELECT * FROM POINT WHERE ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pointId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPoint(rs);
                }
            }
        }
        
        return null;
    }
    
    private int getTotalPointsUtilisateurDirect(Connection conn, int utilisateurId) throws SQLException {
        String sql = "SELECT POINTS_TOTAL FROM UTILISATEUR WHERE ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("POINTS_TOTAL");
                }
            }
        }
        
        return 0;
    }
    
    private PointConfig mapResultSetToPointConfig(ResultSet rs) throws SQLException {
        PointConfig config = new PointConfig();
        config.setId(rs.getInt("ID"));
        config.setTypeConfig(rs.getString("TYPE_CONFIG"));
        config.setRefId(rs.getObject("REF_ID", Integer.class));
        config.setPoints(rs.getInt("POINTS"));
        config.setConditionValeur(rs.getObject("CONDITION_VALEUR", Double.class));
        config.setConditionType(rs.getString("CONDITION_TYPE"));
        config.setDateDebut(rs.getTimestamp("DATE_DEBUT"));
        config.setDateFin(rs.getTimestamp("DATE_FIN"));
        config.setUtilisateurId(rs.getObject("UTILISATEUR_ID", Integer.class));
        config.setStatut(rs.getString("STATUT"));
        config.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        config.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        
        return config;
    }
    
    private Point mapResultSetToPoint(ResultSet rs) throws SQLException {
        Point point = new Point();
        point.setId(rs.getInt("ID"));
        point.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        point.setCommandeId(rs.getObject("COMMANDE_ID", Integer.class));
        
        // CORRECTION : Ajouter le mapping pour RESERVATION_ID
        point.setReservationId(rs.getObject("RESERVATION_ID", Integer.class));
        
        point.setSourceType(rs.getString("SOURCE_TYPE"));
        point.setSourceConfigId(rs.getObject("SOURCE_CONFIG_ID", Integer.class));
        point.setPointsObtenus(rs.getInt("POINTS_OBTENUS"));
        point.setDateObtention(rs.getTimestamp("DATE_OBTENTION"));
        point.setDateExpiration(rs.getTimestamp("DATE_EXPIRATION"));
        point.setStatut(rs.getString("STATUT"));
        point.setNotes(rs.getString("NOTES"));
        point.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        point.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        
        return point;
    }
    
    // === MÉTHODES PUBLIQUES POUR L'HISTORIQUE ===
    
    // Récupérer l'historique complet d'un utilisateur
    public List<PointHistorique> getHistoriqueCompletUtilisateur(int utilisateurId, Date dateDebut, Date dateFin) {
        return historiqueManager.getHistoriqueUtilisateur(utilisateurId, dateDebut, dateFin);
    }
    
    // Méthode pour générer un rapport d'historique
//    public Map<String, Object> genererRapportHistorique(int utilisateurId, Date dateDebut, Date dateFin) {
//        return historiqueManager.genererRapportHistorique(utilisateurId, dateDebut, dateFin);
//    }
    
    // === MÉTHODES PUBLIQUES POUR LA GESTION DES POINTS ===
    
    // Récupérer les points d'un utilisateur
    public List<Point> getPointsParUtilisateur(int utilisateurId, Date dateDebut, Date dateFin) {
        List<Point> points = new ArrayList<>();
        String sql = "SELECT * FROM POINT WHERE UTILISATEUR_ID = ? " +
                     "AND DATE_OBTENTION BETWEEN ? AND ? " +
                     "AND STATUT = 'VALIDE' " +
                     "ORDER BY DATE_OBTENTION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setDate(2, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(3, new java.sql.Date(dateFin.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    points.add(mapResultSetToPoint(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return points;
    }
    
    // Récupérer les points expirant bientôt (dans les 30 jours)
    public List<Point> getPointsExpirantBientot(int utilisateurId) {
        List<Point> points = new ArrayList<>();
        String sql = "SELECT * FROM POINT WHERE UTILISATEUR_ID = ? " +
                     "AND STATUT = 'VALIDE' " +
                     "AND DATE_EXPIRATION BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 30 DAY) " +
                     "ORDER BY DATE_EXPIRATION ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    points.add(mapResultSetToPoint(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return points;
    }
    
    // Récupérer le total des points d'un utilisateur
    public int getTotalPointsUtilisateur(int utilisateurId) {
        String sql = "SELECT POINTS_TOTAL FROM UTILISATEUR WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("POINTS_TOTAL");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Récupérer le solde utilisable (points valides)
    public int getSoldePointsUtilisables(int utilisateurId) {
        String sql = "SELECT SUM(POINTS_OBTENUS) as total_utilisable FROM POINT " +
                     "WHERE UTILISATEUR_ID = ? AND STATUT = 'VALIDE' " +
                     "AND DATE_EXPIRATION > NOW()";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_utilisable");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Annuler des points (par exemple, annulation de commande)
    public Map<String, Object> annulerPoints(int pointId, int adminId, String raison, String ipAdresse) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer le point
            Point point = getPointById(conn, pointId);
            if (point == null || !"VALIDE".equals(point.getStatut())) {
                result.put("success", false);
                result.put("error", "Point non trouvé ou déjà annulé/expiré");
                return result;
            }
            
            // Marquer le point comme annulé
            String sql = "UPDATE POINT SET STATUT = 'ANNULE', NOTES = CONCAT(NOTES, ' | Annulé: ', ?), " +
                         "UPDATED_AT = NOW() WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, raison);
                stmt.setInt(2, pointId);
                stmt.executeUpdate();
            }
            
            // Mettre à jour le total de l'utilisateur
            mettreAJourTotalPointsUtilisateur(conn, point.getUtilisateurId(), -point.getPointsObtenus());
            
            // Créer l'historique
            PointHistorique historique = new PointHistorique();
            historique.setPointId(pointId);
            historique.setUtilisateurId(point.getUtilisateurId());
            historique.setTypeAction("ANNULATION");
            historique.setSourceType("ADMIN");
            historique.setSourceRefId(adminId);
            historique.setTotalPoints(point.getPointsObtenus());
            historique.setAncienPoint(getTotalPointsUtilisateurDirect(conn, point.getUtilisateurId()) + point.getPointsObtenus());
            historique.setNouveauPoint(getTotalPointsUtilisateurDirect(conn, point.getUtilisateurId()));
            historique.setDifference(-point.getPointsObtenus());
            historique.setNotes("Annulation manuelle: " + raison);
            historique.setIpAdresse(ipAdresse);
            historique.setUtilisateurModif(adminId);
            
            historiqueManager.creerHistorique(conn, historique);
            
            conn.commit();
            
            result.put("success", true);
            result.put("pointsAnnules", point.getPointsObtenus());
            result.put("message", point.getPointsObtenus() + " points annulés avec succès");
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de l'annulation: " + e.getMessage());
        }
        
        return result;
    }
    
    // Vérifier l'éligibilité aux bonus
    public Map<String, Object> verifierEligibiliteBonus(int utilisateurId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            // Vérifier les commandes journalières
            String sqlCommandes = "SELECT NOMBRE_COMMANDES FROM COMMANDE_JOURNALIERE " +
                                 "WHERE UTILISATEUR_ID = ? AND DATE_JOUR = CURDATE()";
            
            int nbCommandesAujourdhui = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlCommandes)) {
                stmt.setInt(1, utilisateurId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nbCommandesAujourdhui = rs.getInt("NOMBRE_COMMANDES");
                    }
                }
            }
            
            // Vérifier les configurations disponibles
            List<PointConfig> configsQte = getConfigsDisponibles("QTE_COMMANDE_JOURNALIERE");
            List<PointConfig> configsMontant = getConfigsDisponibles("MONTANT_TOTAL");
            
            result.put("success", true);
            result.put("commandesAujourdhui", nbCommandesAujourdhui);
            result.put("configurationsQte", configsQte.size());
            result.put("configurationsMontant", configsMontant.size());
            result.put("eligibleQte", !configsQte.isEmpty());
            result.put("eligibleMontant", !configsMontant.isEmpty());
            result.put("message", "Vérification d'éligibilité terminée");
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de la vérification: " + e.getMessage());
        }
        
        return result;
    }
    
    // Méthode pour récupérer les configurations disponibles
    private List<PointConfig> getConfigsDisponibles(String typeConfig) {
        List<PointConfig> configs = new ArrayList<>();
        String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = ? " +
                     "AND STATUT = 'ACTIF' AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY CREATED_AT DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeConfig);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    configs.add(mapResultSetToPointConfig(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return configs;
    }
    
    public boolean mettreAJourTotalPointsUtilisateur(int userId, int difference) {
        String sql = "UPDATE UTILISATEUR SET " +
                     "POINTS_TOTAL = POINTS_TOTAL + ?, " +
                     "POINT = POINT + ?, " +
                     "UPDATE_DATE = NOW() " +
                     "WHERE ID = ?";
        
        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, difference);
                stmt.setInt(2, difference);
                stmt.setInt(3, userId);
                
                int rowsUpdated = stmt.executeUpdate();
                System.out.println("Total points utilisateur mis à jour: " + rowsUpdated + " ligne(s)");
                
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du total des points utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
   public boolean marquerPointsCommeUtilises(int userId, int pointsAUtiliser, int transactionId) {
    System.out.println("=== DÉBUT marquerPointsCommeUtilises ===");
    System.out.println("User ID: " + userId);
    System.out.println("Points à utiliser: " + pointsAUtiliser);
    System.out.println("Transaction ID: " + transactionId);
    
    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);
        
        // 1. Récupérer les points valides de l'utilisateur (FIFO)
        List<Map<String, Object>> pointsDisponibles = new ArrayList<>();
        String sqlSelect = "SELECT ID, POINTS_OBTENUS, NOTES FROM POINT " +
                          "WHERE UTILISATEUR_ID = ? AND STATUT = 'VALIDE' AND DATE_EXPIRATION > NOW() " +
                          "ORDER BY DATE_OBTENTION ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> point = new HashMap<>();
                    point.put("id", rs.getInt("ID"));
                    point.put("points", rs.getInt("POINTS_OBTENUS"));
                    point.put("notes", rs.getString("NOTES"));
                    pointsDisponibles.add(point);
                }
            }
        }
        
        System.out.println("Points disponibles (lignes): " + pointsDisponibles.size());
        int pointsRestants = pointsAUtiliser;
        
        // 2. Parcourir les points et les ajuster selon la quantité nécessaire
        for (Map<String, Object> point : pointsDisponibles) {
            int pointId = (int) point.get("id");
            int pointsDansLigne = (int) point.get("points");
            String notes = (String) point.get("notes");
            
            if (pointsRestants <= 0) break;
            
            System.out.println("Traitement ligne ID " + pointId + " avec " + pointsDansLigne + " points");
            
            if (pointsDansLigne <= pointsRestants) {
                // Cas 1: On utilise toute cette ligne
                System.out.println("✓ Utilisation complète de la ligne " + pointId + 
                                 " (" + pointsDansLigne + " points)");
                
                // Marquer la ligne comme utilisée
                String sqlUpdate = "UPDATE POINT SET STATUT = 'UTILISE', " +
                                  "NOTES = CONCAT(COALESCE(NOTES, ''), ' | Converti via transaction #', ?), " +
                                  "UPDATED_AT = NOW() " +
                                  "WHERE ID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                    stmt.setInt(1, transactionId);
                    stmt.setInt(2, pointId);
                    stmt.executeUpdate();
                }
                
                pointsRestants -= pointsDansLigne;
                
            } else {
                // Cas 2: On doit splitter la ligne (pointsDansLigne > pointsRestants)
                System.out.println("✓ Split de la ligne " + pointId + 
                                 ": " + pointsRestants + "/" + pointsDansLigne + " points");
                
                // 2a. Créer une NOUVELLE ligne pour les points utilisés
                String sqlInsert = "INSERT INTO POINT (UTILISATEUR_ID, COMMANDE_ID, RESERVATION_ID, " +
                                  "SOURCE_TYPE, SOURCE_CONFIG_ID, POINTS_OBTENUS, NOTES, " +
                                  "DATE_OBTENTION, DATE_EXPIRATION, STATUT) " +
                                  "SELECT UTILISATEUR_ID, COMMANDE_ID, RESERVATION_ID, " +
                                  "SOURCE_TYPE, SOURCE_CONFIG_ID, ?, " +
                                  "CONCAT(NOTES, ' | Partiellement converti: ', ?, ' points via transaction #', ?), " +
                                  "DATE_OBTENTION, DATE_EXPIRATION, 'UTILISE' " +
                                  "FROM POINT WHERE ID = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                    stmt.setInt(1, pointsRestants); // Points utilisés
                    stmt.setInt(2, pointsRestants); // Pour les notes
                    stmt.setInt(3, transactionId);
                    stmt.setInt(4, pointId);
                    stmt.executeUpdate();
                }
                
                // 2b. Mettre à jour la ligne originale avec les points restants
                int pointsRestantsDansLigne = pointsDansLigne - pointsRestants;
                String sqlUpdate = "UPDATE POINT SET POINTS_OBTENUS = ?, " +
                                  "NOTES = CONCAT(COALESCE(NOTES, ''), ' | Points restants: ', ?, ' après conversion transaction #', ?), " +
                                  "UPDATED_AT = NOW() " +
                                  "WHERE ID = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                    stmt.setInt(1, pointsRestantsDansLigne);
                    stmt.setInt(2, pointsRestantsDansLigne);
                    stmt.setInt(3, transactionId);
                    stmt.setInt(4, pointId);
                    stmt.executeUpdate();
                }
                
                pointsRestants = 0; // Tous les points nécessaires ont été utilisés
            }
            
            System.out.println("Points restants à utiliser: " + pointsRestants);
        }
        
        if (pointsRestants > 0) {
            System.err.println("ERREUR: Points insuffisants après traitement. Restants: " + pointsRestants);
            conn.rollback();
            return false;
        }
        
        conn.commit();
        System.out.println("=== FIN marquerPointsCommeUtilises - SUCCÈS ===");
        return true;
        
    } catch (SQLException e) {
        System.err.println("ERREUR SQL lors du marquage des points comme utilisés: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}