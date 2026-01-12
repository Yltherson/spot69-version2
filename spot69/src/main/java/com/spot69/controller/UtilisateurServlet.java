//package com.spot69.controller;
//
//import com.spot69.dao.RoleDAO;
//import com.spot69.dao.UtilisateurDAO;
//import com.spot69.model.Role;
//import com.spot69.model.Utilisateur;
//import com.spot69.utils.PasswordUtils;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//@WebServlet({ "/UtilisateurServlet", "/blok/UtilisateurServlet" })
//public class UtilisateurServlet extends HttpServlet {
//
//    private UtilisateurDAO utilisateurDAO;
//
//    @Override
//    public void init() throws ServletException {
//        utilisateurDAO = new UtilisateurDAO();
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
//        if (action == null) {
//            response.sendRedirect("index.jsp");
//            return;
//        }
//
//        switch (action) {
//            case "login":
//                login(request, response);
//                break;
//            case "register":
//                register(request, response);
//                break;
//            case "modifier":
//                modifier(request, response);
//                break;
//            case "supprimer":
//                supprimer(request, response);
//                break;
//            default:
//                response.sendRedirect("index.jsp");
//                break;
//        }
//    }
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//
//        String action = request.getParameter("action");
//        String who = request.getParameter("whoislogoin");
//
//        if (action == null) {
//            response.sendRedirect("index.jsp");
//            return;
//        }
//
//        switch (action) {
//            case "logout":
//                handleLogout(request, response, who);
//                break;
//
//            case "add":
//                showAddUserForm(request, response);
//                break;
//
//            case "lister":
//                lister(request, response);
//                break;
//
//            case "voir":
//            case "profil":
//                handleVoirProfil(request, response);
//                break;
//
//            default:
//                response.sendRedirect("index.jsp");
//                break;
//        }
//    }
//
//    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//        String whoislogin = request.getParameter("whoislogin");
//        String redirectTo = request.getParameter("redirectTo");
//
//        // Vérification des champs vides
//        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
//            StringBuilder message = new StringBuilder("Veuillez remplir ");
//
//            if (username == null || username.isEmpty()) {
//                message.append("le nom d'utilisateur");
//            }
//            if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
//                message.append(" et ");
//            }
//            if (password == null || password.isEmpty()) {
//                message.append("le mot de passe");
//            }
//            message.append(".");
//
//            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
//            request.getSession().setAttribute(toastKey, message.toString());
//
//            if (!"redirectTo".equals(redirectTo)) {
//                request.getSession().setAttribute("toastType", "error");
//            }
//
//            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
//            response.sendRedirect(request.getContextPath() + redir);
//            return;
//        }
//
//        Utilisateur user = utilisateurDAO.findByLoginOrEmail(username);
//
//        if (user == null) {
//            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
//            String toastValue = "Utilisateur introuvable.";
//            request.getSession().setAttribute(toastKey, toastValue);
//            if (!"dashboard".equals(redirectTo)) {
//                request.getSession().setAttribute("toastType", "error");
//            }
//
//            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
//            response.sendRedirect(request.getContextPath() + redir);
//            return;
//        }
//
//        if (!PasswordUtils.checkPassword(password, user.getMotDePasse())) {
//            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
//            String toastValue = "Mot de passe incorrect.";
//            request.getSession().setAttribute(toastKey, toastValue);
//            if (!"dashboard".equals(redirectTo)) {
//                request.getSession().setAttribute("toastType", "error");
//            }
//
//            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
//            response.sendRedirect(request.getContextPath() + redir);
//            return;
//        }
//
//        // Connexion réussie
//        HttpSession session = request.getSession();
//        session.setAttribute("user", user);
//        session.setAttribute("userId", user.getId());
//        System.out.println("LOGIN --------------");
//        System.out.println(user.getId() );
//        session.setAttribute("username", user.getLogin());
//        session.setAttribute("role", user.getRole().getRoleName());
//        Utilisateur freshUser = utilisateurDAO.findById(user.getId());
//        if (freshUser != null) {
//            request.getSession().setAttribute("user", freshUser);
//            // Redirection vers la page d'accueil
//        }
//
//        // Redirection spéciale si rôle = ADMINISTRATEUR ou SUPER_ADMINISTRATEUR
////        if ("ADMINISTRATEUR".equalsIgnoreCase(user.getRole().getRoleName()) || "SUPER_ADMINISTRATEUR".equalsIgnoreCase(user.getRole().getRoleName())) {
////            session.setAttribute("ToastAdmSuccesNotif", "Connexion réussie !");
////            response.sendRedirect(request.getContextPath() + "/blok/index.jsp");
////            return;
////        }
//
//        // Sinon redirection selon whoislogin
//        if ("dashboard".equals(redirectTo)) {
//            session.setAttribute("ToastAdmSuccesNotif", "Connexion réussie !");
//            response.sendRedirect(request.getContextPath() + "/blok/index.jsp");
//        } else {
//            session.setAttribute("toastMessage", "Connexion réussie !");
//            session.setAttribute("toastType", "success");
//            response.sendRedirect(request.getContextPath() + "/index.jsp");
//        }
//    }
//
////    private void register(HttpServletRequest request, HttpServletResponse response)
////            throws IOException, ServletException {
////        request.setCharacterEncoding("UTF-8");
////        response.setCharacterEncoding("UTF-8");
////        response.setContentType("text/html; charset=UTF-8");
////        
////
////        String nom = request.getParameter("nom");
////        String prenom = request.getParameter("prenom");
////        String email = request.getParameter("email");
////        String login = request.getParameter("username");
////        String password = request.getParameter("password");
////        String whoislogin = request.getParameter("whoislogin");
////
////        String pourcentageStr = request.getParameter("pourcentage");
////        BigDecimal pourcentage = null;
////        
////        String toastKey = "ADM".equals(whoislogin) ? "ToastAdmErrorNotif" : "toastMessage";
////        String toastType = "ADM".equals(whoislogin) ? null : "error";
////        String redirectPage = "ADM".equals(whoislogin) ? "add-user.jsp" : "index.jsp";
////
////        if (pourcentageStr != null && !pourcentageStr.isEmpty()) {
////            try {
////                BigDecimal temp = new BigDecimal(pourcentageStr);
////
////                // Vérifier que c'est bien un entier (pas de décimales)
////                if (temp.stripTrailingZeros().scale() > 0) {
////                	
////                    request.getSession().setAttribute(toastKey, "Le pourcentage doit être un entier.");
////                    if (toastType != null) request.getSession().setAttribute("toastType", "error");
////                    responseRedirect(request, response, redirectPage);
////                    return;
////                }
////
////                // Diviser par 100
////                pourcentage = temp.divide(BigDecimal.valueOf(100));
////
////            } catch (NumberFormatException e) {
////                request.getSession().setAttribute(toastKey, "Le pourcentage doit être un nombre entier valide.");
////                if (toastType != null) request.getSession().setAttribute("toastType", "error");
////                responseRedirect(request, response, redirectPage);
////                return;
////            }
////        } else {
////            pourcentage = BigDecimal.ZERO; // valeur par défaut si vide
////        }
////
////       
////
////        List<String> missingFields = new ArrayList<>();
////        if (nom == null || nom.trim().isEmpty()) missingFields.add("Nom");
////        if (prenom == null || prenom.trim().isEmpty()) missingFields.add("Prénom");
//////        if (email == null || email.trim().isEmpty()) missingFields.add("Email");
////        if (login == null || login.trim().isEmpty()) missingFields.add("Identifiant");
////        if (password == null || password.trim().isEmpty()) missingFields.add("Mot de passe");
////
////        if (!missingFields.isEmpty()) {
////            String message = "Veuillez remplir les champs suivants : " + String.join(", ", missingFields);
////            request.getSession().setAttribute(toastKey, message);
////            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
////            responseRedirect(request, response, redirectPage);
////            return;
////        }
////
////        // Vérifier doublons login/email
////        Utilisateur existingUser = utilisateurDAO.findByLoginOrEmail(login);
////        if (existingUser != null && login.equalsIgnoreCase(existingUser.getLogin())) {
////            request.getSession().setAttribute(toastKey, "Ce nom d'utilisateur est déjà utilisé.");
////            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
////            responseRedirect(request, response, redirectPage);
////            return;
////        }
////
////        if (email != null && !email.trim().isEmpty()) {
////            existingUser = utilisateurDAO.findByLoginOrEmail(email);
////            if (existingUser != null && email.equalsIgnoreCase(existingUser.getEmail())) {
////                request.getSession().setAttribute(toastKey, "Cet email est déjà utilisé.");
////                if (toastType != null) request.getSession().setAttribute("toastType", toastType);
////                responseRedirect(request, response, redirectPage);
////                return;
////            }
////        }
////
////
////        // Définir le rôle
////        Role role = null;
////        HttpSession session = request.getSession(false);
////        if (session != null) {
////            String currentUserRole = (String) session.getAttribute("role");
////            if ("ADMINISTRATEUR".equals(currentUserRole) || "SUPER_ADMINISTRATEUR".equals(currentUserRole)) {
////                String roleParam = request.getParameter("role");
////                if (roleParam != null && !roleParam.trim().isEmpty()) {
////                    try {
////                        int roleId = Integer.parseInt(roleParam);
////                        role = RoleDAO.findById(roleId);
////                        if (role == null) {
////                            request.getSession().setAttribute(toastKey, "Rôle invalide sélectionné.");
////                            responseRedirect(request, response, redirectPage);
////                            return;
////                        }
////                    } catch (NumberFormatException e) {
////                        request.getSession().setAttribute(toastKey, "ID du rôle invalide.");
////                        responseRedirect(request, response, redirectPage);
////                        return;
////                    }
////                }
////            }
////        }
////
////        if (role == null) {
////            role = RoleDAO.findById(3); // rôle CLIENT par défaut 
////        }
////
////        Utilisateur user = new Utilisateur();
////        user.setNom(nom);
////        user.setPrenom(prenom);
////        user.setEmail(email);
////        user.setLogin(login);
////        user.setMotDePasse(PasswordUtils.hashPassword(password));
////        user.setRole(role);
////        user.setPourcentage(pourcentage);
////
////        boolean success = utilisateurDAO.insert(user);
////
////        if (success) {
////            request.getSession().setAttribute(toastKey, "Inscription réussie !");
////            if (toastType != null) request.getSession().setAttribute("toastType", "success");
////        } else {
////            request.getSession().setAttribute(toastKey, "Erreur technique lors de l’inscription.");
////            if (toastType != null) request.getSession().setAttribute("toastType", "error");
////        }
////
////        responseRedirect(request, response, redirectPage);
////    }
//    
////    ylth-------------------------------------------------------------------------------
//    private void register(HttpServletRequest request, HttpServletResponse response)
//            throws IOException, ServletException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//        
//        // Récupération des nouveaux champs
//        String nom = request.getParameter("nom");
//        String prenom = request.getParameter("prenom");
//        String email = request.getParameter("email");
//        String login = request.getParameter("username");
//        String password = request.getParameter("password");
//        String telephone = request.getParameter("telephone");
//        String adresse = request.getParameter("adresse");
//        String whoislogin = request.getParameter("whoislogin");
//
//        // Pourcentage par défaut à 0 pour les clients
//        BigDecimal pourcentage = BigDecimal.ZERO;
//        
//        String toastKey = "ADM".equals(whoislogin) ? "ToastAdmErrorNotif" : "toastMessage";
//        String toastType = "ADM".equals(whoislogin) ? null : "error";
//        String redirectPage = "ADM".equals(whoislogin) ? "add-user.jsp" : "index.jsp";
//
//        // Validation des champs obligatoires
//        List<String> missingFields = new ArrayList<>();
//        if (nom == null || nom.trim().isEmpty()) missingFields.add("Nom");
//        if (prenom == null || prenom.trim().isEmpty()) missingFields.add("Prénom");
//        if (login == null || login.trim().isEmpty()) missingFields.add("Identifiant");
//        if (password == null || password.trim().isEmpty()) missingFields.add("Mot de passe");
//
//        if (!missingFields.isEmpty()) {
//            String message = "Veuillez remplir les champs obligatoires : " + String.join(", ", missingFields);
//            request.getSession().setAttribute(toastKey, message);
//            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
//            responseRedirect(request, response, redirectPage);
//            return;
//        }
//
//        // Vérifier doublons login/email
//        Utilisateur existingUser = utilisateurDAO.findByLoginOrEmail(login);
//        if (existingUser != null && login.equalsIgnoreCase(existingUser.getLogin())) {
//            request.getSession().setAttribute(toastKey, "Ce nom d'utilisateur est déjà utilisé.");
//            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
//            responseRedirect(request, response, redirectPage);
//            return;
//        }
//
//        if (email != null && !email.trim().isEmpty()) {
//            existingUser = utilisateurDAO.findByLoginOrEmail(email);
//            if (existingUser != null && email.equalsIgnoreCase(existingUser.getEmail())) {
//                request.getSession().setAttribute(toastKey, "Cet email est déjà utilisé.");
//                if (toastType != null) request.getSession().setAttribute("toastType", toastType);
//                responseRedirect(request, response, redirectPage);
//                return;
//            }
//        }
//
//        // Définir le rôle (CLIENT par défaut pour l'inscription mobile)
//        Role role = RoleDAO.findById(3); // rôle CLIENT
//
//        // Création de l'utilisateur
//        Utilisateur user = new Utilisateur();
//        user.setNom(nom);
//        user.setPrenom(prenom);
//        user.setEmail(email != null ? email.trim() : null); // email peut être null
//        user.setLogin(login);
//        user.setMotDePasse(PasswordUtils.hashPassword(password));
//        user.setRole(role);
//        user.setPourcentage(pourcentage);
//
//        // Insertion dans la base de données avec les nouveaux paramètres
//        boolean success = utilisateurDAO.insert(user, telephone, adresse);
//
//        if (success) {
//            request.getSession().setAttribute(toastKey, "Inscription réussie !");
//            if (toastType != null) request.getSession().setAttribute("toastType", "success");
//        } else {
//            request.getSession().setAttribute(toastKey, "Erreur technique lors de l'inscription.");
//            if (toastType != null) request.getSession().setAttribute("toastType", "error");
//        }
//
//        responseRedirect(request, response, redirectPage);
//    }
//
//    private void modifier(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//
//        try {
//        	int id = Integer.parseInt(request.getParameter("id"));
//        	String plafondStr = request.getParameter("plafond");
//        	int plafond = 0; // utiliser Integer pour accepter null
//
//        	if (plafondStr != null && !plafondStr.trim().isEmpty()) {
//        	    try {
//        	        plafond = Integer.parseInt(plafondStr.trim());
//        	    } catch (NumberFormatException e) {
//        	        request.getSession().setAttribute("ToastAdmErrorNotif", "Plafond invalide.");
//        	        request.getSession().setAttribute("toastType", "error");
//        	        response.sendRedirect("UtilisateurServlet?action=lister");
//        	        return;
//        	    }
//        	}
//
//        	// Plus tard, tu peux faire : user.setPlafond(plafond);
//
//            String nom = request.getParameter("nom");
//            String prenom = request.getParameter("prenom");
//            String email = request.getParameter("email");
//            String login = request.getParameter("username");
//            String password = request.getParameter("password");
//            String roleIdStr = request.getParameter("role");
//            
//            String pourcentageStr = request.getParameter("pourcentage");
//            BigDecimal pourcentage = null;
//
//            if (pourcentageStr != null && !pourcentageStr.isEmpty()) {
//                try {
//                    BigDecimal temp = new BigDecimal(pourcentageStr);
//
//                    // Vérifier que c'est bien un entier (pas de décimales)
//                    if (temp.stripTrailingZeros().scale() > 0) {
//                        request.getSession().setAttribute("ToastAdmErrorNotif", "Le pourcentage doit être un entier.");
//                       request.getSession().setAttribute("toastType", "error");
//                        response.sendRedirect("UtilisateurServlet?action=lister");
//                        return;
//                    }
//
//                    // Diviser par 100
//                    pourcentage = temp.divide(BigDecimal.valueOf(100));
//
//                } catch (NumberFormatException e) {
//                    request.getSession().setAttribute("ToastAdmErrorNotif", "Le pourcentage doit être un nombre entier valide.");
//                   request.getSession().setAttribute("toastType", "error");
//                   response.sendRedirect("UtilisateurServlet?action=lister");
//                    return;
//                }
//            } else {
//                pourcentage = BigDecimal.ZERO; // valeur par défaut si vide
//            }
//
//            if (nom == null || prenom == null || email == null || login == null || roleIdStr == null) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Champs requis manquants.");
//                request.getSession().setAttribute("toastType", "error");
//                response.sendRedirect("UtilisateurServlet?action=lister");
//                return;
//            }
//
//            int roleId = Integer.parseInt(roleIdStr);
//            Role role = RoleDAO.findById(roleId);
//            if (role == null) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Rôle introuvable.");
//                request.getSession().setAttribute("toastType", "error");
//                response.sendRedirect("UtilisateurServlet?action=lister");
//                return;
//            }
//
//            Utilisateur user = utilisateurDAO.findById(id);
//            if (user == null) {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Utilisateur non trouvé.");
//                request.getSession().setAttribute("toastType", "error");
//                response.sendRedirect("UtilisateurServlet?action=lister");
//                return;
//            }
//
//            user.setNom(nom.trim());
//            user.setPrenom(prenom.trim());
//            user.setEmail(email.trim());
//            user.setLogin(login.trim());
//            user.setRole(role);
//            user.setPourcentage(pourcentage);
//            user.setPlafond(plafond);
//
//            boolean changePassword = false;
//            if (password != null && !password.trim().isEmpty()) {
//                user.setMotDePasse(PasswordUtils.hashPassword(password.trim()));
//                changePassword = true;
//            }
//
//            boolean success = utilisateurDAO.update(user, changePassword);
//
//            if (success) {
//                request.getSession().setAttribute("ToastAdmSuccesNotif", "Modification réussie !");
//                request.getSession().setAttribute("toastType", "success");
//            } else {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification.");
//                request.getSession().setAttribute("toastType", "error");
//            }
//
//            response.sendRedirect("UtilisateurServlet?action=lister");
//
//        } catch (NumberFormatException e) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur interne : " + e.getMessage());
//            request.getSession().setAttribute("toastType", "error");
//            response.sendRedirect("UtilisateurServlet?action=lister");
//        }
//    }
//
//    private void supprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            boolean success = utilisateurDAO.delete(id);
//
//            if (success) {
//                request.getSession().setAttribute("toastMessage", "Utilisateur supprimé avec succès.");
//                request.getSession().setAttribute("toastType", "success");
//            } else {
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression.");
//                request.getSession().setAttribute("toastType", "error");
//            }
//
//            response.sendRedirect("UtilisateurServlet?action=lister");
//
//        } catch (NumberFormatException e) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
//        }
//    }
//
//    private void lister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Utilisateur> utilisateurs = utilisateurDAO.findAllVisible(); // méthode à créer dans DAO
//        request.setAttribute("utilisateurs", utilisateurs);
//        List<Role> roles = RoleDAO.findAllVisible();
//        request.setAttribute("roles", roles);
//        request.getRequestDispatcher("list-user.jsp").forward(request, response);
//    }
//
//    private void showAddUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Role> roles = RoleDAO.findAllVisible();
//        request.setAttribute("roles", roles);
//        request.getRequestDispatcher("add-user.jsp").forward(request, response);
//    }
//    
//    private void handleLogout(HttpServletRequest request, HttpServletResponse response, String who)
//            throws IOException {
//        HttpSession session = request.getSession(false);
//        if (session != null) session.invalidate();
//
//        if ("ADM".equals(who)) {
//            response.sendRedirect("login.jsp");
//        } else {
//            request.getSession().setAttribute("toastMessage", "Déconnexion réussie !");
//            request.getSession().setAttribute("toastType", "success");
//            response.sendRedirect("index.jsp");
//        }
//    }
//
//    private void handleVoirProfil(HttpServletRequest request, HttpServletResponse response)
//            throws IOException, ServletException {
//        String idStr = request.getParameter("id");
//        if (idStr != null) {
//            try {
//                int id = Integer.parseInt(idStr);
//                Utilisateur user = utilisateurDAO.findById(id);
//                if (user != null) {
//                    request.setAttribute("user", user);
//                    request.getRequestDispatcher("profile.jsp").forward(request, response);
//                } else {
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
//                }
//            } catch (NumberFormatException e) {
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
//            }
//        } else {
//            response.sendRedirect("index.jsp");
//        }
//    }
//
//
// 
//
//    // Méthode pour dispatcher vers une page en forward
//    private void responseRedirect(HttpServletRequest request, HttpServletResponse response, String redirectPage)
//            throws ServletException, IOException {
//        request.getRequestDispatcher(redirectPage).forward(request, response);
//    }
//}

