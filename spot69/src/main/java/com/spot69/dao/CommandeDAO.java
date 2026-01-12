package com.spot69.dao;

import com.spot69.model.BilanUtilisateur;
import com.spot69.model.Commande;
import com.spot69.model.CommandeDetail;
import com.spot69.model.CompteClient;
import com.spot69.model.Credit;
import com.spot69.model.Notification;
import com.spot69.model.Plat;
import com.spot69.model.Produit;
import com.spot69.model.RapportCommande;
import com.spot69.model.Role;
import com.spot69.model.TableRooftop;
import com.spot69.model.TransactionCompte;
import com.spot69.model.Utilisateur;
import com.spot69.model.Versement;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandeDAO {

	private Connection getConnection() throws SQLException {
		return DBConnection.getConnection();
	}

	TableRooftopDAO tableDao = new TableRooftopDAO(); // DAO pour récupérer la table


	public boolean updateTransactionId(int commandeId, int transactionId) {
	    String sql = "UPDATE COMMANDE SET ID_TRANSACTION_COMPTE = ? WHERE ID = ?";
	    
	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, transactionId);
	        stmt.setInt(2, commandeId);
	        
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// 1. Ajouter une commande et ses détails
	public int ajouterCommande(Commande commande, List<CommandeDetail> details) {
		String sqlCommande = "INSERT INTO COMMANDE (NUMERO_COMMANDE, CLIENT_ID, DATE_COMMANDE, STATUT_COMMANDE, MONTANT_TOTAL, MODE_PAIEMENT, STATUT_PAIEMENT, MONTANT_PAYE, UTILISATEUR_ID, NOTES, CREATED_AT, UPDATED_AT, STATUT, CASHED_BY) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE', ?)";
		String sqlDetail = "INSERT INTO COMMANDE_DETAIL (COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, PRIX_UNITAIRE, SOUS_TOTAL, NOTES, STATUT, CREATED_AT, UPDATED_AT) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'VISIBLE', ?, ?)";
		String sqlUpdateFactureDetail = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU + ? , UPDATED_AT = ? WHERE PRODUIT_ID = ? AND STATUT = 'VISIBLE'";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			// Vérifier le rôle de l'utilisateur pour déterminer si on doit mettre CASHED_BY
			UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
			Utilisateur user = utilisateurDAO.findById(commande.getUtilisateurId());
			boolean isCaissier = user != null && user.getRole() != null
					&& "CAISSIER(ERE)".equalsIgnoreCase(user.getRole().getRoleName());

			// Insertion commande
			try (PreparedStatement stmt = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, commande.getNumeroCommande());
				if (commande.getClientId() != null) {
					stmt.setInt(2, commande.getClientId());
				} else {
					stmt.setNull(2, Types.INTEGER);
				}
				stmt.setTimestamp(3, commande.getDateCommande());
				stmt.setString(4, commande.getStatutCommande());
				stmt.setBigDecimal(5, commande.getMontantTotal());
				stmt.setString(6, commande.getModePaiement());
				stmt.setString(7, commande.getStatutPaiement());
				stmt.setBigDecimal(8, commande.getMontantPaye());
				stmt.setInt(9, commande.getUtilisateurId());
				stmt.setString(10, commande.getNotes());
				Timestamp now = new Timestamp(new Date().getTime());
				stmt.setTimestamp(11, now);
				stmt.setTimestamp(12, now);

				// CASHED_BY - seulement si c'est un caissier
				if (isCaissier) {
					stmt.setInt(13, commande.getUtilisateurId());
				} else {
					stmt.setNull(13, Types.INTEGER);
				}

				int affectedRows = stmt.executeUpdate();
				if (affectedRows == 0) {
					conn.rollback();
					return -1;
				}

				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int commandeId = rs.getInt(1);
					commande.setId(commandeId);

					try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail);
							PreparedStatement stmtUpdateFacture = conn.prepareStatement(sqlUpdateFactureDetail)) {

						for (CommandeDetail detail : details) {
							// Insertion dans COMMANDE_DETAIL
							stmtDetail.setInt(1, commandeId);
							if (detail.getProduitId() != null) {
								stmtDetail.setInt(2, detail.getProduitId());
							} else {
								stmtDetail.setNull(2, Types.INTEGER);
							}
							if (detail.getPlatId() != null) {
								stmtDetail.setInt(3, detail.getPlatId());
							} else {
								stmtDetail.setNull(3, Types.INTEGER);
							}
							stmtDetail.setInt(4, detail.getQuantite());
							stmtDetail.setBigDecimal(5, detail.getPrixUnitaire());
							stmtDetail.setBigDecimal(6, detail.getSousTotal());
							stmtDetail.setString(7, detail.getNotes());
							stmtDetail.setTimestamp(8, now);
							stmtDetail.setTimestamp(9, now);

							stmtDetail.executeUpdate();

							// MAJ facture_detail uniquement si c'est un produit (produitId != null)
							if (detail.getProduitId() != null) {
								stmtUpdateFacture.setInt(1, detail.getQuantite()); // QTE_VENDU += quantite commandée
								stmtUpdateFacture.setTimestamp(2, now); // UPDATED_AT
								stmtUpdateFacture.setInt(3, detail.getProduitId()); // WHERE PRODUIT_ID = ?

								int updatedRows = stmtUpdateFacture.executeUpdate();
								if (updatedRows == 0) {
									// Optionnel : gérer le cas où aucune ligne n'a été mise à jour (ex: produit non
									// trouvé dans facture_detail)
									// Tu peux choisir d'insérer une nouvelle ligne, ou ignorer
								}
							}
						}
					}

