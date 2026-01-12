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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/CaisseCaissiereServlet", "/blok/CaisseCaissiereServlet"})
public class CaisseCaissiereServlet extends HttpServlet {

    private CaisseCaissiereDAO caisseCaissiereDAO;
    private TransactionCaisseDAO transactionCaisseDAO;
    private UtilisateurDAO utilisateurDAO;
    private CommandeDAO commandeDAO;
    private TransactionCompteDAO transactionCompteDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        caisseCaissiereDAO = new CaisseCaissiereDAO();
        transactionCaisseDAO = new TransactionCaisseDAO();
        utilisateurDAO = new UtilisateurDAO();
        commandeDAO = new CommandeDAO();
        transactionCompteDAO = new TransactionCompteDAO();
        
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
            case "ouvrirCaisse":
                handleOuvrirCaisse(request, response);
                break;
            case "fermerCaisse":
                handleFermerCaisse(request, response);
                break;
            case "enregistrerTransaction":
                handleEnregistrerTransaction(request, response);
                break;
            case "enregistrerVente":
                handleEnregistrerVente(request, response);
                break;
            case "genererRapport":
                handleGenererRapport(request, response);
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
            handleDashboardCaisse(request, response);
            return;
        }

        switch (action) {
     // Ajoutez dans le switch du doGet
        case "etatCompteCaissiere":
            handleEtatCompteCaissiere(request, response);
            break;
            case "dashboard":
                handleDashboardCaisse(request, response);
                break;
            case "formOuvrirCaisse":
                handleFormOuvrirCaisse(request, response);
                break;
            case "formFermerCaisse":
                handleFormFermerCaisse(request, response);
                break;
            case "etatCaisse":
                handleEtatCaisse(request, response);
                break;
            case "compteClient":
                handleCompteClient(request, response);
                break;
            case "compteServeuse":
                handleCompteServeuse(request, response);
                break;
            case "compteDirect":
                handleCompteDirect(request, response);
                break;
            case "controleRemettre":
                handleControleRemettre(request, response);
                break;
            case "historiqueTransactions":
                handleHistoriqueTransactions(request, response);
                break;
            case "rapportJournalier":
                handleRapportJournalier(request, response);
                break;
            case "listeCaisses":
                handleListeCaisses(request, response);
                break;
            case "detailCaisse":
                handleDetailCaisse(request, response);
                break;
            case "getCaisseOuverteJSON":
                handleGetCaisseOuverteJSON(request, response);
                break;
            case "getSoldeTheoriqueJSON":
                handleGetSoldeTheoriqueJSON(request, response);
                break;
            case "getTransactionsCaisseJSON":
                handleGetTransactionsCaisseJSON(request, response);
                break;
            case "exportExcel":
                handleExportExcel(request, response);
                break;
            case "printRapport":
                handlePrintRapport(request, response);
                break;
            default:
                handleDashboardCaisse(request, response);
                break;
        }
    }

    // ======== HANDLERS GET ========
    private void handleEtatCompteCaissiere(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("=== DÉBUT handleEtatCompteCaissiere ===");
            
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Récupérer les paramètres
            String caissiereIdStr = request.getParameter("caissiereId");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            // Récupérer la liste des caissières
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE)");
            
            // Déterminer la caissière sélectionnée
            Integer selectedCaissiereId = null;
            if (caissiereIdStr != null && !caissiereIdStr.isEmpty()) {
                selectedCaissiereId = Integer.parseInt(caissiereIdStr);
            } else if (caissieres != null && !caissieres.isEmpty()) {
                selectedCaissiereId = caissieres.get(0).getId();
            }
            
            // Récupérer la caissière sélectionnée
            Utilisateur selectedCaissiere = null;
            if (selectedCaissiereId != null) {
                selectedCaissiere = utilisateurDAO.findById(selectedCaissiereId);
            }
            
            // Convertir les dates
            Timestamp dateDebut = null;
            Timestamp dateFin = null;
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
            
            try {
                if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                    Date parsedDate = (Date) sdfInput.parse(dateDebutStr);
                    dateDebut = new Timestamp(parsedDate.getTime());
                }
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    Date parsedDate = (Date) sdfInput.parse(dateFinStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    dateFin = new Timestamp(cal.getTime().getTime());
                }
            } catch (Exception e) {
                System.out.println("Erreur parsing dates: " + e.getMessage());
            }
            
            // Récupérer les données du tableau
