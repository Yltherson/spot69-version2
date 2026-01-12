package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;
import com.spot69.utils.EmailUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.internet.*;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;




public class CreditDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

 // Scheduler global pour tous les crédits
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    // Ajouter un crédit
//    public int ajouterCredit(Credit credit) {
//        String sql = "INSERT INTO CREDIT (UTILISATEUR_ID, COMMANDE_ID, MONTANT_TOTAL, MONTANT_PAYE, STATUT, DATE_CREDIT, CREATED_AT, UPDATED_AT) " +
//                     "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//
//            int creditId;
//            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                stmt.setInt(1, credit.getUtilisateurId());
//                stmt.setInt(2, credit.getCommandeId());
//                stmt.setInt(3, credit.getMontantTotal());
//                stmt.setInt(4, credit.getMontantPaye() != 0 ? credit.getMontantPaye() : 0);
//                stmt.setString(5, credit.getStatut() != null ? credit.getStatut() : "NON_PAYE");
//                Timestamp now = new Timestamp(new Date().getTime());
//                stmt.setTimestamp(6, now);
//                stmt.setTimestamp(7, now);
//
//                stmt.executeUpdate();
//
//                try (ResultSet rs = stmt.getGeneratedKeys()) {
//                    if (rs.next()) {
//                        creditId = rs.getInt(1);
//                        credit.setId(creditId);
//                    } else {
//                        conn.rollback();
//                        return -1;
//                    }
//                }
//            }
//
//            conn.commit();
//            return creditId;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }

    public int ajouterCredit(Credit credit) {
        String sqlCredit = "INSERT INTO CREDIT (UTILISATEUR_ID, COMMANDE_ID, MONTANT_TOTAL, MONTANT_PAYE, STATUT, DATE_CREDIT, CREATED_AT, UPDATED_AT) " +
                         "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)";
        String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT = 'HIDDEN', UPDATED_AT = ? WHERE ID = ?";
        String sqlInsertNotification = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) " +
                                     "VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";
        String sqlSelectUserEmail = "SELECT EMAIL FROM UTILISATEUR WHERE ID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            int creditId;
            
            // 1. Insérer le crédit
            try (PreparedStatement stmt = conn.prepareStatement(sqlCredit, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, credit.getUtilisateurId());
                stmt.setInt(2, credit.getCommandeId());
                stmt.setInt(3, credit.getMontantTotal());
                stmt.setInt(4, credit.getMontantPaye() != 0 ? credit.getMontantPaye() : 0);
                stmt.setString(5, "NON_VALIDE"); // Statut initial
                Timestamp now = new Timestamp(new Date().getTime());
                stmt.setTimestamp(6, now);
                stmt.setTimestamp(7, now);

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        creditId = rs.getInt(1);
                        credit.setId(creditId);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // 2. Mettre la commande en HIDDEN
            try (PreparedStatement stmtUpdateCommande = conn.prepareStatement(sqlUpdateCommande)) {
                Timestamp now = new Timestamp(new Date().getTime());
                stmtUpdateCommande.setTimestamp(1, now);
                stmtUpdateCommande.setInt(2, credit.getCommandeId());
                stmtUpdateCommande.executeUpdate();
            }

            // 3. Récupérer l'email de l'utilisateur
            String userEmail = null;
            try (PreparedStatement stmtSelectEmail = conn.prepareStatement(sqlSelectUserEmail)) {
                stmtSelectEmail.setInt(1, credit.getUtilisateurId());
                ResultSet rs = stmtSelectEmail.executeQuery();
                if (rs.next()) {
                    userEmail = rs.getString("EMAIL");
                }
            }

            // 4. Insérer la notification
            try (PreparedStatement stmtNotification = conn.prepareStatement(sqlInsertNotification)) {
                Timestamp now = new Timestamp(new Date().getTime());
                String message = "Une commande crédit a été effectuée sur votre compte. Montant: " + credit.getMontantTotal() + " HTG. Veuillez valider dans les 5 minutes.";
                
                stmtNotification.setString(1, "SYSTEM");
                stmtNotification.setInt(2, credit.getUtilisateurId());
                stmtNotification.setString(3, message);
                stmtNotification.setTimestamp(4, now);
                stmtNotification.setTimestamp(5, now);
                stmtNotification.executeUpdate();
            }

            conn.commit();

            // 5. Envoyer l'email (en dehors de la transaction)
            if (userEmail != null && !userEmail.trim().isEmpty()) {
            	CommandeDAO commandeDAO = new CommandeDAO();
				//                envoyerEmailValidationCredit(userEmail, credit);
//            	 EmailUtils.envoyerEmailValidationCredit(userEmail, credit);
            	List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(credit.getCommandeId());
            	EmailUtils.envoyerEmailValidationCredit(userEmail, credit, details);
            }

            // 6. Programmer l'annulation automatique après 5 minutes
            programmerAnnulationAutomatique(creditId, credit.getCommandeId(), credit.getUtilisateurId());

            return creditId;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
//    private void envoyerEmailValidationCredit(String destinataire, Credit credit) {
//        try {
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.office365.com");	
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//
//            // CORRECTION : Utiliser getDefaultInstance au lieu de getInstance
//            javax.mail.Session session = Session.getDefaultInstance(props, new Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("contact@pot69.net", "Emanagement2024@");
//                }
//            });
//
//            // Le reste du code devrait fonctionner
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("contact@pot69.net"));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
//            message.setSubject("Validation de commande crédit - Pot69");
//            
//            String htmlContent = "<html>"
//                    + "<body>"
//                    + "<h2>Validation de commande crédit</h2>"
//                    + "<p>Une commande crédit a été effectuée sur votre compte :</p>"
//                    + "<ul>"
//                    + "<li><strong>Montant :</strong> " + credit.getMontantTotal() + " HTG</li>"
//                    + "<li><strong>Date :</strong> " + new Date() + "</li>"
//                    + "</ul>"
//                    + "<p><strong>IMPORTANT :</strong> Cette commande a été effectuée à votre compte.</p>"
//                    + "<p>Si c'est vous qui avez effectué cette commande, aucune action n'est requise.</p>"
//                    + "<p>Si vous n'avez pas effectué cette commande, veuillez ignorer cet email - la commande sera automatiquement annulée dans 5 minutes.</p>"
//                    + "<br>"
//                    + "<p>Cordialement,<br>L'équipe Pot69</p>"
//                    + "</body>"
//                    + "</html>";
//
//            message.setContent(htmlContent, "text/html; charset=utf-8");
//            
//            //Transport.send(message);
//            try {
//                Transport transport = session.getTransport("smtp");
//                transport.connect(); // ici tu verras si l'auth fonctionne
//                System.out.println("Connecté au SMTP !");
//                System.out.println("Email envoyé avec succès à: " + destinataire);
//                transport.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            
//
//            
//        } catch (Exception e) {
//            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
    // Programme l'annulation automatique d'un crédit
    public void programmerAnnulationAutomatique(int creditId, int commandeId, int utilisateurId) {

        System.out.println("[SCHEDULER] Programmation de l'annulation automatique dans 5 min pour creditId="
                + creditId + ", commandeId=" + commandeId + ", userId=" + utilisateurId);

        scheduler.schedule(() -> {
            System.out.println("[SCHEDULER] → Déclenchement du timer pour creditId=" + creditId);
            annulerCreditSiNonValide(creditId, commandeId, utilisateurId);
        }, 5, TimeUnit.MINUTES);
    }
    
    public void rechargerTasksAnnulation() {
        String sql = "SELECT ID, COMMANDE_ID, UTILISATEUR_ID, DATE_CREDIT " +
                     "FROM CREDIT WHERE STATUT = 'NON_VALIDE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("[RECOVERY] Vérification des crédits NON_VALIDE au démarrage...");

            while (rs.next()) {

                int creditId = rs.getInt("ID");
                int commandeId = rs.getInt("COMMANDE_ID");
                int utilisateurId = rs.getInt("UTILISATEUR_ID");
                Timestamp dateCredit = rs.getTimestamp("DATE_CREDIT");

                long elapsedMs = System.currentTimeMillis() - dateCredit.getTime();
                long delayMs = 5 * 60 * 1000 - elapsedMs; // 5 minutes

                if (delayMs <= 0) {
                    // ⏳ Temps écoulé → annuler immédiatement
                    System.out.println("[RECOVERY] Temps écoulé → Annulation immédiate creditId=" + creditId);
                    annulerCreditSiNonValide(creditId, commandeId, utilisateurId);
                } else {
                    // 🕒 Temps restant → reprogrammer
                    long delayMinutes = delayMs / 60000;
                    System.out.println("[RECOVERY] Reprogrammation dans " + delayMinutes +
                            " min pour creditId=" + creditId);

                    scheduler.schedule(() -> {
                        annulerCreditSiNonValide(creditId, commandeId, utilisateurId);
                    }, delayMs, TimeUnit.MILLISECONDS);
                }
            }

        } catch (SQLException e) {
            System.err.println("[RECOVERY] ERREUR SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }



//    private void annulerCreditSiNonValide(int creditId, int commandeId, int utilisateurId) {
//        // ton code actuel d'annulation
//        System.out.println("Annulation automatique du crédit " + creditId);
//    }

//    private void annulerCreditSiNonValide(int creditId, int commandeId, int utilisateurId) {
//        String sqlCheckCredit = "SELECT STATUT FROM CREDIT WHERE ID = ?";
//        String sqlAnnulerCredit = "UPDATE CREDIT SET STATUT = 'ANNULE', UPDATED_AT = ? WHERE ID = ? AND STATUT = 'NON_VALIDE'";
//        String sqlAnnulerCommande = "UPDATE COMMANDE SET STATUT = 'ANNULE', UPDATED_AT = ? WHERE ID = ? AND STATUT = 'HIDDEN'";
//        String sqlInsertNotification = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) " +
//                                     "VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//
//            // Vérifier si le crédit n'est toujours pas validé
//            boolean estNonValide = false;
//            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckCredit)) {
//                stmtCheck.setInt(1, creditId);
//                ResultSet rs = stmtCheck.executeQuery();
//                if (rs.next() && "NON_VALIDE".equals(rs.getString("STATUT"))) {
//                    estNonValide = true;
//                }
//            }
//
//            if (estNonValide) {
//                // Annuler le crédit
//                try (PreparedStatement stmtAnnulerCredit = conn.prepareStatement(sqlAnnulerCredit)) {
//                    Timestamp now = new Timestamp(new Date().getTime());
//                    stmtAnnulerCredit.setTimestamp(1, now);
//                    stmtAnnulerCredit.setInt(2, creditId);
//                    stmtAnnulerCredit.executeUpdate();
//                }
//
//                // Annuler la commande
//                try (PreparedStatement stmtAnnulerCommande = conn.prepareStatement(sqlAnnulerCommande)) {
//                    Timestamp now = new Timestamp(new Date().getTime());
//                    stmtAnnulerCommande.setTimestamp(1, now);
//                    stmtAnnulerCommande.setInt(2, commandeId);
//                    stmtAnnulerCommande.executeUpdate();
//                }
//
//                // Notification d'annulation
//                try (PreparedStatement stmtNotification = conn.prepareStatement(sqlInsertNotification)) {
//                    Timestamp now = new Timestamp(new Date().getTime());
//                    String message = "Votre commande crédit a été automatiquement annulée car elle n'a pas été validée dans les délais.";
//                    
//                    stmtNotification.setString(1, "SYSTEM");
//                    stmtNotification.setInt(2, utilisateurId);
//                    stmtNotification.setString(3, message);
//                    stmtNotification.setTimestamp(4, now);
//                    stmtNotification.setTimestamp(5, now);
//                    stmtNotification.executeUpdate();
//                }
//                
//             // Envoi email d'annulation
//                UtilisateurDAO userdao = new UtilisateurDAO();   
//                Utilisateur user = userdao.findById(utilisateurId); 
//                String userEmail = user.getEmail(); // Méthode pour récupérer l'email de l'utilisateur
//                if (userEmail != null && !userEmail.trim().isEmpty()) {
//                	CommandeDAO commandeDAO = new CommandeDAO();
//    				//                envoyerEmailValidationCredit(userEmail, credit);
////                	 EmailUtils.envoyerEmailValidationCredit(userEmail, credit);
//                	List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
//                	EmailUtils.envoyerEmailAnnulationCredit(userEmail,details);
////                	EmailUtils.envoyerEmailAnnulationCredit(userEmail, creditId);
//                }
//
//                conn.commit();
//                System.out.println("Crédit " + creditId + " annulé automatiquement après 5 minutes");
//            } else {
//                conn.rollback();
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Erreur lors de l'annulation automatique: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    
    
    private void annulerCreditSiNonValide(int creditId, int commandeId, int utilisateurId) {

        System.out.println("[AUTO-ANNULATION] Début annulerCreditSiNonValide()");
        System.out.println("[AUTO-ANNULATION] creditId=" + creditId + ", commandeId=" + commandeId + ", userId=" + utilisateurId);

        String sqlCheckCredit = "SELECT STATUT FROM CREDIT WHERE ID = ?";

        try (Connection conn = getConnection()) {

            System.out.println("[AUTO-ANNULATION] Connexion obtenue");
            conn.setAutoCommit(false);

            boolean estNonValide = false;

            // Vérification du statut actuel
            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckCredit)) {
                stmtCheck.setInt(1, creditId);
                System.out.println("[AUTO-ANNULATION] Vérification du statut du crédit...");

                ResultSet rs = stmtCheck.executeQuery();

                if (rs.next()) {
                    String statut = rs.getString("STATUT");
                    System.out.println("[AUTO-ANNULATION] STATUT ACTUEL DU CREDIT = " + statut);

                    if ("NON_VALIDE".equals(statut)) {
                        estNonValide = true;
                    }
                } else {
                    System.out.println("[AUTO-ANNULATION] ERREUR : Aucun crédit trouvé pour ID=" + creditId);
                }
            }

            // Si plus NON_VALIDE → ne rien faire
            if (!estNonValide) {
                System.out.println("[AUTO-ANNULATION] Le crédit n'est plus NON_VALIDE → STOP ANNULATION");
                conn.rollback();
                return;
            }

            // ANNULATION DE LA COMMANDE
            System.out.println("[AUTO-ANNULATION] → ANNULATION DE LA COMMANDE AVANT LE CREDIT");

            CommandeDAO commandeDAO = new CommandeDAO();
            boolean cmdAnnulee = commandeDAO.modifierStatutCommande(commandeId, "ANNULE", 30);

            System.out.println("[AUTO-ANNULATION] Résultat annulation commande : " + cmdAnnulee);

            if (!cmdAnnulee) {
                System.out.println("[AUTO-ANNULATION] Impossible d'annuler la commande, rollback...");
                conn.rollback();
                return;
            }

            // ANNULATION DU CREDIT
            System.out.println("[AUTO-ANNULATION] → ANNULATION DU CREDIT");

            String sqlAnnulerCredit = 
                    "UPDATE CREDIT SET STATUT = 'ANNULE', UPDATED_AT = ? WHERE ID = ? AND STATUT = 'NON_VALIDE'";

            try (PreparedStatement stmtAnnulerCredit = conn.prepareStatement(sqlAnnulerCredit)) {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                stmtAnnulerCredit.setTimestamp(1, now);
                stmtAnnulerCredit.setInt(2, creditId);

                int rows = stmtAnnulerCredit.executeUpdate();
                System.out.println("[AUTO-ANNULATION] Crédit annulé ? rows=" + rows);
            }

            // INSERT NOTIFICATION
            System.out.println("[AUTO-ANNULATION] → INSERTION NOTIFICATION");

            String sqlInsertNotification =
                "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) "
                + "VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";

            try (PreparedStatement stmtNotif = conn.prepareStatement(sqlInsertNotification)) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                String message =
                    "Votre commande crédit a été automatiquement annulée car elle n'a pas été validée dans les délais.";

                stmtNotif.setString(1, "SYSTEM");
                stmtNotif.setInt(2, utilisateurId);
                stmtNotif.setString(3, message);
                stmtNotif.setTimestamp(4, now);
                stmtNotif.setTimestamp(5, now);

                int notifRows = stmtNotif.executeUpdate();
                System.out.println("[AUTO-ANNULATION] Notification insérée ? rows=" + notifRows);
            }

            // ENVOI EMAIL
            System.out.println("[AUTO-ANNULATION] → ENVOI EMAIL");

            UtilisateurDAO userDAO = new UtilisateurDAO();
            Utilisateur user = userDAO.findById(utilisateurId);

            if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {

                CommandeDAO cDAO = new CommandeDAO();
                List<CommandeDetail> details = cDAO.getDetailsByCommandeId(commandeId);

                System.out.println("[AUTO-ANNULATION] Envoi email à " + user.getEmail());
                EmailUtils.envoyerEmailAnnulationCredit(user.getEmail(), details);

            } else {
                System.out.println("[AUTO-ANNULATION] EMAIL NON ENVOYÉ – utilisateur sans email");
            }

            conn.commit();
            System.out.println("[AUTO-ANNULATION] ✓ Succès : crédit " + creditId + " et commande " + commandeId + " annulés");

        } catch (SQLException e) {
            System.err.println("[AUTO-ANNULATION] ERREUR SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Commande> getCommandesHiddenAvecCreditsNonValides(int userId) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, cr.ID as CREDIT_ID, cr.MONTANT_TOTAL, cr.STATUT as CREDIT_STATUT " +
                     "FROM COMMANDE c " +
                     "INNER JOIN CREDIT cr ON c.ID = cr.COMMANDE_ID " +
                     "WHERE c.STATUT = 'HIDDEN' " +
                     "AND cr.STATUT = 'NON_VALIDE' " +
                     "AND cr.UTILISATEUR_ID = ? " +
                     "ORDER BY c.CREATED_AT DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("ID"));
                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
                commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
                commande.setStatut(rs.getString("STATUT"));
                
                // Informations sur le crédit
                Credit credit = new Credit();
                credit.setId(rs.getInt("CREDIT_ID"));
                credit.setMontantTotal(rs.getInt("MONTANT_TOTAL"));
                credit.setStatut(rs.getString("CREDIT_STATUT"));
                commande.setCredit(credit);
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public int ajouterCredit_v2(Credit credit, boolean toValidate) {
        String sqlCredit = "INSERT INTO CREDIT (UTILISATEUR_ID, COMMANDE_ID, MONTANT_TOTAL, MONTANT_PAYE, STATUT, DATE_CREDIT, CREATED_AT, UPDATED_AT) " +
                         "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            int creditId;
            String statutInitial;
            
            // Déterminer le statut initial en fonction de toValidate
            if (toValidate) {
                statutInitial = "NON_VALIDE"; // Besoin de validation
            } else {
                statutInitial = "NON_PAYE"; // Pas besoin de validation, directement actif
            }
            
            // 1. Insérer le crédit
            try (PreparedStatement stmt = conn.prepareStatement(sqlCredit, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, credit.getUtilisateurId());
                stmt.setInt(2, credit.getCommandeId());
                stmt.setInt(3, credit.getMontantTotal());
                stmt.setInt(4, credit.getMontantPaye() != 0 ? credit.getMontantPaye() : 0);
                stmt.setString(5, statutInitial);
                Timestamp now = new Timestamp(new Date().getTime());
                stmt.setTimestamp(6, now);
                stmt.setTimestamp(7, now);

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        creditId = rs.getInt(1);
                        credit.setId(creditId);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // 2. Gestion de la commande et notifications selon toValidate
            if (toValidate) {
                // Si besoin de validation : cacher la commande, envoyer notification et email
                String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT = 'HIDDEN', UPDATED_AT = ? WHERE ID = ?";
                String sqlInsertNotification = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) " +
                                             "VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";
                String sqlSelectUserEmail = "SELECT EMAIL FROM UTILISATEUR WHERE ID = ?";
                
                // 2a. Mettre la commande en HIDDEN
                try (PreparedStatement stmtUpdateCommande = conn.prepareStatement(sqlUpdateCommande)) {
                    Timestamp now = new Timestamp(new Date().getTime());
                    stmtUpdateCommande.setTimestamp(1, now);
                    stmtUpdateCommande.setInt(2, credit.getCommandeId());
                    stmtUpdateCommande.executeUpdate();
                }
                
                // 2b. Récupérer l'email de l'utilisateur
                String userEmail = null;
                try (PreparedStatement stmtSelectEmail = conn.prepareStatement(sqlSelectUserEmail)) {
                    stmtSelectEmail.setInt(1, credit.getUtilisateurId());
                    ResultSet rs = stmtSelectEmail.executeQuery();
                    if (rs.next()) {
                        userEmail = rs.getString("EMAIL");
                    }
                }
                
                // 2c. Insérer la notification
                try (PreparedStatement stmtNotification = conn.prepareStatement(sqlInsertNotification)) {
                    Timestamp now = new Timestamp(new Date().getTime());
                    String message = "Une commande crédit a été effectuée sur votre compte. Montant: " + 
                                   credit.getMontantTotal() + " HTG. Veuillez valider dans les 5 minutes.";
                    
                    stmtNotification.setString(1, "SYSTEM");
                    stmtNotification.setInt(2, credit.getUtilisateurId());
                    stmtNotification.setString(3, message);
                    stmtNotification.setTimestamp(4, now);
                    stmtNotification.setTimestamp(5, now);
                    stmtNotification.executeUpdate();
                }
                
                conn.commit();
                
                // 2d. Envoyer l'email (en dehors de la transaction)
                if (userEmail != null && !userEmail.trim().isEmpty()) {
                    try {
                        CommandeDAO commandeDAO = new CommandeDAO();
                        List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(credit.getCommandeId());
                        EmailUtils.envoyerEmailValidationCredit(userEmail, credit, details);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                        // Ne pas retourner d'erreur pour une erreur d'email
                    }
                }
                
                // 2e. Programmer l'annulation automatique après 5 minutes
                programmerAnnulationAutomatique(creditId, credit.getCommandeId(), credit.getUtilisateurId());
                
            } else {
                // Si pas besoin de validation : laisser la commande visible
                // Créer une notification simple
                String sqlInsertNotification = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) " +
                                             "VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";
                
                try (PreparedStatement stmtNotification = conn.prepareStatement(sqlInsertNotification)) {
                    Timestamp now = new Timestamp(new Date().getTime());
                    String message = "Nouvelle commande crédit enregistrée. Montant: " + 
                                   credit.getMontantTotal() + " HTG.";
                    
                    stmtNotification.setString(1, "SYSTEM");
                    stmtNotification.setInt(2, credit.getUtilisateurId());
                    stmtNotification.setString(3, message);
                    stmtNotification.setTimestamp(4, now);
                    stmtNotification.setTimestamp(5, now);
                    stmtNotification.executeUpdate();
                }
                
                conn.commit();
            }

            return creditId;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public boolean validerCredit(int creditId, int utilisateurId) {

        System.out.println("=== [DAO] Début validerCredit ===");
        System.out.println("[DAO] creditId = " + creditId);
        System.out.println("[DAO] utilisateurId = " + utilisateurId);

        String sqlUpdateCredit = "UPDATE CREDIT SET STATUT = 'NON_PAYE', UPDATED_AT = ? WHERE ID = ? AND UTILISATEUR_ID = ?";
        String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT = 'VISIBLE', UPDATED_AT = ? WHERE ID = (SELECT COMMANDE_ID FROM CREDIT WHERE ID = ?)";
        String sqlInsertNotification = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, CREATED_AT, UPDATED_AT, STATUS, IS_READ) VALUES (?, ?, ?, ?, ?, 'VISIBLE', 0)";

        try (Connection conn = getConnection()) {

            System.out.println("[DAO] Connexion OK");

            conn.setAutoCommit(false);
            Timestamp now = new Timestamp(new Date().getTime());

            // --- 1. VALIDER LE CRÉDIT ---
            System.out.println("[DAO] Exécution SQL Update Credit : " + sqlUpdateCredit);

            int rowsUpdated;
            try (PreparedStatement stmtCredit = conn.prepareStatement(sqlUpdateCredit)) {
                stmtCredit.setTimestamp(1, now);
                stmtCredit.setInt(2, creditId);
                stmtCredit.setInt(3, utilisateurId);

                rowsUpdated = stmtCredit.executeUpdate();
            }

            System.out.println("[DAO] rowsUpdated (CREDIT) = " + rowsUpdated);

            if (rowsUpdated == 0) {
                System.out.println("[DAO] Aucun crédit mis à jour -> ROLLBACK !");
                conn.rollback();
                return false;
            }

            // --- 2. Rendre COMMANDE visible ---
            System.out.println("[DAO] Exécution SQL Update Commande : " + sqlUpdateCommande);

            try (PreparedStatement stmtCommande = conn.prepareStatement(sqlUpdateCommande)) {
                stmtCommande.setTimestamp(1, now);
                stmtCommande.setInt(2, creditId);
                int rowsCmd = stmtCommande.executeUpdate();

                System.out.println("[DAO] rowsUpdated (COMMANDE) = " + rowsCmd);
            }

            // --- 3. Notification ---
            System.out.println("[DAO] Exécution SQL Notification");

            try (PreparedStatement stmtNotification = conn.prepareStatement(sqlInsertNotification)) {

                String message = "Votre commande crédit a été validée avec succès.";

                stmtNotification.setString(1, "SYSTEM");
                stmtNotification.setInt(2, utilisateurId);
                stmtNotification.setString(3, message);
                stmtNotification.setTimestamp(4, now);
                stmtNotification.setTimestamp(5, now);
                stmtNotification.executeUpdate();
            }

            conn.commit();
            System.out.println("[DAO] COMMIT OK – Crédit validé !");
            return true;

        } catch (SQLException e) {
            System.out.println("[DAO] ERREUR SQL !");
            e.printStackTrace();
            return false;
        }
    }

    // Payer partiellement
//
//    public boolean payerCredit(int creditId, BigDecimal montant, int utilisateurId) {
//        String sqlCredit = "UPDATE CREDIT SET MONTANT_PAYE = MONTANT_PAYE + ?, " +
//                           "STATUT = CASE " +
//                           "WHEN MONTANT_PAYE + ? < MONTANT_TOTAL THEN 'PARTIEL' " +
//                           "WHEN MONTANT_PAYE + ? >= MONTANT_TOTAL THEN 'PAYE' " +
//                           "ELSE STATUT END, " +
//                           "UPDATED_AT = NOW() WHERE ID = ?";
//
//        String sqlVersement = "INSERT INTO VERSEMENT (CREDIT_ID, UTILISATEUR_ID, MONTANT, DATE_VERSEMENT, CREATED_AT, UPDATED_AT) " +
//                              "VALUES (?, ?, ?, NOW(), NOW(), NOW())";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false); // début de transaction
//
//            // 1️⃣ Mise à jour du crédit
//            try (PreparedStatement stmtCredit = conn.prepareStatement(sqlCredit)) {
//                stmtCredit.setBigDecimal(1, montant);
//                stmtCredit.setBigDecimal(2, montant);
//                stmtCredit.setBigDecimal(3, montant);
//                stmtCredit.setInt(4, creditId);
//
//                int updated = stmtCredit.executeUpdate();
//                if (updated == 0) {
//                    conn.rollback();
//                    return false;
//                }
//            }
//
//            // 2️⃣ Ajout du versement
//            try (PreparedStatement stmtVersement = conn.prepareStatement(sqlVersement)) {
//                stmtVersement.setInt(1, creditId);
//                stmtVersement.setInt(2, utilisateurId);
//                stmtVersement.setBigDecimal(3, montant);
//                stmtVersement.executeUpdate();
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
    
 // Payer un montant global réparti sur plusieurs crédits
//    public boolean payerCreditGlobal(int utilisateurId, int montantTotal) {
//        String sqlCredits = "SELECT ID, MONTANT_TOTAL, MONTANT_PAYE FROM CREDIT " +
//                            "WHERE UTILISATEUR_ID = ? AND (STATUT = 'NON_PAYE' OR STATUT = 'PARTIEL') " +
//                            "ORDER BY DATE_CREDIT ASC";
//
//        String sqlUpdateCredit = "UPDATE CREDIT SET MONTANT_PAYE = ?, " +
//                                 "STATUT = CASE WHEN ? < MONTANT_TOTAL THEN 'PARTIEL' ELSE 'PAYE' END, " +
//                                 "UPDATED_AT = NOW() WHERE ID = ?";
//
//        String sqlInsertVersement = "INSERT INTO VERSEMENT (CREDIT_ID, UTILISATEUR_ID, MONTANT, DATE_VERSEMENT, CREATED_AT, UPDATED_AT) " +
//                                    "VALUES (?, ?, ?, NOW(), NOW(), NOW())";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//
//            try (PreparedStatement stmtCredits = conn.prepareStatement(sqlCredits, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
//                stmtCredits.setInt(1, utilisateurId);
//                try (ResultSet rs = stmtCredits.executeQuery()) {
//                    int restant = montantTotal;
//
//                    while (rs.next() && restant > 0) {
//                        int creditId = rs.getInt("ID");
//                        int montantTotalCredit = rs.getInt("MONTANT_TOTAL");
//                        int montantPayeActuel = rs.getInt("MONTANT_PAYE");
//                        int soldeCredit = montantTotalCredit - montantPayeActuel;
//
//                        int montantVersement = Math.min(restant, soldeCredit);
//                        int nouveauMontantPaye = montantPayeActuel + montantVersement;
//
//                        // 1️⃣ Mettre à jour le crédit
//                        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateCredit)) {
//                            stmtUpdate.setInt(1, nouveauMontantPaye);
//                            stmtUpdate.setInt(2, nouveauMontantPaye);
//                            stmtUpdate.setInt(3, creditId);
//                            stmtUpdate.executeUpdate();
//                        }
//
//                        // 2️⃣ Ajouter le versement
//                        try (PreparedStatement stmtVersement = conn.prepareStatement(sqlInsertVersement)) {
//                            stmtVersement.setInt(1, creditId);
//                            stmtVersement.setInt(2, utilisateurId);
//                            stmtVersement.setInt(3, montantVersement);
//                            stmtVersement.executeUpdate();
//                        }
//
//                        // Décrémenter le montant restant à payer
//                        restant -= montantVersement;
//                    }
//
//                    conn.commit();
//                    return true;
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    
//    public boolean payerCreditGlobal(int utilisateurId, int montantTotal) {
//        String sqlCredits = "SELECT ID, MONTANT_TOTAL, MONTANT_PAYE, STATUT FROM CREDIT " +
//                            "WHERE UTILISATEUR_ID = ? AND (STATUT = 'NON_PAYE' OR STATUT = 'PARTIEL') " +
//                            "ORDER BY DATE_CREDIT ASC";
//
//        String sqlUpdateCredit = "UPDATE CREDIT SET MONTANT_PAYE = ?, " +
//                                 "STATUT = CASE WHEN ? < MONTANT_TOTAL THEN 'PARTIEL' ELSE 'PAYE' END, " +
//                                 "UPDATED_AT = NOW() WHERE ID = ?";
//
//        String sqlInsertVersement = "INSERT INTO VERSEMENT (CREDIT_ID, UTILISATEUR_ID, MONTANT, DATE_VERSEMENT, CREATED_AT, UPDATED_AT) " +
//                                    "VALUES (?, ?, ?, NOW(), NOW(), NOW())";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//
//            try (PreparedStatement stmtCredits = conn.prepareStatement(sqlCredits, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
//                stmtCredits.setInt(1, utilisateurId);
//                System.out.println("=== Requête PAYER CREDIT ===");
//                System.out.println("Param userId: " + utilisateurId);
//                System.out.println("Param montantVerse: " + montantTotal);
//                System.out.println("SQL exécuté: " + sqlCredits);
//
//                try (ResultSet rs = stmtCredits.executeQuery()) {
//                    if (!rs.isBeforeFirst()) {
//                        System.out.println("⚠️ Aucun crédit trouvé pour l'utilisateur " + utilisateurId);
//                        return false; // pas de crédits à payer
//                    }
//
//                    int restant = montantTotal;
//
//                    while (rs.next() && restant > 0) {
//                        int creditId = rs.getInt("ID");
//                        int montantTotalCredit = rs.getInt("MONTANT_TOTAL");
//                        int montantPayeActuel = rs.getInt("MONTANT_PAYE");
//                        String statut = rs.getString("STATUT");
//                        int soldeCredit = montantTotalCredit - montantPayeActuel;
//                        int montantVersement = Math.min(restant, soldeCredit);
//                        int nouveauMontantPaye = montantPayeActuel + montantVersement;
//
//                        System.out.println("Traitement crédit ID=" + creditId + 
//                                           ", Montant total=" + montantTotalCredit +
//                                           ", Montant payé=" + montantPayeActuel +
//                                           ", Statut=" + statut +
//                                           ", Solde=" + soldeCredit +
//                                           ", Montant versement=" + montantVersement +
//                                           ", Restant à payer=" + restant);
//
//                        // 1️⃣ Mettre à jour le crédit
//                        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateCredit)) {
//                            stmtUpdate.setInt(1, nouveauMontantPaye);
//                            stmtUpdate.setInt(2, nouveauMontantPaye);
//                            stmtUpdate.setInt(3, creditId);
//                            int rows = stmtUpdate.executeUpdate();
//                            System.out.println("Rows mis à jour dans CREDIT: " + rows);
//                        }
//
//                        // 2️⃣ Ajouter le versement
//                        try (PreparedStatement stmtVersement = conn.prepareStatement(sqlInsertVersement)) {
//                            stmtVersement.setInt(1, creditId);
//                            stmtVersement.setInt(2, utilisateurId);
//                            stmtVersement.setInt(3, montantVersement);
//                            int rowsVersement = stmtVersement.executeUpdate();
//                            System.out.println("Rows insérées dans VERSEMENT: " + rowsVersement);
//                        }
//
//                        restant -= montantVersement;
//                    }
//
//                    conn.commit();
//                    System.out.println("Paiement finalisé avec succès.");
//                    return true;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean payerCreditGlobal(int utilisateurId, int montantTotal) {
        String sqlCredits = "SELECT ID, COMMANDE_ID, MONTANT_TOTAL, MONTANT_PAYE, STATUT FROM CREDIT " +
                            "WHERE UTILISATEUR_ID = ? AND (STATUT = 'NON_PAYE' OR STATUT = 'PARTIEL') " +
                            "ORDER BY DATE_CREDIT ASC";

        String sqlUpdateCredit = "UPDATE CREDIT SET MONTANT_PAYE = ?, " +
                                 "STATUT = CASE WHEN ? < MONTANT_TOTAL THEN 'PARTIEL' ELSE 'PAYE' END, " +
                                 "UPDATED_AT = NOW() WHERE ID = ?";

        String sqlInsertVersement = "INSERT INTO VERSEMENT (CREDIT_ID, UTILISATEUR_ID, MONTANT, DATE_VERSEMENT, CREATED_AT, UPDATED_AT) " +
                                    "VALUES (?, ?, ?, NOW(), NOW(), NOW())";

        String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT_PAIEMENT = ? WHERE ID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCredits = conn.prepareStatement(sqlCredits)) {
                stmtCredits.setInt(1, utilisateurId);
                System.out.println("=== Requête PAYER CREDIT ===");
                System.out.println("Param userId: " + utilisateurId);
                System.out.println("Param montantVerse: " + montantTotal);
                System.out.println("SQL exécuté: " + sqlCredits);

                try (ResultSet rs = stmtCredits.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("⚠️ Aucun crédit trouvé pour l'utilisateur " + utilisateurId);
                        return false; // pas de crédits à payer
                    }

                    int restant = montantTotal;

                    while (rs.next() && restant > 0) {
                        int creditId = rs.getInt("ID");
                        int commandeId = rs.getInt("COMMANDE_ID");
                        int montantTotalCredit = rs.getInt("MONTANT_TOTAL");
                        int montantPayeActuel = rs.getInt("MONTANT_PAYE");
                        String statut = rs.getString("STATUT");
                        int soldeCredit = montantTotalCredit - montantPayeActuel;
                        int montantVersement = Math.min(restant, soldeCredit);
                        int nouveauMontantPaye = montantPayeActuel + montantVersement;

                        System.out.println("Traitement crédit ID=" + creditId + 
                                           ", Montant total=" + montantTotalCredit +
                                           ", Montant payé=" + montantPayeActuel +
                                           ", Statut=" + statut +
                                           ", Solde=" + soldeCredit +
                                           ", Montant versement=" + montantVersement +
                                           ", Restant à payer=" + restant);

                        // 1️⃣ Mettre à jour le crédit
                        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateCredit)) {
                            stmtUpdate.setInt(1, nouveauMontantPaye);
                            stmtUpdate.setInt(2, nouveauMontantPaye);
                            stmtUpdate.setInt(3, creditId);
                            int rows = stmtUpdate.executeUpdate();
                            System.out.println("Rows mis à jour dans CREDIT: " + rows);
                        }

                        // 2️⃣ Ajouter le versement
                        try (PreparedStatement stmtVersement = conn.prepareStatement(sqlInsertVersement)) {
                            stmtVersement.setInt(1, creditId);
                            stmtVersement.setInt(2, utilisateurId);
                            stmtVersement.setInt(3, montantVersement);
                            int rowsVersement = stmtVersement.executeUpdate();
                            System.out.println("Rows insérées dans VERSEMENT: " + rowsVersement);
                        }

                        // 🔹 Mise à jour du statut de la commande
