package com.spot69.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spot69.dao.*;
import com.spot69.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/CompteClientServlet", "/blok/CompteClientServlet"})
public class CompteClientServlet extends HttpServlet {

    private CompteClientDAO compteClientDAO;
    private TransactionCompteDAO transactionCompteDAO;
    private CaisseCaissiereDAO caisseCaissiereDAO;
    private TransactionCaisseDAO transactionCaisseDAO;
    private UtilisateurDAO utilisateurDAO;
    private CommandeDAO commandeDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        compteClientDAO = new CompteClientDAO();
        transactionCompteDAO = new TransactionCompteDAO();
        caisseCaissiereDAO = new CaisseCaissiereDAO();
        transactionCaisseDAO = new TransactionCaisseDAO();
        utilisateurDAO = new UtilisateurDAO();
        commandeDAO = new CommandeDAO();
        
        // Configurer Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gson = gsonBuilder.create();
    }

    private void setupEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
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
            case "creerCompte":
                handleCreerCompte(request, response);
                break;
            case "modifierLimiteCredit":
                handleModifierLimiteCredit(request, response);
                break;
            case "effectuerDepot":
                handleEffectuerDepot(request, response);
                break;
            case "effectuerRetrait":
                handleEffectuerRetrait(request, response);
                break;
            case "payerCommandeViaCompte":
                handlePayerCommandeViaCompte(request, response);
                break;
            case "payerCommandeMixte":
                handlePayerCommandeMixte(request, response);
                break;
            case "ouvrirCaisse":
                handleOuvrirCaisse(request, response);
                break;
            case "fermerCaisse":
                handleFermerCaisse(request, response);
                break;
            case "enregistrerTransactionCaisse":
                handleEnregistrerTransactionCaisse(request, response);
                break;
            case "enregistrerVente":
                handleEnregistrerVente(request, response);
                break;
            case "ajusterSolde":
                handleAjusterSolde(request, response);
                break;
            case "initialiserComptes":
                handleInitialiserComptes(request, response);
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
            handleListeComptes(request, response);
            return;
        }

        switch (action) {
            case "listeComptes":
                handleListeComptes(request, response);
                break;
            case "etatCompte":
                handleEtatCompte(request, response);
                break;
             // Dans doGet()
            case "gestionLimiteCredit":
                handleGestionLimiteCredit(request, response);
                break;
            case "historiqueTransactions":
                handleHistoriqueTransactions(request, response);
                break;
            case "formDepot":
                handleFormDepot(request, response);
                break;
            case "formPaiementCommande":
                handleFormPaiementCommande(request, response);
                break;
            case "listeCaisses":
                handleListeCaisses(request, response);
                break;
            case "etatCaisse":
                handleEtatCaisse(request, response);
                break;
            case "rapportCaisse":
                handleRapportCaisse(request, response);
                break;
            case "historiqueCaisse":
                handleHistoriqueCaisse(request, response);
                break;
            case "getSoldeJSON":
                handleGetSoldeJSON(request, response);
                break;
            case "getHistoriqueJSON":
                handleGetHistoriqueJSON(request, response);
                break;
            case "getEtatGeneralJSON":
                handleGetEtatGeneralJSON(request, response);
                break;
            case "getRapportCaisseJSON":
                handleGetRapportCaisseJSON(request, response);
                break;
            case "getComptesJSON":
                handleGetComptesJSON(request, response);
                break;
            case "dashboard":
                handleDashboard(request, response);
                break;
            case "searchClient":
                handleSearchClient(request, response);
                break;
            case "printReleve":
                handlePrintReleve(request, response);
                break;
            case "exportExcel":
                handleExportExcel(request, response);
                break;
            case "formCaisse":
                handleFormCaisse(request, response);
                break;
            case "detailTransaction":
                handleDetailTransaction(request, response);
                break;

            case "initialiserComptes":
                handleInitialiserComptes(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    // ======== HANDLERS GET ========

//    private void handleListeComptes(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            HttpSession session = request.getSession(false);
//            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//            if (userId == null) {
//                response.sendRedirect("login.jsp");
//                return;
//            }
//
//            // Vérifier les permissions
//            Utilisateur user = utilisateurDAO.findById(userId);
//            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//                                 !user.getRole().getRoleName().equals("MANAGEUR") &&
//                                 !user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
//                setErrorNotif(request, "Permission refusée");
//                response.sendRedirect("index.jsp");
//                return;
//            }
//
//            // Récupérer les filtres
//            String searchTerm = request.getParameter("search");
//            String minSoldeStr = request.getParameter("minSolde");
//            String maxSoldeStr = request.getParameter("maxSolde");
//            String statutFilter = request.getParameter("statutFilter");
//            
//            // Déclarer les variables comme final pour les utiliser dans les lambdas
//            final BigDecimal minSolde;
//            final BigDecimal maxSolde;
//            
//            if (minSoldeStr != null && !minSoldeStr.isEmpty()) {
//                minSolde = new BigDecimal(minSoldeStr);
//            } else {
//                minSolde = null;
//            }
//            
//            if (maxSoldeStr != null && !maxSoldeStr.isEmpty()) {
//                maxSolde = new BigDecimal(maxSoldeStr);
//            } else {
//                maxSolde = null;
//            }
//
//            // Récupérer tous les comptes
//            List<CompteClient> comptes = compteClientDAO.getAllComptes();
//            
//            // Filtrer localement (à améliorer avec une requête SQL plus précise si nécessaire)
//            if (searchTerm != null && !searchTerm.isEmpty()) {
//                final String term = searchTerm.toLowerCase(); // Déclarer final pour le lambda
//                comptes.removeIf(compte -> 
//                    !(compte.getClient().getNom().toLowerCase().contains(term) ||
//                      compte.getClient().getPrenom().toLowerCase().contains(term) ||
//                      compte.getClient().getEmail().toLowerCase().contains(term) ||
//                      String.valueOf(compte.getId()).contains(term))
//                );
//            }
//            
//            if (minSolde != null) {
//                comptes.removeIf(compte -> compte.getSolde().compareTo(minSolde) < 0);
//            }
//            
//            if (maxSolde != null) {
//                comptes.removeIf(compte -> compte.getSolde().compareTo(maxSolde) > 0);
//            }
//            
//            // Calculer les statistiques
//            BigDecimal soldeTotal = BigDecimal.ZERO;
//            int nombreComptes = comptes.size();
//            int nombrePositifs = 0;
//            int nombreNegatifs = 0;
//            
//            for (CompteClient compte : comptes) {
//                soldeTotal = soldeTotal.add(compte.getSolde());
//                if (compte.getSolde().compareTo(BigDecimal.ZERO) > 0) {
//                    nombrePositifs++;
//                } else if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
//                    nombreNegatifs++;
//                }
//            }
//
//            request.setAttribute("comptes", comptes);
//            request.setAttribute("soldeTotal", soldeTotal);
//            request.setAttribute("nombreComptes", nombreComptes);
//            request.setAttribute("nombrePositifs", nombrePositifs);
//            request.setAttribute("nombreNegatifs", nombreNegatifs);
//            request.setAttribute("searchTerm", searchTerm);
//            request.setAttribute("minSolde", minSoldeStr);
//            request.setAttribute("maxSolde", maxSoldeStr);
//            request.setAttribute("statutFilter", statutFilter);
//
//            request.getRequestDispatcher("liste-comptes.jsp").forward(request, response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            setErrorNotif(request, "Erreur technique: " + e.getMessage());
//            response.sendRedirect("index.jsp");
//        }
//    }
 // Modifiez la méthode handleListeComptes pour inclure le filtrage par crédit
    private void handleListeComptes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                                 !user.getRole().getRoleName().equals("MANAGEUR") &&
                                 !user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("index.jsp");
                return;
            }

            // Récupérer les filtres
            String searchTerm = request.getParameter("search");
            String minSoldeStr = request.getParameter("minSolde");
            String maxSoldeStr = request.getParameter("maxSolde");
            String avecCredit = request.getParameter("avecCredit");
            
            final BigDecimal minSolde;
            final BigDecimal maxSolde;
            
            if (minSoldeStr != null && !minSoldeStr.isEmpty()) {
                minSolde = new BigDecimal(minSoldeStr);
            } else {
                minSolde = null;
            }
            
            if (maxSoldeStr != null && !maxSoldeStr.isEmpty()) {
                maxSolde = new BigDecimal(maxSoldeStr);
            } else {
                maxSolde = null;
            }

            // Récupérer tous les comptes
            List<CompteClient> comptes = compteClientDAO.getAllComptes();
            
            // Filtrer localement
            if (searchTerm != null && !searchTerm.isEmpty()) {
                final String term = searchTerm.toLowerCase();
                comptes.removeIf(compte -> 
                    !(compte.getClient().getNom().toLowerCase().contains(term) ||
                      compte.getClient().getPrenom().toLowerCase().contains(term) ||
                      compte.getClient().getEmail().toLowerCase().contains(term) ||
                      String.valueOf(compte.getId()).contains(term))
                );
            }
            
            if (minSolde != null) {
                comptes.removeIf(compte -> compte.getSolde().compareTo(minSolde) < 0);
            }
            
            if (maxSolde != null) {
                comptes.removeIf(compte -> compte.getSolde().compareTo(maxSolde) > 0);
            }
            
            // Filtrer par crédit
            if ("oui".equals(avecCredit)) {
                comptes.removeIf(compte -> compte.getLimiteCredit().compareTo(BigDecimal.ZERO) <= 0);
            } else if ("non".equals(avecCredit)) {
                comptes.removeIf(compte -> compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0);
            }

            // Calculer les statistiques
            BigDecimal soldeTotal = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            BigDecimal totalCreditUtilise = BigDecimal.ZERO;
            int nombreComptes = comptes.size();
            int nombrePositifs = 0;
            int nombreNegatifs = 0;
            int nombreAvecCredit = 0;
            
            for (CompteClient compte : comptes) {
                soldeTotal = soldeTotal.add(compte.getSolde());
                totalCredit = totalCredit.add(compte.getLimiteCredit());
                
                if (compte.getSolde().compareTo(BigDecimal.ZERO) > 0) {
                    nombrePositifs++;
                } else if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                    nombreNegatifs++;
                    totalCreditUtilise = totalCreditUtilise.add(compte.getSolde().abs());
                }
                
                if (compte.getLimiteCredit().compareTo(BigDecimal.ZERO) > 0) {
                    nombreAvecCredit++;
                }
            }

            request.setAttribute("comptes", comptes);
            request.setAttribute("soldeTotal", soldeTotal);
            request.setAttribute("totalCredit", totalCredit);
            request.setAttribute("totalCreditUtilise", totalCreditUtilise);
            request.setAttribute("nombreComptes", nombreComptes);
            request.setAttribute("nombrePositifs", nombrePositifs);
            request.setAttribute("nombreNegatifs", nombreNegatifs);
            request.setAttribute("nombreAvecCredit", nombreAvecCredit);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("minSolde", minSoldeStr);
            request.setAttribute("maxSolde", maxSoldeStr);
            request.setAttribute("avecCredit", avecCredit);

            request.getRequestDispatcher("liste-comptes.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }
    

 // Nouvelles méthodes à ajouter dans la classe
 private void handleGestionLimiteCredit(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     try {
         HttpSession session = request.getSession(false);
         Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
         if (userId == null) {
             response.sendRedirect("login.jsp");
             return;
         }

         // Vérifier permissions
         Utilisateur user = utilisateurDAO.findById(userId);
         if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                              !user.getRole().getRoleName().equals("MANAGEUR"))) {
             setErrorNotif(request, "Permission refusée");
             response.sendRedirect("index.jsp");
             return;
         }

         String compteIdStr = request.getParameter("compteId");
         if (compteIdStr == null || compteIdStr.isEmpty()) {
             setErrorNotif(request, "Compte non spécifié");
             response.sendRedirect("CompteClientServlet?action=listeComptes");
             return;
         }

         int compteId = Integer.parseInt(compteIdStr);
         CompteClient compte = compteClientDAO.getCompteById(compteId);
         
         if (compte == null) {
             setErrorNotif(request, "Compte non trouvé");
             response.sendRedirect("CompteClientServlet?action=listeComptes");
             return;
         }

         request.setAttribute("compte", compte);
         request.getRequestDispatcher("gestion-limite-credit.jsp").forward(request, response);

     } catch (Exception e) {
         e.printStackTrace();
         setErrorNotif(request, "Erreur technique: " + e.getMessage());
         response.sendRedirect("CompteClientServlet?action=listeComptes");
     }
 }

 private void handleModifierLimiteCredit(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     try {
    	 
         System.out.println("DANS handleModifierLimiteCredit:");
         HttpSession session = request.getSession(false);
         Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
         if (userId == null) {
             response.sendRedirect("login.jsp");
             return;
         }

         // Vérifier permissions
         Utilisateur user = utilisateurDAO.findById(userId);
//         if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//                              !user.getRole().getRoleName().equals("MANAGEUR"))) {
//             setErrorNotif(request, "Permission refusée");
//             response.sendRedirect("index.jsp");
//             return;
//         }

         String compteIdStr = request.getParameter("compteId");
         String nouvelleLimiteStr = request.getParameter("nouvelleLimite");
         String raison = request.getParameter("raison");
         
         if (compteIdStr == null || nouvelleLimiteStr == null) {
             setErrorNotif(request, "Paramètres requis manquants");
             response.sendRedirect("CompteClientServlet?action=listeComptes");
             return;
         }

         int compteId = Integer.parseInt(compteIdStr);
         BigDecimal nouvelleLimite = new BigDecimal(nouvelleLimiteStr);
         
         // Validation
         if (nouvelleLimite.compareTo(BigDecimal.ZERO) < 0) {
             setErrorNotif(request, "La limite de crédit ne peut pas être négative");
             response.sendRedirect("CompteClientServlet?action=gestionLimiteCredit&compteId=" + compteId);
             return;
         }

         // Récupérer le compte pour vérifier l'ancienne limite
         CompteClient compte = compteClientDAO.getCompteById(compteId);
         if (compte == null) {
             setErrorNotif(request, "Compte non trouvé");
             response.sendRedirect("CompteClientServlet?action=listeComptes");
             return;
         }
         
         BigDecimal ancienneLimite = compte.getLimiteCredit();
         
         // Vérifier si le solde actuel ne dépasse pas la nouvelle limite
         BigDecimal soldeActuel = compte.getSolde();
         BigDecimal limiteNegative = nouvelleLimite.negate();
         if (soldeActuel.compareTo(limiteNegative) < 0) {
             setErrorNotif(request, "Impossible: Le solde actuel (" + soldeActuel + " HTG) " +
                                   "dépasse déjà la nouvelle limite négative (" + limiteNegative + " HTG)");
             response.sendRedirect("CompteClientServlet?action=gestionLimiteCredit&compteId=" + compteId);
             return;
         }

         // Mettre à jour la limite de crédit
         boolean success = compteClientDAO.mettreAJourLimiteCredit(compteId, nouvelleLimite);
         
         if (success) {
             // Enregistrer une transaction d'ajustement
             TransactionCompte transaction = new TransactionCompte();
             transaction.setCompteClientId(compteId);
             transaction.setTypeTransactionId(4); // ID pour "AJUSTEMENT" (à vérifier dans votre base)
             transaction.setMontant(BigDecimal.ZERO);
             transaction.setSoldeAvant(compte.getSolde());
             transaction.setSoldeApres(compte.getSolde());
             transaction.setCaissiereId(userId);
             
             String notes = "Modification limite crédit: " + 
                           ancienneLimite + " HTG → " + nouvelleLimite + " HTG";
             if (raison != null && !raison.trim().isEmpty()) {
                 notes += " - Raison: " + raison;
             }
             transaction.setNotes(notes);
             transaction.setDateTransaction(new Timestamp(System.currentTimeMillis()));
             
             transactionCompteDAO.creerTransaction(transaction);
             
             setSuccessNotif(request, "Limite de crédit modifiée: " + 
                           ancienneLimite + " HTG → " + nouvelleLimite + " HTG");
             response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + compte.getClientId());
         } else {
             setErrorNotif(request, "Erreur lors de la mise à jour de la limite");
             response.sendRedirect("CompteClientServlet?action=gestionLimiteCredit&compteId=" + compteId);
         }

     } catch (Exception e) {
         e.printStackTrace();
         setErrorNotif(request, "Erreur technique: " + e.getMessage());
         response.sendRedirect("CompteClientServlet?action=listeComptes");
     }
 }

    private void handleEtatCompte(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            int targetClientId;
            
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                targetClientId = Integer.parseInt(clientIdStr);
                
                // Vérifier les permissions si on consulte un autre compte
                Utilisateur user = utilisateurDAO.findById(userId);
                if (user == null || 
                    (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                     !user.getRole().getRoleName().equals("MANAGEUR") &&
                     !user.getRole().getRoleName().equals("CAISSIER(ERE)") &&
                     userId != targetClientId)) {
                    setErrorNotif(request, "Permission refusée pour consulter ce compte");
                    response.sendRedirect("index.jsp");
                    return;
                }
            } else {
                targetClientId = userId; // Par défaut, son propre compte
            }

            // Récupérer l'état complet du compte (sans GestionCompteDAO)
            Map<String, Object> etatCompte = getEtatCompteClient(targetClientId);
            
            if (etatCompte == null) {
                setErrorNotif(request, "Compte non trouvé");
                response.sendRedirect("index.jsp");
                return;
            }

            // Récupérer les filtres de date
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            Date dateDebut = null;
            Date dateFin = null;
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            // Récupérer l'historique filtré
            List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
                targetClientId, dateDebut, dateFin);

            request.setAttribute("etatCompte", etatCompte);
            request.setAttribute("transactions", transactions);
            request.setAttribute("clientId", targetClientId);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);

            request.getRequestDispatcher("etat-compte.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    // Méthode pour remplacer GestionCompteDAO.getEtatCompteClient()
    private Map<String, Object> getEtatCompteClient(int clientId) {
        Map<String, Object> etat = new HashMap<>();
        
        // Récupérer le compte
        CompteClient compte = compteClientDAO.getCompteByClientId(clientId);
        
        if (compte == null) {
            // Créer le compte s'il n'existe pas
            compte = new CompteClient();
            compte.setClientId(clientId);
            compte.setSolde(BigDecimal.ZERO);
            int compteId = compteClientDAO.creerCompteClient(compte);
            if (compteId == -1) {
                return null;
            }
            compte.setId(compteId);
        }
        
        etat.put("compte", compte);
        
        // Récupérer les transactions
        List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
            clientId, null, null);
        etat.put("transactions", transactions);
        
        // Récupérer les commandes impayées liées à ce client
        List<Commande> commandesCredit = commandeDAO.getCommandesCreditAllClients(null, null, clientId);
        etat.put("commandesCredit", commandesCredit);
        
        // Calculer les statistiques
        etat.put("totalDepots", calculerTotalDepots(transactions));
        etat.put("totalDepenses", calculerTotalDepenses(transactions));
        etat.put("soldeCreditTotal", calculerSoldeCreditTotal(commandesCredit));
        
        return etat;
    }

    private BigDecimal calculerTotalDepots(List<TransactionCompte> transactions) {
        if (transactions == null) return BigDecimal.ZERO;
        return transactions.stream()
            .filter(t -> t.getTypeTransaction() != null && "DEPOT".equals(t.getTypeTransaction().getCode()))
            .map(TransactionCompte::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculerTotalDepenses(List<TransactionCompte> transactions) {
        if (transactions == null) return BigDecimal.ZERO;
        return transactions.stream()
            .filter(t -> t.getTypeTransaction() != null && "DEPENSE".equals(t.getTypeTransaction().getCode()))
            .map(TransactionCompte::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculerSoldeCreditTotal(List<Commande> commandes) {
        if (commandes == null) return BigDecimal.ZERO;
        return commandes.stream()
            .filter(c -> c.getCredit() != null)
            .map(c -> BigDecimal.valueOf(c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void handleHistoriqueTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String typeTransaction = request.getParameter("typeTransaction");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            int clientId;
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                clientId = Integer.parseInt(clientIdStr);
            } else {
                clientId = userId;
            }

            Date dateDebut = null;
            Date dateFin = null;
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            // Récupérer l'historique
            List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
                clientId, dateDebut, dateFin);

            // Filtrer par type si spécifié
            if (typeTransaction != null && !typeTransaction.isEmpty() && !"TOUS".equals(typeTransaction)) {
                transactions.removeIf(t -> t.getTypeTransaction() == null || !typeTransaction.equals(t.getTypeTransaction().getCode()));
            }

            // Récupérer le client
            Utilisateur client = utilisateurDAO.findById(clientId);

            request.setAttribute("transactions", transactions);
            request.setAttribute("client", client);
            request.setAttribute("selectedType", typeTransaction);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);

            request.getRequestDispatcher("historique-transactions.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleFormDepot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

          System.out.println("DANS HANDLE FORM DEPOT:");
            // Vérifier si c'est un caissier
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || !user.getRole().getRoleName().equals("CAISSIER(ERE)")) {
                setErrorNotif(request, "Seuls les caissiers peuvent effectuer des dépôts");
                response.sendRedirect("index.jsp");
                return;
            }
            
            

            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.isEmpty()) {
                // Formulaire pour sélectionner un client
                List<Utilisateur> clients = utilisateurDAO.findByRole("CAISSIER(ERE");
                request.setAttribute("clients", clients);
                request.getRequestDispatcher("form-select-client-depot.jsp").forward(request, response);
            } else {

                System.out.println("-------------JNOJNJJNBJ");
                int clientId = Integer.parseInt(clientIdStr);
                Utilisateur client = utilisateurDAO.findById(clientId);
                if (client == null) {
                    setErrorNotif(request, "Client non trouvé");
                    response.sendRedirect("CompteClientServlet?action=formDepot");
                    return;
                }
                
                // Récupérer le compte du client
                CompteClient compte = compteClientDAO.getCompteByClientId(clientId);
                if (compte == null) {
                    // Créer le compte s'il n'existe pas
                    compte = new CompteClient();
                    compte.setClientId(clientId);
                    compte.setSolde(BigDecimal.ZERO);
                    compteClientDAO.creerCompteClient(compte);
                }
                
                // Vérifier si le caissier a une caisse ouverte
                CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    setErrorNotif(request, "Vous devez ouvrir une caisse avant d'effectuer un dépôt");
                    response.sendRedirect("CompteClientServlet?action=formCaisse");
                    return;
                }

                request.setAttribute("client", client);
                request.setAttribute("compte", compte);
                request.setAttribute("caisse", caisse);
                request.getRequestDispatcher("form-depot.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();

          System.out.println( "Erreur technique: " + e.getMessage());
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleFormPaiementCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String commandeIdStr = request.getParameter("commandeId");
            if (commandeIdStr == null || commandeIdStr.isEmpty()) {
                setErrorNotif(request, "Commande non spécifiée");
                response.sendRedirect("liste-commandes.jsp");
                return;
            }

            int commandeId = Integer.parseInt(commandeIdStr);
            Commande commande = commandeDAO.getCommandeById(commandeId);
            
            if (commande == null || commande.getClientId() == null) {
                setErrorNotif(request, "Commande ou client non trouvé");
                response.sendRedirect("liste-commandes.jsp");
                return;
            }

            // Vérifier si la commande est déjà payée
            if ("PAYE".equals(commande.getStatutPaiement())) {
                setErrorNotif(request, "Cette commande est déjà payée");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
                return;
            }

            // Récupérer le compte du client
            CompteClient compte = compteClientDAO.getCompteByClientId(commande.getClientId());
            if (compte == null) {
                // Créer le compte s'il n'existe pas
                compte = new CompteClient();
                compte.setClientId(commande.getClientId());
                compte.setSolde(BigDecimal.ZERO);
                compteClientDAO.creerCompteClient(compte);
            }

            Utilisateur client = utilisateurDAO.findById(commande.getClientId());

            request.setAttribute("commande", commande);
            request.setAttribute("compte", compte);
            request.setAttribute("client", client);
            request.setAttribute("userId", userId);

            request.getRequestDispatcher("form-paiement-compte.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("liste-commandes.jsp");
        }
    }

    private void handleListeCaisses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                                 !user.getRole().getRoleName().equals("MANAGEUR") &&
                                 !user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("index.jsp");
                return;
            }

            // Récupérer les filtres
            String caissiereIdStr = request.getParameter("caissiereId");
            String statutFilter = request.getParameter("statut");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            Integer caissiereId = null;
            Date dateDebut = null;
            Date dateFin = null;
            
            if (caissiereIdStr != null && !caissiereIdStr.isEmpty()) {
                caissiereId = Integer.parseInt(caissiereIdStr);
            }
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            // Récupérer les caisses
            List<CaisseCaissiere> caisses = caisseCaissiereDAO.getHistoriqueCaisses(
                caissiereId, dateDebut, dateFin);
            
            // Filtrer par statut si spécifié
            if (statutFilter != null && !statutFilter.isEmpty() && !"TOUS".equals(statutFilter)) {
                caisses.removeIf(c -> !statutFilter.equals(c.getStatut()));
            }

            // Récupérer la liste des caissières
            List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE");

            request.setAttribute("caisses", caisses);
            request.setAttribute("caissieres", caissieres);
            request.setAttribute("selectedCaissiereId", caissiereIdStr);
            request.setAttribute("selectedStatut", statutFilter);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);

            request.getRequestDispatcher("liste-caisses.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleEtatCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                // Vérifier si le caissier a une caisse ouverte
                CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    // Pas de caisse ouverte, rediriger vers formulaire d'ouverture
                    response.sendRedirect("CompteClientServlet?action=formCaisse");
                    return;
                }
                caisseIdStr = String.valueOf(caisse.getId());
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CompteClientServlet?action=listeCaisses");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                !user.getRole().getRoleName().equals("MANAGEUR") &&
                caisse.getCaissiereId() != userId) {
                setErrorNotif(request, "Vous n'avez pas accès à cette caisse");
                response.sendRedirect("index.jsp");
                return;
            }

            // Récupérer les transactions
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);
            
            // Calculer les totaux
            BigDecimal totalVentes = BigDecimal.ZERO;
            BigDecimal totalDepots = BigDecimal.ZERO;
            BigDecimal totalRetraits = BigDecimal.ZERO;
            
            for (TransactionCaisse transaction : transactions) {
                switch (transaction.getTypeOperation()) {
                    case "VENTE":
                        totalVentes = totalVentes.add(transaction.getMontant());
                        break;
                    case "DEPOT":
                        totalDepots = totalDepots.add(transaction.getMontant());
                        break;
                    case "RETRAIT":
                        totalRetraits = totalRetraits.add(transaction.getMontant().abs());
                        break;
                }
            }
            
            // Calculer le solde théorique
            BigDecimal soldeTheorique = caisse.getSoldeInitial()
                .add(totalVentes)
                .add(totalDepots)
                .subtract(totalRetraits);
            
            BigDecimal difference = caisse.getSoldeFinal() != null ? 
                caisse.getSoldeFinal().subtract(soldeTheorique) : BigDecimal.ZERO;

            request.setAttribute("caisse", caisse);
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalVentes", totalVentes);
            request.setAttribute("totalDepots", totalDepots);
            request.setAttribute("totalRetraits", totalRetraits);
            request.setAttribute("soldeTheorique", soldeTheorique);
            request.setAttribute("difference", difference);

            request.getRequestDispatcher("etat-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleRapportCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                                 !user.getRole().getRoleName().equals("MANAGEUR"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("index.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            String dateStr = request.getParameter("date");
            
            if (caisseIdStr == null || caisseIdStr.isEmpty() || dateStr == null || dateStr.isEmpty()) {
                setErrorNotif(request, "Paramètres manquants");
                response.sendRedirect("CompteClientServlet?action=listeCaisses");
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            Date date = Date.valueOf(dateStr);
            
            // Générer le rapport
            Map<String, BigDecimal> rapport = transactionCaisseDAO.getRapportJournalier(caisseId, date);
            
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);

            request.setAttribute("rapport", rapport);
            request.setAttribute("caisse", caisse);
            request.setAttribute("date", date);

            request.getRequestDispatcher("rapport-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleHistoriqueCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                setErrorNotif(request, "Caisse non spécifiée");
                response.sendRedirect("CompteClientServlet?action=listeCaisses");
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CompteClientServlet?action=listeCaisses");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                !user.getRole().getRoleName().equals("MANAGEUR") &&
                caisse.getCaissiereId() != userId) {
                setErrorNotif(request, "Vous n'avez pas accès à cette caisse");
                response.sendRedirect("index.jsp");
                return;
            }

            // Récupérer les transactions
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);

            request.setAttribute("caisse", caisse);
            request.setAttribute("transactions", transactions);

            request.getRequestDispatcher("historique-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleGetSoldeJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.isEmpty()) {
                sendJsonError(response, "clientId requis", 400);
                return;
            }

            int clientId = Integer.parseInt(clientIdStr);
            BigDecimal solde = transactionCompteDAO.getSoldeClient(clientId);

            Map<String, Object> result = new HashMap<>();
            result.put("solde", solde);
            result.put("success", true);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(result, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handleGetHistoriqueJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String clientIdStr = request.getParameter("clientId");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            if (clientIdStr == null || clientIdStr.isEmpty()) {
                sendJsonError(response, "clientId requis", 400);
                return;
            }

            int clientId = Integer.parseInt(clientIdStr);
            Date dateDebut = null;
            Date dateFin = null;
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
                clientId, dateDebut, dateFin);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(transactions, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handleGetEtatGeneralJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Map<String, Object> etatGeneral = getEtatGeneralComptes();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(etatGeneral, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    // Méthode pour remplacer GestionCompteDAO.getEtatGeneralComptes()
    private Map<String, Object> getEtatGeneralComptes() {
        Map<String, Object> etatGeneral = new HashMap<>();
        
        List<CompteClient> comptes = compteClientDAO.getAllComptes();
        
        BigDecimal soldeTotal = BigDecimal.ZERO;
        int nombreComptes = comptes.size();
        int nombreClientsSoldePositif = 0;
        int nombreClientsSoldeNegatif = 0;
        
        for (CompteClient compte : comptes) {
            soldeTotal = soldeTotal.add(compte.getSolde());
            
            if (compte.getSolde().compareTo(BigDecimal.ZERO) > 0) {
                nombreClientsSoldePositif++;
            } else if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                nombreClientsSoldeNegatif++;
            }
        }
        
        // Caisses ouvertes
        List<CaisseCaissiere> caissesOuvertes = caisseCaissiereDAO.getHistoriqueCaisses(null, null, null);
        caissesOuvertes.removeIf(c -> !"OUVERTE".equals(c.getStatut()));
        
        // Commandes impayées
        List<Commande> commandesCredit = commandeDAO.getCommandesCreditAllClients(null, null, null);
        
        etatGeneral.put("soldeTotal", soldeTotal);
        etatGeneral.put("nombreComptes", nombreComptes);
        etatGeneral.put("nombreClientsSoldePositif", nombreClientsSoldePositif);
        etatGeneral.put("nombreClientsSoldeNegatif", nombreClientsSoldeNegatif);
        etatGeneral.put("nombreCaissesOuvertes", caissesOuvertes.size());
        etatGeneral.put("nombreCommandesCredit", commandesCredit.size());
        
        return etatGeneral;
    }

    private void handleGetRapportCaisseJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String caisseIdStr = request.getParameter("caisseId");
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                sendJsonError(response, "caisseId requis", 400);
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            Map<String, Object> rapport = genererRapportCaisse(caisseId);

            if (rapport == null) {
                sendJsonError(response, "Caisse non trouvée", 404);
                return;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(rapport, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    // Méthode pour remplacer GestionCompteDAO.genererRapportCaisse()
    private Map<String, Object> genererRapportCaisse(int caisseId) {
        Map<String, Object> rapport = new HashMap<>();
        
        CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
        if (caisse == null) {
            return null;
        }
        
        List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);
        
        // Calculer les totaux
        BigDecimal totalVentes = BigDecimal.ZERO;
        BigDecimal totalDepots = BigDecimal.ZERO;
        BigDecimal totalRetraits = BigDecimal.ZERO;
        
        for (TransactionCaisse transaction : transactions) {
            switch (transaction.getTypeOperation()) {
                case "VENTE":
                    totalVentes = totalVentes.add(transaction.getMontant());
                    break;
                case "DEPOT":
                    totalDepots = totalDepots.add(transaction.getMontant());
                    break;
                case "RETRAIT":
                    totalRetraits = totalRetraits.add(transaction.getMontant().abs());
                    break;
            }
        }
        
        BigDecimal soldeTheorique = caisse.getSoldeInitial()
            .add(totalVentes)
            .add(totalDepots)
            .subtract(totalRetraits);
        
        rapport.put("caisse", caisse);
        rapport.put("totalVentes", totalVentes);
        rapport.put("totalDepots", totalDepots);
        rapport.put("totalRetraits", totalRetraits);
        rapport.put("soldeTheorique", soldeTheorique);
        rapport.put("nombreTransactions", transactions.size());
        
        return rapport;
    }

    private void handleGetComptesJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            List<CompteClient> comptes = compteClientDAO.getAllComptes();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(comptes, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier le rôle
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String roleName = user.getRole().getRoleName();
            
            if ("ADMINISTRATEUR".equals(roleName) || "MANAGEUR".equals(roleName)) {
                // Dashboard administrateur
                handleDashboardAdmin(request, response, user);
            } else if ("CAISSIER(ERE)".equals(roleName)) {
                // Dashboard caissier
                handleDashboardCaissier(request, response, user);
            } else if ("CLIENT".equals(roleName)) {
                // Dashboard client
                handleDashboardClient(request, response, user);
            } else {
                // Autres rôles
                response.sendRedirect("index.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleDashboardAdmin(HttpServletRequest request, HttpServletResponse response, Utilisateur user)
            throws ServletException, IOException {
        
        // Récupérer les statistiques générales
        Map<String, Object> etatGeneral = getEtatGeneralComptes();
        
        // Récupérer les caisses ouvertes
        List<CaisseCaissiere> caissesOuvertes = caisseCaissiereDAO.getHistoriqueCaisses(null, null, null);
        caissesOuvertes.removeIf(c -> !"OUVERTE".equals(c.getStatut()));
        
        // Récupérer les comptes avec solde négatif
        List<CompteClient> comptesNegatifs = new java.util.ArrayList<>();
        for (CompteClient compte : compteClientDAO.getAllComptes()) {
            if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
                comptesNegatifs.add(compte);
            }
        }

        request.setAttribute("etatGeneral", etatGeneral);
        request.setAttribute("caissesOuvertes", caissesOuvertes);
        request.setAttribute("comptesNegatifs", comptesNegatifs);
        request.setAttribute("user", user);

        request.getRequestDispatcher("dashboard-admin-comptes.jsp").forward(request, response);
    }

    private void handleDashboardCaissier(HttpServletRequest request, HttpServletResponse response, Utilisateur user)
            throws ServletException, IOException {
        
        // Vérifier si une caisse est ouverte
        CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(user.getId());
        
        if (caisse == null) {
            // Pas de caisse ouverte, rediriger vers formulaire d'ouverture
            request.getRequestDispatcher("form-ouverture-caisse.jsp").forward(request, response);
            return;
        }
        
        // Récupérer les transactions de la journée
        java.sql.Date aujourdhui = new java.sql.Date(System.currentTimeMillis());
        Map<String, BigDecimal> rapportJournalier = transactionCaisseDAO.getRapportJournalier(
            caisse.getId(), aujourdhui);
        
        // Récupérer la dernière caisse fermée
        CaisseCaissiere derniereCaisseFermee = caisseCaissiereDAO.getDerniereCaisseFermee(user.getId());

        request.setAttribute("caisse", caisse);
        request.setAttribute("rapportJournalier", rapportJournalier);
        request.setAttribute("derniereCaisseFermee", derniereCaisseFermee);
        request.setAttribute("user", user);

        request.getRequestDispatcher("dashboard-caissier.jsp").forward(request, response);
    }

    private void handleDashboardClient(HttpServletRequest request, HttpServletResponse response, Utilisateur user)
            throws ServletException, IOException {
        
        // Récupérer l'état du compte
        Map<String, Object> etatCompte = getEtatCompteClient(user.getId());
        
        if (etatCompte == null) {
            // Créer le compte s'il n'existe pas
            CompteClient compte = new CompteClient();
            compte.setClientId(user.getId());
            compte.setSolde(BigDecimal.ZERO);
            compteClientDAO.creerCompteClient(compte);
            etatCompte = getEtatCompteClient(user.getId());
        }
        
        // Récupérer les dernières transactions
        List<TransactionCompte> dernieresTransactions = transactionCompteDAO.getHistoriqueClient(
            user.getId(), null, null);
        if (dernieresTransactions.size() > 10) {
            dernieresTransactions = dernieresTransactions.subList(0, 10);
        }

        request.setAttribute("etatCompte", etatCompte);
        request.setAttribute("dernieresTransactions", dernieresTransactions);
        request.setAttribute("user", user);

        request.getRequestDispatcher("dashboard-client.jsp").forward(request, response);
    }

    private void handleSearchClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("searchTerm");
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                sendJsonError(response, "Terme de recherche requis", 400);
                return;
            }

            List<Utilisateur> clients = utilisateurDAO.searchClients(searchTerm);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(clients, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handlePrintReleve(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            int clientId;
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                clientId = Integer.parseInt(clientIdStr);
            } else {
                clientId = userId;
            }

            Date dateDebut = null;
            Date dateFin = null;
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            // Récupérer les données
            Utilisateur client = utilisateurDAO.findById(clientId);
            CompteClient compte = compteClientDAO.getCompteByClientId(clientId);
            List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
                clientId, dateDebut, dateFin);

            // Préparer le HTML pour impression
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateImpression = sdf.format(new java.util.Date());

            request.setAttribute("client", client);
            request.setAttribute("compte", compte);
            request.setAttribute("transactions", transactions);
            request.setAttribute("dateDebut", dateDebutStr);
            request.setAttribute("dateFin", dateFinStr);
            request.setAttribute("dateImpression", dateImpression);

            request.getRequestDispatcher("print-releve-compte.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=etatCompte");
        }
    }

    private void handleExportExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String typeExport = request.getParameter("typeExport");
            
            if ("transactions".equals(typeExport)) {
                handleExportTransactionsExcel(request, response);
            } else if ("comptes".equals(typeExport)) {
                handleExportComptesExcel(request, response);
            } else if ("caisses".equals(typeExport)) {
                handleExportCaissesExcel(request, response);
            } else {
                setErrorNotif(request, "Type d'export non supporté");
                response.sendRedirect("index.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleExportTransactionsExcel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String clientIdStr = request.getParameter("clientId");
        String dateDebutStr = request.getParameter("dateDebut");
        String dateFinStr = request.getParameter("dateFin");
        
        int clientId = Integer.parseInt(clientIdStr);
        Date dateDebut = Date.valueOf(dateDebutStr);
        Date dateFin = Date.valueOf(dateFinStr);

        List<TransactionCompte> transactions = transactionCompteDAO.getHistoriqueClient(
            clientId, dateDebut, dateFin);
        Utilisateur client = utilisateurDAO.findById(clientId);

        // Créer le fichier Excel
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=transactions_" + 
            client.getNom() + "_" + dateDebutStr + "_" + dateFinStr + ".xls");
        
        // Écrire le contenu Excel (simplifié - utiliser une bibliothèque comme Apache POI en production)
        response.getWriter().write("Date\tType\tMontant\tSolde Avant\tSolde Après\tNotes\n");
        
        for (TransactionCompte transaction : transactions) {
            response.getWriter().write(
                transaction.getDateTransaction() + "\t" +
                transaction.getTypeTransaction().getLibelle() + "\t" +
                transaction.getMontant() + "\t" +
                transaction.getSoldeAvant() + "\t" +
                transaction.getSoldeApres() + "\t" +
                (transaction.getNotes() != null ? transaction.getNotes() : "") + "\n"
            );
        }
    }

    private void handleFormCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier si c'est un caissier
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || !user.getRole().getRoleName().equals("CAISSIER(ERE)")) {
                setErrorNotif(request, "Seuls les caissiers peuvent gérer les caisses");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est déjà ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse != null) {
                // Rediriger vers l'état de la caisse
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisse.getId());
                return;
            }

            // Récupérer la dernière caisse fermée pour pré-remplir le solde initial
            CaisseCaissiere derniereCaisse = caisseCaissiereDAO.getDerniereCaisseFermee(userId);
            BigDecimal soldeInitial = BigDecimal.ZERO;
            if (derniereCaisse != null && derniereCaisse.getSoldeFinal() != null) {
                soldeInitial = derniereCaisse.getSoldeFinal();
            }

            request.setAttribute("soldeInitial", soldeInitial);
            request.setAttribute("derniereCaisse", derniereCaisse);
            request.setAttribute("user", user);

            request.getRequestDispatcher("form-ouverture-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleDetailTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String transactionIdStr = request.getParameter("transactionId");
            String typeTransaction = request.getParameter("type"); // "compte" ou "caisse"
            
            if (transactionIdStr == null || typeTransaction == null) {
                setErrorNotif(request, "Paramètres manquants");
                response.sendRedirect("index.jsp");
                return;
            }

            int transactionId = Integer.parseInt(transactionIdStr);
            
            if ("compte".equals(typeTransaction)) {
                // TODO: Implémenter la récupération détaillée d'une transaction compte
                setErrorNotif(request, "Détail transaction compte non implémenté");
                response.sendRedirect("index.jsp");
            } else if ("caisse".equals(typeTransaction)) {
                // TODO: Implémenter la récupération détaillée d'une transaction caisse
                setErrorNotif(request, "Détail transaction caisse non implémenté");
                response.sendRedirect("index.jsp");
            } else {
                setErrorNotif(request, "Type de transaction invalide");
                response.sendRedirect("index.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    // ======== HANDLERS POST ========

    private void handleCreerCompte(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String soldeInitialStr = request.getParameter("soldeInitial");
            
            if (clientIdStr == null || clientIdStr.isEmpty()) {
                setErrorNotif(request, "Client ID requis");
                response.sendRedirect("CompteClientServlet?action=listeComptes");
                return;
            }

            int clientId = Integer.parseInt(clientIdStr);
            BigDecimal soldeInitial = BigDecimal.ZERO;
            
            if (soldeInitialStr != null && !soldeInitialStr.isEmpty()) {
                soldeInitial = new BigDecimal(soldeInitialStr);
            }

            // Vérifier si le compte existe déjà
            CompteClient compteExistant = compteClientDAO.getCompteByClientId(clientId);
            if (compteExistant != null) {
                setErrorNotif(request, "Un compte existe déjà pour ce client");
                response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientId);
                return;
            }

            // Créer le compte
            CompteClient compte = new CompteClient();
            compte.setClientId(clientId);
            compte.setSolde(soldeInitial);
            
            int compteId = compteClientDAO.creerCompteClient(compte);
            
            if (compteId > 0) {
                setSuccessNotif(request, "Compte créé avec succès pour le client");
                response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientId);
            } else {
                setErrorNotif(request, "Erreur lors de la création du compte");
                response.sendRedirect("CompteClientServlet?action=listeComptes");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=listeComptes");
        }
    }

    private void handleEffectuerDepot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer caissiereId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (caissiereId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String montantStr = request.getParameter("montant");
            String modePaiement = request.getParameter("modePaiement");
            String notes = request.getParameter("notes");
            
            if (clientIdStr == null || montantStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CompteClientServlet?action=formDepot");
                return;
            }

            int clientId = Integer.parseInt(clientIdStr);
            BigDecimal montant = new BigDecimal(montantStr);
            
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                setErrorNotif(request, "Le montant doit être positif");
                response.sendRedirect("CompteClientServlet?action=formDepot&clientId=" + clientId);
                return;
            }

            // Vérifier que le caissier a une caisse ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(caissiereId);
            if (caisse == null) {
                setErrorNotif(request, "Vous devez ouvrir une caisse avant d'effectuer un dépôt");
                response.sendRedirect("CompteClientServlet?action=formCaisse");
                return;
            }

            // Effectuer le dépôt
            boolean success = transactionCompteDAO.effectuerDepot(clientId, montant, caissiereId, notes);
            
            if (success) {
                // Enregistrer également dans la caisse
                transactionCaisseDAO.enregistrerDepot(caisse.getId(), montant, modePaiement, clientId, notes);
                
                setSuccessNotif(request, "Dépôt de " + montant + " HTG effectué avec succès");
                response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientId);
            } else {
                setErrorNotif(request, "Erreur lors du dépôt");
                response.sendRedirect("CompteClientServlet?action=formDepot&clientId=" + clientId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=formDepot");
        }
    }

    private void handleEffectuerRetrait(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String clientIdStr = request.getParameter("clientId");
            String montantStr = request.getParameter("montant");
            String raison = request.getParameter("raison");
            String description = request.getParameter("description");
            
            if (clientIdStr == null || montantStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientIdStr);
                return;
            }

            int clientId = Integer.parseInt(clientIdStr);
            BigDecimal montant = new BigDecimal(montantStr);
            
            // Vérifier que l'utilisateur a assez de solde
            BigDecimal solde = transactionCompteDAO.getSoldeClient(clientId);
            if (montant.compareTo(solde) > 0) {
                setErrorNotif(request, "Solde insuffisant. Solde disponible: " + solde + " HTG");
                response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientId);
                return;
            }

            // TODO: Implémenter la logique de retrait
            setErrorNotif(request, "Fonctionnalité retrait non implémentée");
            response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + clientId);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=etatCompte&clientId=" + request.getParameter("clientId"));
        }
    }

    private void handlePayerCommandeViaCompte(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer caissiereId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (caissiereId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String commandeIdStr = request.getParameter("commandeId");
            if (commandeIdStr == null || commandeIdStr.isEmpty()) {
                setErrorNotif(request, "Commande non spécifiée");
                response.sendRedirect("liste-commandes.jsp");
                return;
            }

            int commandeId = Integer.parseInt(commandeIdStr);
            
            // Payer la commande via compte
            boolean success = transactionCompteDAO.payerCommandeViaCompte(commandeId, caissiereId);
            
            if (success) {
                setSuccessNotif(request, "Commande payée avec succès via compte client");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            } else {
                setErrorNotif(request, "Erreur lors du paiement de la commande. Solde insuffisant?");
                response.sendRedirect("CompteClientServlet?action=formPaiementCommande&commandeId=" + commandeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CommandeServlet?action=detail&id=" + request.getParameter("commandeId"));
        }
    }

    private void handlePayerCommandeMixte(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer caissiereId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (caissiereId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String commandeIdStr = request.getParameter("commandeId");
            String montantCompteStr = request.getParameter("montantCompte");
            String montantEspecesStr = request.getParameter("montantEspeces");
            String modePaiementEspeces = request.getParameter("modePaiementEspeces");
            
            if (commandeIdStr == null || montantCompteStr == null || montantEspecesStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CompteClientServlet?action=formPaiementCommande&commandeId=" + commandeIdStr);
                return;
            }

            int commandeId = Integer.parseInt(commandeIdStr);
            BigDecimal montantCompte = new BigDecimal(montantCompteStr);
            BigDecimal montantEspeces = new BigDecimal(montantEspecesStr);
            
            // Vérifier que le caissier a une caisse ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(caissiereId);
            if (caisse == null) {
                setErrorNotif(request, "Vous devez ouvrir une caisse avant d'effectuer un paiement mixte");
                response.sendRedirect("CompteClientServlet?action=formCaisse");
                return;
            }
            
            // Effectuer le paiement mixte manuellement
            boolean success = effectuerPaiementMixte(
                commandeId, caissiereId, montantCompte, montantEspeces, modePaiementEspeces);
            
            if (success) {
                setSuccessNotif(request, "Paiement mixte effectué avec succès: " + 
                    montantCompte + " HTG via compte + " + montantEspeces + " HTG en " + modePaiementEspeces);
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            } else {
                setErrorNotif(request, "Erreur lors du paiement mixte");
                response.sendRedirect("CompteClientServlet?action=formPaiementCommande&commandeId=" + commandeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CommandeServlet?action=detail&id=" + request.getParameter("commandeId"));
        }
    }

    // Méthode pour remplacer GestionCompteDAO.effectuerPaiementMixte()
    private boolean effectuerPaiementMixte(int commandeId, int caissiereId, 
            BigDecimal montantCompte, BigDecimal montantEspeces, String modePaiementEspeces) {
        try {
            // Récupérer la commande
            Commande commande = commandeDAO.getCommandeById(commandeId);
            if (commande == null || commande.getClientId() == null) {
                return false;
            }
            
            // Vérifier le solde si paiement par compte
            if (montantCompte.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal solde = transactionCompteDAO.getSoldeClient(commande.getClientId());
                if (solde.compareTo(montantCompte) < 0) {
                    return false;
                }
                
                // Créer une transaction de dépense pour la partie compte
                TransactionCompte transaction = new TransactionCompte();
                transaction.setCompteClientId(commande.getClientId());
                transaction.setTypeTransactionId(getTypeTransactionId("DEPENSE"));
                transaction.setMontant(montantCompte);
                transaction.setSoldeAvant(solde);
                transaction.setSoldeApres(solde.subtract(montantCompte));
                transaction.setCaissiereId(caissiereId);
                transaction.setCommandeId(commandeId);
                transaction.setNotes("Paiement mixte commande " + commande.getNumeroCommande() + 
                                   " - Partie compte: " + montantCompte + " HTG");
                
                if (transactionCompteDAO.creerTransaction(transaction) == -1) {
                    return false;
                }
            }
            
            // Enregistrer la partie espèces dans la caisse
            if (montantEspeces.compareTo(BigDecimal.ZERO) > 0) {
                CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(caissiereId);
                if (caisse != null) {
                    transactionCaisseDAO.enregistrerDepot(
                        caisse.getId(), 
                        montantEspeces, 
                        modePaiementEspeces, 
                        commande.getClientId(), 
                        "Paiement mixte commande " + commande.getNumeroCommande() + 
                        " - Partie espèces: " + montantEspeces + " HTG"
                    );
                }
            }
            
            // Mettre à jour la commande
            return commandeDAO.updateCommandeStatutPaiement(caissiereId, commandeId, "PAYE", "CASH");
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getTypeTransactionId(String code) {
        // Méthode simplifiée - normalement via un DAO
        switch (code) {
            case "DEPOT": return 1;
            case "DEPENSE": return 2;
            default: return -1;
        }
    }

    private void handleOuvrirCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer caissiereId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (caissiereId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String soldeInitialStr = request.getParameter("soldeInitial");
            BigDecimal soldeInitial = BigDecimal.ZERO;
            
            if (soldeInitialStr != null && !soldeInitialStr.isEmpty()) {
                soldeInitial = new BigDecimal(soldeInitialStr);
            }

            // Vérifier si une caisse est déjà ouverte
            CaisseCaissiere caisseExistante = caisseCaissiereDAO.getCaisseOuverte(caissiereId);
            if (caisseExistante != null) {
                setErrorNotif(request, "Une caisse est déjà ouverte pour cette caissière");
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseExistante.getId());
                return;
            }

            // Créer la caisse
            CaisseCaissiere caisse = new CaisseCaissiere();
            caisse.setCaissiereId(caissiereId);
            caisse.setSoldeInitial(soldeInitial);
            
            int caisseId = caisseCaissiereDAO.ouvrirCaisse(caisse);
            
            if (caisseId > 0) {
                setSuccessNotif(request, "Caisse ouverte avec succès. Solde initial: " + soldeInitial + " HTG");
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseId);
            } else {
                setErrorNotif(request, "Erreur lors de l'ouverture de la caisse");
                response.sendRedirect("CompteClientServlet?action=formCaisse");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=formCaisse");
        }
    }

//    private void handleFermerCaisse(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            HttpSession session = request.getSession(false);
//            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//            if (userId == null) {
//                response.sendRedirect("login.jsp");
//                return;
//            }
//
//            String caisseIdStr = request.getParameter("caisseId");
//            String soldeFinalStr = request.getParameter("soldeFinal");
//            
//            if (caisseIdStr == null || soldeFinalStr == null) {
//                setErrorNotif(request, "Paramètres requis manquants");
//                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseIdStr);
//                return;
//            }
//
//            int caisseId = Integer.parseInt(caisseIdStr);
//            BigDecimal soldeFinal = new BigDecimal(soldeFinalStr);
//            
//            // Vérifier que l'utilisateur a le droit de fermer cette caisse
//            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
//            if (caisse == null) {
//                setErrorNotif(request, "Caisse non trouvée");
//                response.sendRedirect("CompteClientServlet?action=listeCaisses");
//                return;
//            }
//            
//            if (caisse.getCaissiereId() != userId) {
//                Utilisateur user = utilisateurDAO.findById(userId);
//                if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//                    !user.getRole().getRoleName().equals("MANAGEUR")) {
//                    setErrorNotif(request, "Vous ne pouvez fermer que votre propre caisse");
//                    response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseId);
//                    return;
//                }
//            }
//
//            // Fermer la caisse
//            boolean success = caisseCaissiereDAO.fermerCaisse(caisseId, soldeFinal);
//            
//            if (success) {
//                // Calculer l'écart
//                BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);
//                BigDecimal ecart = soldeFinal.subtract(soldeTheorique);
//                
//                String message = "Caisse fermée avec succès. ";
//                if (ecart.compareTo(BigDecimal.ZERO) == 0) {
//                    message += "Aucun écart détecté.";
//                } else {
//                    message += "Écart détecté: " + ecart + " HTG";
//                }
//                
//                setSuccessNotif(request, message);
//                response.sendRedirect("CompteClientServlet?action=rapportCaisse&caisseId=" + caisseId);
//            } else {
//                setErrorNotif(request, "Erreur lors de la fermeture de la caisse");
//                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseId);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            setErrorNotif(request, "Erreur technique: " + e.getMessage());
//            response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + request.getParameter("caisseId"));
//        }
//    }
    private void handleFermerCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            String soldeFinalStr = request.getParameter("soldeFinal");
            String shotStr = request.getParameter("shot");
            String montantShotStr = request.getParameter("montantShot");
            String notes = request.getParameter("notes");
            
            if (caisseIdStr == null || soldeFinalStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CaisseCaissiereServlet?action=etatCaisse&caisseId=" + caisseIdStr);
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            BigDecimal soldeFinal = new BigDecimal(soldeFinalStr);
            
            // Déterminer si la caisse est shot
            boolean shot = false;
            BigDecimal montantShot = BigDecimal.ZERO;
            
            if (shotStr != null && "true".equals(shotStr)) {
                shot = true;
                if (montantShotStr != null && !montantShotStr.isEmpty()) {
                    montantShot = new BigDecimal(montantShotStr);
                }
            }
            
            // Vérifier que l'utilisateur a le droit de fermer cette caisse
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }
            
            if (caisse.getCaissiereId() != userId) {
                Utilisateur user = utilisateurDAO.findById(userId);
                if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                    !user.getRole().getRoleName().equals("MANAGEUR")) {
                    setErrorNotif(request, "Vous ne pouvez fermer que votre propre caisse");
                    response.sendRedirect("CaisseCaissiereServlet?action=etatCaisse&caisseId=" + caisseId);
                    return;
                }
            }

            // Calculer le solde théorique
            BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);
            BigDecimal ecart = soldeFinal.subtract(soldeTheorique);
            
            // Validation : si shot est true, montantShot doit être > 0
            if (shot && montantShot.compareTo(BigDecimal.ZERO) <= 0) {
                setErrorNotif(request, "Si la caisse est SHOT, vous devez spécifier le montant du déficit");
                response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse&caisseId=" + caisseId);
                return;
            }

            // Fermer la caisse avec les nouveaux paramètres
            boolean success = caisseCaissiereDAO.fermerCaisse(caisseId, soldeFinal, shot, montantShot);
            
            if (success) {
                String message = "Caisse fermée avec succès. ";
                
                if (shot) {
                    message += "SHOT déclaré: " + montantShot + " HTG. ";
                    message += "La caissière doit: " + montantShot + " HTG. ";
                }
                
                if (ecart.compareTo(BigDecimal.ZERO) == 0) {
                    message += "Aucun écart détecté.";
                } else {
                    message += "Écart détecté: " + ecart + " HTG";
                }
                
                if (notes != null && !notes.trim().isEmpty()) {
                    message += " - Notes: " + notes;
                }
                
                // Si shot, enregistrer une transaction spécifique
                if (shot) {
                    TransactionCaisse transaction = new TransactionCaisse();
                    transaction.setCaisseId(caisseId);
                    transaction.setTypeOperation("RETRAIT");
                    transaction.setMontant(montantShot.negate());
                    transaction.setModePaiement("SHOT");
                    transaction.setNotes("Shot déclaré lors de la fermeture: " + montantShot + " HTG" + 
                                       (notes != null ? " - " + notes : ""));
                    transaction.setDateOperation(new Timestamp(System.currentTimeMillis()));
                    
                    transactionCaisseDAO.enregistrerTransaction(transaction);
                }
                
                setSuccessNotif(request, message);
                response.sendRedirect("CaisseCaissiereServlet?action=detailCaisse&caisseId=" + caisseId);
            } else {
                setErrorNotif(request, "Erreur lors de la fermeture de la caisse");
                response.sendRedirect("CaisseCaissiereServlet?action=etatCaisse&caisseId=" + caisseId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CaisseCaissiereServlet?action=etatCaisse&caisseId=" + request.getParameter("caisseId"));
        }
    }

    private void handleEnregistrerTransactionCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            String typeOperation = request.getParameter("typeOperation");
            String montantStr = request.getParameter("montant");
            String modePaiement = request.getParameter("modePaiement");
            String clientIdStr = request.getParameter("clientId");
            String notes = request.getParameter("notes");
            
            if (caisseIdStr == null || typeOperation == null || montantStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseIdStr);
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            BigDecimal montant = new BigDecimal(montantStr);
            
            // Pour les retraits, le montant doit être négatif
            if ("RETRAIT".equals(typeOperation)) {
                montant = montant.negate();
            }
            
            Integer clientId = null;
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                clientId = Integer.parseInt(clientIdStr);
            }

            // Créer la transaction
            TransactionCaisse transaction = new TransactionCaisse();
            transaction.setCaisseId(caisseId);
            transaction.setTypeOperation(typeOperation);
            transaction.setMontant(montant);
            transaction.setModePaiement(modePaiement);
            transaction.setClientId(clientId);
            transaction.setNotes(notes);
            
            int transactionId = transactionCaisseDAO.enregistrerTransaction(transaction);
            
            if (transactionId > 0) {
                String operation = "RETRAIT".equals(typeOperation) ? "retrait" : 
                                  "DEPOT".equals(typeOperation) ? "dépôt" : "transaction";
                setSuccessNotif(request, operation + " enregistré avec succès");
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseId);
            } else {
                setErrorNotif(request, "Erreur lors de l'enregistrement de la transaction");
                response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + caisseId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=etatCaisse&caisseId=" + request.getParameter("caisseId"));
        }
    }

    private void handleEnregistrerVente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer caissiereId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (caissiereId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String commandeIdStr = request.getParameter("commandeId");
            String montantStr = request.getParameter("montant");
            String modePaiement = request.getParameter("modePaiement");
            
            if (commandeIdStr == null || montantStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeIdStr);
                return;
            }

            int commandeId = Integer.parseInt(commandeIdStr);
            BigDecimal montant = new BigDecimal(montantStr);
            
            // Vérifier que le caissier a une caisse ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(caissiereId);
            if (caisse == null) {
                setErrorNotif(request, "Vous devez ouvrir une caisse avant d'enregistrer une vente");
                response.sendRedirect("CompteClientServlet?action=formCaisse");
                return;
            }

            // Enregistrer la vente
            boolean success = transactionCaisseDAO.enregistrerVente(caisse.getId(), commandeId, montant, modePaiement);
            
            if (success) {
                setSuccessNotif(request, "Vente enregistrée dans la caisse");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            } else {
                setErrorNotif(request, "Erreur lors de l'enregistrement de la vente");
                response.sendRedirect("CommandeServlet?action=detail&id=" + commandeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CommandeServlet?action=detail&id=" + request.getParameter("commandeId"));
        }
    }

    private void handleAjusterSolde(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                                 !user.getRole().getRoleName().equals("MANAGEUR"))) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("index.jsp");
                return;
            }

            String compteIdStr = request.getParameter("compteId");
            String nouveauSoldeStr = request.getParameter("nouveauSolde");
            String raison = request.getParameter("raison");
            
            if (compteIdStr == null || nouveauSoldeStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CompteClientServlet?action=listeComptes");
                return;
            }

            int compteId = Integer.parseInt(compteIdStr);
            BigDecimal nouveauSolde = new BigDecimal(nouveauSoldeStr);
            
            // Mettre à jour le solde
            boolean success = compteClientDAO.mettreAJourSolde(compteId, nouveauSolde);
            
            if (success) {
                // TODO: Enregistrer une transaction d'ajustement
                setSuccessNotif(request, "Solde ajusté avec succès");
                response.sendRedirect("CompteClientServlet?action=listeComptes");
            } else {
                setErrorNotif(request, "Erreur lors de l'ajustement du solde");
                response.sendRedirect("CompteClientServlet?action=listeComptes");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=listeComptes");
        }
    }

    private void handleInitialiserComptes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Vérifier les permissions
//            Utilisateur user = utilisateurDAO.findById(userId);
//            if (user == null || !user.getRole().getRoleName().equals("ADMINISTRATEUR")) {
//                setErrorNotif(request, "Permission refusée");
//                response.sendRedirect("index.jsp");
//                return;
//            }

            // Initialiser les comptes
            compteClientDAO.initialiserComptesClients();
            
            setSuccessNotif(request, "Comptes clients initialisés avec succès");
            response.sendRedirect("CompteClientServlet?action=listeComptes");

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CompteClientServlet?action=listeComptes");
        }
    }

    // ======== MÉTHODES UTILITAIRES ========

    private void sendJsonError(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("success", false);
        
        gson.toJson(error, response.getWriter());
    }

    private void setSuccessNotif(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("toastMessage", message);
        session.setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("toastMessage", message);
        session.setAttribute("toastType", "error");
    }

    // Méthodes de placeholder pour l'export Excel
    private void handleExportComptesExcel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Implémentation similaire à handleExportTransactionsExcel
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Export comptes non implémenté");
    }

    private void handleExportCaissesExcel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Implémentation similaire à handleExportTransactionsExcel
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Export caisses non implémenté");
    }
}