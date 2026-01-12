package com.spot69.controller;

import com.spot69.dao.PanierDAO;
import com.spot69.model.Panier;
import com.spot69.model.Plat;
import com.spot69.model.Produit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet({"/PanierServlet", "/blok/PanierServlet"})
public class PanierServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PanierDAO panierDAO;

    @Override
    public void init() throws ServletException {
        panierDAO = new PanierDAO();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupEncoding(request, response);
        Integer utilisateurId = getUtilisateurId(request);

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "lister":
                listerPanierPage(utilisateurId, request, response);
                break;

            case "lister-json":
                listerPanierJson(utilisateurId, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action GET inconnue");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupEncoding(request, response);
        Integer utilisateurId = getUtilisateurId(request);

        String action = request.getParameter("action");
        String type = request.getParameter("type");
        String idStr = request.getParameter("id");

        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "ajouter":
                ajouterAuPanier(utilisateurId, type, idStr, response);
                break;

            case "augmenter":
                modifierQuantitePanier(utilisateurId, type, idStr, true, request, response);
                break;

            case "diminuer":
                modifierQuantitePanier(utilisateurId, type, idStr, false, request, response);
                break;

            case "supprimer":
                supprimerDuPanier(utilisateurId, type, idStr, request, response);
                break;
                
            case "set-quantity":
                setQuantityInCart(utilisateurId, type, idStr, request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action POST inconnue");
        }
    }

    // ========================= MÉTHODES PRIVÉES ==============================

    private void setupEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            // UTF-8 toujours supporté, log si nécessaire
            e.printStackTrace();
        }
    }

    private Integer getUtilisateurId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null && session.getAttribute("userId") != null)
                ? (Integer) session.getAttribute("userId")
                : null;
    }

    private void setQuantityInCart(Integer userId, String type, String idStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Connectez-vous pour modifier le panier.");
            return;
        }

        if (type == null || idStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Paramètres manquants.");
            return;
        }

        try {
            int panierId = Integer.parseInt(idStr);
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            if (quantity < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Quantité invalide.");
                return;
            }
            
            boolean success = panierDAO.setQuantity(userId, panierId, type, quantity);
            
            if (success) {
                response.getWriter().write("Quantité mise à jour.");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Erreur lors de la mise à jour de la quantité.");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID ou quantité invalide.");
        }
    }
    private void listerPanierPage(Integer utilisateurId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (utilisateurId == null) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"non connecte\"}");
            return;
        }

        List<Panier> panier = panierDAO.listerPanier(utilisateurId);
        request.setAttribute("listePanier", panier);
        request.getRequestDispatcher("/WEB-INF/views/panier.jsp").forward(request, response);
    }

//    private void listerPanierJson(Integer utilisateurId, HttpServletResponse response) throws IOException {
//        if (utilisateurId == null) {
//            response.getWriter().write("[]");
//            return;
//        }
//
//        List<Panier> liste = panierDAO.listerPanier(utilisateurId);
//        StringBuilder json = new StringBuilder("[");
//
//        for (int i = 0; i < liste.size(); i++) {
//            Panier p = liste.get(i);
//            json.append("{")
//                .append("\"panier_id\":").append(p.getId()).append(",")
//                .append("\"utilisateur_id\":").append(p.getUtilisateurId()).append(",")
//                .append("\"quantite\":").append(p.getQuantite()).append(",")
//                .append("\"dateAjout\":\"").append(p.getDateAjout()).append("\",")
//                .append("\"createdAt\":\"").append(p.getCreatedAt()).append("\",")
//                .append("\"updatedAt\":\"").append(p.getUpdatedAt()).append("\",")
//                .append("\"deletedBy\":").append(p.getDeletedBy() == null ? "null" : p.getDeletedBy()).append(",")
//                .append("\"total\":").append(p.getTotal()).append(",");
//
//            if (p.getPlat() != null) {
//                Plat plat = p.getPlat();
//                json.append("\"plat\":{")
//                    .append("\"id\":").append(plat.getId()).append(",")
//                    .append("\"nom\":\"").append(escapeJson(plat.getNom())).append("\",")
//                    .append("\"image\":\"").append(escapeJson(plat.getImage())).append("\",")
//                    .append("\"prix\":").append(plat.getPrix())
//                    .append("},");
//            } else {
//                json.append("\"plat\":null,");
//            }
//
//            if (p.getProduit() != null) {
//                Produit prod = p.getProduit();
//                json.append("\"produit\":{")
//                    .append("\"id\":").append(prod.getId()).append(",")
//                    .append("\"nom\":\"").append(escapeJson(prod.getNom())).append("\",")
//                    .append("\"imageUrl\":\"").append(escapeJson(prod.getImageUrl())).append("\",")
//                    .append("\"prixVente\":").append(prod.getPrixVente().doubleValue())
//                    .append("}");
//            } else {
//                json.append("\"produit\":null");
//            }
//
//            json.append("}");
//            if (i < liste.size() - 1) json.append(",");
//        }
//
//        json.append("]");
//        response.setContentType("application/json");
//        response.getWriter().write(json.toString());
//    }
    
