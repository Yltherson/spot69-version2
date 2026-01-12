package com.spot69.dao;

import com.spot69.model.TransactionCompte;
import com.spot69.model.CompteClient;
import com.spot69.model.Notification;
import com.spot69.model.TypeTransaction;
import com.spot69.model.Utilisateur;
import com.spot69.model.Commande;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionCompteDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // 1. Créer une transaction (avec mise à jour automatique du solde via trigger)
//    public int creerTransaction(TransactionCompte transaction) {
//        String sql = "INSERT INTO TRANSACTION_COMPTE " +
//                     "(COMPTE_CLIENT_ID, TYPE_TRANSACTION_ID, MONTANT, SOLDE_AVANT, SOLDE_APRES, " +
//                     "CAISSIERE_ID, COMMANDE_ID, RESERVATION_EVENEMENT_ID, NOTES, DATE_TRANSACTION) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            
//            stmt.setInt(1, transaction.getCompteClientId());
//            stmt.setInt(2, transaction.getTypeTransactionId());
//            stmt.setBigDecimal(3, transaction.getMontant());
//            stmt.setBigDecimal(4, transaction.getSoldeAvant());
//            stmt.setBigDecimal(5, transaction.getSoldeApres());
//            stmt.setInt(6, transaction.getCaissiereId());
//            
//            if (transaction.getCommandeId() != null) {
//                stmt.setInt(7, transaction.getCommandeId());
//            } else {
//                stmt.setNull(7, Types.INTEGER);
//            }
//            
//            if (transaction.getReservationEvenementId() != null) {
//                stmt.setInt(10, transaction.getReservationEvenementId());
//            } else {
//                stmt.setNull(10, Types.INTEGER);
//            }
//            
//            stmt.setString(8, transaction.getNotes());
//            stmt.setTimestamp(9, transaction.getDateTransaction() != null ? 
//                transaction.getDateTransaction() : new Timestamp(System.currentTimeMillis()));
//            
//            int affectedRows = stmt.executeUpdate();
//            if (affectedRows > 0) {
//                ResultSet rs = stmt.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return -1;
//    }
    public int creerTransaction(TransactionCompte transaction) {
        String sql = "INSERT INTO TRANSACTION_COMPTE " +
                     "(COMPTE_CLIENT_ID, TYPE_TRANSACTION_ID, MONTANT, SOLDE_AVANT, SOLDE_APRES, " +
                     "CAISSIERE_ID, COMMANDE_ID, RESERVATION_EVENEMENT_ID, NOTES, DATE_TRANSACTION) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getCompteClientId());
            stmt.setInt(2, transaction.getTypeTransactionId());
            stmt.setBigDecimal(3, transaction.getMontant());
            stmt.setBigDecimal(4, transaction.getSoldeAvant());
            stmt.setBigDecimal(5, transaction.getSoldeApres());
            stmt.setInt(6, transaction.getCaissiereId());
            
            // CORRECTION ICI : Réservation d'événement
            if (transaction.getReservationEvenementId() != null) {
                stmt.setInt(8, transaction.getReservationEvenementId()); // COLONNE 8
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            // Commande (colonne 7)
            if (transaction.getCommandeId() != null) {
                stmt.setInt(7, transaction.getCommandeId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setString(9, transaction.getNotes());
            stmt.setTimestamp(10, transaction.getDateTransaction() != null ? 
                transaction.getDateTransaction() : new Timestamp(System.currentTimeMillis()));
            
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
    
 // Dans TransactionCompteDAO
    public List<TransactionCompte> getTransactionsReservation(int utilisateurId, int reservationId) {
        List<TransactionCompte> transactions = new ArrayList<>();
        String sql = "SELECT tc.*, " +
                     "cc.CLIENT_ID, cc.SOLDE AS compte_solde, " +
                     "tt.CODE AS type_code, tt.LIBELLE AS type_libelle, " +
                     "caissier.ID AS caissier_id, caissier.NOM AS caissier_nom, caissier.PRENOM AS caissier_prenom, " +
                     "re.ID AS reservation_id, re.MONTANT_TOTAL AS reservation_montant " +
                     "FROM TRANSACTION_COMPTE tc " +
                     "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
                     "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
                     "LEFT JOIN UTILISATEUR caissier ON tc.CAISSIERE_ID = caissier.ID " +
                     "LEFT JOIN RESERVATION_EVENEMENT re ON tc.RESERVATION_EVENEMENT_ID = re.ID " +
                     "WHERE cc.CLIENT_ID = ? " +
                     "AND tc.RESERVATION_EVENEMENT_ID = ? " +
                     "ORDER BY tc.DATE_TRANSACTION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setInt(2, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionCompte(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
 // Dans TransactionCompteDAO.java
    public Map<String, Object> verifierTransaction(int transactionId) {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT tc.ID, tc.RESERVATION_EVENEMENT_ID, tc.MONTANT, " +
                     "tc.NOTES, tc.DATE_TRANSACTION, " +
                     "tt.CODE as type_code, tt.LIBELLE as type_libelle " +
                     "FROM TRANSACTION_COMPTE tc " +
                     "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
                     "WHERE tc.ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result.put("success", true);
                    result.put("transactionId", rs.getInt("ID"));
                    result.put("reservationEvenementId", rs.getInt("RESERVATION_EVENEMENT_ID"));
                    result.put("montant", rs.getBigDecimal("MONTANT"));
                    result.put("notes", rs.getString("NOTES"));
                    result.put("dateTransaction", rs.getTimestamp("DATE_TRANSACTION"));
                    result.put("typeCode", rs.getString("type_code"));
                    result.put("typeLibelle", rs.getString("type_libelle"));
                } else {
                    result.put("success", false);
                    result.put("error", "Transaction non trouvée");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Erreur lors de la vérification: " + e.getMessage());
        }
        
        return result;
    }

    // 2. Effectuer un dépôt
//    public boolean effectuerDepot(int clientId, BigDecimal montant, int caissiereId, String notes) {
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            
//            // Récupérer le compte
//            CompteClientDAO compteDAO = new CompteClientDAO();
//            CompteClient compte = compteDAO.getCompteByClientId(clientId);
//            
//            if (compte == null) {
//                // Créer le compte s'il n'existe pas
//                compte = new CompteClient();
//                compte.setClientId(clientId);
//                compte.setSolde(BigDecimal.ZERO);
//                int compteId = compteDAO.creerCompteClient(compte);
//                if (compteId == -1) {
//                    conn.rollback();
//                    return false;
//                }
//                compte.setId(compteId);
//            }
//            
//            // Créer la transaction
//            TransactionCompte transaction = new TransactionCompte();
//            transaction.setCompteClientId(compte.getId());
//            transaction.setTypeTransactionId(getTypeTransactionId("DEPOT"));
//            transaction.setMontant(montant);
//            transaction.setSoldeAvant(compte.getSolde());
//            transaction.setSoldeApres(compte.getSolde().add(montant));
//            transaction.setCaissiereId(caissiereId);
//            transaction.setNotes(notes);
//            transaction.setDateTransaction(new Timestamp(System.currentTimeMillis()));
//            
//            int transactionId = creerTransaction(transaction);
//            if (transactionId == -1) {
//                conn.rollback();
//                return false;
//            }
//            
//            conn.commit();
//            return true;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    public boolean effectuerDepot(int clientId, BigDecimal montant, int caissiereId, String notes) {
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            
//            // Récupérer le compte
//            CompteClientDAO compteDAO = new CompteClientDAO();
//            CompteClient compte = compteDAO.getCompteByClientId(clientId);
//            
//            if (compte == null) {
//                // Créer le compte s'il n'existe pas
//                compte = new CompteClient();
//                compte.setClientId(clientId);
//                compte.setSolde(BigDecimal.ZERO);
//                compte.setLimiteCredit(BigDecimal.ZERO); // Par défaut, pas de crédit
//                int compteId = compteDAO.creerCompteClient(compte);
//                if (compteId == -1) {
//                    conn.rollback();
//                    return false;
//                }
//                compte.setId(compteId);
//            }
//            
//            BigDecimal soldeAvant = compte.getSolde();
//            BigDecimal limiteCredit = compte.getLimiteCredit();
//            
//            System.out.println("=== DÉPÔT EN COURS ===");
//            System.out.println("Solde avant: " + String.format("%.2f HTG", soldeAvant));
//            System.out.println("Montant dépôt: " + String.format("%.2f HTG", montant));
//            System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
//            
//            // Calculer le nouveau solde
//            BigDecimal soldeApres = soldeAvant.add(montant);
//            
//            // Calculer le crédit utilisé avant et après
//            BigDecimal creditUtiliseAvant = BigDecimal.ZERO;
//            BigDecimal creditUtiliseApres = BigDecimal.ZERO;
//            
//            if (soldeAvant.compareTo(BigDecimal.ZERO) < 0) {
//                creditUtiliseAvant = soldeAvant.abs();
//            }
//            if (soldeApres.compareTo(BigDecimal.ZERO) < 0) {
//                creditUtiliseApres = soldeApres.abs();
//            }
//            
//            System.out.println("Crédit utilisé avant: " + String.format("%.2f HTG", creditUtiliseAvant));
//            System.out.println("Crédit utilisé après: " + String.format("%.2f HTG", creditUtiliseApres));
//            System.out.println("Crédit disponible avant: " + 
//                             String.format("%.2f HTG", limiteCredit.subtract(creditUtiliseAvant)));
//            System.out.println("Crédit disponible après: " + 
//                             String.format("%.2f HTG", limiteCredit.subtract(creditUtiliseApres)));
//            
//            // 1. Mettre à jour le solde dans COMPTE_CLIENT
//            String updateSoldeSql = "UPDATE COMPTE_CLIENT SET SOLDE = ?, DATE_MAJ = ? WHERE ID = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(updateSoldeSql)) {
//                stmt.setBigDecimal(1, soldeApres);
//                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//                stmt.setInt(3, compte.getId());
//                
//                int rowsUpdated = stmt.executeUpdate();
//                if (rowsUpdated == 0) {
//                    conn.rollback();
//                    return false;
//                }
//            }
//            
//            // 2. Créer la transaction
//            TransactionCompte transaction = new TransactionCompte();
//            transaction.setCompteClientId(compte.getId());
//            transaction.setTypeTransactionId(getTypeTransactionId("DEPOT")); // 1 = DÉPÔT
//            
//            // Le montant est toujours positif pour un dépôt
//            transaction.setMontant(montant);
//            transaction.setSoldeAvant(soldeAvant);
//            transaction.setSoldeApres(soldeApres);
//            transaction.setCaissiereId(caissiereId);
//            
//            // Ajouter des notes détaillées sur l'impact sur le crédit
//            String notesDetaillees = notes != null ? notes + " - " : "";
//            if (creditUtiliseAvant.compareTo(BigDecimal.ZERO) > 0) {
//                if (creditUtiliseApres.compareTo(BigDecimal.ZERO) > 0) {
//                    // Solde reste négatif après dépôt
//                    BigDecimal creditRembourse = montant.min(creditUtiliseAvant);
//                    notesDetaillees += "Partiellement utilisé pour rembourser crédit: " + 
//                                     String.format("%.2f HTG", creditRembourse) + 
//                                     " | Crédit restant: " + String.format("%.2f HTG", creditUtiliseApres);
//                } else {
//                    // Solde devient positif après dépôt (crédit entièrement remboursé)
//                    notesDetaillees += "Entièrement utilisé pour rembourser crédit de " + 
//                                     String.format("%.2f HTG", creditUtiliseAvant);
//                }
//            } else if (soldeAvant.compareTo(BigDecimal.ZERO) < 0 && soldeApres.compareTo(BigDecimal.ZERO) >= 0) {
//                // Le dépôt a remboursé tout le crédit et laissé un solde positif
//                BigDecimal creditRembourse = creditUtiliseAvant;
//                BigDecimal surplus = montant.subtract(creditRembourse);
//                notesDetaillees += "Crédit remboursé: " + String.format("%.2f HTG", creditRembourse) +
//                                 " | Solde positif ajouté: " + String.format("%.2f HTG", surplus);
//            }
//            
//            transaction.setNotes(notesDetaillees);
//            transaction.setDateTransaction(new Timestamp(System.currentTimeMillis()));
//            
//            int transactionId = creerTransaction(transaction);
//            if (transactionId == -1) {
//                conn.rollback();
//                return false;
//            }
//            
//            // 3. Si le dépôt a remboursé du crédit, créer une notification pour le client
//         // Messages très simples
//            if (creditUtiliseAvant.compareTo(BigDecimal.ZERO) > 0) {
//                BigDecimal creditRembourse = creditUtiliseAvant.subtract(creditUtiliseApres);
//                
//                try {
//                    Notification notif = new Notification();
//                    notif.setGeneratedBy("SYSTEM");
//                    notif.setToUser(clientId);
//                    notif.setTypeNotif("DEPOT");
//                    notif.setStatus("VISIBLE");
//                    
//                    String message = "Dépôt de " + String.format("%.2f HTG", montant) + " effectué";
//                    
//                    if (creditRembourse.compareTo(BigDecimal.ZERO) > 0) {
//                        message += ". Crédit remboursé: " + String.format("%.2f HTG", creditRembourse);
//                    }
//                    
//                    if (creditUtiliseApres.compareTo(BigDecimal.ZERO) > 0) {
//                        message += ". Crédit restant: " + String.format("%.2f HTG", creditUtiliseApres);
//                    } else if (creditUtiliseApres.compareTo(BigDecimal.ZERO) == 0 && 
//                              creditUtiliseAvant.compareTo(BigDecimal.ZERO) > 0) {
//                        message += ". Votre crédit est maintenant à 0 HTG";
//                    }
//                    
//                    notif.setMessages(message);
//                    
//                    NotificationDAO notifDAO = new NotificationDAO();
//                    notifDAO.ajouterNotification(notif);
//                    
//                } catch (Exception e) {
//                    System.err.println("Erreur notification: " + e.getMessage());
//                }
//            } else {
//                // Pas de crédit utilisé
//                try {
//                    Notification notif = new Notification();
//                    notif.setGeneratedBy("SYSTEM");
//                    notif.setToUser(clientId);
//                    notif.setMessages("Dépôt de " + String.format("%.2f HTG", montant) + 
//                                    " effectué. " +
//                                    "Nouveau solde : " + String.format("%.2f HTG", soldeApres));
//                    notif.setTypeNotif("DEPOT");
//                    notif.setStatus("VISIBLE");
//                    
//                    NotificationDAO notifDAO = new NotificationDAO();
//                    notifDAO.ajouterNotification(notif);
//                    
//                } catch (Exception e) {
//                    System.err.println("Erreur notification: " + e.getMessage());
//                }
//            }
//            
//            conn.commit();
//            System.out.println("Dépôt réussi pour client #" + clientId);
//            return true;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

 // 2. Effectuer un dépôt avec gestion du crédit et remboursement automatique
    public boolean effectuerDepot(int clientId, BigDecimal montant, int caissiereId, String notes) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer le compte
            CompteClientDAO compteDAO = new CompteClientDAO();
            CompteClient compte = compteDAO.getCompteByClientId(clientId);
            
            if (compte == null) {
                // Créer le compte s'il n'existe pas
                compte = new CompteClient();
                compte.setClientId(clientId);
                compte.setSolde(BigDecimal.ZERO);
                compte.setLimiteCredit(BigDecimal.ZERO); // Par défaut, pas de crédit
                int compteId = compteDAO.creerCompteClient(compte);
                if (compteId == -1) {
                    conn.rollback();
                    return false;
                }
                compte.setId(compteId);
            }
            
            BigDecimal soldeAvant = compte.getSolde();
            BigDecimal limiteCredit = compte.getLimiteCredit();
            
            System.out.println("=== DÉPÔT EN COURS ===");
            System.out.println("Client ID: " + clientId);
            System.out.println("Solde avant: " + String.format("%.2f HTG", soldeAvant));
            System.out.println("Montant dépôt: " + String.format("%.2f HTG", montant));
            System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
            
            // Calculer le nouveau solde
            BigDecimal soldeApres = soldeAvant.add(montant);
            
            // Calculer le crédit utilisé avant et après
            BigDecimal creditUtiliseAvant = BigDecimal.ZERO;
            BigDecimal creditUtiliseApres = BigDecimal.ZERO;
            
            if (soldeAvant.compareTo(BigDecimal.ZERO) < 0) {
                creditUtiliseAvant = soldeAvant.abs();
            }
            if (soldeApres.compareTo(BigDecimal.ZERO) < 0) {
                creditUtiliseApres = soldeApres.abs();
            }
            
            System.out.println("Crédit utilisé avant: " + String.format("%.2f HTG", creditUtiliseAvant));
            System.out.println("Crédit utilisé après: " + String.format("%.2f HTG", creditUtiliseApres));
            System.out.println("Crédit disponible avant: " + 
                             String.format("%.2f HTG", limiteCredit.subtract(creditUtiliseAvant)));
            System.out.println("Crédit disponible après: " + 
                             String.format("%.2f HTG", limiteCredit.subtract(creditUtiliseApres)));
            
            // 1. Mettre à jour le solde dans COMPTE_CLIENT
            String updateSoldeSql = "UPDATE COMPTE_CLIENT SET SOLDE = ?, DATE_MAJ = ? WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSoldeSql)) {
                stmt.setBigDecimal(1, soldeApres);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(3, compte.getId());
                
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Si du crédit a été remboursé, mettre à jour les crédits en attente
            BigDecimal creditRembourse = creditUtiliseAvant.subtract(creditUtiliseApres);
            if (creditRembourse.compareTo(BigDecimal.ZERO) > 0) {
                CreditDAO creditDAO = new CreditDAO();
	            int montantInt = creditRembourse.intValue();
	            boolean success = creditDAO.payerCreditGlobal(clientId, montantInt);
                System.out.println("Crédits remboursés: " + String.format("%.2f HTG", creditRembourse));
            }
            
            // 3. Créer la transaction
            TransactionCompte transaction = new TransactionCompte();
            transaction.setCompteClientId(compte.getId());
            transaction.setTypeTransactionId(getTypeTransactionId("DEPOT")); // 1 = DÉPÔT
            
            // Le montant est toujours positif pour un dépôt
            transaction.setMontant(montant);
            transaction.setSoldeAvant(soldeAvant);
            transaction.setSoldeApres(soldeApres);
            transaction.setCaissiereId(caissiereId);
            
            // Ajouter des notes détaillées sur l'impact sur le crédit
            String notesDetaillees = notes != null ? notes + " - " : "";
            if (creditUtiliseAvant.compareTo(BigDecimal.ZERO) > 0) {
                if (creditUtiliseApres.compareTo(BigDecimal.ZERO) > 0) {
                    // Solde reste négatif après dépôt
                    notesDetaillees += "Partiellement utilisé pour rembourser crédit: " + 
                                     String.format("%.2f HTG", creditRembourse) + 
                                     " | Crédit restant: " + String.format("%.2f HTG", creditUtiliseApres);
                } else {
                    // Solde devient positif après dépôt (crédit entièrement remboursé)
                    notesDetaillees += "Entièrement utilisé pour rembourser crédit de " + 
                                     String.format("%.2f HTG", creditUtiliseAvant);
                }
            } else if (soldeAvant.compareTo(BigDecimal.ZERO) < 0 && soldeApres.compareTo(BigDecimal.ZERO) >= 0) {
                // Le dépôt a remboursé tout le crédit et laissé un solde positif
                BigDecimal surplus = montant.subtract(creditUtiliseAvant);
                notesDetaillees += "Crédit remboursé: " + String.format("%.2f HTG", creditUtiliseAvant) +
                                 " | Solde positif ajouté: " + String.format("%.2f HTG", surplus);
            }
            
            transaction.setNotes(notesDetaillees);
            transaction.setDateTransaction(new Timestamp(System.currentTimeMillis()));
            
            int transactionId = creerTransaction(transaction);
            if (transactionId == -1) {
                conn.rollback();
                return false;
            }
            
            // 4. Créer une notification pour le client
            try {
                Notification notif = new Notification();
                notif.setGeneratedBy("SYSTEM");
                notif.setToUser(clientId);
                notif.setTypeNotif("DEPOT");
                notif.setStatus("VISIBLE");
                
                String message = "Dépôt de " + String.format("%.2f HTG", montant) + " effectué";
                
                if (creditRembourse.compareTo(BigDecimal.ZERO) > 0) {
                    message += ". Crédit remboursé: " + String.format("%.2f HTG", creditRembourse);
                }
                
                if (creditUtiliseApres.compareTo(BigDecimal.ZERO) > 0) {
                    message += ". Crédit restant: " + String.format("%.2f HTG", creditUtiliseApres);
                } else if (creditUtiliseApres.compareTo(BigDecimal.ZERO) == 0 && 
                          creditUtiliseAvant.compareTo(BigDecimal.ZERO) > 0) {
                    message += ". Votre crédit est maintenant à 0 HTG";
                }
                
                notif.setMessages(message);
                
                NotificationDAO notifDAO = new NotificationDAO();
                notifDAO.ajouterNotification(notif);
                
            } catch (Exception e) {
                System.err.println("Erreur notification: " + e.getMessage());
            }
            
            conn.commit();
            System.out.println("Dépôt réussi pour client #" + clientId);
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Payer une commande via compte
    public boolean payerCommandeViaCompte(int commandeId, int caissiereId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Récupérer la commande
            CommandeDAO commandeDAO = new CommandeDAO();
            Commande commande = commandeDAO.getCommandeById(commandeId);
            
            if (commande == null || commande.getClientId() == null) {
                conn.rollback();
                return false;
            }
            
            // Vérifier le solde disponible (solde + limite crédit)
            CompteClientDAO compteDAO = new CompteClientDAO();
            CompteClient compte = compteDAO.getCompteByClientId(commande.getClientId());
            
            if (compte == null) {
                // Créer le compte s'il n'existe pas
                compte = new CompteClient();
                compte.setClientId(commande.getClientId());
                compte.setSolde(BigDecimal.ZERO);
                compte.setLimiteCredit(BigDecimal.ZERO);
                int compteId = compteDAO.creerCompteClient(compte);
                if (compteId == -1) {
                    conn.rollback();
                    return false;
                }
                compte.setId(compteId);
            }
            
            // Vérifier si le paiement est possible
            BigDecimal soldeDisponible = compte.getSoldeDisponible();
            if (soldeDisponible.compareTo(commande.getMontantTotal()) < 0) {
                conn.rollback();
                return false;
            }
            
            // Calculer le nouveau solde
            BigDecimal nouveauSolde = compte.getSolde().subtract(commande.getMontantTotal());
            
            // Vérifier que le nouveau solde ne dépasse pas la limite négative
            BigDecimal limiteNegative = compte.getLimiteCredit().negate();
            if (nouveauSolde.compareTo(limiteNegative) < 0) {
                conn.rollback();
                return false;
            }
            
            // Créer la transaction de dépense
            TransactionCompte transaction = new TransactionCompte();
            transaction.setCompteClientId(compte.getId());
            transaction.setTypeTransactionId(getTypeTransactionId("DEPENSE"));
            transaction.setMontant(commande.getMontantTotal());
            transaction.setSoldeAvant(compte.getSolde());
            transaction.setSoldeApres(nouveauSolde);
            transaction.setCaissiereId(caissiereId);
            transaction.setCommandeId(commandeId);
            transaction.setNotes("Paiement commande " + commande.getNumeroCommande());
            transaction.setDateTransaction(new Timestamp(System.currentTimeMillis()));
            
            int transactionId = creerTransaction(transaction);
            if (transactionId == -1) {
                conn.rollback();
                return false;
            }
            
            // Mettre à jour le solde du compte
            if (!compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde)) {
                conn.rollback();
                return false;
            }
            
            // Mettre à jour la commande
            String updateCommande = "UPDATE COMMANDE SET " +
                                   "STATUT_PAIEMENT = 'PAYE', " +
                                   "MODE_PAIEMENT = 'COMPTE', " +
                                   "PAIEMENT_VIA_COMPTE = 1, " +
                                   "ID_TRANSACTION_COMPTE = ?, " +
                                   "UPDATED_AT = ? " +
                                   "WHERE ID = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateCommande)) {
                stmt.setInt(1, transactionId);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(3, commandeId);
                
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 4. Récupérer l'historique d'un client
    public List<TransactionCompte> getHistoriqueClient(int clientId, java.sql.Date dateDebut, java.sql.Date dateFin) {
        List<TransactionCompte> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT tc.*, " +
            "cc.CLIENT_ID, cc.SOLDE AS compte_solde, " +
            "tt.CODE AS type_code, tt.LIBELLE AS type_libelle, " +
            "caissier.ID AS caissier_id, caissier.NOM AS caissier_nom, caissier.PRENOM AS caissier_prenom, " +
            "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " +
            "FROM TRANSACTION_COMPTE tc " +
            "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
            "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
            "LEFT JOIN UTILISATEUR caissier ON tc.CAISSIERE_ID = caissier.ID " +
            "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID " +
            "WHERE cc.CLIENT_ID = ? "
        );
        
        if (dateDebut != null) {
            sql.append("AND DATE(tc.DATE_TRANSACTION) >= ? ");
        }
        if (dateFin != null) {
            sql.append("AND DATE(tc.DATE_TRANSACTION) <= ? ");
        }
        sql.append("ORDER BY tc.DATE_TRANSACTION DESC");
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, clientId);
            
            if (dateDebut != null) {
                stmt.setDate(paramIndex++, dateDebut);
            }
            if (dateFin != null) {
                stmt.setDate(paramIndex, dateFin);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionCompte(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // 5. Récupérer le solde actuel
    public BigDecimal getSoldeClient(int clientId) {
        String sql = "SELECT SOLDE FROM COMPTE_CLIENT WHERE CLIENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("SOLDE");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // Helper pour obtenir l'ID d'un type de transaction
    private int getTypeTransactionId(String code) {
        String sql = "SELECT ID FROM TYPE_TRANSACTION WHERE CODE = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Helper pour mapper ResultSet -> TransactionCompte
    private TransactionCompte mapTransactionCompte(ResultSet rs) throws SQLException {
        TransactionCompte transaction = new TransactionCompte();
        transaction.setId(rs.getInt("ID"));
        transaction.setCompteClientId(rs.getInt("COMPTE_CLIENT_ID"));
        transaction.setTypeTransactionId(rs.getInt("TYPE_TRANSACTION_ID"));
        transaction.setMontant(rs.getBigDecimal("MONTANT"));
        transaction.setSoldeAvant(rs.getBigDecimal("SOLDE_AVANT"));
        transaction.setSoldeApres(rs.getBigDecimal("SOLDE_APRES"));
        transaction.setCaissiereId(rs.getInt("CAISSIERE_ID"));
        
        int commandeId = rs.getInt("COMMANDE_ID");
        if (!rs.wasNull()) {
            transaction.setCommandeId(commandeId);
        }
        
        transaction.setNotes(rs.getString("NOTES"));
        transaction.setDateTransaction(rs.getTimestamp("DATE_TRANSACTION"));
        
        // Mapper type transaction
        TypeTransaction type = new TypeTransaction();
        type.setId(rs.getInt("TYPE_TRANSACTION_ID"));
        type.setCode(rs.getString("type_code"));
        type.setLibelle(rs.getString("type_libelle"));
        transaction.setTypeTransaction(type);
        
        // Mapper caissière
        Utilisateur caissiere = new Utilisateur();
        caissiere.setId(rs.getInt("caissier_id"));
        caissiere.setNom(rs.getString("caissier_nom"));
        caissiere.setPrenom(rs.getString("caissier_prenom"));
        transaction.setCaissiere(caissiere);
        
        // Mapper commande si présente
        if (commandeId > 0) {
            Commande commande = new Commande();
            commande.setId(commandeId);
            commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
            commande.setMontantTotal(rs.getBigDecimal("commande_montant"));
            transaction.setCommande(commande);
        }
        
        return transaction;
    }
    
 // Dans votre TransactionCompteDAO.java, ajoutez ces nouvelles méthodes :

 // 6. Récupérer l'historique avec pagination
 public List<TransactionCompte> getHistoriqueClient(int clientId, java.sql.Date dateDebut, 
         java.sql.Date dateFin, Integer limit, Integer offset) {
     List<TransactionCompte> transactions = new ArrayList<>();
     StringBuilder sql = new StringBuilder(
         "SELECT tc.*, " +
         "cc.CLIENT_ID, cc.SOLDE AS compte_solde, " +
         "tt.CODE AS type_code, tt.LIBELLE AS type_libelle, " +
         "caissier.ID AS caissier_id, caissier.NOM AS caissier_nom, caissier.PRENOM AS caissier_prenom, " +
         "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " +
         "FROM TRANSACTION_COMPTE tc " +
         "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
         "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
         "LEFT JOIN UTILISATEUR caissier ON tc.CAISSIERE_ID = caissier.ID " +
         "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID " +
         "WHERE cc.CLIENT_ID = ? "
     );
     
     if (dateDebut != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) >= ? ");
     }
     if (dateFin != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) <= ? ");
     }
     sql.append("ORDER BY tc.DATE_TRANSACTION DESC");
     
     // Ajouter la pagination si nécessaire
     if (limit != null) {
         sql.append(" LIMIT ?");
     }
     if (offset != null && limit != null) {
         sql.append(" OFFSET ?");
     }
     
     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
         
         int paramIndex = 1;
         stmt.setInt(paramIndex++, clientId);
         
         if (dateDebut != null) {
             stmt.setDate(paramIndex++, dateDebut);
         }
         if (dateFin != null) {
             stmt.setDate(paramIndex++, dateFin);
         }
         
         if (limit != null) {
             stmt.setInt(paramIndex++, limit);
         }
         if (offset != null && limit != null) {
             stmt.setInt(paramIndex, offset);
         }
         
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 transactions.add(mapTransactionCompte(rs));
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return transactions;
 }

 // 7. Récupérer l'historique avec filtre par type
 public List<TransactionCompte> getHistoriqueClientFiltre(int clientId, java.sql.Date dateDebut, 
         java.sql.Date dateFin, String typeTransaction, Integer limit, Integer offset) {
     List<TransactionCompte> transactions = new ArrayList<>();
     StringBuilder sql = new StringBuilder(
         "SELECT tc.*, " +
         "cc.CLIENT_ID, cc.SOLDE AS compte_solde, " +
         "tt.CODE AS type_code, tt.LIBELLE AS type_libelle, " +
         "caissier.ID AS caissier_id, caissier.NOM AS caissier_nom, caissier.PRENOM AS caissier_prenom, " +
         "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " +
         "FROM TRANSACTION_COMPTE tc " +
         "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
         "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
         "LEFT JOIN UTILISATEUR caissier ON tc.CAISSIERE_ID = caissier.ID " +
         "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID " +
         "WHERE cc.CLIENT_ID = ? AND tt.CODE = ? "
     );
     
     if (dateDebut != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) >= ? ");
     }
     if (dateFin != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) <= ? ");
     }
     sql.append("ORDER BY tc.DATE_TRANSACTION DESC");
     
     // Ajouter la pagination si nécessaire
     if (limit != null) {
         sql.append(" LIMIT ?");
     }
     if (offset != null && limit != null) {
         sql.append(" OFFSET ?");
     }
     
     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
         
         int paramIndex = 1;
         stmt.setInt(paramIndex++, clientId);
         stmt.setString(paramIndex++, typeTransaction);
         
         if (dateDebut != null) {
             stmt.setDate(paramIndex++, dateDebut);
         }
         if (dateFin != null) {
             stmt.setDate(paramIndex++, dateFin);
         }
         
         if (limit != null) {
             stmt.setInt(paramIndex++, limit);
         }
         if (offset != null && limit != null) {
             stmt.setInt(paramIndex, offset);
         }
         
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 transactions.add(mapTransactionCompte(rs));
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return transactions;
 }

 // 8. Compter le nombre total de transactions (pour pagination)
 public int countTransactionsClient(int clientId, java.sql.Date dateDebut, java.sql.Date dateFin, String typeTransaction) {
     StringBuilder sql = new StringBuilder(
         "SELECT COUNT(*) as total " +
         "FROM TRANSACTION_COMPTE tc " +
         "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
         "JOIN TYPE_TRANSACTION tt ON tc.TYPE_TRANSACTION_ID = tt.ID " +
         "WHERE cc.CLIENT_ID = ? "
     );
     
     if (typeTransaction != null && !typeTransaction.isEmpty() && !"TOUS".equals(typeTransaction)) {
         sql.append("AND tt.CODE = ? ");
     }
     
     if (dateDebut != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) >= ? ");
     }
     if (dateFin != null) {
         sql.append("AND DATE(tc.DATE_TRANSACTION) <= ? ");
     }
     
     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
         
         int paramIndex = 1;
         stmt.setInt(paramIndex++, clientId);
         
         if (typeTransaction != null && !typeTransaction.isEmpty() && !"TOUS".equals(typeTransaction)) {
             stmt.setString(paramIndex++, typeTransaction);
         }
         
         if (dateDebut != null) {
             stmt.setDate(paramIndex++, dateDebut);
         }
         if (dateFin != null) {
             stmt.setDate(paramIndex, dateFin);
         }
         
         try (ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 return rs.getInt("total");
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return 0;
 }

 // 9. Récupérer les statistiques de transactions
 public Map<String, Object> getStatistiquesTransactions(int clientId, java.sql.Date dateDebut, java.sql.Date dateFin) {
     Map<String, Object> stats = new HashMap<>();
     
     String sql = "SELECT " +
                 "SUM(CASE WHEN tc.MONTANT > 0 THEN tc.MONTANT ELSE 0 END) as total_depots, " +
                 "SUM(CASE WHEN tc.MONTANT < 0 THEN ABS(tc.MONTANT) ELSE 0 END) as total_retraits, " +
                 "COUNT(*) as nombre_transactions, " +
                 "COUNT(DISTINCT DATE(tc.DATE_TRANSACTION)) as jours_activite " +
                 "FROM TRANSACTION_COMPTE tc " +
                 "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
                 "WHERE cc.CLIENT_ID = ? " +
                 "AND (? IS NULL OR DATE(tc.DATE_TRANSACTION) >= ?) " +
                 "AND (? IS NULL OR DATE(tc.DATE_TRANSACTION) <= ?)";
     
     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {
         
         stmt.setInt(1, clientId);
         stmt.setDate(2, dateDebut);
         stmt.setDate(3, dateDebut);
         stmt.setDate(4, dateFin);
         stmt.setDate(5, dateFin);
         
         try (ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 stats.put("totalDepots", rs.getBigDecimal("total_depots") != null ? 
                     rs.getBigDecimal("total_depots") : BigDecimal.ZERO);
                 stats.put("totalRetraits", rs.getBigDecimal("total_retraits") != null ? 
                     rs.getBigDecimal("total_retraits") : BigDecimal.ZERO);
                 stats.put("nombreTransactions", rs.getInt("nombre_transactions"));
                 stats.put("joursActivite", rs.getInt("jours_activite"));
                 
                 // Calculer la moyenne quotidienne
                 int jours = rs.getInt("jours_activite");
                 if (jours > 0) {
                     BigDecimal totalDepots = rs.getBigDecimal("total_depots") != null ? 
                         rs.getBigDecimal("total_depots") : BigDecimal.ZERO;
                     BigDecimal totalRetraits = rs.getBigDecimal("total_retraits") != null ? 
                         rs.getBigDecimal("total_retraits") : BigDecimal.ZERO;
                     
                     stats.put("moyenneDepotsQuotidien", 
                         totalDepots.divide(new BigDecimal(jours), 2, BigDecimal.ROUND_HALF_UP));
                     stats.put("moyenneRetraitsQuotidien", 
                         totalRetraits.divide(new BigDecimal(jours), 2, BigDecimal.ROUND_HALF_UP));
                 }
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return stats;
 }

 // 10. Récupérer les transactions par période (pour graphiques)
 public List<Map<String, Object>> getTransactionsParPeriode(int clientId, String periode, int nombrePeriodes) {
     List<Map<String, Object>> result = new ArrayList<>();
     
     String sql = "";
     if ("jour".equalsIgnoreCase(periode)) {
         sql = "SELECT DATE(tc.DATE_TRANSACTION) as periode, " +
               "SUM(CASE WHEN tc.MONTANT > 0 THEN tc.MONTANT ELSE 0 END) as depots, " +
               "SUM(CASE WHEN tc.MONTANT < 0 THEN ABS(tc.MONTANT) ELSE 0 END) as retraits, " +
               "COUNT(*) as nombre_transactions " +
               "FROM TRANSACTION_COMPTE tc " +
               "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
               "WHERE cc.CLIENT_ID = ? " +
               "AND tc.DATE_TRANSACTION >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
               "GROUP BY DATE(tc.DATE_TRANSACTION) " +
               "ORDER BY DATE(tc.DATE_TRANSACTION) DESC";
     } else if ("mois".equalsIgnoreCase(periode)) {
         sql = "SELECT DATE_FORMAT(tc.DATE_TRANSACTION, '%Y-%m') as periode, " +
               "SUM(CASE WHEN tc.MONTANT > 0 THEN tc.MONTANT ELSE 0 END) as depots, " +
               "SUM(CASE WHEN tc.MONTANT < 0 THEN ABS(tc.MONTANT) ELSE 0 END) as retraits, " +
               "COUNT(*) as nombre_transactions " +
               "FROM TRANSACTION_COMPTE tc " +
               "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
               "WHERE cc.CLIENT_ID = ? " +
               "AND tc.DATE_TRANSACTION >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
               "GROUP BY DATE_FORMAT(tc.DATE_TRANSACTION, '%Y-%m') " +
               "ORDER BY DATE_FORMAT(tc.DATE_TRANSACTION, '%Y-%m') DESC";
     } else if ("annee".equalsIgnoreCase(periode)) {
         sql = "SELECT YEAR(tc.DATE_TRANSACTION) as periode, " +
               "SUM(CASE WHEN tc.MONTANT > 0 THEN tc.MONTANT ELSE 0 END) as depots, " +
               "SUM(CASE WHEN tc.MONTANT < 0 THEN ABS(tc.MONTANT) ELSE 0 END) as retraits, " +
               "COUNT(*) as nombre_transactions " +
               "FROM TRANSACTION_COMPTE tc " +
               "JOIN COMPTE_CLIENT cc ON tc.COMPTE_CLIENT_ID = cc.ID " +
               "WHERE cc.CLIENT_ID = ? " +
               "AND tc.DATE_TRANSACTION >= DATE_SUB(CURDATE(), INTERVAL ? YEAR) " +
               "GROUP BY YEAR(tc.DATE_TRANSACTION) " +
               "ORDER BY YEAR(tc.DATE_TRANSACTION) DESC";
     }
     
     if (sql.isEmpty()) {
         return result;
     }
     
     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {
         
         stmt.setInt(1, clientId);
         stmt.setInt(2, nombrePeriodes);
         
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 Map<String, Object> periodeData = new HashMap<>();
                 periodeData.put("periode", rs.getString("periode"));
                 periodeData.put("depots", rs.getBigDecimal("depots") != null ? 
                     rs.getBigDecimal("depots") : BigDecimal.ZERO);
                 periodeData.put("retraits", rs.getBigDecimal("retraits") != null ? 
                     rs.getBigDecimal("retraits") : BigDecimal.ZERO);
                 periodeData.put("nombreTransactions", rs.getInt("nombre_transactions"));
                 result.add(periodeData);
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return result;
 }
}