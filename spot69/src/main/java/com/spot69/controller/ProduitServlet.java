package com.spot69.controller;

import javax.servlet.annotation.MultipartConfig;

import com.spot69.dao.InventaireCategorieDAO;
import com.spot69.dao.MenuCategorieDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.dao.RayonDAO;
import com.spot69.model.InventaireCategorie;
import com.spot69.model.MenuCategorie;
import com.spot69.model.Produit;
import com.spot69.model.Rayon;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,    // 2MB en mémoire avant écriture temporaire
    maxFileSize = 1024 * 1024 * 10,         // 10MB max par fichier
    maxRequestSize = 1024 * 1024 * 50       // 50MB pour l'ensemble de la requête
)
@WebServlet({"/ProduitServlet", "/blok/ProduitServlet"})
public class ProduitServlet extends HttpServlet {

    private ProduitDAO produitDAO;
    private InventaireCategorieDAO inventaireCategorieDAO;
    private final MenuCategorieDAO menuCategorieDAO = new MenuCategorieDAO();

    private static String UPLOAD_PRODUIT_DIR = "";

    @Override
    public void init() throws ServletException {
    	UPLOAD_PRODUIT_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "produits";
//    	production
        ServletContext context = getServletContext();
//        UPLOAD_PRODUIT_DIR = context.getInitParameter("PROD_PRODUIT_UPLOAD_ROOT_DIR");

        produitDAO = new ProduitDAO();
//        inventaireCategorieDAO = new InventaireCategorieDAO();
    }

    private void setupEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace(); // UTF-8 doit être supporté toujours
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("index.jsp");
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
                handleSupprimer(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("liste-produit.jsp");
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
            default:
                response.sendRedirect("liste-produit.jsp");
                break;
        }
    }

    // ======= Handlers POST =======

    private void handleAjouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Produit produit = extraireProduitDepuisRequest(request);

            String codeGenere = genererCodeProduit(produit.getCategorieId(), produit.getSousCategorieId());
            produit.setCodeProduit(codeGenere);

            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String imageUrl = sauvegarderFichier(request, filePart, "produits");
                produit.setImageUrl(imageUrl);
            }else {
               produit.setImageUrl("uploads/default/default.png");
            }
            

            boolean success = produitDAO.ajouterProduit(produit);
            if (success) {
                setSuccessNotif(request, "Produit ajouté avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de l'ajout du produit.");
            }
            response.sendRedirect("ProduitServlet?action=add");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ProduitServlet?action=add");
        }
    }

    private void handleModifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Produit produit = produitDAO.chercherParId(id);

            if (produit == null) {
                setErrorNotif(request, "Produit introuvable.");
                response.sendRedirect("ProduitServlet?action=lister");
                return;
            }

            Produit updated = extraireProduitDepuisRequest(request);
            String old_image = produit.getImageUrl();
            updated.setId(id);

            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String imageUrl = sauvegarderFichier(request, filePart, "produits");
                updated.setImageUrl(imageUrl);
            }else{
            	updated.setImageUrl(old_image);
            }

            boolean success = produitDAO.modifierProduit(updated);
            if (success) {
                setSuccessNotif(request, "Produit modifié avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la modification du produit.");
            }
            response.sendRedirect("ProduitServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ProduitServlet?action=lister");
        }
    }

    private void handleSupprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            HttpSession session = request.getSession(false);
            Integer deletedBy = session != null && session.getAttribute("userId") != null
                    ? (Integer) session.getAttribute("userId")
                    : 0;

            boolean success = produitDAO.supprimerProduit(id, deletedBy);
            if (success) {
                setSuccessNotif(request, "Produit supprimé avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la suppression du produit.");
            }
            response.sendRedirect("ProduitServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    // ======= Handlers GET =======

//    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        InventaireCategorieDAO catDao = new InventaireCategorieDAO();
//        List<InventaireCategorie> categoriesAvecSous = catDao.getAllCategoriesWithSousCategories();
//
//        request.setAttribute("categoriesAvecSous", categoriesAvecSous);
//        request.getRequestDispatcher("ajout-produit.jsp").forward(request, response);
//    }
    
//    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        MenuCategorieDAO menuCatDao = new MenuCategorieDAO();
//        List<MenuCategorie> menuCategories = menuCatDao.getAll();
//        request.setAttribute("menuCategories", menuCategories);
//        request.getRequestDispatcher("ajout-produit.jsp").forward(request, response);
//    }
    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer la liste des rayons
        RayonDAO rayonDAO = new RayonDAO();
        List<Rayon> rayons = rayonDAO.getAll();
        request.setAttribute("rayons", rayons);
        
        request.getRequestDispatcher("ajout-produit.jsp").forward(request, response);
    }

//    List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
//    request.setAttribute("menuCategories", menuCategories);
//    

