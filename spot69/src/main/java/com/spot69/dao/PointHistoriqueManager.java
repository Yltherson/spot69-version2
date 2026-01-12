package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PointHistoriqueManager {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // Méthode principale pour créer une entrée d'historique
    public void creerHistorique(Connection conn, PointHistorique historique) throws SQLException {
        String sql = "INSERT INTO POINT_HISTORIQUE (" +
                     "POINT_ID, COMMANDE_ID, UTILISATEUR_ID, RESERVATION_ID, " +
                     "TYPE_ACTION, SOURCE_TYPE, SOURCE_REF_ID, " +
                     "QUANTITE, POINTS_PAR_UNITE, TOTAL_POINTS, " +
                     "ANCIEN_POINT, NOUVEAU_POINT, DIFFERENCE, " +
                     "NOTES, IP_ADRESSE, UTILISATEUR_MODIF, CREATED_AT" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            
            // POINT_ID
            if (historique.getPointId() != null) {
                stmt.setInt(index++, historique.getPointId());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // COMMANDE_ID
            if (historique.getCommandeId() != null) {
                stmt.setInt(index++, historique.getCommandeId());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // UTILISATEUR_ID (obligatoire)
            stmt.setInt(index++, historique.getUtilisateurId());
            
            // RESERVATION_ID
            if (historique.getReservationId() != null) {
                stmt.setInt(index++, historique.getReservationId());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // TYPE_ACTION (obligatoire)
            stmt.setString(index++, historique.getTypeAction());
            
            // SOURCE_TYPE
            if (historique.getSourceType() != null) {
                stmt.setString(index++, historique.getSourceType());
            } else {
                stmt.setNull(index++, Types.VARCHAR);
            }
            
            // SOURCE_REF_ID
            if (historique.getSourceRefId() != null) {
                stmt.setInt(index++, historique.getSourceRefId());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // QUANTITE
            if (historique.getQuantite() != null) {
                stmt.setInt(index++, historique.getQuantite());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // POINTS_PAR_UNITE
            if (historique.getPointsParUnite() != null) {
                stmt.setInt(index++, historique.getPointsParUnite());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // TOTAL_POINTS
            if (historique.getTotalPoints() != null) {
                stmt.setInt(index++, historique.getTotalPoints());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // ANCIEN_POINT
            if (historique.getAncienPoint() != null) {
                stmt.setInt(index++, historique.getAncienPoint());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // NOUVEAU_POINT
            if (historique.getNouveauPoint() != null) {
                stmt.setInt(index++, historique.getNouveauPoint());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // DIFFERENCE
            if (historique.getDifference() != null) {
                stmt.setInt(index++, historique.getDifference());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            // NOTES
            if (historique.getNotes() != null && !historique.getNotes().trim().isEmpty()) {
                stmt.setString(index++, historique.getNotes());
            } else {
                stmt.setNull(index++, Types.VARCHAR);
            }
            
            // IP_ADRESSE
            if (historique.getIpAdresse() != null && !historique.getIpAdresse().trim().isEmpty()) {
                stmt.setString(index++, historique.getIpAdresse());
            } else {
                stmt.setNull(index++, Types.VARCHAR);
            }
            
            // UTILISATEUR_MODIF
            if (historique.getUtilisateurModif() != null) {
                stmt.setInt(index++, historique.getUtilisateurModif());
            } else {
                stmt.setNull(index++, Types.INTEGER);
            }
            
            stmt.executeUpdate();
        }
    }
    
 // Historique pour bonus montant total (version pour réservations)
    // Historique pour bonus montant des réservations
 // Historique pour bonus montant des réservations
 // Historique pour bonus montant des réservations
    public void historiqueBonusMontantReservation(Connection conn, Point point, double montantReservation, 
                                                String ipAdresse) throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal + point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setReservationId(point.getReservationId()); // ID de réservation
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("BONUS_MONTANT");
        historique.setSourceType("RESERVATION_EVENEMENT");
        historique.setSourceRefId(point.getReservationId());
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(point.getPointsObtenus());
        historique.setNotes(String.format("Bonus pour réservation événement de %.2f HTG", montantReservation));
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(point.getUtilisateurId());
        
        creerHistorique(conn, historique);
        
        System.out.println("Historique points créé pour réservation #" + point.getReservationId() + 
                           " - " + point.getPointsObtenus() + " points");
    }
    
 // Ajoutez cette méthode à PointHistoriqueManager
    public void historiqueAcquisitionReservation(Connection conn, Point point, double montantReservation, 
                                               String notes, String ipAdresse) throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal + point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setReservationId(point.getReservationId()); // IMPORTANT: pour réservations
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("ACQUISITION");
        historique.setSourceType("RESERVATION_EVENEMENT");
        historique.setSourceRefId(point.getReservationId());
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(point.getPointsObtenus());
        historique.setNotes("Points pour réservation " + 
                           " - Montant: " + String.format("%.0f HTG", montantReservation) +
                           (notes != null ? " - " + notes : ""));
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(point.getUtilisateurId());
        
        creerHistorique(conn, historique);
        
        System.out.println("✅ Historique créé pour réservation #" + point.getReservationId() + 
                          " - Points: " + point.getPointsObtenus());
    }
    
 // Dans PointHistoriqueManager
    public List<PointHistorique> getHistoriqueReservationsUtilisateur(int utilisateurId, 
                                                                     Date dateDebut, 
                                                                     Date dateFin) {
        List<PointHistorique> historiqueList = new ArrayList<>();
        String sql = "SELECT * FROM POINT_HISTORIQUE " +
                     "WHERE UTILISATEUR_ID = ? " +
                     "AND (SOURCE_TYPE = 'RESERVATION_EVENEMENT' OR RESERVATION_ID IS NOT NULL) " +
                     "AND CREATED_AT BETWEEN ? AND ? " +
                     "ORDER BY CREATED_AT DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setTimestamp(2, new Timestamp(dateDebut.getTime()));
            stmt.setTimestamp(3, new Timestamp(dateFin.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiqueList.add(mapResultSetToPointHistorique(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return historiqueList;
    }

    // Méthode pour récupérer le total des points d'un utilisateur
//    private int getTotalPointsUtilisateur(Connection conn, int utilisateurId) throws SQLException {
//        String sql = "SELECT POINTS_TOTAL FROM UTILISATEUR WHERE ID = ?";
//        
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, utilisateurId);
//            
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("POINTS_TOTAL");
//                }
//            }
//        }
//        
//        return 0;
//    }

    // Méthode utilitaire pour récupérer le total des points d'un utilisateur (utilisée par _v2)
    public int getTotalPointsUtilisateurDirect(Connection conn, int utilisateurId) throws SQLException {
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
    
    // Historique pour acquisition de points via commande
    public void historiqueAcquisitionCommande(Connection conn, Point point, CommandeDetail detail, 
                                             String notes, String ipAdresse) throws SQLException {
        // Récupérer l'ancien total de points
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal + point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setCommandeId(point.getCommandeId());
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("ACQUISITION");
        historique.setSourceType(point.getSourceType());
        historique.setSourceRefId(detail.getProduitId() != null ? detail.getProduitId() : detail.getPlatId());
        historique.setQuantite(detail.getQuantite());
        historique.setPointsParUnite(point.getPointsObtenus() / detail.getQuantite());
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(point.getPointsObtenus());
        historique.setNotes(genererNotesAcquisition(point, detail, notes));
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(point.getUtilisateurId()); // L'utilisateur lui-même
        
        creerHistorique(conn, historique);
    }
    
    // Historique pour bonus de commandes journalières
    public void historiqueBonusQteCommandes(Connection conn, Point point, int nbCommandes, 
                                           String ipAdresse) throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal + point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setCommandeId(point.getCommandeId());
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("BONUS_QTE_COMMANDES");
        historique.setSourceType(point.getSourceType());
        historique.setSourceRefId(nbCommandes);
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(point.getPointsObtenus());
        historique.setNotes("Bonus pour " + nbCommandes + " commandes effectuées aujourd'hui");
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(point.getUtilisateurId());
        
        creerHistorique(conn, historique);
    }
    
    // Historique pour bonus montant total
    public void historiqueBonusMontant(Connection conn, Point point, double montantCommande, 
                                      String ipAdresse) throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal + point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setCommandeId(point.getCommandeId());
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("BONUS_MONTANT");
        historique.setSourceType(point.getSourceType());
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(point.getPointsObtenus());
        historique.setNotes(String.format("Bonus pour commande de %.2f€", montantCommande));
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(point.getUtilisateurId());
        
        creerHistorique(conn, historique);
    }
    
    // Historique pour ajustement manuel des points (par admin)
    public void historiqueAjustementManuel(Connection conn, int utilisateurId, int ancienTotal, 
                                          int nouveauTotal, int difference, int adminId, 
                                          String raison, String ipAdresse) throws SQLException {
        PointHistorique historique = new PointHistorique();
        historique.setUtilisateurId(utilisateurId);
        historique.setTypeAction("AJUSTEMENT_MANUEL");
        historique.setSourceType("ADMIN");
        historique.setSourceRefId(adminId);
        historique.setTotalPoints(Math.abs(difference));
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(difference);
        historique.setNotes("Ajustement manuel: " + raison);
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(adminId);
        
        creerHistorique(conn, historique);
    }
    
    // Historique pour expiration de points
    public void historiqueExpirationPoints(Connection conn, Point point, String ipAdresse) 
            throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, point.getUtilisateurId());
        int nouveauTotal = ancienTotal - point.getPointsObtenus();
        
        PointHistorique historique = new PointHistorique();
        historique.setPointId(point.getId());
        historique.setUtilisateurId(point.getUtilisateurId());
        historique.setTypeAction("EXPIRATION");
        historique.setSourceType("SYSTEME");
        historique.setTotalPoints(point.getPointsObtenus());
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(-point.getPointsObtenus());
        historique.setNotes("Points expirés (date: " + point.getDateExpiration() + ")");
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(null); // Système
        
        creerHistorique(conn, historique);
    }
    
    // Historique pour utilisation de points (réduction, échange)
    public void historiqueUtilisationPoints(Connection conn, int utilisateurId, int pointsUtilises, 
                                           String typeUtilisation, String reference, 
                                           int adminId, String notes, String ipAdresse) 
            throws SQLException {
        int ancienTotal = getTotalPointsUtilisateur(conn, utilisateurId);
        int nouveauTotal = ancienTotal - pointsUtilises;
        
        PointHistorique historique = new PointHistorique();
        historique.setUtilisateurId(utilisateurId);
        historique.setTypeAction("UTILISATION");
        historique.setSourceType(typeUtilisation); // REDUCTION, ECHANGE, CADEAU, etc.
        historique.setSourceRefId(null); // Pourrait être l'ID de la réduction ou du bon
        historique.setTotalPoints(pointsUtilises);
        historique.setAncienPoint(ancienTotal);
        historique.setNouveauPoint(nouveauTotal);
        historique.setDifference(-pointsUtilises);
        historique.setNotes("Utilisation pour " + reference + ". " + notes);
        historique.setIpAdresse(ipAdresse);
        historique.setUtilisateurModif(adminId);
        
        creerHistorique(conn, historique);
    }
    
    // Récupérer l'historique d'un utilisateur
  public List<PointHistorique> getHistoriqueUtilisateur(int utilisateurId, Date dateDebut, Date dateFin) {
    List<PointHistorique> historiqueList = new ArrayList<>();
    
    // CORRECTION : Utiliser LocalDateTime pour éviter les problèmes de fuseau horaire
    String sql = "SELECT * FROM POINT_HISTORIQUE " +
                 "WHERE UTILISATEUR_ID = ? " +
                 "AND DATE(CREATED_AT) BETWEEN DATE(?) AND DATE(?) " + // DATE() pour ignorer l'heure
                 "ORDER BY CREATED_AT DESC";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, utilisateurId);
        stmt.setDate(2, dateDebut);
        stmt.setDate(3, dateFin);
        
        System.out.println("SQL Debug: utilisateurId=" + utilisateurId + 
                         ", dateDebut=" + dateDebut + 
                         ", dateFin=" + dateFin);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                historiqueList.add(mapResultSetToPointHistorique(rs));
            }
        }
        
        System.out.println("Nombre d'historiques trouvés: " + historiqueList.size());
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return historiqueList;
}
    
    // Méthode pour générer des notes détaillées
    private String genererNotesAcquisition(Point point, CommandeDetail detail, String notesAdditionnelles) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Acquisition de ").append(point.getPointsObtenus()).append(" points");
        
        if (detail.getProduitId() != null) {
            sb.append(" pour produit ID ").append(detail.getProduitId());
        } else if (detail.getPlatId() != null) {
            sb.append(" pour plat ID ").append(detail.getPlatId());
        }
        
        sb.append(" (x").append(detail.getQuantite()).append(")");
        
        if (point.getSourceType() != null) {
            sb.append(" - Source: ").append(point.getSourceType());
        }
        
        if (notesAdditionnelles != null && !notesAdditionnelles.trim().isEmpty()) {
            sb.append(" - ").append(notesAdditionnelles);
        }
        
        return sb.toString();
    }
    
    // Méthode privée pour récupérer le total des points d'un utilisateur
    private int getTotalPointsUtilisateur(Connection conn, int utilisateurId) throws SQLException {
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
    
    // Mapper ResultSet à PointHistorique
    private PointHistorique mapResultSetToPointHistorique(ResultSet rs) throws SQLException {
        PointHistorique historique = new PointHistorique();
        historique.setId(rs.getInt("ID"));
        historique.setPointId(rs.getObject("POINT_ID", Integer.class));
        historique.setCommandeId(rs.getObject("COMMANDE_ID", Integer.class));
        historique.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        historique.setReservationId(rs.getObject("RESERVATION_ID", Integer.class));
        historique.setTypeAction(rs.getString("TYPE_ACTION"));
        historique.setSourceType(rs.getString("SOURCE_TYPE"));
        historique.setSourceRefId(rs.getObject("SOURCE_REF_ID", Integer.class));
        historique.setQuantite(rs.getObject("QUANTITE", Integer.class));
        historique.setPointsParUnite(rs.getObject("POINTS_PAR_UNITE", Integer.class));
        historique.setTotalPoints(rs.getObject("TOTAL_POINTS", Integer.class));
        historique.setAncienPoint(rs.getObject("ANCIEN_POINT", Integer.class));
        historique.setNouveauPoint(rs.getObject("NOUVEAU_POINT", Integer.class));
        historique.setDifference(rs.getObject("DIFFERENCE", Integer.class));
        historique.setNotes(rs.getString("NOTES"));
        historique.setIpAdresse(rs.getString("IP_ADRESSE"));
        historique.setUtilisateurModif(rs.getObject("UTILISATEUR_MODIF", Integer.class));
        historique.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        
        return historique;
    }
    
 // Méthode pour enregistrer l'utilisation/consommation de points
    public boolean enregistrerUtilisationPoints(
            int utilisateurId, 
            int pointsUtilises, 
            double valeurObtenue, 
            String typeUtilisation, 
            Integer commandeId, 
            Integer produitId, 
            Integer platId, 
            Integer quantite) {
        
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Commencer une transaction
            
            // 1. Récupérer l'ancien total de points de l'utilisateur
            int ancienTotal = getTotalPointsUtilisateur(conn, utilisateurId);
            int nouveauTotal = ancienTotal - pointsUtilises;
            
            if (nouveauTotal < 0) {
                throw new SQLException("Le solde des points deviendrait négatif");
            }
            
            // 2. Créer l'entrée d'historique
            PointHistorique historique = new PointHistorique();
            historique.setUtilisateurId(utilisateurId);
            
            // Déterminer le type d'action
            if (typeUtilisation != null) {
                if (typeUtilisation.contains("Conversion") || typeUtilisation.contains("convertir")) {
                    historique.setTypeAction("CONVERSION_POINTS");
                    historique.setSourceType("CONVERSION_ARGENT");
                } else if (typeUtilisation.contains("Réduction") || typeUtilisation.contains("réduction")) {
                    historique.setTypeAction("UTILISATION_REDUCTION");
                    historique.setSourceType("REDUCTION_COMMANDE");
                } else if (typeUtilisation.contains("Échange") || typeUtilisation.contains("échange")) {
                    historique.setTypeAction("UTILISATION_ECHANGE");
                    historique.setSourceType("ECHANGE_CADEAU");
                } else {
                    historique.setTypeAction("UTILISATION");
                    historique.setSourceType(typeUtilisation);
                }
            } else {
                historique.setTypeAction("UTILISATION");
                historique.setSourceType("SYSTEME");
            }
            
            // Définir les références
            if (commandeId != null) {
                historique.setCommandeId(commandeId);
                historique.setSourceRefId(commandeId);
            } else if (produitId != null) {
                historique.setSourceRefId(produitId);
            } else if (platId != null) {
                historique.setSourceRefId(platId);
            }
            
            // Définir les quantités et points
            historique.setQuantite(quantite != null ? quantite : 1);
            historique.setTotalPoints(pointsUtilises);
            historique.setAncienPoint(ancienTotal);
            historique.setNouveauPoint(nouveauTotal);
            historique.setDifference(-pointsUtilises); // Négatif car utilisation
            
            // Générer les notes
            StringBuilder notesBuilder = new StringBuilder();
            notesBuilder.append("Utilisation de ").append(pointsUtilises).append(" points");
            
            if (valeurObtenue > 0) {
                notesBuilder.append(" pour une valeur de ")
                           .append(String.format("%.2f", valeurObtenue))
                           .append(" HTG");
            }
            
            if (typeUtilisation != null && !typeUtilisation.isEmpty()) {
                notesBuilder.append(" (").append(typeUtilisation).append(")");
            }
            
            if (commandeId != null) {
                notesBuilder.append(" - Commande #").append(commandeId);
            }
            
            if (produitId != null) {
                notesBuilder.append(" - Produit #").append(produitId);
            }
            
            if (platId != null) {
                notesBuilder.append(" - Plat #").append(platId);
            }
            
            historique.setNotes(notesBuilder.toString());
            
            // Adresse IP (pourrait être passée en paramètre si nécessaire)
            historique.setIpAdresse("127.0.0.1"); // Valeur par défaut, à adapter
            historique.setUtilisateurModif(utilisateurId); // L'utilisateur lui-même
            
            // 3. Enregistrer dans l'historique
            creerHistorique(conn, historique);
            
            // 4. Mettre à jour le total des points de l'utilisateur
            updateTotalPointsUtilisateur(conn, utilisateurId, nouveauTotal);
            

            PointManagerDAO pointManagerDAO = new PointManagerDAO();
            // 5. Marquer les points spécifiques comme utilisés (si nécessaire)
//            pointManagerDAO.marquerPointsUtilises(utilisateurId, -pointsUtilises);
            
            conn.commit(); // Valider la transaction
            success = true;
            
            System.out.println("✅ Historique d'utilisation enregistré - " + 
                              utilisateurId + " a utilisé " + pointsUtilises + " points");
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }

    // Méthode pour mettre à jour le total des points d'un utilisateur
    private void updateTotalPointsUtilisateur(Connection conn, int utilisateurId, int nouveauTotal) 
            throws SQLException {
        
    	String sql = "UPDATE UTILISATEUR SET " +
                "POINTS_TOTAL =  ?, " +
                "POINT = ?, " +
                "UPDATE_DATE = NOW() " +
                "WHERE ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nouveauTotal);
            stmt.setInt(2, nouveauTotal);
            stmt.setInt(3, utilisateurId);
            stmt.executeUpdate();
        }
    }

    // Méthode pour marquer les points spécifiques comme utilisés
//    private void marquerPointsUtilises(Connection conn, int utilisateurId, int pointsAUtiliser) 
//            throws SQLException {
//        
//        // Cette méthode marque les points les plus anciens comme utilisés (FIFO)
//        String sqlSelect = "SELECT ID, POINTS_OBTENUS, POINTS_UTILISES FROM POINT " +
//                          "WHERE UTILISATEUR_ID = ? AND STATUS = 'ACTIF' " +
//                          "AND DATE_EXPIRATION > NOW() " +
//                          "AND POINTS_OBTENUS > COALESCE(POINTS_UTILISES, 0) " +
//                          "ORDER BY DATE_ACQUISITION ASC";
//        
//        String sqlUpdate = "UPDATE POINT SET POINTS_UTILISES = ?, STATUS = ? WHERE ID = ?";
//        
//        try (PreparedStatement selectStmt = conn.prepareStatement(sqlSelect);
//             PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
//            
//            selectStmt.setInt(1, utilisateurId);
//            ResultSet rs = selectStmt.executeQuery();
//            
//            int pointsRestants = pointsAUtiliser;
//            
//            while (rs.next() && pointsRestants > 0) {
//                int pointId = rs.getInt("ID");
//                int pointsObtenus = rs.getInt("POINTS_OBTENUS");
//                int pointsUtilises = rs.getInt("POINTS_UTILISES");
//                
//                int pointsDisponibles = pointsObtenus - pointsUtilises;
//                int pointsAUtiliserCeLot = Math.min(pointsRestants, pointsDisponibles);
//                
//                int nouveauxPointsUtilises = pointsUtilises + pointsAUtiliserCeLot;
//                String nouveauStatus = (nouveauxPointsUtilises >= pointsObtenus) ? "UTILISE" : "PARTIEL";
//                
//                updateStmt.setInt(1, nouveauxPointsUtilises);
//                updateStmt.setString(2, nouveauStatus);
//                updateStmt.setInt(3, pointId);
//                updateStmt.executeUpdate();
//                
//                pointsRestants -= pointsAUtiliserCeLot;
//                
//                System.out.println("Point #" + pointId + ": " + pointsAUtiliserCeLot + 
//                                 " points marqués comme utilisés");
//            }
//            
//            if (pointsRestants > 0) {
//                System.out.println("⚠️ Attention: " + pointsRestants + 
//                                 " points n'ont pas pu être spécifiquement attribués");
//            }
//        }
//    }

    // Méthode surchargée pour une utilisation simplifiée (spécialement pour la conversion)
    public boolean enregistrerUtilisationPoints(
            int utilisateurId, 
            int pointsUtilises, 
            double valeurObtenue, 
            String typeUtilisation) {
        
        return enregistrerUtilisationPoints(
            utilisateurId, 
            pointsUtilises, 
            valeurObtenue, 
            typeUtilisation, 
            null, null, null, null
        );
    }

    // Méthode pour enregistrer spécifiquement une conversion de points
    public boolean enregistrerConversionPoints(
            int utilisateurId, 
            int pointsUtilises, 
            double valeurObtenue, 
            int transactionCompteId) {
        
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
//            int ancienTotal = getTotalPointsUtilisateur(conn, utilisateurId);
//            int nouveauTotal = ancienTotal - pointsUtilises;
//            
//            if (nouveauTotal < 0) {
//                throw new SQLException("Solde de points insuffisant");
//            }
//            
//            PointHistorique historique = new PointHistorique();
//            historique.setUtilisateurId(utilisateurId);
//            historique.setTypeAction("CONVERSION_POINTS");
//            historique.setSourceType("CONVERSION_ARGENT");
//            historique.setSourceRefId(transactionCompteId);
//            historique.setTotalPoints(pointsUtilises);
//            historique.setAncienPoint(ancienTotal);
//            historique.setNouveauPoint(nouveauTotal);
//            historique.setDifference(-pointsUtilises);
            PointHistorique historique = new PointHistorique();
            historique.setUtilisateurId(utilisateurId);
            historique.setTypeAction("CONVERSION_POINTS");
            historique.setSourceType("CONVERSION_ARGENT");
            historique.setSourceRefId(transactionCompteId);
            historique.setTotalPoints(pointsUtilises);
            
            // Récupérer le total actuel après conversion
            int nouveauTotal = getTotalPointsUtilisateur(conn, utilisateurId);
            int ancienTotal = nouveauTotal + pointsUtilises; // Calculer l'ancien total
            
            historique.setAncienPoint(ancienTotal);
            historique.setNouveauPoint(nouveauTotal);
            historique.setDifference(-pointsUtilises);
            
            String notes = String.format(
                "Conversion de %d points en %.2f HTG (Transaction #%d)",
                pointsUtilises, valeurObtenue, transactionCompteId
            );
            historique.setNotes(notes);
            historique.setIpAdresse("127.0.0.1");
            historique.setUtilisateurModif(utilisateurId);
            
            creerHistorique(conn, historique);
//            updateTotalPointsUtilisateur(conn, utilisateurId, nouveauTotal);
            
//            marquerPointsUtilises(conn, utilisateurId, pointsUtilises);
            
            conn.commit();
            success = true;
            
            System.out.println("✅ Conversion enregistrée: " + pointsUtilises + 
                              " points -> " + String.format("%.2f", valeurObtenue) + " HTG");
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                }
            }
        }
        
        return success;
    }
}