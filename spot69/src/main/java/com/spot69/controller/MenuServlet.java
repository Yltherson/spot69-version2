package com.spot69.controller;

import com.spot69.dao.MenuCategorieDAO;
import com.spot69.dao.PlatDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.dao.RayonDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.MenuCategorie;
import com.spot69.model.Plat;
import com.spot69.model.Rayon;
import com.spot69.model.RayonHierarchique;
import com.spot69.model.Utilisateur;
import com.spot69.model.Produit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet({"/blok/MenuServlet", "/MenuServlet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, 
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class MenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MenuCategorieDAO menuCategorieDAO = new MenuCategorieDAO();
    private final PlatDAO platDAO = new PlatDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final RayonDAO rayonDAO = new RayonDAO(); 
    
    // Chemins d'upload pour plats et catégories
    private static  String UPLOAD_PLAT_DIR ="";
    private static  String UPLOAD_CATEGORIE_DIR="";
    
    @Override
    public void init() throws ServletException {
        // ========== Choisir l’environnement ==========
        
        // Local
    	UPLOAD_PLAT_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "plats";
    	UPLOAD_CATEGORIE_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "categories";
        //  Production
//        ServletContext context = getServletContext();
//        UPLOAD_PLAT_DIR = context.getInitParameter("PROD_PLAT_UPLOAD_ROOT_DIR");
//        UPLOAD_CATEGORIE_DIR = context.getInitParameter("PROD_CATEGORIE_UPLOAD_ROOT_DIR");

        // =============================================

        System.out.println("INIT ImageServlet:");
        System.out.println("platUploadDir = " + UPLOAD_PLAT_DIR);
        System.out.println("categorieUploadDir = " + UPLOAD_CATEGORIE_DIR);
    }
    
    

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");

    	
    	String action = request.getParameter("action");

        if (action == null) {
            afficherListeCategories(request, response);
            return;
        }

        switch (action) {
        	case "editSousCategorie":
            case "editCategorie":
                afficherCategorie(request, response);
                break;
            case "categorie-parente-json":
                afficherCategoriesParentesJson(request, response);
                break;
            case "search":
                searchProducts(request, response);
                break;
            case "editPlat":
                afficherPlat(request, response);
                break;
            case "getPlat":
                getPlatJson(request, response);
                break;
            case "placer-commande":
                afficherPageCommande(request, response);
                break;

            case "deleteSousCategorie":
            case "deleteCategorie":
                supprimerCategorie(request, response);
                break;
            case "deletePlat":
                supprimerPlat(request, response);
                break;
            case "liste-plat":
                afficherListePlats(request, response);
                break;
            case "liste-categorie":
                afficherListeCategories(request, response);
                break;
            case "menu-c":
            	afficherCategories(request, response);
            	break;
            case "categorie-parente": //sa
                afficherCategoriesParentes(request, response);
                break;
            case "sous-categories": //sa
            	afficherSousCategoriesByParentId(request, response);
                break;
            case "plats-par-sous-categories": //sa
            	afficherPlatsParSousCategorieJson(request, response);;
                break;
            case "menu-sc":
            	afficherLesSousCategories(request, response);;
                break;
            case "liste-rayons":
                afficherListeRayons(request, response);
                break;
            case "editRayon":
                afficherRayon(request, response);
                break;
            case "deleteRayon":
                supprimerRayon(request, response);
                break;
            case "categories-by-rayon":
            	afficherCategoriesByRayonId(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");

    	String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "addCategorie":
                ajouterCategorie(request, response);
                break;
            case "updateCategorie":
                modifierCategorie(request, response);
                break;
            case "addPlat":
                ajouterPlat(request, response);
                break;
            case "updatePlat":
                modifierPlat(request, response);
                break;
            case "sous-categories-list":
            	afficherLesSousCategories(request, response);
                break;
            case "sous-categories":
            	afficherSousCategoriesByParentId(request, response);
                break;
            case "plats-par-categorie":
                afficherPlatsParCategorie(request, response);
                break;
            case "addRayon":
                ajouterRayon(request, response);
                break;
            case "updateRayon":
                modifierRayon(request, response);
                break;
            case "categories-by-rayon":
                afficherCategoriesByRayonId(request, response);
                break;

            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    // ===================== CATÉGORIES ===========================
    private void afficherListeCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
        request.setAttribute("categories", menuCategories);
        request.getRequestDispatcher("/blok/list-category-menu.jsp").forward(request, response);
    }

    private void afficherCategorie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String page = request.getParameter("page");
        
        MenuCategorie menuCategorie = menuCategorieDAO.getById(id);
        if (menuCategorie == null) {
            response.sendRedirect("MenuServlet?action=liste-categorie&null");
            return;
        }

        request.setAttribute("categorie", menuCategorie);
        
        if("l-cat".equals(page)) {
            request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
            request.getRequestDispatcher("/blok/list-category-menu.jsp").forward(request, response);
        }else if("l-subCat".equals(page)) {
            request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
            request.getRequestDispatcher("/blok/list-sousCategory-menu.jsp").forward(request, response);
        
        }

   }
    private void afficherPageCommande(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Charger les catégories parentes pour la barre de catégories
        List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
        request.setAttribute("categoriesParentes", categoriesParentes);
        
        List<Utilisateur> cLients = utilisateurDAO.findAllVisibleByRole("CLIENT");
        request.setAttribute("cLients", cLients);
        
        // Si vous voulez précharger la première catégorie et ses sous-catégories
        if (!categoriesParentes.isEmpty()) {
            int firstCategoryId = categoriesParentes.get(0).getId();
            List<MenuCategorie> sousCategories = menuCategorieDAO.getSousCategoriesByParentId(firstCategoryId);
            request.setAttribute("firstCategoryId", firstCategoryId);
            
            if (!sousCategories.isEmpty()) {
                List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategories.get(0).getId());
                request.setAttribute("firstSubCategoryId", sousCategories.get(0).getId());
                request.setAttribute("preloadedPlats", plats);
            }
        }
        
        request.getRequestDispatcher("/blok/placer-commande.jsp").forward(request, response);
    }
    
    private void searchProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String searchTerm = request.getParameter("search");
            List<Plat> plats = new ArrayList<>();
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                plats = menuCategorieDAO.searchPlatsByName(searchTerm);
            }
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Créer un objet avec la propriété "plats"
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("plats", gson.toJsonTree(plats));
            
            response.getWriter().write(gson.toJson(jsonObject));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            // Retourner un JSON vide en cas d'erreur
            response.getWriter().write("{\"plats\":[]}");
        }
    }
    
    private void ajouterCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String page = request.getParameter("page");
        try {
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String utilisateurIdStr = request.getParameter("utilisateurId");
            String parentIdStr = request.getParameter("parentId");
            String rayonIdStr = request.getParameter("rayonId"); // Nouveau paramètre

            if (nom == null || nom.trim().isEmpty() ||
                utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {

                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
                if("l-cat".equals(page)) {
                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
                    response.sendRedirect("MenuServlet?action=menu-c");
                }else if("l-subCat".equals(page)) {
                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
                    response.sendRedirect("MenuServlet?action=menu-sc");
                }
                return;
            }

            int utilisateurId = Integer.parseInt(utilisateurIdStr);
            Integer parentId = (parentIdStr != null && !parentIdStr.trim().isEmpty()) ? Integer.parseInt(parentIdStr) : null;
            
            // Gestion du rayon
            Rayon rayon = null;
            if (rayonIdStr != null && !rayonIdStr.trim().isEmpty()) {
                int rayonId = Integer.parseInt(rayonIdStr);
                rayon = rayonDAO.getById(rayonId);
            }

            // Validation: rayon obligatoire pour les catégories parentes
            if (parentId == null && rayon == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Le rayon est obligatoire pour les catégories parentes.");
                if("l-cat".equals(page)) {
                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
                    response.sendRedirect("MenuServlet?action=menu-c");
                }else if("l-subCat".equals(page)) {
                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
                    response.sendRedirect("MenuServlet?action=menu-sc");
                }
                return;
            }

            // Récupérer l'image de la catégorie si présente
            String imageUrl = null;
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                imageUrl = sauvegarderFichier(request, filePart, "categories");
            }

            MenuCategorie menuCategorie = new MenuCategorie();
            menuCategorie.setNom(nom.trim());
            menuCategorie.setDescription(description.trim());
            menuCategorie.setCreationDate(LocalDateTime.now());
            menuCategorie.setUpdateDate(LocalDateTime.now());
            menuCategorie.setUtilisateurId(utilisateurId);
            menuCategorie.setStatut("visible");
            menuCategorie.setParentId(parentId);
            menuCategorie.setImageUrl(imageUrl);
            menuCategorie.setRayon(rayon); // Assigner le rayon

            menuCategorieDAO.ajouter(menuCategorie);
            request.getSession().setAttribute("ToastAdmSuccesNotif", "Catégorie ajoutée avec succès !");
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        if("l-cat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-c");
        }else if("l-subCat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-sc");
        }
    }

    private void modifierCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String page = request.getParameter("page");
        try {
            String idStr = request.getParameter("id");
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String utilisateurIdStr = request.getParameter("utilisateurId");
            String parentIdStr = request.getParameter("parentId");
            String rayonIdStr = request.getParameter("rayonId"); // Nouveau paramètre

            if (idStr == null || idStr.trim().isEmpty() ||
                nom == null || nom.trim().isEmpty() ||
                utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {

                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
                if("l-cat".equals(page)) {
                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
                    response.sendRedirect("MenuServlet?action=menu-c");
                }else if("l-subCat".equals(page)) {
                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
                    response.sendRedirect("MenuServlet?action=menu-sc");
                }
                return;
            }

            int id = Integer.parseInt(idStr);
            int utilisateurId = Integer.parseInt(utilisateurIdStr);
            Integer parentId = (parentIdStr != null && !parentIdStr.trim().isEmpty()) ? Integer.parseInt(parentIdStr) : null;
            
            // Gestion du rayon
            Rayon rayon = null;
            if (rayonIdStr != null && !rayonIdStr.trim().isEmpty()) {
                int rayonId = Integer.parseInt(rayonIdStr);
                rayon = rayonDAO.getById(rayonId);
            }

            // Validation: rayon obligatoire pour les catégories parentes
            if (parentId == null && rayon == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Le rayon est obligatoire pour les catégories parentes.");
                if("l-cat".equals(page)) {
                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
                    response.sendRedirect("MenuServlet?action=menu-c");
                }else if("l-subCat".equals(page)) {
                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
                    response.sendRedirect("MenuServlet?action=menu-sc");
                }
                return;
            }

            MenuCategorie menuCategorie = menuCategorieDAO.getById(id);
            if (menuCategorie == null) {
                if("l-cat".equals(page)) {
                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
                    response.sendRedirect("MenuServlet?action=menu-c");
                }else if("l-subCat".equals(page)) {
                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
                    response.sendRedirect("MenuServlet?action=menu-sc");
                }
                return;
            }

            menuCategorie.setNom(nom.trim());
            menuCategorie.setDescription(description.trim());
            menuCategorie.setUpdateDate(LocalDateTime.now());
            menuCategorie.setUtilisateurId(utilisateurId);
            menuCategorie.setStatut("visible");
            menuCategorie.setParentId(parentId);
            menuCategorie.setRayon(rayon); // Assigner le rayon

            // Gérer l'image de la catégorie
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String imageUrl = sauvegarderFichier(request, filePart, "categories");
                menuCategorie.setImageUrl(imageUrl);
            }

            menuCategorieDAO.modifier(menuCategorie);
            request.getSession().setAttribute("ToastAdmSuccesNotif", "Catégorie modifiée avec succès !");
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        if("l-cat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-c");
        }else if("l-subCat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-sc");
        }
    }

