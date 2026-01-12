package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;


public class CaisseCaissiereDAO {
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // 1. Ouvrir une caisse
    public int ouvrirCaisse(CaisseCaissiere caisse) {
        String sql = "INSERT INTO CAISSE_CAISSIERE (CAISSIERE_ID, OUVERTURE, SOLDE_INITIAL, STATUT, SHOT, MONTANT_SHOT) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, caisse.getCaissiereId());
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setBigDecimal(3, caisse.getSoldeInitial() != null ? 
                caisse.getSoldeInitial() : BigDecimal.ZERO);
            stmt.setString(4, "OUVERTE");
            stmt.setBoolean(5, caisse.isShot() != null ? caisse.isShot() : false);
            stmt.setBigDecimal(6, caisse.getMontantShot() != null ? 
                caisse.getMontantShot() : BigDecimal.ZERO);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
 // Méthode pour récupérer les dépôts sur caisse (DETOT_CAISSE)
    public List<Map<String, Object>> getDepotsCaisse(Integer caisseId, Date dateDebut, Date dateFin) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT * FROM depots_caisse WHERE caisse_id = ?";
        
        List<Object> params = new ArrayList<>();
        params.add(caisseId);
        
        if (dateDebut != null) {
            sql += " AND date_depot >= ?";
            params.add(dateDebut);
        }
        if (dateFin != null) {
            sql += " AND date_depot <= ?";
            params.add(dateFin);
        }
        
        sql += " ORDER BY date_depot ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i).toLowerCase(), rs.getObject(i));
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    // Méthode pour récupérer le total des retraits
    public BigDecimal getTotalRetraitsByCaisse(Integer caisseId) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(montant) as total FROM transactions_caisse " +
                     "WHERE caisse_id = ? AND type_transaction = 'RETRAIT'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caisseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return total != null ? total : BigDecimal.ZERO;
    }
    
