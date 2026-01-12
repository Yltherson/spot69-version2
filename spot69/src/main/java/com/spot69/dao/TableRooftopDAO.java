package com.spot69.dao;

import com.spot69.model.TableRooftop;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TableRooftopDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une table
    public boolean ajouterTable(TableRooftop table) {
        String sql = "INSERT INTO TABLE_ROOFTOP (NUMERO_TABLE, ETAT_ACTUEL, STATUT, PLAFOND, DELETED_BY, CREATED_AT, UPDATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, table.getNumeroTable());
            stmt.setString(2, table.getEtatActuel());
            stmt.setString(3, table.getStatut());
            stmt.setBigDecimal(4, table.getPlafond());
            if (table.getDeletedBy() != null) {
                stmt.setInt(5, table.getDeletedBy());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setTimestamp(6, 
            	    table.getCreatedAt() != null ? 
            	        table.getCreatedAt() : 
            	        Timestamp.valueOf(LocalDateTime.now())
            	);

            	stmt.setTimestamp(7, 
            	    table.getUpdatedAt() != null ? 
            	        table.getUpdatedAt() : 
            	        Timestamp.valueOf(LocalDateTime.now())
            	);


            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    public List<TableRooftop> listerTablesParVendeuse(int utilisateurId) throws SQLException {
//        String sql = "SELECT DISTINCT t.* " +
//                     "FROM TABLE_ROOFTOP t " +
//                     "JOIN COMMANDE c ON c.TABLE_ID = t.ID " +
//                     "WHERE c.UTILISATEUR_ID = ? " +
//                     "AND DATE(c.DATE_COMMANDE) = CURRENT_DATE " +
//                     "AND c.STATUT = 'VISIBLE' " +
//                     "AND t.STATUT = 'VISIBLE'";
//
//        List<TableRooftop> tables = new ArrayList<>();
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, utilisateurId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    TableRooftop table = new TableRooftop();
//                    table.setId(rs.getInt("ID"));
//                    table.setNumeroTable(rs.getInt("NUMERO_TABLE"));
//                    table.setEtatActuel(rs.getString("ETAT_ACTUEL"));
//                    table.setStatut(rs.getString("STATUT"));
//                    table.setPlafond(rs.getBigDecimal("PLAFOND"));
//                    table.setCreatedAt(rs.getTimestamp("CREATED_AT"));
//                    table.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
//
//                    tables.add(table);
//                }
//            }
//        }
//        return tables;
//    }
//    public List<TableRooftop> listerTablesParVendeuse(int utilisateurId) throws SQLException {
//        String sql = "SELECT DISTINCT t.* " +
//                     "FROM TABLE_ROOFTOP t " +
//                     "JOIN COMMANDE c ON c.TABLE_ID = t.ID " +
//                     "WHERE c.UTILISATEUR_ID = ? " +
//                     "AND DATE(c.DATE_COMMANDE) = CURRENT_DATE " +
//                     "AND c.STATUT = 'VISIBLE' " +
//                     "AND t.STATUT = 'VISIBLE' " +
//                     "AND t.ETAT_ACTUEL = 'RESERVE'";
//
//        List<TableRooftop> tables = new ArrayList<>();
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, utilisateurId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    TableRooftop table = new TableRooftop();
//                    table.setId(rs.getInt("ID"));
//                    table.setNumeroTable(rs.getInt("NUMERO_TABLE"));
//                    table.setEtatActuel(rs.getString("ETAT_ACTUEL"));
//                    table.setStatut(rs.getString("STATUT"));
//                    table.setPlafond(rs.getBigDecimal("PLAFOND"));
//                    table.setCreatedAt(rs.getTimestamp("CREATED_AT"));
//                    table.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
//
//                    tables.add(table);
//                }
//            }
//        }
//        return tables;
//    }
    public List<TableRooftop> listerTablesParVendeuse(int utilisateurId) throws SQLException {
        String sql = "SELECT " +
                     " t.ID, t.NUMERO_TABLE, t.ETAT_ACTUEL, t.STATUT, " +
                     " t.PLAFOND, t.CREATED_AT, t.UPDATED_AT, " +
                     " COUNT(c.ID) AS QTE_COMMANDES, " +
                     " COALESCE(SUM(c.MONTANT_TOTAL), 0) AS MONTANT_TOTAL " +
                     "FROM TABLE_ROOFTOP t " +
                     "JOIN COMMANDE c ON c.TABLE_ID = t.ID " +
                     "WHERE c.UTILISATEUR_ID = ? " +
                     "AND DATE(c.DATE_COMMANDE) = CURRENT_DATE " +
                     "AND c.STATUT = 'VISIBLE' " +
                     "AND t.STATUT = 'VISIBLE' " +
                     "AND t.ETAT_ACTUEL = 'RESERVE' " +
                     "GROUP BY t.ID, t.NUMERO_TABLE, t.ETAT_ACTUEL, t.STATUT, " +
                     " t.PLAFOND, t.CREATED_AT, t.UPDATED_AT";

        List<TableRooftop> tables = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateurId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TableRooftop table = new TableRooftop();
                    table.setId(rs.getInt("ID"));
                    table.setNumeroTable(rs.getInt("NUMERO_TABLE"));
                    table.setEtatActuel(rs.getString("ETAT_ACTUEL"));
                    table.setStatut(rs.getString("STATUT"));
                    table.setPlafond(rs.getBigDecimal("PLAFOND"));
                    table.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    table.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

                    // nouveaux champs
                    table.setQteCommandes(rs.getInt("QTE_COMMANDES"));
                    table.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));

                    tables.add(table);
                }
            }
        }
        return tables;
    }


    
    // Modifier uniquement l'état actuel d'une table
