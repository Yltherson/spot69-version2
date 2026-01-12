package com.spot69.dao;

import com.spot69.model.Facture;
import com.spot69.model.FactureDetail;
import com.spot69.model.InventaireCategorie;
import com.spot69.model.MouvementStock;
import com.spot69.model.Produit;
import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MouvementStockDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

   
//OLD
//    public List<MouvementStock> mouvementStockParProduit(int idProduit) {
//        List<MouvementStock> mouvements = new ArrayList<>();
//
//        try (Connection conn = getConnection()) {
//
//            // 1) Factures visibles => Entrées
//            String sqlFactureIn =
//                "SELECT fd.QTE_UNITE, f.CREATED_AT AS date_facture " +
//                "FROM FACTURE_DETAIL fd " +
//                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
//                "WHERE fd.PRODUIT_ID = ? " +
//                "  AND fd.STATUT = 'VISIBLE' " +
//                "  AND f.STATUT = 'VISIBLE'"; 
//
//            try (PreparedStatement stmt = conn.prepareStatement(sqlFactureIn)) {
//                stmt.setInt(1, idProduit);
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        MouvementStock m = new MouvementStock();
//                        m.setDate(rs.getTimestamp("date_facture"));
//                        m.setQteIn(rs.getBigDecimal("QTE_UNITE").intValue());
//                        m.setQteOut(0);
//                        m.setTypeMouvement("FACTURE");
//                        mouvements.add(m);
//                    }
//                }
//            }
//
//            // 2) Factures supprimées => Sorties (on garde la même date que la facture d'origine)
//            String sqlFactureDeleted =
//            	    "SELECT fd.QTE_UNITE, f.CREATED_AT AS date_creation, f.UPDATED_AT AS date_deleted " +
//            	    "FROM FACTURE_DETAIL fd " +
//            	    "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
//            	    "WHERE fd.PRODUIT_ID = ? " +
//            	    "  AND fd.STATUT = 'DELETED' " +
//            	    "  AND f.STATUT = 'DELETED'";
//
//
//            try (PreparedStatement stmt = conn.prepareStatement(sqlFactureDeleted)) {
//                stmt.setInt(1, idProduit);
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        int qte = rs.getBigDecimal("QTE_UNITE").intValue();
//                        Timestamp dateCreation = rs.getTimestamp("date_creation");
//                        Timestamp dateDeleted = rs.getTimestamp("date_deleted");
//
//                        // Ligne entrée
//                        MouvementStock mIn = new MouvementStock();
//                        mIn.setDate(dateCreation);
//                        mIn.setQteIn(qte);
//                        mIn.setQteOut(0);
//                        mIn.setTypeMouvement("FACTURE");
//                        mouvements.add(mIn);
//
//                        // Ligne sortie
//                        MouvementStock mOut = new MouvementStock();
//                        mOut.setDate(dateDeleted);
//                        mOut.setQteIn(0);
//                        mOut.setQteOut(qte);
//                        mOut.setTypeMouvement("FACTURE (SUPPRIMÉE)");
//                        mouvements.add(mOut);
//                    }
//                }
//            }
//
//            // 3) Commandes (sorties + annulations)
//            String sqlCmd =
//                "SELECT cd.QUANTITE, c.STATUT_COMMANDE, " +
//                "       c.CREATED_AT AS date_commande, " +
//                "       c.UPDATED_AT AS date_annulation " +
//                "FROM COMMANDE_DETAIL cd " +
//                "JOIN COMMANDE c ON cd.COMMANDE_ID = c.ID " +
//                "WHERE cd.PRODUIT_ID = ? " +
//                "  AND cd.STATUT = 'VISIBLE' " +
//                "  AND c.STATUT = 'VISIBLE'";
//
//            try (PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
//                stmt.setInt(1, idProduit);
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        int qte = rs.getInt("QUANTITE");
//                        String statutCmd = rs.getString("STATUT_COMMANDE");
//                        Timestamp dateCmd = rs.getTimestamp("date_commande");
//                        Timestamp dateAnnulation = rs.getTimestamp("date_annulation");
//
//                        // Sortie (commande normale)
//                        MouvementStock mSortie = new MouvementStock();
//                        mSortie.setDate(dateCmd);
//                        mSortie.setQteIn(0);
//                        mSortie.setQteOut(qte);
//                        mSortie.setTypeMouvement("CMD");
//                        mouvements.add(mSortie);
//
//                        // Réintégration (si annulée)
//                        if ("ANNULE".equalsIgnoreCase(statutCmd)) {
//                            MouvementStock mAnnule = new MouvementStock();
//                            mAnnule.setDate(dateAnnulation != null ? dateAnnulation : dateCmd);
//                            mAnnule.setQteIn(qte);
//                            mAnnule.setQteOut(0);
//                            mAnnule.setTypeMouvement("CMD (ANNULE)");
//                            mouvements.add(mAnnule);
//                        }
//                    }
//                }
//            }
//
//            // 4) Tri chronologique (cas spécial : FACTURE SUPPRIMÉE toujours avant FACTURE si même date)
//            mouvements.sort((m1, m2) -> {
//                int cmp = m1.getDate().compareTo(m2.getDate());
//                if (cmp == 0) {
//                    // Facture supprimée avant facture normale
//                    if ("FACTURE (SUPPRIMÉE)".equals(m1.getTypeMouvement()) &&
//                        "FACTURE".equals(m2.getTypeMouvement())) {
//                        return -1;
//                    }
//                    if ("FACTURE (SUPPRIMÉE)".equals(m2.getTypeMouvement()) &&
//                        "FACTURE".equals(m1.getTypeMouvement())) {
//                        return 1;
//                    }
//                }
//                return cmp;
//            });
//
//
//            // 5) Calcul du stock
//            int stockActuel = 0;
//            for (MouvementStock m : mouvements) {
//                m.setStockDebut(stockActuel);
//                stockActuel += m.getQteIn();
//                stockActuel -= m.getQteOut();
//                m.setStockFin(stockActuel);
//            }
//
//            // 6) Optionnel : afficher du plus récent au plus ancien
//            Collections.reverse(mouvements);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return mouvements;
//    }
    
    public List<MouvementStock> mouvementStockParProduit(int idProduit) {
        List<MouvementStock> mouvements = new ArrayList<>();

        try (Connection conn = getConnection()) {

            // 1) Factures visibles => Entrées (sauf ajustements)
            String sqlFactureIn =
                "SELECT fd.QTE_UNITE, f.CREATED_AT AS date_facture, " +
                "       CASE WHEN f.NO_FACTURE LIKE 'AJUST-%' THEN 'AJUSTEMENT ENTREE' ELSE 'FACTURE' END AS type_mvt " +
                "FROM FACTURE_DETAIL fd " +
                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "  AND fd.STATUT = 'VISIBLE' " +
                "  AND f.STATUT = 'VISIBLE' " +
                "  AND fd.QUANTITE > 0 " +
                "  AND f.NO_FACTURE NOT LIKE 'AJUST-%'"; // Exclure les ajustements de cette requête

            try (PreparedStatement stmt = conn.prepareStatement(sqlFactureIn)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        MouvementStock m = new MouvementStock();
                        m.setDate(rs.getTimestamp("date_facture"));
                        
                        // Vérifier si QTE_UNITE est NULL
                        BigDecimal qteUnite = rs.getBigDecimal("QTE_UNITE");
                        if (qteUnite != null) {
                            m.setQteIn(qteUnite.intValue());
                        } else {
                            m.setQteIn(0); // Valeur par défaut si NULL
                        }
                        
                        m.setQteOut(0);
                        m.setTypeMouvement(rs.getString("type_mvt"));
                        mouvements.add(m);
                    }
                }
            }

            // 2) Ajustements en sortie (quantités négatives)
            String sqlAjustementSortie =
                "SELECT ABS(fd.QUANTITE) AS quantite, f.CREATED_AT AS date_ajustement, " +
                "       f.NO_FACTURE " +
                "FROM FACTURE_DETAIL fd " +
                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "  AND fd.STATUT = 'VISIBLE' " +
                "  AND f.STATUT = 'VISIBLE' " +
                "  AND f.NO_FACTURE LIKE 'AJUST-%' " +
                "  AND fd.QUANTITE < 0";

            try (PreparedStatement stmt = conn.prepareStatement(sqlAjustementSortie)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        MouvementStock m = new MouvementStock();
                        m.setDate(rs.getTimestamp("date_ajustement"));
                        m.setQteIn(0);
                        
                        // Vérifier si quantite est NULL
                        BigDecimal quantite = rs.getBigDecimal("quantite");
                        if (quantite != null) {
                            m.setQteOut(quantite.intValue());
                        } else {
                            m.setQteOut(0);
                        }
                        
                        m.setTypeMouvement("AJUSTEMENT SORTIE");
                        mouvements.add(m);
                    }
                }
            }

            // 3) Ajustements en entrée (quantités positives)
            String sqlAjustementEntree =
                "SELECT fd.QUANTITE, f.CREATED_AT AS date_ajustement, " +
                "       f.NO_FACTURE " +
                "FROM FACTURE_DETAIL fd " +
                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "  AND fd.STATUT = 'VISIBLE' " +
                "  AND f.STATUT = 'VISIBLE' " +
                "  AND f.NO_FACTURE LIKE 'AJUST-%' " +
                "  AND fd.QUANTITE > 0";

            try (PreparedStatement stmt = conn.prepareStatement(sqlAjustementEntree)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        MouvementStock m = new MouvementStock();
                        m.setDate(rs.getTimestamp("date_ajustement"));
                        
                        // Vérifier si QUANTITE est NULL
                        BigDecimal quantite = rs.getBigDecimal("QUANTITE");
                        if (quantite != null) {
                            m.setQteIn(quantite.intValue());
                        } else {
                            m.setQteIn(0);
                        }
                        
                        m.setQteOut(0);
                        m.setTypeMouvement("AJUSTEMENT ENTREE");
                        mouvements.add(m);
                    }
                }
            }

            // 4) Factures supprimées => Sorties
            String sqlFactureDeleted =
                "SELECT fd.QTE_UNITE, f.CREATED_AT AS date_creation, f.UPDATED_AT AS date_deleted " +
                "FROM FACTURE_DETAIL fd " +
                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "  AND fd.STATUT = 'DELETED' " +
                "  AND f.STATUT = 'DELETED' " +
                "  AND f.NO_FACTURE NOT LIKE 'AJUST-%'";

            try (PreparedStatement stmt = conn.prepareStatement(sqlFactureDeleted)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Vérifier si QTE_UNITE est NULL
                        BigDecimal qteUnite = rs.getBigDecimal("QTE_UNITE");
                        if (qteUnite == null) {
                            continue; // Passer à l'itération suivante
                        }
                        
                        int qte = qteUnite.intValue();
                        Timestamp dateCreation = rs.getTimestamp("date_creation");
                        Timestamp dateDeleted = rs.getTimestamp("date_deleted");

                        // Ligne entrée
                        MouvementStock mIn = new MouvementStock();
                        mIn.setDate(dateCreation);
                        mIn.setQteIn(qte);
                        mIn.setQteOut(0);
                        mIn.setTypeMouvement("FACTURE");
                        mouvements.add(mIn);

                        // Ligne sortie
                        MouvementStock mOut = new MouvementStock();
                        mOut.setDate(dateDeleted);
                        mOut.setQteIn(0);
                        mOut.setQteOut(qte);
                        mOut.setTypeMouvement("FACTURE (SUPPRIMÉE)");
                        mouvements.add(mOut);
                    }
                }
            }

            // 5) Ajustements supprimés
            String sqlAjustementDeleted =
                "SELECT fd.QUANTITE, f.CREATED_AT AS date_creation, f.UPDATED_AT AS date_deleted, " +
                "       f.NO_FACTURE " +
                "FROM FACTURE_DETAIL fd " +
                "JOIN FACTURE f ON fd.FACTURE_ID = f.ID " +
                "WHERE fd.PRODUIT_ID = ? " +
                "  AND fd.STATUT = 'DELETED' " +
                "  AND f.STATUT = 'DELETED' " +
                "  AND f.NO_FACTURE LIKE 'AJUST-%'";

            try (PreparedStatement stmt = conn.prepareStatement(sqlAjustementDeleted)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Vérifier si QUANTITE est NULL
                        BigDecimal quantiteBD = rs.getBigDecimal("QUANTITE");
                        if (quantiteBD == null) {
                            continue;
                        }
                        
                        int quantite = quantiteBD.intValue();
                        Timestamp dateCreation = rs.getTimestamp("date_creation");
                        Timestamp dateDeleted = rs.getTimestamp("date_deleted");

                        // Déterminer si c'était une entrée ou sortie
                        String typeOriginal = quantite > 0 ? "AJUSTEMENT ENTREE" : "AJUSTEMENT SORTIE";
                        int qteAbs = Math.abs(quantite);

                        // Ligne ajustement original
                        MouvementStock mOriginal = new MouvementStock();
                        mOriginal.setDate(dateCreation);
                        if (quantite > 0) {
                            mOriginal.setQteIn(qteAbs);
                            mOriginal.setQteOut(0);
                        } else {
                            mOriginal.setQteIn(0);
                            mOriginal.setQteOut(qteAbs);
                        }
                        mOriginal.setTypeMouvement(typeOriginal);
                        mouvements.add(mOriginal);

                        // Ligne annulation (inverse)
                        MouvementStock mAnnule = new MouvementStock();
                        mAnnule.setDate(dateDeleted);
                        if (quantite > 0) {
                            mAnnule.setQteIn(0);
                            mAnnule.setQteOut(qteAbs);
                        } else {
                            mAnnule.setQteIn(qteAbs);
                            mAnnule.setQteOut(0);
                        }
                        mAnnule.setTypeMouvement(typeOriginal + " (SUPPRIMÉ)");
                        mouvements.add(mAnnule);
                    }
                }
            }

            // 6) Commandes (sorties + annulations)
            String sqlCmd =
                "SELECT cd.QUANTITE, c.STATUT_COMMANDE, " +
                "       c.CREATED_AT AS date_commande, " +
                "       c.UPDATED_AT AS date_annulation " +
                "FROM COMMANDE_DETAIL cd " +
                "JOIN COMMANDE c ON cd.COMMANDE_ID = c.ID " +
                "WHERE cd.PRODUIT_ID = ? " +
                "  AND cd.STATUT = 'VISIBLE' " +
                "  AND c.STATUT = 'VISIBLE'";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCmd)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Vérifier si QUANTITE est NULL
                        BigDecimal quantiteBD = rs.getBigDecimal("QUANTITE");
                        if (quantiteBD == null) {
                            continue;
                        }
                        
                        int qte = quantiteBD.intValue();
                        String statutCmd = rs.getString("STATUT_COMMANDE");
                        Timestamp dateCmd = rs.getTimestamp("date_commande");
                        Timestamp dateAnnulation = rs.getTimestamp("date_annulation");

                        // Sortie (commande normale)
                        MouvementStock mSortie = new MouvementStock();
                        mSortie.setDate(dateCmd);
                        mSortie.setQteIn(0);
                        mSortie.setQteOut(qte);
                        mSortie.setTypeMouvement("CMD");
                        mouvements.add(mSortie);

                        // Réintégration (si annulée)
                        if ("ANNULE".equalsIgnoreCase(statutCmd) && dateAnnulation != null) {
                            MouvementStock mAnnule = new MouvementStock();
                            mAnnule.setDate(dateAnnulation);
                            mAnnule.setQteIn(qte);
                            mAnnule.setQteOut(0);
                            mAnnule.setTypeMouvement("CMD (ANNULE)");
                            mouvements.add(mAnnule);
                        }
                    }
                }
            }

            // 7) Tri chronologique avec priorité pour les suppressions
            mouvements.sort((m1, m2) -> {
                int cmp = m1.getDate().compareTo(m2.getDate());
                if (cmp == 0) {
                    // Priorité 1: Facture supprimée avant facture normale
                    if (m1.getTypeMouvement().contains("SUPPRIMÉ") && 
                        !m2.getTypeMouvement().contains("SUPPRIMÉ")) {
                        return -1;
                    }
                    if (m2.getTypeMouvement().contains("SUPPRIMÉ") && 
                        !m1.getTypeMouvement().contains("SUPPRIMÉ")) {
                        return 1;
                    }
                    // Priorité 2: Ajustements après factures normales (même date)
                    if (m1.getTypeMouvement().contains("AJUSTEMENT") && 
                        !m2.getTypeMouvement().contains("AJUSTEMENT")) {
                        return 1;
                    }
                    if (m2.getTypeMouvement().contains("AJUSTEMENT") && 
                        !m1.getTypeMouvement().contains("AJUSTEMENT")) {
                        return -1;
                    }
                }
                return cmp;
            });

            // 8) Calcul du stock
            int stockActuel = 0;
            for (MouvementStock m : mouvements) {
                m.setStockDebut(stockActuel);
                stockActuel += m.getQteIn();
                stockActuel -= m.getQteOut();
                m.setStockFin(stockActuel);
            }

            // 9) Afficher du plus récent au plus ancien
            Collections.reverse(mouvements);

        } catch (SQLException e) {
            e.printStackTrace();
            // Retourner une liste vide en cas d'erreur
            return new ArrayList<>();
        }

        return mouvements;
    }

 // Ajoutez cette méthode pour créer un ajustement
    public boolean creerAjustement(int produitId, int quantite, int userId, String justification) {
        String sqlFacture = "INSERT INTO FACTURE (NO_FACTURE, MONTANT_TOTAL, UTILISATEUR_ID, CREATED_AT, UPDATED_AT) " +
                           "VALUES (?, 0, ?, NOW(), NOW())";
        
        String sqlDetail = "INSERT INTO FACTURE_DETAIL (FACTURE_ID, PRODUIT_ID, QUANTITE, STATUT, CREATED_AT) " +
                          "VALUES (?, ?, ?, 'VISIBLE', NOW())";
        
        String sqlUpdateProduit = "UPDATE PRODUITS SET QTE_EN_STOCK = QTE_EN_STOCK + ? WHERE ID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Générer un numéro de facture d'ajustement unique
            String numeroAjustement = genererNumeroAjustement();

            // 1. Créer la facture d'ajustement
            int factureId;
            try (PreparedStatement stmtFacture = conn.prepareStatement(sqlFacture, Statement.RETURN_GENERATED_KEYS)) {
                stmtFacture.setString(1, numeroAjustement);
                stmtFacture.setInt(2, userId);
                stmtFacture.executeUpdate();

                ResultSet rs = stmtFacture.getGeneratedKeys();
                if (rs.next()) {
                    factureId = rs.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Créer le détail de la facture
            try (PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail)) {
                stmtDetail.setInt(1, factureId);
                stmtDetail.setInt(2, produitId);
                stmtDetail.setInt(3, quantite);
                stmtDetail.executeUpdate();
            }

            // 3. Mettre à jour le stock du produit
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateProduit)) {
                stmtUpdate.setInt(1, quantite);
                stmtUpdate.setInt(2, produitId);
                stmtUpdate.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String genererNumeroAjustement() {
        Random random = new Random();
        String numero;
        do {
            int part1 = 100 + random.nextInt(900);     // Trois chiffres
            int part2 = 1000 + random.nextInt(9000);   // Quatre chiffres
            int part3 = 100 + random.nextInt(900);     // Trois chiffres
            numero = "AJUST-" + part1 + "-" + part2 + "-" + part3;
        } while (existeNumeroAjustement(numero));
        return numero;
    }

    private boolean existeNumeroAjustement(String numero) {
        String sql = "SELECT COUNT(*) FROM FACTURE WHERE NO_FACTURE = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }




    
}