//	                PointDAO pointDAO = new PointDAO();
//	                pointDAO.insererPointsPourCommande(commande.getUtilisateurId(), commande.getId(), details);

					conn.commit();
					return commandeId;
				} else {
					conn.rollback();
					return -1;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean mettreAJourStockLorsSuppression(int commandeId) {
		System.out.println("update apele");
		String sqlCheckStatut = "SELECT STATUT_COMMANDE FROM COMMANDE WHERE ID = ?";
		String sqlSelectProduits = "SELECT PRODUIT_ID, QUANTITE FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND PRODUIT_ID IS NOT NULL";
		String sqlUpdateQteVendu = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU - ?, UPDATED_AT = ? WHERE PRODUIT_ID = ? AND STATUT = 'VISIBLE'";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			// Vérifier le statut de la commande
			String statut = null;
			try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheckStatut)) {
				checkStmt.setInt(1, commandeId);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					statut = rs.getString("STATUT_COMMANDE");
				} else {
					return false; // commande non trouvée
				}
			}

			// Ne mettre à jour les stocks que si la commande est ANNULEE
			if (!"ANNULE".equalsIgnoreCase(statut)) {
				System.out.println("li pa anile tounoen");
				return false;
			}

			Timestamp now = new Timestamp(new Date().getTime());

			try (PreparedStatement selectProduits = conn.prepareStatement(sqlSelectProduits);
					PreparedStatement updateQte = conn.prepareStatement(sqlUpdateQteVendu)) {
				selectProduits.setInt(1, commandeId);
				ResultSet rs = selectProduits.executeQuery();
				while (rs.next()) {
					int produitId = rs.getInt("PRODUIT_ID");
					int quantite = rs.getInt("QUANTITE");

					updateQte.setInt(1, quantite); // QTE_VENDU -= quantite annulée
					updateQte.setTimestamp(2, now); // UPDATED_AT
					updateQte.setInt(3, produitId); // WHERE PRODUIT_ID = ?
					updateQte.executeUpdate();
				}
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}



	public int ajouterCommandePOS(Commande commande, List<CommandeDetail> details) {
	    String sqlCommande = "INSERT INTO COMMANDE (NUMERO_COMMANDE, CLIENT_ID, DATE_COMMANDE, STATUT_COMMANDE, MONTANT_TOTAL, MODE_PAIEMENT, STATUT_PAIEMENT, MONTANT_PAYE, UTILISATEUR_ID, NOTES, TABLE_ID, CREATED_AT, UPDATED_AT, STATUT, CASHED_BY) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE', ?)";
	    String sqlDetail = "INSERT INTO COMMANDE_DETAIL (COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, PRIX_UNITAIRE, SOUS_TOTAL, NOTES, STATUT, CREATED_AT, UPDATED_AT) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?, 'VISIBLE', ?, ?)";
	    
	    // SQL pour les mises à jour (uniquement pour les commandes non-crédit)
	    String sqlUpdateFactureDetail = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU + ? , UPDATED_AT = ? WHERE PRODUIT_ID = ? AND STATUT = 'VISIBLE'";
	    String sqlReserverTable = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = 'RESERVE', UPDATED_AT = ? WHERE ID = ?";

	    try (Connection conn = getConnection()) {
	        conn.setAutoCommit(false);

	        // Vérifier le rôle de l'utilisateur pour déterminer si on doit mettre CASHED_BY
	        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
	        Utilisateur user = utilisateurDAO.findById(commande.getUtilisateurId());
	        boolean isCaissier = user != null && user.getRole() != null
	                && "CAISSIER(ERE)".equalsIgnoreCase(user.getRole().getRoleName());

	        // 1. Insertion de la commande
	        try (PreparedStatement stmt = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setString(1, commande.getNumeroCommande());
	            if (commande.getClientId() != null)
	                stmt.setInt(2, commande.getClientId());
	            else
	                stmt.setNull(2, Types.INTEGER);

	            stmt.setTimestamp(3, commande.getDateCommande());
	            stmt.setString(4, commande.getStatutCommande());
	            stmt.setBigDecimal(5, commande.getMontantTotal());
	            stmt.setString(6, commande.getModePaiement());
	            stmt.setString(7, commande.getStatutPaiement());
	            stmt.setBigDecimal(8, commande.getMontantPaye());
	            stmt.setInt(9, commande.getUtilisateurId());
	            stmt.setString(10, commande.getNotes());

	            // Table ID
	            if (commande.getTableRooftop() != null)
	                stmt.setInt(11, commande.getTableRooftop().getId());
	            else
	                stmt.setNull(11, Types.INTEGER);

	            Timestamp now = new Timestamp(new Date().getTime());
	            stmt.setTimestamp(12, now);
	            stmt.setTimestamp(13, now);

	            // CASHED_BY - seulement si c'est un caissier
	            if (isCaissier) {
	                stmt.setInt(14, commande.getUtilisateurId());
	            } else {
	                stmt.setNull(14, Types.INTEGER);
	            }

	            int affectedRows = stmt.executeUpdate();
	            if (affectedRows == 0) {
	                conn.rollback();
	                return -1;
	            }

	            // Récupération de l'ID généré
	            ResultSet rs = stmt.getGeneratedKeys();
	            if (rs.next()) {
	                int commandeId = rs.getInt(1);
	                commande.setId(commandeId);

	                // 2. Insertion des détails de commande
	                try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail)) {

	                    for (CommandeDetail detail : details) {
	                        stmtDetail.setInt(1, commandeId);
	                        if (detail.getProduitId() != null)
	                            stmtDetail.setInt(2, detail.getProduitId());
	                        else
	                            stmtDetail.setNull(2, Types.INTEGER);

	                        if (detail.getPlatId() != null)
	                            stmtDetail.setInt(3, detail.getPlatId());
	                        else
	                            stmtDetail.setNull(3, Types.INTEGER);

	                        stmtDetail.setInt(4, detail.getQuantite());
	                        stmtDetail.setBigDecimal(5, detail.getPrixUnitaire());
	                        stmtDetail.setBigDecimal(6, detail.getSousTotal());
	                        stmtDetail.setString(7, detail.getNotes());
	                        stmtDetail.setTimestamp(8, now);
	                        stmtDetail.setTimestamp(9, now);

	                        stmtDetail.executeUpdate();
	                    }
	                }

	                // 3. Mises à jour conditionnelles - UNIQUEMENT si ce n'est PAS une commande crédit
	                if (!commande.isCredit()) {
	                    // Mise à jour facture_detail pour les produits
	                    try (PreparedStatement stmtUpdateFacture = conn.prepareStatement(sqlUpdateFactureDetail)) {
	                        for (CommandeDetail detail : details) {
	                            if (detail.getProduitId() != null) {
	                                stmtUpdateFacture.setInt(1, detail.getQuantite());
	                                stmtUpdateFacture.setTimestamp(2, now);
	                                stmtUpdateFacture.setInt(3, detail.getProduitId());
	                                stmtUpdateFacture.executeUpdate();
	                            }
	                        }
	                    }

	                    // Ajout des points fidélité
//	                    PointDAO pointDAO = new PointDAO();
//	                    pointDAO.insererPointsPourCommande(commande.getUtilisateurId(), commande.getId(), details);

	                    // Réservation de table
	                    if (commande.getTableRooftop() != null) {
	                        try (PreparedStatement stmtReserver = conn.prepareStatement(sqlReserverTable)) {
	                            stmtReserver.setTimestamp(1, now);
	                            stmtReserver.setInt(2, commande.getTableRooftop().getId());
	                            stmtReserver.executeUpdate();
	                        }
	                    }
	                }

	                conn.commit();
	                return commandeId;

	            } else {
	                conn.rollback();
	                return -1;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }
	}

	public boolean validerCommandeCredit(int commandeId) {
	    String sqlUpdateFactureDetail = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU + ? , UPDATED_AT = ? WHERE PRODUIT_ID = ? AND STATUT = 'VISIBLE'";
	    String sqlReserverTable = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = 'RESERVE', UPDATED_AT = ? WHERE ID = ?";
	    String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT_PAIEMENT = 'PAYE', UPDATED_AT = ? WHERE ID = ?";
	    String sqlSelectDetails = "SELECT PRODUIT_ID, PLAT_ID, QUANTITE FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND STATUT = 'VISIBLE'";
	    String sqlSelectCommande = "SELECT TABLE_ID, UTILISATEUR_ID FROM COMMANDE WHERE ID = ?";

	    try (Connection conn = getConnection()) {
	        conn.setAutoCommit(false);

	        Timestamp now = new Timestamp(new Date().getTime());

	        // 1. Récupérer les détails de la commande
	        List<CommandeDetail> details = new ArrayList<>();
	        int tableId = 0;
	        int utilisateurId = 0;

	        try (PreparedStatement stmtSelectCommande = conn.prepareStatement(sqlSelectCommande)) {
	            stmtSelectCommande.setInt(1, commandeId);
	            ResultSet rsCommande = stmtSelectCommande.executeQuery();
	            if (rsCommande.next()) {
	                tableId = rsCommande.getInt("TABLE_ID");
	                utilisateurId = rsCommande.getInt("UTILISATEUR_ID");
	            }
	        }

	        try (PreparedStatement stmtSelectDetails = conn.prepareStatement(sqlSelectDetails)) {
	            stmtSelectDetails.setInt(1, commandeId);
	            ResultSet rs = stmtSelectDetails.executeQuery();
	            while (rs.next()) {
	                CommandeDetail detail = new CommandeDetail();
	                detail.setProduitId(rs.getInt("PRODUIT_ID"));
	                detail.setPlatId(rs.getInt("PLAT_ID"));
	                detail.setQuantite(rs.getInt("QUANTITE"));
	                details.add(detail);
	            }
	        }

	        // 2. Mettre à jour les stocks (facture_detail)
	        try (PreparedStatement stmtUpdateFacture = conn.prepareStatement(sqlUpdateFactureDetail)) {
	            for (CommandeDetail detail : details) {
	                if (detail.getProduitId() != null) {
	                    stmtUpdateFacture.setInt(1, detail.getQuantite());
	                    stmtUpdateFacture.setTimestamp(2, now);
	                    stmtUpdateFacture.setInt(3, detail.getProduitId());
	                    stmtUpdateFacture.executeUpdate();
	                }
	            }
	        }

	        // 3. Ajouter les points fidélité
	        PointDAO pointDAO = new PointDAO();
	        pointDAO.insererPointsPourCommande(utilisateurId, commandeId, details);

	        // 4. Réserver la table si nécessaire
	        if (tableId > 0) {
	            try (PreparedStatement stmtReserver = conn.prepareStatement(sqlReserverTable)) {
	                stmtReserver.setTimestamp(1, now);
	                stmtReserver.setInt(2, tableId);
	                stmtReserver.executeUpdate();
	            }
	        }

	        // 5. Mettre à jour le statut de la commande
	        try (PreparedStatement stmtUpdateCommande = conn.prepareStatement(sqlUpdateCommande)) {
	            stmtUpdateCommande.setTimestamp(1, now);
	            stmtUpdateCommande.setInt(2, commandeId);
	            int rowsUpdated = stmtUpdateCommande.executeUpdate();
	            
	            if (rowsUpdated > 0) {
	                conn.commit();
	                return true;
	            } else {
	                conn.rollback();
	                return false;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	public boolean modifierCommandePOS(Commande commande, List<CommandeDetail> nouveauxDetails) {
//        String sqlUpdateCommande = "UPDATE COMMANDE SET CLIENT_ID=?, DATE_COMMANDE=?, STATUT_COMMANDE=?, " +
//                "MONTANT_TOTAL=?, MODE_PAIEMENT=?, STATUT_PAIEMENT=?, MONTANT_PAYE=?, UTILISATEUR_ID=?, NOTES=?, " +
//                "TABLE_ID=?, UPDATED_AT=? WHERE ID=? AND STATUT='VISIBLE'";

		String sqlUpdateCommande = "UPDATE COMMANDE SET MONTANT_TOTAL=?, MODE_PAIEMENT=?, STATUT_PAIEMENT=?, MONTANT_PAYE=?, NOTES=?, "
				+ "UPDATED_AT=? WHERE ID=? AND STATUT='VISIBLE'";

		String sqlMasquerDetails = "UPDATE COMMANDE_DETAIL SET STATUT='DELETED', UPDATED_AT=? WHERE COMMANDE_ID=? AND STATUT='VISIBLE'";

		String sqlInsertDetail = "INSERT INTO COMMANDE_DETAIL (COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, PRIX_UNITAIRE, SOUS_TOTAL, NOTES, STATUT, CREATED_AT, UPDATED_AT) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'VISIBLE', ?, ?)";

		String sqlUpdateFactureDetailDelta = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU + ? , UPDATED_AT=? WHERE PRODUIT_ID=? AND STATUT='VISIBLE'";

		String sqlReserverTable = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = 'RESERVE', UPDATED_AT=? WHERE ID=?";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			Timestamp now = new Timestamp(new Date().getTime());

			// 1. Mise à jour commande
			try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateCommande)) {

//                if (commande.getClientId() != null) stmt.setInt(1, commande.getClientId());
//                else stmt.setNull(1, Types.INTEGER);

//                stmt.setTimestamp(2, commande.getDateCommande());
//                stmt.setString(3, commande.getStatutCommande());
				stmt.setBigDecimal(1, commande.getMontantTotal());
				stmt.setString(2, commande.getModePaiement());
				stmt.setString(3, commande.getStatutPaiement());
				stmt.setBigDecimal(4, commande.getMontantPaye());
				// stmt.setInt(5, commande.getUtilisateurId());
				stmt.setString(5, commande.getNotes());

//                if (commande.getTableRooftop() != null) stmt.setInt(10, commande.getTableRooftop().getId());
//                else stmt.setNull(10, Types.INTEGER);

				stmt.setTimestamp(6, now);
				stmt.setInt(7, commande.getId());

				if (stmt.executeUpdate() == 0) {
					conn.rollback();
					return false;
				}
			}

			mettreAJourStockLorsSuppression(commande.getId());

			// 2. Récupérer les anciens détails
			List<CommandeDetail> anciensDetails = getDetailsByCommandeId(commande.getId(), conn);

			// 3. Supprimer logiquement les anciens détails
			try (PreparedStatement stmtMasquer = conn.prepareStatement(sqlMasquerDetails)) {
				stmtMasquer.setTimestamp(1, now);
				stmtMasquer.setInt(2, commande.getId());
				stmtMasquer.executeUpdate();
			}

			// 4. Réinsérer les nouveaux détails + appliquer delta sur facture_detail
			try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertDetail);
					PreparedStatement stmtDelta = conn.prepareStatement(sqlUpdateFactureDetailDelta)) {

				for (CommandeDetail newDetail : nouveauxDetails) {
					// insertion
					stmtInsert.setInt(1, commande.getId());
					if (newDetail.getProduitId() != null)
						stmtInsert.setInt(2, newDetail.getProduitId());
					else
						stmtInsert.setNull(2, Types.INTEGER);

					if (newDetail.getPlatId() != null)
						stmtInsert.setInt(3, newDetail.getPlatId());
					else
						stmtInsert.setNull(3, Types.INTEGER);

					stmtInsert.setInt(4, newDetail.getQuantite());
					stmtInsert.setBigDecimal(5, newDetail.getPrixUnitaire());
					stmtInsert.setBigDecimal(6, newDetail.getSousTotal());
					stmtInsert.setString(7, newDetail.getNotes());
					stmtInsert.setTimestamp(8, now);
					stmtInsert.setTimestamp(9, now);
					stmtInsert.executeUpdate();

					// appliquer delta sur facture_detail si produit
					if (newDetail.getProduitId() != null) {
						int ancienneQte = anciensDetails.stream().filter(
								d -> d.getProduitId() != null && d.getProduitId().equals(newDetail.getProduitId()))
								.mapToInt(CommandeDetail::getQuantite).sum();

						int delta = newDetail.getQuantite() - ancienneQte;

						if (delta != 0) {
							stmtDelta.setInt(1, delta);
							stmtDelta.setTimestamp(2, now);
							stmtDelta.setInt(3, newDetail.getProduitId());
							stmtDelta.executeUpdate();
						}
					}
				}
			}

			// 5. Mise à jour de la table si assignée
			if (commande.getTableRooftop() != null) {
				try (PreparedStatement stmtReserver = conn.prepareStatement(sqlReserverTable)) {
					stmtReserver.setTimestamp(1, now);
					stmtReserver.setInt(2, commande.getTableRooftop().getId());
					stmtReserver.executeUpdate();
				}
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<CommandeDetail> getDetailsByCommandeId(int commandeId, Connection conn) throws SQLException {
		String sql = "SELECT ID, COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, PRIX_UNITAIRE, SOUS_TOTAL, NOTES, STATUT, CREATED_AT, UPDATED_AT "
				+ "FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND STATUT = 'VISIBLE'";

		List<CommandeDetail> details = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, commandeId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					CommandeDetail detail = new CommandeDetail();
					detail.setId(rs.getInt("ID"));
					detail.setCommandeId(rs.getInt("COMMANDE_ID"));

					int produitId = rs.getInt("PRODUIT_ID");
					if (!rs.wasNull())
						detail.setProduitId(produitId);

					int platId = rs.getInt("PLAT_ID");
					if (!rs.wasNull())
						detail.setPlatId(platId);

					detail.setQuantite(rs.getInt("QUANTITE"));
					detail.setPrixUnitaire(rs.getBigDecimal("PRIX_UNITAIRE"));
					detail.setSousTotal(rs.getBigDecimal("SOUS_TOTAL"));
					detail.setNotes(rs.getString("NOTES"));
					detail.setStatut(rs.getString("STATUT"));
					detail.setCreatedAt(rs.getTimestamp("CREATED_AT"));
					detail.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

					details.add(detail);
				}
			}
		}

		return details;
	}

	public boolean existeNumeroCommande(String noFacture) {
		String sql = "SELECT COUNT(*) FROM COMMANDE WHERE NUMERO_COMMANDE = ? AND STATUT = 'VISIBLE'";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, noFacture);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateCommandeStatutPaiement(int userId, int commandeId, String statutPaiement,
	        String modePaiement) {
	    
	    // 1. Vérifier d'abord si la commande a déjà un CASHED_BY
	    String sqlSelect = "SELECT CASHED_BY, STATUT_PAIEMENT FROM COMMANDE WHERE ID = ?";
	    Integer cashedByExist = null;
	    String currentStatutPaiement = null;
	    
	    try (Connection conn = getConnection(); 
	         PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
	        
	        stmtSelect.setInt(1, commandeId);
	        ResultSet rs = stmtSelect.executeQuery();
	        if (rs.next()) {
	            cashedByExist = rs.getObject("CASHED_BY") != null ? rs.getInt("CASHED_BY") : null;
	            currentStatutPaiement = rs.getString("STATUT_PAIEMENT");
	        } else {
	            System.out.println("❌ Commande introuvable avec ID: " + commandeId);
	            return false;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	    
	    // 2. Vérifier l'utilisateur et son rôle
	    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
	    Utilisateur user = utilisateurDAO.findById(userId);

	    if (user == null || user.getRole() == null) {
	        System.out.println("❌ Utilisateur introuvable ou sans rôle.");
	        return false;
	    }

	    String roleName = user.getRole().getRoleName();
	    boolean isCaissier = "CAISSIER(ERE)".equalsIgnoreCase(roleName);
	    
	    // 3. Vérifier la cohérence métier
	    if (cashedByExist != null && cashedByExist != userId && isCaissier) {
	        System.out.println("⚠️  Commande déjà encaissée par caissier #" + cashedByExist + 
	                          " - CASHED_BY conservé pour le premier caissier");
	    }
	    
	    // 4. Construire la requête UPDATE
	    StringBuilder sqlUpdate = new StringBuilder();
	    sqlUpdate.append("UPDATE COMMANDE SET STATUT_PAIEMENT = ?, MODE_PAIEMENT = ?, UPDATED_AT = ?");
	    
	    List<Object> params = new ArrayList<>();
	    params.add(statutPaiement);
	    params.add(modePaiement);
	    params.add(new Timestamp(System.currentTimeMillis()));
	    
	    // LOGIQUE : Mettre à jour CASHED_BY UNIQUEMENT s'il est NULL et que c'est un caissier
	    boolean shouldUpdateCashedBy = false;
	    if (isCaissier && cashedByExist == null && !"NON_PAYE".equals(statutPaiement)) {
	        // Seulement si: caissier, CASHED_BY vide, et paiement effectif (pas "NON_PAYE")
	        sqlUpdate.append(", CASHED_BY = ?");
	        params.add(userId);
	        shouldUpdateCashedBy = true;
	        System.out.println("✅ Mise à jour de CASHED_BY à " + userId + " (premier caissier pour ce paiement)");
	    } else if (cashedByExist != null) {
	        System.out.println("ℹ️  CASHED_BY déjà défini à " + cashedByExist + " - CONSERVÉ");
	    }
	    
	    sqlUpdate.append(" WHERE ID = ?");
	    params.add(commandeId);
	    
	    // 5. Exécuter la mise à jour
	    try (Connection conn = getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(sqlUpdate.toString())) {
	        
	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }
	        
	        int rows = stmt.executeUpdate();
	        
	        // 6. Logs détaillés
	        System.out.println("📝 Paiement commande " + commandeId + 
	                          " - Statut: " + currentStatutPaiement + " → " + statutPaiement +
	                          ", Mode: " + modePaiement +
	                          ", Par user: " + userId + 
	                          ", CASHED_BY: " + (shouldUpdateCashedBy ? userId : cashedByExist));
	        
	        return rows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean modifierCommande(Commande commande, List<CommandeDetail> details, int log_user) {
		String sqlSelectOld = "SELECT STATUT_COMMANDE FROM COMMANDE WHERE ID = ?";
		String sqlUpdateCommande = "UPDATE COMMANDE SET NUMERO_COMMANDE = ?, CLIENT_ID = ?, DATE_COMMANDE = ?, STATUT_COMMANDE = ?, MONTANT_TOTAL = ?, MODE_PAIEMENT = ?, STATUT_PAIEMENT = ?, MONTANT_PAYE = ?, UTILISATEUR_ID = ?, NOTES = ?, UPDATED_AT = ? WHERE ID = ?";
		String sqlDeleteDetails = "DELETE FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ?";
		String sqlInsertDetail = "INSERT INTO COMMANDE_DETAIL (COMMANDE_ID, PRODUIT_ID, PLAT_ID, QUANTITE, PRIX_UNITAIRE, SOUS_TOTAL, NOTES, STATUT, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			// 1. Récupérer l'ancien statut
			String ancienStatut = null;
			try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelectOld)) {
				stmtSelect.setInt(1, commande.getId());
				ResultSet rs = stmtSelect.executeQuery();
				if (rs.next()) {
					ancienStatut = rs.getString("STATUT_COMMANDE");
				}
			}

			// 2. Mise à jour de la commande
			try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateCommande)) {
				stmt.setString(1, commande.getNumeroCommande());
				if (commande.getClientId() != null)
					stmt.setInt(2, commande.getClientId());
				else
					stmt.setNull(2, java.sql.Types.INTEGER);

				stmt.setTimestamp(3, commande.getDateCommande());
				stmt.setString(4, commande.getStatutCommande());
				stmt.setBigDecimal(5, commande.getMontantTotal());
				stmt.setString(6, commande.getModePaiement());
				stmt.setString(7, commande.getStatutPaiement());
				stmt.setBigDecimal(8, commande.getMontantPaye());
				stmt.setInt(9, commande.getUtilisateurId());
				stmt.setString(10, commande.getNotes());
				stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
				stmt.setInt(12, commande.getId());
				stmt.executeUpdate();
			}

			// 3. Supprimer les anciens détails
			try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteDetails)) {
				stmtDelete.setInt(1, commande.getId());
				stmtDelete.executeUpdate();
			}

			// 4. Réinsertion des nouveaux détails
			Timestamp now = new Timestamp(System.currentTimeMillis());
			for (CommandeDetail detail : details) {
				try (PreparedStatement stmtDetail = conn.prepareStatement(sqlInsertDetail)) {
					stmtDetail.setInt(1, commande.getId());
					if (detail.getProduitId() != null)
						stmtDetail.setInt(2, detail.getProduitId());
					else
						stmtDetail.setNull(2, java.sql.Types.INTEGER);

					if (detail.getPlatId() != null)
						stmtDetail.setInt(3, detail.getPlatId());
					else
						stmtDetail.setNull(3, java.sql.Types.INTEGER);

					stmtDetail.setInt(4, detail.getQuantite());
					stmtDetail.setBigDecimal(5, detail.getPrixUnitaire());
					stmtDetail.setBigDecimal(6, detail.getSousTotal());
					stmtDetail.setString(7, detail.getNotes());
					stmtDetail.setString(8, detail.getStatut());
					stmtDetail.setTimestamp(9, now);
					stmtDetail.setTimestamp(10, now);

					stmtDetail.executeUpdate();
				}
			}

			// 5. Vérifier si le statut a changé
			if (ancienStatut != null && !ancienStatut.equalsIgnoreCase(commande.getStatutCommande())) {
				String message = "La commande " + commande.getNumeroCommande() + " est passée de " + ancienStatut
						+ " à " + commande.getStatutCommande() + ".";

				try (PreparedStatement stmtNotif = conn.prepareStatement(
						"INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, STATUS, CREATED_AT, UPDATED_AT, UPDATED_BY) VALUES (?, ?, ?, 'VISIBLE', NOW(), NOW(), ?)")) {
					stmtNotif.setString(1, "SYSTEM");
					stmtNotif.setInt(2, commande.getUtilisateurId());
					stmtNotif.setString(3, message);
					stmtNotif.setInt(4, log_user);
					stmtNotif.executeUpdate();
				}
			}

			conn.commit();

			// ✅ 6. Si le nouveau statut est ANNULE → mise à jour stock
			if ("ANNULE".equalsIgnoreCase(commande.getStatutCommande())) {
				mettreAJourStockLorsSuppression(commande.getId());
			}

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean supprimerCommande(int commandeId, int deletedBy) {
		String sqlCheckStatut = "SELECT STATUT_COMMANDE FROM COMMANDE WHERE ID = ?";
		String sqlUpdateCommande = "UPDATE COMMANDE SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE ID = ?";
		String sqlUpdateDetails = "UPDATE COMMANDE_DETAIL SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE COMMANDE_ID = ?";
		String sqlSelectProduits = "SELECT PRODUIT_ID, QUANTITE FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND PRODUIT_ID IS NOT NULL";
		String sqlUpdateQteVendu = "UPDATE FACTURE_DETAIL SET QTE_VENDU = QTE_VENDU - ?, UPDATED_AT = ? WHERE PRODUIT_ID = ? AND STATUT = 'VISIBLE'";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			String statut = null;
			try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheckStatut)) {
				checkStmt.setInt(1, commandeId);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					statut = rs.getString("STATUT_COMMANDE");
				} else {
					return false; // commande non trouvée
				}
			}

			// Vérifie que le statut est ANNULE ou LIVREE
			if (!"ANNULE".equalsIgnoreCase(statut) && !"LIVRE".equalsIgnoreCase(statut)) {
				return false; // Suppression refusée
			}

			Timestamp now = new Timestamp(new Date().getTime());

			// Diminuer QTE_VENDU pour les produits associés si la commande est ANNULEE
			if ("ANNULE".equalsIgnoreCase(statut)) {
				try (PreparedStatement selectProduits = conn.prepareStatement(sqlSelectProduits);
						PreparedStatement updateQte = conn.prepareStatement(sqlUpdateQteVendu)) {
					selectProduits.setInt(1, commandeId);
					ResultSet rs = selectProduits.executeQuery();
					while (rs.next()) {
						int produitId = rs.getInt("PRODUIT_ID");
						int quantite = rs.getInt("QUANTITE");

						updateQte.setInt(1, quantite); // QTE_VENDU -= quantite annulée
						updateQte.setTimestamp(2, now); // UPDATED_AT
						updateQte.setInt(3, produitId); // WHERE PRODUIT_ID = ?
						updateQte.executeUpdate();
					}
				}
			}

			// Suppression logique de la commande
			try (PreparedStatement stmtCommande = conn.prepareStatement(sqlUpdateCommande)) {
				stmtCommande.setInt(1, deletedBy);
				stmtCommande.setTimestamp(2, now);
				stmtCommande.setInt(3, commandeId);
				stmtCommande.executeUpdate();
			}

			try (PreparedStatement stmtDetails = conn.prepareStatement(sqlUpdateDetails)) {
				stmtDetails.setInt(1, deletedBy);
				stmtDetails.setTimestamp(2, now);
				stmtDetails.setInt(3, commandeId);
				stmtDetails.executeUpdate();
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 4. Récupérer une commande par ID avec ses détails
	public Commande getCommandeAvecDetails(int commandeId) {
		Commande commande = null;

		String sqlCommande = "SELECT * FROM COMMANDE WHERE ID = ? AND (STATUT IS NULL OR STATUT = 'VISIBLE') AND (STATUT_COMMANDE IS NULL OR STATUT_COMMANDE != 'ANNULE') ";
		String sqlDetails = "SELECT * FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND (STATUT IS NULL OR STATUT = 'VISIBLE')";

		try (Connection conn = getConnection()) {
			// Charger commande
			try (PreparedStatement stmt = conn.prepareStatement(sqlCommande)) {
				stmt.setInt(1, commandeId);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						commande = new Commande();
						commande.setId(rs.getInt("ID"));
						commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
						commande.setClientId(rs.getInt("CLIENT_ID"));
						if (rs.wasNull())
							commande.setClientId(null);
						commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
						commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
						commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
						commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
						commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
						commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
						commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
						commande.setNotes(rs.getString("NOTES"));
						commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
						commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
						commande.setDeletedBy(rs.getInt("DELETED_BY"));
						if (rs.wasNull())
							commande.setDeletedBy(null);
						commande.setStatut(rs.getString("STATUT"));
					}
				}
			}

			if (commande != null) {
				List<CommandeDetail> details = new ArrayList<>();
				try (PreparedStatement stmtDetails = conn.prepareStatement(sqlDetails)) {
					stmtDetails.setInt(1, commandeId);
					try (ResultSet rsD = stmtDetails.executeQuery()) {
						while (rsD.next()) {
							CommandeDetail detail = new CommandeDetail();
							detail.setId(rsD.getInt("ID"));
							detail.setCommandeId(rsD.getInt("COMMANDE_ID"));
							int produitId = rsD.getInt("PRODUIT_ID");
							if (rsD.wasNull())
								produitId = 0;
							detail.setProduitId(produitId != 0 ? produitId : null);
							int platId = rsD.getInt("PLAT_ID");
							if (rsD.wasNull())
								platId = 0;
							detail.setPlatId(platId != 0 ? platId : null);
							detail.setQuantite(rsD.getInt("QUANTITE"));
							detail.setPrixUnitaire(rsD.getBigDecimal("PRIX_UNITAIRE"));
							detail.setSousTotal(rsD.getBigDecimal("SOUS_TOTAL"));
							detail.setNotes(rsD.getString("NOTES"));
							detail.setStatut(rsD.getString("STATUT"));
							detail.setCreatedAt(rsD.getTimestamp("CREATED_AT"));
							detail.setUpdatedAt(rsD.getTimestamp("UPDATED_AT"));
							int deletedBy = rsD.getInt("DELETED_BY");
							detail.setDeletedBy(rsD.wasNull() ? null : deletedBy);

							details.add(detail);
						}
					}
				}
				commande.setDetails(details);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commande;
	}

	public List<CommandeDetail> getDetailsByCommandeId(int commandeId) {
		List<CommandeDetail> details = new ArrayList<>();

		String sql = "SELECT cd.*, "
				+ "p.ID AS p_ID, p.NOM AS p_NOM, p.DESCRIPTION AS p_DESCRIPTION, p.IMAGE_URL AS p_IMAGE_URL, "
				+ "p.EMPLACEMENT AS p_EMPLACEMENT, p.CODE_PRODUIT AS p_CODE_PRODUIT, p.CATEGORIE_ID AS p_CATEGORIE_ID, p.QTE_POINTS AS p_QTE_POINTS, "
				+ "p.QTE_EN_STOCK AS p_QTE_EN_STOCK, p.SOUS_CATEGORIE_ID AS p_SOUS_CATEGORIE_ID, p.UNITE_VENTE AS p_UNITE_VENTE, "
				+ "p.CONTENU_PAR_UNITE AS p_CONTENU_PAR_UNITE, p.SEUIL_ALERTE AS p_SEUIL_ALERTE, p.PRIX_VENTE AS p_PRIX_VENTE, "
				+ "p.CREATED_AT AS p_CREATED_AT, p.UPDATED_AT AS p_UPDATED_AT, p.DELETED_BY AS p_DELETED_BY, "
				+ "pl.ID AS pl_ID, pl.NOM AS pl_NOM, pl.DESCRIPTION AS pl_DESCRIPTION, pl.QTE_POINTS AS pl_QTE_POINTS  "
				+ // ajoute plus de champs Plat si besoin
				"FROM COMMANDE_DETAIL cd " + "LEFT JOIN PRODUITS p ON cd.PRODUIT_ID = p.ID "
				+ "LEFT JOIN PLAT pl ON cd.PLAT_ID = pl.ID "
				+ "WHERE cd.COMMANDE_ID = ? AND (cd.STATUT IS NULL OR cd.STATUT = 'VISIBLE')";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, commandeId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					CommandeDetail detail = new CommandeDetail();

					// Champs de CommandeDetail
					detail.setId(rs.getInt("ID"));
					detail.setCommandeId(rs.getInt("COMMANDE_ID"));

					Integer produitId = rs.getInt("PRODUIT_ID");
					if (rs.wasNull())
						produitId = null;
					detail.setProduitId(produitId);

					Integer platId = rs.getInt("PLAT_ID");
					if (rs.wasNull())
						platId = null;
					detail.setPlatId(platId);

					detail.setQuantite(rs.getInt("QUANTITE"));
					detail.setPrixUnitaire(rs.getBigDecimal("PRIX_UNITAIRE"));
					detail.setSousTotal(rs.getBigDecimal("SOUS_TOTAL"));
					detail.setNotes(rs.getString("NOTES"));
					detail.setStatut(rs.getString("STATUT"));
					detail.setCreatedAt(rs.getTimestamp("CREATED_AT"));
					detail.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

					Integer deletedBy = rs.getInt("DELETED_BY");
					if (rs.wasNull())
						deletedBy = null;
					detail.setDeletedBy(deletedBy);

					// Mapper Produit
					if (produitId != null) {
						Produit produit = new Produit();
						produit.setId(rs.getInt("p_ID"));
						produit.setNom(rs.getString("p_NOM"));
						produit.setDescription(rs.getString("p_DESCRIPTION"));
						produit.setImageUrl(rs.getString("p_IMAGE_URL"));
						produit.setEmplacement(rs.getString("p_EMPLACEMENT"));
						produit.setCodeProduit(rs.getString("p_CODE_PRODUIT"));
						produit.setCategorieId(rs.getInt("p_CATEGORIE_ID"));
						produit.setQteEnStock(rs.getInt("p_QTE_EN_STOCK"));
						produit.setSousCategorieId(rs.getInt("p_SOUS_CATEGORIE_ID"));
						produit.setUniteVente(rs.getString("p_UNITE_VENTE"));
						produit.setContenuParUnite(rs.getString("p_CONTENU_PAR_UNITE"));
						produit.setSeuilAlerte(rs.getInt("p_SEUIL_ALERTE"));
						produit.setPrixVente(rs.getBigDecimal("p_PRIX_VENTE"));
						produit.setCreatedAt(rs.getTimestamp("p_CREATED_AT"));
						produit.setUpdatedAt(rs.getTimestamp("p_UPDATED_AT"));
						produit.setQtePoints(rs.getInt("p_QTE_POINTS"));
						Integer produitDeletedBy = rs.getInt("p_DELETED_BY");
						if (rs.wasNull())
							produitDeletedBy = null;
						produit.setDeletedBy(produitDeletedBy);

						detail.setProduit(produit);
					} else {
						detail.setProduit(null);
					}

					// Mapper Plat
					if (platId != null) {
						Plat plat = new Plat();
						plat.setId(rs.getInt("pl_ID"));
						plat.setNom(rs.getString("pl_NOM"));
						plat.setDescription(rs.getString("pl_DESCRIPTION"));
						plat.setQtePoints(rs.getInt("pl_QTE_POINTS"));
						// ajoute d'autres champs si nécessaire
						detail.setPlat(plat);
					} else {
						detail.setPlat(null);
					}

					details.add(detail);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return details;
	}


	public List<Commande> getCommandesByFilters(Integer userId, Integer tableId, Timestamp dateDebut,
			Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();

		try (Connection conn = getConnection()) {
			StringBuilder sql = new StringBuilder("SELECT c.*, "
					+ "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN, "
					+ "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, staff.ID_ROLE AS staff_ROLE_ID, "
					+ "c.TABLE_ID, "
					+ "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
					+ "FROM COMMANDE c " + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
					+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
					+ "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
					+ "WHERE c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' ");

			if (userId != null)
				sql.append(" AND c.UTILISATEUR_ID = ?");
			if (tableId != null)
				sql.append(" AND c.TABLE_ID = ?");
			if (dateDebut != null)
				sql.append(" AND c.DATE_COMMANDE >= ?");
			if (dateFin != null)
				sql.append(" AND c.DATE_COMMANDE <= ?");

			sql.append(" ORDER BY c.CREATED_AT DESC");

			try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
				int paramIndex = 1;
				if (userId != null)
					stmt.setInt(paramIndex++, userId);
				if (tableId != null)
					stmt.setInt(paramIndex++, tableId);
				if (dateDebut != null)
					stmt.setTimestamp(paramIndex++, dateDebut);
				if (dateFin != null)
					stmt.setTimestamp(paramIndex++, dateFin);

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Commande commande = new Commande();
						commande.setId(rs.getInt("ID"));
						commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
						commande.setClientId(rs.getInt("CLIENT_ID"));
						commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
						commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
						commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
						commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
						commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
						commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
						commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
						commande.setNotes(rs.getString("NOTES"));
						commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
						commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
						int deletedBy = rs.getInt("DELETED_BY");
						commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
						commande.setStatut(rs.getString("STATUT"));

						// Client
						int idClient = rs.getInt("client_ID");
						if (!rs.wasNull()) {
							Utilisateur client = new Utilisateur();
							client.setId(idClient);
							client.setNom(rs.getString("client_NOM"));
							client.setPrenom(rs.getString("client_PRENOM"));
							client.setEmail(rs.getString("client_EMAIL"));
							client.setLogin(rs.getString("client_LOGIN"));
							commande.setClient(client);
						}

						// Staff
						int idStaff = rs.getInt("staff_ID");
						if (!rs.wasNull()) {
							UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
							Utilisateur staff = utilisateurDAO.findById(idStaff);
							commande.setUtilisateur(staff);
						}

						// Table
						int tId = rs.getInt("TABLE_ID");
						if (!rs.wasNull()) {
							TableRooftop table = tableDao.chercherParId(tId);
							commande.setTableRooftop(table);
						}

						// Crédit
						if (rs.getObject("credit_ID") != null) {
							Credit credit = new Credit();
							credit.setId(rs.getInt("credit_ID"));
							credit.setCommandeId(commande.getId());
							credit.setMontantTotal(rs.getInt("credit_TOTAL"));
							credit.setMontantPaye(rs.getInt("credit_PAYE"));
							credit.setStatut(rs.getString("credit_STATUT"));
							commande.setCredit(credit);
							commande.setIsCredit(true); // c'est un crédit
						} else {
							commande.setIsCredit(false);
						}

						commandes.add(commande);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commandes;
	}


	public Map<Versement, List<Commande>> getHistoriqueVersementsAvecDetails(Integer clientId, Timestamp dateDebut,
			Timestamp dateFin) {
		Map<Versement, List<Commande>> historique = new LinkedHashMap<>();
		System.out.println("Params -> clientId: " + clientId + ", dateDebut: " + dateDebut + ", dateFin: " + dateFin);
		StringBuilder sql = new StringBuilder(
				"SELECT v.ID AS versement_ID, v.CREDIT_ID, v.UTILISATEUR_ID AS versement_UTILISATEUR_ID, "
						+ "v.MONTANT AS versement_MONTANT, v.DATE_VERSEMENT, v.CREATED_AT AS versement_CREATED_AT, v.UPDATED_AT AS versement_UPDATED_AT, "
						+ "cr.ID AS credit_ID, cr.COMMANDE_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT, "
						+ "c.ID AS commande_ID, c.NUMERO_COMMANDE, c.CLIENT_ID, c.DATE_COMMANDE, c.MONTANT_TOTAL AS commande_TOTAL, c.MONTANT_PAYE AS commande_PAYE, c.STATUT_COMMANDE "
						+ "FROM VERSEMENT v " + "LEFT JOIN CREDIT cr ON v.CREDIT_ID = cr.ID "
						+ "LEFT JOIN COMMANDE c ON cr.COMMANDE_ID = c.ID " + "WHERE 1=1 "
						+ "AND (c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE') ");

		if (clientId != null)
			sql.append(" AND (c.CLIENT_ID = ? OR c.ID IS NULL)");
		if (dateDebut != null)
			sql.append(" AND v.DATE_VERSEMENT >= ?");
		if (dateFin != null)
			sql.append(" AND v.DATE_VERSEMENT <= ?");

		sql.append(" ORDER BY v.DATE_VERSEMENT ASC, c.DATE_COMMANDE ASC");

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int index = 1;
			if (clientId != null)
				stmt.setInt(index++, clientId);
			if (dateDebut != null)
				stmt.setTimestamp(index++, dateDebut);
			if (dateFin != null)
				stmt.setTimestamp(index++, dateFin);

			System.out.println("SQL final: " + stmt);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					// Construire le versement
					Versement versement = new Versement();
					versement.setId(rs.getInt("versement_ID"));
					versement.setCreditId(rs.getInt("CREDIT_ID"));
					versement.setUtilisateurId(rs.getInt("versement_UTILISATEUR_ID"));
					versement.setMontant(rs.getBigDecimal("versement_MONTANT"));
					versement.setDateVersement(rs.getTimestamp("DATE_VERSEMENT"));
					versement.setCreatedAt(rs.getTimestamp("versement_CREATED_AT"));
					versement.setUpdatedAt(rs.getTimestamp("versement_UPDATED_AT"));

					System.out.println("Versement récupéré -> ID: " + versement.getId() + ", Montant: "
							+ versement.getMontant() + ", Date: " + versement.getDateVersement());

					// Construire la commande associée uniquement si existe
					Integer commandeId = rs.getObject("commande_ID") != null ? rs.getInt("commande_ID") : null;
					if (commandeId != null) {
						Commande commande = new Commande();
						commande.setId(commandeId);
						commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
						commande.setClientId(rs.getInt("CLIENT_ID"));
						commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
						commande.setMontantTotal(rs.getBigDecimal("commande_TOTAL"));
						commande.setMontantPaye(rs.getBigDecimal("commande_PAYE"));
						commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));

						System.out.println("  Commande -> ID: " + commande.getId() + ", Num: "
								+ commande.getNumeroCommande() + ", Total: " + commande.getMontantTotal());

						// Construire le crédit associé
						Credit credit = new Credit();
						credit.setId(rs.getInt("credit_ID"));
						credit.setCommandeId(rs.getInt("COMMANDE_ID"));
						credit.setMontantTotal(rs.getInt("credit_TOTAL"));
						credit.setMontantPaye(rs.getInt("credit_PAYE"));
						credit.setStatut(rs.getString("credit_STATUT"));
						commande.setCredit(credit);
						commande.setIsCredit(true);

						// Charger les détails de la commande
						List<CommandeDetail> details = getDetailsByCommandeId(commande.getId());
						commande.setDetails(details);

						System.out.println("    Nombre de détails: " + (details != null ? details.size() : 0));

						historique.computeIfAbsent(versement, k -> new ArrayList<>()).add(commande);
					} else {
						// Pas de commande associée
						historique.computeIfAbsent(versement, k -> new ArrayList<>());
						System.out.println("  Aucune commande associée pour ce versement.");
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Historique complet récupéré, nombre de versements: " + historique.size());
		System.out.println("Historique complet récupéré, nombre de versements: " + historique.size());
		for (Map.Entry<Versement, List<Commande>> entry : historique.entrySet()) {
			Versement v = entry.getKey();
			System.out.println(
					"Versement ID=" + v.getId() + ", Montant=" + v.getMontant() + ", Date=" + v.getDateVersement());
			for (Commande c : entry.getValue()) {
				System.out.println("  Commande ID=" + c.getId() + ", Num=" + c.getNumeroCommande() + ", Total="
						+ c.getMontantTotal());
			}
		}

		return historique;
	}


	public List<Commande> getAllCommandesCreditForClient(Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();

		System.out.println("DEBUG: Début getAllCommandesCreditForClient pour clientId = " + clientId);

		StringBuilder sql = new StringBuilder("SELECT c.*, "
				+ "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, "
				+ "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN, "
				+ "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT, "
				+ "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE, "
				+ "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, "
				+ "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN, "
				+ "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT, "
				+ "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE, "
				+ "cr.ID AS credit_ID, cr.UTILISATEUR_ID AS credit_UTILISATEUR_ID, "
				+ "cr.COMMANDE_ID AS credit_COMMANDE_ID, cr.MONTANT_TOTAL AS credit_TOTAL, "
				+ "cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT, "
				+ "cr.DATE_CREDIT AS credit_DATE, cr.CREATED_AT AS credit_CREATED_AT, cr.UPDATED_AT AS credit_UPDATED_AT, "
				+ "c.TABLE_ID " + "FROM COMMANDE c "
				+ "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID AND cr.STATUT IN ('NON_PAYE','PARTIEL') " + // <-- LEFT
																											// JOIN
				"LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
				+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID " + "WHERE c.STATUT = 'VISIBLE' "
				+ "AND c.CLIENT_ID = ? " + "AND c.STATUT_COMMANDE != 'ANNULE' ");

		if (dateDebut != null)
			sql.append(" AND c.DATE_COMMANDE >= ?");
		if (dateFin != null)
			sql.append(" AND c.DATE_COMMANDE <= ?");
		if (dateDebut == null && dateFin == null)
			sql.append(" AND DATE(c.DATE_COMMANDE) = CURDATE()");

		sql.append(" ORDER BY c.DATE_COMMANDE ASC");

		System.out.println("DEBUG: SQL construite = " + sql.toString());

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int index = 1;
			stmt.setInt(index++, clientId);

			if (dateDebut != null) {
				stmt.setTimestamp(index++, dateDebut);
			}
			if (dateFin != null) {
				stmt.setTimestamp(index++, dateFin);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Commande commande = new Commande();
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
					commande.setClientId(rs.getInt("CLIENT_ID"));
					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));

					// Charger le crédit seulement s'il existe
					int creditId = rs.getInt("credit_ID");
					if (!rs.wasNull()) {
						Credit credit = new Credit();
						credit.setId(creditId);
						credit.setUtilisateurId(rs.getInt("credit_UTILISATEUR_ID"));
						credit.setCommandeId(rs.getInt("credit_COMMANDE_ID"));
						credit.setMontantTotal(rs.getInt("credit_TOTAL"));
						credit.setMontantPaye(rs.getInt("credit_PAYE"));
						credit.setStatut(rs.getString("credit_STATUT"));
						credit.setDateCredit(rs.getTimestamp("credit_DATE"));
						credit.setCreatedAt(rs.getTimestamp("credit_CREATED_AT"));
						credit.setUpdatedAt(rs.getTimestamp("credit_UPDATED_AT"));
						commande.setCredit(credit);
						commande.setIsCredit(true);
					} else {
						commande.setIsCredit(false);
					}

					// Charger le client
					int idClient = rs.getInt("client_ID");
					if (!rs.wasNull()) {
						Utilisateur client = new Utilisateur();
						client.setId(idClient);
						client.setNom(rs.getString("client_NOM"));
						client.setPrenom(rs.getString("client_PRENOM"));
						client.setEmail(rs.getString("client_EMAIL"));
						client.setLogin(rs.getString("client_LOGIN"));
						commande.setClient(client);
					}

					// Charger le staff
					int idStaff = rs.getInt("staff_ID");
					if (!rs.wasNull()) {
						UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
						Utilisateur staff = utilisateurDAO.findById(idStaff);
						commande.setUtilisateur(staff);
					}

					commandes.add(commande);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("DEBUG: Fin getAllCommandesCreditForClient, commandes.size() = " + commandes.size());
		return commandes;
	}



	public List<Commande> getCommandesCreditNonValide(Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
	    List<Commande> commandes = new ArrayList<>();
	    System.out.println("DEBUG: Début getCommandesCreditNonValide()");
	    System.out.println("DEBUG: clientId=" + clientId + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin);

	    StringBuilder sql = new StringBuilder(
	        "SELECT c.*, "
	        + "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
	        + "FROM COMMANDE c "
	        + "JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
	        + "WHERE (cr.STATUT NOT IN ('PAYE', 'ANNULE')) "
	        + "AND (c.STATUT_COMMANDE IS NULL OR c.STATUT_COMMANDE != 'ANNULE') "
	     //   + "AND (c.STATUT IS NULL OR c.STATUT = 'VISIBLE') "
	        + "AND (c.STATUT = 'HIDDEN')" // Commandes non validées
	    );

	    if (clientId != null)
	        sql.append(" AND c.CLIENT_ID = ? ");
	    if (dateDebut != null)
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	    if (dateFin != null)
	        sql.append(" AND c.DATE_COMMANDE <= ? ");

	    sql.append(" ORDER BY c.DATE_COMMANDE DESC");

	    System.out.println("DEBUG: SQL final = " + sql.toString());

	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        int index = 1;
	        if (clientId != null) {
	            stmt.setInt(index++, clientId);
	            System.out.println("DEBUG: Paramètre clientId=" + clientId);
	        }
	        if (dateDebut != null) {
	            stmt.setTimestamp(index++, dateDebut);
	            System.out.println("DEBUG: Paramètre dateDebut=" + dateDebut);
	        }
	        if (dateFin != null) {
	            stmt.setTimestamp(index++, dateFin);
	            System.out.println("DEBUG: Paramètre dateFin=" + dateFin);
	        }

	        System.out.println("DEBUG: Exécution de la requête...");
	        try (ResultSet rs = stmt.executeQuery()) {
	            System.out.println("DEBUG: ResultSet obtenu");
	            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

	            while (rs.next()) {
	                Commande commande = new Commande();
	                commande.setId(rs.getInt("ID"));
	                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
	                commande.setClientId(rs.getInt("CLIENT_ID"));
	                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
	                commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
	                commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
	                commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
	                commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
	                commande.setNotes(rs.getString("NOTES"));
	                commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	                commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));

	                // Charger crédit
	                Credit credit = new Credit();
	                credit.setId(rs.getInt("credit_ID"));
	                credit.setMontantTotal(rs.getInt("credit_TOTAL"));
	                credit.setMontantPaye(rs.getInt("credit_PAYE"));
	                credit.setStatut(rs.getString("credit_STATUT"));
	                commande.setCredit(credit);
	                commande.setIsCredit(true);

	                // Charger client
	                try {
	                    Utilisateur client = utilisateurDAO.findById(commande.getClientId());
	                    commande.setClient(client);
	                } catch (Exception e) {
	                    System.out.println("DEBUG: Client introuvable pour ID=" + commande.getClientId());
	                }

	                // Charger utilisateur créateur
	                try {
	                    Utilisateur utilisateur = utilisateurDAO.findById(commande.getUtilisateurId());
	                    commande.setUtilisateur(utilisateur);
	                } catch (Exception e) {
	                    System.out.println("DEBUG: Utilisateur introuvable pour ID=" + commande.getUtilisateurId());
	                }

	                commandes.add(commande);
	            }
	        }

	    } catch (SQLException e) {
	        System.out.println("DEBUG: SQLException attrapée");
	        e.printStackTrace();
	    }

	    System.out.println("DEBUG: Nombre de commandes récupérées = " + commandes.size());
	    System.out.println("DEBUG: Fin getCommandesCreditNonValide()");
	    return commandes;
	}

	
	public List<Commande> getCommandesCredit(Integer userId, Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();
		System.out.println("DEBUG: Début getCommandesCredit()");
		System.out.println("DEBUG: clientId=" + clientId + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin);

		StringBuilder sql = new StringBuilder(
				"SELECT c.*, cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
						+ "FROM COMMANDE c " + "JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
						+ "WHERE (cr.STATUT = 'NON_PAYE' OR cr.STATUT = 'PARTIEL') "
						+ "AND (c.STATUT_COMMANDE IS NULL OR c.STATUT_COMMANDE != 'ANNULE') "
						+ "AND (c.STATUT IS NULL OR c.STATUT = 'VISIBLE')");

		if (clientId != null)
			sql.append(" AND c.CLIENT_ID = ? ");
		if (dateDebut != null)
			sql.append(" AND c.DATE_COMMANDE >= ? ");
		if (dateFin != null)
			sql.append(" AND c.DATE_COMMANDE <= ? ");

		sql.append(" ORDER BY c.DATE_COMMANDE ASC");
		System.out.println("DEBUG: SQL final = " + sql.toString());

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int index = 1;
			if (clientId != null) {
				stmt.setInt(index++, clientId);
				System.out.println("DEBUG: Paramètre clientId=" + clientId);
			}
			if (dateDebut != null) {
				stmt.setTimestamp(index++, dateDebut);
				System.out.println("DEBUG: Paramètre dateDebut=" + dateDebut);
			}
			if (dateFin != null) {
				stmt.setTimestamp(index++, dateFin);
				System.out.println("DEBUG: Paramètre dateFin=" + dateFin);
			}

			System.out.println("DEBUG: Exécution de la requête...");
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("DEBUG: ResultSet obtenu");
				UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

				while (rs.next()) {
					Commande commande = new Commande();
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
					commande.setClientId(rs.getInt("CLIENT_ID"));
					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));

					System.out.println("DEBUG: Commande récupérée -> ID=" + commande.getId() + ", UTILISATEUR_ID="
							+ commande.getUtilisateurId() + ", CLIENT_ID=" + commande.getClientId());

					// Charger le crédit associé
					Credit credit = new Credit();
					credit.setId(rs.getInt("credit_ID"));
					credit.setMontantTotal(rs.getInt("credit_TOTAL"));
					credit.setMontantPaye(rs.getInt("credit_PAYE"));
					credit.setStatut(rs.getString("credit_STATUT"));
					commande.setCredit(credit);
					commande.setIsCredit(true);

					System.out.println("DEBUG: Crédit associé -> ID=" + credit.getId() + ", MONTANT_TOTAL="
							+ credit.getMontantTotal() + ", STATUT=" + credit.getStatut());

					// Charger le client
					try {
						Utilisateur client = utilisateurDAO.findById(commande.getClientId());
						commande.setClient(client);
						System.out
								.println("DEBUG: Client associé -> ID=" + client.getId() + ", NOM=" + client.getNom());
					} catch (Exception e) {
						System.out.println("DEBUG: Client introuvable pour ID=" + commande.getClientId());
					}

					// Charger l'utilisateur qui a créé la commande
					try {
						Utilisateur utilisateur = utilisateurDAO.findById(commande.getUtilisateurId());
						commande.setUtilisateur(utilisateur);
						System.out.println("DEBUG: Utilisateur associé -> ID=" + utilisateur.getId() + ", NOM="
								+ utilisateur.getNom());
					} catch (Exception e) {
						System.out.println("DEBUG: Utilisateur introuvable pour ID=" + commande.getUtilisateurId());
					}

					commandes.add(commande);
				}
			}

		} catch (SQLException e) {
			System.out.println("DEBUG: SQLException attrapée");
			e.printStackTrace();
		}

		System.out.println("DEBUG: Nombre de commandes récupérées = " + commandes.size());
		System.out.println("DEBUG: Fin getCommandesCredit()");
		return commandes;
	}

	public List<Commande> getCommandesCreditAllClients(Timestamp dateDebut, Timestamp dateFin, Integer clientId) {
	    List<Commande> commandes = new ArrayList<>();
	    System.out.println("DEBUG: Début getCommandesCreditAllClients()");
	    System.out.println("DEBUG: clientId=" + clientId + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin);

	    StringBuilder sql = new StringBuilder(
	            "SELECT c.*, cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
	                    + "FROM COMMANDE c " + "JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
	                    + "WHERE (cr.STATUT = 'NON_PAYE' OR cr.STATUT = 'PARTIEL') "
	                    + "AND (c.STATUT_COMMANDE IS NULL OR c.STATUT_COMMANDE != 'ANNULE') "
	                    + "AND (c.STATUT IS NULL OR c.STATUT = 'VISIBLE')");

	    // Ajouter les conditions seulement si les paramètres ne sont pas null
	    if (clientId != null)
	        sql.append(" AND c.CLIENT_ID = ? ");
	    if (dateDebut != null)
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	    if (dateFin != null)
	        sql.append(" AND c.DATE_COMMANDE <= ? ");

	    sql.append(" ORDER BY c.DATE_COMMANDE ASC");
	    System.out.println("DEBUG: SQL final = " + sql.toString());

	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        int index = 1;
	        if (clientId != null) {
	            stmt.setInt(index++, clientId);
	            System.out.println("DEBUG: Paramètre clientId=" + clientId);
	        }
	        if (dateDebut != null) {
	            stmt.setTimestamp(index++, dateDebut);
	            System.out.println("DEBUG: Paramètre dateDebut=" + dateDebut);
	        }
	        if (dateFin != null) {
	            stmt.setTimestamp(index++, dateFin);
	            System.out.println("DEBUG: Paramètre dateFin=" + dateFin);
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

	            while (rs.next()) {
	                Commande commande = new Commande();
	                commande.setId(rs.getInt("ID"));
	                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
	                commande.setClientId(rs.getInt("CLIENT_ID"));
	                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
	                commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
	                commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
	                commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
	                commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
	                commande.setNotes(rs.getString("NOTES"));
	                commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	                commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));

	                // Charger le crédit
	                Credit credit = new Credit();
	                credit.setId(rs.getInt("credit_ID"));
	                credit.setMontantTotal(rs.getInt("credit_TOTAL"));
	                credit.setMontantPaye(rs.getInt("credit_PAYE"));
	                credit.setStatut(rs.getString("credit_STATUT"));
	                commande.setCredit(credit);
	                commande.setIsCredit(true);

	                // Charger le client
	                try {
	                    Utilisateur client = utilisateurDAO.findById(commande.getClientId());
	                    commande.setClient(client);
	                } catch (Exception e) {
	                    System.out.println("DEBUG: Client introuvable pour ID=" + commande.getClientId());
	                }

	                // Charger l'utilisateur créateur
	                try {
	                    Utilisateur utilisateur = utilisateurDAO.findById(commande.getUtilisateurId());
	                    commande.setUtilisateur(utilisateur);
	                } catch (Exception e) {
	                    System.out.println("DEBUG: Utilisateur introuvable pour ID=" + commande.getUtilisateurId());
	                }

	                commandes.add(commande);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    System.out.println("DEBUG: Nombre de commandes récupérées = " + commandes.size());
	    System.out.println("DEBUG: Fin getCommandesCreditAllClients()");
	    return commandes;
	}


	public List<Commande> getCommandesPaye(Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();
		System.out.println("DEBUG: Début getCommandesPaye()");
		System.out.println("DEBUG: clientId=" + clientId + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin);

		StringBuilder sql = new StringBuilder("SELECT c.*, "
				+ "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
				+ "FROM COMMANDE c " + "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
				+ "WHERE (c.STATUT IS NULL OR c.STATUT = 'VISIBLE') "
				+ "AND (c.STATUT_COMMANDE IS NULL OR c.STATUT_COMMANDE != 'ANNULE') "
				+ "AND (cr.STATUT IS NULL OR cr.STATUT = 'PAYE')");

		if (clientId != null)
			sql.append(" AND c.CLIENT_ID = ? ");
		if (dateDebut != null)
			sql.append(" AND c.DATE_COMMANDE >= ? ");
		if (dateFin != null)
			sql.append(" AND c.DATE_COMMANDE <= ? ");

		sql.append(" ORDER BY c.DATE_COMMANDE DESC");
		System.out.println("DEBUG: SQL final = " + sql.toString());

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int index = 1;
			if (clientId != null) {
				stmt.setInt(index++, clientId);
				System.out.println("DEBUG: Paramètre clientId=" + clientId);
			}
			if (dateDebut != null) {
				stmt.setTimestamp(index++, dateDebut);
				System.out.println("DEBUG: Paramètre dateDebut=" + dateDebut);
			}
			if (dateFin != null) {
				stmt.setTimestamp(index++, dateFin);
				System.out.println("DEBUG: Paramètre dateFin=" + dateFin);
			}

			System.out.println("DEBUG: Exécution de la requête...");
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("DEBUG: ResultSet obtenu");
				UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

				while (rs.next()) {
					Commande commande = new Commande();
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
					commande.setClientId(rs.getInt("CLIENT_ID"));
					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));

					System.out.println("DEBUG: Commande récupérée -> ID=" + commande.getId() + ", UTILISATEUR_ID="
							+ commande.getUtilisateurId() + ", CLIENT_ID=" + commande.getClientId());

					// Charger le crédit si existant
					int creditId = rs.getInt("credit_ID");
					if (!rs.wasNull()) {
						Credit credit = new Credit();
						credit.setId(creditId);
						credit.setMontantTotal(rs.getInt("credit_TOTAL"));
						credit.setMontantPaye(rs.getInt("credit_PAYE"));
						credit.setStatut(rs.getString("credit_STATUT"));
						commande.setCredit(credit);
						commande.setIsCredit(true);

						System.out.println("DEBUG: Crédit associé -> ID=" + credit.getId() + ", MONTANT_TOTAL="
								+ credit.getMontantTotal() + ", STATUT=" + credit.getStatut());
					} else {
						commande.setIsCredit(false);
					}

					// Charger le client
					try {
						Utilisateur client = utilisateurDAO.findById(commande.getClientId());
						commande.setClient(client);
						System.out
								.println("DEBUG: Client associé -> ID=" + client.getId() + ", NOM=" + client.getNom());
					} catch (Exception e) {
						System.out.println("DEBUG: Client introuvable pour ID=" + commande.getClientId());
					}

					// Charger l'utilisateur qui a créé la commande
					try {
						Utilisateur utilisateur = utilisateurDAO.findById(commande.getUtilisateurId());
						commande.setUtilisateur(utilisateur);
						System.out.println("DEBUG: Utilisateur associé -> ID=" + utilisateur.getId() + ", NOM="
								+ utilisateur.getNom());
					} catch (Exception e) {
						System.out.println("DEBUG: Utilisateur introuvable pour ID=" + commande.getUtilisateurId());
					}

					commandes.add(commande);
				}
			}

		} catch (SQLException e) {
			System.out.println("DEBUG: SQLException attrapée");
			e.printStackTrace();
		}

		System.out.println("DEBUG: Nombre de commandes récupérées = " + commandes.size());
		System.out.println("DEBUG: Fin getCommandesPaye()");
		return commandes;
	}

	public boolean validerCommande(int id, String numeroCommande, String modePaiement, BigDecimal montantPaye,
			int log_user) {
		String sql = "UPDATE COMMANDE SET " + "STATUT_COMMANDE = ?, " + "MODE_PAIEMENT = ?, " + "STATUT_PAIEMENT = ?, "
				+ "MONTANT_PAYE = ? " + "WHERE ID = ? AND NUMERO_COMMANDE = ?";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			// 1. Mise à jour de la commande
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, "LIVRE");
				stmt.setString(2, modePaiement);
				stmt.setString(3, "PAYE");
				stmt.setBigDecimal(4, montantPaye);
				stmt.setInt(5, id);
				stmt.setString(6, numeroCommande);

				int rows = stmt.executeUpdate();
				if (rows == 0) {
					conn.rollback();
					return false;
				}
			}

			// 2. Récupération de l’utilisateur concerné
			int userId = -1;
			try (PreparedStatement stmtUser = conn
					.prepareStatement("SELECT UTILISATEUR_ID FROM COMMANDE WHERE ID = ?")) {
				stmtUser.setInt(1, id);
				ResultSet rs = stmtUser.executeQuery();
				if (rs.next()) {
					userId = rs.getInt("UTILISATEUR_ID");
				}
			}

			// 3. Insertion notification
			if (userId > 0) {
				String message = "La commande " + numeroCommande + " est désormais PRÊTE.";
				try (PreparedStatement stmtNotif = conn.prepareStatement(
						"INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, MESSAGES, STATUS, CREATED_AT, UPDATED_AT, UPDATED_BY) VALUES (?, ?, ?, 'VISIBLE', NOW(), NOW(), ?)")) {
					stmtNotif.setString(1, "SYSTEM");
					stmtNotif.setInt(2, userId);
					stmtNotif.setString(3, message);
					stmtNotif.setInt(4, log_user);
					stmtNotif.executeUpdate();
				}
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Nouvelle méthode pour passer la table en RESERVE
	public void reserverTable(int tableId) {
		String sql = "UPDATE TABLE_ROOFTOP SET ETAT_ACTUEL = 'RESERVE', UPDATED_AT = ? WHERE ID = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
			stmt.setInt(2, tableId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//METHODE BON ANCIEN 22/12/25
//	public boolean modifierStatutCommande(int id, String nouveauStatut, int log_user) {
//	    String sqlSelect = "SELECT STATUT_COMMANDE, UTILISATEUR_ID, NUMERO_COMMANDE, CASHED_BY FROM COMMANDE WHERE ID = ?";
//	    
//	    try (Connection conn = getConnection()) {
////	        Connection conn = getConnection();
//	        if (conn == null) {
//	            throw new IllegalStateException("❌ Connexion DB NULL dans modifierStatutCommande()");
//	        }
//	        conn.setAutoCommit(false);
//
//	        // 1. Récupérer les informations de la commande
//	        String ancienStatut = null;
//	        int userId = -1;
//	        String numeroCommande = null;
//	        Integer cashedByExist = null;
//
//	        try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
//	            stmtSelect.setInt(1, id);
//	            ResultSet rs = stmtSelect.executeQuery();
//	            if (rs.next()) {
//	                ancienStatut = rs.getString("STATUT_COMMANDE");
//	                userId = rs.getInt("UTILISATEUR_ID");
//	                numeroCommande = rs.getString("NUMERO_COMMANDE");
//	                cashedByExist = rs.getObject("CASHED_BY") != null ? rs.getInt("CASHED_BY") : null;
//	            } else {
//	                conn.rollback();
//	                System.out.println("❌ Commande introuvable avec ID: " + id);
//	                return false;
//	            }
//	        }
//
//	        // 2. Vérifier le rôle de l'utilisateur
//	        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
//	        Utilisateur user = utilisateurDAO.findById(log_user);
//	        boolean isCaissier = false;
//	        
//	        if (user != null && user.getRole() != null) {
//	            String roleName = user.getRole().getRoleName();
//	            isCaissier = "CAISSIER(ERE)".equalsIgnoreCase(roleName);
//	        }
//
//	        // 3. Construire la requête UPDATE
//	        StringBuilder sqlUpdate = new StringBuilder("UPDATE COMMANDE SET STATUT_COMMANDE = ?, UPDATED_AT = ?");
//	        List<Object> params = new ArrayList<>();
//	        
//	        params.add(nouveauStatut);
//	        params.add(new Timestamp(System.currentTimeMillis()));
//	        
//	        // LOGIQUE PRINCIPALE : Mettre à jour CASHED_BY UNIQUEMENT s'il est NULL et que c'est un caissier
//	        boolean shouldUpdateCashedBy = false;
//	        if (isCaissier && cashedByExist == null && !"ANNULE".equalsIgnoreCase(nouveauStatut)) {
//	            // Seulement si : c'est un caissier, CASHED_BY est vide, et la commande n'est pas annulée
//	            sqlUpdate.append(", CASHED_BY = ?");
//	            params.add(log_user);
//	            shouldUpdateCashedBy = true;
//	            System.out.println("✅ Mise à jour de CASHED_BY à " + log_user + " (premier caissier)");
//	        } else if (cashedByExist != null) {
//	            System.out.println("⚠️  CASHED_BY déjà défini à " + cashedByExist + " - CONSERVÉ (premier caissier garde l'encaissement)");
//	        }
//	        
//	        sqlUpdate.append(" WHERE ID = ?");
//	        params.add(id);
//	        
//	        // 4. Exécuter la mise à jour
//	        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate.toString())) {
//	            for (int i = 0; i < params.size(); i++) {
//	                stmtUpdate.setObject(i + 1, params.get(i));
//	            }
//	            
//	            int rows = stmtUpdate.executeUpdate();
//	            if (rows == 0) {
//	                conn.rollback();
//	                return false;
//	            }
//	        }
//
//	        // 5. Mise à jour du stock si annulation
//	        if ("ANNULE".equalsIgnoreCase(nouveauStatut)) {
//	            if (!mettreAJourStockLorsSuppression(id)) {
//	                conn.rollback();
//	                return false;
//	            }
//	            // Si annulation, on pourrait vouloir réinitialiser CASHED_BY
//	            // Mais selon votre logique métier, on pourrait le garder pour traçabilité
//	        }
//
//	        // 6. Notification
//	        if (ancienStatut != null && !ancienStatut.equals(nouveauStatut) && userId > 0) {
//	            String message = "La commande " + numeroCommande + " est passée de " + ancienStatut + " à " + nouveauStatut + ".";
//	            if (shouldUpdateCashedBy) {
//	                message += " (Encaissé par caissier #" + log_user + ")";
//	            } else if (cashedByExist != null) {
//	                message += " (Déjà encaissé par caissier #" + cashedByExist + ")";
//	            }
//	            
//	            try (PreparedStatement stmtNotif = conn.prepareStatement(
//	                    "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, TYPE_NOTIF, MESSAGES, STATUS, CREATED_AT, UPDATED_AT, UPDATED_BY) VALUES (?, ?, 'COMMANDE', ?, 'VISIBLE', NOW(), NOW(), ?)")) {
//	                stmtNotif.setString(1, "SYSTEM");
//	                stmtNotif.setInt(2, userId);
//	                stmtNotif.setString(3, message);
//	                stmtNotif.setInt(4, log_user);
//	                stmtNotif.executeUpdate();
//	            }
//	        }
//
//	        conn.commit();
//	        
//	        // Log de fin
////	        System.out.println("📝 Commande " + id + " - Statut: " + ancienStatut + " → " + nouveauStatut + 
////	                          ", Modifié par: " + log_user + 
////	                          ", CASHED_BY: " + (shouldUpdateCashedBy ? log_user : cashedByExist));
//	        
//	        return true;
//
//	    } catch (SQLException e) {
//	        System.err.println("❌ Erreur SQL: " + e.getMessage());
//	        e.printStackTrace();
//	        return false;
//	    }
//	}
	
	public boolean modifierStatutCommande(int id, String nouveauStatut, int log_user) {
	    String sqlSelect = "SELECT STATUT_COMMANDE, UTILISATEUR_ID, NUMERO_COMMANDE, CASHED_BY, MODE_PAIEMENT, MONTANT_TOTAL FROM COMMANDE WHERE ID = ?";
	    
	    try (Connection conn = getConnection()) {
	        if (conn == null) {
	            throw new IllegalStateException("❌ Connexion DB NULL dans modifierStatutCommande()");
	        }
	        conn.setAutoCommit(false);

	        // 1. Récupérer les informations de la commande
	        String ancienStatut = null;
	        int userId = -1;
	        String numeroCommande = null;
	        String modePaiement = null;
	        BigDecimal montantTotal = BigDecimal.ZERO;
	        Integer cashedByExist = null;

	        try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
	            stmtSelect.setInt(1, id);
	            ResultSet rs = stmtSelect.executeQuery();
	            if (rs.next()) {
	                ancienStatut = rs.getString("STATUT_COMMANDE");
	                userId = rs.getInt("UTILISATEUR_ID");
	                numeroCommande = rs.getString("NUMERO_COMMANDE");
	                modePaiement = rs.getString("MODE_PAIEMENT");
	                montantTotal = rs.getBigDecimal("MONTANT_TOTAL");
	                cashedByExist = rs.getObject("CASHED_BY") != null ? rs.getInt("CASHED_BY") : null;
	            } else {
	                conn.rollback();
	                System.out.println("❌ Commande introuvable avec ID: " + id);
	                return false;
	            }
	        }

	        // 2. Vérifier le rôle de l'utilisateur
	        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
	        Utilisateur user = utilisateurDAO.findById(log_user);
	        boolean isCaissier = false;
	        
	        if (user != null && user.getRole() != null) {
	            String roleName = user.getRole().getRoleName();
	            isCaissier = "CAISSIER(ERE)".equalsIgnoreCase(roleName);
	        }

	        // 3. LOGIQUE DE REMBOURSEMENT SI MODE DE PAIEMENT EST "SOLDE"
	        boolean remboursementEffectue = false;
	        TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	        CompteClientDAO compteDAO = new CompteClientDAO();
	        
	        // Si la commande est annulée et le mode de paiement était "SOLDE", on effectue un remboursement
	        if ("ANNULE".equalsIgnoreCase(nouveauStatut) && "SOLDE".equalsIgnoreCase(modePaiement) && userId > 0) {
	            System.out.println("💰 Détection d'annulation avec mode de paiement SOLDE - Lancement du remboursement");
	            
	            // Récupérer le compte client
	            CompteClient compte = compteDAO.getCompteByClientId(userId);
	            
	            if (compte != null) {
	                try {
	                    // Créer une transaction de type "RETOUR" (ID 9)
	                    TransactionCompte transactionRemboursement = new TransactionCompte();
	                    transactionRemboursement.setCompteClientId(compte.getId());
	                    transactionRemboursement.setTypeTransactionId(3); // ID pour RETOUR
	                    transactionRemboursement.setMontant(montantTotal); // Montant positif = crédit
	                    transactionRemboursement.setSoldeAvant(compte.getSolde());
	                    
	                    // Calculer le nouveau solde après remboursement
	                    BigDecimal nouveauSolde = compte.getSolde().add(montantTotal);
	                    transactionRemboursement.setSoldeApres(nouveauSolde);
	                    transactionRemboursement.setCaissiereId(log_user);
	                    transactionRemboursement.setCommandeId(id);
	                    transactionRemboursement.setNotes("Remboursement commande " + numeroCommande + " annulée");
	                    transactionRemboursement.setDateTransaction(new Timestamp(System.currentTimeMillis()));
	                    
	                    // Créer la transaction
	                    int transactionId = transactionDAO.creerTransaction(transactionRemboursement);
	                    if (transactionId == -1) {
	                        System.out.println("❌ Échec de création de transaction de remboursement");
	                        conn.rollback();
	                        return false;
	                    }
	                    
	                    // Mettre à jour le solde du compte
	                    if (!compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde)) {
	                        System.out.println("❌ Échec de mise à jour du solde");
	                        conn.rollback();
	                        return false;
	                    }
	                    
	                    remboursementEffectue = true;
	                    System.out.println("✅ Remboursement effectué pour commande " + numeroCommande + 
	                                     " - Montant: " + montantTotal + " HTG");
	                    
	                    // Créer une notification pour le client
	                    try {
	                        Notification notif = new Notification();
	                        notif.setGeneratedBy("SYSTEM");
	                        notif.setToUser(userId);
	                        notif.setTypeNotif("REMBOURSEMENT");
	                        notif.setStatus("VISIBLE");
	                        notif.setMessages("Remboursement de " + montantTotal + " HTG pour l'annulation de la commande " + 
	                                        numeroCommande + ". Nouveau solde: " + nouveauSolde + " HTG");
	                        
	                        NotificationDAO notifDAO = new NotificationDAO();
	                        notifDAO.ajouterNotification(notif);
	                    } catch (Exception e) {
	                        System.err.println("⚠️  Erreur lors de la création de la notification: " + e.getMessage());
	                    }
	                    
	                } catch (Exception e) {
	                    System.err.println("❌ Erreur lors du remboursement: " + e.getMessage());
	                    e.printStackTrace();
	                    conn.rollback();
	                    return false;
	                }
	            } else {
	                System.out.println("⚠️  Compte client non trouvé pour l'utilisateur ID: " + userId);
	            }
	        }

	        // 4. Construire la requête UPDATE
	        StringBuilder sqlUpdate = new StringBuilder("UPDATE COMMANDE SET STATUT_COMMANDE = ?, UPDATED_AT = ?");
	        List<Object> params = new ArrayList<>();
	        
	        params.add(nouveauStatut);
	        params.add(new Timestamp(System.currentTimeMillis()));
	        
	        // LOGIQUE PRINCIPALE : Mettre à jour CASHED_BY UNIQUEMENT s'il est NULL et que c'est un caissier
	        boolean shouldUpdateCashedBy = false;
	        if (isCaissier && cashedByExist == null && !"ANNULE".equalsIgnoreCase(nouveauStatut)) {
	            // Seulement si : c'est un caissier, CASHED_BY est vide, et la commande n'est pas annulée
	            sqlUpdate.append(", CASHED_BY = ?");
	            params.add(log_user);
	            shouldUpdateCashedBy = true;
	            System.out.println("✅ Mise à jour de CASHED_BY à " + log_user + " (premier caissier)");
	        } else if (cashedByExist != null) {
	            System.out.println("⚠️  CASHED_BY déjà défini à " + cashedByExist + " - CONSERVÉ (premier caissier garde l'encaissement)");
	        }
	        
	        sqlUpdate.append(" WHERE ID = ?");
	        params.add(id);
	        
	        // 5. Exécuter la mise à jour
	        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate.toString())) {
	            for (int i = 0; i < params.size(); i++) {
	                stmtUpdate.setObject(i + 1, params.get(i));
	            }
	            
	            int rows = stmtUpdate.executeUpdate();
	            if (rows == 0) {
	                conn.rollback();
	                return false;
	            }
	        }

	        // 6. Mise à jour du stock si annulation
	        if ("ANNULE".equalsIgnoreCase(nouveauStatut) && !remboursementEffectue) {
	            // Seulement si pas de remboursement (mode de paiement différent de SOLDE)
	            if (!mettreAJourStockLorsSuppression(id)) {
	                conn.rollback();
	                return false;
	            }
	        }

	        // 7. Notification
	        if (ancienStatut != null && !ancienStatut.equals(nouveauStatut) && userId > 0) {
	            String message = "La commande " + numeroCommande + " est passée de " + ancienStatut + " à " + nouveauStatut + ".";
	            
	            // Ajouter des informations sur le remboursement si applicable
	            if (remboursementEffectue) {
	                message += " Remboursement de " + montantTotal + " HTG effectué sur votre compte.";
	            }
	            
	            if (shouldUpdateCashedBy) {
	                message += " (Encaissé par caissier #" + log_user + ")";
	            } else if (cashedByExist != null) {
	                message += " (Déjà encaissé par caissier #" + cashedByExist + ")";
	            }
	            
	            try (PreparedStatement stmtNotif = conn.prepareStatement(
	                    "INSERT INTO NOTIFICATION (GENERATED_BY, TO_USER, TYPE_NOTIF, MESSAGES, STATUS, CREATED_AT, UPDATED_AT, UPDATED_BY) VALUES (?, ?, 'COMMANDE', ?, 'VISIBLE', NOW(), NOW(), ?)")) {
	                stmtNotif.setString(1, "SYSTEM");
	                stmtNotif.setInt(2, userId);
	                stmtNotif.setString(3, message);
	                stmtNotif.setInt(4, log_user);
	                stmtNotif.executeUpdate();
	            }
	        }

	        conn.commit();
	        
	        // Log de fin
//	        System.out.println("📝 Commande " + id + " - Statut: " + ancienStatut + " → " + nouveauStatut + 
//	                          ", Modifié par: " + log_user + 
//	                          ", CASHED_BY: " + (shouldUpdateCashedBy ? log_user : cashedByExist) +
//	                          (remboursementEffectue ? ", REMBOURSEMENT EFFECTUÉ" : ""));
	        
	        return true;

	    } catch (SQLException e) {
	        System.err.println("❌ Erreur SQL: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
	public Commande getCommandeById(int id) {
		Commande commande = null;

		String sql = "SELECT * FROM COMMANDE WHERE ID = ? AND (STATUT IS NULL OR STATUT = 'VISIBLE') AND (STATUT_COMMANDE IS NULL OR STATUT_COMMANDE != 'ANNULE')";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					commande = new Commande();
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));

					int clientId = rs.getInt("CLIENT_ID");
					commande.setClientId(rs.wasNull() ? null : clientId);

					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
					commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
   
					int deletedBy = rs.getInt("DELETED_BY");
					commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
					commande.setStatut(rs.getString("STATUT"));

					// Charger la table si table_id présent
					int tableId = rs.getInt("TABLE_ID");
					if (!rs.wasNull()) {
						TableRooftop table = tableDao.chercherParId(tableId);
						commande.setTableRooftop(table);
					}

					// 🔹 Charger uniquement l'utilisateur (staff)
					int utilisateurId = rs.getInt("UTILISATEUR_ID");
					if (!rs.wasNull() && utilisateurId > 0) {
						UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
						Utilisateur staff = utilisateurDAO.findById(utilisateurId);
						commande.setUtilisateur(staff);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commande;
	}


	public Commande getCommandeByNumero(String numeroCommande, Integer userId) {
		Commande commande = null;

		String sqlCommandeBase = "SELECT c.*, "
				+ "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
				+ "FROM COMMANDE c " + "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID " + "WHERE c.NUMERO_COMMANDE = ? "
				+ "AND (c.STATUT IS NULL OR c.STATUT = 'VISIBLE') "
				+ "AND (c.STATUT_COMMANDE IS NULL OR c.STATUT_COMMANDE != 'ANNULE')";

		String sqlDetails = "SELECT * FROM COMMANDE_DETAIL WHERE COMMANDE_ID = ? AND (STATUT IS NULL OR STATUT = 'VISIBLE')";

		try (Connection conn = getConnection()) {
			// Récupérer le rôle de l'utilisateur
			String roleSql = "SELECT r.NOM_ROLE FROM UTILISATEUR u " + "JOIN ROLE r ON u.ID_ROLE = r.ID "
					+ "WHERE u.ID = ? AND u.STATUT = 'VISIBLE' AND r.STATUT = 'VISIBLE'";
			String roleName = null;
			try (PreparedStatement roleStmt = conn.prepareStatement(roleSql)) {
				roleStmt.setInt(1, userId);
				try (ResultSet rsRole = roleStmt.executeQuery()) {
					if (rsRole.next()) {
						roleName = rsRole.getString("NOM_ROLE");
					}
				}
			}

			// Construire la requête avec filtre utilisateur selon le rôle
			StringBuilder sqlCommande = new StringBuilder(sqlCommandeBase);
			boolean isCaissier = "CAISSIER(ERE)".equalsIgnoreCase(roleName);

			if (!isCaissier) {
				sqlCommande.append(" AND c.UTILISATEUR_ID = ?");
			}

			try (PreparedStatement stmt = conn.prepareStatement(sqlCommande.toString())) {
				stmt.setString(1, numeroCommande);
				if (!isCaissier) {
					stmt.setInt(2, userId); // seulement pour les non-caissiers
				}

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						commande = new Commande();
						commande.setId(rs.getInt("ID"));
						commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
						commande.setClientId(rs.getInt("CLIENT_ID"));
						commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
						commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
						commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
						commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
						commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
						commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
						commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
						commande.setNotes(rs.getString("NOTES"));
						commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
						commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
						commande.setDeletedBy(rs.getInt("DELETED_BY"));
						commande.setStatut(rs.getString("STATUT"));

						// Charger table
						int tableId = rs.getInt("TABLE_ID");
						if (!rs.wasNull()) {
							TableRooftop table = tableDao.chercherParId(tableId);
							commande.setTableRooftop(table);
						}

						// Charger staff
						int utilisateurId = rs.getInt("UTILISATEUR_ID");
						if (!rs.wasNull() && utilisateurId > 0) {
							UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
							Utilisateur staff = utilisateurDAO.findById(utilisateurId);
							commande.setUtilisateur(staff);
						}

						// Charger client
						int clientId = rs.getInt("CLIENT_ID");
						if (!rs.wasNull() && clientId > 0) {
							UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
							Utilisateur client = utilisateurDAO.findById(clientId);
							commande.setClient(client);
						}

						// Charger crédit
						if (rs.getObject("credit_ID") != null) {
							Credit credit = new Credit();
							credit.setId(rs.getInt("credit_ID"));
							credit.setCommandeId(commande.getId());
							credit.setMontantTotal(rs.getInt("credit_TOTAL"));
							credit.setMontantPaye(rs.getInt("credit_PAYE"));
							credit.setStatut(rs.getString("credit_STATUT"));
							commande.setCredit(credit);
							commande.setIsCredit(true);
						} else {
							commande.setIsCredit(false);
						}
					}
				}

				// Charger les détails
				if (commande != null) {
					List<CommandeDetail> details = new ArrayList<>();
					try (PreparedStatement stmtDetails = conn.prepareStatement(sqlDetails)) {
						stmtDetails.setInt(1, commande.getId());
						try (ResultSet rsD = stmtDetails.executeQuery()) {
							while (rsD.next()) {
								CommandeDetail detail = new CommandeDetail();
								detail.setId(rsD.getInt("ID"));
								detail.setCommandeId(rsD.getInt("COMMANDE_ID"));
								detail.setProduitId(rsD.getInt("PRODUIT_ID"));
								detail.setPlatId(rsD.getInt("PLAT_ID"));
								detail.setQuantite(rsD.getInt("QUANTITE"));
								detail.setPrixUnitaire(rsD.getBigDecimal("PRIX_UNITAIRE"));
								detail.setSousTotal(rsD.getBigDecimal("SOUS_TOTAL"));
								detail.setNotes(rsD.getString("NOTES"));
								detail.setStatut(rsD.getString("STATUT"));
								detail.setCreatedAt(rsD.getTimestamp("CREATED_AT"));
								detail.setUpdatedAt(rsD.getTimestamp("UPDATED_AT"));
								details.add(detail);
							}
						}
					}
					commande.setDetails(details);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commande; // <-- maintenant à l'extérieur du try
	}

	public List<Commande> getAllCommandesVisibles() {
		List<Commande> commandes = new ArrayList<>();

		String sql = "SELECT " + "c.*," +
		// Client info
				"client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
				+ "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
				+ "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
				+ "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
				+ "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
				+ "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
				+ "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
				+
				// Staff info
				"staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
				+ "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
				+ "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
				+ "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
				+ "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
				+ "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
				+ "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
				+ "c.TABLE_ID " + "FROM COMMANDE c " + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
				+ "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID "
				+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
				+ "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " + "WHERE c.STATUT = 'VISIBLE' "
				+ "AND c.STATUT_COMMANDE != 'ANNULE' " + "AND DATE(c.DATE_COMMANDE) = CURRENT_DATE "
				+ "ORDER BY c.CREATED_AT DESC";

		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Commande commande = new Commande();

				// Champs de Commande
				commande.setId(rs.getInt("ID"));
				commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
				commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
				commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
				commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
				commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
				commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
				commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
				commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
				commande.setNotes(rs.getString("NOTES"));
				commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
				commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

				int deletedBy = rs.getInt("DELETED_BY");
				commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
				commande.setStatut(rs.getString("STATUT"));

				// Récupération du CLIENT avec rôle
				int clientId = rs.getInt("client_ID");
				if (!rs.wasNull()) {
					Utilisateur client = new Utilisateur();
					client.setId(clientId);
					client.setNom(rs.getString("client_NOM"));
					client.setPrenom(rs.getString("client_PRENOM"));
					client.setEmail(rs.getString("client_EMAIL"));
					client.setLogin(rs.getString("client_LOGIN"));
					client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
					client.setStatut(rs.getString("client_STATUT"));
					client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
					client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));

					int clientRoleId = rs.getInt("client_ROLE_ID");
					if (!rs.wasNull()) {
						Role roleClient = new Role();
						roleClient.setId(clientRoleId);
						roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
						roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
						roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
						roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
						roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
						client.setRole(roleClient);
					}

					commande.setClient(client);
				}

				// Récupération du STAFF avec rôle
				int idStaff = rs.getInt("staff_ID");
				if (!rs.wasNull()) {
					Utilisateur staff = new Utilisateur();
					staff.setId(idStaff);
					staff.setNom(rs.getString("staff_NOM"));
					staff.setPrenom(rs.getString("staff_PRENOM"));
					staff.setEmail(rs.getString("staff_EMAIL"));
					staff.setLogin(rs.getString("staff_LOGIN"));
					staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
					staff.setStatut(rs.getString("staff_STATUT"));
					staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
					staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));

					int staffRoleId = rs.getInt("staff_ROLE_ID");
					if (!rs.wasNull()) {
						Role roleStaff = new Role();
						roleStaff.setId(staffRoleId);
						roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
						roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
						roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
						roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
						roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
						staff.setRole(roleStaff);
					}

					commande.setUtilisateur(staff);
				}

				// Récupérer la table si table_id présent
				int tableId = rs.getInt("TABLE_ID");
				if (!rs.wasNull()) {
					TableRooftop table = tableDao.chercherParId(tableId);
					commande.setTableRooftop(table);
				}

				commandes.add(commande);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commandes;
	}