//    private void ajouterCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//
//        String page = request.getParameter("page");
//    	try {
//
//            System.out.println("li la ");
//            String nom = request.getParameter("nom");
//            String description = request.getParameter("description");
//            String utilisateurIdStr = request.getParameter("utilisateurId");
//            String parentIdStr = request.getParameter("parentId");
//
//            if (nom == null || nom.trim().isEmpty() ||
//                utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {
//
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
//                if("l-cat".equals(page)) {
//                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//                    response.sendRedirect("MenuServlet?action=menu-c");
//                }else if("l-subCat".equals(page)) {
//                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//                    response.sendRedirect("MenuServlet?action=menu-sc");
//                }
//                return;
//            }
//
//            int utilisateurId = Integer.parseInt(utilisateurIdStr);
//            Integer parentId = (parentIdStr != null && !parentIdStr.trim().isEmpty()) ? Integer.parseInt(parentIdStr) : null;
//
//            // Récupérer l'image de la catégorie si présente
//            String imageUrl = null;
//            Part filePart = request.getPart("image");
//            if (filePart != null && filePart.getSize() > 0) {
//                imageUrl = sauvegarderFichier(request, filePart, "categories"); // Sauvegarde de l'image
//            }
//
//            MenuCategorie menuCategorie = new MenuCategorie();
//            menuCategorie.setNom(nom.trim());
//            menuCategorie.setDescription(description.trim());
//            menuCategorie.setCreationDate(LocalDateTime.now());
//            menuCategorie.setUpdateDate(LocalDateTime.now());
//            menuCategorie.setUtilisateurId(utilisateurId);
//            menuCategorie.setStatut("visible");
//            menuCategorie.setParentId(parentId);
//            menuCategorie.setImageUrl(imageUrl);  // Assigner l'URL de l'image
//
//            menuCategorieDAO.ajouter(menuCategorie);
//            request.getSession().setAttribute("ToastAdmSuccesNotif", "Catégorie ajoutée avec succès !");
//        } catch (Exception e) {
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la catégorie."  + e.getMessage());
//            e.printStackTrace();
//        }
//        if("l-cat".equals(page)) {
//            request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//            response.sendRedirect("MenuServlet?action=menu-c");
//        }else if("l-subCat".equals(page)) {
//            request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//            response.sendRedirect("MenuServlet?action=menu-sc");
//        }
//    }
//
//    private void modifierCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//
//        String page = request.getParameter("page");
//    	try {
//            String idStr = request.getParameter("id");
//            String nom = request.getParameter("nom");
//            String description = request.getParameter("description");
//            String utilisateurIdStr = request.getParameter("utilisateurId");
//            String parentIdStr = request.getParameter("parentId");
//
//            if (idStr == null || idStr.trim().isEmpty() ||
//                nom == null || nom.trim().isEmpty() ||
//                utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {
//
//                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
//                if("l-cat".equals(page)) {
//                    request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//                    response.sendRedirect("MenuServlet?action=menu-c");
//                }else if("l-subCat".equals(page)) {
//                    request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//                    response.sendRedirect("MenuServlet?action=menu-sc");
//                }
//                return;
//            }
//
//            int id = Integer.parseInt(idStr);
//            int utilisateurId = Integer.parseInt(utilisateurIdStr);
//            Integer parentId = (parentIdStr != null && !parentIdStr.trim().isEmpty()) ? Integer.parseInt(parentIdStr) : null;
//
//            MenuCategorie menuCategorie = menuCategorieDAO.getById(id);
//            if (menuCategorie == null) {
//            	 if("l-cat".equals(page)) {
//                     request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//                     response.sendRedirect("MenuServlet?action=menu-c");
//                 }else if("l-subCat".equals(page)) {
//                     request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//                     response.sendRedirect("MenuServlet?action=menu-sc");
//                 }
//                return;
//            }
//
//            menuCategorie.setNom(nom.trim());
//            menuCategorie.setDescription(description.trim());
//            menuCategorie.setUpdateDate(LocalDateTime.now());
//            menuCategorie.setUtilisateurId(utilisateurId);
//            menuCategorie.setStatut("visible");
//            menuCategorie.setParentId(parentId);
//
//            // Gérer l'image de la catégorie
//            Part filePart = request.getPart("image");
//            if (filePart != null && filePart.getSize() > 0) {
//                String imageUrl = sauvegarderFichier(request, filePart, "categories");  // Sauvegarder l'image
//                menuCategorie.setImageUrl(imageUrl);
//            }
//
//            menuCategorieDAO.modifier(menuCategorie);
//            request.getSession().setAttribute("ToastAdmSuccesNotif", "Catégorie modifiée avec succès !");
//        } catch (Exception e) {
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification de la catégorie.");
//        }
//        if("l-cat".equals(page)) {
//            request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//            response.sendRedirect("MenuServlet?action=menu-c");
//        }else if("l-subCat".equals(page)) {
//            request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//            response.sendRedirect("MenuServlet?action=menu-sc");
//        }
//    }

