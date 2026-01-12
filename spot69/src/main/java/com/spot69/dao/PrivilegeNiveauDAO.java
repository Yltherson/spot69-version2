package com.spot69.dao;

import com.spot69.model.PrivilegeNiveau;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PrivilegeNiveauDAO {

    private Connection getConnection() throws SQLException {																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																
        return DBConnection.getConnection();
    }

    // Ajouter un niveau de privilège
    public boolean ajouter(PrivilegeNiveau niveau) {
        String sql = "INSERT INTO PRIVILEGE_NIVEAU (NOM, SEUIL_POINTS, POURCENTAGE_REDUCTION, DESCRIPTION, " +
                     "COULEUR, STATUT, CREATED_BY) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, niveau.getNom());
            stmt.setInt(2, niveau.getSeuilPoints());
            stmt.setBigDecimal(3, niveau.getPourcentageReduction());
            stmt.setString(4, niveau.getDescription() != null ? niveau.getDescription() : "");
            stmt.setString(5, niveau.getCouleur());
            stmt.setString(6, niveau.getStatut());
            stmt.setObject(7, niveau.getCreatedBy());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier un niveau de privilège
    public boolean modifier(PrivilegeNiveau niveau) {
        String sql = "UPDATE PRIVILEGE_NIVEAU SET NOM = ?, SEUIL_POINTS = ?, POURCENTAGE_REDUCTION = ?, " +
                     "DESCRIPTION = ?, COULEUR = ?, STATUT = ?, UPDATED_BY = ?, UPDATED_AT = CURRENT_TIMESTAMP " +
                     "WHERE ID = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, niveau.getNom());
            stmt.setInt(2, niveau.getSeuilPoints());
            stmt.setBigDecimal(3, niveau.getPourcentageReduction());
            stmt.setString(4, niveau.getDescription());
            stmt.setString(5, niveau.getCouleur());
            stmt.setString(6, niveau.getStatut());
            stmt.setObject(7, niveau.getUpdatedBy());
            stmt.setInt(8, niveau.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer logiquement un niveau
    public boolean supprimer(int id) {
        String sql = "DELETE FROM PRIVILEGE_NIVEAU WHERE ID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
//            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Rechercher par ID
    public PrivilegeNiveau chercherParId(int id) {
        String sql = "SELECT * FROM PRIVILEGE_NIVEAU WHERE ID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireNiveau(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Rechercher par nom
    public PrivilegeNiveau chercherParNom(String nom) {
        String sql = "SELECT * FROM PRIVILEGE_NIVEAU WHERE NOM = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraireNiveau(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister tous les niveaux (actifs par défaut)
    public List<PrivilegeNiveau> listerTous() {
        List<PrivilegeNiveau> liste = new ArrayList<>();
        String sql = "SELECT * FROM PRIVILEGE_NIVEAU ORDER BY SEUIL_POINTS ASC, NOM ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(extraireNiveau(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Lister les niveaux actifs seulement
    public List<PrivilegeNiveau> listerActifs() {
        List<PrivilegeNiveau> liste = new ArrayList<>();
        String sql = "SELECT * FROM PRIVILEGE_NIVEAU WHERE STATUT = 'ACTIF' ORDER BY SEUIL_POINTS ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(extraireNiveau(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Compter les niveaux actifs
    public int compterActifs() {
        String sql = "SELECT COUNT(*) AS TOTAL FROM PRIVILEGE_NIVEAU WHERE STATUT = 'ACTIF'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public PrivilegeNiveau determinerNiveauUtilisateur(int points) {
        List<PrivilegeNiveau> niveaux = listerActifs();
        
        if (niveaux.isEmpty()) {
            return chercherParNom("CLASSIC");
        }
        
        // 1. Trier les niveaux par seuil croissant
        niveaux.sort(Comparator.comparingInt(PrivilegeNiveau::getSeuilPoints));
        
        System.out.println("=== DEBUG ===");
        System.out.println("Points de l'utilisateur : " + points);
        System.out.println("Niveaux triés :");
        for (int i = 0; i < niveaux.size(); i++) {
            PrivilegeNiveau niveau = niveaux.get(i);
            System.out.println("  " + niveau.getNom() + " (seuil: " + niveau.getSeuilPoints() + ")");
        }
        
        // 2. Parcourir les niveaux pour trouver le bon intervalle
        for (int i = 0; i < niveaux.size(); i++) {
            PrivilegeNiveau niveau = niveaux.get(i);
            int seuilMin = (i == 0) ? 0 : niveau.getSeuilPoints(); // Premier niveau commence à 0
            int seuilMax = Integer.MAX_VALUE;
            
            // Si ce n'est pas le dernier niveau, le seuil max est le seuil du niveau suivant - 1
            if (i < niveaux.size() - 1) {
                seuilMax = niveaux.get(i + 1).getSeuilPoints() - 1;
            }
            
            System.out.println("\nExamen du niveau : " + niveau.getNom());
            System.out.println("  Intervalle : [" + seuilMin + " - " + 
                             (seuilMax == Integer.MAX_VALUE ? "∞" : seuilMax) + "]");
            
            if (points >= seuilMin && points <= seuilMax) {
                System.out.println("  ✓ Points " + points + " dans l'intervalle");
                System.out.println("=== RESULTAT ===");
                System.out.println("Niveau déterminé : " + niveau.getNom());
                return niveau;
            } else {
                System.out.println("  ✗ Points hors intervalle");
            }
        }
        
        // 3. Si aucun niveau trouvé (ne devrait jamais arriver avec cette logique)
        System.out.println("\n=== RESULTAT ===");
        System.out.println("Aucun intervalle trouvé, retour BRONZE par défaut");
        return chercherParNom("CLASSIC");
    }

    // Vérifier si un nom de niveau existe déjà
    public boolean existeNom(String nom, int excludeId) {
        String sql = "SELECT COUNT(*) AS COUNT FROM PRIVILEGE_NIVEAU WHERE NOM = ? AND ID != ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("COUNT") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode utilitaire pour extraire un niveau depuis ResultSet
    private PrivilegeNiveau extraireNiveau(ResultSet rs) throws SQLException {
        PrivilegeNiveau niveau = new PrivilegeNiveau();
        
        niveau.setId(rs.getInt("ID"));
        niveau.setNom(rs.getString("NOM"));
        niveau.setSeuilPoints(rs.getInt("SEUIL_POINTS"));
        niveau.setPourcentageReduction(rs.getBigDecimal("POURCENTAGE_REDUCTION"));
        niveau.setDescription(rs.getString("DESCRIPTION"));
        niveau.setCouleur(rs.getString("COULEUR"));
        niveau.setStatut(rs.getString("STATUT"));
        niveau.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        niveau.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        niveau.setCreatedBy(rs.getObject("CREATED_BY") != null ? rs.getInt("CREATED_BY") : null);
        niveau.setUpdatedBy(rs.getObject("UPDATED_BY") != null ? rs.getInt("UPDATED_BY") : null);
        
        return niveau;
    }
}