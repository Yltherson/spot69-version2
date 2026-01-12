package com.spot69.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spot69.dao.CommandeDAO;
import com.spot69.dao.CreditDAO;
import com.spot69.dao.DepenseDAO;
import com.spot69.dao.MenuCategorieDAO;
import com.spot69.dao.NotificationDAO;
import com.spot69.dao.PanierDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.dao.PlatDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.dao.RoleDAO;
import com.spot69.model.Commande;
import com.spot69.model.CommandeDetail;
import com.spot69.model.Credit;
import com.spot69.model.Depense;
import com.spot69.model.MenuCategorie;
import com.spot69.model.Notification;
import com.spot69.model.Panier;
import com.spot69.model.Plat;
import com.spot69.model.Produit;
import com.spot69.model.RapportCommande;
import com.spot69.model.Rayon;
import com.spot69.model.RayonAvecPlats;
import com.spot69.model.RayonHierarchique;
import com.spot69.model.Role;
import com.spot69.model.TableRooftop;
import com.spot69.model.Utilisateur;
import com.spot69.model.Versement;
import com.spot69.utils.ImpressionCommande;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;


@WebServlet({"/CommandeServlet", "/blok/CommandeServlet"})
public class CommandeServlet extends HttpServlet {

   
	private CommandeDAO commandeDAO;
    private  PlatDAO platDAO;
    private  CreditDAO creditDAO;
    private UtilisateurDAO utilisateurDAO;
    private ProduitDAO produitDAO;
    private MenuCategorieDAO menuCategorieDAO;

    @Override
    public void init() throws ServletException {
        commandeDAO = new CommandeDAO();
        platDAO = new PlatDAO();
        utilisateurDAO = new UtilisateurDAO();
        creditDAO = new CreditDAO();
        produitDAO = new ProduitDAO();
        menuCategorieDAO = new MenuCategorieDAO();
    }
    private String genererNumeroCommande() {
        LocalDateTime now = LocalDateTime.now();
        String numeroCommande;
        
        do {
            // Première partie : YYMMDD (année, mois, jour)
            String part1 = String.format("%02d%02d%02d",
                now.getYear() % 100,  // 2 derniers chiffres de l'année
                now.getMonthValue(),  // mois
                now.getDayOfMonth()); // jour
            
            // Deuxième partie : HHMMSS (heures, minutes, secondes)
            String part2 = String.format("%02d%02d%02d",
                now.getHour(),    // heures
                now.getMinute(),  // minutes
                now.getSecond()); // secondes
            
            numeroCommande = "CMD-" + part1 + "-" + part2;
        } while (commandeDAO.existeNumeroCommande(numeroCommande));
        
        return numeroCommande;
    }


    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "add":
                afficherFormulaireAjout(request, response);
                break;
            case "go-to-menu":
                goToMenu(request, response);
                break;
            case "get-commande-traitement":
                getCommandeTraitement(request, response);
                break;    
            case "edit":
                afficherFormulaireEdition(request, response);
                break;
            case "delete":
                traiterSuppressionCommande(request, response);
                break;
            case "details":
                afficherDetailsCommande(request, response);
                break;
            case "lister":
                listerCommandes(request, response);
                break;
            case "getcmd-by-filtres-for-user":
            	ListerCommandesParFiltresForUserId(request, response);
                break;
            case "get-all-cmd-credit-for-client":
            	listerCommandesCreditAll(request, response);
                break;
            case "lister-credit":
            	listerCommandesCredit(request, response);
                break;
            case "historique-versement":
                listerHistoriqueVersements(request, response);
                break;
            case "liste-toutes-commande":
                listerToutesCommandes(request, response);
                break; 
            case "rapport-commandes-by-filtres":
            	rapportCommandesByFiltres(request, response);
                break; 
            case "rapport-commandes-cashed-by-caissiere-with-filtres-json":
                rapportCommandesCashedByCaissiereWithFiltresJSON(request, response);
                break;
            case "supprimer":
            	traiterSuppressionCommande(request, response);
                break;
            case "placer-commande":
            	placerCommande(request, response);
            	break;
            case "liste-commande-par-staff":
            	listeCommandeEmisParStaff(request, response);
                break;
                
            
            case "getdetailsjson":
                getDetailsAsJson(request, response);
                break;
            case "getallcommandesbyfiltres":
                getAllCommandesByFiltres(request, response);
                break;
                

                // 🚀 Tes nouveaux cas :
                case "caissiere-commandes-cashed":
                    getCaissiereCommandeCashed(request, response);
                    break;
                case "user-commandes-cashed-by-caissiere":
                    getUserCommandeCachedByCaissiere(request, response);
                    break;
                case "all-commandes-grouped-by-caissiere":
                    getAllCommandesGroupedByCaissiere(request, response);
                    break;
                    // Dans CommandeServlet.java, ajoutez ce nouveau cas dans doGet():
                case "imprimer":
                    imprimerCommande(request, response);
                    break;
                case "handle-proforma":
                    handleProforma(request, response);
                    break;
                case "handle-commande":  // <-- NOUVEAU CAS
                    System.out.println(">>> Appel de handleCommande");
                    handleCommande(request, response);
                    break;

            default:
                listerCommandes(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        System.out.println(action);
       
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "ajouter":
                traiterAjoutCommande(request, response);
                break;
            case "handle-proforma":
                handleProforma(request, response);
                break;
            case "validercredit":
                validerCreditAction(request, response);
                break;
            case "handle-commande":  // <-- NOUVEAU CAS
                System.out.println(">>> Appel de handleCommande");
                handleCommande(request, response);
                break;

            case "modifier":
                traiterModificationCommande(request, response);
                break;
            case "modifierstatut":
                System.out.println(">>> Appel de updateStatutCommandes");
                updateStatutCommandes(request, response);
                break;
            case "valider":
                System.out.println(">>> Appel de validerCommandes");
                validerCommandes(request, response);
                break;
            default:
                System.out.println(">>> le default");
                response.sendRedirect("blok/CommandeServlet?action=lister");
                break;
        }
    }

    

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     
        // Autres préparations (ex: liste clients...)
        request.getRequestDispatcher("ajouter-commande.jsp").forward(request, response);
    }
    
    private void goToMenu(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	int uId = 0;
    	 HttpSession session = request.getSession(false);
         if (session != null && session.getAttribute("userId") != null) {
        	 uId = (int) session.getAttribute("userId");
         }
        // Autres préparations (ex: liste clients...)
        request.getRequestDispatcher("MenuServlet?action=categorie-parente").forward(request, response);
    }
    
    private void placerCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	List<Plat> plats = platDAO.listerPlats();
        request.setAttribute("plats", plats);
       
		List<Utilisateur> cLients = utilisateurDAO.findAllVisibleByRole("CLIENT");
        request.setAttribute("cLients", cLients);
    	
        // Autres préparations (ex: liste clients...)
        request.getRequestDispatcher("commander.jsp").forward(request, response);
    }

    

    private void afficherFormulaireEdition(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("CommandeServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Commande commande = commandeDAO.getCommandeAvecDetails(id);
            if (commande == null) {
                response.sendRedirect("CommandeServlet?action=lister");
                return;
            }
            request.setAttribute("commande", commande);
            request.getRequestDispatcher("modifier-commande.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("CommandeServlet?action=lister");
        }
    }

    private void afficherDetailsCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("CommandeServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Commande commande = commandeDAO.getCommandeAvecDetails(id);
            if (commande == null) {
                response.sendRedirect("CommandeServlet?action=lister");
                return;
            }
            request.setAttribute("commande", commande);
            request.getRequestDispatcher("details-commande.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("CommandeServlet?action=lister");
        }
    }

    private void validerCreditAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== [Servlet] Début validerCreditAction ===");

        try {
            // Récupération du creditId
            String creditParam = request.getParameter("creditId");
            System.out.println("[Servlet] Param creditId reçu = " + creditParam);

            int creditId = Integer.parseInt(creditParam);

            // Récupération utilisateur connecté
            HttpSession session = request.getSession(false);
            Integer utilisateurId = (session != null) ? (Integer) session.getAttribute("userId") : null;

            System.out.println("[Servlet] utilisateurId trouvé en session = " + utilisateurId);

            if (utilisateurId == null) {
                System.out.println("[Servlet] ERREUR : Aucun utilisateur connecté !");
                response.sendRedirect("login.jsp");
                return;
            }

            CreditDAO dao = new CreditDAO();

            System.out.println("[Servlet] Appel dao.validerCredit(" + creditId + ", " + utilisateurId + ")");

            boolean ok = dao.validerCredit(creditId, utilisateurId);

            System.out.println("[Servlet] Résultat DAO : " + ok);

            if (ok) {
                request.getSession().setAttribute("success", "Crédit validé avec succès !");
            } else {
                request.getSession().setAttribute("error", "Échec de la validation du crédit.");
            }

            System.out.println("=== [Servlet] Redirection vers la liste ===");
            response.sendRedirect("CommandeServlet?action=lister&commandeType=creditnonvalide");

        } catch (Exception ex) {
            System.out.println("[Servlet] Exception attrapée !");
            ex.printStackTrace();
            request.getSession().setAttribute("error", "Erreur lors de la validation.");
            response.sendRedirect("CommandeServlet?action=lister&commandeType=creditnonvalide");
        }
    }


    private void getCommandeTraitement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String numeroCommande = request.getParameter("numCommande");
        String staffIdParam = request.getParameter("staffId");
        int staffId = 0;
        if (staffIdParam != null && !staffIdParam.isEmpty()) {
            try {
                staffId = Integer.parseInt(staffIdParam);
            } catch (NumberFormatException e) {
                staffId = 0;
            }
        }

        if (numeroCommande == null || numeroCommande.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le numéro de commande est requis.");
            return;
        }

        NotificationDAO notificationDAO = new NotificationDAO();
        List<Notification> notifications = notificationDAO.recupererTraitementCommande(staffId, numeroCommande);

        // Convertir les notifications en JSON propre
        List<Map<String, Object>> notificationsJson = new ArrayList<>();
        for (Notification n : notifications) {
            Map<String, Object> notifMap = new HashMap<>();
            notifMap.put("id", n.getId());
            notifMap.put("generatedBy", n.getGeneratedBy());
            notifMap.put("toUser", n.getToUser());
            notifMap.put("updatedBy", n.getUpdatedBy());
            notifMap.put("messages", n.getMessages());
            notifMap.put("createdAt", n.getCreatedAt().toString()); // LocalDateTime -> String
            notifMap.put("updatedAt", n.getUpdatedAt() != null ? n.getUpdatedAt().toString() : null);
            notifMap.put("status", n.getStatus());

            if (n.getUtilisateur() != null) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", n.getUtilisateur().getId());
                userMap.put("nom", n.getUtilisateur().getNom());
                userMap.put("prenom", n.getUtilisateur().getPrenom());
                notifMap.put("utilisateur", userMap);
            }

            notificationsJson.add(notifMap);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(notificationsJson);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    
 // 1️⃣ Récupérer toutes les commandes encaissées par des caissières/caissiers
