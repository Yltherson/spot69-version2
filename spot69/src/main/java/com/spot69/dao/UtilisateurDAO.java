package com.spot69.dao;

import com.spot69.model.PrivilegeNiveau;
import com.spot69.model.Role;
import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    /**
     * Cherche un utilisateur par login ou email
     */
	
    public Utilisateur findByLoginOrEmail(String loginOrEmail) {
        Utilisateur user = null;
        String sql = "SELECT ID, NOM, PRENOM, EMAIL, LOGIN, MOT_DE_PASSE, STATUT, " +
                     "POURCENTAGE, PLAFOND, POINT, PRIVILLEGE, CREATION_DATE, UPDATE_DATE, " +
                     "DATE_NAISSANCE, TELEPHONE, ADRESSE, ID_ROLE " +  // Ajouté DATE_NAISSANCE, TELEPHONE, ADRESSE
//                     "FROM UTILISATEUR WHERE LOGIN = ? OR EMAIL = ? AND STATUT = 'VISIBLE'";
					"FROM UTILISATEUR WHERE LOGIN = ? AND STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginOrEmail);
//            stmt.setString(2, loginOrEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur();
                    user.setId(rs.getInt("ID"));
                    user.setNom(rs.getString("NOM"));
                    user.setPrenom(rs.getString("PRENOM"));
                    user.setEmail(rs.getString("EMAIL"));
                    user.setLogin(rs.getString("LOGIN"));
                    user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    user.setStatut(rs.getString("STATUT"));
                    user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                    user.setPlafond(rs.getInt("PLAFOND"));
                    user.setPoint(rs.getInt("POINT"));
                    user.setPrivilege(rs.getString("PRIVILLEGE"));
                    user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
                    user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                    user.setDateNaissance(rs.getDate("DATE_NAISSANCE")); // Nouveau
                    user.setTelephone(rs.getString("TELEPHONE"));
                    user.setAdresse(rs.getString("ADRESSE"));

                    int roleId = rs.getInt("ID_ROLE");
                    Role role = RoleDAO.findById(roleId);
                    user.setRole(role);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    
    public List<Utilisateur> searchClients(String searchTerm) {
	    List<Utilisateur> clients = new ArrayList<>();
	    
	    // Modifier la requête pour rechercher les clients (rôle CLIENT)
	    String sql = "SELECT u.*, " +
	                 "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
	                 "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
	                 "FROM UTILISATEUR u " +
	                 "JOIN ROLE r ON u.ID_ROLE = r.ID " +
	                 "WHERE u.STATUT = 'VISIBLE' " +
	                 "AND r.STATUT = 'VISIBLE' " +
	                 "AND r.NOM_ROLE LIKE '%CLIENT%' " +
	                 "AND (u.NOM LIKE ? OR u.PRENOM LIKE ? OR u.EMAIL LIKE ? OR u.LOGIN LIKE ?) " +
	                 "ORDER BY u.NOM, u.PRENOM";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        // Préparer le terme de recherche avec wildcards
	        String likeTerm = "%" + searchTerm + "%";
	        stmt.setString(1, likeTerm);
	        stmt.setString(2, likeTerm);
	        stmt.setString(3, likeTerm);
	        stmt.setString(4, likeTerm);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Utilisateur u = extractUserFromResultSet(rs);
	                
	                // Créer l'objet Role
	                Role role = new Role();
	                role.setId(rs.getInt("ROLE_ID"));
	                role.setRoleName(rs.getString("NOM_ROLE"));
	                role.setDroits(rs.getString("DROITS"));
	                role.setStatut(rs.getString("ROLE_STATUT"));
	                role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
	                role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));
	                
	                u.setRole(role);
	                clients.add(u);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return clients;
	}
    
 // Dans la méthode findByLoginOrEmail, ajoutez dateNaissance