//                        try (PreparedStatement stmtCmd = conn.prepareStatement(
//                                "SELECT COUNT(*) AS total, SUM(CASE WHEN STATUT = 'PAYE' THEN 1 ELSE 0 END) AS payeCount " +
//                                "FROM CREDIT WHERE COMMANDE_ID = ?")) {
//                            stmtCmd.setInt(1, commandeId);
//                            try (ResultSet rsCmd = stmtCmd.executeQuery()) {
//                                if (rsCmd.next()) {
//                                    int total = rsCmd.getInt("total");
//                                    int payeCount = rsCmd.getInt("payeCount");
//                                    String statutCommande = (payeCount == total) ? "PAYE" : "PARTIEL";
//
//                                    try (PreparedStatement stmtUpdateCmd = conn.prepareStatement(sqlUpdateCommande)) {
//                                        stmtUpdateCmd.setString(1, statutCommande);
//                                        stmtUpdateCmd.setInt(2, commandeId);
//                                        int rowsCmd = stmtUpdateCmd.executeUpdate();
//                                        System.out.println("Commande ID=" + commandeId + " mise à jour avec statut " + statutCommande + " (" + rowsCmd + " row(s) affectée(s))");
//                                    }
//                                }
//                            }
//                        }
                     // 🔹 Mise à jour du statut de la commande et du mode de paiement
                        try (PreparedStatement stmtCmd = conn.prepareStatement(
                                "SELECT COUNT(*) AS total, SUM(CASE WHEN STATUT = 'PAYE' THEN 1 ELSE 0 END) AS payeCount " +
                                "FROM CREDIT WHERE COMMANDE_ID = ?")) {
                            stmtCmd.setInt(1, commandeId);
                            try (ResultSet rsCmd = stmtCmd.executeQuery()) {
                                if (rsCmd.next()) {
                                    int total = rsCmd.getInt("total");
                                    int payeCount = rsCmd.getInt("payeCount");
                                    String statutCommande = (payeCount == total) ? "PAYE" : "PARTIEL";

                                    // Mettre à jour la commande
                                    String sqlUpdateCommandeFinal = "UPDATE COMMANDE SET STATUT_PAIEMENT = ?, MODE_PAIEMENT = ? WHERE ID = ?";
                                    try (PreparedStatement stmtUpdateCmd = conn.prepareStatement(sqlUpdateCommandeFinal)) {
                                        stmtUpdateCmd.setString(1, statutCommande);
                                        stmtUpdateCmd.setString(2, (statutCommande.equals("PAYE") ? "CASH" : null)); // CASH si payé
                                        stmtUpdateCmd.setInt(3, commandeId);
                                        int rowsCmd = stmtUpdateCmd.executeUpdate();
                                        System.out.println("Commande ID=" + commandeId + " mise à jour avec statut " + statutCommande + " et mode paiement " + (statutCommande.equals("PAYE") ? "CASH" : "NULL") + " (" + rowsCmd + " row(s) affectée(s))");
                                    }
                                }
                            }
                        }


                        restant -= montantVersement;
                    }

                    conn.commit();
                    System.out.println("Paiement finalisé avec succès.");
                    return true;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Payer tout le crédit
    public boolean payerToutCredit(int creditId) {
        String sql = "UPDATE CREDIT SET MONTANT_PAYE = MONTANT_TOTAL, STATUT = 'PAYE', UPDATED_AT = NOW() WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creditId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Liste des crédits par utilisateur avec total restant
    public List<Credit> getCreditsByUtilisateur(int utilisateurId) {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM CREDIT WHERE UTILISATEUR_ID = ? ORDER BY DATE_CREDIT DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(mapCredit(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits;
    }

    // Total restant pour un utilisateur
    public BigDecimal getTotalCreditRestant(int utilisateurId) {
        String sql = "SELECT SUM(MONTANT_TOTAL - MONTANT_PAYE) AS total_restant FROM CREDIT WHERE UTILISATEUR_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_restant") != null ? rs.getBigDecimal("total_restant") : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // Filtrer crédits
    public List<Credit> filtrerCredits(Integer utilisateurId, Timestamp dateDebut, Timestamp dateFin) {
        List<Credit> credits = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM CREDIT WHERE 1=1 ");
        if (utilisateurId != null) sql.append(" AND UTILISATEUR_ID = ?");
        if (dateDebut != null) sql.append(" AND DATE_CREDIT >= ?");
        if (dateFin != null) sql.append(" AND DATE_CREDIT <= ?");
        sql.append(" ORDER BY DATE_CREDIT DESC");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (utilisateurId != null) stmt.setInt(index++, utilisateurId);
            if (dateDebut != null) stmt.setTimestamp(index++, dateDebut);
            if (dateFin != null) stmt.setTimestamp(index++, dateFin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(mapCredit(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits;
    }

    // Liste des crédits du jour
    public List<Credit> getCreditsDuJour() {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM CREDIT WHERE DATE(DATE_CREDIT) = CURDATE() ORDER BY DATE_CREDIT DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                credits.add(mapCredit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credits;
    }

    // Vérifier plafond
    public boolean plafondDepasse(int utilisateurId, BigDecimal plafond) {
        BigDecimal restant = getTotalCreditRestant(utilisateurId);
        return restant.compareTo(plafond) >= 0;
    }
    
    public List<Commande> getCommandesCredit(Integer userId, Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
        List<Commande> commandes = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT c.*, cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT " +
            "FROM COMMANDE c " +
            "JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID " +
            "WHERE (cr.STATUT = 'NON_PAYE' OR cr.STATUT = 'PARTIEL') "
        );

        if (userId != null) sql.append("AND c.UTILISATEUR_ID = ? ");
        if (clientId != null) sql.append("AND c.CLIENT_ID = ? ");
        if (dateDebut != null) sql.append("AND c.DATE_COMMANDE >= ? ");
        if (dateFin != null) sql.append("AND c.DATE_COMMANDE <= ? ");

        sql.append("ORDER BY c.DATE_COMMANDE ASC");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (userId != null) stmt.setInt(index++, userId);
            if (clientId != null) stmt.setInt(index++, clientId);
            if (dateDebut != null) stmt.setTimestamp(index++, dateDebut);
            if (dateFin != null) stmt.setTimestamp(index++, dateFin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = new Commande();
                    commande.setId(rs.getInt("ID"));
                    commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
                    commande.setClientId(rs.getInt("CLIENT_ID"));
                    commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
                    commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
                    commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
                    commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
                    commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
                    commande.setNotes(rs.getString("NOTES"));
                    commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                    commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
                    commande.setCashedBy(rs.getInt("CASHED_BY"));

                    // Vérification que l'utilisateur courant a accès à cette commande
                    if (userId != null && commande.getUtilisateurId() != userId) continue;

                    // Charger le crédit associé
                    Credit credit = new Credit();
                    credit.setId(rs.getInt("credit_ID"));
                    credit.setMontantTotal(rs.getInt("credit_TOTAL"));
                    credit.setMontantPaye(rs.getInt("credit_PAYE"));
                    credit.setStatut(rs.getString("credit_STATUT"));
                    commande.setCredit(credit);  // il faut ajouter un champ Credit dans Commande
                    commande.setIsCredit(false);  // logique côté Java

                    commandes.add(commande);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }


    // Supprimer un crédit (soft delete)
    public boolean supprimerCredit(int creditId, int deletedBy) {
        String sql = "UPDATE CREDIT SET STATUT = 'DELETED', UPDATED_AT = NOW() WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creditId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mapping ResultSet -> Credit
    private Credit mapCredit(ResultSet rs) throws SQLException {
        Credit credit = new Credit();
        credit.setId(rs.getInt("ID"));
        credit.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        credit.setCommandeId(rs.getInt("COMMANDE_ID"));
        credit.setMontantTotal(rs.getInt("MONTANT_TOTAL"));
        credit.setMontantPaye(rs.getInt("MONTANT_PAYE"));
        credit.setStatut(rs.getString("STATUT"));
        credit.setDateCredit(rs.getTimestamp("DATE_CREDIT"));
        credit.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        credit.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

        // Charger utilisateur associé
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        credit.setUtilisateur(utilisateurDAO.findById(rs.getInt("UTILISATEUR_ID")));

        // Charger commande associée
        CommandeDAO commandeDAO = new CommandeDAO();
        credit.setCommande(commandeDAO.getCommandeAvecDetails(rs.getInt("COMMANDE_ID")));

        return credit;
    }
}
