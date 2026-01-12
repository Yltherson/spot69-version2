package com.spot69.dao;

import com.spot69.model.Fournisseur;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class FournisseurDAO {

    public boolean insert(Fournisseur f) {
        String sql = "INSERT INTO FOURNISSEUR (NOM, CONTACT, TELEPHONE, EMAIL, DEVISE_PREFERENCE, MODE_PAIEMENT, CREDIT_AUTORISE, LIMITE_CREDIT, SOLDE_ACTUEL) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            stmt.setString(3, f.getTelephone());
            stmt.setString(4, f.getEmail());
            stmt.setString(5, f.getDevisePreference());
            stmt.setString(6, f.getModePaiement());
            stmt.setBoolean(7, f.isCreditAutorise());
            stmt.setBigDecimal(8, f.getLimiteCredit());
            stmt.setBigDecimal(9, f.getSoldeActuel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Fournisseur f) {
        String sql = "UPDATE FOURNISSEUR SET NOM = ?, CONTACT = ?, TELEPHONE = ?, EMAIL = ?, DEVISE_PREFERENCE = ?, MODE_PAIEMENT = ?, " +
                     "CREDIT_AUTORISE = ?, LIMITE_CREDIT = ?, SOLDE_ACTUEL = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getContact());
            stmt.setString(3, f.getTelephone());
            stmt.setString(4, f.getEmail());
            stmt.setString(5, f.getDevisePreference());
            stmt.setString(6, f.getModePaiement());
            stmt.setBoolean(7, f.isCreditAutorise());
            stmt.setBigDecimal(8, f.getLimiteCredit());
            stmt.setBigDecimal(9, f.getSoldeActuel());
            stmt.setInt(10, f.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id, int deletedBy) {
        String sql = "UPDATE FOURNISSEUR SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deletedBy);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Fournisseur findById(int id) {
        String sql = "SELECT * FROM FOURNISSEUR WHERE ID = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fournisseur> findAllVisible() {
        List<Fournisseur> list = new ArrayList<>();
        String sql = "SELECT * FROM FOURNISSEUR WHERE STATUT = 'VISIBLE' ORDER BY CREATED_AT DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Fournisseur extractFromResultSet(ResultSet rs) throws SQLException {
        Fournisseur f = new Fournisseur();
        f.setId(rs.getInt("ID"));
        f.setNom(rs.getString("NOM"));
        f.setContact(rs.getString("CONTACT"));
        f.setTelephone(rs.getString("TELEPHONE"));
        f.setEmail(rs.getString("EMAIL"));
        f.setDevisePreference(rs.getString("DEVISE_PREFERENCE"));
        f.setModePaiement(rs.getString("MODE_PAIEMENT"));
        f.setCreditAutorise(rs.getBoolean("CREDIT_AUTORISE"));
        f.setLimiteCredit(rs.getBigDecimal("LIMITE_CREDIT"));
        f.setSoldeActuel(rs.getBigDecimal("SOLDE_ACTUEL"));
        f.setStatut(rs.getString("STATUT"));
        f.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        f.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        f.setDeletedBy(rs.getObject("DELETED_BY") != null ? rs.getInt("DELETED_BY") : null);
        return f;
    }

    public Fournisseur findByEmail(String email) {
        String sql = "SELECT * FROM FOURNISSEUR WHERE EMAIL = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
