package com.spot69.dao;

import com.spot69.model.*;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FactureDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public int ajouterFacture(Facture facture, List<FactureDetail> details) {
        String sql = "INSERT INTO FACTURE (NO_FACTURE, MONTANT_TOTAL, MONTANT_VERSE, MOYEN_PAIEMENT, IS_CREDIT, SOLDE, FOURNISSEUR_ID, UTILISATEUR_ID, CREATED_AT, UPDATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDetail = "INSERT INTO FACTURE_DETAIL (FACTURE_ID, PRODUIT_ID, QUANTITE, PRIX_ACHAT_PAR_UNITE_MESURE, PRIX_ACHAT_TOTAL, QTE_UNITE, PRIX_REVIENT_UNITE) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlInventaire = "INSERT INTO INVENTAIRE (FACTURE_DETAIL_ID, PRODUIT_ID, UTILISATEUR_ID, QUANTITE_STOCK, EMPLACEMENT, FOURNISSEUR_ID, CREATED_AT, UPDATED_AT) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int factureId;

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, facture.getNoFacture());
                stmt.setInt(2, facture.getMontantTotal());
                stmt.setInt(3, facture.getMontantVerse());
                stmt.setString(4, facture.getMoyenPaiement());
                stmt.setBoolean(5, facture.isCredit());
                stmt.setInt(6, facture.getSolde());
                stmt.setInt(7, facture.getFournisseur().getId());
                stmt.setInt(8, facture.getUtilisateur().getId());
                stmt.setTimestamp(9, new Timestamp(new Date().getTime()));
                stmt.setTimestamp(10, new Timestamp(new Date().getTime()));
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    factureId = rs.getInt(1);
                    facture.setId(factureId);
                } else {
                    conn.rollback();
                    return -1;
                }
            }

            for (FactureDetail detail : details) {
                try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail, Statement.RETURN_GENERATED_KEYS)) {
                    
                	stmtDetail.setInt(1, factureId);
                    stmtDetail.setInt(2, detail.getProduit().getId());
                    stmtDetail.setInt(3, detail.getQuantite());
                    stmtDetail.setInt(4, detail.getPrixAchatParUniteMesure());
                    stmtDetail.setInt(5, detail.getPrixAchatTotal());
                    stmtDetail.setInt(6, detail.getQteUnite());
                    stmtDetail.setInt(7, detail.getPrixRevientUnite());
                    stmtDetail.executeUpdate();

                    ResultSet rs = stmtDetail.getGeneratedKeys();
                    if (rs.next()) {
                        int detailId = rs.getInt(1);
                        detail.setId(detailId);

                        try (PreparedStatement stmtInv = conn.prepareStatement(sqlInventaire)) {
                            stmtInv.setInt(1, detailId);
                            stmtInv.setInt(2, detail.getProduit().getId());
                            stmtInv.setInt(3, facture.getUtilisateur().getId());
                            stmtInv.setInt(4, detail.getQuantite());
                            stmtInv.setString(5, detail.getProduit().getEmplacement());
                            stmtInv.setInt(6, facture.getFournisseur().getId());
                            stmtInv.setTimestamp(7, new Timestamp(new Date().getTime()));
                            stmtInv.setTimestamp(8, new Timestamp(new Date().getTime()));
                            stmtInv.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
            return factureId;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public boolean modifierFacture(Facture facture, List<FactureDetail> details) {
        String sqlUpdateFacture = "UPDATE FACTURE SET MONTANT_TOTAL = ?, MONTANT_VERSE = ?, MOYEN_PAIEMENT = ?, IS_CREDIT = ?, SOLDE = ?, FOURNISSEUR_ID = ?, UTILISATEUR_ID = ?, UPDATED_AT = ? WHERE ID = ?";
        
        String sqlDeleteDetails = "DELETE FROM FACTURE_DETAIL WHERE FACTURE_ID = ?";

        String sqlInsertDetail = "INSERT INTO FACTURE_DETAIL (FACTURE_ID, PRODUIT_ID, QUANTITE, PRIX_ACHAT_PAR_UNITE_MESURE, PRIX_ACHAT_TOTAL, QTE_UNITE, PRIX_REVIENT_UNITE) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlInsertInventaire = "INSERT INTO INVENTAIRE (FACTURE_DETAIL_ID, PRODUIT_ID, UTILISATEUR_ID, QUANTITE_STOCK, EMPLACEMENT, FOURNISSEUR_ID, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // 1. Mise à jour de la facture
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateFacture)) {
                stmt.setInt(1, facture.getMontantTotal());
                stmt.setInt(2, facture.getMontantVerse());
                stmt.setString(3, facture.getMoyenPaiement());
                stmt.setBoolean(4, facture.isCredit());
                stmt.setInt(5, facture.getSolde());
                stmt.setInt(6, facture.getFournisseur().getId());
                stmt.setInt(7, facture.getUtilisateur().getId());
                stmt.setTimestamp(8, new Timestamp(new Date().getTime()));
                stmt.setInt(9, facture.getId());
                stmt.executeUpdate();
            }

            // 2. Suppression des anciens détails
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteDetails)) {
                stmtDelete.setInt(1, facture.getId());
                stmtDelete.executeUpdate();
            }

            // 3. Réinsertion de tous les détails (inserts uniquement)
            for (FactureDetail detail : details) {
                try (PreparedStatement stmtDetail = conn.prepareStatement(sqlInsertDetail, Statement.RETURN_GENERATED_KEYS)) {
                    stmtDetail.setInt(1, facture.getId());
                    stmtDetail.setInt(2, detail.getProduit().getId());
                    stmtDetail.setInt(3, detail.getQuantite());
                    stmtDetail.setInt(4, detail.getPrixAchatParUniteMesure());
                    stmtDetail.setInt(5, detail.getPrixAchatTotal());
                    stmtDetail.setInt(6, detail.getQteUnite());
                    stmtDetail.setInt(7, detail.getPrixRevientUnite());
                    stmtDetail.executeUpdate();

                    ResultSet rs = stmtDetail.getGeneratedKeys();
                    if (rs.next()) {
                        int detailId = rs.getInt(1);
                        detail.setId(detailId);

                        try (PreparedStatement stmtInv = conn.prepareStatement(sqlInsertInventaire)) {
                            stmtInv.setInt(1, detailId);
                            stmtInv.setInt(2, detail.getProduit().getId());
                            stmtInv.setInt(3, facture.getUtilisateur().getId());
                            stmtInv.setInt(4, detail.getQuantite());
                            stmtInv.setString(5, detail.getProduit().getEmplacement());
                            stmtInv.setInt(6, facture.getFournisseur().getId());
                            Timestamp now = new Timestamp(new Date().getTime());
                            stmtInv.setTimestamp(7, now);
                            stmtInv.setTimestamp(8, now);
                            stmtInv.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    
    public boolean existeNumeroFacture(String noFacture) {
        String sql = "SELECT COUNT(*) FROM FACTURE WHERE NO_FACTURE = ? AND STATUT = 'VISIBLE'";
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


    public void supprimerFacture(int id, int deletedBy) {
        String sqlUpdateFacture = "UPDATE FACTURE SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE ID = ?";
        String sqlUpdateDetails = "UPDATE FACTURE_DETAIL SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = ? WHERE FACTURE_ID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Mettre à jour la facture
            try (PreparedStatement stmtFacture = conn.prepareStatement(sqlUpdateFacture)) {
                stmtFacture.setInt(1, deletedBy);
                stmtFacture.setTimestamp(2, new Timestamp(new Date().getTime()));
                stmtFacture.setInt(3, id);
                stmtFacture.executeUpdate();
            }

            // Mettre à jour les détails
            try (PreparedStatement stmtDetails = conn.prepareStatement(sqlUpdateDetails)) {
                stmtDetails.setInt(1, deletedBy);
                stmtDetails.setTimestamp(2, new Timestamp(new Date().getTime()));
                stmtDetails.setInt(3, id);
                stmtDetails.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Ici tu peux aussi rollback si tu veux, ou gérer l'erreur
        }
    }

    public List<Facture> listerFactures() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT f.*, " +
                     "fr.ID AS fournisseur_id, fr.NOM AS fournisseur_nom, " +
                     "u.ID AS utilisateur_id, u.NOM AS utilisateur_nom, " +
                     "du.ID AS deletedby_id, du.NOM AS deletedby_nom " +
                     "FROM FACTURE f " +
                     "LEFT JOIN FOURNISSEUR fr ON f.FOURNISSEUR_ID = fr.ID " +
                     "LEFT JOIN UTILISATEUR u ON f.UTILISATEUR_ID = u.ID " +
                     "LEFT JOIN UTILISATEUR du ON f.DELETED_BY = du.ID " +
                     "WHERE f.STATUT IS NULL OR f.STATUT = 'VISIBLE' " +
                     "ORDER BY f.CREATED_AT DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Facture facture = new Facture();

                facture.setId(rs.getInt("ID"));
                facture.setNoFacture(rs.getString("NO_FACTURE"));
                facture.setMontantTotal(rs.getInt("MONTANT_TOTAL"));
                facture.setMontantVerse(rs.getInt("MONTANT_VERSE"));

                facture.setMoyenPaiement(rs.getString("MOYEN_PAIEMENT"));
                facture.setDeletedInterval(rs.getInt("DELETED_INTERVAL"));
                facture.setSolde(rs.getInt("SOLDE"));

                facture.setCredit(rs.getBoolean("IS_CREDIT"));
                facture.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                facture.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

                // Fournisseur
                int fournisseurId = rs.getInt("fournisseur_id");
                if (!rs.wasNull()) {
                    Fournisseur fournisseur = new Fournisseur();
                    fournisseur.setId(fournisseurId);
                    fournisseur.setNom(rs.getString("fournisseur_nom"));
                    // ajoute les autres setters ici selon ta classe Fournisseur
                    facture.setFournisseur(fournisseur);
                }

                // Utilisateur
                int utilisateurId = rs.getInt("utilisateur_id");
                if (!rs.wasNull()) {
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setId(utilisateurId);
                    utilisateur.setNom(rs.getString("utilisateur_nom"));
                    // ajoute les autres setters ici selon ta classe Utilisateur
                    facture.setUtilisateur(utilisateur);
                }

                // DeletedBy (peut être NULL)
                int deletedById = rs.getInt("deletedby_id");
                if (!rs.wasNull()) {
                    Utilisateur deletedBy = new Utilisateur();
                    deletedBy.setId(deletedById);
                    deletedBy.setNom(rs.getString("deletedby_nom"));
                    // autres setters si besoin
                    facture.setDeletedBy(deletedBy);
                }

                factures.add(facture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return factures;
    }


    public Facture chercherParId(int id) {
        String sql = "SELECT * FROM FACTURE WHERE ID = ?";
        Facture facture = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extraction des données dans des variables locales
                	int factureId = rs.getInt("ID");
                	int deletedInterval = rs.getInt("DELETED_INTERVAL");
                	String noFacture = rs.getString("NO_FACTURE");

                	// Conversion sécurisée pour BigInteger
                	Integer montantTotal = Integer.valueOf(rs.getInt("MONTANT_TOTAL"));
                	Integer montantVerse = Integer.valueOf(rs.getInt("MONTANT_VERSE"));

                	// Pour BigDecimal (solde), on utilise getBigDecimal
                	Integer solde = Integer.valueOf(rs.getInt("SOLDE"));

                	String moyenPaiement = rs.getString("MOYEN_PAIEMENT");
                	boolean isCredit = rs.getBoolean("IS_CREDIT");
                	Timestamp createdAt = rs.getTimestamp("CREATED_AT");
                	Timestamp updatedAt = rs.getTimestamp("UPDATED_AT");
                	int deletedById = rs.getInt("DELETED_BY");
                	int fournisseurId = rs.getInt("FOURNISSEUR_ID");
                	int utilisateurId = rs.getInt("UTILISATEUR_ID");


                    // Construction de l'objet Facture en dehors du ResultSet
                    facture = new Facture();
                    facture.setId(factureId);
                    facture.setDeletedInterval(deletedInterval);
                    facture.setNoFacture(noFacture);
                    facture.setMontantTotal(montantTotal);
                    facture.setMontantVerse(montantVerse);
                    facture.setMoyenPaiement(moyenPaiement);
                    facture.setSolde(solde);
                    facture.setCredit(isCredit);
                    facture.setCreatedAt(createdAt);
                    facture.setUpdatedAt(updatedAt);

                    // Chargement des objets associés (avec appels DAO)
                    if (deletedById != 0) {
                        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                        facture.setDeletedBy(utilisateurDAO.findById(deletedById));
                    }

                    FournisseurDAO fournisseurDAO = new FournisseurDAO();
                    facture.setFournisseur(fournisseurDAO.findById(fournisseurId));

                    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                    facture.setUtilisateur(utilisateurDAO.findById(utilisateurId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return facture;
    }


    public Facture chercherParNumero(String numero) {
        String sql = "SELECT * FROM FACTURE WHERE NO_FACTURE = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extraireFacture(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateDeletedInterval(int days) {
        String sql = "UPDATE FACTURE SET DELETED_INTERVAL = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getDeletedInterval() {
        String sql = "SELECT DELETED_INTERVAL FROM FACTURE LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("DELETED_INTERVAL");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }



    public List<FactureDetail> getDetailsFacture(int factureId) {
        List<FactureDetail> details = new ArrayList<>();

        String sql = "SELECT fd.*, " +
                     "       p.ID AS produit_id, p.NOM AS produit_nom, p.DESCRIPTION AS produit_description, " +
                     "       p.UNITE_VENTE, p.PRIX_VENTE, p.CONTENU_PAR_UNITE " +
                     "FROM FACTURE_DETAIL fd " +
                     "JOIN PRODUITS p ON fd.PRODUIT_ID = p.ID " +
                     "WHERE fd.FACTURE_ID = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, factureId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FactureDetail detail = new FactureDetail();

                    detail.setId(rs.getInt("ID"));
                    detail.setQuantite(rs.getInt("QUANTITE"));
                    detail.setPrixAchatParUniteMesure(rs.getInt("PRIX_ACHAT_PAR_UNITE_MESURE"));
                    detail.setPrixAchatTotal(rs.getInt("PRIX_ACHAT_TOTAL"));
                    detail.setQteUnite(rs.getInt("QTE_UNITE"));
                    detail.setPrixRevientUnite(rs.getInt("PRIX_REVIENT_UNITE"));


                    // Produit
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("produit_id"));
                    produit.setNom(rs.getString("produit_nom"));
                    produit.setDescription(rs.getString("produit_description"));
                    produit.setUniteVente(rs.getString("UNITE_VENTE"));
                    produit.setContenuParUnite(rs.getString("CONTENU_PAR_UNITE"));
                    produit.setPrixVente(rs.getBigDecimal("PRIX_VENTE"));

                    detail.setProduit(produit);
                    details.add(detail);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return details;
    }



    private Facture extraireFacture(ResultSet rs) throws SQLException {
        Facture facture = new Facture();
        facture.setId(rs.getInt("ID"));
        facture.setNoFacture(rs.getString("NO_FACTURE"));
        facture.setMontantTotal(rs.getInt("MONTANT_TOTAL"));
        facture.setMontantVerse(rs.getInt("MONTANT_VERSE"));

        facture.setMoyenPaiement(rs.getString("MOYEN_PAIEMENT"));
        facture.setDeletedInterval(rs.getInt("DELETED_INTERVAL"));
        facture.setSolde(rs.getInt("SOLDE"));

        facture.setCredit(rs.getBoolean("IS_CREDIT"));
        facture.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        facture.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

      
        if (rs.getInt("DELETED_BY") != 0) {
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            facture.setDeletedBy(utilisateurDAO.findById(rs.getInt("DELETED_BY")));
        }

        FournisseurDAO fournisseurDAO = new FournisseurDAO();
        facture.setFournisseur(fournisseurDAO.findById(rs.getInt("FOURNISSEUR_ID")));

        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        facture.setUtilisateur(utilisateurDAO.findById(rs.getInt("UTILISATEUR_ID")));

        return facture;
    }
}