package com.spot69.controller;

import com.spot69.dao.RoleDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Role;
import com.spot69.model.Utilisateur;
import com.spot69.utils.PasswordUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet({ "/UtilisateurServlet", "/blok/UtilisateurServlet" })
public class UtilisateurServlet extends HttpServlet {

    private UtilisateurDAO utilisateurDAO;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
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
            case "login":
                login(request, response);
                break;
            case "register":
                register(request, response);
                break;
            case "modifier":
                modifier(request, response);
                break;
            case "supprimer":
                supprimer(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");
        String who = request.getParameter("whoislogoin");

        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "logout":
                handleLogout(request, response, who);
                break;

            case "add":
                showAddUserForm(request, response);
                break;

            case "lister":
                lister(request, response);
                break;

            case "voir":
            case "profil":
                handleVoirProfil(request, response);
                break;

            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String whoislogin = request.getParameter("whoislogin");
        String redirectTo = request.getParameter("redirectTo");

        // Vérification des champs vides
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            StringBuilder message = new StringBuilder("Veuillez remplir ");

            if (username == null || username.isEmpty()) {
                message.append("le nom d'utilisateur");
            }
            if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
                message.append(" et ");
            }
            if (password == null || password.isEmpty()) {
                message.append("le mot de passe");
            }
            message.append(".");

            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
            request.getSession().setAttribute(toastKey, message.toString());

