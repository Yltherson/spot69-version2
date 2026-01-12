package com.spot69.dao;

import com.spot69.model.Point;
import com.spot69.model.PointHistorique;
import com.spot69.model.PrivilegeNiveau;
import com.spot69.model.Produit;
import com.spot69.model.Commande;
import com.spot69.model.CommandeDetail;
import com.spot69.model.Plat;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PointDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }


 
        private final ProduitDAO produitDAO = new ProduitDAO();
        private final PlatDAO platDAO = new PlatDAO();
        private final PrivilegeNiveauDAO privilegeNiveauDAO = new PrivilegeNiveauDAO();

        /**
         * Crée un enregistrement de points pour une commande et son utilisateur
         * et insère les historiques.
         */
        public int insererPointsPourCommande(int utilisateurId, int commandeId,
                                             List<CommandeDetail> details) {
            String sqlPoint = "INSERT INTO POINT (UTILISATEUR_ID, COMMANDE_ID, POINTS_OBTENUS, CREATED_AT, UPDATED_AT, STATUS) " +
                              "VALUES (?, ?, ?, ?, ?, 'VISIBLE')";
            String sqlHistorique = "INSERT INTO POINT_HISTORIQUE (POINT_ID, COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, OLD_QTE_POINTS, CREATED_AT, UPDATED_AT, STATUS) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE')";

            Timestamp now = new Timestamp(new Date().getTime());

            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);

                // 1️⃣ Calculer total points
                int totalPoints = 0;
                for (CommandeDetail d : details) {
                    int pointsUnitaire = 0;
                    if (d.getProduitId() != null) {
                        Produit produit = produitDAO.chercherParId(d.getProduitId());
                        if (produit != null) pointsUnitaire = produit.getQtePoints();
                    } else if (d.getPlatId() != null) {
                        Plat plat = platDAO.chercherParId(d.getPlatId());
                        if (plat != null) pointsUnitaire = plat.getQtePoints();
                    }
                    totalPoints += pointsUnitaire * d.getQuantite();
                }

                // 2️⃣ Insertion dans POINT
                int pointId;
                try (PreparedStatement stmt = conn.prepareStatement(sqlPoint, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, utilisateurId);
                    stmt.setInt(2, commandeId);
                    stmt.setInt(3, totalPoints);
                    stmt.setTimestamp(4, now);
                    stmt.setTimestamp(5, now);
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) pointId = rs.getInt(1);
                        else throw new SQLException("Impossible de récupérer l'ID du point");
                    }
                }

                // 3️⃣ Insertion des historiques
                try (PreparedStatement stmtHist = conn.prepareStatement(sqlHistorique)) {
                    for (CommandeDetail d : details) {
                        int pointsUnitaire = 0;
                        if (d.getProduitId() != null) {
                            Produit produit = produitDAO.chercherParId(d.getProduitId());
                            if (produit != null) pointsUnitaire = produit.getQtePoints();
                        } else if (d.getPlatId() != null) {
                            Plat plat = platDAO.chercherParId(d.getPlatId());
                            if (plat != null) pointsUnitaire = plat.getQtePoints();
                        }

                        stmtHist.setInt(1, pointId);
                        stmtHist.setInt(2, commandeId);

                        if (d.getProduitId() != null) stmtHist.setInt(3, d.getProduitId());
                        else stmtHist.setNull(3, Types.INTEGER);

                        if (d.getPlatId() != null) stmtHist.setInt(4, d.getPlatId());
                        else stmtHist.setNull(4, Types.INTEGER);

                        stmtHist.setInt(5, d.getQuantite());
                        stmtHist.setInt(6, pointsUnitaire);
                        stmtHist.setTimestamp(7, now);
                        stmtHist.setTimestamp(8, now);

                        stmtHist.addBatch();
                    }
                    stmtHist.executeBatch();
                }

                conn.commit();
                return totalPoints; // ou pointId selon ton besoin

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        
        /**
         * Récupère la liste des points pour un utilisateur avec filtre de date
         */
