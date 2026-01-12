package com.spot69.controller;

 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spot69.dao.InventaireCategorieDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.dao.InventaireCategorieDAO;
import com.spot69.dao.MouvementStockDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.model.FactureDetail;
import com.spot69.model.InventaireCategorie;
import com.spot69.model.MouvementStock;
import com.spot69.model.Produit;

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

/**
 * Servlet implementation class MouvementStockServlet
 */
@WebServlet({"/MouvementStockServlet", "/blok/MouvementStockServlet"})
public class MouvementStockServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ProduitDAO produitDAO;
	private MouvementStockDAO mouvementStockDAO;
    private InventaireCategorieDAO inventaireCategorieDAO;

    // Chemins d'upload pour plats et catégories
    private static  String UPLOAD_PRODUIT_DIR ="";
    

    @Override
    public void init() throws ServletException {
        // Local
    	UPLOAD_PRODUIT_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "produits";
//    	production
//        ServletContext context = getServletContext();
//      UPLOAD_PRODUIT_DIR = context.getInitParameter("PROD_PRODUIT_UPLOAD_ROOT_DIR");
    
        produitDAO = new ProduitDAO();
        mouvementStockDAO = new MouvementStockDAO();
        inventaireCategorieDAO = new InventaireCategorieDAO();
    }
       
 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("liste-produit.jsp");
            return;
        }

        switch (action) {
            case "add":
                afficherFormulaireAjoutProduit(request, response);
                break;

            case "get-mouvement-produit":
                afficherMouvementsParProduit(request, response);
                break;

            case "lister":
                afficherListeProduitsAvecStatistiques(request, response);
                break;

            default:
                response.sendRedirect("liste-produit.jsp");
        }
    }

    private void afficherFormulaireAjoutProduit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<InventaireCategorie> categoriesAvecSous = inventaireCategorieDAO.getAllCategoriesWithSousCategories();
        request.setAttribute("categoriesAvecSous", categoriesAvecSous);
        request.getRequestDispatcher("ajout-produit.jsp").forward(request, response);
    }

    private void afficherMouvementsParProduit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("liste-produit.jsp");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            List<MouvementStock> mouvements = mouvementStockDAO.mouvementStockParProduit(id);
            if (mouvements != null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String json = gson.toJson(mouvements);
                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit introuvable");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void afficherListeProduitsAvecStatistiques(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Produit> produits = produitDAO.listerProduits();
        List<InventaireCategorie> categoriesAvecSous = inventaireCategorieDAO.getAllCategoriesWithSousCategories();

        int nbProduitsEnRupture = produitDAO.compterProduitsEnRupture();
        int qteTotaleProduits = produitDAO.compterQuantiteTotaleProduits();
        BigDecimal grandTotal = produitDAO.recupererGrandTotal();

        request.setAttribute("produits", produits);
        request.setAttribute("categoriesAvecSous", categoriesAvecSous);
        request.setAttribute("nbProduitsEnRupture", nbProduitsEnRupture);
        request.setAttribute("qteTotaleProduits", qteTotaleProduits);
        request.setAttribute("GrandTotal", grandTotal);

        request.getRequestDispatcher("mouvement-stock.jsp").forward(request, response);
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "ajuster-stock":
                traiterAjustementStock(request, response);
                break;
            // ... autres cas existants
        }
    }
    private void traiterAjustementStock(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        
        try {
            int produitId = Integer.parseInt(request.getParameter("produitId"));
            int quantite = Integer.parseInt(request.getParameter("quantite"));
            String raison = request.getParameter("raison");
            
            // Récupérer l'ID utilisateur de la session
            Integer userId = (Integer) session.getAttribute("userId");
            
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            MouvementStockDAO mouvementDAO = new MouvementStockDAO();
            boolean success = mouvementDAO.creerAjustement(produitId, quantite, userId, raison);
            
            if (success) {
                session.setAttribute("ToastAdmSuccesNotif", "Ajustement de stock effectué avec succès.");
                session.setAttribute("toastType", "success");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajustement du stock.");
                session.setAttribute("toastType", "error");
            }
            
            response.sendRedirect("MouvementStockServlet?action=lister");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            session.setAttribute("toastType", "error");
            response.sendRedirect("MouvementStockServlet?action=lister");
        }
    }
	
	// ===================== UPLOAD IMAGE ===========================
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

        String uploadDirPath =  UPLOAD_PRODUIT_DIR;
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

}
