//package com.spot69.controller;
//
//import com.spot69.dao.UtilisateurDAO;
//import com.spot69.model.Utilisateur;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.List;
//
//@WebServlet({ "/PrivilegeServlet", "/blok/PrivilegeServlet" })
//public class PrivilegeServlet extends HttpServlet {
//
//    private UtilisateurDAO utilisateurDAO;
//
//    @Override
//    public void init() throws ServletException {
//        utilisateurDAO = new UtilisateurDAO();
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//        
//        String action = request.getParameter("action");
//        
//        if ("lister".equals(action)) {
//            // Rediriger vers la JSP
//            request.getRequestDispatcher("privilege-69.jsp").forward(request, response);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//        
//        String action = request.getParameter("action");
//        
//        if ("update".equals(action)) {
//            updateSingleUser(request, response);
//        } else if ("updateMultiple".equals(action)) {
//            updateMultipleUsers(request, response);
//        }
//    }
//    
//    private void updateSingleUser(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            int userId = Integer.parseInt(request.getParameter("userId"));
//            String privilege = request.getParameter("privilege");
//            String pointsStr = request.getParameter("points");
//            String pourcentageStr = request.getParameter("pourcentage");
//            
//            // Récupérer l'utilisateur
//            Utilisateur user = utilisateurDAO.findById(userId);
//            if (user == null) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Utilisateur non trouvé.");
//                response.sendRedirect("privilege-69.jsp");
//                return;
//            }
//            
//            // Mettre à jour les informations
//            user.setPrivilege(privilege != null ? privilege : "BRONZE");
//            
//            if (pointsStr != null && !pointsStr.isEmpty()) {
//                try {
//                    user.setPoint(Integer.parseInt(pointsStr));
//                } catch (NumberFormatException e) {
//                    // Garder la valeur existante
//                }
//            }
//            
//            if (pourcentageStr != null && !pourcentageStr.isEmpty()) {
//                try {
//                    int pourcentageInt = Integer.parseInt(pourcentageStr);
//                    BigDecimal pourcentage = BigDecimal.valueOf(pourcentageInt).divide(BigDecimal.valueOf(100));
//                    user.setPourcentage(pourcentage);
//                } catch (NumberFormatException e) {
//                    // Garder la valeur existante
//                }
//            }
//            
//            // Mettre à jour dans la base de données
//            boolean success = updateUserPrivilege(user);
//            
//            if (success) {
//                request.getSession().setAttribute("ToastAdmSuccesNotif", 
//                    "Niveau de privilège mis à jour avec succès pour " + user.getPrenom() + " " + user.getNom());
//            } else {
//                request.getSession().setAttribute("ToastAdmErrorNotif", 
//                    "Erreur lors de la mise à jour du niveau de privilège");
//            }
//            
//            response.sendRedirect("privilege-69.jsp");
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.getSession().setAttribute("ToastAdmErrorNotif", 
//                "Erreur : " + e.getMessage());
//            response.sendRedirect("privilege-69.jsp");
//        }
//    }
//    
//    private void updateMultipleUsers(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            String[] selectedUsers = request.getParameterValues("selectedUsers");
//            String newPrivilege = request.getParameter("newPrivilege");
//            
//            if (selectedUsers == null || selectedUsers.length == 0) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", 
//                    "Veuillez sélectionner au moins un utilisateur");
//                response.sendRedirect("privilege-69.jsp");
//                return;
//            }
//            
//            if (newPrivilege == null || newPrivilege.trim().isEmpty()) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", 
//                    "Veuillez sélectionner un niveau de privilège");
//                response.sendRedirect("privilege-69.jsp");
//                return;
//            }
//            
//            int successCount = 0;
//            int failCount = 0;
//            
//            for (String userIdStr : selectedUsers) {
//                try {
//                    int userId = Integer.parseInt(userIdStr);
//                    Utilisateur user = utilisateurDAO.findById(userId);
//                    
//                    if (user != null) {
//                        user.setPrivilege(newPrivilege);
//                        if (updateUserPrivilege(user)) {
//                            successCount++;
//                        } else {
//                            failCount++;
//                        }
//                    } else {
//                        failCount++;
//                    }
//                } catch (NumberFormatException e) {
//                    failCount++;
//                }
//            }
//            
//            String message;
//            if (failCount == 0) {
//                message = "Niveau de privilège mis à jour avec succès pour " + successCount + " utilisateur(s)";
//                request.getSession().setAttribute("ToastAdmSuccesNotif", message);
//            } else {
//                message = successCount + " utilisateur(s) mis à jour, " + failCount + " échec(s)";
//                request.getSession().setAttribute("ToastAdmWarningNotif", message);
//            }
//            
//            response.sendRedirect("privilege-69.jsp");
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.getSession().setAttribute("ToastAdmErrorNotif", 
//                "Erreur lors de la mise à jour multiple : " + e.getMessage());
//            response.sendRedirect("privilege-69.jsp");
//        }
//    }
//    
//    private boolean updateUserPrivilege(Utilisateur user) {
//        // Méthode pour mettre à jour uniquement les champs de privilège
//        String sql = "UPDATE UTILISATEUR SET PRIVILLEGE = ?, POINT = ?, POURCENTAGE = ?, UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
//        
//        try (java.sql.Connection conn = com.spot69.utils.DBConnection.getConnection();
//             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
//            
//            stmt.setString(1, user.getPrivilege());
//            stmt.setInt(2, user.getPoint() != null ? user.getPoint() : 0);
//            stmt.setBigDecimal(3, user.getPourcentage());
//            stmt.setInt(4, user.getId());
//            
//            return stmt.executeUpdate() > 0;
//            
//        } catch (java.sql.SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}

package com.spot69.controller;

import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet({ "/PrivilegeServlet", "/blok/PrivilegeServlet" })
public class PrivilegeServlet extends HttpServlet {

    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "lister":
                lister(request, response);
                break;
            case "details":
                details(request, response);
                break;
            case "getUser":
                getUserForEdit(request, response);
                break;
            case "view":
                view(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "update":
                updateSingleUser(request, response);
                break;
            case "updateMultiple":
                updateMultipleUsers(request, response);
                break;
            case "resetPoints":
                resetPoints(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

//    private void lister(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            // Récupérer les filtres
//            String privilegeFilter = request.getParameter("privilege");
//            String pointsMinStr = request.getParameter("pointsMin");
//            
//            // Récupérer tous les clients
//            List<Utilisateur> clients = utilisateurDAO.findAllVisibleByRoleClient("CLIENT");
//            
//            // Appliquer les filtres
//            if (privilegeFilter != null && !privilegeFilter.isEmpty()) {
//                clients.removeIf(client -> {
//                    String clientPrivilege = client.getPrivilege() != null ? 
//                            client.getPrivilege().toUpperCase() : "BRONZE";
//                    return !clientPrivilege.equals(privilegeFilter.toUpperCase());
//                });
//            }
//            
//            if (pointsMinStr != null && !pointsMinStr.isEmpty()) {
//                try {
//                    int pointsMin = Integer.parseInt(pointsMinStr);
//                    clients.removeIf(client -> 
//                        client.getPoint() == null || client.getPoint() < pointsMin);
//                } catch (NumberFormatException e) {
//                    // Ignorer le filtre si la valeur n'est pas valide
//                }
//            }
//            
//            // Charger les données complètes pour chaque client
//            for (Utilisateur client : clients) {
//                Utilisateur fullClient = utilisateurDAO.findById(client.getId());
//                if (fullClient != null) {
//                    client.setPourcentage(fullClient.getPourcentage());
//                    client.setPoint(fullClient.getPoint());
//                    client.setPrivilege(fullClient.getPrivilege());
//                }
//            }
//            
//            request.setAttribute("clients", clients);
//            request.getRequestDispatcher("privilege-69.jsp").forward(request, response);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.getSession().setAttribute("ToastAdmErrorNotif", 
//                "Erreur lors du chargement de la liste : " + e.getMessage());
//            response.sendRedirect("index.jsp");
//        }
//    }

    private void details(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            Utilisateur user = utilisateurDAO.findById(userId);
            
            if (user == null) {
                response.setContentType("text/html");
                response.getWriter().write("<div class='alert alert-danger'>Utilisateur non trouvé</div>");
                return;
            }
            
            // Générer le HTML des détails
            StringBuilder html = new StringBuilder();
            html.append("<div class='row'>");
            
            html.append("<div class='col-md-4'>");
            html.append("<div class='card shadow mb-4'>");
            html.append("<div class='card-header bg-primary text-white'>");
            html.append("<h6 class='m-0 font-weight-bold'><i class='fe fe-user mr-2'></i>Informations du client</h6>");
            html.append("</div>");
            html.append("<div class='card-body'>");
            html.append("<div class='text-center mb-4'>");
            html.append("<div class='avatar-circle bg-primary mx-auto mb-3' style='width: 80px; height: 80px;'>");
            html.append("<span class='text-white h3 mb-0'>");
            html.append(user.getPrenom().charAt(0));
            html.append(user.getNom().charAt(0));
            html.append("</span>");
            html.append("</div>");
            html.append("<h5 class='font-weight-bold'>").append(user.getPrenom()).append(" ").append(user.getNom()).append("</h5>");
            html.append("<p class='text-muted'>Client</p>");
            html.append("</div>");
            
            html.append("<div class='row mb-2'>");
            html.append("<div class='col-6'><strong>Email:</strong></div>");
            html.append("<div class='col-6'>").append(user.getEmail() != null ? user.getEmail() : "N/A").append("</div>");
            html.append("</div>");
            
            html.append("<div class='row mb-2'>");
            html.append("<div class='col-6'><strong>Téléphone:</strong></div>");
            html.append("<div class='col-6'>").append(user.getTelephone() != null ? user.getTelephone() : "N/A").append("</div>");
            html.append("</div>");
            
            html.append("<div class='row mb-2'>");
            html.append("<div class='col-6'><strong>Adresse:</strong></div>");
            html.append("<div class='col-6'>").append(user.getAdresse() != null ? user.getAdresse() : "N/A").append("</div>");
            html.append("</div>");
            
            html.append("</div></div></div>");
            
            // Colonne des privilèges
            html.append("<div class='col-md-8'>");
            html.append("<div class='card shadow mb-4'>");
            html.append("<div class='card-header bg-success text-white'>");
            html.append("<h6 class='m-0 font-weight-bold'><i class='fe fe-award mr-2'></i>Statut Privilège 69</h6>");
            html.append("</div>");
            html.append("<div class='card-body'>");
            
            // Niveau de privilège
            String privilege = user.getPrivilege() != null ? user.getPrivilege().toUpperCase() : "BRONZE";
            String badgeClass = getBadgeClass(privilege);
            
            html.append("<div class='row mb-4'>");
            html.append("<div class='col-12'>");
            html.append("<h6 class='font-weight-bold mb-3'>Niveau de privilège</h6>");
            html.append("<span class='badge ").append(badgeClass).append(" p-3 mb-3' style='font-size: 1.2rem;'>");
            html.append("<i class='fe fe-award mr-2'></i>");
            html.append(privilege);
            html.append("</span>");
            html.append("</div></div>");
            
            // Points et réduction
            html.append("<div class='row'>");
            html.append("<div class='col-md-6 mb-3'>");
            html.append("<div class='card border-left-primary shadow h-100 py-2'>");
            html.append("<div class='card-body'>");
            html.append("<div class='row no-gutters align-items-center'>");
            html.append("<div class='col mr-2'>");
            html.append("<div class='text-xs font-weight-bold text-primary text-uppercase mb-1'>Points accumulés</div>");
            html.append("<div class='h5 mb-0 font-weight-bold text-gray-800'>");
            html.append(user.getPoint() != null ? user.getPoint() : 0);
            html.append(" pts</div>");
            html.append("</div>");
            html.append("<div class='col-auto'>");
            html.append("<i class='fe fe-dollar-sign fe-2x text-gray-300'></i>");
            html.append("</div></div></div></div></div>");
            
            html.append("<div class='col-md-6 mb-3'>");
            html.append("<div class='card border-left-success shadow h-100 py-2'>");
            html.append("<div class='card-body'>");
            html.append("<div class='row no-gutters align-items-center'>");
            html.append("<div class='col mr-2'>");
            html.append("<div class='text-xs font-weight-bold text-success text-uppercase mb-1'>Pourcentage de réduction</div>");
            html.append("<div class='h5 mb-0 font-weight-bold text-gray-800'>");
            html.append(user.getPourcentage() != null ? 
                user.getPourcentage().multiply(BigDecimal.valueOf(100)).intValue() : 0);
            html.append(" %</div>");
            html.append("</div>");
            html.append("<div class='col-auto'>");
            html.append("<i class='fe fe-percent fe-2x text-gray-300'></i>");
            html.append("</div></div></div></div></div>");
            html.append("</div>");
            
            // Informations supplémentaires
            html.append("<div class='mt-4'>");
            html.append("<h6 class='font-weight-bold mb-3'>Historique</h6>");
            html.append("<p class='text-muted'>");
            html.append("Membre depuis le ").append(user.getCreationDate()).append("<br>");
            html.append("Dernière mise à jour: ").append(user.getUpdateDate());
            html.append("</p>");
            html.append("</div>");
            
            html.append("</div></div></div>");
            html.append("</div>");
            
            response.setContentType("text/html");
            response.getWriter().write(html.toString());
            
        } catch (NumberFormatException e) {
            response.setContentType("text/html");
            response.getWriter().write("<div class='alert alert-danger'>ID utilisateur invalide</div>");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("<div class='alert alert-danger'>Erreur lors du chargement des détails</div>");
        }
    }

    private void getUserForEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            Utilisateur user = utilisateurDAO.findById(userId);
            
            if (user == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Utilisateur non trouvé\"}");
                return;
            }
            
            // Retourner les données en JSON
            String json = String.format(
                "{\"id\": %d, \"nom\": \"%s\", \"prenom\": \"%s\", \"privilege\": \"%s\", " +
                "\"point\": %d, \"pourcentage\": %.2f}",
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getPrivilege() != null ? user.getPrivilege() : "BRONZE",
                user.getPoint() != null ? user.getPoint() : 0,
                user.getPourcentage() != null ? user.getPourcentage().doubleValue() : 0.0
            );
            
            response.setContentType("application/json");
            response.getWriter().write(json);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Erreur lors du chargement des données\"}");
        }
    }

    private void view(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Utilisateur> clients = utilisateurDAO.findAllVisibleByRole("CLIENT");
            
            // Charger les données complètes pour chaque client
            for (Utilisateur client : clients) {
                Utilisateur fullClient = utilisateurDAO.findById(client.getId());
                if (fullClient != null) {
                    client.setPourcentage(fullClient.getPourcentage());
                    client.setPoint(fullClient.getPoint());
                    client.setPrivilege(fullClient.getPrivilege());
                }
            }
            
            request.setAttribute("clients", clients);
            request.getRequestDispatcher("privilege-69.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur lors du chargement de la page : " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void updateSingleUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String privilege = request.getParameter("privilege");
            String pointsStr = request.getParameter("points");
            String pourcentageStr = request.getParameter("pourcentage");
            
            boolean success = handleUserPrivilege(userId, privilege, pointsStr, pourcentageStr, null);
            
            if (success) {
                Utilisateur user = utilisateurDAO.findById(userId);
                request.getSession().setAttribute("ToastAdmSuccesNotif", 
                    "Niveau de privilège mis à jour avec succès pour " + user.getPrenom() + " " + user.getNom());
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", 
                    "Erreur lors de la mise à jour du niveau de privilège");
            }
            
            response.sendRedirect("PrivilegeServlet?action=lister");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur : " + e.getMessage());
            response.sendRedirect("PrivilegeServlet?action=lister");
        }
    }
    
    private void updateMultipleUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String[] selectedUsers = request.getParameterValues("selectedUsers");
            String newPrivilege = request.getParameter("newPrivilege");
            
            if (selectedUsers == null || selectedUsers.length == 0) {
                request.getSession().setAttribute("ToastAdmErrorNotif", 
                    "Veuillez sélectionner au moins un utilisateur");
                response.sendRedirect("PrivilegeServlet?action=lister");
                return;
            }
            
            if (newPrivilege == null || newPrivilege.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmErrorNotif", 
                    "Veuillez sélectionner un niveau de privilège");
                response.sendRedirect("PrivilegeServlet?action=lister");
                return;
            }
            
            int successCount = 0;
            int failCount = 0;
            
            for (String userIdStr : selectedUsers) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    boolean success = handleUserPrivilege(userId, newPrivilege, null, null, "MULTIPLE");
                    
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (NumberFormatException e) {
                    failCount++;
                }
            }
            
            String message;
            if (failCount == 0) {
                message = "Niveau de privilège mis à jour avec succès pour " + successCount + " utilisateur(s)";
                request.getSession().setAttribute("ToastAdmSuccesNotif", message);
            } else {
                message = successCount + " utilisateur(s) mis à jour, " + failCount + " échec(s)";
                request.getSession().setAttribute("ToastAdmWarningNotif", message);
            }
            
            response.sendRedirect("PrivilegeServlet?action=lister");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur lors de la mise à jour multiple : " + e.getMessage());
            response.sendRedirect("PrivilegeServlet?action=lister");
        }
    }
    
    private void resetPoints(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            Utilisateur user = utilisateurDAO.findById(userId);
            
            if (user == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Utilisateur non trouvé.");
                response.sendRedirect("PrivilegeServlet?action=lister");
                return;
            }
            
            // Réinitialiser les points à 0
            user.setPoint(0);
            boolean success = updateUserPrivilege(user);
            
            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", 
                    "Points réinitialisés avec succès pour " + user.getPrenom() + " " + user.getNom());
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", 
                    "Erreur lors de la réinitialisation des points");
            }
            
            response.sendRedirect("PrivilegeServlet?action=lister");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur : " + e.getMessage());
            response.sendRedirect("PrivilegeServlet?action=lister");
        }
    }
    
    /**
     * Méthode centrale pour gérer les privilèges des utilisateurs
     * @param userId ID de l'utilisateur
     * @param privilege Nouveau niveau de privilège
     * @param pointsStr Nouveau nombre de points (peut être null)
     * @param pourcentageStr Nouveau pourcentage de réduction (peut être null)
     * @param updateType Type de mise à jour ("SINGLE" ou "MULTIPLE")
     * @return true si la mise à jour a réussi, false sinon
     */
    private boolean handleUserPrivilege(int userId, String privilege, String pointsStr, 
                                       String pourcentageStr, String updateType) {
        try {
            // Récupérer l'utilisateur
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null) {
                return false;
            }
            
            // Mettre à jour les informations
            user.setPrivilege(privilege != null ? privilege : "BRONZE");
            
            // Pour les mises à jour simples, mettre à jour les points et pourcentage si fournis
            if ("SINGLE".equals(updateType) || updateType == null) {
                if (pointsStr != null && !pointsStr.isEmpty()) {
                    try {
                        user.setPoint(Integer.parseInt(pointsStr));
                    } catch (NumberFormatException e) {
                        // Garder la valeur existante
                    }
                }
                
                if (pourcentageStr != null && !pourcentageStr.isEmpty()) {
                    try {
                        int pourcentageInt = Integer.parseInt(pourcentageStr);
                        BigDecimal pourcentage = BigDecimal.valueOf(pourcentageInt)
                                .divide(BigDecimal.valueOf(100));
                        user.setPourcentage(pourcentage);
                    } catch (NumberFormatException e) {
                        // Garder la valeur existante
                    }
                }
            }
            
            // Mettre à jour dans la base de données
            return updateUserPrivilege(user);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateUserPrivilege(Utilisateur user) {
        // Méthode pour mettre à jour uniquement les champs de privilège
        String sql = "UPDATE UTILISATEUR SET PRIVILLEGE = ?, POINT = ?, POURCENTAGE = ?, UPDATE_DATE = CURRENT_TIMESTAMP WHERE ID = ?";
        
        try (java.sql.Connection conn = com.spot69.utils.DBConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getPrivilege());
            stmt.setInt(2, user.getPoint() != null ? user.getPoint() : 0);
            stmt.setBigDecimal(3, user.getPourcentage());
            stmt.setInt(4, user.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String getBadgeClass(String privilege) {
        switch(privilege.toUpperCase()) {
            case "VIP": return "badge-vip";
            case "GOLD": return "badge-gold";
            case "SILVER": return "badge-silver";
            default: return "badge-bronze";
        }
    }
    
    private void lister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Récupérer les filtres
            String privilegeFilter = request.getParameter("privilege");
            String pointsMinStr = request.getParameter("pointsMin");
            
            // Récupérer tous les clients
            List<Utilisateur> clients = utilisateurDAO.findAllVisibleByRoleClient("CLIENT");
            
            // Appliquer les filtres
            if (privilegeFilter != null && !privilegeFilter.isEmpty()) {
                clients.removeIf(client -> {
                    String clientPrivilege = client.getPrivilege() != null ? 
                            client.getPrivilege().toUpperCase() : "BRONZE";
                    return !clientPrivilege.equals(privilegeFilter.toUpperCase());
                });
            }
            
            if (pointsMinStr != null && !pointsMinStr.isEmpty()) {
                try {
                    int pointsMin = Integer.parseInt(pointsMinStr);
                    clients.removeIf(client -> 
                        client.getPoint() == null || client.getPoint() < pointsMin);
                } catch (NumberFormatException e) {
                    // Ignorer le filtre si la valeur n'est pas valide
                }
            }
            
            // NE PAS recharger chaque utilisateur individuellement - utilisez les données déjà chargées
            // La méthode findAllVisibleByRoleClient charge déjà toutes les données nécessaires
            
            request.setAttribute("clients", clients);
            request.getRequestDispatcher("privilege-69.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur lors du chargement de la liste : " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }
}