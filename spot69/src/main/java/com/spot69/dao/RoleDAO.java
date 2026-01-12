package com.spot69.dao;

import com.spot69.model.Role;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public static boolean insert(Role role) {
        String sql = "INSERT INTO ROLE (NOM_ROLE, DROITS, STATUT) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDroits());
            stmt.setString(3, role.getStatut());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(int id) {
        String sql = "UPDATE ROLE SET STATUT = 'DELETED', UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Role> findAllVisible() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM ROLE WHERE STATUT = 'VISIBLE' ORDER BY CREATED_AT DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractRole(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Role findById(int id) {
        String sql = "SELECT * FROM ROLE WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractRole(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateDroits(int roleId, String newDroits) {
        String sql = "UPDATE ROLE SET DROITS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newDroits);
            stmt.setInt(2, roleId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateRoleName(int roleId, String roleName) {
        String sql = "UPDATE ROLE SET NOM_ROLE = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            stmt.setInt(2, roleId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Role extractRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("ID"));
        role.setRoleName(rs.getString("NOM_ROLE"));
        role.setDroits(rs.getString("DROITS"));
        role.setStatut(rs.getString("STATUT"));
        role.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        role.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        return role;
    }
}