//    private void supprimerCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        String page = request.getParameter("page");
//    	try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            menuCategorieDAO.supprimer(id);
//            request.getSession().setAttribute("ToastAdmSuccesNotif", "Catégorie supprimée avec succès !");
//        } catch (Exception e) {
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression de la catégorie.");
//        }
//        
//        if("l-cat".equals(page)) {
//            request.setAttribute("categorie-parente", menuCategorieDAO.getCategoriesParentes());
//            response.sendRedirect("MenuServlet?action=menu-c");
//        }else if("l-subCat".equals(page)) {
//            request.setAttribute("sous-categories", menuCategorieDAO.getAllSubCategory());
//            response.sendRedirect("MenuServlet?action=menu-sc");
//        }
//        
//    }
    private void supprimerCategorie(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String page = request.getParameter("page");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String result = menuCategorieDAO.supprimer(id);
            
            if (result.startsWith("SUCCESS:")) {
                // Extraction du message de succès
                String successMessage = result.substring(8); // Enlève "SUCCESS:"
                request.getSession().setAttribute("ToastAdmSuccesNotif", successMessage);
            } else {
                // Message d'erreur détaillé
                request.getSession().setAttribute("ToastAdmErrorNotif", result);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "ID de catégorie invalide.");
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur inattendue : " + e.getMessage());
        }
        
        // Redirection selon la page d'origine
        if ("l-cat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-c");
        } else if ("l-subCat".equals(page)) {
            response.sendRedirect("MenuServlet?action=menu-sc");
        } else {
            // Redirection par défaut
            response.sendRedirect("MenuServlet?action=menu-c");
        }
    }

    private void getPlatJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Plat plat = platDAO.chercherParId(id);
            
            if (plat == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Plat non trouvé\"}");
                return;
            }
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            Gson gson = new Gson();
            String json = gson.toJson(plat);
            response.getWriter().write(json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID invalide\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur serveur\"}");
        }
    }

    private void afficherSousCategoriesByParentId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int parentId = Integer.parseInt(request.getParameter("parentId"));

            // Récupérer les sous-catégories par parentId
            List<MenuCategorie> sousCategoriesFood = menuCategorieDAO.getSousCategoriesByParentId(parentId);

            // Si aucune sous-catégorie n'est trouvée, on renvoie un message vide
            if (sousCategoriesFood == null || sousCategoriesFood.isEmpty()) {
                sousCategoriesFood = new ArrayList<>();  // Liste vide
            }

            // Récupérer les plats de la première sous-catégorie (si elle existe)
            List<Plat> plats = new ArrayList<>();
            if (!sousCategoriesFood.isEmpty()) {
                plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategoriesFood.get(0).getId()); // Afficher les plats de la première sous-catégorie
            }

            // Créer une réponse JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("sousCategories", new Gson().toJsonTree(sousCategoriesFood));
            jsonResponse.add("plats", new Gson().toJsonTree(plats));

            // Définir le type de réponse comme étant JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Écrire la réponse JSON
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur lors du chargement des sous-catégories.\"}");
        }
    }


    private void afficherPlatsParCategorie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int categorieId = Integer.parseInt(request.getParameter("categorieId"));
            List<Plat> plats = menuCategorieDAO.getPlatsByCategoryId(categorieId);
            MenuCategorie menuCategorie = menuCategorieDAO.getById(categorieId);
            request.setAttribute("plats", plats);
            request.setAttribute("categorie", menuCategorie);
            request.getRequestDispatcher("/menu.jsp").forward(request, response);
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors du chargement des plats.");
            response.sendRedirect("MenuServlet?action=liste-plat");
        }
    }
    
    private void afficherPlatsParSousCategorieJson(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Récupérer l'ID de la catégorie
            int sousCategorieId = Integer.parseInt(request.getParameter("categorieId"));

            // Récupérer les plats associés à cette catégorie
            List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategorieId);
            
            // Créer une réponse JSON
            JsonObject jsonResponse = new JsonObject();
            System.out.println("PLATS  récupérées : " + plats);

            // Ajouter les plats au JSON
            if (plats != null && !plats.isEmpty()) {
                jsonResponse.add("plats", new Gson().toJsonTree(plats));
            } else {
                jsonResponse.add("plats", new JsonArray()); // Si aucun plat n'est trouvé, on renvoie un tableau vide
            }

            // Définir le type de réponse comme étant JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Écrire la réponse JSON
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur lors du chargement des plats.\"}");
        }
    }


    private void afficherCategoriesParentes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
        // Log pour vérifier que les catégories sont bien récupérées
        System.out.println("Catégories parentes récupérées : " + categoriesParentes);

        // Assurez-vous que vous passez correctement les catégories parentes à la JSP
        request.setAttribute("categorie-parente", categoriesParentes);
        
        request.getRequestDispatcher("/menu.jsp").forward(request, response); // Affiche le menu avec les catégories parentes
    }
    
    private void afficherCategoriesParentesJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
            System.out.println("Catégories parentes récupérées : " + categoriesParentes);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            Gson gson = new Gson();
            String json = gson.toJson(categoriesParentes);
            response.getWriter().write(json);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur lors du chargement des catégories.\"}");
        }
    }
    private void afficherLesSousCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuCategorie> sousCategories = menuCategorieDAO.getAllSubCategory();
        // Log pour vérifier que les catégories sont bien récupérées
        System.out.println("Catégories parentes récupérées : " + sousCategories);

        // Assurez-vous que vous passez correctement les catégories parentes à la JSP
        request.setAttribute("sous-categories", sousCategories);
        
        request.getRequestDispatcher("list-sousCategory-menu.jsp").forward(request, response); // Affiche le menu avec les catégories parentes
    }
    
    
    private void afficherCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
        // Log pour vérifier que les catégories sont bien récupérées
        System.out.println("Catégories parentes récupérées : " + categoriesParentes);

        // Assurez-vous que vous passez correctement les catégories parentes à la JSP
        request.setAttribute("categorie-parente", categoriesParentes);
        
        request.getRequestDispatcher("list-category-menu.jsp").forward(request, response); // Affiche le menu avec les catégories parentes
    }


    // ===================== PLATS ===========================
