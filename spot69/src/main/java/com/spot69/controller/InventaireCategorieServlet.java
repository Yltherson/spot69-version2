package com.spot69.controller;

import com.google.gson.Gson;
import com.spot69.dao.InventaireCategorieDAO;
import com.spot69.model.InventaireCategorie;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet({"/InventaireCategorieServlet", "/blok/InventaireCategorieServlet"})
public class InventaireCategorieServlet extends HttpServlet {

    private InventaireCategorieDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new InventaireCategorieDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "ajouter":
                ajouter(request, response);
                break;
            case "modifier":
                modifier(request, response);
                break;
            case "supprimer":
                supprimer(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("index");
            return;
        }

        if ("sous-categories-json".equalsIgnoreCase(action)) {
            handleSousCategoriesJson(request, response);
            return;
        }

        switch (action) {
            case "lister-cat":
                handleListerCategories(request, response);
                break;

            case "lister-scat":
                handleListerSousCategories(request, response);
                break;

            case "voirSousCategorie":
                handleVoirSousCategorie(request, response);
                break;

            case "edit":
                handleEditCategorie(request, response);
                break;

            default:
                response.sendRedirect("index.jsp");
        }
    }

    private void ajouter(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String type = request.getParameter("type");
        try {
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String statut = "VISIBLE";

            InventaireCategorie c = new InventaireCategorie();
            c.setNom(nom);
            c.setDescription(description);
            c.setType(type);
            c.setStatut(statut);

            if ("SOUS_CATEGORIE".equalsIgnoreCase(type)) {
                int categorieId = Integer.parseInt(request.getParameter("categorieId"));
                c.setCategorieId(categorieId);
            }

            boolean success = dao.insert(c);
            setNotification(request, success, "Ajout");

            redirectByType(response, type);

        } catch (Exception e) {
            e.printStackTrace();
            setError(request, "Erreur technique: " + e.getMessage());
            redirectByType(response, type);
        }
    }

    private void modifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");

            InventaireCategorie c = dao.findById(id, type);
            if (c == null) {
                setError(request, "Catégorie introuvable.");
                redirectByType(response, type);
                return;
            }

            c.setNom(request.getParameter("nom"));
            c.setDescription(request.getParameter("description"));
            c.setType(type);
            c.setStatut("VISIBLE");

            if ("SOUS_CATEGORIE".equalsIgnoreCase(type)) {
                c.setCategorieId(Integer.parseInt(request.getParameter("categorieId")));
            }

            boolean success = dao.update(c);
            setNotification(request, success, "Modification");

            redirectByType(response, type);

        } catch (Exception e) {
            e.printStackTrace();
            setError(request, "Erreur: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void supprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");

            HttpSession session = request.getSession(false);
            int deletedBy = session != null && session.getAttribute("userId") != null
                    ? (Integer) session.getAttribute("userId") : 0;

            boolean success = "CATEGORIE".equalsIgnoreCase(type)
                    ? dao.supprimerCategorieEtSousCategories(id, deletedBy)
                    : dao.deleteSousCategorie(id, deletedBy);

            setNotification(request, success, "Suppression");

            redirectByType(response, type);

        } catch (Exception e) {
            e.printStackTrace();
            setError(request, "Erreur suppression: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleSousCategoriesJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String parentIdStr = request.getParameter("parentId");
        if (parentIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre parentId manquant");
            return;
        }
        try {
            int parentId = Integer.parseInt(parentIdStr);
            List<InventaireCategorie> sousCats = dao.getSousCategoriesByCategorie(parentId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            class ResponseJson {
                List<InventaireCategorie> sousCategories;
                ResponseJson(List<InventaireCategorie> sousCategories) {
                    this.sousCategories = sousCategories;
                }
            }

            String json = new Gson().toJson(new ResponseJson(sousCats));
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "parentId invalide");
        }
    }

   

    private void handleListerCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<InventaireCategorie> categories = dao.findAllVisibleCategorie();
        request.setAttribute("categories-inventaire", categories);
        request.getRequestDispatcher("liste-categorie-inventaire.jsp").forward(request, response);
    }

    private void handleListerSousCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<InventaireCategorie> categories = dao.findAllVisibleCategorie();
        request.setAttribute("categories-inventaire", categories);
        List<InventaireCategorie> sousCategories = dao.findAllVisibleSousCategorie();
        request.setAttribute("sousCategories-inventaire", sousCategories);
        request.getRequestDispatcher("liste-sous-categorie-inventaire.jsp").forward(request, response);
    }

    private void handleVoirSousCategorie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int categorieId = Integer.parseInt(request.getParameter("categorieId"));
            List<InventaireCategorie> sousCats = dao.getSousCategoriesByCategorie(categorieId);
            request.setAttribute("sousCategories-invcentaire", sousCats);
            request.getRequestDispatcher("sous-categorie.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void handleEditCategorie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");

            InventaireCategorie c = dao.findById(id, type);
            if (c != null) {
                if ("SOUS_CATEGORIE".equalsIgnoreCase(type)) {
                    request.setAttribute("sous-categorie-inventaire", c);
                    List<InventaireCategorie> categories = dao.findAllVisibleCategorie();
                    request.setAttribute("categories-inventaire", categories);
                    List<InventaireCategorie> sousCategories = dao.findAllVisibleSousCategorie();
                    request.setAttribute("sousCategories-inventaire", sousCategories);
                    request.getRequestDispatcher("liste-sous-categorie-inventaire.jsp").forward(request, response);
                } else {
                    request.setAttribute("categorie-inventaire", c);
                    List<InventaireCategorie> parents = dao.findAllVisibleCategorie();
                    request.setAttribute("categories-inventaire", parents);
                    request.getRequestDispatcher("liste-categorie-inventaire.jsp").forward(request, response);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catégorie introuvable");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }


    private void redirectByType(HttpServletResponse response, String type) throws IOException {
        if ("SOUS_CATEGORIE".equalsIgnoreCase(type)) {
            response.sendRedirect("InventaireCategorieServlet?action=lister-scat");
        } else {
            response.sendRedirect("InventaireCategorieServlet?action=lister-cat");
        }
    }

    private void setNotification(HttpServletRequest request, boolean success, String action) {
        String message = success ? action + " réussie." : "Échec de la " + action.toLowerCase();
        String type = success ? "success" : "error";
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", type);
    }

    private void setError(HttpServletRequest request, String msg) {
        request.getSession().setAttribute("ToastAdmErrorNotif", msg);
        request.getSession().setAttribute("toastType", "error");
    }
}
