package com.spot69.controller;

import com.spot69.dao.ReservationDAO;
import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Reservation;
import com.spot69.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet({"/ReservationServlet", "/blok/ReservationServlet"})
public class ReservationServlet extends HttpServlet {

    private ReservationDAO reservationDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Override
    public void init() throws ServletException {
        reservationDAO = new ReservationDAO();
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("liste-reservations.jsp");
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
                response.sendRedirect("liste-reservations.jsp");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("liste-reservations.jsp");
            return;
        }

        switch (action) {
            case "add":
                handleAddPage(request, response);
                break;
            case "edit":
                handleEditPage(request, response);
                break;
            case "lister":
                handleLister(request, response);
                break;
            case "details":
                handleDetails(request, response);
                break;
            case "par-statut":
                handleParStatut(request, response);
                break;
            case "par-chambre":
                handleParChambre(request, response);
                break;
            case "par-utilisateur":
                handleParUtilisateur(request, response);
                break;
            case "edit-form":
                handleEditForm(request, response);
                break;
            case "details-content":
                handleDetailsContent(request, response);
                break;
            default:
                response.sendRedirect("liste-reservations.jsp");
                break;
        }
    }

    // ======= Handlers POST =======

    private void handleAjouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Reservation reservation = extraireReservationDepuisRequest(request);
            
            // Champs de paiement
            reservation.setPaymentMethod(request.getParameter("paymentMethod"));
            reservation.setPayerName(request.getParameter("payerName"));
            reservation.setPayerPhone(request.getParameter("payerPhone"));
            reservation.setTransactionId(request.getParameter("transactionId"));
            reservation.setPaymentNote(request.getParameter("paymentNote"));
            
            // Déterminer le statut de paiement
            String paymentMethod = reservation.getPaymentMethod();
            if ("SOLDE".equals(paymentMethod)) {
                reservation.setPaymentStatus("completed");
            } else {
                reservation.setPaymentStatus("pending");
            }

            // Vérifier les conflits de réservation
            if (reservationDAO.hasConflict(reservation.getRoomId(), reservation.getStart(), 
                                         reservation.getEnd(), null)) {
                setErrorNotif(request, "Conflit de réservation : la chambre n'est pas disponible pour cette période.");
                response.sendRedirect("ReservationServlet?action=add");
                return;
            }

            // Statut par défaut
            if (reservation.getStatus() == null) {
                reservation.setStatus("en cours");
            }

            String reservationId = reservationDAO.ajouterReservation(reservation);
            if (reservationId != null) {
                setSuccessNotif(request, "Réservation ajoutée avec succès.");
                
                // Gérer le paiement SOLDE
                if ("SOLDE".equals(reservation.getPaymentMethod())) {
                    // TODO: Déduire du solde
                    setSuccessNotif(request, "Réservation ajoutée avec succès. Montant déduit de votre solde.");
                }
            } else {
                setErrorNotif(request, "Erreur lors de l'ajout de la réservation.");
            }
            response.sendRedirect("ReservationServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ReservationServlet?action=add");
        }
    }

    private Reservation extraireReservationDepuisRequest(HttpServletRequest request) throws ParseException {
        Reservation reservation = new Reservation();

        // Informations de base de la réservation
        String roomId = request.getParameter("roomId");
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'roomId' est requis.");
        }
        reservation.setRoomId(roomId);

        // Dates de début et fin
        String startStr = request.getParameter("start");
        String endStr = request.getParameter("end");
        if (startStr == null || endStr == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont requises.");
        }
        reservation.setStart(dateFormat.parse(startStr));
        reservation.setEnd(dateFormat.parse(endStr));

        // Type de réservation
        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de réservation est requis.");
        }
        reservation.setType(type);

        // Titre
        String title = request.getParameter("title");
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est requis.");
        }
        reservation.setTitle(title);

        // Prix total
        String prixTotalStr = request.getParameter("prixTotal");
        if (prixTotalStr == null || prixTotalStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prix total est requis.");
        }
        reservation.setPrixTotal(new BigDecimal(prixTotalStr));

        // Statut
        String status = request.getParameter("status");
        reservation.setStatus(status != null ? status : "en cours");

        // Utilisateur ID
        HttpSession session = request.getSession(false);
        Integer utilisateurId = null;
        
        // Essayer de récupérer depuis la requête d'abord
        String utilisateurIdStr = request.getParameter("userId");
        if (utilisateurIdStr != null && !utilisateurIdStr.trim().isEmpty()) {
            try {
                utilisateurId = Integer.parseInt(utilisateurIdStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID utilisateur invalide.");
            }
        }
        // Sinon utiliser l'utilisateur connecté
        else if (session != null && session.getAttribute("userId") != null) {
            utilisateurId = (Integer) session.getAttribute("userId");
        }
        
        if (utilisateurId == null) {
            throw new IllegalArgumentException("ID utilisateur requis.");
        }
        reservation.setUtilisateurId(utilisateurId);

        // Heure d'arrivée (optionnel)
        reservation.setArrivalTime(request.getParameter("arrivalTime"));

        // Gestion de numberOfDays vs numberOfNights
        if ("sejour".equals(type)) {
            // Pour les séjours, utiliser numberOfDays
            String numberOfDays = request.getParameter("numberOfDays");
            if (numberOfDays != null && !numberOfDays.trim().isEmpty()) {
                reservation.setNumberOfNights(Integer.parseInt(numberOfDays));
            }
        } else if (request.getParameter("numberOfNights") != null) {
            // Pour les nuits uniques
            String numberOfNights = request.getParameter("numberOfNights");
            if (!numberOfNights.trim().isEmpty()) {
                reservation.setNumberOfNights(Integer.parseInt(numberOfNights));
            }
        }
        
        // Autres champs optionnels
        String durationHours = request.getParameter("durationHours");
        if (durationHours != null && !durationHours.trim().isEmpty()) {
            reservation.setDurationHours(Integer.parseInt(durationHours));
        }
        
        String numberOfSlots = request.getParameter("numberOfSlots");
        if (numberOfSlots != null && !numberOfSlots.trim().isEmpty()) {
            reservation.setNumberOfSlots(Integer.parseInt(numberOfSlots));
        }

        return reservation;
    }

    private void handleModifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = request.getParameter("id");
            Reservation reservationExistante = reservationDAO.chercherParId(id);

            if (reservationExistante == null) {
                setErrorNotif(request, "Réservation introuvable.");
                response.sendRedirect("ReservationServlet?action=lister");
                return;
            }

            Reservation updated = extraireReservationDepuisRequest(request);
            updated.setId(id);
            
            // Conserver les champs de paiement existants ou les mettre à jour
            updated.setPaymentMethod(request.getParameter("paymentMethod"));
            updated.setPayerName(request.getParameter("payerName"));
            updated.setPayerPhone(request.getParameter("payerPhone"));
            updated.setTransactionId(request.getParameter("transactionId"));
            updated.setPaymentNote(request.getParameter("paymentNote"));
            
            // Mettre à jour le statut de paiement si fourni
            String paymentStatus = request.getParameter("paymentStatus");
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                updated.setPaymentStatus(paymentStatus);
            } else {
                updated.setPaymentStatus(reservationExistante.getPaymentStatus());
            }

            // Vérifier les conflits en excluant la réservation actuelle
            if (reservationDAO.hasConflict(updated.getRoomId(), updated.getStart(), 
                                         updated.getEnd(), id)) {
                setErrorNotif(request, "Conflit de réservation : la chambre n'est pas disponible pour cette période.");
                response.sendRedirect("ReservationServlet?action=edit&id=" + id);
                return;
            }

            boolean success = reservationDAO.modifierReservation(updated);
            if (success) {
                setSuccessNotif(request, "Réservation modifiée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la modification de la réservation.");
            }
            response.sendRedirect("ReservationServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ReservationServlet?action=lister");
        }
    }

    private void handleSupprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = request.getParameter("id");

            boolean success = reservationDAO.supprimerReservation(id);
            if (success) {
                setSuccessNotif(request, "Réservation supprimée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la suppression de la réservation.");
            }
            response.sendRedirect("ReservationServlet?action=lister");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void handleChangerStatut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = request.getParameter("id");
            String nouveauStatut = request.getParameter("statut");
            
            System.out.println("Changement de statut - ID: " + id + ", Nouveau statut: " + nouveauStatut);

            Reservation reservation = reservationDAO.chercherParId(id);
            if (reservation == null) {
                System.out.println("Réservation non trouvée pour ID: " + id);
                setErrorNotif(request, "Réservation introuvable.");
                response.sendRedirect("ReservationServlet?action=lister");
                return;
            }

            System.out.println("Ancien statut: " + reservation.getStatus());
            reservation.setStatus(nouveauStatut);
            System.out.println("Nouveau statut après set: " + reservation.getStatus());
            
            boolean success = reservationDAO.modifierReservation(reservation);
            
            if (success) {
                System.out.println("Statut modifié avec succès");
                setSuccessNotif(request, "Statut de la réservation modifié avec succès.");
            } else {
                System.out.println("Erreur lors de la modification");
                setErrorNotif(request, "Erreur lors du changement de statut.");
            }
            response.sendRedirect("ReservationServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ReservationServlet?action=lister");
        }
    }

    // ======= Handlers GET =======

    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("ajout-reservation.jsp").forward(request, response);
    }

    private void handleEditPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            Reservation reservation = reservationDAO.chercherParId(id);
            if (reservation != null) {
                request.setAttribute("reservation", reservation);
                request.getRequestDispatcher("modifier-reservation.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Réservation introuvable");
            }
        } else {
            response.sendRedirect("liste-reservations.jsp");
        }
    }

    private void handleLister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Reservation> reservations = reservationDAO.listerReservations();
        request.setAttribute("reservations", reservations);
        request.getRequestDispatcher("liste-reservations.jsp").forward(request, response);
    }

    private void handleDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            Reservation reservation = reservationDAO.chercherParId(id);
            if (reservation != null) {
                request.setAttribute("reservation", reservation);
                request.getRequestDispatcher("details-reservation.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Réservation introuvable");
            }
        } else {
            response.sendRedirect("liste-reservations.jsp");
        }
    }

    private void handleParStatut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String statut = request.getParameter("statut");
        if (statut != null) {
            List<Reservation> reservations = reservationDAO.listerParStatut(statut);
            request.setAttribute("reservations", reservations);
            request.setAttribute("filtreStatut", statut);
            request.getRequestDispatcher("liste-reservations.jsp").forward(request, response);
        } else {
            response.sendRedirect("ReservationServlet?action=lister");
        }
    }

    private void handleParChambre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String roomId = request.getParameter("roomId");
        if (roomId != null) {
            List<Reservation> reservations = reservationDAO.listerParChambre(roomId);
            request.setAttribute("reservations", reservations);
            request.setAttribute("filtreChambre", roomId);
            request.getRequestDispatcher("liste-reservations.jsp").forward(request, response);
        } else {
            response.sendRedirect("ReservationServlet?action=lister");
        }
    }
    
    private void handleParUtilisateur(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String utilisateurIdStr = request.getParameter("utilisateurId");
            String statut = request.getParameter("statut");
            
            if (utilisateurIdStr == null || utilisateurIdStr.trim().isEmpty()) {
                // Si aucun ID utilisateur n'est fourni, utiliser l'utilisateur connecté
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("userId") != null) {
                    utilisateurIdStr = String.valueOf(session.getAttribute("userId"));
                } else {
                    setErrorNotif(request, "ID utilisateur requis.");
                    response.sendRedirect("ReservationServlet?action=lister");
                    return;
                }
            }
            
            int utilisateurId = Integer.parseInt(utilisateurIdStr);
            List<Reservation> reservations;
            
            if (statut != null && !statut.trim().isEmpty()) {
                reservations = reservationDAO.listerParUtilisateurEtStatut(utilisateurId, statut);
            } else {
                reservations = reservationDAO.listerParUtilisateur(utilisateurId);
            }
            
            request.setAttribute("reservations", reservations);
            request.setAttribute("filtreUtilisateur", utilisateurId);
            request.setAttribute("filtreStatut", statut);
            
            request.getRequestDispatcher("liste-reservations.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            setErrorNotif(request, "ID utilisateur invalide.");
            response.sendRedirect("ReservationServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ReservationServlet?action=lister");
        }
    }

    // ======= Méthodes utilitaires =======

    // Ajoutez ces méthodes dans votre ReservationServlet.java

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            if (id != null) {
                Reservation reservation = reservationDAO.chercherParId(id);
                if (reservation != null) {
                    // Récupérer la liste des utilisateurs
                    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                    List<Utilisateur> utilisateurs = utilisateurDAO.findAllVisible();
                    request.setAttribute("utilisateurs", utilisateurs);
                    
                    request.setAttribute("reservation", reservation);
                    request.getRequestDispatcher("edit-reservation-form.jsp").forward(request, response);
                } else {
                    response.getWriter().write("<div class='alert alert-danger'>Réservation introuvable</div>");
                }
            } else {
                response.getWriter().write("<div class='alert alert-danger'>ID de réservation requis</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<div class='alert alert-danger'>Erreur: " + e.getMessage() + "</div>");
        }
    }

    private void handleDetailsContent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            if (id != null) {
                Reservation reservation = reservationDAO.chercherParId(id);
                if (reservation != null) {
                    // Récupérer les informations utilisateur
                    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                    Utilisateur utilisateur = null;
                    if (reservation.getUtilisateurId() != null) {
                        utilisateur = utilisateurDAO.findById(reservation.getUtilisateurId());
                    }
                    
                    request.setAttribute("reservation", reservation);
                    request.setAttribute("utilisateur", utilisateur);
                    request.getRequestDispatcher("reservation-details.jsp").forward(request, response);
                } else {
                    response.getWriter().write("<div class='alert alert-danger'>Réservation introuvable</div>");
                }
            } else {
                response.getWriter().write("<div class='alert alert-danger'>ID de réservation requis</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<div class='alert alert-danger'>Erreur: " + e.getMessage() + "</div>");
        }
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