//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        LocalDate localDateDebut = (dateDebutStr != null && !dateDebutStr.isEmpty()
//                ? LocalDate.parse(dateDebutStr) : LocalDate.now();
//        LocalDate localDateFin = (dateFinStr != null && !dateFinStr.isEmpty())
//                ? LocalDate.parse(dateFinStr) : LocalDate.now();
//
//        Timestamp dateDebut = Timestamp.valueOf(localDateDebut.atStartOfDay());
//        Timestamp dateFin = Timestamp.valueOf(localDateFin.atTime(23, 59, 59));
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        System.out.println("===== getCaissiereCommandeCashed START =====");
//        System.out.println("Dates: " + dateDebut + " -> " + dateFin);
//
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(dateDebut, dateFin);
//        System.out.println("Total commandes récupérées = " + commandes.size());
//
//        for (Commande commande : commandes) {
//            System.out.println("Commande ID=" + commande.getId() + ", Montant=" + commande.getMontantTotal());
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//        
//        
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("dateDebutStr", localDateDebut.toString()); // ex: 2025-09-06
//        request.setAttribute("dateFinStr", localDateFin.toString());
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//        System.out.println("===== getCaissiereCommandeCashed END =====");
//    }
    
    private Timestamp parseDateTime(HttpServletRequest request, String paramName, boolean isStart) {
        String value = request.getParameter(paramName);
        try {
            if (value != null && !value.isEmpty()) {
                // Convertir le format datetime-local (YYYY-MM-DDTHH:MM) en format SQL
                if (value.contains("T")) {
                    String datetimeStr = value.replace('T', ' ') + (isStart ? ":00" : ":59");
                    return Timestamp.valueOf(datetimeStr);
                } else {
                    // Fallback pour l'ancien format date seulement
                    return isStart 
                        ? Timestamp.valueOf(value + " 00:00:00")
                        : Timestamp.valueOf(value + " 23:59:59");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Valeurs par défaut
        LocalDate today = LocalDate.now();
        return isStart
                ? Timestamp.valueOf(today.atStartOfDay())
                : Timestamp.valueOf(today.atTime(23, 59, 59));
    }
    
    private Timestamp parseDateTime2(String dateTimeStr, boolean isStart) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return isStart 
                ? Timestamp.valueOf(LocalDate.now().atStartOfDay())
                : Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
        }
        
        try {
            // Si la chaîne contient un espace, c'est déjà au format SQL
            if (dateTimeStr.contains(" ")) {
                return Timestamp.valueOf(dateTimeStr);
            }
            // Si la chaîne contient T, c'est le format datetime-local
            else if (dateTimeStr.contains("T")) {
                String[] parts = dateTimeStr.split("T");
                String datePart = parts[0];
                String timePart = parts[1];
                
                if (isStart) {
                    return Timestamp.valueOf(datePart + " " + timePart + ":00");
                } else {
                    return Timestamp.valueOf(datePart + " " + timePart + ":59");
                }
            }
            // Sinon, c'est juste une date YYYY-MM-DD
            else {
                return isStart 
                    ? Timestamp.valueOf(dateTimeStr + " 00:00:00")
                    : Timestamp.valueOf(dateTimeStr + " 23:59:59");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return isStart 
                ? Timestamp.valueOf(LocalDate.now().atStartOfDay())
                : Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
        }
    }

//    
//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Dates
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        LocalDate localDateDebut = (dateDebutStr != null && !dateDebutStr.isEmpty())
//                ? LocalDate.parse(dateDebutStr) : LocalDate.now();
//        LocalDate localDateFin = (dateFinStr != null && !dateFinStr.isEmpty())
//                ? LocalDate.parse(dateFinStr) : LocalDate.now();
//
//        Timestamp dateDebut = Timestamp.valueOf(localDateDebut.atStartOfDay());
//        Timestamp dateFin = Timestamp.valueOf(localDateFin.atTime(23, 59, 59));
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        // 👉 Récupération filtres
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        // DAO
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(dateDebut, dateFin, categorieId, sousCategorieId);
//
//        for (Commande commande : commandes) {
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("dateDebutStr", localDateDebut.toString());
//        request.setAttribute("dateFinStr", localDateFin.toString());
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//    }
    
//    OLD BON
//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
//        Timestamp dateFin = parseDateTime(request, "dateFin", false);
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(dateDebut, dateFin);
//        for (Commande commande : commandes) {
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("dateDebutStr", dateDebut.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("dateFinStr", dateFin.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//    }

//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
//        Timestamp dateFin = parseDateTime(request, "dateFin", false);
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        // MODIFICATION: Ajouter les paramètres de catégorie à l'appel DAO
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(
//            dateDebut, dateFin, categorieId, sousCategorieId
//        );
//        
//        for (Commande commande : commandes) {
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("dateDebutStr", dateDebut.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("dateFinStr", dateFin.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//    }
//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
//        Timestamp dateFin = parseDateTime(request, "dateFin", false);
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        // Charger les sous-catégories si une catégorie est sélectionnée
//        List<MenuCategorie> sousCategories = null;
//        if (categorieId != null) {
//            sousCategories = menuCategorieDAO.getSousCategoriesByParentId(categorieId);
//        }
//
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(
//            dateDebut, dateFin, categorieId, sousCategorieId
//        );
//        
//        for (Commande commande : commandes) {
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("sousCategories", sousCategories); // Ajouter ceci
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("dateDebutStr", dateDebut.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("dateFinStr", dateFin.toLocalDateTime().toString().replace("T", " "));
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//    }
//
//    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
//        Timestamp dateFin = parseDateTime(request, "dateFin", false);
//
//        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        // Charger les sous-catégories si une catégorie est sélectionnée
//        List<MenuCategorie> sousCategories = null;
//        if (categorieId != null) {
//            sousCategories = menuCategorieDAO.getSousCategoriesByParentId(categorieId);
//        }
//
//        // 📌 Récupérer les commandes
//        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(
//            dateDebut, dateFin, categorieId, sousCategorieId
//        );
//        
//        for (Commande commande : commandes) {
//            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
//        }
//
//        // 🔹 Conversion Timestamp -> java.sql.Date pour les dépenses
//        java.sql.Date dateDebutSql = (dateDebut != null) ? new java.sql.Date(dateDebut.getTime()) : null;
//        java.sql.Date dateFinSql   = (dateFin != null) ? new java.sql.Date(dateFin.getTime()) : null;
//
//        // 📌 Récupérer les dépenses et crédits
//        DepenseDAO depenseDAO = new DepenseDAO();
//        List<Depense> depenses = depenseDAO.getDepensesFiltrees(dateDebutSql, dateFinSql, null, null);
//
//        List<Commande> commandesCredits = creditDAO.getCommandesCredit(null, null, dateDebut, dateFin);
//        //
//                // Calculer le total des crédits restants (montantTotal - montantPaye)
//                int totalCredits = 0;
//                for (Commande c : commandesCredits) {
//                    if (c.getCredit() != null) {
//                        int restant = c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye();
//                        if (restant > 0) totalCredits += restant;
//                    }
//                }
//       List<Commande> Commandescredits = creditDAO.getCommandesCredit(null, null, dateDebut, dateFin);
//       request.setAttribute("totalCredits", totalCredits);
//
//        // 🔹 Mettre les résultats en attributs de requête
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//        request.setAttribute("sousCategories", sousCategories);
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("depenses", depenses);
//        request.setAttribute("Commandescredits", Commandescredits);
//        request.setAttribute("dateDebutStr", (dateDebut != null) ? dateDebut.toLocalDateTime().toString().replace("T", " ") : "");
//        request.setAttribute("dateFinStr", (dateFin != null) ? dateFin.toLocalDateTime().toString().replace("T", " ") : "");
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//
//        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
//    }
    private void getCaissiereCommandeCashed(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
        Timestamp dateFin = parseDateTime(request, "dateFin", false);

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }

        Integer categorieId = parseInteger(request.getParameter("categorieId"));
        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));

        // Charger les sous-catégories si une catégorie est sélectionnée
        List<MenuCategorie> sousCategories = null;
        if (categorieId != null) {
            sousCategories = menuCategorieDAO.getSousCategoriesByParentId(categorieId);
        }

        // 📌 Récupérer les commandes encaissées
        List<Commande> commandes = commandeDAO.getCaissiereCommandesCashed(dateDebut, dateFin, categorieId, sousCategorieId);
        for (Commande commande : commandes) {
            commande.setDetails(commandeDAO.getDetailsByCommandeId(commande.getId()));
        }

        // 🔹 Conversion Timestamp -> java.sql.Date pour les dépenses
        java.sql.Date dateDebutSql = (dateDebut != null) ? new java.sql.Date(dateDebut.getTime()) : null;
        java.sql.Date dateFinSql   = (dateFin != null) ? new java.sql.Date(dateFin.getTime()) : null;
     // ⚠️ Ajouter 1 jour à la borne supérieure
        if (dateFinSql != null) {
            LocalDate finPlusUn = dateFinSql.toLocalDate().plusDays(1);
            dateFinSql = java.sql.Date.valueOf(finPlusUn);
        }


        // 📌 Récupérer les dépenses
        DepenseDAO depenseDAO = new DepenseDAO();
        List<Depense> depenses = depenseDAO.getDepensesFiltrees(dateDebutSql, dateFinSql, null, null);

        // 📌 Récupérer les commandes avec crédits
        List<Commande> commandesCredits = creditDAO.getCommandesCredit(null, null, dateDebut, dateFin);

        // Calculer le total des crédits restants (montantTotal - montantPaye)
        int totalCredits = 0;
        for (Commande c : commandesCredits) {
            if (c.getCredit() != null) {
                int restant = c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye();
                if (restant > 0) totalCredits += restant;
            }
        }
        
        System.out.println(totalCredits);


        // 🔹 Mettre les résultats en attributs de requête
        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
        request.setAttribute("sousCategories", sousCategories);
        request.setAttribute("commandes", commandes);
        request.setAttribute("depenses", depenses);
        request.setAttribute("commandesCredits", commandesCredits);
        request.setAttribute("totalCredits", totalCredits); // ✅ total correct
        request.setAttribute("dateDebutStr", (dateDebut != null) ? dateDebut.toLocalDateTime().toString().replace("T", " ") : "");
        request.setAttribute("dateFinStr", (dateFin != null) ? dateFin.toLocalDateTime().toString().replace("T", " ") : "");
        request.setAttribute("categorieId", categorieId);
        request.setAttribute("sousCategorieId", sousCategorieId);

        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
    }



    private void getUserCommandeCachedByCaissiere(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer caissierId = parseInteger(request.getParameter("caissierId"));
        if (caissierId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "caissierId manquant");
            return;
        }

        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
        Timestamp dateFin = parseDateTime(request, "dateFin", false);

        if (dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }

        Integer categorieId = parseInteger(request.getParameter("categorieId"));
        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));

        // MODIFICATION: Ajouter les paramètres de catégorie à l'appel DAO
        List<Commande> commandes = commandeDAO.getUserCommandesCachedByCaissiere(
                caissierId, dateDebut, dateFin, categorieId, sousCategorieId
        );

        Map<Integer, Map<String, Object>> utilisateursMap = new HashMap<>();
        for (Commande c : commandes) {
            Utilisateur u = (c.getClient() != null) ? c.getClient() : c.getUtilisateur();
            if (u != null) {
                int uid = u.getId();
                utilisateursMap.computeIfAbsent(uid, k -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", uid);
                    userInfo.put("nom", u.getNom());
                    userInfo.put("prenom", u.getPrenom());
                    userInfo.put("role", u.getRole() != null ? u.getRole().getRoleName() : null);
                    userInfo.put("commandes", new ArrayList<Commande>());
                    return userInfo;
                });
                ((List<Commande>) utilisateursMap.get(uid).get("commandes")).add(c);
            }
        }

        List<Map<String, Object>> utilisateursList = new ArrayList<>(utilisateursMap.values());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new com.google.gson.Gson().toJson(utilisateursList));
    }


    private void getAllCommandesGroupedByCaissiere(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
        Timestamp dateFin = parseDateTime(request, "dateFin", false);

        if (dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }

        Integer categorieId = parseInteger(request.getParameter("categorieId"));
        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));

        // MODIFICATION: Ajouter les paramètres de catégorie à l'appel DAO
        Map<Utilisateur, List<Commande>> grouped = commandeDAO.getAllCommandesGroupedByCaissiere(
            dateDebut, dateFin, categorieId, sousCategorieId
        );

        request.setAttribute("commandesGrouped", grouped);
        request.getRequestDispatcher("/blok/liste-rapport-par-caissiere.jsp").forward(request, response);
    }

    
    private void listerCommandes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        
        Timestamp dateDebut = null;
        Timestamp dateFin = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
                dateDebut = new Timestamp(parsedDebut.getTime());
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                java.util.Date parsedFin = sdf.parse(dateFinStr);
                // Fin de journée
                dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // Optionnel : définir dateDebut/dateFin à null si parsing échoue
            dateDebut = null;
            dateFin = null;
        }
        List<Commande> commandes = commandeDAO.getAllCommandesForUserId(userId,dateDebut,dateFin);
        
        for (Commande commande : commandes) {
            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
            commande.setDetails(details); // injecter les détails
        }
        request.setAttribute("commandes", commandes);

      //  request.setAttribute("commandes", commandes);
        request.getRequestDispatcher("/blok/liste-commandes.jsp").forward(request, response);
    }
    
