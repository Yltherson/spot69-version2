package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DepenseDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    /* =============================
       📌 CRUD DEPENSE TYPE
       ============================= */
    public int ajouterType(DepenseType type) {
        String sql = "INSERT INTO DEPENSE_TYPE (DESCRIPTION, STATUS, CREATED_AT, UPDATED_AT) VALUES (?, ?, NOW(), NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getDescription());
            stmt.setString(2, type.getStatus() != null ? type.getStatus() : "VISIBLE");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    type.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<DepenseType> getAllTypes() {
        List<DepenseType> list = new ArrayList<>();
        String sql = "SELECT * FROM DEPENSE_TYPE WHERE STATUS = 'VISIBLE'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DepenseType t = new DepenseType();
                t.setId(rs.getInt("ID"));
                t.setDescription(rs.getString("DESCRIPTION"));
                t.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                t.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                t.setStatus(rs.getString("STATUS"));
                t.setDeletedBy((Integer) rs.getObject("DELETED_BY"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String supprimerType(int id) {
        String checkSql = "SELECT COUNT(*) FROM DEPENSE WHERE ID_TYPE = ?";
        String updateSql = "UPDATE DEPENSE_TYPE SET STATUS='DELETED', UPDATED_AT=NOW() WHERE ID=?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 🔴 Dépenses liées → on retourne un message explicite
                    return "Impossible de supprimer : ce type est utilisé dans des dépenses.";
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                return (rows > 0) ? "SUCCESS" : "Aucun type trouvé à supprimer.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur SQL : " + e.getMessage();
        }
    }


    /* =============================
       📌 CRUD DEPENSE
       ============================= */
    public int ajouterDepense(Depense d) {
        String sql = "INSERT INTO DEPENSE (ID_TYPE, MONTANT, NOTES, DATE, USER_ID, STATUS, CAISSIERE_ID, CREATED_AT, UPDATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, d.getIdType());
            stmt.setInt(2, d.getMontant());
            stmt.setString(3, d.getNotes());
            stmt.setDate(4, d.getDate());
            stmt.setInt(5, d.getUserId());
            stmt.setString(6, d.getStatus() != null ? d.getStatus() : "VISIBLE");
            stmt.setInt(7, d.getCaissiereId());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    d.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Depense> getDepensesFiltrees(Date dateDebut, Date dateFin, Integer userId, Integer typeId) {
        List<Depense> depenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT d.ID, d.ID_TYPE, d.MONTANT, d.NOTES, d.DATE, d.USER_ID, " +
            "d.STATUS, d.CREATED_AT, d.UPDATED_AT, d.CAISSIERE_ID, " +
            "t.DESCRIPTION AS TYPE_DESCRIPTION, " +
            "u.ID AS USER_ID, u.NOM AS USER_NOM, u.PRENOM AS USER_PRENOM, u.EMAIL AS USER_EMAIL, " +
            "u.LOGIN AS USER_LOGIN, u.POURCENTAGE AS USER_POURCENTAGE " +
            "FROM DEPENSE d " +
            "JOIN DEPENSE_TYPE t ON d.ID_TYPE = t.ID " +
            "JOIN UTILISATEUR u ON d.USER_ID = u.ID " +
            "WHERE d.STATUS = 'VISIBLE' "
        );

        List<Object> params = new ArrayList<>();

        // ✅ Filtres dynamiques
        if (dateDebut != null) {
            sql.append("AND d.DATE >= ? ");
            params.add(dateDebut);
        }
        if (dateFin != null) {
            sql.append("AND d.DATE < ? ");
            params.add(dateFin);
        }

        if (userId != null) {
            sql.append("AND d.USER_ID = ? ");
            params.add(userId);
            System.out.println("Filtre userId : " + userId);
        }
        if (typeId != null) {
            sql.append("AND d.ID_TYPE = ? ");
            params.add(typeId);
            System.out.println("Filtre typeId : " + typeId);
        }

        // ✅ Par défaut : dépenses du jour (Java côté serveur)
        if (dateDebut == null && dateFin == null) {
            sql.append("AND d.DATE >= ? AND d.DATE < ? ");
            LocalDate today = LocalDate.now();
            params.add(Date.valueOf(today));
            params.add(Date.valueOf(today.plusDays(1)));
            System.out.println("Aucune date spécifiée : filtre sur les dépenses du jour");
        }

        sql.append("ORDER BY d.DATE DESC, d.ID DESC");

        System.out.println("SQL final : " + sql);
        System.out.println("Paramètres : " + params);

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
                System.out.println("Paramètre " + (i + 1) + " = " + params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Depense d = new Depense();
                    d.setId(rs.getInt("ID"));
                    d.setIdType(rs.getInt("ID_TYPE"));
                    d.setMontant(rs.getInt("MONTANT"));
                    d.setNotes(rs.getString("NOTES"));
                    d.setDate(rs.getDate("DATE"));
                    d.setUserId(rs.getInt("USER_ID"));
                    d.setStatus(rs.getString("STATUS"));
                    d.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    d.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                    d.setCaissiereId(rs.getInt("CAISSIERE_ID"));

                    // ✅ Type
                    DepenseType type = new DepenseType();
                    type.setId(rs.getInt("ID_TYPE"));
                    type.setDescription(rs.getString("TYPE_DESCRIPTION"));
                    d.setType(type);

                    // ✅ Utilisateur
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("USER_ID"));
                    u.setNom(rs.getString("USER_NOM"));
                    u.setPrenom(rs.getString("USER_PRENOM"));
                    u.setEmail(rs.getString("USER_EMAIL"));
                    u.setLogin(rs.getString("USER_LOGIN"));
                    u.setPourcentage(rs.getBigDecimal("USER_POURCENTAGE"));
                    d.setUtilisateur(u);

                    depenses.add(d);
                    System.out.println("Depense ajoutée : " + d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Nombre total de dépenses récupérées : " + depenses.size());
        return depenses;
    }



    public List<Depense> getAllDepenses() {
        List<Depense> list = new ArrayList<>();
        String sql = "SELECT d.*, t.ID AS TYPE_ID, t.DESCRIPTION AS TYPE_DESC " +
                     "FROM DEPENSE d " +
                     "JOIN DEPENSE_TYPE t ON d.ID_TYPE = t.ID " +
                     "WHERE d.STATUS='VISIBLE' ORDER BY d.DATE DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Depense d = new Depense();
                d.setId(rs.getInt("ID"));
                d.setIdType(rs.getInt("ID_TYPE"));
                d.setMontant(rs.getInt("MONTANT"));
                d.setNotes(rs.getString("NOTES"));
                d.setDate(rs.getDate("DATE"));
                d.setUserId(rs.getInt("USER_ID"));
                d.setStatus(rs.getString("STATUS"));
                d.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                d.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

                // Type associé
                DepenseType t = new DepenseType();
                t.setId(rs.getInt("TYPE_ID"));
                t.setDescription(rs.getString("TYPE_DESC"));
                d.setType(t);

                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean supprimerDepense(int id) {
        String sql = "UPDATE DEPENSE SET STATUS='DELETED', UPDATED_AT=NOW() WHERE ID=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