//    private void listerPanierJson(Integer utilisateurId, HttpServletResponse response) throws IOException {
//        if (utilisateurId == null) {
//            response.getWriter().write("[]");
//            return;
//        }
//
//        List<Panier> liste = panierDAO.listerPanier(utilisateurId);
//        StringBuilder json = new StringBuilder("[");
//
//        for (int i = 0; i < liste.size(); i++) {
//            Panier p = liste.get(i);
//            json.append("{")
//                .append("\"panier_id\":").append(p.getId()).append(",")
//                .append("\"utilisateur_id\":").append(p.getUtilisateurId()).append(",")
//                .append("\"quantite\":").append(p.getQuantite()).append(",")
//                .append("\"dateAjout\":\"").append(p.getDateAjout()).append("\",")
//                .append("\"createdAt\":\"").append(p.getCreatedAt()).append("\",")
//                .append("\"updatedAt\":\"").append(p.getUpdatedAt()).append("\",")
//                .append("\"deletedBy\":").append(p.getDeletedBy() == null ? "null" : p.getDeletedBy()).append(",")
//                .append("\"total\":").append(p.getTotal()).append(",");
//
//            if (p.getPlat() != null) {
//                Plat plat = p.getPlat();
//                json.append("\"plat\":{")
//                    .append("\"id\":").append(plat.getId()).append(",")
//                    .append("\"nom\":\"").append(escapeJson(plat.getNom())).append("\",")
//                    .append("\"image\":\"").append(escapeJson(plat.getImage())).append("\",")
//                    .append("\"prix\":").append(plat.getPrix())
//                    .append("},");
//                
//                	
//            } else {
//                json.append("\"plat\":null,");
//            }
//
//            if (p.getProduit() != null) {
//                Produit prod = p.getProduit();
//                json.append("\"produit\":{")
//                    .append("\"id\":").append(prod.getId()).append(",")
//                    .append("\"nom\":\"").append(escapeJson(prod.getNom())).append("\",")
//                    .append("\"imageUrl\":\"").append(escapeJson(prod.getImageUrl())).append("\",")
//                    .append("\"prixVente\":").append(prod.getPrixVente().doubleValue());
//                
//                // Ajout des catégories si elles existent
//                if (prod.getCategorie() != null) {
//                    json.append(",\"categorie\":{\"id\":").append(prod.getCategorie().getId())
//                        .append(",\"nom\":\"").append(escapeJson(prod.getCategorie().getNom())).append("\"}");
//                }
//                
//                if (prod.getSousCategorie() != null) {
//                    json.append(",\"sousCategorie\":{\"id\":").append(prod.getSousCategorie().getId())
//                        .append(",\"nom\":\"").append(escapeJson(prod.getSousCategorie().getNom())).append("\"}");
//                }
//                
//                json.append("}");
//            } else {
//                json.append("\"produit\":null");
//            }
//
//            json.append("}");
//            if (i < liste.size() - 1) json.append(",");
//        }
//
//        json.append("]");
//        response.setContentType("application/json");
//        response.getWriter().write(json.toString());
//    }

    private void listerPanierJson(Integer utilisateurId, HttpServletResponse response) throws IOException {
        if (utilisateurId == null) {
            response.getWriter().write("[]");
            return;
        }

        List<Panier> liste = panierDAO.listerPanier(utilisateurId);
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < liste.size(); i++) {
            Panier p = liste.get(i);
            json.append("{")
                .append("\"panier_id\":").append(p.getId()).append(",")
                .append("\"utilisateur_id\":").append(p.getUtilisateurId()).append(",")
                .append("\"quantite\":").append(p.getQuantite()).append(",")
                .append("\"dateAjout\":\"").append(p.getDateAjout()).append("\",")
                .append("\"createdAt\":\"").append(p.getCreatedAt()).append("\",")
                .append("\"updatedAt\":\"").append(p.getUpdatedAt()).append("\",")
                .append("\"deletedBy\":").append(p.getDeletedBy() == null ? "null" : p.getDeletedBy()).append(",")
                .append("\"total\":").append(p.getTotal()).append(",");

            if (p.getPlat() != null) {
                Plat plat = p.getPlat();
                json.append("\"plat\":{")
                    .append("\"id\":").append(plat.getId()).append(",")
                    .append("\"nom\":\"").append(escapeJson(plat.getNom())).append("\",")
                    .append("\"image\":\"").append(escapeJson(plat.getImage())).append("\",")
                    .append("\"prix\":").append(plat.getPrix());

                // Catégorie Plat
                if (plat.getCategorieMenu() != null) {
                    json.append(",\"categorie\":{")
                        .append("\"id\":").append(plat.getCategorieMenu().getId()).append(",")
                        .append("\"nom\":\"").append(escapeJson(plat.getCategorieMenu().getNom())).append("\"}");
                } else {
                    json.append(",\"categorie\":null");
                }

                // Sous-catégorie Plat
                if (plat.getSousCategorieMenu() != null) {
                    json.append(",\"sousCategorie\":{")
                        .append("\"id\":").append(plat.getSousCategorieMenu().getId()).append(",")
                        .append("\"nom\":\"").append(escapeJson(plat.getSousCategorieMenu().getNom())).append("\"}");
                } else {
                    json.append(",\"sousCategorie\":null");
                }

                json.append("},");
            } else {
                json.append("\"plat\":null,");
            }

            if (p.getProduit() != null) {
                Produit prod = p.getProduit();
                json.append("\"produit\":{")
                    .append("\"id\":").append(prod.getId()).append(",")
                    .append("\"nom\":\"").append(escapeJson(prod.getNom())).append("\",")
                    .append("\"imageUrl\":\"").append(escapeJson(prod.getImageUrl())).append("\",")
                    .append("\"prixVente\":").append(prod.getPrixVente().doubleValue());

                // Catégorie Produit
                if (prod.getCategorieMenu() != null) {
                    json.append(",\"categorie\":{")
                        .append("\"id\":").append(prod.getCategorieMenu().getId()).append(",")
                        .append("\"nom\":\"").append(escapeJson(prod.getCategorieMenu().getNom())).append("\"}");
                } else {
                    json.append(",\"categorie\":null");
                }

                // Sous-catégorie Produit
                if (prod.getSousCategorieMenu() != null) {
                    json.append(",\"sousCategorie\":{")
                        .append("\"id\":").append(prod.getSousCategorieMenu().getId()).append(",")
                        .append("\"nom\":\"").append(escapeJson(prod.getSousCategorieMenu().getNom())).append("\"}");
                } else {
                    json.append(",\"sousCategorie\":null");
                }

                json.append("}");
            } else {
                json.append("\"produit\":null");
            }

            json.append("}");
            if (i < liste.size() - 1) json.append(",");
        }

        json.append("]");
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }

    
    private void ajouterAuPanier(Integer userId, String type, String idStr, HttpServletResponse response) throws IOException {
        if (userId == null) {
            response.getWriter().write("Connectez-vous pour commander.");
            return;
        }

        if (type == null || idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            String result = panierDAO.ajouterAuPanier(userId, id, type);

            // Si la réponse contient "Erreur", on l'affiche comme une erreur
            if (result.startsWith("Erreur") || result.startsWith("Échec")) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            response.getWriter().write(result);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }


    private void modifierQuantitePanier(Integer userId, String type, String idStr, boolean augmenter, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (userId == null) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Connectez-vous pour commander.");
            request.getSession().setAttribute("ToastMessage", "Connectez-vous pour commander.");
            response.sendRedirect("MenuServlet?action=categorie-parente");
            return;
        }

        if (type == null || idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean ok = augmenter
                    ? panierDAO.augmenterQteAuPanier(userId, id, type)
                    : panierDAO.diminuerQuantite(userId, id, type);

			/*
			 * String msg = augmenter ? "Quantité augmentée." : "Quantité diminuée."; String
			 * err = augmenter ? "Erreur lors de l'augmentation." :
			 * "Erreur lors de la diminution.";
			 * 
			 * request.getSession().setAttribute("toastType", ok ? "success" : "error");
			 * request.getSession().setAttribute(ok ? "ToastMessage" : "ToastAdmErrorNotif",
			 * ok ? msg : err);
			 */

            response.sendRedirect("MenuServlet?action=categorie-parente");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void supprimerDuPanier(Integer userId, String type, String idStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (userId == null) {
            request.getSession().setAttribute("ToastAdmErrorNotif", "Connectez-vous pour supprimer.");
            response.sendRedirect("MenuServlet?action=categorie-parente");
            return;
        }

        if (type == null || idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean ok = panierDAO.supprimerDuPanier(userId, id, type);
            if (!ok) {
                request.getSession().setAttribute("ToastMessage", "Erreur lors de la suppression.");
                request.getSession().setAttribute("toastType", "error");
            }
            response.sendRedirect("MenuServlet?action=categorie-parente");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }
}
