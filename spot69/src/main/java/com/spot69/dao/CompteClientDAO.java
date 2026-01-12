package com.spot69.dao;

import com.spot69.model.CompteClient;
import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompteClientDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // 1. Créer un compte pour un client (avec limite crédit)
    public int creerCompteClient(CompteClient compte) {
        String sql = "INSERT INTO COMPTE_CLIENT (CLIENT_ID, SOLDE, LIMITE_CREDIT, DATE_CREATION, DATE_MAJ) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, compte.getClientId());
            stmt.setBigDecimal(2, compte.getSolde());
            stmt.setBigDecimal(3, compte.getLimiteCredit());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, now);
            stmt.setTimestamp(5, now);
            
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

    // 2. Récupérer un compte par ID client
    public CompteClient getCompteByClientId(int clientId) {
        String sql = "SELECT cc.*, " +
                     "u.ID AS u_id, u.NOM AS u_nom, u.PRENOM AS u_prenom, u.EMAIL AS u_email, " +
                     "u.LOGIN AS u_login, u.STATUT AS u_statut " +
                     "FROM COMPTE_CLIENT cc " +
                     "JOIN UTILISATEUR u ON cc.CLIENT_ID = u.ID " +
                     "WHERE cc.CLIENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCompteClient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 3. Récupérer un compte par ID
    public CompteClient getCompteById(int id) {
        String sql = "SELECT cc.*, " +
                     "u.ID AS u_id, u.NOM AS u_nom, u.PRENOM AS u_prenom, u.EMAIL AS u_email, " +
                     "u.LOGIN AS u_login, u.STATUT AS u_statut " +
                     "FROM COMPTE_CLIENT cc " +
                     "JOIN UTILISATEUR u ON cc.CLIENT_ID = u.ID " +
                     "WHERE cc.ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCompteClient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Mettre à jour le solde
//    public boolean mettreAJourSolde(int compteId, BigDecimal nouveauSolde) {
//        String sql = "UPDATE COMPTE_CLIENT SET SOLDE = ?, DATE_MAJ = ? WHERE ID = ?";
//        
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            
//            stmt.setBigDecimal(1, nouveauSolde);
//            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//            stmt.setInt(3, compteId);
//            
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

 // 4. Mettre à jour le solde avec gestion du crédit
//    public boolean mettreAJourSolde(int compteId, BigDecimal nouveauSolde) {
//        // D'abord, récupérer l'ancien solde et la limite de crédit
//        String selectSql = "SELECT SOLDE, LIMITE_CREDIT FROM COMPTE_CLIENT WHERE ID = ?";
//        String updateSql = "UPDATE COMPTE_CLIENT SET SOLDE = ?, DATE_MAJ = ? WHERE ID = ?";
//        
//        try (Connection conn = getConnection();
//             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
//             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
//            
//            // 1. Récupérer l'ancien état
//            selectStmt.setInt(1, compteId);
//            ResultSet rs = selectStmt.executeQuery();
//            
//            if (rs.next()) {
//                BigDecimal ancienSolde = rs.getBigDecimal("SOLDE");
//                BigDecimal limiteCredit = rs.getBigDecimal("LIMITE_CREDIT");
//                
//                System.out.println("=== MISE À JOUR DE SOLDE ===");
//                System.out.println("Ancien solde: " + String.format("%.2f HTG", ancienSolde));
//                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSolde));
//                System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
//                
//                // 2. Calculer la différence (montant du dépôt ou retrait)
//                BigDecimal difference = nouveauSolde.subtract(ancienSolde);
//                
//                // 3. Si c'est un dépôt (nouveauSolde > ancienSolde) ET ancien solde était négatif
//                // Cela signifie qu'on rembourse du crédit utilisé
//                if (difference.compareTo(BigDecimal.ZERO) > 0 && ancienSolde.compareTo(BigDecimal.ZERO) < 0) {
//                    // Le montant déposé est utilisé pour rembourser le crédit
//                    BigDecimal montantARembourser = difference.min(ancienSolde.abs());
//                    System.out.println("Dépôt utilisé pour rembourser crédit: " + 
//                                     String.format("%.2f HTG", montantARembourser));
//                    
//                    // Calculer le nouveau crédit disponible
//                    BigDecimal creditDejaUtiliseAvant = ancienSolde.abs();
//                    BigDecimal creditDejaUtiliseApres = creditDejaUtiliseAvant.subtract(montantARembourser);
//                    
//                    System.out.println("Crédit utilisé avant: " + 
//                                     String.format("%.2f HTG", creditDejaUtiliseAvant));
//                    System.out.println("Crédit utilisé après: " + 
//                                     String.format("%.2f HTG", creditDejaUtiliseApres));
//                    System.out.println("Crédit disponible avant: " + 
//                                     String.format("%.2f HTG", limiteCredit.subtract(creditDejaUtiliseAvant)));
//                    System.out.println("Crédit disponible après: " + 
//                                     String.format("%.2f HTG", limiteCredit.subtract(creditDejaUtiliseApres)));
//                }
//                
//                // 4. Mettre à jour le solde
//                updateStmt.setBigDecimal(1, nouveauSolde);
//                updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//                updateStmt.setInt(3, compteId);
//                
//                boolean success = updateStmt.executeUpdate() > 0;
//                
////                if (success) {
////                    // 5. Créer une transaction pour enregistrer l'opération
////                    creerTransactionPourDepot(conn, compteId, ancienSolde, nouveauSolde, 
////                                            "Mise à jour solde", userId);
////                }
//                
//                return success;
//            }
//            
//            return false;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    // Méthode pour créer une transaction de dépôt
    private void creerTransactionPourDepot(Connection conn, int compteId, 
                                           BigDecimal soldeAvant, BigDecimal soldeApres,
                                           String notes, int userId) throws SQLException {
        String sql = "INSERT INTO TRANSACTION_COMPTE (COMPTE_CLIENT_ID, MONTANT, " +
                     "SOLDE_AVANT, SOLDE_APRES, NOTES, DATE_TRANSACTION, " +
                     "TYPE_TRANSACTION_ID, CAISSIERE_ID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            BigDecimal montant = soldeApres.subtract(soldeAvant);
            
            stmt.setInt(1, compteId);
            stmt.setBigDecimal(2, montant);
            stmt.setBigDecimal(3, soldeAvant);
            stmt.setBigDecimal(4, soldeApres);
            stmt.setString(5, notes);
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(7, 1); // Type transaction = DÉPÔT
            stmt.setInt(8, userId); // ID du caissier
            
            stmt.executeUpdate();
        }
    }
    // 5. Mettre à jour la limite de crédit
    public boolean mettreAJourLimiteCredit(int compteId, BigDecimal nouvelleLimite) {
        String sql = "UPDATE COMPTE_CLIENT SET LIMITE_CREDIT = ?, DATE_MAJ = ? WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, nouvelleLimite);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, compteId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
 // 4. Mettre à jour le solde avec gestion du crédit et remboursement automatique
    public boolean mettreAJourSolde(int compteId, BigDecimal nouveauSolde) {
        // D'abord, récupérer l'ancien solde et la limite de crédit
        String selectSql = "SELECT SOLDE, LIMITE_CREDIT, CLIENT_ID FROM COMPTE_CLIENT WHERE ID = ?";
        String updateSql = "UPDATE COMPTE_CLIENT SET SOLDE = ?, DATE_MAJ = ? WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            // 1. Récupérer l'ancien état
            selectStmt.setInt(1, compteId);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal ancienSolde = rs.getBigDecimal("SOLDE");
                BigDecimal limiteCredit = rs.getBigDecimal("LIMITE_CREDIT");
                int clientId = rs.getInt("CLIENT_ID");
                
                System.out.println("=== MISE À JOUR DE SOLDE ===");
                System.out.println("Client ID: " + clientId);
                System.out.println("Ancien solde: " + String.format("%.2f HTG", ancienSolde));
                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSolde));
                System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
                
                // 2. Calculer la différence (montant du dépôt ou retrait)
                BigDecimal difference = nouveauSolde.subtract(ancienSolde);
                
                // 3. Si c'est un dépôt (nouveauSolde > ancienSolde) ET ancien solde était négatif
                // Cela signifie qu'on rembourse du crédit utilisé
                if (difference.compareTo(BigDecimal.ZERO) > 0 && ancienSolde.compareTo(BigDecimal.ZERO) < 0) {
                    // Le montant déposé est utilisé pour rembourser le crédit
                    BigDecimal montantARembourser = difference.min(ancienSolde.abs());
                    System.out.println("Dépôt utilisé pour rembourser crédit: " + 
                                     String.format("%.2f HTG", montantARembourser));
                    
                    // Calculer le nouveau crédit disponible
                    BigDecimal creditDejaUtiliseAvant = ancienSolde.abs();
                    BigDecimal creditDejaUtiliseApres = creditDejaUtiliseAvant.subtract(montantARembourser);
                    
                    System.out.println("Crédit utilisé avant: " + 
                                     String.format("%.2f HTG", creditDejaUtiliseAvant));
                    System.out.println("Crédit utilisé après: " + 
                                     String.format("%.2f HTG", creditDejaUtiliseApres));
                    System.out.println("Crédit disponible avant: " + 
                                     String.format("%.2f HTG", limiteCredit.subtract(creditDejaUtiliseAvant)));
                    System.out.println("Crédit disponible après: " + 
                                     String.format("%.2f HTG", limiteCredit.subtract(creditDejaUtiliseApres)));
                    
                    // 4. Si du crédit a été remboursé, mettre à jour les crédits en attente
                    if (montantARembourser.compareTo(BigDecimal.ZERO) > 0) {
                    	// DAO
                    	            CreditDAO creditDAO = new CreditDAO();
                    	            int montantInt = montantARembourser.intValue();
                    	            boolean success = creditDAO.payerCreditGlobal(clientId, montantInt);

//                        payerCreditGlobal(, );
                        System.out.println("Crédits mis à jour pour le client #" + clientId);
                    }
                }
                
                // 5. Mettre à jour le solde
                updateStmt.setBigDecimal(1, nouveauSolde);
                updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                updateStmt.setInt(3, compteId);
                
                boolean success = updateStmt.executeUpdate() > 0;
                
                return success;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. Récupérer tous les comptes
    public List<CompteClient> getAllComptes() {
        List<CompteClient> comptes = new ArrayList<>();
        String sql = "SELECT cc.*, " +
                     "u.ID AS u_id, u.NOM AS u_nom, u.PRENOM AS u_prenom, u.EMAIL AS u_email, " +
                     "u.LOGIN AS u_login, u.STATUT AS u_statut " +
                     "FROM COMPTE_CLIENT cc " +
                     "JOIN UTILISATEUR u ON cc.CLIENT_ID = u.ID " +
                     "ORDER BY cc.DATE_MAJ DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                comptes.add(mapCompteClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comptes;
    }

    // 7. Initialiser les comptes pour tous les clients existants
    public void initialiserComptesClients() {
        String sql = "INSERT INTO COMPTE_CLIENT (CLIENT_ID, SOLDE, LIMITE_CREDIT) " +
                     "SELECT ID, 0.00, 0.00 FROM UTILISATEUR u " +
                     "WHERE u.STATUT = 'VISIBLE' " +
                     "AND u.ID_ROLE IN (SELECT ID FROM ROLE WHERE NOM_ROLE LIKE '%CLIENT%') " +
                     "AND NOT EXISTS (SELECT 1 FROM COMPTE_CLIENT cc WHERE cc.CLIENT_ID = u.ID)";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rows = stmt.executeUpdate(sql);
            System.out.println(rows + " comptes clients initialisés.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 8. Vérifier si une transaction est possible
    public boolean peutPayer(int clientId, BigDecimal montant) {
        String sql = "SELECT (SOLDE + COALESCE(LIMITE_CREDIT, 0)) as solde_disponible " +
                     "FROM COMPTE_CLIENT WHERE CLIENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal soldeDisponible = rs.getBigDecimal("solde_disponible");
                    if (soldeDisponible != null) {
                        return soldeDisponible.compareTo(montant) >= 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 9. Récupérer le solde disponible
    public BigDecimal getSoldeDisponible(int clientId) {
        String sql = "SELECT (SOLDE + COALESCE(LIMITE_CREDIT, 0)) as solde_disponible " +
                     "FROM COMPTE_CLIENT WHERE CLIENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("solde_disponible");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // Helper pour mapper ResultSet -> CompteClient
    private CompteClient mapCompteClient(ResultSet rs) throws SQLException {
        CompteClient compte = new CompteClient();
        compte.setId(rs.getInt("ID"));
        compte.setClientId(rs.getInt("CLIENT_ID"));
        compte.setSolde(rs.getBigDecimal("SOLDE"));
        compte.setLimiteCredit(rs.getBigDecimal("LIMITE_CREDIT"));
        compte.setDateCreation(rs.getTimestamp("DATE_CREATION"));
        compte.setDateMaj(rs.getTimestamp("DATE_MAJ"));
        
        // Mapper l'utilisateur
        Utilisateur client = new Utilisateur();
        client.setId(rs.getInt("u_id"));
        client.setNom(rs.getString("u_nom"));
        client.setPrenom(rs.getString("u_prenom"));
        client.setEmail(rs.getString("u_email"));
        client.setLogin(rs.getString("u_login"));
        client.setStatut(rs.getString("u_statut"));
        compte.setClient(client);
        
        return compte;
    }
}