//    private void afficherListePlats(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Plat> plats = platDAO.listerPlats();
//        List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
//
//        request.setAttribute("plats", plats);
//        request.setAttribute("categories", menuCategories);
//        ProduitDAO produitDAO = new ProduitDAO();
//        //List<Produit> boissonsDisponibles = produitDAO.getBoissonsDisponibles();
//
//        List<Produit> produitsDisponibles = produitDAO.listerProduits();
//
//       // request.setAttribute("boissons", boissonsDisponibles);
//        request.setAttribute("produits", produitsDisponibles);
//        request.getRequestDispatcher("/blok/list-plat.jsp").forward(request, response);
//    }
//    private void afficherListePlats(HttpServletRequest request, HttpServletResponse response) 
//            throws ServletException, IOException {
//        List<Plat> plats = platDAO.listerPlats();
//        ProduitDAO produitDAO = new ProduitDAO();
//        RayonDAO rayonDAO = new RayonDAO();
//        
//        // Récupérer tous les rayons et catégories
//        List<Rayon> rayons = rayonDAO.getAll();
//        List<MenuCategorie> categories = menuCategorieDAO.getAll();
//        List<Produit> produitsDisponibles = produitDAO.listerProduits();
//        
//        request.setAttribute("plats", plats);
//        request.setAttribute("rayons", rayons);
//        request.setAttribute("categories", categories);
//        request.setAttribute("produits", produitsDisponibles);
//        
//        request.getRequestDispatcher("/blok/list-plat.jsp").forward(request, response);
//    }
    private void afficherListePlats(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== DEBUT afficherListePlats ===");
        
        // Récupérer tous les plats
        List<Plat> plats = platDAO.listerPlats();
        System.out.println("Nombre de plats récupérés: " + (plats != null ? plats.size() : 0));
        
        ProduitDAO produitDAO = new ProduitDAO();
        RayonDAO rayonDAO = new RayonDAO();
        
        // Récupérer TOUS les rayons
        System.out.println("Récupération de tous les rayons...");
        List<Rayon> rayons = rayonDAO.getAll();
        System.out.println("Nombre de rayons récupérés: " + (rayons != null ? rayons.size() : 0));
        
        // Récupérer TOUTES les catégories
        System.out.println("Récupération de toutes les catégories...");
        List<MenuCategorie> categories = menuCategorieDAO.getAll();
        System.out.println("Nombre de catégories récupérées: " + (categories != null ? categories.size() : 0));
        
        // Récupérer TOUS les produits disponibles
        System.out.println("Récupération de tous les produits...");
        List<Produit> produitsDisponibles = produitDAO.listerProduits();
        System.out.println("Nombre de produits récupérés: " + (produitsDisponibles != null ? produitsDisponibles.size() : 0));
        
        // Récupérer la hiérarchie complète
        System.out.println("Récupération de la hiérarchie complète...");
        List<RayonHierarchique> hierarchieRayons = menuCategorieDAO.getHierarchieCompleteRayons();
        System.out.println("Hiérarchie rayons: " + (hierarchieRayons != null ? hierarchieRayons.size() : 0));
        
        // Créer des maps pour accéder rapidement aux données
        Map<Integer, MenuCategorie> categorieMap = new HashMap<>();
        Map<Integer, Rayon> rayonMap = new HashMap<>();
        
        if (categories != null) {
            for (MenuCategorie cat : categories) {
                categorieMap.put(cat.getId(), cat);
            }
        }
        
        if (rayons != null) {
            for (Rayon rayon : rayons) {
                rayonMap.put(rayon.getId(), rayon);
            }
        }
        
        // Séparer catégories parentes et sous-catégories
        List<MenuCategorie> categoriesParents = new ArrayList<>();
        List<MenuCategorie> sousCategories = new ArrayList<>();
        
        if (categories != null) {
            for (MenuCategorie cat : categories) {
                if (cat.getParentId() == null) {
                    categoriesParents.add(cat);
                } else {
                    sousCategories.add(cat);
                }
            }
        }
        
        System.out.println("Catégories parents: " + categoriesParents.size());
        System.out.println("Sous-catégories: " + sousCategories.size());
        
        // Récupérer tous les plats avec hiérarchie pour le modal
        List<Plat> tousLesPlats = menuCategorieDAO.getAllPlatsWithoutRestrictions();
        System.out.println("Tous les plats avec hiérarchie: " + (tousLesPlats != null ? tousLesPlats.size() : 0));
        debugPlatsData( plats);
        // Ajouter tous les attributs à la requête
        request.setAttribute("plats", plats);
        
        request.setAttribute("rayons", rayons);
        request.setAttribute("categories", categories);
        request.setAttribute("categoriesParents", categoriesParents);
        request.setAttribute("sousCategories", sousCategories);
        request.setAttribute("produits", produitsDisponibles);
        request.setAttribute("hierarchieRayons", hierarchieRayons);
        request.setAttribute("categorieMap", categorieMap);
        request.setAttribute("rayonMap", rayonMap);
        request.setAttribute("tousLesPlats", tousLesPlats);
        
        System.out.println("=== FIN afficherListePlats ===");
        
        request.getRequestDispatcher("/blok/list-plat.jsp").forward(request, response);
    }
    
 // Dans la méthode qui prépare les données pour la JSP
    private void debugPlatsData(List<Plat> plats) {
        System.out.println("=== DEBUG PLATS DATA ===");
        System.out.println("Nombre total de plats: " + plats.size());
        
        int countWithProduct = 0;
        int countWithoutCategories = 0;
        
        for (Plat plat : plats) {
            if (plat.getProduit() != null) {
                countWithProduct++;
                System.out.println("Plat ID " + plat.getId() + " a un produit: " + plat.getProduit().getNom());
                System.out.println("  - Catégorie produit: " + 
                    (plat.getProduit().getCategorie() != null ? plat.getProduit().getCategorie().getNom() : "NULL"));
                System.out.println("  - Sous-catégorie produit: " + 
                    (plat.getProduit().getSousCategorie() != null ? plat.getProduit().getSousCategorie().getNom() : "NULL"));
                System.out.println("  - Rayon produit: " + 
                    (plat.getProduit().getRayon() != null ? plat.getProduit().getRayon().getNom() : "NULL"));
            }
            
            if ((plat.getCategorie() == null || plat.getCategorie().getNom() == null) && 
                (plat.getProduit() == null || plat.getProduit().getCategorie() == null)) {
                countWithoutCategories++;
                System.out.println("PLAT SANS CATÉGORIE: " + plat.getNom() + " (ID: " + plat.getId() + ")");
            }
        }
        
        System.out.println("Plats avec produit: " + countWithProduct);
        System.out.println("Plats sans catégorie: " + countWithoutCategories);
        System.out.println("==========================");
    }
    
    

    private void afficherPlat(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Plat plat = platDAO.chercherParId(id);
        if (plat == null) {
            response.sendRedirect("MenuServlet?action=liste-plat");
            return;
        }

        List<Plat> plats = platDAO.listerPlats();
        List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
        
        ProduitDAO produitDAO = new ProduitDAO();
        List<Produit> boissonsDisponibles = produitDAO.getBoissonsDisponibles();

        request.setAttribute("boissons", boissonsDisponibles);

        request.setAttribute("categories", menuCategories);
        List<Produit> produitsDisponibles = produitDAO.listerProduits();

       // request.setAttribute("boissons", boissonsDisponibles);
        request.setAttribute("produits", produitsDisponibles);
        request.setAttribute("plat", plat);
        request.setAttribute("plats", plats);
        request.setAttribute("categories", menuCategories);
        request.getRequestDispatcher("/blok/list-plat.jsp").forward(request, response);
    }