//            List<Map<String, Object>> tableauEtats = new ArrayList<>();
//            
//            if (selectedCaissiereId != null) {
//                // Récupérer les rapports de caisse pour cette caissière dans la période
//                CaisseCaissiereDAO caisseDAO = new CaisseCaissiereDAO();
//                
//                // Convertir Timestamp en Date pour la méthode getHistoriqueCaisses
//                Date dateDebutSql = null;
//                Date dateFinSql = null;
//                if (dateDebut != null) {
//                    dateDebutSql = new Date(dateDebut.getTime());
//                }
//                if (dateFin != null) {
//                    dateFinSql = new Date(dateFin.getTime());
//                }
//                
//                List<CaisseCaissiere> rapports = caisseDAO.getHistoriqueCaisses(
//                    selectedCaissiereId, dateDebutSql, dateFinSql);
//                
//                // Pour chaque rapport, créer une ligne du tableau
//                for (CaisseCaissiere rapport : rapports) {
//                    Map<String, Object> ligne = new HashMap<>();
//                    
//                    // Informations de base
//                    ligne.put("dateOuverture", rapport.getOuverture());
//                    ligne.put("dateFermeture", rapport.getFermeture());
//                    ligne.put("statut", rapport.getStatut());
//                    ligne.put("libelle", "Rapport de contrôle");
//                    
//                    // Solde début = solde initial
//                    ligne.put("soldeDebut", rapport.getSoldeInitial());
//                    
//                    // Récupérer les totaux pour ce rapport
//                    // 1. Totaux des entrées (dépôts clients + ventes + dépôts caisse)
//                    TransactionCaisseDAO transactionDAO = new TransactionCaisseDAO();
//                    BigDecimal totalEntrees = BigDecimal.ZERO;
//                    
//                    // Dépôts clients - Utiliser la méthode corrigée
//                    List<Map<String, Object>> depotsClientsMap = caisseDAO.getDepotsCompteClient(
//                        rapport.getId(), null, null);
//                    for (Map<String, Object> depotMap : depotsClientsMap) {
//                        BigDecimal montant = (BigDecimal) depotMap.get("montant");
//                        if (montant != null) {
//                            totalEntrees = totalEntrees.add(montant);
//                        }
//                    }
//                    
//                    // Ventes (toutes les commandes)
//                    // Note: Ces méthodes retournent List<Map<String, Object>>, pas List<Commande>
//                    // Vous devez adapter votre code en conséquence
//                    List<Map<String, Object>> commandesClientsMap = caisseDAO.getCommandesEncaisseesParClient(
//                        rapport.getCaissiereId(), rapport.getId(), null, null);
//                    List<Map<String, Object>> commandesServeusesMap = caisseDAO.getCommandesEncaisseesParClient(
//                        rapport.getCaissiereId(), rapport.getId(), null, null);
//                    List<Map<String, Object>> commandesComptoirMap = caisseDAO.getCommandesCaisseDirecte(
//                        rapport.getCaissiereId(), rapport.getId(), null, null);
//                    
//                    for (Map<String, Object> cmdMap : commandesClientsMap) {
//                        String statutPaiement = (String) cmdMap.get("statut_paiement");
//                        BigDecimal montantTotal = (BigDecimal) cmdMap.get("montant_total");
//                        if ("PAYE".equals(statutPaiement) && montantTotal != null) {
//                            totalEntrees = totalEntrees.add(montantTotal);
//                        }
//                    }
//                    for (Map<String, Object> cmdMap : commandesServeusesMap) {
//                        String statutPaiement = (String) cmdMap.get("statut_paiement");
//                        BigDecimal montantTotal = (BigDecimal) cmdMap.get("montant_total");
//                        if ("PAYE".equals(statutPaiement) && montantTotal != null) {
//                            totalEntrees = totalEntrees.add(montantTotal);
//                        }
//                    }
//                    for (Map<String, Object> cmdMap : commandesComptoirMap) {
//                        String statutPaiement = (String) cmdMap.get("statut_paiement");
//                        BigDecimal montantTotal = (BigDecimal) cmdMap.get("montant_total");
//                        if ("PAYE".equals(statutPaiement) && montantTotal != null) {
//                            totalEntrees = totalEntrees.add(montantTotal);
//                        }
//                    }
//                    
//                    ligne.put("entrees", totalEntrees);
//                    
//                    // 2. Totaux des sorties (retraits + commandes clients non payées)
//                    BigDecimal totalSorties = BigDecimal.ZERO;
//                    
//                    // Retraits depuis transactions
////                    BigDecimal totalRetraits = transactionDAO.getTotalRetraitsByCaisse(rapport.getId());
////                    totalSorties = totalSorties.add(totalRetraits != null ? totalRetraits : BigDecimal.ZERO);
//         
//                    BigDecimal totalRetraits =  BigDecimal.ZERO;
//                    
//                    // Commandes clients non payées
//                    for (Map<String, Object> cmdMap : commandesClientsMap) {
//                        String statutPaiement = (String) cmdMap.get("statut_paiement");
//                        BigDecimal montantTotal = (BigDecimal) cmdMap.get("montant_total");
//                        if (!"PAYE".equals(statutPaiement) && montantTotal != null) {
//                            totalSorties = totalSorties.add(montantTotal);
//                        }
//                    }
//                    
//                    ligne.put("sorties", totalSorties);
//                    
//                    // Calcul du solde fin théorique
//                    // Solde fin = solde début + entrées - sorties
//                    BigDecimal soldeFinTheorique = rapport.getSoldeInitial()
//                        .add(totalEntrees)
//                        .subtract(totalSorties);
//                    
//                    ligne.put("soldeFinTheorique", soldeFinTheorique);
//                    
//                    // Si le rapport est fermé, montrer le solde final réel et les SHOTS
//                    if ("FERME_RAPPORT".equals(rapport.getStatut()) || "FERMEE".equals(rapport.getStatut())) {
//                        ligne.put("soldeFinReel", rapport.getSoldeFinal());
//                        ligne.put("shot", rapport.isShot());
//                        ligne.put("montantShot", rapport.getMontantShot());
//                        ligne.put("montantDonne", rapport.getMontantDonne());
//                    }
//                    
//                    tableauEtats.add(ligne);
//                }
//            }
            List<Map<String, Object>> tableauEtats = new ArrayList<>();
            
            if (selectedCaissiereId != null) {
                CaisseCaissiereDAO caisseDAO = new CaisseCaissiereDAO();
                
                // Récupérer les rapports de contrôle
                Date dateDebutSql = null;
                Date dateFinSql = null;
                if (dateDebut != null) {
                    dateDebutSql = new Date(dateDebut.getTime());
                }
                if (dateFin != null) {
                    dateFinSql = new Date(dateFin.getTime());
                }
                
                List<CaisseCaissiere> rapports = caisseDAO.getHistoriqueCaisses(
                    selectedCaissiereId, dateDebutSql, dateFinSql);
                
                // Pour chaque rapport de contrôle
                for (CaisseCaissiere rapport : rapports) {
                    // 1. Ligne pour le contrôle
                    Map<String, Object> ligneControle = new HashMap<>();
                    ligneControle.put("dateOuverture", rapport.getOuverture());
                    ligneControle.put("typeMouvement", "CONTROLE");
                    ligneControle.put("description", "Rapport de contrôle");
                    ligneControle.put("soldeDebut", rapport.getSoldeInitial());
                    
                    // Calculer les entrées et sorties pour ce contrôle
                    BigDecimal totalEntreesControle = BigDecimal.ZERO;
                    BigDecimal totalSortiesControle = BigDecimal.ZERO;
                    
                    // a) Dépôts sur caisse (DETOT_CAISSE)
                    List<Map<String, Object>> depotsCaisse = caisseDAO.getDepotsCaisse(
                        rapport.getId(), null, null);
                    
                    for (Map<String, Object> depot : depotsCaisse) {
                        Map<String, Object> ligneDepot = new HashMap<>();
                        Timestamp dateDepot = (Timestamp) depot.get("date_depot");
                        BigDecimal montant = (BigDecimal) depot.get("montant");
                        String commentaire = (String) depot.get("commentaire");
                        
                        if (montant != null) {
                            totalEntreesControle = totalEntreesControle.add(montant);
                            
                            // Créer une ligne pour ce dépôt
                            ligneDepot.put("dateOuverture", dateDepot);
                            ligneDepot.put("typeMouvement", "DETOT_CAISSE");
                            ligneDepot.put("description", commentaire != null ? commentaire : "Dépôt sur caisse");
                            ligneDepot.put("soldeDebut", null);
                            ligneDepot.put("entrees", montant);
                            ligneDepot.put("sorties", BigDecimal.ZERO);
                            tableauEtats.add(ligneDepot);
                        }
                    }
                    
                    // b) Dépôts clients
                    List<Map<String, Object>> depotsClients = caisseDAO.getDepotsCompteClient(
                        rapport.getId(), null, null);
                    
                    for (Map<String, Object> depot : depotsClients) {
                        Map<String, Object> ligneDepotClient = new HashMap<>();
                        Timestamp dateDepot = (Timestamp) depot.get("date_depot");
                        BigDecimal montant = (BigDecimal) depot.get("montant");
                        String clientNom = (String) depot.get("nom_client");
                        String clientPrenom = (String) depot.get("prenom_client");
                        
                        if (montant != null) {
                            totalEntreesControle = totalEntreesControle.add(montant);
                            
                            // Créer une ligne pour ce dépôt client
                            ligneDepotClient.put("dateOuverture", dateDepot);
                            ligneDepotClient.put("typeMouvement", "DEPOT_CLIENT");
                            ligneDepotClient.put("description", "Dépôt client: " + 
                                (clientNom != null ? clientNom + " " + clientPrenom : "Client"));
                            ligneDepotClient.put("soldeDebut", null);
                            ligneDepotClient.put("entrees", montant);
                            ligneDepotClient.put("sorties", BigDecimal.ZERO);
                            tableauEtats.add(ligneDepotClient);
                        }
                    }
                    
                    // c) Retraits (sorties)
                    BigDecimal totalRetraits = BigDecimal.ZERO;
                    // Utilisez votre méthode pour récupérer les retraits
                    // totalRetraits = transactionDAO.getTotalRetraitsByCaisse(rapport.getId());
                    totalSortiesControle = totalSortiesControle.add(totalRetraits != null ? totalRetraits : BigDecimal.ZERO);
                    
                    // d) Commandes (à adapter selon votre logique métier)
                    // ... (code existant pour les commandes)
                    
                    ligneControle.put("entrees", totalEntreesControle);
                    ligneControle.put("sorties", totalSortiesControle);
                    
                    // Calcul du solde fin théorique
                    BigDecimal soldeFinTheorique = rapport.getSoldeInitial()
                        .add(totalEntreesControle)
                        .subtract(totalSortiesControle);
                    ligneControle.put("soldeFinTheorique", soldeFinTheorique);
                    
                    // Informations de fermeture
                    if ("FERME_RAPPORT".equals(rapport.getStatut()) || "FERMEE".equals(rapport.getStatut())) {
                        ligneControle.put("dateFermeture", rapport.getFermeture());
                        ligneControle.put("soldeFinReel", rapport.getSoldeFinal());
                        ligneControle.put("shot", rapport.isShot());
                        ligneControle.put("montantShot", rapport.getMontantShot());
                        // Note: Le montantDonne n'est plus utilisé comme colonne séparée
                    }
                    
                    // Ajouter la ligne contrôle en premier
                    tableauEtats.add(0, ligneControle);
                }
                
                // Trier par date (plus ancien en premier)
                tableauEtats.sort((a, b) -> {
                    Timestamp dateA = (Timestamp) a.get("dateOuverture");
                    Timestamp dateB = (Timestamp) b.get("dateOuverture");
                    if (dateA == null && dateB == null) return 0;
                    if (dateA == null) return -1;
                    if (dateB == null) return 1;
                    return dateA.compareTo(dateB);
                });
            }
            
            // Passer les données à la JSP
            request.setAttribute("caissieres", caissieres);
            request.setAttribute("selectedCaissiereId", selectedCaissiereId);
            request.setAttribute("selectedCaissiere", selectedCaissiere);
            request.setAttribute("dateDebut", dateDebutStr);
            request.setAttribute("dateFin", dateFinStr);
            request.setAttribute("tableauEtats", tableauEtats);
            
            System.out.println("DEBUG: Nombre de lignes dans tableauEtats: " + tableauEtats.size());
            System.out.println("=== FIN handleEtatCompteCaissiere ===");
            
            request.getRequestDispatcher("etat-compte-caissiere.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("ERREUR CRITIQUE dans handleEtatCompteCaissiere: " + e.getMessage());
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }
    
    private void handleDashboardCaisse(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            boolean caisseOuverte = caisse != null;

            if (caisseOuverte) {
                // Calculer les statistiques
                BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisse.getId());
                
                // Récupérer les transactions de la journée
                java.sql.Date aujourdhui = new java.sql.Date(System.currentTimeMillis());
                List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisse.getId());
                
                // Calculer les totaux
                BigDecimal totalVentes = BigDecimal.ZERO;
                BigDecimal totalDepots = BigDecimal.ZERO;
                BigDecimal totalRetraits = BigDecimal.ZERO;
                
                for (TransactionCaisse transaction : transactions) {
                    if ("VENTE".equals(transaction.getTypeOperation())) {
                        totalVentes = totalVentes.add(transaction.getMontant());
                    } else if ("DEPOT".equals(transaction.getTypeOperation())) {
                        totalDepots = totalDepots.add(transaction.getMontant());
                    } else if ("RETRAIT".equals(transaction.getTypeOperation())) {
                        totalRetraits = totalRetraits.add(transaction.getMontant().abs());
                    }
                }
                
                request.setAttribute("caisse", caisse);
                request.setAttribute("soldeTheorique", soldeTheorique);
                request.setAttribute("totalVentes", totalVentes);
                request.setAttribute("totalDepots", totalDepots);
                request.setAttribute("totalRetraits", totalRetraits);
                request.setAttribute("nombreTransactions", transactions.size());
            }

            request.setAttribute("caisseOuverte", caisseOuverte);
            request.setAttribute("user", user);
            request.setAttribute("derniereCaisse", caisseCaissiereDAO.getDerniereCaisseFermee(userId));

            request.getRequestDispatcher("dashboard-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleFormOuvrirCaisse(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est déjà ouverte
            CaisseCaissiere caisseExistante = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisseExistante != null) {
                setErrorNotif(request, "Une caisse est déjà ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Récupérer la dernière caisse fermée pour pré-remplir
            CaisseCaissiere derniereCaisse = caisseCaissiereDAO.getDerniereCaisseFermee(userId);
            BigDecimal soldeInitial = BigDecimal.ZERO;
            boolean shot = false;
            
            if (derniereCaisse != null) {
                soldeInitial = derniereCaisse.getSoldeFinal() != null ? 
                    derniereCaisse.getSoldeFinal() : BigDecimal.ZERO;
                shot = derniereCaisse.isShot() != null ? derniereCaisse.isShot() : false;
            }

            request.setAttribute("soldeInitial", soldeInitial);
            request.setAttribute("shot", shot);
            request.setAttribute("derniereCaisse", derniereCaisse);
            request.setAttribute("user", user);

            request.getRequestDispatcher("form-ouvrir-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleFormFermerCaisse(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Calculer le solde théorique
            BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisse.getId());

            request.setAttribute("caisse", caisse);
            request.setAttribute("soldeTheorique", soldeTheorique);
            request.setAttribute("user", user);

            request.getRequestDispatcher("form-fermer-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

	/*
	 * private void handleEtatCaisse(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { try { HttpSession session =
	 * request.getSession(false); Integer userId = (session != null) ? (Integer)
	 * session.getAttribute("userId") : null; if (userId == null) {
	 * response.sendRedirect("login.jsp"); return; }
	 * 
	 * System.out.println("Erreur technique: ");
	 * 
	 * String caisseIdStr = request.getParameter("caisseId"); String caissiereIdStr
	 * = request.getParameter("caissiereId"); CaisseCaissiere caisse = null;
	 * 
	 * if (caisseIdStr != null) { // Vue d'une caisse spécifique int caisseId =
	 * Integer.parseInt(caisseIdStr); caisse =
	 * caisseCaissiereDAO.getCaisseById(caisseId);
	 * 
	 * // Vérifier les permissions // Utilisateur user =
	 * utilisateurDAO.findById(userId); // if
	 * (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && //
	 * !user.getRole().getRoleName().equals("MANAGEUR") && // (caisse == null ||
	 * caisse.getCaissiereId() != userId)) { // setErrorNotif(request,
	 * "Permission refusée"); // response.sendRedirect("index.jsp"); // return; // }
	 * } else if (caissiereIdStr != null) { // Vue de la caisse d'une caissière
	 * spécifique int caissiereId = Integer.parseInt(caissiereIdStr); caisse =
	 * caisseCaissiereDAO.getCaisseOuverte(caissiereId);
	 * 
	 * // Vérifier les permissions // Utilisateur user =
	 * utilisateurDAO.findById(userId); // boolean isAdmin =
	 * user.getRole().getRoleName().equals("ADMINISTRATEUR") || //
	 * user.getRole().getRoleName().equals("MANAGEUR"); // boolean isSelf =
	 * caissiereId == userId; // // if (!isAdmin && !isSelf) { //
	 * setErrorNotif(request, "Vous ne pouvez voir que votre propre caisse"); //
	 * response.sendRedirect("index.jsp"); // return; // } } else { // Par défaut,
	 * la caisse de l'utilisateur courant caisse =
	 * caisseCaissiereDAO.getCaisseOuverte(userId); }
	 * 
	 * // Passer la liste des caissières à la JSP List<Utilisateur> caissieres =
	 * utilisateurDAO.findByRole("CAISSIER(ERE)");
	 * request.setAttribute("caissieres", caissieres);
	 * 
	 * if (caisse != null) { // Récupérer les transactions List<TransactionCaisse>
	 * transactions = transactionCaisseDAO.getTransactionsByCaisse(caisse.getId());
	 * 
	 * // Calculer les totaux BigDecimal totalVentes = BigDecimal.ZERO; BigDecimal
	 * totalDepots = BigDecimal.ZERO; BigDecimal totalRetraits = BigDecimal.ZERO;
	 * 
	 * for (TransactionCaisse transaction : transactions) { if
	 * ("VENTE".equals(transaction.getTypeOperation())) { totalVentes =
	 * totalVentes.add(transaction.getMontant()); } else if
	 * ("DEPOT".equals(transaction.getTypeOperation())) { totalDepots =
	 * totalDepots.add(transaction.getMontant()); } else if
	 * ("RETRAIT".equals(transaction.getTypeOperation())) { totalRetraits =
	 * totalRetraits.add(transaction.getMontant().abs()); } }
	 * 
	 * BigDecimal soldeTheorique = caisse.getSoldeInitial() .add(totalVentes)
	 * .add(totalDepots) .subtract(totalRetraits);
	 * 
	 * request.setAttribute("caisse", caisse); request.setAttribute("transactions",
	 * transactions); request.setAttribute("totalVentes", totalVentes);
	 * request.setAttribute("totalDepots", totalDepots);
	 * request.setAttribute("totalRetraits", totalRetraits);
	 * request.setAttribute("soldeTheorique", soldeTheorique); }
	 * 
	 * request.getRequestDispatcher("etat-caisse.jsp").forward(request, response);
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * System.out.println("Erreur technique: " + e.getMessage());
	 * response.sendRedirect("index.jsp"); } }
	 */
    
//   private void handleEtatCaisse(HttpServletRequest request, HttpServletResponse response)
//        throws ServletException, IOException {
//    try {
//        System.out.println("=== DÉBUT handleEtatCaisse ===");
//        
//        HttpSession session = request.getSession(false);
//        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
//        if (userId == null) {
//            System.out.println("DEBUG: Session utilisateur non trouvée, redirection vers login");
//            response.sendRedirect("login.jsp");
//            return;
//        }
//        
//        System.out.println("DEBUG: User ID: " + userId);
//
//        String caisseIdStr = request.getParameter("caisseId");
//        System.out.println("DEBUG: caisseId param: " + caisseIdStr);
//        
//        if (caisseIdStr == null || caisseIdStr.isEmpty()) {
//            System.out.println("DEBUG: Aucun caisseId fourni, recherche caisse ouverte pour userId: " + userId);
//            // Vérifier si le caissier a une caisse ouverte
//            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
//            if (caisse == null) {
//                System.out.println("DEBUG: Aucune caisse ouverte trouvée, redirection vers formulaire");
//                // Pas de caisse ouverte, rediriger vers formulaire d'ouverture
//                response.sendRedirect("CompteClientServlet?action=formCaisse");
//                return;
//            }
//            caisseIdStr = String.valueOf(caisse.getId());
//            System.out.println("DEBUG: Caisse ouverte trouvée, ID: " + caisseIdStr);
//        }
//
//        int caisseId = Integer.parseInt(caisseIdStr);
//        System.out.println("DEBUG: Caisse ID final: " + caisseId);
//        
//        CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
//        
//        if (caisse == null) {
//            System.out.println("DEBUG: Caisse non trouvée avec ID: " + caisseId);
//            setErrorNotif(request, "Caisse non trouvée");
//            response.sendRedirect("CompteClientServlet?action=listeCaisses");
//            return;
//        }
//        
//        System.out.println("DEBUG: Caisse trouvée - Caissiere ID: " + caisse.getCaissiereId() + 
//                         ", Statut: " + caisse.getStatut() + 
//                         ", Ouverture: " + caisse.getOuverture() + 
//                         ", Fermeture: " + caisse.getFermeture());
//
//        // Vérifier les permissions
//        Utilisateur user = utilisateurDAO.findById(userId);
//        System.out.println("DEBUG: Utilisateur role: " + user.getRole().getRoleName());
//        
//        if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//            !user.getRole().getRoleName().equals("MANAGEUR") &&
//            caisse.getCaissiereId() != userId) {
//            System.out.println("DEBUG: Accès refusé - userId: " + userId + ", caissiereId: " + caisse.getCaissiereId());
//            setErrorNotif(request, "Vous n'avez pas accès à cette caisse");
//            response.sendRedirect("index.jsp");
//            return;
//        }
//
//        System.out.println("DEBUG: Récupération des transactions...");
//        // Récupérer les transactions
//        List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);
//        System.out.println("DEBUG: Nombre de transactions: " + transactions.size());
//        
//        // Calculer les totaux
//        BigDecimal totalVentes = BigDecimal.ZERO;
//        BigDecimal totalDepots = BigDecimal.ZERO;
//        BigDecimal totalRetraits = BigDecimal.ZERO;
//        
//        for (TransactionCaisse transaction : transactions) {
//            switch (transaction.getTypeOperation()) {
//                case "VENTE":
//                    totalVentes = totalVentes.add(transaction.getMontant());
//                    break;
//                case "DEPOT":
//                    totalDepots = totalDepots.add(transaction.getMontant());
//                    break;
//                case "RETRAIT":
//                    totalRetraits = totalRetraits.add(transaction.getMontant().abs());
//                    break;
//            }
//        }
//        
//        System.out.println("DEBUG: Total ventes: " + totalVentes);
//        System.out.println("DEBUG: Total dépôts: " + totalDepots);
//        System.out.println("DEBUG: Total retraits: " + totalRetraits);
//        
//        // Calculer le solde théorique
//        BigDecimal soldeTheorique = caisse.getSoldeInitial()
//            .add(totalVentes)
//            .add(totalDepots)
//            .subtract(totalRetraits);
//        
//        BigDecimal difference = caisse.getSoldeFinal() != null ? 
//            caisse.getSoldeFinal().subtract(soldeTheorique) : BigDecimal.ZERO;
//            
//        System.out.println("DEBUG: Solde initial: " + caisse.getSoldeInitial());
//        System.out.println("DEBUG: Solde théorique: " + soldeTheorique);
//        System.out.println("DEBUG: Solde final: " + caisse.getSoldeFinal());
//        System.out.println("DEBUG: Différence: " + difference);
//
//        // Récupérer les paramètres de date de filtrage
//        String dateDebutStr = request.getParameter("dateDebut");
//        String dateFinStr = request.getParameter("dateFin");
//        
//        System.out.println("DEBUG: dateDebut param: " + dateDebutStr);
//        System.out.println("DEBUG: dateFin param: " + dateFinStr);
//        
//        Timestamp dateDebut = null;
//        Timestamp dateFin = null;
//        
//        try {
//            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                Date parsedDate = (Date) sdf.parse(dateDebutStr);
//                dateDebut = new Timestamp(parsedDate.getTime());
//                System.out.println("DEBUG: dateDebut parsée: " + dateDebut);
//            }
//            if (dateFinStr != null && !dateFinStr.isEmpty()) {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                Date parsedDate = (Date) sdf.parse(dateFinStr);
//                dateFin = new Timestamp(parsedDate.getTime());
//                // Ajouter 23h59m59s pour inclure toute la journée
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(parsedDate);
//                cal.set(Calendar.HOUR_OF_DAY, 23);
//                cal.set(Calendar.MINUTE, 59);
//                cal.set(Calendar.SECOND, 59);
//                dateFin = new Timestamp(cal.getTime().getTime());
//                System.out.println("DEBUG: dateFin parsée avec fin de journée: " + dateFin);
//            }
//        } catch (Exception e) {
//            System.out.println("Erreur parsing dates: " + e.getMessage());
//        }
//
//        System.out.println("DEBUG: Récupération des dépôts par client...");
//        // Récupérer les dépôts par client
//        List<Map<String, Object>> depotsClients = caisseCaissiereDAO.getDepotsCompteClient(
//            caisse.getId(), dateDebut, dateFin);
//        System.out.println("DEBUG: Nombre de dépôts clients: " + depotsClients.size());
//        
//        System.out.println("DEBUG: Récupération des commandes encaissées par client...");
//        // Récupérer les commandes encaissées par client
//        List<Map<String, Object>> commandesClients = caisseCaissiereDAO.getCommandesEncaisseesParClient(
//            caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
//        System.out.println("DEBUG: Nombre d'entrées commandes clients: " + commandesClients.size());
//        
//        System.out.println("DEBUG: Récupération des commandes par serveuse...");
//        // Récupérer les commandes par serveuse
//        List<Map<String, Object>> commandesServeuses = caisseCaissiereDAO.getCommandesEncaisseesParServeuse(
//            caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
//        System.out.println("DEBUG: Nombre d'entrées commandes serveuses: " + commandesServeuses.size());
//        
//        System.out.println("DEBUG: Récupération des commandes directes caissière...");
//        // Récupérer les commandes directes de la caissière (COMPTOIR)
//        List<Map<String, Object>> commandesComptoir = caisseCaissiereDAO.getCommandesCaisseDirecte(
//            caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
//        System.out.println("DEBUG: Nombre de commandes directes: " + commandesComptoir.size());
//        
//        System.out.println("DEBUG: Calcul du total des shots...");
//        // Récupérer le total des shots de la caissière
//        BigDecimal totalShots = caisseCaissiereDAO.getTotalShotsCaissiere(caisse.getCaissiereId());
//        System.out.println("DEBUG: Total shots caissière: " + totalShots);
//        
//        // Mettre à jour le montant des shots dans l'objet caisse
//        caisse.setMontantShot(totalShots);
//
//        request.setAttribute("caisse", caisse);
//        request.setAttribute("transactions", transactions);
//        request.setAttribute("totalVentes", totalVentes);
//        request.setAttribute("totalDepots", totalDepots);
//        request.setAttribute("totalRetraits", totalRetraits);
//        request.setAttribute("soldeTheorique", soldeTheorique);
//        request.setAttribute("difference", difference);
//        
//        // Ajouter les nouvelles données
//        request.setAttribute("depotsClients", depotsClients);
//        request.setAttribute("commandesClients", commandesClients);
//        request.setAttribute("commandesServeuses", commandesServeuses);
//        request.setAttribute("commandesComptoir", commandesComptoir);
//        
//        // Ajouter les dates pour le formulaire
//        request.setAttribute("dateDebut", dateDebutStr);
//        request.setAttribute("dateFin", dateFinStr);
//
//        System.out.println("DEBUG: Forward vers etat-caisse.jsp");
//        request.getRequestDispatcher("etat-caisse.jsp").forward(request, response);
//
//    } catch (Exception e) {
//        System.out.println("ERREUR CRITIQUE dans handleEtatCaisse: " + e.getMessage());
//        e.printStackTrace();
//        setErrorNotif(request, "Erreur technique: " + e.getMessage());
////        response.sendRedirect("index.jsp");
//    } finally {
//        System.out.println("=== FIN handleEtatCaisse ===");
//    }
//}

    
    private void handleEtatCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("=== DÉBUT handleEtatCaisse ===");
            
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                System.out.println("DEBUG: Session utilisateur non trouvée, redirection vers login");
                response.sendRedirect("login.jsp");
                return;
            }
            
            System.out.println("DEBUG: User ID: " + userId);

            String caisseIdStr = request.getParameter("caisseId");
            System.out.println("DEBUG: caisseId param: " + caisseIdStr);
            
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                System.out.println("DEBUG: Aucun caisseId fourni, recherche caisse ouverte pour userId: " + userId);
                // Vérifier si le caissier a une caisse ouverte
                CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    System.out.println("DEBUG: Aucune caisse ouverte trouvée, redirection vers formulaire");
                    // Pas de caisse ouverte, rediriger vers formulaire d'ouverture
                    response.sendRedirect("CompteClientServlet?action=formCaisse");
                    return;
                }
                caisseIdStr = String.valueOf(caisse.getId());
                System.out.println("DEBUG: Caisse ouverte trouvée, ID: " + caisseIdStr);
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            System.out.println("DEBUG: Caisse ID final: " + caisseId);
            
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                System.out.println("DEBUG: Caisse non trouvée avec ID: " + caisseId);
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CompteClientServlet?action=listeCaisses");
                return;
            }
            
            System.out.println("DEBUG: Caisse trouvée - Caissiere ID: " + caisse.getCaissiereId() + 
                             ", Statut: " + caisse.getStatut() + 
                             ", Ouverture: " + caisse.getOuverture() + 
                             ", Fermeture: " + caisse.getFermeture());

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            System.out.println("DEBUG: Utilisateur role: " + user.getRole().getRoleName());
            
            if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                !user.getRole().getRoleName().equals("MANAGEUR") &&
                caisse.getCaissiereId() != userId) {
                System.out.println("DEBUG: Accès refusé - userId: " + userId + ", caissiereId: " + caisse.getCaissiereId());
                setErrorNotif(request, "Vous n'avez pas accès à cette caisse");
                response.sendRedirect("index.jsp");
                return;
            }

            System.out.println("DEBUG: Récupération des transactions...");
            // Récupérer les transactions
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);
            System.out.println("DEBUG: Nombre de transactions: " + transactions.size());
            
            // Calculer les totaux de dépôts et retraits uniquement depuis les transactions
            BigDecimal totalDepotsTransactions = BigDecimal.ZERO;
            BigDecimal totalRetraits = BigDecimal.ZERO;
            
            for (TransactionCaisse transaction : transactions) {
                switch (transaction.getTypeOperation()) {
                    case "DEPOT":
                        totalDepotsTransactions = totalDepotsTransactions.add(transaction.getMontant());
                        break;
                    case "RETRAIT":
                        totalRetraits = totalRetraits.add(transaction.getMontant().abs());
                        break;
                }
            }

            // Récupérer les paramètres de date de filtrage
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            
            System.out.println("DEBUG: dateDebut param: " + dateDebutStr);
            System.out.println("DEBUG: dateFin param: " + dateFinStr);
            
            Timestamp dateDebut = null;
            Timestamp dateFin = null;
            
            try {
                if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = (Date) sdf.parse(dateDebutStr);
                    dateDebut = new Timestamp(parsedDate.getTime());
                    System.out.println("DEBUG: dateDebut parsée: " + dateDebut);
                }
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = (Date) sdf.parse(dateFinStr);
                    dateFin = new Timestamp(parsedDate.getTime());
                    // Ajouter 23h59m59s pour inclure toute la journée
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    dateFin = new Timestamp(cal.getTime().getTime());
                    System.out.println("DEBUG: dateFin parsée avec fin de journée: " + dateFin);
                }
            } catch (Exception e) {
                System.out.println("Erreur parsing dates: " + e.getMessage());
            }

            System.out.println("DEBUG: Récupération des dépôts par client...");
            // Récupérer les dépôts par client
            List<Map<String, Object>> depotsClients = caisseCaissiereDAO.getDepotsCompteClient(
                caisse.getId(), dateDebut, dateFin);
            System.out.println("DEBUG: Nombre de dépôts clients: " + depotsClients.size());
            
            System.out.println("DEBUG: Récupération des commandes encaissées par client...");
            // Récupérer les commandes encaissées par client
            List<Map<String, Object>> commandesClients = caisseCaissiereDAO.getCommandesEncaisseesParClient(
                caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
            System.out.println("DEBUG: Nombre d'entrées commandes clients: " + commandesClients.size());
            
            System.out.println("DEBUG: Récupération des commandes par serveuse...");
            // Récupérer les commandes par serveuse
            List<Map<String, Object>> commandesServeuses = caisseCaissiereDAO.getCommandesEncaisseesParServeuse(
                caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
            System.out.println("DEBUG: Nombre d'entrées commandes serveuses: " + commandesServeuses.size());
            
            System.out.println("DEBUG: Récupération des commandes directes caissière...");
            // Récupérer les commandes directes de la caissière (COMPTOIR)
            List<Map<String, Object>> commandesComptoir = caisseCaissiereDAO.getCommandesCaisseDirecte(
                caisse.getCaissiereId(), caisse.getId(), dateDebut, dateFin);
            System.out.println("DEBUG: Nombre de commandes directes: " + commandesComptoir.size());
            
            // CALCULER LE TOTAL DES VENTES (TOUTES LES COMMANDES)
            BigDecimal totalVentes = BigDecimal.ZERO;
            
            // 1. Commandes clients (payées ou non, peu importe)
            if (commandesClients != null) {
                for (Map<String, Object> commande : commandesClients) {
                    BigDecimal montant = (BigDecimal) commande.get("total");
                    if (montant != null) {
                        totalVentes = totalVentes.add(montant);
                    }
                }
            }
            
            // 2. Commandes serveuses (payées + non payées)
            if (commandesServeuses != null) {
                for (Map<String, Object> serveuse : commandesServeuses) {
                    // Total payé
                    BigDecimal montantPaye = (BigDecimal) serveuse.get("total_paye");
                    if (montantPaye != null) {
                        totalVentes = totalVentes.add(montantPaye);
                    }
                    
                    // Total non payé
                    BigDecimal montantNonPaye = (BigDecimal) serveuse.get("total_non_paye");
                    if (montantNonPaye != null) {
                        totalVentes = totalVentes.add(montantNonPaye);
                    }
                }
            }
            
            // 3. Commandes comptoir (payées + non payées)
            if (commandesComptoir != null) {
                for (Map<String, Object> commande : commandesComptoir) {
                    BigDecimal montant = (BigDecimal) commande.get("montant_total");
                    if (montant != null) {
                        totalVentes = totalVentes.add(montant);
                    }
                }
            }
            
            // TOTAL DES DÉPÔTS (dépôts clients + transactions DEPOT)
            BigDecimal totalDepots = BigDecimal.ZERO;
            
            // Dépôts clients
            if (depotsClients != null) {
                for (Map<String, Object> depot : depotsClients) {
                    BigDecimal montant = (BigDecimal) depot.get("total");
                    if (montant != null) {
                        totalDepots = totalDepots.add(montant);
                    }
                }
            }
            
            // Ajouter les dépôts des transactions
            totalDepots = totalDepots.add(totalDepotsTransactions);
            
            System.out.println("DEBUG: Total ventes (toutes commandes): " + totalVentes);
            System.out.println("DEBUG: Total dépôts (clients + transactions): " + totalDepots);
            System.out.println("DEBUG: Total retraits: " + totalRetraits);
            
            // Calculer le solde théorique
            BigDecimal soldeTheorique = caisse.getSoldeInitial()
                .add(totalVentes)
                .add(totalDepots)
                .subtract(totalRetraits);
            
            BigDecimal difference = caisse.getSoldeFinal() != null ? 
                caisse.getSoldeFinal().subtract(soldeTheorique) : BigDecimal.ZERO;
                
            System.out.println("DEBUG: Solde initial: " + caisse.getSoldeInitial());
            System.out.println("DEBUG: Solde théorique: " + soldeTheorique);
            System.out.println("DEBUG: Solde final: " + caisse.getSoldeFinal());
            System.out.println("DEBUG: Différence: " + difference);

            System.out.println("DEBUG: Calcul du total des shots...");
            // Récupérer le total des shots de la caissière
            BigDecimal totalShots = caisseCaissiereDAO.getTotalShotsCaissiere(caisse.getCaissiereId());
            System.out.println("DEBUG: Total shots caissière: " + totalShots);
            
            // Mettre à jour le montant des shots dans l'objet caisse
            caisse.setMontantShot(totalShots);

            request.setAttribute("caisse", caisse);
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalVentes", totalVentes);
            request.setAttribute("totalDepots", totalDepots);
            request.setAttribute("totalRetraits", totalRetraits);
            request.setAttribute("soldeTheorique", soldeTheorique);
            request.setAttribute("difference", difference);
            
            // Ajouter les nouvelles données
            request.setAttribute("depotsClients", depotsClients);
            request.setAttribute("commandesClients", commandesClients);
            request.setAttribute("commandesServeuses", commandesServeuses);
            request.setAttribute("commandesComptoir", commandesComptoir);
            request.setAttribute("totalShots", totalShots);
            
            // Ajouter les dates pour le formulaire
            request.setAttribute("dateDebut", dateDebutStr);
            request.setAttribute("dateFin", dateFinStr);

            System.out.println("DEBUG: Forward vers etat-caisse.jsp");
            request.getRequestDispatcher("etat-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("ERREUR CRITIQUE dans handleEtatCaisse: " + e.getMessage());
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
//            response.sendRedirect("index.jsp");
        } finally {
            System.out.println("=== FIN handleEtatCaisse ===");
        }
    }
    private void handleCompteClient(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Récupérer les dépôts par client
//            List<Map<String, Object>> depotsClients = caisseCaissiereDAO.getDepotsCompteClient(caisse.getId());
            
            // Récupérer les commandes encaissées par client
            List<Map<String, Object>> commandesClients = caisseCaissiereDAO.getCommandesEncaisseesParClient(userId, caisse.getId());

            request.setAttribute("caisse", caisse);
//            request.setAttribute("depotsClients", depotsClients);
            request.setAttribute("commandesClients", commandesClients);
            request.setAttribute("user", user);

            request.getRequestDispatcher("compte-client-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleCompteServeuse(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Récupérer les commandes encaissées par serveuse
            List<Map<String, Object>> commandesServeuses = caisseCaissiereDAO.getCommandesEncaisseesParServeuse(userId, caisse.getId());

            request.setAttribute("caisse", caisse);
            request.setAttribute("commandesServeuses", commandesServeuses);
            request.setAttribute("user", user);

            request.getRequestDispatcher("compte-serveuse-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleCompteDirect(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Récupérer les commandes créées directement par la caissière
//            List<Map<String, Object>> commandesDirectes = caisseCaissiereDAO.getCommandesCaisseDirecte(userId, caisse.getId());

            request.setAttribute("caisse", caisse);
//            request.setAttribute("commandesDirectes", commandesDirectes);
            request.setAttribute("user", user);

            request.getRequestDispatcher("compte-direct-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleControleRemettre(HttpServletRequest request, HttpServletResponse response)
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
                setErrorNotif(request, "Accès réservé aux caissiers");
                response.sendRedirect("index.jsp");
                return;
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Calculer le contrôle à remettre
            Map<String, BigDecimal> controle = caisseCaissiereDAO.calculerControleARemettre(
                userId, caisse.getId(), caisse.isShot() != null ? caisse.isShot() : false);

            request.setAttribute("caisse", caisse);
            request.setAttribute("controle", controle);
            request.setAttribute("user", user);

            request.getRequestDispatcher("controle-remettre.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
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

            String caisseIdStr = request.getParameter("caisseId");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            String typeOperation = request.getParameter("typeOperation");
            
            CaisseCaissiere caisse;
            if (caisseIdStr != null) {
                int caisseId = Integer.parseInt(caisseIdStr);
                caisse = caisseCaissiereDAO.getCaisseById(caisseId);
                
                // Vérifier les permissions
                Utilisateur user = utilisateurDAO.findById(userId);
                if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                    !user.getRole().getRoleName().equals("MANAGEUR") &&
                    (caisse == null || caisse.getCaissiereId() != userId)) {
                    setErrorNotif(request, "Permission refusée");
                    response.sendRedirect("index.jsp");
                    return;
                }
            } else {
                caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    setErrorNotif(request, "Aucune caisse ouverte");
                    response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                    return;
                }
            }

            Date dateDebut = null;
            Date dateFin = null;
            
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                dateDebut = Date.valueOf(dateDebutStr);
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                dateFin = Date.valueOf(dateFinStr);
            }

            // Récupérer les transactions
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisseAndFilters(
                caisse.getId(), typeOperation, dateDebut, dateFin);

            request.setAttribute("caisse", caisse);
            request.setAttribute("transactions", transactions);
            request.setAttribute("selectedType", typeOperation);
            request.setAttribute("selectedDateDebut", dateDebutStr);
            request.setAttribute("selectedDateFin", dateFinStr);

            request.getRequestDispatcher("historique-transactions-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleRapportJournalier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String dateStr = request.getParameter("date");
            java.sql.Date date = dateStr != null ? Date.valueOf(dateStr) : new java.sql.Date(System.currentTimeMillis());
            
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Générer le rapport journalier
            Map<String, BigDecimal> rapport = transactionCaisseDAO.getRapportJournalier(caisse.getId(), date);
            
            // Récupérer les transactions du jour
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisseAndDate(caisse.getId(), date);

            request.setAttribute("caisse", caisse);
            request.setAttribute("rapport", rapport);
            request.setAttribute("transactions", transactions);
            request.setAttribute("date", date);

            request.getRequestDispatcher("rapport-journalier-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
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
//            Utilisateur user = utilisateurDAO.findById(userId);
//            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//                                 !user.getRole().getRoleName().equals("MANAGEUR"))) {
//                setErrorNotif(request, "Permission refusée");
//                response.sendRedirect("index.jsp");
//                return;
//            }

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

            List<CaisseCaissiere> caisses = caisseCaissiereDAO.getHistoriqueCaisses(caissiereId, dateDebut, dateFin);
            List<Utilisateur> caissieres = utilisateurDAO.findByRole("CAISSIER(ERE)");

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
//            response.sendRedirect("index.jsp");
        }
    }

    private void handleDetailCaisse(HttpServletRequest request, HttpServletResponse response)
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
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            // Vérifier les permissions
            Utilisateur user = utilisateurDAO.findById(userId);
            if (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
                !user.getRole().getRoleName().equals("MANAGEUR") &&
                caisse.getCaissiereId() != userId) {
                setErrorNotif(request, "Permission refusée");
                response.sendRedirect("index.jsp");
                return;
            }

            // Calculer les statistiques
            BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);
            List<Map<String, Object>> commandesClients = caisseCaissiereDAO.getCommandesEncaisseesParClient(
                caisse.getCaissiereId(), caisseId);
            List<Map<String, Object>> commandesServeuses = caisseCaissiereDAO.getCommandesEncaisseesParServeuse(
                caisse.getCaissiereId(), caisseId);
            Map<String, BigDecimal> controle = caisseCaissiereDAO.calculerControleARemettre(
                caisse.getCaissiereId(), caisseId, caisse.isShot() != null ? caisse.isShot() : false);

            request.setAttribute("caisse", caisse);
            request.setAttribute("soldeTheorique", soldeTheorique);
            request.setAttribute("commandesClients", commandesClients);
            request.setAttribute("commandesServeuses", commandesServeuses);
            request.setAttribute("controle", controle);

            request.getRequestDispatcher("detail-caisse.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handleGetCaisseOuverteJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                sendJsonError(response, "Non authentifié", 401);
                return;
            }

            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("caisseOuverte", caisse != null);
            if (caisse != null) {
                result.put("caisse", caisse);
                result.put("soldeTheorique", caisseCaissiereDAO.calculerSoldeTheorique(caisse.getId()));
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(result, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handleGetSoldeTheoriqueJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String caisseIdStr = request.getParameter("caisseId");
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                sendJsonError(response, "caisseId requis", 400);
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("soldeTheorique", soldeTheorique);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(result, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
        }
    }

    private void handleGetTransactionsCaisseJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String caisseIdStr = request.getParameter("caisseId");
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                sendJsonError(response, "caisseId requis", 400);
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            List<TransactionCaisse> transactions = transactionCaisseDAO.getTransactionsByCaisse(caisseId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            gson.toJson(transactions, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Erreur technique: " + e.getMessage(), 500);
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
            String caisseIdStr = request.getParameter("caisseId");
            
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                setErrorNotif(request, "Caisse non spécifiée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            // TODO: Implémenter l'export Excel selon le type
            // Pour l'instant, redirection vers la page de détail
            response.sendRedirect("CaisseCaissiereServlet?action=detailCaisse&caisseId=" + caisseId);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    private void handlePrintRapport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            String rapportType = request.getParameter("rapportType");
            
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                setErrorNotif(request, "Caisse non spécifiée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            int caisseId = Integer.parseInt(caisseIdStr);
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseById(caisseId);
            
            if (caisse == null) {
                setErrorNotif(request, "Caisse non trouvée");
                response.sendRedirect("CaisseCaissiereServlet?action=listeCaisses");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateImpression = sdf.format(new java.util.Date());

            if ("complet".equals(rapportType)) {
                // Rapport complet
                BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);
                List<Map<String, Object>> commandesClients = caisseCaissiereDAO.getCommandesEncaisseesParClient(
                    caisse.getCaissiereId(), caisseId);
                List<Map<String, Object>> commandesServeuses = caisseCaissiereDAO.getCommandesEncaisseesParServeuse(
                    caisse.getCaissiereId(), caisseId);
//                List<Map<String, Object>> commandesDirectes = caisseCaissiereDAO.getCommandesCaisseDirecte(
//                    caisse.getCaissiereId(), caisseId);
                Map<String, BigDecimal> controle = caisseCaissiereDAO.calculerControleARemettre(
                    caisse.getCaissiereId(), caisseId, caisse.isShot() != null ? caisse.isShot() : false);
                
                request.setAttribute("caisse", caisse);
                request.setAttribute("soldeTheorique", soldeTheorique);
                request.setAttribute("commandesClients", commandesClients);
                request.setAttribute("commandesServeuses", commandesServeuses);
//                request.setAttribute("commandesDirectes", commandesDirectes);
                request.setAttribute("controle", controle);
                request.setAttribute("dateImpression", dateImpression);
                
                request.getRequestDispatcher("print-rapport-complet.jsp").forward(request, response);
            } else {
                // Rapport simple
                BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseId);
                Map<String, BigDecimal> controle = caisseCaissiereDAO.calculerControleARemettre(
                    caisse.getCaissiereId(), caisseId, caisse.isShot() != null ? caisse.isShot() : false);
                
                request.setAttribute("caisse", caisse);
                request.setAttribute("soldeTheorique", soldeTheorique);
                request.setAttribute("controle", controle);
                request.setAttribute("dateImpression", dateImpression);
                
                request.getRequestDispatcher("print-rapport-simple.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
        }
    }

    // ======== HANDLERS POST ========

    private void handleOuvrirCaisse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String soldeInitialStr = request.getParameter("soldeInitial");
            String shotStr = request.getParameter("shot");
            
            BigDecimal soldeInitial = BigDecimal.ZERO;
            boolean shot = false;
            
            if (soldeInitialStr != null && !soldeInitialStr.isEmpty()) {
                soldeInitial = new BigDecimal(soldeInitialStr);
            }
            
            if (shotStr != null && "true".equals(shotStr)) {
                shot = true;
            }

            // Vérifier si une caisse est déjà ouverte
            CaisseCaissiere caisseExistante = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisseExistante != null) {
                setErrorNotif(request, "Une caisse est déjà ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Créer la caisse
            CaisseCaissiere caisse = new CaisseCaissiere();
            caisse.setCaissiereId(userId);
            caisse.setSoldeInitial(soldeInitial);
            caisse.setShot(shot);
            
            int caisseId = caisseCaissiereDAO.ouvrirCaisse(caisse);
            
            if (caisseId > 0) {
                String message = "Caisse ouverte avec succès. Solde initial: " + soldeInitial + " HTG";
                if (shot) {
                    message += " (SHOT activé)";
                }
                setSuccessNotif(request, message);
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
            } else {
                setErrorNotif(request, "Erreur lors de l'ouverture de la caisse");
                response.sendRedirect("CaisseCaissiereServlet?action=formOuvrirCaisse");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CaisseCaissiereServlet?action=formOuvrirCaisse");
        }
    }

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
            
            if (soldeFinalStr == null || soldeFinalStr.isEmpty()) {
                setErrorNotif(request, "Solde final requis");
                response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse");
                return;
            }

            BigDecimal soldeFinal = new BigDecimal(soldeFinalStr);
            boolean shot = "true".equals(shotStr);
            BigDecimal montantShot = BigDecimal.ZERO;
            
            // Récupérer le montant du shot si spécifié
            if (shot && montantShotStr != null && !montantShotStr.isEmpty()) {
                montantShot = new BigDecimal(montantShotStr);
                if (montantShot.compareTo(BigDecimal.ZERO) <= 0) {
                    setErrorNotif(request, "Le montant du shot doit être positif");
                    response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse");
                    return;
                }
            } else if (shot) {
                // Si shot est coché mais pas de montant, on calcule l'écart
                CaisseCaissiere caisseCheck = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisseCheck != null) {
                    BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisseCheck.getId());
                    BigDecimal difference = soldeTheorique.subtract(soldeFinal);
                    if (difference.compareTo(BigDecimal.ZERO) > 0) {
                        montantShot = difference;
                    }
                }
            }

            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse;
            if (caisseIdStr != null && !caisseIdStr.isEmpty()) {
                // Cas où on ferme une caisse spécifique (pour admin)
                int caisseId = Integer.parseInt(caisseIdStr);
                caisse = caisseCaissiereDAO.getCaisseById(caisseId);
                
                // Vérifier les permissions
                Utilisateur user = utilisateurDAO.findById(userId);
                boolean isAdmin = user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
                                 user.getRole().getRoleName().equals("MANAGEUR");
                
                if (caisse == null || (!isAdmin && caisse.getCaissiereId() != userId)) {
                    setErrorNotif(request, "Permission refusée ou caisse non trouvée");
                    response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                    return;
                }
            } else {
                // Cas normal: caissier ferme sa propre caisse
                caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    setErrorNotif(request, "Aucune caisse ouverte");
                    response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                    return;
                }
            }

            // Calculer le solde théorique
            BigDecimal soldeTheorique = caisseCaissiereDAO.calculerSoldeTheorique(caisse.getId());
            BigDecimal difference = soldeFinal.subtract(soldeTheorique);

            // Validation supplémentaire pour le shot
            if (shot) {
                // Si montantShot > 0 mais pas spécifié explicitement, utiliser l'écart
                if (montantShot.compareTo(BigDecimal.ZERO) <= 0) {
                    montantShot = difference.abs();
                }
                
                // S'assurer que montantShot est positif
                if (montantShot.compareTo(BigDecimal.ZERO) <= 0) {
                    setErrorNotif(request, "Pour déclarer un SHOT, le montant doit être positif");
                    response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse&caisseId=" + caisse.getId());
                    return;
                }
            }

            // Fermer la caisse avec tous les paramètres
            boolean success = caisseCaissiereDAO.fermerCaisse(caisse.getId(), soldeFinal, shot, montantShot);
            
            if (success) {
                String message = "Caisse fermée avec succès. ";
                
                if (shot) {
                    message += "SHOT déclaré: " + montantShot + " HTG. ";
                    // Enregistrer une transaction pour le shot
                    TransactionCaisse transaction = new TransactionCaisse();
                    transaction.setCaisseId(caisse.getId());
                    transaction.setTypeOperation("RETRAIT");
                    transaction.setMontant(montantShot.negate());
                    transaction.setModePaiement("SHOT");
                    transaction.setNotes("Shot déclaré lors de la fermeture: " + montantShot + " HTG" + 
                                       (notes != null ? " - " + notes : ""));
                    transaction.setDateOperation(new Timestamp(System.currentTimeMillis()));
                    
                    transactionCaisseDAO.enregistrerTransaction(transaction);
                }
                
                if (difference.compareTo(BigDecimal.ZERO) != 0) {
                    message += "Écart: " + (difference.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + 
                              difference + " HTG. ";
                }
                
                message += "Solde final: " + soldeFinal + " HTG";
                
                if (notes != null && !notes.trim().isEmpty()) {
                    message += " - Notes: " + notes;
                }
                
                setSuccessNotif(request, message);
                
                // Redirection selon le contexte
                if (caisseIdStr != null) {
                    // Admin qui ferme une caisse spécifique
                    response.sendRedirect("CaisseCaissiereServlet?action=detailCaisse&caisseId=" + caisse.getId());
                } else {
                    // Caissier ferme sa propre caisse
                    response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                }
            } else {
                setErrorNotif(request, "Erreur lors de la fermeture de la caisse");
                response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse&caisseId=" + caisse.getId());
            }

        } catch (NumberFormatException e) {
            setErrorNotif(request, "Format de nombre invalide");
            response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CaisseCaissiereServlet?action=formFermerCaisse");
        }
    }

    private void handleEnregistrerTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String typeOperation = request.getParameter("typeOperation");
            String montantStr = request.getParameter("montant");
            String modePaiement = request.getParameter("modePaiement");
            String clientIdStr = request.getParameter("clientId");
            String notes = request.getParameter("notes");
            
            if (typeOperation == null || montantStr == null) {
                setErrorNotif(request, "Paramètres requis manquants");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            BigDecimal montant = new BigDecimal(montantStr);
            
            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                return;
            }

            // Créer la transaction
            TransactionCaisse transaction = new TransactionCaisse();
            transaction.setCaisseId(caisse.getId());
            transaction.setTypeOperation(typeOperation);
            transaction.setMontant("RETRAIT".equals(typeOperation) ? montant.negate() : montant);
            transaction.setModePaiement(modePaiement);
            
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                transaction.setClientId(Integer.parseInt(clientIdStr));
            }
            
            transaction.setNotes(notes);
            transaction.setDateOperation(new Timestamp(System.currentTimeMillis()));
            
            int transactionId = transactionCaisseDAO.enregistrerTransaction(transaction);
            
            if (transactionId > 0) {
                setSuccessNotif(request, "Transaction enregistrée avec succès");
                response.sendRedirect("CaisseCaissiereServlet?action=etatCaisse");
            } else {
                setErrorNotif(request, "Erreur lors de l'enregistrement de la transaction");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
        }
    }

    private void handleEnregistrerVente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
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
            
            // Vérifier si une caisse est ouverte
            CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
            if (caisse == null) {
                setErrorNotif(request, "Aucune caisse ouverte");
                response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
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

    private void handleGenererRapport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String caisseIdStr = request.getParameter("caisseId");
            String rapportType = request.getParameter("rapportType");
            
            if (caisseIdStr == null || caisseIdStr.isEmpty()) {
                CaisseCaissiere caisse = caisseCaissiereDAO.getCaisseOuverte(userId);
                if (caisse == null) {
                    setErrorNotif(request, "Aucune caisse ouverte");
                    response.sendRedirect("CaisseCaissiereServlet?action=dashboard");
                    return;
                }
                caisseIdStr = String.valueOf(caisse.getId());
            }

            // Redirection vers la page d'impression
            response.sendRedirect("CaisseCaissiereServlet?action=printRapport&caisseId=" + 
                                caisseIdStr + "&rapportType=" + rapportType);

        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("index.jsp");
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
}