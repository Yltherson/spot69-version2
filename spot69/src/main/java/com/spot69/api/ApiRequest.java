	package com.spot69.api;
	
	import com.google.gson.Gson;
	import com.google.gson.GsonBuilder;
	import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
	import com.google.gson.JsonParser;
	import com.spot69.dao.ChambreDAO;
	import com.spot69.dao.CommandeDAO;
	import com.spot69.dao.CompteClientDAO;
	import com.spot69.dao.MenuCategorieDAO;
	import com.spot69.dao.PointManagerDAO;
	import com.spot69.dao.PrivilegeNiveauDAO;
	import com.spot69.dao.NotificationDAO;
	import com.spot69.dao.CreditDAO;
	import com.spot69.dao.EvenementDAO;
	import com.spot69.dao.PanierDAO;
	import com.spot69.dao.PlatDAO;
import com.spot69.dao.PointConfigDAO;
import com.spot69.dao.PointDAO;
import com.spot69.dao.PointHistoriqueManager;
import com.spot69.dao.ProduitDAO;
	import com.spot69.dao.PromoDAO;
	import com.spot69.dao.RayonDAO;
	import com.spot69.dao.ReservationDAO;
import com.spot69.dao.ReservationEvenementDAO;
import com.spot69.dao.RoleDAO;
	import com.spot69.dao.TableRooftopDAO;
	import com.spot69.dao.TransactionCompteDAO;
import com.spot69.dao.TypeTableEvenementDAO;
import com.spot69.dao.UtilisateurDAO;
	import com.spot69.model.BilanUtilisateur;
	import com.spot69.model.Chambre;
	import com.spot69.model.Commande;
	import com.spot69.model.CommandeDetail;
	import com.spot69.model.CompteClient;
	import com.spot69.model.MenuCategorie;
	import com.spot69.model.Notification;
	import com.spot69.model.Panier;
	import com.spot69.model.Credit;
	import com.spot69.model.Evenement;
	import com.spot69.model.Plat;
import com.spot69.model.PointConfig;
import com.spot69.model.PointHistorique;
import com.spot69.model.PrivilegeNiveau;
	import com.spot69.model.Produit;
	import com.spot69.model.Promo;
	import com.spot69.model.Rayon;
	import com.spot69.model.Reservation;
import com.spot69.model.ReservationEvenement;
import com.spot69.model.ReservationTable;
import com.spot69.model.Role;
	import com.spot69.model.TableRooftop;
	import com.spot69.model.TransactionCompte;
import com.spot69.model.TypeTableEvenement;
import com.spot69.model.TypeTransaction;
	import com.spot69.model.Utilisateur;
import com.spot69.utils.DBConnection;
import com.spot69.utils.PasswordUtils;
	
	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.PrintWriter;
	import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.time.LocalDate;
	import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Collections;
	import java.util.Date;
	import java.util.List;
	import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
	import javax.servlet.annotation.WebServlet;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import javax.servlet.http.HttpSession;
	
	@WebServlet({ "/api", "/blok/api" })
	public class ApiRequest extends HttpServlet {
		private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
		// DAOs (à initialiser correctement)
		private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
		private CreditDAO creditDAO = new CreditDAO();
		private CommandeDAO commandeDAO = new CommandeDAO();
		private final MenuCategorieDAO menuCategorieDAO = new MenuCategorieDAO();
		private final PlatDAO platDAO = new PlatDAO();
		private final ProduitDAO produitDAO = new ProduitDAO();
		private final TableRooftopDAO tableDAO = new TableRooftopDAO();
		private final NotificationDAO notificationDAO = new NotificationDAO();
		private final ChambreDAO chambreDAO = new ChambreDAO();
		private final PrivilegeNiveauDAO privilegeNiveauDAO = new PrivilegeNiveauDAO();
	
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			JsonObject jsonResponse = new JsonObject();
	
			try {
				String action = request.getParameter("action");
				if (action == null || action.isEmpty()) {
					sendError(response, out, "Paramètre 'action' manquant", 400);
					return;
				}
	
				switch (action.toLowerCase()) {
				case "login":
					handleLogin(request, response, out);
					break;
				case "alltables":
					handleAllTables(request, response, out);
					break;
				case "getinventaire":
					handleInventaire(request, response, out);
					break;
				case "bilan":
					handleBilan(request, response, out);
					break;
				case "listcommandesstaff":
					handleListCommandesStaff(request, response, out);
					break;
				case "listcommandeswithdetails":
					handleListCommandesWithDetails(request, response, out);
					break;
				case "searchplats":
					handleSearchPlats(request, response, out);
					break;
				case "searchplatByName":
					handleSearchPlats(request, response, out);
					break;
				case "getvendeusetable":
					handleVendeuseTable(request, response, out);
					break;
				case "rapportvente":
					handleRapportCommande(request, response, out);
					break;
				case "getmenu":
					handleGetMenu(request, response, out);
					break;
					// Ajouter dans le switch(action.toLowerCase())
				case "getpointconfig":
				    handleGetPointConfig(request, response, out);
				    break;
	
				case "listecreditforclientbystaff":
					handleListCreditForClientByStaff(request, response, out);
					break;
	
	//                case "bilancredit":
	//                    handleBilanCreditForUser(request, response, out);
	//                    break;  
				case "getnotification":
					handleNotificationForUser(request, response, out);
					break;
				case "getuserbyrole":
					handleGetUserByRole(request, response, out);
					break;
				case "deletenotification":
					handleDeleteNotificationForUser(request, response, out);
					break;
				case "listcommandesclient":
					handleListCommandesClient(request, response, out);
					break;
				case "listetable":
					handleListTable(request, response, out);
					break;
				case "ajoutercommande":
					handleCommande(request, response, out);
					break;
	
				case "getdetailscommande":
					handleGetDetails(request, response, out);
					break;
				case "getbynumero":
					handleGetByNumero(request, response, out);
					break;
				case "categorie-parente":
					handleCategoriesParentes(request, response, out);
					break;
				case "sous-categories":
					handleSousCategoriesByParentId(request, response, out);
					break;
				case "plats-par-sous-categories":
					handlePlatsParSousCategorieJson(request, response, out);
					break;
	//                    @ByYlth
				case "categorieplat":
					handlePlatsParCategorieJson(request, response, out);
					break;
				case "chambres":
					handleListeChambres(request, response, out);
					break;
				case "chambres-disponibles":
	//                    handleChambresDisponibles(request, response, out);
					break;
				case "reservations-utilisateur":
					handleListeReservationsUtilisateur(request, response, out);
					break;
				case "listcommandesclientdefault":
					handlelistCommandesByIdClient(request, response, out);
					break;
				// Ajoute dans ton switch(action.toLowerCase()) :
	//
	//                case "ajouterpanier":
	//                    handleAjouterPanier(request, response, out);
	//                    break;
					
				case "listeevenements":
				    handleListerEvenements(request, response, out);
				    break;
				case "evenementdetails":
				    handleEvenementDetails(request, response, out);
				    break;
				case "mesreservationsevenements":
				    handleMesReservationsEvenements(request, response, out);
				    break;
				case "reservationsevenementsadmin":
				    handleReservationsEvenementsAdmin(request, response, out);
				    break;
	
				case "listerpanier":
					handleListerPanier(request, response, out);
					break;
				case "supprimerpanier":
					handleSupprimerPanier(request, response, out);
					break;
	
	
				case "viderpanier":
					handleViderPanier(request, response, out);
					break;
	
	
	                case "changenotiftoread":
	                    handleChangeNotifToRead(request, response, out);
	                    break;
	                    
	                case "promos":
	                    handleGetPromos(request, response, out);
	                    break;
	
	                case "refreshdata":
	                    handleRefreshData(request, response, out);
	                    break;
	                case "istherenotif":
	    				handleIsThereNotif(request, response, out);
	    				break;
//	    				COMPTE CAISSE
	                case "getcompteclient":
	                    handleGetCompteClient(request, response, out);
	                    break;
//	                case "getsoldeclient":
//	                    handleGetSoldeClient(request, response, out);
//	                    break;
	                case "gethistoriquetransactions":
	                    handleGetHistoriqueTransactions(request, response, out);
	                    break;
//	                case "getcommandescreditclient":
//	                    handleGetCommandesCreditClient(request, response, out);
//	                    break;
	                case "getstatistiquescompte":
	                    handleGetStatistiquesCompte(request, response, out);
	                    break;
	                case "getetatcomptecomplet":
	                    handleGetEtatCompteComplet(request, response, out);
	                    break;
//	                case "gettransactionsfiltrees":
//	                    handleGetTransactionsFiltrees(request, response, out);
//	                    break;
//	                case "getetatgeneralcomptes":
//	                    handleGetEtatGeneralComptes(request, response, out);
//	                    break;
	                case "getlasttransactions":
	                    handleGetLastTransactions(request, response, out);
	                    break;
	                    
	                case "privilegeniveaux":
	                    handlePrivilegeNiveaux(request, response, out);
	                    break;
	                case "privilegeniveauactifs":
	                    handlePrivilegeNiveauActifs(request, response, out);
	                    break;
	                 // Dans votre switch(action.toLowerCase()) du doGet, ajoutez :
	                case "historiquepointsclient":
	                    handleHistoriquePointsClient(request, response, out);
	                    break;
	                
	
	
				default:
					sendError(response, out, "Action non reconnue", 400);
				}
			} catch (Exception e) {
				sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
			}
		}
	
	
		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
	
			try {
				String action = request.getParameter("action");
				if (action == null || action.isEmpty()) {
					sendError(response, out, "Paramètre 'action' manquant", 400);
					return;
				}
	
				switch (action.toLowerCase()) {
				case "ajoutercommande":
					handleCommande(request, response, out);
					break;
				case "ajoutercommande_v2":
					handleCommande_v2(request, response, out);
					break;
				case "payercredit":
					handlePayerCredit(request, response, out);
					break;
				case "modifierstatutpaiement":
					handlePaiementCommande(request, response, out);
					break;
				case "modifierstatutcommande":
					handleUpdateCommandeStatus(request, response, out);
					break;
				case "updateetattable":
					handleUpdateTableState(request, response, out);
					break;
				case "ajouter-reservation":
					handleAddReservation(request, response, out);
					break;
	
				case "register":
					handleRegister(request, response, out);
					break;
					
	             	
	             case "ajouterpromo":
	                 handleAjouterPromo(request, response, out);
	                 break;
	             case "modifierpromo":
	                 handleModifierPromo(request, response, out);
	                 break;
	             case "supprimerpromo":
	                 handleSupprimerPromo(request, response, out);
	                 break;
	             case "reorderpromos":
	                 handleReorderPromos(request, response, out);
	                 break;
//	COMTE CAISSE
	             case "effectuerdepot":
	                 handleEffectuerDepot(request, response, out);
	                 break;
//	             case "effectuerretrait":
//	                 handleEffectuerRetrait(request, response, out);
//	                 break;
//	             case "effectuertransfercompte":
//	                 handleEffectuerTransfertCompte(request, response, out);
//	                 break;
//	             case "effectuerajustementsolde":
//	                 handleEffectuerAjustementSolde(request, response, out);
//	                 break;
//	             case "payercommandeviacompte":
//	                 handlePayerCommandeViaCompte(request, response, out);
//	                 break;
	                 
	             case "reserverevenement":
	            	    handleReserverEvenement(request, response, out);
	            	    break;
//	            	case "validerreservationevenement":
//	            	    handleValiderReservationEvenement(request, response, out);
//	            	    break;
	            	case "annulerreservationevenement":
	            	    handleAnnulerReservationEvenement(request, response, out);
	            	    break;
	            	case "convertirpoints":
	                    handleConvertirPoints(request, response, out);
	                    break;
				default:
					sendError(response, out, "Action non reconnue en POST", 400);
				}
	
			} catch (Exception e) {
				sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
			}
		}
		

		
		private void handleListerEvenements(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
    try {
        EvenementDAO evenementDAO = new EvenementDAO();
        List<Evenement> evenements = evenementDAO.getAllWithTables(); // Utiliser la nouvelle méthode
        
        JsonArray evenementsArray = new JsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Evenement evenement : evenements) {
            JsonObject eventJson = new JsonObject();
            eventJson.addProperty("id", evenement.getId());
            eventJson.addProperty("titre", evenement.getTitre());
            eventJson.addProperty("artisteGroupe", evenement.getArtisteGroupe());
            eventJson.addProperty("description", evenement.getDescription());
            eventJson.addProperty("dateEvent", 
                evenement.getDateEvent() != null ? 
                sdf.format(evenement.getDateEventAsDate()) : "");
            eventJson.addProperty("statut", evenement.getStatut());
            eventJson.addProperty("capaciteTotale", evenement.getCapaciteTotale());
            
            // Calculer le prix minimum des tables
            BigDecimal prixMin = BigDecimal.ZERO;
            List<TypeTableEvenement> tables = evenement.getTypesTables();
            if (tables != null && !tables.isEmpty()) {
                prixMin = tables.stream()
                    .map(TypeTableEvenement::getPrix)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            }
            eventJson.addProperty("prixMin", prixMin.toString());
            eventJson.addProperty("nombreTables", tables != null ? tables.size() : 0);
            
            // Ajouter la liste des tables
            JsonArray tablesArray = new JsonArray();
            if (tables != null) {
                for (TypeTableEvenement table : tables) {
                    JsonObject tableJson = new JsonObject();
                    tableJson.addProperty("id", table.getId());
                    tableJson.addProperty("nom", table.getNom());
                    tableJson.addProperty("description", table.getDescription());
                    tableJson.addProperty("capacite", table.getCapacite());
                    tableJson.addProperty("prix", table.getPrix().toString());
                    tableJson.addProperty("statut", table.getStatut());
                    tablesArray.add(tableJson);
                }
            }
            eventJson.add("tables", tablesArray);
            
            if (evenement.hasMedia()) {
                String imagePath = getImagePath(request, "evenements", evenement.getMediaPath());
                eventJson.addProperty("imageUrl", imagePath);
            }
            
            evenementsArray.add(eventJson);
        }
        
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", "ok");
        
        JsonObject data = new JsonObject();
        data.add("evenements", evenementsArray);
        data.addProperty("total", evenements.size());
        
        responseJson.add("data", data);
        out.print(responseJson.toString());
        
    } catch (Exception e) {
        e.printStackTrace();
        sendError(response, out, "Erreur: " + e.getMessage(), 500);
    }
}
		
		private void handleConvertirPoints(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    System.out.println("=== DÉBUT handleConvertirPoints ===");
		    
		    try {
		        // Log des informations de la requête
		        System.out.println("Méthode HTTP: " + request.getMethod());
		        System.out.println("Content-Type: " + request.getContentType());
		        System.out.println("Content-Length: " + request.getContentLength());
		        System.out.println("URL: " + request.getRequestURL());
		        System.out.println("Query String: " + request.getQueryString());
		        
		        // Lire le corps de la requête
		        StringBuilder sb = new StringBuilder();
		        BufferedReader reader = request.getReader();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line);
		        }
		        String body = sb.toString();
		        
		        System.out.println("Corps de la requête (raw): " + body);
		        
		        if (body == null || body.trim().isEmpty()) {
		            System.err.println("ERREUR: Corps de requête vide");
		            sendError(response, out, "Corps de requête vide", 400);
		            return;
		        }
		        
		        JsonObject jsonBody = null;
		        try {
		            jsonBody = JsonParser.parseString(body).getAsJsonObject();
		            System.out.println("JSON parsé avec succès");
		        } catch (Exception e) {
		            System.err.println("ERREUR: Impossible de parser le JSON: " + e.getMessage());
		            sendError(response, out, "Format JSON invalide", 400);
		            return;
		        }
		        
		        // Log du JSON parsé
		        System.out.println("JSON parsé: " + jsonBody.toString());
		        
		        // Récupérer les paramètres avec des logs détaillés
		        String userIdStr = null;
		        String pointsAUtiliserStr = null;
		        
		        try {
		            if (jsonBody.has("userId")) {
		                userIdStr = jsonBody.get("userId").getAsString();
		                System.out.println("userId trouvé: " + userIdStr);
		            } else {
		                System.err.println("ERREUR: Clé 'userId' manquante dans JSON");
		            }
		            
		            if (jsonBody.has("pointsAUtiliser")) {
		                pointsAUtiliserStr = jsonBody.get("pointsAUtiliser").getAsString();
		                System.out.println("pointsAUtiliser trouvé: " + pointsAUtiliserStr);
		            } else {
		                System.err.println("ERREUR: Clé 'pointsAUtiliser' manquante dans JSON");
		            }
		        } catch (Exception e) {
		            System.err.println("ERREUR lors de l'extraction des valeurs JSON: " + e.getMessage());
		            sendError(response, out, "Erreur de format JSON", 400);
		            return;
		        }
		        
		        if (userIdStr == null || pointsAUtiliserStr == null) {
		            System.err.println("ERREUR: Paramètres manquants");
		            System.err.println("userIdStr: " + userIdStr);
		            System.err.println("pointsAUtiliserStr: " + pointsAUtiliserStr);
		            sendError(response, out, "Paramètres manquants (userId, pointsAUtiliser)", 400);
		            return;
		        }
		        
		        int userId = 0;
		        int pointsAUtiliser = 0;
		        
		        try {
		            userId = Integer.parseInt(userIdStr);
		            System.out.println("userId parsé: " + userId);
		        } catch (NumberFormatException e) {
		            System.err.println("ERREUR: Format numérique invalide pour userId: " + userIdStr);
		            sendError(response, out, "Format de userId invalide", 400);
		            return;
		        }
		        
		        try {
		            pointsAUtiliser = Integer.parseInt(pointsAUtiliserStr);
		            System.out.println("pointsAUtiliser parsé: " + pointsAUtiliser);
		        } catch (NumberFormatException e) {
		            System.err.println("ERREUR: Format numérique invalide pour pointsAUtiliser: " + pointsAUtiliserStr);
		            sendError(response, out, "Format de pointsAUtiliser invalide", 400);
		            return;
		        }
		        
		        if (pointsAUtiliser <= 0) {
		            System.err.println("ERREUR: pointsAUtiliser <= 0: " + pointsAUtiliser);
		            sendError(response, out, "Le nombre de points doit être supérieur à 0", 400);
		            return;
		        }
		        
		        System.out.println("=== VÉRIFICATION DES POINTS ===");
		        
		        // 1. Vérifier que l'utilisateur a assez de points
		        PointDAO pointDAO = new PointDAO();
		        int totalPointsDisponibles = pointDAO.getTotalPointsUtilisateur_v2(userId);
		        System.out.println("Total points disponibles pour user#" + userId + ": " + totalPointsDisponibles);
		        
		        if (pointsAUtiliser > totalPointsDisponibles) {
		            System.err.println("ERREUR: Points insuffisants");
		            System.err.println("Demandé: " + pointsAUtiliser);
		            System.err.println("Disponible: " + totalPointsDisponibles);
		            sendError(response, out, 
		                "Points insuffisants: " + pointsAUtiliser + 
		                " demandés, " + totalPointsDisponibles + " disponibles", 400);
		            return;
		        }
		        
		        System.out.println("=== CONVERSION POINTS EN VALEUR ===");
		        
		        // 2. Convertir les points en valeur monétaire
		        PointConfigDAO pointConfigDAO = new PointConfigDAO();
		        Double valeurConvertie = pointConfigDAO.convertirPointsEnValeur(pointsAUtiliser);
		        System.out.println("Valeur convertie pour " + pointsAUtiliser + " points: " + valeurConvertie);
		        
		        if (valeurConvertie == null) {
		            System.err.println("ERREUR: Aucune configuration VALEUR_POINT active trouvée");
		            sendError(response, out, "Erreur de configuration: aucune configuration VALEUR_POINT active trouvée", 500);
		            return;
		        }
		        
		        System.out.println("=== RÉCUPÉRATION DU COMPTE CLIENT ===");
		        
		        // 3. Récupérer le compte client
		        CompteClientDAO compteClientDAO = new CompteClientDAO();
		        CompteClient compte = compteClientDAO.getCompteByClientId(userId);
		        
		        if (compte == null) {
		            System.out.println("Compte non trouvé pour user#" + userId + ", création...");
		            // Créer le compte s'il n'existe pas
		            compte = new CompteClient();
		            compte.setClientId(userId);
		            compte.setSolde(BigDecimal.ZERO);
		            int compteId = compteClientDAO.creerCompteClient(compte);
		            if (compteId <= 0) {
		                System.err.println("ERREUR: Échec création compte pour user#" + userId);
		                sendError(response, out, "Erreur lors de la création du compte", 500);
		                return;
		            }
		            compte.setId(compteId);
		            System.out.println("Compte créé avec ID: " + compteId);
		        } else {
		            System.out.println("Compte existant trouvé ID: " + compte.getId() + ", solde: " + compte.getSolde());
		        }
		        
		        // 4. Effectuer la transaction (ajout au solde)
		        BigDecimal ancienSolde = compte.getSolde();
		        BigDecimal montantAjoute = BigDecimal.valueOf(valeurConvertie);
		        BigDecimal nouveauSolde = ancienSolde.add(montantAjoute);
		        
		        System.out.println("Ancien solde: " + ancienSolde);
		        System.out.println("Montant à ajouter: " + montantAjoute);
		        System.out.println("Nouveau solde calculé: " + nouveauSolde);
		        
		        boolean updateReussi = compteClientDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
		        
		        if (!updateReussi) {
		            System.err.println("ERREUR: Échec mise à jour solde pour compte ID: " + compte.getId());
		            sendError(response, out, "Erreur lors de la mise à jour du solde", 500);
		            return;
		        }
		        
		        System.out.println("Solde mis à jour avec succès");
		        
		        // 5. Créer la transaction dans TransactionCompte
		        TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
		        TransactionCompte transaction = new TransactionCompte();
		        transaction.setCompteClientId(compte.getId());
		        transaction.setMontant(montantAjoute); // Montant positif = dépôt
		        transaction.setSoldeAvant(ancienSolde);
		        transaction.setSoldeApres(nouveauSolde);
		        transaction.setNotes("Conversion de " + pointsAUtiliser + " points en " + 
		                            String.format("%.2f", valeurConvertie) + " HTG");
		        transaction.setDateTransaction(new Timestamp(new Date().getTime()));
		        transaction.setCaissiereId(userId);
		        transaction.setTypeTransactionId(7); // ID pour "DEPOT"
		        
		        int transactionId = transactionDAO.creerTransaction(transaction);
		        
		        if (transactionId <= 0) {
		            System.err.println("ERREUR: Échec création transaction");
		            sendError(response, out, "Erreur lors de l'enregistrement de la transaction", 500);
		            return;
		        }
		        
		        System.out.println("Transaction créée ID: " + transactionId);
		        
		        // 6. Enregistrer dans l'historique des points (POINT_HISTORIQUE)
		        PointHistoriqueManager historiqueManager = new PointHistoriqueManager();
		        boolean historiqueReussi = historiqueManager.enregistrerConversionPoints(
		                userId, 
		                pointsAUtiliser, 
		                valeurConvertie, 
		                transactionId
		            );
		        
		        if (!historiqueReussi) {
		            System.err.println("ATTENTION: échec de l'enregistrement dans l'historique des points");
		        } else {
		            System.out.println("Historique des points enregistré avec succès");
		        }
		        
		        // 6.1. Marquer les points comme utilisés dans la table POINT

                PointManagerDAO pointManagerDAO = new PointManagerDAO();
		        boolean pointsMarquesUtilises = pointManagerDAO.marquerPointsCommeUtilises(userId, pointsAUtiliser, transactionId);
		        if (!pointsMarquesUtilises) {
		            System.err.println("ATTENTION: échec du marquage des points comme utilisés");
		        } else {
		            System.out.println("Points marqués comme utilisés avec succès: " + pointsAUtiliser + " points");
		        }
		        
		        // 7. Mettre à jour le total de points dans la table UTILISATEUR
		        boolean totalMisAJour = pointManagerDAO.mettreAJourTotalPointsUtilisateur(userId, -pointsAUtiliser);
		        if (!totalMisAJour) {
		            System.err.println("ATTENTION: échec de la mise à jour du total des points utilisateur");
		        } else {
		            System.out.println("Total points utilisateur mis à jour: -" + pointsAUtiliser + " points");
		        }
		        
		        // 8. Mettre à jour le niveau de l'utilisateur
		        PrivilegeNiveauDAO privilegeNiveauDAO = new PrivilegeNiveauDAO();
		        PrivilegeNiveau nouveauNiveau = null;
		        
		        try {
		            nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(userId);
		            if (nouveauNiveau != null) {
		                System.out.println("Niveau utilisateur vérifié/mis à jour: " + nouveauNiveau.getNom());
		            } else {
		                System.out.println("Niveau utilisateur inchangé ou non déterminé");
		            }
		        } catch (Exception e) {
		            System.err.println("ERREUR lors de la mise à jour du niveau: " + e.getMessage());
		            e.printStackTrace();
		        }
		        
		        // 9. Créer une notification pour l'utilisateur
		        NotificationDAO notificationDAO = new NotificationDAO();
		        Notification notif = new Notification();
		        notif.setGeneratedBy("SYSTEM");
		        notif.setToUser(userId);
		        notif.setMessages("Vous avez converti " + pointsAUtiliser + 
		                         " points en " + String.format("%.2f", valeurConvertie) + 
		                         " HTG. Nouveau solde: " + String.format("%.2f", nouveauSolde) + " HTG");
		        notif.setTypeNotif("CONVERSION_POINTS");
		        notif.setStatus("VISIBLE");
		        
		        boolean notifAjoutee = notificationDAO.ajouterNotification(notif);
		        System.out.println("Notification ajoutée: " + notifAjoutee);
		        
		        // 10. Préparer la réponse JSON
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        
		        JsonObject data = new JsonObject();
		        data.addProperty("pointsUtilises", pointsAUtiliser);
		        data.addProperty("valeurObtenue", valeurConvertie);
		        data.addProperty("nouveauSolde", nouveauSolde);
		        data.addProperty("ancienSolde", ancienSolde);
		        data.addProperty("ancienTotalPoints", totalPointsDisponibles);
		        data.addProperty("nouveauTotalPoints", totalPointsDisponibles - pointsAUtiliser);
		        
		        if (nouveauNiveau != null) {
		            data.addProperty("niveau", nouveauNiveau.getNom());
		            data.addProperty("pourcentageReduction", nouveauNiveau.getPourcentageReduction());
		        }
		        
		        data.addProperty("transactionId", transactionId);
		        data.addProperty("message", "Conversion réussie !");
		        
		        responseJson.add("data", data);
		        
		        String responseBody = responseJson.toString();
		        System.out.println("Réponse JSON: " + responseBody);
		        
		        out.print(responseBody);
		        out.flush();
		        
		        System.out.println("=== CONVERSION POINTS RÉUSSIE ===");
		        System.out.println("Utilisateur #" + userId);
		        System.out.println("Points utilisés: " + pointsAUtiliser);
		        System.out.println("Valeur obtenue: " + String.format("%.2f", valeurConvertie) + " HTG");
		        System.out.println("Ancien solde: " + String.format("%.2f", ancienSolde) + " HTG");
		        System.out.println("Nouveau solde: " + String.format("%.2f", nouveauSolde) + " HTG");
		        System.out.println("=== FIN handleConvertirPoints ===");
		        
		    } catch (NumberFormatException e) {
		        System.err.println("EXCEPTION NumberFormatException: " + e.getMessage());
		        e.printStackTrace();
		        sendError(response, out, "Format de paramètre invalide", 400);
		    } catch (Exception e) {
		        System.err.println("EXCEPTION générale: " + e.getClass().getName() + " - " + e.getMessage());
		        e.printStackTrace();
		        sendError(response, out, "Erreur lors de la conversion: " + e.getMessage(), 500);
		    }
		}
		
		private void handleGetPointConfig(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        System.out.println("=== DEBUG handleGetPointConfig ===");
		        
		        // Optionnel: Vérifier l'authentification
		        String userIdStr = request.getParameter("userId");
		        if (userIdStr == null) {
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        
		        int userId = Integer.parseInt(userIdStr);
		        
		        // Récupérer les configurations de points (type VALEUR_POINT)
		        PointConfigDAO pointConfigDAO = new PointConfigDAO();
		        List<PointConfig> configs = pointConfigDAO.getConfigsByType("VALEUR_POINT");
		        
		        System.out.println("Nombre de configs VALEUR_POINT trouvées: " + (configs != null ? configs.size() : 0));
		        
		        // Préparer la réponse JSON
		        JsonArray configsArray = new JsonArray();
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        if (configs != null && !configs.isEmpty()) {
		            // Trier par valeur de points croissante
		            configs.sort((a, b) -> {
		                double valA = a.getConditionValeur() != null ? a.getConditionValeur() : 0;
		                double valB = b.getConditionValeur() != null ? b.getConditionValeur() : 0;
		                return Double.compare(valA, valB);
		            });
		            
		            for (PointConfig config : configs) {
		                JsonObject configJson = new JsonObject();
		                configJson.addProperty("id", config.getId());
		                configJson.addProperty("typeConfig", config.getTypeConfig());
		                configJson.addProperty("conditionType", config.getConditionType());
		                
		                if (config.getConditionValeur() != null) {
		                    configJson.addProperty("conditionValeur", config.getConditionValeur());
		                } else {
		                    configJson.addProperty("conditionValeur", 0);
		                }
		                
		                configJson.addProperty("points", config.getPoints());
		                configJson.addProperty("statut", config.getStatut());
		                
		                if (config.getDateDebut() != null) {
		                    configJson.addProperty("dateDebut", dateFormat.format(config.getDateDebut()));
		                } else {
		                    configJson.addProperty("dateDebut", "");
		                }
		                
		                if (config.getDateFin() != null) {
		                    configJson.addProperty("dateFin", dateFormat.format(config.getDateFin()));
		                } else {
		                    configJson.addProperty("dateFin", "");
		                }
		                
		                
		                
		                configsArray.add(configJson);
		            }
		        }
		        
		        // Calculer les statistiques
		        double tauxPrincipal = 0;
		        if (configs != null && !configs.isEmpty()) {
		            // Prendre la première configuration (la plus basse) comme taux principal
		            PointConfig principale = configs.get(0);
		            if (principale.getConditionValeur() != null && principale.getConditionValeur() > 0) {
		                tauxPrincipal = (double) principale.getPoints() / principale.getConditionValeur();
		            }
		        }
		        
		        // Préparer la réponse complète
		        JsonObject responseData = new JsonObject();
		        responseData.add("configurations", configsArray);
 
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        responseJson.add("data", responseData);
		        
		        out.print(gson.toJson(responseJson));
		        
		        System.out.println("=== FIN handleGetPointConfig ===");
		        
		    } catch (NumberFormatException e) {
		        sendError(response, out, "Format de userId invalide", 400);
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur lors de la récupération des configurations de points: " + e.getMessage(), 500);
		    }
		}

		private void handleEvenementDetails(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String eventIdStr = request.getParameter("eventId");
		        
		        if (eventIdStr == null) {
		            sendError(response, out, "Paramètre eventId manquant", 400);
		            return;
		        }
		        
		        int eventId = Integer.parseInt(eventIdStr);
		        EvenementDAO evenementDAO = new EvenementDAO();
		        Evenement evenement = evenementDAO.getById(eventId);
		        
		        if (evenement == null) {
		            sendError(response, out, "Événement introuvable", 404);
		            return;
		        }
		        
		        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		        
		        JsonObject eventJson = new JsonObject();
		        eventJson.addProperty("id", evenement.getId());
		        eventJson.addProperty("titre", evenement.getTitre());
		        eventJson.addProperty("artisteGroupe", evenement.getArtisteGroupe());
		        eventJson.addProperty("description", evenement.getDescription());
		        eventJson.addProperty("capaciteTotale", evenement.getCapaciteTotale());
		        
		        // Pour LocalDateTime
		        if (evenement.getDateEvent() != null) {
		            try {
		                LocalDateTime dateEvent = evenement.getDateEvent();
		                eventJson.addProperty("dateEvent", dateEvent.format(dateFormatter));
		            } catch (Exception e) {
		                eventJson.addProperty("dateEvent", evenement.getDateEvent().toString());
		            }
		        } else {
		            eventJson.addProperty("dateEvent", "");
		        }
		        
		        eventJson.addProperty("statut", evenement.getStatut());
		        
		        // Pour createdAt
		        if (evenement.getCreatedAt() != null) {
		            try {
		                LocalDateTime createdAt = evenement.getCreatedAt();
		                eventJson.addProperty("createdAt", createdAt.format(dateFormatter));
		            } catch (Exception e) {
		                eventJson.addProperty("createdAt", evenement.getCreatedAt().toString());
		            }
		        } else {
		            eventJson.addProperty("createdAt", "");
		        }
		        
		        // Ajouter les tables
		        TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
		        List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(eventId);
		        
		        // Calculer le prix minimum des tables
		        BigDecimal prixMin = BigDecimal.ZERO;
		        if (tables != null && !tables.isEmpty()) {
		            prixMin = tables.stream()
		                .map(TypeTableEvenement::getPrix)
		                .min(BigDecimal::compareTo)
		                .orElse(BigDecimal.ZERO);
		        }
		        eventJson.addProperty("prixMin", prixMin.toString());
		        eventJson.addProperty("nombreTables", tables != null ? tables.size() : 0);
		        
		        // Ajouter la liste des tables
		        JsonArray tablesArray = new JsonArray();
		        if (tables != null) {
		            for (TypeTableEvenement table : tables) {
		                JsonObject tableJson = new JsonObject();
		                tableJson.addProperty("id", table.getId());
		                tableJson.addProperty("nom", table.getNom());
		                tableJson.addProperty("description", table.getDescription());
		                tableJson.addProperty("capacite", table.getCapacite());
		                tableJson.addProperty("prix", table.getPrix().toString());
		                tableJson.addProperty("statut", table.getStatut());
		                tablesArray.add(tableJson);
		            }
		        }
		        eventJson.add("tables", tablesArray);
		        
		        if (evenement.hasMedia()) {
		            String imagePath = getImagePath(request, "evenements", evenement.getMediaPath());
		            eventJson.addProperty("imageUrl", imagePath);
		        }
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        responseJson.add("data", eventJson);
		        out.print(responseJson.toString());
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}
		private void handleReserverEvenement(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
    try {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
        
        String userIdStr = jsonBody.get("userId").getAsString();
        String eventIdStr = jsonBody.get("eventId").getAsString();
        String moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
        
        if (userIdStr == null || eventIdStr == null || moyenPaiement == null) {
            sendError(response, out, "Paramètres manquants", 400);
            return;
        }
        
        int userId = Integer.parseInt(userIdStr);
        int eventId = Integer.parseInt(eventIdStr);
        
        EvenementDAO evenementDAO = new EvenementDAO();
        Evenement evenement = evenementDAO.getById(eventId);
        
        if (evenement == null || !"VISIBLE".equals(evenement.getStatut())) {
            sendError(response, out, "Événement non disponible", 404);
            return;
        }
        
        if (evenement.getDateEvent().isBefore(LocalDateTime.now())) {
            sendError(response, out, "L'événement est déjà passé", 400);
            return;
        }
        
        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
        
        if (reservationDAO.utilisateurADejaReserve(eventId, userId)) {
            sendError(response, out, "Vous avez déjà une réservation pour cet événement", 400);
            return;
        }
        
        List<ReservationTable> tables = new ArrayList<>();
        TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
        BigDecimal montantTotal = BigDecimal.ZERO;
        int capaciteTotale = 0;
        
        if (jsonBody.has("tables") && jsonBody.get("tables").isJsonArray()) {
            JsonArray tablesArray = jsonBody.get("tables").getAsJsonArray();
            
            if (tablesArray.size() == 0) {
                sendError(response, out, "Aucune table sélectionnée", 400);
                return;
            }
            
            for (JsonElement element : tablesArray) {
                JsonObject tableObj = element.getAsJsonObject();
                
                int tableId = tableObj.get("tableId").getAsInt();
                int quantite = tableObj.get("quantite").getAsInt();
                
                if (quantite <= 0) {
                    sendError(response, out, "Quantité invalide pour la table " + tableId, 400);
                    return;
                }
                
                TypeTableEvenement table = typeTableDAO.getById(tableId);
                if (table == null || !"ACTIF".equals(table.getStatut())) {
                    sendError(response, out, "Table non disponible: " + tableId, 400);
                    return;
                }
                
                int disponible = typeTableDAO.getQuantiteDisponible(tableId);
                if (disponible < quantite) {
                    sendError(response, out, "Quantité non disponible pour la table: " + table.getNom(), 400);
                    return;
                }
                
                BigDecimal montantTable = table.getPrix().multiply(new BigDecimal(quantite));
                
                ReservationTable reservationTable = new ReservationTable();
                reservationTable.setTypeTableId(tableId);
                reservationTable.setQuantite(quantite);
                reservationTable.setMontant(montantTable);
                
                tables.add(reservationTable);
                montantTotal = montantTotal.add(montantTable);
                capaciteTotale += table.getCapacite() * quantite;
            }
        } else if (jsonBody.has("tableId") && jsonBody.has("quantiteTables")) {
            int tableId = jsonBody.get("tableId").getAsInt();
            int quantiteTables = jsonBody.get("quantiteTables").getAsInt();
            
            TypeTableEvenement table = typeTableDAO.getById(tableId);
            if (table == null || !"ACTIF".equals(table.getStatut())) {
                sendError(response, out, "Type de table non disponible", 400);
                return;
            }
            
            montantTotal = table.getPrix().multiply(new BigDecimal(quantiteTables));
            capaciteTotale = table.getCapacite() * quantiteTables;
            
            ReservationTable reservationTable = new ReservationTable();
            reservationTable.setTypeTableId(tableId);
            reservationTable.setQuantite(quantiteTables);
            reservationTable.setMontant(montantTotal);
            
            tables.add(reservationTable);
        } else {
            sendError(response, out, "Aucune table spécifiée", 400);
            return;
        }
        
        ReservationEvenement reservation = new ReservationEvenement();
        reservation.setEvenementId(eventId);
        reservation.setUtilisateurId(userId);
        reservation.setMoyenPaiement(moyenPaiement);
        reservation.setMontantTotal(montantTotal);
        
        if (moyenPaiement.equals("MONCASH") || moyenPaiement.equals("NATCASH") || moyenPaiement.equals("VIREMENT")) {
            if (!jsonBody.has("nomPersonne") || !jsonBody.has("numeroTransaction")) {
                sendError(response, out, "Informations de transaction manquantes pour ce moyen de paiement", 400);
                return;
            }
            reservation.setNomPersonne(jsonBody.get("nomPersonne").getAsString());
            reservation.setNumeroTransaction(jsonBody.get("numeroTransaction").getAsString());
            reservation.setStatut("EN_ATTENTE");
        } else {
            reservation.setStatut("CONFIRMEE");
        }
        
        if (jsonBody.has("numeroTransfert")) {
            reservation.setNumeroTransfert(jsonBody.get("numeroTransfert").getAsString());
        }
        
        boolean paiementParSolde = "SOLDE".equalsIgnoreCase(moyenPaiement);
        
        // ========== GESTION SOLDE ==========
        if (paiementParSolde) {
            CompteClientDAO compteDAO = new CompteClientDAO();
            CompteClient compte = compteDAO.getCompteByClientId(userId);
            
            if (compte == null) {
                sendError(response, out, "Compte client introuvable", 404);
                return;
            }
            
            BigDecimal soldeActuel = compte.getSolde();
            
            // RÈGLE : Pas de crédit pour les réservations
            if (soldeActuel.compareTo(montantTotal) < 0) {
                BigDecimal manquant = montantTotal.subtract(soldeActuel);
                sendError(response, out, 
                    "Solde insuffisant pour la réservation.\n" +
                    "Solde disponible: " + String.format("%.2f HTG", soldeActuel) + "\n" +
                    "Montant réservation: " + String.format("%.2f HTG", montantTotal) + "\n" +
                    "Il manque: " + String.format("%.2f HTG", manquant) + "\n" +
                    "Note: Les réservations ne peuvent pas utiliser le crédit.", 400);
                return;
            }
            
            BigDecimal nouveauSolde = soldeActuel.subtract(montantTotal);
            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
            
            if (!updateReussi) {
                sendError(response, out, "Erreur lors du débit du solde", 500);
                return;
            }
            
            System.out.println("=== RÉSERVATION ÉVÉNEMENT PAR SOLDE ===");
            System.out.println("Client #" + userId);
            System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuel));
            System.out.println("Montant réservation: " + String.format("%.2f HTG", montantTotal));
            System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSolde));
        }
        
        // Créer la réservation
        int reservationId = reservationDAO.ajouterAvecTables(reservation, tables);
        
        if (reservationId > 0) {
            // ========== GESTION TRANSACTION (SOLDE UNIQUEMENT) ==========
            if (paiementParSolde) {
                try {
                    CompteClientDAO compteDAO = new CompteClientDAO();
                    CompteClient compte = compteDAO.getCompteByClientId(userId);
                    
                    if (compte != null) {
                        BigDecimal ancienSolde = compte.getSolde().add(montantTotal);
                        BigDecimal nouveauSolde = compte.getSolde();
                        
                        TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
                        TransactionCompte transaction = new TransactionCompte();
                        transaction.setCompteClientId(compte.getId());
                        transaction.setMontant(montantTotal.negate());
                        transaction.setSoldeAvant(ancienSolde);
                        transaction.setSoldeApres(nouveauSolde);
                        transaction.setNotes("Réservation événement: " + evenement.getTitre() + " #" + reservationId);
                        transaction.setDateTransaction(new Timestamp(new Date().getTime()));
                        transaction.setCaissiereId(userId);
                        transaction.setTypeTransactionId(6);
                        transaction.setReservationEvenementId(reservationId);
                        
                        int transactionId = transactionDAO.creerTransaction(transaction);
                        
                        if (transactionId > 0) {
                            System.out.println("Transaction événement enregistrée #" + transactionId);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur création transaction: " + e.getMessage());
                }
            }
            
            // ========== ATTRIBUTION DES POINTS ==========
            // SOLDE : Points attribués immédiatement
            // MONCASH/NATCASH/VIREMENT : Points attribués après validation
            if (paiementParSolde) {
                try {
                    PointManagerDAO pointManagerDAO = new PointManagerDAO();
                    Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourReservation_v2(
                        userId, 
                        reservationId, 
                        montantTotal, 
                        "SOLDE", 
                        request.getRemoteAddr());
                    
                    if (resultPoints.containsKey("success") && (Boolean) resultPoints.get("success")) {
                        int totalPoints = (Integer) resultPoints.get("totalPoints");
                        System.out.println("Points attribués pour événement: " + totalPoints + " points");
                        
                        PointDAO pointDAO = new PointDAO();
                        PrivilegeNiveau nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(userId);
                        
                        if (nouveauNiveau != null) {
                            System.out.println("🎉 Utilisateur #" + userId + " a changé de niveau vers: " + nouveauNiveau.getNom());
                            // La notification a été automatiquement insérée dans la table NOTIFICATION
                        }
                        
                        
                        NotificationDAO notifDAO = new NotificationDAO();
                        Notification pointsNotif = new Notification();
                        pointsNotif.setGeneratedBy("SYSTEM");
                        pointsNotif.setToUser(userId);
                        pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + 
                                              " points pour votre réservation d'événement #" + reservationId);
                        pointsNotif.setTypeNotif("POINTS");
                        pointsNotif.setStatus("VISIBLE");
                        notifDAO.ajouterNotification(pointsNotif);
                    } else {
                        System.err.println("Erreur attribution points: " + resultPoints.get("error"));
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
                }
            }
            
            // ========== RÉPONSE JSON ==========
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "ok");
            
            JsonObject data = new JsonObject();
            data.addProperty("reservationId", reservationId);
            
            String confirmationMessage = "Réservation créée avec succès";
            if (paiementParSolde) {
                CompteClientDAO compteDAO = new CompteClientDAO();
                CompteClient compte = compteDAO.getCompteByClientId(userId);
                if (compte != null) {
                    BigDecimal ancienSolde = compte.getSolde().add(montantTotal);
                    BigDecimal nouveauSolde = compte.getSolde();
                    
                    confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
                    confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
                    confirmationMessage += "\n🎉 Des points vous ont été attribués !";
                }
            } else if (moyenPaiement.equals("MONCASH") || moyenPaiement.equals("NATCASH") || moyenPaiement.equals("VIREMENT")) {
                confirmationMessage += "\nVotre réservation est en attente de validation.";
                confirmationMessage += "\nLes points vous seront attribués après validation du paiement.";
            }
            
            data.addProperty("message", confirmationMessage);
            data.addProperty("montantTotal", montantTotal.toString());
            data.addProperty("capaciteTotale", capaciteTotale);
            data.addProperty("nombreTables", tables.size());
            data.addProperty("statut", reservation.getStatut());
            
            JsonArray tablesDetails = new JsonArray();
            for (ReservationTable table : tables) {
                JsonObject tableDetail = new JsonObject();
                tableDetail.addProperty("tableId", table.getTypeTableId());
                tableDetail.addProperty("quantite", table.getQuantite());
                tableDetail.addProperty("montant", table.getMontant().toString());
                tablesDetails.add(tableDetail);
            }
            data.add("tables", tablesDetails);
            
            responseJson.add("data", data);
            out.print(responseJson.toString());
            
            // ========== NOTIFICATIONS ==========
            NotificationDAO notifDAO = new NotificationDAO();
            
            String messageUser = "Votre réservation pour " + evenement.getTitre() + 
                               " (" + tables.size() + " type(s) de table(s)) a été créée. ";
            
            if ("CONFIRMEE".equals(reservation.getStatut())) {
                messageUser += "Statut: Confirmée";
                if (paiementParSolde) {
                    messageUser += " - Points attribués";
                }
            } else {
                messageUser += "Statut: En attente de validation";
            }
            
            Notification notifUser = new Notification();
            notifUser.setGeneratedBy("SYSTEM");
            notifUser.setToUser(userId);
            notifUser.setMessages(messageUser);
            notifUser.setTypeNotif("RESERVATION_EVENEMENT");
            notifUser.setStatus("VISIBLE");
            notifDAO.ajouterNotification(notifUser);
            
            if (moyenPaiement.equals("MONCASH") || moyenPaiement.equals("NATCASH") || moyenPaiement.equals("VIREMENT")) {
                UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                List<Utilisateur> admins = utilisateurDAO.findByRole("ADMINISTRATEUR");
                for (Utilisateur admin : admins) {
                    Notification notifAdmin = new Notification();
                    notifAdmin.setGeneratedBy("SYSTEM");
                    notifAdmin.setToUser(admin.getId());
                    notifAdmin.setMessages("Nouvelle réservation événement #" + reservationId + 
                                         " en attente de validation. Montant: " + montantTotal + " HTG");
                    notifAdmin.setTypeNotif("ADMIN_RESERVATION");
                    notifAdmin.setStatus("VISIBLE");
                    notifDAO.ajouterNotification(notifAdmin);
                }
            }
            
        } else {
            sendError(response, out, "Erreur création réservation", 500);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        sendError(response, out, "Erreur: " + e.getMessage(), 500);
    }
}
		private void handleTablesEvenement(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String eventIdStr = request.getParameter("eventId");
		        
		        if (eventIdStr == null) {
		            sendError(response, out, "Paramètre eventId manquant", 400);
		            return;
		        }
		        
		        int eventId = Integer.parseInt(eventIdStr);
		        TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
		        List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(eventId);
		        
		        JsonArray tablesArray = new JsonArray();
		        
		        for (TypeTableEvenement table : tables) {
		            JsonObject tableJson = new JsonObject();
		            tableJson.addProperty("id", table.getId());
		            tableJson.addProperty("nom", table.getNom());
		            tableJson.addProperty("description", table.getDescription());
		            tableJson.addProperty("capacite", table.getCapacite());
		            tableJson.addProperty("prix", table.getPrix().toString());
		            tableJson.addProperty("statut", table.getStatut());
		            tableJson.addProperty("evenementId", table.getEvenementId());
		            
		            tablesArray.add(tableJson);
		        }
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        
		        JsonObject data = new JsonObject();
		        data.add("tables", tablesArray);
		        data.addProperty("total", tables.size());
		        
		        responseJson.add("data", data);
		        out.print(responseJson.toString());
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}

		private void handleMesReservationsEvenements(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String userIdStr = request.getParameter("userId");
		        
		        if (userIdStr == null) {
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        
		        int userId = Integer.parseInt(userIdStr);
		        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
		        List<ReservationEvenement> reservations = reservationDAO.getByUtilisateurId(userId);
		        
		        JsonArray reservationsArray = new JsonArray();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        for (ReservationEvenement reservation : reservations) {
		            JsonObject resJson = new JsonObject();
		            resJson.addProperty("id", reservation.getId());
		            resJson.addProperty("evenementId", reservation.getEvenementId());
		            resJson.addProperty("typeTableId", reservation.getTypeTableId());
		            resJson.addProperty("quantiteTables", reservation.getQuantiteTables());
		            resJson.addProperty("capaciteTotale", reservation.getCapaciteTotale());
		            resJson.addProperty("montantTotal", reservation.getMontantTotal().toString());
		            resJson.addProperty("statut", reservation.getStatut());
		            resJson.addProperty("moyenPaiement", reservation.getMoyenPaiement());
		            resJson.addProperty("nomPersonne", reservation.getNomPersonne() != null ? reservation.getNomPersonne() : "");
		            resJson.addProperty("numeroTransaction", reservation.getNumeroTransaction() != null ? reservation.getNumeroTransaction() : "");
		            resJson.addProperty("dateReservation", 
		                reservation.getDateReservation() != null ? 
		                sdf.format(Timestamp.valueOf(reservation.getDateReservation())) : "");
		            resJson.addProperty("dateValidation", 
		                reservation.getDateValidation() != null ? 
		                sdf.format(Timestamp.valueOf(reservation.getDateValidation())) : "");
		            
		            EvenementDAO evenementDAO = new EvenementDAO();
		            Evenement evenement = evenementDAO.getById(reservation.getEvenementId());
		            
		            if (evenement != null) {
		                JsonObject eventJson = new JsonObject();
		                eventJson.addProperty("titre", evenement.getTitre());
		                eventJson.addProperty("artisteGroupe", evenement.getArtisteGroupe());
		                eventJson.addProperty("dateEvent", 
		                    evenement.getDateEvent() != null ? 
		                    sdf.format(evenement.getDateEventAsDate()) : "");
		                
		                if (evenement.hasMedia()) {
		                    String imagePath = getImagePath(request, "evenements", evenement.getMediaPath());
		                    eventJson.addProperty("imageUrl", imagePath);
		                }
		                
		                resJson.add("evenement", eventJson);
		            }
		            
		            // Ajouter les informations sur la table
		            TypeTableEvenementDAO typeTableDAO = new TypeTableEvenementDAO();
		            TypeTableEvenement table = typeTableDAO.getById(reservation.getTypeTableId());
		            
		            if (table != null) {
		                JsonObject tableJson = new JsonObject();
		                tableJson.addProperty("nom", table.getNom());
		                tableJson.addProperty("description", table.getDescription());
		                tableJson.addProperty("prix", table.getPrix().toString());
		                tableJson.addProperty("capacite", table.getCapacite());
		                resJson.add("table", tableJson);
		            }
		            
		            reservationsArray.add(resJson);
		        }
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        
		        JsonObject data = new JsonObject();
		        data.add("reservations", reservationsArray);
		        data.addProperty("total", reservations.size());
		        
		        responseJson.add("data", data);
		        out.print(responseJson.toString());
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}
		
		
		private void handleReservationsEvenementsAdmin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String userIdStr = request.getParameter("userId");
		        String statut = request.getParameter("statut");
		        
		        if (userIdStr == null) {
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        
		        int userId = Integer.parseInt(userIdStr);
		        
		        Utilisateur user = utilisateurDAO.findById(userId);
		        if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
		                            !user.getRole().getRoleName().equals("MANAGEUR"))) {
		            sendError(response, out, "Permission refusée", 403);
		            return;
		        }
		        
		        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
		        List<ReservationEvenement> reservations;
		        
		        if (statut != null && !statut.isEmpty()) {
		            if ("EN_ATTENTE".equals(statut)) {
		                reservations = reservationDAO.getEnAttente();
		            } else {
		                reservations = reservationDAO.getAllForAdmin();
		                reservations.removeIf(r -> !statut.equals(r.getStatut()));
		            }
		        } else {
		            reservations = reservationDAO.getAllForAdmin();
		        }
		        
		        JsonArray reservationsArray = new JsonArray();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        for (ReservationEvenement reservation : reservations) {
		            JsonObject resJson = new JsonObject();
		            resJson.addProperty("id", reservation.getId());
		            resJson.addProperty("evenementId", reservation.getEvenementId());
		            resJson.addProperty("utilisateurId", reservation.getUtilisateurId());
		            resJson.addProperty("quantiteTickets", reservation.getQuantiteTickets());
		            resJson.addProperty("montantTotal", reservation.getMontantTotal().toString());
		            resJson.addProperty("statut", reservation.getStatut());
		            resJson.addProperty("moyenPaiement", reservation.getMoyenPaiement());
		            resJson.addProperty("nomPersonne", reservation.getNomPersonne() != null ? reservation.getNomPersonne() : "");
		            resJson.addProperty("numeroTransaction", reservation.getNumeroTransaction() != null ? reservation.getNumeroTransaction() : "");
		            resJson.addProperty("numeroTransfert", reservation.getNumeroTransfert() != null ? reservation.getNumeroTransfert() : "");
		            resJson.addProperty("dateReservation", 
		                reservation.getDateReservation() != null ? 
		                sdf.format(Timestamp.valueOf(reservation.getDateReservation())) : "");
		            resJson.addProperty("dateValidation", 
		                reservation.getDateValidation() != null ? 
		                sdf.format(Timestamp.valueOf(reservation.getDateValidation())) : "");
		            resJson.addProperty("validePar", reservation.getValidePar() != null ? reservation.getValidePar() : 0);
		            resJson.addProperty("notes", reservation.getNotes() != null ? reservation.getNotes() : "");
		            
		            EvenementDAO evenementDAO = new EvenementDAO();
		            Evenement evenement = evenementDAO.getById(reservation.getEvenementId());
		            
		            if (evenement != null) {
		                JsonObject eventJson = new JsonObject();
		                eventJson.addProperty("titre", evenement.getTitre());
		                eventJson.addProperty("artisteGroupe", evenement.getArtisteGroupe());
		                eventJson.addProperty("dateEvent", 
		                    evenement.getDateEvent() != null ? 
		                    sdf.format(evenement.getDateEventAsDate()) : "");
		                
		                resJson.add("evenement", eventJson);
		            }
		            
		            reservationsArray.add(resJson);
		        }
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        
		        JsonObject data = new JsonObject();
		        data.add("reservations", reservationsArray);
		        data.addProperty("total", reservations.size());
		        
		        int enAttente = reservationDAO.getEnAttente().size();
		        int confirmees = (int) reservations.stream().filter(r -> "CONFIRMEE".equals(r.getStatut())).count();
		        int annulees = (int) reservations.stream().filter(r -> "ANNULEE".equals(r.getStatut())).count();
		        
		        JsonObject stats = new JsonObject();
		        stats.addProperty("enAttente", enAttente);
		        stats.addProperty("confirmees", confirmees);
		        stats.addProperty("annulees", annulees);
		        stats.addProperty("total", reservations.size());
		        
		        data.add("statistiques", stats);
		        responseJson.add("data", data);
		        out.print(responseJson.toString());
		        
		    } catch (Exception e) {
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}
//
//		private void handleValiderReservationEvenement(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//		        
//		        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
//		        
//		        String adminIdStr = jsonBody.get("adminId").getAsString();
//		        String reservationIdStr = jsonBody.get("reservationId").getAsString();
//		        String notes = jsonBody.has("notes") ? jsonBody.get("notes").getAsString() : "";
//		        
//		        if (adminIdStr == null || reservationIdStr == null) {
//		            sendError(response, out, "Paramètres manquants", 400);
//		            return;
//		        }
//		        
//		        int adminId = Integer.parseInt(adminIdStr);
//		        int reservationId = Integer.parseInt(reservationIdStr);
//		        
//		        Utilisateur admin = utilisateurDAO.findById(adminId);
//		        if (admin == null || (!admin.getRole().getRoleName().equals("ADMINISTRATEUR") && 
//		                            !admin.getRole().getRoleName().equals("MANAGEUR"))) {
//		            sendError(response, out, "Permission refusée", 403);
//		            return;
//		        }
//		        
//		        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
//		        boolean success = reservationDAO.validerReservation(reservationId, adminId, notes);
//		        
//		        if (success) {
//		            ReservationEvenement reservation = reservationDAO.getById(reservationId);
//		            if (reservation != null) {
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(reservation.getUtilisateurId());
//		                notif.setMessages("Votre réservation événement #" + reservationId + " a été validée!");
//		                notif.setTypeNotif("RESERVATION_EVENEMENT");
//		                notif.setStatus("VISIBLE");
//		                
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//		            }
//		            
//		            JsonObject responseJson = new JsonObject();
//		            responseJson.addProperty("status", "ok");
//		            
//		            JsonObject data = new JsonObject();
//		            data.addProperty("message", "Réservation validée avec succès");
//		            data.addProperty("reservationId", reservationId);
//		            
//		            responseJson.add("data", data);
//		            out.print(responseJson.toString());
//		        } else {
//		            sendError(response, out, "Erreur validation réservation", 500);
//		        }
//		        
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
//		    }
//		}

		private void handleAnnulerReservationEvenement(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        StringBuilder sb = new StringBuilder();
		        BufferedReader reader = request.getReader();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line);
		        }
		        String body = sb.toString();
		        
		        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
		        
		        String userIdStr = jsonBody.get("userId").getAsString();
		        String reservationIdStr = jsonBody.get("reservationId").getAsString();
		        String raison = jsonBody.has("raison") ? jsonBody.get("raison").getAsString() : "";
		        
		        if (userIdStr == null || reservationIdStr == null) {
		            sendError(response, out, "Paramètres manquants", 400);
		            return;
		        }
		        
		        int userId = Integer.parseInt(userIdStr);
		        int reservationId = Integer.parseInt(reservationIdStr);
		        
		        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
		        boolean success = reservationDAO.annulerReservation(reservationId, userId, raison);
		        
		        if (success) {
		            ReservationEvenement reservation = reservationDAO.getById(reservationId);
		            if (reservation != null && "SOLDE".equals(reservation.getMoyenPaiement())) {
		                CompteClientDAO compteDAO = new CompteClientDAO();
		                CompteClient compte = compteDAO.getCompteByClientId(userId);
		                
		                if (compte != null) {
		                    BigDecimal nouveauSolde = compte.getSolde().add(reservation.getMontantTotal());
		                    compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
		                    
		                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
		                    TransactionCompte transaction = new TransactionCompte();
		                    transaction.setCompteClientId(compte.getId());
		                    transaction.setMontant(reservation.getMontantTotal());
		                    transaction.setSoldeAvant(compte.getSolde());
		                    transaction.setSoldeApres(nouveauSolde);
		                    transaction.setNotes("Remboursement réservation événement annulée #" + reservationId);
		                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
		                    transaction.setCaissiereId(userId);
		                    transaction.setTypeTransactionId(1);
		                    
		                    transactionDAO.creerTransaction(transaction);
		                }
		            }
		            
		            JsonObject responseJson = new JsonObject();
		            responseJson.addProperty("status", "ok");
		            
		            JsonObject data = new JsonObject();
		            data.addProperty("message", "Réservation annulée avec succès");
		            data.addProperty("reservationId", reservationId);
		            
		            responseJson.add("data", data);
		            out.print(responseJson.toString());
		        } else {
		            sendError(response, out, "Erreur annulation réservation", 500);
		        }
		        
		    } catch (Exception e) {
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}
	
		private void handleListCommandesWithDetails(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			String userIdStr = request.getParameter("utilisateur_id");
			String tableIdStr = request.getParameter("table_id");
			String dateDebutStr = request.getParameter("dateDebut");
			String dateFinStr = request.getParameter("dateFin");
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre utilisateur_id manquant", 400);
				return;
			}
	
			try {
				int userId = Integer.parseInt(userIdStr);
				Integer tableId = (tableIdStr != null && !tableIdStr.isEmpty()) ? Integer.parseInt(tableIdStr) : null;
	
				Timestamp dateDebut = (dateDebutStr != null && !dateDebutStr.isEmpty()) ? Timestamp.valueOf(dateDebutStr) // déjà
																															// au
																															// format
																															// yyyy-MM-dd
																															// HH:mm:ss
						: null;
	
				Timestamp dateFin = (dateFinStr != null && !dateFinStr.isEmpty()) ? Timestamp.valueOf(dateFinStr) // déjà au
																													// format
																													// yyyy-MM-dd
																													// HH:mm:ss
						: null;
	
				// Récupérer toutes les commandes avec filtres
				List<Commande> commandes = commandeDAO.getCommandesByFilters(userId, tableId, dateDebut, dateFin);
	
				// Charger les détails pour chaque commande
				for (Commande commande : commandes) {
					List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
					commande.setDetails(details);
				}
	
				Gson gsonIncludeNulls = new GsonBuilder().serializeNulls().create();
	
				jsonResponse("ok", gsonIncludeNulls.toJsonTree(commandes), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
		
		private void handleHistoriquePointsClient(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String userIdStr = request.getParameter("userId");
		        String clientIdStr = request.getParameter("clientId"); // Optionnel pour admin
		        String dateDebutStr = request.getParameter("dateDebut");
		        String dateFinStr = request.getParameter("dateFin");
		        
		        if (userIdStr == null) {
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        
		        int userId = Integer.parseInt(userIdStr);
		        int targetClientId = userId;
		        
		        // Vérifier les permissions si clientId est fourni
		        if (clientIdStr != null && !clientIdStr.isEmpty()) {
		            int paramClientId = Integer.parseInt(clientIdStr);
		            Utilisateur user = utilisateurDAO.findById(userId);
		            if (user != null && (user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
		                                user.getRole().getRoleName().equals("MANAGEUR"))) {
		                targetClientId = paramClientId;
		            } else {
		                sendError(response, out, "Permission refusée", 403);
		                return;
		            }
		        }
		        
		        // Parser les dates
		        java.sql.Date dateDebut = null;
		        java.sql.Date dateFin = null;
		        
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        
		        try {
		            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
		                java.util.Date parsedDebut = sdf.parse(dateDebutStr);
		                dateDebut = new java.sql.Date(parsedDebut.getTime());
		            }
		            
		            if (dateFinStr != null && !dateFinStr.isEmpty()) {
		                java.util.Date parsedFin = sdf.parse(dateFinStr);
		                dateFin = new java.sql.Date(parsedFin.getTime());
		            }
		        } catch (ParseException e) {
		            sendError(response, out, "Format de date invalide. Utilisez YYYY-MM-DD", 400);
		            return;
		        }
		        
		        // ========== AJOUT IMPORTANT ==========
		        // Si les dates ne sont pas fournies, prendre le jour actuel
		        if (dateDebut == null) {
		            // Date de début = aujourd'hui à 00:00:00
		            Calendar calDebut = Calendar.getInstance();
		            calDebut.set(Calendar.HOUR_OF_DAY, 0);
		            calDebut.set(Calendar.MINUTE, 0);
		            calDebut.set(Calendar.SECOND, 0);
		            calDebut.set(Calendar.MILLISECOND, 0);
		            dateDebut = new java.sql.Date(calDebut.getTimeInMillis());
		        }

		        
		        if (dateFin == null) {
		            // Date de fin = aujourd'hui à 23:59:59
		            Calendar calFin = Calendar.getInstance();
		            calFin.set(Calendar.HOUR_OF_DAY, 23);
		            calFin.set(Calendar.MINUTE, 59);
		            calFin.set(Calendar.SECOND, 59);
		            calFin.set(Calendar.MILLISECOND, 999);
		            dateFin = new java.sql.Date(calFin.getTimeInMillis());
		        }
		        
		        System.out.println("Debug - Date début: " + dateDebut);
		        System.out.println("Debug - Date fin: " + dateFin);
		        // ======================================
		        
		        // Récupérer l'historique des points via le DAO existant
		        PointHistoriqueManager historiqueManager = new PointHistoriqueManager();
		        List<PointHistorique> historiqueList = historiqueManager.getHistoriqueUtilisateur(targetClientId, dateDebut, dateFin);
		        
		        // Préparer la réponse JSON
		        JsonArray historiqueArray = new JsonArray();
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        for (PointHistorique historique : historiqueList) {
		            JsonObject historiqueJson = new JsonObject();
		            historiqueJson.addProperty("id", historique.getId());
		            historiqueJson.addProperty("typeAction", historique.getTypeAction());
		            historiqueJson.addProperty("sourceType", historique.getSourceType() != null ? historique.getSourceType() : "");
		            historiqueJson.addProperty("totalPoints", historique.getTotalPoints() != null ? historique.getTotalPoints() : 0);
		            historiqueJson.addProperty("ancienPoint", historique.getAncienPoint() != null ? historique.getAncienPoint() : 0);
		            historiqueJson.addProperty("nouveauPoint", historique.getNouveauPoint() != null ? historique.getNouveauPoint() : 0);
		            historiqueJson.addProperty("difference", historique.getDifference() != null ? historique.getDifference() : 0);
		            historiqueJson.addProperty("notes", historique.getNotes() != null ? historique.getNotes() : "");
		            historiqueJson.addProperty("createdAt", 
		                historique.getCreatedAt() != null ? 
		                dateFormat.format(historique.getCreatedAt()) : "");
		            
		            // Informations supplémentaires selon le type
		            if (historique.getCommandeId() != null) {
		                historiqueJson.addProperty("commandeId", historique.getCommandeId());
		            }
		            
		            if (historique.getQuantite() != null) {
		                historiqueJson.addProperty("quantite", historique.getQuantite());
		            }
		            
		            if (historique.getPointsParUnite() != null) {
		                historiqueJson.addProperty("pointsParUnite", historique.getPointsParUnite());
		            }
		            
		            historiqueArray.add(historiqueJson);
		        }
		        
		        // Calculer les statistiques
		        int totalAcquis = 0;
		        int totalUtilises = 0;
		        int totalExpires = 0;
		        
		        for (PointHistorique historique : historiqueList) {
		            if (historique.getDifference() != null) {
		                if (historique.getDifference() > 0) {
		                    totalAcquis += historique.getDifference();
		                } else if (historique.getDifference() < 0) {
		                    if ("EXPIRATION".equals(historique.getTypeAction())) {
		                        totalExpires += Math.abs(historique.getDifference());
		                    } else if ("UTILISATION".equals(historique.getTypeAction())) {
		                        totalUtilises += Math.abs(historique.getDifference());
		                    }
		                }
		            }
		        }
		        
		        // Récupérer le solde actuel de points de l'utilisateur
		        Utilisateur client = utilisateurDAO.findById(targetClientId);
		        int pointsActuels = (client != null && client.getPoint() != null) ? client.getPoint() : 0;
		        
		        JsonObject responseJson = new JsonObject();
		        responseJson.addProperty("status", "ok");
		        
		        JsonObject data = new JsonObject();
		        data.add("historique", historiqueArray);
		        data.addProperty("totalOperations", historiqueList.size());
		        data.addProperty("pointsActuels", pointsActuels);
		        data.addProperty("totalAcquis", totalAcquis);
		        data.addProperty("totalUtilises", totalUtilises);
		        data.addProperty("totalExpires", totalExpires);
		        
		        // Toujours afficher les dates utilisées dans la réponse
		        data.addProperty("dateDebut", sdf.format(dateDebut));
		        data.addProperty("dateFin", sdf.format(dateFin));
		        data.addProperty("dateDebutParDefaut", dateDebutStr == null || dateDebutStr.isEmpty());
		        data.addProperty("dateFinParDefaut", dateFinStr == null || dateFinStr.isEmpty());
		        
		        responseJson.add("data", data);
		        out.print(responseJson.toString());
		        
		    } catch (NumberFormatException e) {
		        sendError(response, out, "Format de paramètre invalide", 400);
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		    }
		}
		private String genererNumeroCommande() {
			Random random = new Random();
			String numeroCommande;
			do {
				int part1 = 100 + random.nextInt(900); // 3 chiffres entre 100 et 999
				int part2 = 1000 + random.nextInt(9000); // 4 chiffres entre 1000 et 9999
				numeroCommande = "CMD-" + part1 + "-" + part2;
			} while (commandeDAO.existeNumeroCommande(numeroCommande));
			return numeroCommande;
		}
	
	
	
		private void handleUpdateTableState(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Lire le corps JSON
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				String body = sb.toString();
				System.out.println("Corps de la requête JSON: " + body);
	
				JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	
				// Vérifier les paramètres
				if (!json.has("tableId") || !json.has("etat") || !json.has("userId")) {
					sendError(response, out, "Paramètres manquants (tableId, etat, userId)", 400);
					return;
				}
	
				int tableId = json.get("tableId").getAsInt();
				String etatActuel = json.get("etat").getAsString().toUpperCase();
				int userId = json.get("userId").getAsInt();
	
				// DAO
				TableRooftopDAO tableDAO = new TableRooftopDAO();
				TableRooftop table = tableDAO.chercherParId(tableId);
				if (table == null) {
					sendError(response, out, "Table introuvable", 404);
					return;
				}
	
				if ("DELETED".equalsIgnoreCase(table.getStatut())) {
					JsonObject data = new JsonObject();
					data.addProperty("message", "Opération invalide, la table a été supprimée");
					jsonResponse("error", data, out);
					return;
				}
	
				try {
					// Mettre à jour l'état
					table.setEtatActuel(etatActuel);
					table.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					boolean success = tableDAO.modifierTableState(tableId, etatActuel);
	
					JsonObject data = new JsonObject();
					data.addProperty("message",
							success ? "État de la table modifié" : "Erreur lors de la modification de l'état");
					jsonResponse(success ? "ok" : "error", data, out);
	
				} catch (IllegalStateException ex) {
					// Ici on retourne le message pour le toast
					JsonObject data = new JsonObject();
					data.addProperty("message", ex.getMessage()); // "Valider d'abord toutes les commandes de cette table"
					jsonResponse("error", data, out);
				}
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la mise à jour de l'état de la table: " + e.getMessage(), 500);
			}
		}
	
		private void sendError(HttpServletResponse response, PrintWriter out, String message, int code) {
			response.setStatus(code);
			JsonObject errorResponse = new JsonObject();
			errorResponse.addProperty("status", "error");
			errorResponse.addProperty("code", code);
			errorResponse.addProperty("message", message);
			out.print(gson.toJson(errorResponse));
			out.flush();
		}
	
		
	
		private void handleLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String username = request.getParameter("login");
			String password = request.getParameter("password");
			String clientIp = request.getRemoteAddr();
	
			// Validation des paramètres
			if (username == null || password == null) {
				sendError(response, out, "Paramètres manquants (login/password)", 400);
				return;
			}
	
			// Vérification de l'IP si nécessaire
	//        if (!clientIp.equals("127.0.0.1") || !clientIp.equals("192.168.1.115")) {
	//            sendError(response, out, "Accès non autorisé depuis cette IP", 403);
	//            return;
	//        }
	
			try {
				// 1. Trouver l'utilisateur par login/email
				Utilisateur user = utilisateurDAO.findByLoginOrEmail(username);
	
				if (user == null) {
					sendError(response, out, "Nom d'utilisateur invalides", 401);
					return;
				}
	
				// 2. Vérifier le mot de passe
				if (!PasswordUtils.checkPassword(password, user.getMotDePasse())) {
					sendError(response, out, "Mot de pase invalides", 401);
					return;
				}
	
				// 3. Vérifier le rôle de l'utilisateur
				String role = user.getRole().getRoleName();
				if (!(role.equalsIgnoreCase("CAISSIER(ERE)") || role.equalsIgnoreCase("VENDEUR(EUSE)")
						|| role.equalsIgnoreCase("CLIENT") || role.equalsIgnoreCase("VENDEUR"))) {
					sendError(response, out, "Accès refusé", 403);
					return;
				}
	
				// 3. Si tout est valide, préparer la réponse
				JsonObject userData = new JsonObject();
				userData.addProperty("id", user.getId());
				userData.addProperty("login", user.getLogin());
				userData.addProperty("email", user.getEmail());
				userData.addProperty("nomComplet", user.getNom());
				userData.addProperty("ip", clientIp);
				userData.addProperty("role", user.getRole().getRoleName());
				userData.addProperty("telephone", user.getTelephone());
				userData.addProperty("adresse", user.getAdresse());
				userData.addProperty("point", user.getPoint());
				userData.addProperty("privillege", user.getPrivilege());
	
				JsonObject responseData = new JsonObject();
				responseData.add("user", userData);
	
				jsonResponse("ok", responseData, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur d'authentification: " + e.getMessage(), 500);
			}
		}
	
		private void handleIsThereNotif(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String userIdStr = request.getParameter("userId");
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre manquant (userId)", 400);
				return;
			}
	
			try {
				int userId = Integer.parseInt(userIdStr);
				boolean existe = notificationDAO.isThereNotif(userId);
				jsonResponse("ok", existe ? 1 : 0, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleChangeNotifToRead(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String userIdStr = request.getParameter("userId");
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre manquant (userId)", 400);
				return;
			}
			System.out.println("CHAGER APPELE");
	
			try {
				int userId = Integer.parseInt(userIdStr);
				boolean success = notificationDAO.changeNotifToRead(userId);
				if (success) {
					jsonResponse("ok", "Notifications marquées comme lues", out);
				} else {
					jsonResponse("ok", "Aucune notification non lue trouvée", out);
				}
	
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleGetUserByRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Récupérer le paramètre "role"
				String roleName = request.getParameter("role");
				if (roleName == null || roleName.isEmpty()) {
					sendError(response, out, "Paramètre 'role' manquant", 400);
					return;
				}
	
				// Appel DAO pour récupérer les utilisateurs par rôle
				List<Utilisateur> users = utilisateurDAO.findByRole(roleName);
	
				// Préparer le tableau JSON
				JsonArray jsonUsers = new JsonArray();
				for (Utilisateur u : users) {
					JsonObject jsonUser = new JsonObject();
					jsonUser.addProperty("id", u.getId());
					jsonUser.addProperty("nom", u.getNom());
					jsonUser.addProperty("prenom", u.getPrenom());
					jsonUser.addProperty("email", u.getEmail());
					jsonUser.addProperty("login", u.getLogin());
					jsonUser.add("role", gson.toJsonTree(u.getRole()));
					jsonUser.addProperty("statut", u.getStatut());
					jsonUser.addProperty("creationDate", u.getCreationDate().toString());
					jsonUser.addProperty("updateDate", u.getUpdateDate().toString());
					jsonUsers.add(jsonUser);
				}
	
				// Retour JSON uniforme
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.addProperty("status", "ok");
				jsonResponse.add("data", jsonUsers);
	
				out.print(gson.toJson(jsonResponse));
				out.flush();
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération des utilisateurs par rôle: " + e.getMessage(),
						500);
			}
		}
	
	//    private void handleCategoriesParentes(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
	//            jsonResponse("ok", gson.toJsonTree(categoriesParentes), out);
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du chargement des catégories parentes: " + e.getMessage(), 500);
	//        }
	//    }
	//
	//    private void handleSousCategoriesByParentId(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            String parentIdStr = request.getParameter("parentId");
	//            if (parentIdStr == null) {
	//                sendError(response, out, "Paramètre parentId manquant", 400);
	//                return;
	//            }
	//            int parentId = Integer.parseInt(parentIdStr);
	//
	//            List<MenuCategorie> sousCategories = menuCategorieDAO.getSousCategoriesByParentId(parentId);
	//            if (sousCategories == null) sousCategories = new ArrayList<>();
	//
	//            List<Plat> plats = new ArrayList<>();
	//            if (!sousCategories.isEmpty()) {
	//                plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategories.get(0).getId());
	//            }
	//
	//            JsonObject data = new JsonObject();
	//            data.add("sousCategories", gson.toJsonTree(sousCategories));
	//            data.add("plats", gson.toJsonTree(plats));
	//
	//            jsonResponse("ok", data, out);
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du chargement des sous-catégories: " + e.getMessage(), 500);
	//        }
	//    }
	
		private void handleNotificationForUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String userIdStr = request.getParameter("userId");
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre manquant (userId)", 400);
				return;
			}
	
			try {
				int userId = Integer.parseInt(userIdStr);
				List<Notification> notifications = notificationDAO.recupererNotificationsUtilisateur(userId);
	
				jsonResponse("ok", gson.toJsonTree(notifications), out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleDeleteNotificationForUser(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			String userIdStr = request.getParameter("userId");
			String deletedByStr = request.getParameter("deletedBy"); // optionnel: qui supprime
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre manquant (userId)", 400);
				return;
			}
	
			try {
				int userId = Integer.parseInt(userIdStr);
				int deletedBy = (deletedByStr != null) ? Integer.parseInt(deletedByStr) : userId; // par défaut c'est
																									// lui-même
	
				boolean success = notificationDAO.supprimerNotificationsUtilisateur(userId, deletedBy);
	
				if (success) {
					jsonResponse("ok", "Notifications supprimées pour l'utilisateur " + userId, out);
				} else {
					sendError(response, out, "Aucune notification trouvée ou erreur de suppression", 404);
				}
	
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleCategoriesParentes(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				List<MenuCategorie> categoriesParentes = menuCategorieDAO.getCategoriesParentes();
				if (categoriesParentes == null)
					categoriesParentes = new ArrayList<>();
	
				// Ajouter l'URL finale pour chaque catégorie
				for (MenuCategorie categorie : categoriesParentes) {
					String finalImageUrl = getImagePath(request, "categorie", categorie.getImageUrl());
					categorie.setImageUrl(finalImageUrl);
				}
	
				jsonResponse("ok", gson.toJsonTree(categoriesParentes), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du chargement des catégories parentes: " + e.getMessage(), 500);
			}
		}
	
	//    private void handleBilan(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            String userIdStr = request.getParameter("userId");
	//            String dateDebutStr = request.getParameter("dateDebut");
	//            String dateFinStr = request.getParameter("dateFin");
	//
	//            if (userIdStr == null || dateDebutStr == null || dateFinStr == null) {
	//                sendError(response, out, "Paramètres manquants (userId, dateDebut, dateFin)", 400);
	//                return;
	//            }
	//
	//            int userId = Integer.parseInt(userIdStr);
	//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//            java.util.Date parsedDebut = sdf.parse(dateDebutStr);
	//            java.util.Date parsedFin = sdf.parse(dateFinStr);
	//
	//            Timestamp dateDebut = new Timestamp(parsedDebut.getTime());
	//            Timestamp dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1); // fin de journée
	//
	//            BilanUtilisateur bilan = commandeDAO.getBilanByStaffId(userId, dateDebut, dateFin);
	//            jsonResponse("ok", gson.toJsonTree(bilan), out);
	//
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du chargement du Bilan: " + e.getMessage(), 500);
	//        }
	//    }
	
		private void handleBilan(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				String dateDebutStr = request.getParameter("dateDebut");
				String dateFinStr = request.getParameter("dateFin");
	
				if (userIdStr == null || dateDebutStr == null || dateFinStr == null) {
					sendError(response, out, "Paramètres manquants (userId, dateDebut, dateFin)", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
	
				// 🔹 Nouveau format avec heures, minutes et secondes
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
				java.util.Date parsedDebut = sdf.parse(dateDebutStr);
				java.util.Date parsedFin = sdf.parse(dateFinStr);
	
				Timestamp dateDebut = new Timestamp(parsedDebut.getTime());
				Timestamp dateFin = new Timestamp(parsedFin.getTime()); // ⚠️ pas besoin d'ajouter +24h
	
				BilanUtilisateur bilan = commandeDAO.getBilanByStaffId(userId, dateDebut, dateFin);
	
				// 🔹 On renvoie aussi les dates formatées correctement dans la réponse
				JsonObject responseJson = new JsonObject();
				responseJson.addProperty("dateDebut", sdf.format(dateDebut));
				responseJson.addProperty("dateFin", sdf.format(dateFin));
				responseJson.add("bilan", gson.toJsonTree(bilan));
	
				jsonResponse("ok", responseJson, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du chargement du Bilan: " + e.getMessage(), 500);
			}
		}
	
		private void handleSousCategoriesByParentId(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			try {
				String parentIdStr = request.getParameter("parentId");
				if (parentIdStr == null) {
					sendError(response, out, "Paramètre parentId manquant", 400);
					return;
				}
				int parentId = Integer.parseInt(parentIdStr);
	
				List<MenuCategorie> sousCategories = menuCategorieDAO.getSousCategoriesByParentId(parentId);
				if (sousCategories == null)
					sousCategories = new ArrayList<>();
	
				// Ajouter l'URL finale pour les sous-catégories
				for (MenuCategorie sousCategorie : sousCategories) {
					String finalImageUrl = getImagePath(request, "categorie", sousCategorie.getImageUrl());
					sousCategorie.setImageUrl(finalImageUrl);
				}
	
				List<Plat> plats = new ArrayList<>();
				if (!sousCategories.isEmpty()) {
					plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategories.get(0).getId());
					if (plats == null)
						plats = new ArrayList<>();
	
					// Ajouter l'URL finale pour les plats et leurs produits liés
					for (Plat plat : plats) {
						String finalPlatImage = getImagePath(request, "plat", plat.getImage());
						plat.setImage(finalPlatImage);
	
						if (plat.getProduit() != null) {
							String finalProduitImage = getImagePath(request, "produit", plat.getProduit().getImageUrl());
							plat.getProduit().setImageUrl(finalProduitImage);
						}
					}
				}
	
				JsonObject data = new JsonObject();
				data.add("sousCategories", gson.toJsonTree(sousCategories));
				data.add("plats", gson.toJsonTree(plats));
	
				jsonResponse("ok", data, out);
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du chargement des sous-catégories: " + e.getMessage(), 500);
			}
		}
	
		private void handlePlatsParSousCategorieJson(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			try {
				String categorieIdStr = request.getParameter("sousCategorieId");
				if (categorieIdStr == null) {
					sendError(response, out, "Paramètre categorieId manquant", 400);
					return;
				}
				int sousCategorieId = Integer.parseInt(categorieIdStr);
	
				List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sousCategorieId);
				if (plats == null)
					plats = new ArrayList<>();
	
				for (Plat plat : plats) {
					// URL pour le plat
					String finalImageUrl = getImagePath(request, "plat", plat.getImage());
					plat.setImage(finalImageUrl);
	
					// URL pour le produit lié (s'il existe)
					if (plat.getProduit() != null) {
						String finalProduitImage = getImagePath(request, "produit", plat.getProduit().getImageUrl());
						plat.getProduit().setImageUrl(finalProduitImage);
					}
				}
	
				JsonObject data = new JsonObject();
				data.add("plats", gson.toJsonTree(plats));
	
				jsonResponse("ok", data, out);
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du chargement des plats: " + e.getMessage(), 500);
			}
		}
	
		private void handleSearchPlats(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String query = request.getParameter("q");
				if (query == null || query.trim().isEmpty()) {
					sendError(response, out, "Paramètre de recherche manquant", 400);
					return;
				}
	
				// DAO : méthode qui cherche les plats dont le nom contient la query
				List<Plat> plats = menuCategorieDAO.searchPlatsByName(query.trim());
				if (plats == null)
					plats = new ArrayList<>();
	
				// Génération URLs images comme pour les autres méthodes
				for (Plat plat : plats) {
					String finalImageUrl = getImagePath(request, "plat", plat.getImage());
					plat.setImage(finalImageUrl);
	
					if (plat.getProduit() != null) {
						String finalProduitImage = getImagePath(request, "produit", plat.getProduit().getImageUrl());
						plat.getProduit().setImageUrl(finalProduitImage);
					}
				}
	
				JsonObject data = new JsonObject();
				data.add("plats", gson.toJsonTree(plats));
				jsonResponse("ok", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la recherche des plats: " + e.getMessage(), 500);
			}
		}
	
	//    @ByYlth
		private void handlePlatsParCategorieJson(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			try {
				String categorieIdStr = request.getParameter("categorieId");
				if (categorieIdStr == null) {
					sendError(response, out, "Paramètre categorieId manquant", 400);
					return;
				}
	
				int id = Integer.parseInt(categorieIdStr);
	
				// ✅ Appel DAO
				List<Plat> plats = platDAO.listePlats(id);
				if (plats == null)
					plats = new ArrayList<>();
	
				// 🔄 Génération des URLs d’image
				for (Plat plat : plats) {
					String finalImageUrl = getImagePath(request, "plat", plat.getImage());
					plat.setImage(finalImageUrl);
	
	//                if (plat.getProduit() != null) {
	//                    String finalProduitImage = getImagePath(request, "produit", plat.getProduit().getImageUrl());
	//                    plat.getProduit().setImageUrl(finalProduitImage);
	//                }
				}
	
				// ✅ Construction JSON
				JsonObject data = new JsonObject();
				data.add("plats", gson.toJsonTree(plats));
	
				jsonResponse("ok", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du chargement des plats : " + e.getMessage(), 500);
			}
		}
	
		private void handleInventaire(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				if (userIdStr == null) {
					sendError(response, out, "Paramètre userId manquant", 400);
					return;
				}
				ProduitDAO produitDAO = new ProduitDAO();
				Map<String, Object> inventaire = produitDAO.listerInventaires();
	
				// Si aucun produit trouvé
				if (inventaire == null || inventaire.isEmpty()) {
					jsonResponse("ok", gson.toJsonTree(Collections.emptyList()), out);
					return;
				}
	
				// Envoi de la liste des produits au format JSON
				jsonResponse("ok", gson.toJsonTree(inventaire), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération de l'inventaire : " + e.getMessage(), 500);
			}
		}
	
		private void handleListCommandesStaff(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String userIdStr = request.getParameter("userId");
			if (userIdStr == null) {
				sendError(response, out, "Paramètre userId manquant", 400);
				return;
			}
	
			try {
				int userId = Integer.parseInt(userIdStr);
				Timestamp dateDebut = null;
				Timestamp dateFin = null;
	
				List<Commande> commandes = commandeDAO.getAllCommandesByStaffMember(userId, dateDebut, dateFin);
	
				for (Commande commande : commandes) {
					List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
					commande.setDetails(details);
				}
	
				jsonResponse("ok", gson.toJsonTree(commandes), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleListCommandesClient(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String userIdStr = request.getParameter("userId");
			if (userIdStr == null) {
				sendError(response, out, "Paramètre userId manquant", 400);
				return;
			}
	
			Timestamp dateDebut = null;
			Timestamp dateFin = null;
	
			try {
				int userId = Integer.parseInt(userIdStr);
				List<Commande> commandes = commandeDAO.getAllCommandesForUserId(userId, dateDebut, dateFin);
	
				for (Commande commande : commandes) {
					List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
					commande.setDetails(details);
				}
	
				jsonResponse("ok", gson.toJsonTree(commandes), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
	//    private void handleRapportCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        String userIdStr = request.getParameter("userId");
	//        String dateDebutStr = request.getParameter("dateDebut");
	//        String dateFinStr = request.getParameter("dateFin");
	//
	//        if (userIdStr == null || dateDebutStr == null || dateFinStr == null) {
	//            sendError(response, out, "Paramètres manquants (userId, dateDebut, dateFin)", 400);
	//            return;
	//        }
	//
	//        try {
	//            int userId = Integer.parseInt(userIdStr);
	//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//            java.util.Date parsedDebut = sdf.parse(dateDebutStr);
	//            java.util.Date parsedFin = sdf.parse(dateFinStr);
	//
	//            Timestamp dateDebut = new Timestamp(parsedDebut.getTime());
	//            Timestamp dateFin = new Timestamp(parsedFin.getTime() + 24*60*60*1000 - 1); // fin de journée
	//
	//
	//            List<Commande> commandes = commandeDAO.getRapportCommandes(userId, dateDebut, dateFin);
	//
	//            for (Commande commande : commandes) {
	//                List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
	//                commande.setDetails(details);
	//            }
	//
	//            jsonResponse("ok", gson.toJsonTree(commandes), out);
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	//        }
	//    }
		private void handleListCreditForClientByStaff(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			String userIdStr = request.getParameter("userId");
			String clientIdStr = request.getParameter("clientId");
			String dateDebutStr = request.getParameter("dateDebut");
			String dateFinStr = request.getParameter("dateFin");
	
			if (userIdStr == null) {
				sendError(response, out, "Paramètre userId manquant", 400);
				return;
			}
	
			Integer userId;
			Integer clientId = null;
			Timestamp dateDebut = null;
			Timestamp dateFin = null;
	
			try {
				userId = Integer.parseInt(userIdStr);
	
				if (clientIdStr != null && !clientIdStr.isEmpty()) {
					clientId = Integer.parseInt(clientIdStr);
				}
	
				if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
					dateDebut = Timestamp.valueOf(dateDebutStr);
				}
	
				if (dateFinStr != null && !dateFinStr.isEmpty()) {
					dateFin = Timestamp.valueOf(dateFinStr);
				}
	
				// Récupérer les commandes crédit
				List<Commande> commandes = commandeDAO.getCommandesCredit(userId, clientId, dateDebut, dateFin);
	
				// Pour chaque commande, charger les détails
				for (Commande commande : commandes) {
					List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
					commande.setDetails(details);
				}
	
				// Retour JSON
				jsonResponse("ok", gson.toJsonTree(commandes), out);
	
			} catch (NumberFormatException e) {
				sendError(response, out, "Erreur: userId ou clientId invalide", 400);
			} catch (IllegalArgumentException e) {
				sendError(response, out, "Erreur: date invalide", 400);
			} catch (Exception e) {
				sendError(response, out, "Erreur serveur: " + e.getMessage(), 500);
				e.printStackTrace();
			}
		}
	//
	//	private void handleRapportCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//		String userIdStr = request.getParameter("userId");
	//		String dateDebutStr = request.getParameter("dateDebut");
	//		String dateFinStr = request.getParameter("dateFin");
	//
	//		if (dateDebutStr == null || dateFinStr == null) {
	//			sendError(response, out, "Paramètres manquants (dateDebut, dateFin)", 400);
	//			return;
	//		}
	//
	//		try {
	//			Integer userId = (userIdStr == null || userIdStr.isEmpty()) ? null : Integer.parseInt(userIdStr);
	//
	//			// 🔧 CORRECTION : Utiliser SimpleDateFormat avec heure
	//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	//			// Début de journée à 00:00:00
	//			java.util.Date parsedDebut = sdf.parse(dateDebutStr + " 00:00:00");
	//			// Fin de journée à 23:59:59
	//			java.util.Date parsedFin = sdf.parse(dateFinStr + " 23:59:59");
	//
	//			Timestamp dateDebut = new Timestamp(parsedDebut.getTime());
	//			Timestamp dateFin = new Timestamp(parsedFin.getTime());
	//
	//			System.out.println("📅 Date début envoyée à SQL: " + dateDebut);
	//			System.out.println("📅 Date fin envoyée à SQL: " + dateFin);
	//
	//			List<Commande> commandes = commandeDAO.getRapportCommandes(userId, dateDebut, dateFin);
	//
	//			for (Commande commande : commandes) {
	//				List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
	//				commande.setDetails(details);
	//			}
	//
	//			jsonResponse("ok", gson.toJsonTree(commandes), out);
	//
	//		} catch (Exception e) {
	//			sendError(response, out, "Erreur: " + e.getMessage(), 500);
	//			e.printStackTrace();
	//		}
	//	}
		
		private void handleRapportCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    String userIdStr = request.getParameter("userId");
		    String dateDebutStr = request.getParameter("dateDebut");
		    String dateFinStr = request.getParameter("dateFin");
	
		    if (dateDebutStr == null || dateFinStr == null) {
		        sendError(response, out, "Paramètres manquants (dateDebut, dateFin)", 400);
		        return;
		    }
	
		    try {
		        Integer userId = (userIdStr == null || userIdStr.isEmpty()) ? null : Integer.parseInt(userIdStr);
	
		        // ✅ FORMAT QUI SUPPORTE LES TIMESTAMPS COMPLETS
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        Timestamp dateDebut = null;
		        Timestamp dateFin = null;
	
		        // Parser la date de début directement depuis le paramètre
		        try {
		            java.util.Date parsedDebut = sdf.parse(dateDebutStr);
		            dateDebut = new Timestamp(parsedDebut.getTime());
		        } catch (ParseException e) {
		            // Si le format avec heure ne marche pas, essayer avec juste la date
		            try {
		                SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
		                java.util.Date parsedDebut = sdfDateOnly.parse(dateDebutStr);
		                Calendar calDebut = Calendar.getInstance();
		                calDebut.setTime(parsedDebut);
		                calDebut.set(Calendar.HOUR_OF_DAY, 0);
		                calDebut.set(Calendar.MINUTE, 0);
		                calDebut.set(Calendar.SECOND, 0);
		                calDebut.set(Calendar.MILLISECOND, 0);
		                dateDebut = new Timestamp(calDebut.getTimeInMillis());
		            } catch (ParseException e2) {
		                sendError(response, out, "Format de dateDebut invalide: " + dateDebutStr, 400);
		                return;
		            }
		        }
	
		        // Parser la date de fin directement depuis le paramètre
		        try {
		            java.util.Date parsedFin = sdf.parse(dateFinStr);
		            dateFin = new Timestamp(parsedFin.getTime());
		        } catch (ParseException e) {
		            // Si le format avec heure ne marche pas, essayer avec juste la date
		            try {
		                SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
		                java.util.Date parsedFin = sdfDateOnly.parse(dateFinStr);
		                Calendar calFin = Calendar.getInstance();
		                calFin.setTime(parsedFin);
		                calFin.set(Calendar.HOUR_OF_DAY, 23);
		                calFin.set(Calendar.MINUTE, 59);
		                calFin.set(Calendar.SECOND, 59);
		                calFin.set(Calendar.MILLISECOND, 0);
		                dateFin = new Timestamp(calFin.getTimeInMillis());
		            } catch (ParseException e2) {
		                sendError(response, out, "Format de dateFin invalide: " + dateFinStr, 400);
		                return;
		            }
		        }
	
		        // Affichage détaillé pour vérification
		        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		        System.out.println("📅 Date début reçue: " + dateDebutStr);
		        System.out.println("📅 Date fin reçue: " + dateFinStr);
		        System.out.println("📅 Date début envoyée à SQL: " + displayFormat.format(dateDebut));
		        System.out.println("📅 Date fin envoyée à SQL: " + displayFormat.format(dateFin));
	
		        List<Commande> commandes = commandeDAO.getRapportCommandes(userId, dateDebut, dateFin);
	
		        for (Commande commande : commandes) {
		            List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
		            commande.setDetails(details);
		        }
	
		        jsonResponse("ok", gson.toJsonTree(commandes), out);
	
		    } catch (Exception e) {
		        sendError(response, out, "Erreur: " + e.getMessage(), 500);
		        e.printStackTrace();
		    }
		}
	//    private void handleCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) { 
	//        try {
	//            // 1. Récupération des infos utilisateur
	//            String userIdStr = request.getParameter("userId");
	//            if (userIdStr == null) {
	//                sendError(response, out, "Paramètre userId manquant", 400);
	//                return;
	//            }
	//            int userId = Integer.parseInt(userIdStr);
	//
	//            // 1bis. Récupération de la table
	//            String tableIdStr = request.getParameter("tableId");
	//            Integer tableId = (tableIdStr != null && !tableIdStr.isEmpty()) ? Integer.parseInt(tableIdStr) : null;
	//
	//            // 2. Récupération des détails en JSON
	//            String detailsJson = request.getParameter("details");
	//            if (detailsJson == null) {
	//                sendError(response, out, "Paramètre details manquant", 400);
	//                return;
	//            }
	//            List<CommandeDetail> details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>(){}.getType());
	//
	//            // 3. Création de la commande
	//            Commande commande = new Commande();
	//            commande.setUtilisateurId(userId);
	//            commande.setNumeroCommande(genererNumeroCommande());
	//            commande.setStatut("EN_ATTENTE");
	//            if (tableId != null) {
	//                TableRooftop table = new TableRooftop();
	//                table.setId(tableId);
	//                commande.setTableRooftop(table);
	//            }
	//
	//            // 4. Ajout dans la base
	//            int newId = commandeDAO.ajouterCommandePOS(commande, details);
	//
	//            // 5. Si table définie, passer le statut à RESERVE
	//            if (tableId != null) {
	//                commandeDAO.reserverTable(tableId);
	//            }
	//
	//            if (newId > 0) {
	//                // 6. Suppression des lignes du panier correspondantes
	//                for (CommandeDetail detail : details) {
	//                    Integer panierId = detail.getPanierId();
	//                    if (panierId != null) {
	//                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
	//                        PanierDAO pannierDAO = new PanierDAO();
	//                        pannierDAO.supprimerDuPanier(userId, panierId, type);
	//                    }
	//                }
	//
	//                // 7. Réponse JSON succès
	//                JsonObject data = new JsonObject();
	//                data.addProperty("commandeId", newId);
	//                data.addProperty("numeroCommande", commande.getNumeroCommande());
	//                data.addProperty("message", "Commande ajoutée et panier mis à jour avec succès");
	//                jsonResponse("ok", data, out);
	//
	//            } else {
	//                sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
	//            }
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
	//        }
	//    }
	
	//private void handleCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//    try {
	//        // 1️⃣ Lire le corps de la requête
	//        StringBuilder sb = new StringBuilder();
	//        BufferedReader reader = request.getReader();
	//        String line;
	//        while ((line = reader.readLine()) != null) {
	//            sb.append(line);
	//        }
	//        String body = sb.toString();
	//
	//        if (body.trim().isEmpty()) {
	//            sendError(response, out, "Corps de requête vide", 400);
	//            return;
	//        }
	//
	//        // 2️⃣ Parser le JSON
	//        JsonObject jsonBody;
	//        try {
	//            jsonBody = gson.fromJson(body, JsonObject.class);
	//        } catch (Exception e) {
	//            sendError(response, out, "JSON invalide", 400);
	//            return;
	//        }
	//
	//        // 3️⃣ Récupérer userId
	//        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
	//            sendError(response, out, "Paramètre userId manquant", 400);
	//            return;
	//        }
	//        int userId = jsonBody.get("userId").getAsInt();
	//        System.out.println("Id user a " + userId);
	//
	//        // 4️⃣ Récupérer tableId (optionnel)
	//        Integer tableId = null;
	//        if (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull()) {
	//            tableId = jsonBody.get("tableId").getAsInt();
	//        }
	//        
	//        Integer clientId = null;
	//        if (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull()) {
	//            clientId = jsonBody.get("clientId").getAsInt();
	//        }
	//
	//       
	//        
	//        Integer serveuseId = null;
	//        if (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull()) {
	//            serveuseId = jsonBody.get("serveuseId").getAsInt();
	//        }
	//
	//        // 5️⃣ Récupérer les détails
	//        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
	//            sendError(response, out, "Paramètre details manquant ou invalide", 400);
	//            return;
	//        }
	//        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
	//        List<CommandeDetail> details;
	//        try {
	//            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {}.getType());
	//        } catch (Exception e) {
	//            sendError(response, out, "Détails JSON invalides", 400);
	//            return;
	//        }
	//
	//        // 6️⃣ Validation des détails
	//        for (int i = 0; i < details.size(); i++) {
	//            CommandeDetail d = details.get(i);
	//            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
	//                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
	//                return;
	//            }
	//            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
	//                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
	//                return;
	//            }
	//        }
	//
	//        // 7️⃣ Création de la commande
	//        Commande commande = new Commande();
	//       // commande.setUtilisateurId(userId);
	//     
	//        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
	//            sendError(response, out, "Paramètre numeroCommande manquant", 400);
	//            return;
	//        }
	//
	//        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
	//        commande.setNumeroCommande(numeroCommande);
	//
	//        commande.setStatutCommande("EN_ATTENTE");
	////        commande.setModePaiement("NON_PAYE");
	//        String statutPaiement = jsonBody.get("statutPaiement").getAsString(); // RESERVE ou DISPONIBLE
	////        if (statutPaiement == null ) {
	////        	statutPaiement = "NON_PAYE";
	////        }
	//        commande.setStatutPaiement(statutPaiement);
	//        
	//        String modePaiement = jsonBody.get("moyenPaiement").getAsString(); // RESERVE ou DISPONIBLE
	////        if (statutPaiement == null ) {
	////        	modePaiement = "NON_PAYE";
	////        }
	//        commande.setModePaiement(modePaiement);
	//        commande.setMontantTotal(null);
	//        
	// 
	//        commande.setDateCommande(new Timestamp(new Date().getTime()));
	//
	//        // 🔹 Récupérer le montantTotal depuis le JSON
	//        BigDecimal montantTotal = null;
	//        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
	//            try {
	//                montantTotal = jsonBody.get("montantTotal").getAsBigDecimal();
	//            } catch (Exception ignored) {}
	//        }
	//        commande.setMontantTotal(montantTotal);
	//        
	//        if (clientId != null && clientId > 0 ) {
	//            commande.setClientId(clientId);
	//        }
	//
	//        if (tableId != null && tableId > 0) {
	//            TableRooftop table = new TableRooftop();
	//            table.setId(tableId);
	//            commande.setTableRooftop(table);
	//        }
	//
	//        if (serveuseId != null && serveuseId > 0) {
	//            commande.setUtilisateurId(serveuseId);
	//        } else {
	//            commande.setUtilisateurId(userId);
	//        }
	//
	//
	//        	
	//
	//        // 8️⃣ Ajout dans la base
	//        int newId = commandeDAO.ajouterCommandePOS(commande, details);
	//        if (newId <= 0) {
	//            sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
	//            return;
	//        }
	//
	//        // 9️⃣ Si table définie, passer le statut à RESERVE
	//        if (tableId != null) {
	//            commandeDAO.reserverTable(tableId);
	//        }
	//
	//        // 🔟 Suppression des lignes du panier correspondantes
	//        for (CommandeDetail detail : details) {
	//            Integer panierId = detail.getPanierId();
	//            if (panierId != null) {
	//                String type = (detail.getPlatId() != null) ? "plat" : "produit";
	//                PanierDAO pannierDAO = new PanierDAO();
	//                pannierDAO.supprimerDuPanier(userId, panierId, type);
	//            }
	//        }
	//
	//        // 1️⃣1️⃣ Réponse JSON succès
	//        JsonObject data = new JsonObject();
	//        data.addProperty("commandeId", newId);
	//        data.addProperty("numeroCommande", commande.getNumeroCommande());
	//        data.addProperty("message", "Commande ajoutée et panier mis à jour avec succès");
	//        jsonResponse("ok", data, out);
	//
	//    } catch (Exception e) {
	//        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
	//    }
	//}
//		BON 24-12-25
//		private void handleCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//			try {
//				// 1️⃣ Lire le corps de la requête
//				StringBuilder sb = new StringBuilder();
//				BufferedReader reader = request.getReader();
//				String line;
//				while ((line = reader.readLine()) != null) {
//					sb.append(line);
//				}
//				String body = sb.toString();
//	
//				if (body.trim().isEmpty()) {
//					sendError(response, out, "Corps de requête vide", 400);
//					return;
//				}
//	
//				// 2️⃣ Parser le JSON
//				JsonObject jsonBody;
//				try {
//					jsonBody = gson.fromJson(body, JsonObject.class);
//				} catch (Exception e) {
//					sendError(response, out, "JSON invalide", 400);
//					return;
//				}
//	
//				// 🔹 Récupérer action
//				String action = "";
//				if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//					action = jsonBody.get("action").getAsString().trim();
//				}
//	
//				// 3️⃣ Récupérer userId
//				if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//					sendError(response, out, "Paramètre userId manquant", 400);
//					return;
//				}
//				int userId = jsonBody.get("userId").getAsInt();
//	
//				// 4️⃣ Récupérer tableId / clientId / serveuseId
//				Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
//						? jsonBody.get("tableId").getAsInt()
//						: null;
//				Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
//						? jsonBody.get("clientId").getAsInt()
//						: null;
//				Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
//						? jsonBody.get("serveuseId").getAsInt()
//						: null;
//	
//				// 5️⃣ Récupérer les détails
//				if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
//					sendError(response, out, "Paramètre details manquant ou invalide", 400);
//					return;
//				}
//				JsonArray detailsJson = jsonBody.getAsJsonArray("details");
//				List<CommandeDetail> details;
//				try {
//					details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
//					}.getType());
//				} catch (Exception e) {
//					sendError(response, out, "Détails JSON invalides", 400);
//					return;
//				}
//	
//				// 6️⃣ Validation des détails
//				for (int i = 0; i < details.size(); i++) {
//					CommandeDetail d = details.get(i);
//					if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
//						sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
//						return;
//					}
//					if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
//						sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
//						return;
//					}
//				}
//	
//				// 7️⃣ Construire l’objet commande
//				Commande commande = new Commande();
//	
//				if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
//					sendError(response, out, "Paramètre numeroCommande manquant", 400);
//					return;
//				}
//				String numeroCommande = jsonBody.get("numeroCommande").getAsString();
//				commande.setNumeroCommande(numeroCommande);
//	
//				// commande.setStatutCommande("EN_ATTENTE");
//				commande.setDateCommande(new Timestamp(new Date().getTime()));
//				if (jsonBody.has("statutCommande")) {
//					commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
//				}
//				if (jsonBody.has("statutPaiement")) {
//					commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
//				}
//				if (jsonBody.has("moyenPaiement")) {
//					commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
//				}
//				if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
//					commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
//				}
//	
//				if (clientId != null && clientId > 0)
//					commande.setClientId(clientId);
//				if (tableId != null && tableId > 0) {
//					TableRooftop table = new TableRooftop();
//					table.setId(tableId);
//					commande.setTableRooftop(table);
//				}
//				commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);
//	
//				// 8️⃣ Selon action
//				if ("update".equalsIgnoreCase(action)) {
//					// Vérifier commandeId
//					if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
//						sendError(response, out, "Paramètre commandeId manquant pour update", 400);
//						return;
//					}
//					int commandeId = jsonBody.get("commandeId").getAsInt();
//					commande.setId(commandeId);
//	
//					boolean success = commandeDAO.modifierCommandePOS(commande, details);
//					if (!success) {
//						sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
//						return;
//					}
//	
//					JsonObject data = new JsonObject();
//					data.addProperty("commandeId", commandeId);
//					data.addProperty("numeroCommande", numeroCommande);
//					data.addProperty("message", "Commande mise à jour avec succès");
//					jsonResponse("ok", data, out);
//	
//	
//				} else {
//					// Cas par défaut → Ajout
//					boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//					System.out.println("Valeur de credit: " + isCredit);
//	
//					if (isCredit) {
//						// 🔹 Vérifier le plafond AVANT insertion
//						Utilisateur client = utilisateurDAO.findById(clientId);
//						if (client == null) {
//							sendError(response, out, "Client introuvable", 404);
//							return;
//						}
//	
//						int plafond = client.getPlafond();
//	
//						// 🔹 Total crédits en cours
//						List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
//						int totalCredits = creditsEnCours.stream()
//								.mapToInt(c -> c.getCredit() != null
//										? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
//										: 0)
//								.sum();
//	
//						int montantCommande = commande.getMontantTotal().intValue();
//	
//						if ((totalCredits + montantCommande) > plafond) {
//							sendError(response, out, "Plafond atteint", 400);
//							return; // 🚨 On stoppe → aucune commande insérée
//						}
//	
//						// ✅ Plafond OK → insérer la commande
//						int newId = commandeDAO.ajouterCommandePOS(commande, details);
//						if (newId <= 0) {
//							sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//							return;
//						}
//						
//						
//	
//						// 🔹 Ajouter le crédit
//						Credit credit = new Credit();
//						credit.setUtilisateurId(clientId);
//						credit.setCommandeId(newId);
//						credit.setMontantTotal(montantCommande);
//						credit.setMontantPaye(0);
//						credit.setStatut("NON_PAYE");
//	
//						CreditDAO creditDAO = new CreditDAO();
//						int creditId = creditDAO.ajouterCredit(credit);
//						if (creditId <= 0) {
//							sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//							return;
//						}
//						
//						// 🔔 Notification pour commande crédit
//						Notification notif = new Notification();
//						notif.setGeneratedBy("SYSTEM");
//						notif.setToUser(clientId);
//						notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
//						                  " d’un montant de " + montantCommande + " HTG");
//						notif.setTypeNotif("CREDIT");
//						notif.setStatus("VISIBLE");
//	
//						NotificationDAO notifDAO = new NotificationDAO();
//						notifDAO.ajouterNotification(notif);
//	
//						System.out.println("Notification crédit envoyée au client #" + clientId);
//	
//						if (tableId != null) {
//							commandeDAO.reserverTable(tableId);
//						}
//	
//						// 🔟 Supprimer du panier
//						for (CommandeDetail detail : details) {
//							Integer panierId = detail.getPanierId();
//							if (panierId != null) {
//								String type = (detail.getPlatId() != null) ? "plat" : "produit";
//								PanierDAO pannierDAO = new PanierDAO();
//								pannierDAO.supprimerDuPanier(userId, panierId, type);
//							}
//						}
//	
//						JsonObject data = new JsonObject();
//						data.addProperty("commandeId", newId);
//						data.addProperty("numeroCommande", numeroCommande);
//						data.addProperty("message", "Commande crédit ajoutée avec succès");
//						jsonResponse("ok", data, out);
//	
//					} else {
//						// 🔹 Cas normal → on insère directement
//						int newId = commandeDAO.ajouterCommandePOS(commande, details);
//						if (newId <= 0) {
//							sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//							return;
//						}
//						
//						   commande.setId(newId);
//						   
//						// 🔹 Attribuer les points pour cette commande (SEULEMENT si ce n'est pas un crédit)
//						   PointManagerDAO pointManagerDAO = new PointManagerDAO();
//						   Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//						   
//					        if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//					            // Log l'erreur mais ne pas bloquer la commande
//					            System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//					        } else {
//					            // Points attribués avec succès
//					            int totalPoints = (Integer) resultPoints.get("totalPoints");
//					            System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//					            Notification notif = new Notification();
//							    notif.setGeneratedBy("SYSTEM");
//							    notif.setToUser(clientId);
//							    notif.setMessages("Vous venez de gagner : "   +  totalPoints + " pour cette commande.");
//							    notif.setTypeNotif("POINTS");
//							    notif.setStatus("VISIBLE");
//	
//							    NotificationDAO notifDAO = new NotificationDAO();
//							    notifDAO.ajouterNotification(notif);
//					            
//					            // Optionnel : envoyer une notification au client
//					            if (clientId != null && totalPoints > 0) {
//					                System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//					            }
//					        }
//					        
//	
//						if (tableId != null) {
//							commandeDAO.reserverTable(tableId);
//						}
//	
//						for (CommandeDetail detail : details) {
//							Integer panierId = detail.getPanierId();
//							if (panierId != null) {
//								String type = (detail.getPlatId() != null) ? "plat" : "produit";
//								PanierDAO pannierDAO = new PanierDAO();
//								pannierDAO.supprimerDuPanier(userId, panierId, type);
//							}
//						}
//	
//						JsonObject data = new JsonObject();
//						data.addProperty("commandeId", newId);
//						// 🔔 Notification pour commande normale
//						if (clientId != null) {
//						    Notification notif = new Notification();
//						    notif.setGeneratedBy("SYSTEM");
//						    notif.setToUser(clientId);
//						    notif.setMessages("Nouvelle commande #" + numeroCommande +
//						                      " montant: " + commande.getMontantTotal() + " HTG");
//						    notif.setTypeNotif("COMMANDE");
//						    notif.setStatus("VISIBLE");
//	
//						    NotificationDAO notifDAO = new NotificationDAO();
//						    notifDAO.ajouterNotification(notif);
//	
//						    System.out.println("Notification commande envoyée au client #" + clientId);
//						}
//	
//						data.addProperty("numeroCommande", numeroCommande);
//						data.addProperty("message", "Commande ajoutée avec succès");
//						jsonResponse("ok", data, out);
//					}
//				}
//	
//			} catch (Exception e) {
//				sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//			}
//		}
//		FIN HANDLECOMMANDE 23-12-25 BON
		
		private void handleCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        System.out.println("=== DEBUT handleCommande ===");
		        
		        // 1️⃣ Lire le corps de la requête
		        System.out.println("Étape 1: Lecture du corps de la requête");
		        StringBuilder sb = new StringBuilder();
		        BufferedReader reader = request.getReader();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line);
		        }
		        String body = sb.toString();
		        System.out.println("Corps reçu: " + body);

		        if (body.trim().isEmpty()) {
		            System.out.println("ERREUR: Corps vide");
		            sendError(response, out, "Corps de requête vide", 400);
		            return;
		        }

		        // 2️⃣ Parser le JSON
		        System.out.println("Étape 2: Parsing JSON");
		        JsonObject jsonBody;
		        try {
		            jsonBody = gson.fromJson(body, JsonObject.class);
		            System.out.println("JSON parsé avec succès: " + jsonBody);
		        } catch (Exception e) {
		            System.out.println("ERREUR parsing JSON: " + e.getMessage());
		            e.printStackTrace();
		            sendError(response, out, "JSON invalide", 400);
		            return;
		        }

		        // 🔹 Récupérer action
		        String action = "";
		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
		            action = jsonBody.get("action").getAsString().trim();
		            System.out.println("Action détectée: " + action);
		        } else {
		            System.out.println("Aucune action spécifiée, mode par défaut (ajout)");
		        }

		        // 3️⃣ Récupérer userId
		        System.out.println("Étape 3: Vérification userId");
		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
		            System.out.println("ERREUR: userId manquant");
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        int userId = jsonBody.get("userId").getAsInt();
		        System.out.println("userId: " + userId);

		        // 4️⃣ Récupérer tableId / clientId / serveuseId
		        System.out.println("Étape 4: Récupération des IDs");
		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
		                ? jsonBody.get("tableId").getAsInt()
		                : null;
		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
		                ? jsonBody.get("clientId").getAsInt()
		                : null;
		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
		                ? jsonBody.get("serveuseId").getAsInt()
		                : null;
		        
		        System.out.println("tableId: " + tableId + ", clientId: " + clientId + ", serveuseId: " + serveuseId);

		        // 5️⃣ Récupérer les détails
		        System.out.println("Étape 5: Récupération des détails");
		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
		            System.out.println("ERREUR: details manquant ou invalide");
		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
		            return;
		        }
		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
		        System.out.println("Détails JSON: " + detailsJson);
		        
		        List<CommandeDetail> details;
		        try {
		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
		            }.getType());
		            System.out.println("Détails parsés: " + details.size() + " éléments");
		        } catch (Exception e) {
		            System.out.println("ERREUR parsing détails: " + e.getMessage());
		            e.printStackTrace();
		            sendError(response, out, "Détails JSON invalides", 400);
		            return;
		        }

		        // 6️⃣ Validation des détails
		        System.out.println("Étape 6: Validation des détails");
		        for (int i = 0; i < details.size(); i++) {
		            CommandeDetail d = details.get(i);
		            System.out.println("Détail " + i + ": " + d);
		            
		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
		                System.out.println("ERREUR validation détail " + i + ": produitId/platId=" + 
		                    d.getProduitId() + "/" + d.getPlatId() + ", quantite=" + d.getQuantite());
		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
		                return;
		            }
		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
		                System.out.println("ERREUR prix unitaire détail " + i + ": " + d.getPrixUnitaire());
		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
		                return;
		            }
		        }

		        // 7️⃣ Construire l’objet commande
		        System.out.println("Étape 7: Construction de la commande");
		        Commande commande = new Commande();

		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
		            System.out.println("ERREUR: numeroCommande manquant");
		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
		            return;
		        }
		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
		        commande.setNumeroCommande(numeroCommande);
		        System.out.println("numeroCommande: " + numeroCommande);

		        commande.setDateCommande(new Timestamp(new Date().getTime()));
		        
		        if (jsonBody.has("statutCommande")) {
		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
		        }
		        if (jsonBody.has("statutPaiement")) {
		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
		        }
		        if (jsonBody.has("moyenPaiement")) {
		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
		        }
		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
		            BigDecimal montantTotal = jsonBody.get("montantTotal").getAsBigDecimal();
		            commande.setMontantTotal(montantTotal);
		            System.out.println("montantTotal: " + montantTotal);
		        } else {
		            System.out.println("ATTENTION: montantTotal non spécifié");
		        }

		        if (clientId != null && clientId > 0) {
		            commande.setClientId(clientId);
		        }
		        if (tableId != null && tableId > 0) {
		            TableRooftop table = new TableRooftop();
		            table.setId(tableId);
		            commande.setTableRooftop(table);
		        }
		        int utilisateurIdFinal = (serveuseId != null && serveuseId > 0) ? serveuseId : userId;
		        commande.setUtilisateurId(utilisateurIdFinal);
		        System.out.println("Utilisateur ID final: " + utilisateurIdFinal);

		        // 8️⃣ Selon action
		        System.out.println("Étape 8: Traitement action: " + action);
		        if ("update".equalsIgnoreCase(action)) {
		            System.out.println("Mode UPDATE");
		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
		                System.out.println("ERREUR: commandeId manquant pour update");
		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
		                return;
		            }
		            int commandeId = jsonBody.get("commandeId").getAsInt();
		            commande.setId(commandeId);
		            System.out.println("Mise à jour commande ID: " + commandeId);

		            System.out.println("Appel à commandeDAO.modifierCommandePOS...");
		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
		            System.out.println("Résultat modification: " + success);
		            
		            if (!success) {
		                System.out.println("ERREUR lors de la mise à jour");
		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
		                return;
		            }

		            JsonObject data = new JsonObject();
		            data.addProperty("commandeId", commandeId);
		            data.addProperty("numeroCommande", numeroCommande);
		            data.addProperty("message", "Commande mise à jour avec succès");
		            jsonResponse("ok", data, out);
		            System.out.println("UPDATE terminé avec succès");

		        } else {
		            System.out.println("Mode AJOUT (par défaut)");
		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
		            System.out.println("isCredit: " + isCredit);

		            if (isCredit) {
		                System.out.println("Mode CRÉDIT activé");
		                // 🔹 Vérifier le plafond AVANT insertion
		                System.out.println("Vérification plafond pour clientId: " + clientId);
		                Utilisateur client = utilisateurDAO.findById(clientId);
		                if (client == null) {
		                    System.out.println("ERREUR: Client introuvable ID=" + clientId);
		                    sendError(response, out, "Client introuvable", 404);
		                    return;
		                }
		                System.out.println("Client trouvé: " + client.getNom());

		                int plafond = client.getPlafond();
		                System.out.println("Plafond client: " + plafond);

		                // 🔹 Total crédits en cours
		                System.out.println("Récupération crédits en cours...");
		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
		                int totalCredits = creditsEnCours.stream()
		                        .mapToInt(c -> c.getCredit() != null
		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
		                                : 0)
		                        .sum();
		                System.out.println("Total crédits en cours: " + totalCredits);

		                int montantCommande = commande.getMontantTotal().intValue();
		                System.out.println("Montant commande: " + montantCommande);

		                if ((totalCredits + montantCommande) > plafond) {
		                    System.out.println("ERREUR plafond: " + (totalCredits + montantCommande) + " > " + plafond);
		                    sendError(response, out, "Plafond atteint", 400);
		                    return;
		                }
		                System.out.println("Plafond OK");

		                // ✅ Plafond OK → insérer la commande
		                System.out.println("Insertion commande POS...");
		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
		                System.out.println("Nouvelle commande ID: " + newId);
		                
		                if (newId <= 0) {
		                    System.out.println("ERREUR insertion commande");
		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
		                    return;
		                }
		                
		                // 🔹 Ajouter le crédit
		                System.out.println("Création objet crédit...");
		                Credit credit = new Credit();
		                credit.setUtilisateurId(clientId);
		                credit.setCommandeId(newId);
		                credit.setMontantTotal(montantCommande);
		                credit.setMontantPaye(0);
		                credit.setStatut("NON_PAYE");

		                CreditDAO creditDAO = new CreditDAO();
		                System.out.println("Insertion crédit...");
		                int creditId = creditDAO.ajouterCredit(credit);
		                System.out.println("Crédit ID: " + creditId);
		                
		                if (creditId <= 0) {
		                    System.out.println("ERREUR insertion crédit");
		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
		                    return;
		                }
		                
		                // 🔔 Notification pour commande crédit
		                System.out.println("Création notification crédit...");
		                Notification notif = new Notification();
		                notif.setGeneratedBy("SYSTEM");
		                notif.setToUser(clientId);
		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
		                                " d’un montant de " + montantCommande + " HTG");
		                notif.setTypeNotif("CREDIT");
		                notif.setStatus("VISIBLE");

		                NotificationDAO notifDAO = new NotificationDAO();
		                notifDAO.ajouterNotification(notif);

		                System.out.println("Notification crédit envoyée au client #" + clientId);

		                if (tableId != null) {
		                    System.out.println("Réservation table ID: " + tableId);
		                    commandeDAO.reserverTable(tableId);
		                }

		                // 🔟 Supprimer du panier
		                System.out.println("Nettoyage panier...");
		                for (CommandeDetail detail : details) {
		                    Integer panierId = detail.getPanierId();
		                    if (panierId != null) {
		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
		                        PanierDAO pannierDAO = new PanierDAO();
		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
		                        System.out.println("Élément " + panierId + " supprimé du panier");
		                    }
		                }

		                JsonObject data = new JsonObject();
		                data.addProperty("commandeId", newId);
		                data.addProperty("numeroCommande", numeroCommande);
		                data.addProperty("message", "Commande crédit ajoutée avec succès");
		                jsonResponse("ok", data, out);
		                System.out.println("Commande crédit terminée avec succès");

		            } else {
		                System.out.println("Mode NORMAL (non-crédit)");
		                // 🔹 Cas normal → on insère directement
		                System.out.println("Insertion commande POS...");
		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
		                System.out.println("Nouvelle commande ID: " + newId);
		                
		                if (newId <= 0) {
		                    System.out.println("ERREUR insertion commande");
		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
		                    return;
		                }
		                
		                commande.setId(newId);
		                
		                // 🔹 Attribuer les points pour cette commande
//		                System.out.println("Attribution des points...");
//		                PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//		                
//		                System.out.println("Résultat attribution points: " + resultPoints);
//		                
//		                if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                    System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                } else {
//		                    int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                    System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//		                    
//		                    Notification notif = new Notification();
//		                    notif.setGeneratedBy("SYSTEM");
//		                    notif.setToUser(clientId);
//		                    notif.setMessages("Vous venez de gagner : " + totalPoints + " points pour cette commande.");
//		                    notif.setTypeNotif("POINTS");
//		                    notif.setStatus("VISIBLE");
//
//		                    NotificationDAO notifDAO = new NotificationDAO();
//		                    notifDAO.ajouterNotification(notif);
//		                    
//		                    if (clientId != null && totalPoints > 0) {
//		                        System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//		                    }
//		                }

		                if (tableId != null) {
		                    System.out.println("Réservation table ID: " + tableId);
		                    commandeDAO.reserverTable(tableId);
		                }

		                System.out.println("Nettoyage panier...");
		                for (CommandeDetail detail : details) {
		                    Integer panierId = detail.getPanierId();
		                    if (panierId != null) {
		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
		                        PanierDAO pannierDAO = new PanierDAO();
		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
		                        System.out.println("Élément " + panierId + " supprimé du panier");
		                    }
		                }

		                // 🔔 Notification pour commande normale
		                if (clientId != null) {
		                    System.out.println("Création notification commande...");
		                    Notification notif = new Notification();
		                    notif.setGeneratedBy("SYSTEM");
		                    notif.setToUser(clientId);
		                    notif.setMessages("Nouvelle commande #" + numeroCommande +
		                                    " montant: " + commande.getMontantTotal() + " HTG");
		                    notif.setTypeNotif("COMMANDE");
		                    notif.setStatus("VISIBLE");

		                    NotificationDAO notifDAO = new NotificationDAO();
		                    notifDAO.ajouterNotification(notif);

		                    System.out.println("Notification commande envoyée au client #" + clientId);
		                }

		                JsonObject data = new JsonObject();
		                data.addProperty("commandeId", newId);
		                data.addProperty("numeroCommande", numeroCommande);
		                data.addProperty("message", "Commande ajoutée avec succès");
		                jsonResponse("ok", data, out);
		                System.out.println("Commande normale terminée avec succès");
		            }
		        }
		        
		        System.out.println("=== FIN handleCommande (succès) ===");

		    } catch (Exception e) {
		        System.err.println("=== ERREUR GLOBALE handleCommande ===");
		        System.err.println("Message: " + e.getMessage());
		        System.err.println("StackTrace:");
		        e.printStackTrace();
		        
		        // Ajoutez cette ligne pour voir quelle partie du code échoue
		        System.err.println("Erreur à: " + e.getStackTrace()[0]);
		        
		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
		    }
		}
		
//		private void handleCommande_v2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        // 1️⃣ Lire le corps de la requête
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//	
//		        if (body.trim().isEmpty()) {
//		            sendError(response, out, "Corps de requête vide", 400);
//		            return;
//		        }
//	
//		        // 2️⃣ Parser le JSON
//		        JsonObject jsonBody;
//		        try {
//		            jsonBody = gson.fromJson(body, JsonObject.class);
//		        } catch (Exception e) {
//		            sendError(response, out, "JSON invalide", 400);
//		            return;
//		        }
//	
//		        // 🔹 Récupérer action
//		        String action = "";
//		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//		            action = jsonBody.get("action").getAsString().trim();
//		        }
//	
//		        // 3️⃣ Récupérer userId
//		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//		            sendError(response, out, "Paramètre userId manquant", 400);
//		            return;
//		        }
//		        int userId = jsonBody.get("userId").getAsInt();
//	
//		        // 4️⃣ Récupérer tableId / clientId / serveuseId
//		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
//		                ? jsonBody.get("tableId").getAsInt()
//		                : null;
//		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
//		                ? jsonBody.get("clientId").getAsInt()
//		                : null;
//		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
//		                ? jsonBody.get("serveuseId").getAsInt()
//		                : null;
//	
//		        // 5️⃣ Récupérer les détails
//		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
//		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
//		            return;
//		        }
//		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
//		        List<CommandeDetail> details;
//		        try {
//		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
//		            }.getType());
//		        } catch (Exception e) {
//		            sendError(response, out, "Détails JSON invalides", 400);
//		            return;
//		        }
//	
//		        // 6️⃣ Validation des détails
//		        for (int i = 0; i < details.size(); i++) {
//		            CommandeDetail d = details.get(i);
//		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
//		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
//		                return;
//		            }
//		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
//		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
//		                return;
//		            }
//		        }
//	
//		        // 7️⃣ Construire l'objet commande
//		        Commande commande = new Commande();
//	
//		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
//		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
//		            return;
//		        }
//		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
//		        commande.setNumeroCommande(numeroCommande);
//	
//		        commande.setDateCommande(new Timestamp(new Date().getTime()));
//		        if (jsonBody.has("statutCommande")) {
//		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
//		        }
//		        if (jsonBody.has("statutPaiement")) {
//		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
//		        }
//		        if (jsonBody.has("moyenPaiement")) {
//		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
//		        }
//		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
//		            commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
//		        }
//	
//		        if (clientId != null && clientId > 0)
//		            commande.setClientId(clientId);
//		        if (tableId != null && tableId > 0) {
//		            TableRooftop table = new TableRooftop();
//		            table.setId(tableId);
//		            commande.setTableRooftop(table);
//		        }
//		        commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);
//	
//		        // 8️⃣ Récupérer le moyen de paiement
//		        String moyenPaiement = null;
//		        if (jsonBody.has("moyenPaiement") && !jsonBody.get("moyenPaiement").isJsonNull()) {
//		            moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
//		        }
//	
//		        // 9️⃣ Selon action
//		        if ("update".equalsIgnoreCase(action)) {
//		            // Vérifier commandeId
//		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
//		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
//		                return;
//		            }
//		            int commandeId = jsonBody.get("commandeId").getAsInt();
//		            commande.setId(commandeId);
//	
//		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
//		            if (!success) {
//		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
//		                return;
//		            }
//	
//		            JsonObject data = new JsonObject();
//		            data.addProperty("commandeId", commandeId);
//		            data.addProperty("numeroCommande", numeroCommande);
//		            data.addProperty("message", "Commande mise à jour avec succès");
//		            jsonResponse("ok", data, out);
//	
//		        } else {
//		            // Cas par défaut → Ajout
//		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//		            System.out.println("Valeur de credit: " + isCredit);
//	
//		            if (isCredit) {
//		                // 🔹 Vérifier le plafond AVANT insertion
//		                Utilisateur client = utilisateurDAO.findById(clientId);
//		                if (client == null) {
//		                    sendError(response, out, "Client introuvable", 404);
//		                    return;
//		                }
//	
//		                int plafond = client.getPlafond();
//	
//		                // 🔹 Total crédits en cours
//		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
//		                int totalCredits = creditsEnCours.stream()
//		                        .mapToInt(c -> c.getCredit() != null
//		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
//		                                : 0)
//		                        .sum();
//	
//		                int montantCommande = commande.getMontantTotal().intValue();
//	
//		                if ((totalCredits + montantCommande) > plafond) {
//		                    sendError(response, out, "Plafond atteint", 400);
//		                    return; // 🚨 On stoppe → aucune commande insérée
//		                }
//	
//		                // ✅ Plafond OK → insérer la commande
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                // 🔹 Ajouter le crédit
//		                Credit credit = new Credit();
//		                credit.setUtilisateurId(clientId);
//		                credit.setCommandeId(newId);
//		                credit.setMontantTotal(montantCommande);
//		                credit.setMontantPaye(0);
//		                credit.setStatut("NON_PAYE");
//	
//		                CreditDAO creditDAO = new CreditDAO();
//		                int creditId = creditDAO.ajouterCredit(credit);
//		                if (creditId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//		                    return;
//		                }
//		                
//		                // 🔔 Notification pour commande crédit
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(clientId);
//		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
//		                                " d'un montant de " + montantCommande + " HTG");
//		                notif.setTypeNotif("CREDIT");
//		                notif.setStatus("VISIBLE");
//	
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//	
//		                System.out.println("Notification crédit envoyée au client #" + clientId);
//	
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//	
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//	
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                data.addProperty("numeroCommande", numeroCommande);
//		                data.addProperty("message", "Commande crédit ajoutée avec succès");
//		                jsonResponse("ok", data, out);
//	
//		            } else {
//		                // 🔹 Cas normal → vérifier le moyen de paiement
//		                boolean paiementParSolde = "solde".equalsIgnoreCase(moyenPaiement);
//		                
//		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    // Récupérer le compte du client
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    
//		                    if (compte == null) {
//		                        sendError(response, out, "Compte client introuvable", 404);
//		                        return;
//		                    }
//		                    
//		                    BigDecimal soldeActuel = compte.getSolde();
//		                    BigDecimal montantCommande = commande.getMontantTotal();
//		                    
//		                    // Vérifier si le solde est suffisant
//		                    if (soldeActuel.compareTo(montantCommande) < 0) {
//		                        sendError(response, out, 
//		                                "Solde insuffisant. Solde actuel: " + 
//		                                String.format("%.2f HTG", soldeActuel) + 
//		                                ", Montant commande: " + 
//		                                String.format("%.2f HTG", montantCommande), 400);
//		                        return; // 🚨 On stoppe → aucune commande insérée
//		                    }
//		                }
//	
//		                // 🔹 Insérer la commande
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                commande.setId(newId);
//		                
//		                // ========== DÉBIT DU COMPTE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    try {
//		                        CompteClientDAO compteDAO = new CompteClientDAO();
//		                        CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                        
//		                        if (compte != null) {
//		                            // Calculer le nouveau solde
//		                            BigDecimal soldeActuel = compte.getSolde();
//		                            BigDecimal montantCommande = commande.getMontantTotal();
//		                            BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
//		                            
//		                            // Mettre à jour le solde dans la base
//		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
//		                            
//		                            if (updateReussi) {
//		                                System.out.println("Solde mis à jour avec succès pour la commande #" + newId);
//		                                
//		                                // 🔹 Créer une transaction pour le débit
//		                                // Note: Vous devez avoir une classe TransactionCompteDAO et TransactionCompte
//		                                try {
//		                                    // Enregistrer la transaction (débit)
//		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
//		                                    TransactionCompte transaction = new TransactionCompte();
//		                                    transaction.setCompteClientId(compte.getId());
//		                                    transaction.setMontant(montantCommande.negate()); // Montant négatif pour débit
//		                                    transaction.setTypeTransaction(new TypeTransaction(2, "RETRAIT", "Débit du compte"));
//		                                    transaction.setSoldeAvant(soldeActuel);
//		                                    transaction.setSoldeApres(nouveauSolde);
//		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
//		                                    transaction.setNotes("Débit pour commande #" + numeroCommande);
//	//	                                    transaction.setUtilisateurId(userId);
//		                                       
//		                                    int transactionId = transactionDAO.creerTransaction(transaction);
//		                                    
//		                                    if (transactionId > 0) {
//		                                        System.out.println("Transaction enregistrée #" + transactionId);
//		                                    } else {
//		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur création transaction: " + e.getMessage());
//		                                }
//		                                
//		                                // ========== ATTRIBUTION DES POINTS ==========
//		                                // On donne des points seulement si paiement par solde
//		                                PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                                Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//		                                
//		                                if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                                    // Log l'erreur mais ne pas bloquer la commande
//		                                    System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                                } else {
//		                                    // Points attribués avec succès
//		                                    int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                                    System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//		                                    
//		                                    // Notification pour les points
//		                                    Notification pointsNotif = new Notification();
//		                                    pointsNotif.setGeneratedBy("SYSTEM");
//		                                    pointsNotif.setToUser(clientId);
//		                                    pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre commande #" + numeroCommande);
//		                                    pointsNotif.setTypeNotif("POINTS");
//		                                    pointsNotif.setStatus("VISIBLE");
//	
//		                                    NotificationDAO notifDAO = new NotificationDAO();
//		                                    notifDAO.ajouterNotification(pointsNotif);
//		                                    
//		                                    System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//		                                }
//		                                
//		                            } else {
//		                                System.err.println("Erreur lors de la mise à jour du solde pour la commande #" + newId);
//		                            }
//		                        }
//		                    } catch (Exception e) {
//		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
//		                        // Ne pas bloquer la commande si problème avec le compte
//		                    }
//		                } else {
//		                    // Si pas de paiement par solde, pas d'attribution de points
//		                    System.out.println("Paiement par " + moyenPaiement + " - pas d'attribution de points");
//		                }
//	
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//	
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//	
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                
//		                // 🔔 Notification pour commande normale
//		                if (clientId != null) {
//		                    String messageNotif = "Nouvelle commande #" + numeroCommande +
//		                                        " montant: " + commande.getMontantTotal() + " HTG";
//		                    
//		                    if (paiementParSolde) {
//		                        messageNotif += " - Débité de votre compte";
//		                    } else {
//		                        messageNotif += " - Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen");
//		                    }
//		                    
//		                    Notification notif = new Notification();
//		                    notif.setGeneratedBy("SYSTEM");
//		                    notif.setToUser(clientId);
//		                    notif.setMessages(messageNotif);
//		                    notif.setTypeNotif("COMMANDE");
//		                    notif.setStatus("VISIBLE");
//	
//		                    NotificationDAO notifDAO = new NotificationDAO();
//		                    notifDAO.ajouterNotification(notif);
//	
//		                    System.out.println("Notification commande envoyée au client #" + clientId);
//		                }
//	
//		                data.addProperty("numeroCommande", numeroCommande);
//		                data.addProperty("message", "Commande ajoutée avec succès" + 
//		                    (paiementParSolde ? " et débitée du compte" : ""));
//		                jsonResponse("ok", data, out);
//		            }
//		        }
//	
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//		    }
//		}
		
		
//		private void handleCommande_v2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        // 1️⃣ Lire le corps de la requête
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//
//		        if (body.trim().isEmpty()) {
//		            sendError(response, out, "Corps de requête vide", 400);
//		            return;
//		        }
//
//		        // 2️⃣ Parser le JSON
//		        JsonObject jsonBody;
//		        try {
//		            jsonBody = gson.fromJson(body, JsonObject.class);
//		        } catch (Exception e) {
//		            sendError(response, out, "JSON invalide", 400);
//		            return;
//		        }
//
//		        // 🔹 Récupérer action
//		        String action = "";
//		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//		            action = jsonBody.get("action").getAsString().trim();
//		        }
//
//		        // 3️⃣ Récupérer userId
//		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//		            sendError(response, out, "Paramètre userId manquant", 400);
//		            return;
//		        }
//		        int userId = jsonBody.get("userId").getAsInt();
//
//		        // 4️⃣ Récupérer tableId / clientId / serveuseId
//		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
//		                ? jsonBody.get("tableId").getAsInt()
//		                : null;
//		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
//		                ? jsonBody.get("clientId").getAsInt()
//		                : null;
//		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
//		                ? jsonBody.get("serveuseId").getAsInt()
//		                : null;
//
//		        // 5️⃣ Récupérer les détails
//		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
//		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
//		            return;
//		        }
//		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
//		        List<CommandeDetail> details;
//		        try {
//		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
//		            }.getType());
//		        } catch (Exception e) {
//		            sendError(response, out, "Détails JSON invalides", 400);
//		            return;
//		        }
//
//		        // 6️⃣ Validation des détails
//		        for (int i = 0; i < details.size(); i++) {
//		            CommandeDetail d = details.get(i);
//		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
//		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
//		                return;
//		            }
//		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
//		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
//		                return;
//		            }
//		        }
//
//		        // 7️⃣ Construire l'objet commande
//		        Commande commande = new Commande();
//
//		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
//		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
//		            return;
//		        }
//		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
//		        commande.setNumeroCommande(numeroCommande);
//
//		        commande.setDateCommande(new Timestamp(new Date().getTime()));
//		        if (jsonBody.has("statutCommande")) {
//		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
//		        }
//		        if (jsonBody.has("statutPaiement")) {
//		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
//		        }
//		        if (jsonBody.has("moyenPaiement")) {
//		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
//		        }
//		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
//		            commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
//		        }
//
//		        if (clientId != null && clientId > 0)
//		            commande.setClientId(clientId);
//		        if (tableId != null && tableId > 0) {
//		            TableRooftop table = new TableRooftop();
//		            table.setId(tableId);
//		            commande.setTableRooftop(table);
//		        }
//		        commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);
//
//		        // 8️⃣ Récupérer le moyen de paiement
//		        String moyenPaiement = null;
//		        if (jsonBody.has("moyenPaiement") && !jsonBody.get("moyenPaiement").isJsonNull()) {
//		            moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
//		        }
//
//		        // 9️⃣ Selon action
//		        if ("update".equalsIgnoreCase(action)) {
//		            // Vérifier commandeId
//		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
//		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
//		                return;
//		            }
//		            int commandeId = jsonBody.get("commandeId").getAsInt();
//		            commande.setId(commandeId);
//
//		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
//		            if (!success) {
//		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
//		                return;
//		            }
//
//		            JsonObject data = new JsonObject();
//		            data.addProperty("commandeId", commandeId);
//		            data.addProperty("numeroCommande", numeroCommande);
//		            data.addProperty("message", "Commande mise à jour avec succès");
//		            jsonResponse("ok", data, out);
//
//		        } else {
//		            // Cas par défaut → Ajout
//		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//		            System.out.println("Valeur de credit: " + isCredit);
//
//		            if (isCredit) {
//		                // 🔹 Vérifier le plafond AVANT insertion
//		                Utilisateur client = utilisateurDAO.findById(clientId);
//		                if (client == null) {
//		                    sendError(response, out, "Client introuvable", 404);
//		                    return;
//		                }
//
//		                int plafond = client.getPlafond();
//
//		                // 🔹 Total crédits en cours
//		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
//		                int totalCredits = creditsEnCours.stream()
//		                        .mapToInt(c -> c.getCredit() != null
//		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
//		                                : 0)
//		                        .sum();
//
//		                int montantCommande = commande.getMontantTotal().intValue();
//
//		                if ((totalCredits + montantCommande) > plafond) {
//		                    sendError(response, out, "Plafond atteint", 400);
//		                    return; // 🚨 On stoppe → aucune commande insérée
//		                }
//
//		                // ✅ Plafond OK → insérer la commande
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                // 🔹 Ajouter le crédit
//		                Credit credit = new Credit();
//		                credit.setUtilisateurId(clientId);
//		                credit.setCommandeId(newId);
//		                credit.setMontantTotal(montantCommande);
//		                credit.setMontantPaye(0);
//		                credit.setStatut("NON_PAYE");
//
//		                CreditDAO creditDAO = new CreditDAO();
//		                int creditId = creditDAO.ajouterCredit(credit);
//		                if (creditId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//		                    return;
//		                }
//		                
//		                // 🔔 Notification pour commande crédit
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(clientId);
//		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
//		                                " d'un montant de " + montantCommande + " HTG");
//		                notif.setTypeNotif("CREDIT");
//		                notif.setStatus("VISIBLE");
//
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//
//		                System.out.println("Notification crédit envoyée au client #" + clientId);
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                data.addProperty("numeroCommande", numeroCommande);
//		                data.addProperty("message", "Commande crédit ajoutée avec succès");
//		                jsonResponse("ok", data, out);
//
//		            } else {
//		                // 🔹 Cas normal → vérifier le moyen de paiement
//		                boolean paiementParSolde = "solde".equalsIgnoreCase(moyenPaiement);
//		                
//		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		             // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    // Récupérer le compte du client avec limite de crédit
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    
//		                    if (compte == null) {
//		                        // Créer le compte s'il n'existe pas
//		                        compte = new CompteClient();
//		                        compte.setClientId(clientId);
//		                        compte.setSolde(BigDecimal.ZERO);
//		                        compte.setLimiteCredit(BigDecimal.ZERO);
//		                        int compteId = compteDAO.creerCompteClient(compte);
//		                        if (compteId <= 0) {
//		                            sendError(response, out, "Erreur lors de la création du compte", 500);
//		                            return;
//		                        }
//		                        compte.setId(compteId);
//		                    }
//		                    
//		                    BigDecimal soldeActuel = compte.getSolde();
//		                    BigDecimal limiteCredit = compte.getLimiteCredit();
//		                    BigDecimal montantCommande = commande.getMontantTotal();
//		                    
//		                    // 🔹 CORRECTION IMPORTANTE : Calculer le crédit déjà utilisé et le crédit restant
//		                    BigDecimal creditDejaUtilise = BigDecimal.ZERO;
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
//		                        creditDejaUtilise = soldeActuel.abs(); // La partie négative = crédit déjà utilisé
//		                    }
//		                    
//		                    BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
//		                    BigDecimal soldeDisponible = soldeActuel.add(creditRestantDisponible);
//		                    
//		                    System.out.println("=== VÉRIFICATION DE CRÉDIT ===");
//		                    System.out.println("Solde actuel: " + String.format("%.2f HTG", soldeActuel));
//		                    System.out.println("Limite crédit totale: " + String.format("%.2f HTG", limiteCredit));
//		                    System.out.println("Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise));
//		                    System.out.println("Crédit restant disponible: " + String.format("%.2f HTG", creditRestantDisponible));
//		                    System.out.println("Solde disponible (solde + crédit restant): " + String.format("%.2f HTG", soldeDisponible));
//		                    System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
//		                    
//		                    // 🔹 Vérifier si le solde disponible est suffisant
//		                    if (soldeDisponible.compareTo(montantCommande) < 0) {
//		                        BigDecimal manquant = montantCommande.subtract(soldeDisponible);
//		                        sendError(response, out, 
//		                                "Solde disponible insuffisant.\n" +
//		                                "Solde actuel: " + String.format("%.2f HTG", soldeActuel) + "\n" +
//		                                "Limite de crédit totale: " + String.format("%.2f HTG", limiteCredit) + "\n" +
//		                                "Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise) + "\n" +
//		                                "Crédit disponible restant: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
//		                                "Solde disponible: " + String.format("%.2f HTG", soldeDisponible) + "\n" +
//		                                "Montant commande: " + String.format("%.2f HTG", montantCommande) + "\n" +
//		                                "Il manque: " + String.format("%.2f HTG", manquant), 400);
//		                        return; // 🚨 On stoppe → aucune commande insérée
//		                    }
//		                    
//		                    // 🔹 Vérifier que le nouveau solde ne dépasse pas la limite négative
//		                    BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
//		                    BigDecimal limiteNegative = limiteCredit.negate();
//		                    
//		                    if (nouveauSolde.compareTo(limiteNegative) < 0) {
//		                        BigDecimal depassement = limiteNegative.subtract(nouveauSolde);
//		                        sendError(response, out, 
//		                                "Limite de crédit dépassée.\n" +
//		                                "Nouveau solde: " + String.format("%.2f HTG", nouveauSolde) + "\n" +
//		                                "Limite négative autorisée: " + String.format("%.2f HTG", limiteNegative) + "\n" +
//		                                "Dépassement: " + String.format("%.2f HTG", depassement), 400);
//		                        return; // 🚨 On stoppe → aucune commande insérée
//		                    }
//		                }
//
//		                // 🔹 Insérer la commande
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                commande.setId(newId);
//		                
//		                // ========== DÉBIT DU COMPTE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    try {
//		                        CompteClientDAO compteDAO = new CompteClientDAO();
//		                        CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                        
//		                        if (compte != null) {
//		                            // Calculer le nouveau solde
//		                            BigDecimal soldeActuel = compte.getSolde();
//		                            BigDecimal montantCommande = commande.getMontantTotal();
//		                            BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
//		                            
//		                            // Mettre à jour le solde dans la base
//		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
//		                            
//		                            if (updateReussi) {
//		                                System.out.println("Solde mis à jour avec succès pour la commande #" + newId);
//		                                System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuel));
//		                                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSolde));
//		                                System.out.println("Utilisation crédit: " + 
//		                                    (nouveauSolde.compareTo(BigDecimal.ZERO) < 0 ? 
//		                                     "OUI (" + nouveauSolde.abs() + " HTG)" : "NON"));
//		                                
//		                                // 🔹 Créer une transaction pour le débit
//		                                try {
//		                                    // Récupérer le type de transaction "DEPENSE" (ID 2)
//		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
//		                                    TransactionCompte transaction = new TransactionCompte();
//		                                    transaction.setCompteClientId(compte.getId());
//		                                    transaction.setMontant(montantCommande);
//		                                    transaction.setSoldeAvant(soldeActuel);
//		                                    transaction.setSoldeApres(nouveauSolde);
//		                                    
//		                                    // Si le solde devient négatif, noter l'utilisation du crédit
//		                                    String notes = "Paiement commande #" + numeroCommande;
//		                                    if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
//		                                        notes += " - Utilisation crédit: " + 
//		                                                String.format("%.2f HTG", nouveauSolde.abs());
//		                                    }
//		                                    transaction.setNotes(notes);
//		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
//		                                    
//		                                    // Récupérer l'ID du caissier/utilisateur actuel
//		                                    // (Assurez-vous que votre modèle TransactionCompte a un setter pour caissiereId)
//		                                    transaction.setCaissiereId(userId);
//		                                    
//		                                    // Définir le type de transaction (DEPENSE = ID 2)
//		                                    transaction.setTypeTransactionId(2); // ID pour "DEPENSE"
//		                                    
//		                                    // Lier à la commande
//		                                    transaction.setCommandeId(newId);
//		                                    
//		                                    // Créer la transaction
//		                                    int transactionId = transactionDAO.creerTransaction(transaction);
//		                                    
//		                                    if (transactionId > 0) {
//		                                        System.out.println("Transaction enregistrée #" + transactionId);
//		                                        
//		                                        // Mettre à jour la commande avec l'ID de transaction
//		                                        commandeDAO.updateTransactionId(newId, transactionId);
//		                                    } else {
//		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur création transaction: " + e.getMessage());
//		                                    e.printStackTrace();
//		                                }
//		                                
//		                                // ========== ATTRIBUTION DES POINTS ==========
//		                                // On donne des points seulement si paiement par solde
//		                                // (même si le solde est négatif = crédit utilisé)
//		                                try {
//		                                    PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                                    Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//		                                    
//		                                    if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                                        // Log l'erreur mais ne pas bloquer la commande
//		                                        System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                                    } else {
//		                                        // Points attribués avec succès
//		                                        int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                                        System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//		                                        
//		                                        // Notification pour les points
//		                                        Notification pointsNotif = new Notification();
//		                                        pointsNotif.setGeneratedBy("SYSTEM");
//		                                        pointsNotif.setToUser(clientId);
//		                                        pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre commande #" + numeroCommande);
//		                                        pointsNotif.setTypeNotif("POINTS");
//		                                        pointsNotif.setStatus("VISIBLE");
//
//		                                        NotificationDAO notifDAO = new NotificationDAO();
//		                                        notifDAO.ajouterNotification(pointsNotif);
//		                                        
//		                                        System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
//		                                }
//		                                
//		                            } else {
//		                                System.err.println("Erreur lors de la mise à jour du solde pour la commande #" + newId);
//		                            }
//		                        }
//		                    } catch (Exception e) {
//		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
//		                        e.printStackTrace();
//		                        // Ne pas bloquer la commande si problème avec le compte
//		                    }
//		                } else {
//		                    // Si pas de paiement par solde, pas d'attribution de points
//		                    System.out.println("Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen") + " - pas d'attribution de points");
//		                }
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                
//		                // 🔔 Notification pour commande normale
//		                if (clientId != null) {
//		                    // Récupérer le compte pour savoir si crédit utilisé
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    String messageNotif = "Nouvelle commande #" + numeroCommande +
//		                                        " montant: " + String.format("%.2f HTG", commande.getMontantTotal());
//		                    
//		                    if (paiementParSolde && compte != null) {
//		                        BigDecimal nouveauSolde = compte.getSolde().subtract(commande.getMontantTotal());
//		                        if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
//		                            messageNotif += " - Débité de votre compte (Crédit utilisé: " + 
//		                                          String.format("%.2f HTG", nouveauSolde.abs()) + ")";
//		                        } else {
//		                            messageNotif += " - Débité de votre compte";
//		                        }
//		                    } else {
//		                        messageNotif += " - Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen");
//		                    }
//		                    
//		                    Notification notif = new Notification();
//		                    notif.setGeneratedBy("SYSTEM");
//		                    notif.setToUser(clientId);
//		                    notif.setMessages(messageNotif);
//		                    notif.setTypeNotif("COMMANDE");
//		                    notif.setStatus("VISIBLE");
//
//		                    NotificationDAO notifDAO = new NotificationDAO();
//		                    notifDAO.ajouterNotification(notif);
//
//		                    System.out.println("Notification commande envoyée au client #" + clientId);
//		                }
//
//		                data.addProperty("numeroCommande", numeroCommande);
//		                
//		                // Message de confirmation avec info crédit si utilisé
//		                String confirmationMessage = "Commande ajoutée avec succès";
//		                if (paiementParSolde && clientId != null) {
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    if (compte != null) {
//		                        BigDecimal ancienSolde = compte.getSolde().add(commande.getMontantTotal());
//		                        BigDecimal nouveauSolde = compte.getSolde();
//		                        
//		                        if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
//		                            confirmationMessage += " - Débité du compte (Crédit utilisé: " + 
//		                                                String.format("%.2f HTG", nouveauSolde.abs()) + ")";
//		                        } else {
//		                            confirmationMessage += " - Débité du compte";
//		                        }
//		                        
//		                        confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
//		                        confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
//		                        confirmationMessage += "\nSolde disponible: " + 
//		                                            String.format("%.2f HTG", nouveauSolde.add(compte.getLimiteCredit()));
//		                    }
//		                }
//		                
//		                data.addProperty("message", confirmationMessage);
//		                jsonResponse("ok", data, out);
//		            }
//		        }
//
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//		        e.printStackTrace();
//		    }
//		}
		
//		private void handleCommande_v2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        // 1️⃣ Lire le corps de la requête
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//
//		        if (body.trim().isEmpty()) {
//		            sendError(response, out, "Corps de requête vide", 400);
//		            return;
//		        }
//
//		        // 2️⃣ Parser le JSON
//		        JsonObject jsonBody;
//		        try {
//		            jsonBody = gson.fromJson(body, JsonObject.class);
//		        } catch (Exception e) {
//		            sendError(response, out, "JSON invalide", 400);
//		            return;
//		        }
//
//		        // 🔹 Récupérer action
//		        String action = "";
//		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//		            action = jsonBody.get("action").getAsString().trim();
//		        }
//
//		        // 3️⃣ Récupérer userId
//		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//		            sendError(response, out, "Paramètre userId manquant", 400);
//		            return;
//		        }
//		        int userId = jsonBody.get("userId").getAsInt();
//
//		        // 4️⃣ Récupérer tableId / clientId / serveuseId
//		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
//		                ? jsonBody.get("tableId").getAsInt()
//		                : null;
//		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
//		                ? jsonBody.get("clientId").getAsInt()
//		                : null;
//		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
//		                ? jsonBody.get("serveuseId").getAsInt()
//		                : null;
//
//		        // 5️⃣ Récupérer les détails
//		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
//		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
//		            return;
//		        }
//		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
//		        List<CommandeDetail> details;
//		        try {
//		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
//		            }.getType());
//		        } catch (Exception e) {
//		            sendError(response, out, "Détails JSON invalides", 400);
//		            return;
//		        }
//
//		        // 6️⃣ Validation des détails
//		        for (int i = 0; i < details.size(); i++) {
//		            CommandeDetail d = details.get(i);
//		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
//		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
//		                return;
//		            }
//		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
//		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
//		                return;
//		            }
//		        }
//
//		        // 7️⃣ Construire l'objet commande
//		        Commande commande = new Commande();
//
//		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
//		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
//		            return;
//		        }
//		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
//		        commande.setNumeroCommande(numeroCommande);
//
//		        commande.setDateCommande(new Timestamp(new Date().getTime()));
//		        if (jsonBody.has("statutCommande")) {
//		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
//		        }
//		        
//		        // Par défaut, statut paiement = PAYER (sera modifié si crédit utilisé)
//		        commande.setStatutPaiement("PAYER");
//		        
//		        if (jsonBody.has("statutPaiement")) {
//		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
//		        }
//		        if (jsonBody.has("moyenPaiement")) {
//		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
//		        }
//		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
//		            commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
//		        }
//
//		        if (clientId != null && clientId > 0)
//		            commande.setClientId(clientId);
//		        if (tableId != null && tableId > 0) {
//		            TableRooftop table = new TableRooftop();
//		            table.setId(tableId);
//		            commande.setTableRooftop(table);
//		        }
//		        commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);
//
//		        // 8️⃣ Récupérer le moyen de paiement
//		        String moyenPaiement = null;
//		        if (jsonBody.has("moyenPaiement") && !jsonBody.get("moyenPaiement").isJsonNull()) {
//		            moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
//		        }
//
//		        // 9️⃣ Selon action
//		        if ("update".equalsIgnoreCase(action)) {
//		            // Vérifier commandeId
//		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
//		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
//		                return;
//		            }
//		            int commandeId = jsonBody.get("commandeId").getAsInt();
//		            commande.setId(commandeId);
//
//		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
//		            if (!success) {
//		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
//		                return;
//		            }
//
//		            JsonObject data = new JsonObject();
//		            data.addProperty("commandeId", commandeId);
//		            data.addProperty("numeroCommande", numeroCommande);
//		            data.addProperty("message", "Commande mise à jour avec succès");
//		            jsonResponse("ok", data, out);
//
//		        } else {
//		            // Cas par défaut → Ajout
//		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//		            System.out.println("Valeur de isCredit: " + isCredit);
//
//		            if (isCredit) {
//		                // 🔹 Vérifier le plafond AVANT insertion
//		                Utilisateur client = utilisateurDAO.findById(clientId);
//		                if (client == null) {
//		                    sendError(response, out, "Client introuvable", 404);
//		                    return;
//		                }
//
//		                int plafond = client.getPlafond();
//
//		                // 🔹 Total crédits en cours
//		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
//		                int totalCredits = creditsEnCours.stream()
//		                        .mapToInt(c -> c.getCredit() != null
//		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
//		                                : 0)
//		                        .sum();
//
//		                int montantCommande = commande.getMontantTotal().intValue();
//
//		                if ((totalCredits + montantCommande) > plafond) {
//		                    sendError(response, out, "Plafond atteint", 400);
//		                    return; // 🚨 On stoppe → aucune commande insérée
//		                }
//
//		                // ✅ Plafond OK → insérer la commande avec statut PARTIEL
//		                commande.setStatutPaiement("PARTIEL");
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                // 🔹 Ajouter le crédit
//		                Credit credit = new Credit();
//		                credit.setUtilisateurId(clientId);
//		                credit.setCommandeId(newId);
//		                credit.setMontantTotal(montantCommande);
//		                credit.setMontantPaye(0);
//		                credit.setStatut("NON_PAYE");
//
//		                CreditDAO creditDAO = new CreditDAO();
//		                int creditId = creditDAO.ajouterCredit(credit);
//		                if (creditId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//		                    return;
//		                }
//		                
//		                // 🔔 Notification pour commande crédit
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(clientId);
//		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
//		                                " d'un montant de " + montantCommande + " HTG");
//		                notif.setTypeNotif("CREDIT");
//		                notif.setStatus("VISIBLE");
//
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//
//		                System.out.println("Notification crédit envoyée au client #" + clientId);
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                data.addProperty("numeroCommande", numeroCommande);
//		                data.addProperty("message", "Commande crédit ajoutée avec succès");
//		                jsonResponse("ok", data, out);
//
//		            } else {
//		                // 🔹 Cas normal → vérifier le moyen de paiement
//		                boolean paiementParSolde = "solde".equalsIgnoreCase(moyenPaiement);
//		                
//		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		             // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    // Récupérer le compte du client avec limite de crédit
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    
//		                    if (compte == null) {
//		                        // Créer le compte s'il n'existe pas
//		                        compte = new CompteClient();
//		                        compte.setClientId(clientId);
//		                        compte.setSolde(BigDecimal.ZERO);
//		                        compte.setLimiteCredit(BigDecimal.ZERO);
//		                        int compteId = compteDAO.creerCompteClient(compte);
//		                        if (compteId <= 0) {
//		                            sendError(response, out, "Erreur lors de la création du compte", 500);
//		                            return;
//		                        }
//		                        compte.setId(compteId);
//		                    }
//		                    
//		                    BigDecimal soldeActuel = compte.getSolde();
//		                    BigDecimal limiteCredit = compte.getLimiteCredit();
//		                    BigDecimal montantCommande = commande.getMontantTotal();
//		                    
//		                    // 🔹 CORRECTION : Calculer le crédit déjà utilisé et le crédit restant
//		                    BigDecimal creditDejaUtilise = BigDecimal.ZERO;
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
//		                        creditDejaUtilise = soldeActuel.abs(); // La partie négative = crédit déjà utilisé
//		                    }
//		                    
//		                    BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
//		                    
//		                    // 🔹 CORRECTION IMPORTANTE : Le solde disponible pour payer est juste le crédit restant
//		                    // car le solde actuel est déjà négatif (crédit déjà utilisé)
//		                    BigDecimal soldeDisponiblePourPayer = creditRestantDisponible;
//		                    
//		                    System.out.println("=== VÉRIFICATION DE CRÉDIT ===");
//		                    System.out.println("Solde actuel: " + String.format("%.2f HTG", soldeActuel));
//		                    System.out.println("Limite crédit totale: " + String.format("%.2f HTG", limiteCredit));
//		                    System.out.println("Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise));
//		                    System.out.println("Crédit restant disponible: " + String.format("%.2f HTG", creditRestantDisponible));
//		                    System.out.println("Solde disponible pour payer: " + String.format("%.2f HTG", soldeDisponiblePourPayer));
//		                    System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
//		                    
//		                    // DÉTERMINER LE TYPE DE PAIEMENT
//		                    boolean utiliserCredit = false;
//		                    
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) >= 0) {
//		                        // Cas 1: Solde positif ou nul
//		                        if (soldeActuel.compareTo(montantCommande) >= 0) {
//		                            // Solde suffisant sans utiliser de crédit
//		                            System.out.println("Paiement avec solde uniquement (pas de crédit)");
//		                            utiliserCredit = false;
//		                        } else {
//		                            // Solde positif mais insuffisant, utiliser crédit
//		                            System.out.println("Paiement partiel avec solde + crédit");
//		                            utiliserCredit = true;
//		                            commande.setStatutPaiement("PARTIEL");
//		                        }
//		                    } else {
//		                        // Cas 2: Solde déjà négatif (crédit déjà utilisé)
//		                        // On ne peut utiliser que le crédit restant disponible
//		                        System.out.println("Solde négatif - utilisation crédit restant");
//		                        utiliserCredit = true;
//		                        commande.setStatutPaiement("PARTIEL");
//		                    }
//		                    
//		                    // 🔹 Vérifier si le crédit restant est suffisant
//		                    if (utiliserCredit && soldeDisponiblePourPayer.compareTo(montantCommande) < 0) {
//		                        BigDecimal manquant = montantCommande.subtract(soldeDisponiblePourPayer);
//		                        sendError(response, out, 
//		                                "Crédit disponible insuffisant.\n" +
//		                                "Solde actuel: " + String.format("%.2f HTG", soldeActuel) + "\n" +
//		                                "Limite de crédit totale: " + String.format("%.2f HTG", limiteCredit) + "\n" +
//		                                "Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise) + "\n" +
//		                                "Crédit disponible restant: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
//		                                "Montant commande: " + String.format("%.2f HTG", montantCommande) + "\n" +
//		                                "Il manque: " + String.format("%.2f HTG", manquant), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Vérifier que le nouveau solde ne dépasse pas la limite négative
//		                    BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
//		                    BigDecimal limiteNegative = limiteCredit.negate();
//		                    
//		                    if (nouveauSolde.compareTo(limiteNegative) < 0) {
//		                        BigDecimal depassement = limiteNegative.subtract(nouveauSolde);
//		                        sendError(response, out, 
//		                                "Limite de crédit dépassée.\n" +
//		                                "Nouveau solde: " + String.format("%.2f HTG", nouveauSolde) + "\n" +
//		                                "Limite négative autorisée: " + String.format("%.2f HTG", limiteNegative) + "\n" +
//		                                "Dépassement: " + String.format("%.2f HTG", depassement), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Insérer la commande
//		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                    if (newId <= 0) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                        return;
//		                    }
//		                    
//		                    commande.setId(newId);
//		                    
//		                    // ========== DÉBIT DU COMPTE ==========
//		                    try {
//		                        // Re-récupérer le compte pour être sûr d'avoir les dernières données
//		                        compte = compteDAO.getCompteByClientId(clientId);
//		                        
//		                        if (compte != null) {
//		                            // Calculer le nouveau solde
//		                            BigDecimal soldeActuelCompte = compte.getSolde();
//		                            BigDecimal nouveauSoldeCompte = soldeActuelCompte.subtract(montantCommande);
//		                            
//		                            // Mettre à jour le solde dans la base
//		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSoldeCompte);
//		                            
//		                            if (updateReussi) {
//		                                System.out.println("=== MISE À JOUR SOLDE ===");
//		                                System.out.println("Solde mis à jour avec succès pour la commande #" + newId);
//		                                System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuelCompte));
//		                                System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
//		                                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSoldeCompte));
//		                                System.out.println("Utilisation crédit: " + 
//		                                    (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0 ? 
//		                                     "OUI (" + nouveauSoldeCompte.abs() + " HTG)" : "NON"));
//		                                
//		                                // ========== GESTION CRÉDIT ==========
//		                                if (utiliserCredit) {
//		                                    // Si utilisation de crédit, créer un enregistrement dans CREDIT
//		                                    Credit credit = new Credit();
//		                                    credit.setUtilisateurId(clientId);
//		                                    credit.setCommandeId(newId);
//		                                    credit.setMontantTotal(montantCommande.intValue());
//		                                    
//		                                    // Calculer combien a été payé avec le solde réel (s'il était positif)
//		                                    BigDecimal montantPayeAvecSolde = BigDecimal.ZERO;
//		                                    if (soldeActuelCompte.compareTo(BigDecimal.ZERO) > 0) {
//		                                        montantPayeAvecSolde = soldeActuelCompte.min(montantCommande);
//		                                    }
//		                                    credit.setMontantPaye(montantPayeAvecSolde.intValue());
//		                                    
//		                                    CreditDAO creditDAO = new CreditDAO();
//		                                    // toValidate = false car c'est un paiement par solde avec crédit utilisé
//		                                    int creditId = creditDAO.ajouterCredit_v2(credit, false);
//		                                    if (creditId <= 0) {
//		                                        System.err.println("Erreur lors de l'ajout du crédit pour commande mixte");
//		                                    } else {
//		                                        System.out.println("Crédit créé pour commande mixte #" + newId + " (ID crédit: " + creditId + ")");
//		                                    }
//
//		                                    NotificationDAO notifDAO = new NotificationDAO();
//		                                    // 🔔 Notification pour commande avec crédit
//		                                    Notification creditNotif = new Notification();
//		                                    creditNotif.setGeneratedBy("SYSTEM");
//		                                    creditNotif.setToUser(clientId);
//		                                    creditNotif.setMessages("Commande #" + numeroCommande + 
//		                                                          " payée avec votre crédit disponible");
//		                                    creditNotif.setTypeNotif("CREDIT");
//		                                    creditNotif.setStatus("VISIBLE");
//		                                    notifDAO.ajouterNotification(creditNotif);
//		                                    
//		                                } else {
//		                                    // ========== ATTRIBUTION DES POINTS ==========
//		                                    // On donne des points seulement si paiement par solde SANS utiliser de crédit
//		                                    // ET seulement si le solde reste positif après paiement
//		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) >= 0) {
//		                                        try {
//		                                            PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                                            Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//		                                            
//		                                            if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                                                // Log l'erreur mais ne pas bloquer la commande
//		                                                System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                                            } else {
//		                                                // Points attribués avec succès
//		                                                int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                                                System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//		                                                
//		                                                // Notification pour les points
//		                                                Notification pointsNotif = new Notification();
//		                                                pointsNotif.setGeneratedBy("SYSTEM");
//		                                                pointsNotif.setToUser(clientId);
//		                                                pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre commande #" + numeroCommande);
//		                                                pointsNotif.setTypeNotif("POINTS");
//		                                                pointsNotif.setStatus("VISIBLE");
//
//		                                                NotificationDAO notifDAO = new NotificationDAO();
//		                                                notifDAO.ajouterNotification(pointsNotif);
//		                                                
//		                                                System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//		                                            }
//		                                        } catch (Exception e) {
//		                                            System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
//		                                        }
//		                                    } else {
//		                                        System.out.println("Pas de points attribués - solde négatif après paiement");
//		                                    }
//		                                }
//		                                
//		                                // 🔹 Créer une transaction pour le débit
//		                                try {
//		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
//		                                    TransactionCompte transaction = new TransactionCompte();
//		                                    transaction.setCompteClientId(compte.getId());
//		                                    transaction.setMontant(montantCommande);
//		                                    transaction.setSoldeAvant(soldeActuelCompte);
//		                                    transaction.setSoldeApres(nouveauSoldeCompte);
//		                                    
//		                                    String notes = "Paiement commande #" + numeroCommande;
//		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0) {
//		                                        notes += " - Utilisation crédit: " + 
//		                                                String.format("%.2f HTG", nouveauSoldeCompte.abs());
//		                                    } else {
//		                                        notes += " - Paiement avec solde";
//		                                    }
//		                                    transaction.setNotes(notes);
//		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
//		                                    transaction.setCaissiereId(userId);
//		                                    transaction.setTypeTransactionId(2); // ID pour "DEPENSE"
//		                                    transaction.setCommandeId(newId);
//		                                    
//		                                    int transactionId = transactionDAO.creerTransaction(transaction);
//		                                    
//		                                    if (transactionId > 0) {
//		                                        System.out.println("Transaction enregistrée #" + transactionId);
//		                                        commandeDAO.updateTransactionId(newId, transactionId);
//		                                    } else {
//		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur création transaction: " + e.getMessage());
//		                                    e.printStackTrace();
//		                                }
//		                                
//		                            } else {
//		                                System.err.println("Erreur lors de la mise à jour du solde pour la commande #" + newId);
//		                            }
//		                        }
//		                    } catch (Exception e) {
//		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
//		                        e.printStackTrace();
//		                    }
//		                    
//		                } else {
//		                    // 🔹 Paiement autre que "solde" - insérer la commande normalement
//		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                    if (newId <= 0) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                        return;
//		                    }
//		                    commande.setId(newId);
//		                    
//		                    System.out.println("Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen") + " - pas d'attribution de points");
//		                }
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", commande.getId());
//		                
//		                // 🔔 Notification pour commande normale
//		                if (clientId != null) {
//		                    String messageNotif = "Nouvelle commande #" + numeroCommande +
//		                                        " montant: " + String.format("%.2f HTG", commande.getMontantTotal());
//		                    
//		                    if (paiementParSolde) {
//		                        messageNotif += " - Débité de votre compte";
//		                        if (commande.getStatutPaiement().equals("PARTIEL")) {
//		                            messageNotif += " (partiellement avec crédit)";
//		                        }
//		                    } else {
//		                        messageNotif += " - Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen");
//		                    }
//		                    
//		                    Notification notif = new Notification();
//		                    notif.setGeneratedBy("SYSTEM");
//		                    notif.setToUser(clientId);
//		                    notif.setMessages(messageNotif);
//		                    notif.setTypeNotif("COMMANDE");
//		                    notif.setStatus("VISIBLE");
//
//		                    NotificationDAO notifDAO = new NotificationDAO();
//		                    notifDAO.ajouterNotification(notif);
//
//		                    System.out.println("Notification commande envoyée au client #" + clientId);
//		                }
//
//		                data.addProperty("numeroCommande", numeroCommande);
//		                
//		                // Message de confirmation
//		                String confirmationMessage = "Commande ajoutée avec succès";
//		                if (paiementParSolde && clientId != null) {
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    if (compte != null) {
//		                        BigDecimal ancienSolde = compte.getSolde().add(commande.getMontantTotal());
//		                        BigDecimal nouveauSolde = compte.getSolde();
//		                        
//		                        confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
//		                        confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
//		                        
//		                        if (commande.getStatutPaiement().equals("PARTIEL")) {
//		                            confirmationMessage += "\n⚠️ Cette commande a utilisé votre limite de crédit";
//		                        }
//		                    }
//		                }
//		                
//		                data.addProperty("message", confirmationMessage);
//		                jsonResponse("ok", data, out);
//		            }
//		        }
//
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//		        e.printStackTrace();
//		    }
//		}
		
//		private void handleCommande_v2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        // 1️⃣ Lire le corps de la requête
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//
//		        if (body.trim().isEmpty()) {
//		            sendError(response, out, "Corps de requête vide", 400);
//		            return;
//		        }
//
//		        // 2️⃣ Parser le JSON
//		        JsonObject jsonBody;
//		        try {
//		            jsonBody = gson.fromJson(body, JsonObject.class);
//		        } catch (Exception e) {
//		            sendError(response, out, "JSON invalide", 400);
//		            return;
//		        }
//
//		        // 🔹 Récupérer action
//		        String action = "";
//		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//		            action = jsonBody.get("action").getAsString().trim();
//		        }
//
//		        // 3️⃣ Récupérer userId
//		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//		            sendError(response, out, "Paramètre userId manquant", 400);
//		            return;
//		        }
//		        int userId = jsonBody.get("userId").getAsInt();
//
//		        // 4️⃣ Récupérer tableId / clientId / serveuseId
//		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
//		                ? jsonBody.get("tableId").getAsInt()
//		                : null;
//		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
//		                ? jsonBody.get("clientId").getAsInt()
//		                : null;
//		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
//		                ? jsonBody.get("serveuseId").getAsInt()
//		                : null;
//
//		        // 5️⃣ Récupérer les détails
//		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
//		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
//		            return;
//		        }
//		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
//		        List<CommandeDetail> details;
//		        try {
//		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
//		            }.getType());
//		        } catch (Exception e) {
//		            sendError(response, out, "Détails JSON invalides", 400);
//		            return;
//		        }
//
//		        // 6️⃣ Validation des détails
//		        for (int i = 0; i < details.size(); i++) {
//		            CommandeDetail d = details.get(i);
//		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
//		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
//		                return;
//		            }
//		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
//		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
//		                return;
//		            }
//		        }
//
//		        // 7️⃣ Construire l'objet commande
//		        Commande commande = new Commande();
//
//		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
//		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
//		            return;
//		        }
//		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
//		        commande.setNumeroCommande(numeroCommande);
//
//		        commande.setDateCommande(new Timestamp(new Date().getTime()));
//		        if (jsonBody.has("statutCommande")) {
//		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
//		        }
//		        
//		        // Par défaut, statut paiement = PAYE (sera modifié si nécessaire)
//		        commande.setStatutPaiement("PAYE");
//		        
//		        if (jsonBody.has("statutPaiement")) {
//		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
//		        }
//		        if (jsonBody.has("moyenPaiement")) {
//		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
//		        }
//		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
//		            commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
//		        }
//
//		        if (clientId != null && clientId > 0)
//		            commande.setClientId(clientId);
//		        if (tableId != null && tableId > 0) {
//		            TableRooftop table = new TableRooftop();
//		            table.setId(tableId);
//		            commande.setTableRooftop(table);
//		        }
//		        commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);
//
//		        // 8️⃣ Récupérer le moyen de paiement
//		        String moyenPaiement = null;
//		        if (jsonBody.has("moyenPaiement") && !jsonBody.get("moyenPaiement").isJsonNull()) {
//		            moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
//		        }
//
//		        // 9️⃣ Selon action
//		        if ("update".equalsIgnoreCase(action)) {
//		            // Vérifier commandeId
//		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
//		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
//		                return;
//		            }
//		            int commandeId = jsonBody.get("commandeId").getAsInt();
//		            commande.setId(commandeId);
//
//		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
//		            if (!success) {
//		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
//		                return;
//		            }
//
//		            JsonObject data = new JsonObject();
//		            data.addProperty("commandeId", commandeId);
//		            data.addProperty("numeroCommande", numeroCommande);
//		            data.addProperty("message", "Commande mise à jour avec succès");
//		            jsonResponse("ok", data, out);
//
//		        } else {
//		            // Cas par défaut → Ajout
//		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//		            System.out.println("Valeur de isCredit: " + isCredit);
//
//		            if (isCredit) {
//		                // 🔹 Vérifier le plafond AVANT insertion
//		                Utilisateur client = utilisateurDAO.findById(clientId);
//		                if (client == null) {
//		                    sendError(response, out, "Client introuvable", 404);
//		                    return;
//		                }
//
//		                int plafond = client.getPlafond();
//
//		                // 🔹 Total crédits en cours
//		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
//		                int totalCredits = creditsEnCours.stream()
//		                        .mapToInt(c -> c.getCredit() != null
//		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
//		                                : 0)
//		                        .sum();
//
//		                int montantCommande = commande.getMontantTotal().intValue();
//
//		                if ((totalCredits + montantCommande) > plafond) {
//		                    sendError(response, out, "Plafond atteint", 400);
//		                    return; // 🚨 On stoppe → aucune commande insérée
//		                }
//
//		                // ✅ Plafond OK → insérer la commande avec statut NON_PAYE (selon la règle 3)
//		                commande.setStatutPaiement("NON_PAYE");
//		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                if (newId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                    return;
//		                }
//		                
//		                // 🔹 Ajouter le crédit
//		                Credit credit = new Credit();
//		                credit.setUtilisateurId(clientId);
//		                credit.setCommandeId(newId);
//		                credit.setMontantTotal(montantCommande);
//		                credit.setMontantPaye(0);
//		                credit.setStatut("NON_PAYE");
//
//		                CreditDAO creditDAO = new CreditDAO();
//		                int creditId = creditDAO.ajouterCredit(credit);
//		                if (creditId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//		                    return;
//		                }
//		                
//		                // 🔔 Notification pour commande crédit
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(clientId);
//		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
//		                                " d'un montant de " + montantCommande + " HTG");
//		                notif.setTypeNotif("CREDIT");
//		                notif.setStatus("VISIBLE");
//
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//
//		                System.out.println("Notification crédit envoyée au client #" + clientId);
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", newId);
//		                data.addProperty("numeroCommande", numeroCommande);
//		                data.addProperty("message", "Commande crédit ajoutée avec succès - Statut: NON_PAYE");
//		                jsonResponse("ok", data, out);
//
//		            } else {
//		                // 🔹 Cas normal → vérifier le moyen de paiement
//		                boolean paiementParSolde = "solde".equalsIgnoreCase(moyenPaiement);
//		                
//		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		                if (paiementParSolde && clientId != null) {
//		                    // Récupérer le compte du client avec limite de crédit
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    
//		                    if (compte == null) {
//		                        // Créer le compte s'il n'existe pas
//		                        compte = new CompteClient();
//		                        compte.setClientId(clientId);
//		                        compte.setSolde(BigDecimal.ZERO);
//		                        compte.setLimiteCredit(BigDecimal.ZERO);
//		                        int compteId = compteDAO.creerCompteClient(compte);
//		                        if (compteId <= 0) {
//		                            sendError(response, out, "Erreur lors de la création du compte", 500);
//		                            return;
//		                        }
//		                        compte.setId(compteId);
//		                    }
//		                    
//		                    BigDecimal soldeActuel = compte.getSolde();
//		                    BigDecimal limiteCredit = compte.getLimiteCredit();
//		                    BigDecimal montantCommande = commande.getMontantTotal();
//		                    
//		                    // 🔹 CORRECTION : Calculer le crédit déjà utilisé et le crédit restant
//		                    BigDecimal creditDejaUtilise = BigDecimal.ZERO;
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
//		                        creditDejaUtilise = soldeActuel.abs(); // La partie négative = crédit déjà utilisé
//		                    }
//		                    
//		                    BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
//		                    
//		                    // 🔹 CORRECTION IMPORTANTE : Le solde disponible pour payer est juste le crédit restant
//		                    // car le solde actuel est déjà négatif (crédit déjà utilisé)
//		                    BigDecimal soldeDisponiblePourPayer = creditRestantDisponible;
//		                    
//		                    System.out.println("=== VÉRIFICATION DE CRÉDIT ===");
//		                    System.out.println("Solde actuel: " + String.format("%.2f HTG", soldeActuel));
//		                    System.out.println("Limite crédit totale: " + String.format("%.2f HTG", limiteCredit));
//		                    System.out.println("Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise));
//		                    System.out.println("Crédit restant disponible: " + String.format("%.2f HTG", creditRestantDisponible));
//		                    System.out.println("Solde disponible pour payer: " + String.format("%.2f HTG", soldeDisponiblePourPayer));
//		                    System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
//		                    
//		                    // DÉTERMINER LE TYPE DE PAIEMENT ET LE STATUT
//		                    boolean utiliserCredit = false;
//		                    String statutPaiement = "PAYE"; // Par défaut
//		                    
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) >= 0) {
//		                        // Cas 1: Solde positif ou nul
//		                        if (soldeActuel.compareTo(montantCommande) >= 0) {
//		                            // Règle 1: Solde suffisant sans utiliser de crédit → PAYE
//		                            System.out.println("Paiement avec solde uniquement (pas de crédit) → STATUT: PAYE");
//		                            utiliserCredit = false;
//		                            statutPaiement = "PAYE";
//		                        } else {
//		                            // Règle 2: Solde positif mais insuffisant, utiliser crédit → PARTIEL
//		                            System.out.println("Paiement partiel avec solde + crédit → STATUT: PARTIEL");
//		                            utiliserCredit = true;
//		                            statutPaiement = "PARTIEL";
//		                        }
//		                    } else {
//		                        // Cas 2: Solde déjà négatif (crédit déjà utilisé)
//		                        // On ne peut utiliser que le crédit restant disponible
//		                        System.out.println("Solde négatif - utilisation crédit restant → STATUT: PARTIEL");
//		                        utiliserCredit = true;
//		                        statutPaiement = "PARTIEL";
//		                    }
//		                    
//		                    // Mettre à jour le statut de paiement de la commande
//		                    commande.setStatutPaiement(statutPaiement);
//		                    
//		                    // 🔹 Vérifier si le crédit restant est suffisant
//		                    if (utiliserCredit && soldeDisponiblePourPayer.compareTo(montantCommande) < 0) {
//		                        BigDecimal manquant = montantCommande.subtract(soldeDisponiblePourPayer);
//		                        sendError(response, out, 
//		                                "Crédit disponible insuffisant.\n" +
//		                                "Solde actuel: " + String.format("%.2f HTG", soldeActuel) + "\n" +
//		                                "Limite de crédit totale: " + String.format("%.2f HTG", limiteCredit) + "\n" +
//		                                "Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise) + "\n" +
//		                                "Crédit disponible restant: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
//		                                "Montant commande: " + String.format("%.2f HTG", montantCommande) + "\n" +
//		                                "Il manque: " + String.format("%.2f HTG", manquant), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Vérifier que le nouveau solde ne dépasse pas la limite négative
//		                    BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
//		                    BigDecimal limiteNegative = limiteCredit.negate();
//		                    
//		                    if (nouveauSolde.compareTo(limiteNegative) < 0) {
//		                        BigDecimal depassement = limiteNegative.subtract(nouveauSolde);
//		                        sendError(response, out, 
//		                                "Limite de crédit dépassée.\n" +
//		                                "Nouveau solde: " + String.format("%.2f HTG", nouveauSolde) + "\n" +
//		                                "Limite négative autorisée: " + String.format("%.2f HTG", limiteNegative) + "\n" +
//		                                "Dépassement: " + String.format("%.2f HTG", depassement), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Insérer la commande
//		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                    if (newId <= 0) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                        return;
//		                    }
//		                    
//		                    commande.setId(newId);
//		                    
//		                    // ========== DÉBIT DU COMPTE ==========
//		                    try {
//		                        // Re-récupérer le compte pour être sûr d'avoir les dernières données
//		                        compte = compteDAO.getCompteByClientId(clientId);
//		                        
//		                        if (compte != null) {
//		                            // Calculer le nouveau solde
//		                            BigDecimal soldeActuelCompte = compte.getSolde();
//		                            BigDecimal nouveauSoldeCompte = soldeActuelCompte.subtract(montantCommande);
//		                            
//		                            // Mettre à jour le solde dans la base
//		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSoldeCompte);
//		                            
//		                            if (updateReussi) {
//		                                System.out.println("=== MISE À JOUR SOLDE ===");
//		                                System.out.println("Solde mis à jour avec succès pour la commande #" + newId);
//		                                System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuelCompte));
//		                                System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
//		                                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSoldeCompte));
//		                                System.out.println("Statut paiement: " + statutPaiement);
//		                                System.out.println("Utilisation crédit: " + 
//		                                    (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0 ? 
//		                                     "OUI (" + nouveauSoldeCompte.abs() + " HTG)" : "NON"));
//		                                
//		                                // ========== GESTION CRÉDIT ==========
//		                                if (utiliserCredit) {
//		                                    // Si utilisation de crédit, créer un enregistrement dans CREDIT
//		                                    Credit credit = new Credit();
//		                                    credit.setUtilisateurId(clientId);
//		                                    credit.setCommandeId(newId);
//		                                    credit.setMontantTotal(montantCommande.intValue());
//		                                    
//		                                    // Calculer combien a été payé avec le solde réel (s'il était positif)
//		                                    BigDecimal montantPayeAvecSolde = BigDecimal.ZERO;
//		                                    if (soldeActuelCompte.compareTo(BigDecimal.ZERO) > 0) {
//		                                        montantPayeAvecSolde = soldeActuelCompte.min(montantCommande);
//		                                    }
//		                                    credit.setMontantPaye(montantPayeAvecSolde.intValue());
//		                                    
//		                                    // Si PARTIEL, statut "PARTIEL", sinon "PAYE"
//		                                    credit.setStatut(statutPaiement);
//		                                    
//		                                    CreditDAO creditDAO = new CreditDAO();
//		                                    // toValidate = false car c'est un paiement par solde avec crédit utilisé
//		                                    int creditId = creditDAO.ajouterCredit_v2(credit, false);
//		                                    if (creditId <= 0) {
//		                                        System.err.println("Erreur lors de l'ajout du crédit pour commande mixte");
//		                                    } else {
//		                                        System.out.println("Crédit créé pour commande mixte #" + newId + " (ID crédit: " + creditId + ")");
//		                                        System.out.println("Montant total: " + montantCommande + " HTG");
//		                                        System.out.println("Montant payé avec solde: " + montantPayeAvecSolde + " HTG");
//		                                        System.out.println("Montant restant: " + (montantCommande.subtract(montantPayeAvecSolde)) + " HTG");
//		                                    }
//
//		                                    NotificationDAO notifDAO = new NotificationDAO();
//		                                    // 🔔 Notification pour commande avec crédit
//		                                    Notification creditNotif = new Notification();
//		                                    creditNotif.setGeneratedBy("SYSTEM");
//		                                    creditNotif.setToUser(clientId);
//		                                    if (statutPaiement.equals("PARTIEL")) {
//		                                        creditNotif.setMessages("Commande #" + numeroCommande + 
//		                                                              " payée partiellement avec votre crédit disponible");
//		                                    } else {
//		                                        creditNotif.setMessages("Commande #" + numeroCommande + 
//		                                                              " payée avec votre crédit disponible");
//		                                    }
//		                                    creditNotif.setTypeNotif("CREDIT");
//		                                    creditNotif.setStatus("VISIBLE");
//		                                    notifDAO.ajouterNotification(creditNotif);
//		                                    
//		                                } else {
//		                                    // ========== ATTRIBUTION DES POINTS ==========
//		                                    // On donne des points seulement si paiement complet (PAYE)
//		                                    // et si le solde reste positif ou nul après paiement
//		                                    if (statutPaiement.equals("PAYE") && nouveauSoldeCompte.compareTo(BigDecimal.ZERO) >= 0) {
//		                                        try {
//		                                            PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                                            Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
//		                                            
//		                                            if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                                                // Log l'erreur mais ne pas bloquer la commande
//		                                                System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                                            } else {
//		                                                // Points attribués avec succès
//		                                                int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                                                System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
//		                                                
//		                                                PointDAO pointDAO = new PointDAO();
//		                                                PrivilegeNiveau nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(commande.getClientId());
//		                                                
//		                                                if (nouveauNiveau != null) {
//		                                                    System.out.println("🎉 Utilisateur #" + userId + " a changé de niveau vers: " + nouveauNiveau.getNom());
//		                                                    // La notification a été automatiquement insérée dans la table NOTIFICATION
//		                                                }
//		                                                
//		                                                // Notification pour les points
//		                                                Notification pointsNotif = new Notification();
//		                                                pointsNotif.setGeneratedBy("SYSTEM");
//		                                                pointsNotif.setToUser(clientId);
//		                                                pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre commande #" + numeroCommande);
//		                                                pointsNotif.setTypeNotif("POINTS");
//		                                                pointsNotif.setStatus("VISIBLE");
//
//		                                                NotificationDAO notifDAO = new NotificationDAO();
//		                                                notifDAO.ajouterNotification(pointsNotif);
//		                                                
//		                                                System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
//		                                            }
//		                                        } catch (Exception e) {
//		                                            System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
//		                                        }
//		                                    } else {
//		                                        System.out.println("Pas de points attribués - statut: " + statutPaiement + 
//		                                                          ", solde: " + String.format("%.2f HTG", nouveauSoldeCompte));
//		                                    }
//		                                }
//		                                
//		                                // 🔹 Créer une transaction pour le débit
//		                                try {
//		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
//		                                    TransactionCompte transaction = new TransactionCompte();
//		                                    transaction.setCompteClientId(compte.getId());
//		                                    transaction.setMontant(montantCommande);
//		                                    transaction.setSoldeAvant(soldeActuelCompte);
//		                                    transaction.setSoldeApres(nouveauSoldeCompte);
//		                                    
//		                                    String notes = "Paiement commande #" + numeroCommande + " - Statut: " + statutPaiement;
//		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0) {
//		                                        notes += " - Utilisation crédit: " + 
//		                                                String.format("%.2f HTG", nouveauSoldeCompte.abs());
//		                                    } else {
//		                                        notes += " - Paiement avec solde";
//		                                    }
//		                                    transaction.setNotes(notes);
//		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
//		                                    transaction.setCaissiereId(userId);
//		                                    transaction.setTypeTransactionId(2); // ID pour "DEPENSE"
//		                                    transaction.setCommandeId(newId);
//		                                    
//		                                    int transactionId = transactionDAO.creerTransaction(transaction);
//		                                    
//		                                    if (transactionId > 0) {
//		                                        System.out.println("Transaction enregistrée #" + transactionId);
//		                                        commandeDAO.updateTransactionId(newId, transactionId);
//		                                    } else {
//		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur création transaction: " + e.getMessage());
//		                                    e.printStackTrace();
//		                                }
//		                                
//		                            } else {
//		                                System.err.println("Erreur lors de la mise à jour du solde pour la commande #" + newId);
//		                            }
//		                        }
//		                    } catch (Exception e) {
//		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
//		                        e.printStackTrace();
//		                    }
//		                    
//		                } else {
//		                    // 🔹 Paiement autre que "solde" - insérer la commande normalement avec statut PAYE
//		                    commande.setStatutPaiement("PAYE");
//		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
//		                    if (newId <= 0) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
//		                        return;
//		                    }
//		                    commande.setId(newId);
//		                    
//		                    System.out.println("Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen") + 
//		                                      " - Statut: PAYE - pas d'attribution de points");
//		                }
//
//		                if (tableId != null) {
//		                    commandeDAO.reserverTable(tableId);
//		                }
//
//		                // 🔟 Supprimer du panier
//		                for (CommandeDetail detail : details) {
//		                    Integer panierId = detail.getPanierId();
//		                    if (panierId != null) {
//		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
//		                        PanierDAO pannierDAO = new PanierDAO();
//		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
//		                    }
//		                }
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("commandeId", commande.getId());
//		                
//		                // 🔔 Notification pour commande normale
//		                if (clientId != null) {
//		                    String messageNotif = "Nouvelle commande #" + numeroCommande +
//		                                        " montant: " + String.format("%.2f HTG", commande.getMontantTotal());
//		                    
//		                    if (paiementParSolde) {
//		                        messageNotif += " - Débité de votre compte";
//		                        if (commande.getStatutPaiement().equals("PARTIEL")) {
//		                            messageNotif += " (partiellement avec crédit)";
//		                        }
//		                    } else {
//		                        messageNotif += " - Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen");
//		                    }
//		                    
//		                    messageNotif += " - Statut: " + commande.getStatutPaiement();
//		                    
//		                    Notification notif = new Notification();
//		                    notif.setGeneratedBy("SYSTEM");
//		                    notif.setToUser(clientId);
//		                    notif.setMessages(messageNotif);
//		                    notif.setTypeNotif("COMMANDE");
//		                    notif.setStatus("VISIBLE");
//
//		                    NotificationDAO notifDAO = new NotificationDAO();
//		                    notifDAO.ajouterNotification(notif);
//
//		                    System.out.println("Notification commande envoyée au client #" + clientId);
//		                }
//
//		                data.addProperty("numeroCommande", numeroCommande);
//		                
//		                // Message de confirmation avec statut
//		                String confirmationMessage = "Commande ajoutée avec succès - Statut: " + commande.getStatutPaiement();
//		                if (paiementParSolde && clientId != null) {
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
//		                    if (compte != null) {
//		                        BigDecimal ancienSolde = compte.getSolde().add(commande.getMontantTotal());
//		                        BigDecimal nouveauSolde = compte.getSolde();
//		                        
//		                        confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
//		                        confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
//		                        
//		                        if (commande.getStatutPaiement().equals("PARTIEL")) {
//		                            confirmationMessage += "\n⚠️ Cette commande a utilisé votre limite de crédit";
//		                        }
//		                    }
//		                }
//		                
//		                data.addProperty("message", confirmationMessage);
//		                jsonResponse("ok", data, out);
//		            }
//		        }
//
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//		        e.printStackTrace();
//		    }
//		}
//	
		
		private void handleCommande_v2(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        // 1️⃣ Lire le corps de la requête
		        StringBuilder sb = new StringBuilder();
		        BufferedReader reader = request.getReader();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line);
		        }
		        String body = sb.toString();

		        if (body.trim().isEmpty()) {
		            sendError(response, out, "Corps de requête vide", 400);
		            return;
		        }
		        
		        

		        // 2️⃣ Parser le JSON
		        JsonObject jsonBody;
		        try {
		            jsonBody = gson.fromJson(body, JsonObject.class);
		        } catch (Exception e) {
		            sendError(response, out, "JSON invalide", 400);
		            return;
		        }

		        // 🔹 Récupérer action
		        String action = "";
		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
		            action = jsonBody.get("action").getAsString().trim();
		        }

		        // 3️⃣ Récupérer userId
		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
		            sendError(response, out, "Paramètre userId manquant", 400);
		            return;
		        }
		        int userId = jsonBody.get("userId").getAsInt();

		        // 4️⃣ Récupérer tableId / clientId / serveuseId
		        Integer tableId = (jsonBody.has("tableId") && !jsonBody.get("tableId").isJsonNull())
		                ? jsonBody.get("tableId").getAsInt()
		                : null;
		        Integer clientId = (jsonBody.has("clientId") && !jsonBody.get("clientId").isJsonNull())
		                ? jsonBody.get("clientId").getAsInt()
		                : null;
		        Integer serveuseId = (jsonBody.has("serveuseId") && !jsonBody.get("serveuseId").isJsonNull())
		                ? jsonBody.get("serveuseId").getAsInt()
		                : null;

		        // 5️⃣ Récupérer les détails
		        if (!jsonBody.has("details") || !jsonBody.get("details").isJsonArray()) {
		            sendError(response, out, "Paramètre details manquant ou invalide", 400);
		            return;
		        }
		        JsonArray detailsJson = jsonBody.getAsJsonArray("details");
		        List<CommandeDetail> details;
		        try {
		            details = gson.fromJson(detailsJson, new com.google.gson.reflect.TypeToken<List<CommandeDetail>>() {
		            }.getType());
		        } catch (Exception e) {
		            sendError(response, out, "Détails JSON invalides", 400);
		            return;
		        }

		        // 6️⃣ Validation des détails
		        for (int i = 0; i < details.size(); i++) {
		            CommandeDetail d = details.get(i);
		            if ((d.getProduitId() == null && d.getPlatId() == null) || d.getQuantite() <= 0) {
		                sendError(response, out, "Détail de commande invalide à l'index " + i, 400);
		                return;
		            }
		            if (d.getPrixUnitaire() == null || d.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
		                sendError(response, out, "Prix unitaire invalide à l'index " + i, 400);
		                return;
		            }
		        }

		        // 7️⃣ Construire l'objet commande
		        Commande commande = new Commande();

		        if (!jsonBody.has("numeroCommande") || jsonBody.get("numeroCommande").isJsonNull()) {
		            sendError(response, out, "Paramètre numeroCommande manquant", 400);
		            return;
		        }
		        String numeroCommande = jsonBody.get("numeroCommande").getAsString();
		        commande.setNumeroCommande(numeroCommande);

		        commande.setDateCommande(new Timestamp(new Date().getTime()));
		        if (jsonBody.has("statutCommande")) {
		            commande.setStatutCommande(jsonBody.get("statutCommande").getAsString());
		        }
		        
		        // Par défaut, statut paiement = PAYE (sera modifié si crédit utilisé)
		        commande.setStatutPaiement("PAYE");
		        
		        if (jsonBody.has("statutPaiement")) {
		            commande.setStatutPaiement(jsonBody.get("statutPaiement").getAsString());
		        }
		        if (jsonBody.has("moyenPaiement")) {
		            commande.setModePaiement(jsonBody.get("moyenPaiement").getAsString());
		        }
		        if (jsonBody.has("montantTotal") && !jsonBody.get("montantTotal").isJsonNull()) {
		            commande.setMontantTotal(jsonBody.get("montantTotal").getAsBigDecimal());
		        }

		        if (clientId != null && clientId > 0)
		            commande.setClientId(clientId);
		        if (tableId != null && tableId > 0) {
		            TableRooftop table = new TableRooftop();
		            table.setId(tableId);
		            commande.setTableRooftop(table);
		        }
		        commande.setUtilisateurId((serveuseId != null && serveuseId > 0) ? serveuseId : userId);

		        // 8️⃣ Récupérer le moyen de paiement
		        String moyenPaiement = null;
		        if (jsonBody.has("moyenPaiement") && !jsonBody.get("moyenPaiement").isJsonNull()) {
		            moyenPaiement = jsonBody.get("moyenPaiement").getAsString();
		        }

		        // 9️⃣ Selon action
		        if ("update".equalsIgnoreCase(action)) {
		            // Vérifier commandeId
		            if (!jsonBody.has("commandeId") || jsonBody.get("commandeId").isJsonNull()) {
		                sendError(response, out, "Paramètre commandeId manquant pour update", 400);
		                return;
		            }
		            int commandeId = jsonBody.get("commandeId").getAsInt();
		            commande.setId(commandeId);

		            boolean success = commandeDAO.modifierCommandePOS(commande, details);
		            if (!success) {
		                sendError(response, out, "Erreur lors de la mise à jour de la commande", 500);
		                return;
		            }

		            JsonObject data = new JsonObject();
		            data.addProperty("commandeId", commandeId);
		            data.addProperty("numeroCommande", numeroCommande);
		            data.addProperty("message", "Commande mise à jour avec succès");
		            jsonResponse("ok", data, out);

		        } else {
		            // Cas par défaut → Ajout
		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
		            System.out.println("Valeur de isCredit: " + isCredit);

		            if (isCredit) {
		                // 🔹 Vérifier le plafond AVANT insertion
		                Utilisateur client = utilisateurDAO.findById(clientId);
		                if (client == null) {
		                    sendError(response, out, "Client introuvable", 404);
		                    return;
		                }

		                int plafond = client.getPlafond();

		                // 🔹 Total crédits en cours
		                List<Commande> creditsEnCours = creditDAO.getCommandesCredit(null, clientId, null, null);
		                int totalCredits = creditsEnCours.stream()
		                        .mapToInt(c -> c.getCredit() != null
		                                ? c.getCredit().getMontantTotal() - c.getCredit().getMontantPaye()
		                                : 0)
		                        .sum();

		                int montantCommande = commande.getMontantTotal().intValue();

		                if ((totalCredits + montantCommande) > plafond) {
		                    sendError(response, out, "Plafond atteint", 400);
		                    return; // 🚨 On stoppe → aucune commande insérée
		                }

		                // ✅ Plafond OK → insérer la commande avec statut NON_PAYE
		                commande.setStatutPaiement("NON_PAYE"); // ← MODIFICATION ICI
		                int newId = commandeDAO.ajouterCommandePOS(commande, details);
		                if (newId <= 0) {
		                    sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
		                    return;
		                }
		                
		                // 🔹 Ajouter le crédit
		                Credit credit = new Credit();
		                credit.setUtilisateurId(clientId);
		                credit.setCommandeId(newId);
		                credit.setMontantTotal(montantCommande);
		                credit.setMontantPaye(0);
		                credit.setStatut("NON_PAYE");

		                CreditDAO creditDAO = new CreditDAO();
		                int creditId = creditDAO.ajouterCredit(credit);
		                if (creditId <= 0) {
		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
		                    return;
		                }
		                
		                // 🔔 Notification pour commande crédit
		                Notification notif = new Notification();
		                notif.setGeneratedBy("SYSTEM");
		                notif.setToUser(clientId);
		                notif.setMessages("Nouvelle commande à crédit #" + numeroCommande + 
		                                " d'un montant de " + montantCommande + " HTG");
		                notif.setTypeNotif("CREDIT");
		                notif.setStatus("VISIBLE");

		                NotificationDAO notifDAO = new NotificationDAO();
		                notifDAO.ajouterNotification(notif);

		                System.out.println("Notification crédit envoyée au client #" + clientId);

		                if (tableId != null) {
		                    commandeDAO.reserverTable(tableId);
		                }

		                // 🔟 Supprimer du panier
		                for (CommandeDetail detail : details) {
		                    Integer panierId = detail.getPanierId();
		                    if (panierId != null) {
		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
		                        PanierDAO pannierDAO = new PanierDAO();
		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
		                    }
		                }

		                JsonObject data = new JsonObject();
		                data.addProperty("commandeId", newId);
		                data.addProperty("numeroCommande", numeroCommande);
		                data.addProperty("message", "Commande crédit ajoutée avec succès");
		                jsonResponse("ok", data, out);

		            } else {
		                // 🔹 Cas normal → vérifier le moyen de paiement
		                boolean paiementParSolde = "solde".equalsIgnoreCase(moyenPaiement);
		                
		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
		                if (paiementParSolde && clientId != null) {
		                    // Récupérer le compte du client avec limite de crédit
		                    CompteClientDAO compteDAO = new CompteClientDAO();
		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
		                    
		                    if (compte == null) {
		                        // Créer le compte s'il n'existe pas
		                        compte = new CompteClient();
		                        compte.setClientId(clientId);
		                        compte.setSolde(BigDecimal.ZERO);
		                        compte.setLimiteCredit(BigDecimal.ZERO);
		                        int compteId = compteDAO.creerCompteClient(compte);
		                        if (compteId <= 0) {
		                            sendError(response, out, "Erreur lors de la création du compte", 500);
		                            return;
		                        }
		                        compte.setId(compteId);
		                    }
		                    
		                    BigDecimal soldeActuel = compte.getSolde();
		                    BigDecimal limiteCredit = compte.getLimiteCredit();
		                    BigDecimal montantCommande = commande.getMontantTotal();
		                    
		                    // 🔹 DEBUG: Afficher les informations pour le débogage
		                    System.out.println("=== DEBUG PAIEMENT SOLDE ===");
		                    System.out.println("Client ID: " + clientId);
		                    System.out.println("Solde actuel: " + String.format("%.2f HTG", soldeActuel));
		                    System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
		                    System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
		                    
		                    // 🔹 Calculer le crédit déjà utilisé
		                    BigDecimal creditDejaUtilise = BigDecimal.ZERO;
		                    if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
		                        creditDejaUtilise = soldeActuel.abs();
		                        System.out.println("Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise));
		                    }
		                    
		                    BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
		                    System.out.println("Crédit restant disponible: " + String.format("%.2f HTG", creditRestantDisponible));
		                    
		                    // 🔹 DÉTERMINER LE TYPE DE PAIEMENT ET LE STATUT
		                    boolean utiliserCredit = false;
		                    String statutPaiement = "PAYE"; // Par défaut
		                    
		                    // CAS 1: Solde suffisant seul
		                    if (soldeActuel.compareTo(montantCommande) >= 0) {
		                        // Solde suffisant sans utiliser de crédit
		                        System.out.println("CAS 1: Paiement avec solde uniquement (PAYE)");
		                        utiliserCredit = false;
		                        statutPaiement = "PAYE";
		                        
		                    } else if (soldeActuel.compareTo(BigDecimal.ZERO) >= 0 && 
		                             soldeActuel.add(creditRestantDisponible).compareTo(montantCommande) >= 0) {
		                        // CAS 2: Solde + crédit
		                        System.out.println("CAS 2: Paiement avec solde + crédit (PARTIEL)");
		                        utiliserCredit = true;
		                        statutPaiement = "PARTIEL";
		                        
		                    } else if (soldeActuel.compareTo(BigDecimal.ZERO) < 0 && 
		                             creditRestantDisponible.compareTo(montantCommande) >= 0) {
		                        // CAS 3: Crédit uniquement (solde déjà négatif)
		                        System.out.println("CAS 3: Paiement avec crédit uniquement (NON_PAYE)");
		                        utiliserCredit = true;
		                        statutPaiement = "NON_PAYE";
		                        
		                    } else {
		                        // CAS 4: Insuffisant
		                        BigDecimal montantDisponible = soldeActuel.compareTo(BigDecimal.ZERO) >= 0 
		                            ? soldeActuel.add(creditRestantDisponible)
		                            : creditRestantDisponible;
		                        BigDecimal manquant = montantCommande.subtract(montantDisponible);
		                        sendError(response, out, 
		                                "Solde et crédit insuffisants.\n" +
		                                "Solde actuel: " + String.format("%.2f HTG", soldeActuel) + "\n" +
		                                "Crédit disponible: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
		                                "Total disponible: " + String.format("%.2f HTG", montantDisponible) + "\n" +
		                                "Montant commande: " + String.format("%.2f HTG", montantCommande) + "\n" +
		                                "Il manque: " + String.format("%.2f HTG", manquant), 400);
		                        return;
		                    }
		                    
		                    // 🔹 Mettre à jour le statut de paiement de la commande
		                    commande.setStatutPaiement(statutPaiement);
		                    System.out.println("Statut de paiement défini à: " + statutPaiement);
		                    
		                    // 🔹 Vérifier que le nouveau solde ne dépasse pas la limite négative
		                    BigDecimal nouveauSolde = soldeActuel.subtract(montantCommande);
		                    BigDecimal limiteNegative = limiteCredit.negate();
		                    
		                    if (nouveauSolde.compareTo(limiteNegative) < 0) {
		                        BigDecimal depassement = limiteNegative.subtract(nouveauSolde);
		                        sendError(response, out, 
		                                "Limite de crédit dépassée.\n" +
		                                "Nouveau solde: " + String.format("%.2f HTG", nouveauSolde) + "\n" +
		                                "Limite négative autorisée: " + String.format("%.2f HTG", limiteNegative) + "\n" +
		                                "Dépassement: " + String.format("%.2f HTG", depassement), 400);
		                        return;
		                    }
		                    
		                    // 🔹 Insérer la commande
		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
		                    if (newId <= 0) {
		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
		                        return;
		                    }
		                    
		                    commande.setId(newId);
		                    
		                    // ========== DÉBIT DU COMPTE ==========
		                    try {
		                        // Re-récupérer le compte pour être sûr d'avoir les dernières données
		                        compte = compteDAO.getCompteByClientId(clientId);
		                        
		                        if (compte != null) {
		                            // Calculer le nouveau solde
		                            BigDecimal soldeActuelCompte = compte.getSolde();
		                            BigDecimal nouveauSoldeCompte = soldeActuelCompte.subtract(montantCommande);
		                            
		                            // Mettre à jour le solde dans la base
		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSoldeCompte);
		                            
		                            if (updateReussi) {
		                                System.out.println("=== MISE À JOUR SOLDE ===");
		                                System.out.println("Solde mis à jour avec succès pour la commande #" + newId);
		                                System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuelCompte));
		                                System.out.println("Montant commande: " + String.format("%.2f HTG", montantCommande));
		                                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSoldeCompte));
		                                System.out.println("Statut paiement: " + statutPaiement);
		                                
		                                // ========== GESTION CRÉDIT ==========
		                                if (utiliserCredit && !statutPaiement.equals("NON_PAYE")) {
		                                    // Si utilisation de crédit mais pas totalement (PARTIEL)
		                                    Credit credit = new Credit();
		                                    credit.setUtilisateurId(clientId);
		                                    credit.setCommandeId(newId);
		                                    credit.setMontantTotal(montantCommande.intValue());
		                                    
		                                    // Calculer combien a été payé avec le solde réel
		                                    BigDecimal montantPayeAvecSolde = BigDecimal.ZERO;
		                                    if (soldeActuelCompte.compareTo(BigDecimal.ZERO) > 0) {
		                                        montantPayeAvecSolde = soldeActuelCompte.min(montantCommande);
		                                    }
		                                    credit.setMontantPaye(montantPayeAvecSolde.intValue());
		                                    credit.setStatut("PARTIEL");
		                                    
		                                    CreditDAO creditDAO = new CreditDAO();
		                                    int creditId = creditDAO.ajouterCredit(credit);
		                                    if (creditId <= 0) {
		                                        System.err.println("Erreur lors de l'ajout du crédit pour commande mixte");
		                                    } else {
		                                        System.out.println("Crédit créé pour commande mixte #" + newId + " (ID crédit: " + creditId + ")");
		                                    }

		                                    NotificationDAO notifDAO = new NotificationDAO();
		                                    Notification creditNotif = new Notification();
		                                    creditNotif.setGeneratedBy("SYSTEM");
		                                    creditNotif.setToUser(clientId);
		                                    creditNotif.setMessages("Commande #" + numeroCommande + 
		                                                          " payée partiellement avec votre crédit (" + 
		                                                          montantPayeAvecSolde.intValue() + "/" + montantCommande.intValue() + " HTG)");
		                                    creditNotif.setTypeNotif("CREDIT");
		                                    creditNotif.setStatus("VISIBLE");
		                                    notifDAO.ajouterNotification(creditNotif);
		                                    
		                                } else if (statutPaiement.equals("NON_PAYE")) {
		                                    // Si totalement à crédit
		                                    Credit credit = new Credit();
		                                    credit.setUtilisateurId(clientId);
		                                    credit.setCommandeId(newId);
		                                    credit.setMontantTotal(montantCommande.intValue());
		                                    credit.setMontantPaye(0);
		                                    credit.setStatut("NON_PAYE");
		                                    
		                                    CreditDAO creditDAO = new CreditDAO();
		                                    int creditId = creditDAO.ajouterCredit(credit);
		                                    if (creditId <= 0) {
		                                        System.err.println("Erreur lors de l'ajout du crédit pour commande non payée");
		                                    } else {
		                                        System.out.println("Crédit créé pour commande non payée #" + newId + " (ID crédit: " + creditId + ")");
		                                    }
		                                } else {
		                                    // ========== ATTRIBUTION DES POINTS ==========
		                                    // On donne des points seulement si paiement complet (PAYE) sans crédit
		                                    if (statutPaiement.equals("PAYE")) {
		                                        try {
		                                            PointManagerDAO pointManagerDAO = new PointManagerDAO();
		                                            Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(commande, details, request.getRemoteAddr());
		                                            

	                                              
		                                            if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
		                                                // Log l'erreur mais ne pas bloquer la commande
		                                                System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
		                                            } else {
		                                                // Points attribués avec succès
		                                                int totalPoints = (Integer) resultPoints.get("totalPoints");
		                                                System.out.println("Points attribués: " + totalPoints + " pour la commande #" + newId);
		                                                
		                                                PointDAO pointDAO = new PointDAO();
		                                                PrivilegeNiveau nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(userId);
		                                                
		                                                // Notification pour les points
		                                                Notification pointsNotif = new Notification();
		                                                pointsNotif.setGeneratedBy("SYSTEM");
		                                                pointsNotif.setToUser(clientId);
		                                                pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre commande #" + numeroCommande);
		                                                pointsNotif.setTypeNotif("POINTS");
		                                                pointsNotif.setStatus("VISIBLE");

		                                                NotificationDAO notifDAO = new NotificationDAO();
		                                                notifDAO.ajouterNotification(pointsNotif);
		                                                
		                                                System.out.println("Client #" + clientId + " a reçu " + totalPoints + " points");
		                                            }
		                                        } catch (Exception e) {
		                                            System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
		                                        }
		                                    } else {
		                                        System.out.println("Pas de points attribués - statut paiement: " + statutPaiement);
		                                    }
		                                }
		                                
		                                // 🔹 Créer une transaction pour le débit
		                                try {
		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
		                                    TransactionCompte transaction = new TransactionCompte();
		                                    transaction.setCompteClientId(compte.getId());
		                                    transaction.setMontant(montantCommande);
		                                    transaction.setSoldeAvant(soldeActuelCompte);
		                                    transaction.setSoldeApres(nouveauSoldeCompte);
		                                    
		                                    String notes = "Paiement commande #" + numeroCommande + " - Statut: " + statutPaiement;
		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0) {
		                                        notes += " - Utilisation crédit: " + 
		                                                String.format("%.2f HTG", nouveauSoldeCompte.abs());
		                                    }
		                                    transaction.setNotes(notes);
		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
		                                    transaction.setCaissiereId(userId);
		                                    transaction.setTypeTransactionId(2); // ID pour "DEPENSE"
		                                    transaction.setCommandeId(newId);
		                                    
		                                    int transactionId = transactionDAO.creerTransaction(transaction);
		                                    
		                                    if (transactionId > 0) {
		                                        System.out.println("Transaction enregistrée #" + transactionId);
		                                        commandeDAO.updateTransactionId(newId, transactionId);
		                                    } else {
		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
		                                    }
		                                } catch (Exception e) {
		                                    System.err.println("Erreur création transaction: " + e.getMessage());
		                                    e.printStackTrace();
		                                }
		                                
		                            } else {
		                                System.err.println("Erreur lors de la mise à jour du solde pour la commande #" + newId);
		                            }
		                        }
		                    } catch (Exception e) {
		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
		                        e.printStackTrace();
		                    }
		                    
		                } else {
		                    // 🔹 Paiement autre que "solde" - insérer la commande normalement
		                    // Par défaut PAYE pour les autres moyens de paiement
		                    if (!commande.getStatutPaiement().equals("PARTIEL") && !commande.getStatutPaiement().equals("NON_PAYE")) {
		                        commande.setStatutPaiement("PAYE");
		                    }
		                    
		                    int newId = commandeDAO.ajouterCommandePOS(commande, details);
		                    if (newId <= 0) {
		                        sendError(response, out, "Erreur lors de l'ajout de la commande", 500);
		                        return;
		                    }
		                    commande.setId(newId);
		                    
		                    System.out.println("Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen") + 
		                                     " - Statut: " + commande.getStatutPaiement());
		                }

		                if (tableId != null) {
		                    commandeDAO.reserverTable(tableId);
		                }

		                // 🔟 Supprimer du panier
		                for (CommandeDetail detail : details) {
		                    Integer panierId = detail.getPanierId();
		                    if (panierId != null) {
		                        String type = (detail.getPlatId() != null) ? "plat" : "produit";
		                        PanierDAO pannierDAO = new PanierDAO();
		                        pannierDAO.supprimerDuPanier(userId, panierId, type);
		                    }
		                }

		                JsonObject data = new JsonObject();
		                data.addProperty("commandeId", commande.getId());
		                
		                // 🔔 Notification pour commande normale
		                if (clientId != null) {
		                    String messageNotif = "Nouvelle commande #" + numeroCommande +
		                                        " montant: " + String.format("%.2f HTG", commande.getMontantTotal()) +
		                                        " - Statut: " + commande.getStatutPaiement();
		                    
		                    if (paiementParSolde) {
		                        messageNotif += " - Débité de votre compte";
		                    } else {
		                        messageNotif += " - Paiement par " + (moyenPaiement != null ? moyenPaiement : "autre moyen");
		                    }
		                    
		                    Notification notif = new Notification();
		                    notif.setGeneratedBy("SYSTEM");
		                    notif.setToUser(clientId);
		                    notif.setMessages(messageNotif);
		                    notif.setTypeNotif("COMMANDE");
		                    notif.setStatus("VISIBLE");

		                    NotificationDAO notifDAO = new NotificationDAO();
		                    notifDAO.ajouterNotification(notif);

		                    System.out.println("Notification commande envoyée au client #" + clientId);
		                }

		                data.addProperty("numeroCommande", numeroCommande);
		                
		                // Message de confirmation
		                String confirmationMessage = "Commande ajoutée avec succès";
		                confirmationMessage += "\nStatut paiement: " + commande.getStatutPaiement();
		                
		                if (paiementParSolde && clientId != null) {
		                    CompteClientDAO compteDAO = new CompteClientDAO();
		                    CompteClient compte = compteDAO.getCompteByClientId(clientId);
		                    if (compte != null) {
		                        BigDecimal ancienSolde = compte.getSolde().add(commande.getMontantTotal());
		                        BigDecimal nouveauSolde = compte.getSolde();
		                        
		                        confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
		                        confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
		                        
		                        if (commande.getStatutPaiement().equals("PARTIEL")) {
		                            confirmationMessage += "\n⚠️ Cette commande a utilisé votre limite de crédit (paiement partiel)";
		                        } else if (commande.getStatutPaiement().equals("NON_PAYE")) {
		                            confirmationMessage += "\n⚠️ Cette commande est entièrement à crédit";
		                        }
		                    }
		                }
		                
		                data.addProperty("message", confirmationMessage);
		                jsonResponse("ok", data, out);
		            }
		        }

		    } catch (Exception e) {
		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
		        e.printStackTrace();
		    }
		}
		private void handleListTable(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			// Récupération et validation du paramètre "etat"
			String etat = request.getParameter("etat"); // RESERVE ou DISPONIBLE
			if (etat == null || (!etat.equalsIgnoreCase("RESERVE") && !etat.equalsIgnoreCase("DISPONIBLE"))) {
				sendError(response, out, "Paramètre etat manquant ou invalide (RESERVE ou DISPONIBLE)", 400);
				return;
			}
	
			try {
				// Appel DAO pour lister les tables selon l'état (en majuscule)
				List<TableRooftop> tables = tableDAO.listerTablesParEtatPos(etat.toUpperCase());
	
				// Stockage dans l'objet request (optionnel si tu veux l'utiliser côté JSP)
				request.setAttribute("tables", tables);
	
				// Envoi de la réponse JSON
				jsonResponse("ok", gson.toJsonTree(tables), out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération des tables: " + e.getMessage(), 500);
			}
		}
	
		private void handleAllTables(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	
			try {
				// Appel DAO pour lister les tables selon l'état (en majuscule)
				List<TableRooftop> tables = tableDAO.listerTables();
	
				// Stockage dans l'objet request (optionnel si tu veux l'utiliser côté JSP)
				request.setAttribute("tables", tables);
	
				// Envoi de la réponse JSON
				jsonResponse("ok", gson.toJsonTree(tables), out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération des tables: " + e.getMessage(), 500);
			}
		}
	
		private void handleVendeuseTable(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Récupération de l'utilisateur depuis la session (ou un param)
				String userIdStr = request.getParameter("userId"); // RESERVE ou DISPONIBLE
				if (userIdStr == null) {
					sendError(response, out, "Paramètre userId manquant", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
	
				// Récupérer toutes les tables gérées par cette vendeuse (via ses commandes)
				List<TableRooftop> tables = tableDAO.listerTablesParVendeuse(userId);
	
				// Stocker si besoin
				request.setAttribute("tablesVendeuse", tables);
	
				// Réponse JSON
				jsonResponse("ok", gson.toJsonTree(tables), out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération des tables vendeuse: " + e.getMessage(), 500);
			}
		}
	
		private void handleGetDetails(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String idStr = request.getParameter("id");
			if (idStr == null) {
				sendError(response, out, "Paramètre id manquant", 400);
				return;
			}
	
			try {
				int commandeId = Integer.parseInt(idStr);
				List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
	
				// Gson qui inclut les champs null
				Gson gsonIncludeNulls = new GsonBuilder().serializeNulls().create();
	
				jsonResponse("ok", gsonIncludeNulls.toJsonTree(details), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
		private void handleGetByNumero(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			String numero = request.getParameter("numero");
			if (numero == null || numero.trim().isEmpty()) {
				sendError(response, out, "Paramètre 'numero' manquant ou vide", 400);
				return;
			}
			// Récupération de l'utilisateur depuis la session (ou un param)
			String userIdStr = request.getParameter("userId"); // RESERVE ou DISPONIBLE
			if (userIdStr == null) {
				sendError(response, out, "Paramètre userId manquant", 400);
				return;
			}
	
			int userId = Integer.parseInt(userIdStr);
	
			try {
				Commande commande = commandeDAO.getCommandeByNumero(numero, userId);
				if (commande == null) {
					// Si pas trouvé, renvoyer une erreur claire
					sendError(response, out, "Commande introuvable pour le numéro: " + numero, 404);
					return;
				}
	
				List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
				if (details != null) {
					commande.setDetails(details);
				} else {
					// Même si details est null, on peut envoyer une liste vide
					commande.setDetails(new ArrayList<>());
				}
	
				jsonResponse("ok", gson.toJsonTree(commande), out);
	
			} catch (Exception e) {
				e.printStackTrace();
				sendError(response, out, "Erreur serveur: " + e.getMessage(), 500);
			}
		}
	
		private void jsonResponse(String status, Object data, PrintWriter out) {
			JsonObject response = new JsonObject();
			response.addProperty("status", status);
			response.add("data", gson.toJsonTree(data));
			out.print(gson.toJson(response));
			out.flush();
		}
	
		private void handlePaiementCommande(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Lire le corps de la requête JSON
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				String body = sb.toString();
				System.out.println("Corps de la requête JSON: " + body);
	
				// Parser le JSON
				JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	
				// Récupérer les paramètres
				if (!json.has("commandeId") || !json.has("statutPaiement") || !json.has("modePaiement")
						|| !json.has("userId")) {
					sendError(response, out, "Paramètres manquants (commandeId, statutPaiement, modePaiement, userId)",
							400);
					return;
				}
	
				int commandeId = json.get("commandeId").getAsInt();
				String statutPaiement = json.get("statutPaiement").getAsString();
				String modePaiement = json.get("modePaiement").getAsString();
				int userId = json.get("userId").getAsInt();
	
				// Créer l'objet Commande minimal pour la mise à jour
				Commande commande = new Commande();
				commande.setId(commandeId);
				commande.setStatutPaiement(statutPaiement);
				commande.setModePaiement(modePaiement);
				commande.setUtilisateurId(userId);
	
				// DAO
				CommandeDAO commandeDAO = new CommandeDAO();
				boolean success = commandeDAO.updateCommandeStatutPaiement(userId, commandeId, statutPaiement,
						modePaiement);
	
				// Préparer la réponse JSON
				JsonObject data = new JsonObject();
				data.addProperty("message",
						success ? "Statut commande modifié" : "Erreur lors de la modification du statut");
				jsonResponse(success ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la modification du statut: " + e.getMessage(), 500);
			}
		}
	
	//    private void handlePayerCredit(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            // Lire le corps de la requête JSON
	//            StringBuilder sb = new StringBuilder();
	//            BufferedReader reader = request.getReader();
	//            String line;
	//            while ((line = reader.readLine()) != null) {
	//                sb.append(line);
	//            }
	//            String body = sb.toString();
	//            System.out.println("Corps de la requête JSON: " + body);
	//
	//            // Parser le JSON
	//            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	//
	//            // Vérifier que tous les paramètres sont présents
	//            if ( !json.has("montant") || !json.has("utilisateurId")) {
	//                sendError(response, out, "Paramètres manquants (creditId, montant, utilisateurId)", 400);
	//                return;
	//            }
	//
	//            // Récupérer les paramètres
	//            int montant = json.get("montantVerse").getAsInt();
	//            int utilisateurId = json.get("userId").getAsInt();
	//
	//            // DAO
	//            CreditDAO creditDAO = new CreditDAO();
	//            boolean success = creditDAO.payerCreditGlobal(utilisateurId, montant );
	//
	//            // Préparer la réponse "maison"
	//            JsonObject data = new JsonObject();
	//            data.addProperty("message", success ? "Paiement effectué avec succès" : "Erreur lors du paiement");
	//            jsonResponse(success ? "ok" : "error", data, out);
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du paiement du crédit: " + e.getMessage(), 500);
	//        }
	//    }
	//    private void handlePayerCredit(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            // Lire les paramètres POST
	//            String montantStr = request.getParameter("montantVerse");
	//            String userIdStr = request.getParameter("userId");
	//
	//            if (montantStr == null || userIdStr == null) {
	//                sendError(response, out, "Paramètres manquants (montantVerse, userId)", 400);
	//                return;
	//            }
	//
	//            int montant = Integer.parseInt(montantStr);
	//            int utilisateurId = Integer.parseInt(userIdStr);
	//
	//            // DAO
	//            CreditDAO creditDAO = new CreditDAO();
	//            boolean success = creditDAO.payerCreditGlobal(utilisateurId, montant);
	//
	//            // Préparer la réponse
	//            JsonObject data = new JsonObject();
	//            data.addProperty("message", success ? "Paiement effectué avec succès" : "Erreur lors du paiement");
	//            jsonResponse(success ? "ok" : "error", data, out);
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du paiement du crédit: " + e.getMessage(), 500);
	//        }
	//    }
	
		private void handlePayerCredit(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Lire les paramètres POST
				String montantStr = request.getParameter("montantVerse");
				String userIdStr = request.getParameter("userId");
	
				System.out.println("=== Requête PAYER CREDIT ===");
				System.out.println("Param userId: " + userIdStr);
				System.out.println("Param montantVerse: " + montantStr);
	
				if (montantStr == null || userIdStr == null) {
					sendError(response, out, "Paramètres manquants (montantVerse, userId)", 400);
					return;
				}
	
				int montant = Integer.parseInt(montantStr);
				int utilisateurId = Integer.parseInt(userIdStr);
	
				System.out.println("Montant converti: " + montant);
				System.out.println("Utilisateur converti: " + utilisateurId);
	
				// DAO
				CreditDAO creditDAO = new CreditDAO();
				boolean success = creditDAO.payerCreditGlobal(utilisateurId, montant);
	
				System.out.println("Résultat DAO: " + success);
	
				// Préparer la réponse
				JsonObject data = new JsonObject();
				data.addProperty("message", success ? "Paiement effectué avec succès" : "Erreur lors du paiement");
				jsonResponse(success ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				e.printStackTrace();
				sendError(response, out, "Erreur lors du paiement du crédit: " + e.getMessage(), 500);
			}
		}
	
		private void handleUpdateCommandeStatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				// Lire le corps de la requête JSON
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				String body = sb.toString();
				System.out.println("Corps de la requête JSON: " + body);
	
				// Parser le JSON
				JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	
				// Récupérer les paramètres
				if (!json.has("commandeId") || !json.has("statutCommande") || !json.has("userId")) {
					sendError(response, out, "Paramètres manquants (commandeId, statutCommande, userId)", 400);
					return;
				}
	
				int commandeId = json.get("commandeId").getAsInt();
				String statutCommande = json.get("statutCommande").getAsString();
				int userId = json.get("userId").getAsInt();
	
				// DAO
				CommandeDAO commandeDAO = new CommandeDAO();
	
				// 🔹 Récupérer la commande existante
				Commande commande = commandeDAO.getCommandeById(commandeId);
				if (commande == null) {
					sendError(response, out, "Commande introuvable", 404);
					return;
				}
	
				// 🔹 Vérifier si elle est déjà annulée
				if ("ANNULE".equalsIgnoreCase(commande.getStatutCommande())) {
					JsonObject data = new JsonObject();
					data.addProperty("message", "Operation invalide, la commande a été annulée");
					jsonResponse("error", data, out);
					return;
				}
	
				// Modifier le statut
				boolean success = commandeDAO.modifierStatutCommande(commandeId, statutCommande, userId);
	
				// Préparer la réponse JSON
				JsonObject data = new JsonObject();
				data.addProperty("message",
						success ? "Statut commande modifié" : "Erreur lors de la modification du statut");
				jsonResponse(success ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la modification du statut: " + e.getMessage(), 500);
			}
		}
	
		private void handleAjouterPanier(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				String produitIdStr = request.getParameter("produitId");
				String quantiteStr = request.getParameter("quantite");
				String type = request.getParameter("type"); // "plat" ou "produit"
	
				if (userIdStr == null || produitIdStr == null || quantiteStr == null || type == null) {
					sendError(response, out, "Paramètres manquants (userId, produitId, quantite, type)", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
				int produitId = Integer.parseInt(produitIdStr);
				int quantite = Integer.parseInt(quantiteStr);
	
				PanierDAO panierDAO = new PanierDAO();
	//            boolean ajoutOk = panierDAO.ajouterAuPanier(userId, produitId, quantite, type);
	//
	//            JsonObject data = new JsonObject();
	//            data.addProperty("message", ajoutOk ? "Ajouté au panier" : "Erreur lors de l'ajout");
	//            jsonResponse(ajoutOk ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de l'ajout au panier: " + e.getMessage(), 500);
			}
		}
	
	//    private void handleListerPanier(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            String userIdStr = request.getParameter("userId");
	//            if (userIdStr == null) {
	//                sendError(response, out, "Paramètre userId manquant", 400);
	//                return;
	//            }
	//
	//            int userId = Integer.parseInt(userIdStr);
	//            PanierDAO panierDAO = new PanierDAO();
	//            List<Panier> panierList = panierDAO.listerPanier(userId);
	//
	//            jsonResponse("ok", panierList, out);
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors de la récupération du panier: " + e.getMessage(), 500);
	//        }
	//    }
	
		private void handleListerPanier(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				if (userIdStr == null) {
					sendError(response, out, "Paramètre userId manquant", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
				PanierDAO panierDAO = new PanierDAO();
				List<Panier> panierList = panierDAO.listerPanier(userId);
				if (panierList == null)
					panierList = new ArrayList<>();
	
				// 🔹 Appliquer l'URL finale aux images
				for (Panier panier : panierList) {
					if (panier.getProduit() != null) {
						String finalProduitImage = getImagePath(request, "produit", panier.getProduit().getImageUrl());
						panier.getProduit().setImageUrl(finalProduitImage);
	
						// Catégorie et sous-catégorie produit
						if (panier.getProduit().getCategorieMenu() != null) {
							String catImage = getImagePath(request, "categorie",
									panier.getProduit().getCategorieMenu().getImageUrl());
							panier.getProduit().getCategorieMenu().setImageUrl(catImage);
						}
						if (panier.getProduit().getSousCategorieMenu() != null) {
							String sousCatImage = getImagePath(request, "categorie",
									panier.getProduit().getSousCategorieMenu().getImageUrl());
							panier.getProduit().getSousCategorieMenu().setImageUrl(sousCatImage);
						}
					}
	
					if (panier.getPlat() != null) {
						String finalPlatImage = getImagePath(request, "plat", panier.getPlat().getImage());
						panier.getPlat().setImage(finalPlatImage);
	
						// Catégorie et sous-catégorie plat
						if (panier.getPlat().getCategorieMenu() != null) {
							String catImage = getImagePath(request, "categorie",
									panier.getPlat().getCategorieMenu().getImageUrl());
							panier.getPlat().getCategorieMenu().setImageUrl(catImage);
						}
						if (panier.getPlat().getSousCategorieMenu() != null) {
							String sousCatImage = getImagePath(request, "categorie",
									panier.getPlat().getSousCategorieMenu().getImageUrl());
							panier.getPlat().getSousCategorieMenu().setImageUrl(sousCatImage);
						}
					}
				}
	
				jsonResponse("ok", panierList, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la récupération du panier: " + e.getMessage(), 500);
			}
		}
	
		private void handleSupprimerPanier(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				String panierIdStr = request.getParameter("panierId");
				String type = request.getParameter("type");
	
				if (userIdStr == null || panierIdStr == null || type == null) {
					sendError(response, out, "Paramètres manquants (userId, panierId, type)", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
				int panierId = Integer.parseInt(panierIdStr);
	
				PanierDAO panierDAO = new PanierDAO();
				boolean supprOk = panierDAO.supprimerDuPanier(userId, panierId, type);
	
				JsonObject data = new JsonObject();
				data.addProperty("message", supprOk ? "Supprimé du panier" : "Erreur lors de la suppression");
				jsonResponse(supprOk ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors de la suppression du panier: " + e.getMessage(), 500);
			}
		}
	
		private void handleViderPanier(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				String userIdStr = request.getParameter("userId");
				if (userIdStr == null) {
					sendError(response, out, "Paramètre userId manquant", 400);
					return;
				}
	
				int userId = Integer.parseInt(userIdStr);
				PanierDAO panierDAO = new PanierDAO();
	//            boolean viderOk = panierDAO.viderPanier(userId);
	//
	//            JsonObject data = new JsonObject();
	//            data.addProperty("message", viderOk ? "Panier vidé" : "Erreur lors du vidage");
	//            jsonResponse(viderOk ? "ok" : "error", data, out);
	
			} catch (Exception e) {
				sendError(response, out, "Erreur lors du vidage du panier: " + e.getMessage(), 500);
			}
		}
	
	//    private void handleGetMenu(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            JsonObject root = new JsonObject();
	//            JsonArray categoriesJson = new JsonArray();
	//
	//            // 1. Récupérer toutes les catégories parentes
	//            List<MenuCategorie> categories = menuCategorieDAO.getCategoriesParentes();
	//
	//            for (MenuCategorie cat : categories) {
	//
	//                JsonObject catJson = new JsonObject();
	//                catJson.addProperty("id", cat.getId());
	//                catJson.addProperty("nom", cat.getNom());
	//                catJson.addProperty("description", cat.getDescription());
	//
	//                // Image finale
	//                String finalImage = getImagePath(request, "categorie", cat.getImageUrl());
	//                catJson.addProperty("imageUrl", finalImage);
	//
	//                // 2. Récupérer les sous catégories
	//                List<MenuCategorie> sousCats = menuCategorieDAO.getSousCategoriesByParentId(cat.getId());
	//                JsonArray sousCatsJson = new JsonArray();
	//
	//                for (MenuCategorie sous : sousCats) {
	//
	//                    JsonObject sousJson = new JsonObject();
	//                    sousJson.addProperty("id", sous.getId());
	//                    sousJson.addProperty("nom", sous.getNom());
	//
	//                    // Image finale
	//                    String finalSousImg = getImagePath(request, "categorie", sous.getImageUrl());
	//                    sousJson.addProperty("imageUrl", finalSousImg);
	//
	//                    // 3. Récupérer les plats de la sous-catégorie
	//                    List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sous.getId());
	//
	//                    JsonArray platsJson = new JsonArray();
	//                    for (Plat plat : plats) {
	//
	//                        JsonObject platJson = new JsonObject();
	//                        platJson.addProperty("id", plat.getId());
	//                        platJson.addProperty("nom", plat.getNom());
	//                        platJson.addProperty("description", plat.getDescription());
	//                        platJson.addProperty("prix", plat.getPrix());
	//
	//                        // image finale du  plat
	//                        String finalPlatImg = getImagePath(request, "plat", plat.getImage());
	//                        platJson.addProperty("image", finalPlatImg);
	//
	//                        // Produit lié si existe
	//                        if (plat.getProduit() != null) {
	//                            JsonObject prodJson = new JsonObject();
	//                            prodJson.addProperty("id", plat.getProduit().getId());
	//                            prodJson.addProperty("nom", plat.getProduit().getNom());
	//                            prodJson.addProperty("prix", plat.getProduit().getPrixVente());
	//                            prodJson.addProperty("qteEnStock", plat.getProduit().getQteEnStock());
	//
	//                            // image du produit
	//                            String finalProdImg = getImagePath(request, "produit", plat.getProduit().getImageUrl());
	//                            prodJson.addProperty("imageUrl", finalProdImg);
	//
	//                            platJson.add("produit", prodJson);
	//                        }
	//
	//                        platsJson.add(platJson);
	//                    }
	//
	//                    // Ajouter les plats à la sous-catégorie
	//                    sousJson.add("plats", platsJson);
	//                    sousCatsJson.add(sousJson);
	//                }
	//
	//                // Ajouter les sous catégories à la catégorie parente
	//                catJson.add("sousCategories", sousCatsJson);
	//
	//                // Ajouter la catégorie au tableau final
	//                categoriesJson.add(catJson);
	//            }
	//
	//            root.add("categories", categoriesJson);
	//            jsonResponse("ok", root, out);
	//
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors du chargement du menu complet: " + e.getMessage(), 500);
	//        }
	//    }
	
		private void handleGetMenu(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
			try {
				JsonObject root = new JsonObject();
				JsonArray rayonsJson = new JsonArray();
	
				// 1. Récupérer tous les rayons
				RayonDAO rayonDAO = new RayonDAO();
				List<Rayon> rayons = rayonDAO.getAll();
	
				for (Rayon rayon : rayons) {
					JsonObject rayonJson = new JsonObject();
					rayonJson.addProperty("id", rayon.getId());
					rayonJson.addProperty("nom", rayon.getNom());
					rayonJson.addProperty("description", rayon.getDescription());
	
					// Image du rayon
					String finalRayonImage = getImagePath(request, "rayon", rayon.getImageUrl());
					rayonJson.addProperty("imageUrl", finalRayonImage);
	
					JsonArray categoriesJson = new JsonArray();
	
					// 2. Récupérer les catégories parentes de ce rayon
					List<MenuCategorie> categories = menuCategorieDAO.getCategoriesByRayonId(rayon.getId());
	
					for (MenuCategorie cat : categories) {
						JsonObject catJson = new JsonObject();
						catJson.addProperty("id", cat.getId());
						catJson.addProperty("nom", cat.getNom());
						catJson.addProperty("description", cat.getDescription());
	
						// Image de la catégorie
						String finalImage = getImagePath(request, "categorie", cat.getImageUrl());
						catJson.addProperty("imageUrl", finalImage);
	
						// 3. Récupérer les sous-catégories de cette catégorie parente
						List<MenuCategorie> sousCats = menuCategorieDAO.getSousCategoriesByParentId(cat.getId());
						JsonArray sousCatsJson = new JsonArray();
	
						for (MenuCategorie sous : sousCats) {
							JsonObject sousJson = new JsonObject();
							sousJson.addProperty("id", sous.getId());
							sousJson.addProperty("nom", sous.getNom());
							sousJson.addProperty("description", sous.getDescription());
	
							// Image de la sous-catégorie
							String finalSousImg = getImagePath(request, "categorie", sous.getImageUrl());
							sousJson.addProperty("imageUrl", finalSousImg);
	
							// 4. Récupérer les plats de la sous-catégorie
							List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sous.getId());
							JsonArray platsJson = new JsonArray();
	
							for (Plat plat : plats) {
								JsonObject platJson = new JsonObject();
								platJson.addProperty("id", plat.getId());
								platJson.addProperty("nom", plat.getNom());
								platJson.addProperty("description", plat.getDescription());
								platJson.addProperty("prix", plat.getPrix());
								platJson.addProperty("qtePoints", plat.getQtePoints());
	
								// Image du plat
								String finalPlatImg = getImagePath(request, "plat", plat.getImage());
								platJson.addProperty("image", finalPlatImg);
	
								// Produit lié si existe
								if (plat.getProduit() != null) {
									JsonObject prodJson = new JsonObject();
									prodJson.addProperty("id", plat.getProduit().getId());
									prodJson.addProperty("nom", plat.getProduit().getNom());
									prodJson.addProperty("prix", plat.getProduit().getPrixVente());
									prodJson.addProperty("qteEnStock", plat.getProduit().getQteEnStock());
									prodJson.addProperty("qtePoints", plat.getProduit().getQtePoints());
	
									// Image du produit
									String finalProdImg = getImagePath(request, "produit", plat.getProduit().getImageUrl());
									prodJson.addProperty("imageUrl", finalProdImg);
	
									platJson.add("produit", prodJson);
								}
	
								platsJson.add(platJson);
							}
	
							// Ajouter les plats à la sous-catégorie
							sousJson.add("plats", platsJson);
							sousCatsJson.add(sousJson);
						}
	
						// Ajouter les sous-catégories à la catégorie parente
						catJson.add("sousCategories", sousCatsJson);
	
						// Ajouter la catégorie au tableau des catégories du rayon
						categoriesJson.add(catJson);
					}
	
					// Ajouter les catégories au rayon
					rayonJson.add("categories", categoriesJson);
	
					// Ajouter le rayon au tableau final seulement s'il a des catégories
					if (categoriesJson.size() > 0) {
						rayonsJson.add(rayonJson);
					}
				}
	
				// Ajouter également les catégories sans rayon (au cas où)
				JsonArray categoriesSansRayonJson = new JsonArray();
				List<MenuCategorie> categoriesSansRayon = menuCategorieDAO.getCategoriesSansRayon();
	
				for (MenuCategorie cat : categoriesSansRayon) {
					JsonObject catJson = new JsonObject();
					catJson.addProperty("id", cat.getId());
					catJson.addProperty("nom", cat.getNom());
					catJson.addProperty("description", cat.getDescription());
	
					// Image de la catégorie
					String finalImage = getImagePath(request, "categorie", cat.getImageUrl());
					catJson.addProperty("imageUrl", finalImage);
	
					// Récupérer les sous-catégories
					List<MenuCategorie> sousCats = menuCategorieDAO.getSousCategoriesByParentId(cat.getId());
					JsonArray sousCatsJson = new JsonArray();
	
					for (MenuCategorie sous : sousCats) {
						JsonObject sousJson = new JsonObject();
						sousJson.addProperty("id", sous.getId());
						sousJson.addProperty("nom", sous.getNom());
						sousJson.addProperty("description", sous.getDescription());
	
						// Image de la sous-catégorie
						String finalSousImg = getImagePath(request, "categorie", sous.getImageUrl());
						sousJson.addProperty("imageUrl", finalSousImg);
	
						// Récupérer les plats
						List<Plat> plats = menuCategorieDAO.getPlatsBySubCategoryId(sous.getId());
						JsonArray platsJson = new JsonArray();
	
						for (Plat plat : plats) {
							JsonObject platJson = new JsonObject();
							platJson.addProperty("id", plat.getId());
							platJson.addProperty("nom", plat.getNom());
							platJson.addProperty("description", plat.getDescription());
							platJson.addProperty("prix", plat.getPrix());
							platJson.addProperty("qtePoints", plat.getQtePoints());
	
							// Image du plat
							String finalPlatImg = getImagePath(request, "plat", plat.getImage());
							platJson.addProperty("image", finalPlatImg);
	
							// Produit lié si existe
							if (plat.getProduit() != null) {
								JsonObject prodJson = new JsonObject();
								prodJson.addProperty("id", plat.getProduit().getId());
								prodJson.addProperty("nom", plat.getProduit().getNom());
								prodJson.addProperty("prix", plat.getProduit().getPrixVente());
								prodJson.addProperty("qteEnStock", plat.getProduit().getQteEnStock());
								prodJson.addProperty("qtePoints", plat.getProduit().getQtePoints());
	
								// Image du produit
								String finalProdImg = getImagePath(request, "produit", plat.getProduit().getImageUrl());
								prodJson.addProperty("imageUrl", finalProdImg);
	
								platJson.add("produit", prodJson);
							}
	
							platsJson.add(platJson);
						}
	
						sousJson.add("plats", platsJson);
						sousCatsJson.add(sousJson);
					}
	
					catJson.add("sousCategories", sousCatsJson);
					categoriesSansRayonJson.add(catJson);
				}
	
				root.add("rayons", rayonsJson);
	
				// Ajouter les catégories sans rayon séparément
				if (categoriesSansRayonJson.size() > 0) {
					root.add("categoriesSansRayon", categoriesSansRayonJson);
				}
	
				jsonResponse("ok", root, out);
	
			} catch (Exception e) {
				e.printStackTrace();
				sendError(response, out, "Erreur lors du chargement du menu complet: " + e.getMessage(), 500);
			}
		}
	
	//    @ByYlth
		private void handleListeChambres(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
    try {
    	System.out.println("=== DÉBUT TRAITEMENT CHAMBRES ===");
        // Récupérer toutes les chambres AVEC leurs réservations
        List<Chambre> chambres = chambreDAO.listerChambres();
        
        if (chambres == null) {
            chambres = new ArrayList<>();
        }

        // Construire la réponse JSON
        JsonArray chambresArray = new JsonArray();
        String contextPath = request.getContextPath();

        for (Chambre chambre : chambres) {
            JsonObject chambreJson = new JsonObject();
            chambreJson.addProperty("id", chambre.getId());
            chambreJson.addProperty("nomChambre", chambre.getNomChambre());
            chambreJson.addProperty("descriptionChambre", chambre.getDescriptionChambre());
            chambreJson.addProperty("capacite", chambre.getCapacite());
            chambreJson.addProperty("prixMoment", chambre.getPrixMoment());
            chambreJson.addProperty("prixNuit", chambre.getPrixNuit());
            chambreJson.addProperty("prixJour", chambre.getPrixJour());
            chambreJson.addProperty("prixSejour", chambre.getPrixSejour());
            chambreJson.addProperty("disponible", chambre.isDisponible());
            chambreJson.addProperty("utilisateurId", chambre.getUtilisateurId());
            chambreJson.addProperty("statut", chambre.getStatut());

            // Gérer les installations
            JsonArray installationsArray = new JsonArray();
            List<String> installationsList = chambre.getInstallationsList();
            if (installationsList != null && !installationsList.isEmpty()) {
                for (String installation : installationsList) {
                    if (!installation.trim().isEmpty()) {
                        installationsArray.add(installation);
                    }
                }
            }
            chambreJson.add("installations", installationsArray);

            // Gérer les médias (images)
            JsonArray mediasArray = new JsonArray();
            String mediaUrls = chambre.getMedia();

            if (mediaUrls != null && !mediaUrls.isEmpty()) {
                String[] mediaArray = mediaUrls.split(",");

                for (String mediaUrl : mediaArray) {
                    mediaUrl = mediaUrl.trim();
                    if (mediaUrl.isEmpty())
                        continue;

                    String finalImagePath = getImagePath(request, "chambres", mediaUrl);
                    mediasArray.add(finalImagePath);
                }
            } else {
                // Image par défaut
                mediasArray.add(contextPath + "/images/default/chambre-default.png");
            }

            chambreJson.add("medias", mediasArray);

            // AJOUTER LES PÉRIODES DE RÉSERVATION PAR TYPE
            // Moments réservés
            JsonArray momentsArray = new JsonArray();
            List<String> momentsReserves = chambre.getMomentsReserves();
            if (momentsReserves != null && !momentsReserves.isEmpty()) {
                for (String moment : momentsReserves) {
                    JsonObject momentJson = new JsonObject();
                    momentJson.addProperty("periode", moment);
                    momentJson.addProperty("type", "moment");
                    momentsArray.add(momentJson);
                }
            }
            chambreJson.add("momentsReserves", momentsArray);

            // Nuits réservées
            JsonArray nuitsArray = new JsonArray();
            List<String> nuitsReserves = chambre.getNuitsReserves();
            if (nuitsReserves != null && !nuitsReserves.isEmpty()) {
                for (String nuit : nuitsReserves) {
                    JsonObject nuitJson = new JsonObject();
                    nuitJson.addProperty("date", nuit);
                    nuitJson.addProperty("type", "nuit");
                    nuitsArray.add(nuitJson);
                }
            }
            chambreJson.add("nuitsReservees", nuitsArray);

            // Jours réservés
            JsonArray joursArray = new JsonArray();
            List<String> joursReserves = chambre.getJoursReserves();
            if (joursReserves != null && !joursReserves.isEmpty()) {
                for (String jour : joursReserves) {
                    JsonObject jourJson = new JsonObject();
                    jourJson.addProperty("date", jour);
                    jourJson.addProperty("type", "jour");
                    joursArray.add(jourJson);
                }
            }
            chambreJson.add("joursReserves", joursArray);

            // Séjours réservés
            JsonArray sejoursArray = new JsonArray();
            List<String> sejoursReserves = chambre.getSejoursReserves();
            if (sejoursReserves != null && !sejoursReserves.isEmpty()) {
                for (String sejour : sejoursReserves) {
                    JsonObject sejourJson = new JsonObject();
                    sejourJson.addProperty("periode", sejour);
                    sejourJson.addProperty("type", "sejour");
                    sejoursArray.add(sejourJson);
                }
            }
            chambreJson.add("sejoursReserves", sejoursArray);

            // Résumé des réservations (pour affichage rapide)
            JsonObject resumeReservations = new JsonObject();
            resumeReservations.addProperty("totalReservations", 
                momentsReserves.size() + nuitsReserves.size() + joursReserves.size() + sejoursReserves.size());
            resumeReservations.addProperty("moments", momentsReserves.size());
            resumeReservations.addProperty("nuits", nuitsReserves.size());
            resumeReservations.addProperty("jours", joursReserves.size());
            resumeReservations.addProperty("sejours", sejoursReserves.size());
            
            // Dernière réservation si disponible
            String derniereReservation = "";
            if (!sejoursReserves.isEmpty()) {
                derniereReservation = "Séjour: " + sejoursReserves.get(sejoursReserves.size() - 1);
            } else if (!joursReserves.isEmpty()) {
                derniereReservation = "Jour: " + joursReserves.get(joursReserves.size() - 1);
            } else if (!nuitsReserves.isEmpty()) {
                derniereReservation = "Nuit: " + nuitsReserves.get(nuitsReserves.size() - 1);
            } else if (!momentsReserves.isEmpty()) {
                derniereReservation = "Moment: " + momentsReserves.get(momentsReserves.size() - 1);
            }
            resumeReservations.addProperty("derniereReservation", derniereReservation);
            
            chambreJson.add("resumeReservations", resumeReservations);

            chambresArray.add(chambreJson);
        }

        // Préparer la réponse finale
        JsonObject responseData = new JsonObject();
        responseData.addProperty("total", chambres.size());
        responseData.add("chambres", chambresArray);

        jsonResponse("ok", responseData, out);

    } catch (Exception e) {
        e.printStackTrace();
        sendError(response, out, "Erreur lors de la récupération des chambres: " + e.getMessage(), 500);
    }
}
	
	//	lister par user
		private void handleListeReservationsUtilisateur(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			try {
				// Récupérer l'ID utilisateur depuis les paramètres ou la session
				String utilisateurIdStr = request.getParameter("utilisateurId");
				Integer utilisateurId = null;
	
				if (utilisateurIdStr != null && !utilisateurIdStr.trim().isEmpty()) {
					utilisateurId = Integer.parseInt(utilisateurIdStr);
				} else {
					// Utiliser l'utilisateur connecté
					HttpSession session = request.getSession(false);
					if (session != null && session.getAttribute("userId") != null) {
						utilisateurId = (Integer) session.getAttribute("userId");
					}
				}
	
				if (utilisateurId == null) {
					sendError(response, out, "ID utilisateur requis", 400);
					return;
				}
	
				// Récupérer le filtre statut optionnel
				String statut = request.getParameter("statut");
	
				ReservationDAO reservationDAO = new ReservationDAO();
				List<Reservation> reservations;
	
				if (statut != null && !statut.trim().isEmpty()) {
					reservations = reservationDAO.listerParUtilisateurEtStatut(utilisateurId, statut);
				} else {
					reservations = reservationDAO.listerParUtilisateur(utilisateurId);
				}
	
				if (reservations == null) {
					reservations = new ArrayList<>();
				}
	
				// Construire la réponse JSON
				JsonArray reservationsArray = new JsonArray();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
				for (Reservation reservation : reservations) {
					JsonObject reservationJson = new JsonObject();
					reservationJson.addProperty("id", reservation.getId());
					reservationJson.addProperty("roomId", reservation.getRoomId());
					reservationJson.addProperty("start", dateFormat.format(reservation.getStart()));
					reservationJson.addProperty("end", dateFormat.format(reservation.getEnd()));
					reservationJson.addProperty("type", reservation.getType());
					reservationJson.addProperty("status", reservation.getStatus());
					reservationJson.addProperty("title", reservation.getTitle());
					reservationJson.addProperty("prixTotal", reservation.getPrixTotal());
					reservationJson.addProperty("userId", reservation.getUtilisateurId());
	
					// Champs optionnels
					if (reservation.getArrivalTime() != null) {
						reservationJson.addProperty("arrivalTime", reservation.getArrivalTime());
					}
					if (reservation.getNumberOfNights() != null) {
						reservationJson.addProperty("numberOfNights", reservation.getNumberOfNights());
					}
					if (reservation.getNumberOfSlots() != null) {
						reservationJson.addProperty("numberOfSlots", reservation.getNumberOfSlots());
					}
					if (reservation.getDurationHours() != null) {
						reservationJson.addProperty("durationHours", reservation.getDurationHours());
					}
	
					reservationsArray.add(reservationJson);
				}
	
				// Préparer la réponse finale
				JsonObject responseData = new JsonObject();
				responseData.addProperty("total", reservations.size());
				responseData.addProperty("utilisateurId", utilisateurId);
				responseData.add("reservations", reservationsArray);
	
				jsonResponse("ok", responseData, out);
	
			} catch (NumberFormatException e) {
				sendError(response, out, "ID utilisateur invalide", 400);
			} catch (Exception e) {
				e.printStackTrace();
				sendError(response, out, "Erreur lors de la récupération des réservations: " + e.getMessage(), 500);
			}
		}
	
		private void handlelistCommandesByIdClient(HttpServletRequest request, HttpServletResponse response,
				PrintWriter out) {
			String userIdStr = request.getParameter("userId");
			if (userIdStr == null) {
				sendError(response, out, "Paramètre userId manquant", 400);
				return;
			}
	
			String dateDebutStr = request.getParameter("dateDebut");
			String dateFinStr = request.getParameter("dateFin");
	
			Timestamp dateDebut = null;
			Timestamp dateFin = null;
	
			try {
				int userId = Integer.parseInt(userIdStr);
	
				// Si aucune date n'est précisée, définir la période des 30 derniers jours
				if ((dateDebutStr == null || dateDebutStr.isEmpty()) && (dateFinStr == null || dateFinStr.isEmpty())) {
	
					// Date de fin = maintenant
					dateFin = new Timestamp(System.currentTimeMillis());
	
					// Date de début = il y a 30 jours
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_YEAR, -30);
					dateDebut = new Timestamp(calendar.getTimeInMillis());
	
				} else {
					// Traitement normal des dates si elles sont fournies
					if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
						dateDebut = Timestamp.valueOf(dateDebutStr);
					}
	
					if (dateFinStr != null && !dateFinStr.isEmpty()) {
						dateFin = Timestamp.valueOf(dateFinStr);
					}
				}
	
				List<Commande> commandes = commandeDAO.getAllCommandesForUserId(userId, dateDebut, dateFin);
	
				for (Commande commande : commandes) {
					List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commande.getId());
					commande.setDetails(details);
				}
	
				jsonResponse("ok", gson.toJsonTree(commandes), out);
			} catch (Exception e) {
				sendError(response, out, "Erreur: " + e.getMessage(), 500);
			}
		}
	
	//	------------------------------------------------------------------------------------------
	//	@reservastions by @me
	//	------------------------------------------------------------------------------------------
//		private void handleAddReservation(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
//				throws ParseException {
//			try {
//				// Lire le corps de la requête JSON
//				StringBuilder sb = new StringBuilder();
//				String line;
//				while ((line = request.getReader().readLine()) != null) {
//					sb.append(line);
//				}
//				String jsonBody = sb.toString();
//	
//				// Parser le JSON
//				JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
//	
//				Reservation reservation = new Reservation();
//				reservation.setRoomId(jsonObject.get("roomId").getAsString());
//	
//				// Parser les dates avec SimpleDateFormat
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	
//				String startStr = jsonObject.get("start").getAsString();
//				String endStr = jsonObject.get("end").getAsString();
//	
//				reservation.setStart(dateFormat.parse(startStr));
//				reservation.setEnd(dateFormat.parse(endStr));
//	
//				reservation.setType(jsonObject.get("type").getAsString());
//				reservation.setStatus(jsonObject.get("status").getAsString());
//				reservation.setTitle(jsonObject.get("title").getAsString());
//				reservation.setPrixTotal(new BigDecimal(jsonObject.get("prixTotal").getAsString()));
//	
//				// Récupérer l'ID utilisateur depuis le JSON ou la session
//				if (jsonObject.has("userId")) {
//					reservation.setUtilisateurId(jsonObject.get("userId").getAsInt());
//				} else {
//	//				HttpSession session = request.getSession(false);
//	//				if (session != null && session.getAttribute("userId") != null) {
//	//					reservation.setUtilisateurId((Integer) session.getAttribute("userId"));
//	//				} else {
//						sendError(response, out, "ID utilisateur requis", 400);
//						return;
//	//				}
//				}
//	
//				// Champs optionnels
//				if (jsonObject.has("arrivalTime")) {
//					reservation.setArrivalTime(jsonObject.get("arrivalTime").getAsString());
//				}
//				if (jsonObject.has("numberOfNights")) {
//					reservation.setNumberOfNights(jsonObject.get("numberOfNights").getAsInt());
//				}
//				if (jsonObject.has("numberOfSlots")) {
//					reservation.setNumberOfSlots(jsonObject.get("numberOfSlots").getAsInt());
//				}
//				if (jsonObject.has("durationHours")) {
//					reservation.setDurationHours(jsonObject.get("durationHours").getAsInt());
//				}
//	
//				ReservationDAO reservationDAO = new ReservationDAO();
//	
//				// Vérifier les conflits
//				if (reservationDAO.hasConflict(reservation.getRoomId(), reservation.getStart(), reservation.getEnd(),
//						null)) {
//					sendError(response, out, "Conflit de réservation : la chambre n'est pas disponible pour cette période.",
//							409);
//					return;
//				}
//	
//				boolean success = reservationDAO.ajouterReservation(reservation);
//	
//				if (success) {
//					// 🔔 Création d'une notification
//	//			    Notification notif = new Notification();
//	//			    notif.setUserId(reservation.getUtilisateurId());
//	//			    notif.setMessage("Nouvelle réservation créée pour la chambre " + reservation.getRoomId());
//	//			    notif.setType("RESERVATION");
//	//			    notif.setDateCreation(new Date());
//	//
//	//			    NotificationDAO notificationDAO = new NotificationDAO();
//	//			    notificationDAO.insert(notif);
//				    
//				    Notification notif = new Notification();
//				    notif.setGeneratedBy("SYSTEM");
//				    notif.setToUser(reservation.getUtilisateurId());
//				    notif.setMessages("Nouvelle réservation créée pour la chambre : "   +  reservation.getRoomId());
//				    notif.setTypeNotif("RESERVATION");
//				    notif.setStatus("VISIBLE");
//	
//				    NotificationDAO notifDAO = new NotificationDAO();
//				    notifDAO.ajouterNotification(notif);
//				    
//					JsonObject responseData = new JsonObject();
//	//	            responseData.addProperty("id", reservation.getId());
//					responseData.addProperty("message", "Réservation créée avec succès");
//					responseData.addProperty("userId", reservation.getUtilisateurId());
//					jsonResponse("ok", responseData, out);
//				} else {
//					sendError(response, out, "Erreur lors de la création de la réservation", 500);
//				}
//	
//			} catch (Exception e) {
//				e.printStackTrace();
//				sendError(response, out, "Erreur lors de la création de la réservation: " + e.getMessage(), 500);
//			}
//		}
		
//		private void handleAddReservation(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
//		        throws ParseException {
//		    try {
//		        // Lire le corps de la requête JSON
//		        StringBuilder sb = new StringBuilder();
//		        String line;
//		        while ((line = request.getReader().readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String jsonBody = sb.toString();
//
//		        // Parser le JSON
//		        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
//
//		        Reservation reservation = new Reservation();
//		        reservation.setRoomId(jsonObject.get("roomId").getAsString());
//
//		        // Parser les dates avec SimpleDateFormat
//		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		        String startStr = jsonObject.get("start").getAsString();
//		        String endStr = jsonObject.get("end").getAsString();
//
//		        reservation.setStart(dateFormat.parse(startStr));
//		        reservation.setEnd(dateFormat.parse(endStr));
//
//		        reservation.setType(jsonObject.get("type").getAsString());
//		        reservation.setStatus(jsonObject.get("status").getAsString());
//		        reservation.setTitle(jsonObject.get("title").getAsString());
//		        reservation.setPrixTotal(new BigDecimal(jsonObject.get("prixTotal").getAsString()));
//
//		        // Récupérer l'ID utilisateur depuis le JSON ou la session
//		        if (jsonObject.has("userId")) {
//		            reservation.setUtilisateurId(jsonObject.get("userId").getAsInt());
//		        } else {
//		            sendError(response, out, "ID utilisateur requis", 400);
//		            return;
//		        }
//
//		        // Champs optionnels
//		        if (jsonObject.has("arrivalTime")) {
//		            reservation.setArrivalTime(jsonObject.get("arrivalTime").getAsString());
//		        }
//		        
//		        // CHANGEMENT ICI : gérer numberOfDays et numberOfNights
//		        if (jsonObject.has("numberOfDays")) {
//		            // Pour les séjours, on utilise numberOfDays
//		            reservation.setNumberOfNights(jsonObject.get("numberOfDays").getAsInt());
//		        } else if (jsonObject.has("numberOfNights")) {
//		            // Pour la nuit unique, on garde numberOfNights
//		            reservation.setNumberOfNights(jsonObject.get("numberOfNights").getAsInt());
//		        }
//		        
//		        if (jsonObject.has("numberOfSlots")) {
//		            reservation.setNumberOfSlots(jsonObject.get("numberOfSlots").getAsInt());
//		        }
//		        if (jsonObject.has("durationHours")) {
//		            reservation.setDurationHours(jsonObject.get("durationHours").getAsInt());
//		        }
//		        
//		        // NOUVEAUX CHAMPS DE PAIEMENT
//		        if (jsonObject.has("paymentMethod")) {
//		            reservation.setPaymentMethod(jsonObject.get("paymentMethod").getAsString());
//		        }
//		        if (jsonObject.has("payerName")) {
//		            reservation.setPayerName(jsonObject.get("payerName").getAsString());
//		        }
//		        if (jsonObject.has("payerPhone")) {
//		            reservation.setPayerPhone(jsonObject.get("payerPhone").getAsString());
//		        }
//		        if (jsonObject.has("transactionId")) {
//		            reservation.setTransactionId(jsonObject.get("transactionId").getAsString());
//		        }
//		        if (jsonObject.has("paymentNote")) {
//		            reservation.setPaymentNote(jsonObject.get("paymentNote").getAsString());
//		        }
//		        if (jsonObject.has("paymentStatus")) {
//		            reservation.setPaymentStatus(jsonObject.get("paymentStatus").getAsString());
//		        } else {
//		            reservation.setPaymentStatus("pending"); // Valeur par défaut
//		        }
//
//		        ReservationDAO reservationDAO = new ReservationDAO();
//
//		        // Vérifier les conflits
//		        if (reservationDAO.hasConflict(reservation.getRoomId(), reservation.getStart(), reservation.getEnd(),
//		                null)) {
//		            sendError(response, out, "Conflit de réservation : la chambre n'est pas disponible pour cette période.",
//		                    409);
//		            return;
//		        }
//
//		        boolean success = reservationDAO.ajouterReservation(reservation);
//
//		        if (success) {
//		            // 🔔 Création d'une notification
//		            Notification notif = new Notification();
//		            notif.setGeneratedBy("SYSTEM");
//		            notif.setToUser(reservation.getUtilisateurId());
//		            notif.setMessages("Nouvelle réservation créée pour la chambre : " + reservation.getRoomId());
//		            notif.setTypeNotif("RESERVATION");
//		            notif.setStatus("VISIBLE");
//
//		            NotificationDAO notifDAO = new NotificationDAO();
//		            notifDAO.ajouterNotification(notif);
//		            
//		            // NOUVEAU : Si paiement avec SOLDE, déduire du solde de l'utilisateur
//		            if ("SOLDE".equals(reservation.getPaymentMethod())) {
//		                // TODO: Implémenter la déduction du solde
//		                // UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
//		                // utilisateurDAO.deduireSolde(reservation.getUtilisateurId(), reservation.getPrixTotal());
//		                
//		                // Mettre à jour le statut de paiement à "completed"
//		                reservation.setPaymentStatus("completed");
//		                // Optionnellement, mettre à jour la réservation dans la base de données
//		            }
//		            
//		            JsonObject responseData = new JsonObject();
//		            responseData.addProperty("message", "Réservation créée avec succès");
//		            responseData.addProperty("userId", reservation.getUtilisateurId());
//		            responseData.addProperty("reservationId", reservation.getId());
//		            responseData.addProperty("paymentMethod", reservation.getPaymentMethod());
//		            responseData.addProperty("paymentStatus", reservation.getPaymentStatus());
//		            jsonResponse("ok", responseData, out);
//		        } else {
//		            sendError(response, out, "Erreur lors de la création de la réservation", 500);
//		        }
//
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		        sendError(response, out, "Erreur lors de la création de la réservation: " + e.getMessage(), 500);
//		    }
//		}
//	
		
//		private void handleAddReservation(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//		    try {
//		        // 1️⃣ Lire le corps de la requête
//		        StringBuilder sb = new StringBuilder();
//		        BufferedReader reader = request.getReader();
//		        String line;
//		        while ((line = reader.readLine()) != null) {
//		            sb.append(line);
//		        }
//		        String body = sb.toString();
//
//		        if (body.trim().isEmpty()) {
//		            sendError(response, out, "Corps de requête vide", 400);
//		            return;
//		        }
//
//		        // 2️⃣ Parser le JSON
//		        JsonObject jsonBody;
//		        try {
//		            jsonBody = gson.fromJson(body, JsonObject.class);
//		        } catch (Exception e) {
//		            sendError(response, out, "JSON invalide", 400);
//		            return;
//		        }
//
//		        // 🔹 Récupérer l'action
//		        String action = "";
//		        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
//		            action = jsonBody.get("action").getAsString().trim();
//		        }
//
//		        // 3️⃣ Vérifier les paramètres obligatoires
//		        if (!jsonBody.has("roomId") || jsonBody.get("roomId").isJsonNull()) {
//		            sendError(response, out, "Paramètre roomId manquant", 400);
//		            return;
//		        }
//		        if (!jsonBody.has("userId") || jsonBody.get("userId").isJsonNull()) {
//		            sendError(response, out, "Paramètre userId manquant", 400);
//		            return;
//		        }
//		        if (!jsonBody.has("start") || jsonBody.get("start").isJsonNull()) {
//		            sendError(response, out, "Paramètre start manquant", 400);
//		            return;
//		        }
//		        if (!jsonBody.has("end") || jsonBody.get("end").isJsonNull()) {
//		            sendError(response, out, "Paramètre end manquant", 400);
//		            return;
//		        }
//		        if (!jsonBody.has("type") || jsonBody.get("type").isJsonNull()) {
//		            sendError(response, out, "Paramètre type manquant", 400);
//		            return;
//		        }
//		        if (!jsonBody.has("prixTotal") || jsonBody.get("prixTotal").isJsonNull()) {
//		            sendError(response, out, "Paramètre prixTotal manquant", 400);
//		            return;
//		        }
//
//		        // 4️⃣ Récupérer les paramètres de base
//		        String roomId = jsonBody.get("roomId").getAsString();
//		        int userId = jsonBody.get("userId").getAsInt();
//		        String startStr = jsonBody.get("start").getAsString();
//		        String endStr = jsonBody.get("end").getAsString();
//		        String type = jsonBody.get("type").getAsString();
//		        BigDecimal prixTotal = jsonBody.get("prixTotal").getAsBigDecimal();
//
//		        // 5️⃣ Convertir les dates
//		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		        Date startDate, endDate;
//		        try {
//		            startDate = dateFormat.parse(startStr);
//		            endDate = dateFormat.parse(endStr);
//		        } catch (Exception e) {
//		            sendError(response, out, "Format de date invalide. Utiliser yyyy-MM-dd HH:mm:ss", 400);
//		            return;
//		        }
//
//		        // 6️⃣ Vérifier les conflits de réservation
//		        ReservationDAO reservationDAO = new ReservationDAO();
//		        if (reservationDAO.hasConflict(roomId, startDate, endDate, null)) {
//		            sendError(response, out, "La chambre n'est pas disponible pour cette période", 409);
//		            return;
//		        }
//
//		        // 7️⃣ Vérifier si l'utilisateur a déjà une réservation en conflit
//		        if (reservationDAO.hasConflictAvecUtilisateur(roomId, startDate, endDate, null, userId)) {
//		            sendError(response, out, "Vous avez déjà une réservation en conflit avec cette période", 409);
//		            return;
//		        }
//
//		        // 8️⃣ Construire l'objet réservation
//		        Reservation reservation = new Reservation();
//		        reservation.setRoomId(roomId);
//		        reservation.setStart(startDate);
//		        reservation.setEnd(endDate);
//		        reservation.setType(type);
//		        reservation.setStatus("en cours");
//		        reservation.setPrixTotal(prixTotal);
//		        reservation.setUtilisateurId(userId);
//
//		        // Titre par défaut
//		        String title = "Réservation " + type;
//		        if (jsonBody.has("title") && !jsonBody.get("title").isJsonNull()) {
//		            title = jsonBody.get("title").getAsString();
//		        }
//		        reservation.setTitle(title);
//
//		        // Informations supplémentaires
//		        if (jsonBody.has("arrivalTime") && !jsonBody.get("arrivalTime").isJsonNull()) {
//		            reservation.setArrivalTime(jsonBody.get("arrivalTime").getAsString());
//		        }
//		        if (jsonBody.has("numberOfDays") && !jsonBody.get("numberOfDays").isJsonNull()) {
//		            reservation.setNumberOfNights(jsonBody.get("numberOfDays").getAsInt());
//		        }
//		        if (jsonBody.has("numberOfSlots") && !jsonBody.get("numberOfSlots").isJsonNull()) {
//		            reservation.setNumberOfSlots(jsonBody.get("numberOfSlots").getAsInt());
//		        }
//		        if (jsonBody.has("durationHours") && !jsonBody.get("durationHours").isJsonNull()) {
//		            reservation.setDurationHours(jsonBody.get("durationHours").getAsInt());
//		        }
//
//		        // 9️⃣ Récupérer les informations de paiement
//		        String paymentMethod = "MONCASH"; // Valeur par défaut
//		        if (jsonBody.has("paymentMethod") && !jsonBody.get("paymentMethod").isJsonNull()) {
//		            paymentMethod = jsonBody.get("paymentMethod").getAsString();
//		        }
//		        reservation.setPaymentMethod(paymentMethod);
//
//		        // Numéro de réservation
//		        String numeroReservation = "";
//		        if (jsonBody.has("numeroReservation") && !jsonBody.get("numeroReservation").isJsonNull()) {
//		            numeroReservation = jsonBody.get("numeroReservation").getAsString();
//		        } else {
//		            numeroReservation = "RES-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
//		                              "-" + System.currentTimeMillis() % 10000;
//		        }
//
//		        // Informations de paiement
//		        if (jsonBody.has("payerName") && !jsonBody.get("payerName").isJsonNull()) {
//		            reservation.setPayerName(jsonBody.get("payerName").getAsString());
//		        }
//		        if (jsonBody.has("payerPhone") && !jsonBody.get("payerPhone").isJsonNull()) {
//		            reservation.setPayerPhone(jsonBody.get("payerPhone").getAsString());
//		        }
//		        if (jsonBody.has("transactionId") && !jsonBody.get("transactionId").isJsonNull()) {
//		            reservation.setTransactionId(jsonBody.get("transactionId").getAsString());
//		        }
//		        if (jsonBody.has("paymentNote") && !jsonBody.get("paymentNote").isJsonNull()) {
//		            reservation.setPaymentNote(jsonBody.get("paymentNote").getAsString());
//		        }
//
//		        // 🔟 Selon l'action
//		        if ("update".equalsIgnoreCase(action)) {
//		            // Vérifier reservationId pour update
//		            if (!jsonBody.has("reservationId") || jsonBody.get("reservationId").isJsonNull()) {
//		                sendError(response, out, "Paramètre reservationId manquant pour update", 400);
//		                return;
//		            }
//		            String reservationId = jsonBody.get("reservationId").getAsString();
//		            reservation.setId(reservationId);
//
//		            // Vérifier les conflits (en excluant la réservation actuelle)
//		            if (reservationDAO.hasConflict(roomId, startDate, endDate, reservationId)) {
//		                sendError(response, out, "La chambre n'est pas disponible pour cette période", 409);
//		                return;
//		            }
//
//		            boolean success = reservationDAO.modifierReservation(reservation);
//		            if (!success) {
//		                sendError(response, out, "Erreur lors de la mise à jour de la réservation", 500);
//		                return;
//		            }
//
//		            JsonObject data = new JsonObject();
//		            data.addProperty("reservationId", reservationId);
//		            data.addProperty("numeroReservation", numeroReservation);
//		            data.addProperty("message", "Réservation mise à jour avec succès");
//		            jsonResponse("ok", data, out);
//
//		        } else {
//		            // Cas par défaut → Ajout
//		            boolean isCredit = jsonBody.has("isCredit") && jsonBody.get("isCredit").getAsInt() == 1;
//		            System.out.println("=== DÉBUT TRAITEMENT RÉSERVATION ===");
//		            System.out.println("Valeur de isCredit: " + isCredit);
//		            System.out.println("Payment method: " + paymentMethod);
//
//		            // Déclarer statutPaiement ici pour qu'il soit accessible dans tout le bloc else
//		            String statutPaiement = "PAYE";
//		            
//		            // 1️⃣ Cas du crédit complet
//		            if (isCredit) {
//		                System.out.println("Mode: Crédit complet");
//
//		                // 🔹 Vérifier le plafond de crédit AVANT insertion
//		                Utilisateur client = utilisateurDAO.findById(userId);
//		                if (client == null) {
//		                    sendError(response, out, "Utilisateur introuvable", 404);
//		                    return;
//		                }
//
//		                // 🔹 Vérifier le compte client
//		                CompteClientDAO compteDAO = new CompteClientDAO();
//		                CompteClient compte = compteDAO.getCompteByClientId(userId);
//		                
//		                if (compte == null) {
//		                    // Créer le compte s'il n'existe pas
//		                    compte = new CompteClient();
//		                    compte.setClientId(userId);
//		                    compte.setSolde(BigDecimal.ZERO);
//		                    compte.setLimiteCredit(BigDecimal.ZERO);
//		                    int compteId = compteDAO.creerCompteClient(compte);
//		                    if (compteId <= 0) {
//		                        sendError(response, out, "Erreur lors de la création du compte", 500);
//		                        return;
//		                    }
//		                    compte.setId(compteId);
//		                }
//
//		                BigDecimal limiteCredit = compte.getLimiteCredit();
//		                BigDecimal soldeActuel = compte.getSolde();
//
//		                System.out.println("Limite crédit: " + limiteCredit);
//		                System.out.println("Solde actuel: " + soldeActuel);
//		                System.out.println("Montant réservation: " + prixTotal);
//
//		                // 🔹 Calculer le crédit déjà utilisé
//		                BigDecimal creditDejaUtilise = BigDecimal.ZERO;
//		                if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
//		                    creditDejaUtilise = soldeActuel.abs();
//		                }
//		                BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
//		                
//		                System.out.println("Crédit déjà utilisé: " + creditDejaUtilise);
//		                System.out.println("Crédit restant disponible: " + creditRestantDisponible);
//
//		                // 🔹 Vérifier si le crédit restant est suffisant
//		                if (creditRestantDisponible.compareTo(prixTotal) < 0) {
//		                    BigDecimal manquant = prixTotal.subtract(creditRestantDisponible);
//		                    sendError(response, out, 
//		                            "Crédit disponible insuffisant.\n" +
//		                            "Limite de crédit: " + String.format("%.2f HTG", limiteCredit) + "\n" +
//		                            "Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise) + "\n" +
//		                            "Crédit disponible: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
//		                            "Montant réservation: " + String.format("%.2f HTG", prixTotal) + "\n" +
//		                            "Il manque: " + String.format("%.2f HTG", manquant), 400);
//		                    return;
//		                }
//
//		                // ✅ Crédit suffisant → insérer la réservation avec statut PARTIEL
//		                reservation.setStatus("confirmé");
//		                reservation.setPaymentStatus("en cours");
//		                statutPaiement = "PARTIEL";
//		                
//		                boolean success = reservationDAO.ajouterReservation(reservation);
//		                if (!success) {
//		                    sendError(response, out, "Erreur lors de l'ajout de la réservation", 500);
//		                    return;
//		                }
//
//		                // 🔹 Récupérer l'ID de la réservation insérée
//		                String reservationId = reservation.getId(); // Assurez-vous que votre DAO retourne l'ID
//		                
//		                // 🔹 Ajouter le crédit
//		                Credit credit = new Credit();
//		                credit.setUtilisateurId(userId);
//		                credit.setCommandeId(0); // Pas de commande associée
//		                credit.setMontantTotal(prixTotal.intValue());
//		                credit.setMontantPaye(0);
//		                credit.setStatut("NON_PAYE");
//
//		                CreditDAO creditDAO = new CreditDAO();
//		                int creditId = creditDAO.ajouterCredit(credit);
//		                if (creditId <= 0) {
//		                    sendError(response, out, "Erreur lors de l'ajout du crédit", 500);
//		                    return;
//		                }
//		                
//		                // 🔹 Mettre à jour le solde du compte
//		                BigDecimal nouveauSolde = soldeActuel.subtract(prixTotal);
//		                boolean updateSolde = compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
//		                
//		                if (!updateSolde) {
//		                    System.err.println("Erreur lors de la mise à jour du solde");
//		                } else {
//		                    System.out.println("Solde mis à jour: " + soldeActuel + " → " + nouveauSolde);
//		                }
//
//		                // 🔔 Notification pour réservation à crédit
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(userId);
//		                notif.setMessages("Nouvelle réservation à crédit #" + numeroReservation + 
//		                                " d'un montant de " + prixTotal + " HTG");
//		                notif.setTypeNotif("RESERVATION_CREDIT");
//		                notif.setStatus("VISIBLE");
//
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//
//		                System.out.println("Notification crédit envoyée à l'utilisateur #" + userId);
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("reservationId", reservationId);
//		                data.addProperty("numeroReservation", numeroReservation);
//		                data.addProperty("message", "Réservation crédit ajoutée avec succès");
//		                jsonResponse("ok", data, out);
//
//		            } else {
//		                // 2️⃣ Cas normal → vérifier le moyen de paiement
//		                System.out.println("Mode: Paiement normal");
//		                
//		                boolean paiementParSolde = "SOLDE".equalsIgnoreCase(paymentMethod);
//		                
//		                // ========== VÉRIFICATION DE SOLDE si paiement par solde ==========
//		                if (paiementParSolde) {
//		                    System.out.println("Paiement par SOLDE détecté");
//		                    
//		                    // Récupérer le compte du client avec limite de crédit
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(userId);
//		                    
//		                    if (compte == null) {
//		                        // Créer le compte s'il n'existe pas
//		                        compte = new CompteClient();
//		                        compte.setClientId(userId);
//		                        compte.setSolde(BigDecimal.ZERO);
//		                        compte.setLimiteCredit(BigDecimal.ZERO);
//		                        int compteId = compteDAO.creerCompteClient(compte);
//		                        if (compteId <= 0) {
//		                            sendError(response, out, "Erreur lors de la création du compte", 500);
//		                            return;
//		                        }
//		                        compte.setId(compteId);
//		                    }
//		                    
//		                    BigDecimal soldeActuel = compte.getSolde();
//		                    BigDecimal limiteCredit = compte.getLimiteCredit();
//		                    
//		                    System.out.println("=== VÉRIFICATION DE CRÉDIT ===");
//		                    System.out.println("Solde actuel: " + String.format("%.2f HTG", soldeActuel));
//		                    System.out.println("Limite crédit: " + String.format("%.2f HTG", limiteCredit));
//		                    System.out.println("Montant réservation: " + String.format("%.2f HTG", prixTotal));
//		                    
//		                    // 🔹 DÉTERMINER LE TYPE DE PAIEMENT
//		                    boolean utiliserCredit = false;
//		                    
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) >= 0) {
//		                        // Cas 1: Solde positif ou nul
//		                        if (soldeActuel.compareTo(prixTotal) >= 0) {
//		                            // Solde suffisant sans utiliser de crédit
//		                            System.out.println("Paiement avec solde uniquement (pas de crédit)");
//		                            utiliserCredit = false;
//		                        } else {
//		                            // Solde positif mais insuffisant, utiliser crédit
//		                            System.out.println("Paiement partiel avec solde + crédit");
//		                            utiliserCredit = true;
//		                            statutPaiement = "PARTIEL";
//		                        }
//		                    } else {
//		                        // Cas 2: Solde déjà négatif (crédit déjà utilisé)
//		                        System.out.println("Solde négatif - utilisation crédit");
//		                        utiliserCredit = true;
//		                        statutPaiement = "PARTIEL";
//		                    }
//		                    
//		                    // 🔹 Calculer le crédit disponible
//		                    BigDecimal creditDejaUtilise = BigDecimal.ZERO;
//		                    if (soldeActuel.compareTo(BigDecimal.ZERO) < 0) {
//		                        creditDejaUtilise = soldeActuel.abs();
//		                    }
//		                    BigDecimal creditRestantDisponible = limiteCredit.subtract(creditDejaUtilise);
//		                    BigDecimal soldeDisponiblePourPayer = creditRestantDisponible;
//		                    
//		                    System.out.println("Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise));
//		                    System.out.println("Crédit disponible: " + String.format("%.2f HTG", creditRestantDisponible));
//		                    
//		                    // 🔹 Vérifier si le crédit restant est suffisant
//		                    if (utiliserCredit && soldeDisponiblePourPayer.compareTo(prixTotal) < 0) {
//		                        BigDecimal manquant = prixTotal.subtract(soldeDisponiblePourPayer);
//		                        sendError(response, out, 
//		                                "Crédit disponible insuffisant.\n" +
//		                                "Solde actuel: " + String.format("%.2f HTG", soldeActuel) + "\n" +
//		                                "Limite de crédit: " + String.format("%.2f HTG", limiteCredit) + "\n" +
//		                                "Crédit déjà utilisé: " + String.format("%.2f HTG", creditDejaUtilise) + "\n" +
//		                                "Crédit disponible: " + String.format("%.2f HTG", creditRestantDisponible) + "\n" +
//		                                "Montant réservation: " + String.format("%.2f HTG", prixTotal) + "\n" +
//		                                "Il manque: " + String.format("%.2f HTG", manquant), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Vérifier que le nouveau solde ne dépasse pas la limite négative
//		                    BigDecimal nouveauSolde = soldeActuel.subtract(prixTotal);
//		                    BigDecimal limiteNegative = limiteCredit.negate();
//		                    
//		                    if (nouveauSolde.compareTo(limiteNegative) < 0) {
//		                        BigDecimal depassement = limiteNegative.subtract(nouveauSolde);
//		                        sendError(response, out, 
//		                                "Limite de crédit dépassée.\n" +
//		                                "Nouveau solde: " + String.format("%.2f HTG", nouveauSolde) + "\n" +
//		                                "Limite négative: " + String.format("%.2f HTG", limiteNegative) + "\n" +
//		                                "Dépassement: " + String.format("%.2f HTG", depassement), 400);
//		                        return;
//		                    }
//		                    
//		                    // 🔹 Définir le statut de paiement de la réservation
//		                    reservation.setPaymentStatus(utiliserCredit ? "pending" : "completed");
//		                    
//		                    // 🔹 Insérer la réservation
//		                    boolean success = reservationDAO.ajouterReservation(reservation);
//		                    if (!success) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la réservation", 500);
//		                        return;
//		                    }
//		                    
//		                    String reservationId = reservation.getId();
//		                    
//		                    // ========== DÉBIT DU COMPTE ==========
//		                    try {
//		                        // Re-récupérer le compte pour être sûr d'avoir les dernières données
//		                        compte = compteDAO.getCompteByClientId(userId);
//		                        
//		                        if (compte != null) {
//		                            // Calculer le nouveau solde
//		                            BigDecimal soldeActuelCompte = compte.getSolde();
//		                            BigDecimal nouveauSoldeCompte = soldeActuelCompte.subtract(prixTotal);
//		                            
//		                            // Mettre à jour le solde dans la base
//		                            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSoldeCompte);
//		                            
//		                            if (updateReussi) {
//		                                System.out.println("=== MISE À JOUR SOLDE ===");
//		                                System.out.println("Solde mis à jour avec succès pour la réservation");
//		                                System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuelCompte));
//		                                System.out.println("Montant réservation: " + String.format("%.2f HTG", prixTotal));
//		                                System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSoldeCompte));
//		                                System.out.println("Utilisation crédit: " + 
//		                                    (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0 ? 
//		                                     "OUI (" + nouveauSoldeCompte.abs() + " HTG)" : "NON"));
//		                                
//		                                // ========== GESTION CRÉDIT ==========
//		                                if (utiliserCredit) {
//		                                    // Si utilisation de crédit, créer un enregistrement dans CREDIT
//		                                    Credit credit = new Credit();
//		                                    credit.setUtilisateurId(userId);
//		                                    credit.setCommandeId(0); // Pas de commande associée
//		                                    credit.setMontantTotal(prixTotal.intValue());
//		                                    
//		                                    // Calculer combien a été payé avec le solde réel
//		                                    BigDecimal montantPayeAvecSolde = BigDecimal.ZERO;
//		                                    if (soldeActuelCompte.compareTo(BigDecimal.ZERO) > 0) {
//		                                        montantPayeAvecSolde = soldeActuelCompte.min(prixTotal);
//		                                    }
//		                                    credit.setMontantPaye(montantPayeAvecSolde.intValue());
//		                                    
//		                                    CreditDAO creditDAO = new CreditDAO();
//		                                    int creditId = creditDAO.ajouterCredit(credit);
//		                                    if (creditId <= 0) {
//		                                        System.err.println("Erreur lors de l'ajout du crédit pour réservation mixte");
//		                                    } else {
//		                                        System.out.println("Crédit créé pour réservation mixte (ID crédit: " + creditId + ")");
//		                                    }
//
//		                                    // 🔔 Notification pour réservation avec crédit
//		                                    Notification creditNotif = new Notification();
//		                                    creditNotif.setGeneratedBy("SYSTEM");
//		                                    creditNotif.setToUser(userId);
//		                                    creditNotif.setMessages("Réservation #" + numeroReservation + 
//		                                                          " payée avec votre crédit disponible");
//		                                    creditNotif.setTypeNotif("RESERVATION_CREDIT");
//		                                    creditNotif.setStatus("VISIBLE");
//
//		                                    NotificationDAO notifDAO = new NotificationDAO();
//		                                    notifDAO.ajouterNotification(creditNotif);
//		                                    
//		                                } else {
//		                                    // ========== ATTRIBUTION DES POINTS ==========
//		                                    // On donne des points seulement si paiement par solde SANS utiliser de crédit
//		                                    // ET seulement si le solde reste positif après paiement
//		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) >= 0) {
//		                                        try {
//		                                            PointManagerDAO pointManagerDAO = new PointManagerDAO();
//		                                            
//		                                            // Créer une commande fictive pour les points
//		                                            Commande commandeFictive = new Commande();
//		                                            commandeFictive.setClientId(userId);
//		                                            commandeFictive.setMontantTotal(prixTotal);
//		                                            commandeFictive.setId(0); // Pas d'ID réel
//		                                            
//		                                            List<CommandeDetail> details = new ArrayList<>();
//		                                            CommandeDetail detail = new CommandeDetail();
//		                                            detail.setPrixUnitaire(prixTotal);
//		                                            detail.setQuantite(1);
//		                                            details.add(detail);
//		                                            
//		                                            Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourCommande(
//		                                                commandeFictive, details, request.getRemoteAddr());
//		                                            
//		                                            if (resultPoints.containsKey("success") && !(Boolean) resultPoints.get("success")) {
//		                                                System.err.println("Erreur lors de l'attribution des points: " + resultPoints.get("error"));
//		                                            } else {
//		                                                int totalPoints = (Integer) resultPoints.get("totalPoints");
//		                                                System.out.println("Points attribués: " + totalPoints);
//		                                                
//		                                                // Notification pour les points
//		                                                Notification pointsNotif = new Notification();
//		                                                pointsNotif.setGeneratedBy("SYSTEM");
//		                                                pointsNotif.setToUser(userId);
//		                                                pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + " points pour votre réservation #" + numeroReservation);
//		                                                pointsNotif.setTypeNotif("POINTS");
//		                                                pointsNotif.setStatus("VISIBLE");
//
//		                                                NotificationDAO notifDAO = new NotificationDAO();
//		                                                notifDAO.ajouterNotification(pointsNotif);
//		                                                
//		                                                System.out.println("Utilisateur #" + userId + " a reçu " + totalPoints + " points");
//		                                            }
//		                                        } catch (Exception e) {
//		                                            System.err.println("Erreur lors de l'attribution des points: " + e.getMessage());
//		                                        }
//		                                    } else {
//		                                        System.out.println("Pas de points attribués - solde négatif après paiement");
//		                                    }
//		                                }
//		                                
//		                                // 🔹 Créer une transaction pour le débit
//		                                try {
//		                                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
//		                                    TransactionCompte transaction = new TransactionCompte();
//		                                    transaction.setCompteClientId(compte.getId());
//		                                    transaction.setMontant(prixTotal);
//		                                    transaction.setSoldeAvant(soldeActuelCompte);
//		                                    transaction.setSoldeApres(nouveauSoldeCompte);
//		                                    
//		                                    String notes = "Paiement réservation #" + numeroReservation;
//		                                    if (nouveauSoldeCompte.compareTo(BigDecimal.ZERO) < 0) {
//		                                        notes += " - Utilisation crédit: " + 
//		                                                String.format("%.2f HTG", nouveauSoldeCompte.abs());
//		                                    } else {
//		                                        notes += " - Paiement avec solde";
//		                                    }
//		                                    transaction.setNotes(notes);
//		                                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
//		                                    transaction.setCaissiereId(userId);
//		                                    transaction.setTypeTransactionId(2); // ID pour "DEPENSE"
//		                                    transaction.setCommandeId(0); // Pas de commande
//		                                    
//		                                    int transactionId = transactionDAO.creerTransaction(transaction);
//		                                    
//		                                    if (transactionId > 0) {
//		                                        System.out.println("Transaction enregistrée #" + transactionId);
//		                                    } else {
//		                                        System.err.println("Erreur lors de l'enregistrement de la transaction");
//		                                    }
//		                                } catch (Exception e) {
//		                                    System.err.println("Erreur création transaction: " + e.getMessage());
//		                                    e.printStackTrace();
//		                                }
//		                                
//		                            } else {
//		                                System.err.println("Erreur lors de la mise à jour du solde pour la réservation");
//		                            }
//		                        }
//		                    } catch (Exception e) {
//		                        System.err.println("Erreur lors du traitement du compte: " + e.getMessage());
//		                        e.printStackTrace();
//		                    }
//		                    
//		                } else {
//		                    // 🔹 Paiement autre que "SOLDE" - insérer la réservation normalement
//		                    reservation.setPaymentStatus("pending");
//		                    statutPaiement = "EN_ATTENTE"; // 🔹 Définir statutPaiement pour les paiements non-SOLDE
//		                    
//		                    if ("CARTE".equalsIgnoreCase(paymentMethod)) {
//		                        reservation.setPaymentStatus("pending_onsite");
//		                    } else if ("MONCASH".equalsIgnoreCase(paymentMethod) || "NATCASH".equalsIgnoreCase(paymentMethod)) {
//		                        reservation.setPaymentStatus("pending_verification");
//		                    }
//		                    
//		                    boolean success = reservationDAO.ajouterReservation(reservation);
//		                    if (!success) {
//		                        sendError(response, out, "Erreur lors de l'ajout de la réservation", 500);
//		                        return;
//		                    }
//		                    
//		                    System.out.println("Paiement par " + paymentMethod + " - pas d'attribution de points");
//		                }
//
//		                String reservationId = reservation.getId();
//		                
//		                // 🔔 Notification pour réservation normale
//		                String messageNotif = "Nouvelle réservation #" + numeroReservation +
//		                                    " montant: " + String.format("%.2f HTG", prixTotal);
//		                
//		                if (paiementParSolde) {
//		                    messageNotif += " - Débité de votre compte";
//		                    if ("PARTIEL".equals(statutPaiement)) {
//		                        messageNotif += " (partiellement avec crédit)";
//		                    }
//		                } else {
//		                    messageNotif += " - Paiement par " + (paymentMethod != null ? paymentMethod : "autre moyen");
//		                }
//		                
//		                Notification notif = new Notification();
//		                notif.setGeneratedBy("SYSTEM");
//		                notif.setToUser(userId);
//		                notif.setMessages(messageNotif);
//		                notif.setTypeNotif("RESERVATION");
//		                notif.setStatus("VISIBLE");
//
//		                NotificationDAO notifDAO = new NotificationDAO();
//		                notifDAO.ajouterNotification(notif);
//
//		                System.out.println("Notification réservation envoyée à l'utilisateur #" + userId);
//
//		                JsonObject data = new JsonObject();
//		                data.addProperty("reservationId", reservationId);
//		                data.addProperty("numeroReservation", numeroReservation);
//		                
//		                // Message de confirmation
//		                String confirmationMessage = "Réservation ajoutée avec succès";
//		                if (paiementParSolde) {
//		                    CompteClientDAO compteDAO = new CompteClientDAO();
//		                    CompteClient compte = compteDAO.getCompteByClientId(userId);
//		                    if (compte != null) {
//		                        BigDecimal ancienSolde = compte.getSolde().add(prixTotal);
//		                        BigDecimal nouveauSolde = compte.getSolde();
//		                        
//		                        confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
//		                        confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
//		                        
//		                        if ("PARTIEL".equals(statutPaiement)) {
//		                            confirmationMessage += "\n⚠️ Cette réservation a utilisé votre limite de crédit";
//		                        }
//		                    }
//		                }
//		                
//		                data.addProperty("message", confirmationMessage);
//		                jsonResponse("ok", data, out);
//		            }
//		        }
//
//		    } catch (Exception e) {
//		        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//		        e.printStackTrace();
//		    }
//		}

		private void handleAddReservation(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
    try {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        
        if (body.trim().isEmpty()) {
            sendError(response, out, "Corps de requête vide", 400);
            return;
        }
        
        JsonObject jsonBody;
        try {
            jsonBody = gson.fromJson(body, JsonObject.class);
        } catch (Exception e) {
            sendError(response, out, "JSON invalide", 400);
            return;
        }
        
        // 🔹 Récupérer l'action
        String action = "";
        if (jsonBody.has("action") && !jsonBody.get("action").isJsonNull()) {
            action = jsonBody.get("action").getAsString().trim();
        }
        
        // 3️⃣ Vérifier les paramètres obligatoires
        String[] requiredFields = {"roomId", "userId", "start", "end", "type", "prixTotal"};
        for (String field : requiredFields) {
            if (!jsonBody.has(field) || jsonBody.get(field).isJsonNull()) {
                sendError(response, out, "Paramètre " + field + " manquant", 400);
                return;
            }
        }
        
        // 4️⃣ Récupérer les paramètres de base
        String roomId = jsonBody.get("roomId").getAsString();
        int userId = jsonBody.get("userId").getAsInt();
        String startStr = jsonBody.get("start").getAsString();
        String endStr = jsonBody.get("end").getAsString();
        String type = jsonBody.get("type").getAsString();
        BigDecimal prixTotal = jsonBody.get("prixTotal").getAsBigDecimal();
        String paymentMethod = jsonBody.has("paymentMethod") && !jsonBody.get("paymentMethod").isJsonNull() 
            ? jsonBody.get("paymentMethod").getAsString() 
            : "MONCASH";
        
        // 5️⃣ Convertir les dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startStr);
            endDate = dateFormat.parse(endStr);
        } catch (Exception e) {
            sendError(response, out, "Format de date invalide. Utiliser yyyy-MM-dd HH:mm:ss", 400);
            return;
        }
        
        // 6️⃣ Vérifier les conflits de réservation
        ReservationDAO reservationDAO = new ReservationDAO();
        if (reservationDAO.hasConflict(roomId, startDate, endDate, null)) {
            sendError(response, out, "La chambre n'est pas disponible pour cette période", 409);
            return;
        }
        
        // 7️⃣ Vérifier si l'utilisateur a déjà une réservation en conflit
        if (reservationDAO.hasConflictAvecUtilisateur(roomId, startDate, endDate, null, userId)) {
            sendError(response, out, "Vous avez déjà une réservation en conflit avec cette période", 409);
            return;
        }
        
        // 8️⃣ Traitement selon l'action (update ou add)
        if ("update".equalsIgnoreCase(action)) {
            handleUpdateReservation(jsonBody, reservationDAO, roomId, startDate, endDate, out, response);
            return;
        }
        
        // 🔹 AJOUT DE NOUVELLE RÉSERVATION
        boolean paiementParSolde = "SOLDE".equalsIgnoreCase(paymentMethod);
        
        // ========== GESTION SOLDE ==========
        if (paiementParSolde) {
            CompteClientDAO compteDAO = new CompteClientDAO();
            CompteClient compte = compteDAO.getCompteByClientId(userId);
            
            if (compte == null) {
                sendError(response, out, "Compte client introuvable", 404);
                return;
            }
            
            BigDecimal soldeActuel = compte.getSolde();
            
            // RÈGLE : Pas de crédit pour les réservations de chambre
            if (soldeActuel.compareTo(prixTotal) < 0) {
                BigDecimal manquant = prixTotal.subtract(soldeActuel);
                sendError(response, out, 
                    "Solde insuffisant pour la réservation.\n" +
                    "Solde disponible: " + String.format("%.2f HTG", soldeActuel) + "\n" +
                    "Montant réservation: " + String.format("%.2f HTG", prixTotal) + "\n" +
                    "Il manque: " + String.format("%.2f HTG", manquant) + "\n" +
                    "Note: Les réservations de chambre ne peuvent pas utiliser le crédit.", 400);
                return;
            }
            
            BigDecimal nouveauSolde = soldeActuel.subtract(prixTotal);
            boolean updateReussi = compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
            
            if (!updateReussi) {
                sendError(response, out, "Erreur lors du débit du solde", 500);
                return;
            }
            
            System.out.println("=== RÉSERVATION CHAMBRE PAR SOLDE ===");
            System.out.println("Client #" + userId);
            System.out.println("Ancien solde: " + String.format("%.2f HTG", soldeActuel));
            System.out.println("Montant réservation: " + String.format("%.2f HTG", prixTotal));
            System.out.println("Nouveau solde: " + String.format("%.2f HTG", nouveauSolde));
        }
        
        // 9️⃣ Construire l'objet réservation
        Reservation reservation = new Reservation();
        reservation.setRoomId(roomId);
        reservation.setStart(startDate);
        reservation.setEnd(endDate);
        reservation.setType(type);
        reservation.setStatus("confirmé");
        reservation.setPrixTotal(prixTotal);
        reservation.setUtilisateurId(userId);
        reservation.setPaymentMethod(paymentMethod);
        
        // Définir le statut de paiement selon le moyen de paiement
        if (paiementParSolde) {
            reservation.setPaymentStatus("completed");
        } else if ("CARTE".equalsIgnoreCase(paymentMethod)) {
            reservation.setPaymentStatus("pending_onsite");
        } else if ("MONCASH".equalsIgnoreCase(paymentMethod) || "NATCASH".equalsIgnoreCase(paymentMethod)) {
            reservation.setPaymentStatus("pending_verification");
        } else {
            reservation.setPaymentStatus("pending");
        }
        
        // Titre par défaut
        String title = "Réservation " + type;
        if (jsonBody.has("title") && !jsonBody.get("title").isJsonNull()) {
            title = jsonBody.get("title").getAsString();
        }
        reservation.setTitle(title);
        
        // Numéro de réservation
        String numeroReservation = "";
        if (jsonBody.has("numeroReservation") && !jsonBody.get("numeroReservation").isJsonNull()) {
            numeroReservation = jsonBody.get("numeroReservation").getAsString();
        } else {
            numeroReservation = "RES-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + 
                              "-" + System.currentTimeMillis() % 10000;
        }
        
        // Informations supplémentaires
        if (jsonBody.has("arrivalTime") && !jsonBody.get("arrivalTime").isJsonNull()) {
            reservation.setArrivalTime(jsonBody.get("arrivalTime").getAsString());
        }
        if (jsonBody.has("numberOfDays") && !jsonBody.get("numberOfDays").isJsonNull()) {
            reservation.setNumberOfNights(jsonBody.get("numberOfDays").getAsInt());
        }
        if (jsonBody.has("numberOfSlots") && !jsonBody.get("numberOfSlots").isJsonNull()) {
            reservation.setNumberOfSlots(jsonBody.get("numberOfSlots").getAsInt());
        }
        if (jsonBody.has("durationHours") && !jsonBody.get("durationHours").isJsonNull()) {
            reservation.setDurationHours(jsonBody.get("durationHours").getAsInt());
        }
        
        // Informations de paiement (pour MONCASH/NATCASH/VIREMENT)
        if (jsonBody.has("payerName") && !jsonBody.get("payerName").isJsonNull()) {
            reservation.setPayerName(jsonBody.get("payerName").getAsString());
        }
        if (jsonBody.has("payerPhone") && !jsonBody.get("payerPhone").isJsonNull()) {
            reservation.setPayerPhone(jsonBody.get("payerPhone").getAsString());
        }
        if (jsonBody.has("transactionId") && !jsonBody.get("transactionId").isJsonNull()) {
            reservation.setTransactionId(jsonBody.get("transactionId").getAsString());
        }
        if (jsonBody.has("paymentNote") && !jsonBody.get("paymentNote").isJsonNull()) {
            reservation.setPaymentNote(jsonBody.get("paymentNote").getAsString());
        }
        
        // 🔟 Ajouter la réservation
//        boolean success =
//        if (!success) {
//            sendError(response, out, "Erreur lors de l'ajout de la réservation", 500);
//            return;
//        }
        String reservationId =  reservationDAO.ajouterReservation(reservation);
        if (reservationId == null || reservationId.trim().isEmpty()) {
            sendError(response, out, "Erreur lors de l'ajout de la réservation - ID non retourné", 500);
            return;
        }
        
//        String reservationId = reservation.getId();
        
        // ========== GESTION TRANSACTION (SOLDE UNIQUEMENT) ==========
        if (paiementParSolde) {
            try {
                CompteClientDAO compteDAO = new CompteClientDAO();
                CompteClient compte = compteDAO.getCompteByClientId(userId);
                
                if (compte != null) {
                    BigDecimal ancienSolde = compte.getSolde().add(prixTotal);
                    BigDecimal nouveauSolde = compte.getSolde();
                    
                    TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
                    TransactionCompte transaction = new TransactionCompte();
                    transaction.setCompteClientId(compte.getId());
                    transaction.setMontant(prixTotal.negate());
                    transaction.setSoldeAvant(ancienSolde);
                    transaction.setSoldeApres(nouveauSolde);
                    transaction.setNotes("Réservation chambre: " + type + " #" + reservationId);
                    transaction.setDateTransaction(new Timestamp(new Date().getTime()));
                    transaction.setCaissiereId(userId);
                    transaction.setTypeTransactionId(8); // ID pour "DEPENSE"
                    transaction.setCommandeId(0);
                    
                    int transactionId = transactionDAO.creerTransaction(transaction);
                    
                    if (transactionId > 0) {
                        System.out.println("Transaction chambre enregistrée #" + transactionId);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur création transaction: " + e.getMessage());
            }
        }
        
     // ========== ATTRIBUTION DES POINTS ==========
     // SOLDE : Points attribués immédiatement
     // MONCASH/NATCASH/VIREMENT : Points attribués après validation
     String confirmationMessage = "Réservation ajoutée avec succès";

     if (paiementParSolde) {
         try {
             System.out.println("=== DÉBUT ATTRIBUTION POINTS CHAMBRE ===");
             System.out.println("User ID: " + userId);
             System.out.println("Reservation ID: " + reservationId);
             System.out.println("Montant: " + prixTotal);
             System.out.println("Payment Method: SOLDE");
             
             PointManagerDAO pointManagerDAO = new PointManagerDAO();
             Map<String, Object> resultPoints = pointManagerDAO.attribuerPointsPourReservationChambre_v2(
                 userId, 
                 Integer.parseInt(reservationId), 
                 prixTotal, 
                 "SOLDE", 
                 request.getRemoteAddr());
             
             // Afficher tout le contenu du résultat pour déboguer
             System.out.println("=== CONTENU RESULT POINTS ===");
             for (Map.Entry<String, Object> entry : resultPoints.entrySet()) {
                 System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
             }
             System.out.println("=============================");
             
             if (resultPoints.containsKey("success")) {
                Boolean  success = (Boolean) resultPoints.get("success");
                 System.out.println("Success flag: " + success);
                 
                 if (success) {
                     int totalPoints = (Integer) resultPoints.get("totalPoints");
                     System.out.println("Points attribués pour chambre: " + totalPoints + " points");
                     
                     PointDAO pointDAO = new PointDAO();
                     PrivilegeNiveau nouveauNiveau = pointDAO.verifierEtMettreAJourNiveau(userId);
                     
                     if (nouveauNiveau != null) {
                         System.out.println("🎉 Utilisateur #" + userId + " a changé de niveau vers: " + nouveauNiveau.getNom());
                     }
                     
                     // Notification pour les points
                     NotificationDAO notifDAO = new NotificationDAO();
                     Notification pointsNotif = new Notification();
                     pointsNotif.setGeneratedBy("SYSTEM");
                     pointsNotif.setToUser(userId);
                     pointsNotif.setMessages("Vous venez de gagner : " + totalPoints + 
                                           " points pour votre réservation de chambre #" + reservationId);
                     pointsNotif.setTypeNotif("POINTS");
                     pointsNotif.setStatus("VISIBLE");
                     notifDAO.ajouterNotification(pointsNotif);
                     
                     confirmationMessage += "\n🎉 Vous avez gagné " + totalPoints + " points de fidélité !";
                     
                 } else {
                     String errorMsg = resultPoints.containsKey("error") ? 
                         (String) resultPoints.get("error") : "Erreur inconnue (pas de message d'erreur)";
                     System.err.println("❌ Erreur attribution points: " + errorMsg);
                     confirmationMessage += "\n⚠️ L'attribution des points a échoué: " + errorMsg + ", mais la réservation est confirmée.";
                 }
             } else {
                 System.err.println("❌ Clé 'success' manquante dans le résultat");
                 confirmationMessage += "\n⚠️ Erreur inattendue dans l'attribution des points, mais la réservation est confirmée.";
             }
             
             System.out.println("=== FIN ATTRIBUTION POINTS CHAMBRE ===");
             
         } catch (NumberFormatException e) {
             System.err.println("❌ Erreur de format de reservationId: " + reservationId);
             System.err.println("Exception: " + e.getMessage());
             e.printStackTrace();
             confirmationMessage += "\n⚠️ Erreur de format (ID réservation), mais la réservation est confirmée.";
         } catch (Exception e) {
             System.err.println("❌ Exception lors de l'attribution des points:");
             System.err.println("Message: " + e.getMessage());
             System.err.println("Cause: " + e.getCause());
             e.printStackTrace();
             confirmationMessage += "\n⚠️ Erreur technique lors de l'attribution des points: " + e.getMessage() + 
                                  ", mais la réservation est confirmée.";
         }
     }
        // Ajouter les informations de solde au message de confirmation
        if (paiementParSolde) {
            CompteClientDAO compteDAO = new CompteClientDAO();
            CompteClient compte = compteDAO.getCompteByClientId(userId);
            if (compte != null) {
                BigDecimal ancienSolde = compte.getSolde().add(prixTotal);
                BigDecimal nouveauSolde = compte.getSolde();
                
                confirmationMessage += "\nAncien solde: " + String.format("%.2f HTG", ancienSolde);
                confirmationMessage += "\nNouveau solde: " + String.format("%.2f HTG", nouveauSolde);
            }
        } else if ("MONCASH".equals(paymentMethod) || "NATCASH".equals(paymentMethod) || "VIREMENT".equals(paymentMethod)) {
            confirmationMessage += "\nVotre réservation est en attente de validation.";
            confirmationMessage += "\nLes points vous seront attribués après validation du paiement.";
        }
        
        // ========== RÉPONSE JSON ==========
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", "ok");
        
        JsonObject data = new JsonObject();
        data.addProperty("reservationId", reservationId);
        data.addProperty("numeroReservation", numeroReservation);
        data.addProperty("message", confirmationMessage);
        data.addProperty("montantTotal", prixTotal.toString());
        data.addProperty("paymentStatus", reservation.getPaymentStatus());
        data.addProperty("paymentMethod", paymentMethod);
        
        responseJson.add("data", data);
        out.print(responseJson.toString());
        
        // ========== NOTIFICATIONS ==========
        NotificationDAO notifDAO = new NotificationDAO();
        
        String messageUser = "Votre réservation de chambre #" + numeroReservation + 
                           " (" + type + ") a été créée. ";
        
        if ("completed".equals(reservation.getPaymentStatus())) {
            messageUser += "Statut: Confirmée";
            if (paiementParSolde) {
                messageUser += " - Points attribués";
            }
        } else {
            messageUser += "Statut: En attente de validation";
        }
        
        Notification notifUser = new Notification();
        notifUser.setGeneratedBy("SYSTEM");
        notifUser.setToUser(userId);
        notifUser.setMessages(messageUser);
        notifUser.setTypeNotif("RESERVATION");
        notifUser.setStatus("VISIBLE");
        notifDAO.ajouterNotification(notifUser);
        
        // Notification admin pour paiements en attente
        if ("MONCASH".equals(paymentMethod) || "NATCASH".equals(paymentMethod) || "VIREMENT".equals(paymentMethod)) {
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            List<Utilisateur> admins = utilisateurDAO.findByRole("ADMINISTRATEUR");
            for (Utilisateur admin : admins) {
                Notification notifAdmin = new Notification();
                notifAdmin.setGeneratedBy("SYSTEM");
                notifAdmin.setToUser(admin.getId());
                notifAdmin.setMessages("Nouvelle réservation chambre #" + reservationId + 
                                     " en attente de validation. Montant: " + prixTotal + " HTG");
                notifAdmin.setTypeNotif("ADMIN_RESERVATION");
                notifAdmin.setStatus("VISIBLE");
                notifDAO.ajouterNotification(notifAdmin);
            }
        }
        
    } catch (Exception e) {
        sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
        e.printStackTrace();
    }
}

		// Méthode pour gérer la mise à jour (extraite pour plus de clarté)
		private void handleUpdateReservation(JsonObject jsonBody, ReservationDAO reservationDAO, 
		                                     String roomId, Date startDate, Date endDate, 
		                                     PrintWriter out, HttpServletResponse response) {
		    // Vérifier reservationId pour update
		    if (!jsonBody.has("reservationId") || jsonBody.get("reservationId").isJsonNull()) {
		        sendError(response, out, "Paramètre reservationId manquant pour update", 400);
		        return;
		    }
		    
		    String reservationId = jsonBody.get("reservationId").getAsString();
		    
		    // Vérifier les conflits (en excluant la réservation actuelle)
		    if (reservationDAO.hasConflict(roomId, startDate, endDate, reservationId)) {
		        sendError(response, out, "La chambre n'est pas disponible pour cette période", 409);
		        return;
		    }
		    
		    // Construire l'objet réservation pour update
		    Reservation reservation = new Reservation();
		    reservation.setId(reservationId);
		    reservation.setRoomId(roomId);
		    reservation.setStart(startDate);
		    reservation.setEnd(endDate);
		    
		    if (jsonBody.has("type") && !jsonBody.get("type").isJsonNull()) {
		        reservation.setType(jsonBody.get("type").getAsString());
		    }
		    if (jsonBody.has("prixTotal") && !jsonBody.get("prixTotal").isJsonNull()) {
		        reservation.setPrixTotal(jsonBody.get("prixTotal").getAsBigDecimal());
		    }
		    if (jsonBody.has("title") && !jsonBody.get("title").isJsonNull()) {
		        reservation.setTitle(jsonBody.get("title").getAsString());
		    }
		    
		    boolean success = reservationDAO.modifierReservation(reservation);
		    if (!success) {
		        sendError(response, out, "Erreur lors de la mise à jour de la réservation", 500);
		        return;
		    }
		    
		    JsonObject data = new JsonObject();
		    data.addProperty("reservationId", reservationId);
		    data.addProperty("message", "Réservation mise à jour avec succès");
		    jsonResponse("ok", data, out);
		}
		// @byme-------------------------------------------------------------------------------------
	
	
	//    -------------------------------promo
	//    private void handleGetPromos(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	//        try {
	//            PromoDAO promoDAO = new PromoDAO();
	//            List<Promo> promos;
	//            
	//            // Vérifier si on veut toutes les promos (admin) ou seulement pour le carrousel
	//            String type = request.getParameter("type");
	//            if ("all".equalsIgnoreCase(type)) {
	//                promos = promoDAO.listerToutesPromos();
	//            } else {
	//                promos = promoDAO.listerPromosPourCarrousel();
	//            }
	//            
	//            // Transformer les chemins de médias en URLs complètes
	//            for (Promo promo : promos) {
	//                String mediaPath = promo.getCheminMedia();
	//                if (mediaPath != null && !mediaPath.isEmpty()) {
	//                    // Déterminez le type de média pour le chemin
	//                    String mediaType = "promo";
	//                    if (promo.getTypeContenu().equalsIgnoreCase("video")) {
	//                        mediaType = "video";
	//                    }
	//                    String finalMediaUrl = getImagePath(request, mediaType, mediaPath);
	//                    promo.setCheminMedia(finalMediaUrl);
	//                }
	//            }
	//            
	//            jsonResponse("ok", gson.toJsonTree(promos), out);
	//            
	//        } catch (Exception e) {
	//            sendError(response, out, "Erreur lors de la récupération des promos: " + e.getMessage(), 500);
	//        }
	//    }
		
		private void handleGetPromos(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        PromoDAO promoDAO = new PromoDAO();
		        List<Promo> promos;
		        
		        // Vérifier si on veut toutes les promos (admin) ou seulement pour le carrousel
		        String type = request.getParameter("type");
		        String statut = request.getParameter("statut");
		        
		        if ("all".equalsIgnoreCase(type)) {
		            promos = promoDAO.listerToutesPromos();
		        } else {
		            promos = promoDAO.listerPromosPourCarrousel();
		        }
		        
		        // Filtrer par statut si spécifié
		        if (statut != null && !statut.trim().isEmpty()) {
		            promos.removeIf(p -> !statut.equalsIgnoreCase(p.getStatut()));
		        }
		        
		        // Transformer les chemins de médias en URLs complètes
		        for (Promo promo : promos) {
		            String mediaPath = promo.getCheminMedia();
		            if (mediaPath != null && !mediaPath.isEmpty()) {
		                // Déterminer le type de média pour le chemin
		                String mediaType = promo.getTypeContenu().equalsIgnoreCase("video") ? "promos/videos" : "promos/images";
		                String finalMediaUrl = getImagePath(request, mediaType, mediaPath);
		                promo.setCheminMedia(finalMediaUrl);
		            }
		        }
		        
		        // Préparer la réponse JSON structurée
		        JsonObject responseData = new JsonObject();
		        responseData.addProperty("total", promos.size());
		        responseData.add("promos", gson.toJsonTree(promos));
		        
		        // Statistiques
		        int totalActives = (int) promos.stream()
		                .filter(p -> "actif".equals(p.getStatut()))
		                .count();
		        int totalVideos = (int) promos.stream()
		                .filter(p -> "video".equalsIgnoreCase(p.getTypeContenu()))
		                .count();
		        int totalImages = (int) promos.stream()
		                .filter(p -> "image".equalsIgnoreCase(p.getTypeContenu()))
		                .count();
		        
		        JsonObject stats = new JsonObject();
		        stats.addProperty("totalActives", totalActives);
		        stats.addProperty("totalVideos", totalVideos);
		        stats.addProperty("totalImages", totalImages);
		        stats.addProperty("totalPromos", promos.size());
		        
		        responseData.add("statistics", stats);
		        
		        jsonResponse("ok", responseData, out);
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur lors de la récupération des promos: " + e.getMessage(), 500);
		    }
		}
		
		
		private void handleGetPromoById(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		    try {
		        String idStr = request.getParameter("id");
		        if (idStr == null) {
		            sendError(response, out, "Paramètre id manquant", 400);
		            return;
		        }
		        
		        int id = Integer.parseInt(idStr);
		        PromoDAO promoDAO = new PromoDAO();
		        Promo promo = promoDAO.chercherParId(id);
		        
		        if (promo == null) {
		            sendError(response, out, "Promo introuvable", 404);
		            return;
		        }
		        
		        // Transformer le chemin média en URL complète
		        String mediaPath = promo.getCheminMedia();
		        if (mediaPath != null && !mediaPath.isEmpty()) {
		            String mediaType = promo.getTypeContenu().equalsIgnoreCase("video") ? "video" : "promo";
		            String finalMediaUrl = getImagePath(request, mediaType, mediaPath);
		            promo.setCheminMedia(finalMediaUrl);
		        }
		        
		        jsonResponse("ok", gson.toJsonTree(promo), out);
		        
		    } catch (NumberFormatException e) {
		        sendError(response, out, "ID invalide", 400);
		    } catch (Exception e) {
		        e.printStackTrace();
		        sendError(response, out, "Erreur lors de la récupération de la promo: " + e.getMessage(), 500);
		    }
		}
		
	
	    private void handleAjouterPromo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            // Lire le corps JSON
	            StringBuilder sb = new StringBuilder();
	            BufferedReader reader = request.getReader();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            String body = sb.toString();
	            
	            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	            
	            // Créer l'objet Promo
	            Promo promo = new Promo();
	            promo.setTitre(json.get("titre").getAsString());
	            promo.setSousTitre(json.get("sousTitre").getAsString());
	            promo.setDescription(json.get("description").getAsString());
	            promo.setTypeContenu(json.get("typeContenu").getAsString());
	            promo.setCheminMedia(json.get("cheminMedia").getAsString());
	            
	            // Gérer les couleurs de gradient
	            JsonArray gradientArray = json.getAsJsonArray("couleursGradient");
	            List<String> couleurs = new ArrayList<>();
	            for (int i = 0; i < gradientArray.size(); i++) {
	                couleurs.add(gradientArray.get(i).getAsString());
	            }
	            promo.setCouleursGradient(couleurs);
	            
	            promo.setTexteBouton(json.get("texteBouton").getAsString());
	            promo.setRouteCible(json.get("routeCible").getAsString());
	            
	            if (json.has("dureeVideo") && !json.get("dureeVideo").isJsonNull()) {
	                promo.setDureeVideo(json.get("dureeVideo").getAsString());
	            }
	            
	            if (json.has("vues") && !json.get("vues").isJsonNull()) {
	                promo.setVues(json.get("vues").getAsString());
	            }
	            
	            promo.setOrdreAffichage(json.get("ordreAffichage").getAsInt());
	            promo.setStatut(json.get("statut").getAsString());
	            
	            // Dates
	            if (json.has("dateDebut") && !json.get("dateDebut").isJsonNull()) {
	                LocalDateTime dateDebut = LocalDateTime.parse(json.get("dateDebut").getAsString());
	                promo.setDateDebut(dateDebut);
	            }
	            
	            if (json.has("dateFin") && !json.get("dateFin").isJsonNull()) {
	                LocalDateTime dateFin = LocalDateTime.parse(json.get("dateFin").getAsString());
	                promo.setDateFin(dateFin);
	            }
	            
	            // Utilisateur (depuis la session ou paramètre)
	            String userIdStr = request.getParameter("userId");
	            if (userIdStr == null) {
	                HttpSession session = request.getSession(false);
	                if (session != null && session.getAttribute("userId") != null) {
	                    promo.setUtilisateurId((Integer) session.getAttribute("userId"));
	                } else {
	                    sendError(response, out, "Utilisateur non identifié", 401);
	                    return;
	                }
	            } else {
	                promo.setUtilisateurId(Integer.parseInt(userIdStr));
	            }
	            
	            // Ajouter dans la base
	            PromoDAO promoDAO = new PromoDAO();
	            int newId = promoDAO.ajouterPromo(promo);
	            
	            if (newId > 0) {
	                JsonObject data = new JsonObject();
	                data.addProperty("promoId", newId);
	                data.addProperty("message", "Promo ajoutée avec succès");
	                jsonResponse("ok", data, out);
	            } else {
	                sendError(response, out, "Erreur lors de l'ajout de la promo", 500);
	            }
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de l'ajout de la promo: " + e.getMessage(), 500);
	        }
	    }
	
	    private void handleModifierPromo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            StringBuilder sb = new StringBuilder();
	            BufferedReader reader = request.getReader();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            String body = sb.toString();
	            
	            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	            
	            Promo promo = new Promo();
	            promo.setId(json.get("id").getAsInt());
	            promo.setTitre(json.get("titre").getAsString());
	            promo.setSousTitre(json.get("sousTitre").getAsString());
	            promo.setDescription(json.get("description").getAsString());
	            promo.setTypeContenu(json.get("typeContenu").getAsString());
	            promo.setCheminMedia(json.get("cheminMedia").getAsString());
	            
	            // Gérer les couleurs de gradient
	            JsonArray gradientArray = json.getAsJsonArray("couleursGradient");
	            List<String> couleurs = new ArrayList<>();
	            for (int i = 0; i < gradientArray.size(); i++) {
	                couleurs.add(gradientArray.get(i).getAsString());
	            }
	            promo.setCouleursGradient(couleurs);
	            
	            promo.setTexteBouton(json.get("texteBouton").getAsString());
	            promo.setRouteCible(json.get("routeCible").getAsString());
	            
	            if (json.has("dureeVideo") && !json.get("dureeVideo").isJsonNull()) {
	                promo.setDureeVideo(json.get("dureeVideo").getAsString());
	            }
	            
	            if (json.has("vues") && !json.get("vues").isJsonNull()) {
	                promo.setVues(json.get("vues").getAsString());
	            }
	            
	            promo.setOrdreAffichage(json.get("ordreAffichage").getAsInt());
	            promo.setStatut(json.get("statut").getAsString());
	            
	            // Dates
	            if (json.has("dateDebut") && !json.get("dateDebut").isJsonNull()) {
	                LocalDateTime dateDebut = LocalDateTime.parse(json.get("dateDebut").getAsString());
	                promo.setDateDebut(dateDebut);
	            }
	            
	            if (json.has("dateFin") && !json.get("dateFin").isJsonNull()) {
	                LocalDateTime dateFin = LocalDateTime.parse(json.get("dateFin").getAsString());
	                promo.setDateFin(dateFin);
	            }
	            
	            // Modifier dans la base
	            PromoDAO promoDAO = new PromoDAO();
	            boolean success = promoDAO.modifierPromo(promo);
	            
	            JsonObject data = new JsonObject();
	            data.addProperty("message", success ? "Promo modifiée avec succès" : "Erreur lors de la modification");
	            jsonResponse(success ? "ok" : "error", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de la modification de la promo: " + e.getMessage(), 500);
	        }
	    }
	
	    private void handleSupprimerPromo(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String idStr = request.getParameter("id");
	            if (idStr == null) {
	                sendError(response, out, "Paramètre id manquant", 400);
	                return;
	            }
	            
	            int id = Integer.parseInt(idStr);
	            PromoDAO promoDAO = new PromoDAO();
	            boolean success = promoDAO.supprimerPromo(id);
	            
	            JsonObject data = new JsonObject();
	            data.addProperty("message", success ? "Promo supprimée avec succès" : "Erreur lors de la suppression");
	            jsonResponse(success ? "ok" : "error", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de la suppression de la promo: " + e.getMessage(), 500);
	        }
	    }
	
	    private void handleReorderPromos(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            StringBuilder sb = new StringBuilder();
	            BufferedReader reader = request.getReader();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            String body = sb.toString();
	            
	            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
	            JsonArray ordersArray = json.getAsJsonArray("orders");
	            
	            PromoDAO promoDAO = new PromoDAO();
	            boolean allSuccess = true;
	            
	            for (int i = 0; i < ordersArray.size(); i++) {
	                JsonObject orderObj = ordersArray.get(i).getAsJsonObject();
	                int promoId = orderObj.get("id").getAsInt();
	                int newOrder = orderObj.get("order").getAsInt();
	                
	                boolean success = promoDAO.mettreAJourOrdreAffichage(promoId, newOrder);
	                if (!success) {
	                    allSuccess = false;
	                }
	            }
	            
	            JsonObject data = new JsonObject();
	            data.addProperty("message", allSuccess ? "Ordre mis à jour avec succès" : "Erreur lors de la mise à jour de l'ordre");
	            jsonResponse(allSuccess ? "ok" : "error", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de la réorganisation des promos: " + e.getMessage(), 500);
	        }
	    }
	
	    // Ajouter cette méthode getImagePath pour les promos
	//    private String getImagePathPromo(HttpServletRequest request, String type, String mediaUrl) {
	//        if (mediaUrl != null && !mediaUrl.isEmpty()) {
	//            switch (type.toLowerCase()) {
	//                case "promo":
	//                case "image":
	//                    if (mediaUrl.startsWith("uploads/promos/")) {
	//                        return request.getContextPath() + "/images/promos/" 
	//                               + mediaUrl.substring("uploads/promos/".length());
	//                    }
	//                    break;
	//                case "video":
	//                    if (mediaUrl.startsWith("uploads/videos/")) {
	//                        return request.getContextPath() + "/videos/" 
	//                               + mediaUrl.substring("uploads/videos/".length());
	//                    }
	//                    break;
	//            }
	//        }
	//        
	//        // Retour par défaut selon le type
	//        switch (type.toLowerCase()) {
	//            case "promo":
	//            case "image":
	//                return "/spot69/images/promos/default.png";
	//            case "video":
	//                return "/spot69/videos/default.mp4";
	//            default:
	//                return "/spot69/images/default.png";
	//        }
	//    }
	    
	    
//	    private void handleRefreshData(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
//	        String userIdStr = request.getParameter("userId");
//	        
//	        if (userIdStr == null) {
//	            sendError(response, out, "Paramètre userId manquant", 400);
//	            return;
//	        }
//	        
//	        try {
//	            int userId = Integer.parseInt(userIdStr);
//	            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
//	            
//	            // Récupère les informations de l'utilisateur
//	            Utilisateur user = utilisateurDAO.getPrivilegeAndPointsOnly(userId);
//	            
//	            if (user == null) {
//	                sendError(response, out, "Utilisateur non trouvé", 404);
//	                return;
//	            }
//	            
//	            // 🔥 APPEL DE TA MÉTHODE
//	            boolean existeNotif = notificationDAO.isThereNotif(userId); // retourne true/false
//	            
//	         // récupérer events
//	            EvenementDAO evenementDAO = new EvenementDAO();
//	            List<Evenement> lastEvents = evenementDAO.getLastTwo();
//	
//	            // construire lastEventsObject (event1,event2)
//	            JsonObject lastEventsObject = new JsonObject();
//	            int idx = 1;
//	            for (Evenement ev : lastEvents) {
//	                JsonObject evJson = new JsonObject();
//	                evJson.addProperty("id", ev.getId());
//	                evJson.addProperty("titre", ev.getTitre());
//	                evJson.addProperty("artiste_groupe", ev.getArtisteGroupe());
//	                evJson.addProperty("date_event", ev.getDateEvent() != null ? ev.getDateEvent().toString() : "");
//	                evJson.addProperty("description", ev.getDescription());
//	                evJson.addProperty("media_image", getImagePath(request, "evenement", ev.getMediaPath()));
//	                lastEventsObject.add("event" + idx, evJson);
//	                idx++;
//	            }
//	
//	            // construire data
//	            JsonObject data = new JsonObject();
//	            data.addProperty("userId", userId);
//	            data.addProperty("privilege", user.getPrivilege());
//	            data.addProperty("point", user.getPoint() != null ? user.getPoint() : 0);
//	            data.add("lastevent", lastEventsObject);
//	            
//	            
//	
//	            // **AJOUT : chaîne JSON prête à logger côté client**
//	            Gson gson = new Gson();
//	            data.addProperty("lastevent_raw", gson.toJson(lastEventsObject)); // stringified version
//	
//	            data.addProperty("isThereNotif", existeNotif ? 1 : 0);
//	            
//	            CompteClientDAO compteDAO = new CompteClientDAO();
//                CompteClient compte = compteDAO.getCompteByClientId(userId);
//                
//
//	            data.addProperty("solde", compte.getSolde());
//	
//	            // envoi : s'assurer du bon content-type
//	            response.setContentType("application/json;charset=UTF-8");
//	            JsonObject responseObj = new JsonObject();
//	            responseObj.addProperty("status", "ok");
//	            responseObj.add("data", data);
//	            out.print(responseObj.toString());
//	
//	            
//	        } catch (NumberFormatException e) {
//	            sendError(response, out, "userId invalide", 400);
//	        } catch (Exception e) {
//	            sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
//	        }
//	    }
	    
	    private void handleRefreshData(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        String userIdStr = request.getParameter("userId");
	        
	        if (userIdStr == null) {
	            sendError(response, out, "Paramètre userId manquant", 400);
	            return;
	        }
	        
	        try {
	            int userId = Integer.parseInt(userIdStr);
	            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
	            
	            // Récupère les informations de l'utilisateur
	            Utilisateur user = utilisateurDAO.getPrivilegeAndPointsOnly(userId);
	            
	            if (user == null) {
	                sendError(response, out, "Utilisateur non trouvé", 404);
	                return;
	            }
	            
	            // 🔥 APPEL DE TA MÉTHODE
	            boolean existeNotif = notificationDAO.isThereNotif(userId); // retourne true/false
	            
	            // récupérer events
	            EvenementDAO evenementDAO = new EvenementDAO();
	            List<Evenement> lastEvents = evenementDAO.getLastTwo();

	            // construire lastEventsObject (event1,event2)
	            JsonObject lastEventsObject = new JsonObject();
	            int idx = 1;
	            for (Evenement ev : lastEvents) {
	                JsonObject evJson = new JsonObject();
	                evJson.addProperty("id", ev.getId());
	                evJson.addProperty("titre", ev.getTitre());
	                evJson.addProperty("artiste_groupe", ev.getArtisteGroupe());
	                evJson.addProperty("date_event", ev.getDateEvent() != null ? ev.getDateEvent().toString() : "");
	                evJson.addProperty("description", ev.getDescription());
	                evJson.addProperty("media_image", getImagePath(request, "evenement", ev.getMediaPath()));
	                lastEventsObject.add("event" + idx, evJson);
	                idx++;
	            }

	            // AJOUT: Récupérer les promos pour le carrousel
	            JsonObject promosMedia = new JsonObject();
	            try {
	                PromoDAO promoDAO = new PromoDAO();
	                List<Promo> promosCarrousel = promoDAO.listerPromosPourCarrousel();
	                
	                JsonArray promosArray = new JsonArray();
	                
	                for (Promo promo : promosCarrousel) {
	                    JsonObject promoJson = new JsonObject();
	                    promoJson.addProperty("id", promo.getId());
	                    promoJson.addProperty("titre", promo.getTitre());
	                    promoJson.addProperty("description", promo.getDescription());
	                    promoJson.addProperty("type_contenu", promo.getTypeContenu());
	                    promoJson.addProperty("date_debut", promo.getDateDebut() != null ? promo.getDateDebut().toString() : "");
	                    promoJson.addProperty("date_fin", promo.getDateFin() != null ? promo.getDateFin().toString() : "");
	                    promoJson.addProperty("statut", promo.getStatut());
	                    promoJson.addProperty("ordre_affichage", promo.getOrdreAffichage());
	                    
	                    // Construire le chemin média complet
	                    String mediaPath = promo.getCheminMedia();
	                    if (mediaPath != null && !mediaPath.isEmpty()) {
	                        // Déterminer le type de média pour le chemin
	                        String mediaType = promo.getTypeContenu().equalsIgnoreCase("video") ? "promos/videos" : "promos/images";
	                        String finalMediaUrl = getImagePath(request, mediaType, mediaPath);
	                        promoJson.addProperty("chemin_media", finalMediaUrl);
	                    } else {
	                        promoJson.addProperty("chemin_media", "");
	                    }
	                    
	                    promosArray.add(promoJson);
	                }
	                
	                promosMedia.add("carrousel", promosArray);
	                
	                // Statistiques supplémentaires
	                int totalActives = (int) promosCarrousel.stream()
	                        .filter(p -> "actif".equals(p.getStatut()))
	                        .count();
	                int totalVideos = (int) promosCarrousel.stream()
	                        .filter(p -> "video".equalsIgnoreCase(p.getTypeContenu()))
	                        .count();
	                int totalImages = (int) promosCarrousel.stream()
	                        .filter(p -> "image".equalsIgnoreCase(p.getTypeContenu()))
	                        .count();
	                
	                promosMedia.addProperty("total_actives", totalActives);
	                promosMedia.addProperty("total_promos", promosCarrousel.size());
	                promosMedia.addProperty("total_videos", totalVideos);
	                promosMedia.addProperty("total_images", totalImages);
	                
	            } catch (Exception e) {
	                // En cas d'erreur, on met un objet vide plutôt que de bloquer toute la réponse
	                promosMedia.add("carrousel", new JsonArray());
	                promosMedia.addProperty("error", "Erreur lors du chargement des promos: " + e.getMessage());
	                promosMedia.addProperty("total_actives", 0);
	                promosMedia.addProperty("total_promos", 0);
	                promosMedia.addProperty("total_videos", 0);
	                promosMedia.addProperty("total_images", 0);
	            }

	            // construire data
	            JsonObject data = new JsonObject();
	            data.addProperty("userId", userId);
	            data.addProperty("privilege", user.getPrivilege());
	            data.addProperty("point", user.getPoint() != null ? user.getPoint() : 0);
	            data.add("lastevent", lastEventsObject);
	            data.add("promosMedia", promosMedia); // AJOUT DES PROMOS
	            
	            // **AJOUT : chaîne JSON prête à logger côté client**
	            Gson gson = new Gson();
	            data.addProperty("lastevent_raw", gson.toJson(lastEventsObject)); // stringified version

	            data.addProperty("isThereNotif", existeNotif ? 1 : 0);
	            
	            CompteClientDAO compteDAO = new CompteClientDAO();
	            CompteClient compte = compteDAO.getCompteByClientId(userId);
	            
	            if (compte != null) {
	                data.addProperty("solde", compte.getSolde());
	                data.addProperty("limiteCredit", compte.getLimiteCredit());
	            } else {
	                data.addProperty("solde", 0.0);
	            }

	            // envoi : s'assurer du bon content-type
	            response.setContentType("application/json;charset=UTF-8");
	            JsonObject responseObj = new JsonObject();
	            responseObj.addProperty("status", "ok");
	            responseObj.add("data", data);
	            out.print(responseObj.toString());

	        } catch (NumberFormatException e) {
	            sendError(response, out, "userId invalide", 400);
	        } catch (Exception e) {
	            sendError(response, out, "Erreur interne: " + e.getMessage(), 500);
	        }
	    }
	
	    private String getImagePath(HttpServletRequest request, String type, String imageUrl) {
	        if (imageUrl != null && !imageUrl.isEmpty()) {
	            switch (type.toLowerCase()) {
	                case "produit":
	                    if (imageUrl.startsWith("uploads/produits/")) {
	                        return request.getContextPath() + "/images/produits/"
	                                + imageUrl.substring("uploads/produits/".length());
	                    }
	                    break;
	                case "plat":
	                    if (imageUrl.startsWith("uploads/plats/")) {
	                        return request.getContextPath() + "/images/plats/" + imageUrl.substring("uploads/plats/".length());
	                    }
	                    break;
	                case "categorie":
	                    if (imageUrl.startsWith("uploads/categories/")) {
	                        return request.getContextPath() + "/images/categories/"
	                                + imageUrl.substring("uploads/categories/".length());
	                    }
	                    break;
	                case "chambres":
	                    if (imageUrl.startsWith("uploads/chambres/")) {
	                        return request.getContextPath() + "/images/chambres/"
	                                + imageUrl.substring("uploads/chambres/".length());
	                    }
	                    break;
	                case "promos/images":
	                    if (imageUrl.startsWith("uploads/promos/images")) {
	                        return request.getContextPath() + "/images/promos/"
	                                + imageUrl.substring("uploads/promos/".length());
	                    }
	                    break;
	                case "promos/videos":
	                    if (imageUrl.startsWith("uploads/promos/videos/")) {
	                        return request.getContextPath() + "/images/promos/videos/"
	                                + imageUrl.substring("uploads/promos/videos/".length());
	                    }
	                case "evenements":
	    				if (imageUrl.startsWith("uploads/evenements/")) {
	    					return request.getContextPath() + "/images/evenements/"
	    							+ imageUrl.substring("uploads/evenements/".length());
	    				}
	    				break;
	                    
	            }
	        }
	
	        // Retour par défaut selon le type
	        switch (type.toLowerCase()) {
	            case "produit":
	                return "/spot69/images/produits/default.png";
	            case "plat":
	                return "/spot69/images/plats/default.png";
	            case "categorie":
	                return "/spot69/images/categories/default.png";
	            case "chambres":
	                return "/spot69/images/chambres/default.png";
	            case "promo":
	                return "/spot69/images/promos/default.png";
	            case "video":
	                return "/spot69/videos/default.mp4";
	        	case "evenement":
	    			return "/spot69/images/chambres/default.png";
	            default:
	                return "/spot69/images/default.png"; // fallback générique si type inconnu
	        }
	    }
	    
	//    private UtilisateurDAO utilisateurDAO;
	    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	    @Override
	    public void init() throws ServletException {
	        utilisateurDAO = new UtilisateurDAO();
	    }
	    
	    private void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        // Déclarer DATE_FORMAT localement si ce n'est pas déjà fait
	        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	        
	        String nom = request.getParameter("nom");
	        String prenom = request.getParameter("prenom");
	        String email = request.getParameter("email");
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	        String telephone = request.getParameter("telephone");
	        String adresse = request.getParameter("adresse");
	        String dateNaissanceStr = request.getParameter("date_naissance");
	        String clientIp = request.getRemoteAddr();
	
	        // Validation des paramètres obligatoires
	        if (nom == null || prenom == null || username == null || password == null) {
	            sendError(response, out, "Paramètres obligatoires manquants (nom, prenom, username, password)", 400);
	            return;
	        }
	
	        // Vérification de la longueur du mot de passe
	        if (password.length() < 6) {
	            sendError(response, out, "Le mot de passe doit contenir au moins 6 caractères", 400);
	            return;
	        }
	
	        // Validation de l'email si fourni
	        if (email != null && !email.trim().isEmpty()) {
	            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
	            if (!email.matches(emailRegex)) {
	                sendError(response, out, "Format d'email invalide", 400);
	                return;
	            }
	        }
	
	        // Validation de la date de naissance si fournie
	        java.sql.Date dateNaissance = null; // Utiliser explicitement java.sql.Date
	        if (dateNaissanceStr != null && !dateNaissanceStr.trim().isEmpty()) {
	            try {
	                java.util.Date parsedDate = DATE_FORMAT.parse(dateNaissanceStr.trim());
	                dateNaissance = new java.sql.Date(parsedDate.getTime()); // Conversion en java.sql.Date
	                
	                // Validation: l'utilisateur doit avoir au moins 13 ans
	                java.util.Date currentDate = new java.util.Date();
	                java.util.Date minBirthDate = new java.util.Date(currentDate.getTime() - (13L * 365 * 24 * 60 * 60 * 1000));
	                if (parsedDate.after(minBirthDate)) {
	                    sendError(response, out, "Vous devez avoir au moins 13 ans pour vous inscrire", 400);
	                    return;
	                }
	                
	            } catch (ParseException e) {
	                sendError(response, out, "Format de date invalide. Utilisez YYYY-MM-DD", 400);
	                return;
	            }
	        }
	
	        try {
	            // 1. Vérifier si l'username est déjà utilisé
	            Utilisateur existingUser = utilisateurDAO.findByLoginOrEmail(username);
	            if (existingUser != null) {
	                sendError(response, out, "Ce nom d'utilisateur est déjà utilisé", 409);
	                return;
	            }
	
	            // 2. Vérifier si l'email est déjà utilisé (si fourni)
	            if (email != null && !email.trim().isEmpty()) {
	                existingUser = utilisateurDAO.findByLoginOrEmail(email);
	                if (existingUser != null) {
	                    sendError(response, out, "Cet email est déjà utilisé", 409);
	                    return;
	                }
	            }
	
	            // 3. Définir le rôle CLIENT par défaut
	            Role roleClient = RoleDAO.findById(3); // ID du rôle CLIENT
	            if (roleClient == null) {
	                sendError(response, out, "Erreur de configuration: rôle CLIENT introuvable", 500);
	                return;
	            }
	
	            // 4. Créer le nouvel utilisateur
	            Utilisateur newUser = new Utilisateur();
	            newUser.setNom(nom.trim());
	            newUser.setPrenom(prenom.trim());
	            newUser.setEmail(email != null ? email.trim() : null);
	            newUser.setLogin(username.trim());
	            newUser.setMotDePasse(PasswordUtils.hashPassword(password));
	            newUser.setRole(roleClient);
	            newUser.setPourcentage(BigDecimal.ZERO); // Pourcentage à 0 par défaut
	            newUser.setDateNaissance(dateNaissance); // Définir la date de naissance sur l'utilisateur
	
	            // 5. Insérer l'utilisateur dans la base avec la date de naissance
	            // Pas besoin de cast, dateNaissance est déjà java.sql.Date
	            boolean success = utilisateurDAO.insert(newUser, telephone, adresse, dateNaissance);
	
	            if (!success) {
	                sendError(response, out, "Erreur lors de la création du compte", 500);
	                return;
	            }
	
	            // 6. Récupérer l'utilisateur fraîchement créé pour la réponse
	            Utilisateur createdUser = utilisateurDAO.findByLoginOrEmail(username);
	            if (createdUser == null) {
	                sendError(response, out, "Compte créé mais erreur de récupération", 500);
	                return;
	            }
	
	            // 7. Préparer la réponse JSON
	            JsonObject userData = new JsonObject();
	            userData.addProperty("id", createdUser.getId());
	            userData.addProperty("login", createdUser.getLogin());
	            userData.addProperty("email", createdUser.getEmail() != null ? createdUser.getEmail() : "");
	            userData.addProperty("nom", createdUser.getNom());
	            userData.addProperty("prenom", createdUser.getPrenom());
	            userData.addProperty("telephone", telephone != null ? telephone : "");
	            userData.addProperty("adresse", adresse != null ? adresse : "");
	            
	            // Ajouter la date de naissance à la réponse
	            if (createdUser.getDateNaissance() != null) {
	                userData.addProperty("dateNaissance", DATE_FORMAT.format(createdUser.getDateNaissance()));
	            } else {
	                userData.addProperty("dateNaissance", "");
	            }
	            
	            userData.addProperty("ip", clientIp);
	            userData.addProperty("role", createdUser.getRole().getRoleName());
	            userData.addProperty("dateCreation", createdUser.getCreationDate().toString());
	
	            JsonObject responseData = new JsonObject();
	            responseData.add("user", userData);
	
	            jsonResponse("ok", responseData, out);
	
	        } catch (Exception e) {
	            e.printStackTrace();
	            sendError(response, out, "Erreur lors de l'inscription: " + e.getMessage(), 500);
	        }
	    }
	//    --------------------------------------------------------------------------------------------------
	    
//	    COMPTE CAISSE
	    private void handleGetCompteClient(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String userIdStr = request.getParameter("userId");
	            String clientIdStr = request.getParameter("clientId"); // Optionnel pour admin/caissier
	            
	            if (userIdStr == null) {
	                sendError(response, out, "Paramètre userId manquant", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int targetClientId = userId;
	            
	            // Si clientId est fourni, vérifier les permissions
	            if (clientIdStr != null && !clientIdStr.isEmpty()) {
	                int paramClientId = Integer.parseInt(clientIdStr);
	                
	                // Vérifier si l'utilisateur a les permissions
	                Utilisateur user = utilisateurDAO.findById(userId);
	                if (user != null && (user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
	                                    user.getRole().getRoleName().equals("MANAGEUR") ||
	                                    user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
	                    targetClientId = paramClientId;
	                } else {
	                    sendError(response, out, "Permission refusée", 403);
	                    return;
	                }
	            }
	            
	            // Récupérer le compte
	            CompteClientDAO compteDAO = new CompteClientDAO();
	            CompteClient compte = compteDAO.getCompteByClientId(targetClientId);
	            
	            if (compte == null) {
	                // Créer le compte s'il n'existe pas
	                compte = new CompteClient();
	                compte.setClientId(targetClientId);
	                compte.setSolde(BigDecimal.ZERO);
	                int compteId = compteDAO.creerCompteClient(compte);
	                if (compteId > 0) {
	                    compte.setId(compteId);
	                } else {
	                    sendError(response, out, "Erreur lors de la création du compte", 500);
	                    return;
	                }
	            }
	            
	            JsonObject data = new JsonObject();
	            data.addProperty("compteId", compte.getId());
	            data.addProperty("clientId", compte.getClientId());
	            data.addProperty("solde", compte.getSolde());
	            data.addProperty("dateCreation", compte.getDateCreation() != null ? 
	                compte.getDateCreation().toString() : "");
	            data.addProperty("dateMaj", compte.getDateMaj() != null ? 
	                compte.getDateMaj().toString() : "");
	            
	            // Ajouter les infos client si disponibles
	            if (compte.getClient() != null) {
	                JsonObject clientInfo = new JsonObject();
	                clientInfo.addProperty("nom", compte.getClient().getNom());
	                clientInfo.addProperty("prenom", compte.getClient().getPrenom());
	                clientInfo.addProperty("email", compte.getClient().getEmail());
	                clientInfo.addProperty("telephone", compte.getClient().getTelephone());
	                data.add("client", clientInfo);
	            }
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleGetHistoriqueTransactions(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String userIdStr = request.getParameter("userId");
	            String clientIdStr = request.getParameter("clientId"); // Optionnel
	            String dateDebutStr = request.getParameter("dateDebut");
	            String dateFinStr = request.getParameter("dateFin");
	            String typeTransaction = request.getParameter("typeTransaction"); // Optionnel
	            String limitStr = request.getParameter("limit"); // Optionnel
	            String offsetStr = request.getParameter("offset"); // Optionnel
	            
	            if (userIdStr == null) {
	                sendError(response, out, "Paramètre userId manquant", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int targetClientId = userId;
	            
	            // Vérifier les permissions si clientId est fourni
	            if (clientIdStr != null && !clientIdStr.isEmpty()) {
	                int paramClientId = Integer.parseInt(clientIdStr);
	                Utilisateur user = utilisateurDAO.findById(userId);
	                if (user != null && (user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
	                                    user.getRole().getRoleName().equals("MANAGEUR") ||
	                                    user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
	                    targetClientId = paramClientId;
	                } else {
	                    sendError(response, out, "Permission refusée", 403);
	                    return;
	                }
	            }
	            
	            // Parser les dates
	            java.sql.Date dateDebut = null;
	            java.sql.Date dateFin = null;
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            
	            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
	                dateDebut = new java.sql.Date(sdf.parse(dateDebutStr).getTime());
	            }
	            
	            if (dateFinStr != null && !dateFinStr.isEmpty()) {
	                dateFin = new java.sql.Date(sdf.parse(dateFinStr).getTime());
	            }
	            
	            // Limite et offset
	            Integer limit = null;
	            Integer offset = null;
	            
	            if (limitStr != null && !limitStr.isEmpty()) {
	                limit = Integer.parseInt(limitStr);
	            }
	            
	            if (offsetStr != null && !offsetStr.isEmpty()) {
	                offset = Integer.parseInt(offsetStr);
	            }
	            
	            // Récupérer l'historique
	            TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	            List<TransactionCompte> transactions;
	            
	            if (typeTransaction != null && !typeTransaction.isEmpty() && !"TOUS".equals(typeTransaction)) {
	                // Filtrer par type
	                transactions = transactionDAO.getHistoriqueClientFiltre(
	                    targetClientId, dateDebut, dateFin, typeTransaction, limit, offset);
	            } else {
	                transactions = transactionDAO.getHistoriqueClient(
	                    targetClientId, dateDebut, dateFin, limit, offset);
	            }
	            
	            // Compter le total pour la pagination
	            int totalTransactions = transactionDAO.countTransactionsClient(
	                targetClientId, dateDebut, dateFin, typeTransaction);
	            
	            // Préparer la réponse
	            JsonArray transactionsArray = new JsonArray();
	            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            
	            for (TransactionCompte transaction : transactions) {
	                JsonObject transJson = new JsonObject();
	                transJson.addProperty("id", transaction.getId());
	                transJson.addProperty("montant", transaction.getMontant());
	                transJson.addProperty("soldeAvant", transaction.getSoldeAvant());
	                transJson.addProperty("soldeApres", transaction.getSoldeApres());
	                transJson.addProperty("dateTransaction", 
	                    transaction.getDateTransaction() != null ? 
	                    displayFormat.format(transaction.getDateTransaction()) : "");
	                transJson.addProperty("notes", transaction.getNotes() != null ? transaction.getNotes() : "");
	                
	                // Type de transaction
	                if (transaction.getTypeTransaction() != null) {
	                    JsonObject typeJson = new JsonObject();
	                    typeJson.addProperty("id", transaction.getTypeTransaction().getId());
	                    typeJson.addProperty("code", transaction.getTypeTransaction().getCode());
	                    typeJson.addProperty("libelle", transaction.getTypeTransaction().getLibelle());
	                    transJson.add("typeTransaction", typeJson);
	                }
	                
	                // Caissier
	                if (transaction.getCaissiere() != null) {
	                    JsonObject caissierJson = new JsonObject();
	                    caissierJson.addProperty("id", transaction.getCaissiere().getId());
	                    caissierJson.addProperty("nom", transaction.getCaissiere().getNom());
	                    caissierJson.addProperty("prenom", transaction.getCaissiere().getPrenom());
	                    transJson.add("caissier", caissierJson);
	                }
	                
	                // Commande liée
	                if (transaction.getCommandeId() != null && transaction.getCommandeId() > 0) {
	                    transJson.addProperty("commandeId", transaction.getCommandeId());
	                    if (transaction.getCommande() != null) {
	                        transJson.addProperty("commandeNumero", transaction.getCommande().getNumeroCommande());
	                    }
	                }
	                
	                transactionsArray.add(transJson);
	            }
	            
	            // Ajouter les statistiques
	            BigDecimal soldeActuel = transactionDAO.getSoldeClient(targetClientId);
	            
	            JsonObject data = new JsonObject();
	            data.add("transactions", transactionsArray);
	            data.addProperty("soldeActuel", soldeActuel);
	            data.addProperty("nombreTransactions", transactions.size());
	            data.addProperty("totalTransactions", totalTransactions);
	            data.addProperty("clientId", targetClientId);
	            
	            // Ajouter des métadonnées de pagination
	            if (limit != null) {
	                int currentPage = offset != null ? (offset / limit) + 1 : 1;
	                int totalPages = (int) Math.ceil((double) totalTransactions / limit);
	                
	                data.addProperty("currentPage", currentPage);
	                data.addProperty("totalPages", totalPages);
	                data.addProperty("itemsPerPage", limit);
	            }
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleGetEtatCompteComplet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String userIdStr = request.getParameter("userId");
	            String clientIdStr = request.getParameter("clientId"); // Optionnel
	            
	            if (userIdStr == null) {
	                sendError(response, out, "Paramètre userId manquant", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int targetClientId = userId;
	            
	            // Vérifier les permissions
	            if (clientIdStr != null && !clientIdStr.isEmpty()) {
	                int paramClientId = Integer.parseInt(clientIdStr);
	                Utilisateur user = utilisateurDAO.findById(userId);
	                if (user != null && (user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
	                                    user.getRole().getRoleName().equals("MANAGEUR") ||
	                                    user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
	                    targetClientId = paramClientId;
	                } else {
	                    sendError(response, out, "Permission refusée", 403);
	                    return;
	                }
	            }
	            
	            // Récupérer le compte
	            CompteClientDAO compteDAO = new CompteClientDAO();
	            CompteClient compte = compteDAO.getCompteByClientId(targetClientId);
	            
	            if (compte == null) {
	                // Créer le compte s'il n'existe pas
	                compte = new CompteClient();
	                compte.setClientId(targetClientId);
	                compte.setSolde(BigDecimal.ZERO);
	                compteDAO.creerCompteClient(compte);
	                compte = compteDAO.getCompteByClientId(targetClientId);
	            }
	            
	            // Récupérer l'historique des transactions (10 dernières)
	            TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	            List<TransactionCompte> transactions = transactionDAO.getHistoriqueClient(
	                targetClientId, null, null, 10, 0);
	            
	            // Récupérer les commandes à crédit impayées
	            CreditDAO creditDAO = new CreditDAO();
	            List<Commande> commandesCredit = creditDAO.getCommandesCredit(null, targetClientId, null, null);
	            
	            // Calculer les statistiques
	            BigDecimal totalDepots = BigDecimal.ZERO;
	            BigDecimal totalRetraits = BigDecimal.ZERO;
	            
	            List<TransactionCompte> toutesTransactions = transactionDAO.getHistoriqueClient(
	                targetClientId, null, null, null, null);
	            
	            for (TransactionCompte trans : toutesTransactions) {
	                if (trans.getMontant().compareTo(BigDecimal.ZERO) > 0) {
	                    totalDepots = totalDepots.add(trans.getMontant());
	                } else {
	                    totalRetraits = totalRetraits.add(trans.getMontant().abs());
	                }
	            }
	            
	            // Total crédits impayés
	            BigDecimal totalCreditsImpayes = BigDecimal.ZERO;
	            for (Commande commande : commandesCredit) {
	                if (commande.getCredit() != null) {
	                    int montantRestant = commande.getCredit().getMontantTotal() - 
	                                       commande.getCredit().getMontantPaye();
	                    totalCreditsImpayes = totalCreditsImpayes.add(new BigDecimal(montantRestant));
	                }
	            }
	            
	            // Préparer la réponse
	            JsonObject data = new JsonObject();
	            
	            // Informations du compte
	            JsonObject compteJson = new JsonObject();
	            compteJson.addProperty("id", compte.getId());
	            compteJson.addProperty("clientId", compte.getClientId());
	            compteJson.addProperty("solde", compte.getSolde());
	            compteJson.addProperty("dateCreation", compte.getDateCreation() != null ? 
	                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(compte.getDateCreation()) : "");
	            compteJson.addProperty("dateMaj", compte.getDateMaj() != null ? 
	                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(compte.getDateMaj()) : "");
	            data.add("compte", compteJson);
	            
	            // Informations client
	            if (compte.getClient() != null) {
	                JsonObject clientJson = new JsonObject();
	                clientJson.addProperty("id", compte.getClient().getId());
	                clientJson.addProperty("nom", compte.getClient().getNom());
	                clientJson.addProperty("prenom", compte.getClient().getPrenom());
	                clientJson.addProperty("email", compte.getClient().getEmail());
	                clientJson.addProperty("telephone", compte.getClient().getTelephone());
	                clientJson.addProperty("plafondCredit", compte.getClient().getPlafond());
	                data.add("client", clientJson);
	            }
	            
	            // Dernières transactionsdateTransaction
	            JsonArray transactionsArray = new JsonArray();
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            
	            for (TransactionCompte transaction : transactions) {
	                JsonObject transJson = new JsonObject();
	                transJson.addProperty("id", transaction.getId());
	                transJson.addProperty("montant", transaction.getMontant());
	                transJson.addProperty("soldeAvant", transaction.getSoldeAvant());
	                transJson.addProperty("soldeApres", transaction.getSoldeApres());
	                transJson.addProperty("", 
	                    transaction.getDateTransaction() != null ? 
	                    sdf.format(transaction.getDateTransaction()) : "");
	                transJson.addProperty("notes", transaction.getNotes() != null ? transaction.getNotes() : "");
	                
	                if (transaction.getTypeTransaction() != null) {
	                    transJson.addProperty("typeTransaction", transaction.getTypeTransaction().getLibelle());
	                }
	                
	                transactionsArray.add(transJson);
	            }
	            data.add("dernieresTransactions", transactionsArray);
	            
	            // Commandes à crédit impayées
	            JsonArray commandesCreditArray = new JsonArray();
	            for (Commande commande : commandesCredit) {
	                JsonObject commandeJson = new JsonObject();
	                commandeJson.addProperty("id", commande.getId());
	                commandeJson.addProperty("numero", commande.getNumeroCommande());
	                commandeJson.addProperty("date", 
	                    commande.getDateCommande() != null ? 
	                    sdf.format(commande.getDateCommande()) : "");
	                commandeJson.addProperty("montantTotal", commande.getMontantTotal());
	                
	                if (commande.getCredit() != null) {
	                    int montantRestant = commande.getCredit().getMontantTotal() - 
	                                       commande.getCredit().getMontantPaye();
	                    commandeJson.addProperty("montantPaye", commande.getCredit().getMontantPaye());
	                    commandeJson.addProperty("montantRestant", montantRestant);
	                    commandeJson.addProperty("statutCredit", commande.getCredit().getStatut());
	                }
	                
	                commandesCreditArray.add(commandeJson);
	            }
	            data.add("commandesCreditImpayees", commandesCreditArray);
	            
	            // Statistiques
	            JsonObject statsJson = new JsonObject();
	            statsJson.addProperty("totalDepots", totalDepots);
	            statsJson.addProperty("totalRetraits", totalRetraits);
	            statsJson.addProperty("totalCreditsImpayes", totalCreditsImpayes);
	            statsJson.addProperty("nombreTransactions", toutesTransactions.size());
	            statsJson.addProperty("nombreCommandesCredit", commandesCredit.size());
	            data.add("statistiques", statsJson);
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleEffectuerDepot(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            // Lire le corps JSON
	            StringBuilder sb = new StringBuilder();
	            BufferedReader reader = request.getReader();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            String body = sb.toString();
	            
	            JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
	            
	            // Récupérer les paramètres
	            String userIdStr = jsonBody.get("userId").getAsString();
	            String clientIdStr = jsonBody.get("clientId").getAsString();
	            String montantStr = jsonBody.get("montant").getAsString();
	            String modePaiement = jsonBody.has("modePaiement") ? jsonBody.get("modePaiement").getAsString() : "CASH";
	            String notes = jsonBody.has("notes") ? jsonBody.get("notes").getAsString() : "";
	            
	            if (userIdStr == null || clientIdStr == null || montantStr == null) {
	                sendError(response, out, "Paramètres manquants", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int clientId = Integer.parseInt(clientIdStr);
	            BigDecimal montant = new BigDecimal(montantStr);
	            
	            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
	                sendError(response, out, "Le montant doit être positif", 400);
	                return;
	            }
	            
	            // Vérifier les permissions
	            Utilisateur user = utilisateurDAO.findById(userId);
	            if (user == null || (!user.getRole().getRoleName().equals("ADMINISTRATEUR") && 
	                                !user.getRole().getRoleName().equals("MANAGEUR") &&
	                                !user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
	                sendError(response, out, "Permission refusée", 403);
	                return;
	            }
	            
	            // Effectuer le dépôt
	            TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	            boolean success = transactionDAO.effectuerDepot(clientId, montant, userId, notes);
	            
	            if (success) {
	                // Récupérer le nouveau solde
	                BigDecimal nouveauSolde = transactionDAO.getSoldeClient(clientId);
	                
	                JsonObject data = new JsonObject();
	                data.addProperty("message", "Dépôt effectué avec succès");
	                data.addProperty("montant", montant);
	                data.addProperty("nouveauSolde", nouveauSolde);
	                data.addProperty("clientId", clientId);
	                
	                // Créer une notification pour le client
	                Notification notif = new Notification();
	                notif.setGeneratedBy("SYSTEM");
	                notif.setToUser(clientId);
	                notif.setMessages("Dépôt de " + montant + " HTG effectué sur votre compte. Nouveau solde: " + 
	                                 nouveauSolde + " HTG");
	                notif.setTypeNotif("DEPOT");
	                notif.setStatus("VISIBLE");
	                
	                NotificationDAO notifDAO = new NotificationDAO();
	                notifDAO.ajouterNotification(notif);
	                
	                jsonResponse("ok", data, out);
	            } else {
	                sendError(response, out, "Erreur lors du dépôt", 500);
	            }
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleGetStatistiquesCompte(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String userIdStr = request.getParameter("userId");
	            String clientIdStr = request.getParameter("clientId"); // Optionnel
	            String periode = request.getParameter("periode"); // "jour", "semaine", "mois", "annee"
	            
	            if (userIdStr == null) {
	                sendError(response, out, "Paramètre userId manquant", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int targetClientId = userId;
	            
	            // Vérifier les permissions
	            if (clientIdStr != null && !clientIdStr.isEmpty()) {
	                int paramClientId = Integer.parseInt(clientIdStr);
	                Utilisateur user = utilisateurDAO.findById(userId);
	                if (user != null && (user.getRole().getRoleName().equals("ADMINISTRATEUR") || 
	                                    user.getRole().getRoleName().equals("MANAGEUR") ||
	                                    user.getRole().getRoleName().equals("CAISSIER(ERE)"))) {
	                    targetClientId = paramClientId;
	                } else {
	                    sendError(response, out, "Permission refusée", 403);
	                    return;
	                }
	            }
	            
	            // Déterminer la période - Conversion en java.sql.Date
	            java.sql.Date dateDebutSql = null;
	            java.sql.Date dateFinSql = null;
	            
	            if (periode != null) {
	                Calendar cal = Calendar.getInstance();
	                
	                switch (periode.toLowerCase()) {
	                    case "jour":
	                        cal.add(Calendar.DAY_OF_MONTH, -1);
	                        dateDebutSql = new java.sql.Date(cal.getTime().getTime());
	                        break;
	                    case "semaine":
	                        cal.add(Calendar.DAY_OF_MONTH, -7);
	                        dateDebutSql = new java.sql.Date(cal.getTime().getTime());
	                        break;
	                    case "mois":
	                        cal.add(Calendar.MONTH, -1);
	                        dateDebutSql = new java.sql.Date(cal.getTime().getTime());
	                        break;
	                    case "annee":
	                        cal.add(Calendar.YEAR, -1);
	                        dateDebutSql = new java.sql.Date(cal.getTime().getTime());
	                        break;
	                    default:
	                        // Toutes les transactions - dateDebut reste null
	                        break;
	                }
	            }
	            
	            // Date de fin (aujourd'hui)
	            dateFinSql = new java.sql.Date(new java.util.Date().getTime());
	            
	            // Récupérer les transactions
	            TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	            List<TransactionCompte> transactions = transactionDAO.getHistoriqueClient(
	                targetClientId, dateDebutSql, dateFinSql, null, null);
	            
	            // Calculer les statistiques
	            BigDecimal totalDepots = BigDecimal.ZERO;
	            BigDecimal totalRetraits = BigDecimal.ZERO;
	            int nombreDepots = 0;
	            int nombreRetraits = 0;
	            BigDecimal soldeActuel = transactionDAO.getSoldeClient(targetClientId);
	            
	            for (TransactionCompte transaction : transactions) {
	                if (transaction.getMontant().compareTo(BigDecimal.ZERO) > 0) {
	                    totalDepots = totalDepots.add(transaction.getMontant());
	                    nombreDepots++;
	                } else {
	                    totalRetraits = totalRetraits.add(transaction.getMontant().abs());
	                    nombreRetraits++;
	                }
	            }
	            
	            // Récupérer les crédits impayés
	            CreditDAO creditDAO = new CreditDAO();
	            List<Commande> commandesCredit = creditDAO.getCommandesCredit(null, targetClientId, null, null);
	            BigDecimal totalCreditsImpayes = BigDecimal.ZERO;
	            
	            for (Commande commande : commandesCredit) {
	                if (commande.getCredit() != null) {
	                    int montantRestant = commande.getCredit().getMontantTotal() - 
	                                       commande.getCredit().getMontantPaye();
	                    totalCreditsImpayes = totalCreditsImpayes.add(new BigDecimal(montantRestant));
	                }
	            }
	            
	            // Préparer la réponse
	            JsonObject data = new JsonObject();
	            data.addProperty("soldeActuel", soldeActuel);
	            data.addProperty("totalDepots", totalDepots);
	            data.addProperty("totalRetraits", totalRetraits);
	            data.addProperty("nombreDepots", nombreDepots);
	            data.addProperty("nombreRetraits", nombreRetraits);
	            data.addProperty("totalCreditsImpayes", totalCreditsImpayes);
	            data.addProperty("nombreCreditsImpayes", commandesCredit.size());
	            data.addProperty("periode", periode != null ? periode : "tous");
	            data.addProperty("nombreTransactions", transactions.size());
	            
	            // Moyennes
	            if (nombreDepots > 0) {
	                data.addProperty("moyenneDepot", totalDepots.divide(new BigDecimal(nombreDepots), 2, BigDecimal.ROUND_HALF_UP));
	            }
	            if (nombreRetraits > 0) {
	                data.addProperty("moyenneRetrait", totalRetraits.divide(new BigDecimal(nombreRetraits), 2, BigDecimal.ROUND_HALF_UP));
	            }
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleGetLastTransactions(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String userIdStr = request.getParameter("userId");
	            String limitStr = request.getParameter("limit");
	            
	            if (userIdStr == null) {
	                sendError(response, out, "Paramètre userId manquant", 400);
	                return;
	            }
	            
	            int userId = Integer.parseInt(userIdStr);
	            int limit = 10; // Par défaut
	            
	            if (limitStr != null && !limitStr.isEmpty()) {
	                limit = Integer.parseInt(limitStr);
	            }
	            
	            // Récupérer les transactions
	            TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
	            List<TransactionCompte> transactions = transactionDAO.getHistoriqueClient(
	                userId, null, null, limit, 0);
	            
	            // Préparer la réponse
	            JsonArray transactionsArray = new JsonArray();
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            
	            for (TransactionCompte transaction : transactions) {
	                JsonObject transJson = new JsonObject();
	                transJson.addProperty("id", transaction.getId());
	                transJson.addProperty("montant", transaction.getMontant());
	                transJson.addProperty("soldeAvant", transaction.getSoldeAvant());
	                transJson.addProperty("soldeApres", transaction.getSoldeApres());
	                transJson.addProperty("dateTransaction", 
	                    transaction.getDateTransaction() != null ? 
	                    sdf.format(transaction.getDateTransaction()) : "");
	                transJson.addProperty("notes", transaction.getNotes() != null ? transaction.getNotes() : "");
	                
	                if (transaction.getTypeTransaction() != null) {
	                    JsonObject typeJson = new JsonObject();
	                    typeJson.addProperty("code", transaction.getTypeTransaction().getCode());
	                    typeJson.addProperty("libelle", transaction.getTypeTransaction().getLibelle());
	                    transJson.add("typeTransaction", typeJson);
	                }
	                
	                transactionsArray.add(transJson);
	            }
	            
	            JsonObject data = new JsonObject();
	            data.add("transactions", transactionsArray);
	            data.addProperty("nombre", transactions.size());
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur: " + e.getMessage(), 500);
	        }
	    }
	
	//    @Override
	//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	//            throws ServletException, IOException {
	//        doGet(request, response);
	//    }
	    
	    

	    private void handlePrivilegeNiveaux(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            List<PrivilegeNiveau> niveaux = privilegeNiveauDAO.listerTous();
	            
	            // Créer un tableau JSON avec uniquement les champs demandés
	            JsonArray niveauxJson = new JsonArray();
	            for (PrivilegeNiveau niveau : niveaux) {
	                JsonObject niveauJson = new JsonObject();
	                niveauJson.addProperty("id", niveau.getId());
	                niveauJson.addProperty("nom", niveau.getNom());
	                niveauJson.addProperty("seuilPoints", niveau.getSeuilPoints());
	                niveauJson.addProperty("pourcentageReduction", niveau.getPourcentageReduction());
	                niveauJson.addProperty("description", niveau.getDescription());
	                niveauxJson.add(niveauJson);
	            }
	            
	            jsonResponse("ok", niveauxJson, out);
	            System.out.println("Tous les niveau envoyé");
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de la récupération des niveaux de privilège: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handlePrivilegeNiveauActifs(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            List<PrivilegeNiveau> niveaux = privilegeNiveauDAO.listerActifs();
	            
	            // Créer un tableau JSON avec uniquement les champs demandés
	            JsonArray niveauxJson = new JsonArray();
	            for (PrivilegeNiveau niveau : niveaux) {
	                JsonObject niveauJson = new JsonObject();
	                niveauJson.addProperty("id", niveau.getId());
	                niveauJson.addProperty("nom", niveau.getNom());
	                niveauJson.addProperty("seuilPoints", niveau.getSeuilPoints());
	                niveauJson.addProperty("pourcentageReduction", niveau.getPourcentageReduction());
	                niveauJson.addProperty("description", niveau.getDescription());
	                niveauxJson.add(niveauJson);
	            }
	            
	            jsonResponse("ok", niveauxJson, out);
	            
	        } catch (Exception e) {
	            sendError(response, out, "Erreur lors de la récupération des niveaux actifs: " + e.getMessage(), 500);
	        }
	    }
	    
	    private void handleSendVerificationCode(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String identifier = request.getParameter("identifier");
	            
	            if (identifier == null || identifier.trim().isEmpty()) {
	                sendError(response, out, "Identifiant requis", 400);
	                return;
	            }
	            
	            // Rechercher l'utilisateur par email ou username
	            Utilisateur user = utilisateurDAO.findByLoginOrEmail(identifier);
	            
	            if (user == null) {
	                sendError(response, out, "Aucun compte trouvé avec cet identifiant", 404);
	                return;
	            }
	            
	            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
	                sendError(response, out, "Aucun email associé à ce compte", 400);
	                return;
	            }
	            
	            // Générer un code à 6 chiffres
	            String code = String.format("%06d", new Random().nextInt(999999));
	            
	            // Calculer la date d'expiration (5 minutes)
	            java.sql.Timestamp expiresAt = new java.sql.Timestamp(
	                System.currentTimeMillis() + (5 * 60 * 1000)
	            );
	            
	            // Insérer le code dans la base de données
	            String insertSql = "INSERT INTO verification_codes (email, code, expires_at) VALUES (?, ?, ?)";
	            try (Connection conn = DBConnection.getConnection();
	                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
	                
	                pstmt.setString(1, user.getEmail());
	                pstmt.setString(2, code);
	                pstmt.setTimestamp(3, expiresAt);
	                pstmt.executeUpdate();
	            }
	            
	            // Envoyer l'email avec le code
	            sendVerificationEmail(user.getEmail(), code);
	            
	            // Préparer la réponse
	            JsonObject data = new JsonObject();
	            data.addProperty("email", user.getEmail());
	            data.addProperty("message", "Code envoyé avec succès");
	            
	            jsonResponse("ok", data, out);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            sendError(response, out, "Erreur lors de l'envoi du code: " + e.getMessage(), 500);
	        }
	    }

	    private void handleVerifyCode(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
	        try {
	            String email = request.getParameter("email");
	            String code = request.getParameter("code");
	            
	            if (email == null || code == null) {
	                sendError(response, out, "Email et code requis", 400);
	                return;
	            }
	            
	            // Vérifier le code dans la base de données
	            String sql = "SELECT * FROM verification_codes " +
	                        "WHERE email = ? AND code = ? AND used = FALSE AND expires_at > NOW() " +
	                        "ORDER BY created_at DESC LIMIT 1";
	            
	            try (Connection conn = DBConnection.getConnection();
	                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
	                
	                pstmt.setString(1, email);
	                pstmt.setString(2, code);
	                
	                ResultSet rs = pstmt.executeQuery();
	                
	                if (rs.next()) {
	                    // Marquer le code comme utilisé
	                    String updateSql = "UPDATE verification_codes SET used = TRUE WHERE id = ?";
	                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                        updateStmt.setInt(1, rs.getInt("id"));
	                        updateStmt.executeUpdate();
	                    }
	                    
	                    // Générer un token pour la réinitialisation
	                    String token = generateToken(email);
	                    
	                    // Préparer la réponse
	                    JsonObject data = new JsonObject();
	                    data.addProperty("token", token);
	                    data.addProperty("email", email);
	                    data.addProperty("message", "Code vérifié avec succès");
	                    
	                    jsonResponse("ok", data, out);
	                } else {
	                    // Incrémenter les tentatives d'échec
	                    incrementFailedAttempts(email, code);
	                    sendError(response, out, "Code incorrect ou expiré", 400);
	                }
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            sendError(response, out, "Erreur lors de la vérification: " + e.getMessage(), 500);
	        }
	    }

	    private void sendVerificationEmail(String email, String code) {
	        try {
	            Properties props = new Properties();
	            props.put("mail.smtp.host", "smtp.office365.com");
	            props.put("mail.smtp.port", "587");
	            props.put("mail.smtp.auth", "true");
	            props.put("mail.smtp.starttls.enable", "true");

	            Session session = Session.getInstance(props, new Authenticator() {
	                @Override
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication("contact@spot69.net", "Emanagement2024@");
	                }
	            });

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("contact@spot69.net"));
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
	            message.setSubject("Code de vérification - SPOT69");

	            String htmlContent = "<html><body>"
	                    + "<h2>Code de vérification SPOT69</h2>"
	                    + "<p>Voici votre code de vérification :</p>"
	                    + "<h1 style='color: #D4AF37; font-size: 36px; letter-spacing: 10px;'>" + code + "</h1>"
	                    + "<p>Ce code est valable pendant <strong>5 minutes</strong>.</p>"
	                    + "<p>Si vous n'avez pas demandé ce code, veuillez ignorer cet email.</p>"
	                    + "<br><p>Cordialement,<br>L'équipe SPOT69</p>"
	                    + "</body></html>";

	            message.setContent(htmlContent, "text/html; charset=utf-8");
	            Transport.send(message);

	            System.out.println("Email de vérification envoyé à: " + email);

	        } catch (MessagingException e) {
	            System.err.println("Erreur lors de l'envoi de l'email à " + email);
	            e.printStackTrace();
	        }
	    }

	    private void incrementFailedAttempts(String email, String code) {
	        String sql = "UPDATE verification_codes SET attempts = attempts + 1 " +
	                    "WHERE email = ? AND code = ? AND expires_at > NOW()";
	        
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            
	            pstmt.setString(1, email);
	            pstmt.setString(2, code);
	            pstmt.executeUpdate();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private String generateToken(String email) {
	        // Générer un token JWT ou simplement un token aléatoire
	        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
	    }
	 
	}