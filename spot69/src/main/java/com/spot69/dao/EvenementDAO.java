package com.spot69.dao;

import com.spot69.model.Evenement;
import com.spot69.model.TypeTableEvenement;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

   // =====================================
    // Ajouter un événement (sans prix_ticket)
    // =====================================
    public void ajouter(Evenement event) {
        String sql = "INSERT INTO EVENEMENT (TITRE, ARTISTE_GROUPE, MEDIA_PATH, DATE_EVENT, " +
                     "DESCRIPTION, UTILISATEUR_ID, STATUT, CAPACITE_TOTALE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getTitre());
            stmt.setString(2, event.getArtisteGroupe());
            stmt.setString(3, event.getMediaPath());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getDateEvent()));
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getUtilisateurId());
            stmt.setString(7, event.getStatut() != null ? event.getStatut() : "VISIBLE");
            stmt.setInt(8, event.getCapaciteTotale()); // Nouvelle colonne

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de l'événement", e);
        }
    }
    
 // =====================================
 // Récupérer un événement avec ses tables
 // =====================================
 public Evenement getByIdWithTables(int id) {
     Evenement event = getById(id);
     if (event != null) {
         TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
         List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(id);
         event.setTypesTables(tables);
     }
     return event;
 }

 // =====================================
 // Récupérer tous les événements avec leurs tables
 // =====================================
 public List<Evenement> getAllWithTables() {
     List<Evenement> events = getAll();
     if (events != null && !events.isEmpty()) {
         TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
         for (Evenement event : events) {
             List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(event.getId());
             event.setTypesTables(tables);
         }
     }
     return events;
 }

 // =====================================
 // Récupérer les événements à venir avec tables
 // =====================================
 public List<Evenement> getUpcomingEventsWithTables(int limit) {
     List<Evenement> events = getUpcomingEvents(limit);
     if (events != null && !events.isEmpty()) {
         TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
         for (Evenement event : events) {
             List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(event.getId());
             event.setTypesTables(tables);
         }
     }
     return events;
 }

    // =====================================
    // Modifier un événement
    // =====================================
    public void modifier(Evenement event) {
        String sql = "UPDATE EVENEMENT SET TITRE = ?, ARTISTE_GROUPE = ?, MEDIA_PATH = ?, " +
                     "DATE_EVENT = ?, DESCRIPTION = ?, UTILISATEUR_ID = ?, STATUT = ?, " +
                     "CAPACITE_TOTALE = ?, UPDATED_AT = NOW() WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitre());
            stmt.setString(2, event.getArtisteGroupe());
            stmt.setString(3, event.getMediaPath());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getDateEvent()));
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getUtilisateurId());
            stmt.setString(7, event.getStatut());
            stmt.setInt(8, event.getCapaciteTotale());
            stmt.setInt(9, event.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification de l'événement", e);
        }
    }

    // =====================================
    // Récupérer un événement par ID
    // =====================================
    public Evenement getById(int id) {
        String sql = "SELECT * FROM EVENEMENT WHERE ID = ? AND STATUT != 'DELETED'";
        Evenement event = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                event = buildEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event;
    }

    // =====================================
    // Helper pour construire l'objet
    // =====================================
    private Evenement buildEvent(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
        e.setId(rs.getInt("ID"));
        e.setTitre(rs.getString("TITRE"));
        e.setArtisteGroupe(rs.getString("ARTISTE_GROUPE"));
        e.setMediaPath(rs.getString("MEDIA_PATH"));
        e.setDateEvent(rs.getTimestamp("DATE_EVENT").toLocalDateTime());
        e.setDescription(rs.getString("DESCRIPTION"));
        e.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
        e.setCapaciteTotale(rs.getInt("CAPACITE_TOTALE")); // Nouveau
        
        // Gérer les dates qui peuvent être null
        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            e.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UPDATED_AT");
        if (updatedAt != null) {
            e.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // DELETED_BY peut être null
        int deletedBy = rs.getInt("DELETED_BY");
        if (!rs.wasNull()) {
            e.setDeletedBy(deletedBy);
        }
        
        e.setStatut(rs.getString("STATUT"));
        
        return e;
    }

    // =====================================
    // Mettre à jour la capacité totale
    // =====================================
    public void mettreAJourCapaciteTotale(int evenementId) {
        TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
        int capaciteTotale = typeTableDAO.calculerCapaciteTotale(evenementId);
        
        String sql = "UPDATE EVENEMENT SET CAPACITE_TOTALE = ? WHERE ID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, capaciteTotale);
            stmt.setInt(2, evenementId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // =====================================
    // Modifier uniquement le média (si besoin)
    // =====================================
    public void modifierMedia(int id, String mediaPath) {
        String sql = "UPDATE EVENEMENT SET MEDIA_PATH = ?, UPDATED_AT = NOW() WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mediaPath);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la modification du média pour l'événement ID: " + id);
            e.printStackTrace();
        }
    }

    // =====================================
    // Supprimer (logique)
    // =====================================
    public void supprimer(int id, int deleteUser) {
        String sql = "UPDATE EVENEMENT SET STATUT = 'DELETED', DELETED_BY = ?, UPDATED_AT = NOW() WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deleteUser);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(">>> [DEBUG] Événement supprimé (logique), ID: " + id + ", lignes affectées: " + rowsAffected);
            
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la suppression de l'événement ID: " + id);
            e.printStackTrace();
        }
    }

    // =====================================
    // Désactiver (Hidden)
    // =====================================
    public void desactiver(int id) {
        String sql = "UPDATE EVENEMENT SET STATUT = 'HIDDEN', UPDATED_AT = NOW() WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(">>> [DEBUG] Événement désactivé, ID: " + id + ", lignes affectées: " + rowsAffected);
            
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la désactivation de l'événement ID: " + id);
            e.printStackTrace();
        }
    }

    // =====================================
    // Réactiver (Visible)
    // =====================================
    public void reactiver(int id) {
        String sql = "UPDATE EVENEMENT SET STATUT = 'VISIBLE', UPDATED_AT = NOW() WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(">>> [DEBUG] Événement réactivé, ID: " + id + ", lignes affectées: " + rowsAffected);
            
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la réactivation de l'événement ID: " + id);
            e.printStackTrace();
        }
    }



    // =====================================
    // Lister tous les événements visibles
    // =====================================
    public List<Evenement> getAll() {
        List<Evenement> liste = new ArrayList<>();
        String sql = "SELECT * FROM EVENEMENT WHERE STATUT = 'VISIBLE' ORDER BY DATE_EVENT DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(buildEvent(rs));
            }
            System.out.println(">>> [DEBUG] Nombre d'événements récupérés: " + liste.size());
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la récupération de tous les événements");
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================
    // Lister tous les événements (admin)
    // =====================================
    public List<Evenement> getAllForAdmin() {
        List<Evenement> liste = new ArrayList<>();
        String sql = "SELECT * FROM EVENEMENT WHERE STATUT != 'DELETED' ORDER BY DATE_EVENT DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(buildEvent(rs));
            }
            System.out.println(">>> [DEBUG] Nombre d'événements (admin) récupérés: " + liste.size());
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la récupération de tous les événements (admin)");
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================
    // Récupérer les événements à venir
    // =====================================
    public List<Evenement> getUpcomingEvents(int limit) {
        List<Evenement> liste = new ArrayList<>();
        String sql = "SELECT * FROM EVENEMENT WHERE STATUT = 'VISIBLE' AND DATE_EVENT >= NOW() " +
                     "ORDER BY DATE_EVENT ASC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                liste.add(buildEvent(rs));
            }
            System.out.println(">>> [DEBUG] Événements à venir récupérés: " + liste.size());
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la récupération des événements à venir");
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================
    // Récupérer les événements par utilisateur
    // =====================================
    public List<Evenement> getByUserId(int userId) {
        List<Evenement> liste = new ArrayList<>();
        String sql = "SELECT * FROM EVENEMENT WHERE UTILISATEUR_ID = ? AND STATUT != 'DELETED' ORDER BY DATE_EVENT DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                liste.add(buildEvent(rs));
            }
            System.out.println(">>> [DEBUG] Événements pour l'utilisateur " + userId + ": " + liste.size());
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la récupération des événements pour l'utilisateur: " + userId);
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================
    // Helper pour construire l'objet
    // =====================================
