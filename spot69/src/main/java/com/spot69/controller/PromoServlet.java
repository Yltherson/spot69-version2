
package com.spot69.controller;

import com.spot69.dao.PromoDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Promo;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 50, // 50MB pour les vidéos
    maxRequestSize = 1024 * 1024 * 100 // 100MB
)
@WebServlet({"/PromoServlet", "/blok/PromoServlet"})
public class PromoServlet extends HttpServlet {

    private PromoDAO promoDAO;
    private static String UPLOAD_PROMO_IMAGES_DIR = "";
    private static String UPLOAD_PROMO_VIDEOS_DIR = "";
    
    // Patterns de date pour le parsing
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DATABASE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init() throws ServletException {
        // Initialisation des répertoires d'upload
        ServletContext context = getServletContext();
        
        // Utiliser les paramètres d'initialisation du contexte comme ProduitServlet
//        String uploadRoot = context.getInitParameter("UPLOAD_ROOT_DIR");
//        if (uploadRoot == null || uploadRoot.isEmpty()) {
            // Fallback au répertoire home si pas configuré
//        String  uploadRoot = System.getProperty("user.home") + File.separator + "uploads";
        String uploadRoot = context.getInitParameter("UPLOAD_ROOT_DIR");
//        }
        
        UPLOAD_PROMO_IMAGES_DIR = uploadRoot + File.separator + "promos" + File.separator + "images";
        UPLOAD_PROMO_VIDEOS_DIR = uploadRoot + File.separator + "promos" + File.separator + "videos";
        
        
        promoDAO = new PromoDAO();
        
        // Créer les répertoires s'ils n'existent pas
//        new File(UPLOAD_PROMO_IMAGES_DIR).mkdirs();
//        new File(UPLOAD_PROMO_VIDEOS_DIR).mkdirs();
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
            response.sendRedirect("promo.jsp");
            return;
        }

        switch (action) {
            case "ajouter":
                handleAjouter(request, response);
                break;
            case "modifier":
                handleModifier(request, response);
                break;
            case "supprimer":
            	System.out.println("handlesupprimer promo");
                handleSupprimer(request, response);
                break;
            case "changerStatut":
                handleChangerStatut(request, response);
                break;
            case "reorder":
                handleReorder(request, response);
                break;
            case "uploadMedia":
                handleUploadMedia(request, response);
                break;
            default:
                response.sendRedirect("promo.jsp");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            // Par défaut, lister les promos
            handleLister(request, response);
            return;
        }

