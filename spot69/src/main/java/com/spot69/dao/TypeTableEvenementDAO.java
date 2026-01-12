package com.spot69.dao;

import com.spot69.model.TypeTableEvenement;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeTableEvenementDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter un type de table
    public int ajouter(TypeTableEvenement typeTable) {
        String sql = "INSERT INTO TYPE_TABLE_EVENEMENT (EVENEMENT_ID, NOM, DESCRIPTION, CAPACITE, PRIX, STATUT) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, typeTable.getEvenementId());
            stmt.setString(2, typeTable.getNom());
            stmt.setString(3, typeTable.getDescription());
            stmt.setInt(4, typeTable.getCapacite());
            stmt.setBigDecimal(5, typeTable.getPrix());
            stmt.setString(6, typeTable.getStatut() != null ? typeTable.getStatut() : "ACTIF");
            
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
            throw new RuntimeException("Erreur lors de l'ajout du type de table", e);
        }
        return -1;
    }

    // Modifier un type de table
    public void modifier(TypeTableEvenement typeTable) {
        String sql = "UPDATE TYPE_TABLE_EVENEMENT SET NOM = ?, DESCRIPTION = ?, CAPACITE = ?, " +
                     "PRIX = ?, STATUT = ? WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeTable.getNom());
            stmt.setString(2, typeTable.getDescription());
            stmt.setInt(3, typeTable.getCapacite());
            stmt.setBigDecimal(4, typeTable.getPrix());
            stmt.setString(5, typeTable.getStatut());
            stmt.setInt(6, typeTable.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification du type de table", e);
        }
    }

    // Récupérer par ID
    public TypeTableEvenement getById(int id) {
        String sql = "SELECT * FROM TYPE_TABLE_EVENEMENT WHERE ID = ?";
        TypeTableEvenement typeTable = null;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                typeTable = buildTypeTable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return typeTable;
    }

    // Récupérer tous les types de table pour un événement
    public List<TypeTableEvenement> getByEvenementId(int evenementId) {
        List<TypeTableEvenement> types = new ArrayList<>();
        String sql = "SELECT * FROM TYPE_TABLE_EVENEMENT WHERE EVENEMENT_ID = ? AND STATUT = 'ACTIF' " +
                     "ORDER BY PRIX ASC, CAPACITE ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, evenementId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(buildTypeTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return types;
    }

    // Récupérer tous les types de table pour un événement (admin)
    public List<TypeTableEvenement> getByEvenementIdForAdmin(int evenementId) {
        List<TypeTableEvenement> types = new ArrayList<>();
        String sql = "SELECT * FROM TYPE_TABLE_EVENEMENT WHERE EVENEMENT_ID = ? ORDER BY PRIX ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, evenementId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                types.add(buildTypeTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return types;
    }

    // Supprimer (logique)
    public void desactiver(int id) {
        String sql = "UPDATE TYPE_TABLE_EVENEMENT SET STATUT = 'INACTIF' WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Réactiver
    public void reactiver(int id) {
        String sql = "UPDATE TYPE_TABLE_EVENEMENT SET STATUT = 'ACTIF' WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer définitivement
    public void supprimer(int id) {
        String sql = "DELETE FROM TYPE_TABLE_EVENEMENT WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifier la disponibilité
    public int getQuantiteDisponible(int typeTableId) {
        String sql = "SELECT COUNT(*) as total_reserve FROM RESERVATION_EVENEMENT " +
                     "WHERE TYPE_TABLE_ID = ? AND STATUT IN ('EN_ATTENTE', 'CONFIRMEE')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, typeTableId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Pour l'exemple, on suppose qu'on a une quantité fixe par type de table
                // Dans la réalité, vous auriez une table de disponibilité
                return 10 - rs.getInt("total_reserve"); // Exemple: 10 tables max par type
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    // Méthode helper pour construire l'objet
    private TypeTableEvenement buildTypeTable(ResultSet rs) throws SQLException {
        TypeTableEvenement typeTable = new TypeTableEvenement();
        
        typeTable.setId(rs.getInt("ID"));
        typeTable.setEvenementId(rs.getInt("EVENEMENT_ID"));
        typeTable.setNom(rs.getString("NOM"));
        typeTable.setDescription(rs.getString("DESCRIPTION"));
        typeTable.setCapacite(rs.getInt("CAPACITE"));
        typeTable.setPrix(rs.getBigDecimal("PRIX"));
        typeTable.setStatut(rs.getString("STATUT"));
        
        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            typeTable.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UPDATED_AT");
        if (updatedAt != null) {
            typeTable.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Calculer la disponibilité
        typeTable.setQuantiteDisponible(getQuantiteDisponible(typeTable.getId()));
        
        return typeTable;
    }

    // Calculer la capacité totale d'un événement
    public int calculerCapaciteTotale(int evenementId) {
        String sql = "SELECT SUM(CAPACITE) as total_capacite FROM TYPE_TABLE_EVENEMENT " +
                     "WHERE EVENEMENT_ID = ? AND STATUT = 'ACTIF'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, evenementId);
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