//AJOUTER PLAT OLD
//    private void ajouterPlat(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        request.setCharacterEncoding("UTF-8");
//
//        try {
//            String nom = request.getParameter("nom");
//            String description = request.getParameter("description");
//            String rayonIdStr = request.getParameter("rayonId");
//            String prixStr = request.getParameter("prix");
//            String categorieIdStr = request.getParameter("categorieId");
//            String sousCategorieIdStr = request.getParameter("sousCategorieId");
//
//            HttpSession session = request.getSession(false);
//            Integer utilisateurId = session != null && session.getAttribute("userId") != null
//                    ? (Integer) session.getAttribute("userId")
//                    : 0;
//
//            String utiliserProduitExistante = request.getParameter("utiliserProduitExistante");
//            String produitExistanteIdStr = request.getParameter("produitExistanteId");
//
//            Plat plat = new Plat();
//
//            // Cas produit existant
//            if ("oui".equalsIgnoreCase(utiliserProduitExistante) && produitExistanteIdStr != null && !produitExistanteIdStr.isEmpty()) {
//
//                StringBuilder champsManquants = new StringBuilder();
//                if (produitExistanteIdStr.trim().isEmpty()) champsManquants.append("• Produit existant<br>");
//                if (utiliserProduitExistante.trim().isEmpty()) champsManquants.append("• Option d’utilisation du produit<br>");
////                if (categorieIdStr == null || categorieIdStr.trim().isEmpty()) champsManquants.append("• Catégorie<br>");
////                if (sousCategorieIdStr == null || sousCategorieIdStr.trim().isEmpty()) champsManquants.append("• Sous-catégorie<br>");
//
//                if (champsManquants.length() > 0) {
//                    request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir les champs obligatoires suivants :<br>" + champsManquants.toString());
//                    response.sendRedirect("MenuServlet?action=liste-plat");
//                    return;
//                }
//
////                int categorieId = Integer.parseInt(categorieIdStr);
////                int sousCategorieId = Integer.parseInt(sousCategorieIdStr);
//                int produitExistanteId = Integer.parseInt(produitExistanteIdStr);
//
//                Produit produit = produitDAO.chercherParId(produitExistanteId);
//                if (produit == null) {
//                    request.getSession().setAttribute("ToastAdmErrorNotif", "Produit existant introuvable.");
//                    response.sendRedirect("MenuServlet?action=liste-plat");
//                    return;
//                }
//
//                // Copier les données du produit existant
//                plat.setProductId(produit.getId());
////                plat.setCategorieId(categorieId);
////                plat.setSousCategorieId(sousCategorieId);
//                plat.setUtilisateurId(utilisateurId);
//                plat.setCreationDate(LocalDateTime.now());
//                plat.setUpdateDate(LocalDateTime.now());
//
//                boolean success = platDAO.ajouterPlatProduitExistante(plat);
//                if (!success) {
//                    request.getSession().setAttribute("ToastAdmErrorNotif", "Ce produit existe déjà pour cette catégorie et sous-catégorie.");
//                    response.sendRedirect("MenuServlet?action=liste-plat");
//                    return;
//                }
//
//                request.getSession().setAttribute("ToastAdmSuccesNotif", "Plat ajouté avec succès !");
//
//            } else { // Cas création d’un nouveau plat
//
//                StringBuilder champsManquants = new StringBuilder();
//                if (nom == null || nom.trim().isEmpty()) champsManquants.append("• Nom du plat, ");
//                if (prixStr == null || prixStr.trim().isEmpty()) champsManquants.append("• Prix, ");
//                if (rayonIdStr == null || rayonIdStr.trim().isEmpty()) champsManquants.append("• Rayon, ");
//                if (categorieIdStr == null || categorieIdStr.trim().isEmpty()) champsManquants.append("• Catégorie, ");
//                if (sousCategorieIdStr == null || sousCategorieIdStr.trim().isEmpty()) champsManquants.append("• Sous-catégorie, ");
//
//                if (champsManquants.length() > 0) {
//                    request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir les champs obligatoires suivants : " + champsManquants.toString());
//                    response.sendRedirect("MenuServlet?action=liste-plat");
//                    return;
//                }
//
//                double prix = Double.parseDouble(prixStr);
//                int categorieId = Integer.parseInt(categorieIdStr);
//                int sousCategorieId = Integer.parseInt(sousCategorieIdStr);
//                int rayonId = Integer.parseInt(rayonIdStr);
//
//                Part filePart = request.getPart("image");
//                String fileName;
//                if (filePart == null || filePart.getSize() == 0) {
//                    fileName = "uploads/default/default.png";
//                } else {
//                    fileName = sauvegarderFichier(request, filePart, "plats");
//                }
//                
//                String qtePointsStr = request.getParameter("qtePoints");
//                int qtePoints = Integer.parseInt(qtePointsStr);
//                plat.setQtePoints(qtePoints);
//
//                plat.setNom(nom.trim());
//                plat.setDescription(description.trim());
//                plat.setRayonId(rayonId);
//                plat.setPrix(prix);
//                plat.setCategorieId(categorieId);
//                plat.setSousCategorieId(sousCategorieId);
//                plat.setUtilisateurId(utilisateurId);
//                plat.setCreationDate(LocalDateTime.now());
//                plat.setUpdateDate(LocalDateTime.now());
//                plat.setStatut("visible");
//                plat.setImage(fileName);
//
//                platDAO.ajouterPlat(plat);
//                request.getSession().setAttribute("ToastAdmSuccesNotif", "Plat ajouté avec succès !");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout du plat : " + e.getMessage());
//        }
//
//        response.sendRedirect("MenuServlet?action=liste-plat");
//    }
    private void ajouterPlat(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer utilisateurId = session != null && session.getAttribute("userId") != null
                ? (Integer) session.getAttribute("userId")
                : 0;

        try {
            String selectedProductIds = request.getParameter("selectedProductIds");
            
            // MODIFICATION : On vérifie directement les produits sélectionnés
            if (selectedProductIds == null || selectedProductIds.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez sélectionner au moins un produit.");
                response.sendRedirect("MenuServlet?action=liste-plat");
                return;
            }
            
            // Séparer les IDs
            String[] produitIds = selectedProductIds.split(",");
            int successCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (String produitIdStr : produitIds) {
                if (produitIdStr.trim().isEmpty()) continue;
                
                try {
                    int produitId = Integer.parseInt(produitIdStr.trim());
                    
                    // Vérifier si le produit existe
                    Produit produit = produitDAO.chercherParId(produitId);
                    if (produit == null) {
                        errors.append("Produit ID ").append(produitId).append(" introuvable.<br>");
                        errorCount++;
                        continue;
                    }
                    
                    // Vérifier si le produit est déjà dans le menu
                    if (platDAO.isProduitAlreadyInMenu(produitId)) {
                        errors.append(produit.getNom()).append(" est déjà dans le menu.<br>");
                        errorCount++;
                        continue;
                    }
                    
                    // Créer le plat
                    Plat plat = new Plat();
                    plat.setProductId(produitId);
                    plat.setNom(null); // Utilisera le nom du produit
                    plat.setDescription(null); // Utilisera la description du produit
                    plat.setPrix(0.0); // Utilisera le prix du produit
                    
                    // Récupérer la hiérarchie du produit
                    if (produit.getCategorie() != null) {
                        plat.setCategorieId(produit.getCategorie().getId());
                        
                        if (produit.getSousCategorie() != null) {
                            plat.setSousCategorieId(produit.getSousCategorie().getId());
                        }
                        
                        if (produit.getCategorie().getRayon() != null) {
                            plat.setRayonId(produit.getCategorie().getRayon().getId());
                        }
                    }
                    
                    plat.setUtilisateurId(utilisateurId);
                    plat.setCreationDate(LocalDateTime.now());
                    plat.setUpdateDate(LocalDateTime.now());
                    plat.setStatut("VISIBLE");
                    plat.setImage(null); // Utilisera l'image du produit
                    
                    // Ajouter le plat
                    boolean success = platDAO.ajouterPlatProduitExistante(plat);
                    if (success) {
                        successCount++;
                    } else {
                        errors.append("Erreur lors de l'ajout de ").append(produit.getNom()).append(".<br>");
                        errorCount++;
                    }
                    
                } catch (NumberFormatException e) {
                    errors.append("ID produit invalide: ").append(produitIdStr).append("<br>");
                    errorCount++;
                } catch (Exception e) {
                    errors.append("Erreur avec produit ID ").append(produitIdStr).append(": ").append(e.getMessage()).append("<br>");
                    errorCount++;
                }
            }
            
            // Préparer le message de résultat
            StringBuilder message = new StringBuilder();
            if (successCount > 0) {
                message.append(successCount).append(" produit(s) ajouté(s) avec succès au menu.");
            }
            if (errorCount > 0) {
                if (message.length() > 0) message.append(" ");
                message.append(errorCount).append(" erreur(s) : ").append(errors.toString());
            }
            
            if (successCount > 0) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", message.toString());
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", message.toString());
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", 
                "Erreur lors de l'ajout : " + e.getMessage());
        }
        
        response.sendRedirect("MenuServlet?action=liste-plat");
    }


    private void modifierPlat(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	request.setCharacterEncoding("UTF-8");
    	

   	 HttpSession session = request.getSession(false);
    	 Integer utilisateurId = session != null && session.getAttribute("userId") != null
                 ? (Integer) session.getAttribute("userId")
                 : 0;

        try {
        	String idStr = request.getParameter("id");
        	String nom = request.getParameter("nom");
        	String description = request.getParameter("description");
        	String prixStr = request.getParameter("prix");
        	String rayonIdStr = request.getParameter("rayonId");
        	String categorieIdStr = request.getParameter("categorieId");
        	String sousCategorieIdStr = request.getParameter("sousCategorieId");

        	StringBuilder champsVides = new StringBuilder();

        	if (idStr == null || idStr.trim().isEmpty()) {
        	    champsVides.append("ID, ");
        	}
        	if (nom == null || nom.trim().isEmpty()) {
        	    champsVides.append("Nom, ");
        	}
        	if (prixStr == null || prixStr.trim().isEmpty()) {
        	    champsVides.append("Prix, ");
        	}
        	if (categorieIdStr == null || categorieIdStr.trim().isEmpty()) {
        	    champsVides.append("Catégorie, ");
        	}
        	if (sousCategorieIdStr == null || sousCategorieIdStr.trim().isEmpty()) {
        	    champsVides.append("Sous-catégorie, ");
        	}
        	if (rayonIdStr == null || rayonIdStr.trim().isEmpty()) {
        	    champsVides.append("Rayon, ");
        	}

        	// S'il y a des champs vides, on affiche un message d'erreur
        	if (champsVides.length() > 0) {
        	    // Supprimer la virgule finale et l’espace
        	    champsVides.setLength(champsVides.length() - 2);

        	    request.getSession().setAttribute("ToastAdmErrorNotif", 
        	        "Veuillez remplir les champs obligatoires : " + champsVides.toString());

        	    response.sendRedirect("MenuServlet?action=liste-plat");
        	    return;
        	}

            int id = Integer.parseInt(idStr);
            Plat plat = platDAO.chercherParId(id);
            if (plat == null) {
                response.sendRedirect("MenuServlet?action=liste-plat");
                return;
            }

            double prix = Double.parseDouble(prixStr);
            int categorieId = Integer.parseInt(categorieIdStr);
            int sousCategorieId = Integer.parseInt(sousCategorieIdStr); 
            int rayonId = Integer.parseInt(rayonIdStr); 
            
            String qtePointsStr = request.getParameter("qtePoints");
            int qtePoints = Integer.parseInt(qtePointsStr);
            plat.setQtePoints(qtePoints);

            plat.setNom(nom.trim());
            plat.setRayonId(rayonId);
            plat.setDescription(description.trim());
            plat.setPrix(prix);
            plat.setCategorieId(categorieId);
            plat.setSousCategorieId(sousCategorieId);
            plat.setUtilisateurId(utilisateurId);

            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = sauvegarderFichier(request, filePart, "plats");
                plat.setImage(fileName);
            }

            plat.setUpdateDate(LocalDateTime.now());
            platDAO.modifierPlat(plat);

            request.getSession().setAttribute("ToastAdmSuccesNotif", "Plat modifié avec succès !");
        } catch (Exception e) {
        	  // Affiche l'erreur complète dans la console pour debug
            System.out.println("Erreur lors de la modification du plat : " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification du plat.");
        }
        response.sendRedirect("MenuServlet?action=liste-plat");
    }