//    private void rapportCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupération des paramètres
//        String staffIdStr = request.getParameter("staffId");
//        String roleIdStr = request.getParameter("roleId");
//        String boissonIdStr = request.getParameter("boissonId");
//        String nourritureIdStr = request.getParameter("nourritureId");
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        Integer staffId = parseInteger(staffIdStr);
//        Integer roleId = parseInteger(roleIdStr);
//
//        // Détermination plat/produit
//        Integer platId = null;
//        Integer produitId = null;
//
//        if (boissonIdStr != null && !boissonIdStr.isEmpty()) {
//            Plat boisson = platDAO.chercherParId(parseInteger(boissonIdStr));
//            if (boisson != null && boisson.getProductId() > 0) {
//                produitId = boisson.getProductId();
//            } else if (boisson != null) {
//                platId = boisson.getId();
//            }
//        } else if (nourritureIdStr != null && !nourritureIdStr.isEmpty()) {
//            Plat nourriture = platDAO.chercherParId(parseInteger(nourritureIdStr));
//            if (nourriture != null && nourriture.getProductId() > 0) {
//                produitId = nourriture.getProductId();
//            } else if (nourriture != null) {
//                platId = nourriture.getId();
//            }
//        }
//
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
//                dateDebut = new Timestamp(parsedDebut.getTime());
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                java.util.Date parsedFin = sdf.parse(dateFinStr);
//                // Fin de journée
//                dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1);
//            }
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//            // Optionnel : définir dateDebut/dateFin à null si parsing échoue
//            dateDebut = null;
//            dateFin = null;
//        }
//
//        // Récupération des rapports
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandes(
//                staffId, roleId, platId, produitId, dateDebut, dateFin
//        );
//
//        // Préparer les données pour la JSP
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//
//        List<Plat> boissons = menuCategorieDAO.getThePlatsByCategoryId(6);
//        List<Plat> nourritures = menuCategorieDAO.getThePlatsByCategoryId(9);
//
//        ProduitDAO produitDAO = new ProduitDAO();
//
//        for (Plat plat : boissons) {
//            if (plat.getProductId() > 0) {
//                Produit produit = produitDAO.chercherParId(plat.getProductId());
//                if (produit != null) {
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    plat.setNom(produit.getNom());
//                }
//            }
//        }
//
//        request.setAttribute("boissons", boissons);
//        request.setAttribute("nourritures", nourritures);
//        request.setAttribute("rapports", rapports);
//
//        // Calcul du total global (optionnel)
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        request.setAttribute("totalMontant", totalMontant);
//        request.setAttribute("totalQuantite", totalQuantite);
//
//        // Redirection vers la JSP de rapport
//        request.getRequestDispatcher("/blok/liste-rapport-commandes.jsp").forward(request, response);
//    }



//    private void rapportCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupération des paramètres
//        String staffIdStr = request.getParameter("staffId");
//        String roleIdStr = request.getParameter("roleId");
//        String boissonIdStr = request.getParameter("boissonId");
//        String nourritureIdStr = request.getParameter("nourritureId");
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        Integer staffId = parseInteger(staffIdStr);
//        Integer roleId = parseInteger(roleIdStr);
//
//        // Détermination plat/produit
//        Integer platId = null;
//        Integer produitId = null;
//
//        if (boissonIdStr != null && !boissonIdStr.isEmpty()) {
//            Plat boisson = platDAO.chercherParId(parseInteger(boissonIdStr));
//            if (boisson != null && boisson.getProductId() > 0) {
//                produitId = boisson.getProductId();
//            } else if (boisson != null) {
//                platId = boisson.getId();
//            }
//        } else if (nourritureIdStr != null && !nourritureIdStr.isEmpty()) {
//            Plat nourriture = platDAO.chercherParId(parseInteger(nourritureIdStr));
//            if (nourriture != null && nourriture.getProductId() > 0) {
//                produitId = nourriture.getProductId();
//            } else if (nourriture != null) {
//                platId = nourriture.getId();
//            }
//        }
//
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
//                dateDebut = new Timestamp(parsedDebut.getTime());
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                java.util.Date parsedFin = sdf.parse(dateFinStr);
//                dateFin = new Timestamp(parsedFin.getTime());
//            }
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//            dateDebut = null;
//            dateFin = null;
//        }
//
//        // Si aucune date n'est fournie, par défaut on prend aujourd'hui
//        if (dateDebut == null && dateFin == null) {
//            java.util.Calendar cal = java.util.Calendar.getInstance();
//            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
//            cal.set(java.util.Calendar.MINUTE, 0);
//            cal.set(java.util.Calendar.SECOND, 0);
//            cal.set(java.util.Calendar.MILLISECOND, 0);
//            dateDebut = new Timestamp(cal.getTimeInMillis());
//
//            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
//            cal.set(java.util.Calendar.MINUTE, 59);
//            cal.set(java.util.Calendar.SECOND, 59);
//            cal.set(java.util.Calendar.MILLISECOND, 999);
//            dateFin = new Timestamp(cal.getTimeInMillis());
//        }
//
//
//
//        // Récupération des rapports
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandes(
//                staffId, roleId, platId, produitId, dateDebut, dateFin
//        );
//
//        // Préparer les données pour la JSP
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//
//        List<Plat> boissons = menuCategorieDAO.getThePlatsByCategoryId(6);
//        List<Plat> nourritures = menuCategorieDAO.getThePlatsByCategoryId(9);
//
//        ProduitDAO produitDAO = new ProduitDAO();
//        for (Plat plat : boissons) {
//            if (plat.getProductId() > 0) {
//                Produit produit = produitDAO.chercherParId(plat.getProductId());
//                if (produit != null) {
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    plat.setNom(produit.getNom());
//                }
//            }
//        }
//
//        request.setAttribute("boissons", boissons);
//        request.setAttribute("nourritures", nourritures);
//        request.setAttribute("rapports", rapports);
//        
//     
//
//        // Calcul du total global
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        request.setAttribute("totalMontant", totalMontant);
//        request.setAttribute("totalQuantite", totalQuantite);
//
//        // Redirection vers la JSP de rapport
//        request.getRequestDispatcher("/blok/liste-rapport-commandes.jsp").forward(request, response);
//    }
    
//    OLD RAPPORT-COMMANDE 23-12-25
//    private void rapportCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupération des paramètres
//        String staffIdStr = request.getParameter("staffId");
//        String roleIdStr = request.getParameter("roleId");
//        String categorieIdStr = request.getParameter("categorieId");
//        String sousCategorieIdStr = request.getParameter("sousCategorieId");
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        Integer staffId = parseInteger(staffIdStr);
//        Integer roleId = parseInteger(roleIdStr);
//        Integer categorieId = parseInteger(categorieIdStr);
//        Integer sousCategorieId = parseInteger(sousCategorieIdStr);
//
//        // Gestion des dates
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                dateDebut = new Timestamp(sdf.parse(dateDebutStr).getTime());
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                dateFin = new Timestamp(sdf.parse(dateFinStr).getTime());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Par défaut : aujourd’hui
//        // Si aucune date n'est fournie, par défaut on prend aujourd'hui
//        if (dateDebut == null && dateFin == null) {
//            java.util.Calendar cal = java.util.Calendar.getInstance();
//            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
//            cal.set(java.util.Calendar.MINUTE, 0);
//            cal.set(java.util.Calendar.SECOND, 0);
//            cal.set(java.util.Calendar.MILLISECOND, 0);
//            dateDebut = new Timestamp(cal.getTimeInMillis());
//
//            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
//            cal.set(java.util.Calendar.MINUTE, 59);
//            cal.set(java.util.Calendar.SECOND, 59);
//            cal.set(java.util.Calendar.MILLISECOND, 999);
//            dateFin = new Timestamp(cal.getTimeInMillis());
//        }
//
//        // 🔥 Appel DAO avec catégorie et sous-catégorie
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandesByCategoryAndSubcategory(
//                staffId, roleId, categorieId, sousCategorieId, dateDebut, dateFin
//        );
//
//        // Préparer les données pour la JSP
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//        request.setAttribute("categories", menuCategorieDAO.getCategoriesParentes());
//
//        List<MenuCategorie> sousCategories = null;
//        if (categorieId != null) {
//            sousCategories = menuCategorieDAO.getSousCategoriesByParentId(categorieId);
//        }
//
//        request.setAttribute("sousCategories", sousCategories);
//        request.setAttribute("categorieId", categorieId);
//        request.setAttribute("sousCategorieId", sousCategorieId);
//        request.setAttribute("rapports", rapports);
//
//        // Totaux
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        request.setAttribute("totalMontant", totalMontant);
//        request.setAttribute("totalQuantite", totalQuantite);
//
//        request.getRequestDispatcher("/blok/liste-rapport-commandes.jsp").forward(request, response);
//    }
    private void rapportCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Récupérer tous les paramètres de filtre hiérarchiques
        String staffIdStr = request.getParameter("staffId");
        String roleIdStr = request.getParameter("roleId");
        String rayonIdStr = request.getParameter("rayonId");
        String categorieIdStr = request.getParameter("categorieId");
        String sousCategorieIdStr = request.getParameter("sousCategorieId");
        String platIdStr = request.getParameter("platId");
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");

        System.out.println("=== DEBUG Rapport: Paramètres reçus ===");
        System.out.println("staffId: " + staffIdStr);
        System.out.println("roleId: " + roleIdStr);
        System.out.println("rayonId: " + rayonIdStr);
        System.out.println("categorieId: " + categorieIdStr);
        System.out.println("sousCategorieId: " + sousCategorieIdStr);
        System.out.println("platId: " + platIdStr);
        System.out.println("dateDebut: " + dateDebutStr);
        System.out.println("dateFin: " + dateFinStr);

        // Parser les paramètres
        Integer staffId = parseIntegerSafely(staffIdStr);
        Integer roleId = parseIntegerSafely(roleIdStr);
        Integer rayonId = parseIntegerSafely(rayonIdStr);
        Integer categorieId = parseIntegerSafely(categorieIdStr);
        Integer sousCategorieId = parseIntegerSafely(sousCategorieIdStr);
        Integer platId = parseIntegerSafely(platIdStr);

        // Parser les dates
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        LocalDateTime localDateDebut = null;
//        LocalDateTime localDateFin = null;
//
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                localDateDebut = LocalDateTime.parse(dateDebutStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateDebut = null;
//            System.err.println("Erreur parsing dateDebut: " + e.getMessage());
//        }
//
//        try {
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                localDateFin = LocalDateTime.parse(dateFinStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateFin = null;
//            System.err.println("Erreur parsing dateFin: " + e.getMessage());
//        }
//
//        // Valeurs par défaut si null
//        if (localDateDebut == null) {
//            localDateDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
//        }
//        if (localDateFin == null) {
//            localDateFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
//        }
//
//        // Convertir en Date pour la DAO
//        Date dateDebut = java.sql.Timestamp.valueOf(localDateDebut);
//        Date dateFin = java.sql.Timestamp.valueOf(localDateFin);
//
//        // Swap si inversées
//        if (dateDebut.after(dateFin)) {
//            Date tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
        Timestamp dateFin = parseDateTime(request, "dateFin", false);

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }

        System.out.println("=== DEBUG Rapport: Paramètres parsés ===");
        System.out.println("staffId (parsed): " + staffId);
        System.out.println("roleId (parsed): " + roleId);
        System.out.println("rayonId (parsed): " + rayonId);
        System.out.println("categorieId (parsed): " + categorieId);
        System.out.println("sousCategorieId (parsed): " + sousCategorieId);
        System.out.println("platId (parsed): " + platId);
        System.out.println("dateDebut (parsed): " + dateDebut);
        System.out.println("dateFin (parsed): " + dateFin);

        // Récupérer les rapports avec filtres hiérarchiques
        List<RapportCommande> rapports = commandeDAO.getRapportCommandesByFiltresHierarchiques(
                staffId, roleId, rayonId, categorieId, sousCategorieId, platId, dateDebut, dateFin);

        // Préparer les données pour la JSP
        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
        request.setAttribute("roles", RoleDAO.findAllVisible());

        // Récupérer la hiérarchie complète pour les filtres
        System.out.println("=== DEBUG Rapport: Récupération de la hiérarchie complète ===");
        List<RayonHierarchique> hierarchieRayons = menuCategorieDAO.getHierarchieCompleteRayons();
        
        // Associer les plats aux sous-catégories
        if (hierarchieRayons != null) {
            for (RayonHierarchique rayonHierarchique : hierarchieRayons) {
                if (rayonHierarchique.getRayon() != null) {
                    Map<Integer, List<Plat>> platsParSousCategorie = new HashMap<>();
                    
                    // Parcourir toutes les sous-catégories de ce rayon
                    if (rayonHierarchique.getSousCategoriesParCategorie() != null) {
                        for (List<MenuCategorie> sousCategories : rayonHierarchique.getSousCategoriesParCategorie().values()) {
                            for (MenuCategorie sousCategorie : sousCategories) {
                                List<Plat> plats = menuCategorieDAO.getPlatsBySousCategorieId(sousCategorie.getId());
                                if (plats != null && !plats.isEmpty()) {
                                    platsParSousCategorie.put(sousCategorie.getId(), plats);
                                }
                            }
                        }
                    }
                    
                    rayonHierarchique.setPlatsParSousCategorie(platsParSousCategorie);
                }
            }
        }

        // Passer les données à la JSP
        request.setAttribute("hierarchieRayons", hierarchieRayons);
        request.setAttribute("rapports", rapports);
        
        // Passer aussi les paramètres de filtre pour les garder dans le formulaire
        request.setAttribute("filtreStaffId", staffIdStr);
        request.setAttribute("filtreRoleId", roleIdStr);
        request.setAttribute("filtreRayonId", rayonIdStr);
        request.setAttribute("filtreCategorieId", categorieIdStr);
        request.setAttribute("filtreSousCategorieId", sousCategorieIdStr);
        request.setAttribute("filtrePlatId", platIdStr);
        request.setAttribute("filtreDateDebut", dateDebutStr);
        request.setAttribute("filtreDateFin", dateFinStr);

        // Totaux
        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();

        request.setAttribute("totalMontant", totalMontant);
        request.setAttribute("totalQuantite", totalQuantite);

        System.out.println("=== DEBUG Rapport: Fin de la méthode, forwarding vers JSP ===");
        request.getRequestDispatcher("/blok/liste-rapport-commandes.jsp").forward(request, response);
    }

  

    
//    private void rapportCommandesCashedByCaissiereWithFiltresJSON(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupération du caissierId depuis le paramètre
//        String caissierIdStr = request.getParameter("caissierId");
//        Integer caissierId = parseInteger(caissierIdStr);
//
//        // Récupération des dates
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//        
//        // Récupération du staffId
//        String staffIdStr = request.getParameter("staffId");
//        Integer staffId = parseInteger(staffIdStr);
//
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                if (dateDebutStr.contains(" ")) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime localDebut = LocalDateTime.parse(dateDebutStr, formatter);
//                    dateDebut = Timestamp.valueOf(localDebut);
//                } else {
//                    LocalDate localDebut = LocalDate.parse(dateDebutStr);
//                    dateDebut = Timestamp.valueOf(localDebut.atStartOfDay());
//                }
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                if (dateFinStr.contains(" ")) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime localFin = LocalDateTime.parse(dateFinStr, formatter);
//                    dateFin = Timestamp.valueOf(localFin);
//                } else {
//                    LocalDate localFin = LocalDate.parse(dateFinStr);
//                    dateFin = Timestamp.valueOf(localFin.atTime(23, 59, 59));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Valeurs par défaut
//        if (dateDebut == null) dateDebut = Timestamp.valueOf(LocalDate.now().atStartOfDay());
//        if (dateFin == null) dateFin = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
//
//        // Swap si inversées
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        // Récupération du rapport filtré par caissier
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandesCashedByCaissierForUser(
//        		staffId, null, null, null, caissierId, dateDebut, dateFin
//        );
//
//        // Calcul des totaux
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        // Préparation du JSON
//        List<Map<String, Object>> jsonList = new ArrayList<>();
//        for (RapportCommande r : rapports) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("nomPlat", r.getNomPlat());
//            map.put("quantiteTotale", r.getQuantiteTotale());
//            map.put("prixUnitaire", r.getPrixUnitaire());
//            map.put("montantTotal", r.getMontantTotal());
//            jsonList.add(map);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("rapports", jsonList);
//        result.put("totalMontant", totalMontant);
//        result.put("totalQuantite", totalQuantite);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(new com.google.gson.Gson().toJson(result));
//    }
    
//    private void rapportCommandesCashedByCaissiereWithFiltresJSON(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        Integer caissierId = parseInteger(request.getParameter("caissierId"));
//        Integer staffId = parseInteger(request.getParameter("staffId"));
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//
//        Timestamp dateDebut = null, dateFin = null;
//        try {
//            String dateDebutStr = request.getParameter("dateDebut");
//            String dateFinStr = request.getParameter("dateFin");
//
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                if (dateDebutStr.contains(" ")) {
//                    dateDebut = Timestamp.valueOf(LocalDateTime.parse(dateDebutStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                } else {
//                    dateDebut = Timestamp.valueOf(LocalDate.parse(dateDebutStr).atStartOfDay());
//                }
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                if (dateFinStr.contains(" ")) {
//                    dateFin = Timestamp.valueOf(LocalDateTime.parse(dateFinStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                } else {
//                    dateFin = Timestamp.valueOf(LocalDate.parse(dateFinStr).atTime(23, 59, 59));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (dateDebut == null) dateDebut = Timestamp.valueOf(LocalDate.now().atStartOfDay());
//        if (dateFin == null) dateFin = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        // Appel DAO avec filtres catégorie/sous-catégorie
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandesCashedByCaissierForUser(
//                staffId, null, null, null, caissierId, dateDebut, dateFin, categorieId, sousCategorieId
//        );
//
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        List<Map<String, Object>> jsonList = new ArrayList<>();
//        for (RapportCommande r : rapports) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("nomPlat", r.getNomPlat());
//            map.put("quantiteTotale", r.getQuantiteTotale());
//            map.put("prixUnitaire", r.getPrixUnitaire());
//            map.put("montantTotal", r.getMontantTotal());
//            jsonList.add(map);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("rapports", jsonList);
//        result.put("totalMontant", totalMontant);
//        result.put("totalQuantite", totalQuantite);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(new com.google.gson.Gson().toJson(result));
//    }
    
//    private void rapportCommandesCashedByCaissiereWithFiltresJSON(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
////        Integer caissierId = parseInteger(request.getParameter("caissierId"));
////        Integer staffId = parseInteger(request.getParameter("staffId"));
////        Integer categorieId = parseInteger(request.getParameter("categorieId"));
////        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
////
////        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
////        Timestamp dateFin = parseDateTime(request, "dateFin", false);
////
////        if (dateDebut.after(dateFin)) {
////            Timestamp tmp = dateDebut;
////            dateDebut = dateFin;
////            dateFin = tmp;
////        }
//     // Récupération CORRECTE des paramètres
//        Integer caissierId = parseInteger(request.getParameter("caissierId"));
//        Integer staffId = parseInteger(request.getParameter("staffId"));
//        Integer categorieId = parseInteger(request.getParameter("categorieId"));
//        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
//        
//        // Récupération CORRECTE des dates
//        // Récupération CORRECTE des dates
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//        
//        Timestamp dateDebut = parseDateTime2(dateDebutStr, true);
//        Timestamp dateFin = parseDateTime2(dateFinStr, false);
//
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//        System.out.println("Paramètres reçus:");
//        System.out.println("staffId: " + staffId);
//        System.out.println("caissierId: " + caissierId);
//        System.out.println("categorieId: " + categorieId);
//        System.out.println("sousCategorieId: " + sousCategorieId);
//        System.out.println("dateDebut: " + dateDebut);
//        System.out.println("dateFin: " + dateFin);
//
//        List<RapportCommande> rapports = commandeDAO.getRapportCommandesCashedByCaissierForUser(
//                staffId, null, null, null, caissierId, dateDebut, dateFin
//        );
//
//        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
//        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();
//
//        List<Map<String, Object>> jsonList = new ArrayList<>();
//        for (RapportCommande r : rapports) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("nomPlat", r.getNomPlat());
//            map.put("quantiteTotale", r.getQuantiteTotale());
//            map.put("prixUnitaire", r.getPrixUnitaire());
//            map.put("montantTotal", r.getMontantTotal());
//            jsonList.add(map);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("rapports", jsonList);
//        result.put("totalMontant", totalMontant);
//        result.put("totalQuantite", totalQuantite);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(new com.google.gson.Gson().toJson(result));
//    }

    private void rapportCommandesCashedByCaissiereWithFiltresJSON(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Récupération CORRECTE des paramètres
        Integer caissierId = parseInteger(request.getParameter("caissierId"));
        Integer staffId = parseInteger(request.getParameter("staffId"));
        Integer categorieId = parseInteger(request.getParameter("categorieId"));
        Integer sousCategorieId = parseInteger(request.getParameter("sousCategorieId"));
        
        // Récupération CORRECTE des dates
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        
        Timestamp dateDebut = parseDateTime2(dateDebutStr, true);
        Timestamp dateFin = parseDateTime2(dateFinStr, false);

        if (dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }
        
        System.out.println("Paramètres reçus:");
        System.out.println("staffId: " + staffId);
        System.out.println("caissierId: " + caissierId);
        System.out.println("categorieId: " + categorieId);
        System.out.println("sousCategorieId: " + sousCategorieId);
        System.out.println("dateDebut: " + dateDebut);
        System.out.println("dateFin: " + dateFin);

        // Appel avec les nouveaux paramètres
        List<RapportCommande> rapports = commandeDAO.getRapportCommandesCashedByCaissierForUser(
                staffId, null, null, null, caissierId, 
                categorieId, sousCategorieId, // Ajouter ces paramètres
                dateDebut, dateFin
        );

        int totalMontant = rapports.stream().mapToInt(RapportCommande::getMontantTotal).sum();
        int totalQuantite = rapports.stream().mapToInt(RapportCommande::getQuantiteTotale).sum();

        List<Map<String, Object>> jsonList = new ArrayList<>();
        for (RapportCommande r : rapports) {
            Map<String, Object> map = new HashMap<>();
            map.put("nomPlat", r.getNomPlat());
            map.put("quantiteTotale", r.getQuantiteTotale());
            map.put("prixUnitaire", r.getPrixUnitaire());
            map.put("montantTotal", r.getMontantTotal());
            jsonList.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rapports", jsonList);
        result.put("totalMontant", totalMontant);
        result.put("totalQuantite", totalQuantite);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new com.google.gson.Gson().toJson(result));
    }

    
//    private void listerHistoriqueVersements(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer clientId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (clientId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//        
//        
//
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
//                dateDebut = new Timestamp(parsedDebut.getTime());
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                java.util.Date parsedFin = sdf.parse(dateFinStr);
//                // Fin de journée
//                dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1);
//            }
//        } catch (java.text.ParseException e) {
//            dateDebut = null;
//            dateFin = null;
//        }
//
//        // Récupérer l'historique des versements avec les commandes et leurs détails
//        Map<Versement, List<Commande>> historique = commandeDAO.getHistoriqueVersementsAvecDetails(clientId, dateDebut, dateFin);
//       // Map<Versement, List<Commande>> historique = commandeDAO.getHistoriqueVersementsAvecDetails(clientId, dateDebut, dateFin);
//
//     // DEBUG serveur : afficher chaque versement et son nombre de commandes
//     for (Map.Entry<Versement, List<Commande>> entry : historique.entrySet()) {
//         Versement v = entry.getKey();
//         List<Commande> commandes = entry.getValue();
//         System.out.println("Versement ID=" + v.getId() + ", Montant=" + v.getMontant() + ", Nb Commandes=" + commandes.size());
//         for (Commande c : commandes) {
//             System.out.println("    Commande ID=" + c.getId() + ", Num=" + c.getNumeroCommande() + ", Montant=" + c.getMontantTotal());
//         }
//     }
//
//     request.setAttribute("historiqueVersements", historique);
//
//
//        request.setAttribute("historiqueVersements", historique);
//        request.getRequestDispatcher("/blok/historique-versements.jsp").forward(request, response);
//    }
    private void listerHistoriqueVersements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer clientId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (clientId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");

        Timestamp dateDebut;
        Timestamp dateFin;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date now = new Date();

            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = new Timestamp(sdf.parse(dateDebutStr.substring(0, 10)).getTime());
            } else {
                // Par défaut : début de journée aujourd'hui
                dateDebut = Timestamp.valueOf(sdf.format(now) + " 00:00:00");
            }

            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = new Timestamp(sdf.parse(dateFinStr.substring(0, 10)).getTime() + 24*60*60*1000 - 1);
            } else {
                // Par défaut : fin de journée aujourd'hui
                dateFin = Timestamp.valueOf(sdf.format(now) + " 23:59:59");
            }

        } catch (java.text.ParseException e) {
            // En cas d'erreur, on prend la journée actuelle
            Date now = new Date();
            dateDebut = Timestamp.valueOf(sdf.format(now) + " 00:00:00");
            dateFin = Timestamp.valueOf(sdf.format(now) + " 23:59:59");
        }

        // Passer les valeurs à la JSP pour remplir les inputs
        request.setAttribute("dateDebutStr", dateDebut.toString());
        request.setAttribute("dateFinStr", dateFin.toString());

        // Appel DAO
        Map<Versement, List<Commande>> historique = 
            commandeDAO.getHistoriqueVersementsAvecDetails(clientId, dateDebut, dateFin);

        request.setAttribute("historiqueVersements", historique);
        request.getRequestDispatcher("/blok/historique-versements.jsp").forward(request, response);
    }


    
    private void listerCommandesCredit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer clientId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (clientId == null) {
            response.sendRedirect("login.jsp");
            return;
        }


        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");

        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
                dateDebut = new Timestamp(parsedDebut.getTime());
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                java.util.Date parsedFin = sdf.parse(dateFinStr);
                // Fin de journée
                dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1);
            }
        } catch (java.text.ParseException e) {
            dateDebut = null;
            dateFin = null;
        }

        // Récupérer toutes les commandes crédit pour ce client
        List<Commande> commandesCredit = commandeDAO.getAllCommandesCreditForClient(clientId, dateDebut, dateFin);

        // Filtrer pour que seules les commandes du staff courant soient incluses
        List<Commande> commandesFiltrees = new ArrayList<>();
        for (Commande commande : commandesCredit) {
            if (commande.getClientId() == clientId) {
                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
                commande.setDetails(details); // injecter les détails
                commandesFiltrees.add(commande);
            }
        }

        request.setAttribute("commandesCredit", commandesFiltrees);
        request.getRequestDispatcher("/blok/liste-credit.jsp").forward(request, response);
    }

    