            if (!"redirectTo".equals(redirectTo)) {
                request.getSession().setAttribute("toastType", "error");
            }

            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
            response.sendRedirect(request.getContextPath() + redir);
            return;
        }

        Utilisateur user = utilisateurDAO.findByLoginOrEmail(username);

        if (user == null) {
            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
            String toastValue = "Utilisateur introuvable.";
            request.getSession().setAttribute(toastKey, toastValue);
            if (!"dashboard".equals(redirectTo)) {
                request.getSession().setAttribute("toastType", "error");
            }

            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
            response.sendRedirect(request.getContextPath() + redir);
            return;
        }

        if (!PasswordUtils.checkPassword(password, user.getMotDePasse())) {
            String toastKey = "dashboard".equals(redirectTo) ? "ToastAdmErrorNotif" : "toastMessage";
            String toastValue = "Mot de passe incorrect.";
            request.getSession().setAttribute(toastKey, toastValue);
            if (!"dashboard".equals(redirectTo)) {
                request.getSession().setAttribute("toastType", "error");
            }

            String redir = "dashboard".equals(redirectTo) ? "/blok/login.jsp" : "/index.jsp";
            response.sendRedirect(request.getContextPath() + redir);
            return;
        }

        // Connexion réussie
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        System.out.println("LOGIN --------------");
        System.out.println(user.getId() );
        session.setAttribute("username", user.getLogin());
        session.setAttribute("role", user.getRole().getRoleName());
        Utilisateur freshUser = utilisateurDAO.findById(user.getId());
        if (freshUser != null) {
            request.getSession().setAttribute("user", freshUser);
        }

        // Redirection spéciale si rôle = ADMINISTRATEUR ou SUPER_ADMINISTRATEUR
        if ("ADMINISTRATEUR".equalsIgnoreCase(user.getRole().getRoleName()) || "SUPER_ADMINISTRATEUR".equalsIgnoreCase(user.getRole().getRoleName())) {
            session.setAttribute("ToastAdmSuccesNotif", "Connexion réussie !");
            response.sendRedirect(request.getContextPath() + "/blok/MenuServlet?action=placer-commande");
            return;
        }

        // Sinon redirection selon whoislogin
        if ("dashboard".equals(redirectTo)) {
            session.setAttribute("ToastAdmSuccesNotif", "Connexion réussie !");
            response.sendRedirect(request.getContextPath() + "/blok/MenuServlet?action=placer-commande");
        } else {
            session.setAttribute("toastMessage", "Connexion réussie !");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/blok/MenuServlet?action=placer-commande");
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Récupération des paramètres
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String login = request.getParameter("username");
        String password = request.getParameter("password");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        String dateNaissanceStr = request.getParameter("date_naissance");
        String whoislogin = request.getParameter("whoislogin");

        // Pourcentage par défaut à 0 pour les clients
        BigDecimal pourcentage = BigDecimal.ZERO;
        
        String toastKey = "ADM".equals(whoislogin) ? "ToastAdmErrorNotif" : "toastMessage";
        String toastType = "ADM".equals(whoislogin) ? null : "error";
        String redirectPage = "ADM".equals(whoislogin) ? "add-user.jsp" : "index.jsp";

        // Validation des champs obligatoires
        List<String> missingFields = new ArrayList<>();
        if (nom == null || nom.trim().isEmpty()) missingFields.add("Nom");
        if (prenom == null || prenom.trim().isEmpty()) missingFields.add("Prénom");
        if (login == null || login.trim().isEmpty()) missingFields.add("Identifiant");
        if (password == null || password.trim().isEmpty()) missingFields.add("Mot de passe");

        if (!missingFields.isEmpty()) {
            String message = "Veuillez remplir les champs obligatoires : " + String.join(", ", missingFields);
            request.getSession().setAttribute(toastKey, message);
            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
            responseRedirect(request, response, redirectPage);
            return;
        }

        // Vérifier doublons login/email
        Utilisateur existingUser = utilisateurDAO.findByLoginOrEmail(login);
        if (existingUser != null && login.equalsIgnoreCase(existingUser.getLogin())) {
            request.getSession().setAttribute(toastKey, "Ce nom d'utilisateur est déjà utilisé.");
            if (toastType != null) request.getSession().setAttribute("toastType", toastType);
            responseRedirect(request, response, redirectPage);
            return;
        }

        if (email != null && !email.trim().isEmpty()) {
            existingUser = utilisateurDAO.findByLoginOrEmail(email);
            if (existingUser != null && email.equalsIgnoreCase(existingUser.getEmail())) {
                request.getSession().setAttribute(toastKey, "Cet email est déjà utilisé.");
                if (toastType != null) request.getSession().setAttribute("toastType", toastType);
                responseRedirect(request, response, redirectPage);
                return;
            }
        }

        // Traitement de la date de naissance
        java.sql.Date dateNaissance = null; // Utiliser java.sql.Date explicitement
        if (dateNaissanceStr != null && !dateNaissanceStr.trim().isEmpty()) {
            try {
                java.util.Date parsedDate = DATE_FORMAT.parse(dateNaissanceStr.trim());
                dateNaissance = new java.sql.Date(parsedDate.getTime()); // Conversion en java.sql.Date
                
                // Validation de l'âge
                java.util.Date currentDate = new java.util.Date();
                java.util.Date minBirthDate = new java.util.Date(currentDate.getTime() - (13L * 365 * 24 * 60 * 60 * 1000));
                if (parsedDate.after(minBirthDate)) {
                    request.getSession().setAttribute(toastKey, "Vous devez avoir au moins 13 ans pour vous inscrire.");
                    if (toastType != null) request.getSession().setAttribute("toastType", toastType);
                    responseRedirect(request, response, redirectPage);
                    return;
                }
                
            } catch (ParseException e) {
                request.getSession().setAttribute(toastKey, "Format de date invalide. Utilisez AAAA-MM-JJ.");
                if (toastType != null) request.getSession().setAttribute("toastType", toastType);
                responseRedirect(request, response, redirectPage);
                return;
            }
        }

        // Définir le rôle
        Role role = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            String currentUserRole = (String) session.getAttribute("role");
            if ("ADMINISTRATEUR".equals(currentUserRole) || "SUPER_ADMINISTRATEUR".equals(currentUserRole)) {
                String roleParam = request.getParameter("role");
                if (roleParam != null && !roleParam.trim().isEmpty()) {
                    try {
                        int roleId = Integer.parseInt(roleParam);
                        role = RoleDAO.findById(roleId);
                        if (role == null) {
                            request.getSession().setAttribute(toastKey, "Rôle invalide sélectionné.");
                            responseRedirect(request, response, redirectPage);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        request.getSession().setAttribute(toastKey, "ID du rôle invalide.");
                        responseRedirect(request, response, redirectPage);
                        return;
                    }
                }
            }
        }

        if (role == null) {
            role = RoleDAO.findById(3); // rôle CLIENT par défaut 
        }

        // Création de l'utilisateur
        Utilisateur user = new Utilisateur();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setLogin(login);
        user.setMotDePasse(PasswordUtils.hashPassword(password));
        user.setRole(role);
        user.setPourcentage(pourcentage);
        user.setDateNaissance(dateNaissance); // Définit la date de naissance

        // Insertion dans la base de données
        boolean success = utilisateurDAO.insert(user, telephone, adresse, dateNaissance);

        if (success) {
            request.getSession().setAttribute(toastKey, "Inscription réussie !");
            if (toastType != null) request.getSession().setAttribute("toastType", "success");
        } else {
            request.getSession().setAttribute(toastKey, "Erreur technique lors de l'inscription.");
            if (toastType != null) request.getSession().setAttribute("toastType", "error");
        }

        responseRedirect(request, response, redirectPage);
    }
    private void modifier(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String plafondStr = request.getParameter("plafond");
            int plafond = 0;

            if (plafondStr != null && !plafondStr.trim().isEmpty()) {
                try {
                    plafond = Integer.parseInt(plafondStr.trim());
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("ToastAdmErrorNotif", "Plafond invalide.");
                    request.getSession().setAttribute("toastType", "error");
                    response.sendRedirect("UtilisateurServlet?action=lister");
                    return;
                }
            }

            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String statut = request.getParameter("statut");
            String email = request.getParameter("email");
            String login = request.getParameter("username");
            String password = request.getParameter("password");
            String roleIdStr = request.getParameter("role");
            String telephone = request.getParameter("telephone");
            String adresse = request.getParameter("adresse");
            String dateNaissanceStr = request.getParameter("date_naissance"); // Nouveau
            
            String pourcentageStr = request.getParameter("pourcentage");
            BigDecimal pourcentage = null;

            if (pourcentageStr != null && !pourcentageStr.isEmpty()) {
                try {
                    BigDecimal temp = new BigDecimal(pourcentageStr);

                    // Vérifier que c'est bien un entier (pas de décimales)
                    if (temp.stripTrailingZeros().scale() > 0) {
                        request.getSession().setAttribute("ToastAdmErrorNotif", "Le pourcentage doit être un entier.");
                       request.getSession().setAttribute("toastType", "error");
                        response.sendRedirect("UtilisateurServlet?action=lister");
                        return;
                    }

                    // Diviser par 100
                    pourcentage = temp.divide(BigDecimal.valueOf(100));

                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("ToastAdmErrorNotif", "Le pourcentage doit être un nombre entier valide.");
                   request.getSession().setAttribute("toastType", "error");
                   response.sendRedirect("UtilisateurServlet?action=lister");
                    return;
                }
            } else {
                pourcentage = BigDecimal.ZERO; // valeur par défaut si vide
            }

            if (nom == null || prenom == null || email == null || login == null || roleIdStr == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Champs requis manquants.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("UtilisateurServlet?action=lister");
                return;
            }

            int roleId = Integer.parseInt(roleIdStr);
            Role role = RoleDAO.findById(roleId);
            if (role == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Rôle introuvable.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("UtilisateurServlet?action=lister");
                return;
            }

            Utilisateur user = utilisateurDAO.findById(id);
            if (user == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Utilisateur non trouvé.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("UtilisateurServlet?action=lister");
                return;
            }

            // Traitement de la date de naissance
            Date dateNaissance = null;
            if (dateNaissanceStr != null && !dateNaissanceStr.trim().isEmpty()) {
                try {
                    java.util.Date parsedDate = DATE_FORMAT.parse(dateNaissanceStr.trim());
                    dateNaissance = new Date(parsedDate.getTime());
                    
                    // Validation de l'âge
                    java.util.Date currentDate = new java.util.Date();
                    java.util.Date minBirthDate = new java.util.Date(currentDate.getTime() - (13L * 365 * 24 * 60 * 60 * 1000));
                    if (parsedDate.after(minBirthDate)) {
                        request.getSession().setAttribute("ToastAdmErrorNotif", "L'utilisateur doit avoir au moins 13 ans.");
                        request.getSession().setAttribute("toastType", "error");
                        response.sendRedirect("UtilisateurServlet?action=lister");
                        return;
                    }
                    
                } catch (ParseException e) {
                    request.getSession().setAttribute("ToastAdmErrorNotif", "Format de date invalide. Utilisez AAAA-MM-JJ.");
                    request.getSession().setAttribute("toastType", "error");
                    response.sendRedirect("UtilisateurServlet?action=lister");
                    return;
                }
            }

            user.setNom(nom.trim());
            user.setPrenom(prenom.trim());
            user.setEmail(email.trim());
            user.setStatut(statut.trim());
            user.setLogin(login.trim());
            user.setRole(role);
            user.setPourcentage(pourcentage);
            user.setPlafond(plafond);
            user.setTelephone(telephone != null ? telephone.trim() : null);
            user.setAdresse(adresse != null ? adresse.trim() : null);
            user.setDateNaissance(dateNaissance); // Définit la date de naissance

            boolean changePassword = false;
            if (password != null && !password.trim().isEmpty()) {
                user.setMotDePasse(PasswordUtils.hashPassword(password.trim()));
                changePassword = true;
            }

            boolean success = utilisateurDAO.update(user, changePassword);

            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Modification réussie !");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification.");
                request.getSession().setAttribute("toastType", "error");
            }

            response.sendRedirect("UtilisateurServlet?action=lister");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur interne : " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("UtilisateurServlet?action=lister");
        }
    }

    private void supprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = utilisateurDAO.delete(id);

            if (success) {
                request.getSession().setAttribute("toastMessage", "Utilisateur supprimé avec succès.");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression.");
                request.getSession().setAttribute("toastType", "error");
            }

            response.sendRedirect("UtilisateurServlet?action=lister");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void lister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAllVisible();
        request.setAttribute("utilisateurs", utilisateurs);
        List<Role> roles = RoleDAO.findAllVisible();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("list-user.jsp").forward(request, response);
    }

    private void showAddUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Role> roles = RoleDAO.findAllVisible();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("add-user.jsp").forward(request, response);
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, String who)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        if ("ADM".equals(who)) {
            response.sendRedirect("login.jsp");
        } else {
            request.getSession().setAttribute("toastMessage", "Déconnexion réussie !");
            request.getSession().setAttribute("toastType", "success");
            response.sendRedirect("index.jsp");
        }
    }

    private void handleVoirProfil(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Utilisateur user = utilisateurDAO.findById(id);
                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("profile.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur introuvable");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    // Méthode pour dispatcher vers une page en forward
    private void responseRedirect(HttpServletRequest request, HttpServletResponse response, String redirectPage)
            throws ServletException, IOException {
        request.getRequestDispatcher(redirectPage).forward(request, response);
    }
}