//    private void supprimerPlat(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            
//            HttpSession session = request.getSession(false);
//            Integer utilisateurId = session != null && session.getAttribute("userId") != null
//                    ? (Integer) session.getAttribute("userId")
//                    : 0;
//            
//            platDAO.supprimerPlat(id, utilisateurId);
//            
//            request.getSession().setAttribute("ToastAdmSuccesNotif", "Plat supprimé avec succès !");
//        } catch (Exception e) {
//            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression du plat.");
//        }
//        response.sendRedirect("MenuServlet?action=liste-plat");
//    }
    private void supprimerPlat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            HttpSession session = request.getSession(false);
            Integer utilisateurId = (session != null && session.getAttribute("userId") != null)
                    ? (Integer) session.getAttribute("userId")
                    : null;

            if (utilisateurId == null) {
                // Aucun utilisateur connecté → pas de suppression
                request.getSession().setAttribute("ToastAdmErrorNotif", "Opération non possible (maintenance en cours).");
            } else {
                // Supprimer seulement si utilisateurId est valide
                platDAO.supprimerPlat(id, utilisateurId);
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Plat supprimé avec succès !");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression du plat.");
        }
        response.sendRedirect("MenuServlet?action=liste-plat");
    }


    // ===================== UPLOAD IMAGE ===========================
