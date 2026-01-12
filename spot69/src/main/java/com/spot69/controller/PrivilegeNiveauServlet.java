package com.spot69.controller;

import com.spot69.dao.PrivilegeNiveauDAO;
import com.spot69.model.PrivilegeNiveau;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet({ "/PrivilegeNiveauServlet", "/blok/PrivilegeNiveauServlet" })
public class PrivilegeNiveauServlet extends HttpServlet {

    private PrivilegeNiveauDAO privilegeNiveauDAO;

    @Override
    public void init() throws ServletException {
        privilegeNiveauDAO = new PrivilegeNiveauDAO();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        setupEncoding(request, response);
        
        String action = request.getParameter("action");
        
//        if (action == null || action.isEmpty()) {
//            action = "lister";
//        }

        switch (action) {
            case "lister":
                handleLister(request, response);
                break;
            case "add":
                handleAddPage(request, response);
                break;
            case "edit":
                handleEditPage(request, response);
                break;
            case "details":
                handleDetails(request, response);
                break;
            default:
                response.sendRedirect("PrivilegeNiveauServlet?action=add");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        setupEncoding(request, response);
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
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
            case "changer-statut":
                handleChangerStatut(request, response);
                break;
            default:
                response.sendRedirect("PrivilegeNiveauServlet?action=lister");
                break;
        }
    }

    // ======= Handlers GET =======

    private void handleLister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<PrivilegeNiveau> niveaux = privilegeNiveauDAO.listerTous();
            int totalActifs = privilegeNiveauDAO.compterActifs();
            
            request.setAttribute("niveaux", niveaux);
            request.setAttribute("totalActifs", totalActifs);
            request.setAttribute("totalNiveaux", niveaux.size());
            
            request.getRequestDispatcher("privilege-niveau.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur lors du chargement des niveaux: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleAddPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("ajout-privilege-niveau.jsp").forward(request, response);
    }

    private void handleEditPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                PrivilegeNiveau niveau = privilegeNiveauDAO.chercherParId(id);
                
