<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="com.spot69.model.ReservationEvenement,com.spot69.model.Evenement,java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<meta charset="UTF-8">

<style>
    .detail-card {
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;
    }
    .detail-label {
        font-weight: 600;
        color: #666;
        margin-bottom: 5px;
    }
    .detail-value {
        font-size: 16px;
        color: #333;
    }
    .payment-info {
        background: #f8f9fa;
        border-left: 4px solid #007bff;
        padding: 15px;
        border-radius: 5px;
    }
    .timeline {
        position: relative;
        padding-left: 30px;
    }
    .timeline:before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 2px;
        background: #dee2e6;
    }
    .timeline-item {
        position: relative;
        margin-bottom: 20px;
    }
    .timeline-item:before {
        content: '';
        position: absolute;
        left: -30px;
        top: 5px;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: #007bff;
    }
</style>

<%
ReservationEvenement reservation = (ReservationEvenement) request.getAttribute("reservation");
if (reservation == null) {
    response.sendRedirect("EvenementServlet?action=reservations");
    return;
}
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails réservation #<%= reservation.getId() %></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.3/css/all.min.css">
</head>
<body>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-12">
            <!-- En-tête -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="h3 mb-2">
                        <i class="fas fa-ticket-alt"></i> Réservation #<%= reservation.getId() %>
                    </h2>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="EvenementServlet?action=lister">Événements</a></li>
                            <li class="breadcrumb-item"><a href="EvenementServlet?action=reservations">Réservations</a></li>
                            <li class="breadcrumb-item active">Détails</li>
                        </ol>
                    </nav>
                </div>
                <div>
                    <a href="javascript:window.print()" class="btn btn-outline-secondary">
                        <i class="fas fa-print"></i> Imprimer
                    </a>
                    <a href="EvenementServlet?action=reservations" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Retour
                    </a>
                </div>
            </div>
            
            <!-- Statut -->
            <div class="alert 
                <%= "CONFIRMEE".equals(reservation.getStatut()) ? "alert-success" : 
                   "EN_ATTENTE".equals(reservation.getStatut()) ? "alert-warning" : "alert-danger" %> 
                mb-4">
                <div class="d-flex align-items-center">
                    <i class="fas 
                        <%= "CONFIRMEE".equals(reservation.getStatut()) ? "fa-check-circle" : 
                           "EN_ATTENTE".equals(reservation.getStatut()) ? "fa-clock" : "fa-times-circle" %> 
                        fa-2x mr-3"></i>
                    <div>
                        <h4 class="alert-heading mb-1">
                            Réservation <%= reservation.getStatut().toLowerCase() %>
                        </h4>
                        <% if ("CONFIRMEE".equals(reservation.getStatut()) && reservation.getDateValidation() != null) { %>
                        <p class="mb-0">Validée le <fmt:formatDate value="<%= reservation.getDateValidation() %>" pattern="dd/MM/yyyy à HH:mm" /></p>
                        <% } else if ("ANNULEE".equals(reservation.getStatut())) { %>
                        <p class="mb-0">Annulée</p>
                        <% } else { %>
                        <p class="mb-0">En attente de validation</p>
                        <% } %>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <!-- Informations client -->
                <div class="col-lg-6">
                    <div class="detail-card">
                        <h5 class="mb-4">
                            <i class="fas fa-user"></i> Informations client
                        </h5>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">ID Client</div>
                                <div class="detail-value">#<%= reservation.getUtilisateurId() %></div>
                            </div>
                            <% if (reservation.getUtilisateur() != null) { %>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Nom complet</div>
                                <div class="detail-value">
                                    <%= reservation.getUtilisateur().getNom() %> <%= reservation.getUtilisateur().getPrenom() %>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Email</div>
                                <div class="detail-value">
                                    <%= reservation.getUtilisateur().getEmail() != null ? reservation.getUtilisateur().getEmail() : "Non renseigné" %>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Téléphone</div>
                                <div class="detail-value">
                                    <%= reservation.getUtilisateur().getTelephone() != null ? reservation.getUtilisateur().getTelephone() : "Non renseigné" %>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                
                <!-- Informations événement -->
                <div class="col-lg-6">
                    <div class="detail-card">
                        <h5 class="mb-4">
                            <i class="fas fa-calendar-alt"></i> Informations événement
                        </h5>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">ID Événement</div>
                                <div class="detail-value">#<%= reservation.getEvenementId() %></div>
                            </div>
                            <% if (reservation.getEvenement() != null) { %>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Titre</div>
                                <div class="detail-value">
                                    <a href="EvenementServlet?action=details&id=<%= reservation.getEvenementId() %>">
                                        <%= reservation.getEvenement().getTitre() %>
                                    </a>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Date</div>
                                <div class="detail-value">
                                    <fmt:formatDate value="<%= reservation.getEvenement().getDateEventAsDate() %>" pattern="dd/MM/yyyy HH:mm" />
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Prix unitaire</div>
                                <div class="detail-value">
                                    <fmt:formatNumber value="<%= reservation.getEvenement().getCapaciteTotale()) %>" type="currency" currencyCode="HTG" />
                                </div>
                            </div>
                            <% } %>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Quantité tickets</div>
                                <div class="detail-value"><%= reservation.getQuantiteTickets() %></div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <div class="detail-label">Montant total</div>
                                <div class="detail-value">
                                    <strong><fmt:formatNumber value="<%= reservation.getMontantTotal() %>" type="currency" currencyCode="HTG" /></strong>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Informations paiement -->
                <div class="col-lg-12">
                    <div class="detail-card">
                        <h5 class="mb-4">
                            <i class="fas fa-credit-card"></i> Informations paiement
                        </h5>
                        <div class="payment-info">
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <div class="detail-label">Moyen de paiement</div>
                                    <div class="detail-value">
                                        <span class="badge badge-primary">
                                            <%= reservation.getMoyenPaiement() %>
                                        </span>
                                    </div>
                                </div>
                                
                                <% if (reservation.getNomPersonne() != null && !reservation.getNomPersonne().isEmpty()) { %>
                                <div class="col-md-4 mb-3">
                                    <div class="detail-label">Nom de la personne</div>
                                    <div class="detail-value"><%= reservation.getNomPersonne() %></div>
                                </div>
                                <% } %>
                                
                                <% if (reservation.getNumeroTransaction() != null && !reservation.getNumeroTransaction().isEmpty()) { %>
                                <div class="col-md-4 mb-3">
                                    <div class="detail-label">Numéro transaction</div>
                                    <div class="detail-value"><%= reservation.getNumeroTransaction() %></div>
                                </div>
                                <% } %>
                                
                                 <% if (reservation.getNumeroTransfert() != null && !reservation.getNumeroTransfert().isEmpty()) { %>
                                <div class="col-md-4 mb-3">
                                    <div class="detail-label">Numéro transfert</div>
                                    <div class="detail-value"><%= reservation.getNumeroTransfert() %></div>
                                </div>
                                <% } %>
                                
                                <% if ("SOLDE".equals(reservation.getMoyenPaiement())) { %>
                                <div class="col-md-12">
                                    <div class="alert alert-info mt-3">
                                        <i class="fas fa-info-circle"></i> Paiement effectué avec le solde du compte client.
                                    </div>
                                </div>
                                <% } else if ("EN_ATTENTE".equals(reservation.getStatut()) && 
                                           ("MONCASH".equals(reservation.getMoyenPaiement()) || 
                                            "NATCASH".equals(reservation.getMoyenPaiement()) || 
                                            "VIREMENT".equals(reservation.getMoyenPaiement()))) { %>
                                <div class="col-md-12">
                                    <div class="alert alert-warning mt-3">
                                        <i class="fas fa-exclamation-triangle"></i> 
                                        Paiement électronique en attente de validation.
                                    </div>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Timeline -->
                <div class="col-lg-12">
                    <div class="detail-card">
                        <h5 class="mb-4">
                            <i class="fas fa-history"></i> Historique
                        </h5>
                        <div class="timeline">
                            <div class="timeline-item">
                                <div class="detail-label">Date réservation</div>
                                <div class="detail-value">
                                    <fmt:formatDate value="<%= reservation.getDateReservation() %>" pattern="dd/MM/yyyy à HH:mm:ss" />
                                </div>
                            </div>
                            
                            <% if (reservation.getDateValidation() != null) { %>
                            <div class="timeline-item">
                                <div class="detail-label">Date validation</div>
                                <div class="detail-value">
                                    <fmt:formatDate value="<%= reservation.getDateValidation() %>" pattern="dd/MM/yyyy à HH:mm:ss" />
                                    <% if (reservation.getValidePar() != null) { %>
                                    <br><small>par utilisateur #<%= reservation.getValidePar() %></small>
                                    <% } %>
                                </div>
                            </div>
                            <% } %>
                            
                            <% if (reservation.getCreatedAt() != null) { %>
                            <div class="timeline-item">
                                <div class="detail-label">Date création</div>
                                <div class="detail-value">
                                    <fmt:formatDate value="<%= reservation.getCreatedAt() %>" pattern="dd/MM/yyyy à HH:mm:ss" />
                                </div>
                            </div>
                            <% } %>
                            
                            <% if (reservation.getUpdatedAt() != null) { %>
                            <div class="timeline-item">
                                <div class="detail-label">Dernière modification</div>
                                <div class="detail-value">
                                    <fmt:formatDate value="<%= reservation.getUpdatedAt() %>" pattern="dd/MM/yyyy à HH:mm:ss" />
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                
                <!-- Notes -->
                <% if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) { %>
                <div class="col-lg-12">
                    <div class="detail-card">
                        <h5 class="mb-4">
                            <i class="fas fa-sticky-note"></i> Notes
                        </h5>
                        <div class="alert alert-light">
                            <%= reservation.getNotes().replace("\n", "<br>") %>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            
            <!-- Actions -->
            <div class="detail-card mt-4">
                <h5 class="mb-4">
                    <i class="fas fa-cogs"></i> Actions
                </h5>
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <% if ("EN_ATTENTE".equals(reservation.getStatut()) && 
                               ("MONCASH".equals(reservation.getMoyenPaiement()) || 
                                "NATCASH".equals(reservation.getMoyenPaiement()) || 
                                "VIREMENT".equals(reservation.getMoyenPaiement()))) { %>
                        <button class="btn btn-success btn-block" onclick="validerReservation()">
                            <i class="fas fa-check"></i> Valider la réservation
                        </button>
                        <% } %>
                    </div>
                    
                    <div class="col-md-4 mb-3">
                        <% if (!"ANNULEE".equals(reservation.getStatut())) { %>
                        <button class="btn btn-danger btn-block" onclick="annulerReservation()">
                            <i class="fas fa-times"></i> Annuler la réservation
                        </button>
                        <% } %>
                    </div>
                    
                    <div class="col-md-4 mb-3">
                        <a href="EvenementServlet?action=reservations" class="btn btn-secondary btn-block">
                            <i class="fas fa-arrow-left"></i> Retour aux réservations
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
function validerReservation() {
    if (confirm('Valider cette réservation ? Une notification sera envoyée au client.')) {
        window.location.href = 'EvenementServlet?action=validerreservation&id=<%=reservation.getId() %>';
    }
}

function annulerReservation() {
    var raison = prompt('Veuillez saisir la raison de l\'annulation :');
    if (raison && raison.trim() !== '') {
        if (confirm('Annuler cette réservation ? Une notification sera envoyée au client.')) {
            window.location.href = 'EvenementServlet?action=annulerreservation&id=<%= reservation.getId() %>&raison=' + encodeURIComponent(raison);
        }
    } else if (raison !== null) {
        alert('La raison de l\'annulation est requise.');
    }
}

// Impression optimisée
function setupPrint() {
    var originalTitle = document.title;
    document.title = 'Reservation_<%= reservation.getId() %>_<%=new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) %>';
    
    window.print();
    
    setTimeout(function() {
        document.title = originalTitle;
    }, 1000);
}

// Raccourci clavier pour impression
document.addEventListener('keydown', function(e) {
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        setupPrint();
    }
});
</script>

</body>
</html> --%>