//    private void listerToutesCommandes(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//    	HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//        List<Commande> commandes = commandeDAO.getAllCommandesVisibles();
//        
//        for (Commande commande : commandes) {
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//            commande.setDetails(details); // injecter les détails
//        }
//
//        List<Utilisateur> utilisateurs = utilisateurDAO.findAllVisible(); // méthode à créer dans DAO
//        request.setAttribute("utilisateurs", utilisateurs);
//        List<Role> roles = RoleDAO.findAllVisible();
//
//        List<Plat> boissons = menuCategorieDAO.getPlatsByCategoryId(6);
//        request.setAttribute("boissons", boissons);
//        
//        List<Plat> nourritures = menuCategorieDAO.getPlatsByCategoryId(9);
//        request.setAttribute("nourritures", nourritures);
//        
//        request.setAttribute("roles", roles);
//        request.setAttribute("commandes", commandes);
//
//        
//       // request.setAttribute("commandes", commandes);
//        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//    }
//    LISTER TOUES COMMANDES BON
//    private void listerToutesCommandes(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupérer les commandes et leurs détails
//        List<Commande> commandes = commandeDAO.getAllCommandesVisibles();
//        for (Commande commande : commandes) {
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//            commande.setDetails(details);
//        }
//
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//
//        // Récupérer les plats Boissons et Nourritures
//        List<Plat> boissons = menuCategorieDAO.getPlatsByRayonId(1);
//        List<Plat> nourritures = menuCategorieDAO.getPlatsByRayonId(2);
//
//        ProduitDAO produitDAO = new ProduitDAO();
//
//        // Pour tous les plats existants de type boissons avec productId, récupérer le produit
//        for (Plat plat : boissons) {
//            if (plat.getProductId() > 0) {
//                Produit produit = produitDAO.chercherParId(plat.getProductId());
//                if (produit != null) {
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    plat.setNom(produit.getNom()); // remplace le nom du plat par le nom du produit
//                }
//            }
//        }
//
//
//        request.setAttribute("boissons", boissons);
//        request.setAttribute("nourritures", nourritures);
//        request.setAttribute("commandes", commandes);
//
//        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//    }
    
//    private void listerToutesCommandes(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Récupérer les commandes et leurs détails
//        List<Commande> commandes = commandeDAO.getAllCommandesVisibles();
//        for (Commande commande : commandes) {
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//            commande.setDetails(details);
//        }
//
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//
//        // Récupérer dynamiquement TOUS les rayons actifs
//        List<Rayon> rayonsActifs = menuCategorieDAO.getAllRayonsActifs();
//        
//        // Créer une liste de RayonAvecPlats
//        List<RayonAvecPlats> rayonsAvecPlats = new ArrayList<>();
//        
//        ProduitDAO produitDAO = new ProduitDAO();
//        
//        // Pour chaque rayon, récupérer les plats et créer l'objet
//        for (Rayon rayon : rayonsActifs) {
//            List<Plat> plats = menuCategorieDAO.getPlatsByRayonId(rayon.getId());
//            
//            // Pour tous les plats existants avec productId, récupérer le produit
//            for (Plat plat : plats) {
//                if (plat.getProduit().getId() > 0) {
//                    Produit produit = produitDAO.chercherParId(plat.getProduit().getId());
//                    if (produit != null) {
//                        plat.setProduit(produit);
//                        plat.setTypePlat("PRODUIT");
//                        // remplace le nom du plat par le nom du produit si nécessaire
//                        if (plat.getNom() == null || plat.getNom().isEmpty()) {
//                            plat.setNom(produit.getNom());
//                        }
//                    }
//                }
//            }
//            
//            RayonAvecPlats rayonAvecPlats = new RayonAvecPlats(rayon, plats);
//            rayonsAvecPlats.add(rayonAvecPlats);
//        }
//        
//        // Passer les données à la JSP
//        request.setAttribute("rayonsAvecPlats", rayonsAvecPlats);
//        request.setAttribute("commandes", commandes);
//
//        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//    }
    
//    LSTE TOUTRES COMMANDES 23-12-25
//   private void listerToutesCommandes(HttpServletRequest request, HttpServletResponse response)
//        throws ServletException, IOException {
//    HttpSession session = request.getSession(false);
//    Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//    if (userId == null) {
//        response.sendRedirect("login.jsp");
//        return;
//    }
//
//    // Récupérer les commandes et leurs détails
//    List<Commande> commandes = commandeDAO.getAllCommandesVisibles();
//    for (Commande commande : commandes) {
//        List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//        commande.setDetails(details);
//    }
//
//    request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//    request.setAttribute("roles", RoleDAO.findAllVisible());
//
//    // Récupérer TOUS les plats sans aucune restriction
//    List<Plat> tousLesPlats = menuCategorieDAO.getAllPlatsWithoutRestrictions();
//    
//    // Pour le débogage : afficher ce qu'on a récupéré
//    System.out.println("=== DEBUG: Liste de tous les plats ===");
//    for (Plat plat : tousLesPlats) {
//        System.out.println("Plat ID: " + plat.getId() + 
//                          ", Nom: " + plat.getNom() + 
//                          ", RayonID: " + plat.getRayonId() +
//                          ", CatID: " + plat.getCategorieId() +
//                          ", SousCatID: " + plat.getSousCategorieId() +
//                          ", Produit: " + (plat.getProduit() != null ? plat.getProduit().getId() : "NULL"));
//    }
//    
//    // Organiser par état pour faciliter la correction
//    List<Plat> platsAvecProblemes = new ArrayList<>();
//    List<Plat> platsNormaux = new ArrayList<>();
//    
//    for (Plat plat : tousLesPlats) {
//        // Vérifier les problèmes
//        boolean hasProblem = false;
//        StringBuilder problemes = new StringBuilder();
//        
//        if (plat.getRayonId() == null || plat.getRayonId() == 0) {
//            hasProblem = true;
//            problemes.append("Pas de rayon; ");
//        }
//        
//        if (plat.getCategorieId() == null || plat.getCategorieId() == 0) {
//            hasProblem = true;
//            problemes.append("Pas de catégorie; ");
//        }
//        
//        if (plat.getProduit() == null) {
//            hasProblem = true;
//            problemes.append("Pas de produit; ");
//        }
//        
//        if (hasProblem) {
//            // Ajouter l'info du problème à l'objet Plat
//            plat.setDescription((plat.getDescription() != null ? plat.getDescription() : "") + 
//                               " [PROBLÈMES: " + problemes.toString() + "]");
//            platsAvecProblemes.add(plat);
//        } else {
//            platsNormaux.add(plat);
//        }
//    }
//    
//    // Passer toutes les listes à la JSP
//    request.setAttribute("tousLesPlats", tousLesPlats);
//    request.setAttribute("platsAvecProblemes", platsAvecProblemes);
//    request.setAttribute("platsNormaux", platsNormaux);
//    request.setAttribute("commandes", commandes);
//
//    request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//}
    
    private void listerToutesCommandes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== DEBUT listerToutesCommandes ===");
        
        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        
        System.out.println("Session trouvée: " + (session != null));
        System.out.println("User ID récupéré: " + userId);
        
        if (userId == null) {
            System.out.println("Aucun user ID trouvé, redirection vers login.jsp");
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("=== ÉTAPE 1: Récupération des commandes ===");
        
        // Récupérer les commandes et leurs détails
        System.out.println("Appel à commandeDAO.getAllCommandesVisibles()...");
        List<Commande> commandes = commandeDAO.getAllCommandesVisibles();
        System.out.println("Nombre de commandes récupérées: " + (commandes != null ? commandes.size() : 0));
        
        // Injecter les détails pour chaque commande
        if (commandes != null) {
            System.out.println("=== ÉTAPE 2: Injection des détails des commandes ===");
            for (Commande commande : commandes) {
                System.out.println("Récupération des détails pour commande ID: " + commande.getId() + 
                                 " - " + commande.getNumeroCommande());
                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
                commande.setDetails(details);
                System.out.println("  -> " + (details != null ? details.size() : 0) + " détails trouvés");
            }
        }

        System.out.println("=== ÉTAPE 3: Récupération des utilisateurs et rôles ===");
        
        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
        System.out.println("Utilisateurs récupérés");
        
        request.setAttribute("roles", RoleDAO.findAllVisible());
        System.out.println("Rôles récupérés");

        System.out.println("=== ÉTAPE 4: Récupération de la hiérarchie complète (rayons, catégories, sous-catégories) ===");
        
        // Récupérer la hiérarchie complète comme dans la méthode avec filtres
        System.out.println("Appel à menuCategorieDAO.getHierarchieCompleteRayons()...");
        List<RayonHierarchique> hierarchieRayons = menuCategorieDAO.getHierarchieCompleteRayons();
        
        // Debug de la hiérarchie
        if (hierarchieRayons != null) {
            System.out.println("Nombre de rayons dans la hiérarchie: " + hierarchieRayons.size());
            
            for (int i = 0; i < hierarchieRayons.size(); i++) {
                RayonHierarchique rh = hierarchieRayons.get(i);
                if (rh.getRayon() != null) {
                    System.out.println("Rayon " + (i+1) + ": ID=" + rh.getRayon().getId() + 
                                     ", Nom=" + rh.getRayon().getNom());
                    
                    // Catégories
                    if (rh.getCategories() != null) {
                        System.out.println("  Nombre de catégories: " + rh.getCategories().size());
                        for (int j = 0; j < rh.getCategories().size(); j++) {
                            MenuCategorie cat = rh.getCategories().get(j);
                            System.out.println("  Catégorie " + (j+1) + ": ID=" + cat.getId() + 
                                             ", Nom=" + cat.getNom() + 
                                             ", ParentId=" + cat.getParentId());
                        }
                    } else {
                        System.out.println("  Aucune catégorie trouvée");
                    }
                    
                    // Sous-catégories
                    if (rh.getSousCategoriesParCategorie() != null) {
                        System.out.println("  Sous-catégories par catégorie: " + 
                                         rh.getSousCategoriesParCategorie().size() + " catégories ont des sous-catégories");
                        for (Map.Entry<Integer, List<MenuCategorie>> entry : 
                             rh.getSousCategoriesParCategorie().entrySet()) {
                            System.out.println("    Catégorie ID " + entry.getKey() + 
                                             " a " + entry.getValue().size() + " sous-catégories");
                            for (MenuCategorie sousCat : entry.getValue()) {
                                System.out.println("      Sous-catégorie: ID=" + sousCat.getId() + 
                                                 ", Nom=" + sousCat.getNom());
                            }
                        }
                    } else {
                        System.out.println("  Aucune sous-catégorie trouvée");
                    }
                } else {
                    System.out.println("Rayon " + (i+1) + ": NULL");
                }
                System.out.println("---");
            }
        } else {
            System.out.println("hierarchieRayons est NULL");
        }

        System.out.println("=== ÉTAPE 5: Récupération de tous les plats avec hiérarchie ===");
        
        // Récupérer tous les plats avec leur hiérarchie
        System.out.println("Appel à menuCategorieDAO.getAllPlatsWithHierarchy()...");
        List<Plat> tousLesPlats = menuCategorieDAO.getAllPlatsWithHierarchy();
        
        if (tousLesPlats != null) {
            System.out.println("Nombre total de plats: " + tousLesPlats.size());
            for (int i = 0; i < Math.min(tousLesPlats.size(), 10); i++) { // Afficher les 10 premiers
                Plat plat = tousLesPlats.get(i);
                System.out.println("Plat " + (i+1) + ": ID=" + plat.getId() + 
                                 ", Nom=" + plat.getNom() + 
                                 ", RayonId=" + plat.getRayonId() +
                                 ", CategorieId=" + plat.getCategorieId() +
                                 ", SousCategorieId=" + plat.getSousCategorieId() +
                                 ", ProduitId=" + plat.getProductId());
            }
            if (tousLesPlats.size() > 10) {
                System.out.println("... et " + (tousLesPlats.size() - 10) + " plats supplémentaires");
            }
        } else {
            System.out.println("tousLesPlats est NULL");
        }

        System.out.println("=== ÉTAPE 6: Association des plats aux sous-catégories dans la hiérarchie ===");
        
        // Pour chaque rayon dans la hiérarchie, ajouter ses plats
        if (hierarchieRayons != null) {
            for (RayonHierarchique rayonHierarchique : hierarchieRayons) {
                if (rayonHierarchique.getRayon() != null) {
                    Map<Integer, List<Plat>> platsParSousCategorie = new HashMap<>();
                    
                    // Parcourir toutes les sous-catégories de ce rayon
                    if (rayonHierarchique.getSousCategoriesParCategorie() != null) {
                        int totalSousCategories = 0;
                        int totalPlats = 0;
                        
                        for (List<MenuCategorie> sousCategories : rayonHierarchique.getSousCategoriesParCategorie().values()) {
                            totalSousCategories += sousCategories.size();
                            for (MenuCategorie sousCategorie : sousCategories) {
                                System.out.println("Récupération des plats pour sous-catégorie ID=" + 
                                                 sousCategorie.getId() + " (" + sousCategorie.getNom() + ")");
                                List<Plat> plats = menuCategorieDAO.getPlatsBySousCategorieId(sousCategorie.getId());
                                System.out.println("  -> " + (plats != null ? plats.size() : 0) + " plats trouvés");
                                
                                if (plats != null && !plats.isEmpty()) {
                                    platsParSousCategorie.put(sousCategorie.getId(), plats);
                                    totalPlats += plats.size();
                                    
                                    // Debug des plats
                                    for (Plat plat : plats) {
                                        System.out.println("    Plat: ID=" + plat.getId() + ", Nom=" + plat.getNom());
                                    }
                                }
                            }
                        }
                        
                        System.out.println("Rayon " + rayonHierarchique.getRayon().getNom() + 
                                         ": " + totalSousCategories + " sous-catégories, " + 
                                         totalPlats + " plats au total");
                    }
                    
                    rayonHierarchique.setPlatsParSousCategorie(platsParSousCategorie);
                }
            }
        }

        System.out.println("=== ÉTAPE 7: Vérification des problèmes dans les plats ===");
        
        // Organiser par état pour faciliter la correction
        List<Plat> platsAvecProblemes = new ArrayList<>();
        List<Plat> platsNormaux = new ArrayList<>();
        
        int totalProblemes = 0;
        
        if (tousLesPlats != null) {
            for (Plat plat : tousLesPlats) {
                // Vérifier les problèmes
                boolean hasProblem = false;
                StringBuilder problemes = new StringBuilder();
                
                if (plat.getRayonId() == null || plat.getRayonId() == 0) {
                    hasProblem = true;
                    problemes.append("Pas de rayon; ");
                }
                
                if (plat.getCategorieId() == null || plat.getCategorieId() == 0) {
                    hasProblem = true;
                    problemes.append("Pas de catégorie; ");
                }
                
                if (plat.getProduit() == null) {
                    hasProblem = true;
                    problemes.append("Pas de produit; ");
                }
                
                if (hasProblem) {
                    totalProblemes++;
                    // Ajouter l'info du problème à l'objet Plat
                    plat.setDescription((plat.getDescription() != null ? plat.getDescription() : "") + 
                                       " [PROBLÈMES: " + problemes.toString() + "]");
                    platsAvecProblemes.add(plat);
                    System.out.println("Plat avec problème ID " + plat.getId() + ": " + plat.getNom() + 
                                     " - Problèmes: " + problemes.toString());
                } else {
                    platsNormaux.add(plat);
                }
            }
        }
        
        System.out.println("Résultat de la vérification:");
        System.out.println("  - Plats normaux: " + platsNormaux.size());
        System.out.println("  - Plats avec problèmes: " + platsAvecProblemes.size());
        System.out.println("  - Total problèmes détectés: " + totalProblemes);

        System.out.println("=== ÉTAPE 8: Ajout des attributs à la requête ===");
        
        // Passer TOUTES les données nécessaires à la JSP
        request.setAttribute("hierarchieRayons", hierarchieRayons);
        System.out.println("  - hierarchieRayons: " + (hierarchieRayons != null ? hierarchieRayons.size() : 0) + " rayons");
        
        request.setAttribute("tousLesPlats", tousLesPlats);
        System.out.println("  - tousLesPlats: " + (tousLesPlats != null ? tousLesPlats.size() : 0) + " éléments");
        
        request.setAttribute("platsAvecProblemes", platsAvecProblemes);
        System.out.println("  - platsAvecProblemes: " + platsAvecProblemes.size() + " éléments");
        
        request.setAttribute("platsNormaux", platsNormaux);
        System.out.println("  - platsNormaux: " + platsNormaux.size() + " éléments");
        
        request.setAttribute("commandes", commandes);
        System.out.println("  - commandes: " + (commandes != null ? commandes.size() : 0) + " éléments");
        
        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
        System.out.println("  - utilisateurs: attribut ajouté");
        
        request.setAttribute("roles", RoleDAO.findAllVisible());
        System.out.println("  - roles: attribut ajouté");
        
        System.out.println("=== ÉTAPE 9: Forward vers la JSP ===");
        System.out.println("Forward vers: /blok/liste-toutes-commandes.jsp");
        System.out.println("Tous les attributs ont été correctement ajoutés à la requête");

        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
        
        System.out.println("=== FIN listerToutesCommandes ===");
    }


    