//    private Evenement buildEvent(ResultSet rs) throws SQLException {
//        Evenement e = new Evenement();
//        e.setId(rs.getInt("ID"));
//        e.setTitre(rs.getString("TITRE"));
//        e.setArtisteGroupe(rs.getString("ARTISTE_GROUPE"));
//        
//        // Récupérer MEDIA_PATH (peut être null)
//        String mediaPath = rs.getString("MEDIA_PATH");
//        e.setMediaPath(mediaPath);
//        
//        e.setDateEvent(rs.getTimestamp("DATE_EVENT").toLocalDateTime());
//        e.setDescription(rs.getString("DESCRIPTION"));
//        e.setUtilisateurId(rs.getInt("UTILISATEUR_ID"));
//        
//        // Gérer les dates qui peuvent être null
//        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
//        if (createdAt != null) {
//            e.setCreatedAt(createdAt.toLocalDateTime());
//        }
//        
//        Timestamp updatedAt = rs.getTimestamp("UPDATED_AT");
//        if (updatedAt != null) {
//            e.setUpdatedAt(updatedAt.toLocalDateTime());
//        }
//        
//        // DELETED_BY peut être null
//        int deletedBy = rs.getInt("DELETED_BY");
//        if (!rs.wasNull()) {
//            e.setDeletedBy(deletedBy);
//        }
//        
//        e.setStatut(rs.getString("STATUT"));
//        
//        // Debug
//        System.out.println(">>> [DEBUG buildEvent] ID: " + e.getId() + 
//                         ", Titre: " + e.getTitre() + 
//                         ", Media: " + (e.hasMedia() ? "Oui" : "Non"));
//        
//        return e;
//    }

    // =====================================
    // Vérifier si un titre existe déjà
    // =====================================
    public boolean titreExists(String titre) {
        String sql = "SELECT COUNT(*) FROM EVENEMENT WHERE TITRE = ? AND STATUT != 'DELETED'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors de la vérification du titre: " + titre);
            e.printStackTrace();
        }
        
        return false;
    }
    
 // =====================================
 // Récupérer les 2 derniers événements
 // =====================================
 public List<Evenement> getLastTwo() {
     List<Evenement> liste = new ArrayList<>();
     String sql = "SELECT * FROM EVENEMENT WHERE STATUT = 'VISIBLE' ORDER BY DATE_EVENT DESC LIMIT 2";

     try (Connection conn = getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql);
          ResultSet rs = stmt.executeQuery()) {

         while (rs.next()) {
             liste.add(buildEvent(rs));
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }

     return liste;
 }


    // =====================================
    // Compter le nombre d'événements
    // =====================================
    public int countEvents() {
        String sql = "SELECT COUNT(*) FROM EVENEMENT WHERE STATUT = 'VISIBLE'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(">>> [ERROR] Erreur lors du comptage des événements");
            e.printStackTrace();
        }
        
        return 0;
    }
}