//GETALLCOMMANDES BON 22-12-25
//	public List<Commande> getAllCommandesVisiblesByFiltres(Integer staffId, Integer roleId, Integer platId,
//			Integer produitId, Date dateDebut, Date dateFin) {
//		List<Commande> commandes = new ArrayList<>();
//
//		System.out.println("=== DEBUT getAllCommandesVisiblesByFiltres ===");
//		System.out.println("Paramètres reçus - staffId: " + staffId + ", roleId: " + roleId + ", produitId: "
//				+ produitId + ", dateDebut: " + dateDebut + ", dateFin: " + dateFin);
//
//		StringBuilder sql = new StringBuilder("SELECT DISTINCT " + // Ajout de DISTINCT pour éviter les doublons
//				"c.*," +
//				// Client info
//				"client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
//				+ "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
//				+ "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
//				+ "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
//				+ "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
//				+ "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
//				+ "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
//				+
//
//				// Staff info
//				"staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
//				+ "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
//				+ "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
//				+ "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
//				+ "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
//				+ "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
//				+ "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
//				+ "c.TABLE_ID " + "FROM COMMANDE c " + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
//				+ "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID "
//				+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
//				+ "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
//				+ "LEFT JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID " + // Jointure avec les détails de commande
//				"WHERE c.STATUT = 'VISIBLE' " + "AND c.STATUT_COMMANDE != 'ANNULE' ");
//
//		List<Object> params = new ArrayList<>();
//
//		if (staffId != null) {
//			sql.append(" AND staff.ID = ? ");
//			params.add(staffId);
//		}
//
//		if (roleId != null) {
//			sql.append(" AND role_staff.ID = ? ");
//			params.add(roleId);
//		}
//
//		// Filtre plat/produit
//		if (produitId != null) {
//			sql.append(" AND cd.PRODUIT_ID = ? ");
//			params.add(produitId);
//		}
//		if (platId != null) {
//			sql.append(" AND cd.PLAT_ID = ? ");
//			params.add(platId);
//		}
//
//		if (dateDebut != null) {
//			sql.append(" AND c.DATE_COMMANDE >= ? ");
//			params.add(new java.sql.Timestamp(dateDebut.getTime()));
//		}
//
//		if (dateFin != null) {
//			sql.append(" AND c.DATE_COMMANDE <= ? ");
//			params.add(new java.sql.Timestamp(dateFin.getTime()));
//		}
//
//		sql.append(" ORDER BY c.CREATED_AT DESC");
//
//		System.out.println("SQL généré: " + sql.toString());
//		System.out.println("Paramètres: " + params);
//
//		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//
//			// Injecter les paramètres
//			for (int i = 0; i < params.size(); i++) {
//				stmt.setObject(i + 1, params.get(i));
//			}
//
//			System.out.println("Exécution de la requête...");
//
//			try (ResultSet rs = stmt.executeQuery()) {
//				System.out.println("Résultats de la requête:");
//				int count = 0;
//
//				while (rs.next()) {
//					count++;
//					Commande commande = new Commande();
//
//					// Champs de Commande
//					commande.setId(rs.getInt("ID"));
//					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
//					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
//					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
//					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
//					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
//					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
//					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
//					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//					commande.setNotes(rs.getString("NOTES"));
//					commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
//					commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
//
//					int deletedBy = rs.getInt("DELETED_BY");
//					commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
//					commande.setStatut(rs.getString("STATUT"));
//
//					// Récupération du CLIENT avec rôle
//					int clientId = rs.getInt("client_ID");
//					if (!rs.wasNull()) {
//						Utilisateur client = new Utilisateur();
//						client.setId(clientId);
//						client.setNom(rs.getString("client_NOM"));
//						client.setPrenom(rs.getString("client_PRENOM"));
//						client.setEmail(rs.getString("client_EMAIL"));
//						client.setLogin(rs.getString("client_LOGIN"));
//						client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
//						client.setStatut(rs.getString("client_STATUT"));
//						client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
//						client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));
//
//						int clientRoleId = rs.getInt("client_ROLE_ID");
//						if (!rs.wasNull()) {
//							Role roleClient = new Role();
//							roleClient.setId(clientRoleId);
//							roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
//							roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
//							roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
//							roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
//							roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
//							client.setRole(roleClient);
//						}
//
//						commande.setClient(client);
//					}
//
//					// Récupération du STAFF avec rôle
//					int idStaff = rs.getInt("staff_ID");
//					if (!rs.wasNull()) {
//						Utilisateur staff = new Utilisateur();
//						staff.setId(idStaff);
//						staff.setNom(rs.getString("staff_NOM"));
//						staff.setPrenom(rs.getString("staff_PRENOM"));
//						staff.setEmail(rs.getString("staff_EMAIL"));
//						staff.setLogin(rs.getString("staff_LOGIN"));
//						staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
//						staff.setStatut(rs.getString("staff_STATUT"));
//						staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
//						staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));
//
//						int staffRoleId = rs.getInt("staff_ROLE_ID");
//						if (!rs.wasNull()) {
//							Role roleStaff = new Role();
//							roleStaff.setId(staffRoleId);
//							roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
//							roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
//							roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
//							roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
//							roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
//							staff.setRole(roleStaff);
//						}
//
//						commande.setUtilisateur(staff);
//					}
//
//					// Récupérer la table si table_id présent
//					int tableId = rs.getInt("TABLE_ID");
//					if (!rs.wasNull()) {
//						TableRooftop table = tableDao.chercherParId(tableId);
//						commande.setTableRooftop(table);
//					}
//
//					commandes.add(commande);
//					System.out.println(
//							"Commande trouvée: " + commande.getNumeroCommande() + " - ID: " + commande.getId());
//				}
//
//				System.out.println("Nombre total de commandes trouvées: " + count);
//			}
//
//		} catch (SQLException e) {
//			System.out.println("Erreur SQL: " + e.getMessage());
//			e.printStackTrace();
//		}
//
//		System.out.println("=== FIN getAllCommandesVisiblesByFiltres ===");
//		System.out.println("Nombre de commandes retournées: " + commandes.size());
//
//		return commandes;
//	}
	public List<Commande> getAllCommandesVisiblesByFiltres(Integer staffId, Integer roleId, Integer platId,
	        Integer produitId, Date dateDebut, Date dateFin) {
	    // Appeler la nouvelle méthode avec rayonId = null (pour compatibilité)
	    return getAllCommandesVisiblesByFiltres(staffId, roleId, null, platId, produitId, dateDebut, dateFin);
	}

	// NOUVELLE méthode avec rayonId
	public List<Commande> getAllCommandesVisiblesByFiltres(Integer staffId, Integer roleId, Integer rayonId,
	        Integer platId, Integer produitId, Date dateDebut, Date dateFin) {
	    
	    List<Commande> commandes = new ArrayList<>();

	    System.out.println("=== DEBUT getAllCommandesVisiblesByFiltres (avec rayon) ===");
	    System.out.println("Paramètres reçus - staffId: " + staffId + ", roleId: " + roleId + 
	            ", rayonId: " + rayonId + ", platId: " + platId + ", produitId: " + produitId + 
	            ", dateDebut: " + dateDebut + ", dateFin: " + dateFin);

	    StringBuilder sql = new StringBuilder("SELECT DISTINCT " +
	            "c.*," +
	            // Client info
	            "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
	            + "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
	            + "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
	            + "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
	            + "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
	            + "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
	            + "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
	            +

	            // Staff info
	            "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
	            + "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
	            + "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
	            + "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
	            + "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
	            + "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
	            + "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
	            + "c.TABLE_ID, cd.RAYON_ID " + // Ajout du RAYON_ID pour le filtrage
	            "FROM COMMANDE c " + 
	            "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID " +
	            "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID " +
	            "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID " +
	            "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " +
	            "LEFT JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID " + 
	            "WHERE c.STATUT = 'VISIBLE' " + 
	            "AND c.STATUT_COMMANDE != 'ANNULE' ");

	    List<Object> params = new ArrayList<>();

	    if (staffId != null) {
	        sql.append(" AND staff.ID = ? ");
	        params.add(staffId);
	    }

	    if (roleId != null) {
	        sql.append(" AND role_staff.ID = ? ");
	        params.add(roleId);
	    }

	    // NOUVEAU: Filtre par rayon
	    if (rayonId != null) {
	        sql.append(" AND cd.RAYON_ID = ? ");
	        params.add(rayonId);
	    }

	    // Filtre plat/produit
	    if (produitId != null) {
	        sql.append(" AND cd.PRODUIT_ID = ? ");
	        params.add(produitId);
	    }
	    if (platId != null) {
	        sql.append(" AND cd.PLAT_ID = ? ");
	        params.add(platId);
	    }

	    if (dateDebut != null) {
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
	    }

	    if (dateFin != null) {
	        sql.append(" AND c.DATE_COMMANDE <= ? ");
	        params.add(new java.sql.Timestamp(dateFin.getTime()));
	    }

	    sql.append(" ORDER BY c.CREATED_AT DESC");

	    System.out.println("SQL généré: " + sql.toString());
	    System.out.println("Paramètres: " + params);

	    // ... le reste de votre code existant reste inchangé ...
	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			// Injecter les paramètres
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			System.out.println("Exécution de la requête...");

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Résultats de la requête:");
				int count = 0;

				while (rs.next()) {
					count++;
					Commande commande = new Commande();

					// Champs de Commande
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
					commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

					int deletedBy = rs.getInt("DELETED_BY");
					commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
					commande.setStatut(rs.getString("STATUT"));

					// Récupération du CLIENT avec rôle
					int clientId = rs.getInt("client_ID");
					if (!rs.wasNull()) {
						Utilisateur client = new Utilisateur();
						client.setId(clientId);
						client.setNom(rs.getString("client_NOM"));
						client.setPrenom(rs.getString("client_PRENOM"));
						client.setEmail(rs.getString("client_EMAIL"));
						client.setLogin(rs.getString("client_LOGIN"));
						client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
						client.setStatut(rs.getString("client_STATUT"));
						client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
						client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));

						int clientRoleId = rs.getInt("client_ROLE_ID");
						if (!rs.wasNull()) {
							Role roleClient = new Role();
							roleClient.setId(clientRoleId);
							roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
							roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
							roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
							roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
							roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
							client.setRole(roleClient);
						}

						commande.setClient(client);
					}

					// Récupération du STAFF avec rôle
					int idStaff = rs.getInt("staff_ID");
					if (!rs.wasNull()) {
						Utilisateur staff = new Utilisateur();
						staff.setId(idStaff);
						staff.setNom(rs.getString("staff_NOM"));
						staff.setPrenom(rs.getString("staff_PRENOM"));
						staff.setEmail(rs.getString("staff_EMAIL"));
						staff.setLogin(rs.getString("staff_LOGIN"));
						staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
						staff.setStatut(rs.getString("staff_STATUT"));
						staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
						staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));

						int staffRoleId = rs.getInt("staff_ROLE_ID");
						if (!rs.wasNull()) {
							Role roleStaff = new Role();
							roleStaff.setId(staffRoleId);
							roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
							roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
							roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
							roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
							roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
							staff.setRole(roleStaff);
						}

						commande.setUtilisateur(staff);
					}

					// Récupérer la table si table_id présent
					int tableId = rs.getInt("TABLE_ID");
					if (!rs.wasNull()) {
						TableRooftop table = tableDao.chercherParId(tableId);
						commande.setTableRooftop(table);
					}

					commandes.add(commande);
					System.out.println(
							"Commande trouvée: " + commande.getNumeroCommande() + " - ID: " + commande.getId());
				}

				System.out.println("Nombre total de commandes trouvées: " + count);
			}

		} catch (SQLException e) {
			System.out.println("Erreur SQL: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("=== FIN getAllCommandesVisiblesByFiltres ===");
		System.out.println("Nombre de commandes retournées: " + commandes.size());
	    
	    return commandes;
	}
	
	// Dans CommandeDAO.java
//	public List<Commande> getAllCommandesVisiblesByFiltresHierarchiques(
//	        Integer staffId, Integer roleId, Integer rayonId, Integer categorieId, 
//	        Integer sousCategorieId, Integer platId, Date dateDebut, Date dateFin) {
//	    
//	    List<Commande> commandes = new ArrayList<>();
//	    
//	    System.out.println("=== DEBUT getAllCommandesVisiblesByFiltresHierarchiques ===");
//	    System.out.println("Paramètres: staffId=" + staffId + ", roleId=" + roleId + 
//	            ", rayonId=" + rayonId + ", categorieId=" + categorieId + 
//	            ", sousCategorieId=" + sousCategorieId + ", platId=" + platId);
//
//	    StringBuilder sql = new StringBuilder("SELECT DISTINCT " +
//	            "c.*," +
//	            // Client info
//	            "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
//	            + "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
//	            + "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
//	            + "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
//	            + "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
//	            + "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
//	            + "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
//	            +
//
//	            // Staff info
//	            "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
//	            + "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
//	            + "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
//	            + "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
//	            + "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
//	            + "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
//	            + "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
//	            + "c.TABLE_ID, cd.CATEGORIE_ID, cd.SOUS_CATEGORIE_ID " +
//	            "FROM COMMANDE c " + 
//	            "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID " +
//	            "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID " +
//	            "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID " +
//	            "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " +
//	            "LEFT JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID " + 
//	            "WHERE c.STATUT = 'VISIBLE' " + 
//	            "AND c.STATUT_COMMANDE != 'ANNULE' ");
//
//	    List<Object> params = new ArrayList<>();
//
//	    if (staffId != null) {
//	        sql.append(" AND staff.ID = ? ");
//	        params.add(staffId);
//	    }
//
//	    if (roleId != null) {
//	        sql.append(" AND role_staff.ID = ? ");
//	        params.add(roleId);
//	    }
//
//	    // Filtres hiérarchiques
//	    if (rayonId != null) {
//	        sql.append(" AND cd.RAYON_ID = ? ");
//	        params.add(rayonId);
//	    }
//	    
//	    if (categorieId != null) {
//	        sql.append(" AND cd.CATEGORIE_ID = ? ");
//	        params.add(categorieId);
//	    }
//	    
//	    if (sousCategorieId != null) {
//	        sql.append(" AND cd.SOUS_CATEGORIE_ID = ? ");
//	        params.add(sousCategorieId);
//	    }
//	    
//	    if (platId != null) {
//	        sql.append(" AND cd.PLAT_ID = ? ");
//	        params.add(platId);
//	    }
//
//	    if (dateDebut != null) {
//	        sql.append(" AND c.DATE_COMMANDE >= ? ");
//	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
//	    }
//
//	    if (dateFin != null) {
//	        sql.append(" AND c.DATE_COMMANDE <= ? ");
//	        params.add(new java.sql.Timestamp(dateFin.getTime()));
//	    }
//
//	    sql.append(" ORDER BY c.CREATED_AT DESC");
//
//	    System.out.println("SQL généré: " + sql.toString());
//	    System.out.println("Paramètres: " + params);
//
//	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//
//			// Injecter les paramètres
//			for (int i = 0; i < params.size(); i++) {
//				stmt.setObject(i + 1, params.get(i));
//			}
//
//			System.out.println("Exécution de la requête...");
//
//			try (ResultSet rs = stmt.executeQuery()) {
//				System.out.println("Résultats de la requête:");
//				int count = 0;
//
//				while (rs.next()) {
//					count++;
//					Commande commande = new Commande();
//
//					// Champs de Commande
//					commande.setId(rs.getInt("ID"));
//					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
//					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
//					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
//					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
//					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
//					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
//					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
//					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//					commande.setNotes(rs.getString("NOTES"));
//					commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
//					commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
//
//					int deletedBy = rs.getInt("DELETED_BY");
//					commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
//					commande.setStatut(rs.getString("STATUT"));
//
//					// Récupération du CLIENT avec rôle
//					int clientId = rs.getInt("client_ID");
//					if (!rs.wasNull()) {
//						Utilisateur client = new Utilisateur();
//						client.setId(clientId);
//						client.setNom(rs.getString("client_NOM"));
//						client.setPrenom(rs.getString("client_PRENOM"));
//						client.setEmail(rs.getString("client_EMAIL"));
//						client.setLogin(rs.getString("client_LOGIN"));
//						client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
//						client.setStatut(rs.getString("client_STATUT"));
//						client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
//						client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));
//
//						int clientRoleId = rs.getInt("client_ROLE_ID");
//						if (!rs.wasNull()) {
//							Role roleClient = new Role();
//							roleClient.setId(clientRoleId);
//							roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
//							roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
//							roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
//							roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
//							roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
//							client.setRole(roleClient);
//						}
//
//						commande.setClient(client);
//					}
//
//					// Récupération du STAFF avec rôle
//					int idStaff = rs.getInt("staff_ID");
//					if (!rs.wasNull()) {
//						Utilisateur staff = new Utilisateur();
//						staff.setId(idStaff);
//						staff.setNom(rs.getString("staff_NOM"));
//						staff.setPrenom(rs.getString("staff_PRENOM"));
//						staff.setEmail(rs.getString("staff_EMAIL"));
//						staff.setLogin(rs.getString("staff_LOGIN"));
//						staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
//						staff.setStatut(rs.getString("staff_STATUT"));
//						staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
//						staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));
//
//						int staffRoleId = rs.getInt("staff_ROLE_ID");
//						if (!rs.wasNull()) {
//							Role roleStaff = new Role();
//							roleStaff.setId(staffRoleId);
//							roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
//							roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
//							roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
//							roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
//							roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
//							staff.setRole(roleStaff);
//						}
//
//						commande.setUtilisateur(staff);
//					}
//
//					// Récupérer la table si table_id présent
//					int tableId = rs.getInt("TABLE_ID");
//					if (!rs.wasNull()) {
//						TableRooftop table = tableDao.chercherParId(tableId);
//						commande.setTableRooftop(table);
//					}
//
//					commandes.add(commande);
//					System.out.println(
//							"Commande trouvée: " + commande.getNumeroCommande() + " - ID: " + commande.getId());
//				}
//
//				System.out.println("Nombre total de commandes trouvées: " + count);
//			}
//
//		} catch (SQLException e) {
//			System.out.println("Erreur SQL: " + e.getMessage());
//			e.printStackTrace();
//		}
//
//	    System.out.println("=== FIN getAllCommandesVisiblesByFiltresHierarchiques ===");
//	    System.out.println("Nombre de commandes retournées: " + commandes.size());
//
//	    return commandes;
//	}
//	public List<Commande> getAllCommandesVisiblesByFiltresHierarchiques(
//	        Integer staffId, Integer roleId, Integer rayonId, Integer categorieId, 
//	        Integer sousCategorieId, Integer platId, Date dateDebut, Date dateFin) {
//	    
//	    List<Commande> commandes = new ArrayList<>();
//	    
//	    System.out.println("=== DEBUT getAllCommandesVisiblesByFiltresHierarchiques ===");
//	    System.out.println("Paramètres: staffId=" + staffId + ", roleId=" + roleId + 
//	            ", rayonId=" + rayonId + ", categorieId=" + categorieId + 
//	            ", sousCategorieId=" + sousCategorieId + ", platId=" + platId);
//
//	    // Version optimisée avec EXISTS pour éviter les doublons
//	    StringBuilder sql = new StringBuilder("SELECT DISTINCT " +
//	            "c.*," +
//	            // Client info
//	            "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
//	            + "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
//	            + "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
//	            + "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
//	            + "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
//	            + "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
//	            + "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
//	            +
//
//	            // Staff info
//	            "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
//	            + "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
//	            + "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
//	            + "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
//	            + "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
//	            + "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
//	            + "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
//	            + "c.TABLE_ID " +
//	            "FROM COMMANDE c " + 
//	            "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID " +
//	            "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID " +
//	            "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID " +
//	            "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " +
//	            "WHERE c.STATUT = 'VISIBLE' " + 
//	            "AND c.STATUT_COMMANDE != 'ANNULE' ");
//
//	    List<Object> params = new ArrayList<>();
//
//	    if (staffId != null) {
//	        sql.append(" AND staff.ID = ? ");
//	        params.add(staffId);
//	    }
//
//	    if (roleId != null) {
//	        sql.append(" AND role_staff.ID = ? ");
//	        params.add(roleId);
//	    }
//
//	    // Filtres hiérarchiques avec EXISTS
//	    if (rayonId != null || categorieId != null || sousCategorieId != null || platId != null) {
//	        sql.append(" AND EXISTS (SELECT 1 FROM COMMANDE_DETAIL cd ");
//	        sql.append("LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID ");
//	        sql.append("LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID ");
//	        sql.append("WHERE cd.COMMANDE_ID = c.ID ");
//	        
//	        List<Object> subParams = new ArrayList<>();
//	        
//	        if (rayonId != null) {
//	            sql.append(" AND (p.RAYON_ID = ? OR prod.RAYON_ID = ?) ");
//	            subParams.add(rayonId);
//	            subParams.add(rayonId);
//	        }
//	        
//	        if (categorieId != null) {
//	            sql.append(" AND (p.CATEGORIE_ID = ? OR prod.CATEGORIE_ID = ?) ");
//	            subParams.add(categorieId);
//	            subParams.add(categorieId);
//	        }
//	        
//	        if (sousCategorieId != null) {
//	            sql.append(" AND (p.SOUS_CATEGORIE_ID = ? OR prod.SOUS_CATEGORIE_ID = ?) ");
//	            subParams.add(sousCategorieId);
//	            subParams.add(sousCategorieId);
//	        }
//	        
//	        if (platId != null) {
//	            sql.append(" AND cd.PLAT_ID = ? ");
//	            subParams.add(platId);
//	        }
//	        
//	        sql.append(") ");
//	        params.addAll(subParams);
//	    }
//
//	    if (dateDebut != null) {
//	        sql.append(" AND c.DATE_COMMANDE >= ? ");
//	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
//	    }
//
//	    if (dateFin != null) {
//	        sql.append(" AND c.DATE_COMMANDE <= ? ");
//	        params.add(new java.sql.Timestamp(dateFin.getTime()));
//	    }
//
//	    sql.append(" ORDER BY c.CREATED_AT DESC");
//
//	    System.out.println("SQL généré: " + sql.toString());
//	    System.out.println("Paramètres: " + params);
//
//	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//
//	        // Injecter les paramètres
//	        for (int i = 0; i < params.size(); i++) {
//	            stmt.setObject(i + 1, params.get(i));
//	        }
//
//	        System.out.println("Exécution de la requête...");
//
//	        try (ResultSet rs = stmt.executeQuery()) {
//	            System.out.println("Résultats de la requête:");
//	            int count = 0;
//
//	            while (rs.next()) {
//	                count++;
//	                Commande commande = new Commande();
//
//	                // Champs de Commande
//	                commande.setId(rs.getInt("ID"));
//	                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
//	                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
//	                commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
//	                commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
//	                commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
//	                commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
//	                commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
//	                commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//	                commande.setNotes(rs.getString("NOTES"));
//	                commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
//	                commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
//
//	                int deletedBy = rs.getInt("DELETED_BY");
//	                commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
//	                commande.setStatut(rs.getString("STATUT"));
//
//	                // Récupération du CLIENT avec rôle
//	                int clientId = rs.getInt("client_ID");
//	                if (!rs.wasNull()) {
//	                    Utilisateur client = new Utilisateur();
//	                    client.setId(clientId);
//	                    client.setNom(rs.getString("client_NOM"));
//	                    client.setPrenom(rs.getString("client_PRENOM"));
//	                    client.setEmail(rs.getString("client_EMAIL"));
//	                    client.setLogin(rs.getString("client_LOGIN"));
//	                    client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
//	                    client.setStatut(rs.getString("client_STATUT"));
//	                    client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
//	                    client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));
//
//	                    int clientRoleId = rs.getInt("client_ROLE_ID");
//	                    if (!rs.wasNull()) {
//	                        Role roleClient = new Role();
//	                        roleClient.setId(clientRoleId);
//	                        roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
//	                        roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
//	                        roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
//	                        roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
//	                        roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
//	                        client.setRole(roleClient);
//	                    }
//
//	                    commande.setClient(client);
//	                }
//
//	                // Récupération du STAFF avec rôle
//	                int idStaff = rs.getInt("staff_ID");
//	                if (!rs.wasNull()) {
//	                    Utilisateur staff = new Utilisateur();
//	                    staff.setId(idStaff);
//	                    staff.setNom(rs.getString("staff_NOM"));
//	                    staff.setPrenom(rs.getString("staff_PRENOM"));
//	                    staff.setEmail(rs.getString("staff_EMAIL"));
//	                    staff.setLogin(rs.getString("staff_LOGIN"));
//	                    staff.setMotDePasse(rs.getString("staff_MOT_DE_PASse"));
//	                    staff.setStatut(rs.getString("staff_STATUT"));
//	                    staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
//	                    staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));
//
//	                    int staffRoleId = rs.getInt("staff_ROLE_ID");
//	                    if (!rs.wasNull()) {
//	                        Role roleStaff = new Role();
//	                        roleStaff.setId(staffRoleId);
//	                        roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
//	                        roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
//	                        roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
//	                        roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
//	                        roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
//	                        staff.setRole(roleStaff);
//	                    }
//
//	                    commande.setUtilisateur(staff);
//	                }
//
//	                // Récupérer la table si table_id présent
//	                int tableId = rs.getInt("TABLE_ID");
//	                if (!rs.wasNull()) {
//	                    TableRooftop table = tableDao.chercherParId(tableId);
//	                    commande.setTableRooftop(table);
//	                }
//
//	                commandes.add(commande);
//	                System.out.println(
//	                        "Commande trouvée: " + commande.getNumeroCommande() + " - ID: " + commande.getId());
//	            }
//
//	            System.out.println("Nombre total de commandes trouvées: " + count);
//	        }
//
//	    } catch (SQLException e) {
//	        System.out.println("Erreur SQL: " + e.getMessage());
//	        e.printStackTrace();
//	    }
//
//	    System.out.println("=== FIN getAllCommandesVisiblesByFiltresHierarchiques ===");
//	    System.out.println("Nombre de commandes retournées: " + commandes.size());
//
//	    return commandes;
//	}
	public List<Commande> getAllCommandesVisiblesByFiltresHierarchiques(
	        Integer staffId, Integer roleId, Integer rayonId, Integer categorieId, 
	        Integer sousCategorieId, Integer platId, Date dateDebut, Date dateFin) {
	    
	    List<Commande> commandes = new ArrayList<>();
	    
	    System.out.println("=== DEBUT getAllCommandesVisiblesByFiltresHierarchiques ===");
	    System.out.println("Paramètres: staffId=" + staffId + ", roleId=" + roleId + 
	            ", rayonId=" + rayonId + ", categorieId=" + categorieId + 
	            ", sousCategorieId=" + sousCategorieId + ", platId=" + platId);

	    // Version corrigée avec calcul du montant total
	    StringBuilder sql = new StringBuilder("SELECT DISTINCT " +
	            "c.*," +
	            // Client info
	            "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM,"
	            + "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN,"
	            + "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT,"
	            + "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE,"
	            + "role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME,"
	            + "role_client.DROITS AS client_ROLE_DROITS, role_client.STATUT AS client_ROLE_STATUT,"
	            + "role_client.CREATED_AT AS client_ROLE_CREATED_AT, role_client.UPDATED_AT AS client_ROLE_UPDATED_AT,"
	            +

	            // Staff info
	            "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM,"
	            + "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN,"
	            + "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT,"
	            + "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE,"
	            + "role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME,"
	            + "role_staff.DROITS AS staff_ROLE_DROITS, role_staff.STATUT AS staff_ROLE_STATUT,"
	            + "role_staff.CREATED_AT AS staff_ROLE_CREATED_AT, role_staff.UPDATED_AT AS staff_ROLE_UPDATED_AT,"
	            + "c.TABLE_ID, " +
	            
	            // CALCUL DU MONTANT TOTAL CORRIGÉ - AJOUTER CETTE LIGNE
	            "(SELECT SUM(cd2.PRIX_UNITAIRE * cd2.QUANTITE) FROM COMMANDE_DETAIL cd2 " +
	            "WHERE cd2.COMMANDE_ID = c.ID AND cd2.STATUT = 'VISIBLE') AS MONTANT_CALCULE " +
	            
	            "FROM COMMANDE c " + 
	            "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID " +
	            "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID " +
	            "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID " +
	            "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " +
	            "WHERE c.STATUT = 'VISIBLE' " + 
	            "AND c.STATUT_COMMANDE != 'ANNULE' ");

	    List<Object> params = new ArrayList<>();

	    if (staffId != null) {
	        sql.append(" AND staff.ID = ? ");
	        params.add(staffId);
	    }

	    if (roleId != null) {
	        sql.append(" AND role_staff.ID = ? ");
	        params.add(roleId);
	    }

	    // Filtres hiérarchiques avec EXISTS
	    if (rayonId != null || categorieId != null || sousCategorieId != null || platId != null) {
	        sql.append(" AND EXISTS (SELECT 1 FROM COMMANDE_DETAIL cd ");
	        sql.append("LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID ");
	        sql.append("LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID ");
	        sql.append("WHERE cd.COMMANDE_ID = c.ID ");
	        sql.append("AND cd.STATUT = 'VISIBLE' ");
	        
	        List<Object> subParams = new ArrayList<>();
	        
	        if (rayonId != null) {
	            sql.append(" AND (p.RAYON_ID = ? OR prod.RAYON_ID = ?) ");
	            subParams.add(rayonId);
	            subParams.add(rayonId);
	        }
	        
	        if (categorieId != null) {
	            sql.append(" AND (p.CATEGORIE_ID = ? OR prod.CATEGORIE_ID = ?) ");
	            subParams.add(categorieId);
	            subParams.add(categorieId);
	        }
	        
	        if (sousCategorieId != null) {
	            sql.append(" AND (p.SOUS_CATEGORIE_ID = ? OR prod.SOUS_CATEGORIE_ID = ?) ");
	            subParams.add(sousCategorieId);
	            subParams.add(sousCategorieId);
	        }
	        
	        if (platId != null) {
	            sql.append(" AND cd.PLAT_ID = ? ");
	            subParams.add(platId);
	        }
	        
	        sql.append(") ");
	        params.addAll(subParams);
	    }

	    if (dateDebut != null) {
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
	    }

	    if (dateFin != null) {
	        sql.append(" AND c.DATE_COMMANDE <= ? ");
	        params.add(new java.sql.Timestamp(dateFin.getTime()));
	    }

	    sql.append(" ORDER BY c.CREATED_AT DESC");

	    System.out.println("SQL généré: " + sql.toString());
	    System.out.println("Paramètres: " + params);

	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        // Injecter les paramètres
	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }

	        System.out.println("Exécution de la requête...");

	        try (ResultSet rs = stmt.executeQuery()) {
	            System.out.println("Résultats de la requête:");
	            int count = 0;
	            BigDecimal totalGeneral = BigDecimal.ZERO;

	            while (rs.next()) {
	                count++;
	                Commande commande = new Commande();

	                // Champs de Commande
	                commande.setId(rs.getInt("ID"));
	                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
	                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
	                commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
	                
	                // UTILISER LE MONTANT CALCULÉ AU LIEU DE MONTANT_TOTAL
	                BigDecimal montantCalcule = rs.getBigDecimal("MONTANT_CALCULE");
	                commande.setMontantTotal(montantCalcule);
	                
	                // Pour comparaison, récupérer aussi le montant stocké
	                BigDecimal montantStored = rs.getBigDecimal("MONTANT_TOTAL");
	                
	                // Calculer le total général
	                totalGeneral = totalGeneral.add(montantCalcule != null ? montantCalcule : BigDecimal.ZERO);
	                
	                commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
	                commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
	                commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
	                commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	                commande.setNotes(rs.getString("NOTES"));
	                commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
	                commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

	                int deletedBy = rs.getInt("DELETED_BY");
	                commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
	                commande.setStatut(rs.getString("STATUT"));

	                // Récupération du CLIENT avec rôle
	                int clientId = rs.getInt("client_ID");
	                if (!rs.wasNull()) {
	                    Utilisateur client = new Utilisateur();
	                    client.setId(clientId);
	                    client.setNom(rs.getString("client_NOM"));
	                    client.setPrenom(rs.getString("client_PRENOM"));
	                    client.setEmail(rs.getString("client_EMAIL"));
	                    client.setLogin(rs.getString("client_LOGIN"));
	                    client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
	                    client.setStatut(rs.getString("client_STATUT"));
	                    client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
	                    client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));

	                    int clientRoleId = rs.getInt("client_ROLE_ID");
	                    if (!rs.wasNull()) {
	                        Role roleClient = new Role();
	                        roleClient.setId(clientRoleId);
	                        roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
	                        roleClient.setDroits(rs.getString("client_ROLE_DROITS"));
	                        roleClient.setStatut(rs.getString("client_ROLE_STATUT"));
	                        roleClient.setCreatedAt(rs.getTimestamp("client_ROLE_CREATED_AT"));
	                        roleClient.setUpdatedAt(rs.getTimestamp("client_ROLE_UPDATED_AT"));
	                        client.setRole(roleClient);
	                    }

	                    commande.setClient(client);
	                }

	                // Récupération du STAFF avec rôle
	                int idStaff = rs.getInt("staff_ID");
	                if (!rs.wasNull()) {
	                    Utilisateur staff = new Utilisateur();
	                    staff.setId(idStaff);
	                    staff.setNom(rs.getString("staff_NOM"));
	                    staff.setPrenom(rs.getString("staff_PRENOM"));
	                    staff.setEmail(rs.getString("staff_EMAIL"));
	                    staff.setLogin(rs.getString("staff_LOGIN"));
	                    staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
	                    staff.setStatut(rs.getString("staff_STATUT"));
	                    staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
	                    staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));

	                    int staffRoleId = rs.getInt("staff_ROLE_ID");
	                    if (!rs.wasNull()) {
	                        Role roleStaff = new Role();
	                        roleStaff.setId(staffRoleId);
	                        roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
	                        roleStaff.setDroits(rs.getString("staff_ROLE_DROITS"));
	                        roleStaff.setStatut(rs.getString("staff_ROLE_STATUT"));
	                        roleStaff.setCreatedAt(rs.getTimestamp("staff_ROLE_CREATED_AT"));
	                        roleStaff.setUpdatedAt(rs.getTimestamp("staff_ROLE_UPDATED_AT"));
	                        staff.setRole(roleStaff);
	                    }

	                    commande.setUtilisateur(staff);
	                }

	                // Récupérer la table si table_id présent
	                int tableId = rs.getInt("TABLE_ID");
	                if (!rs.wasNull()) {
	                    TableRooftop table = tableDao.chercherParId(tableId);
	                    commande.setTableRooftop(table);
	                }

	                commandes.add(commande);
	                System.out.println(
	                        "Commande trouvée: " + commande.getNumeroCommande() + " - ID: " + commande.getId() +
	                        " - Montant calculé: " + (montantCalcule != null ? montantCalcule : "0") +
	                        " - Montant stocké: " + (montantStored != null ? montantStored : "0") +
	                        " - Différence: " + (montantCalcule != null && montantStored != null ? 
	                            montantCalcule.subtract(montantStored) : "N/A"));
	            }

	            System.out.println("Nombre total de commandes trouvées: " + count);
	            System.out.println("Total général calculé: " + totalGeneral);
	        }

	    } catch (SQLException e) {
	        System.out.println("Erreur SQL: " + e.getMessage());
	        e.printStackTrace();
	    }

	    System.out.println("=== FIN getAllCommandesVisiblesByFiltresHierarchiques ===");
	    System.out.println("Nombre de commandes retournées: " + commandes.size());

	    return commandes;
	}


	public List<RapportCommande> getRapportCommandesByCategoryAndSubcategory(Integer staffId, Integer roleId,
	        Integer categorieId, Integer sousCategorieId, Date dateDebut, Date dateFin) {

	    List<RapportCommande> rapports = new ArrayList<>();

	    StringBuilder sql = new StringBuilder("SELECT "
	            + "   COALESCE(cd.PLAT_ID, cd.PRODUIT_ID) AS item_id, "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN 'PLAT' "
	            + "       ELSE 'PRODUIT' "
	            + "   END AS item_type, "
	            + "   COALESCE(p.NOM, prod.NOM) AS NOM, "
	            + "   SUM(cd.QUANTITE) AS total_quantite, "
	            + "   SUM(cd.SOUS_TOTAL) AS total_montant, "  // Utiliser SOUS_TOTAL au lieu de calculer
	            + "   MAX(cd.PRIX_UNITAIRE) AS prix_unitaire, "
	            + "   MAX(c.DATE_COMMANDE) AS date_vente "
	            + "FROM COMMANDE c "
	            + "JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID "
	            + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
	            + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
	            + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
	            + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
	            + "WHERE c.STATUT = 'VISIBLE' "
	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
	            + "   AND cd.STATUT = 'VISIBLE' ");

	    List<Object> params = new ArrayList<>();

	    // Filtre staff
	    if (staffId != null) {
	        sql.append(" AND c.UTILISATEUR_ID = ? ");
	        params.add(staffId);
	    }

	    // Filtre role - simplifié
	    if (roleId != null) {
	        sql.append(" AND EXISTS ( "
	                + "   SELECT 1 FROM UTILISATEUR u "
	                + "   LEFT JOIN ROLE r ON u.ID_ROLE = r.ID "
	                + "   WHERE u.ID = c.UTILISATEUR_ID AND r.ID = ? "
	                + ") ");
	        params.add(roleId);
	    }

	    // Filtre catégorie - corrigé
	    if (categorieId != null) {
	        sql.append(" AND ( "
	                + "   (cd.PLAT_ID IS NOT NULL AND EXISTS ("
	                + "       SELECT 1 FROM PLAT p2 "
	                + "       WHERE p2.ID = cd.PLAT_ID AND p2.CATEGORIE_ID = ?"
	                + "   )) "
	                + "   OR (cd.PRODUIT_ID IS NOT NULL AND EXISTS ("
	                + "       SELECT 1 FROM PRODUITS pr "
	                + "       LEFT JOIN PLAT pl ON pr.ID = pl.PRODUIT_ID "
	                + "       WHERE pr.ID = cd.PRODUIT_ID AND pl.CATEGORIE_ID = ?"
	                + "   )) "
	                + ") ");
	        params.add(categorieId);
	        params.add(categorieId);
	    }

	    // Filtre sous-catégorie - corrigé
	    if (sousCategorieId != null) {
	        sql.append(" AND ( "
	                + "   (cd.PLAT_ID IS NOT NULL AND EXISTS ("
	                + "       SELECT 1 FROM PLAT p2 "
	                + "       WHERE p2.ID = cd.PLAT_ID AND p2.SOUS_CATEGORIE_ID = ?"
	                + "   )) "
	                + "   OR (cd.PRODUIT_ID IS NOT NULL AND EXISTS ("
	                + "       SELECT 1 FROM PRODUITS pr "
	                + "       LEFT JOIN PLAT pl ON pr.ID = pl.PRODUIT_ID "
	                + "       WHERE pr.ID = cd.PRODUCT_ID AND pl.SOUS_CATEGORIE_ID = ?"
	                + "   )) "
	                + ") ");
	        params.add(sousCategorieId);
	        params.add(sousCategorieId);
	    }

	    // Filtre date
	    if (dateDebut != null) {
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
	    }
	    if (dateFin != null) {
	        sql.append(" AND c.DATE_COMMANDE <= ? ");
	        params.add(new java.sql.Timestamp(dateFin.getTime()));
	    }

	    sql.append(" GROUP BY "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN CONCAT('PLAT_', cd.PLAT_ID) "
	            + "       ELSE CONCAT('PRODUIT_', cd.PRODUIT_ID) "
	            + "   END, "
	            + "   COALESCE(p.NOM, prod.NOM) "
	            + "ORDER BY total_quantite DESC");

	    System.out.println("SQL généré: " + sql.toString());
	    System.out.println("Paramètres: " + params);

	    try (Connection conn = getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < params.size(); i++) {
	            Object param = params.get(i);
	            if (param instanceof Integer) {
	                stmt.setInt(i + 1, (Integer) param);
	            } else if (param instanceof Timestamp) {
	                stmt.setTimestamp(i + 1, (Timestamp) param);
	            } else {
	                stmt.setObject(i + 1, param);
	            }
	        }

	        System.out.println("Exécution de la requête...");

	        try (ResultSet rs = stmt.executeQuery()) {
	            int count = 0;
	            int totalQuantite = 0;
	            int totalMontant = 0;

	            while (rs.next()) {
	                count++;
	                RapportCommande rapport = new RapportCommande();

	                String itemType = rs.getString("item_type");
	                int itemId = rs.getInt("item_id");
	                
	                if ("PLAT".equals(itemType)) {
	                    rapport.setPlatId(itemId);
	                    rapport.setProduitId(null);
	                } else {
	                    rapport.setPlatId(null);
	                    rapport.setProduitId(itemId);
	                }

	                rapport.setNomPlat(rs.getString("NOM"));
	                rapport.setQuantiteTotale(rs.getInt("total_quantite"));
	                rapport.setMontantTotal(rs.getInt("total_montant"));
	                rapport.setPrixUnitaire(rs.getInt("prix_unitaire"));
	                
	                Timestamp dateVente = rs.getTimestamp("date_vente");
	                rapport.setDateVente(dateVente);

	                totalQuantite += rs.getInt("total_quantite");
	                totalMontant += rs.getInt("total_montant");

	                rapports.add(rapport);
	            }

	            System.out.println("Nombre total de lignes trouvées: " + count);
	            System.out.println("Total quantité: " + totalQuantite);
	            System.out.println("Total montant: " + totalMontant);
	        }

	    } catch (SQLException e) {
	        System.out.println("Erreur SQL: " + e.getMessage());
	        e.printStackTrace();
	    }

	    System.out.println("=== FIN getRapportCommandesByCategoryAndSubcategory ===");
	    System.out.println("Nombre de rapports retournés: " + rapports.size());

	    return rapports;
	}
	public List<RapportCommande> getRapportCommandes(Integer staffId, Integer roleId, Integer platId, Integer produitId,
			Date dateDebut, Date dateFin) {

		List<RapportCommande> rapports = new ArrayList<>();

		System.out.println("=== DEBUT getRapportCommandes ===");
		System.out.println("Paramètres reçus - staffId: " + staffId + ", roleId: " + roleId + ", platId: " + platId
				+ ", produitId: " + produitId + ", dateDebut: " + dateDebut + ", dateFin: " + dateFin);

		StringBuilder sql = new StringBuilder("SELECT " + "cd.PLAT_ID, " + "cd.PRODUIT_ID, "
				+ "COALESCE(p.NOM, prod.NOM) AS NOM, " + "SUM(cd.QUANTITE) AS total_quantite, "
				+ "SUM(cd.PRIX_UNITAIRE * cd.QUANTITE) AS total_montant, " + "MAX(cd.PRIX_UNITAIRE) AS prix_unitaire, "
				+ "MAX(c.DATE_COMMANDE) AS date_vente " + "FROM COMMANDE_DETAIL cd "
				+ "INNER JOIN COMMANDE c ON cd.COMMANDE_ID = c.ID " + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
				+ "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
				+ "INNER JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
				+ "INNER JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID " + "WHERE c.STATUT = 'VISIBLE' "
				+ "AND c.STATUT_COMMANDE != 'ANNULE' " + "AND cd.STATUT = 'VISIBLE' " // <-- Ajout du filtre pour ne
																						// compter que les détails
																						// visibles
		);

		List<Object> params = new ArrayList<>();

		if (staffId != null) {
			sql.append(" AND staff.ID = ? ");
			params.add(staffId);
		}

		if (roleId != null) {
			sql.append(" AND role_staff.ID = ? ");
			params.add(roleId);
		}

		if (platId != null) {
			sql.append(" AND cd.PLAT_ID = ? ");
			params.add(platId);
		}

		if (produitId != null) {
			sql.append(" AND cd.PRODUIT_ID = ? ");
			params.add(produitId);
		}

		if (dateDebut != null) {
			sql.append(" AND c.DATE_COMMANDE >= ? ");
			params.add(new java.sql.Timestamp(dateDebut.getTime()));
		}

		if (dateFin != null) {
			sql.append(" AND c.DATE_COMMANDE <= ? ");
			params.add(new java.sql.Timestamp(dateFin.getTime()));
		}

		sql.append(" GROUP BY cd.PLAT_ID, cd.PRODUIT_ID, NOM ");
		sql.append(" ORDER BY total_quantite DESC");

		System.out.println("SQL généré: " + sql.toString());
		System.out.println("Paramètres: " + params);

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			// Injection des paramètres
			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof Integer) {
					stmt.setInt(i + 1, (Integer) param);
				} else if (param instanceof Timestamp) {
					stmt.setTimestamp(i + 1, (Timestamp) param);
				} else {
					stmt.setObject(i + 1, param);
				}
			}

			System.out.println("Exécution de la requête...");

			try (ResultSet rs = stmt.executeQuery()) {
				int count = 0;

				while (rs.next()) {
					count++;
					RapportCommande rapport = new RapportCommande();

					int plat = rs.getInt("PLAT_ID");
					rapport.setPlatId(rs.wasNull() ? null : plat);

					int prod = rs.getInt("PRODUIT_ID");
					rapport.setProduitId(rs.wasNull() ? null : prod);

					rapport.setNomPlat(rs.getString("NOM"));
					rapport.setQuantiteTotale(rs.getInt("total_quantite"));
					rapport.setMontantTotal(rs.getInt("total_montant"));

					int prix = rs.getInt("prix_unitaire");
					rapport.setPrixUnitaire(rs.wasNull() ? null : prix);

					Timestamp dateVente = rs.getTimestamp("date_vente");
					rapport.setDateVente(dateVente);

					rapports.add(rapport);

					System.out.println("Rapport trouvé: " + rapport.getNomPlat() + " - Quantité: "
							+ rapport.getQuantiteTotale() + " - Prix unitaire: " + rapport.getPrixUnitaire());
				}

				System.out.println("Nombre total de lignes trouvées: " + count);
			}

		} catch (SQLException e) {
			System.out.println("Erreur SQL: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("=== FIN getRapportCommandesss ===");
		System.out.println("Nombre de rapports retournés: " + rapports.size());

		return rapports;
	}


	public List<RapportCommande> getRapportCommandesCashedByCaissierForUser(
	        Integer staffId, Integer roleId,
	        Integer platId, Integer produitId,
	        Integer caissierId,
	        Integer categorieId, Integer sousCategorieId,
	        Date dateDebut, Date dateFin) {

	    List<RapportCommande> rapports = new ArrayList<>();

	    StringBuilder sql = new StringBuilder(
	            "SELECT "
	            + "   COALESCE(cd.PLAT_ID, cd.PRODUIT_ID) AS item_id, "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN 'PLAT' "
	            + "       ELSE 'PRODUIT' "
	            + "   END AS item_type, "
	            + "   COALESCE(p.NOM, prod.NOM) AS NOM, "
	            + "   SUM(cd.QUANTITE) AS total_quantite, "
	            + "   SUM(cd.SOUS_TOTAL) AS total_montant, "
	            + "   MAX(cd.PRIX_UNITAIRE) AS prix_unitaire, "
	            + "   MAX(c.DATE_COMMANDE) AS date_vente, "
	            + "   COUNT(DISTINCT c.ID) AS nombre_commandes, "
	            + "   c.CASHED_BY "  // Ajouter CASHED_BY pour vérification
	            + "FROM COMMANDE c "
	            + "INNER JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID "
	            + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
	            + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
	            + "WHERE c.STATUT = 'VISIBLE' "
	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
	            + "   AND cd.STATUT = 'VISIBLE' "
	    );

	    List<Object> params = new ArrayList<>();

	    // Filtrer par caissier
	    if (caissierId != null) {
	        if (caissierId == -1) {
	            sql.append(" AND c.CASHED_BY IS NULL ");
	        } else {
	            sql.append(" AND c.CASHED_BY = ? ");
	            params.add(caissierId);
	        }
	    }

	    // CORRECTION : Filtrer par staff/client comme dans l'ancienne version
	    if (staffId != null) {
	        sql.append(" AND (c.UTILISATEUR_ID = ? OR c.CLIENT_ID = ?) ");
	        params.add(staffId);
	        params.add(staffId);
	    }

	    // CORRECTION : Filtrer par rôle comme dans l'ancienne version
	    if (roleId != null) {
	        // Ajouter les JOIN nécessaires pour les rôles
	        sql.insert(0, 
	            "SELECT "
	            + "   COALESCE(cd.PLAT_ID, cd.PRODUIT_ID) AS item_id, "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN 'PLAT' "
	            + "       ELSE 'PRODUIT' "
	            + "   END AS item_type, "
	            + "   COALESCE(p.NOM, prod.NOM) AS NOM, "
	            + "   SUM(cd.QUANTITE) AS total_quantite, "
	            + "   SUM(cd.SOUS_TOTAL) AS total_montant, "
	            + "   MAX(cd.PRIX_UNITAIRE) AS prix_unitaire, "
	            + "   MAX(c.DATE_COMMANDE) AS date_vente, "
	            + "   COUNT(DISTINCT c.ID) AS nombre_commandes, "
	            + "   c.CASHED_BY "
	            + "FROM COMMANDE c "
	            + "INNER JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID "
	            + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
	            + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
	            + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
	            + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
	            + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
	            + "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID "
	            + "WHERE c.STATUT = 'VISIBLE' "
	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
	            + "   AND cd.STATUT = 'VISIBLE' "
	        );
	        
	        // Remplacer le WHERE existant
	        int whereIndex = sql.indexOf("WHERE c.STATUT = 'VISIBLE'");
	        sql.replace(whereIndex, sql.length(), 
	            "WHERE c.STATUT = 'VISIBLE' "
	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
	            + "   AND cd.STATUT = 'VISIBLE' "
	        );
	        
	        sql.append(" AND (role_staff.ID = ? OR role_client.ID = ?) ");
	        params.add(roleId);
	        params.add(roleId);
	    }

	    // Filtrer par plat ID
	    if (platId != null) {
	        sql.append(" AND cd.PLAT_ID = ? ");
	        params.add(platId);
	    }

	    // Filtrer par produit ID
	    if (produitId != null) {
	        sql.append(" AND cd.PRODUIT_ID = ? ");
	        params.add(produitId);
	    }

	    // Filtre catégorie - version simplifiée
	    if (categorieId != null) {
	        sql.append(
	                " AND ( " +
	                "   (cd.PLAT_ID IS NOT NULL AND EXISTS (" +
	                "       SELECT 1 FROM PLAT p2 " +
	                "       WHERE p2.ID = cd.PLAT_ID AND p2.CATEGORIE_ID = ?" +
	                "   )) " +
	                "   OR (cd.PRODUIT_ID IS NOT NULL AND EXISTS (" +
	                "       SELECT 1 FROM PRODUITS pr " +
	                "       LEFT JOIN PLAT pl ON pr.ID = pl.PRODUIT_ID " +
	                "       WHERE pr.ID = cd.PRODUIT_ID AND pl.CATEGORIE_ID = ?" +
	                "   )) " +
	                ") "
	        );
	        params.add(categorieId);
	        params.add(categorieId);
	    }

	    // Filtre sous-catégorie - version simplifiée
	    if (sousCategorieId != null) {
	        sql.append(
	                " AND ( " +
	                "   (cd.PLAT_ID IS NOT NULL AND EXISTS (" +
	                "       SELECT 1 FROM PLAT p2 " +
	                "       WHERE p2.ID = cd.PLAT_ID AND p2.SOUS_CATEGORIE_ID = ?" +
	                "   )) " +
	                "   OR (cd.PRODUIT_ID IS NOT NULL AND EXISTS (" +
	                "       SELECT 1 FROM PRODUITS pr " +
	                "       LEFT JOIN PLAT pl ON pr.ID = pl.PRODUIT_ID " +
	                "       WHERE pr.ID = cd.PRODUIT_ID AND pl.SOUS_CATEGORIE_ID = ?" +
	                "   )) " +
	                ") "
	        );
	        params.add(sousCategorieId);
	        params.add(sousCategorieId);
	    }

	    // Filtres date
	    if (dateDebut != null) {
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
	    }

	    if (dateFin != null) {
	        sql.append(" AND c.DATE_COMMANDE <= ? ");
	        params.add(new java.sql.Timestamp(dateFin.getTime()));
	    }

	    // Groupement amélioré
	    sql.append(" GROUP BY "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN CONCAT('PLAT_', cd.PLAT_ID) "
	            + "       ELSE CONCAT('PRODUIT_', cd.PRODUIT_ID) "
	            + "   END, "
	            + "   COALESCE(p.NOM, prod.NOM) "
	            + "ORDER BY total_quantite DESC");

	    System.out.println("SQL généré: " + sql.toString());
	    System.out.println("Paramètres: " + params);

	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < params.size(); i++) {
	            Object param = params.get(i);
	            if (param instanceof Integer) {
	                stmt.setInt(i + 1, (Integer) param);
	            } else if (param instanceof Timestamp) {
	                stmt.setTimestamp(i + 1, (Timestamp) param);
	            } else {
	                stmt.setObject(i + 1, param);
	            }
	        }

	        System.out.println("Exécution de la requête...");

	        try (ResultSet rs = stmt.executeQuery()) {
	            int count = 0;
	            int totalQuantite = 0;
	            int totalMontant = 0;
	            int totalCommandes = 0;

	            while (rs.next()) {
	                count++;
	                RapportCommande rapport = new RapportCommande();

	                String itemType = rs.getString("item_type");
	                int itemId = rs.getInt("item_id");
	                
	                if ("PLAT".equals(itemType)) {
	                    rapport.setPlatId(itemId);
	                    rapport.setProduitId(null);
	                } else {
	                    rapport.setPlatId(null);
	                    rapport.setProduitId(itemId);
	                }

	                rapport.setNomPlat(rs.getString("NOM"));
	                rapport.setQuantiteTotale(rs.getInt("total_quantite"));
	                rapport.setMontantTotal(rs.getInt("total_montant"));
	                rapport.setPrixUnitaire(rs.getInt("prix_unitaire"));
	                rapport.setDateVente(rs.getTimestamp("date_vente"));
	                
	                // Stocker aussi le CASHED_BY pour référence
	                Integer cashedBy = rs.getObject("CASHED_BY") != null ? rs.getInt("CASHED_BY") : null;
//	                rapport.setCashedBy(cashedBy);
	                
	                totalQuantite += rs.getInt("total_quantite");
	                totalMontant += rs.getInt("total_montant");
	                totalCommandes += rs.getInt("nombre_commandes");

	                rapports.add(rapport);
	                
	                System.out.println("Trouvé: " + rapport.getNomPlat() 
	                    + " - Quantité: " + rapport.getQuantiteTotale()
	                    + " - Caissier: " + (cashedBy != null ? cashedBy : "Non encaissé"));
	            }

	            System.out.println("Résumé: " + count + " lignes, " 
	                + totalQuantite + " unités, "
	                + totalMontant + " montant total, "
	                + totalCommandes + " commandes distinctes");
	        }

	    } catch (SQLException e) {
	        System.out.println("Erreur SQL: " + e.getMessage());
	        e.printStackTrace();
	    }

	    System.out.println("Nombre de rapports retournés: " + rapports.size());
	    return rapports;
	}