//    private void handleEditPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String idStr = request.getParameter("id");
//        if (idStr != null) {
//            try {
//                int id = Integer.parseInt(idStr);
//                Produit produit = produitDAO.chercherParId(id);
//                if (produit != null) {
//                    request.setAttribute("produit", produit);
//                    InventaireCategorieDAO catDao = new InventaireCategorieDAO();
////                    List<InventaireCategorie> categoriesAvecSous = catDao.getAllCategoriesWithSousCategories();
////                    request.setAttribute("categoriesAvecSous", categoriesAvecSous);
//                    List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
//                    request.setAttribute("menuCategories", menuCategories);
//                    request.getRequestDispatcher("liste-produit.jsp").forward(request, response);
//                } else {
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit introuvable");
//                }
//            } catch (NumberFormatException e) {
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
//            }
//        } else {
//            response.sendRedirect("liste-produit.jsp");
//        }
//    }
    private void handleEditPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Produit produit = produitDAO.chercherParId(id);
                if (produit != null) {
                    request.setAttribute("produit", produit);
                    
                    // Récupérer la liste des rayons
                    RayonDAO rayonDAO = new RayonDAO();
                    List<Rayon> rayons = rayonDAO.getAll();
                    request.setAttribute("rayons", rayons);
                    
                    // Récupérer le rayon actuel du produit (via la catégorie)
                    Rayon rayonActuel = null;
                    if (produit.getCategorie() != null && produit.getCategorie().getRayon() != null) {
                        rayonActuel = produit.getCategorie().getRayon();
                        request.setAttribute("rayonActuel", rayonActuel);
                    }
                    
                    request.getRequestDispatcher("liste-produit.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit introuvable");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("liste-produit.jsp");
        }
    }

    private void handleLister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Produit> produits = produitDAO.listerProduits();
        request.setAttribute("produits", produits);

//        InventaireCategorieDAO catDao = new InventaireCategorieDAO();
//        List<InventaireCategorie> categoriesAvecSous = catDao.getAllCategoriesWithSousCategories();
        List<MenuCategorie> menuCategories = menuCategorieDAO.getAll();
        request.setAttribute("menuCategories", menuCategories);

        int nbProduitsEnRupture = produitDAO.compterProduitsEnRupture();
        int qteTotaleProduits = produitDAO.compterQuantiteTotaleProduits();
        BigDecimal grandTotal = produitDAO.recupererGrandTotal();

        request.setAttribute("nbProduitsEnRupture", nbProduitsEnRupture);
        request.setAttribute("qteTotaleProduits", qteTotaleProduits);
        request.setAttribute("GrandTotal", grandTotal);

//        request.setAttribute("categoriesAvecSous", categoriesAvecSous);
        request.getRequestDispatcher("liste-produit.jsp").forward(request, response);
    }

    // ======= Méthodes utilitaires =======

