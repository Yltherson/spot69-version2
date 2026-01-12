package com.spot69.controller;

import com.spot69.dao.TableRooftopDAO;
import com.spot69.model.TableRooftop;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@WebServlet({"/TableRooftopServlet", "/blok/TableRooftopServlet"})
public class TableRooftopServlet extends HttpServlet {

    private TableRooftopDAO tableDAO;

    @Override
    public void init() throws ServletException {
        tableDAO = new TableRooftopDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "liste";

        switch (action) {
            case "etat":
                listerTablesParEtat(request, response);
                break;
            case "modifier":
                afficherFormulaireModification(request, response);
                break;
            case "liste":
            default:
                listerTables(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) return;

        switch (action) {
            case "ajouter":
                ajouterTable(request, response);
                break;
            case "modifier":
                modifierTable(request, response);
                break;
            case "supprimer":
                supprimerTable(request, response);
                break;
        }
    }

    // Méthodes utilitaires

//    private void ajouterTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        TableRooftop table = new TableRooftop();
//        table.setNumeroTable(Integer.parseInt(request.getParameter("numeroTable")));
//        table.setEtatActuel(request.getParameter("etatActuel"));
//        table.setPlafond(new BigDecimal(request.getParameter("plafond")));
//        table.setStatut("VISIBLE");
//
//        tableDAO.ajouterTable(table);
//        response.sendRedirect("TableRooftopServlet?action=liste");
//    }

    private void ajouterTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            TableRooftop table = new TableRooftop();

            // Gestion de numeroTable
            String numeroTableParam = request.getParameter("numeroTable");
            if (numeroTableParam != null && !numeroTableParam.trim().isEmpty()) {
                table.setNumeroTable(Integer.parseInt(numeroTableParam));
            } else {
                table.setNumeroTable(0); // Si ton modèle accepte null, sinon mettre 0
            }

            // Gestion de etatActuel
            String etatActuelParam = request.getParameter("etatActuel");
            table.setEtatActuel((etatActuelParam != null && !etatActuelParam.trim().isEmpty()) ? etatActuelParam : null);

            // Gestion de plafond (obligatoire, mais on vérifie quand même)
            String plafondParam = request.getParameter("plafond");
            if (plafondParam != null && !plafondParam.trim().isEmpty()) {
                table.setPlafond(new BigDecimal(plafondParam));
            } else {
                table.setPlafond(BigDecimal.ZERO); // Ou null si autorisé
            }

            table.setStatut("VISIBLE");

            tableDAO.ajouterTable(table);
            response.sendRedirect("TableRooftopServlet?action=liste");

        } catch (Exception e) {
            // Stocker le message d'erreur dans la session
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            response.sendRedirect("TableRooftopServlet?action=liste");
        }
    }

    
    private void modifierTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TableRooftop table = new TableRooftop();
        table.setId(Integer.parseInt(request.getParameter("id")));
        table.setNumeroTable(Integer.parseInt(request.getParameter("numeroTable")));
        table.setEtatActuel(request.getParameter("etatActuel"));
        table.setPlafond(new BigDecimal(request.getParameter("plafond")));
        table.setStatut("VISIBLE");

        tableDAO.modifierTable(table);
       
        response.sendRedirect("TableRooftopServlet?action=liste");
    }

    private void supprimerTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int deletedBy = Integer.parseInt(request.getParameter("deletedBy"));
        tableDAO.supprimerTable(id, deletedBy);
        response.sendRedirect("TableRooftopServlet?action=liste");
    }

    private void listerTables(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<TableRooftop> tables = tableDAO.listerTables();
        request.setAttribute("tables", tables);
        request.getRequestDispatcher("liste-tables.jsp").forward(request, response);
    }

    private void listerTablesParEtat(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String etat = request.getParameter("etat");
        List<TableRooftop> tables = tableDAO.listerTablesParEtat(etat);
        request.setAttribute("tables", tables);
        request.getRequestDispatcher("liste-tables.jsp").forward(request, response);
    }

    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        TableRooftop table = tableDAO.chercherParId(id);
        request.setAttribute("table", table);
     
        List<TableRooftop> tables = tableDAO.listerTables();
        request.setAttribute("tables", tables);
        request.getRequestDispatcher("liste-tables.jsp").forward(request, response);
    }
}