//    public Utilisateur findByLoginOrEmail(String loginOrEmail) {
//        Utilisateur user = null;
//        String sql = "SELECT ID, NOM, PRENOM, EMAIL, LOGIN, MOT_DE_PASSE, STATUT, " +
//                     "POURCENTAGE, PLAFOND, POINT, PRIVILLEGE, CREATION_DATE, UPDATE_DATE, " +
//                     "DATE_NAISSANCE, ID_ROLE " +  // Ajouté DATE_NAISSANCE
//                     "FROM UTILISATEUR WHERE LOGIN = ? OR EMAIL = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, loginOrEmail);
//            stmt.setString(2, loginOrEmail);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    user = new Utilisateur();
//                    user.setId(rs.getInt("ID"));
//                    user.setNom(rs.getString("NOM"));
//                    user.setPrenom(rs.getString("PRENOM"));
//                    user.setEmail(rs.getString("EMAIL"));
//                    user.setLogin(rs.getString("LOGIN"));
//                    user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
//                    user.setStatut(rs.getString("STATUT"));
//                    user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
//                    user.setPlafond(rs.getInt("PLAFOND"));
//                    user.setPoint(rs.getInt("POINT"));
//                    user.setPrivilege(rs.getString("PRIVILLEGE"));
//                    user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
//                    user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
//                    user.setDateNaissance(rs.getDate("DATE_NAISSANCE")); // Nouveau
//
//                    int roleId = rs.getInt("ID_ROLE");
//                    Role role = RoleDAO.findById(roleId);
//                    user.setRole(role);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return user;
//    }

    // Modifiez la méthode insert pour inclure la date de naissance
    public boolean insert(Utilisateur user, String telephone, String adresse, Date dateNaissance) {
        String sql = "INSERT INTO UTILISATEUR (NOM, PRENOM, EMAIL, LOGIN, MOT_DE_PASSE, " +
                     "ID_ROLE, POURCENTAGE, TELEPHONE, ADRESSE, DATE_NAISSANCE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());

            // Email nullable
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, user.getEmail().trim());
            }

            stmt.setString(4, user.getLogin());
            stmt.setString(5, user.getMotDePasse());
            stmt.setInt(6, user.getRole().getId());
            stmt.setBigDecimal(7, user.getPourcentage());
            
            // Telephone nullable
            if (telephone == null || telephone.trim().isEmpty()) {
                stmt.setNull(8, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(8, telephone.trim());
            }
            
            // Adresse nullable
            if (adresse == null || adresse.trim().isEmpty()) {
                stmt.setNull(9, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(9, adresse.trim());
            }
            
            // Date de naissance nullable
            if (dateNaissance == null) {
                stmt.setNull(10, java.sql.Types.DATE);
            } else {
                stmt.setDate(10, dateNaissance);
            }

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de l'insertion de l'utilisateur");
            System.out.println("Nom=" + user.getNom() + ", Login=" + user.getLogin() + 
                             ", Email=" + user.getEmail());
            System.out.println("Téléphone=" + telephone + ", Adresse=" + adresse + 
                             ", DateNaissance=" + dateNaissance);
            e.printStackTrace();
        }

        return false;
    }
    
 // Méthode extractUserFromResultSet - ajoutez dateNaissance
    private Utilisateur extractUserFromResultSet(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("ID"));
        user.setNom(rs.getString("NOM"));
        user.setPrenom(rs.getString("PRENOM"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
        user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
        user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
        user.setStatut(rs.getString("STATUT"));
        user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
        user.setPlafond(rs.getInt("PLAFOND"));
        user.setTelephone(rs.getString("TELEPHONE"));
        user.setAdresse(rs.getString("ADRESSE"));
        user.setPoint(rs.getInt("POINT"));
        user.setPrivilege(rs.getString("PRIVILLEGE"));
        user.setDateNaissance(rs.getDate("DATE_NAISSANCE")); // Nouveau

        int roleId = rs.getInt("ID_ROLE");
        Role role = RoleDAO.findById(roleId);
        user.setRole(role);

        return user;
    }


    public Utilisateur findByIdWithPermissions(int id) {
        Utilisateur user = null;
        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, " 
                   + "u.STATUT, u.CREATION_DATE, u.UPDATE_DATE, u.POURCENTAGE, u.PLAFOND,"
                   + "u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " // Ajouté DATE_NAISSANCE
                   + "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT,"
                   + "r.CREATED_AT, r.UPDATED_AT "
                   + "FROM UTILISATEUR u "
                   + "LEFT JOIN ROLE r ON u.ID_ROLE = r.ID "
                   + "WHERE u.ID = ? AND u.STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur();
                    user.setId(rs.getInt("ID"));
                    user.setNom(rs.getString("NOM"));
                    user.setPrenom(rs.getString("PRENOM"));
                    user.setEmail(rs.getString("EMAIL"));
                    user.setLogin(rs.getString("LOGIN"));
                    user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    user.setStatut(rs.getString("STATUT"));
                    user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
                    user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                    user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                    user.setPlafond(rs.getInt("PLAFOND"));
                    user.setTelephone(rs.getString("TELEPHONE"));
                    user.setAdresse(rs.getString("ADRESSE"));
                    user.setPoint(rs.getInt("POINT"));
                    user.setPrivilege(rs.getString("PRIVILLEGE"));
                    user.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                    int roleId = rs.getInt("ROLE_ID");
                    if (!rs.wasNull()) {
                        Role role = new Role();
                        role.setId(roleId);
                        role.setRoleName(rs.getString("NOM_ROLE"));
                        role.setDroits(rs.getString("DROITS") != null ? rs.getString("DROITS") : "");
                        role.setStatut(rs.getString("ROLE_STATUT"));
                        role.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                        role.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
                        user.setRole(role);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<Utilisateur> findByRole(String roleName) {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, "
                   + "u.STATUT, u.CREATION_DATE, u.UPDATE_DATE, u.POURCENTAGE, u.PLAFOND, "
                   + "u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " // Ajouté DATE_NAISSANCE
                   + "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, "
                   + "r.CREATED_AT, r.UPDATED_AT "
                   + "FROM UTILISATEUR u "
                   + "JOIN ROLE r ON u.ID_ROLE = r.ID "
                   + "WHERE r.NOM_ROLE LIKE ? AND u.STATUT = 'VISIBLE' AND r.STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + roleName + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Utilisateur user = new Utilisateur();
                    user.setId(rs.getInt("ID"));
                    user.setNom(rs.getString("NOM"));
                    user.setPrenom(rs.getString("PRENOM"));
                    user.setEmail(rs.getString("EMAIL"));
                    user.setLogin(rs.getString("LOGIN"));
                    user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    user.setStatut(rs.getString("STATUT"));
                    user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
                    user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                    user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                    user.setPlafond(rs.getInt("PLAFOND"));
                    user.setTelephone(rs.getString("TELEPHONE"));
                    user.setAdresse(rs.getString("ADRESSE"));
                    user.setPoint(rs.getInt("POINT"));
                    user.setPrivilege(rs.getString("PRIVILLEGE"));
                    user.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                    Role role = new Role();
                    role.setId(rs.getInt("ROLE_ID"));
                    role.setRoleName(rs.getString("NOM_ROLE"));
                    role.setDroits(rs.getString("DROITS"));
                    role.setStatut(rs.getString("ROLE_STATUT"));
                    role.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    role.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

                    user.setRole(role);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean delete(int id) {
        String sql = "UPDATE UTILISATEUR SET STATUT = 'DELETED', UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Utilisateur> findAllVisible() {
        List<Utilisateur> list = new ArrayList<>();

        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, u.PLAFOND, " +
                "u.STATUT, u.CREATION_DATE AS USER_CREATION_DATE, u.UPDATE_DATE AS USER_UPDATE_DATE, " +
                "u.POURCENTAGE, u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " + // Ajouté DATE_NAISSANCE
                "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
                "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
                "FROM UTILISATEUR u " +
                "JOIN ROLE r ON u.ID_ROLE = r.ID " +
                "WHERE u.STATUT = 'VISIBLE' " +
                "AND r.STATUT = 'VISIBLE' " +
                "AND r.NOM_ROLE NOT IN ('ADMINISTRATEUR', 'SUPER_ADMINISTRATEUR') " +
                "ORDER BY u.CREATION_DATE DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("ID"));
                u.setNom(rs.getString("NOM"));
                u.setPrenom(rs.getString("PRENOM"));
                u.setEmail(rs.getString("EMAIL"));
                u.setLogin(rs.getString("LOGIN"));
                u.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                u.setStatut(rs.getString("STATUT"));
                u.setCreationDate(rs.getTimestamp("USER_CREATION_DATE"));
                u.setUpdateDate(rs.getTimestamp("USER_UPDATE_DATE"));
                u.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                u.setPlafond(rs.getInt("PLAFOND"));
                u.setTelephone(rs.getString("TELEPHONE"));
                u.setAdresse(rs.getString("ADRESSE"));
                u.setPoint(rs.getInt("POINT"));
                u.setPrivilege(rs.getString("PRIVILLEGE"));
                u.setDateNaissance(rs.getDate("DATE_NAISSANCE"));
                
                Role role = new Role();
                role.setId(rs.getInt("ROLE_ID"));
                role.setRoleName(rs.getString("NOM_ROLE"));
                role.setDroits(rs.getString("DROITS"));
                role.setStatut(rs.getString("ROLE_STATUT"));
                role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
                role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));

                u.setRole(role);
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Utilisateur> findAllUserData() {
        List<Utilisateur> list = new ArrayList<>();

        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, u.PLAFOND, " +
                "u.STATUT, u.CREATION_DATE AS USER_CREATION_DATE, u.UPDATE_DATE AS USER_UPDATE_DATE, " +
                "u.POURCENTAGE, u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " + // Ajouté DATE_NAISSANCE
                "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
                "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
                "FROM UTILISATEUR u " +
                "JOIN ROLE r ON u.ID_ROLE = r.ID " +
                "WHERE u.STATUT = 'VISIBLE' " +
                "AND r.STATUT = 'VISIBLE' " +
                "ORDER BY u.CREATION_DATE DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("ID"));
                u.setNom(rs.getString("NOM"));
                u.setPrenom(rs.getString("PRENOM"));
                u.setEmail(rs.getString("EMAIL"));
                u.setLogin(rs.getString("LOGIN"));
                u.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                u.setStatut(rs.getString("STATUT"));
                u.setCreationDate(rs.getTimestamp("USER_CREATION_DATE"));
                u.setUpdateDate(rs.getTimestamp("USER_UPDATE_DATE"));
                u.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                u.setPlafond(rs.getInt("PLAFOND"));
                u.setTelephone(rs.getString("TELEPHONE"));
                u.setAdresse(rs.getString("ADRESSE"));
                u.setPoint(rs.getInt("POINT"));
                u.setPrivilege(rs.getString("PRIVILLEGE"));
                u.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                Role role = new Role();
                role.setId(rs.getInt("ROLE_ID"));
                role.setRoleName(rs.getString("NOM_ROLE"));
                role.setDroits(rs.getString("DROITS"));
                role.setStatut(rs.getString("ROLE_STATUT"));
                role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
                role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));

                u.setRole(role);
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Utilisateur> findAllVisibleByRole(String roleName) {
        List<Utilisateur> list = new ArrayList<>();

        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, u.PLAFOND, " +
                     "u.STATUT, u.CREATION_DATE AS USER_CREATION_DATE, u.UPDATE_DATE AS USER_UPDATE_DATE, " +
                     "u.POURCENTAGE, u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " + // Ajouté DATE_NAISSANCE
                     "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
                     "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
                     "FROM UTILISATEUR u " +
                     "JOIN ROLE r ON u.ID_ROLE = r.ID " +
                     "WHERE u.STATUT = 'VISIBLE' " +
                     "AND r.STATUT = 'VISIBLE' " +
                     "AND r.NOM_ROLE = ? " +
                     "ORDER BY u.CREATION_DATE DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("ID"));
                    u.setNom(rs.getString("NOM"));
                    u.setPrenom(rs.getString("PRENOM"));
                    u.setEmail(rs.getString("EMAIL"));
                    u.setLogin(rs.getString("LOGIN"));
                    u.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    u.setStatut(rs.getString("STATUT"));
                    u.setCreationDate(rs.getTimestamp("USER_CREATION_DATE"));
                    u.setUpdateDate(rs.getTimestamp("USER_UPDATE_DATE"));
                    u.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                    u.setPlafond(rs.getInt("PLAFOND"));
                    u.setTelephone(rs.getString("TELEPHONE"));
                    u.setAdresse(rs.getString("ADRESSE"));
                    u.setPoint(rs.getInt("POINT"));
                    u.setPrivilege(rs.getString("PRIVILLEGE"));
                    u.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                    Role role = new Role();
                    role.setId(rs.getInt("ROLE_ID"));
                    role.setRoleName(rs.getString("NOM_ROLE"));
                    role.setDroits(rs.getString("DROITS"));
                    role.setStatut(rs.getString("ROLE_STATUT"));
                    role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
                    role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));

                    u.setRole(role);
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Utilisateur> getUtilisateursParRole(int roleId) {
        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = "SELECT u.*, r.ID AS role_id, r.NOM_ROLE, r.DROITS, r.STATUT AS role_statut, " +
                     "r.CREATED_AT AS role_created_at, r.UPDATED_AT AS role_updated_at " +
                     "FROM utilisateur u " +
                     "JOIN role r ON u.ID_ROLE = r.ID " +
                     "WHERE r.ID = ? AND u.STATUT = 'VISIBLE'";

        try (Connection conn = DBConnection.getConnection();
       	     PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Role role = new Role(
                    rs.getInt("role_id"),
                    rs.getString("NOM_ROLE"),
                    rs.getString("DROITS"),
                    rs.getString("role_statut"),
                    rs.getTimestamp("role_created_at"),
                    rs.getTimestamp("role_updated_at")
                );

                // Utiliser le constructeur avec tous les champs
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("ID"));
                utilisateur.setNom(rs.getString("NOM"));
                utilisateur.setPrenom(rs.getString("PRENOM"));
                utilisateur.setEmail(rs.getString("EMAIL"));
                utilisateur.setLogin(rs.getString("LOGIN"));
                utilisateur.setRole(role);
                utilisateur.setStatut(rs.getString("STATUT"));
                utilisateur.setCreationDate(rs.getTimestamp("CREATION_DATE"));
                utilisateur.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
                utilisateur.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                utilisateur.setPlafond(rs.getInt("PLAFOND"));
                utilisateur.setTelephone(rs.getString("TELEPHONE"));
                utilisateur.setAdresse(rs.getString("ADRESSE"));
                utilisateur.setPoint(rs.getInt("POINT"));
                utilisateur.setPrivilege(rs.getString("PRIVILLEGE"));
                utilisateur.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                utilisateurs.add(utilisateur);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }

    /**
     * Insère un nouvel utilisateur avec date de naissance
     */
//    public boolean insert(Utilisateur user, String telephone, String adresse, java.sql.Date dateNaissance) {
//        String sql = "INSERT INTO UTILISATEUR (NOM, PRENOM, EMAIL, LOGIN, MOT_DE_PASSE, " +
//                     "ID_ROLE, POURCENTAGE, TELEPHONE, ADRESSE, DATE_NAISSANCE) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, user.getNom());
//            stmt.setString(2, user.getPrenom());
//
//            // Email nullable
//            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
//                stmt.setNull(3, java.sql.Types.VARCHAR);
//            } else {
//                stmt.setString(3, user.getEmail().trim());
//            }
//
//            stmt.setString(4, user.getLogin());
//            stmt.setString(5, user.getMotDePasse());
//            stmt.setInt(6, user.getRole().getId());
//            stmt.setBigDecimal(7, user.getPourcentage());
//            
//            // Telephone nullable
//            if (telephone == null || telephone.trim().isEmpty()) {
//                stmt.setNull(8, java.sql.Types.VARCHAR);
//            } else {
//                stmt.setString(8, telephone.trim());
//            }
//            
//            // Adresse nullable
//            if (adresse == null || adresse.trim().isEmpty()) {
//                stmt.setNull(9, java.sql.Types.VARCHAR);
//            } else {
//                stmt.setString(9, adresse.trim());
//            }
//            
//            // Date de naissance nullable - pas besoin de cast car c'est déjà java.sql.Date
//            if (dateNaissance == null) {
//                stmt.setNull(10, java.sql.Types.DATE);
//            } else {
//                stmt.setDate(10, dateNaissance);
//            }
//
//            int rows = stmt.executeUpdate();
//            return rows > 0;
//
//        } catch (SQLException e) {
//            System.out.println("Erreur SQL lors de l'insertion de l'utilisateur");
//            System.out.println("Nom=" + user.getNom() + ", Login=" + user.getLogin() + 
//                             ", Email=" + user.getEmail());
//            System.out.println("Téléphone=" + telephone + ", Adresse=" + adresse + 
//                             ", DateNaissance=" + dateNaissance);
//            e.printStackTrace();
//        }
//
//        return false;
//    }
    /**
     * Recherche par ID
     */
    public Utilisateur findById(int id) {
        Utilisateur user = null;
        String sql = "SELECT u.ID, u.NOM, u.PRENOM, u.EMAIL, u.LOGIN, u.MOT_DE_PASSE, u.PLAFOND, " +
                     "u.STATUT, u.CREATION_DATE AS USER_CREATION_DATE, u.UPDATE_DATE AS USER_UPDATE_DATE, " +
                     "u.POURCENTAGE, u.TELEPHONE, u.ADRESSE, u.POINT, u.PRIVILLEGE, u.DATE_NAISSANCE, " + // Ajouté DATE_NAISSANCE
                     "r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
                     "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
                     "FROM UTILISATEUR u " +
                     "JOIN ROLE r ON u.ID_ROLE = r.ID " +
                     "WHERE u.ID = ? " +
                     "AND u.STATUT = 'VISIBLE' " +
                     "AND r.STATUT = 'VISIBLE' ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur();
                    user.setId(rs.getInt("ID"));
                    user.setNom(rs.getString("NOM"));
                    user.setPrenom(rs.getString("PRENOM"));
                    user.setEmail(rs.getString("EMAIL"));
                    user.setLogin(rs.getString("LOGIN"));
                    user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                    user.setStatut(rs.getString("STATUT"));
                    user.setCreationDate(rs.getTimestamp("USER_CREATION_DATE"));
                    user.setUpdateDate(rs.getTimestamp("USER_UPDATE_DATE"));
                    user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
                    user.setPlafond(rs.getInt("PLAFOND"));
                    user.setTelephone(rs.getString("TELEPHONE"));
                    user.setAdresse(rs.getString("ADRESSE"));
                    user.setPoint(rs.getInt("POINT"));
                    user.setPrivilege(rs.getString("PRIVILLEGE"));
                    user.setDateNaissance(rs.getDate("DATE_NAISSANCE"));

                    Role role = new Role();
                    role.setId(rs.getInt("ROLE_ID"));
                    role.setRoleName(rs.getString("NOM_ROLE"));
                    role.setDroits(rs.getString("DROITS"));
                    role.setStatut(rs.getString("ROLE_STATUT"));
                    role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
                    role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));

                    user.setRole(role);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Mise à jour des infos utilisateur
     */
    public boolean update(Utilisateur user, boolean changePassword) {
        String sql;
        if (changePassword) {
            sql = "UPDATE UTILISATEUR SET NOM = ?, PRENOM = ?, EMAIL = ?, LOGIN = ?, MOT_DE_PASSE = ?, " +
                  "ID_ROLE = ?, POURCENTAGE = ?, PLAFOND = ?, TELEPHONE = ?, ADRESSE = ?, DATE_NAISSANCE = ?, " +
                  "STATUT = ?, UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
        } else {
            sql = "UPDATE UTILISATEUR SET NOM = ?, PRENOM = ?, EMAIL = ?, LOGIN = ?, " +
                  "ID_ROLE = ?, POURCENTAGE = ?, PLAFOND = ?, TELEPHONE = ?, ADRESSE = ?, DATE_NAISSANCE = ?, " +
                  "STATUT = ?, UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getLogin());

            int paramIndex = 5;
            
            if (changePassword) {
                stmt.setString(paramIndex++, user.getMotDePasse()); // hashé
            }
            
            stmt.setInt(paramIndex++, user.getRole().getId());
            stmt.setBigDecimal(paramIndex++, user.getPourcentage());
            
            if (user.getPlafond() != 0) {
                stmt.setInt(paramIndex++, user.getPlafond());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.INTEGER);
            }
            
            // Telephone
            if (user.getTelephone() != null && !user.getTelephone().trim().isEmpty()) {
                stmt.setString(paramIndex++, user.getTelephone().trim());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
            }
            
            // Adresse
            if (user.getAdresse() != null && !user.getAdresse().trim().isEmpty()) {
                stmt.setString(paramIndex++, user.getAdresse().trim());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
            }
            
            // Date de naissance
            if (user.getDateNaissance() != null) {
                stmt.setDate(paramIndex++, user.getDateNaissance());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.DATE);
            }
            
            // Statut (boaqer)
            if (user.getStatut() != null && !user.getStatut().trim().isEmpty()) {
                stmt.setString(paramIndex++, user.getStatut().trim());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
            }
            
            stmt.setInt(paramIndex, user.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de la mise à jour de l'utilisateur ID=" + user.getId());
            System.out.println("SQL = " + sql);
            System.out.println("Nom = " + user.getNom() + ", Email = " + user.getEmail() + ", Login = " + user.getLogin());
            if (user.getPlafond() != 0) System.out.println("Plafond = " + user.getPlafond());
            if (user.getStatut() != null) System.out.println("Statut = " + user.getStatut());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Méthode utilitaire pour créer un Utilisateur à partir du ResultSet
     */
//    private Utilisateur extractUserFromResultSet(ResultSet rs) throws SQLException {
//        Utilisateur user = new Utilisateur();
//        user.setId(rs.getInt("ID"));
//        user.setNom(rs.getString("NOM"));
//        user.setPrenom(rs.getString("PRENOM"));
//        user.setEmail(rs.getString("EMAIL"));
//        user.setLogin(rs.getString("LOGIN"));
//        user.setMotDePasse(rs.getString("MOT_DE_PASSE"));
//        user.setCreationDate(rs.getTimestamp("CREATION_DATE"));
//        user.setUpdateDate(rs.getTimestamp("UPDATE_DATE"));
//        user.setStatut(rs.getString("STATUT"));
//        user.setPourcentage(rs.getBigDecimal("POURCENTAGE"));
//        user.setPlafond(rs.getInt("PLAFOND"));
//        user.setTelephone(rs.getString("TELEPHONE"));
//        user.setAdresse(rs.getString("ADRESSE"));
//        user.setPoint(rs.getInt("POINT"));
//        user.setPrivilege(rs.getString("PRIVILLEGE"));
//        user.setDateNaissance(rs.getDate("DATE_NAISSANCE"));
//
//        int roleId = rs.getInt("ID_ROLE");
//        Role role = RoleDAO.findById(roleId);
//        user.setRole(role);
//
//        return user;
//    }
    
    public List<Utilisateur> findAllVisibleByRoleClient(String roleName) {
        List<Utilisateur> list = new ArrayList<>();

        String sql = "SELECT u.*, r.ID AS ROLE_ID, r.NOM_ROLE, r.DROITS, r.STATUT AS ROLE_STATUT, " +
                     "r.CREATED_AT AS ROLE_CREATED_AT, r.UPDATED_AT AS ROLE_UPDATED_AT " +
                     "FROM UTILISATEUR u " +
                     "JOIN ROLE r ON u.ID_ROLE = r.ID " +
                     "WHERE u.STATUT = 'VISIBLE' " +
                     "AND r.STATUT = 'VISIBLE' " +
                     "AND r.NOM_ROLE = ? " +
                     "ORDER BY u.CREATION_DATE DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Utilisateur u = extractUserFromResultSet(rs);
                    
                    Role role = new Role();
                    role.setId(rs.getInt("ROLE_ID"));
                    role.setRoleName(rs.getString("NOM_ROLE"));
                    role.setDroits(rs.getString("DROITS"));
                    role.setStatut(rs.getString("ROLE_STATUT"));
                    role.setCreatedAt(rs.getTimestamp("ROLE_CREATED_AT"));
                    role.setUpdatedAt(rs.getTimestamp("ROLE_UPDATED_AT"));
                    
                    u.setRole(role);
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public void mettreAJourPrivilegeAuto(Utilisateur user) {
        PrivilegeNiveauDAO niveauDAO = new PrivilegeNiveauDAO();
        PrivilegeNiveau niveau = niveauDAO.determinerNiveauUtilisateur(user.getPoint() != null ? user.getPoint() : 0);
        
        if (niveau != null) {
            user.setPrivilege(niveau.getNom());
            user.setPourcentage(niveau.getPourcentageReduction().divide(new BigDecimal(100)));
            
            String sql = "UPDATE UTILISATEUR SET PRIVILLEGE = ?, POURCENTAGE = ?, UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, user.getPrivilege());
                stmt.setBigDecimal(2, user.getPourcentage());
                stmt.setInt(3, user.getId());
                stmt.executeUpdate();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Utilisateur getPrivilegeAndPointsOnly(int userId) {
        String sql = "SELECT PRIVILLEGE, POINT FROM UTILISATEUR WHERE ID = ? AND STATUT = 'VISIBLE'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = new Utilisateur();
                    user.setId(userId);
                    user.setPrivilege(rs.getString("PRIVILLEGE"));
                    user.setPoint(rs.getInt("POINT"));
                    return user;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}