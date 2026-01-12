package com.spot69.dao;

import com.spot69.model.Reservation;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une réservation avec tous les nouveaux champs
//    public boolean ajouterReservation(Reservation reservation) {
//        String sql = "INSERT INTO RESERVATIONS (ROOM_ID, START_DATE, END_DATE, TYPE, STATUS, TITLE, " +
//                     "PRIX_TOTAL, ARRIVAL_TIME, NUMBER_OF_NIGHTS, NUMBER_OF_SLOTS, DURATION_HOURS, " +
//                     "UTILISATEUR_ID, PAYMENT_METHOD, PAYER_NAME, PAYER_PHONE, TRANSACTION_ID, " +
//                     "PAYMENT_NOTE, PAYMENT_STATUS) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            
//            stmt.setString(1, reservation.getRoomId());
//            stmt.setTimestamp(2, new Timestamp(reservation.getStart().getTime()));
//            stmt.setTimestamp(3, new Timestamp(reservation.getEnd().getTime()));
//            stmt.setString(4, reservation.getType());
//            stmt.setString(5, reservation.getStatus() != null ? reservation.getStatus() : "en cours");
//            stmt.setString(6, reservation.getTitle());
//            stmt.setBigDecimal(7, reservation.getPrixTotal());
//            stmt.setString(8, reservation.getArrivalTime());
//            stmt.setObject(9, reservation.getNumberOfNights());
//            stmt.setObject(10, reservation.getNumberOfSlots());
//            stmt.setObject(11, reservation.getDurationHours());
//            stmt.setObject(12, reservation.getUtilisateurId());
//            
//            // NOUVEAUX CHAMPS DE PAIEMENT
//            stmt.setString(13, reservation.getPaymentMethod());
//            stmt.setString(14, reservation.getPayerName());
//            stmt.setString(15, reservation.getPayerPhone());
//            stmt.setString(16, reservation.getTransactionId());
//            stmt.setString(17, reservation.getPaymentNote());
//            stmt.setString(18, reservation.getPaymentStatus());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public String ajouterReservation(Reservation reservation) {
        String sql = "INSERT INTO RESERVATIONS (ROOM_ID, START_DATE, END_DATE, TYPE, STATUS, TITLE, " +
                     "PRIX_TOTAL, ARRIVAL_TIME, NUMBER_OF_NIGHTS, NUMBER_OF_SLOTS, DURATION_HOURS, " +
                     "UTILISATEUR_ID, PAYMENT_METHOD, PAYER_NAME, PAYER_PHONE, TRANSACTION_ID, " +
                     "PAYMENT_NOTE, PAYMENT_STATUS, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, reservation.getRoomId());
            stmt.setTimestamp(2, new Timestamp(reservation.getStart().getTime()));
            stmt.setTimestamp(3, new Timestamp(reservation.getEnd().getTime()));
            stmt.setString(4, reservation.getType());
            stmt.setString(5, reservation.getStatus() != null ? reservation.getStatus() : "en cours");
            stmt.setString(6, reservation.getTitle());
            stmt.setBigDecimal(7, reservation.getPrixTotal());
            stmt.setString(8, reservation.getArrivalTime());
            stmt.setObject(9, reservation.getNumberOfNights());
            stmt.setObject(10, reservation.getNumberOfSlots());
            stmt.setObject(11, reservation.getDurationHours());
            stmt.setObject(12, reservation.getUtilisateurId());
            
            // NOUVEAUX CHAMPS DE PAIEMENT
            stmt.setString(13, reservation.getPaymentMethod());
            stmt.setString(14, reservation.getPayerName());
            stmt.setString(15, reservation.getPayerPhone());
            stmt.setString(16, reservation.getTransactionId());
            stmt.setString(17, reservation.getPaymentNote());
            stmt.setString(18, reservation.getPaymentStatus());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String generatedId = String.valueOf(generatedKeys.getInt(1));
                        
                        // Mettre à jour l'objet reservation avec l'ID généré
                        reservation.setId(generatedId);
                        
                        System.out.println("✅ Réservation insérée avec ID: " + generatedId);
                        System.out.println("  Room ID: " + reservation.getRoomId());
                        System.out.println("  User ID: " + reservation.getUtilisateurId());
                        System.out.println("  Montant: " + reservation.getPrixTotal());
                        System.out.println("  Statut: " + reservation.getStatus());
                        
                        return generatedId;
                    }
                }
            }
            
            System.err.println("❌ Aucune ligne affectée lors de l'insertion de la réservation");
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'ajout de la réservation:");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }

    // Modifier une réservation avec tous les champs
    public boolean modifierReservation(Reservation reservation) {
        String sql = "UPDATE RESERVATIONS SET ROOM_ID = ?, START_DATE = ?, END_DATE = ?, TYPE = ?, " +
                     "STATUS = ?, TITLE = ?, PRIX_TOTAL = ?, ARRIVAL_TIME = ?, NUMBER_OF_NIGHTS = ?, " +
                     "NUMBER_OF_SLOTS = ?, DURATION_HOURS = ?, UPDATED_AT = ?, " +
                     "PAYMENT_METHOD = ?, PAYER_NAME = ?, PAYER_PHONE = ?, TRANSACTION_ID = ?, " +
                     "PAYMENT_NOTE = ?, PAYMENT_STATUS = ? WHERE ID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getRoomId());
            stmt.setTimestamp(2, new Timestamp(reservation.getStart().getTime()));
            stmt.setTimestamp(3, new Timestamp(reservation.getEnd().getTime()));
            stmt.setString(4, reservation.getType());
            stmt.setString(5, reservation.getStatus());
            stmt.setString(6, reservation.getTitle());
            stmt.setBigDecimal(7, reservation.getPrixTotal());
            stmt.setString(8, reservation.getArrivalTime());
            stmt.setObject(9, reservation.getNumberOfNights());
            stmt.setObject(10, reservation.getNumberOfSlots());
            stmt.setObject(11, reservation.getDurationHours());
            stmt.setTimestamp(12, new Timestamp(new Date().getTime()));
            
            // NOUVEAUX CHAMPS DE PAIEMENT
            stmt.setString(13, reservation.getPaymentMethod());
            stmt.setString(14, reservation.getPayerName());
            stmt.setString(15, reservation.getPayerPhone());
            stmt.setString(16, reservation.getTransactionId());
            stmt.setString(17, reservation.getPaymentNote());
            stmt.setString(18, reservation.getPaymentStatus());
            
            stmt.setString(19, reservation.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une réservation
    public boolean supprimerReservation(String id) {
        String sql = "DELETE FROM RESERVATIONS WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Rechercher une réservation par ID
    public Reservation chercherParId(String id) {
        String sql = "SELECT * FROM RESERVATIONS WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister toutes les réservations
    public List<Reservation> listerReservations() {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS ORDER BY START_DATE DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(extraireReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }

    // Lister les réservations par statut
    public List<Reservation> listerParStatut(String statut) {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS WHERE STATUS = ? ORDER BY START_DATE DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String normalizedStatut = normaliserStatutPourSQL(statut);
            stmt.setString(1, normalizedStatut);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les réservations par chambre
    public List<Reservation> listerParChambre(String roomId) {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS WHERE ROOM_ID = ? ORDER BY START_DATE DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    
    // Lister les réservations par utilisateur
    public List<Reservation> listerParUtilisateur(int utilisateurId) {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS WHERE UTILISATEUR_ID = ? ORDER BY START_DATE DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les réservations par utilisateur et statut
    public List<Reservation> listerParUtilisateurEtStatut(int utilisateurId, String statut) {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS WHERE UTILISATEUR_ID = ? AND STATUS = ? ORDER BY START_DATE DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String normalizedStatut = normaliserStatutPourSQL(statut);
            stmt.setInt(1, utilisateurId);
            stmt.setString(2, normalizedStatut);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Compter les réservations par utilisateur
    public int compterReservationsUtilisateur(int utilisateurId) {
        String sql = "SELECT COUNT(*) FROM RESERVATIONS WHERE UTILISATEUR_ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Vérifier les conflits avec utilisateur
    public boolean hasConflictAvecUtilisateur(String roomId, Date start, Date end, String excludeReservationId, int utilisateurId) {
        String sql = "SELECT COUNT(*) FROM RESERVATIONS WHERE ROOM_ID = ? AND STATUS != 'annulé' " +
                     "AND UTILISATEUR_ID = ? " +
                     "AND ((START_DATE BETWEEN ? AND ?) OR (END_DATE BETWEEN ? AND ?) " +
                     "OR (START_DATE <= ? AND END_DATE >= ?))";
        
        if (excludeReservationId != null && !excludeReservationId.isEmpty()) {
            sql += " AND ID != ?";
        }

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, roomId);
            stmt.setInt(paramIndex++, utilisateurId);
            stmt.setTimestamp(paramIndex++, new Timestamp(start.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(end.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(start.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(end.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(start.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(end.getTime()));
            
            if (excludeReservationId != null && !excludeReservationId.isEmpty()) {
                stmt.setString(paramIndex, excludeReservationId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Vérifier les conflits de réservation
    public boolean hasConflict(String roomId, Date start, Date end, String excludeReservationId) {
        String sql = "SELECT COUNT(*) FROM RESERVATIONS WHERE ROOM_ID = ? AND STATUS != 'annulé' " +
                     "AND ((START_DATE BETWEEN ? AND ?) OR (END_DATE BETWEEN ? AND ?) " +
                     "OR (START_DATE <= ? AND END_DATE >= ?))";
        
        if (excludeReservationId != null && !excludeReservationId.isEmpty()) {
            sql += " AND ID != ?";
        }

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            stmt.setTimestamp(2, new Timestamp(start.getTime()));
            stmt.setTimestamp(3, new Timestamp(end.getTime()));
            stmt.setTimestamp(4, new Timestamp(start.getTime()));
            stmt.setTimestamp(5, new Timestamp(end.getTime()));
            stmt.setTimestamp(6, new Timestamp(start.getTime()));
            stmt.setTimestamp(7, new Timestamp(end.getTime()));
            
            if (excludeReservationId != null && !excludeReservationId.isEmpty()) {
                stmt.setString(8, excludeReservationId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode utilitaire pour extraire une réservation depuis ResultSet avec tous les champs
    private Reservation extraireReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        
        // Informations de base
        reservation.setId(rs.getString("ID"));
        reservation.setRoomId(rs.getString("ROOM_ID"));
        reservation.setStart(rs.getTimestamp("START_DATE"));
        reservation.setEnd(rs.getTimestamp("END_DATE"));
        reservation.setType(rs.getString("TYPE"));
        
        // Normaliser le statut
        String dbStatus = rs.getString("STATUS");
        reservation.setStatus(dbStatus);
        
        reservation.setTitle(rs.getString("TITLE"));
        reservation.setPrixTotal(rs.getBigDecimal("PRIX_TOTAL"));
        reservation.setArrivalTime(rs.getString("ARRIVAL_TIME"));
        reservation.setNumberOfNights(rs.getObject("NUMBER_OF_NIGHTS") != null ? rs.getInt("NUMBER_OF_NIGHTS") : null);
        reservation.setNumberOfSlots(rs.getObject("NUMBER_OF_SLOTS") != null ? rs.getInt("NUMBER_OF_SLOTS") : null);
        reservation.setDurationHours(rs.getObject("DURATION_HOURS") != null ? rs.getInt("DURATION_HOURS") : null);
        reservation.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        reservation.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        reservation.setUtilisateurId(rs.getObject("UTILISATEUR_ID") != null ? rs.getInt("UTILISATEUR_ID") : null);
        
        // NOUVEAUX CHAMPS DE PAIEMENT
        reservation.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
        reservation.setPayerName(rs.getString("PAYER_NAME"));
        reservation.setPayerPhone(rs.getString("PAYER_PHONE"));
        reservation.setTransactionId(rs.getString("TRANSACTION_ID"));
        reservation.setPaymentNote(rs.getString("PAYMENT_NOTE"));
        reservation.setPaymentStatus(rs.getString("PAYMENT_STATUS"));
        
        return reservation;
    }
    
    // Méthode pour normaliser le statut pour les requêtes SQL
    private String normaliserStatutPourSQL(String statut) {
        if (statut == null) return null;
        switch(statut.toLowerCase()) {
            case "confirmed":
            case "confirmé":
                return "confirmé";
            case "pending":
            case "en cours":
                return "en cours";
            case "cancelled":
            case "annulé":
                return "annulé";
            default:
                return statut;
        }
    }
}