//    public boolean modifierTableState(int tableId, String nouvelEtat) {
//        String sql = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = ?, UPDATED_AT = ? WHERE ID = ? AND STATUT = 'VISIBLE'";
//        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, nouvelEtat.toUpperCase());
//            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
//            stmt.setInt(3, tableId);
//
//            int rows = stmt.executeUpdate();
//            return rows > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
 // Modifier uniquement l'état actuel d'une table si elle est VISIBLE et toutes ses commandes sont livrées et payées
    public boolean modifierTableState(int tableId, String nouvelEtat) {
        String checkSql = "SELECT COUNT(*) AS pending " +
                          "FROM COMMANDE c " +
                          "WHERE c.TABLE_ID = ? " +
                          "AND (c.STATUT_COMMANDE != 'LIVRE' " +
                          "OR c.MODE_PAIEMENT = 'NON_PAYE' " +
                          "OR c.STATUT_PAIEMENT = 'NON_PAYE')";

        String updateSql = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = ?, UPDATED_AT = ? WHERE ID = ? AND STATUT = 'VISIBLE'";


        try (Connection conn = getConnection()) {
            // 1️⃣ Vérifier les commandes
//            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
//                checkStmt.setInt(1, tableId);
//                try (ResultSet rs = checkStmt.executeQuery()) {
//                    if (rs.next() && rs.getInt("pending") > 0) {
//                        throw new IllegalStateException("Valider d'abord toutes les commandes de cette table");
//                    }
//                }
//            }

            // 2️⃣ Mise à jour
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, nouvelEtat.toUpperCase());
                updateStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                updateStmt.setInt(3, tableId);

                int rows = updateStmt.executeUpdate();
                if (rows == 0) {
                    throw new IllegalStateException("Impossible de modifier l'état : la table n'est pas visible ou déjà à l'état " + nouvelEtat);
                }

                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    // Modifier une table
    public boolean modifierTable(TableRooftop table) {
        String sql = "UPDATE TABLE_ROOFTOP SET NUMERO_TABLE = ?, ETAT_ACTUEL = ?, STATUT = ?, PLAFOND = ?, DELETED_BY = ?, UPDATED_AT = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, table.getNumeroTable());
            stmt.setString(2, table.getEtatActuel());
            stmt.setString(3, table.getStatut());
            stmt.setBigDecimal(4, table.getPlafond());
            if (table.getDeletedBy() != null) {
                stmt.setInt(5, table.getDeletedBy());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(7, table.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer logiquement une table (statut = 'DELETED')
    public boolean supprimerTable(int id, int deletedBy) {
        String sql = "UPDATE TABLE_ROOFTOP SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deletedBy);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chercher une table par ID
    public TableRooftop chercherParId(int id) {
        String sql = "SELECT * FROM TABLE_ROOFTOP WHERE ID = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireTable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister toutes les tables visibles
    public List<TableRooftop> listerTables() {
        List<TableRooftop> liste = new ArrayList<>();
        String sql = "SELECT * FROM TABLE_ROOFTOP WHERE STATUT = 'VISIBLE' ORDER BY NUMERO_TABLE ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                liste.add(extraireTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les tables selon leur état actuel (RESERVE ou DISPONIBLE)
    public List<TableRooftop> listerTablesParEtat(String etat) {
        List<TableRooftop> liste = new ArrayList<>();
        String sql = "SELECT * FROM TABLE_ROOFTOP WHERE STATUT = 'VISIBLE' AND ETAT_ACTUEL = ? ORDER BY NUMERO_TABLE ASC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, etat);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    
    public List<TableRooftop> listerTablesParEtatPos(String etat) {
        List<TableRooftop> liste = new ArrayList<>();
        String sql =
                "SELECT t.ID, t.NUMERO_TABLE, t.ETAT_ACTUEL, t.STATUT, " +
                "       t.PLAFOND, t.CREATED_AT, t.UPDATED_AT, t.DELETED_BY, " +
                "       COUNT(c.ID) AS QTE_COMMANDES, " +
                "       COALESCE(SUM(c.MONTANT_TOTAL), 0) AS MONTANT_TOTAL " +
                "FROM TABLE_ROOFTOP t " +
                "LEFT JOIN COMMANDE c ON c.TABLE_ID = t.ID " +
                "   AND DATE(c.DATE_COMMANDE) = CURRENT_DATE " +
                "   AND c.STATUT = 'VISIBLE' " +
                "WHERE t.STATUT = 'VISIBLE' " +
                "  AND t.ETAT_ACTUEL = ? " +
                "GROUP BY t.ID, t.NUMERO_TABLE, t.ETAT_ACTUEL, t.STATUT, " +
                "         t.PLAFOND, t.CREATED_AT, t.UPDATED_AT " +
                "ORDER BY t.NUMERO_TABLE ASC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, etat);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TableRooftop table = extraireTable(rs);

                // 🔹 Nouveaux champs
                table.setQteCommandes(rs.getInt("QTE_COMMANDES"));
                table.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));

                liste.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }


    // Méthode utilitaire pour extraire une table depuis ResultSet
    private TableRooftop extraireTable(ResultSet rs) throws SQLException {
        TableRooftop table = new TableRooftop();
        table.setId(rs.getInt("ID"));
        table.setNumeroTable(rs.getInt("NUMERO_TABLE"));
        table.setEtatActuel(rs.getString("ETAT_ACTUEL"));
        table.setStatut(rs.getString("STATUT"));
        table.setPlafond(rs.getBigDecimal("PLAFOND"));
        int deletedBy = rs.getInt("DELETED_BY");
        table.setDeletedBy(rs.wasNull() ? null : deletedBy);
        Timestamp created = rs.getTimestamp("CREATED_AT");
        table.setCreatedAt(created); // reste Timestamp

        Timestamp updated = rs.getTimestamp("UPDATED_AT");
        table.setUpdatedAt(updated);

        return table;
    }
}