    // 2. Fermer une caisse
    public boolean fermerCaisse(int caisseId, BigDecimal soldeFinal, boolean shot, BigDecimal montantShot) {
        String sql = "UPDATE CAISSE_CAISSIERE SET " +
                     "FERMETURE = ?, SOLDE_FINAL = ?, STATUT = 'FERMEE', " +
                     "SHOT = ?, MONTANT_SHOT = ? " +
                     "WHERE ID = ? AND STATUT = 'OUVERTE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setBigDecimal(2, soldeFinal);
            stmt.setBoolean(3, shot);
            stmt.setBigDecimal(4, montantShot != null ? montantShot : BigDecimal.ZERO);
            stmt.setInt(5, caisseId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean mettreAJourMontantShot(int caisseId, BigDecimal montantShot) {
        String sql = "UPDATE CAISSE_CAISSIERE SET MONTANT_SHOT = ?, SHOT = ? " +
                     "WHERE ID = ? AND STATUT = 'OUVERTE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, montantShot != null ? montantShot : BigDecimal.ZERO);
            stmt.setBoolean(2, montantShot != null && montantShot.compareTo(BigDecimal.ZERO) > 0);
            stmt.setInt(3, caisseId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 // 4. Calculer le contrôle à remettre (avec prise en compte du shot)
    public Map<String, BigDecimal> calculerControleARemettre(int caissiereId, int caisseId, boolean shot, BigDecimal montantShot) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        // 1. Total des ventes (commandes payées)
        String sqlVentes = "SELECT " +
                          "COALESCE(SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END), 0) AS total_ventes " +
                          "FROM COMMANDE c " +
                          "WHERE c.CASHED_BY = ? " +
                          "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?)";
        
        // 2. Total des crédits payés
        String sqlCredits = "SELECT " +
                           "COALESCE(SUM(t.MONTANT), 0) AS total_credits " +
                           "FROM TRANSACTION_COMPTE t " +
                           "JOIN TYPE_TRANSACTION tt ON t.TYPE_TRANSACTION_ID = tt.ID " +
                           "WHERE t.CAISSIERE_ID = ? " +
                           "AND DATE(t.DATE_TRANSACTION) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                           "AND tt.CODE = 'DEPENSE'";
        
        // 3. Commandes en SOLDE
        String sqlSoldes = "SELECT " +
                          "COALESCE(SUM(c.MONTANT_TOTAL), 0) AS total_solde " +
                          "FROM COMMANDE c " +
                          "WHERE c.CASHED_BY = ? " +
                          "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                          "AND c.MODE_PAIEMENT = 'SOLDE' " +
                          "AND c.STATUT_PAIEMENT = 'PAYE'";
        
        // 4. Total dépôts dans la caisse (argent physique déposé)
        String sqlDepotsCaisse = "SELECT " +
                               "COALESCE(SUM(CASE WHEN TYPE_OPERATION = 'DEPOT' THEN MONTANT ELSE 0 END), 0) AS total_depots " +
                               "FROM TRANSACTION_CAISSE " +
                               "WHERE CAISSE_ID = ?";
        
        // 5. Total retraits de la caisse
        String sqlRetraitsCaisse = "SELECT " +
                                  "COALESCE(SUM(CASE WHEN TYPE_OPERATION = 'RETRAIT' THEN ABS(MONTANT) ELSE 0 END), 0) AS total_retraits " +
                                  "FROM TRANSACTION_CAISSE " +
                                  "WHERE CAISSE_ID = ?";
        
        try (Connection conn = getConnection()) {
            // Total ventes
            BigDecimal totalVentes = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(sqlVentes)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalVentes = rs.getBigDecimal("total_ventes");
                        result.put("totalVentes", totalVentes);
                    }
                }
            }
            
            // Total crédits
            BigDecimal totalCredits = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(sqlCredits)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalCredits = rs.getBigDecimal("total_credits");
                        result.put("totalCredits", totalCredits);
                    }
                }
            }
            
            // Total soldes
            BigDecimal totalSoldes = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(sqlSoldes)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalSoldes = rs.getBigDecimal("total_solde");
                        result.put("totalSoldes", totalSoldes);
                    }
                }
            }
            
            // Total dépôts caisse
            BigDecimal totalDepotsCaisse = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(sqlDepotsCaisse)) {
                stmt.setInt(1, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalDepotsCaisse = rs.getBigDecimal("total_depots");
                        result.put("totalDepotsCaisse", totalDepotsCaisse);
                    }
                }
            }
            
            // Total retraits caisse
            BigDecimal totalRetraitsCaisse = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(sqlRetraitsCaisse)) {
                stmt.setInt(1, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalRetraitsCaisse = rs.getBigDecimal("total_retraits");
                        result.put("totalRetraitsCaisse", totalRetraitsCaisse);
                    }
                }
            }
            
            // Calcul du contrôle théorique (argent qui devrait être dans la caisse)
            BigDecimal controleTheorique = totalVentes
                .subtract(totalCredits)
                .subtract(totalSoldes)
                .add(totalDepotsCaisse)
                .subtract(totalRetraitsCaisse);
            
            result.put("controleTheorique", controleTheorique);
            
            // Si SHOT = true, on a un déficit
            if (shot && montantShot != null) {
                // La caissière doit de l'argent
                BigDecimal montantDu = montantShot;
                result.put("montantShot", montantShot);
                result.put("controleARemettre", controleTheorique.subtract(montantShot));
                result.put("montantDu", montantDu);
            } else {
                // Pas de shot, contrôle normal
                result.put("montantShot", BigDecimal.ZERO);
                result.put("controleARemettre", controleTheorique);
                result.put("montantDu", BigDecimal.ZERO);
            }
            
            // Calcul de l'écart entre solde théorique et solde réel
            CaisseCaissiere caisse = getCaisseById(caisseId);
            if (caisse != null && caisse.getSoldeFinal() != null) {
                BigDecimal ecart = caisse.getSoldeFinal().subtract(controleTheorique);
                result.put("ecart", ecart);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // 5. Calculer le montant total dû par la caissière (shot cumulé)
    public BigDecimal calculerTotalShotCaissiere(int caissiereId) {
        String sql = "SELECT COALESCE(SUM(MONTANT_SHOT), 0) AS total_shot " +
                     "FROM CAISSE_CAISSIERE " +
                     "WHERE CAISSIERE_ID = ? " +
                     "AND SHOT = TRUE " +
                     "AND STATUT = 'FERMEE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_shot");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    // 6. Récupérer l'historique des shots
    public List<Map<String, Object>> getHistoriqueShots(int caissiereId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT cc.ID, cc.OUVERTURE, cc.FERMETURE, cc.MONTANT_SHOT, " +
                     "cc.SOLDE_FINAL, cc.SOLDE_INITIAL, " +
                     "u.NOM, u.PRENOM " +
                     "FROM CAISSE_CAISSIERE cc " +
                     "JOIN UTILISATEUR u ON cc.CAISSIERE_ID = u.ID " +
                     "WHERE cc.CAISSIERE_ID = ? " +
                     "AND cc.SHOT = TRUE " +
                     "AND cc.STATUT = 'FERMEE' " +
                     "ORDER BY cc.FERMETURE DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> shot = new HashMap<>();
                    shot.put("caisseId", rs.getInt("ID"));
                    shot.put("ouverture", rs.getTimestamp("OUVERTURE"));
                    shot.put("fermeture", rs.getTimestamp("FERMETURE"));
                    shot.put("montantShot", rs.getBigDecimal("MONTANT_SHOT"));
                    shot.put("soldeFinal", rs.getBigDecimal("SOLDE_FINAL"));
                    shot.put("soldeInitial", rs.getBigDecimal("SOLDE_INITIAL"));
                    shot.put("caissiereNom", rs.getString("NOM") + " " + rs.getString("PRENOM"));
                    
                    // Calculer l'écart
                    BigDecimal soldeTheorique = calculerSoldeTheorique(rs.getInt("ID"));
                    BigDecimal ecart = rs.getBigDecimal("SOLDE_FINAL").subtract(soldeTheorique);
                    shot.put("ecart", ecart);
                    
                    result.add(shot);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public boolean rembourserShot(int caisseId, BigDecimal montantRembourse) {
        String sql = "UPDATE CAISSE_CAISSIERE SET MONTANT_SHOT = GREATEST(0, MONTANT_SHOT - ?) " +
                     "WHERE ID = ? AND STATUT = 'FERMEE' AND SHOT = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, montantRembourse);
            stmt.setInt(2, caisseId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    // 3. Récupérer la caisse ouverte d'une caissière
    public CaisseCaissiere getCaisseOuverte(int caissiereId) {
        String sql = "SELECT cc.*, u.NOM, u.PRENOM " +
                     "FROM CAISSE_CAISSIERE cc " +
                     "JOIN UTILISATEUR u ON cc.CAISSIERE_ID = u.ID " +
                     "WHERE cc.CAISSIERE_ID = ? AND cc.STATUT = 'OUVERTE' " +
                     "ORDER BY cc.OUVERTURE DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCaisseCaissiere(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 4. Récupérer une caisse par ID
    public CaisseCaissiere getCaisseById(int caisseId) {
        String sql = "SELECT cc.*, u.NOM, u.PRENOM " +
                     "FROM CAISSE_CAISSIERE cc " +
                     "JOIN UTILISATEUR u ON cc.CAISSIERE_ID = u.ID " +
                     "WHERE cc.ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caisseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCaisseCaissiere(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 5. Historique des caisses
    public List<CaisseCaissiere> getHistoriqueCaisses(Integer caissiereId, Date dateDebut, Date dateFin) {
        List<CaisseCaissiere> caisses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT cc.*, u.NOM, u.PRENOM " +
            "FROM CAISSE_CAISSIERE cc " +
            "JOIN UTILISATEUR u ON cc.CAISSIERE_ID = u.ID " +
            "WHERE 1=1 "
        );
        
        if (caissiereId != null) {
            sql.append("AND cc.CAISSIERE_ID = ? ");
        }
        if (dateDebut != null) {
            sql.append("AND DATE(cc.OUVERTURE) >= ? ");
        }
        if (dateFin != null) {
            sql.append("AND DATE(cc.OUVERTURE) <= ? ");
        }
        sql.append("ORDER BY cc.OUVERTURE DESC");
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (caissiereId != null) {
                stmt.setInt(paramIndex++, caissiereId);
            }
            if (dateDebut != null) {
                stmt.setDate(paramIndex++, dateDebut);
            }
            if (dateFin != null) {
                stmt.setDate(paramIndex, dateFin);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caisses.add(mapCaisseCaissiere(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caisses;
    }
    
    // 6. Calculer le solde théorique
    public BigDecimal calculerSoldeTheorique(int caisseId) {
        String sql = "SELECT " +
                     "(SELECT SOLDE_INITIAL FROM CAISSE_CAISSIERE WHERE ID = ?) + " +
                     "COALESCE(SUM(CASE " +
                     "  WHEN TYPE_OPERATION = 'DEPOT' OR TYPE_OPERATION = 'VENTE' THEN MONTANT " +
                     "  WHEN TYPE_OPERATION = 'RETRAIT' THEN -MONTANT " +
                     "  ELSE 0 END), 0) AS solde_theorique " +
                     "FROM TRANSACTION_CAISSE " +
                     "WHERE CAISSE_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caisseId);
            stmt.setInt(2, caisseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("solde_theorique");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    // 7. Méthodes pour les rapports comptes
    
    // 7.1. Comptes clients : dépôts depuis l'ouverture
 // 7.2. Commandes encaissées par rôle (clients) - Avec dates optionnelles
    public List<Map<String, Object>> getCommandesEncaisseesParClient(int caissiereId, int caisseId, Timestamp dateDebut, Timestamp dateFin) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Récupérer la caisse pour avoir la date d'ouverture
        CaisseCaissiere caisse = getCaisseById(caisseId);
        if (caisse == null) {
            return result;
        }
        
        Timestamp ouverture = caisse.getOuverture();
        Timestamp fermeture = caisse.getFermeture() != null ? 
                             caisse.getFermeture() : 
                             new Timestamp(System.currentTimeMillis());
        
        String sql = "SELECT c.*, " +
                    "       u.NOM as client_nom, u.PRENOM as client_prenom, " +
                    "       CONCAT(u.NOM, ' ', u.PRENOM) as nom_complet, " +
                    "       c.MONTANT_TOTAL as total " +
                    "FROM COMMANDE c " +
                    "JOIN UTILISATEUR u ON c.CLIENT_ID = u.ID " +
                    "WHERE c.CASHED_BY = ? " +
                    "AND c.STATUT_PAIEMENT = 'PAYE' " +
                    "AND c.STATUT = 'VISIBLE' " +
                    "AND c.DATE_COMMANDE >= ? " +
                    "AND c.DATE_COMMANDE <= ? "+
                    "AND c.MODE_PAIEMENT = 'SOLDE' " ;
        
        // Ajouter les filtres de dates supplémentaires si fournis
        if (dateDebut != null) {
            sql += "AND c.DATE_COMMANDE >= ? ";
        }
        if (dateFin != null) {
            sql += "AND c.DATE_COMMANDE <= ? ";
        }
        
        sql += "ORDER BY c.DATE_COMMANDE DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, caissiereId);
            stmt.setTimestamp(paramIndex++, ouverture);
            stmt.setTimestamp(paramIndex++, fermeture);
            
            if (dateDebut != null) {
                stmt.setTimestamp(paramIndex++, dateDebut);
            }
            if (dateFin != null) {
                stmt.setTimestamp(paramIndex++, dateFin);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("commande_id", rs.getInt("id"));
                    row.put("numero_commande", rs.getString("numero_commande"));
                    row.put("date_commande", rs.getTimestamp("date_commande"));
                    row.put("montant_total", rs.getBigDecimal("montant_total"));
                    row.put("total", rs.getBigDecimal("montant_total"));
                    row.put("mode_paiement", rs.getString("mode_paiement"));
                    row.put("statut_paiement", rs.getString("statut_paiement"));
                    row.put("client_nom", rs.getString("client_nom"));
                    row.put("client_prenom", rs.getString("client_prenom"));
                    row.put("nom_complet", rs.getString("nom_complet"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 7.3. Commandes encaissées pour les serveuses - Avec dates optionnelles
//    public List<Map<String, Object>> getCommandesEncaisseesParServeuse(int caissiereId, int caisseId, Timestamp dateDebut, Timestamp dateFin) {
//        List<Map<String, Object>> result = new ArrayList<>();
//        
//        // Récupérer la caisse pour avoir la date d'ouverture
//        CaisseCaissiere caisse = getCaisseById(caisseId);
//        if (caisse == null) {
//            return result;
//        }
//        
//        Timestamp ouverture = caisse.getOuverture();
//        Timestamp fermeture = caisse.getFermeture() != null ? 
//                             caisse.getFermeture() : 
//                             new Timestamp(System.currentTimeMillis());
//        
//        String sql = "SELECT u.id as serveuse_id, " +
//                    "       u.nom, u.prenom, " +
//                    "       CONCAT(u.nom, ' ', u.prenom) as nom_complet, " +
//                    "       COUNT(c.id) as nb_commandes, " +
//                    "       SUM(CASE WHEN c.statut_paiement = 'PAYE' THEN c.montant_total ELSE 0 END) as total_paye, " +
//                    "       SUM(CASE WHEN c.statut_paiement != 'PAYE' THEN c.montant_total ELSE 0 END) as total_non_paye, " +
//                    "       SUM(c.montant_total) as total_commandes " +
//                    "FROM commande c " +
//                    "JOIN utilisateur u ON c.utilisateur_id = u.id " +
//                    "WHERE c.cashed_by = ? " +
//                    "AND c.statut = 'VISIBLE' " +
//                    "AND c.date_commande >= ? " +
//                    "AND c.date_commande <= ? ";
//        
//        // Ajouter les filtres de dates supplémentaires si fournis
//        if (dateDebut != null) {
//            sql += "AND c.date_commande >= ? ";
//        }
//        if (dateFin != null) {
//            sql += "AND c.date_commande <= ? ";
//        }
//        
//        sql += "GROUP BY u.id, u.nom, u.prenom " +
//               "ORDER BY total_commandes DESC";
//        
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            
//            int paramIndex = 1;
//            stmt.setInt(paramIndex++, caissiereId);
//            stmt.setTimestamp(paramIndex++, ouverture);
//            stmt.setTimestamp(paramIndex++, fermeture);
//            
//            if (dateDebut != null) {
//                stmt.setTimestamp(paramIndex++, dateDebut);
//            }
//            if (dateFin != null) {
//                stmt.setTimestamp(paramIndex++, dateFin);
//            }
//            
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    Map<String, Object> row = new HashMap<>();
//                    row.put("serveuse_id", rs.getInt("serveuse_id"));
//                    row.put("nom", rs.getString("nom"));
//                    row.put("prenom", rs.getString("prenom"));
//                    row.put("nom_complet", rs.getString("nom_complet"));
//                    row.put("nb_commandes", rs.getInt("nb_commandes"));
//                    row.put("total_paye", rs.getBigDecimal("total_paye"));
//                    row.put("total_non_paye", rs.getBigDecimal("total_non_paye"));
//                    row.put("total_commandes", rs.getBigDecimal("total_commandes"));
//                    result.add(row);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    // 7.5. Nouvelle méthode pour récupérer les détails des commandes d'une serveuse spécifique
    public List<Map<String, Object>> getCommandesByServeuseAndPeriod(int serveuseId, int caissiereId, int caisseId, Timestamp dateDebut, Timestamp dateFin) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Récupérer la caisse pour avoir la date d'ouverture
        CaisseCaissiere caisse = getCaisseById(caisseId);
        if (caisse == null) {
            return result;
        }
        
        Timestamp ouverture = caisse.getOuverture();
        Timestamp fermeture = caisse.getFermeture() != null ? 
                             caisse.getFermeture() : 
                             new Timestamp(System.currentTimeMillis());
        
        String sql = "SELECT c.*, " +
                    "       cl.NOM as client_nom, cl.PRENOM as client_prenom, " +
                    "       CONCAT(cl.NOM, ' ', cl.PRENOM) as nom_complet " +
                    "FROM COMMANDE c " +
                    "LEFT JOIN UTILISATEUR cl ON c.CLENT_ID = cl.ID " +
                    "WHERE c.UTILISATEUR_ID = ? " +
                    "AND c.CASHED_BY = ? " +
                    "AND c.STATUT = 'VISIBLE' " +
                    "AND c.DATE_COMMANDE >= ? " +
                    "AND c.DATE_COMMANDE <= ? ";
        
        // Ajouter les filtres de dates supplémentaires si fournis
        if (dateDebut != null) {
            sql += "AND c.DATE_COMMANDE >= ? ";
        }
        if (dateFin != null) {
            sql += "AND c.DATE_COMMANDE <= ? ";
        }
        
        sql += "ORDER BY c.DATE_COMMANDE DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, serveuseId);
            stmt.setInt(paramIndex++, caissiereId);
            stmt.setTimestamp(paramIndex++, ouverture);
            stmt.setTimestamp(paramIndex++, fermeture);
            
            if (dateDebut != null) {
                stmt.setTimestamp(paramIndex++, dateDebut);
            }
            if (dateFin != null) {
                stmt.setTimestamp(paramIndex++, dateFin);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("numero_commande", rs.getString("numero_commande"));
                    row.put("date_commande", rs.getTimestamp("date_commande"));
                    row.put("montant_total", rs.getBigDecimal("montant_total"));
                    row.put("mode_paiement", rs.getString("mode_paiement"));
                    row.put("statut_paiement", rs.getString("statut_paiement"));
                    row.put("client_nom", rs.getString("client_nom"));
                    row.put("client_prenom", rs.getString("client_prenom"));
                    row.put("nom_complet", rs.getString("nom_complet"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
 // 5. Récupérer les transactions d'une caisse
    public List<TransactionCaisse> getTransactionsByCaisse(int caisseId) {
        List<TransactionCaisse> transactions = new ArrayList<>();
        
        // Récupérer les dates de la caisse
        CaisseCaissiere caisse = getCaisseById(caisseId);
        if (caisse == null) {
            System.out.println("DEBUG: Caisse avec ID " + caisseId + " non trouvée");
            return transactions;
        }
        
        Timestamp ouverture = caisse.getOuverture();
        Timestamp fermeture = caisse.getFermeture() != null ? 
                             caisse.getFermeture() : 
                             new Timestamp(System.currentTimeMillis());
        
        System.out.println("DEBUG: getTransactionsByCaisse - Caisse ID: " + caisseId);
        System.out.println("DEBUG: Date ouverture: " + ouverture);
        System.out.println("DEBUG: Date fermeture: " + fermeture);
        
        String sql = "SELECT tc.*, " + 
                     "cc.CAISSIERE_ID, cc.OUVERTURE, cc.STATUT AS caisse_statut, " +
                     "client.ID AS client_id, client.NOM AS client_nom, client.PRENOM AS client_prenom, " +
                     "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " +
                     "FROM TRANSACTION_CAISSE tc " +
                     "JOIN CAISSE_CAISSIERE cc ON tc.CAISSE_ID = cc.ID " +
                     "LEFT JOIN UTILISATEUR client ON tc.CLIENT_ID = client.ID " +
                     "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID " +
                     "WHERE tc.CAISSE_ID = ? " +
                     "AND tc.DATE_OPERATION >= ? " +
                     "AND tc.DATE_OPERATION <= ? " +
                     "ORDER BY tc.DATE_OPERATION DESC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, caisseId);
            stmt.setTimestamp(2, ouverture);
            stmt.setTimestamp(3, fermeture);
            
            System.out.println("DEBUG: Exécution requête transactions avec paramètres: caisseId=" + caisseId + 
                             ", ouverture=" + ouverture + ", fermeture=" + fermeture);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    transactions.add(mapTransactionCaisse(rs));
                    count++;
                }
                System.out.println("DEBUG: Nombre de transactions récupérées: " + count);
            }
        } catch (SQLException e) {
            System.out.println("ERREUR SQL getTransactionsByCaisse: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
	private TransactionCaisse mapTransactionCaisse(ResultSet rs) throws SQLException {
		TransactionCaisse transaction = new TransactionCaisse();
		transaction.setId(rs.getInt("ID"));
		transaction.setCaisseId(rs.getInt("CAISSE_ID"));
		transaction.setTypeOperation(rs.getString("TYPE_OPERATION"));
		transaction.setMontant(rs.getBigDecimal("MONTANT"));
		transaction.setModePaiement(rs.getString("MODE_PAIEMENT"));

		int clientId = rs.getInt("CLIENT_ID");
		if (!rs.wasNull()) {
			transaction.setClientId(clientId);

			// Mapper client
			Utilisateur client = new Utilisateur();
			client.setId(rs.getInt("client_id"));
			client.setNom(rs.getString("client_nom"));
			client.setPrenom(rs.getString("client_prenom"));
			transaction.setClient(client);
		}

		int commandeId = rs.getInt("COMMANDE_ID");
		if (!rs.wasNull()) {
			transaction.setCommandeId(commandeId);

			// Mapper commande
			Commande commande = new Commande();
			commande.setId(commandeId);
			commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
			commande.setMontantTotal(rs.getBigDecimal("commande_montant"));
			transaction.setCommande(commande);
		}

		transaction.setNotes(rs.getString("NOTES"));
		transaction.setDateOperation(rs.getTimestamp("DATE_OPERATION"));

		// Mapper caisse
		CaisseCaissiere caisse = new CaisseCaissiere();
		caisse.setId(rs.getInt("CAISSE_ID"));
		caisse.setCaissiereId(rs.getInt("CAISSIERE_ID"));
		caisse.setOuverture(rs.getTimestamp("OUVERTURE"));
		caisse.setStatut(rs.getString("caisse_statut"));
		transaction.setCaisse(caisse);

		return transaction;
	}
	
	
	
   public List<Map<String, Object>> getDepotsCompteClient(int caisseId, Timestamp dateDebut, Timestamp dateFin) {
    List<Map<String, Object>> result = new ArrayList<>();
    
    // Récupérer les dates de la caisse
    CaisseCaissiere caisse = getCaisseById(caisseId);
    if (caisse == null) {
        System.out.println("DEBUG: Caisse avec ID " + caisseId + " non trouvée dans getDepotsCompteClient");
        return result;
    }
    
    Timestamp ouverture = caisse.getOuverture();
    Timestamp fermeture = caisse.getFermeture() != null ? 
                         caisse.getFermeture() : 
                         new Timestamp(System.currentTimeMillis());
    
    System.out.println("DEBUG: getDepotsCompteClient - Caisse ID: " + caisseId);
    System.out.println("DEBUG: Date ouverture: " + ouverture);
    System.out.println("DEBUG: Date fermeture: " + fermeture);
    System.out.println("DEBUG: dateDebut param: " + dateDebut);
    System.out.println("DEBUG: dateFin param: " + dateFin);
    
    String sql = "SELECT t.*, " +
                "       c.nom as client_nom, c.prenom as client_prenom, " +
                "       CONCAT(c.nom, ' ', c.prenom) as nom_complet " +
                "FROM transaction_caisse t " +
                "LEFT JOIN utilisateur c ON t.client_id = c.id " +
                "WHERE t.caisse_id = ? " +
                "AND t.type_operation = 'DEPOT' " +
                "AND t.date_operation >= ? " +  // Date ouverture caisse
                "AND t.date_operation <= ? " +  // Date fermeture caisse
                (dateDebut != null ? "AND t.date_operation >= ? " : "") +
                (dateFin != null ? "AND t.date_operation <= ? " : "") +
                "ORDER BY t.date_operation DESC";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        int paramIndex = 1;
        stmt.setInt(paramIndex++, caisseId);
        stmt.setTimestamp(paramIndex++, ouverture);
        stmt.setTimestamp(paramIndex++, fermeture);
        
        if (dateDebut != null) {
            stmt.setTimestamp(paramIndex++, dateDebut);
        }
        if (dateFin != null) {
            stmt.setTimestamp(paramIndex++, dateFin);
        }
        
        System.out.println("DEBUG: Exécution requête dépôts avec paramètres: caisseId=" + caisseId + 
                         ", ouverture=" + ouverture + ", fermeture=" + fermeture +
                         ", dateDebut=" + dateDebut + ", dateFin=" + dateFin);
        
        try (ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("montant", rs.getBigDecimal("montant"));
                row.put("total", rs.getBigDecimal("montant"));
                row.put("mode_paiement", rs.getString("mode_paiement"));
                row.put("date_operation", rs.getTimestamp("date_operation"));
                row.put("client_nom", rs.getString("client_nom"));
                row.put("client_prenom", rs.getString("client_prenom"));
                row.put("nom_complet", rs.getString("nom_complet"));
                result.add(row);
                count++;
            }
            System.out.println("DEBUG: Nombre de dépôts récupérés: " + count);
        }
    } catch (SQLException e) {
        System.out.println("ERREUR SQL getDepotsCompteClient: " + e.getMessage());
        e.printStackTrace();
    }
    return result;
}
    // 7.2. Commandes encaissées par rôle (clients)
    public List<Map<String, Object>> getCommandesEncaisseesParClient(int caissiereId, int caisseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT " +
                     "c.CLIENT_ID, " +
                     "u.NOM, u.PRENOM, " +
                     "COUNT(c.ID) AS nombre_commandes, " +
                     "SUM(c.MONTANT_TOTAL) AS total_commandes, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN 1 ELSE 0 END) AS commandes_payees, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) AS montant_paye, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN 1 ELSE 0 END) AS commandes_non_payees, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) AS montant_non_paye " +
                     "FROM COMMANDE c " +
                     "JOIN UTILISATEUR u ON c.CLIENT_ID = u.ID " +
                     "WHERE c.CASHED_BY = ? " +
                     "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                     "AND u.ROLE = 'CLIENT' " +
                     "GROUP BY c.CLIENT_ID, u.NOM, u.PRENOM " +
                     "ORDER BY u.NOM, u.PRENOM";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            stmt.setInt(2, caisseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("clientId", rs.getInt("CLIENT_ID"));
                    row.put("nom", rs.getString("NOM"));
                    row.put("prenom", rs.getString("PRENOM"));
                    row.put("nombreCommandes", rs.getInt("nombre_commandes"));
                    row.put("totalCommandes", rs.getBigDecimal("total_commandes"));
                    row.put("commandesPayees", rs.getInt("commandes_payees"));
                    row.put("montantPaye", rs.getBigDecimal("montant_paye"));
                    row.put("commandesNonPayees", rs.getInt("commandes_non_payees"));
                    row.put("montantNonPaye", rs.getBigDecimal("montant_non_paye"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // 7.3. Commandes encaissées pour les serveuses
    public List<Map<String, Object>> getCommandesEncaisseesParServeuse(int caissiereId, int caisseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT " +
                     "c.UTILISATEUR_ID, " +
                     "u.NOM, u.PRENOM, " +
                     "COUNT(c.ID) AS nombre_commandes, " +
                     "SUM(c.MONTANT_TOTAL) AS total_commandes, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN 1 ELSE 0 END) AS commandes_payees, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) AS montant_paye, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN 1 ELSE 0 END) AS commandes_non_payees, " +
                     "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) AS montant_non_paye " +
                     "FROM COMMANDE c " +
                     "JOIN UTILISATEUR u ON c.UTILISATEUR_ID = u.ID " +
                     "WHERE c.CASHED_BY = ? " +
                     "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                     "AND u.ROLE = 'VENDEUR(EUSE)' " +
                     "AND c.CLIENT_ID != c.UTILISATEUR_ID " + // Commandes créées pour d'autres
                     "GROUP BY c.UTILISATEUR_ID, u.NOM, u.PRENOM " +
                     "ORDER BY u.NOM, u.PRENOM";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            stmt.setInt(2, caisseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("vendeuseId", rs.getInt("UTILISATEUR_ID"));
                    row.put("nom", rs.getString("NOM"));
                    row.put("prenom", rs.getString("PRENOM"));
                    row.put("nombreCommandes", rs.getInt("nombre_commandes"));
                    row.put("totalCommandes", rs.getBigDecimal("total_commandes"));
                    row.put("commandesPayees", rs.getInt("commandes_payees"));
                    row.put("montantPaye", rs.getBigDecimal("montant_paye"));
                    row.put("commandesNonPayees", rs.getInt("commandes_non_payees"));
                    row.put("montantNonPaye", rs.getBigDecimal("montant_non_paye"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
 // 7.3. Commandes encaissées pour les serveuses
// 7.3. Commandes encaissées pour les serveuses - MODIFIÉ POUR AVOIR ENTRÉE/SORTIE
public List<Map<String, Object>> getCommandesEncaisseesParServeuse(int caissiereId, int caisseId, Timestamp dateDebut, Timestamp dateFin) {
    List<Map<String, Object>> result = new ArrayList<>();
    
    // Récupérer les dates de la caisse
    CaisseCaissiere caisse = getCaisseById(caisseId);
    if (caisse == null) {
        System.out.println("DEBUG: Caisse avec ID " + caisseId + " non trouvée dans getCommandesEncaisseesParServeuse");
        return result;
    }
    
    Timestamp ouverture = caisse.getOuverture();
    Timestamp fermeture = caisse.getFermeture() != null ? 
                         caisse.getFermeture() : 
                         new Timestamp(System.currentTimeMillis());
    
    System.out.println("DEBUG: getCommandesEncaisseesParServeuse - Dates caisse: ouverture=" + ouverture + ", fermeture=" + fermeture);
    
    // Utiliser la fermeture ou l'heure actuelle si pas de fermeture
    if (fermeture == null) {
        fermeture = new Timestamp(System.currentTimeMillis());
    }
    
    String sql = "SELECT " +
                 "c.UTILISATEUR_ID as serveuse_id, " +
                 "u.NOM, u.PRENOM, " +
                 "CONCAT(u.NOM, ' ', u.PRENOM) as nom_complet, " +
                 "COUNT(c.ID) as nb_commandes, " +
                 "SUM(c.MONTANT_TOTAL) as total_commandes, " +
                 
                 // Commandes payées
                 "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN 1 ELSE 0 END) as nb_commandes_payees, " +
                 "SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) as total_paye, " +
                 "MAX(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.DATE_COMMANDE END) as derniere_commande_payee, " +
                 
                 // Commandes non payées
                 "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN 1 ELSE 0 END) as nb_commandes_non_payees, " +
                 "SUM(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END) as total_non_paye, " +
                 "MAX(CASE WHEN c.STATUT_PAIEMENT != 'PAYE' THEN c.DATE_COMMANDE END) as derniere_commande_non_payee " +
                 
                 "FROM COMMANDE c " +
                 "JOIN UTILISATEUR u ON c.UTILISATEUR_ID = u.ID " +
//                 "WHERE c.CASHED_BY = ? " +
					"WHERE c.STATUT = 'VISIBLE' " +
                 "AND c.DATE_COMMANDE >= ? " +
                 "AND c.DATE_COMMANDE <= ? " +
                 (dateDebut != null ? "AND c.DATE_COMMANDE >= ? " : "") +
                 (dateFin != null ? "AND c.DATE_COMMANDE <= ? " : "") +
//                 "AND u.ROLE = 'VENDEUR(EUSE)' " +
                 "AND (u.ID_ROLE IN (SELECT ID FROM ROLE WHERE NOM_ROLE ='VENDEUR(EUSE)')) "+
                 //"AND c.CLIENT_ID != c.UTILISATEUR_ID " + // Commandes créées pour d'autres
                 "GROUP BY c.UTILISATEUR_ID, u.NOM, u.PRENOM " +
                 "HAVING COUNT(c.ID) > 0 " +
                 "ORDER BY u.NOM, u.PRENOM";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        int paramIndex = 1;
//        stmt.setInt(paramIndex++, caissiereId);
        stmt.setTimestamp(paramIndex++, ouverture);
        stmt.setTimestamp(paramIndex++, fermeture);
        
        if (dateDebut != null) {
            stmt.setTimestamp(paramIndex++, dateDebut);
        }
        if (dateFin != null) {
            stmt.setTimestamp(paramIndex++, dateFin);
        }
        
        System.out.println("DEBUG: Exécution requête commandes serveuses avec paramètres: caissiereId=" + caissiereId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("serveuse_id", rs.getInt("serveuse_id"));
                row.put("nom", rs.getString("NOM"));
                row.put("prenom", rs.getString("PRENOM"));
                row.put("nom_complet", rs.getString("nom_complet"));
                row.put("nb_commandes", rs.getInt("nb_commandes"));
                row.put("total_commandes", rs.getBigDecimal("total_commandes"));
                
                // Commandes payées (ENTRÉE)
                row.put("nb_commandes_payees", rs.getInt("nb_commandes_payees"));
                row.put("total_paye", rs.getBigDecimal("total_paye"));
                row.put("derniere_commande_payee", rs.getTimestamp("derniere_commande_payee"));
                
                // Commandes non payées (SORTIE)
                row.put("nb_commandes_non_payees", rs.getInt("nb_commandes_non_payees"));
                row.put("total_non_paye", rs.getBigDecimal("total_non_paye"));
                row.put("derniere_commande_non_payee", rs.getTimestamp("derniere_commande_non_payee"));
                
                result.add(row);
                count++;
            }
            System.out.println("DEBUG: Nombre de serveuses avec commandes: " + count);
        }
    } catch (SQLException e) {
        System.out.println("ERREUR SQL getCommandesEncaisseesParServeuse: " + e.getMessage());
        e.printStackTrace();
    }
    return result;
}

// 7.4. Commandes créées par la caissière elle-même (COMPTOIR) - MODIFIÉ
public List<Map<String, Object>> getCommandesCaisseDirecte(int caissiereId, int caisseId, Timestamp dateDebut, Timestamp dateFin) {
    List<Map<String, Object>> result = new ArrayList<>();
    
    // Récupérer les dates de la caisse
    CaisseCaissiere caisse = getCaisseById(caisseId);
    if (caisse == null) {
        System.out.println("DEBUG: Caisse avec ID " + caisseId + " non trouvée dans getCommandesCaisseDirecte");
        return result;
    }
    
    Timestamp ouverture = caisse.getOuverture();
    Timestamp fermeture = caisse.getFermeture() != null ? 
                         caisse.getFermeture() : 
                         new Timestamp(System.currentTimeMillis());
    
    System.out.println("DEBUG: getCommandesCaisseDirecte - Dates caisse: ouverture=" + ouverture + ", fermeture=" + fermeture);
    
    // Utiliser la fermeture ou l'heure actuelle si pas de fermeture
    if (fermeture == null) {
        fermeture = new Timestamp(System.currentTimeMillis());
    }
    
    String sql = "SELECT " +
                 "c.ID as commande_id, c.NUMERO_COMMANDE, c.DATE_COMMANDE, " +
                 "c.MONTANT_TOTAL, c.MODE_PAIEMENT, c.STATUT_PAIEMENT, " +
                 "c.MONTANT_PAYE, c.NOTES, " +
                 "u.NOM AS client_nom, u.PRENOM AS client_prenom " +
                 "FROM COMMANDE c " +
                 "LEFT JOIN UTILISATEUR u ON c.CLIENT_ID = u.ID " +
                 "WHERE c.UTILISATEUR_ID = ? " + // Commandes créées par la caissière elle-même
                 "AND c.DATE_COMMANDE >= ? " +
                 "AND c.DATE_COMMANDE <= ? " +
                 (dateDebut != null ? "AND c.DATE_COMMANDE >= ? " : "") +
                 (dateFin != null ? "AND c.DATE_COMMANDE <= ? " : "") +
                 "ORDER BY c.DATE_COMMANDE DESC";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        int paramIndex = 1;
        stmt.setInt(paramIndex++, caissiereId);
        stmt.setTimestamp(paramIndex++, ouverture);
        stmt.setTimestamp(paramIndex++, fermeture);
        
        if (dateDebut != null) {
            stmt.setTimestamp(paramIndex++, dateDebut);
        }
        if (dateFin != null) {
            stmt.setTimestamp(paramIndex++, dateFin);
        }
        
        System.out.println("DEBUG: Exécution requête commandes directes avec paramètres: caissiereId=" + caissiereId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("commande_id", rs.getInt("commande_id"));
                row.put("numero_commande", rs.getString("NUMERO_COMMANDE"));
                row.put("date_commande", rs.getTimestamp("DATE_COMMANDE"));
                row.put("montant_total", rs.getBigDecimal("MONTANT_TOTAL"));
                row.put("mode_paiement", rs.getString("MODE_PAIEMENT"));
                row.put("statut_paiement", rs.getString("STATUT_PAIEMENT"));
                row.put("montant_paye", rs.getBigDecimal("MONTANT_PAYE"));
                row.put("notes", rs.getString("NOTES"));
                row.put("client_nom", rs.getString("client_nom"));
                row.put("client_prenom", rs.getString("client_prenom"));
                result.add(row);
                count++;
            }
            System.out.println("DEBUG: Nombre de commandes directes récupérées: " + count);
        }
    } catch (SQLException e) {
        System.out.println("ERREUR SQL getCommandesCaisseDirecte: " + e.getMessage());
        e.printStackTrace();
    }
    return result;
}

// 7.5. Méthode pour récupérer le total des shots de la caissière
public BigDecimal getTotalShotsCaissiere(int caissiereId) {
    BigDecimal totalShots = BigDecimal.ZERO;
    String sql = "SELECT COALESCE(SUM(MONTANT_SHOT), 0) as total_shots " +
                 "FROM CAISSE_CAISSIERE " +
                 "WHERE CAISSIERE_ID = ? " +
                 "AND SHOT = 1 " +
                 "AND STATUT = 'FERMEE'";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, caissiereId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalShots = rs.getBigDecimal("total_shots");
                if (totalShots == null) {
                    totalShots = BigDecimal.ZERO;
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("ERREUR SQL getTotalShotsCaissiere: " + e.getMessage());
        e.printStackTrace();
    }
    
    System.out.println("DEBUG: Total shots pour caissiere " + caissiereId + ": " + totalShots);
    return totalShots;
}
    
    // 7.5. Calcul du contrôle à remettre
    public Map<String, BigDecimal> calculerControleARemettre(int caissiereId, int caisseId, boolean shot) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        // 1. Total des ventes (commandes payées)
        String sqlVentes = "SELECT " +
                          "COALESCE(SUM(CASE WHEN c.STATUT_PAIEMENT = 'PAYE' THEN c.MONTANT_TOTAL ELSE 0 END), 0) AS total_ventes " +
                          "FROM COMMANDE c " +
                          "WHERE c.CASHED_BY = ? " +
                          "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?)";
        
        // 2. Total des crédits payés
        String sqlCredits = "SELECT " +
                           "COALESCE(SUM(t.MONTANT), 0) AS total_credits " +
                           "FROM TRANSACTION_COMPTE t " +
                           "JOIN TYPE_TRANSACTION tt ON t.TYPE_TRANSACTION_ID = tt.ID " +
                           "WHERE t.CAISSIERE_ID = ? " +
                           "AND DATE(t.DATE_TRANSACTION) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                           "AND tt.CODE = 'DEPENSE'";
        
        // 3. Commandes en SOLDE
        String sqlSoldes = "SELECT " +
                          "COALESCE(SUM(c.MONTANT_TOTAL), 0) AS total_solde " +
                          "FROM COMMANDE c " +
                          "WHERE c.CASHED_BY = ? " +
                          "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                          "AND c.MODE_PAIEMENT = 'SOLDE' " +
                          "AND c.STATUT_PAIEMENT = 'PAYE'";
        
        // 4. Commandes non payées
        String sqlNonPayees = "SELECT " +
                             "COALESCE(SUM(c.MONTANT_TOTAL), 0) AS total_non_paye " +
                             "FROM COMMANDE c " +
                             "WHERE c.CASHED_BY = ? " +
                             "AND DATE(c.CREATED_AT) >= (SELECT DATE(OUVERTURE) FROM CAISSE_CAISSIERE WHERE ID = ?) " +
                             "AND c.STATUT_PAIEMENT != 'PAYE'";
        
        try (Connection conn = getConnection()) {
            // Total ventes
            try (PreparedStatement stmt = conn.prepareStatement(sqlVentes)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.put("totalVentes", rs.getBigDecimal("total_ventes"));
                    }
                }
            }
            
            // Total crédits
            try (PreparedStatement stmt = conn.prepareStatement(sqlCredits)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.put("totalCredits", rs.getBigDecimal("total_credits"));
                    }
                }
            }
            
            // Total soldes
            try (PreparedStatement stmt = conn.prepareStatement(sqlSoldes)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.put("totalSoldes", rs.getBigDecimal("total_solde"));
                    }
                }
            }
            
            // Total non payées
            try (PreparedStatement stmt = conn.prepareStatement(sqlNonPayees)) {
                stmt.setInt(1, caissiereId);
                stmt.setInt(2, caisseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.put("totalNonPayees", rs.getBigDecimal("total_non_paye"));
                    }
                }
            }
            
            // Calcul du contrôle
            BigDecimal totalVentes = result.getOrDefault("totalVentes", BigDecimal.ZERO);
            BigDecimal totalCredits = result.getOrDefault("totalCredits", BigDecimal.ZERO);
            BigDecimal totalSoldes = result.getOrDefault("totalSoldes", BigDecimal.ZERO);
            
            BigDecimal controle = totalVentes.subtract(totalCredits).subtract(totalSoldes);
            
            // Si SHOT = true, on commence à négatif
            if (shot) {
                controle = controle.negate();
            }
            
            result.put("controleARemettre", controle);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // 8. Dernière caisse fermée
    public CaisseCaissiere getDerniereCaisseFermee(int caissiereId) {
        String sql = "SELECT cc.*, u.NOM, u.PRENOM " +
                     "FROM CAISSE_CAISSIERE cc " +
                     "JOIN UTILISATEUR u ON cc.CAISSIERE_ID = u.ID " +
                     "WHERE cc.CAISSIERE_ID = ? AND cc.STATUT = 'FERMEE' " +
                     "ORDER BY cc.FERMETURE DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caissiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCaisseCaissiere(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Map<String, Object>> getCommandesEncaisseesParClient(int caissiereId, Timestamp dateDebut, Timestamp dateFin) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Récupérer les informations de caisse de la caissière
        String caisseSql = "SELECT OUVERTURE, FERMETURE FROM CAISSE_CAISSIERE " +
                          "WHERE CAISSIERE_ID = ? AND STATUT = ? " +
                          "ORDER BY OUVERTURE DESC LIMIT 1";
        
        try (Connection conn = getConnection()) {
            // D'abord, vérifier si la caissière a une caisse ouverte
            CaisseCaissiere caisse = getCaisseOuverte(caissiereId);
            Timestamp ouverture = null;
            Timestamp fermeture = null;
            
            if (caisse != null) {
                // Si caisse ouverte, utiliser ouverture -> maintenant
                ouverture = caisse.getOuverture();
                fermeture = new Timestamp(System.currentTimeMillis());
            } else {
                // Sinon, chercher la dernière caisse fermée
                try (PreparedStatement caisseStmt = conn.prepareStatement(caisseSql)) {
                    caisseStmt.setInt(1, caissiereId);
                    caisseStmt.setString(2, "FERMEE");
                    try (ResultSet rs = caisseStmt.executeQuery()) {
                        if (rs.next()) {
                            ouverture = rs.getTimestamp("OUVERTURE");
                            fermeture = rs.getTimestamp("FERMETURE");
                        }
                    }
                }
            }
            
            if (ouverture == null) {
                return result; // Pas de caisse trouvée
            }
            
            // Construire la requête principale pour les commandes
            String sql = "SELECT c.*, " +
                        "       u.nom as client_nom, u.prenom as client_prenom, " +
                        "       CONCAT(u.nom, ' ', u.prenom) as nom_complet, " +
                        "       c.montant_total as total " +
                        "FROM commande c " +
                        "JOIN utilisateur u ON c.client_id = u.id " +
                        "WHERE c.cashed_by = ? " +
                        "AND c.statut_paiement = 'PAYE' " +
                        "AND c.statut = 'VISIBLE' " +
                        "AND c.date_commande >= ? " +
                        "AND c.date_commande <= ? ";
            
            // Ajouter les filtres de dates supplémentaires si fournis
            if (dateDebut != null) {
                sql += "AND c.date_commande >= ? ";
            }
            if (dateFin != null) {
                sql += "AND c.date_commande <= ? ";
            }
            
            sql += "ORDER BY c.date_commande DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                stmt.setInt(paramIndex++, caissiereId);
                stmt.setTimestamp(paramIndex++, ouverture);
                stmt.setTimestamp(paramIndex++, fermeture);
                
                if (dateDebut != null) {
                    stmt.setTimestamp(paramIndex++, dateDebut);
                }
                if (dateFin != null) {
                    stmt.setTimestamp(paramIndex++, dateFin);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("commande_id", rs.getInt("id"));
                        row.put("numero_commande", rs.getString("numero_commande"));
                        row.put("date_commande", rs.getTimestamp("date_commande"));
                        row.put("montant_total", rs.getBigDecimal("montant_total"));
                        row.put("total", rs.getBigDecimal("montant_total"));
                        row.put("mode_paiement", rs.getString("mode_paiement"));
                        row.put("statut_paiement", rs.getString("statut_paiement"));
                        row.put("client_nom", rs.getString("client_nom"));
                        row.put("client_prenom", rs.getString("client_prenom"));
                        row.put("nom_complet", rs.getString("nom_complet"));
                        result.add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Map<String, Object>> getCommandesEncaisseesParServeuse(int caissiereId, Timestamp dateDebut, Timestamp dateFin) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Récupérer les informations de caisse de la caissière
        String caisseSql = "SELECT OUVERTURE, FERMETURE FROM CAISSE_CAISSIERE " +
                          "WHERE CAISSIERE_ID = ? AND STATUT = ? " +
                          "ORDER BY OUVERTURE DESC LIMIT 1";
        
        try (Connection conn = getConnection()) {
            // D'abord, vérifier si la caissière a une caisse ouverte
            CaisseCaissiere caisse = getCaisseOuverte(caissiereId);
            Timestamp ouverture = null;
            Timestamp fermeture = null;
            
            if (caisse != null) {
                // Si caisse ouverte, utiliser ouverture -> maintenant
                ouverture = caisse.getOuverture();
                fermeture = new Timestamp(System.currentTimeMillis());
            } else {
                // Sinon, chercher la dernière caisse fermée
                try (PreparedStatement caisseStmt = conn.prepareStatement(caisseSql)) {
                    caisseStmt.setInt(1, caissiereId);
                    caisseStmt.setString(2, "FERMEE");
                    try (ResultSet rs = caisseStmt.executeQuery()) {
                        if (rs.next()) {
                            ouverture = rs.getTimestamp("OUVERTURE");
                            fermeture = rs.getTimestamp("FERMETURE");
                        }
                    }
                }
            }
            
            if (ouverture == null) {
                return result; // Pas de caisse trouvée
            }
            
            // Construire la requête principale
            String sql = "SELECT u.id as serveuse_id, " +
                        "       u.nom, u.prenom, " +
                        "       CONCAT(u.nom, ' ', u.prenom) as nom_complet, " +
                        "       COUNT(c.id) as nb_commandes, " +
                        "       SUM(CASE WHEN c.statut_paiement = 'PAYE' THEN c.montant_total ELSE 0 END) as total_paye, " +
                        "       SUM(CASE WHEN c.statut_paiement != 'PAYE' THEN c.montant_total ELSE 0 END) as total_non_paye, " +
                        "       SUM(c.montant_total) as total_commandes " +
                        "FROM commande c " +
                        "JOIN utilisateur u ON c.utilisateur_id = u.id " +
                        "WHERE c.cashed_by = ? " +
                        "AND c.statut = 'VISIBLE' " +
                        "AND c.date_commande >= ? " +
                        "AND c.date_commande <= ? ";
            
            // Ajouter les filtres de dates supplémentaires si fournis
            if (dateDebut != null) {
                sql += "AND c.date_commande >= ? ";
            }
            if (dateFin != null) {
                sql += "AND c.date_commande <= ? ";
            }
            
            sql += "GROUP BY u.id, u.nom, u.prenom " +
                   "ORDER BY total_commandes DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                stmt.setInt(paramIndex++, caissiereId);
                stmt.setTimestamp(paramIndex++, ouverture);
                stmt.setTimestamp(paramIndex++, fermeture);
                
                if (dateDebut != null) {
                    stmt.setTimestamp(paramIndex++, dateDebut);
                }
                if (dateFin != null) {
                    stmt.setTimestamp(paramIndex++, dateFin);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("serveuse_id", rs.getInt("serveuse_id"));
                        row.put("nom", rs.getString("nom"));
                        row.put("prenom", rs.getString("prenom"));
                        row.put("nom_complet", rs.getString("nom_complet"));
                        row.put("nb_commandes", rs.getInt("nb_commandes"));
                        row.put("total_paye", rs.getBigDecimal("total_paye"));
                        row.put("total_non_paye", rs.getBigDecimal("total_non_paye"));
                        row.put("total_commandes", rs.getBigDecimal("total_commandes"));
                        result.add(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    // Helper method
    private CaisseCaissiere mapCaisseCaissiere(ResultSet rs) throws SQLException {
        CaisseCaissiere caisse = new CaisseCaissiere();
        caisse.setId(rs.getInt("ID"));
        caisse.setCaissiereId(rs.getInt("CAISSIERE_ID"));
        caisse.setOuverture(rs.getTimestamp("OUVERTURE"));
        caisse.setFermeture(rs.getTimestamp("FERMETURE"));
        caisse.setSoldeInitial(rs.getBigDecimal("SOLDE_INITIAL"));
        caisse.setSoldeFinal(rs.getBigDecimal("SOLDE_FINAL"));
        caisse.setStatut(rs.getString("STATUT"));
        caisse.setShot(rs.getBoolean("SHOT"));
        caisse.setMontantShot(rs.getBigDecimal("MONTANT_SHOT"));
        
        // Mapper la caissière
        Utilisateur caissiere = new Utilisateur();
        caissiere.setId(rs.getInt("CAISSIERE_ID"));
        caissiere.setNom(rs.getString("NOM"));
        caissiere.setPrenom(rs.getString("PRENOM"));
        caisse.setCaissiere(caissiere);
        
        return caisse;
    }
    
}