//        public List<Point> getPointsParUtilisateurAvecCommande(int utilisateurId, Timestamp dateDebut, Timestamp dateFin) {
//            List<Point> points = new ArrayList<>();
//            String sql = "SELECT * FROM POINT " +
//                         "WHERE UTILISATEUR_ID = ? " +
//                         "AND CREATED_AT BETWEEN ? AND ? " +
//                         "AND STATUS = 'VISIBLE' " +
//                         "ORDER BY CREATED_AT DESC";
//
//            CommandeDAO commandeDAO = new CommandeDAO();
//
//            try (Connection conn = getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//                stmt.setInt(1, utilisateurId);
//                stmt.setTimestamp(2, dateDebut);
//                stmt.setTimestamp(3, dateFin);
//
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        Point p = new Point(
//                                rs.getInt("ID"),
//                                rs.getInt("UTILISATEUR_ID"),
//                                rs.getInt("COMMANDE_ID"),
//                                rs.getInt("TOTAL_POINTS"),
//                                rs.getTimestamp("CREATED_AT"),
//                                rs.getTimestamp("UPDATED_AT"),
//                                rs.getString("STATUS")
//                        );
//
//                        // Récupérer la commande complète et l'ajouter au point
//                        Commande commande = commandeDAO.getCommandeById(p.getCommandeId());
//                        p.setCommande(commande);
//
//                        points.add(p);
//                    }
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            return points;
//        }


        /**
         * Récupère l'historique des points pour un point donné avec filtre de date
         */
//        public List<PointHistorique> getHistoriquePointsParPoint(int pointId, Timestamp dateDebut, Timestamp dateFin) {
//            List<PointHistorique> historiques = new ArrayList<>();
//            String sql = "SELECT * FROM POINT_HISTORIQUE " +
//                         "WHERE POINT_ID = ? " +
//                         "AND CREATED_AT BETWEEN ? AND ? " +
//                         "AND STATUS = 'VISIBLE' " +
//                         "ORDER BY CREATED_AT DESC";
//
//            try (Connection conn = getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//                stmt.setInt(1, pointId);
//                stmt.setTimestamp(2, dateDebut);
//                stmt.setTimestamp(3, dateFin);
//
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        PointHistorique ph = new PointHistorique(
//                                rs.getInt("ID"),
//                                rs.getInt("POINT_ID"),
//                                rs.getInt("COMMANDE_ID"),
//                                rs.getInt("PRODUIT_ID"),
//                                rs.getInt("PLAT_ID"),
//                                rs.getInt("QUANTITE"),
//                                rs.getInt("OLD_QTE_POINTS"),
//                                rs.getTimestamp("CREATED_AT"),
//                                rs.getTimestamp("UPDATED_AT"),
//                                rs.getString("STATUS")
//                        );
//
//                        // Récupérer les objets Produit et Plat si nécessaire
//                        if (ph.getProduitId() != 0) {
//                            Produit produit = produitDAO.chercherParId(ph.getProduitId());
//                            ph.setProduit(produit);
//                        }
//                        if (ph.getPlatId() != 0) {
//                            Plat plat = platDAO.chercherParId(ph.getPlatId());
//                            ph.setPlat(plat);
//                        }
//
//                        historiques.add(ph);
//                    }
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            return historiques;
//        }
        
//        public List<Point> getPointsAvecCommandesEtDetails(int utilisateurId, Timestamp dateDebut, Timestamp dateFin) {
//            List<Point> points = new ArrayList<>();
//            String sqlPoint = "SELECT * FROM POINT " +
//                              "WHERE UTILISATEUR_ID = ? " +
//                              "AND CREATED_AT BETWEEN ? AND ? " +
//                              "AND STATUS = 'VISIBLE' " +
//                              "ORDER BY CREATED_AT DESC";
//
//            CommandeDAO commandeDAO = new CommandeDAO();
//
//            try (Connection conn = getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(sqlPoint)) {
//
//                stmt.setInt(1, utilisateurId);
//                stmt.setTimestamp(2, dateDebut);
//                stmt.setTimestamp(3, dateFin);
//
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        Point p = new Point(
//                                rs.getInt("ID"),
//                                rs.getInt("UTILISATEUR_ID"),
//                                rs.getInt("COMMANDE_ID"),
//                                rs.getInt("TOTAL_POINTS"),
//                                rs.getTimestamp("CREATED_AT"),
//                                rs.getTimestamp("UPDATED_AT"),
//                                rs.getString("STATUS")
//                        );
//
//                        // Récupérer la commande complète avec détails (déjà avec qtePoints)
//                        Commande commande = commandeDAO.getCommandeAvecDetails(p.getCommandeId());
//                        p.setCommande(commande);
//
//                        points.add(p);
//                    }
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            return points;
//        }


 


    // Méthodes utilitaires pour récupérer le barème de points
    private int getPointsProduit(Connection conn, int produitId) throws SQLException {
        String sql = "SELECT POINTS FROM PRODUIT WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("POINTS");
            }
        }
        return 0;
    }

    private int getPointsPlat(Connection conn, int platId) throws SQLException {
        String sql = "SELECT POINTS FROM PLAT WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, platId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("POINTS");
            }
        }
        return 0;
    }
    
    public PrivilegeNiveau verifierEtMettreAJourNiveau(int utilisateurId) {
        System.out.println(">>> [DEBUG] Début verifierEtMettreAJourNiveau | utilisateurId = " + utilisateurId);

        try (Connection conn = getConnection()) {
            System.out.println(">>> [DEBUG] Connexion BD obtenue");
            conn.setAutoCommit(false);
            System.out.println(">>> [DEBUG] AutoCommit désactivé");

            try {
                // 1. Total des points
                System.out.println(">>> [DEBUG] Récupération du total des points");
                int totalPoints = getTotalPointsUtilisateur(conn, utilisateurId);
                System.out.println(">>> [DEBUG] Total points = " + totalPoints);

                // 2. Niveau actuel
                System.out.println(">>> [DEBUG] Récupération du niveau actuel");
                PrivilegeNiveau niveauActuel = getNiveauUtilisateur(conn, utilisateurId);
                System.out.println(">>> [DEBUG] Niveau actuel = " +
                        (niveauActuel != null ? niveauActuel.getNom() : "AUCUN"));

                // 3. Nouveau niveau
                System.out.println(">>> [DEBUG] Détermination du nouveau niveau");
                PrivilegeNiveau nouveauNiveau =
                        privilegeNiveauDAO.determinerNiveauUtilisateur(totalPoints);

                System.out.println(">>> [DEBUG] Nouveau niveau = " +
                        (nouveauNiveau != null ? nouveauNiveau.getNom() : "NULL"));

                // Sécurité
                if (nouveauNiveau == null) {
                    System.out.println(">>> [WARN] Aucun niveau trouvé pour " + totalPoints + " points");
                    conn.rollback();
                    return null;
                }

                // 4. Vérification changement
                boolean changementNiveau = false;

                if (niveauActuel == null) {
                    System.out.println(">>> [DEBUG] Aucun niveau actuel → attribution");
                    changementNiveau = true;
                } else if (!niveauActuel.getNom().equals(nouveauNiveau.getNom())) {
                    System.out.println(">>> [DEBUG] Changement de niveau : "
                            + niveauActuel.getNom() + " → " + nouveauNiveau.getNom());
                    changementNiveau = true;
                } else {
                    System.out.println(">>> [DEBUG] Niveau inchangé");
                }

                if (changementNiveau) {
                    // 5. Mise à jour
                    System.out.println(">>> [DEBUG] Mise à jour du niveau utilisateur");
                    boolean miseAJourReussie =
                            mettreAJourNiveauUtilisateur(conn, utilisateurId, nouveauNiveau.getNom());

                    System.out.println(">>> [DEBUG] Résultat mise à jour = " + miseAJourReussie);

                    if (miseAJourReussie) {
                        // 6. Notification
                        System.out.println(">>> [DEBUG] Création notification changement niveau");
                        creerNotificationChangementNiveau(
                                conn, utilisateurId, niveauActuel, nouveauNiveau);

                        conn.commit();
                        System.out.println(">>> [DEBUG] Transaction COMMIT");
                        return nouveauNiveau;
                    } else {
                        System.out.println(">>> [ERROR] Échec mise à jour du niveau");
                    }
                }

                conn.commit();
                System.out.println(">>> [DEBUG] Fin sans changement (COMMIT)");
                return null;

            } catch (SQLException e) {
                System.out.println(">>> [ERROR] SQLException dans transaction");
                System.out.println(">>> [ERROR] Message : " + e.getMessage());
                e.printStackTrace();
                conn.rollback();
                System.out.println(">>> [DEBUG] ROLLBACK effectué");
                return null;
            }

        } catch (SQLException e) {
            System.out.println(">>> [ERROR] SQLException connexion BD");
            System.out.println(">>> [ERROR] Message : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    /**
     * Récupère le total des points d'un utilisateur (avec Connection fournie)
     */
    public int getTotalPointsUtilisateur(Connection conn, int utilisateurId) throws SQLException {
        String sql = "SELECT SUM(POINTS_OBTENUS) AS TOTAL FROM POINT " +
                    "WHERE UTILISATEUR_ID = ? AND STATUT = 'VALIDE'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("TOTAL");
                    return rs.wasNull() ? 0 : total;
                }
            }
        }
        return 0;
    }
    
    public int getTotalPointsUtilisateur_v2( int utilisateurId) throws SQLException {
        String sql = "SELECT SUM(POINTS_OBTENUS) AS TOTAL FROM POINT " +
                    "WHERE UTILISATEUR_ID = ? AND STATUT = 'VALIDE'";
        
        try (Connection conn = getConnection()) {
      PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("TOTAL");
                    return rs.wasNull() ? 0 : total;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    
    /**
     * Récupère le niveau actuel d'un utilisateur (avec Connection fournie)
     */
    private PrivilegeNiveau getNiveauUtilisateur(Connection conn, int utilisateurId) throws SQLException {
        String sql = "SELECT PN.* FROM PRIVILEGE_NIVEAU PN " +
                    "INNER JOIN UTILISATEUR U ON U.PRIVILLEGE = PN.NOM " +
                    "WHERE U.ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PrivilegeNiveau niveau = new PrivilegeNiveau();
                    niveau.setId(rs.getInt("ID"));
                    niveau.setNom(rs.getString("NOM"));
                    niveau.setSeuilPoints(rs.getInt("SEUIL_POINTS"));
                    niveau.setPourcentageReduction(rs.getBigDecimal("POURCENTAGE_REDUCTION"));
                    niveau.setDescription(rs.getString("DESCRIPTION"));
                    niveau.setCouleur(rs.getString("COULEUR"));
                    niveau.setStatut(rs.getString("STATUT"));
                    return niveau;
                }
            }
        }
        return null;
    }
    
    /**
     * Met à jour le niveau de l'utilisateur dans la base de données
     */
    private boolean mettreAJourNiveauUtilisateur(Connection conn, int utilisateurId, String niveau) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET PRIVILLEGE = ?, UPDATE_DATE = CURRENT_TIMESTAMP " +
                    "WHERE ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, niveau);
            stmt.setInt(2, utilisateurId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Crée une notification pour le changement de niveau
     */
    private void creerNotificationChangementNiveau(Connection conn, int utilisateurId, 
                                                  PrivilegeNiveau ancienNiveau, 
                                                  PrivilegeNiveau nouveauNiveau) throws SQLException {
        
        String ancienNiveauNom = ancienNiveau != null ? ancienNiveau.getNom() : "Débutant";
        String message = String.format(
            "🎉 Félicitations ! Vous êtes passé du niveau %s au niveau %s ! " +
            "Profitez de nouveaux avantages avec votre statut %s.",
            ancienNiveauNom, 
            nouveauNiveau.getNom(),
            nouveauNiveau.getNom()
        );
        
        String sql = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, TYPE_NOTIF, " +
                    "CREATED_AT, UPDATED_AT, STATUS, IS_READ) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'VISIBLE', 0)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "SYSTEM");
            stmt.setInt(2, utilisateurId);
            stmt.setString(3, message);
            stmt.setString(4, "CHANGEMENT_NIVEAU");
            
            stmt.executeUpdate();
        }
    }
    
    
}
