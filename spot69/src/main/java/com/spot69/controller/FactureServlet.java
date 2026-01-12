package com.spot69.controller;

import com.google.gson.Gson;
import com.spot69.dao.FactureDAO;
import com.spot69.dao.FournisseurDAO;
import com.spot69.dao.ProduitDAO;
import com.spot69.model.Facture;
import com.spot69.model.FactureDetail;
import com.spot69.model.Fournisseur;
import com.spot69.model.Permissions;
import com.spot69.model.Produit;
import com.spot69.model.Utilisateur;
import com.spot69.utils.PermissionChecker;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet({"/FactureServlet", "/blok/FactureServlet"})
public class FactureServlet extends HttpServlet {

    private FactureDAO factureDAO;
    private ProduitDAO produitDAO;
    private FournisseurDAO fournisseurDAO;

    @Override
    public void init() throws ServletException {
        factureDAO = new FactureDAO();
        produitDAO = new ProduitDAO();
        fournisseurDAO = new FournisseurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
        case "add":
            afficherFormulaireAjout(request, response);
            break;
            case "ajouter":
                afficherFormulaireAjout(request, response);
                break;
            case "lister":
                listerFactures(request, response);
                break;
            case "updatedeletedinterval":
                updateDeletedInterval(request, response);
                break;
            case "voirdetails":
                afficherDetailsFacture(request, response);
                break;
            case "edit":
                afficherFormulaireEdition(request, response);
                break;
                
            case "supprimer":
                traiterSuppressionFacture(request, response);
                break;


            case "getdetails":
                getDetailsAsJson(request, response);
                break;


            default:
                redirectionIndex(response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "ajouter":
                traiterAjoutFacture(request, response);
                break;
            case "modifier":
                traiterModificationFacture(request, response);
                break;

            default:
                redirectionIndex(response);
                break;
        }
    }
    
    private void afficherDetailsFacture(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String factureIdStr = request.getParameter("id");
        if (factureIdStr != null) {
            try {
                int factureId = Integer.parseInt(factureIdStr);
                List<FactureDetail> details = factureDAO.getDetailsFacture(factureId);
                request.setAttribute("details", details);
                request.getRequestDispatcher("details-facture.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect("FactureServlet?action=lister");
            }
        } else {
            response.sendRedirect("FactureServlet?action=lister");
        }
    }
    
    private void updateDeletedInterval(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        int days = 0;

        // Validation du paramètre days
        try {
            days = Integer.parseInt(request.getParameter("days"));
            if (days < 1) {
                session.setAttribute("ToastAdmErrorNotif", "Le nombre de jours doit être supérieur ou égal à 1.");
                session.setAttribute("toastType", "error");
                response.getWriter().write("Erreur : valeur invalide");
                return;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "Nombre de jours non valide.");
            session.setAttribute("toastType", "error");
            response.getWriter().write("Erreur : valeur non numérique");
            return;
        }

        FactureDAO dao = new FactureDAO();
        boolean success = dao.updateDeletedInterval(days);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        if (success) {
            int updatedValue = dao.getDeletedInterval();
            session.setAttribute("ToastAdmSuccesNotif", "Valeur mise à jour avec succès.");
            session.setAttribute("toastType", "success");
            response.sendRedirect("FactureServlet?action=lister");
        } else {
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la mise à jour.");
            session.setAttribute("toastType", "error");
            response.sendRedirect("FactureServlet?action=lister");
        }
    }


    private void afficherFormulaireEdition(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("FactureServlet?action=lister");
            return;
        }

        try {
            int factureId = Integer.parseInt(idStr);
            Facture facture = factureDAO.chercherParId(factureId);
            if (facture == null) {
                response.sendRedirect("FactureServlet?action=lister");
                return;
            }

            List<FactureDetail> details = factureDAO.getDetailsFacture(factureId);

            // Charger aussi les listes pour dropdown autocomplete (produits, fournisseurs)
            List<Produit> produits = produitDAO.listerProduits();
            List<Fournisseur> fournisseurs = fournisseurDAO.findAllVisible();
            
            for (FactureDetail d : details) {
                System.out.println("Produit pour le détail: " + d.getProduit());
            }


            // Mettre en attributs de requête
            request.setAttribute("facture", facture);
            request.setAttribute("details", details);
            request.setAttribute("produits", produits);
            request.setAttribute("fournisseurs", fournisseurs);

            // Forward vers la JSP de formulaire (réutilisation)
            request.getRequestDispatcher("modifier-facture.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("FactureServlet?action=lister");
        }
    }

    
    private void getDetailsAsJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int factureId = Integer.parseInt(request.getParameter("id"));
        List<FactureDetail> details = factureDAO.getDetailsFacture(factureId);

        Gson gson = new Gson();
        String json = gson.toJson(details);
        response.getWriter().write(json);
    }



