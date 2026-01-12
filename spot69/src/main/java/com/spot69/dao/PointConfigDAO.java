package com.spot69.dao;

import com.spot69.model.Notification;
import com.spot69.model.PointConfig;
import com.spot69.model.PrivilegeNiveau;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointConfigDAO {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
 // Créer une configuration et supprimer/archiver les anciennes si type = MONTANT_TOTAL
    public int creerConfig(PointConfig config) {
        String sqlInsert = "INSERT INTO POINT_CONFIG (TYPE_CONFIG, REF_ID, POINTS, CONDITION_VALEUR, " +
                           "CONDITION_TYPE, DATE_DEBUT, DATE_FIN, UTILISATEUR_ID, STATUT) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // transaction

            // --- 1. Si c'est MONTANT_TOTAL, désactiver les anciennes configs ---
            if ("MONTANT_TOTAL".equals(config.getTypeConfig())) {
                String sqlUpdate = "UPDATE POINT_CONFIG SET STATUT = 'INACTIF' " +
                                   "WHERE TYPE_CONFIG = 'MONTANT_TOTAL' AND STATUT = 'ACTIF'";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    int nbModifies = stmtUpdate.executeUpdate();
                    System.out.println("[DEBUG] Anciennes configs MONTANT_TOTAL désactivées : " + nbModifies);
                }
            }
            
            if ("VALEUR_POINT".equals(config.getTypeConfig())) {
                String sqlUpdate = "UPDATE POINT_CONFIG SET STATUT = 'INACTIF' " +
                                   "WHERE TYPE_CONFIG = 'VALEUR_POINT' AND STATUT = 'ACTIF'";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    int nbModifies = stmtUpdate.executeUpdate();
                    System.out.println("[DEBUG] Anciennes configs MONTANT_TOTAL désactivées : " + nbModifies);
                }
            }

            // --- 2. Insérer la nouvelle config ---
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, config.getTypeConfig());

                if (config.getRefId() != null) {
                    stmt.setInt(2, config.getRefId());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                stmt.setInt(3, config.getPoints());

                if (config.getConditionValeur() != null) {
                    stmt.setDouble(4, config.getConditionValeur());
                } else {
                    stmt.setNull(4, Types.DECIMAL);
                }

                stmt.setString(5, config.getConditionType());
                stmt.setTimestamp(6, config.getDateDebut());
                stmt.setTimestamp(7, config.getDateFin());

                if (config.getUtilisateurId() != null) {
                    stmt.setInt(8, config.getUtilisateurId());
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }

                stmt.setString(9, config.getStatut());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conn.commit(); // commit transaction
                        return rs.getInt(1);
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    
    // Mettre à jour une configuration
    public boolean modifierConfig(PointConfig config) {
        String sql = "UPDATE POINT_CONFIG SET TYPE_CONFIG = ?, REF_ID = ?, POINTS = ?, " +
                     "CONDITION_VALEUR = ?, CONDITION_TYPE = ?, DATE_DEBUT = ?, " +
                     "DATE_FIN = ?, STATUT = ?, UPDATED_AT = CURRENT_TIMESTAMP " +
                     "WHERE ID = ? AND STATUT != 'DELETED'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, config.getTypeConfig());
            if (config.getRefId() != null) {
                stmt.setInt(2, config.getRefId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, config.getPoints());
            
            if (config.getConditionValeur() != null) {
                stmt.setDouble(4, config.getConditionValeur());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            
            stmt.setString(5, config.getConditionType());
            stmt.setTimestamp(6, config.getDateDebut());
            stmt.setTimestamp(7, config.getDateFin());
            stmt.setString(8, config.getStatut());
            stmt.setInt(9, config.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Supprimer une configuration (soft delete)
    public boolean supprimerConfig(int configId, int utilisateurId) {
        String sql = "UPDATE POINT_CONFIG SET STATUT = 'DELETED', UTILISATEUR_ID = ?, " +
                     "UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setInt(2, configId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Récupérer une configuration par ID
    public PointConfig getConfigById(int configId) {
        String sql = "SELECT * FROM POINT_CONFIG WHERE ID = ? AND STATUT != 'DELETED'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, configId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Récupérer toutes les configurations actives par type
    public List<PointConfig> getConfigsByType(String typeConfig) {
        List<PointConfig> configs = new ArrayList<>();
        String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = ? AND STATUT = 'ACTIF' " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) ORDER BY CREATED_AT DESC";
        
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
    
    // Récupérer les configurations pour un produit
    public PointConfig getConfigForProduit(int produitId) {
        String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = 'PRODUIT' " +
                     "AND REF_ID = ? AND STATUT = 'ACTIF' " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY CREATED_AT DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produitId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Récupérer les configurations pour une catégorie
    public PointConfig getConfigForCategorie(int categorieId) {
        String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = 'CATEGORIE' " +
                     "AND REF_ID = ? AND STATUT = 'ACTIF' " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY CREATED_AT DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categorieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPointConfig(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
// Dans la classe PointConfigDAO, modifiez ces méthodes :

/**
 * Convertit des points en valeur monétaire selon la configuration VALEUR_POINT
 * en respectant les paliers exacts
 * Exemple: avec config 10 points = 200 HTG et 5 points = 100 HTG
 * Pour 26 points: 20 points (2x200) + 5 points (1x100) = 500 HTG (et non 26 points convertis directement)
 * 
 * @param points Nombre de points à convertir
 * @return La valeur monétaire correspondante, ou null si configuration non trouvée
 */
public Double convertirPointsEnValeur(int points) {
    // Récupérer toutes les configs VALEUR_POINT actives, triées par condition (du plus grand au plus petit)
    List<PointConfig> configs = getToutesConfigsValeurPointTriees();
    
    if (configs == null || configs.isEmpty()) {
        System.err.println("Aucune configuration VALEUR_POINT active trouvée");
        return null;
    }
    
    int pointsRestants = points;
    double valeurTotale = 0.0;
    
    System.out.println("[DEBUG] Conversion de " + points + " points selon les paliers:");
    
    // Parcourir les configs du plus grand palier au plus petit
    for (PointConfig config : configs) {
        int pointsParPalier = config.getPoints();
        double valeurPalier = config.getConditionValeur();
        
        if (pointsParPalier <= 0) continue; // Éviter division par zéro
        
        // Calculer combien de fois ce palier peut être appliqué
        int nombrePaliers = pointsRestants / pointsParPalier;
        
        if (nombrePaliers > 0) {
            double valeurPourCePalier = nombrePaliers * valeurPalier;
            valeurTotale += valeurPourCePalier;
            pointsRestants -= nombrePaliers * pointsParPalier;
            
            System.out.println("[DEBUG] - " + nombrePaliers + " x (" + pointsParPalier + 
                             " points = " + valeurPalier + " HTG) = " + valeurPourCePalier + " HTG");
            System.out.println("[DEBUG]   Points restants: " + pointsRestants);
        }
        
        if (pointsRestants == 0) {
            break; // Tous les points ont été convertis
        }
    }
    
    // Si il reste des points après application des paliers
    if (pointsRestants > 0) {
        System.out.println("[DEBUG] " + pointsRestants + " points restants ne peuvent être convertis " +
                         "(aucun palier correspondant)");
        // Option 1: ignorer les points restants
        // Option 2: utiliser le plus petit palier (commenté)
        // double plusPetitPalier = configs.get(configs.size()-1).getConditionValeur();
        // double plusPetitPoints = configs.get(configs.size()-1).getPoints();
        // double valeurPointsRestants = (pointsRestants * plusPetitPalier) / plusPetitPoints;
        // valeurTotale += valeurPointsRestants;
        // System.out.println("[DEBUG] Conversion proportionnelle: " + pointsRestants + 
        //                  " points = " + valeurPointsRestants + " HTG");
    }
    
    System.out.println("[CONVERSION POINTS] " + points + " points = " + valeurTotale + " HTG");
    return valeurTotale;
}

/**
 * Récupère toutes les configurations VALEUR_POINT actives triées par nombre de points (décroissant)
 * 
 * @return Liste des configurations triées, ou liste vide si aucune
 */
public List<PointConfig> getToutesConfigsValeurPointTriees() {
    List<PointConfig> configs = new ArrayList<>();
    String sql = "SELECT * FROM POINT_CONFIG WHERE TYPE_CONFIG = 'VALEUR_POINT' " +
                 "AND STATUT = 'ACTIF' " +
                 "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                 "ORDER BY POINTS DESC"; // Tri décroissant pour prendre d'abord les plus grands paliers
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
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

/**
 * Convertit une valeur monétaire en points selon les paliers de configuration VALEUR_POINT
 * 
 * @param valeurMontant Valeur monétaire à convertir en points
 * @return Le nombre de points correspondant, ou null si configuration non trouvée
 */
public Integer convertirValeurEnPoints(Double valeurMontant) {
    List<PointConfig> configs = getToutesConfigsValeurPointTriees();
    
    if (configs == null || configs.isEmpty()) {
        System.err.println("Aucune configuration VALEUR_POINT active trouvée");
        return null;
    }
    
    double valeurRestante = valeurMontant;
    int totalPoints = 0;
    
    System.out.println("[DEBUG] Conversion de " + valeurMontant + " HTG en points:");
    
    // Parcourir du plus grand palier au plus petit
    for (PointConfig config : configs) {
        int pointsParPalier = config.getPoints();
        double valeurPalier = config.getConditionValeur();
        
        if (valeurPalier <= 0) continue;
        
        // Calculer combien de fois ce palier peut être appliqué
        int nombrePaliers = (int) (valeurRestante / valeurPalier);
        
        if (nombrePaliers > 0) {
            int pointsPourCePalier = nombrePaliers * pointsParPalier;
            totalPoints += pointsPourCePalier;
            valeurRestante -= nombrePaliers * valeurPalier;
            
            System.out.println("[DEBUG] - " + nombrePaliers + " x (" + valeurPalier + 
                             " HTG = " + pointsParPalier + " points) = " + pointsPourCePalier + " points");
            System.out.println("[DEBUG]   Valeur restante: " + valeurRestante);
        }
        
        if (valeurRestante <= 0.001) { // Tolérance pour les erreurs d'arrondi
            break;
        }
    }
    
    // Si il reste de la valeur non convertie après application des paliers
    if (valeurRestante > 0.001) {
        System.out.println("[DEBUG] " + String.format("%.2f", valeurRestante) + 
                         " HTG restants ne peuvent être convertis exactement");
        // Option: ignorer ou arrondir au palier inférieur
    }
    
    System.out.println("[CONVERSION VALEUR] " + valeurMontant + " HTG = " + totalPoints + " points");
    return totalPoints;
}

/**
 * Version alternative pour utiliser les points de manière optimisée selon les paliers
 * Cette méthode calcule le nombre maximum de points utilisables selon les paliers disponibles
 */
public Integer calculerPointsUtilisablesSelonPaliers(int pointsDisponibles) {
    List<PointConfig> configs = getToutesConfigsValeurPointTriees();
    
    if (configs == null || configs.isEmpty()) {
        System.err.println("Aucune configuration VALEUR_POINT active trouvée");
        return pointsDisponibles; // Retourner tout si pas de paliers
    }
    
    int pointsUtilisables = 0;
    int pointsRestants = pointsDisponibles;
    
    System.out.println("[DEBUG] Calcul des points utilisables sur " + pointsDisponibles + " points disponibles:");
    
    for (PointConfig config : configs) {
        int pointsParPalier = config.getPoints();
        
        // Calculer combien de fois ce palier peut être appliqué
        int nombrePaliers = pointsRestants / pointsParPalier;
        
        if (nombrePaliers > 0) {
            int pointsPourCePalier = nombrePaliers * pointsParPalier;
            pointsUtilisables += pointsPourCePalier;
            pointsRestants -= pointsPourCePalier;
            
            System.out.println("[DEBUG] - " + nombrePaliers + " x " + pointsParPalier + 
                             " points = " + pointsPourCePalier + " points utilisables");
            System.out.println("[DEBUG]   Points restants non utilisables: " + pointsRestants);
        }
        
        if (pointsRestants == 0) {
            break;
        }
    }
    
    System.out.println("[UTILISATION POINTS] Sur " + pointsDisponibles + " points, " + 
                     pointsUtilisables + " sont utilisables selon les paliers");
    
    return pointsUtilisables;
}

/**
 * Méthode optimisée pour utiliser les points selon les paliers
 */
public Double utiliserPointsSelonPaliers(int utilisateurId, int pointsDemandes) {
    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);
        
        try {
            // 1. Vérifier que l'utilisateur a assez de points
            PointDAO pointDAO = new PointDAO();
            int totalPointsDisponibles = pointDAO.getTotalPointsUtilisateur(conn, utilisateurId);
            
            if (pointsDemandes > totalPointsDisponibles) {
                System.err.println("Points insuffisants: " + pointsDemandes + 
                                 " demandés, " + totalPointsDisponibles + " disponibles");
                conn.rollback();
                return null;
            }
            
            // 2. Calculer combien de points peuvent être réellement utilisés selon les paliers
            int pointsUtilisables = calculerPointsUtilisablesSelonPaliers(pointsDemandes);
            
            if (pointsUtilisables < pointsDemandes) {
                System.out.println("[INFO] Ajustement: " + pointsDemandes + " points demandés, " +
                                 "mais seulement " + pointsUtilisables + " utilisables selon les paliers");
            }
            
            // 3. Convertir les points utilisables en valeur
            Double valeurConvertie = convertirPointsEnValeur(pointsUtilisables);
            if (valeurConvertie == null) {
                conn.rollback();
                return null;
            }
            
            // 4. Mettre à jour le niveau de l'utilisateur
            PrivilegeNiveau nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(utilisateurId);
            
            if (nouveauNiveau != null) {
                System.out.println("🎉 Niveau mis à jour après utilisation de points: " + nouveauNiveau.getNom());
            }
            
            conn.commit();
            
            // 5. Créer une notification
            NotificationDAO notificationDAO = new NotificationDAO();
            Notification notif = new Notification();
            notif.setGeneratedBy("SYSTEM");
            notif.setToUser(utilisateurId);
            notif.setMessages("Vous avez utilisé " + pointsUtilisables + 
                             " points (sur " + pointsDemandes + " demandés) " +
                             "pour obtenir " + String.format("%.2f", valeurConvertie) + 
                             " HTG de réduction.");
            notif.setTypeNotif("UTILISATION_POINTS");
            notif.setStatus("VISIBLE");
            notificationDAO.ajouterNotification(notif);
            
            return valeurConvertie;
            
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
            return null;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    
    // Mapper ResultSet à PointConfig
    private PointConfig mapResultSetToPointConfig(ResultSet rs) throws SQLException {
        return new PointConfig(
            rs.getInt("ID"),
            rs.getString("TYPE_CONFIG"),
            rs.getObject("REF_ID", Integer.class),
            rs.getInt("POINTS"),
            rs.getObject("CONDITION_VALEUR", Double.class),
            rs.getString("CONDITION_TYPE"),
            rs.getTimestamp("DATE_DEBUT"),
            rs.getTimestamp("DATE_FIN"),
            rs.getObject("UTILISATEUR_ID", Integer.class),
            rs.getString("STATUT"),
            rs.getTimestamp("CREATED_AT"),
            rs.getTimestamp("UPDATED_AT")
        );
    }
}