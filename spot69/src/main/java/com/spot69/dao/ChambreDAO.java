package com.spot69.dao;

import com.spot69.model.Chambre;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChambreDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // Ajouter une chambre
    public boolean ajouterChambre(Chambre chambre) {
        String sql = "INSERT INTO CHAMBRES (NOM_CHAMBRE, DESCRIPTION_CHAMBRE, CAPACITE, INSTALLATIONS, " +
                     "PRIX_MOMENT, PRIX_NUIT, PRIX_JOUR, PRIX_SEJOUR, DISPONIBLE, MEDIA, UTILISATEUR_ID, CREATED_AT, UPDATED_AT, STATUT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'VISIBLE')";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chambre.getNomChambre());
            stmt.setString(2, chambre.getDescriptionChambre() != null ? chambre.getDescriptionChambre() : "");
            stmt.setInt(3, chambre.getCapacite());
            stmt.setString(4, chambre.getInstallations() != null ? chambre.getInstallations() : "");
            stmt.setBigDecimal(5, chambre.getPrixMoment());
            stmt.setBigDecimal(6, chambre.getPrixNuit());
            stmt.setBigDecimal(7, chambre.getPrixJour());
            stmt.setBigDecimal(8, chambre.getPrixSejour());
            stmt.setBoolean(9, chambre.isDisponible());
            stmt.setString(10, chambre.getMedia() != null ? chambre.getMedia() : "");
            stmt.setInt(11, chambre.getUtilisateurId());

            Timestamp now = new Timestamp(new Date().getTime());
            stmt.setTimestamp(12, now);
            stmt.setTimestamp(13, now);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Modifier une chambre
    public boolean modifierChambre(Chambre chambre) {
        String sql = "UPDATE CHAMBRES SET NOM_CHAMBRE = ?, DESCRIPTION_CHAMBRE = ?, CAPACITE = ?, " +
                     "INSTALLATIONS = ?, PRIX_MOMENT = ?, PRIX_NUIT = ?, PRIX_JOUR = ?, PRIX_SEJOUR = ?, " +
                     "DISPONIBLE = ?, MEDIA = ?, UTILISATEUR_ID = ?, UPDATED_AT = ? WHERE ID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chambre.getNomChambre());
            stmt.setString(2, chambre.getDescriptionChambre());
            stmt.setInt(3, chambre.getCapacite());
            stmt.setString(4, chambre.getInstallations());
            stmt.setBigDecimal(5, chambre.getPrixMoment());
            stmt.setBigDecimal(6, chambre.getPrixNuit());
            stmt.setBigDecimal(7, chambre.getPrixJour());
            stmt.setBigDecimal(8, chambre.getPrixSejour());
            stmt.setBoolean(9, chambre.isDisponible());
            stmt.setString(10, chambre.getMedia());
            stmt.setInt(11, chambre.getUtilisateurId());
            stmt.setTimestamp(12, new Timestamp(new Date().getTime()));
            stmt.setInt(13, chambre.getId());

            int rows = stmt.executeUpdate();
            System.out.println("[INFO] Chambre mise à jour : " + chambre.getId() + " | Lignes affectées = " + rows);

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("[ERREUR] Échec lors de la mise à jour de la chambre ID=" + chambre.getId());
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une chambre (statut = 'DELETED' + deleted_by)
    public boolean supprimerChambre(int id, int deletedBy) {
        String sql = "UPDATE CHAMBRES SET STATUT = 'DELETED', UPDATED_AT = ?, DELETED_BY = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.setInt(2, deletedBy);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Rechercher une chambre par ID
    public Chambre chercherParId(int id) {
        String sql = "SELECT * FROM CHAMBRES WHERE ID = ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireChambre(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister toutes les chambres
//    public List<Chambre> listerChambres() {
//        List<Chambre> liste = new ArrayList<>();
//        String sql = "SELECT * FROM CHAMBRES WHERE STATUT = 'VISIBLE' ORDER BY CREATED_AT DESC";
//
//        try (Connection conn = getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                liste.add(extraireChambre(rs));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return liste;
//    }
 // Dans ChambreDAO.java

 // Lister toutes les chambres avec leurs périodes réservées
 public List<Chambre> listerChambres() {
     List<Chambre> liste = new ArrayList<>();
     
     // Requête pour récupérer les chambres
     String sqlChambres = "SELECT * FROM CHAMBRES WHERE STATUT = 'VISIBLE' ORDER BY CREATED_AT DESC";
     
     // Requête pour récupérer les réservations actives par chambre
     String sqlReservations = "SELECT TYPE, START_DATE, END_DATE FROM RESERVATIONS " +
                             "WHERE ROOM_ID = ? AND STATUS IN ('confirmé', 'en cours') " +
                             "AND END_DATE >= CURDATE() " +
                             "ORDER BY START_DATE";

     try (Connection conn = getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sqlChambres)) {

         while (rs.next()) {
             Chambre chambre = extraireChambre(rs);
             
             // Récupérer les réservations pour cette chambre
             List<String> momentsReserves = new ArrayList<>();
             List<String> nuitsReserves = new ArrayList<>();
             List<String> joursReserves = new ArrayList<>();
             List<String> sejoursReserves = new ArrayList<>();
             
             try (PreparedStatement pstmtResa = conn.prepareStatement(sqlReservations)) {
                 pstmtResa.setString(1, String.valueOf(chambre.getId()));
                 ResultSet rsResa = pstmtResa.executeQuery();
                 
                 while (rsResa.next()) {
                     String type = rsResa.getString("TYPE");
                     Date startDate = rsResa.getTimestamp("START_DATE");
                     Date endDate = rsResa.getTimestamp("END_DATE");
                     
                     // Formater selon le type
                     String periodeFormatee = formaterPeriode(type, startDate, endDate);
                     
                     switch(type.toLowerCase()) {
                         case "moment":
                             momentsReserves.add(periodeFormatee);
                             break;
                         case "nuit":
                             nuitsReserves.add(periodeFormatee);
                             break;
                         case "jour":
                             joursReserves.add(periodeFormatee);
                             break;
                         case "sejour":
                             sejoursReserves.add(periodeFormatee);
                             break;
                     }
                 }
                 
                 // Ajouter les listes à la chambre
                 // Note: Vous devrez ajouter ces champs à votre modèle Chambre
                 chambre.setMomentsReserves(momentsReserves);
                 chambre.setNuitsReserves(nuitsReserves);
                 chambre.setJoursReserves(joursReserves);
                 chambre.setSejoursReserves(sejoursReserves);
                 
             } catch (SQLException e) {
                 System.out.println("Erreur lors de la récupération des réservations pour la chambre " + chambre.getId());
                 e.printStackTrace();
             }
             
             liste.add(chambre);
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }

     return liste;
 }

 // Méthode utilitaire pour formater les périodes
//Méthode utilitaire pour formater les périodes
private String formaterPeriode(String type, Date start, Date end) {
  SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
  SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  switch (type.toLowerCase()) {

      case "moment":
          return dateTimeFormat.format(start) + " - " + dateTimeFormat.format(end);

      case "nuit":
          return "Nuit du " + dateFormat.format(start);

      case "jour":
          return "Journée du " + dateFormat.format(start);

      case "sejour":
          if (start.equals(end)) {
              return dateFormat.format(start);
          } else {
              return dateFormat.format(start) + " au " + dateFormat.format(end);
          }

      default:
          return dateFormat.format(start);
  }
}


    // Rechercher des chambres par nom (LIKE)
    public List<Chambre> chercherParNom(String motCle) {
        List<Chambre> liste = new ArrayList<>();
        String sql = "SELECT * FROM CHAMBRES WHERE NOM_CHAMBRE LIKE ? AND STATUT = 'VISIBLE'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + motCle + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(extraireChambre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les chambres disponibles
    public List<Chambre> listerChambresDisponibles() {
        List<Chambre> liste = new ArrayList<>();
        String sql = "SELECT * FROM CHAMBRES WHERE DISPONIBLE = true AND STATUT = 'VISIBLE' ORDER BY PRIX ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(extraireChambre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }

    // Compter le nombre de chambres disponibles
    public int compterChambresDisponibles() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS NB_DISPONIBLES FROM CHAMBRES WHERE DISPONIBLE = true AND STATUT = 'VISIBLE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("NB_DISPONIBLES");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Compter le nombre total de chambres
    public int compterTotalChambres() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS TOTAL FROM CHAMBRES WHERE STATUT = 'VISIBLE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Méthode utilitaire pour extraire une chambre depuis ResultSet
    private Chambre extraireChambre(ResultSet rs) throws SQLException {
        Chambre chambre = new Chambre();
        chambre.setId(rs.getInt("ID"));
        chambre.setNomChambre(rs.getString("NOM_CHAMBRE"));
        chambre.setDescriptionChambre(rs.getString("DESCRIPTION_CHAMBRE"));
        chambre.setCapacite(rs.getInt("CAPACITE"));
        chambre.setInstallations(rs.getString("INSTALLATIONS"));
        chambre.setPrixMoment(rs.getBigDecimal("PRIX_MOMENT"));
        chambre.setPrixNuit(rs.getBigDecimal("PRIX_NUIT"));
        chambre.setPrixJour(rs.getBigDecimal("PRIX_JOUR"));
        chambre.setPrixSejour(rs.getBigDecimal("PRIX_SEJOUR"));
        chambre.setDisponible(rs.getBoolean("DISPONIBLE"));
        chambre.setMedia(rs.getString("MEDIA"));
        chambre.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        chambre.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        chambre.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        chambre.setDeletedBy(rs.getObject("DELETED_BY") != null ? rs.getInt("DELETED_BY") : null);
        chambre.setStatut(rs.getString("STATUT"));
        return chambre;
    }

}