<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="com.spot69.model.Reservation,com.spot69.model.Utilisateur,java.text.SimpleDateFormat"%>

<%
Reservation reservation = (Reservation) request.getAttribute("reservation");
Utilisateur utilisateur = (Utilisateur) request.getAttribute("utilisateur");
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
%>

<style>
.reservation-details .info-row {
    margin-bottom: 15px;
    padding-bottom: 15px;
    border-bottom: 1px solid #444;
}
.reservation-details .info-label {
    font-weight: 600;
    color: #ffffff;
    min-width: 200px;
    display: inline-block;
}
.reservation-details .info-value {
    color: #ffffff;
}
.reservation-details .section-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: none;
}
.info-container {
    background-color: #212529;
    padding: 20px;
    border-radius: 8px;
    margin-top: 20px;
    color: #ffffff;
    border: 1px solid #444;
}
.status-badge {
    font-size: 0.9rem;
    padding: 5px 10px;
    border-radius: 20px;
}
/* Styles pour les alertes */
.alert-warning {
    background-color: #fef3c7;
    border-color: #f59e0b;
    color: #92400e;
}
.alert-info {
    background-color: #dbeafe;
    border-color: #3b82f6;
    color: #1e40af;
}
/* Styles pour le code */
code {
    color: #fbbf24;
    background-color: #374151;
    padding: 2px 5px;
    border-radius: 3px;
}
/* Style pour les badges dans le tableau */
.type-badge-sejour { background-color: #28a745; color: white; }
.type-badge-nuit { background-color: #007bff; color: white; }
.type-badge-jour { background-color: #ffc107; color: #000; }
.type-badge-moment { background-color: #6f42c1; color: white; }
.badge-info { 
    background-color: #17a2b8; 
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.85rem;
}
/* Styles pour les titres et textes en gras */
strong {
    color: #ffffff;
}
/* Style pour tout le contenu textuel dans reservation-details */
.reservation-details,
.reservation-details span,
.reservation-details div,
.reservation-details p,
.reservation-details h6 {
    color: #ffffff !important;
}
.row-spacing {
    margin-bottom: 10px;
}
</style>

<div class="reservation-details">
    <!-- Toutes les informations dans un seul cadre -->
    <div class="info-container">
        <h6 class="section-title" style="color: #ffffff; border-bottom: none;">Détails de la réservation</h6>
        
        <div class="row">
            <div class="col-md-6">
                <!-- Informations de base -->
                <div class="info-row">
                    <!-- <span class="info-label" style="color: #ffffff;">Titre :</span> -->
                    <span class="info-value" style="color: #ffffff;"><%= reservation.getTitle() %></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Chambre :</span>
                    <span class="info-value" style="color: #ffffff;">Chambre <%= reservation.getRoomId() %></span>
                </div>
                
                <div class="info-row">
                    <span class="" style="color: #ffffff;">Type :</span>
                    <span class="" style="color: #ffffff;">
                        <span class="badge 
                            <% switch(reservation.getType()) {
                                case "sejour": out.print(""); break;
                                case "nuit": out.print(""); break;
                                case "jour": out.print(""); break;
                                case "moment": out.print(""); break;
                            } %>">
                            <%= reservation.getType().toUpperCase() %>
                        </span>
                    </span>
                </div>
                
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Statut :</span>
                    <span class="info-value" style="color: #ffffff;">
                        <%
                        String statusClass = "";
                        String status = reservation.getStatus();
                        if (status != null) {
                            switch(status.toLowerCase()) {
                                case "confirmé":
                                case "confirmed":
                                    statusClass = ""; 
                                    status = "CONFIRMÉ";
                                    break;
                                case "en cours":
                                case "pending":
                                    statusClass = ""; 
                                    status = "EN ATTENTE";
                                    break;
                                case "annulé":
                                case "cancelled":
                                    statusClass = ""; 
                                    status = "ANNULÉ";
                                    break;
                            }
                        }
                        %>
                        <span class="<%= statusClass %>"><%= status %></span>
                    </span>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Prix Total :</span>
                    <span class="info-value" style="color: #ffffff;"><strong><%= reservation.getPrixTotal() %> HTG</strong></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Date de création :</span>
                    <span class="info-value" style="color: #ffffff;"><%= sdf.format(reservation.getCreatedAt()) %></span>
                </div>
                
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Période :</span>
                    <span class="info-value" style="color: #ffffff;">
                        Du <strong><%= sdf.format(reservation.getStart()) %></strong><br>
                        Au <strong><%= sdf.format(reservation.getEnd()) %></strong>
                    </span>
                </div>
            </div>
        </div>

        <!-- Détails selon le type -->
        <div class="row">
            <div class="col-md-12">
                <% if ("sejour".equals(reservation.getType())) { %>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Détails du séjour :</span>
                    <span class="info-value" style="color: #ffffff;">
                        <%= reservation.getNumberOfNights() != null ? reservation.getNumberOfNights() + " nuit(s)" : "" %>
                        <% if (reservation.getArrivalTime() != null) { %>
                        | Arrivée prévue : <%= reservation.getArrivalTime() %>
                        <% } %>
                    </span>
                </div>
                <% } else if ("moment".equals(reservation.getType())) { %>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Détails du moment :</span>
                    <span class="info-value" style="color: #ffffff;">
                        <%= reservation.getNumberOfSlots() != null ? reservation.getNumberOfSlots() + " créneau(x)" : "" %>
                        <% if (reservation.getDurationHours() != null) { %>
                        | Durée : <%= reservation.getDurationHours() + " heure(s)" %>
                        <% } %>
                    </span>
                </div>
                <% } %>
            </div>
        </div>

        

        <!-- Informations utilisateur -->
        <h6 class="section-title" style="color: #ffffff; border-bottom: none; margin-top: 20px;">Informations du client</h6>
        <hr style="border-color: #444; margin: 20px 0;">
        
        <% if (utilisateur != null) { %>
        <div class="row">
            <div class="col-md-6">
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Nom d'utilisateur :</span>
                    <span class="info-value" style="color: #ffffff;"><%= utilisateur.getLogin() %></span>
                </div>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Email :</span>
                    <span class="info-value" style="color: #ffffff;"><%= utilisateur.getEmail() != null ? utilisateur.getEmail() : "Non spécifié" %></span>
                </div>
            </div>
            <div class="col-md-6">
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">ID Utilisateur :</span>
                    <span class="info-value" style="color: #ffffff;"><%= utilisateur.getId() %></span>
                </div>
                <% if (utilisateur.getTelephone() != null) { %>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Téléphone :</span>
                    <span class="info-value" style="color: #ffffff;"><%= utilisateur.getTelephone() %></span>
                </div>
                <% } %>
            </div>
        </div>
        <% } else { %>
        <div class="alert alert-warning">
            <i class="fe fe-alert-triangle mr-2"></i>Informations utilisateur non disponibles
        </div>
        <% } %>

        

        <!-- Informations de paiement -->
        <h6 class="section-title" style="color: #ffffff; border-bottom: none; margin-top: 20px;">Informations de paiement</h6>
        <hr style="border-color: #444; margin: 20px 0;">
        <div class="row">
            <div class="col-md-6">
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Méthode de paiement :</span>
                    <span class="info-value" style="color: #ffffff;">
                        <% 
                        String paymentMethod = reservation.getPaymentMethod();
                        String paymentMethodDisplay = "Non spécifié";
                        if (paymentMethod != null) {
                            switch(paymentMethod) {
                                case "SOLDE": paymentMethodDisplay = "Solde utilisateur"; break;
                                case "MONCASH": paymentMethodDisplay = "MonCash"; break;
                                case "NATCASH": paymentMethodDisplay = "NatCash"; break;
                                default: paymentMethodDisplay = paymentMethod;
                            }
                        }
                        %>
                        <%= paymentMethodDisplay %>
                    </span>
                </div>
                
                <% if (reservation.getPaymentStatus() != null) { %>
<%--                 <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Statut du paiement :</span>
                    <span class="info-value" style="color: #ffffff;">
                        <span class="badge 
                            <% if ("completed".equals(reservation.getPaymentStatus())) { %>
                                badge-success
                            <% } else if ("pending".equals(reservation.getPaymentStatus())) { %>
                                badge-warning
                            <% } else { %>
                                badge-secondary
                            <% } %>">
                            <% 
                            String paymentStatusDisplay = reservation.getPaymentStatus();
                            if ("completed".equals(paymentStatusDisplay)) {
                                paymentStatusDisplay = "Complété";
                            } else if ("pending".equals(paymentStatusDisplay)) {
                                paymentStatusDisplay = "En attente";
                            } else if ("failed".equals(paymentStatusDisplay)) {
                                paymentStatusDisplay = "Échoué";
                            }
                            %>
                            <%= paymentStatusDisplay %>
                        </span>
                    </span>
                </div> --%>
                <% } %>
            </div>
            <div class="col-md-6">
                <% if (reservation.getPayerName() != null) { %>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Nom du payeur :</span>
                    <span class="info-value" style="color: #ffffff;"><%= reservation.getPayerName() %></span>
                </div>
                <% } %>
                <% if (reservation.getPayerPhone() != null) { %>
                <div class="info-row">
                    <span class="info-label" style="color: #ffffff;">Téléphone du payeur :</span>
                    <span class="info-value" style="color: #ffffff;"><%= reservation.getPayerPhone() %></span>
                </div>
                <% } %>
            </div>
        </div>
        
        <% if (reservation.getTransactionId() != null) { %>
        <div class="info-row">
            <span class="info-label" style="color: #ffffff;">ID Transaction :</span>
            <span class="info-value" style="color: #ffffff;"><code style="color: #fbbf24;"><%= reservation.getTransactionId() %></code></span>
        </div>
        <% } %>
        
        <% if (reservation.getPaymentNote() != null) { %>
        <div class="info-row">
            <span class="info-label" style="color: #ffffff;">Note de paiement :</span>
            <span class="info-value" style="color: #ffffff;"><%= reservation.getPaymentNote() %></span>
        </div>
        <% } %>
        
        <% if (reservation.getPaymentMethod() == null && reservation.getPaymentStatus() == null) { %>
        <div class="alert alert-info">
            <i class="fe fe-info mr-2"></i>Aucune information de paiement disponible
        </div>
        <% } %>
    </div>
</div>

<!-- Si vous voulez que TOUT le texte soit blanc (y compris dans les alertes), ajoutez ce style supplémentaire -->
<style>
.reservation-details * {
    color: #ffffff !important;
}
.alert-warning,
.alert-info {
    color: #ffffff !important;
}
.alert-warning {
    background-color: rgba(245, 158, 11, 0.2) !important;
    border-color: #f59e0b !important;
}
.alert-info {
    background-color: rgba(59, 130, 246, 0.2) !important;
    border-color: #3b82f6 !important;
}
.info-container .badge-success { background-color: #28a745 !important; color: white !important; }
.info-container .badge-warning { background-color: #ffc107 !important; color: #000 !important; }
.info-container .badge-danger { background-color: #dc3545 !important; color: white !important; }
.info-container .badge-secondary { background-color: #6c757d !important; color: white !important; }
</style>