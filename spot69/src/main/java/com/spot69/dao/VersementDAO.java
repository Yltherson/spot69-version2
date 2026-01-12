package com.spot69.dao;

import com.spot69.model.Versement;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VersementDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter un versement
    public int ajouterVersement(Versement v) {
    	String sql = "INSERT INTO VERSEMENT (CREDIT_ID, UTILISATEUR_ID, MONTANT, NOTES, MODE_PAIEMENT, CREATED_AT, UPDATED_AT) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      	

       stmt.setInt(1, v.getCreditId());
       stmt.setInt(2, v.getUtilisateurId());
       stmt.setBigDecimal(3, v.getMontant());
       stmt.setString(4, v.getNotes());
       stmt.setString(5, v.getModePaiement());


            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setId(rs.getInt(1));
                    return v.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Lister tous les versements d’un crédit
    public List<Versement> getVersementsByCredit(int creditId) {
        List<Versement> versements = new ArrayList<>();
        String sql = "SELECT * FROM VERSEMENT WHERE CREDIT_ID = ? ORDER BY DATE_VERSEMENT ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creditId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Versement v = new Versement();
                    v.setId(rs.getInt("ID"));
                    v.setCreditId(rs.getInt("CREDIT_ID"));
                    v.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
                    v.setMontant(rs.getBigDecimal("MONTANT"));
                    v.setModePaiement(rs.getString("MODE_PAIEMENT"));
                    v.setNotes(rs.getString("NOTES"));
                    v.setDateVersement(rs.getTimestamp("DATE_VERSEMENT"));
                    v.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    v.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                    versements.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return versements;
    }
}
