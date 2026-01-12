package com.spot69.controller;

import com.spot69.dao.FournisseurDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.model.Fournisseur;
import com.spot69.model.Produit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet({"/InventaireServlet", "/blok/InventaireServlet"})
public class InventaireServlet extends HttpServlet {

    private ProduitDAO produitDAO;
    private FournisseurDAO fournisseurDAO;

    @Override
    public void init() throws ServletException {
        produitDAO = new ProduitDAO();
        fournisseurDAO = new FournisseurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":
                afficherFormulaireAjout(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer la liste des produits
        List<Produit> produits = produitDAO.listerProduits();
        request.setAttribute("produits", produits);

        // Récupérer la liste des fournisseurs visibles
        List<Fournisseur> fournisseurs = fournisseurDAO.findAllVisible();
        request.setAttribute("fournisseurs", fournisseurs);

        // Forward vers la JSP
        request.getRequestDispatcher("reaprovisionner.jsp").forward(request, response);
    }
}