        switch (action) {
            case "add":
                handleAddPage(request, response);
                break;
            case "edit":
                handleEditPage(request, response);
                break;
            case "lister":
                handleLister(request, response);
                break;
            case "preview":
                handlePreview(request, response);
                break;
            case "carrousel":
                handleCarrousel(request, response);
                break;
            case "getPromo":
                handleGetPromo(request, response);
                break;
            case "getMediaUrl":
                handleGetMediaUrl(request, response);
                break;
            default:
                handleLister(request, response);
                break;
        }
    }

    // ======= Handlers POST =======

    private void handleAjouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Promo promo = extrairePromoDepuisRequest(request);
            
            // Gestion du média (image ou vidéo) - similaire à ProduitServlet
            String typeContenu = promo.getTypeContenu();
            String mediaUrl = null;
            
            if ("video".equalsIgnoreCase(typeContenu)) {
                Part videoPart = request.getPart("videoFile");
                if (videoPart != null && videoPart.getSize() > 0) {
                    mediaUrl = sauvegarderFichier(request, videoPart, "promos/videos");
                } else {
                    // Vérifier si une URL est fournie dans un champ caché
                    String videoUrl = request.getParameter("videoUrl");
                    if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                        mediaUrl = videoUrl.trim();
                    }
                }
            } else {
                Part imagePart = request.getPart("imageFile");
                if (imagePart != null && imagePart.getSize() > 0) {
                    mediaUrl = sauvegarderFichier(request, imagePart, "promos/images");
                } else {
                    // Vérifier si une URL est fournie dans un champ caché
                    String imageUrl = request.getParameter("imageUrl");
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        mediaUrl = imageUrl.trim();
                    }
                }
            }
            
            // Si aucun média n'a été uploadé ou spécifié, utiliser une valeur par défaut
            if (mediaUrl == null) {
                if ("video".equalsIgnoreCase(typeContenu)) {
                    mediaUrl = "uploads/videos/default.mp4";
                } else {
                    mediaUrl = "uploads/promos/default.png";
                }
            }
            
            promo.setCheminMedia(mediaUrl);
            
            // Définir l'utilisateur courant
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                promo.setUtilisateurId((int) session.getAttribute("userId"));
            } else {
                // Fallback: premier admin trouvé
                UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                Utilisateur admin = utilisateurDAO.findByRole("ADMIN").stream().findFirst().orElse(null);
                if (admin != null) {
                    promo.setUtilisateurId(admin.getId());
                } else {
                    promo.setUtilisateurId(1); // ID par défaut
                }
            }
            
            // Si l'ordre n'est pas défini, utiliser le max + 1
            if (promo.getOrdreAffichage() == 0) {
                List<Promo> toutesPromos = promoDAO.listerToutesPromos();
                int maxOrdre = toutesPromos.stream()
                        .mapToInt(Promo::getOrdreAffichage)
                        .max()
                        .orElse(0);
                promo.setOrdreAffichage(maxOrdre + 1);
            }
            
            int newId = promoDAO.ajouterPromo(promo);
            if (newId > 0) {
                setSuccessNotif(request, "Promotion ajoutée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de l'ajout de la promotion.");
            }
            response.sendRedirect("PromoServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PromoServlet?action=add");
        }
    }

    private void handleModifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Promo promoExistante = promoDAO.chercherParId(id);

            if (promoExistante == null) {
                setErrorNotif(request, "Promotion introuvable.");
                response.sendRedirect("PromoServlet?action=lister");
                return;
            }

            Promo promoModifiee = extrairePromoDepuisRequest(request);
            promoModifiee.setId(id);
            
            // Conserver l'ancien média par défaut
            String ancienMedia = promoExistante.getCheminMedia();
            promoModifiee.setCheminMedia(ancienMedia);
            
            // Gestion du nouveau média (image ou vidéo) - similaire à ProduitServlet
            String typeContenu = promoModifiee.getTypeContenu();
            boolean mediaChanged = false;
            
            if ("video".equalsIgnoreCase(typeContenu)) {
                Part videoPart = request.getPart("videoFile");
                if (videoPart != null && videoPart.getSize() > 0) {
                    String nouveauMedia = sauvegarderFichier(request, videoPart, "promos/videos");
                    promoModifiee.setCheminMedia(nouveauMedia);
                    mediaChanged = true;
                } else {
                    // Vérifier si une nouvelle URL est fournie
                    String videoUrl = request.getParameter("videoUrl");
                    if (videoUrl != null && !videoUrl.trim().isEmpty() && !videoUrl.equals(ancienMedia)) {
                        promoModifiee.setCheminMedia(videoUrl.trim());
                        mediaChanged = true;
                    }
                }
            } else {
                Part imagePart = request.getPart("imageFile");
                if (imagePart != null && imagePart.getSize() > 0) {
                    String nouveauMedia = sauvegarderFichier(request, imagePart, "promos/images");
                    promoModifiee.setCheminMedia(nouveauMedia);
                    mediaChanged = true;
                } else {
                    // Vérifier si une nouvelle URL est fournie
                    String imageUrl = request.getParameter("imageUrl");
                    if (imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals(ancienMedia)) {
                        promoModifiee.setCheminMedia(imageUrl.trim());
                        mediaChanged = true;
                    }
                }
            }
            
            // Mettre à jour la date de modification
            promoModifiee.setUpdateDate(LocalDateTime.now());
            
            boolean success = promoDAO.modifierPromo(promoModifiee);
            if (success) {
                setSuccessNotif(request, "Promotion modifiée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la modification de la promotion.");
            }
            response.sendRedirect("PromoServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PromoServlet?action=lister");
        }
    }

    private void handleSupprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	System.out.println("methode handlesupprimer promo");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            HttpSession session = request.getSession(false);
            Integer deletedBy = session != null && session.getAttribute("userId") != null
                    ? (Integer) session.getAttribute("userId")
                    : 0;
            System.out.println("appel de supprimer promo");
            boolean success = promoDAO.supprimerPromo(id);
            System.out.println("promoDAO.supprimerPromo(id)" + id);
            if (success) {
                setSuccessNotif(request, "Promotion supprimée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la suppression de la promotion.");
            }
            response.sendRedirect("PromoServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void handleChangerStatut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nouveauStatut = request.getParameter("statut");
            
            Promo promo = promoDAO.chercherParId(id);
            if (promo == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Promotion introuvable\"}");
                return;
            }
            
            promo.setStatut(nouveauStatut);
            promo.setUpdateDate(LocalDateTime.now());
            
            boolean success = promoDAO.modifierPromo(promo);
            
            response.setContentType("application/json");
            if (success) {
                response.getWriter().write("{\"success\": true, \"message\": \"Statut mis à jour\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors de la mise à jour\"}");
            }
        } catch (Exception e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur technique\"}");
        }
    }

    private void handleReorder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Récupérer les données JSON du corps de la requête
            String jsonData = request.getReader().readLine();
            
            // Parser le JSON simple (pour un vrai projet, utiliser Gson ou Jackson)
            // Ici, on suppose que les données viennent d'un formulaire
            String[] ids = request.getParameterValues("ids[]");
            String[] orders = request.getParameterValues("orders[]");
            
            boolean allSuccess = true;
            if (ids != null && orders != null && ids.length == orders.length) {
                for (int i = 0; i < ids.length; i++) {
                    try {
                        int id = Integer.parseInt(ids[i]);
                        int order = Integer.parseInt(orders[i]);
                        boolean success = promoDAO.mettreAJourOrdreAffichage(id, order);
                        if (!success) allSuccess = false;
                    } catch (NumberFormatException e) {
                        allSuccess = false;
                    }
                }
            }
            
            response.setContentType("application/json");
            if (allSuccess) {
                response.getWriter().write("{\"success\": true, \"message\": \"Ordre mis à jour\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors de la mise à jour de l'ordre\"}");
            }
        } catch (Exception e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur technique\"}");
        }
    }

    private void handleUploadMedia(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String type = request.getParameter("type"); // "image" ou "video"
            if (type == null) {
                type = "image"; // Par défaut
            }
            
            Part filePart = request.getPart("mediaFile");
            if (filePart == null || filePart.getSize() == 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Aucun fichier sélectionné\"}");
                return;
            }
            
            String dossier = type.equalsIgnoreCase("video") ? "promos/videos" : "promos/images";
            String mediaUrl = sauvegarderFichier(request, filePart, dossier);
            
            if (mediaUrl != null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"mediaUrl\": \"" + mediaUrl + "\"}");
            } else {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors de l'upload\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur technique: " + e.getMessage() + "\"}");
        }
    }

    // ======= Handlers GET =======

    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Préparer les données pour la page d'ajout
        request.setAttribute("action", "add");
        request.setAttribute("pageTitle", "Ajouter une promotion");
        
        // Liste des routes disponibles pour le dropdown
        Map<String, String> routes = new LinkedHashMap<>();
        routes.put("/gallery", "Galerie");
        routes.put("/menu", "Menu");
        routes.put("/reservation", "Réservation");
        routes.put("/hotel", "Hôtel");
        routes.put("/promotions", "Promotions");
        routes.put("/contact", "Contact");
        routes.put("/", "Accueil");
        request.setAttribute("routes", routes);
        
        request.getRequestDispatcher("promo-form.jsp").forward(request, response);
    }

    private void handleEditPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Promo promo = promoDAO.chercherParId(id);
                if (promo != null) {
                    request.setAttribute("promo", promo);
                    request.setAttribute("action", "edit");
                    request.setAttribute("pageTitle", "Modifier la promotion");
                    
                    // Liste des routes
                    Map<String, String> routes = new LinkedHashMap<>();
                    routes.put("/gallery", "Galerie");
                    routes.put("/menu", "Menu");
                    routes.put("/reservation", "Réservation");
                    routes.put("/hotel", "Hôtel");
                    routes.put("/promotions", "Promotions");
                    routes.put("/contact", "Contact");
                    routes.put("/", "Accueil");
                    request.setAttribute("routes", routes);
                    
                    // Formater les dates pour l'affichage dans le formulaire
                    if (promo.getDateDebut() != null) {
                        String dateDebutInput = promo.getDateDebut().format(INPUT_FORMATTER);
                        request.setAttribute("dateDebutInput", dateDebutInput);
                    }
                    if (promo.getDateFin() != null) {
                        String dateFinInput = promo.getDateFin().format(INPUT_FORMATTER);
                        request.setAttribute("dateFinInput", dateFinInput);
                    }
                    
                    // Ajouter l'URL complète du média pour l'affichage
                    String mediaUrl = getCompleteMediaUrl(request, promo);
                    request.setAttribute("completeMediaUrl", mediaUrl);
                    
                    request.getRequestDispatcher("promo-form.jsp").forward(request, response);
                } else {
                    setErrorNotif(request, "Promotion introuvable");
                    response.sendRedirect("PromoServlet?action=lister");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("PromoServlet?action=lister");
        }
    }

    private void handleLister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les filtres
        String statut = request.getParameter("statut");
        String type = request.getParameter("type");
        String search = request.getParameter("search");
        
        List<Promo> promos = promoDAO.listerToutesPromos();
        
        // Appliquer les filtres
        if (statut != null && !statut.isEmpty()) {
            promos.removeIf(p -> !statut.equalsIgnoreCase(p.getStatut()));
        }
        
        if (type != null && !type.isEmpty()) {
            promos.removeIf(p -> !type.equalsIgnoreCase(p.getTypeContenu()));
        }
        
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            promos.removeIf(p -> 
                !p.getTitre().toLowerCase().contains(searchLower) &&
                !(p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower)) &&
                !(p.getSousTitre() != null && p.getSousTitre().toLowerCase().contains(searchLower))
            );
        }
        
        // Ajouter les URLs complètes des médias pour l'affichage
        for (Promo promo : promos) {
            String completeMediaUrl = getCompleteMediaUrl(request, promo);
            // On peut stocker dans un attribut temporaire si nécessaire
            request.setAttribute("completeMediaUrl_" + promo.getId(), completeMediaUrl);
        }
        
        // Calculer les statistiques
        int totalPromosActives = (int) promos.stream()
                .filter(p -> "actif".equals(p.getStatut()))
                .count();
        
        int totalPromosVideo = (int) promos.stream()
                .filter(p -> "video".equalsIgnoreCase(p.getTypeContenu()))
                .count();
        
        int totalPromosImage = (int) promos.stream()
                .filter(p -> "image".equalsIgnoreCase(p.getTypeContenu()))
                .count();
        
        int totalPromosProgrammees = (int) promos.stream()
                .filter(p -> p.getDateDebut() != null && p.getDateFin() != null)
                .count();
        
        // Set les attributs
        request.setAttribute("promos", promos);
        request.setAttribute("totalPromosActives", totalPromosActives);
        request.setAttribute("totalPromosVideo", totalPromosVideo);
        request.setAttribute("totalPromosImage", totalPromosImage);
        request.setAttribute("totalPromosProgrammees", totalPromosProgrammees);
        
        // Transmettre les filtres pour pré-remplir le formulaire
        request.setAttribute("filterStatut", statut);
        request.setAttribute("filterType", type);
        request.setAttribute("filterSearch", search);
        
        request.getRequestDispatcher("promo.jsp").forward(request, response);
    }

    private void handlePreview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Promo promo = promoDAO.chercherParId(id);
                if (promo != null) {
                    // Préparer les données pour l'aperçu
                    request.setAttribute("promo", promo);
                    
                    // Générer l'HTML pour l'aperçu
                    StringBuilder html = new StringBuilder();
                    html.append("<div class=\"promo-preview\" style=\"max-width: 800px; margin: 0 auto;\">");
                    html.append("<h4 class=\"mb-4\">").append(promo.getTitre()).append("</h4>");
                    
                    String completeMediaUrl = getCompleteMediaUrl(request, promo);
                    
                    if ("video".equalsIgnoreCase(promo.getTypeContenu())) {
                        html.append("<div class=\"mb-3\">");
                        html.append("<video width=\"100%\" controls>");
                        html.append("<source src=\"").append(completeMediaUrl).append("\" type=\"video/mp4\">");
                        html.append("Votre navigateur ne supporte pas la lecture de vidéos.");
                        html.append("</video>");
                        if (promo.getDureeVideo() != null) {
                            html.append("<div class=\"text-muted mt-1\">Durée: ").append(promo.getDureeVideo()).append("</div>");
                        }
                        html.append("</div>");
                    } else {
                        html.append("<div class=\"mb-3\">");
                        html.append("<img src=\"").append(completeMediaUrl).append("\" class=\"img-fluid\" style=\"max-height: 400px; object-fit: cover;\">");
                        html.append("</div>");
                    }
                    
                    html.append("<div class=\"card\">");
                    html.append("<div class=\"card-body\">");
                    if (promo.getSousTitre() != null) {
                        html.append("<h5 class=\"card-subtitle mb-2 text-muted\">").append(promo.getSousTitre()).append("</h5>");
                    }
                    html.append("<p class=\"card-text\">").append(promo.getDescription()).append("</p>");
                    
                    // Afficher le gradient
                    if (promo.getCouleursGradient() != null && !promo.getCouleursGradient().isEmpty()) {
                        html.append("<div class=\"mb-3\">");
                        html.append("<small class=\"text-muted\">Gradient:</small>");
                        html.append("<div style=\"height: 30px; background: linear-gradient(135deg, ");
                        for (int i = 0; i < promo.getCouleursGradient().size(); i++) {
                            if (i > 0) html.append(", ");
                            html.append(promo.getCouleursGradient().get(i));
                        }
                        html.append("); border-radius: 4px;\"></div>");
                        html.append("</div>");
                    }
                    
                    html.append("<div class=\"mt-3\">");
                    html.append("<strong>Route:</strong> ").append(promo.getRouteCible()).append("<br>");
                    html.append("<strong>Bouton:</strong> ").append(promo.getTexteBouton()).append("<br>");
                    html.append("<strong>Vues:</strong> ").append(promo.getVues() != null ? promo.getVues() : "0 vues").append("<br>");
                    html.append("<strong>Ordre:</strong> ").append(promo.getOrdreAffichage()).append("<br>");
                    html.append("<strong>Statut:</strong> <span class=\"badge ").append(getBadgeClassForStatut(promo.getStatut())).append("\">").append(getStatutText(promo.getStatut())).append("</span>");
                    html.append("</div>");
                    
                    // Dates
                    if (promo.getDateDebut() != null || promo.getDateFin() != null) {
                        html.append("<div class=\"mt-3\">");
                        html.append("<strong>Dates:</strong><br>");
                        if (promo.getDateDebut() != null) {
                            html.append("Début: ").append(promo.getDateDebut().format(DISPLAY_FORMATTER)).append("<br>");
                        }
                        if (promo.getDateFin() != null) {
                            html.append("Fin: ").append(promo.getDateFin().format(DISPLAY_FORMATTER));
                        }
                        html.append("</div>");
                    }
                    
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    
                    response.setContentType("text/html");
                    response.getWriter().write(html.toString());
                } else {
                    response.getWriter().write("<div class='alert alert-danger'>Promotion introuvable</div>");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("<div class='alert alert-danger'>ID invalide</div>");
            }
        } else {
            response.getWriter().write("<div class='alert alert-danger'>ID manquant</div>");
        }
    }

    private void handleCarrousel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les promos pour le carrousel (actives et dans la période)
        List<Promo> promos = promoDAO.listerPromosPourCarrousel();
        
        // Convertir en JSON pour le frontend
        response.setContentType("application/json");
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < promos.size(); i++) {
            Promo p = promos.get(i);
            json.append("{");
            json.append("\"id\": \"").append(p.getId()).append("\",");
            json.append("\"type\": \"").append(p.getTypeContenu()).append("\",");
            json.append("\"title\": \"").append(escapeJson(p.getTitre())).append("\",");
            json.append("\"subtitle\": \"").append(p.getSousTitre() != null ? escapeJson(p.getSousTitre()) : "").append("\",");
            json.append("\"description\": \"").append(p.getDescription() != null ? escapeJson(p.getDescription()) : "").append("\",");
            
            // URL complète du média
            String completeMediaUrl = getCompleteMediaUrl(request, p);
            json.append("\"source\": \"").append(completeMediaUrl).append("\",");
            
            // Gradient
            json.append("\"gradient\": [");
            if (p.getCouleursGradient() != null) {
                for (int j = 0; j < p.getCouleursGradient().size(); j++) {
                    if (j > 0) json.append(",");
                    json.append("\"").append(p.getCouleursGradient().get(j)).append("\"");
                }
            } else {
                json.append("\"#8B5CF6\",\"#EC4899\",\"#8B5CF6\"");
            }
            json.append("],");
            
            json.append("\"buttonText\": \"").append(p.getTexteBouton() != null ? escapeJson(p.getTexteBouton()) : "Explorer").append("\",");
            json.append("\"route\": \"").append(p.getRouteCible() != null ? escapeJson(p.getRouteCible()) : "/").append("\",");
            
            if ("video".equalsIgnoreCase(p.getTypeContenu())) {
                json.append("\"duration\": \"").append(p.getDureeVideo() != null ? escapeJson(p.getDureeVideo()) : "0:30").append("\",");
            }
            
            json.append("\"plays\": \"").append(p.getVues() != null ? escapeJson(p.getVues()) : "0 vues").append("\"");
            json.append("}");
            
            if (i < promos.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        response.getWriter().write(json.toString());
    }

    private void handleGetPromo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Promo promo = promoDAO.chercherParId(id);
                if (promo != null) {
                    // Ajouter l'URL complète du média
                    String completeMediaUrl = getCompleteMediaUrl(request, promo);
                    request.setAttribute("completeMediaUrl", completeMediaUrl);
                    
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true, \"promo\": " + toJson(promo) + ", \"completeMediaUrl\": \"" + completeMediaUrl + "\"}");
                } else {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"message\": \"Promotion introuvable\"}");
                }
            } catch (NumberFormatException e) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"ID invalide\"}");
            }
        } else {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"ID manquant\"}");
        }
    }

    private void handleGetMediaUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mediaPath = request.getParameter("mediaPath");
        String type = request.getParameter("type"); // "image" ou "video"
        
        if (mediaPath == null || mediaPath.isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Chemin média manquant\"}");
            return;
        }
        
        if (type == null) {
            // Deviner le type d'après l'extension
            if (mediaPath.toLowerCase().endsWith(".mp4") || mediaPath.toLowerCase().endsWith(".avi") || 
                mediaPath.toLowerCase().endsWith(".mov") || mediaPath.toLowerCase().endsWith(".wmv")) {
                type = "video";
            } else {
                type = "image";
            }
        }
        
        String completeUrl = getCompleteMediaUrl(request.getContextPath(), mediaPath, type);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true, \"completeUrl\": \"" + completeUrl + "\"}");
    }

    // ======= Méthodes utilitaires =======

    private Promo extrairePromoDepuisRequest(HttpServletRequest request) {
        Promo promo = new Promo();

        // Titre principal
        String titre = request.getParameter("titre");
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'titre' est requis.");
        }
        promo.setTitre(titre.trim());

        // Sous-titre
        String sousTitre = request.getParameter("sousTitre");
        promo.setSousTitre(sousTitre != null ? sousTitre.trim() : null);

        // Description
        String description = request.getParameter("description");
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'description' est requis.");
        }
        promo.setDescription(description.trim());

        // Type de contenu
        String typeContenu = request.getParameter("typeContenu");
        if (typeContenu == null || (!"video".equals(typeContenu) && !"image".equals(typeContenu))) {
            typeContenu = "image"; // Valeur par défaut
        }
        promo.setTypeContenu(typeContenu);

        // Couleurs de gradient
        String couleursGradient = request.getParameter("couleursGradient");
        if (couleursGradient != null && !couleursGradient.trim().isEmpty()) {
            promo.setCouleursGradientFromString(couleursGradient.trim());
        }

        // Texte du bouton
        String texteBouton = request.getParameter("texteBouton");
        if (texteBouton == null || texteBouton.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'texteBouton' est requis.");
        }
        promo.setTexteBouton(texteBouton.trim());

        // Route cible
        String routeCible = request.getParameter("routeCible");
        if (routeCible == null || routeCible.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'routeCible' est requis.");
        }
        promo.setRouteCible(routeCible.trim());

        // Durée vidéo (seulement pour les vidéos)
        String dureeVideo = request.getParameter("dureeVideo");
        promo.setDureeVideo(dureeVideo != null ? dureeVideo.trim() : null);

        // Vues
        String vues = request.getParameter("vues");
        promo.setVues(vues != null ? vues.trim() : null);

        // Ordre d'affichage
        String ordreStr = request.getParameter("ordreAffichage");
        try {
            promo.setOrdreAffichage(ordreStr != null ? Integer.parseInt(ordreStr) : 0);
        } catch (NumberFormatException e) {
            promo.setOrdreAffichage(0);
        }

        // Statut
        String statut = request.getParameter("statut");
        if (statut == null || (!"actif".equals(statut) && !"inactif".equals(statut) && !"supprime".equals(statut))) {
            statut = "actif"; // Valeur par défaut
        }
        promo.setStatut(statut);

        // Dates de début et fin
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        
        try {
            if (dateDebutStr != null && !dateDebutStr.trim().isEmpty()) {
                LocalDateTime dateDebut = LocalDateTime.parse(dateDebutStr, INPUT_FORMATTER);
                promo.setDateDebut(dateDebut);
            }
            
            if (dateFinStr != null && !dateFinStr.trim().isEmpty()) {
                LocalDateTime dateFin = LocalDateTime.parse(dateFinStr, INPUT_FORMATTER);
                promo.setDateFin(dateFin);
            }
        } catch (DateTimeParseException e) {
            // Ignorer les erreurs de parsing, les dates restent null
        }

        // Dates de création et modification
        promo.setCreationDate(LocalDateTime.now());
        promo.setUpdateDate(LocalDateTime.now());

        return promo;
    }

    private String getCompleteMediaUrl(HttpServletRequest request, Promo promo) {
        return getCompleteMediaUrl(request.getContextPath(), promo.getCheminMedia(), promo.getTypeContenu());
    }

    private String getCompleteMediaUrl(String contextPath, String mediaPath, String type) {
        if (mediaPath != null && !mediaPath.isEmpty()) {
            // Si c'est déjà une URL complète (commence par http://, https:// ou /)
            if (mediaPath.startsWith("http://") || mediaPath.startsWith("https://") || mediaPath.startsWith("/")) {
                return mediaPath;
            }
            
            // Convertir le chemin relatif en URL absolue
            if (mediaPath.startsWith("uploads/")) {
                if ("video".equalsIgnoreCase(type) && mediaPath.startsWith("uploads/videos/")) {
                    return contextPath + "/videos/" + mediaPath.substring("uploads/videos/".length());
                } else if ("image".equalsIgnoreCase(type) && mediaPath.startsWith("uploads/promos/")) {
                    return contextPath + "/images/promos/" + mediaPath.substring("uploads/promos/".length());
                }
            }
            
            // Si le chemin ne commence pas par uploads/, retourner tel quel
            return mediaPath;
        }
        
        // URL par défaut
        if ("video".equalsIgnoreCase(type)) {
            return contextPath + "/videos/default.mp4";
        } else {
            return contextPath + "/images/promos/default.png";
        }
    }

    private String getBadgeClassForStatut(String statut) {
        switch (statut) {
            case "actif": return "badge-success";
            case "inactif": return "badge-secondary";
            case "supprime": return "badge-danger";
            default: return "badge-light";
        }
    }

    private String getStatutText(String statut) {
        switch (statut) {
            case "actif": return "Actif";
            case "inactif": return "Inactif";
            case "supprime": return "Supprimé";
            default: return statut;
        }
    }

    private void setSuccessNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmErrorNotif", message);
        request.getSession().setAttribute("toastType", "error");
    }

    private String sauvegarderFichier(HttpServletRequest request, Part filePart, String dossier) throws IOException {
        String fileName = getFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        // Extension
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) extension = fileName.substring(i);
        
        // Nom de fichier unique
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Déterminer le répertoire d'upload
        File uploadDir;
        if ("promos/videos".equals(dossier)) {
            uploadDir = new File(UPLOAD_PROMO_VIDEOS_DIR);
        } else {
            uploadDir = new File(UPLOAD_PROMO_IMAGES_DIR);
        }
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fullPath = uploadDir + File.separator + uniqueFileName;
        filePart.write(fullPath);

        return "uploads/" + dossier + "/" + uniqueFileName;
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return null;
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
        }
        return null;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private String toJson(Promo promo) {
        // Méthode simple pour convertir un Promo en JSON
        // Dans un vrai projet, utilisez Gson ou Jackson
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(promo.getId()).append(",");
        json.append("\"titre\":\"").append(escapeJson(promo.getTitre())).append("\",");
        json.append("\"sousTitre\":\"").append(escapeJson(promo.getSousTitre())).append("\",");
        json.append("\"description\":\"").append(escapeJson(promo.getDescription())).append("\",");
        json.append("\"typeContenu\":\"").append(escapeJson(promo.getTypeContenu())).append("\",");
        json.append("\"cheminMedia\":\"").append(escapeJson(promo.getCheminMedia())).append("\",");
        json.append("\"texteBouton\":\"").append(escapeJson(promo.getTexteBouton())).append("\",");
        json.append("\"routeCible\":\"").append(escapeJson(promo.getRouteCible())).append("\",");
        json.append("\"ordreAffichage\":").append(promo.getOrdreAffichage()).append(",");
        json.append("\"statut\":\"").append(escapeJson(promo.getStatut())).append("\"");
        
        if (promo.getCouleursGradient() != null) {
            json.append(",\"couleursGradient\":[");
            for (int i = 0; i < promo.getCouleursGradient().size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(promo.getCouleursGradient().get(i)).append("\"");
            }
            json.append("]");
        }
        
        if (promo.getDateDebut() != null) {
            json.append(",\"dateDebut\":\"").append(promo.getDateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\"");
        }
        
        if (promo.getDateFin() != null) {
            json.append(",\"dateFin\":\"").append(promo.getDateFin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\"");
        }
        
        json.append("}");
        return json.toString();
    }
}