//WITH DATE / HEURE 
//    private void getAllCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        String staffIdStr = request.getParameter("staffId");
//        String roleIdStr = request.getParameter("roleId");
//        String boissonIdStr = request.getParameter("boissonId");
//        String nourritureIdStr = request.getParameter("nourritureId");
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        Integer staffId = parseInteger(staffIdStr);
//        Integer roleId = parseInteger(roleIdStr);
//
//        // Priorité au plat sélectionné : boisson ou nourriture
//        Integer platId = null;
//        Integer produitId = null;
//
//        if (boissonIdStr != null && !boissonIdStr.isEmpty()) {
//            Plat boisson = platDAO.chercherParId(parseInteger(boissonIdStr));
//            if (boisson != null && boisson.getProductId() > 0) {
//                produitId = boisson.getProductId();
//            } else if (boisson != null) {
//                platId = boisson.getId();
//            }
//        } else if (nourritureIdStr != null && !nourritureIdStr.isEmpty()) {
//            Plat nourriture = platDAO.chercherParId(parseInteger(nourritureIdStr));
//            if (nourriture != null && nourriture.getProductId() > 0) {
//                produitId = nourriture.getProductId();
//            } else if (nourriture != null) {
//                platId = nourriture.getId();
//            }
//        }
//
// 
//
//     // Parser les dates depuis les paramètres
//        LocalDate localDateDebut = null;
//        LocalDate localDateFin = null;
//
//        if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//            localDateDebut = LocalDate.parse(dateDebutStr); // yyyy-MM-dd
//        }
//        if (dateFinStr != null && !dateFinStr.isEmpty()) {
//            localDateFin = LocalDate.parse(dateFinStr);
//        }
//
//        // Valeurs par défaut si null
//        if (localDateDebut == null) {
//            localDateDebut = LocalDate.now();
//        }
//        if (localDateFin == null) {
//            localDateFin = LocalDate.now();
//        }
//
//        // Convertir en Timestamp pour la DAO (début et fin de journée)
//        Timestamp dateDebut = Timestamp.valueOf(localDateDebut.atStartOfDay());
//        Timestamp dateFin = Timestamp.valueOf(localDateFin.atTime(23, 59, 59));
//
//        // Swap si inversées
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//
//        // Récupérer commandes filtrées
//        // Puis appeler la DAO avec les deux valeurs
//        List<Commande> commandes = commandeDAO.getAllCommandesVisiblesByFiltres(staffId, roleId, platId, produitId, dateDebut, dateFin);
//
//
//        // Injecter les détails
//        for (Commande commande : commandes) {
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//            commande.setDetails(details);
//        }
//
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//        // Récupérer les plats Boissons et Nourritures
//        List<Plat> boissons = menuCategorieDAO.getThePlatsByCategoryId(6);
//        List<Plat> nourritures = menuCategorieDAO.getThePlatsByCategoryId(9);
//
//        ProduitDAO produitDAO = new ProduitDAO();
//
//        // Pour tous les plats existants de type boissons avec productId, récupérer le produit
//        for (Plat plat : boissons) {
//            if (plat.getProductId() > 0) {
//                Produit produit = produitDAO.chercherParId(plat.getProductId());
//                if (produit != null) {
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    plat.setNom(produit.getNom()); // remplace le nom du plat par le nom du produit
//                }
//            }
//        }
//
//        request.setAttribute("boissons", boissons);
//        request.setAttribute("nourritures", nourritures);
//        request.setAttribute("commandes", commandes);
//
//        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//    }
//   GETALLCOMMANDESBYFILTRESR BON 23/12/25
//    private void getAllCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        String staffIdStr = request.getParameter("staffId");
//        String roleIdStr = request.getParameter("roleId");
//        String boissonIdStr = request.getParameter("boissonId");
//        String nourritureIdStr = request.getParameter("nourritureId");
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//
//        Integer staffId = parseInteger(staffIdStr);
//        Integer roleId = parseInteger(roleIdStr);
//
//        // Priorité au plat sélectionné : boisson ou nourriture
//        Integer platId = null;
//        Integer produitId = null;
//
//        if (boissonIdStr != null && !boissonIdStr.isEmpty()) {
//            Plat boisson = platDAO.chercherParId(parseInteger(boissonIdStr));
//            if (boisson != null && boisson.getProductId() > 0) {
//                produitId = boisson.getProductId();
//            } else if (boisson != null) {
//                platId = boisson.getId();
//            }
//        } else if (nourritureIdStr != null && !nourritureIdStr.isEmpty()) {
//            Plat nourriture = platDAO.chercherParId(parseInteger(nourritureIdStr));
//            if (nourriture != null && nourriture.getProductId() > 0) {
//                produitId = nourriture.getProductId();
//            } else if (nourriture != null) {
//                platId = nourriture.getId();
//            }
//        }
//
//        // Parser les dates depuis les paramètres (datetime-local : yyyy-MM-dd'T'HH:mm)
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        LocalDateTime localDateDebut = null;
//        LocalDateTime localDateFin = null;
//
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                localDateDebut = LocalDateTime.parse(dateDebutStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateDebut = null;
//        }
//
//        try {
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                localDateFin = LocalDateTime.parse(dateFinStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateFin = null;
//        }
//
//        // Valeurs par défaut si null
//        if (localDateDebut == null) {
//            localDateDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
//        }
//        if (localDateFin == null) {
//            localDateFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
//        }
//
//        // Convertir en Timestamp pour la DAO
//        Timestamp dateDebut = Timestamp.valueOf(localDateDebut);
//        Timestamp dateFin = Timestamp.valueOf(localDateFin);
//
//        // Swap si inversées
//        if (dateDebut.after(dateFin)) {
//            Timestamp tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
//
//        // Récupérer commandes filtrées
//        List<Commande> commandes = commandeDAO.getAllCommandesVisiblesByFiltres(
//                staffId, roleId, platId, produitId, dateDebut, dateFin);
//
//        // Injecter les détails
//        for (Commande commande : commandes) {
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//            commande.setDetails(details);
//        }
//
//        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//        request.setAttribute("roles", RoleDAO.findAllVisible());
//
//        // Récupérer les plats Boissons et Nourritures
//        List<Plat> boissons = menuCategorieDAO.getPlatsByRayonId(1);
//        List<Plat> nourritures = menuCategorieDAO.getPlatsByRayonId(2);
//
//        ProduitDAO produitDAO = new ProduitDAO();
//
//        for (Plat plat : boissons) {
//            if (plat.getProductId() > 0) {
//                Produit produit = produitDAO.chercherParId(plat.getProductId());
//                if (produit != null) {
//                    plat.setProduit(produit);
//                    plat.setTypePlat("PRODUIT");
//                    plat.setNom(produit.getNom());
//                }
//            }
//        }
//
//        request.setAttribute("boissons", boissons);
//        request.setAttribute("nourritures", nourritures);
//        request.setAttribute("commandes", commandes);
//
//        request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//    }
   
//   2 BON AVANT AJOUT FILTRE CAT-SUSCAT
//   private void getAllCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
//	        throws ServletException, IOException {
//
//	    HttpSession session = request.getSession(false);
//	    Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//	    if (userId == null) {
//	        response.sendRedirect("login.jsp");
//	        return;
//	    }
//
//	    // Récupérer tous les paramètres de filtre
//	    String staffIdStr = request.getParameter("staffId");
//	    String roleIdStr = request.getParameter("roleId");
//	    String rayonIdStr = request.getParameter("rayonId");
//	    String platIdStr = request.getParameter("platId");
//	    String dateDebutStr = request.getParameter("dateDebut");
//	    String dateFinStr = request.getParameter("dateFin");
//
//	    // Parser les paramètres avec gestion des erreurs
//	    Integer staffId = parseIntegerSafely(staffIdStr);
//	    Integer roleId = parseIntegerSafely(roleIdStr);
//	    Integer rayonId = parseIntegerSafely(rayonIdStr);
//	    Integer platId = parseIntegerSafely(platIdStr);
//	    
//	    // NOTE: On utilise platId directement, pas besoin de le convertir
//	    // car votre DAO accepte déjà platId comme paramètre
//
//	    // Parser les dates depuis les paramètres (datetime-local : yyyy-MM-dd'T'HH:mm)
//	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//	    LocalDateTime localDateDebut = null;
//	    LocalDateTime localDateFin = null;
//
//	    try {
//	        if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//	            localDateDebut = LocalDateTime.parse(dateDebutStr, formatter);
//	        }
//	    } catch (Exception e) {
//	        localDateDebut = null;
//	        System.err.println("Erreur parsing dateDebut: " + e.getMessage());
//	    }
//
//	    try {
//	        if (dateFinStr != null && !dateFinStr.isEmpty()) {
//	            localDateFin = LocalDateTime.parse(dateFinStr, formatter);
//	        }
//	    } catch (Exception e) {
//	        localDateFin = null;
//	        System.err.println("Erreur parsing dateFin: " + e.getMessage());
//	    }
//
//	    // Valeurs par défaut si null
//	    if (localDateDebut == null) {
//	        localDateDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
//	    }
//	    if (localDateFin == null) {
//	        localDateFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
//	    }
//
//	    // Convertir en Date pour la DAO
//	    Date dateDebut = java.sql.Timestamp.valueOf(localDateDebut);
//	    Date dateFin = java.sql.Timestamp.valueOf(localDateFin);
//
//	    // Swap si inversées
//	    if (dateDebut.after(dateFin)) {
//	        Date tmp = dateDebut;
//	        dateDebut = dateFin;
//	        dateFin = tmp;
//	    }
//
//	    // PROBLÈME : Votre DAO n'accepte pas rayonId comme paramètre
//	    // Solution 1: Modifier votre DAO (idéal)
//	    // Solution 2: Filtrage en mémoire après récupération
//	    
//	    System.out.println("Appel à getAllCommandesVisiblesByFiltres avec:");
//	    System.out.println("  staffId: " + staffId);
//	    System.out.println("  roleId: " + roleId);
//	    System.out.println("  platId: " + platId);
//	    System.out.println("  produitId: null (non utilisé dans cette version)");
//	    System.out.println("  dateDebut: " + dateDebut);
//	    System.out.println("  dateFin: " + dateFin);
//
//	    // Récupérer TOUTES les commandes d'abord
//	    List<Commande> toutesCommandes = commandeDAO.getAllCommandesVisiblesByFiltres(
//	            staffId, roleId, platId, null, dateDebut, dateFin);
//	    
//	    // Filtrage additionnel par rayon en mémoire si nécessaire
//	    List<Commande> commandesFiltrees = toutesCommandes;
//	    
//	    if (rayonId != null && rayonId > 0) {
//	        // Si vous voulez filtrer par rayon, il faut modifier la DAO
//	        // Pour l'instant, on ne peut pas filtrer par rayon avec votre DAO actuelle
//	        System.out.println("ATTENTION: Le filtre rayonId=" + rayonId + " ne peut pas être appliqué avec la DAO actuelle");
//	        // Vous pouvez ajouter ce filtrage plus tard
//	    }
//
//	    // Injecter les détails
//	    for (Commande commande : commandesFiltrees) {
//	        List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
//	        commande.setDetails(details);
//	    }
//
//	    request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
//	    request.setAttribute("roles", RoleDAO.findAllVisible());
//
//	    // Récupérer dynamiquement TOUS les rayons actifs
//	    List<Rayon> rayonsActifs = menuCategorieDAO.getAllRayonsActifs();
//	    
//	    // Créer une liste de RayonAvecPlats
//	    List<RayonAvecPlats> rayonsAvecPlats = new ArrayList<>();
//	    
//	    ProduitDAO produitDAO = new ProduitDAO();
//	    
//	    // Pour chaque rayon, récupérer les plats et créer l'objet
//	    for (Rayon rayon : rayonsActifs) {
//	        List<Plat> plats = menuCategorieDAO.getAllPlatsWithoutRestrictions();
//	        
//	        // Filtrer les plats pour ce rayon spécifique
//	        List<Plat> platsDuRayon = new ArrayList<>();
//	        
//	        for (Plat plat : plats) {
//	            // Vérifier si le plat appartient à ce rayon
//	            if (rayon != null && plat.getRayonId() != null && 
//	                plat.getRayonId().equals(rayon.getId())) {
//	                
//	                // CORRECTION: Vérifier d'abord si le produit existe
//	                if (plat.getProduit() != null && plat.getProduit().getId() > 0) {
//	                    Produit produit = produitDAO.chercherParId(plat.getProduit().getId());
//	                    if (produit != null) {
//	                        plat.setProduit(produit);
//	                        plat.setTypePlat("PRODUIT");
//	                        if (plat.getNom() == null || plat.getNom().isEmpty()) {
//	                            plat.setNom(produit.getNom());
//	                        }
//	                    }
//	                }
//	                
//	                platsDuRayon.add(plat);
//	            }
//	        }
//	        
//	        if (!platsDuRayon.isEmpty()) {
//	            RayonAvecPlats rayonAvecPlats = new RayonAvecPlats(rayon, platsDuRayon);
//	            rayonsAvecPlats.add(rayonAvecPlats);
//	        }
//	    }
//	    
//	    // Ajouter une section pour les plats sans rayon (pour débogage)
//	    List<Plat> tousLesPlats = menuCategorieDAO.getAllPlatsWithoutRestrictions();
//	    List<Plat> platsSansRayon = new ArrayList<>();
//	    
//	    for (Plat plat : tousLesPlats) {
//	        if (plat.getRayonId() == null || plat.getRayonId() == 0) {
//	            platsSansRayon.add(plat);
//	        }
//	    }
//	    
//	    if (!platsSansRayon.isEmpty()) {
//	        Rayon rayonSpecial = new Rayon();
//	        rayonSpecial.setId(0);
//	        rayonSpecial.setNom("PLATS SANS RAYON (À CORRIGER)");
//	        RayonAvecPlats rayonProbleme = new RayonAvecPlats(rayonSpecial, platsSansRayon);
//	        rayonsAvecPlats.add(rayonProbleme);
//	    }
//
//	    // SUPPRIMEZ ces lignes - NE PAS créer d'attributs boissons/nourritures
//	    // List<Plat> boissons = new ArrayList<>();
//	    // List<Plat> nourritures = new ArrayList<>();
//	    // ... code supprimé ...
//
//	    // Passer UNIQUEMENT les attributs dynamiques à la JSP
//	    request.setAttribute("rayonsAvecPlats", rayonsAvecPlats);
//	    request.setAttribute("commandes", commandesFiltrees);
//	    
//	    // Passer aussi les paramètres de filtre pour les garder dans le formulaire
//	    request.setAttribute("filtreStaffId", staffIdStr);
//	    request.setAttribute("filtreRoleId", roleIdStr);
//	    request.setAttribute("filtreRayonId", rayonIdStr);
//	    request.setAttribute("filtrePlatId", platIdStr);
//	    request.setAttribute("filtreDateDebut", dateDebutStr);
//	    request.setAttribute("filtreDateFin", dateFinStr);
//
//	    request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
//	}
    private void getAllCommandesByFiltres(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Récupérer tous les paramètres de filtre hiérarchiques
        String staffIdStr = request.getParameter("staffId");
        String roleIdStr = request.getParameter("roleId");
        String rayonIdStr = request.getParameter("rayonId");
        String categorieIdStr = request.getParameter("categorieId");
        String sousCategorieIdStr = request.getParameter("sousCategorieId");
        String platIdStr = request.getParameter("platId");
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");

        System.out.println("=== DEBUG: Paramètres reçus ===");
        System.out.println("staffId: " + staffIdStr);
        System.out.println("roleId: " + roleIdStr);
        System.out.println("rayonId: " + rayonIdStr);
        System.out.println("categorieId: " + categorieIdStr);
        System.out.println("sousCategorieId: " + sousCategorieIdStr);
        System.out.println("platId: " + platIdStr);
        System.out.println("dateDebut: " + dateDebutStr);
        System.out.println("dateFin: " + dateFinStr);

        // Parser les paramètres
        Integer staffId = parseIntegerSafely(staffIdStr);
        Integer roleId = parseIntegerSafely(roleIdStr);
        Integer rayonId = parseIntegerSafely(rayonIdStr);
        Integer categorieId = parseIntegerSafely(categorieIdStr);
        Integer sousCategorieId = parseIntegerSafely(sousCategorieIdStr);
        Integer platId = parseIntegerSafely(platIdStr);

        // Parser les dates
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        LocalDateTime localDateDebut = null;
//        LocalDateTime localDateFin = null;
//
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                localDateDebut = LocalDateTime.parse(dateDebutStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateDebut = null;
//            System.err.println("Erreur parsing dateDebut: " + e.getMessage());
//        }
//
//        try {
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                localDateFin = LocalDateTime.parse(dateFinStr, formatter);
//            }
//        } catch (Exception e) {
//            localDateFin = null;
//            System.err.println("Erreur parsing dateFin: " + e.getMessage());
//        }
//
//        // Valeurs par défaut si null
//        if (localDateDebut == null) {
//            localDateDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
//        }
//        if (localDateFin == null) {
//            localDateFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
//        }
//
//        // Convertir en Date pour la DAO
//        Date dateDebut = java.sql.Timestamp.valueOf(localDateDebut);
//        Date dateFin = java.sql.Timestamp.valueOf(localDateFin);
//
//        // Swap si inversées
//        if (dateDebut.after(dateFin)) {
//            Date tmp = dateDebut;
//            dateDebut = dateFin;
//            dateFin = tmp;
//        }
        Timestamp dateDebut = parseDateTime(request, "dateDebut", true);
        Timestamp dateFin = parseDateTime(request, "dateFin", false);

        if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
            Timestamp tmp = dateDebut;
            dateDebut = dateFin;
            dateFin = tmp;
        }

        System.out.println("=== DEBUG: Paramètres parsés ===");
        System.out.println("staffId (parsed): " + staffId);
        System.out.println("roleId (parsed): " + roleId);
        System.out.println("rayonId (parsed): " + rayonId);
        System.out.println("categorieId (parsed): " + categorieId);
        System.out.println("sousCategorieId (parsed): " + sousCategorieId);
        System.out.println("platId (parsed): " + platId);
        System.out.println("dateDebut (parsed): " + dateDebut);
        System.out.println("dateFin (parsed): " + dateFin);

        // TEST : D'abord, récupérez TOUTES les commandes sans filtre pour voir ce qu'elles contiennent
        System.out.println("=== DEBUG: TEST - Récupération de toutes les commandes pour analyse ===");
        List<Commande> toutesCommandes = commandeDAO.getAllCommandesVisibles();
        System.out.println("Nombre total de commandes: " + (toutesCommandes != null ? toutesCommandes.size() : 0));
        
        if (toutesCommandes != null) {
            for (Commande cmd : toutesCommandes) {
                System.out.println("Commande ID: " + cmd.getId() + " - " + cmd.getNumeroCommande());
                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(cmd.getId());
                if (details != null) {
                    System.out.println("  Détails: " + details.size() + " lignes");
                    for (CommandeDetail detail : details) {
                        // Récupérer les infos du plat ou produit pour voir le rayon
                        if (detail.getPlat() != null) {
                            System.out.println("    - Plat ID: " + detail.getPlat().getId());
                        } else if (detail.getProduit() != null) {
                            System.out.println("    - Produit ID: " + detail.getProduit().getId() + 
                                             ", Nom: " + detail.getProduit().getNom() );
                        } else {
                            System.out.println("    - Aucun plat ni produit associé");
                        }
                    }
                }
            }
        }

        // Récupérer les commandes filtrées
        System.out.println("=== DEBUG: Appel à getAllCommandesVisiblesByFiltresHierarchiques ===");
        List<Commande> commandes = commandeDAO.getAllCommandesVisiblesByFiltresHierarchiques(
                staffId, roleId, rayonId, categorieId, sousCategorieId, platId, dateDebut, dateFin);

        System.out.println("=== DEBUG: Nombre de commandes trouvées: " + (commandes != null ? commandes.size() : 0));

        // Injecter les détails
        if (commandes != null) {
            for (Commande commande : commandes) {
                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
                commande.setDetails(details);
                System.out.println("Commande ID: " + commande.getId() + " - " + commande.getNumeroCommande() + 
                                 " - Détails: " + (details != null ? details.size() : 0));
            }
        }


	    request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());
	    request.setAttribute("roles", RoleDAO.findAllVisible());

	    // Récupérer la hiérarchie complète pour les filtres
	    System.out.println("=== DEBUG: Récupération de la hiérarchie complète ===");
	    List<RayonHierarchique> hierarchieRayons = menuCategorieDAO.getHierarchieCompleteRayons();
	    
	    // Debug de la hiérarchie
	    if (hierarchieRayons != null) {
	        System.out.println("Nombre de rayons dans la hiérarchie: " + hierarchieRayons.size());
	        
	        for (int i = 0; i < hierarchieRayons.size(); i++) {
	            RayonHierarchique rh = hierarchieRayons.get(i);
	            if (rh.getRayon() != null) {
	                System.out.println("Rayon " + (i+1) + ": ID=" + rh.getRayon().getId() + 
	                                 ", Nom=" + rh.getRayon().getNom());
	                
	                // Catégories
	                if (rh.getCategories() != null) {
	                    System.out.println("  Nombre de catégories: " + rh.getCategories().size());
	                    for (int j = 0; j < rh.getCategories().size(); j++) {
	                        MenuCategorie cat = rh.getCategories().get(j);
	                        System.out.println("  Catégorie " + (j+1) + ": ID=" + cat.getId() + 
	                                         ", Nom=" + cat.getNom() + 
	                                         ", ParentId=" + cat.getParentId());
	                    }
	                } else {
	                    System.out.println("  Aucune catégorie trouvée");
	                }
	                
	                // Sous-catégories
	                if (rh.getSousCategoriesParCategorie() != null) {
	                    System.out.println("  Sous-catégories par catégorie: " + 
	                                     rh.getSousCategoriesParCategorie().size() + " catégories ont des sous-catégories");
	                    for (Map.Entry<Integer, List<MenuCategorie>> entry : 
	                         rh.getSousCategoriesParCategorie().entrySet()) {
	                        System.out.println("    Catégorie ID " + entry.getKey() + 
	                                         " a " + entry.getValue().size() + " sous-catégories");
	                        for (MenuCategorie sousCat : entry.getValue()) {
	                            System.out.println("      Sous-catégorie: ID=" + sousCat.getId() + 
	                                             ", Nom=" + sousCat.getNom());
	                        }
	                    }
	                } else {
	                    System.out.println("  Aucune sous-catégorie trouvée");
	                }
	            } else {
	                System.out.println("Rayon " + (i+1) + ": NULL");
	            }
	            System.out.println("---");
	        }
	    } else {
	        System.out.println("hierarchieRayons est NULL");
	    }
	    
	    // Récupérer tous les plats avec leur hiérarchie
	    System.out.println("=== DEBUG: Récupération de tous les plats ===");
	    List<Plat> tousLesPlats = menuCategorieDAO.getAllPlatsWithHierarchy();
	    
	    if (tousLesPlats != null) {
	        System.out.println("Nombre total de plats: " + tousLesPlats.size());
	        for (int i = 0; i < Math.min(tousLesPlats.size(), 10); i++) { // Afficher les 10 premiers
	            Plat plat = tousLesPlats.get(i);
	            System.out.println("Plat " + (i+1) + ": ID=" + plat.getId() + 
	                             ", Nom=" + plat.getNom() + 
	                             ", RayonId=" + plat.getRayonId() +
	                             ", CategorieId=" + plat.getCategorieId() +
	                             ", SousCategorieId=" + plat.getSousCategorieId() +
	                             ", ProduitId=" + plat.getProductId());
	        }
	        if (tousLesPlats.size() > 10) {
	            System.out.println("... et " + (tousLesPlats.size() - 10) + " plats supplémentaires");
	        }
	    } else {
	        System.out.println("tousLesPlats est NULL");
	    }
	    
	    // Pour chaque rayon dans la hiérarchie, ajouter ses plats
	    System.out.println("=== DEBUG: Association des plats aux sous-catégories ===");
	    if (hierarchieRayons != null) {
	        for (RayonHierarchique rayonHierarchique : hierarchieRayons) {
	            if (rayonHierarchique.getRayon() != null) {
	                Map<Integer, List<Plat>> platsParSousCategorie = new HashMap<>();
	                
	                // Parcourir toutes les sous-catégories de ce rayon
	                if (rayonHierarchique.getSousCategoriesParCategorie() != null) {
	                    int totalSousCategories = 0;
	                    int totalPlats = 0;
	                    
	                    for (List<MenuCategorie> sousCategories : rayonHierarchique.getSousCategoriesParCategorie().values()) {
	                        totalSousCategories += sousCategories.size();
	                        for (MenuCategorie sousCategorie : sousCategories) {
	                            System.out.println("Récupération des plats pour sous-catégorie ID=" + 
	                                             sousCategorie.getId() + " (" + sousCategorie.getNom() + ")");
	                            List<Plat> plats = menuCategorieDAO.getPlatsBySousCategorieId(sousCategorie.getId());
	                            System.out.println("  -> " + (plats != null ? plats.size() : 0) + " plats trouvés");
	                            
	                            if (plats != null && !plats.isEmpty()) {
	                                platsParSousCategorie.put(sousCategorie.getId(), plats);
	                                totalPlats += plats.size();
	                                
	                                // Debug des plats
	                                for (Plat plat : plats) {
	                                    System.out.println("    Plat: ID=" + plat.getId() + ", Nom=" + plat.getNom());
	                                }
	                            }
	                        }
	                    }
	                    
	                    System.out.println("Rayon " + rayonHierarchique.getRayon().getNom() + 
	                                     ": " + totalSousCategories + " sous-catégories, " + 
	                                     totalPlats + " plats au total");
	                }
	                
	                rayonHierarchique.setPlatsParSousCategorie(platsParSousCategorie);
	            }
	        }
	    }

	    // Passer les données à la JSP
	    request.setAttribute("hierarchieRayons", hierarchieRayons);
	    request.setAttribute("tousLesPlats", tousLesPlats);
	    request.setAttribute("commandes", commandes);
	    
	    // Passer aussi les paramètres de filtre pour les garder dans le formulaire
	    request.setAttribute("filtreStaffId", staffIdStr);
	    request.setAttribute("filtreRoleId", roleIdStr);
	    request.setAttribute("filtreRayonId", rayonIdStr);
	    request.setAttribute("filtreCategorieId", categorieIdStr);
	    request.setAttribute("filtreSousCategorieId", sousCategorieIdStr);
	    request.setAttribute("filtrePlatId", platIdStr);
	    request.setAttribute("filtreDateDebut", dateDebutStr);
	    request.setAttribute("filtreDateFin", dateFinStr);

	    System.out.println("=== DEBUG: Fin de la méthode, forwarding vers JSP ===");
	    request.getRequestDispatcher("/blok/liste-toutes-commandes.jsp").forward(request, response);
	}
	// Méthode utilitaire pour parser les entiers avec gestion des erreurs
	private Integer parseIntegerSafely(String value) {
	    if (value == null || value.isEmpty() || value.trim().equals("")) {
	        return null;
	    }
	    try {
	        return Integer.parseInt(value.trim());
	    } catch (NumberFormatException e) {
	        System.err.println("Erreur parsing integer: " + value + " - " + e.getMessage());
	        return null;
	    }
	}


    private Integer parseInteger(String str) {
        try { return (str != null && !str.isEmpty()) ? Integer.parseInt(str) : null; }
        catch (NumberFormatException e) { return null; }
    }

    private Timestamp parseTimestamp(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            return new Timestamp(sdf.parse(str).getTime());
        } catch (Exception e) {
            return null;
        }
    }
