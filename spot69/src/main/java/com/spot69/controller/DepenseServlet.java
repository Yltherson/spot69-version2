package com.spot69.controller;

import com.spot69.dao.DepenseDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Depense;
import com.spot69.model.DepenseType;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;

@WebServlet({"/DepenseServlet", "/blok/DepenseServlet"})
public class DepenseServlet extends HttpServlet {

    private DepenseDAO depenseDAO;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        depenseDAO = new DepenseDAO();
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
            case "ajouter":
                ajouter(request, response);
                break;
            case "modifier":
                modifier(request, response);
                break;
            case "supprimer":
                supprimer(request, response);
                break;
            case "addType":   // <-- nouveau pour ajouter un type de dépense
                ajouterType(request, response);
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
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("liste-depenses.jsp");
            return;
        }

        switch (action) {
            case "add":
                handleAfficherFormulaireAjout(request, response);
                break;
            case "lister-type":
                listerTypes(request, response);
                break;

            case "edit":
                handleAfficherFormulaireModification(request, response);
                break;

            case "lister":
                handleLister(request, response);
                break;
                
            case "deleteType":
                supprimerType(request, response);
                break;


            default:
                response.sendRedirect("liste-depenses.jsp");
                break;
        }
    }

    /* ==============================
       📌 MÉTHODES PRIVÉES
       ============================== */

    private void ajouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // === DEBUG : afficher tous les paramètres de la requête ===
            System.out.println("=== Paramètres reçus dans la requête ===");
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println(paramName + " = " + paramValue);
            }
            System.out.println("=======================================");

            int idType = Integer.parseInt(request.getParameter("idType"));
            int caissiereId = Integer.parseInt(request.getParameter("caissiereId"));
            int montant = Integer.parseInt(request.getParameter("montant"));
            String notes = request.getParameter("notes");

            String dateStr = request.getParameter("dateDepense");
            Date date = null;
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                date = Date.valueOf(dateStr); // format attendu : "yyyy-MM-dd"
            } else {
                // Optionnel : définir la date à aujourd'hui si non fournie
                date = new Date(System.currentTimeMillis());
            }

            // Récupérer l'utilisateur connecté depuis la session
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            System.out.println("userId");
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            Depense d = new Depense();
            d.setIdType(idType);
            d.setMontant(montant);
            d.setNotes(notes != null ? notes.trim() : null);
            d.setDate(date);
            d.setUserId(userId);
            d.setCaissiereId(caissiereId);

            int newId = depenseDAO.ajouterDepense(d);

            if (newId > 0) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Dépense ajoutée avec succès.");
                request.getSession().setAttribute("toastType", "success");
                response.sendRedirect("DepenseServlet?action=lister");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la dépense.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("DepenseServlet?action=lister");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("DepenseServlet?action=lister");
        }
    }


    private void modifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            List<Depense> depenses = depenseDAO.getAllDepenses();
            Depense d = depenses.stream().filter(dep -> dep.getId() == id).findFirst().orElse(null);

            if (d == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Dépense introuvable.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("DepenseServlet?action=lister");
                return;
            }

            int idType = Integer.parseInt(request.getParameter("idType"));
            int montant = Integer.parseInt(request.getParameter("montant"));
            String notes = request.getParameter("notes");
            Date date = Date.valueOf(request.getParameter("date"));
            int userId = Integer.parseInt(request.getParameter("userId"));

            d.setIdType(idType);
            d.setMontant(montant);
            d.setNotes(notes != null ? notes.trim() : null);
            d.setDate(date);
            d.setUserId(userId);

            // pas d'update direct dans ton DAO -> on pourrait en ajouter si tu veux
            request.getSession().setAttribute("ToastAdmSuccesNotif", "Dépense modifiée (simulé).");
            request.getSession().setAttribute("toastType", "success");
            response.sendRedirect("DepenseServlet?action=lister");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("DepenseServlet?action=lister");
        }
    }

    private void supprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            boolean success = depenseDAO.supprimerDepense(id);

            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Dépense supprimée avec succès.");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression.");
                request.getSession().setAttribute("toastType", "error");
            }
            response.sendRedirect("DepenseServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void handleAfficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<DepenseType> types = depenseDAO.getAllTypes();
        request.setAttribute("types", types);
        request.getRequestDispatcher("ajout-depense.jsp").forward(request, response);
    }

    private void handleAfficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Depense> depenses = depenseDAO.getAllDepenses();
                Depense d = depenses.stream().filter(dep -> dep.getId() == id).findFirst().orElse(null);
                if (d != null) {
                    request.setAttribute("depense", d);
                    request.setAttribute("types", depenseDAO.getAllTypes());
                    request.getRequestDispatcher("edit-depense.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Dépense introuvable");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("liste-depenses.jsp");
        }
    }
    
    private void ajouterType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String description = request.getParameter("description");
            if (description == null || description.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Le nom du type est obligatoire.");
                response.sendRedirect("DepenseServlet?action=lister");
                return;
            }

            DepenseType type = new DepenseType();
            type.setDescription(description.trim());
            int newId = depenseDAO.ajouterType(type);

            if (newId > 0) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Type de dépense ajouté avec succès.");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout du type.");
                request.getSession().setAttribute("toastType", "error");
            }

            response.sendRedirect("DepenseServlet?action=lister-type");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("DepenseServlet?action=lister");
        }
    }
    
    private void supprimerType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String result = depenseDAO.supprimerType(id);

            if ("SUCCESS".equals(result)) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Type supprimé avec succès.");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", result);
                request.getSession().setAttribute("toastType", "error");
            }

            response.sendRedirect("DepenseServlet?action=lister-type");
        } catch(Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("DepenseServlet?action=lister-type");
        }
    }

    private void listerTypes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<DepenseType> types = depenseDAO.getAllTypes();
        request.setAttribute("types", types);
        request.getRequestDispatcher("liste-type.jsp").forward(request, response);
    }



    private void handleLister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            String userIdStr = request.getParameter("userId");
            String typeIdStr = request.getParameter("typeId");

//            Date dateDebut = (dateDebutStr != null && !dateDebutStr.isEmpty()) ? Date.valueOf(dateDebutStr) : null;
//            Date dateFin = (dateFinStr != null && !dateFinStr.isEmpty()) ? Date.valueOf(dateFinStr) : null;
            Date dateDebut = null;
            		Date dateFin = null;
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                LocalDate fin = LocalDate.parse(dateFinStr).plusDays(1); // inclut toute la journée
                dateFin = Date.valueOf(fin);
            }

            Integer userId = (userIdStr != null && !userIdStr.isEmpty()) ? Integer.parseInt(userIdStr) : null;
            Integer typeId = (typeIdStr != null && !typeIdStr.isEmpty()) ? Integer.parseInt(typeIdStr) : null;

            List<Depense> depenses = depenseDAO.getDepensesFiltrees(dateDebut, dateFin, userId, typeId);
            List<DepenseType> types = depenseDAO.getAllTypes();  // pour le filtre et le modal
            List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE)"); // méthode à créer dans DAO
            request.setAttribute("caissieres", caissieres);
            request.setAttribute("depenses", depenses);
            request.setAttribute("types", types);

            // 🔹 Ajouter les valeurs sélectionnées pour la JSP
            request.setAttribute("selectedTypeId", typeIdStr);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);
            request.setAttribute("selectedUserId", userIdStr);

            request.getRequestDispatcher("liste-depenses.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur technique: " + e.getMessage());
        }
    }



}
