package com.spot69.controller;

import com.spot69.dao.FournisseurDAO;
import com.spot69.model.Fournisseur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet({"/FournisseurServlet", "/blok/FournisseurServlet"})
public class FournisseurServlet extends HttpServlet {

    private FournisseurDAO fournisseurDAO;

    @Override
    public void init() throws ServletException {
        fournisseurDAO = new FournisseurDAO();
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
            response.sendRedirect("liste-fournisseur.jsp");
            return;
        }

        switch (action) {
            case "add":
                handleAfficherFormulaireAjout(request, response);
                break;

            case "edit":
                handleAfficherFormulaireModification(request, response);
                break;

            case "lister":
                handleLister(request, response);
                break;

            default:
                response.sendRedirect("liste-fournisseur.jsp");
                break;
        }
    }
    

    private void ajouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String nom = request.getParameter("nom");
            String contact = request.getParameter("contact");
            String telephone = request.getParameter("telephone");
            String email = request.getParameter("email");
            String devisePreference = request.getParameter("devisePreference");
            String modePaiement = request.getParameter("modePaiement");
            boolean creditAutorise = "on".equals(request.getParameter("creditAutorise"));
            BigDecimal limiteCredit = new BigDecimal(request.getParameter("limiteCredit"));
            BigDecimal soldeActuel = new BigDecimal(request.getParameter("soldeActuel"));

            if (nom == null || nom.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Le nom du fournisseur est requis.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("ajout-fournisseurs.jsp");
                return;
            }

            Fournisseur f = new Fournisseur();
            f.setNom(nom.trim());
            f.setContact(contact != null ? contact.trim() : null);
            f.setTelephone(telephone != null ? telephone.trim() : null);
            f.setEmail(email != null ? email.trim() : null);
            f.setDevisePreference(devisePreference != null ? devisePreference.trim() : "HTG");
            f.setModePaiement(modePaiement != null ? modePaiement.trim() : "CASH");
            f.setCreditAutorise(creditAutorise);
            f.setLimiteCredit(limiteCredit);
            f.setSoldeActuel(soldeActuel);

            boolean success = fournisseurDAO.insert(f);

            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Fournisseur ajouté avec succès.");
                request.getSession().setAttribute("toastType", "success");
                response.sendRedirect("FournisseurServlet?action=lister");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout du fournisseur.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("ajout-fournisseurs.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("ajout-fournisseurs.jsp");
        }
    }

    private void modifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Fournisseur f = fournisseurDAO.findById(id);
            if (f == null) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Fournisseur introuvable.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("FournisseurServlet?action=lister");
                return;
            }

            String nom = request.getParameter("nom");
            String contact = request.getParameter("contact");
            String telephone = request.getParameter("telephone");
            String email = request.getParameter("email");
            String devisePreference = request.getParameter("devisePreference");
            String modePaiement = request.getParameter("modePaiement");
            boolean creditAutorise = "on".equals(request.getParameter("creditAutorise"));
            BigDecimal limiteCredit = new BigDecimal(request.getParameter("limiteCredit"));
            BigDecimal soldeActuel = new BigDecimal(request.getParameter("soldeActuel"));

            if (nom == null || nom.trim().isEmpty()) {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Le nom du fournisseur est requis.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("ajout-fournisseurs.jsp?id=" + id);
                return;
            }

            f.setNom(nom.trim());
            f.setContact(contact != null ? contact.trim() : null);
            f.setTelephone(telephone != null ? telephone.trim() : null);
            f.setEmail(email != null ? email.trim() : null);
            f.setDevisePreference(devisePreference != null ? devisePreference.trim() : "HTG");
            f.setModePaiement(modePaiement != null ? modePaiement.trim() : "CASH");
            f.setCreditAutorise(creditAutorise);
            f.setLimiteCredit(limiteCredit);
            f.setSoldeActuel(soldeActuel);

            boolean success = fournisseurDAO.update(f);

            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Fournisseur modifié avec succès.");
                request.getSession().setAttribute("toastType", "success");
                response.sendRedirect("FournisseurServlet?action=lister");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification du fournisseur.");
                request.getSession().setAttribute("toastType", "error");
                response.sendRedirect("FournisseurServlet?action=lister");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            request.getSession().setAttribute("toastType", "error");
            response.sendRedirect("FournisseurServlet?action=lister");
        }
    }

    private void supprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // Pour le deletedBy, tu peux récupérer l'utilisateur connecté en session
            HttpSession session = request.getSession(false);
            Integer deletedBy = null;
            if (session != null && session.getAttribute("userId") != null) {
                deletedBy = (Integer) session.getAttribute("userId");
            }

            boolean success = fournisseurDAO.delete(id, deletedBy != null ? deletedBy : 0);

            if (success) {
                request.getSession().setAttribute("ToastAdmSuccesNotif", "Fournisseur supprimé avec succès.");
                request.getSession().setAttribute("toastType", "success");
            } else {
                request.getSession().setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression.");
                request.getSession().setAttribute("toastType", "error");
            }
            response.sendRedirect("FournisseurServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    // GET => afficher formulaire ajout/modif ou liste ou détail
   
    
 
    private void handleAfficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("ajout-fournisseur.jsp").forward(request, response);
    }

    private void handleAfficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Fournisseur f = fournisseurDAO.findById(id);
                if (f != null) {
                    request.setAttribute("fournisseur", f);
                    request.getRequestDispatcher("liste-fournisseur.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fournisseur introuvable");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("liste-fournisseur.jsp");
        }
    }

    private void handleLister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Fournisseur> fournisseurs = fournisseurDAO.findAllVisible();
        request.setAttribute("fournisseurs", fournisseurs);
        request.getRequestDispatcher("liste-fournisseur.jsp").forward(request, response);
    }


    
    
}