//    BON LAST 14
	public List<Commande> getCaissiereCommandesCashed(Timestamp dateDebut, Timestamp dateFin, Integer categorieId,
			Integer sousCategorieId) {

		System.out.println("=== getCaissiereCommandesCashed START ===");
		System.out.println("Params: dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", categorieId=" + categorieId
				+ ", sousCategorieId=" + sousCategorieId);

		List<Commande> commandes = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT c.ID, c.NUMERO_COMMANDE, c.DATE_COMMANDE, c.MODE_PAIEMENT, "
			    + "       c.STATUT, c.STATUT_COMMANDE, c.MONTANT_PAYE, c.STATUT_PAIEMENT, "
			    + "       c.CREATED_AT, c.UPDATED_AT, c.UTILISATEUR_ID, c.CLIENT_ID, c.CASHED_BY, c.NOTES, "
			    + "       SUM(cd.PRIX_UNITAIRE * cd.QUANTITE) AS MONTANT_TOTAL, "
			    + "       client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, "
			    + "       role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME, "
			    + "       staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, "
			    + "       role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME, "
			    + "       COALESCE(caissier.ID, -1) AS caissier_ID, "
			    + "       COALESCE(caissier.NOM, 'Système') AS caissier_NOM, "
			    + "       COALESCE(caissier.PRENOM, '') AS caissier_PRENOM, "
			    + "       COALESCE(role_caissier.ID, -1) AS caissier_ROLE_ID, "
			    + "       COALESCE(role_caissier.NOM_ROLE, 'SYSTEM') AS caissier_ROLE_NAME "
			    + "FROM COMMANDE c "
			    + "LEFT JOIN UTILISATEUR caissier ON c.CASHED_BY = caissier.ID "
			    + "LEFT JOIN ROLE role_caissier ON caissier.ID_ROLE = role_caissier.ID "
			    + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
			    + "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID "
			    + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
			    + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
			    + "JOIN COMMANDE_DETAIL cd ON cd.COMMANDE_ID = c.ID "
			    + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
			    + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
			    + "WHERE c.DATE_COMMANDE BETWEEN ? AND ? "
			    + "AND c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' "
			    + "AND cd.STATUT = 'VISIBLE' ");

		List<Object> params = new ArrayList<>();
		params.add(dateDebut);
		params.add(dateFin);

		if (categorieId != null) {
			sql.append(" AND ( "
					+ "  (cd.PLAT_ID IS NOT NULL AND (p.CATEGORIE_ID = ? OR p.CATEGORIE_ID IN (SELECT ID FROM MENU_CATEGORIE WHERE ID = ? AND PARENT_ID IS NULL))) "
					+ "  OR "
					+ "  (cd.PRODUIT_ID IS NOT NULL AND (p_produit.CATEGORIE_ID = ? OR p_produit.CATEGORIE_ID IN (SELECT ID FROM CATEGORIE WHERE ID = ? AND PARENT_ID IS NULL))) "
					+ ") ");
			params.add(categorieId);
			params.add(categorieId);
			params.add(categorieId);
			params.add(categorieId);
		}

		if (sousCategorieId != null) {
			sql.append(" AND ( " + "  (cd.PLAT_ID IS NOT NULL AND p.SOUS_CATEGORIE_ID = ?) "
					+ "  OR (cd.PRODUIT_ID IS NOT NULL AND p_produit.SOUS_CATEGORIE_ID = ?) " + ") ");
			params.add(sousCategorieId);
			params.add(sousCategorieId);
		}

		sql.append(" GROUP BY c.ID, " +
		           "client.ID, client.NOM, client.PRENOM, client_ROLE_ID, client_ROLE_NAME, " +
		           "staff.ID, staff.NOM, staff.PRENOM, staff_ROLE_ID, staff_ROLE_NAME, " +
		           "caissier.ID, caissier_NOM, caissier_PRENOM, caissier_ROLE_ID, caissier_ROLE_NAME ");

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof Integer) {
					ps.setInt(i + 1, (Integer) param);
				} else if (param instanceof Timestamp) {
					ps.setTimestamp(i + 1, (Timestamp) param);
				} else {
					ps.setObject(i + 1, param);
				}
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Commande commande = mapCommandeWithUsers(rs);
					commandes.add(commande);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		BigDecimal totalCalculé = BigDecimal.ZERO;
		for (Commande c : commandes) {
			totalCalculé = totalCalculé.add(c.getMontantTotal() != null ? c.getMontantTotal() : BigDecimal.ZERO);
			System.out.println("Commande ID: " + c.getId() + ", Numéro: " + c.getNumeroCommande() + ", Caissier: "
					+ (c.getCaissier() != null ? c.getCaissier().getNom() : "Système") + ", Montant: "
					+ c.getMontantTotal());
		}
		System.out.println("Total calculé: " + totalCalculé);
		System.out.println("Nombre de commandes: " + commandes.size());
		System.out.println("=== getCaissiereCommandesCashed END ===");

		return commandes;
	}

	public List<Commande> getUserCommandesCachedByCaissiere(int caissiereId, Timestamp startDate, Timestamp endDate,
	        Integer categorieId, Integer sousCategorieId) {

	    System.out.println("=== getUserCommandesCachedByCaissiere START ===");
	    System.out.println("Params: caissiereId=" + caissiereId + ", startDate=" + startDate + ", endDate=" + endDate
	            + ", categorieId=" + categorieId + ", sousCategorieId=" + sousCategorieId);

	    List<Commande> commandes = new ArrayList<>();

	    // SOLUTION : Calculer le montant total d'abord dans une sous-requête
	    StringBuilder sql = new StringBuilder("SELECT c.ID, c.NUMERO_COMMANDE, c.DATE_COMMANDE, c.MODE_PAIEMENT, "
	            + "       c.STATUT, c.STATUT_COMMANDE, c.MONTANT_PAYE, c.STATUT_PAIEMENT, "
	            + "       c.CREATED_AT, c.UPDATED_AT, c.UTILISATEUR_ID, c.CLIENT_ID, c.CASHED_BY, c.NOTES, "
	            + "       cmd_totals.MONTANT_TOTAL, " + // Utiliser le montant pré-calculé
	            // Client
	            "       client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, "
	            + "       role_client.ID AS client_ROLE_ID, role_client.NOM_ROLE AS client_ROLE_NAME, " +
	            // Staff
	            "       staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, "
	            + "       role_staff.ID AS staff_ROLE_ID, role_staff.NOM_ROLE AS staff_ROLE_NAME, " +
	            // Caissier
	            "       COALESCE(caissier.ID, -1) AS caissier_ID, "
	            + "       COALESCE(caissier.NOM, 'Système') AS caissier_NOM, "
	            + "       COALESCE(caissier.PRENOM, '') AS caissier_PRENOM, "
	            + "       COALESCE(role_caissier.ID, -1) AS caissier_ROLE_ID, "
	            + "       COALESCE(role_caissier.NOM_ROLE, 'SYSTEM') AS caissier_ROLE_NAME "
	            + "FROM COMMANDE c "
	            + "LEFT JOIN UTILISATEUR caissier ON c.CASHED_BY = caissier.ID "
	            + "LEFT JOIN ROLE role_caissier ON caissier.ID_ROLE = role_caissier.ID "
	            + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
	            + "LEFT JOIN ROLE role_client ON client.ID_ROLE = role_client.ID "
	            + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
	            + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
	            + "JOIN ("
	            + "    SELECT COMMANDE_ID, SUM(PRIX_UNITAIRE * QUANTITE) AS MONTANT_TOTAL "
	            + "    FROM COMMANDE_DETAIL "
	            + "    WHERE STATUT = 'VISIBLE' "
	            + "    GROUP BY COMMANDE_ID"
	            + ") cmd_totals ON c.ID = cmd_totals.COMMANDE_ID "
	            + "WHERE 1=1 ");

	    List<Object> params = new ArrayList<>();

	    // Condition différente selon si on veut le système ou un caissier spécifique
	    if (caissiereId == -1) {
	        // Cas spécial: on veut uniquement les commandes sans caissier (système)
	        sql.append(" AND c.CASHED_BY IS NULL ");
	    } else {
	        // Cas normal: on veut les commandes d'un caissier spécifique
	        sql.append(" AND c.CASHED_BY = ? ");
	        params.add(caissiereId);
	    }

	    sql.append(" AND c.DATE_COMMANDE BETWEEN ? AND ? ");
	    sql.append(" AND c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' ");
	    // NOTE: On a déjà filtré cd.STATUT = 'VISIBLE' dans la sous-requête cmd_totals

	    params.add(startDate);
	    params.add(endDate);

	    // Pour les filtres par catégorie et sous-catégorie, nous devons rejoindre COMMANDE_DETAIL
	    if (categorieId != null || sousCategorieId != null) {
	        sql.append(" AND EXISTS (SELECT 1 FROM COMMANDE_DETAIL cd2 ");
	        sql.append(" LEFT JOIN PLAT p2 ON cd2.PLAT_ID = p2.ID ");
	        sql.append(" LEFT JOIN PRODUITS prod2 ON cd2.PRODUIT_ID = prod2.ID ");
	        sql.append(" LEFT JOIN PLAT p_produit2 ON prod2.ID = p_produit2.PRODUIT_ID ");
	        sql.append(" WHERE cd2.COMMANDE_ID = c.ID AND cd2.STATUT = 'VISIBLE' ");
	        
	        if (categorieId != null) {
	            sql.append(" AND ( "
	                    + "  (cd2.PLAT_ID IS NOT NULL AND (p2.CATEGORIE_ID = ? OR p2.CATEGORIE_ID IN (SELECT ID FROM MENU_CATEGORIE WHERE ID = ? AND PARENT_ID IS NULL))) "
	                    + "  OR "
	                    + "  (cd2.PRODUIT_ID IS NOT NULL AND (p_produit2.CATEGORIE_ID = ? OR p_produit2.CATEGORIE_ID IN (SELECT ID FROM MENU_CATEGORIE WHERE ID = ? AND PARENT_ID IS NULL))) "
	                    + ") ");
	            params.add(categorieId);
	            params.add(categorieId);
	            params.add(categorieId);
	            params.add(categorieId);
	        }
	        
	        if (sousCategorieId != null) {
	            sql.append(" AND ( " + "  (cd2.PLAT_ID IS NOT NULL AND p2.SOUS_CATEGORIE_ID = ?) "
	                    + "  OR (cd2.PRODUIT_ID IS NOT NULL AND p_produit2.SOUS_CATEGORIE_ID = ?) " + ") ");
	            params.add(sousCategorieId);
	            params.add(sousCategorieId);
	        }
	        
	        sql.append(") ");
	    }
	    
	    sql.append(" GROUP BY "
	            + "c.ID, c.NUMERO_COMMANDE, c.DATE_COMMANDE, c.MODE_PAIEMENT, "
	            + "c.STATUT, c.STATUT_COMMANDE, c.MONTANT_PAYE, c.STATUT_PAIEMENT, "
	            + "c.CREATED_AT, c.UPDATED_AT, c.UTILISATEUR_ID, c.CLIENT_ID, c.CASHED_BY, c.NOTES, "
	            + "cmd_totals.MONTANT_TOTAL, "
	            // Client
	            + "client.ID, client.NOM, client.PRENOM, "
	            + "role_client.ID, role_client.NOM_ROLE, "
	            // Staff
	            + "staff.ID, staff.NOM, staff.PRENOM, "
	            + "role_staff.ID, role_staff.NOM_ROLE, "
	            // Caissier
	            + "caissier.ID, caissier.NOM, caissier.PRENOM, "
	            + "role_caissier.ID, role_caissier.NOM_ROLE "
	    );

	    sql.append(" ORDER BY c.CREATED_AT DESC");

	    try (Connection conn = DBConnection.getConnection();
	            PreparedStatement ps = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < params.size(); i++) {
	            Object param = params.get(i);
	            if (param instanceof Integer) {
	                ps.setInt(i + 1, (Integer) param);
	            } else if (param instanceof Timestamp) {
	                ps.setTimestamp(i + 1, (Timestamp) param);
	            } else {
	                ps.setObject(i + 1, param);
	            }
	        }

	        System.out.println("SQL exécuté : " + sql.toString()); // Pour déboguer
	        System.out.println("Paramètres : " + params); // Pour déboguer

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Commande commande = mapCommandeWithUsers(rs);
	                commandes.add(commande);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    BigDecimal totalCalculé = BigDecimal.ZERO;
	    for (Commande c : commandes) {
	        totalCalculé = totalCalculé.add(c.getMontantTotal() != null ? c.getMontantTotal() : BigDecimal.ZERO);
	        System.out.println("Commande ID: " + c.getId() + ", Numéro: " + c.getNumeroCommande() + ", Caissier: "
	                + (c.getCaissier() != null ? c.getCaissier().getNom() : "Système") + ", Montant recalculé: "
	                + c.getMontantTotal());
	    }
	    System.out.println("Total calculé: " + totalCalculé);
	    System.out.println("Nombre de commandes: " + commandes.size());
	    System.out.println("=== getUserCommandesCachedByCaissiere END ===");

	    return commandes;
	}

	public Map<Utilisateur, List<Commande>> getAllCommandesGroupedByCaissiere(Date dateDebut, Date dateFin,
			Integer categorieId, Integer sousCategorieId) {

		System.out.println("=== getAllCommandesGroupedByCaissiere START ===");

		StringBuilder sql = new StringBuilder("SELECT c.ID, c.NUMERO_COMMANDE, c.DATE_COMMANDE, c.MODE_PAIEMENT, "
				+ "       c.STATUT, c.STATUT_COMMANDE, c.MONTANT_PAYE, c.STATUT_PAIEMENT, c.CREATED_AT, c.UPDATED_AT, "
				+ "       c.UTILISATEUR_ID, c.CLIENT_ID, c.CASHED_BY, c.NOTES, "
				+ "       SUM(cd.PRIX_UNITAIRE * cd.QUANTITE) AS MONTANT_TOTAL, " +
				// Caissier avec valeur par défaut pour les commandes sans caissier
				"       COALESCE(caissier.ID, -1) AS caissier_ID, "
				+ "       COALESCE(caissier.NOM, 'Système') AS caissier_nom, "
				+ "       COALESCE(caissier.PRENOM, '') AS caissier_prenom, "
				+ "       COALESCE(role_caissier.ID, -1) AS caissier_ROLE_ID, "
				+ "       COALESCE(role_caissier.NOM_ROLE, 'SYSTEM') AS caissier_ROLE_NAME " + "FROM COMMANDE c "
				+ "LEFT JOIN UTILISATEUR caissier ON c.CASHED_BY = caissier.ID " + // LEFT JOIN
				"LEFT JOIN ROLE role_caissier ON caissier.ID_ROLE = role_caissier.ID " + // LEFT JOIN
				"LEFT JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID " + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
				+ "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
				+ "LEFT JOIN PLAT p_produit ON prod.ID = p_produit.PRODUIT_ID "
				+ "WHERE (role_caissier.NOM_ROLE = 'CAISSIER(ERE)' OR c.CASHED_BY IS NULL) " + // Inclure les commandes
																								// sans caissier
				"AND c.DATE_COMMANDE BETWEEN ? AND ? " + "AND c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' ");

		List<Object> params = new ArrayList<>();
		params.add(new java.sql.Timestamp(dateDebut.getTime()));
		params.add(new java.sql.Timestamp(dateFin.getTime()));

		// Filtre catégorie
		if (categorieId != null) {
			sql.append(" AND ( " + "  (cd.PLAT_ID IS NOT NULL AND p.CATEGORIE_ID = ?) OR "
					+ "  (cd.PRODUIT_ID IS NOT NULL AND p_produit.CATEGORIE_ID = ?) " + ") ");
			params.add(categorieId);
			params.add(categorieId);
		}

		// Filtre sous-catégorie
		if (sousCategorieId != null) {
			sql.append(" AND ( " + "  (cd.PLAT_ID IS NOT NULL AND p.SOUS_CATEGORIE_ID = ?) OR "
					+ "  (cd.PRODUIT_ID IS NOT NULL AND p_produit.SOUS_CATEGORIE_ID = ?) " + ") ");
			params.add(sousCategorieId);
			params.add(sousCategorieId);
		}

		sql.append(" GROUP BY c.ID, caissier.ID, caissier_nom, caissier_prenom, caissier_ROLE_ID, caissier_ROLE_NAME ");
		sql.append(" ORDER BY caissier.ID, c.CREATED_AT DESC");

		Map<Utilisateur, List<Commande>> grouped = new HashMap<>();

		// Créer l'utilisateur "Système" pour les commandes sans caissier
		Utilisateur systemUser = new Utilisateur();
		systemUser.setId(-1);
		systemUser.setNom("Système");
		systemUser.setPrenom("");
		Role systemRole = new Role();
		systemRole.setId(-1);
		systemRole.setRoleName("SYSTEM");
		systemUser.setRole(systemRole);

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof Timestamp) {
					stmt.setTimestamp(i + 1, (Timestamp) param);
				} else {
					stmt.setObject(i + 1, param);
				}
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Commande commande = mapCommandeWithUsers(rs);

					Utilisateur caissier;
					int caissierId = rs.getInt("caissier_ID");

					if (caissierId == -1) {
						// Utiliser l'utilisateur système
						caissier = systemUser;
					} else {
						caissier = new Utilisateur();
						caissier.setId(caissierId);
						caissier.setNom(rs.getString("caissier_nom"));
						caissier.setPrenom(rs.getString("caissier_prenom"));
						Role role = new Role();
						role.setId(rs.getInt("caissier_ROLE_ID"));
						role.setRoleName(rs.getString("caissier_ROLE_NAME"));
						caissier.setRole(role);
					}

					grouped.computeIfAbsent(caissier, k -> new ArrayList<>()).add(commande);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// S'assurer que l'utilisateur système est toujours présent
		grouped.putIfAbsent(systemUser, new ArrayList<>());

		System.out.println("Nombre de caissiers: " + grouped.size());
		for (Utilisateur u : grouped.keySet()) {
			System.out.println(
					"Caissier: " + u.getNom() + " " + u.getPrenom() + " - Commandes: " + grouped.get(u).size());
		}
		System.out.println("=== getAllCommandesGroupedByCaissiere END ===");

		return grouped;
	}

	private List<Commande> executeCommandeWithUsers(String sql, List<Object> params) {
		List<Commande> commandes = new ArrayList<>();
		System.out.println("===== executeCommandeWithUsers START =====");
		System.out.println("SQL = " + sql);
		System.out.println("Params = " + params);

		BigDecimal totalMontant = BigDecimal.ZERO;
		int count = 0;

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof Date) {
					stmt.setTimestamp(i + 1, new java.sql.Timestamp(((Date) param).getTime()));
					System.out.println("Param " + (i + 1) + " (Date) = " + param);
				} else {
					stmt.setObject(i + 1, param);
					System.out.println("Param " + (i + 1) + " = " + param);
				}
			}

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("ResultSet exécuté, avant boucle");
				while (rs.next()) {
					count++;
					int commandeId = rs.getInt("ID");
					String numero = rs.getString("NUMERO_COMMANDE");
					BigDecimal montant = rs.getBigDecimal("MONTANT_TOTAL");

					System.out.println("ResultSet.next() trouvé, ID = " + commandeId);
					System.out.println(
							"Données brutes - ID: " + commandeId + ", Numéro: " + numero + ", Montant: " + montant);

					Commande commande = mapCommandeWithUsers(rs);
					System.out.println(
							"Commande mappée : " + commande.getId() + ", Montant mappé: " + commande.getMontantTotal());

					totalMontant = totalMontant.add(montant != null ? montant : BigDecimal.ZERO);
					commandes.add(commande);
				}
			}
		} catch (SQLException e) {
			System.out.println("Erreur SQL dans executeCommandeWithUsers: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Total commandes récupérées = " + commandes.size());
		System.out.println("Total lignes ResultSet = " + count);
		System.out.println("Total montant brut = " + totalMontant);
		System.out.println("===== executeCommandeWithUsers END =====");
		return commandes;
	}

	private Commande mapCommandeBase(ResultSet rs) throws SQLException {
		Commande commande = new Commande();
		commande.setId(rs.getInt("ID"));
		commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
		commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
		commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
		commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
		commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
		commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
		commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
		commande.setNotes(rs.getString("NOTES"));
		commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
		commande.setStatut(rs.getString("STATUT"));
		commande.setClientId(rs.getInt("CLIENT_ID"));
		// commande.setIsCredit(rs.getBoolean("IS_CREDIT"));
		return commande;
	}

	private Commande mapCommandeWithUsers(ResultSet rs) throws SQLException {
		System.out.println("=== mapCommandeWithUsers START ===");
		Commande commande = mapCommandeBase(rs);
		System.out.println("Commande base ID = " + commande.getId() + ", Montant base: " + commande.getMontantTotal());

		// Client
		int clientId = rs.getInt("client_ID");
		if (!rs.wasNull()) {
			System.out.println("Mapping client ID = " + clientId);
			Utilisateur client = new Utilisateur();
			client.setId(clientId);
			client.setNom(rs.getString("client_NOM"));
			client.setPrenom(rs.getString("client_PRENOM"));
			Role roleClient = new Role();
			roleClient.setId(rs.getInt("client_ROLE_ID"));
			roleClient.setRoleName(rs.getString("client_ROLE_NAME"));
			client.setRole(roleClient);
			commande.setClient(client);
			System.out.println("Client mappé: " + client.getNom() + " " + client.getPrenom());
		} else {
			System.out.println("Client NULL");
		}

		// Staff
		int staffId = rs.getInt("staff_ID");
		if (!rs.wasNull()) {
			System.out.println("Mapping staff ID = " + staffId);
			Utilisateur staff = new Utilisateur();
			staff.setId(staffId);
			staff.setNom(rs.getString("staff_NOM"));
			staff.setPrenom(rs.getString("staff_PRENOM"));
			Role roleStaff = new Role();
			roleStaff.setId(rs.getInt("staff_ROLE_ID"));
			roleStaff.setRoleName(rs.getString("staff_ROLE_NAME"));
			staff.setRole(roleStaff);
			commande.setUtilisateur(staff);
			System.out.println("Staff mappé: " + staff.getNom() + " " + staff.getPrenom());
		} else {
			System.out.println("Staff NULL");
		}

		// Caissier
		int caissierId = rs.getInt("caissier_ID");
		if (!rs.wasNull()) {
			System.out.println("Mapping caissier ID = " + caissierId);
			Utilisateur caissier = new Utilisateur();
			caissier.setId(caissierId);
			caissier.setNom(rs.getString("caissier_NOM"));
			caissier.setPrenom(rs.getString("caissier_PRENOM"));
			Role roleCaissier = new Role();
			roleCaissier.setId(rs.getInt("caissier_ROLE_ID"));
			roleCaissier.setRoleName(rs.getString("caissier_ROLE_NAME"));
			caissier.setRole(roleCaissier);
			commande.setCaissier(caissier);
			System.out.println("Caissier mappé: " + caissier.getNom() + " " + caissier.getPrenom());
		} else {
			System.out.println("Caissier NULL");
		}

		System.out.println("=== mapCommandeWithUsers END === ID = " + commande.getId() + ", Montant final: "
				+ commande.getMontantTotal());

		return commande;
	}

	public void verifyTotalFromDatabase(Date dateDebut, Date dateFin, Integer categorieId, Integer sousCategorieId) {
		System.out.println("=== VERIFICATION DIRECTE EN BASE ===");

		String sql = "SELECT SUM(c.MONTANT_TOTAL) as total FROM COMMANDE c "
				+ "JOIN UTILISATEUR caissier ON c.CASHED_BY = caissier.ID "
				+ "JOIN ROLE role_caissier ON caissier.ID_ROLE = role_caissier.ID "
				+ "WHERE role_caissier.NOM_ROLE = 'CAISSIER(ERE)' " + "AND c.DATE_COMMANDE BETWEEN ? AND ? "
				+ "AND c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE'";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setTimestamp(1, new java.sql.Timestamp(dateDebut.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(dateFin.getTime()));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					BigDecimal totalBase = rs.getBigDecimal("total");
					System.out.println("TOTAL DIRECT EN BASE (sans filtre catégorie): " + totalBase);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("=== FIN VERIFICATION ===");
	}


	public BilanUtilisateur getBilanByStaffId(int userId, Timestamp dateDebut, Timestamp dateFin) {
		BilanUtilisateur bilan = new BilanUtilisateur();
		bilan.setQteParStatut(new HashMap<>());
		bilan.setMontantParStatut(new HashMap<>());

		bilan.setDateDebut(dateDebut);
		bilan.setDateFin(dateFin);

		bilan.setPourcentage(null);
		bilan.setMontantPourcentage(null);

		// SQL : somme réelle par statut, on ne filtre pas les annulées ici
		String sql = "SELECT STATUT_COMMANDE, " + "       COUNT(*) AS qte, "
				+ "       SUM(MONTANT_TOTAL) AS totalMontant " + "FROM COMMANDE " + "WHERE UTILISATEUR_ID = ? "
				+ "  AND DATE_COMMANDE BETWEEN ? AND ? " + "  AND STATUT = 'VISIBLE' " + "GROUP BY STATUT_COMMANDE";

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			stmt.setTimestamp(2, dateDebut);
			stmt.setTimestamp(3, dateFin);

			try (ResultSet rs = stmt.executeQuery()) {
				int totalCommandes = 0;
				BigDecimal totalMontantGlobal = BigDecimal.ZERO;

				while (rs.next()) {
					String statut = rs.getString("STATUT_COMMANDE");
					int qte = rs.getInt("qte");
					BigDecimal montant = rs.getBigDecimal("totalMontant");
					if (montant == null)
						montant = BigDecimal.ZERO;

					// Sauvegarde par statut
					bilan.getQteParStatut().put(statut, qte);
					bilan.getMontantParStatut().put(statut, montant);

					// Total général : on ignore le statut "ANNULE"
					if (!"ANNULE".equalsIgnoreCase(statut)) {
						totalMontantGlobal = totalMontantGlobal.add(montant);
					}

					totalCommandes += qte; // toutes les commandes visibles
				}

				bilan.setTotalCommandes(totalCommandes);
				bilan.setTotalMontant(totalMontantGlobal); // total global hors annulées
				bilan.setPourcentage(BigDecimal.ZERO);
				bilan.setMontantPourcentage(BigDecimal.ZERO);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("❌ Erreur SQL dans getBilanByStaffId : " + e.getMessage());
		}

		return bilan;
	}
	
	public BilanUtilisateur getBilanByStaffId_v2(int userId, Timestamp dateDebut, Timestamp dateFin) {
	    BilanUtilisateur bilan = new BilanUtilisateur();
	    bilan.setQteParStatut(new HashMap<>());
	    bilan.setMontantParStatut(new HashMap<>());
	    
	    bilan.setDateDebut(dateDebut);
	    bilan.setDateFin(dateFin);
	    bilan.setPourcentage(null);
	    bilan.setMontantPourcentage(null);

	    // Récupérer le type d'utilisateur
	    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
	    Utilisateur user = utilisateurDAO.findById(userId);
	    
	    if (user == null) {
	        System.out.println("❌ Utilisateur non trouvé avec ID: " + userId);
	        return bilan;
	    }
	    
	    // Construire la condition WHERE selon le rôle
	    String whereCondition;
	    String paramName = "";
	    
	    if (user.getRole() != null) {
	        String roleName = user.getRole().getRoleName().toUpperCase();
	        
	        switch(roleName) {
	            case "CAISSIER(ERE)":
	            case "CAISSIERE":
	                whereCondition = "CASHED_BY = ?";
	                paramName = "CASHED_BY";
	                System.out.println("💰 Mode CAISSIER");
	                break;
	                
	            default:
	                whereCondition = "UTILISATEUR_ID = ?";
	                paramName = "UTILISATEUR_ID";
	                System.out.println("👨‍🍳 Mode SERVEUR/STAFF");
	                break;
	        }
	    } else {
	        whereCondition = "UTILISATEUR_ID = ?";
	        paramName = "UTILISATEUR_ID";
	    }

	    // **REQUÊTE UNIQUE avec CASE pour SOLDE**
	    String sql = "SELECT " 
	               + "  STATUT_COMMANDE, " 
	               + "  COUNT(*) AS qte, "
	               + "  SUM(MONTANT_TOTAL) AS totalMontant, "
	               + "  SUM(CASE WHEN MODE_PAIEMENT = 'SOLDE' THEN 1 ELSE 0 END) AS qte_solde, "
	               + "  SUM(CASE WHEN MODE_PAIEMENT = 'SOLDE' THEN MONTANT_TOTAL ELSE 0 END) AS montant_solde "
	               + "FROM COMMANDE " 
	               + "WHERE " + whereCondition
	               + "  AND DATE_COMMANDE BETWEEN ? AND ? " 
	               + "  AND STATUT = 'VISIBLE' " 
	               + "GROUP BY STATUT_COMMANDE";

	    try (Connection conn = getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        int paramIndex = 1;
	        
	        if (!"TOUS".equals(paramName)) {
	            stmt.setInt(paramIndex++, userId);
	        }
	        
	        stmt.setTimestamp(paramIndex++, dateDebut);
	        stmt.setTimestamp(paramIndex, dateFin);

	        System.out.println("📊 Exécution requête bilan avec param: " + paramName + " = " + userId);

	        int totalQteSolde = 0;
	        BigDecimal totalMontantSolde = BigDecimal.ZERO;
	        int totalCommandes = 0;
	        BigDecimal totalMontantGlobal = BigDecimal.ZERO;

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String statut = rs.getString("STATUT_COMMANDE");
	                int qte = rs.getInt("qte");
	                BigDecimal montant = rs.getBigDecimal("totalMontant");
	                if (montant == null)
	                    montant = BigDecimal.ZERO;

	                // Récupérer les valeurs SOLDE pour cette ligne
	                int qteSolde = rs.getInt("qte_solde");
	                BigDecimal montantSolde = rs.getBigDecimal("montant_solde");
	                if (montantSolde == null)
	                    montantSolde = BigDecimal.ZERO;

	                // Sauvegarde par statut
	                bilan.getQteParStatut().put(statut, qte);
	                bilan.getMontantParStatut().put(statut, montant);

	                // Accumuler les totaux SOLDE
	                totalQteSolde += qteSolde;
	                totalMontantSolde = totalMontantSolde.add(montantSolde);

	                // Total général : on ignore le statut "ANNULE"
	                if (!"ANNULE".equalsIgnoreCase(statut)) {
	                    totalMontantGlobal = totalMontantGlobal.add(montant);
	                }

	                totalCommandes += qte; // toutes les commandes visibles
	            }

	            // Mettre à jour le bilan
	            bilan.setTotalCommandes(totalCommandes);
	            bilan.setTotalMontant(totalMontantGlobal);
	            bilan.setTotalCompteClient(totalMontantSolde);
	            bilan.setPourcentage(BigDecimal.ZERO);
	            bilan.setMontantPourcentage(BigDecimal.ZERO);
	            
	            System.out.println("✅ Bilan complet trouvé:");
	            System.out.println("   - Total commandes: " + totalCommandes);
	            System.out.println("   - Montant total: " + totalMontantGlobal + " HTG");
	            System.out.println("   - Commandes SOLDE: " + totalQteSolde);
	            System.out.println("   - Montant SOLDE: " + totalMontantSolde + " HTG");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("❌ Erreur SQL dans getBilanByStaffId : " + e.getMessage());
	    }

	    return bilan;
	}
	
	public List<Commande> getAllCommandesByStaffMember(Integer userId, Timestamp dateDebut, Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();

		try (Connection conn = getConnection()) {

			// 1️⃣ Récupérer le rôle de l'utilisateur courant
			String roleSql = "SELECT r.NOM_ROLE FROM UTILISATEUR u " + "JOIN ROLE r ON u.ID_ROLE = r.ID "
					+ "WHERE u.ID = ? AND u.STATUT = 'VISIBLE' AND r.STATUT = 'VISIBLE'";
			String roleName = null;
			try (PreparedStatement roleStmt = conn.prepareStatement(roleSql)) {
				roleStmt.setInt(1, userId);
				try (ResultSet rsRole = roleStmt.executeQuery()) {
					if (rsRole.next()) {
						roleName = rsRole.getString("NOM_ROLE");
						System.out.println("Role de l'utilisateur " + userId + " = " + roleName);
					}
				}
			}

			// 2️⃣ Construire la requête principale
			StringBuilder sql = new StringBuilder("SELECT " + "c.*, "
					+ "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN, client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT, client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE, "
					+ "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, staff.ID_ROLE AS staff_ROLE_ID, "
					+ "c.TABLE_ID, "
					+ "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
					+ "FROM COMMANDE c " + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
					+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
					+ "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID "
					+ "WHERE c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' ");

			boolean isCaissier = "CAISSIER(ERE)".equalsIgnoreCase(roleName);
			if (isCaissier) {
				sql.append(
						"AND (staff.ID_ROLE IN (SELECT ID FROM ROLE WHERE NOM_ROLE IN ('VENDEUR(EUSE)', 'CLIENT')) OR c.UTILISATEUR_ID = ?)");
			} else {
				sql.append("AND c.UTILISATEUR_ID = ?");
			}

			if (dateDebut != null)
				sql.append(" AND c.DATE_COMMANDE >= ?");
			if (dateFin != null)
				sql.append(" AND c.DATE_COMMANDE <= ?");
			if (dateDebut == null && dateFin == null)
				sql.append(" AND DATE(c.DATE_COMMANDE) = CURDATE()");

			sql.append(" ORDER BY c.CREATED_AT DESC");

			try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
				int paramIndex = 1;
				stmt.setInt(paramIndex++, userId);
				if (dateDebut != null)
					stmt.setTimestamp(paramIndex++, dateDebut);
				if (dateFin != null)
					stmt.setTimestamp(paramIndex++, dateFin);

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Commande commande = new Commande();
						commande.setId(rs.getInt("ID"));
						commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
						commande.setClientId(rs.getInt("CLIENT_ID"));
						commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
						commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
						commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
						commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
						commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
						commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
						commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
						commande.setNotes(rs.getString("NOTES"));
						commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
						commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
						int deletedBy = rs.getInt("DELETED_BY");
						commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
						commande.setStatut(rs.getString("STATUT"));

						// Client
						int idClient = rs.getInt("client_ID");
						if (!rs.wasNull()) {
							Utilisateur client = new Utilisateur();
							client.setId(idClient);
							client.setNom(rs.getString("client_NOM"));
							client.setPrenom(rs.getString("client_PRENOM"));
							client.setEmail(rs.getString("client_EMAIL"));
							client.setLogin(rs.getString("client_LOGIN"));
							client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
							client.setStatut(rs.getString("client_STATUT"));
							client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
							client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));
							commande.setClient(client);
						}

						// Staff
						int idStaff = rs.getInt("staff_ID");
						if (!rs.wasNull()) {
							UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
							Utilisateur staff = utilisateurDAO.findById(idStaff);
							commande.setUtilisateur(staff);
						}

						// Table
						int tableId = rs.getInt("TABLE_ID");
						if (!rs.wasNull()) {
							TableRooftop table = tableDao.chercherParId(tableId);
							commande.setTableRooftop(table);
						}

						// Crédit
						if (rs.getObject("credit_ID") != null) {
							Credit credit = new Credit();
							credit.setId(rs.getInt("credit_ID"));
							credit.setCommandeId(commande.getId());
							credit.setMontantTotal(rs.getInt("credit_TOTAL"));
							credit.setMontantPaye(rs.getInt("credit_PAYE"));
							credit.setStatut(rs.getString("credit_STATUT"));
							commande.setCredit(credit);
							commande.setIsCredit(true); // indique que c'est un crédit
						} else {
							commande.setIsCredit(false);
						}

						commandes.add(commande);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Total commandes récupérées = " + commandes.size());
		return commandes;
	}



	public List<Commande> getAllCommandesForUserId(Integer clientId, Timestamp dateDebut, Timestamp dateFin) {
		List<Commande> commandes = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT " + "c.*, "
				+ "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, "
				+ "client.EMAIL AS client_EMAIL, client.LOGIN AS client_LOGIN, "
				+ "client.MOT_DE_PASSE AS client_MOT_DE_PASSE, client.STATUT AS client_STATUT, "
				+ "client.CREATION_DATE AS client_CREATION_DATE, client.UPDATE_DATE AS client_UPDATE_DATE, "
				+ "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, "
				+ "staff.EMAIL AS staff_EMAIL, staff.LOGIN AS staff_LOGIN, "
				+ "staff.MOT_DE_PASSE AS staff_MOT_DE_PASSE, staff.STATUT AS staff_STATUT, "
				+ "staff.CREATION_DATE AS staff_CREATION_DATE, staff.UPDATE_DATE AS staff_UPDATE_DATE, "
				+ "c.TABLE_ID, "
				+ "cr.ID AS credit_ID, cr.MONTANT_TOTAL AS credit_TOTAL, cr.MONTANT_PAYE AS credit_PAYE, cr.STATUT AS credit_STATUT "
				+ "FROM COMMANDE c " + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
				+ "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
				+ "LEFT JOIN CREDIT cr ON cr.COMMANDE_ID = c.ID " + "WHERE c.STATUT = 'VISIBLE' AND c.CLIENT_ID = ? "
				+ "AND c.STATUT_COMMANDE != 'ANNULE' ");

		if (dateDebut != null) {
			sql.append(" AND c.DATE_COMMANDE >= ?");
		}
		if (dateFin != null) {
			sql.append(" AND c.DATE_COMMANDE <= ?");
		}

		if (dateDebut == null && dateFin == null) {
			sql.append(" AND DATE(c.DATE_COMMANDE) = CURDATE()");
		}

		sql.append(" ORDER BY c.CREATED_AT DESC");

		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int paramIndex = 1;
			stmt.setInt(paramIndex++, clientId);
			if (dateDebut != null)
				stmt.setTimestamp(paramIndex++, dateDebut);
			if (dateFin != null)
				stmt.setTimestamp(paramIndex++, dateFin);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Commande commande = new Commande();
					commande.setId(rs.getInt("ID"));
					commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));

					int idClient = rs.getInt("CLIENT_ID");
					commande.setClientId(rs.wasNull() ? null : idClient);
					commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
					commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
					commande.setMontantTotal(rs.getBigDecimal("MONTANT_TOTAL"));
					commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
					commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
					commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
					commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
					commande.setNotes(rs.getString("NOTES"));
					commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
					commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

					int deletedBy = rs.getInt("DELETED_BY");
					commande.setDeletedBy(rs.wasNull() ? null : deletedBy);
					commande.setStatut(rs.getString("STATUT"));

					// Client
					if (!rs.wasNull()) {
						Utilisateur client = new Utilisateur();
						client.setId(idClient);
						client.setNom(rs.getString("client_NOM"));
						client.setPrenom(rs.getString("client_PRENOM"));
						client.setEmail(rs.getString("client_EMAIL"));
						client.setLogin(rs.getString("client_LOGIN"));
						client.setMotDePasse(rs.getString("client_MOT_DE_PASSE"));
						client.setStatut(rs.getString("client_STATUT"));
						client.setCreationDate(rs.getTimestamp("client_CREATION_DATE"));
						client.setUpdateDate(rs.getTimestamp("client_UPDATE_DATE"));
						commande.setClient(client);
					}

					// Staff
					int idStaff = rs.getInt("staff_ID");
					if (!rs.wasNull()) {
						Utilisateur staff = new Utilisateur();
						staff.setId(idStaff);
						staff.setNom(rs.getString("staff_NOM"));
						staff.setPrenom(rs.getString("staff_PRENOM"));
						staff.setEmail(rs.getString("staff_EMAIL"));
						staff.setLogin(rs.getString("staff_LOGIN"));
						staff.setMotDePasse(rs.getString("staff_MOT_DE_PASSE"));
						staff.setStatut(rs.getString("staff_STATUT"));
						staff.setCreationDate(rs.getTimestamp("staff_CREATION_DATE"));
						staff.setUpdateDate(rs.getTimestamp("staff_UPDATE_DATE"));
						commande.setUtilisateur(staff);
					}

					// Table
					int tableId = rs.getInt("TABLE_ID");
					if (!rs.wasNull()) {
						TableRooftop table = tableDao.chercherParId(tableId);
						commande.setTableRooftop(table);
					}

					// Crédit
					if (rs.getObject("credit_ID") != null) {
						Credit credit = new Credit();
						credit.setId(rs.getInt("credit_ID"));
						credit.setCommandeId(commande.getId());
						credit.setMontantTotal(rs.getInt("credit_TOTAL"));
						credit.setMontantPaye(rs.getInt("credit_PAYE"));
						credit.setStatut(rs.getString("credit_STATUT"));
						commande.setCredit(credit);
						commande.setIsCredit(true); // c'est un crédit
					} else {
						commande.setIsCredit(false);
					}

					commandes.add(commande);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return commandes;
	}

	public List<Commande> getRapportCommandes(Integer userId, Timestamp dateDebut, Timestamp dateFin) {
	    List<Commande> commandes = new ArrayList<>();
	    boolean isCaissiere = false;
	    BigDecimal totalGeneral = BigDecimal.ZERO;

	    // 🔍 Vérifier rôle utilisateur
	    try (Connection conn = getConnection();
	         PreparedStatement checkStmt = conn.prepareStatement(
	             "SELECT r.NOM_ROLE FROM UTILISATEUR u JOIN ROLE r ON u.ID_ROLE = r.ID WHERE u.ID = ?")) {
	        checkStmt.setInt(1, userId);
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next()) {
	                String roleName = rs.getString("NOM_ROLE");
	                System.out.println("🎯 Rôle détecté pour userId=" + userId + " → " + roleName);
	                isCaissiere = roleName != null && roleName.toUpperCase().contains("CAISSIER");
	                System.out.println("👉 isCaissiere=" + isCaissiere);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return commandes;
	    }

	    // AJOUT : Logs pour vérifier les dates
	    System.out.println("🔍 Vérification des dates :");
	    System.out.println("  Date début reçue: " + dateDebut);
	    System.out.println("  Date fin reçue: " + dateFin);
	    
	    System.out.println("  Date début utilisée: " + dateDebut);
	    System.out.println("  Date fin utilisée: " + dateFin);

	    // 🔧 Requête SQL - version corrigée pour matcher getCaissiereCommandesCashed
	    String sql = "SELECT c.ID, c.NUMERO_COMMANDE, c.DATE_COMMANDE, c.MODE_PAIEMENT, "
	               + "c.STATUT, c.STATUT_COMMANDE, c.MONTANT_PAYE, c.STATUT_PAIEMENT, "
	               + "c.CREATED_AT, c.UPDATED_AT, c.UTILISATEUR_ID, c.CLIENT_ID, "
	               + "c.CASHED_BY, c.NOTES, c.TABLE_ID, "
	               // CORRECTION : Même calcul que getCaissiereCommandesCashed
	               + "SUM(cd.SOUS_TOTAL) AS MONTANT_TOTAL, "
	               + "client.ID AS client_ID, client.NOM AS client_NOM, client.PRENOM AS client_PRENOM, "
	               + "staff.ID AS staff_ID, staff.NOM AS staff_NOM, staff.PRENOM AS staff_PRENOM, "
	               + "caissier.ID AS caissier_ID, caissier.NOM AS caissier_NOM, caissier.PRENOM AS caissier_PRENOM "
	               + "FROM COMMANDE c "
	               + "LEFT JOIN UTILISATEUR client ON c.CLIENT_ID = client.ID "
	               + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
	               + "LEFT JOIN UTILISATEUR caissier ON c.CASHED_BY = caissier.ID "
	               // CORRECTION : Ajouter le JOIN avec COMMANDE_DETAIL comme dans l'autre méthode
	               + "JOIN COMMANDE_DETAIL cd ON cd.COMMANDE_ID = c.ID "
	               + "WHERE c.DATE_COMMANDE BETWEEN ? AND ? "
	               + "AND c.STATUT = 'VISIBLE' AND c.STATUT_COMMANDE != 'ANNULE' "
	               + "AND cd.STATUT = 'VISIBLE' ";

	    // CORRECTION : Rétablir la condition complète
	    if (isCaissiere) {
	        sql += "AND (c.CASHED_BY = ? OR c.UTILISATEUR_ID = ? OR c.CLIENT_ID = ?) ";
	    } else {
	        sql += "AND (c.UTILISATEUR_ID = ? OR c.CLIENT_ID = ?) ";
	    }

	    // CORRECTION : Ajouter GROUP BY comme dans l'autre méthode
	    sql += "GROUP BY c.ID, "
	         + "client.ID, client.NOM, client.PRENOM, "
	         + "staff.ID, staff.NOM, staff.PRENOM, "
	         + "caissier.ID, caissier.NOM, caissier.PRENOM "
	         + "ORDER BY c.DATE_COMMANDE DESC, c.CREATED_AT DESC";

	    System.out.println("📝 SQL généré : " + sql);

	    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

	        int index = 1;
	        // Dates d'abord (comme dans getCaissiereCommandesCashed)
	        stmt.setTimestamp(index++, dateDebut);
	        stmt.setTimestamp(index++, dateFin);
	        
	        if (isCaissiere) {
	            stmt.setInt(index++, userId); // CASHED_BY = userId
	            stmt.setInt(index++, userId); // UTILISATEUR_ID = userId
	            stmt.setInt(index++, userId); // CLIENT_ID = userId
	            System.out.println("🔑 Param CASHED_BY = " + userId);
	            System.out.println("🔑 Param UTILISATEUR_ID = " + userId);
	            System.out.println("🔑 Param CLIENT_ID = " + userId);
	        } else {
	            stmt.setInt(index++, userId); // UTILISATEUR_ID = userId
	            stmt.setInt(index++, userId); // CLIENT_ID = userId
	            System.out.println("🔑 Param UTILISATEUR_ID = " + userId);
	            System.out.println("🔑 Param CLIENT_ID = " + userId);
	        }
	        
	        System.out.println("📅 Param dateDebut = " + dateDebut);
	        System.out.println("📅 Param dateFin = " + dateFin);

	        try (ResultSet rs = stmt.executeQuery()) {
	            int count = 0;
	            while (rs.next()) {
	                count++;
	                Commande commande = new Commande();
	                commande.setId(rs.getInt("ID"));
	                commande.setNumeroCommande(rs.getString("NUMERO_COMMANDE"));
	                commande.setDateCommande(rs.getTimestamp("DATE_COMMANDE"));
	                commande.setStatutCommande(rs.getString("STATUT_COMMANDE"));
	                
	                // RÉCUPÉRATION DU MONTANT
	                BigDecimal montant = rs.getBigDecimal("MONTANT_TOTAL");
	                if (montant == null) {
	                    montant = BigDecimal.ZERO;
	                }
	                commande.setMontantTotal(montant);
	                
	                totalGeneral = totalGeneral.add(montant);
	                
	                commande.setModePaiement(rs.getString("MODE_PAIEMENT"));
	                commande.setStatutPaiement(rs.getString("STATUT_PAIEMENT"));
	                commande.setMontantPaye(rs.getBigDecimal("MONTANT_PAYE"));
	                commande.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
	                commande.setCashedBy(rs.getInt("CASHED_BY"));
	                commande.setNotes(rs.getString("NOTES"));
	                commande.setCreatedAt(rs.getTimestamp("CREATED_AT"));
	                commande.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

	                // Mapping du client
	                int rsClientId = rs.getInt("client_ID");
	                if (!rs.wasNull()) {
	                    Utilisateur client = new Utilisateur();
	                    client.setId(rsClientId);
	                    client.setNom(rs.getString("client_NOM"));
	                    client.setPrenom(rs.getString("client_PRENOM"));
	                    commande.setClient(client);
	                }

	                // Mapping du staff
	                int rsStaffId = rs.getInt("staff_ID");
	                if (!rs.wasNull()) {
	                    Utilisateur staff = new Utilisateur();
	                    staff.setId(rsStaffId);
	                    staff.setNom(rs.getString("staff_NOM"));
	                    staff.setPrenom(rs.getString("staff_PRENOM"));
	                    commande.setUtilisateur(staff);
	                }

	                // Mapping du caissier
	                int rsCaissierId = rs.getInt("caissier_ID");
	                if (!rs.wasNull()) {
	                    Utilisateur caissier = new Utilisateur();
	                    caissier.setId(rsCaissierId);
	                    caissier.setNom(rs.getString("caissier_NOM"));
	                    caissier.setPrenom(rs.getString("caissier_PRENOM"));
	                    commande.setCaissier(caissier);
	                }

	                commandes.add(commande);
	                
	                // Log détaillé avec l'heure complète
	                System.out.println(String.format("  %d. ID: %d | No: %s | Date: %s | Montant: %.2f | Caissier: %d | User: %d | Client: %d",
	                    count,
	                    commande.getId(),
	                    commande.getNumeroCommande(),
	                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commande.getDateCommande()),
	                    montant.doubleValue(),
	                    commande.getCashedBy(),
	                    commande.getUtilisateurId(),
	                    commande.getClient() != null ? commande.getClient().getId() : 0
	                ));
	            }
	            
	            System.out.println("✅ Total commandes trouvées: " + count);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("❌ Erreur SQL : " + e.getMessage());
	    }

	    System.out.println("\n📊 RAPPORT FINAL - Nombre de commandes: " + commandes.size());
	    System.out.println("💰 Total général: " + totalGeneral + " HTG");
	    
	    // 🔍 Comparaison avec getCaissiereCommandesCashed
	    System.out.println("\n🔍 COMPARAISON AVEC getCaissiereCommandesCashed:");
	    System.out.println("  Dates utilisées ici: " + dateDebut + " à " + dateFin);
	    System.out.println("  Commandes attendues (IDs): 7848, 7849, 7850, 7852, 7853, 7854");
	    System.out.println("  Commandes obtenues (IDs): " + 
	        commandes.stream().map(c -> String.valueOf(c.getId())).collect(Collectors.joining(", ")));
	    
	    return commandes;
	}
	