//    private void ListerCommandesParFiltresForUserId(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        System.out.println("DEBUG: Début getAllCommandesByFiltresForUserId, userId=" + userId);
//
//        if (userId == null) {
//            System.out.println("DEBUG: userId null, redirection vers login.jsp");
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        String typeCommande = request.getParameter("commandeType"); // "paye" ou "credit"
//        if (typeCommande == null || typeCommande.isEmpty()) {
//            typeCommande = "paye"; // valeur par défaut
//        }
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//        System.out.println("DEBUG: typeCommande=" + typeCommande + ", dateDebut=" + dateDebutStr + ", dateFin=" + dateFinStr);
//
//        // Par défaut aujourd'hui
//        Timestamp dateDebut = (dateDebutStr != null && !dateDebutStr.isEmpty())
//                ? Timestamp.valueOf(dateDebutStr + " 00:00:00")
//                : Timestamp.valueOf(LocalDate.now().atStartOfDay());
//
//        Timestamp dateFin = (dateFinStr != null && !dateFinStr.isEmpty())
//                ? Timestamp.valueOf(dateFinStr + " 23:59:59")
//                : Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
//
//        System.out.println("DEBUG: Timestamp dateDebut=" + dateDebut + ", dateFin=" + dateFin);
//
//        List<Commande> commandes = new ArrayList<>();
//
//        try {
//            if ("credit".equalsIgnoreCase(typeCommande)) {
//                System.out.println("DEBUG: Appel getCommandesCredit()");
//                commandes = commandeDAO.getCommandesCredit(null, userId, dateDebut, dateFin);
//            } else if ("paye".equalsIgnoreCase(typeCommande)) {
//                System.out.println("DEBUG: Appel getCommandesPaye()");
//                commandes = commandeDAO.getCommandesPaye(userId, dateDebut, dateFin);
//            }
//
//            System.out.println("DEBUG: Nombre de commandes récupérées=" + commandes.size());
//
//            // Récupérer les détails de chaque commande
//            for (Commande cmd : commandes) {
//                System.out.println("DEBUG: Chargement des détails pour commande ID=" + cmd.getId());
//                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(cmd.getId());
//                cmd.setDetails(details);
//                System.out.println("DEBUG: Nombre de détails pour commande ID=" + cmd.getId() + " -> " + details.size());
//            }
//
//        } catch (Exception e) {
//            System.out.println("DEBUG: Exception attrapée lors de la récupération des commandes");
//            e.printStackTrace();
//            request.setAttribute("errorMessage", "Erreur lors de la récupération des commandes : " + e.getMessage());
//        }
//
//        request.setAttribute("commandes", commandes);
//        request.setAttribute("commandeType", typeCommande);
//        request.setAttribute("dateDebut", dateDebutStr);
//        request.setAttribute("dateFin", dateFinStr);
//
//        System.out.println("DEBUG: Forward vers /blok/liste-toutes-commandes.jsp");
//        request.getRequestDispatcher("/blok/liste-commandes.jsp").forward(request, response);
//    }
//
//
//    
    
    private void ListerCommandesParFiltresForUserId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        System.out.println("DEBUG: Début ListerCommandesParFiltresForUserId, userId=" + userId);

        if (userId == null) {
            System.out.println("DEBUG: userId null, redirection vers login.jsp");
            response.sendRedirect("login.jsp");
            return;
        }

        // Récupération des paramètres de filtre
        String typeCommande = request.getParameter("commandeType"); // "paye" ou "credit"
        if (typeCommande == null || typeCommande.isEmpty()) {
            typeCommande = "paye"; // valeur par défaut
        }

		        String dateDebutStr = request.getParameter("dateDebut");
		        String dateFinStr = request.getParameter("dateFin");
		
		        System.out.println("DEBUG: typeCommande=" + typeCommande + ", dateDebut=" + dateDebutStr + ", dateFin=" + dateFinStr);
		
		        // Conversion en Timestamp SI les champs ne sont pas vides
		        Timestamp dateDebut = null;
		        Timestamp dateFin = null;
		
		        if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
		            dateDebut = Timestamp.valueOf(dateDebutStr + " 00:00:00");
		        }
		
		        if (dateFinStr != null && !dateFinStr.isEmpty()) {
		            dateFin = Timestamp.valueOf(dateFinStr + " 23:59:59");
		        }
		
		        System.out.println("DEBUG: Timestamp dateDebut=" + dateDebut + ", dateFin=" + dateFin);
		
		        List<Commande> commandes = new ArrayList<>();
		
		        try {
		            if ("credit".equalsIgnoreCase(typeCommande)) {
		                System.out.println("DEBUG: Appel getCommandesCredit()");
		                commandes = commandeDAO.getCommandesCredit(null, userId, dateDebut, dateFin);
		
		            } else if ("paye".equalsIgnoreCase(typeCommande)) {
		                System.out.println("DEBUG: Appel getCommandesPaye()");
		                commandes = commandeDAO.getCommandesPaye(userId, dateDebut, dateFin);
		            }
		            else if ("creditnonvalide".equalsIgnoreCase(typeCommande)) {
		                commandes = commandeDAO.getCommandesCreditNonValide(userId, dateDebut, dateFin);
		            }

		
		        } catch (Exception e) {
		            e.printStackTrace();
		        }


        // Passer les valeurs à la JSP pour affichage et réaffichage des filtres
        request.setAttribute("commandes", commandes);
        request.setAttribute("commandeType", typeCommande);
        request.setAttribute("dateDebut", dateDebutStr);
        request.setAttribute("dateFin", dateFinStr);

        System.out.println("DEBUG: Forward vers /blok/liste-commandes.jsp");
        request.getRequestDispatcher("/blok/liste-commandes.jsp").forward(request, response);
    }
    
    private void listerCommandesCreditAll(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les filtres
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr   = request.getParameter("dateFin");
        String clientIdStr  = request.getParameter("clientId");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Ne pas définir de dates par défaut - si pas de dates, on prend tout
        Timestamp dateDebut = null;
        Timestamp dateFin = null;
        
        // Convertir les dates seulement si elles sont fournies
        if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
            dateDebut = Timestamp.valueOf(dateDebutStr + " 00:00:00");
        }
        if (dateFinStr != null && !dateFinStr.isEmpty()) {
            dateFin = Timestamp.valueOf(dateFinStr + " 23:59:59");
        }
        
        Integer clientId = (clientIdStr != null && !clientIdStr.isEmpty()) ? Integer.parseInt(clientIdStr) : null;

        List<Commande> commandes = commandeDAO.getCommandesCreditAllClients(dateDebut, dateFin, clientId);

        // Charger la liste des clients pour les filtres
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        request.setAttribute("utilisateurs", utilisateurDAO.findAllVisible());

        request.setAttribute("commandes", commandes);
        request.setAttribute("dateDebut", dateDebutStr); // Garder les valeurs originales
        request.setAttribute("dateFin", dateFinStr);
        request.setAttribute("clientId", clientId);

        request.getRequestDispatcher("/blok/liste-commandes-credit.jsp").forward(request, response);
    }


    private void listeCommandeEmisParStaff(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        
        Timestamp dateDebut = null;
        Timestamp dateFin = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
                dateDebut = new Timestamp(parsedDebut.getTime());
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                java.util.Date parsedFin = sdf.parse(dateFinStr);
                // Fin de journée
                dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // Optionnel : définir dateDebut/dateFin à null si parsing échoue
            dateDebut = null;
            dateFin = null;
        }
        List<Commande> commandes = commandeDAO.getAllCommandesByStaffMember(userId, dateDebut, dateFin);
        for (Commande commande : commandes) {
            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
            commande.setDetails(details); // injecter les détails
        }
        System.out.println(commandes);
        request.setAttribute("commandes", commandes);

       // request.setAttribute("commandes", commandes);
        request.getRequestDispatcher("liste-com-passer-par-staff.jsp").forward(request, response);
    }