    private String genererNumeroFacture() {
        Random random = new Random();
        String noFacture;
        do {
            int part1 = 100 + random.nextInt(900);     // Trois chiffres (100 à 999)
            int part2 = 1000 + random.nextInt(9000);   // Quatre chiffres (1000 à 9999)
            noFacture = "NOF-" + part1 + "-" + part2;
        } while (factureDAO.existeNumeroFacture(noFacture));
        return noFacture;
    }

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer la liste des produits
        List<Produit> produits = produitDAO.listerProduits();
        request.setAttribute("produits", produits);

        // Récupérer la liste des fournisseurs visibles
        List<Fournisseur> fournisseurs = fournisseurDAO.findAllVisible();
        request.setAttribute("fournisseurs", fournisseurs);
        
        // Générer un numéro de facture par défaut
        String numeroParDefaut = genererNumeroFacture();
        request.setAttribute("noFactureParDefaut", numeroParDefaut);

        // Forward vers la JSP
        request.getRequestDispatcher("reaprovisionner.jsp").forward(request, response);
    }

    private void listerFactures(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Facture> factures = factureDAO.listerFactures();
        int updatedValue = factureDAO.getDeletedInterval();
        request.setAttribute("factures", factures);
        request.setAttribute("deletedInterval", updatedValue);
        request.getRequestDispatcher("liste-factures.jsp").forward(request, response);
    }

    private void redirectionIndex(HttpServletResponse response) throws IOException {
        response.sendRedirect("index.jsp");
    }

 
    
    private void traiterModificationFacture(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null)
                ? (Integer) session.getAttribute("userId")
                : null;

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int factureId = Integer.parseInt(request.getParameter("factureId")); // caché dans le form
            Facture facture = extraireFactureDepuisRequest(request);
            facture.setId(factureId);

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(userId);
            facture.setUtilisateur(utilisateur);

            List<FactureDetail> details = extraireDetailsDepuisRequest(request);

            boolean updated = factureDAO.modifierFacture(facture, details);

            if (updated) {
                session.setAttribute("ToastAdmSuccesNotif", "Facture modifiée avec succès.");
                session.setAttribute("toastType", "success");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification de la facture.");
                session.setAttribute("toastType", "error");
            }

            response.sendRedirect("FactureServlet?action=lister");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            session.setAttribute("toastType", "error");
            response.sendRedirect("FactureServlet?action=lister");
        }
    }


    private void traiterAjoutFacture(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = (session != null && session.getAttribute("userId") != null)
                ? (Integer) session.getAttribute("userId")
                : null;

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Facture facture = extraireFactureDepuisRequest(request);
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(userId);
            facture.setUtilisateur(utilisateur);
            
            // ✅ Vérifie si le numéro de facture existe déjà
            if (factureDAO.existeNumeroFacture(facture.getNoFacture())) {
                session.setAttribute("ToastAdmErrorNotif", "Numéro de facture déjà existant !");
                session.setAttribute("toastType", "error");
                response.sendRedirect("FactureServlet?action=add");
                return;
            }

            List<FactureDetail> details = extraireDetailsDepuisRequest(request);

            int factureId = factureDAO.ajouterFacture(facture, details);

            if (factureId > 0) {
                session.setAttribute("ToastAdmSuccesNotif", "Facture ajoutée avec succès.");
                session.setAttribute("toastType", "success");
                response.sendRedirect("FactureServlet?action=add");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout de la facture.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("FactureServlet?action=add");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur technique: " + e.getMessage());
            session.setAttribute("toastType", "error");
            response.sendRedirect("FactureServlet?action=add");
        }
    }
    
    private void traiterSuppressionFacture(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Integer deletedBy = null;
        Utilisateur utilisateur = null;

        if (session != null && session.getAttribute("userId") != null && session.getAttribute("user") != null) {
            deletedBy = (Integer) session.getAttribute("userId");
            utilisateur = (Utilisateur) session.getAttribute("user");
        }

        if (deletedBy == null || utilisateur == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Vérification des permissions
        if (!(PermissionChecker.hasPermission(utilisateur, Permissions.SUPPRESSION_FACTURES) ||
              PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_FACTURES))) {
            session.setAttribute("ToastAdmErrorNotif", "Droit manquant : vous n’avez pas la permission de supprimer une facture.");
            session.setAttribute("toastType", "error");
            response.sendRedirect("FactureServlet?action=lister");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("FactureServlet?action=lister");
            return;
        }

        try {
            int factureId = Integer.parseInt(idStr);
            Facture facture = factureDAO.chercherParId(factureId);
            
            if (facture == null) {
                session.setAttribute("ToastAdmErrorNotif", "Facture introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("FactureServlet?action=lister");
                return;
            }

            int deletedInterval = factureDAO.getDeletedInterval();
            long diffInMillis = new java.util.Date().getTime() - facture.getCreatedAt().getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays > deletedInterval) {
                session.setAttribute("ToastAdmErrorNotif", "Le délai de suppression autorisé (" + deletedInterval + " jours) est dépassé.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("FactureServlet?action=lister");
                return;
            }

            factureDAO.supprimerFacture(factureId, deletedBy);

            session.setAttribute("ToastAdmSuccesNotif", "Facture supprimée avec succès.");
            session.setAttribute("toastType", "success");

        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID facture invalide.");
            session.setAttribute("toastType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression : " + e.getMessage());
            session.setAttribute("toastType", "error");
        }

        response.sendRedirect("FactureServlet?action=lister");
    }



    private Facture extraireFactureDepuisRequest(HttpServletRequest request) {
        Facture facture = new Facture();

        String noFacture = request.getParameter("noFacture");
        String fournisseurIdStr = request.getParameter("fournisseur");
        String dateStr = request.getParameter("date");
        String isCreditStr = request.getParameter("isCredit");
        String montantVerseStr = request.getParameter("montantVerse");
        String moyenPaiement = request.getParameter("moyenPaiement");
        String soldeStr = request.getParameter("solde");
        String totalAchatStr = request.getParameter("totalAchat");

        facture.setNoFacture(noFacture);

        if (fournisseurIdStr != null && !fournisseurIdStr.isEmpty()) {
            int fournisseurId = Integer.parseInt(fournisseurIdStr);
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setId(fournisseurId);
            facture.setFournisseur(fournisseur);
        }

        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                java.sql.Date dateSql = java.sql.Date.valueOf(dateStr);
                facture.setCreatedAt(new java.util.Date(dateSql.getTime()));
            } catch (IllegalArgumentException e) {
                facture.setCreatedAt(new java.util.Date());
            }
        } else {
            facture.setCreatedAt(new java.util.Date());
        }

        facture.setMontantTotal(totalAchatStr != null && !totalAchatStr.isEmpty() ? new Integer(totalAchatStr) : 0);
        facture.setMontantVerse(montantVerseStr != null && !montantVerseStr.isEmpty() ? new Integer(montantVerseStr) : 0);
        facture.setSolde(soldeStr != null && !soldeStr.isEmpty() ? new Integer(soldeStr) : 0);
        facture.setCredit("oui".equalsIgnoreCase(isCreditStr));
        facture.setMoyenPaiement(moyenPaiement);
        facture.setUpdatedAt(new java.util.Date());

        return facture;
    }

    private List<FactureDetail> extraireDetailsDepuisRequest(HttpServletRequest request) {
        List<FactureDetail> details = new ArrayList<>();

        String[] produitIds = request.getParameterValues("produitId[]");
        String[] quantites = request.getParameterValues("quantite[]");
        String[] prixAchatParUniteMesures = request.getParameterValues("prixAchatParUniteMesure[]");
        String[] prixAchatTotals = request.getParameterValues("prixAchatTotal[]");
        String[] qteUnites = request.getParameterValues("qteUnite[]");
        String[] prixRevientUnites = request.getParameterValues("prixRevientUnite[]");
        String[] uniteMesures = request.getParameterValues("uniteMesure[]");

        if (produitIds == null) return details;
        for (int i = 0; i < produitIds.length; i++) {
            String idStr = produitIds[i];
            if (idStr == null || idStr.trim().isEmpty()) continue;

            FactureDetail detail = new FactureDetail();

            try {
                int produitId = Integer.parseInt(idStr);
                Produit produit = produitDAO.chercherParId(produitId);
                if (produit == null) {
                    produit = new Produit();
                    produit.setId(produitId);
                    if (uniteMesures != null && i < uniteMesures.length) {
                        produit.setUniteVente(uniteMesures[i]);
                    }
                }
                detail.setProduit(produit);

                // Afficher Produit
                System.out.println("Produit ID: " + produit.getId() + ", Unité: " + produit.getUniteVente());

//                if (quantites != null && i < quantites.length && !quantites[i].isEmpty()) {
//                    detail.setQuantite(Integer.parseInt(quantites[i]));
//                    System.out.println("Quantité: " + quantites[i]);
//                }
                if (quantites != null && i < quantites.length && !quantites[i].isEmpty()) {
                    try {
                        // On convertit d'abord en double, puis on prend la partie entière
                        double qteDecimal = Double.parseDouble(quantites[i]);
                        int qteEntiere = (int) qteDecimal; // tronque la partie décimale
                        detail.setQuantite(qteEntiere);
                        System.out.println("Quantité entière: " + qteEntiere);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur conversion quantité: " + quantites[i]);
                        detail.setQuantite(0); // valeur par défaut en cas d'erreur
                    }
                }

                if (prixAchatParUniteMesures != null && i < prixAchatParUniteMesures.length && !prixAchatParUniteMesures[i].isEmpty()) {
                    detail.setPrixAchatParUniteMesure(new Integer(prixAchatParUniteMesures[i]));
                    System.out.println("Prix achat par unité: " + prixAchatParUniteMesures[i]);
                }
                if (prixAchatTotals != null && i < prixAchatTotals.length && !prixAchatTotals[i].isEmpty()) {
                    detail.setPrixAchatTotal(new Integer(prixAchatTotals[i]));
                    System.out.println("Prix achat total: " + prixAchatTotals[i]);
                }
                if (qteUnites != null && i < qteUnites.length && !qteUnites[i].isEmpty()) {
                    detail.setQteUnite(new Integer(qteUnites[i]));
                    System.out.println("Qté unité: " + qteUnites[i]);
                }
                if (prixRevientUnites != null && i < prixRevientUnites.length && !prixRevientUnites[i].isEmpty()) {
                    detail.setPrixRevientUnite(new Integer(prixRevientUnites[i]));
                    System.out.println("Prix revient unité: " + prixRevientUnites[i]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur conversion nombre: " + e.getMessage());
            }

            details.add(detail);
        }


        return details;
    }
}