//    private String sauvegarderFichier(HttpServletRequest request, Part filePart, String dossier) throws IOException {
//        System.out.println("=====> DÉBUT sauvegarderFichier()");
//
//        String fileName = getFileName(filePart);
//        System.out.println("Nom de fichier reçu : " + fileName);
//
//        if (fileName == null || fileName.isEmpty()) {
//            System.out.println("Aucun fichier reçu, on quitte.");
//            return null;
//        }
//
//        String extension = "";
//        int i = fileName.lastIndexOf('.');
//        if (i > 0) extension = fileName.substring(i);
//        String uniqueFileName = UUID.randomUUID().toString() + extension;
//        System.out.println("Nom de fichier unique généré : " + uniqueFileName);
//
//        String uploadDirPath = dossier.equals("plats") ? UPLOAD_PLAT_DIR : UPLOAD_CATEGORIE_DIR;
//        File uploadDir = new File(uploadDirPath);
//        if (!uploadDir.exists()) {
//            boolean created = uploadDir.mkdirs();
//            System.out.println("Création du dossier ? " + created);
//        }
//
//        String fullPath = uploadDir + File.separator + uniqueFileName;
//        filePart.write(fullPath);
//        System.out.println(" Fichier enregistré avec succès !");
//
//        String cheminRelatif = "uploads/" + dossier + "/" + uniqueFileName;
//        return cheminRelatif;
//    }
    
    private String sauvegarderFichier(HttpServletRequest request, Part filePart, String dossier) throws IOException {
        System.out.println("=====> DÉBUT sauvegarderFichier()");

        String fileName = getFileName(filePart);
        System.out.println("Nom de fichier reçu : " + fileName);

        if (fileName == null || fileName.isEmpty()) {
            System.out.println("Aucun fichier reçu, on quitte.");
            return null;
        }

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) extension = fileName.substring(i);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        System.out.println("Nom de fichier unique généré : " + uniqueFileName);

        // Déterminer le répertoire d'upload selon le type
        String uploadDirPath;
        switch (dossier) {
            case "plats":
                uploadDirPath = UPLOAD_PLAT_DIR;
                break;
            case "categories":
                uploadDirPath = UPLOAD_CATEGORIE_DIR;
                break;
            case "rayons":
                uploadDirPath = UPLOAD_CATEGORIE_DIR; // ou créez un répertoire spécifique pour les rayons
                break;
            default:
                uploadDirPath = UPLOAD_CATEGORIE_DIR;
        }

        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("Création du dossier ? " + created);
        }

        String fullPath = uploadDir + File.separator + uniqueFileName;
        filePart.write(fullPath);
        System.out.println(" Fichier enregistré avec succès !");

        String cheminRelatif = "uploads/" + dossier + "/" + uniqueFileName;
        return cheminRelatif;
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
    
 // ===================== RAYONS ===========================

    private void afficherListeRayons(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Rayon> rayons = rayonDAO.getAll();
        request.setAttribute("rayons", rayons);
        request.getRequestDispatcher("/blok/list-rayon.jsp").forward(request, response);
    }

    private void afficherRayon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Rayon rayon = rayonDAO.getById(id);
        
        if (rayon == null) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Rayon non trouvé.");
            response.sendRedirect("MenuServlet?action=liste-rayons");
            return;
        }

        request.setAttribute("rayon", rayon);
        request.getRequestDispatcher("/blok/list-rayon.jsp").forward(request, response);
    }

    private void ajouterRayon(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String utilisateurIdStr = request.getParameter("utilisateurId");

            // Validation des champs obligatoires
            if (nom == null || nom.trim().isEmpty() || utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
                response.sendRedirect("MenuServlet?action=liste-rayons");
                return;
            }

            int utilisateurId = Integer.parseInt(utilisateurIdStr);

            // Récupérer l'image du rayon si présente
            String imageUrl = null;
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                imageUrl = sauvegarderFichier(request, filePart, "rayons");
            }

            Rayon rayon = new Rayon();
            rayon.setNom(nom.trim());
            rayon.setDescription(description != null ? description.trim() : null);
            rayon.setCreationDate(LocalDateTime.now());
            rayon.setUpdateDate(LocalDateTime.now());
            rayon.setUtilisateurId(utilisateurId);
            rayon.setStatut("VISIBLE");
            rayon.setImageUrl(imageUrl);

            rayonDAO.ajouter(rayon);
            request.getSession().setAttribute("ToastAdmSuccesNotif", "Rayon ajouté avec succès !");
            
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout du rayon : " + e.getMessage());
            e.printStackTrace();
        }
        
        response.sendRedirect("MenuServlet?action=liste-rayons");
    }

    private void modifierRayon(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String idStr = request.getParameter("id");
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String utilisateurIdStr = request.getParameter("utilisateurId");

            // Validation des champs obligatoires
            if (idStr == null || idStr.trim().isEmpty() || 
                nom == null || nom.trim().isEmpty() || 
                utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {
                
                request.getSession().setAttribute("ToastAdmErrorNotif", "Veuillez remplir tous les champs obligatoires.");
                response.sendRedirect("MenuServlet?action=liste-rayons");
                return;
            }

            int id = Integer.parseInt(idStr);
            int utilisateurId = Integer.parseInt(utilisateurIdStr);

            Rayon rayon = rayonDAO.getById(id);
            if (rayon == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Rayon non trouvé.");
                response.sendRedirect("MenuServlet?action=liste-rayons");
                return;
            }

            rayon.setNom(nom.trim());
            rayon.setDescription(description != null ? description.trim() : null);
            rayon.setUpdateDate(LocalDateTime.now());
            rayon.setUtilisateurId(utilisateurId);

            // Gérer l'image du rayon
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String imageUrl = sauvegarderFichier(request, filePart, "rayons");
                rayon.setImageUrl(imageUrl);
            }

            rayonDAO.modifier(rayon);
            request.getSession().setAttribute("ToastAdmSuccesNotif", "Rayon modifié avec succès !");
            
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification du rayon : " + e.getMessage());
            e.printStackTrace();
        }
        
        response.sendRedirect("MenuServlet?action=liste-rayons");
    }

    private void supprimerRayon(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String result = rayonDAO.supprimer(id);
            
            if (result.startsWith("SUCCESS:")) {
                String successMessage = result.substring(8);
                request.getSession().setAttribute("ToastAdmSuccesNotif", successMessage);
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", result);
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "ID de rayon invalide.");
        } catch (Exception e) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur inattendue : " + e.getMessage());
        }
        
        response.sendRedirect("MenuServlet?action=liste-rayons");
    }
    
 // Ajoutez cette méthode dans MenuServlet
    private void afficherCategoriesByRayonId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int rayonId = Integer.parseInt(request.getParameter("rayonId"));
            
            // Utilisez la méthode que nous avons créée dans MenuCategorieDAO
            List<MenuCategorie> categories = menuCategorieDAO.getCategoriesByRayonId(rayonId);
            
            // Créer une réponse JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("categories", new Gson().toJsonTree(categories));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur lors du chargement des catégories.\"}");
        }
    }
}
