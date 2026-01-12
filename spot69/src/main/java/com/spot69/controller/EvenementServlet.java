package com.spot69.controller;

import com.spot69.dao.CompteClientDAO;
import com.spot69.dao.EvenementDAO;
import com.spot69.dao.NotificationDAO;
import com.spot69.dao.ReservationEvenementDAO;
import com.spot69.dao.TransactionCompteDAO;
import com.spot69.dao.TypeTableEvenementDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.CompteClient;
import com.spot69.model.Evenement;
import com.spot69.model.Notification;
import com.spot69.model.Permissions;
import com.spot69.model.ReservationEvenement;
import com.spot69.model.TransactionCompte;
import com.spot69.model.TypeTableEvenement;
import com.spot69.model.Utilisateur;
import com.spot69.utils.PermissionChecker;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;

@WebServlet({"/EvenementServlet", "/blok/EvenementServlet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class EvenementServlet extends HttpServlet {

    private EvenementDAO evenementDAO;
    private TypeTableEvenementDAO typeTableDAO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static String UPLOAD_EVENEMENT_IMAGE_DIR = "";
    private static final List<String> ALLOWED_EXTENSIONS = 
            Collections.unmodifiableList(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"));
        
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public void init() throws ServletException {
//        UPLOAD_EVENEMENT_IMAGE_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "evenements";
        // production
        ServletContext context = getServletContext();
         UPLOAD_EVENEMENT_IMAGE_DIR = context.getInitParameter("UPLOAD_EVENEMENT_IMAGE_DIR");
        
        // Créer le dossier d'upload s'il n'existe pas
        File uploadDir = new File(UPLOAD_EVENEMENT_IMAGE_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println(">>> [INFO] Dossier d'upload créé: " + UPLOAD_EVENEMENT_IMAGE_DIR);
        }
        
        evenementDAO = new EvenementDAO();
        typeTableDAO = new TypeTableEvenementDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        // Vérification de l'authentification pour les actions nécessitant une connexion
        if (utilisateur == null && !action.equals("lister") && !action.equals("details")) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (action.toLowerCase()) {
            case "ajouter":
                afficherFormulaireAjout(request, response);
                break;
            case "modifier":
                afficherFormulaireModification(request, response);
                break;
            case "supprimer":
                traiterSuppression(request, response);
                break;
            case "desactiver":
                traiterDesactivation(request, response);
                break;
            case "reactiver":
                traiterReactivation(request, response);
                break;
            case "details":
                afficherDetails(request, response);
                break;
            case "lister":
                listerEvenements(request, response);
                break;
            case "reservations":
                afficherReservations(request, response);
                break;
            case "reservationsevent":
                afficherReservationsEvenement(request, response);
                break;
            case "detailsreservation":
                afficherDetailsReservation(request, response);
                break;
            case "gerertables":
                gererTablesEvenement(request, response);
                break;
            case "supprimertable":
                supprimerTableEvenement(request, response);
                break;
            default:
                listerEvenements(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (utilisateur == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (action.toLowerCase()) {
            case "ajouter":
                traiterAjoutAvecTables(request, response);
                break;
            case "modifier":
                traiterModificationAvecTables(request, response);
                break;
            case "ajoutertable":
                traiterAjoutTable(request, response);
                break;
            case "modifiertable":
                traiterModificationTable(request, response);
                break;
            case "validerreservation":
                traiterValidationReservation(request, response);
                break;
            case "annulerreservation":
                traiterAnnulationReservation(request, response);
                break;
            default:
                response.sendRedirect("EvenementServlet?action=lister");
                break;
        }
    }

    // =====================
    // MÉTHODES PRIVÉES
    // =====================

    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("ToastAdmErrorNotif", "Vous n'avez pas la permission d'effectuer cette action.");
        session.setAttribute("toastType", "error");
        response.sendRedirect("EvenementServlet?action=lister");
    }

    // Afficher le formulaire d'ajout
    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("ajouter-evenement.jsp").forward(request, response);
    }
    
    // Gérer les tables d'un événement
    private void gererTablesEvenement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        // Vérifier les permissions
        if (!PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_EVENEMENTS)) {
            handleUnauthorized(request, response);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            Evenement evenement = evenementDAO.getById(eventId);
            
            if (evenement == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }
            
            // Charger les tables de l'événement
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementIdForAdmin(eventId);
            request.setAttribute("evenement", evenement);
            request.setAttribute("tables", tables);
            
            request.getRequestDispatcher("gerer-tables-evenement.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("EvenementServlet?action=lister");
        }
    }
    
    // Supprimer une table d'un événement
    private void supprimerTableEvenement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        String tableIdStr = request.getParameter("tableId");
        String eventIdStr = request.getParameter("eventId");
        
        if (tableIdStr == null || tableIdStr.isEmpty() || eventIdStr == null || eventIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }
        
        try {
            int tableId = Integer.parseInt(tableIdStr);
            int eventId = Integer.parseInt(eventIdStr);
            
            // Vérifier si la table existe
            TypeTableEvenement table = typeTableDAO.getById(tableId);
            if (table == null || table.getEvenementId() != eventId) {
                session.setAttribute("ToastAdmErrorNotif", "Table introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            // Supprimer la table
            typeTableDAO.supprimer(tableId);
            
            // Mettre à jour la capacité totale de l'événement
            evenementDAO.mettreAJourCapaciteTotale(eventId);
            
            session.setAttribute("ToastAdmSuccesNotif", "Table supprimée avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
        }
        
        response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventIdStr);
    }
    
    // Traiter l'ajout d'une table
    private void traiterAjoutTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String eventIdStr = request.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            
            // Récupérer les données du formulaire
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String capaciteStr = request.getParameter("capacite");
            String prixStr = request.getParameter("prix");
            
            // Validation
            if (nom == null || nom.trim().isEmpty() ||
                capaciteStr == null || capaciteStr.trim().isEmpty() ||
                prixStr == null || prixStr.trim().isEmpty()) {
                
                session.setAttribute("ToastAdmErrorNotif", "Tous les champs sont obligatoires.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            int capacite;
            BigDecimal prix;
            
            try {
                capacite = Integer.parseInt(capaciteStr);
                if (capacite <= 0) {
                    session.setAttribute("ToastAdmErrorNotif", "La capacité doit être supérieure à 0.");
                    session.setAttribute("toastType", "error");
                    response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("ToastAdmErrorNotif", "La capacité doit être un nombre valide.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            try {
                prix = new BigDecimal(prixStr);
                if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                    session.setAttribute("ToastAdmErrorNotif", "Le prix doit être supérieur à 0.");
                    session.setAttribute("toastType", "error");
                    response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("ToastAdmErrorNotif", "Le prix doit être un nombre valide.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            // Créer l'objet TypeTableEvenement
            TypeTableEvenement table = new TypeTableEvenement();
            table.setEvenementId(eventId);
            table.setNom(nom.trim());
            table.setDescription(description != null ? description.trim() : "");
            table.setCapacite(capacite);
            table.setPrix(prix);
            table.setStatut("ACTIF");
            
            // Ajouter à la base de données
            typeTableDAO.ajouter(table);
            
            // Mettre à jour la capacité totale de l'événement
            evenementDAO.mettreAJourCapaciteTotale(eventId);
            
            session.setAttribute("ToastAdmSuccesNotif", "Table ajoutée avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID d'événement invalide.");
            session.setAttribute("toastType", "error");
        }
        
        response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventIdStr);
    }
    
    // Traiter la modification d'une table
    private void traiterModificationTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String tableIdStr = request.getParameter("tableId");
        String eventIdStr = request.getParameter("eventId");
        
        if (tableIdStr == null || tableIdStr.isEmpty() || eventIdStr == null || eventIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }
        
        try {
            int tableId = Integer.parseInt(tableIdStr);
            int eventId = Integer.parseInt(eventIdStr);
            
            // Récupérer les données du formulaire
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String capaciteStr = request.getParameter("capacite");
            String prixStr = request.getParameter("prix");
            String statut = request.getParameter("statut");
            
            // Validation
            if (nom == null || nom.trim().isEmpty() ||
                capaciteStr == null || capaciteStr.trim().isEmpty() ||
                prixStr == null || prixStr.trim().isEmpty()) {
                
                session.setAttribute("ToastAdmErrorNotif", "Tous les champs sont obligatoires.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            int capacite;
            BigDecimal prix;
            
            try {
                capacite = Integer.parseInt(capaciteStr);
                if (capacite <= 0) {
                    session.setAttribute("ToastAdmErrorNotif", "La capacité doit être supérieure à 0.");
                    session.setAttribute("toastType", "error");
                    response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("ToastAdmErrorNotif", "La capacité doit être un nombre valide.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            try {
                prix = new BigDecimal(prixStr);
                if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                    session.setAttribute("ToastAdmErrorNotif", "Le prix doit être supérieur à 0.");
                    session.setAttribute("toastType", "error");
                    response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("ToastAdmErrorNotif", "Le prix doit être un nombre valide.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            // Récupérer la table existante
            TypeTableEvenement table = typeTableDAO.getById(tableId);
            if (table == null || table.getEvenementId() != eventId) {
                session.setAttribute("ToastAdmErrorNotif", "Table introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventId);
                return;
            }
            
            // Mettre à jour l'objet
            table.setNom(nom.trim());
            table.setDescription(description != null ? description.trim() : "");
            table.setCapacite(capacite);
            table.setPrix(prix);
            table.setStatut(statut != null ? statut : "ACTIF");
            
            // Modifier dans la base de données
            typeTableDAO.modifier(table);
            
            // Mettre à jour la capacité totale de l'événement
            evenementDAO.mettreAJourCapaciteTotale(eventId);
            
            session.setAttribute("ToastAdmSuccesNotif", "Table modifiée avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
        }
        
        response.sendRedirect("EvenementServlet?action=gerertables&id=" + eventIdStr);
    }
    
    private void afficherReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        // Vérifier les permissions
        if (!PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_EVENEMENTS)) {
            handleUnauthorized(request, response);
            return;
        }
        
        ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
        List<ReservationEvenement> reservations;
        
        // Filtrer par statut si fourni
        String statut = request.getParameter("statut");
        if (statut != null && !statut.isEmpty()) {
            reservations = reservationDAO.getReservationsByStatut(statut);
        } else {
            reservations = reservationDAO.getAllForAdmin();
        }
        
        request.setAttribute("reservations", reservations);
        request.setAttribute("evenement", null); // Pour indiquer que c'est toutes les réservations
        request.getRequestDispatcher("liste-reservations-admin.jsp").forward(request, response);
    }

    private void afficherReservationsEvenement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        // Vérifier les permissions
        if (!PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_EVENEMENTS)) {
            handleUnauthorized(request, response);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            Evenement evenement = evenementDAO.getById(eventId);
            
            if (evenement == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }
            
            ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
            List<ReservationEvenement> reservations = reservationDAO.getByEventId(eventId);
            
            // Appliquer les filtres
            String statut = request.getParameter("statut");
            String moyenPaiement = request.getParameter("moyenPaiement");
            String dateDebut = request.getParameter("dateDebut");
            String dateFin = request.getParameter("dateFin");
            
            if (statut != null && !statut.isEmpty()) {
                reservations.removeIf(r -> !statut.equals(r.getStatut()));
            }
            if (moyenPaiement != null && !moyenPaiement.isEmpty()) {
                reservations.removeIf(r -> !moyenPaiement.equals(r.getMoyenPaiement()));
            }
            if (dateDebut != null && !dateDebut.isEmpty()) {
                LocalDate debut = LocalDate.parse(dateDebut);
                reservations.removeIf(r -> r.getDateReservation().toLocalDate().isBefore(debut));
            }
            if (dateFin != null && !dateFin.isEmpty()) {
                LocalDate fin = LocalDate.parse(dateFin);
                reservations.removeIf(r -> r.getDateReservation().toLocalDate().isAfter(fin));
            }
            
            request.setAttribute("reservations", reservations);
            request.setAttribute("evenement", evenement);
            request.getRequestDispatcher("liste-reservations-admin.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("EvenementServlet?action=lister");
        }
    }

    private void afficherDetailsReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        // Vérifier les permissions
        if (!PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_EVENEMENTS)) {
            handleUnauthorized(request, response);
            return;
        }
        
        String reservationIdStr = request.getParameter("id");
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=reservations");
            return;
        }
        
        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
            ReservationEvenement reservation = reservationDAO.getById(reservationId);
            
            if (reservation == null) {
                session.setAttribute("ToastAdmErrorNotif", "Réservation introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=reservations");
                return;
            }
            
            // Charger les informations supplémentaires
            Evenement evenement = evenementDAO.getById(reservation.getEvenementId());
            reservation.setEvenement(evenement);
            
            // Charger le type de table
            TypeTableEvenement typeTable = typeTableDAO.getById(reservation.getTypeTableId());
            reservation.setTypeTable(typeTable);
            
            // Charger les informations utilisateur
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            Utilisateur client = utilisateurDAO.findById(reservation.getUtilisateurId());
            reservation.setUtilisateur(client);
            
            request.setAttribute("reservation", reservation);
            request.getRequestDispatcher("details-reservation.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("EvenementServlet?action=reservations");
        }
    }

    private void traiterValidationReservation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        String reservationIdStr = request.getParameter("id");
        String notes = request.getParameter("notes");
        
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=reservations");
            return;
        }
        
        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
            ReservationEvenement reservation = reservationDAO.getById(reservationId);
            
            if (reservation == null) {
                session.setAttribute("ToastAdmErrorNotif", "Réservation introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=reservations");
                return;
            }
            
            // Valider la réservation
            boolean success = reservationDAO.validerReservation(reservationId, utilisateur.getId(), notes);
            
            if (success) {
                // Créer une notification pour le client
                Notification notification = new Notification();
                notification.setGeneratedBy("SYSTEM");
                notification.setToUser(reservation.getUtilisateurId());
                notification.setMessages("Votre réservation pour l'événement a été validée !");
                notification.setTypeNotif("RESERVATION_CONFIRMEE");
                notification.setStatus("VISIBLE");
                
                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.ajouterNotification(notification);
                
                session.setAttribute("ToastAdmSuccesNotif", "Réservation validée avec succès.");
                session.setAttribute("toastType", "success");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la validation de la réservation.");
                session.setAttribute("toastType", "error");
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID de réservation invalide.");
            session.setAttribute("toastType", "error");
        }
        
        response.sendRedirect("EvenementServlet?action=reservations");
    }

    private void traiterAnnulationReservation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        String reservationIdStr = request.getParameter("id");
        String raison = request.getParameter("raison");
        
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=reservations");
            return;
        }
        
        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
            
            // Annuler la réservation
            boolean success = reservationDAO.annulerReservation(reservationId, utilisateur.getId(), raison);
            
            if (success) {
                // Récupérer la réservation pour les informations
                ReservationEvenement reservation = reservationDAO.getById(reservationId);
                
                // Si paiement par solde, rembourser
                if (reservation != null && "SOLDE".equals(reservation.getMoyenPaiement())) {
                    CompteClientDAO compteDAO = new CompteClientDAO();
                    CompteClient compte = compteDAO.getCompteByClientId(reservation.getUtilisateurId());
                    
                    if (compte != null) {
                        BigDecimal nouveauSolde = compte.getSolde().add(reservation.getMontantTotal());
                        compteDAO.mettreAJourSolde(compte.getId(), nouveauSolde);
                        
                        // Enregistrer transaction de remboursement
                        TransactionCompteDAO transactionDAO = new TransactionCompteDAO();
                        TransactionCompte transaction = new TransactionCompte();
                        transaction.setCompteClientId(compte.getId());
                        transaction.setMontant(reservation.getMontantTotal());
                        transaction.setSoldeAvant(compte.getSolde());
                        transaction.setSoldeApres(nouveauSolde);
                        transaction.setNotes("Remboursement réservation annulée #" + reservationId);
                        transaction.setDateTransaction(new Timestamp(new Date().getTime()));
                        transaction.setCaissiereId(utilisateur.getId());
                        transaction.setTypeTransactionId(1);
                        
                        transactionDAO.creerTransaction(transaction);
                    }
                }
                
                // Notification pour le client
                if (reservation != null) {
                    Notification notification = new Notification();
                    notification.setGeneratedBy("SYSTEM");
                    notification.setToUser(reservation.getUtilisateurId());
                    notification.setMessages("Votre réservation a été annulée.");
                    notification.setTypeNotif("RESERVATION_ANNULEE");
                    notification.setStatus("VISIBLE");
                    
                    NotificationDAO notificationDAO = new NotificationDAO();
                    notificationDAO.ajouterNotification(notification);
                }
                
                session.setAttribute("ToastAdmSuccesNotif", "Réservation annulée avec succès.");
                session.setAttribute("toastType", "success");
            } else {
                session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'annulation de la réservation.");
                session.setAttribute("toastType", "error");
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID de réservation invalide.");
            session.setAttribute("toastType", "error");
        }
        
        response.sendRedirect("EvenementServlet?action=reservations");
    }

    // Afficher le formulaire de modification
    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Evenement evenement = evenementDAO.getById(id);
            
            if (evenement == null) {
                HttpSession session = request.getSession();
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }
            
            // Charger les tables de l'événement
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementIdForAdmin(id);
            evenement.setTypesTables(tables);

            request.setAttribute("evenement", evenement);
            request.getRequestDispatcher("modifier-evenement.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("EvenementServlet?action=lister");
        }
    }

    // Lister tous les événements
    private void listerEvenements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>> [DEBUG] Méthode listerEvenements appelée.");
        
        // Utiliser getAllForAdmin() pour voir tous les événements (même masqués)
        // ou getAll() pour seulement les visibles
        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        
        List<Evenement> evenements;
        if (utilisateur != null && PermissionChecker.hasPermission(utilisateur, Permissions.GESTION_EVENEMENTS)) {
            evenements = evenementDAO.getAllForAdmin();
        } else {
            evenements = evenementDAO.getAll();
        }
        
        request.setAttribute("evenements", evenements);
        request.getRequestDispatcher("liste-evenements.jsp").forward(request, response);
    }
    
    private void detailsEvenements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	 String idStr = request.getParameter("id");
         if (idStr == null || idStr.isEmpty()) {
             response.sendRedirect("EvenementServlet?action=lister");
             return;
         }

         try {
             int id = Integer.parseInt(idStr);
             Evenement evenement = evenementDAO.getById(id);
             
             if (evenement == null) {
                 HttpSession session = request.getSession();
                 session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                 session.setAttribute("toastType", "error");
                 response.sendRedirect("EvenementServlet?action=lister");
                 return;
             }

             request.setAttribute("evenement", evenement);
             request.getRequestDispatcher("details-evenement.jsp").forward(request, response);
             
         } catch (NumberFormatException e) {
             response.sendRedirect("EvenementServlet?action=lister");
         }
    }

    // Afficher les détails d'un événement
    private void afficherDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Evenement evenement = evenementDAO.getById(id);
            
            if (evenement == null) {
                request.setAttribute("errorMessage", "Événement introuvable.");
                request.getRequestDispatcher("erreur.jsp").forward(request, response);
                return;
            }
            
            // Charger les tables de l'événement
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementId(id);
            evenement.setTypesTables(tables);

            request.setAttribute("evenement", evenement);
            request.getRequestDispatcher("details-evenement.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("EvenementServlet?action=lister");
        }
    }

    // Traiter l'ajout d'un événement avec tables
    private void traiterAjoutAvecTables(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        System.out.println("=== DÉBUT traiterAjoutAvecTables ===");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        try {
            // Récupération des paramètres du formulaire
            String titre = request.getParameter("titre");
            String artisteGroupe = request.getParameter("artisteGroupe");
            String dateEventStr = request.getParameter("dateEvent");
            String description = request.getParameter("description");
            String statut = request.getParameter("statut");

            // Validation des champs obligatoires
            if (titre == null || titre.trim().isEmpty()) {
                session.setAttribute("ToastAdmErrorNotif", "Le titre est obligatoire.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=ajouter");
                return;
            }
            
            if (dateEventStr == null || dateEventStr.trim().isEmpty()) {
                session.setAttribute("ToastAdmErrorNotif", "La date est obligatoire.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=ajouter");
                return;
            }

            // Vérifier si le titre existe déjà
            if (evenementDAO.titreExists(titre.trim())) {
                session.setAttribute("ToastAdmErrorNotif", "Un événement avec ce titre existe déjà.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=ajouter");
                return;
            }

            // Conversion de la date
            LocalDateTime dateEvent = LocalDateTime.parse(dateEventStr, formatter);
            
            // Vérifier que la date n'est pas dans le passé
            if (dateEvent.isBefore(LocalDateTime.now())) {
                session.setAttribute("ToastAdmErrorNotif", "La date de l'événement ne peut pas être dans le passé.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=ajouter");
                return;
            }

            // Gestion du fichier image
            Part filePart = request.getPart("imageEvenement");
            String mediaPath = null;
            
            if (filePart != null && filePart.getSize() > 0) {
                System.out.println(">>> [INFO] Fichier image détecté, taille: " + filePart.getSize() + " bytes");
                
                // Validation du fichier
                String fileName = getFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    // Vérifier l'extension
                    String extension = getFileExtension(fileName).toLowerCase();
                    if (!ALLOWED_EXTENSIONS.contains(extension)) {
                        session.setAttribute("ToastAdmErrorNotif", 
                            "Format de fichier non supporté. Formats acceptés: JPG, JPEG, PNG, GIF, WEBP");
                        session.setAttribute("toastType", "error");
                        response.sendRedirect("EvenementServlet?action=ajouter");
                        return;
                    }
                    
                    // Vérifier la taille
                    if (filePart.getSize() > MAX_FILE_SIZE) {
                        session.setAttribute("ToastAdmErrorNotif", 
                            "Fichier trop volumineux. Taille max: 10MB");
                        session.setAttribute("toastType", "error");
                        response.sendRedirect("EvenementServlet?action=ajouter");
                        return;
                    }
                    
                    // Sauvegarder le fichier
                    mediaPath = sauvegarderFichier(filePart);
                    System.out.println(">>> [INFO] Fichier sauvegardé: " + mediaPath);
                }
            }

            // Création de l'objet Evenement
            Evenement evenement = new Evenement();
            evenement.setTitre(titre.trim());
            evenement.setArtisteGroupe(artisteGroupe != null ? artisteGroupe.trim() : "");
            evenement.setMediaPath(mediaPath);
            evenement.setDateEvent(dateEvent);
            evenement.setDescription(description != null ? description.trim() : "");
            evenement.setUtilisateurId(utilisateur.getId());
            evenement.setStatut(statut != null ? statut : "VISIBLE");
            evenement.setCapaciteTotale(0); // Initialiser à 0, sera mis à jour après l'ajout des tables

            System.out.println(">>> [DEBUG] Événement à ajouter:");
            System.out.println("  Titre: " + evenement.getTitre());
            System.out.println("  Artiste: " + evenement.getArtisteGroupe());
            System.out.println("  Media: " + evenement.getMediaPath());
            System.out.println("  Date: " + evenement.getDateEvent());
            System.out.println("  User ID: " + evenement.getUtilisateurId());
            System.out.println("  Statut: " + evenement.getStatut());

            // Appel au DAO pour l'ajout en base
            evenementDAO.ajouter(evenement);
            
            // Traiter les tables du formulaire
            traiterTablesEvenement(request, evenement.getId());
            
            // Mettre à jour la capacité totale de l'événement
            evenementDAO.mettreAJourCapaciteTotale(evenement.getId());
            
            // Message de succès et redirection
            session.setAttribute("ToastAdmSuccesNotif", "Événement ajouté avec succès avec ses tables.");
            session.setAttribute("toastType", "success");
            
            response.sendRedirect("EvenementServlet?action=lister");
            System.out.println("=== FIN traiterAjoutAvecTables (SUCCÈS) ===");
            
        } catch (Exception e) {
            System.err.println(">>> [ERROR] Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de l'ajout: " + e.getMessage());
            session.setAttribute("toastType", "error");
            
            response.sendRedirect("EvenementServlet?action=ajouter");
            System.out.println("=== FIN traiterAjoutAvecTables (ERREUR) ===");
        }
    }
    
    // Méthode pour traiter les tables d'un événement
    private void traiterTablesEvenement(HttpServletRequest request, int evenementId) {
        // Récupérer les tables du formulaire
        String[] noms = request.getParameterValues("tableNom[]");
        String[] descriptions = request.getParameterValues("tableDescription[]");
        String[] capacites = request.getParameterValues("tableCapacite[]");
        String[] prix = request.getParameterValues("tablePrix[]");
        
        if (noms == null || noms.length == 0) {
            System.out.println(">>> [INFO] Aucune table à ajouter pour l'événement ID: " + evenementId);
            return;
        }
        
        System.out.println(">>> [INFO] Nombre de tables à traiter: " + noms.length);
        
        for (int i = 0; i < noms.length; i++) {
            try {
                String nom = noms[i];
                String description = (descriptions != null && i < descriptions.length) ? descriptions[i] : "";
                String capaciteStr = (capacites != null && i < capacites.length) ? capacites[i] : "1";
                String prixStr = (prix != null && i < prix.length) ? prix[i] : "0";
                
                // Valider les données
                if (nom == null || nom.trim().isEmpty()) {
                    System.out.println(">>> [WARNING] Nom de table vide à l'index " + i + ", ignoré.");
                    continue;
                }
                
                int capacite;
                BigDecimal prixDecimal;
                
                try {
                    capacite = Integer.parseInt(capaciteStr);
                    if (capacite <= 0) {
                        System.out.println(">>> [WARNING] Capacité invalide pour la table '" + nom + "', ignorée.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(">>> [WARNING] Capacité non numérique pour la table '" + nom + "', ignorée.");
                    continue;
                }
                
                try {
                    prixDecimal = new BigDecimal(prixStr);
                    if (prixDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                        System.out.println(">>> [WARNING] Prix invalide pour la table '" + nom + "', ignorée.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(">>> [WARNING] Prix non numérique pour la table '" + nom + "', ignorée.");
                    continue;
                }
                
                // Créer et ajouter la table
                TypeTableEvenement table = new TypeTableEvenement();
                table.setEvenementId(evenementId);
                table.setNom(nom.trim());
                table.setDescription(description.trim());
                table.setCapacite(capacite);
                table.setPrix(prixDecimal);
                table.setStatut("ACTIF");
                
                int tableId = typeTableDAO.ajouter(table);
                System.out.println(">>> [INFO] Table ajoutée: " + nom + " (ID: " + tableId + ")");
                
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Erreur lors de l'ajout de la table à l'index " + i + ": " + e.getMessage());
            }
        }
    }

    // Traiter la modification d'un événement avec tables
    private void traiterModificationAvecTables(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }

            int id = Integer.parseInt(idStr);
            
            // Vérifier que l'événement existe
            Evenement existant = evenementDAO.getById(id);
            if (existant == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }

            // Récupération des données du formulaire
            String titre = request.getParameter("titre");
            String artisteGroupe = request.getParameter("artisteGroupe");
            String dateEventStr = request.getParameter("dateEvent");
            String description = request.getParameter("description");
            String statut = request.getParameter("statut");
            
            // Récupérer l'option de suppression d'image
            String deleteImage = request.getParameter("deleteImage");

            // Validation des champs obligatoires
            if (titre == null || titre.trim().isEmpty() || 
                dateEventStr == null || dateEventStr.trim().isEmpty()) {
                session.setAttribute("ToastAdmErrorNotif", "Le titre et la date sont obligatoires.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=modifier&id=" + id);
                return;
            }

            // Vérifier si le titre existe déjà (pour un autre événement)
            if (evenementDAO.titreExists(titre.trim()) && !titre.trim().equals(existant.getTitre())) {
                session.setAttribute("ToastAdmErrorNotif", "Un événement avec ce titre existe déjà.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=modifier&id=" + id);
                return;
            }

            // Conversion de la date
            LocalDateTime dateEvent = LocalDateTime.parse(dateEventStr, formatter);
            
            // Vérifier que la date n'est pas dans le passé
            if (dateEvent.isBefore(LocalDateTime.now())) {
                session.setAttribute("ToastAdmErrorNotif", "La date de l'événement ne peut pas être dans le passé.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=modifier&id=" + id);
                return;
            }

            // Gestion du fichier image
            Part filePart = request.getPart("imageEvenement");
            String mediaPath = existant.getMediaPath(); // Conserver l'ancien chemin par défaut
            
            // Si l'utilisateur a demandé la suppression de l'image
            if ("on".equals(deleteImage) || "true".equals(deleteImage)) {
                // Supprimer le fichier physique si il existe
                if (existant.getMediaPath() != null) {
                    deleteImageFile(existant.getMediaPath());
                }
                mediaPath = null;
            }
            // Si un nouveau fichier est uploadé
            else if (filePart != null && filePart.getSize() > 0) {
                System.out.println(">>> [INFO] Nouveau fichier image détecté pour modification");
                
                // Validation du fichier
                String fileName = getFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    // Vérifier l'extension
                    String extension = getFileExtension(fileName).toLowerCase();
                    if (!ALLOWED_EXTENSIONS.contains(extension)) {
                        session.setAttribute("ToastAdmErrorNotif", 
                            "Format de fichier non supporté. Formats acceptés: JPG, JPEG, PNG, GIF, WEBP");
                        session.setAttribute("toastType", "error");
                        response.sendRedirect("EvenementServlet?action=modifier&id=" + id);
                        return;
                    }
                    
                    // Vérifier la taille
                    if (filePart.getSize() > MAX_FILE_SIZE) {
                        session.setAttribute("ToastAdmErrorNotif", 
                            "Fichier trop volumineux. Taille max: 10MB");
                        session.setAttribute("toastType", "error");
                        response.sendRedirect("EvenementServlet?action=modifier&id=" + id);
                        return;
                    }
                    
                    // Supprimer l'ancien fichier s'il existe
                    if (existant.getMediaPath() != null) {
                        deleteImageFile(existant.getMediaPath());
                    }
                    
                    // Sauvegarder le nouveau fichier
                    mediaPath = sauvegarderFichier(filePart);
                    System.out.println(">>> [INFO] Nouveau fichier sauvegardé: " + mediaPath);
                }
            }

            // Mise à jour de l'objet Evenement
            existant.setTitre(titre.trim());
            existant.setArtisteGroupe(artisteGroupe != null ? artisteGroupe.trim() : "");
            existant.setMediaPath(mediaPath);
            existant.setDateEvent(dateEvent);
            existant.setDescription(description != null ? description.trim() : "");
            existant.setUtilisateurId(utilisateur.getId());
            existant.setStatut(statut != null ? statut : "VISIBLE");

            System.out.println(">>> [DEBUG] Événement à modifier:");
            System.out.println("  ID: " + existant.getId());
            System.out.println("  Titre: " + existant.getTitre());
            System.out.println("  Media: " + existant.getMediaPath());

            // Modification dans la base de données
            evenementDAO.modifier(existant);
            
            // Traiter les tables mises à jour
            traiterTablesModifiees(request, id);
            
            // Mettre à jour la capacité totale
            evenementDAO.mettreAJourCapaciteTotale(id);
            
            session.setAttribute("ToastAdmSuccesNotif", "Événement modifié avec succès.");
            session.setAttribute("toastType", "success");
            response.sendRedirect("EvenementServlet?action=lister");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
            response.sendRedirect("EvenementServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la modification: " + e.getMessage());
            session.setAttribute("toastType", "error");
            response.sendRedirect("EvenementServlet?action=lister");
        }
    }
    
    // Méthode pour traiter les tables modifiées
    private void traiterTablesModifiees(HttpServletRequest request, int evenementId) {
        // Récupérer les IDs des tables existantes
        String[] tableIds = request.getParameterValues("tableId[]");
        
        if (tableIds == null) {
            System.out.println(">>> [INFO] Aucune table à mettre à jour pour l'événement ID: " + evenementId);
            return;
        }
        
        // Récupérer les autres paramètres
        String[] noms = request.getParameterValues("tableNom[]");
        String[] descriptions = request.getParameterValues("tableDescription[]");
        String[] capacites = request.getParameterValues("tableCapacite[]");
        String[] prix = request.getParameterValues("tablePrix[]");
        
        for (int i = 0; i < tableIds.length; i++) {
            try {
                String tableIdStr = tableIds[i];
                String nom = (noms != null && i < noms.length) ? noms[i] : "";
                String description = (descriptions != null && i < descriptions.length) ? descriptions[i] : "";
                String capaciteStr = (capacites != null && i < capacites.length) ? capacites[i] : "1";
                String prixStr = (prix != null && i < prix.length) ? prix[i] : "0";
                
                // Si tableId est vide ou "new", c'est une nouvelle table
                if (tableIdStr == null || tableIdStr.trim().isEmpty() || "new".equals(tableIdStr)) {
                    // Ajouter une nouvelle table
                    if (nom != null && !nom.trim().isEmpty()) {
                        TypeTableEvenement nouvelleTable = new TypeTableEvenement();
                        nouvelleTable.setEvenementId(evenementId);
                        nouvelleTable.setNom(nom.trim());
                        nouvelleTable.setDescription(description.trim());
                        nouvelleTable.setCapacite(Integer.parseInt(capaciteStr));
                        nouvelleTable.setPrix(new BigDecimal(prixStr));
                        nouvelleTable.setStatut("ACTIF");
                        
                        typeTableDAO.ajouter(nouvelleTable);
                        System.out.println(">>> [INFO] Nouvelle table ajoutée: " + nom);
                    }
                } else {
                    // Mettre à jour une table existante
                    int tableId = Integer.parseInt(tableIdStr);
                    TypeTableEvenement tableExistante = typeTableDAO.getById(tableId);
                    
                    if (tableExistante != null && tableExistante.getEvenementId() == evenementId) {
                        // Si le nom est vide, supprimer la table
                        if (nom == null || nom.trim().isEmpty()) {
                            typeTableDAO.supprimer(tableId);
                            System.out.println(">>> [INFO] Table supprimée ID: " + tableId);
                        } else {
                            // Mettre à jour la table
                            tableExistante.setNom(nom.trim());
                            tableExistante.setDescription(description.trim());
                            tableExistante.setCapacite(Integer.parseInt(capaciteStr));
                            tableExistante.setPrix(new BigDecimal(prixStr));
                            
                            typeTableDAO.modifier(tableExistante);
                            System.out.println(">>> [INFO] Table modifiée ID: " + tableId);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Erreur lors du traitement de la table à l'index " + i + ": " + e.getMessage());
            }
        }
    }

    // Traiter la suppression (logique) d'un événement
    private void traiterSuppression(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("user");
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            
            // Vérifier que l'événement existe
            Evenement existant = evenementDAO.getById(id);
            if (existant == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }

            // Supprimer le fichier image s'il existe
            if (existant.getMediaPath() != null) {
                deleteImageFile(existant.getMediaPath());
            }
            
            // Supprimer toutes les tables associées
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementIdForAdmin(id);
            for (TypeTableEvenement table : tables) {
                typeTableDAO.supprimer(table.getId());
            }

            // Suppression logique de l'événement
            evenementDAO.supprimer(id, utilisateur.getId());
            
            session.setAttribute("ToastAdmSuccesNotif", "Événement et ses tables supprimés avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la suppression: " + e.getMessage());
            session.setAttribute("toastType", "error");
        }

        response.sendRedirect("EvenementServlet?action=lister");
    }

    // Traiter la désactivation d'un événement
    private void traiterDesactivation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            
            // Vérifier que l'événement existe
            Evenement existant = evenementDAO.getById(id);
            if (existant == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }

            // Désactivation
            evenementDAO.desactiver(id);
            
            // Désactiver aussi toutes les tables
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementIdForAdmin(id);
            for (TypeTableEvenement table : tables) {
                typeTableDAO.desactiver(table.getId());
            }
            
            session.setAttribute("ToastAdmSuccesNotif", "Événement et ses tables désactivés avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la désactivation: " + e.getMessage());
            session.setAttribute("toastType", "error");
        }

        response.sendRedirect("EvenementServlet?action=lister");
    }

    // Traiter la réactivation d'un événement
    private void traiterReactivation(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("EvenementServlet?action=lister");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            
            // Vérifier que l'événement existe
            Evenement existant = evenementDAO.getById(id);
            if (existant == null) {
                session.setAttribute("ToastAdmErrorNotif", "Événement introuvable.");
                session.setAttribute("toastType", "error");
                response.sendRedirect("EvenementServlet?action=lister");
                return;
            }

            // Réactivation
            evenementDAO.reactiver(id);
            
            // Réactiver aussi toutes les tables
            List<TypeTableEvenement> tables = typeTableDAO.getByEvenementIdForAdmin(id);
            for (TypeTableEvenement table : tables) {
                typeTableDAO.reactiver(table.getId());
            }
            
            session.setAttribute("ToastAdmSuccesNotif", "Événement et ses tables réactivés avec succès.");
            session.setAttribute("toastType", "success");
            
        } catch (NumberFormatException e) {
            session.setAttribute("ToastAdmErrorNotif", "ID invalide.");
            session.setAttribute("toastType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("ToastAdmErrorNotif", "Erreur lors de la réactivation: " + e.getMessage());
            session.setAttribute("toastType", "error");
        }

        response.sendRedirect("EvenementServlet?action=lister");
    }
    
    // Sauvegarder un fichier sur le serveur
    private String sauvegarderFichier(Part filePart) throws IOException {
        String fileName = getFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String extension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(UPLOAD_EVENEMENT_IMAGE_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fullPath = uploadDir + File.separator + uniqueFileName;
        filePart.write(fullPath);

        // Retourner le chemin relatif pour la base de données
        return "uploads/evenements/" + uniqueFileName;
    }
    
    // Supprimer un fichier image
    private void deleteImageFile(String mediaPath) {
        try {
            if (mediaPath != null && !mediaPath.isEmpty()) {
                // Extraire le nom de fichier du chemin
                String fileName = mediaPath.substring(mediaPath.lastIndexOf('/') + 1);
                File file = new File(UPLOAD_EVENEMENT_IMAGE_DIR + File.separator + fileName);
                
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println(">>> [INFO] Fichier supprimé: " + file.getAbsolutePath());
                    } else {
                        System.err.println(">>> [ERROR] Impossible de supprimer le fichier: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [ERROR] Erreur lors de la suppression du fichier: " + e.getMessage());
        }
    }
    
    // Obtenir le nom du fichier depuis la Part
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return null;
        
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
        }
        return null;
    }
    
    // Obtenir l'extension d'un fichier
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }
}