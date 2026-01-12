package com.spot69.dao;

import com.spot69.model.ReservationEvenement;
import com.spot69.model.ReservationTable;
import com.spot69.model.TypeTableEvenement;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationEvenementDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une réservation avec type de table
//    public int ajouter(ReservationEvenement reservation) {
//        String sql = "INSERT INTO reservation_evenement (EVENEMENT_ID, TYPE_TABLE_ID, UTILISATEUR_ID, " +
//                     "QUANTITE_TABLES, CAPACITE_TOTALE, MONTANT_TOTAL, MOYEN_PAIEMENT, STATUT, " +
//                     "NOM_PERSONNE, NUMERO_TRANSACTION, NUMERO_TRANSFERT) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            
//            stmt.setInt(1, reservation.getEvenementId());
//            stmt.setInt(2, reservation.getTypeTableId());
//            stmt.setInt(3, reservation.getUtilisateurId());
//            stmt.setInt(4, reservation.getQuantiteTables());
//            stmt.setInt(5, reservation.getCapaciteTotale());
//            stmt.setBigDecimal(6, reservation.getMontantTotal());
//            stmt.setString(7, reservation.getMoyenPaiement());
//            stmt.setString(8, reservation.getStatut() != null ? reservation.getStatut() : "EN_ATTENTE");
//            stmt.setString(9, reservation.getNomPersonne());
//            stmt.setString(10, reservation.getNumeroTransaction());
//            stmt.setString(11, reservation.getNumeroTransfert());
//            
//            int rowsAffected = stmt.executeUpdate();
//            
//            if (rowsAffected > 0) {
//                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        return generatedKeys.getInt(1);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Erreur lors de l'ajout de la réservation", e);
//        }
//        return -1;
//    }
//    
    public int ajouterAvecTables(ReservationEvenement reservation, List<ReservationTable> tables) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // 1. Créer la réservation principale
            String sqlReservation = "INSERT INTO RESERVATION_EVENEMENT " +
                                   "(EVENEMENT_ID, UTILISATEUR_ID, MOYEN_PAIEMENT, STATUT, " +
                                   "NOM_PERSONNE, NUMERO_TRANSACTION, NUMERO_TRANSFERT) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            int reservationId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sqlReservation, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, reservation.getEvenementId());
                stmt.setInt(2, reservation.getUtilisateurId());
                stmt.setString(3, reservation.getMoyenPaiement());
                stmt.setString(4, reservation.getStatut() != null ? reservation.getStatut() : "EN_ATTENTE");
                stmt.setString(5, reservation.getNomPersonne());
                stmt.setString(6, reservation.getNumeroTransaction());
                stmt.setString(7, reservation.getNumeroTransfert());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            reservationId = generatedKeys.getInt(1);
                        }
                    }
                }
            }
            
            if (reservationId == -1) {
                conn.rollback();
                return -1;
            }
            
            // 2. Ajouter les tables
            ReservationTableDAO tableDAO = new ReservationTableDAO();
            BigDecimal montantTotal = BigDecimal.ZERO;
            int capaciteTotale = 0;
            
            for (ReservationTable table : tables) {
                table.setReservationEvenementId(reservationId);
//                int tableId = tableDAO.ajouterDansTransaction(table, conn);
//                if (tableId == -1) {
//                    conn.rollback();
//                    return -1;
//                }
                
                // Calculer le total
                montantTotal = montantTotal.add(table.getMontant());
                // Calculer la capacité totale (nécessite l'objet TypeTable)
                TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
                TypeTableEvenement typeTable = typeTableDAO.getById(table.getTypeTableId());
                if (typeTable != null) {
                    capaciteTotale += typeTable.getCapacite() * table.getQuantite();
                }
            }
            
            // 3. Mettre à jour le total et la capacité
            String sqlUpdate = "UPDATE RESERVATION_EVENEMENT SET " +
                              "MONTANT_TOTAL = ?, CAPACITE_TOTALE = ? " +
                              "WHERE ID = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setBigDecimal(1, montantTotal);
                stmt.setInt(2, capaciteTotale);
                stmt.setInt(3, reservationId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return reservationId;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de la réservation avec tables", e);
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
    }
    
    // Garder l'ancienne méthode pour compatibilité
    public int ajouter(ReservationEvenement reservation) {
        return ajouterAvecTables(reservation, new ArrayList<>());
    }
    public List<ReservationEvenement> getByUtilisateurId(int userId) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT " +
                     "WHERE UTILISATEUR_ID = ? " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    
    // ==============================================
    // Vérifier si un utilisateur a déjà réservé
    // pour un événement donné
    // ==============================================
    public boolean utilisateurADejaReserve(int eventId, int userId) {
        String sql = "SELECT COUNT(*) FROM RESERVATION_EVENEMENT " +
                     "WHERE EVENEMENT_ID = ? AND UTILISATEUR_ID = ? " +
                     "AND STATUT IN ('EN_ATTENTE', 'CONFIRMEE')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==============================================
    // Récupérer les réservations en attente
    // ==============================================
    public List<ReservationEvenement> getEnAttente() {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT " +
                     "WHERE STATUT = 'EN_ATTENTE' " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    
    

    // Récupérer toutes les réservations (pour admin)
    public List<ReservationEvenement> getAllForAdmin() {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT WHERE STATUT != 'DELETED' " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }

    // Récupérer les réservations par statut
    public List<ReservationEvenement> getReservationsByStatut(String statut) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT WHERE STATUT = ? " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }


    // Récupérer une réservation par ID
    public ReservationEvenement getById(int id) {
        String sql = "SELECT * FROM RESERVATION_EVENEMENT WHERE ID = ?";
        ReservationEvenement reservation = null;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                reservation = buildReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservation;
    }

    // Récupérer les réservations par utilisateur
    public List<ReservationEvenement> getByUserId(int userId) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT WHERE UTILISATEUR_ID = ? " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }

    // Récupérer les réservations par événement
    public List<ReservationEvenement> getByEventId(int eventId) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_EVENEMENT WHERE EVENEMENT_ID = ? " +
                     "ORDER BY DATE_RESERVATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
//
//    // Valider une réservation
    public boolean validerReservation(int reservationId, int validePar, String notes) {
        String sql = "UPDATE reservation_evenement SET STATUT = 'CONFIRMEE', " +
                     "DATE_VALIDATION = NOW(), VALIDE_PAR = ?, NOTES = ? " +
                     "WHERE ID = ? AND STATUT = 'EN_ATTENTE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, validePar);
            stmt.setString(2, notes);
            stmt.setInt(3, reservationId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

 
    // Annuler une réservation
    public boolean annulerReservation(int reservationId, int annulePar, String raison) {
        String sql = "UPDATE RESERVATION_EVENEMENT SET STATUT = 'ANNULEE', " +
                     "NOTES = CONCAT(IFNULL(NOTES, ''), ' | Annulation: ', ?) " +
                     "WHERE ID = ? AND STATUT IN ('EN_ATTENTE', 'CONFIRMEE')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, raison);
            stmt.setInt(2, reservationId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Vérifier la disponibilité d'un type de table
    public boolean verifierDisponibilite(int typeTableId, int quantite) {
        TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
        int disponible = typeTableDAO.getQuantiteDisponible(typeTableId);
        return disponible >= quantite;
    }

    // Méthode helper pour construire l'objet
    private ReservationEvenement buildReservation(ResultSet rs) throws SQLException {
        ReservationEvenement reservation = new ReservationEvenement();
        
        reservation.setId(rs.getInt("ID"));
        reservation.setEvenementId(rs.getInt("EVENEMENT_ID"));
        reservation.setTypeTableId(rs.getInt("TYPE_TABLE_ID"));
        reservation.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        reservation.setQuantiteTables(rs.getInt("QUANTITE_TABLES"));
        reservation.setCapaciteTotale(rs.getInt("CAPACITE_TOTALE"));
        reservation.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
        reservation.setStatut(rs.getString("STATUT"));
        reservation.setMoyenPaiement(rs.getString("MOYEN_PAIEMENT"));
        reservation.setNomPersonne(rs.getString("NOM_PERSONNE"));
        reservation.setNumeroTransaction(rs.getString("NUMERO_TRANSACTION"));
        reservation.setNumeroTransfert(rs.getString("NUMERO_TRANSFERT"));
        
        Timestamp dateReservation = rs.getTimestamp("DATE_RESERVATION");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }
        
        Timestamp dateValidation = rs.getTimestamp("DATE_VALIDATION");
        if (dateValidation != null) {
            reservation.setDateValidation(dateValidation.toLocalDateTime());
        }
        
        int validePar = rs.getInt("VALIDE_PAR");
        if (!rs.wasNull()) {
            reservation.setValidePar(validePar);
        }
        
        reservation.setNotes(rs.getString("NOTES"));
        
        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            reservation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UPDATED_AT");
        if (updatedAt != null) {
            reservation.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return reservation;
    }

    // Récupérer le nombre total de réservations pour un événement
    public int countReservationsByEvent(int eventId) {
        String sql = "SELECT COUNT(*) as total FROM RESERVATION_EVENEMENT " +
                     "WHERE EVENEMENT_ID = ? AND STATUT IN ('EN_ATTENTE', 'CONFIRMEE')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Récupérer le nombre total de personnes pour un événement
    public int countPersonnesByEvent(int eventId) {
        String sql = "SELECT SUM(CAPACITE_TOTALE) as total_personnes FROM RESERVATION_EVENEMENT " +
                     "WHERE EVENEMENT_ID = ? AND STATUT IN ('EN_ATTENTE', 'CONFIRMEE')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_personnes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}