//    OLD SANS RAYON
//    private Produit extraireProduitDepuisRequest(HttpServletRequest request) {
//        Produit produit = new Produit();
//
//        String nom = request.getParameter("nom");
//        if (nom == null || nom.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'nom' est requis.");
//        }
//        produit.setNom(nom);
//        
//        String qtePointsStr = request.getParameter("qtePoints");
//        int qtePoints = Integer.parseInt(qtePointsStr);
//        produit.setQtePoints(qtePoints);
//
//        String description = request.getParameter("description");
//        produit.setDescription(description);
//
//        String emplacement = request.getParameter("emplacement");
//        if (emplacement == null || emplacement.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'emplacement' est requis.");
//        }
//        produit.setEmplacement(emplacement);
//
//        String categorieIdStr = request.getParameter("categorieId");
//        if (categorieIdStr == null || categorieIdStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'categorieId' est requis.");
//        }
//        produit.setCategorieId(Integer.parseInt(categorieIdStr));
//
//        String sousCategorieIdStr = request.getParameter("sousCategorieId");
//        if (sousCategorieIdStr == null || sousCategorieIdStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'sousCategorieId' est requis.");
//        }
//        produit.setSousCategorieId(Integer.parseInt(sousCategorieIdStr));
//        
//        String prixAchatParUniteVenteStr = request.getParameter("prixAchatParUniteVente");
//        if (prixAchatParUniteVenteStr == null || prixAchatParUniteVenteStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'prixAchatParUniteVente' est requis.");
//        }
//        produit.setPrixAchatParUniteVente(Integer.parseInt(prixAchatParUniteVenteStr));
//
//        String uniteVente = request.getParameter("uniteVente");
//        if (uniteVente == null || uniteVente.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'uniteVente' est requis.");
//        }
//        produit.setUniteVente(uniteVente);
//
//        String contenuParUnite = request.getParameter("contenuParUnite");
//        if (contenuParUnite == null || contenuParUnite.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'contenuParUnite' est requis.");
//        }
//        produit.setContenuParUnite(contenuParUnite);
//
//        String seuilAlerteStr = request.getParameter("seuilAlerte");
//        if (seuilAlerteStr == null || seuilAlerteStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'seuilAlerte' est requis.");
//        }
//        produit.setSeuilAlerte(Integer.parseInt(seuilAlerteStr));
//
//        String prixVenteStr = request.getParameter("prixVente");
//        if (prixVenteStr == null || prixVenteStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le champ 'prixVente' est requis.");
//        }
//        try {
//            produit.setPrixVente(new BigDecimal(prixVenteStr));
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Le champ 'prixVente' doit être un nombre valide.");
//        }
//
//        HttpSession session = request.getSession(false);
//        if (session != null && session.getAttribute("userId") != null) {
//            produit.setUtilisateurId((int) session.getAttribute("userId"));
//        }
//
//        produit.setCreatedAt(new Date());
//        produit.setUpdatedAt(new Date());
//
//        return produit;
//    }
    
    private Produit extraireProduitDepuisRequest(HttpServletRequest request) {
        Produit produit = new Produit();

        String nom = request.getParameter("nom");
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'nom' est requis.");
        }
        produit.setNom(nom);
        
        String qtePointsStr = request.getParameter("qtePoints");
        int qtePoints = 0; // Valeur par défaut

        if (qtePointsStr != null && !qtePointsStr.trim().isEmpty()) {
            try {
                qtePoints = Integer.parseInt(qtePointsStr.trim());
            } catch (NumberFormatException e) {
                qtePoints = 0; // Si la valeur n'est pas un nombre valide
                // Optionnel : logger l'erreur ou afficher un message
                // e.printStackTrace();
            }
        } else {
            qtePoints = 0; // Si null ou vide
        }

        produit.setQtePoints(qtePoints);

        String description = request.getParameter("description");
        produit.setDescription(description);

        String emplacement = request.getParameter("emplacement");
        if (emplacement == null || emplacement.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'emplacement' est requis.");
        }
        produit.setEmplacement(emplacement);

        // AJOUTER: Récupération du rayonId
        String rayonIdStr = request.getParameter("rayonId");
        if (rayonIdStr == null || rayonIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'rayonId' est requis.");
        }
        produit.setRayonId(Integer.parseInt(rayonIdStr));

        String categorieIdStr = request.getParameter("categorieId");
        if (categorieIdStr == null || categorieIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'categorieId' est requis.");
        }
        produit.setCategorieId(Integer.parseInt(categorieIdStr));

        String sousCategorieIdStr = request.getParameter("sousCategorieId");
        if (sousCategorieIdStr == null || sousCategorieIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'sousCategorieId' est requis.");
        }
        produit.setSousCategorieId(Integer.parseInt(sousCategorieIdStr));
        
        String prixAchatParUniteVenteStr = request.getParameter("prixAchatParUniteVente");
        if (prixAchatParUniteVenteStr == null || prixAchatParUniteVenteStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'prixAchatParUniteVente' est requis.");
        }
        produit.setPrixAchatParUniteVente(Integer.parseInt(prixAchatParUniteVenteStr));

        String uniteVente = request.getParameter("uniteVente");
        if (uniteVente == null || uniteVente.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'uniteVente' est requis.");
        }
        produit.setUniteVente(uniteVente);

        String contenuParUnite = request.getParameter("contenuParUnite");
        if (contenuParUnite == null || contenuParUnite.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'contenuParUnite' est requis.");
        }
        produit.setContenuParUnite(contenuParUnite);

        String seuilAlerteStr = request.getParameter("seuilAlerte");
        if (seuilAlerteStr == null || seuilAlerteStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'seuilAlerte' est requis.");
        }
        produit.setSeuilAlerte(Integer.parseInt(seuilAlerteStr));

        String prixVenteStr = request.getParameter("prixVente");
        if (prixVenteStr == null || prixVenteStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'prixVente' est requis.");
        }
        try {
            produit.setPrixVente(new BigDecimal(prixVenteStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le champ 'prixVente' doit être un nombre valide.");
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            produit.setUtilisateurId((int) session.getAttribute("userId"));
        }

        produit.setCreatedAt(new Date());
        produit.setUpdatedAt(new Date());

        return produit;
    }

    private void setSuccessNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmErrorNotif", message);
        request.getSession().setAttribute("toastType", "error");
    }

    private String genererCodeProduit(int categorieId, int sousCategorieId) {
        Random random = new Random();
        String codeProduit;
        String prefix;
        
        MenuCategorieDAO menuCategorieDAO = new MenuCategorieDAO();

        MenuCategorie categorie = menuCategorieDAO.getById(categorieId);
        MenuCategorie sousCategorie = menuCategorieDAO.getById(sousCategorieId);

        if (categorie == null || sousCategorie == null) {
            throw new IllegalArgumentException("Catégorie ou sous-catégorie introuvable");
        }

        String lettreCat = safeFirstLetter(categorie.getNom());
        String lettreSousCat = safeFirstLetter(sousCategorie.getNom());
        prefix = lettreCat + lettreSousCat;

        do {
            long numero = 1000L + random.nextInt(9999999);
            codeProduit = prefix + "-" + numero;
        } while (produitDAO.existeCodeProduit(codeProduit));

        return codeProduit;
    }

    private String safeFirstLetter(String texte) {
        return (texte != null && !texte.isEmpty()) ? texte.substring(0, 1).toUpperCase() : "X";
    }

    // Upload image

    private String sauvegarderFichier(HttpServletRequest request, Part filePart, String dossier) throws IOException {
        String fileName = getFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) extension = fileName.substring(i);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(UPLOAD_PRODUIT_DIR);
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
}
