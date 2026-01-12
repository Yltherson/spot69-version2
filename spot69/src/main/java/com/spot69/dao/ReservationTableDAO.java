package com.spot69.dao;

import com.spot69.model.ReservationTable;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationTableDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    
    
    // Ajouter une table à la réservation
    public int ajouter(ReservationTable reservationTable) {
        String sql = "INSERT INTO RESERVATION_TABLE " +
                     "(RESERVATION_EVENEMENT_ID, TYPE_TABLE_ID, QUANTITE, MONTANT) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservationTable.getReservationEvenementId());
            stmt.setInt(2, reservationTable.getTypeTableId());
            stmt.setInt(3, reservationTable.getQuantite());
            stmt.setBigDecimal(4, reservationTable.getMontant());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de la table à la réservation", e);
        }
        return -1;
    }

    // Récupérer les tables d'une réservation
    public List<ReservationTable> getByReservationId(int reservationId) {
        List<ReservationTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_TABLE WHERE RESERVATION_EVENEMENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReservationTable table = new ReservationTable();
                table.setId(rs.getInt("ID"));
                table.setReservationEvenementId(rs.getInt("RESERVATION_EVENEMENT_ID"));
                table.setTypeTableId(rs.getInt("TYPE_TABLE_ID"));
                table.setQuantite(rs.getInt("QUANTITE"));
                table.setMontant(rs.getBigDecimal("MONTANT"));
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tables;
    }

    // Supprimer les tables d'une réservation
    public boolean supprimerParReservation(int reservationId) {
        String sql = "DELETE FROM RESERVATION_TABLE WHERE RESERVATION_EVENEMENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Calculer le total d'une réservation
    public BigDecimal getTotalByReservationId(int reservationId) {
        String sql = "SELECT SUM(MONTANT) as total FROM RESERVATION_TABLE " +
                     "WHERE RESERVATION_EVENEMENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // Calculer la capacité totale d'une réservation
    public int getCapaciteTotaleByReservationId(int reservationId) {
        String sql = "SELECT SUM(tt.CAPACITE * rt.QUANTITE) as total_capacite " +
                     "FROM RESERVATION_TABLE rt " +
                     "JOIN TYPE_TABLE_EVENEMENT tt ON rt.TYPE_TABLE_ID = tt.ID " +
                     "WHERE rt.RESERVATION_EVENEMENT_ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_capacite");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}