//	public List<RapportCommande> getRapportCommandesByFiltresHierarchiques(
//	        Integer staffId, Integer roleId, Integer rayonId, Integer categorieId, 
//	        Integer sousCategorieId, Integer platId, Date dateDebut, Date dateFin) {
//	    
//	    List<RapportCommande> rapports = new ArrayList<>();
//	    
//	    System.out.println("=== DEBUT getRapportCommandesByFiltresHierarchiques ===");
//	    System.out.println("Paramètres: staffId=" + staffId + ", roleId=" + roleId + 
//	            ", rayonId=" + rayonId + ", categorieId=" + categorieId + 
//	            ", sousCategorieId=" + sousCategorieId + ", platId=" + platId);
//
//	    StringBuilder sql = new StringBuilder("SELECT "
//	            + "   COALESCE(cd.PLAT_ID, cd.PRODUIT_ID) AS item_id, "
//	            + "   CASE "
//	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN 'PLAT' "
//	            + "       ELSE 'PRODUIT' "
//	            + "   END AS item_type, "
//	            + "   COALESCE(p.NOM, prod.NOM) AS NOM, "
//	            + "   SUM(cd.QUANTITE) AS total_quantite, "
//	            + "   SUM(cd.SOUS_TOTAL) AS total_montant, "
//	            + "   MAX(cd.PRIX_UNITAIRE) AS prix_unitaire "
//	            + "FROM COMMANDE c "
//	            + "JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID "
//	            + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
//	            + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
//	            + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
//	            + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
//	            + "WHERE c.STATUT = 'VISIBLE' "
//	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
//	            + "   AND cd.STATUT = 'VISIBLE' ");
//
//	    List<Object> params = new ArrayList<>();
//
//	    // Filtre staff
//	    if (staffId != null) {
//	        sql.append(" AND c.UTILISATEUR_ID = ? ");
//	        params.add(staffId);
//	    }
//
//	    // Filtre role
//	    if (roleId != null) {
//	        sql.append(" AND staff.ID_ROLE = ? ");
//	        params.add(roleId);
//	    }
//
//	    // Filtres hiérarchiques CORRIGÉS
//	 // Remplacez tous les COALESCE(..., 0) par directement les champs :
//	    if (rayonId != null) {
//	        sql.append(" AND (");
//	        sql.append("   (cd.PLAT_ID IS NOT NULL AND p.RAYON_ID = ?) ");
//	        sql.append("   OR ");
//	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND prod.RAYON_ID = ?) ");
//	        sql.append(") ");
//	        params.add(rayonId);
//	        params.add(rayonId);
//	    }
//
//	    if (categorieId != null) {
//	        sql.append(" AND (");
//	        sql.append("   (cd.PLAT_ID IS NOT NULL AND p.CATEGORIE_ID = ?) ");
//	        sql.append("   OR ");
//	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND prod.CATEGORIE_ID = ?) ");
//	        sql.append(") ");
//	        params.add(categorieId);
//	        params.add(categorieId);
//	    }
//
//	    if (sousCategorieId != null) {
//	        sql.append(" AND (");
//	        sql.append("   (cd.PLAT_ID IS NOT NULL AND p.SOUS_CATEGORIE_ID = ?) ");
//	        sql.append("   OR ");
//	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND prod.SOUS_CATEGORIE_ID = ?) ");
//	        sql.append(") ");
//	        params.add(sousCategorieId);
//	        params.add(sousCategorieId);
//	    }
//	    
//	    if (platId != null) {
//	        sql.append(" AND cd.PLAT_ID = ? ");
//	        params.add(platId);
//	    }
//
//	    // Filtre date
//	    if (dateDebut != null) {
//	        sql.append(" AND c.DATE_COMMANDE >= ? ");
//	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
//	    }
//	    if (dateFin != null) {
//	        sql.append(" AND c.DATE_COMMANDE <= ? ");
//	        params.add(new java.sql.Timestamp(dateFin.getTime()));
//	    }
//
//	    sql.append(" GROUP BY "
//	            + "   CASE "
//	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN CONCAT('PLAT_', cd.PLAT_ID) "
//	            + "       ELSE CONCAT('PRODUIT_', cd.PRODUIT_ID) "
//	            + "   END, "
//	            + "   COALESCE(p.NOM, prod.NOM) "
//	            + "ORDER BY total_montant DESC");
//
//	    System.out.println("SQL généré: " + sql.toString());
//	    System.out.println("Paramètres: " + params);
//
//	    try (Connection conn = getConnection(); 
//	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//
//	        for (int i = 0; i < params.size(); i++) {
//	            Object param = params.get(i);
//	            if (param instanceof Integer) {
//	                stmt.setInt(i + 1, (Integer) param);
//	            } else if (param instanceof Timestamp) {
//	                stmt.setTimestamp(i + 1, (Timestamp) param);
//	            } else {
//	                stmt.setObject(i + 1, param);
//	            }
//	        }
//
//	        System.out.println("Exécution de la requête...");
//
//	        try (ResultSet rs = stmt.executeQuery()) {
//	            int count = 0;
//	            int totalQuantite = 0;
//	            int totalMontant = 0;
//
//	            while (rs.next()) {
//	                count++;
//	                
//	                RapportCommande rapport = new RapportCommande();
//
//	                String itemType = rs.getString("item_type");
//	                int itemId = rs.getInt("item_id");
//	                
//	                if ("PLAT".equals(itemType)) {
//	                    rapport.setPlatId(itemId);
//	                    rapport.setProduitId(null);
//	                } else {
//	                    rapport.setPlatId(null);
//	                    rapport.setProduitId(itemId);
//	                }
//
//	                rapport.setNomPlat(rs.getString("NOM"));
//	                rapport.setQuantiteTotale(rs.getInt("total_quantite"));
//	                rapport.setMontantTotal(rs.getInt("total_montant"));
//	                rapport.setPrixUnitaire(rs.getInt("prix_unitaire"));
//	                
//	                totalQuantite += rs.getInt("total_quantite");
//	                totalMontant += rs.getInt("total_montant");
//
//	                rapports.add(rapport);
//	            }
//
//	            System.out.println("Nombre total de lignes trouvées: " + count);
//	            System.out.println("Total quantité: " + totalQuantite);
//	            System.out.println("Total montant: " + totalMontant);
//	        }
//
//	    } catch (SQLException e) {
//	        System.out.println("Erreur SQL: " + e.getMessage());
//	        e.printStackTrace();
//	    }
//
//	    System.out.println("=== FIN getRapportCommandesByFiltresHierarchiques ===");
//	    System.out.println("Nombre de rapports retournés: " + rapports.size());
//
//	    return rapports;
//	}
	public List<RapportCommande> getRapportCommandesByFiltresHierarchiques(
	        Integer staffId, Integer roleId, Integer rayonId, Integer categorieId, 
	        Integer sousCategorieId, Integer platId, Date dateDebut, Date dateFin) {
	    
	    List<RapportCommande> rapports = new ArrayList<>();
	    
	    System.out.println("=== DEBUT getRapportCommandesByFiltresHierarchiques ===");
	    System.out.println("Paramètres: staffId=" + staffId + ", roleId=" + roleId + 
	            ", rayonId=" + rayonId + ", categorieId=" + categorieId + 
	            ", sousCategorieId=" + sousCategorieId + ", platId=" + platId);

	    // CORRIGÉ : Utiliser PRIX_UNITAIRE * QUANTITE au lieu de SOUS_TOTAL
	    StringBuilder sql = new StringBuilder("SELECT "
	            + "   COALESCE(cd.PLAT_ID, cd.PRODUIT_ID) AS item_id, "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN 'PLAT' "
	            + "       ELSE 'PRODUIT' "
	            + "   END AS item_type, "
	            + "   COALESCE(p.NOM, prod.NOM) AS NOM, "
	            + "   SUM(cd.QUANTITE) AS total_quantite, "
	            + "   SUM(cd.PRIX_UNITAIRE * cd.QUANTITE) AS total_montant, " // CORRIGÉ ICI
	            + "   MAX(cd.PRIX_UNITAIRE) AS prix_unitaire "
	            + "FROM COMMANDE c "
	            + "JOIN COMMANDE_DETAIL cd ON c.ID = cd.COMMANDE_ID "
	            + "LEFT JOIN PLAT p ON cd.PLAT_ID = p.ID "
	            + "LEFT JOIN PRODUITS prod ON cd.PRODUIT_ID = prod.ID "
	            + "LEFT JOIN UTILISATEUR staff ON c.UTILISATEUR_ID = staff.ID "
	            + "LEFT JOIN ROLE role_staff ON staff.ID_ROLE = role_staff.ID "
	            + "WHERE c.STATUT = 'VISIBLE' "
	            + "   AND c.STATUT_COMMANDE != 'ANNULE' "
	            + "   AND cd.STATUT = 'VISIBLE' ");

	    List<Object> params = new ArrayList<>();

	    // Filtre staff
	    if (staffId != null) {
	        sql.append(" AND c.UTILISATEUR_ID = ? ");
	        params.add(staffId);
	    }

	    // Filtre role
	    if (roleId != null) {
	        sql.append(" AND staff.ID_ROLE = ? ");
	        params.add(roleId);
	    }

	    // Filtres hiérarchiques
	    if (rayonId != null) {
	        sql.append(" AND (");
	        sql.append("   (cd.PLAT_ID IS NOT NULL AND COALESCE(p.RAYON_ID, 0) = ?) ");
	        sql.append("   OR ");
	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND COALESCE(prod.RAYON_ID, 0) = ?) ");
	        sql.append(") ");
	        params.add(rayonId);
	        params.add(rayonId);
	    }
	    
	    if (categorieId != null) {
	        sql.append(" AND (");
	        sql.append("   (cd.PLAT_ID IS NOT NULL AND COALESCE(p.CATEGORIE_ID, 0) = ?) ");
	        sql.append("   OR ");
	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND COALESCE(prod.CATEGORIE_ID, 0) = ?) ");
	        sql.append(") ");
	        params.add(categorieId);
	        params.add(categorieId);
	    }
	    
	    if (sousCategorieId != null) {
	        sql.append(" AND (");
	        sql.append("   (cd.PLAT_ID IS NOT NULL AND COALESCE(p.SOUS_CATEGORIE_ID, 0) = ?) ");
	        sql.append("   OR ");
	        sql.append("   (cd.PRODUIT_ID IS NOT NULL AND COALESCE(prod.SOUS_CATEGORIE_ID, 0) = ?) ");
	        sql.append(") ");
	        params.add(sousCategorieId);
	        params.add(sousCategorieId);
	    }
	    
	    if (platId != null) {
	        sql.append(" AND cd.PLAT_ID = ? ");
	        params.add(platId);
	    }

	    // Filtre date
	    if (dateDebut != null) {
	        sql.append(" AND c.DATE_COMMANDE >= ? ");
	        params.add(new java.sql.Timestamp(dateDebut.getTime()));
	    }
	    if (dateFin != null) {
	        sql.append(" AND c.DATE_COMMANDE <= ? ");
	        params.add(new java.sql.Timestamp(dateFin.getTime()));
	    }

	    sql.append(" GROUP BY "
	            + "   CASE "
	            + "       WHEN cd.PLAT_ID IS NOT NULL THEN CONCAT('PLAT_', cd.PLAT_ID) "
	            + "       ELSE CONCAT('PRODUIT_', cd.PRODUIT_ID) "
	            + "   END, "
	            + "   COALESCE(p.NOM, prod.NOM) "
	            + "ORDER BY total_montant DESC");

	    System.out.println("SQL généré: " + sql.toString());
	    System.out.println("Paramètres: " + params);

	    try (Connection conn = getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < params.size(); i++) {
	            Object param = params.get(i);
	            if (param instanceof Integer) {
	                stmt.setInt(i + 1, (Integer) param);
	            } else if (param instanceof Timestamp) {
	                stmt.setTimestamp(i + 1, (Timestamp) param);
	            } else {
	                stmt.setObject(i + 1, param);
	            }
	        }

	        System.out.println("Exécution de la requête...");

	        try (ResultSet rs = stmt.executeQuery()) {
	            int count = 0;
	            int totalQuantite = 0;
	            int totalMontant = 0;

	            while (rs.next()) {
	                count++;
	                
	                RapportCommande rapport = new RapportCommande();

	                String itemType = rs.getString("item_type");
	                int itemId = rs.getInt("item_id");
	                
	                if ("PLAT".equals(itemType)) {
	                    rapport.setPlatId(itemId);
	                    rapport.setProduitId(null);
	                } else {
	                    rapport.setPlatId(null);
	                    rapport.setProduitId(itemId);
	                }

	                rapport.setNomPlat(rs.getString("NOM"));
	                rapport.setQuantiteTotale(rs.getInt("total_quantite"));
	                rapport.setMontantTotal(rs.getInt("total_montant"));
	                rapport.setPrixUnitaire(rs.getInt("prix_unitaire"));
	                
	                totalQuantite += rs.getInt("total_quantite");
	                totalMontant += rs.getInt("total_montant");

	                rapports.add(rapport);
	            }

	            System.out.println("Nombre total de lignes trouvées: " + count);
	            System.out.println("Total quantité: " + totalQuantite);
	            System.out.println("Total montant: " + totalMontant);
	        }

	    } catch (SQLException e) {
	        System.out.println("Erreur SQL: " + e.getMessage());
	        e.printStackTrace();
	    }

	    System.out.println("=== FIN getRapportCommandesByFiltresHierarchiques ===");
	    System.out.println("Nombre de rapports retournés: " + rapports.size());

	    return rapports;
	}
}