//    private void getDetailsAsJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        String idStr = request.getParameter("id");
//        if (idStr == null) {
//            response.getWriter().write("[]");
//            return;
//        }
//        try {
//            int commandeId = Integer.parseInt(idStr);
//            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
//
//            Gson gson = new GsonBuilder()
//            	    .setExclusionStrategies(new ExclusionStrategy() {
//            	        @Override
//            	        public boolean shouldSkipField(FieldAttributes f) {
//            	            return f.getName().equals("produit") || f.getName().equals("plat");
//            	        }
//            	        @Override
//            	        public boolean shouldSkipClass(Class<?> clazz) {
//            	            return false;
//            	        }
//            	    })
//            	    .create();
//
//
//            String json = gson.toJson(details);
//            response.getWriter().write(json);
//
//        } catch (NumberFormatException e) {
//            response.getWriter().write("[]");
//        }
//    }
    
    private void getDetailsAsJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.getWriter().write("[]");
            return;
        }

        try {
            int commandeId = Integer.parseInt(idStr);
            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);

            // On n'exclut plus produit ni plat
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss") // optionnel, pour les dates
                    .create();

            String json = gson.toJson(details);
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.getWriter().write("[]");
        }
    }



    private void traiterSuppressionCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        

        String redirectTo = request.getParameter("redirectTo");

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("CommandeServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean success = commandeDAO.supprimerCommande(id, userId);

            if (success) {
                session.setAttribute("ToastAdmSuccesNotif", "Commande supprimée avec succès.");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Impossible de supprimer : La commande doit être ANNULEE ou LIVREE.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID de commande invalide.");
        }

        if (redirectTo != null && !redirectTo.trim().isEmpty()){
        	if("liste-com-passer-par-staff.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-commande-par-staff");
        	}
        	else if("liste-toutes-commandes.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-toutes-commande");
        	}
        } else {
            response.sendRedirect("CommandeServlet?action=lister");
        }
       
    }
    
    private void handleCommande(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== DEBUT handleCommande (Dashboard) ===");
        HttpSession session = request.getSession(false);
        
        try {
            // 1️⃣ Récupérer l'utilisateur connecté
            System.out.println("Étape 1: Vérification session utilisateur");
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                System.out.println("ERREUR: Utilisateur non connecté");
                session.setAttribute("ToastAdmErrorNotif", "Vous devez être connecté pour passer une commande");
                response.sendRedirect("login.jsp");
                return;
            }
            System.out.println("Utilisateur connecté ID: " + userId);
            
            // 2️⃣ Récupérer les paramètres du formulaire
            System.out.println("Étape 2: Récupération des paramètres");
            
            // Récupérer clientId (peut être différent de userId si commande par staff)
            String clientIdStr = request.getParameter("clientId");
            String clientName = request.getParameter("clientName");
          
            Integer clientId = null;
            if (clientIdStr != null && !clientIdStr.trim().isEmpty()) {
                try {
                    clientId = Integer.parseInt(clientIdStr);
                    System.out.println("clientId spécifié: " + clientId);
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: clientId invalide: " + clientIdStr);
                }
            }
            
            // Si pas de clientId spécifié, utiliser l'utilisateur connecté
            if (clientId == null || clientId <= 0) {
                clientId = userId;
                System.out.println("Utilisation userId comme clientId: " + clientId);
            }
            
            // Récupérer tableId si disponible
            Integer tableId = null;
            String tableIdStr = request.getParameter("tableId");
            if (tableIdStr != null && !tableIdStr.trim().isEmpty()) {
                try {
                    tableId = Integer.parseInt(tableIdStr);
                    System.out.println("tableId: " + tableId);
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: tableId invalide: " + tableIdStr);
                }
            }
            
            // Vérifier mode crédit
            boolean isCredit = "on".equals(request.getParameter("isCredit")) || 
                              "true".equals(request.getParameter("isCredit")) ||
                              "1".equals(request.getParameter("isCredit"));
            System.out.println("Mode crédit: " + isCredit);
            
            // 3️⃣ Récupérer les détails de commande (panier items)
            System.out.println("Étape 3: Récupération des détails du panier");
            PanierDAO panierDAO = new PanierDAO();
            List<Panier> panierItems = panierDAO.getPanierComplet(userId);
            
            if (panierItems == null || panierItems.isEmpty()) {
                System.out.println("ERREUR: Panier vide");
                session.setAttribute("ToastAdmErrorNotif", "Votre panier est vide");
                response.sendRedirect("MenuServlet?action=placer-commande");
                return;
            }
            
            System.out.println("Nombre d'articles dans le panier: " + panierItems.size());
            
            // 4️⃣ Convertir les items du panier en CommandeDetail
            System.out.println("Étape 4: Conversion panier -> CommandeDetail");
            List<CommandeDetail> details = new ArrayList<>();
            BigDecimal montantTotal = BigDecimal.ZERO;
            
            for (Panier panierItem : panierItems) {
                CommandeDetail detail = new CommandeDetail();
                
                // Informations de base du panier
                detail.setPanierId(panierItem.getId());
                detail.setQuantite(panierItem.getQuantite());
                
                // Déterminer le type et récupérer les informations appropriées
                if (panierItem.getPlatId() != null && panierItem.getPlatId() > 0) {
                    // C'est un plat
                    detail.setPlatId(panierItem.getPlatId());
                    
                    if (panierItem.getPlat() != null) {
                        Plat plat = panierItem.getPlat();
                        
                        // Vérifier si le plat a un produit associé
                        if (plat.getProduit() != null && plat.getProduit().getId() > 0) {
                            // C'est un plat avec produit associé, utiliser le prix du produit
                            Produit produitAssocie = plat.getProduit();
                            detail.setProduitId(produitAssocie.getId());
                            
                            if (produitAssocie.getPrixVente() != null) {
                                detail.setPrixUnitaire(produitAssocie.getPrixVente());
                            } else {
                                // Fallback au prix du plat
                                detail.setPrixUnitaire(BigDecimal.valueOf(plat.getPrix()));
                            }
                        } else {
                            // C'est un plat normal
                            detail.setPrixUnitaire(BigDecimal.valueOf(plat.getPrix()));
                        }
                    }
                    
                } else if (panierItem.getProduitId() != null && panierItem.getProduitId() > 0) {
                    // C'est un produit direct
                    detail.setProduitId(panierItem.getProduitId());
                    
                    if (panierItem.getProduit() != null && panierItem.getProduit().getPrixVente() != null) {
                        detail.setPrixUnitaire(panierItem.getProduit().getPrixVente());
                    }
                }
                
                // Si le prix unitaire n'a pas été défini, utiliser le prix calculé
                if (detail.getPrixUnitaire() == null || detail.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
                    if (panierItem.getTotal() != null && panierItem.getQuantite() > 0) {
                        // Calculer le prix unitaire à partir du total
                        BigDecimal prixUnitaire = panierItem.getTotal()
                            .divide(BigDecimal.valueOf(panierItem.getQuantite()), 2, RoundingMode.HALF_UP);
                        detail.setPrixUnitaire(prixUnitaire);
                    }
                }
                
                // Calculer le sous-total
                if (detail.getPrixUnitaire() != null && detail.getQuantite() > 0) {
                    BigDecimal sousTotal = detail.getPrixUnitaire()
                        .multiply(BigDecimal.valueOf(detail.getQuantite()));
                    detail.setSousTotal(sousTotal);
                    montantTotal = montantTotal.add(sousTotal);
                    
                    System.out.println("Détail ajouté - PanierID: " + detail.getPanierId() + 
                                     ", PlatID: " + detail.getPlatId() + 
                                     ", ProduitID: " + detail.getProduitId() + 
                                     ", Quantité: " + detail.getQuantite() + 
                                     ", Prix: " + detail.getPrixUnitaire() + 
                                     ", Sous-total: " + detail.getSousTotal());
                } else {
                    System.out.println("WARNING: Détail sans prix valide - PanierID: " + detail.getPanierId());
                }
                
                details.add(detail);
            }
            
            System.out.println("Montant total calculé: " + montantTotal);
            System.out.println("Nombre de détails: " + details.size());
            
            // 5️⃣ Validation des détails
            System.out.println("Étape 5: Validation des détails");
            for (int i = 0; i < details.size(); i++) {
                CommandeDetail d = details.get(i);
                if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
                    System.out.println("ERREUR validation détail " + i + 
                                     " - ProduitID: " + d.getProduitId() + 
                                     ", PlatID: " + d.getPlatId() + 
                                     ", Quantité: " + d.getQuantite());
                    session.setAttribute("ToastAdmErrorNotif", 
                        "Produit invalide à l'index " + i + ". Veuillez réessayer.");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
                if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("ERREUR prix détail " + i + " - Prix: " + d.getPrixUnitaire());
                    session.setAttribute("ToastAdmErrorNotif", 
                        "Prix invalide à l'index " + i + ". Veuillez réessayer.");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
            }
            
            // 6️⃣ Construire l'objet Commande
            System.out.println("Étape 6: Construction de la commande");
            Commande commande = new Commande();
            
            // Générer numéro de commande
            String numeroCommande = genererNumeroCommande();
            commande.setNumeroCommande(numeroCommande);
            System.out.println("Numéro commande généré: " + numeroCommande);
            
            commande.setDateCommande(new Timestamp(new Date().getTime()));
            commande.setMontantTotal(montantTotal);
            commande.setClientId(clientId);
            commande.setUtilisateurId(userId); // La personne qui passe la commande (staff)
            
            
            String notes = (clientName != null ? clientName : "Client non enregistrer");
            commande.setNotes(notes);
            
            // Définir les statuts selon le mode
            if (isCredit) {
                commande.setStatutCommande("EN_ATTENTE");
                commande.setStatutPaiement("NON_PAYE");
                commande.setModePaiement("CREDIT");
            } else {
                commande.setStatutCommande("EN_ATTENTE");
                commande.setStatutPaiement("NON_PAYE");
                commande.setModePaiement("CASH"); // Par défaut espèces
            }
            
            // Ajouter table si spécifiée
            if (tableId != null && tableId > 0) {
                TableRooftop table = new TableRooftop();
                table.setId(tableId);
                commande.setTableRooftop(table);
            }
            
            // 7️⃣ Traitement selon le mode (crédit ou normal)
            System.out.println("Étape 7: Traitement commande");
            
            if (isCredit) {
                System.out.println("Mode CRÉDIT activé");
                
                // Vérifier le plafond du client
                Utilisateur client = utilisateurDAO.findById(clientId);
                if (client == null) {
                    System.out.println("ERREUR: Client introuvable");
                    session.setAttribute("ToastAdmErrorNotif", "Client introuvable");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
                
                int plafond = client.getPlafond();
                System.out.println("Plafond client: " + plafond);
                
                // Total crédits en cours
                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
                int totalCredits = creditsEnCours.stream()
                    .mapToInt(c -> c.getCredit() != null 
                        ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye() 
                        : 0)
                    .sum();
                System.out.println("Total crédits en cours: " + totalCredits);
                
                int montantCommande = montantTotal.intValue();
                System.out.println("Montant commande: " + montantCommande);
                
                if ((totalCredits + montantCommande) > plafond) {
                    System.out.println("ERREUR: Plafond atteint");
                    session.setAttribute("ToastAdmErrorNotif", 
                        "Plafond de crédit atteint. Montant disponible: " + (plafond - totalCredits) + " HTG");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
                
                // Insérer la commande
                System.out.println("Insertion commande POS...");
                int newId = commandeDAO.ajouterCommandePOS(commande, details);
                System.out.println("Nouvelle commande ID: " + newId);
                
                if (newId <= 0) {
                    System.out.println("ERREUR insertion commande");
                    session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la commande");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
                
                // Ajouter le crédit
                System.out.println("Création objet crédit...");
                Credit credit = new Credit();
                credit.setUtilisateurId(clientId);
                credit.setCommandeId(newId);
                credit.setMontantTotal(montantCommande);
                credit.setMontantPaye(0);
                credit.setStatut("NON_PAYE");
                
                CreditDAO creditDAO = new CreditDAO();
                System.out.println("Insertion crédit...");
                int creditId = creditDAO.ajouterCredit(credit);
                System.out.println("Crédit ID: " + creditId);
                
                if (creditId <= 0) {
                    System.out.println("WARNING: Échec insertion crédit, mais commande créée");
                }
                
                // Notification pour commande crédit
                System.out.println("Création notification crédit...");
                Notification notif = new Notification();
                notif.setGeneratedBy("SYSTEM");
                notif.setToUser(clientId);
                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
                                 " d'un montant de " + montantCommande + " HTG");
                notif.setTypeNotif("CREDIT");
                notif.setStatus("VISIBLE");
                
                NotificationDAO notifDAO = new NotificationDAO();
                notifDAO.ajouterNotification(notif);
                System.out.println("Notification crédit envoyée");
                
            } else {
                System.out.println("Mode NORMAL (non-crédit)");
                
                // Insérer la commande
                System.out.println("Insertion commande POS...");
                int newId = commandeDAO.ajouterCommandePOS(commande, details);
                System.out.println("Nouvelle commande ID: " + newId);
                
                if (newId <= 0) {
                    System.out.println("ERREUR insertion commande");
                    session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la commande");
                    response.sendRedirect("MenuServlet?action=placer-commande");
                    return;
                }
                
                commande.setId(newId);
                
                // Notification pour commande normale
                System.out.println("Création notification commande...");
                Notification notif = new Notification();
                notif.setGeneratedBy("SYSTEM");
                notif.setToUser(clientId);
                notif.setMessages("Nouvelle commande #" + numeroCommande +
                                 " montant: " + montantTotal + " HTG");
                notif.setTypeNotif("COMMANDE");
                notif.setStatus("VISIBLE");
                
                NotificationDAO notifDAO = new NotificationDAO();
                notifDAO.ajouterNotification(notif);
                System.out.println("Notification commande envoyée");
            }
            
            // 8️⃣ Réservation de table si nécessaire
            if (tableId != null && tableId > 0) {
                System.out.println("Réservation table ID: " + tableId);
                try {
                    commandeDAO.reserverTable(tableId);
                } catch (Exception e) {
                    System.out.println("WARNING: Échec réservation table: " + e.getMessage());
                }
            }
            
            // 9️⃣ Nettoyage du panier
            System.out.println("Nettoyage panier...");
            int itemsSupprimes = 0;
            for (Panier panierItem : panierItems) {
                Integer panierId = panierItem.getId();
                String type = (panierItem.getPlatId() != null) ? "plat" : "produit";
                
                if (panierDAO.supprimerDuPanier(userId, panierId, type)) {
                    itemsSupprimes++;
                    System.out.println("Élément " + panierId + " supprimé du panier");
                } else {
                    System.out.println("WARNING: Échec suppression élément " + panierId + " du panier");
                }
            }
            System.out.println("Total éléments supprimés du panier: " + itemsSupprimes);
            
//            Lancer l'impression
           
            
            // 🔟 Réponse de succès
            System.out.println("=== FIN handleCommande (succès) ===");
            
            // Enregistrer les infos de commande en session pour affichage
            session.setAttribute("lastCommandeNumero", numeroCommande);
            session.setAttribute("lastCommandeMontant", montantTotal);
            session.setAttribute("lastCommandeClientName", clientName);
            
            // Rediriger vers une page de confirmation
            String successMessage = "Commande #" + numeroCommande + " passée avec succès! " +
                                  "Montant: " + montantTotal + " HTG";
            session.setAttribute("ToastAdmSuccesNotif", successMessage);
            
            // Utiliser la classe d'impression
//            ImpressionCommande impression = new ImpressionCommande();
//            impression.imprimerCommande(commande.getId(), response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": true, \"commandeId\": " + commande.getId() + ", \"numero\": \"" + numeroCommande + "\", \"clientName\": \"" + clientName + "\"}");
            out.flush();
            return;
            
            
            // Récupérer l'ID de la commande créée
			/*
			 * int commandeId = commandeDAO.getCommandeIdByNumero(numeroCommande); if
			 * (commandeId > 0) {
			 */
                // Rediriger vers les détails de la commande
//                String redirectUrl = "MenuServlet?action=placer-commande";
//                response.sendRedirect(redirectUrl);
            /*} else {
                // Rediriger vers la liste des commandes
                response.sendRedirect("CommandeServlet?action=lister");
            }
            */
        } catch (Exception e) {
            System.err.println("=== ERREUR GLOBALE handleCommande ===");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            String errorMsg = "Erreur technique lors de la commande: " + 
                             (e.getMessage() != null ? e.getMessage() : "Erreur inconnue");
            if (session != null) {
                session.setAttribute("ToastAdmErrorNotif", errorMsg);
            }
            response.sendRedirect("MenuServlet?action=placer-commande");
        }
    }
    
    
    

    private void traiterAjoutCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String whoIsOrdering = request.getParameter("whoIsOrdering");

        try {
            Commande commande = extraireCommandeDepuisRequest(request);
        	 //whoIsOrdering = commande.getWhoIsOrdering();
            
	         // Génération ici (remplace celui du formulaire)
	         String numeroUnique = genererNumeroCommande();
	         commande.setNumeroCommande(numeroUnique);
            List<CommandeDetail> details = extraireDetailsDepuisRequest(request);

            commande.setUtilisateurId(userId);

            int newId = commandeDAO.ajouterCommande(commande, details);
            if (newId > 0 && commande.isCredit()) {
                // Créer le crédit correspondant
                Credit credit = new Credit();
                credit.setUtilisateurId(commande.getClientId());
                credit.setCommandeId(newId);
                credit.setMontantTotal(commande.getMontantTotal().intValue()); // si MontantTotal est BigDecimal
                credit.setMontantPaye(BigDecimal.ZERO.intValue()); // convertit BigDecimal.ZERO en int
                credit.setStatut("NON_PAYE");

                CreditDAO creditDAO = new CreditDAO();
                int creditId = creditDAO.ajouterCredit(credit);
                if (creditId <= 0) {
                    // Gérer l'erreur si besoin
                    System.out.println("Erreur lors de la création du crédit pour la commande " + newId);
                }
            }

            if (newId > 0) {
                // Supprimer les lignes du panier correspondantes
                for (CommandeDetail detail : details) {
                    Integer panierId = detail.getPanierId();
                    System.out.println(panierId);
                    if (panierId != null) {
                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
                        PanierDAO pannierDAO = new PanierDAO();
						pannierDAO.supprimerDuPanier(userId, panierId, type);
                    }
                }
                if (whoIsOrdering != null) {

                    System.out.println(">>> POU ADM");
                    session.setAttribute("ToastAdmSuccesNotif", "Commande ajoutée avec succès.");
                    response.sendRedirect("CommandeServlet?action=placer-commande");
                    return;
                }

                System.out.println(">>> PA ANTRE");
                session.setAttribute("toastMessage", "Commande ajoutée avec succès!");
                session.setAttribute("toastType", "success");
                response.sendRedirect("CommandeServlet?action=categorie-parente");
                return;

            } else {
            	 if (whoIsOrdering != null) {
            	        session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la commande.");
            	        response.sendRedirect("CommandeServlet?action=placer-commande");
            	        return;
            	    }

            	    session.setAttribute("toastMessage", "Erreur lors de l'ajout de la commande.");
            	    session.setAttribute("toastType", "success");
            	    response.sendRedirect("MenuServlet?action=categorie-parente");
            	    return;
            }



        } catch (Exception e) {
        	
            e.printStackTrace();
            //session.setAttribute("ToastAdmErrorNotif", "Erreur technique : " + e.getMessage());
            if (whoIsOrdering != null) {
                response.sendRedirect("CommandeServlet?action=placer-commande");
                return;
            }
            response.sendRedirect("MenuServlet?action=categorie-parente");
            return;
        }
    }

    private void traiterModificationCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int log_user = (int) session.getAttribute("userId"); 

        try {
            Commande commande = extraireCommandeDepuisRequest(request);
            String idStr = request.getParameter("id");
            if (idStr == null) throw new IllegalArgumentException("ID commande manquant");
            commande.setId(Integer.parseInt(idStr));
            commande.setUtilisateurId(userId);

            List<CommandeDetail> details = extraireDetailsDepuisRequest(request);

            boolean updated = commandeDAO.modifierCommande(commande, details, log_user);

            if (updated) {
                session.setAttribute("ToastAdmSuccesNotif", "Commande modifiée avec succès.");
                response.sendRedirect("CommandeServlet?action=lister");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification de la commande.");
                response.sendRedirect("CommandeServlet?action=edit&id=" + commande.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur technique : " + e.getMessage());
            response.sendRedirect("CommandeServlet?action=lister");
        }
    }
    
    private void validerCommandes(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Récupérer les paramètres
    	String idStr = request.getParameter("idCommande");  // id commande (hidden input)
    	String numeroCommande = request.getParameter("numeroCommande");
    	String modePaiement = request.getParameter("modePaiement");
    	String montantPayeStr = request.getParameter("montantPaye");
        String redirectTo = request.getParameter("redirectTo");

    	System.out.println(">>> Entré dans validerCommandes()");
    	System.out.println("idCommande: " + idStr);
    	System.out.println("numeroCommande: " + numeroCommande);
    	System.out.println("modePaiement: " + modePaiement);
    	System.out.println("montantPaye: " + montantPayeStr);

    	HttpSession session = request.getSession(false);
    	if (session == null || session.getAttribute("userId") == null) {
    	    response.sendRedirect("login.jsp");
    	    return;
    	}
    	
    	int log_user = (int) session.getAttribute("userId"); 

    	if (idStr == null || numeroCommande == null || modePaiement == null || montantPayeStr == null) {
    	    session.setAttribute("ToastAdmErrorNotif", "Paramètres invalides pour valider la commande.");
    	    if (redirectTo != null && !redirectTo.trim().isEmpty()){
            	if("liste-com-passer-par-staff.jsp".equals(redirectTo)) {
                    response.sendRedirect("CommandeServlet?action=liste-commande-par-staff");
                    return;
            	}
            	else if("liste-toutes-commandes.jsp".equals(redirectTo)) {
                    response.sendRedirect("CommandeServlet?action=liste-toutes-commande");
                    return;
            	}
            } else {
                response.sendRedirect("CommandeServlet?action=lister");
                return;
            }
    	    
    	}

        try {
            int id = Integer.parseInt(idStr);
            // on parse montant sans le suffixe HTG si présent
            montantPayeStr = montantPayeStr.replace(" HTG", "").trim();
            BigDecimal montantPaye = new BigDecimal(montantPayeStr);

            boolean updated = commandeDAO.validerCommande(id, numeroCommande, modePaiement, montantPaye, log_user);

            if (updated) {
                session.setAttribute("ToastAdmSuccesNotif", "Commande validée avec succès.");
                session.setAttribute("ToastMessage", "Commande validée avec succès.");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la validation de la commande.");
                session.setAttribute("ToastMessage", "Erreur lors de la validation de la commande.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "Format invalide pour l'identifiant ou le montant.");
        }

        if (redirectTo != null && !redirectTo.trim().isEmpty()){
        	if("liste-com-passer-par-staff.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-commande-par-staff");
               
        	}
        	else if("liste-toutes-commandes.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-toutes-commande");
               
        	}
        } else {
            response.sendRedirect("CommandeServlet?action=lister");
         
        }
    }

    private void updateStatutCommandes(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println(">>> Entré dans System.out.println(\">>> Entré dans validerCommandes()\");()");
    	String idStr = request.getParameter("idCommande");  // id commande
        String nouveauStatut = request.getParameter("nouveauStatut");
        String redirectTo = request.getParameter("redirectTo");
        System.out.println("lalalla");
        System.out.println(">>> Paramètres reçus:");
        System.out.println("idCommande: " + request.getParameter("idCommande"));
        System.out.println("nouveauStatut: " + request.getParameter("nouveauStatut"));
        System.out.println("action: " + request.getParameter("action"));
        System.out.println("redirectTo: " + request.getParameter("redirectTo"));
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int log_user = (int) session.getAttribute("userId"); 
        if (idStr == null || nouveauStatut == null) {
            session.setAttribute("ToastAdmErrorNotif", "Paramètres invalides pdour la modification du statut.");
            if (redirectTo != null && !redirectTo.trim().isEmpty()){
            	if("liste-com-passer-par-staff.jsp".equals(redirectTo)) {
                    response.sendRedirect("CommandeServlet?action=liste-commande-par-staff");
            	}
            	else if("liste-toutes-commandes.jsp".equals(redirectTo)) {
                    response.sendRedirect("CommandeServlet?action=liste-toutes-commande");
            	}
            } else {
                response.sendRedirect("CommandeServlet?action=lister");
            }
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean updated = commandeDAO.modifierStatutCommande(id, nouveauStatut,log_user);

            if (updated) {
                session.setAttribute("ToastAdmSuccesNotif", "Statut de la commande modifié avec succès.");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification du statut.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "Identifiant de commande invalide.");
        }

        if (redirectTo != null && !redirectTo.trim().isEmpty()){
        	if("liste-com-passer-par-staff.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-commande-par-staff");
        	}
        	else if("liste-toutes-commandes.jsp".equals(redirectTo)) {
                response.sendRedirect("CommandeServlet?action=liste-toutes-commande");
        	}
        } else {
            response.sendRedirect("CommandeServlet?action=lister");
        }

    }


    private Commande extraireCommandeDepuisRequest(HttpServletRequest request) {
        Commande commande = new Commande();

        commande.setNumeroCommande(request.getParameter("numeroCommande"));
        commande.setWhoIsOrdering(request.getParameter("whoIsOrdering")); // 🔧 Correction de la faute

        HttpSession session = request.getSession(false);
        Integer sessionUserId = (session != null) ? (Integer) session.getAttribute("userId") : null;

        // Règle spéciale pour les commandes "byStaff"
        if ("byStaff".equals(commande.getWhoIsOrdering())) {
            try {
                // client sélectionné depuis le formulaire
                commande.setClientId(Integer.parseInt(request.getParameter("clientId")));
            } catch (NumberFormatException e) {
                commande.setClientId(0); // Par sécurité
            }
            // utilisateur connecté (staff)
            if (sessionUserId != null) {
                commande.setUtilisateurId(sessionUserId);
            } else {
                commande.setUtilisateurId(0); // fallback
            }
        } else {
            // Commande normale
            if (sessionUserId != null) {
                commande.setClientId(sessionUserId);
            }
            commande.setUtilisateurId(0);
        }

        commande.setNotes(request.getParameter("note"));

        String dateStr = request.getParameter("dateCommande");
        try {
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                commande.setDateCommande(Timestamp.valueOf(dateStr));
            } else {
                commande.setDateCommande(new Timestamp(new Date().getTime()));
            }
        } catch (IllegalArgumentException e) {
            commande.setDateCommande(new Timestamp(new Date().getTime()));
        }

        commande.setStatutCommande(request.getParameter("statutCommande"));

        try {
            String montantTotalStr = request.getParameter("montantTotal");
            if (montantTotalStr != null && !montantTotalStr.isEmpty()) {
                commande.setMontantTotal(new BigDecimal(montantTotalStr));
            }
        } catch (NumberFormatException ignored) {}

        String estCreditStr = request.getParameter("estCredit"); // "yes" ou "no"
    	System.out.println("champ credit " + estCreditStr);
        boolean estCredit = "yes".equalsIgnoreCase(estCreditStr);
        commande.setIsCredit(estCredit);

        if (estCredit) {
            commande.setModePaiement("NON_PAYE");
            commande.setStatutPaiement("NON_PAYE");
        } else {
            commande.setModePaiement(request.getParameter("modePaiement"));
            commande.setStatutPaiement(request.getParameter("statutPaiement"));
        }



        try {
            String montantPayeStr = request.getParameter("montantPaye");
            if (montantPayeStr != null && !montantPayeStr.isEmpty()) {
                commande.setMontantPaye(new BigDecimal(montantPayeStr));
            }
        } catch (NumberFormatException ignored) {}

        return commande;
    }


    private List<CommandeDetail> extraireDetailsDepuisRequest(HttpServletRequest request) {
        List<CommandeDetail> details = new ArrayList<>();

        // Les noms des paramètres envoyés par ton formulaire
        String[] produitIds = request.getParameterValues("produitId[]");
        String[] platIds = request.getParameterValues("platId[]");
        String[] quantites = request.getParameterValues("quantite[]");
        String[] prixUnitaires = request.getParameterValues("prixUnitaire[]");
        String[] sousTotals = request.getParameterValues("sousTotal[]");
        String[] notesDetails = request.getParameterValues("notesDetail[]");
        String[] panierIds = request.getParameterValues("pannierId[]"); // <-- AJOUTÉ

        if (produitIds == null) return details;

        for (int i = 0; i < produitIds.length; i++) {
            CommandeDetail detail = new CommandeDetail();

            try {
                if (produitIds[i] != null && !produitIds[i].isEmpty()) {
                    detail.setProduitId(Integer.parseInt(produitIds[i]));
                }
            } catch (NumberFormatException ignored) {}

            try {
                if (platIds != null && i < platIds.length && platIds[i] != null && !platIds[i].isEmpty()) {
                    detail.setPlatId(Integer.parseInt(platIds[i]));
                }
            } catch (NumberFormatException ignored) {}

            try {
                if (quantites != null && i < quantites.length && quantites[i] != null && !quantites[i].isEmpty()) {
                    detail.setQuantite(Integer.parseInt(quantites[i]));
                }
            } catch (NumberFormatException ignored) {}

            try {
                if (prixUnitaires != null && i < prixUnitaires.length && prixUnitaires[i] != null && !prixUnitaires[i].isEmpty()) {
                    detail.setPrixUnitaire(new BigDecimal(prixUnitaires[i]));
                }
            } catch (NumberFormatException ignored) {}

            try {
                if (sousTotals != null && i < sousTotals.length && sousTotals[i] != null && !sousTotals[i].isEmpty()) {
                    detail.setSousTotal(new BigDecimal(sousTotals[i]));
                }
            } catch (NumberFormatException ignored) {}

            if (notesDetails != null && i < notesDetails.length) {
                detail.setNotes(notesDetails[i]);
            }

            // Récupérer le panierId (AJOUT)
            try {
                if (panierIds != null && i < panierIds.length && panierIds[i] != null && !panierIds[i].isEmpty()) {
                	System.out.println(panierIds[i]);
                    detail.setPanierId(Integer.parseInt(panierIds[i]));
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur de conversion panierId à l’index " + i + " : " + panierIds[i]);
            }

            details.add(detail);
        }

        return details;
    }
    
    // Ajoutez cette méthode dans CommandeServlet:
    private void imprimerCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            session.setAttribute("ToastAdmErrorNotif", "ID de commande manquant.");
            response.sendRedirect("CommandeServlet?action=lister");
            return;
        }
        
        try {
            int commandeId = Integer.parseInt(idStr);
            
            // Vérifier que l'utilisateur a le droit de voir cette commande
            Commande commande = commandeDAO.getCommandeById(commandeId);
            if (commande == null) {
                session.setAttribute("ToastAdmErrorNotif", "Commande non trouvée.");
                response.sendRedirect("CommandeServlet?action=lister");
                return;
            }
            
            // Vérification des permissions (exemple simple)
            if (!userId.equals(commande.getUtilisateurId()) && 
                !userId.equals(commande.getClientId())) {
                // Optionnel: vérifier si l'utilisateur est admin/caissier
                UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                Utilisateur user = utilisateurDAO.findById(userId);
                if (user == null || !user.getRole().getRoleName().contains("ADMIN")) {
                    session.setAttribute("ToastAdmErrorNotif", "Vous n'avez pas l'autorisation d'imprimer cette commande.");
                    response.sendRedirect("CommandeServlet?action=lister");
                    return;
                }
            }
            
            // Utiliser la classe d'impression
            ImpressionCommande impression = new ImpressionCommande();
            impression.imprimerCommande(commandeId, response);
            
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID de commande invalide.");
            response.sendRedirect("CommandeServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'impression: " + e.getMessage());
            response.sendRedirect("CommandeServlet?action=lister");
        }
    }
    
    private void handleProforma(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== DEBUT handleProforma ===");
        HttpSession session = request.getSession(false);
        
        try {
            // 1. Vérifier l'utilisateur connecté
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                session.setAttribute("ToastAdmErrorNotif", "Vous devez être connecté");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // MODIFICATION : Récupérer le NOM du client
            String clientName = request.getParameter("clientName");
            if (clientName == null || clientName.trim().isEmpty()) {
                session.setAttribute("ToastAdmErrorNotif", "Nom du client non spécifié");
                response.sendRedirect("MenuServlet?action=placer-commande");
                return;
            }
            
            System.out.println("Nom du client pour proforma: " + clientName);
            
            // 3. Récupérer les items du panier
            PanierDAO panierDAO = new PanierDAO();
            List<Panier> panierItems = panierDAO.getPanierComplet(userId);
            
            if (panierItems == null || panierItems.isEmpty()) {
                session.setAttribute("ToastAdmErrorNotif", "Votre panier est vide");
                response.sendRedirect("MenuServlet?action=placer-commande");
                return;
            }
            
            // 4. Récupérer les informations de l'utilisateur
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            Utilisateur utilisateur = utilisateurDAO.findById(userId);
            
            // 5. Générer le proforma
            ImpressionCommande impression = new ImpressionCommande();
            
            // MODIFICATION : Créer un objet Utilisateur temporaire avec le nom du client
            Utilisateur clientTemp = new Utilisateur();
            clientTemp.setNom(clientName);
            clientTemp.setPrenom(""); // Laissez vide ou mettez quelque chose comme "Client"
            
            // Déterminer le format d'impression
            String format = request.getParameter("format");
            if ("simple".equals(format)) {
                impression.imprimerProformaSimple(clientTemp, utilisateur, panierItems, response);
            } else {
                impression.imprimerProforma(clientTemp, utilisateur, panierItems, response);
            }
            
            System.out.println("=== FIN handleProforma (succès) ===");
            
            // Nettoyer le panier après impression
            System.out.println("Nettoyage panier...");
            int itemsSupprimes = 0;
            for (Panier panierItem : panierItems) {
                Integer panierId = panierItem.getId();
                String type = (panierItem.getPlatId() != null) ? "plat" : "produit";
                
                if (panierDAO.supprimerDuPanier(userId, panierId, type)) {
                    itemsSupprimes++;
                    System.out.println("Élément " + panierId + " supprimé du panier");
                } else {
                    System.out.println("WARNING: Échec suppression élément " + panierId + " du panier");
                }
            }
            System.out.println("Total éléments supprimés du panier: " + itemsSupprimes);
            
        } catch (Exception e) {
            System.err.println("=== ERREUR handleProforma ===");
            e.printStackTrace();
            System.out.println("ERREUR : " + e.getMessage());
            if (session != null) {
                session.setAttribute("ToastAdmErrorNotif", 
                    "Erreur lors de la génération du proforma: " + e.getMessage());
            }
            response.sendRedirect("MenuServlet?action=placer-commande");
        }
    }
}
