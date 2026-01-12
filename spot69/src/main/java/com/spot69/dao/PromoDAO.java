package com.spot69.dao;

import com.spot69.model.Promo;
import com.spot69.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PromoDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une nouvelle promo
    public int ajouterPromo(Promo promo) {
        String sql = "INSERT INTO PROMO (TITRE, SOUS_TITRE, DESCRIPTION, TYPE_CONTENU, CHEMIN_MEDIA, " +
                     "COULEURS_GRADIENT, TEXTE_BOUTON, ROUTE_CIBLE, DUREE_VIDEO, VUES, " +
                     "ORDRE_AFFICHAGE, STATUT, DATE_DEBUT, DATE_FIN, UTILISATEUR_ID, " +
                     "CREATION_DATE, UPDATE_DATE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, promo.getTitre());
            stmt.setString(2, promo.getSousTitre());
            stmt.setString(3, promo.getDescription());
            stmt.setString(4, promo.getTypeContenu());
            stmt.setString(5, promo.getCheminMedia());
            stmt.setString(6, promo.getCouleursGradientString());
            stmt.setString(7, promo.getTexteBouton());
            stmt.setString(8, promo.getRouteCible());
            stmt.setString(9, promo.getDureeVideo());
            stmt.setString(10, promo.getVues());
            stmt.setInt(11, promo.getOrdreAffichage());
            stmt.setString(12, promo.getStatut());
            
            if (promo.getDateDebut() != null) {
                stmt.setTimestamp(13, Timestamp.valueOf(promo.getDateDebut()));
            } else {
                stmt.setNull(13, Types.TIMESTAMP);
            }
            
            if (promo.getDateFin() != null) {
                stmt.setTimestamp(14, Timestamp.valueOf(promo.getDateFin()));
            } else {
                stmt.setNull(14, Types.TIMESTAMP);
            }
            
            stmt.setInt(15, promo.getUtilisateurId());
            stmt.setTimestamp(16, Timestamp.valueOf(promo.getCreationDate()));
            stmt.setTimestamp(17, Timestamp.valueOf(promo.getUpdateDate()));
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Modifier une promo
    public boolean modifierPromo(Promo promo) {
        String sql = "UPDATE PROMO SET TITRE = ?, SOUS_TITRE = ?, DESCRIPTION = ?, " +
                     "TYPE_CONTENU = ?, CHEMIN_MEDIA = ?, COULEURS_GRADIENT = ?, " +
                     "TEXTE_BOUTON = ?, ROUTE_CIBLE = ?, DUREE_VIDEO = ?, VUES = ?, " +
                     "ORDRE_AFFICHAGE = ?, STATUT = ?, DATE_DEBUT = ?, DATE_FIN = ?, " +
                     "UPDATE_DATE = ? WHERE ID = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promo.getTitre());
            stmt.setString(2, promo.getSousTitre());
            stmt.setString(3, promo.getDescription());
            stmt.setString(4, promo.getTypeContenu());
            stmt.setString(5, promo.getCheminMedia());
            stmt.setString(6, promo.getCouleursGradientString());
            stmt.setString(7, promo.getTexteBouton());
            stmt.setString(8, promo.getRouteCible());
            stmt.setString(9, promo.getDureeVideo());
            stmt.setString(10, promo.getVues());
            stmt.setInt(11, promo.getOrdreAffichage());
            stmt.setString(12, promo.getStatut());
            
            if (promo.getDateDebut() != null) {
                stmt.setTimestamp(13, Timestamp.valueOf(promo.getDateDebut()));
            } else {
                stmt.setNull(13, Types.TIMESTAMP);
            }
            
            if (promo.getDateFin() != null) {
                stmt.setTimestamp(14, Timestamp.valueOf(promo.getDateFin()));
            } else {
                stmt.setNull(14, Types.TIMESTAMP);
            }
            
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(16, promo.getId());
            
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une promo (changer le statut en 'supprime')
    public boolean supprimerPromo(int id) {
        String sql = "DELETE FROM PROMO WHERE ID = ?";
        System.out.println("DELETE FROM PROMO WHERE ID = ?"+id);
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
//            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(1, id);
            
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer une promo par ID
    public Promo chercherParId(int id) {
        String sql = "SELECT * FROM PROMO WHERE ID = ? AND STATUT != 'supprime'";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extrairePromo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister toutes les promos actives (pour l'admin)
    public List<Promo> listerToutesPromos() {
        List<Promo> liste = new ArrayList<>();
        String sql = "SELECT * FROM PROMO WHERE STATUT != 'supprime' ORDER BY ORDRE_AFFICHAGE ASC, CREATION_DATE DESC";
        
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                liste.add(extrairePromo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les promos pour le carrousel (actives et dans la période valide)
    public List<Promo> listerPromosPourCarrousel() {
        List<Promo> liste = new ArrayList<>();
        String sql = "SELECT * FROM PROMO WHERE STATUT = 'actif' " +
                     "AND (DATE_DEBUT IS NULL OR DATE_DEBUT <= NOW()) " +
                     "AND (DATE_FIN IS NULL OR DATE_FIN >= NOW()) " +
                     "ORDER BY ORDRE_AFFICHAGE ASC, CREATION_DATE DESC";
        
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                liste.add(extrairePromo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Méthode utilitaire pour extraire un Promo depuis ResultSet
    private Promo extrairePromo(ResultSet rs) throws SQLException {
        Promo promo = new Promo();
        promo.setId(rs.getInt("ID"));
        promo.setTitre(rs.getString("TITRE"));
        promo.setSousTitre(rs.getString("SOUS_TITRE"));
        promo.setDescription(rs.getString("DESCRIPTION"));
        promo.setTypeContenu(rs.getString("TYPE_CONTENU"));
        promo.setCheminMedia(rs.getString("CHEMIN_MEDIA"));
        
        // Convertir la chaîne de gradient en liste
        String gradientString = rs.getString("COULEURS_GRADIENT");
        promo.setCouleursGradientFromString(gradientString);
        
        promo.setTexteBouton(rs.getString("TEXTE_BOUTON"));
        promo.setRouteCible(rs.getString("ROUTE_CIBLE"));
        promo.setDureeVideo(rs.getString("DUREE_VIDEO"));
        promo.setVues(rs.getString("VUES"));
        promo.setOrdreAffichage(rs.getInt("ORDRE_AFFICHAGE"));
        promo.setStatut(rs.getString("STATUT"));
        
        Timestamp dateDebut = rs.getTimestamp("DATE_DEBUT");
        if (dateDebut != null) {
            promo.setDateDebut(dateDebut.toLocalDateTime());
        }
        
        Timestamp dateFin = rs.getTimestamp("DATE_FIN");
        if (dateFin != null) {
            promo.setDateFin(dateFin.toLocalDateTime());
        }
        
        promo.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        promo.setCreationDate(rs.getTimestamp("CREATION_DATE").toLocalDateTime());
        promo.setUpdateDate(rs.getTimestamp("UPDATE_DATE").toLocalDateTime());
        
        return promo;
    }

    // Mettre à jour l'ordre d'affichage
    public boolean mettreAJourOrdreAffichage(int id, int nouvelOrdre) {
        String sql = "UPDATE PROMO SET ORDRE_AFFICHAGE = ?, UPDATE_DATE = ? WHERE ID = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nouvelOrdre);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, id);
            
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}