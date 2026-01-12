package com.spot69.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spot69.dao.*;
import com.spot69.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/PointServlet", "/blok/PointServlet"})
public class PointServlet extends HttpServlet {

    private PointManagerDAO pointManagerDAO;
    private PointConfigDAO pointConfigDAO;
    private UtilisateurDAO utilisateurDAO;
    private CommandeDAO commandeDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        pointManagerDAO = new PointManagerDAO();
        pointConfigDAO = new PointConfigDAO();
        utilisateurDAO = new UtilisateurDAO();
        commandeDAO = new CommandeDAO();
        
        // Configurer Gson pour le formatage des dates
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gson = gsonBuilder.create();
    }

    private void setupEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("liste-points.jsp");
            return;
        }

        switch (action) {
            case "attribuerPoints":
                handleAttribuerPoints(request, response);
                break;
            case "creerConfig":
                handleCreerConfig(request, response);
                break;
            case "modifierConfig":
                handleModifierConfig(request, response);
                break;
            case "supprimerConfig":
                handleSupprimerConfig(request, response);
                break;
            case "utiliserPoints":
                handleUtiliserPoints(request, response);
                break;
            case "mettreAJourPointsProduit":
                handleMettreAJourPointsProduit(request, response);
                break;
            case "mettreAJourPointsCategorie":
                handleMettreAJourPointsCategorie(request, response);
                break;
            case "expirerPoints":
                handleExpirerPoints(request, response);
                break;
            case "nettoyerJournalieres":
                handleNettoyerJournalieres(request, response);
                break;
            default:
                response.sendRedirect("liste-points.jsp");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            handleLister(request, response);
            return;
        }

        switch (action) {
            case "lister":
                handleLister(request, response);
                break;
            case "configurations":
                handleConfigurations(request, response);
                break;
            case "statistiques":
                handleStatistiques(request, response);
                break;
            case "add":
                handleAddPage(request, response);
                break;
            case "edit":
                handleEditPage(request, response);
                break;
            case "getConfigurationsJSON":
                handleConfigurationsJSON(request, response);
                break;
            case "getStatistiquesJSON":
                handleStatistiquesJSON(request, response);
                break;
            case "getPointsUtilisateurJSON":
                handlePointsUtilisateurJSON(request, response);
                break;
            case "utiliserPoints":
                handleUtiliserPointsPage(request, response);
                break;
            case "formConfig":
                handleFormConfig(request, response);
                break;
            case "getConfigurationsByType":  // AJOUTEZ CETTE LIGNE
                handleGetConfigurationsByType(request, response);
                break;
            case "detailConfig":
                handleDetailConfig(request, response);
                break;
            default:
                response.sendRedirect("liste-points.jsp");
                break;
        }
    }

    // ======= Handlers GET =======

    private void handleLister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer les filtres
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            Date dateDebut = null;
            Date dateFin = null;

            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            } else {
                // Défaut : début du mois
                LocalDate today = LocalDate.now();
                dateDebut = Date.valueOf(today.withDayOfMonth(1));
            }

            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            } else {
                // Défaut : aujourd'hui
                dateFin = Date.valueOf(LocalDate.now());
            }

            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Vérifier les permissions si c'est un admin qui voit les points d'un autre utilisateur
            String userIdParam = request.getParameter("userId");
            int targetUserId = userId;
            
            if (userIdParam != null && !userIdParam.isEmpty()) {
                // Vérifier si l'utilisateur a le droit de voir les points d'autres utilisateurs
                Utilisateur currentUser = utilisateurDAO.findById(userId);
                if (currentUser != null && (currentUser.getRole().equals("ADMINISTRATEUR") || 
                                           currentUser.getRole().equals("MANAGEUR") ||
                                           currentUser.getRole().equals("RESPONSABLE"))) {
                    targetUserId = Integer.parseInt(userIdParam);
                }
            }

            // Récupérer la liste des points
            List<Point> points = pointManagerDAO.getPointsParUtilisateur(targetUserId, dateDebut, dateFin);
            
            // Récupérer le total des points
            int totalPoints = pointManagerDAO.getTotalPointsUtilisateur(targetUserId);
            
            // Récupérer l'utilisateur cible
            Utilisateur targetUser = utilisateurDAO.findById(targetUserId);

            request.setAttribute("points", points);
            request.setAttribute("totalPoints", totalPoints);
            request.setAttribute("targetUser", targetUser);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);
            request.setAttribute("userId", targetUserId);

            request.getRequestDispatcher("liste-points.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("liste-points.jsp");
        }
    }
    
    private void handleConfigurations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== DEBUG handleConfigurations ===");
        System.out.println("Méthode appelée: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Action param: " + request.getParameter("action"));
        
        try {
            String typeFilter = request.getParameter("typeFilter");
            String statutFilter = request.getParameter("statutFilter");
            
            System.out.println("typeFilter: " + typeFilter);
            System.out.println("statutFilter: " + statutFilter);
            
            List<PointConfig> configs = null;
            
            if (typeFilter != null && !typeFilter.isEmpty()) {
                System.out.println("Filtrage par type: " + typeFilter);
                configs = pointConfigDAO.getConfigsByType(typeFilter);
                System.out.println("Nombre de configs trouvées pour type " + typeFilter + ": " + 
                    (configs != null ? configs.size() : 0));
            } else {
                System.out.println("Aucun filtre de type - chargement de tous les types");
                // Par défaut, montrer toutes les configurations actives
                configs = new ArrayList<>();
                String[] types = {"PRODUIT", "CATEGORIE", "SOUS_CATEGORIE", "QTE_COMMANDE_JOURNALIERE", "MONTANT_TOTAL"};
                for (String type : types) {
                    List<PointConfig> configsByType = pointConfigDAO.getConfigsByType(type);
                    System.out.println("Type " + type + ": " + 
                        (configsByType != null ? configsByType.size() : 0) + " configs");
                    if (configsByType != null) {
                        configs.addAll(configsByType);
                    }
                }
                System.out.println("Total configs toutes catégories: " + configs.size());
            }

            // Debug détaillé des configurations
            if (configs != null && !configs.isEmpty()) {
                System.out.println("=== DÉTAIL DES CONFIGS ===");
                for (int i = 0; i < configs.size(); i++) {
                    PointConfig config = configs.get(i);
                    System.out.println("Config " + (i+1) + ":");
                    System.out.println("  ID: " + config.getId());
                    System.out.println("  Type: " + config.getTypeConfig());
                    System.out.println("  RefId: " + config.getRefId());
                    System.out.println("  Points: " + config.getPoints());
                    System.out.println("  Condition: " + config.getConditionType() + " " + config.getConditionValeur());
                    System.out.println("  Statut: " + config.getStatut());
                    System.out.println("  Date début: " + config.getDateDebut());
                    System.out.println("  Date fin: " + config.getDateFin());
                }
                System.out.println("=== FIN DÉTAIL ===");
            } else {
                System.out.println("Aucune configuration trouvée");
            }

            request.setAttribute("configurations", configs);
            request.setAttribute("typeFilter", typeFilter);
            request.setAttribute("statutFilter", statutFilter);

            System.out.println("Forward vers: configurations-points.jsp");
            request.getRequestDispatcher("configurations-points.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("=== ERREUR DANS handleConfigurations ===");
            System.out.println("Exception: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Stack trace:");
            e.printStackTrace();
            System.out.println("=== FIN ERREUR ===");
            
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("configurations-points.jsp");
        }
        
        System.out.println("=== FIN handleConfigurations ===");
    }
    
    private void handleStatistiques(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Récupérer le total des points
            int totalPoints = pointManagerDAO.getTotalPointsUtilisateur(userId);
            
            // Récupérer les points du mois (exemple)
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfMonth = today.withDayOfMonth(1);
            Date dateDebut = Date.valueOf(firstDayOfMonth);
            Date dateFin = Date.valueOf(today);
            
            List<Point> pointsMois = pointManagerDAO.getPointsParUtilisateur(userId, dateDebut, dateFin);
            int pointsMoisTotal = pointsMois.stream().mapToInt(Point::getPointsObtenus).sum();
            
            // Créer les statistiques
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalPoints);
            stats.put("mois", pointsMoisTotal);
            stats.put("semaine", 0); // À implémenter si nécessaire
            stats.put("pointsMois", pointsMois);

            request.setAttribute("statistiques", stats);

            request.getRequestDispatcher("statistiques-points.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("statistiques-points.jsp");
        }
    }
    
    private void handleAddPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.getRequestDispatcher("form-config-point.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleEditPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String configIdStr = request.getParameter("id");
            if (configIdStr == null || configIdStr.isEmpty()) {
                setErrorNotif(request, "ID de configuration manquant");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int configId = Integer.parseInt(configIdStr);
            PointConfig config = pointConfigDAO.getConfigById(configId);
            
            if (config == null) {
                setErrorNotif(request, "Configuration non trouvée");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }

            request.setAttribute("configuration", config);
            request.getRequestDispatcher("form-config-point.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleConfigurationsJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String typeFilter = request.getParameter("type");
            
            List<PointConfig> configs;
            if (typeFilter != null && !typeFilter.isEmpty()) {
                configs = pointConfigDAO.getConfigsByType(typeFilter);
            } else {
                configs = new ArrayList<>();
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(configs, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private void handleStatistiquesJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non authentifié");
                return;
            }

            // Récupérer le total des points
            int totalPoints = pointManagerDAO.getTotalPointsUtilisateur(userId);
            
            // Récupérer les points du mois
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfMonth = today.withDayOfMonth(1);
            Date dateDebut = Date.valueOf(firstDayOfMonth);
            Date dateFin = Date.valueOf(today);
            
            List<Point> pointsMois = pointManagerDAO.getPointsParUtilisateur(userId, dateDebut, dateFin);
            int pointsMoisTotal = pointsMois.stream().mapToInt(Point::getPointsObtenus).sum();
            
            // Créer les statistiques
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalPoints);
            stats.put("mois", pointsMoisTotal);
            stats.put("semaine", 0);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(stats, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private void handlePointsUtilisateurJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String userIdStr = request.getParameter("userId");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            int userId = Integer.parseInt(userIdStr);
            Date dateDebut = Date.valueOf(dateDebutStr);
            Date dateFin = Date.valueOf(dateFinStr);

            List<Point> points = pointManagerDAO.getPointsParUtilisateur(userId, dateDebut, dateFin);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(points, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private void handleUtiliserPointsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Récupérer le total des points disponibles
            int totalPoints = pointManagerDAO.getTotalPointsUtilisateur(userId);
            
            // Récupérer l'utilisateur
            Utilisateur utilisateur = utilisateurDAO.findById(userId);

            request.setAttribute("totalPoints", totalPoints);
            request.setAttribute("utilisateur", utilisateur);

            request.getRequestDispatcher("utiliser-points.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("liste-points.jsp");
        }
    }
    
    private void handleFormConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String configIdStr = request.getParameter("id");
            PointConfig config = null;
            
            if (configIdStr != null && !configIdStr.isEmpty()) {
                int configId = Integer.parseInt(configIdStr);
                config = pointConfigDAO.getConfigById(configId);
            }

            request.setAttribute("configuration", config);
            request.getRequestDispatcher("form-config-point.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleDetailConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String configIdStr = request.getParameter("id");
            
            if (configIdStr == null || configIdStr.isEmpty()) {
                setErrorNotif(request, "ID de configuration manquant");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int configId = Integer.parseInt(configIdStr);
            PointConfig config = pointConfigDAO.getConfigById(configId);
            
            if (config == null) {
                setErrorNotif(request, "Configuration non trouvée");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }

            request.setAttribute("configuration", config);
            request.getRequestDispatcher("detail-config-point.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }

    // ======= Handlers POST =======

    private void handleAttribuerPoints(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer currentUserId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (currentUserId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String commandeIdStr = request.getParameter("commandeId");
            String utilisateurIdStr = request.getParameter("utilisateurId");
            
            if (commandeIdStr == null || utilisateurIdStr == null) {
                setErrorNotif(request, "Paramètres manquants");
                response.sendRedirect("liste-commandes.jsp");
                return;
            }
            
            int commandeId = Integer.parseInt(commandeIdStr);
            int utilisateurId = Integer.parseInt(utilisateurIdStr);
            
            // Récupérer la commande
            Commande commande = commandeDAO.getCommandeById(commandeId);
            if (commande == null) {
                setErrorNotif(request, "Commande non trouvée");
                response.sendRedirect("liste-commandes.jsp");
                return;
            }
            
            // Récupérer les détails de la commande
            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
            
            // Attribuer les points
            Map<String, Object> result = pointManagerDAO.attribuerPointsPourCommande(commande, details,request.getRemoteAddr());
            
            if ((Boolean) result.get("success")) {
                setSuccessNotif(request, "Points attribués avec succès: " + result.get("totalPoints") + " points");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            } else {
                setErrorNotif(request, "Erreur lors de l'attribution des points: " + result.get("error"));
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("liste-commandes.jsp");
        }
    }
    
    private void handleGetConfigurationsByType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== DEBUG handleGetConfigurationsByType ===");
        System.out.println("Type demandé: " + request.getParameter("type"));
        
        try {
            String type = request.getParameter("type");
            
            if (type == null || type.isEmpty()) {
                sendJsonError(response, "Le paramètre 'type' est requis", 400);
                return;
            }
            
            List<PointConfig> configs = pointConfigDAO.getConfigsByType(type);
            System.out.println("Nombre de configs trouvées: " + (configs != null ? configs.size() : 0));
            
            // Convertir en JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            Gson gson = new Gson();
            String json = gson.toJson(configs);
            System.out.println("JSON envoyé: " + (json != null && json.length() > 200 ? json.substring(0, 200) + "..." : json));
            
            response.getWriter().write(json);
            
        } catch (Exception e) {
            System.out.println("Erreur dans handleGetConfigurationsByType: " + e.getMessage());
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void sendJsonError(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
    
    private void handleCreerConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Récupérer les paramètres
            String typeConfig = request.getParameter("typeConfig");
            String refIdStr = request.getParameter("refId");
            String pointsStr = request.getParameter("points");
            String conditionValeurStr = request.getParameter("conditionValeur");
            String conditionType = request.getParameter("conditionType");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            String statut = request.getParameter("statut");
            
            // Validation
            if (typeConfig == null || pointsStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("PointServlet?action=add");
                return;
            }
            
            // Créer l'objet PointConfig
            PointConfig config = new PointConfig();
            config.setTypeConfig(typeConfig);
            
            if (refIdStr != null && !refIdStr.isEmpty()) {
                config.setRefId(Integer.parseInt(refIdStr));
            }
            
            config.setPoints(Integer.parseInt(pointsStr));
            
            if (conditionValeurStr != null && !conditionValeurStr.isEmpty()) {
                config.setConditionValeur(Double.parseDouble(conditionValeurStr));
            }
            
            config.setConditionType(conditionType != null ? conditionType : "=");
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                config.setDateDebut(Timestamp.valueOf(dateDebutStr + " 00:00:00"));
            } else {
                config.setDateDebut(new Timestamp(System.currentTimeMillis()));
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                config.setDateFin(Timestamp.valueOf(dateFinStr + " 23:59:59"));
            }
            
            config.setUtilisateurId(userId);
            config.setStatut(statut != null ? statut : "ACTIF");
            
            // Sauvegarder
            int configId = pointConfigDAO.creerConfig(config);
            
            if (configId > 0) {
                setSuccessNotif(request, "Configuration créée avec succès");
                response.sendRedirect("PointServlet?action=detailConfig&id=" + configId);
            } else {
                setErrorNotif(request, "Erreur lors de la création de la configuration");
                response.sendRedirect("PointServlet?action=add");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=add");
        }
    }
    
    private void handleModifierConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String configIdStr = request.getParameter("id");
            if (configIdStr == null || configIdStr.isEmpty()) {
                setErrorNotif(request, "ID de configuration manquant");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int configId = Integer.parseInt(configIdStr);
            
            // Récupérer la configuration existante
            PointConfig config = pointConfigDAO.getConfigById(configId);
            if (config == null) {
                setErrorNotif(request, "Configuration non trouvée");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            // Mettre à jour les valeurs
            String typeConfig = request.getParameter("typeConfig");
            String refIdStr = request.getParameter("refId");
            String pointsStr = request.getParameter("points");
            String conditionValeurStr = request.getParameter("conditionValeur");
            String conditionType = request.getParameter("conditionType");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            String statut = request.getParameter("statut");
            
            config.setTypeConfig(typeConfig);
            
            if (refIdStr != null && !refIdStr.isEmpty()) {
                config.setRefId(Integer.parseInt(refIdStr));
            } else {
                config.setRefId(null);
            }
            
            config.setPoints(Integer.parseInt(pointsStr));
            
            if (conditionValeurStr != null && !conditionValeurStr.isEmpty()) {
                config.setConditionValeur(Double.parseDouble(conditionValeurStr));
            } else {
                config.setConditionValeur(null);
            }
            
            config.setConditionType(conditionType != null ? conditionType : "=");
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                config.setDateDebut(Timestamp.valueOf(dateDebutStr + " 00:00:00"));
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                config.setDateFin(Timestamp.valueOf(dateFinStr + " 23:59:59"));
            } else {
                config.setDateFin(null);
            }
            
            config.setStatut(statut != null ? statut : "ACTIF");
            
            // Sauvegarder
            boolean success = pointConfigDAO.modifierConfig(config);
            
            if (success) {
                setSuccessNotif(request, "Configuration modifiée avec succès");
                response.sendRedirect("PointServlet?action=detailConfig&id=" + configId);
            } else {
                setErrorNotif(request, "Erreur lors de la modification de la configuration");
                response.sendRedirect("PointServlet?action=edit&id=" + configId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=edit&id=" + request.getParameter("id"));
        }
    }
    
    private void handleSupprimerConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String configIdStr = request.getParameter("id");
            if (configIdStr == null || configIdStr.isEmpty()) {
                setErrorNotif(request, "ID de configuration manquant");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int configId = Integer.parseInt(configIdStr);
            
            // Supprimer (soft delete)
            boolean success = pointConfigDAO.supprimerConfig(configId, userId);
            
            if (success) {
                setSuccessNotif(request, "Configuration supprimée avec succès");
            } else {
                setErrorNotif(request, "Erreur lors de la suppression de la configuration");
            }
            
            response.sendRedirect("PointServlet?action=configurations");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleUtiliserPoints(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String pointsStr = request.getParameter("points");
            String raison = request.getParameter("raison");
            String description = request.getParameter("description");
            
            if (pointsStr == null || raison == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("PointServlet?action=utiliserPoints");
                return;
            }
            
            int points = Integer.parseInt(pointsStr);
            
            // Vérifier que l'utilisateur a assez de points
            int totalPoints = pointManagerDAO.getTotalPointsUtilisateur(userId);
            if (points > totalPoints) {
                setErrorNotif(request, "Vous n'avez que " + totalPoints + " points disponibles");
                response.sendRedirect("PointServlet?action=utiliserPoints");
                return;
            }
            
            // TODO: Implémenter la méthode utiliserPoints dans PointManagerDAO
            setErrorNotif(request, "Fonctionnalité 'utiliserPoints' non implémentée");
            response.sendRedirect("PointServlet?action=utiliserPoints");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=utiliserPoints");
        }
    }
    
    private void handleMettreAJourPointsProduit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String produitIdsStr = request.getParameter("produitIds");
            String pointsStr = request.getParameter("points");
            String typeConfig = request.getParameter("typeConfig");
            
            if (produitIdsStr == null || pointsStr == null || typeConfig == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int points = Integer.parseInt(pointsStr);
            String[] produitIds = produitIdsStr.split(",");
            
            int count = 0;
            for (String produitIdStr : produitIds) {
                try {
                    int produitId = Integer.parseInt(produitIdStr.trim());
                    
                    // Vérifier si une configuration existe déjà pour ce produit
                    PointConfig existingConfig = pointConfigDAO.getConfigForProduit(produitId);
                    
                    if (existingConfig != null) {
                        // Mettre à jour la configuration existante
                        existingConfig.setPoints(points);
                        existingConfig.setTypeConfig(typeConfig);
                        pointConfigDAO.modifierConfig(existingConfig);
                    } else {
                        // Créer une nouvelle configuration
                        PointConfig config = new PointConfig();
                        config.setTypeConfig(typeConfig);
                        config.setRefId(produitId);
                        config.setPoints(points);
                        config.setUtilisateurId(userId);
                        config.setStatut("ACTIF");
                        config.setDateDebut(new Timestamp(System.currentTimeMillis()));
                        
                        pointConfigDAO.creerConfig(config);
                    }
                    
                    count++;
                } catch (NumberFormatException e) {
                    // Ignorer les IDs invalides
                }
            }
            
            setSuccessNotif(request, count + " produits mis à jour avec " + points + " points chacun");
            response.sendRedirect("PointServlet?action=configurations");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleMettreAJourPointsCategorie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String categorieIdStr = request.getParameter("categorieId");
            String sousCategorieIdStr = request.getParameter("sousCategorieId");
            String pointsStr = request.getParameter("points");
            String typeConfig = request.getParameter("typeConfig");
            
            if (categorieIdStr == null || pointsStr == null || typeConfig == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            int categorieId = Integer.parseInt(categorieIdStr);
            int points = Integer.parseInt(pointsStr);
            
            // Déterminer si c'est une catégorie ou sous-catégorie
            String configType = "CATEGORIE";
            Integer refId = categorieId;
            
            if (sousCategorieIdStr != null && !sousCategorieIdStr.isEmpty()) {
                configType = "SOUS_CATEGORIE";
                refId = Integer.parseInt(sousCategorieIdStr);
            }
            
            // Vérifier si une configuration existe déjà
            PointConfig existingConfig = pointConfigDAO.getConfigForCategorie(refId);
            
            boolean success;
            if (existingConfig != null) {
                // Mettre à jour
                existingConfig.setPoints(points);
                existingConfig.setTypeConfig(configType);
                success = pointConfigDAO.modifierConfig(existingConfig);
            } else {
                // Créer
                PointConfig config = new PointConfig();
                config.setTypeConfig(configType);
                config.setRefId(refId);
                config.setPoints(points);
                config.setUtilisateurId(userId);
                config.setStatut("ACTIF");
                config.setDateDebut(new Timestamp(System.currentTimeMillis()));
                
                pointConfigDAO.creerConfig(config);
                success = true;
            }
            
            if (success) {
                String target = (configType.equals("CATEGORIE")) ? "catégorie" : "sous-catégorie";
                setSuccessNotif(request, points + " points définis pour la " + target);
                response.sendRedirect("PointServlet?action=configurations");
            } else {
                setErrorNotif(request, "Erreur lors de la mise à jour");
                response.sendRedirect("PointServlet?action=configurations");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleExpirerPoints(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().equals("ADMINISTRATEUR") && !user.getRole().equals("MANAGEUR"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            // TODO: Implémenter la méthode expirerPoints dans PointManagerDAO
            setErrorNotif(request, "Fonctionnalité 'expirerPoints' non implémentée");
            response.sendRedirect("PointServlet?action=configurations");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }
    
    private void handleNettoyerJournalieres(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().equals("ADMINISTRATEUR") && !user.getRole().equals("MANAGEUR"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("PointServlet?action=configurations");
                return;
            }
            
            // TODO: Implémenter la méthode nettoyerCommandesJournalieresAnciennes
            setErrorNotif(request, "Fonctionnalité 'nettoyerJournalieres' non implémentée");
            response.sendRedirect("PointServlet?action=configurations");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PointServlet?action=configurations");
        }
    }

    // ======= Méthodes utilitaires =======

    private void setSuccessNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmErrorNotif", message);
        request.getSession().setAttribute("toastType", "error");
    }
}