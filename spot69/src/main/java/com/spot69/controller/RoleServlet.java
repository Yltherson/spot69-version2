package com.spot69.controller;

import com.google.gson.Gson;
import com.spot69.dao.RoleDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Role;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet({"/RoleServlet", "/blok/RoleServlet"})
public class RoleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "lister";

        switch (action) {
            case "lister":
                listerRoles(request, response);
                break;
            case "permissions":
                permissionsForm(request, response);
                break;
            case "get-droits": // Nouveau cas pour récupérer les droits
            	getDroitsRole(request, response);
                break;
            case "ajouter":
                showAjouterForm(request, response);
                break;
            case "supprimer":
                supprimerRole(request, response);
                break;
             
            case "get_utilisateurs_by_role":
                getUtilisateursParRoleAsJson(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action GET inconnue");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("ajouter".equals(action)) {
            ajouterRole(request, response);
        }else if ("modifierRoleName".equals(action)) {
        	ModifierRoleName(request, response);
        }else if ("update-droits".equals(action)) {
            updateDroits(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action POST inconnue");
        }
    }

    // ============== Méthodes GET ==================
    
    private void getDroitsRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            Role role = RoleDAO.findById(roleId);
            
            if (role != null) {
                // Création de la réponse JSON manuellement pour plus de contrôle
                String jsonResponse = String.format("{\"success\":true,\"roleId\":%d,\"roleName\":\"%s\",\"droits\":\"%s\"}",
                    role.getId(),
                    escapeJson(role.getRoleName()),
                    role.getDroits() != null ? escapeJson(role.getDroits()) : "");
                
                response.getWriter().write(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\":false,\"message\":\"Rôle non trouvé\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"ID de rôle invalide\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Erreur serveur\"}");
        }
    }

    // Méthode utilitaire pour échapper les chaînes JSON
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
    
    private void getUtilisateursParRoleAsJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int roleId = Integer.parseInt(request.getParameter("id"));

		List<Utilisateur> utilisateurs = utilisateurDAO.getUtilisateursParRole(roleId);

        Gson gson = new Gson();
        String json = gson.toJson(utilisateurs);
        response.getWriter().write(json);
    }


    private void listerRoles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Role> roles = RoleDAO.findAllVisible();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("liste-role.jsp").forward(request, response);
    }

    private void showAjouterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("liste-role.jsp").forward(request, response);
    }
    
    private void permissionsForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	List<Role> roles = RoleDAO.findAllVisible();
        request.setAttribute("roles", roles);
    	request.getRequestDispatcher("permissions.jsp").forward(request, response);
    }

    private void supprimerRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            RoleDAO.delete(id);
            response.sendRedirect("RoleServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    // ============== Méthodes POST ==================

    private void ajouterRole(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nom = request.getParameter("roleName");
        if (nom == null || nom.trim().isEmpty()) {
            request.setAttribute("error", "Le nom du rôle est requis.");
            listerRoles(request, response);
            return;
        }

        Role role = new Role();
        role.setRoleName(nom.trim());
        role.setStatut("VISIBLE");
        role.setDroits(""); // Ajout à part via permission.jsp

        boolean success = RoleDAO.insert(role);
        if (success) {
            response.sendRedirect("RoleServlet?action=lister");
        } else {
            request.setAttribute("error", "Erreur lors de l'ajout du rôle.");
            listerRoles(request, response);
        }
    }
    
    private void ModifierRoleName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roleIdStr = request.getParameter("id");
        String roleName = request.getParameter("roleName");

        if (roleIdStr == null || roleName == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        try {
            int roleId = Integer.parseInt(roleIdStr);
            RoleDAO.updateRoleName(roleId, roleName);
            response.sendRedirect("RoleServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void updateDroits(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

            String roleIdStr = request.getParameter("roleId");
            String droitsParam = request.getParameter("permissions");
            String droitsStr = (droitsParam != null) ? droitsParam : "";

            if (roleIdStr == null) {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"ID de rôle manquant\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rôle manquant");
                }
                return;
            }

            int roleId = Integer.parseInt(roleIdStr);

            System.out.println("roleId = " + roleId);
            System.out.println("droits = " + droitsStr);

            boolean success = RoleDAO.updateDroits(roleId, droitsStr);

            if (isAjax) {
                response.setContentType("application/json");
                if (success) {
                    response.getWriter().write("{\"success\":true}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\":\"Échec de la mise à jour\"}");
                }
            } else {
                response.sendRedirect("RoleServlet?action=lister");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rôle invalide");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }

}
