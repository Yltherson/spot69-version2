package com.spot69.dao;

import com.spot69.model.TransactionCaisse;
import com.spot69.model.CaisseCaissiere;
import com.spot69.model.Utilisateur;
import com.spot69.model.Commande;
import com.spot69.utils.DBConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionCaisseDAO {

	private Connection getConnection() throws SQLException {
		return DBConnection.getConnection();
	}

	// 1. Enregistrer une transaction de caisse
	public int enregistrerTransaction(TransactionCaisse transaction) {
		String sql = "INSERT INTO TRANSACTION_CAISSE " + "(CAISSE_ID, TYPE_OPERATION, MONTANT, MODE_PAIEMENT, "
				+ "CLIENT_ID, COMMANDE_ID, NOTES, DATE_OPERATION) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, transaction.getCaisseId());
			stmt.setString(2, transaction.getTypeOperation());
			stmt.setBigDecimal(3, transaction.getMontant());
			stmt.setString(4, transaction.getModePaiement());

			if (transaction.getClientId() != null) {
				stmt.setInt(5, transaction.getClientId());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}

			if (transaction.getCommandeId() != null) {
				stmt.setInt(6, transaction.getCommandeId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}

			stmt.setString(7, transaction.getNotes());
			stmt.setTimestamp(8, transaction.getDateOperation() != null ? transaction.getDateOperation()
					: new Timestamp(System.currentTimeMillis()));

			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<TransactionCaisse> getTransactionsByCaisseAndFilters(int caisseId, String typeOperation, Date dateDebut,
			Date dateFin) {
		List<TransactionCaisse> transactions = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT tc.*, u.NOM, u.PRENOM " + "FROM TRANSACTION_CAISSE tc "
				+ "LEFT JOIN UTILISATEUR u ON tc.CLIENT_ID = u.ID " + "WHERE tc.CAISSE_ID = ? ");

		if (typeOperation != null && !typeOperation.isEmpty()) {
			sql.append("AND tc.TYPE_OPERATION = ? ");
		}
		if (dateDebut != null) {
			sql.append("AND DATE(tc.DATE_OPERATION) >= ? ");
		}
		if (dateFin != null) {
			sql.append("AND DATE(tc.DATE_OPERATION) <= ? ");
		}
		sql.append("ORDER BY tc.DATE_OPERATION DESC");

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int paramIndex = 1;
			stmt.setInt(paramIndex++, caisseId);

			if (typeOperation != null && !typeOperation.isEmpty()) {
				stmt.setString(paramIndex++, typeOperation);
			}
			if (dateDebut != null) {
				stmt.setDate(paramIndex++, dateDebut);
			}
			if (dateFin != null) {
				stmt.setDate(paramIndex, dateFin);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					transactions.add(mapTransactionCaisse(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactions;
	}

	// 2. Enregistrer une vente
	public boolean enregistrerVente(int caisseId, int commandeId, BigDecimal montant, String modePaiement) {
		CommandeDAO commandeDAO = new CommandeDAO();
		Commande commande = commandeDAO.getCommandeById(commandeId);

		if (commande == null)
			return false;

		TransactionCaisse transaction = new TransactionCaisse();
		transaction.setCaisseId(caisseId);
		transaction.setTypeOperation("VENTE");
		transaction.setMontant(montant);
		transaction.setModePaiement(modePaiement);
		transaction.setClientId(commande.getClientId());
		transaction.setCommandeId(commandeId);
		transaction.setNotes("Vente commande " + commande.getNumeroCommande());

		return enregistrerTransaction(transaction) > 0;
	}

	// 3. Enregistrer un dépôt
	public boolean enregistrerDepot(int caisseId, BigDecimal montant, String modePaiement, Integer clientId,
			String notes) {
		TransactionCaisse transaction = new TransactionCaisse();
		transaction.setCaisseId(caisseId);
		transaction.setTypeOperation("DEPOT");
		transaction.setMontant(montant);
		transaction.setModePaiement(modePaiement);
		transaction.setClientId(clientId);
		transaction.setNotes(notes != null ? notes : "Dépôt en caisse");

		return enregistrerTransaction(transaction) > 0;
	}

	// 4. Enregistrer un retrait
	public boolean enregistrerRetrait(int caisseId, BigDecimal montant, String modePaiement, String notes) {
		TransactionCaisse transaction = new TransactionCaisse();
		transaction.setCaisseId(caisseId);
		transaction.setTypeOperation("RETRAIT");
		transaction.setMontant(montant.negate()); // Montant négatif pour retrait
		transaction.setModePaiement(modePaiement);
		transaction.setNotes(notes != null ? notes : "Retrait de caisse");

		return enregistrerTransaction(transaction) > 0;
	}

	// 5. Récupérer les transactions d'une caisse
	public List<TransactionCaisse> getTransactionsByCaisse(int caisseId) {
		List<TransactionCaisse> transactions = new ArrayList<>();
		String sql = "SELECT tc.*, " + "cc.CAISSIERE_ID, cc.OUVERTURE, cc.STATUT AS caisse_statut, "
				+ "client.ID AS client_id, client.NOM AS client_nom, client.PRENOM AS client_prenom, "
				+ "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " + "FROM TRANSACTION_CAISSE tc "
				+ "JOIN CAISSE_CAISSIERE cc ON tc.CAISSE_ID = cc.ID "
				+ "LEFT JOIN UTILISATEUR client ON tc.CLIENT_ID = client.ID "
				+ "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID " + "WHERE tc.CAISSE_ID = ? "
				+ "ORDER BY tc.DATE_OPERATION DESC";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, caisseId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					transactions.add(mapTransactionCaisse(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactions;
	}

	// 6. Récupérer le total par type d'opération
	public BigDecimal getTotalParType(int caisseId, String typeOperation) {
		String sql = "SELECT SUM(MONTANT) as total FROM TRANSACTION_CAISSE "
				+ "WHERE CAISSE_ID = ? AND TYPE_OPERATION = ?";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, caisseId);
			stmt.setString(2, typeOperation);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getBigDecimal("total");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return BigDecimal.ZERO;
	}

	// 7. Récupérer les transactions d'une période
	public List<TransactionCaisse> getTransactionsByPeriode(java.sql.Date dateDebut, java.sql.Date dateFin) {
		List<TransactionCaisse> transactions = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
				"SELECT tc.*, " + "cc.CAISSIERE_ID, cc.OUVERTURE, cc.STATUT AS caisse_statut, "
						+ "client.ID AS client_id, client.NOM AS client_nom, client.PRENOM AS client_prenom, "
						+ "cmd.NUMERO_COMMANDE, cmd.MONTANT_TOTAL AS commande_montant " + "FROM TRANSACTION_CAISSE tc "
						+ "JOIN CAISSE_CAISSIERE cc ON tc.CAISSE_ID = cc.ID "
						+ "LEFT JOIN UTILISATEUR client ON tc.CLIENT_ID = client.ID "
						+ "LEFT JOIN COMMANDE cmd ON tc.COMMANDE_ID = cmd.ID "
						+ "WHERE DATE(tc.DATE_OPERATION) BETWEEN ? AND ? " + "ORDER BY tc.DATE_OPERATION DESC");

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			stmt.setDate(1, dateDebut);
			stmt.setDate(2, dateFin);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					transactions.add(mapTransactionCaisse(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactions;
	}

	// 8. Générer un rapport journalier
	public Map<String, BigDecimal> getRapportJournalier(int caisseId, java.sql.Date date) {
		Map<String, BigDecimal> rapport = new java.util.HashMap<>();

		String sql = "SELECT TYPE_OPERATION, SUM(MONTANT) as total " + "FROM TRANSACTION_CAISSE "
				+ "WHERE CAISSE_ID = ? AND DATE(DATE_OPERATION) = ? " + "GROUP BY TYPE_OPERATION";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, caisseId);
			stmt.setDate(2, date);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					rapport.put(rs.getString("TYPE_OPERATION"), rs.getBigDecimal("total"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rapport;
	}
	
	 // Récupérer les transactions par date
    public List<TransactionCaisse> getTransactionsByCaisseAndDate(int caisseId, Date date) {
        List<TransactionCaisse> transactions = new ArrayList<>();
        String sql = "SELECT tc.*, u.NOM, u.PRENOM " +
                     "FROM TRANSACTION_CAISSE tc " +
                     "LEFT JOIN UTILISATEUR u ON tc.CLIENT_ID = u.ID " +
                     "WHERE tc.CAISSE_ID = ? " +
                     "AND DATE(tc.DATE_OPERATION) = ? " +
                     "ORDER BY tc.DATE_OPERATION DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, caisseId);
            stmt.setDate(2, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionCaisse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    

	// Helper pour mapper ResultSet -> TransactionCaisse
	private TransactionCaisse mapTransactionCaisse(ResultSet rs) throws SQLException {
		TransactionCaisse transaction = new TransactionCaisse();
		transaction.setId(rs.getInt("ID"));
		transaction.setCaisseId(rs.getInt("CAISSE_ID"));
		transaction.setTypeOperation(rs.getString("TYPE_OPERATION"));
		transaction.setMontant(rs.getBigDecimal("MONTANT"));
		transaction.setModePaiement(rs.getString("MODE_PAIEMENT"));

		int clientId = rs.getInt("CLIENT_ID");
		if (!rs.wasNull()) {
			transaction.setClientId(clientId);

			// Mapper client
			Utilisateur client = new Utilisateur();
			client.setId(rs.getInt("client_id"));
			client.setNom(rs.getString("client_nom"));
			client.setPrenom(rs.getString("client_prenom"));
			transaction.setClient(client);
		}

		int commandeId = rs.getInt("COMMANDE_ID");
		if (!rs.wasNull()) {
			transaction.setCommandeId(commandeId);

			// Mapper commande
			Commande commande = new Commande();
			commande.setId(commandeId);
			commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
			commande.setMontantTotal(rs.getBigDecimal("commande_montant"));
			transaction.setCommande(commande);
		}

		transaction.setNotes(rs.getString("NOTES"));
		transaction.setDateOperation(rs.getTimestamp("DATE_OPERATION"));

		// Mapper caisse
		CaisseCaissiere caisse = new CaisseCaissiere();
		caisse.setId(rs.getInt("CAISSE_ID"));
		caisse.setCaissiereId(rs.getInt("CAISSIERE_ID"));
		caisse.setOuverture(rs.getTimestamp("OUVERTURE"));
		caisse.setStatut(rs.getString("caisse_statut"));
		transaction.setCaisse(caisse);

		return transaction;
	}
}