                if (niveau != null) {
                    request.setAttribute("niveau", niveau);
                    request.getRequestDispatcher("modifier-privilege-niveau.jsp").forward(request, response);
                } else {
                    setErrorNotif(request, "Niveau de privilège introuvable");
                    response.sendRedirect("PrivilegeNiveauServlet?action=lister");
                }
            } catch (NumberFormatException e) {
                setErrorNotif(request, "ID invalide");
                response.sendRedirect("PrivilegeNiveauServlet?action=lister");
            }
        } else {
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
        }
    }

    private void handleDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                PrivilegeNiveau niveau = privilegeNiveauDAO.chercherParId(id);
                
                if (niveau != null) {
                    // Générer le HTML des détails pour affichage modal
                    StringBuilder html = new StringBuilder();
                    
                    html.append("<div class='row'>");
                    html.append("<div class='col-md-12'>");
                    
                    // Badge du niveau
                    html.append("<div class='text-center mb-4'>");
                    html.append("<span class='badge ").append(niveau.getBadgeClass()).append(" p-3 mb-3' style='font-size: 1.5rem;'>");
                    html.append("<i class='fe fe-award mr-2'></i>");
                    html.append(niveau.getNom());
                    html.append("</span>");
                    html.append("</div>");
                    
                    // Informations principales
                    html.append("<div class='card border-left-primary shadow mb-4'>");
                    html.append("<div class='card-body'>");
                    html.append("<div class='row'>");
                    
                    html.append("<div class='col-md-6'>");
                    html.append("<div class='row mb-3'>");
                    html.append("<div class='col-6'><strong>Seuil de points:</strong></div>");
                    html.append("<div class='col-6'>").append(niveau.getSeuilPoints()).append(" pts</div>");
                    html.append("</div>");
                    
                    html.append("<div class='row mb-3'>");
                    html.append("<div class='col-6'><strong>Réduction:</strong></div>");
                    html.append("<div class='col-6'>").append(niveau.getPourcentageReduction()).append(" %</div>");
                    html.append("</div>");
                    html.append("</div>");
                    
                    html.append("<div class='col-md-6'>");
                    html.append("<div class='row mb-3'>");
                    html.append("<div class='col-6'><strong>Statut:</strong></div>");
                    html.append("<div class='col-6'>");
                    html.append("<span class='badge ").append(niveau.getStatut().equals("ACTIF") ? "badge-success" : "badge-secondary").append("'>");
                    html.append(niveau.getStatut());
                    html.append("</span></div>");
                    html.append("</div>");
                    
                    html.append("<div class='row mb-3'>");
                    html.append("<div class='col-6'><strong>Couleur:</strong></div>");
                    html.append("<div class='col-6'>");
                    html.append("<span class='badge' style='background-color: ").append(niveau.getCouleur()).append("'>");
                    html.append(niveau.getCouleur());
                    html.append("</span></div>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div></div>");
                    
                    // Description
                    html.append("<div class='card shadow mb-4'>");
                    html.append("<div class='card-header'>");
                    html.append("<h6 class='m-0 font-weight-bold text-primary'>Description</h6>");
                    html.append("</div>");
                    html.append("<div class='card-body'>");
                    html.append("<p>").append(niveau.getDescription() != null ? niveau.getDescription() : "Aucune description").append("</p>");
                    html.append("</div></div>");
                    
                    // Informations de suivi
                    html.append("<div class='card shadow'>");
                    html.append("<div class='card-header'>");
                    html.append("<h6 class='m-0 font-weight-bold text-primary'>Informations de suivi</h6>");
                    html.append("</div>");
                    html.append("<div class='card-body'>");
                    html.append("<div class='row'>");
                    html.append("<div class='col-md-6'>");
                    html.append("<p><strong>Créé le:</strong> ").append(niveau.getCreatedAt()).append("</p>");
                    html.append("</div>");
                    html.append("<div class='col-md-6'>");
                    html.append("<p><strong>Mis à jour le:</strong> ").append(niveau.getUpdatedAt()).append("</p>");
                    html.append("</div>");
                    html.append("</div>");
                    html.append("</div></div>");
                    
                    html.append("</div></div>");
                    
                    response.setContentType("text/html");
                    response.getWriter().write(html.toString());
                } else {
                    response.setContentType("text/html");
                    response.getWriter().write("<div class='alert alert-danger'>Niveau de privilège introuvable</div>");
                }
            } else {
                response.setContentType("text/html");
                response.getWriter().write("<div class='alert alert-danger'>ID manquant</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("<div class='alert alert-danger'>Erreur lors du chargement des détails</div>");
        }
    }

    // ======= Handlers POST =======

    private void handleAjouter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            PrivilegeNiveau niveau = extraireNiveauDepuisRequest(request);
            
            // Vérifier si le nom existe déjà
            if (privilegeNiveauDAO.existeNom(niveau.getNom(), 0)) {
                setErrorNotif(request, "Un niveau avec ce nom existe déjà");
                response.sendRedirect("PrivilegeNiveauServlet?action=add");
                return;
            }
            
            // Définir l'utilisateur créateur
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                niveau.setCreatedBy((Integer) session.getAttribute("userId"));
            }
            
            boolean success = privilegeNiveauDAO.ajouter(niveau);
            
            if (success) {
                setSuccessNotif(request, "Niveau de privilège ajouté avec succès");
            } else {
                setErrorNotif(request, "Erreur lors de l'ajout du niveau de privilège");
            }
            
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
            
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PrivilegeNiveauServlet?action=add");
        }
    }

    private void handleModifier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            PrivilegeNiveau niveau = privilegeNiveauDAO.chercherParId(id);
            
            if (niveau == null) {
                setErrorNotif(request, "Niveau de privilège introuvable");
                response.sendRedirect("PrivilegeNiveauServlet?action=lister");
                return;
            }
            
            // Vérifier si le nom existe déjà (pour un autre ID)
            String nouveauNom = request.getParameter("nom");
            if (!niveau.getNom().equals(nouveauNom) && 
                privilegeNiveauDAO.existeNom(nouveauNom, id)) {
                setErrorNotif(request, "Un niveau avec ce nom existe déjà");
                response.sendRedirect("PrivilegeNiveauServlet?action=edit&id=" + id);
                return;
            }
            
            // Extraire et mettre à jour les données
            PrivilegeNiveau updated = extraireNiveauDepuisRequest(request);
            updated.setId(id);
            
            // Définir l'utilisateur qui modifie
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                updated.setUpdatedBy((Integer) session.getAttribute("userId"));
            }
            
            boolean success = privilegeNiveauDAO.modifier(updated);
            
            if (success) {
                setSuccessNotif(request, "Niveau de privilège modifié avec succès");
            } else {
                setErrorNotif(request, "Erreur lors de la modification du niveau de privilège");
            }
            
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
            
        } catch (NumberFormatException e) {
            setErrorNotif(request, "ID invalide");
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
        }
    }

    private void handleSupprimer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            HttpSession session = request.getSession(false);
            Integer deletedBy = session != null && session.getAttribute("userId") != null
                    ? (Integer) session.getAttribute("userId")
                    : 0;
            
            boolean success = privilegeNiveauDAO.supprimer(id);
            
            if (success) {
                setSuccessNotif(request, "Niveau de privilège désactivé avec succès");
            } else {
                setErrorNotif(request, "Erreur lors de la désactivation du niveau");
            }
            
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
            
        } catch (NumberFormatException e) {
            setErrorNotif(request, "ID invalide");
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
        }
    }

    private void handleChangerStatut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nouveauStatut = request.getParameter("statut");
            
            if (nouveauStatut == null || (!nouveauStatut.equals("ACTIF") && !nouveauStatut.equals("INACTIF"))) {
                setErrorNotif(request, "Statut invalide");
                response.sendRedirect("PrivilegeNiveauServlet?action=lister");
                return;
            }
            
            PrivilegeNiveau niveau = privilegeNiveauDAO.chercherParId(id);
            if (niveau == null) {
                setErrorNotif(request, "Niveau de privilège introuvable");
                response.sendRedirect("PrivilegeNiveauServlet?action=lister");
                return;
            }
            
            niveau.setStatut(nouveauStatut);
            
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                niveau.setUpdatedBy((Integer) session.getAttribute("userId"));
            }
            
            boolean success = privilegeNiveauDAO.modifier(niveau);
            
            if (success) {
                setSuccessNotif(request, "Statut du niveau modifié avec succès");
            } else {
                setErrorNotif(request, "Erreur lors du changement de statut");
            }
            
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
            
        } catch (NumberFormatException e) {
            setErrorNotif(request, "ID invalide");
            response.sendRedirect("PrivilegeNiveauServlet?action=lister");
        }
    }

    // ======= Méthodes utilitaires =======

    private PrivilegeNiveau extraireNiveauDepuisRequest(HttpServletRequest request) {
        PrivilegeNiveau niveau = new PrivilegeNiveau();
        
        // Nom
        String nom = request.getParameter("nom");
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est requis");
        }
        niveau.setNom(nom.trim().toUpperCase());
        
        // Seuil de points
        String seuilStr = request.getParameter("seuilPoints");
        if (seuilStr == null || seuilStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le seuil de points est requis");
        }
        niveau.setSeuilPoints(Integer.parseInt(seuilStr.trim()));
        
        // Pourcentage de réduction
        String pourcentageStr = request.getParameter("pourcentageReduction");
        if (pourcentageStr == null || pourcentageStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le pourcentage de réduction est requis");
        }
        niveau.setPourcentageReduction(new BigDecimal(pourcentageStr.trim()));
        
        // Description
        String description = request.getParameter("description");
        niveau.setDescription(description != null ? description.trim() : "");
        
        // Couleur
        String couleur = request.getParameter("couleur");
        if (couleur == null || couleur.trim().isEmpty()) {
            throw new IllegalArgumentException("La couleur est requise");
        }
        niveau.setCouleur(couleur.trim());
        
        // Statut
        String statut = request.getParameter("statut");
        niveau.setStatut(statut != null ? statut : "ACTIF");
        
        return niveau;
    }

    private void setSuccessNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmErrorNotif", message);
        request.getSession().setAttribute("toastType", "error");
    }
}