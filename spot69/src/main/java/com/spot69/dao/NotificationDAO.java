package com.spot69.dao;

import com.spot69.model.Notification;
import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une notification
    public boolean ajouterNotification(Notification notif) {
    	String sql = "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, TYPE_NOTIF, CREATED_AT, UPDATED_AT, STATUS) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notif.getGeneratedBy() != null ? notif.getGeneratedBy() : "SYSTEM");
            stmt.setInt(2, notif.getToUser());
            stmt.setString(3, notif.getMessages());

            // TYPE_NOTIF nullable
            if (notif.getTypeNotif() != null)
                stmt.setString(4, notif.getTypeNotif());
            else
                stmt.setNull(4, Types.VARCHAR);

            stmt.setTimestamp(5, Timestamp.valueOf(notif.getCreatedAt() != null ? notif.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(6, Timestamp.valueOf(notif.getUpdatedAt() != null ? notif.getUpdatedAt() : LocalDateTime.now()));
            stmt.setString(7, notif.getStatus() != null ? notif.getStatus() : "VISIBLE");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer logiquement toutes les notifications d'un utilisateur (TO_USER = userId)
    public boolean supprimerNotificationsUtilisateur(int userId, int deletedBy) {
        String sql = "UPDATE NOTIFICATION SET STATUS = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE TO_USER = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deletedBy);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Notification> recupererTraitementCommande(int userId, String numeroCommande) {
        List<Notification> liste = new ArrayList<>();
        String sql = "SELECT * FROM NOTIFICATION " +
                     "WHERE TO_USER = ? AND STATUS = 'VISIBLE' AND MESSAGES LIKE ? " +
                     "ORDER BY CREATED_AT DESC";

        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, "%" + numeroCommande + "%");
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Utilisateur> userMap = new HashMap<>();
            for (Utilisateur u : utilisateurDAO.findAllUserData()) {
                userMap.put(u.getId(), u);
            }

            while (rs.next()) {
                Notification notif = new Notification();
                notif.setId(rs.getInt("ID"));
                notif.setGeneratedBy(rs.getString("GENERATED_BY"));
                notif.setToUser(rs.getInt("TO_USER"));
                notif.setMessages(rs.getString("MESSAGES"));
                notif.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                notif.setUpdatedAt(rs.getTimestamp("UPDATED_AT") != null ? rs.getTimestamp("UPDATED_AT").toLocalDateTime() : null);
                notif.setUpdatedBy(rs.getInt("UPDATED_BY"));
                notif.setDeletedBy(rs.getObject("DELETED_BY") != null ? rs.getInt("DELETED_BY") : null);
                notif.setStatus(rs.getString("STATUS"));
                notif.setIsRead(rs.getString("IS_READ"));

                Utilisateur updatedByUser = userMap.get(notif.getUpdatedBy());
                if (updatedByUser != null) {
                    notif.setUtilisateur(updatedByUser);
                }

                liste.add(notif);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }


    // Récupérer toutes les notifications visibles d'un utilisateur
    public List<Notification> recupererNotificationsUtilisateur(int userId) {
        List<Notification> liste = new ArrayList<>();
        String sql = "SELECT * FROM NOTIFICATION WHERE TO_USER = ? AND STATUS = 'VISIBLE' ORDER BY CREATED_AT DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireNotification(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Méthode utilitaire pour extraire une Notification depuis ResultSet
    private Notification extraireNotification(ResultSet rs) throws SQLException {
        Notification notif = new Notification();
        notif.setId(rs.getInt("ID"));
        notif.setGeneratedBy(rs.getString("GENERATED_BY"));
        notif.setToUser(rs.getInt("TO_USER"));
        notif.setTypeNotif(rs.getString("TYPE_NOTIF"));
        notif.setMessages(rs.getString("MESSAGES"));
        Timestamp created = rs.getTimestamp("CREATED_AT");
        notif.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        Timestamp updated = rs.getTimestamp("UPDATED_AT");
        notif.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        int deletedBy = rs.getInt("DELETED_BY");
        notif.setDeletedBy(rs.wasNull() ? null : deletedBy);
        notif.setStatus(rs.getString("STATUS"));
        notif.setIsRead(rs.getString("IS_READ"));
        return notif;
    }
    
    public boolean isThereNotif(int userId) {
        String sql = "SELECT COUNT(*) AS nb FROM NOTIFICATION WHERE TO_USER = ? AND STATUS = 'VISIBLE' AND IS_READ = 0";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("nb") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Passe toutes les notifications d'un utilisateur à lues (IS_READ = 1)
    public boolean changeNotifToRead(int userId) {
        String sql = "UPDATE NOTIFICATION SET IS_READ = 1, UPDATED_AT = NOW() WHERE TO_USER = ? AND STATUS = 'VISIBLE' AND IS_READ = 0";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
