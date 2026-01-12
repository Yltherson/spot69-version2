<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
 import="com.spot69.model.Evenement,java.text.SimpleDateFormat,java.util.List,com.spot69.model.ReservationEvenement,com.spot69.model.TypeTableEvenement"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<meta charset="UTF-8">

<style>
    .event-header {
        color: white;
        padding: 2rem;
        border-radius: 10px;
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }
    .event-image-detail {
        width: 100%;
        max-height: 400px;
        object-fit: cover;
        border-radius: 10px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
    .info-card {
        border-left: 4px solid #667eea;
        padding-left: 1rem;
        margin-bottom: 1.5rem;
    }
    .statut-badge {
        font-size: 0.9rem;
        padding: 5px 12px;
    }
    .statut-VISIBLE { background-color: #28a745; }
    .statut-HIDDEN { background-color: #6c757d; }
    .statut-COMPLETED { background-color: #007bff; }
    .statut-CANCELLED { background-color: #dc3545; }
    
    .stat-card {
        border-radius: 8px;
        padding: 15px;
        text-align: center;
        color: white;
        margin-bottom: 15px;
    }
   /*  .stat-total { background: linear-gradient(135deg, #333333 0%, #6d6d6d 100%) }
    .stat-confirmed { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); }
    .stat-pending { background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%); }
    .stat-cancelled { background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%); } */
    
    .action-btn-group {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-top: 10px;
    }
    
    .badge-reservation {
        font-size: 12px;
        padding: 4px 8px;
        margin-right: 5px;
    }
    .statut-EN_ATTENTE { background-color: #ffc107; color: black; }
    .statut-CONFIRMEE { background-color: #28a745; color: white; }
    .statut-ANNULEE { background-color: #dc3545; color: white; }
    
    .recent-reservation {
        border-left: 3px solid #007bff;
        padding-left: 10px;
        margin-bottom: 10px;
    }
    
    .table-item {
        background: #f8f9fa;
        border-radius: 8px;
        padding: 12px;
        margin-bottom: 10px;
        border-left: 4px solid #007bff;
    }
    .table-price {
        font-weight: bold;
        color: #28a745;
    }
    .table-capacity {
        color: #6c757d;
        font-size: 0.9rem;
    }
</style>

<%
Evenement evenement = (Evenement) request.getAttribute("evenement");
List<ReservationEvenement> recentReservations = (List<ReservationEvenement>) request.getAttribute("recentReservations");
List<TypeTableEvenement> tables = (List<TypeTableEvenement>) request.getAttribute("tables");

// Récupérer les tables depuis l'événement si non fourni directement
if (tables == null && evenement != null) {
    tables = evenement.getTypesTables();
}

// Trouver le prix minimum et maximum des tables
double prixMin = 0;
double prixMax = 0;
if (tables != null && !tables.isEmpty()) {
    prixMin = Double.MAX_VALUE;
    for (TypeTableEvenement table : tables) {
        double prix = table.getPrix().doubleValue();
        if (prix < prixMin) prixMin = prix;
        if (prix > prixMax) prixMax = prix;
    }
}

// Statistiques
int totalReservations = 0;
int pendingReservations = 0;
int confirmedReservations = 0;
int cancelledReservations = 0;
double totalRevenue = 0.0;

if (request.getAttribute("stats") != null) {
    Object[] stats = (Object[]) request.getAttribute("stats");
    if (stats != null && stats.length >= 4) {
        totalReservations = (int) stats[0];
        confirmedReservations = (int) stats[1];
        pendingReservations = (int) stats[2];
        cancelledReservations = (int) stats[3];
        if (stats.length >= 5 && stats[4] != null) {
            totalRevenue = (double) stats[4];
        }
    }
}

if (evenement == null) {
    response.sendRedirect("EvenementServlet?action=lister");
    return;
}
SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy 'à' HH'h'mm");
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
%>

<!DOCTYPE html>
<jsp:include page="header.jsp" />
<jsp:include page="topbar.jsp" />
<jsp:include page="sidebar.jsp" />

<main role="main" class="main-content">
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <!-- En-tête -->
        <div class="event-header">
          <div class="row align-items-center">
            <div class="col-md-8">
              <h1 class="h2 mb-3"><i class="fe fe-calendar fe-24"></i> <%= evenement.getTitre() %></h1>
              <% if (evenement.getArtisteGroupe() != null && !evenement.getArtisteGroupe().isEmpty()) { %>
              <h3 class="h4 mb-4">
                <i class="fe fe-music fe-16"></i> <%= evenement.getArtisteGroupe() %>
              </h3>
              <% } %>
              
              <div class="d-flex align-items-center flex-wrap gap-3">
                <div class="d-flex align-items-center bg-white rounded-pill px-3 py-1">
                  <i class="fe fe-calendar fe-16 text-primary mr-2"></i>
                  <span class=""><fmt:formatDate value="<%= evenement.getDateEventAsDate() %>" pattern="EEEE dd MMMM yyyy" /></span>
                </div>
                <div class="ml-3 d-flex align-items-center bg-white rounded-pill px-3 py-1">
                  <i class="fe fe-clock fe-16 text-primary mr-2"></i>
                  <span class=""><fmt:formatDate value="<%= evenement.getDateEventAsDate() %>" pattern="HH'h'mm" /></span>
                </div>
                <div class="ml-3 d-flex align-items-center">
                  <span class="badge statut-badge statut-<%= evenement.getStatut() %>">
                    <i class="fe fe-<%= "VISIBLE".equals(evenement.getStatut()) ? "eye" : "eye-off" %> fe-12"></i>
                    <%= evenement.getStatut() %>
                  </span>
                </div>
              </div>
            </div>
            <div class="col-md-4 text-right">
              <!-- Afficher la fourchette de prix des tables -->
              <% if (tables != null && !tables.isEmpty()) { %>
                <div class="bg-white text-dark rounded-pill d-inline-block px-4 py-2 shadow">
                  <i class="fe fe-tag fe-16 mr-1"></i>
                  <% if (prixMin == prixMax) { %>
                    <fmt:formatNumber value="<%= prixMin %>" type="currency" currencyCode="HTG" />
                  <% } else { %>
                    À partir de <fmt:formatNumber value="<%= prixMin %>" type="currency" currencyCode="HTG" />
                  <% } %>
                  <small class="d-block text-muted"><%= tables.size() %> type(s) de table</small>
                </div>
              <% } else { %>
                <div class="bg-warning  rounded-pill d-inline-block px-4 py-2 shadow">
                  <i class="fe fe-alert-triangle fe-16 mr-1"></i>
                  PAS DE TABLES
                </div>
              <% } %>
            </div>
          </div>
        </div>
        
        <div class="row">
          <!-- Colonne gauche : Image -->
          <div class="col-lg-4">
            <div class="card shadow mb-4">
              <div class="card-body p-0">
<%
    String ctx = request.getContextPath();
    String imagePath = ctx + "/images/default/default.jpg"; // valeur par défaut

    if (evenement != null) {
        String imageUrl = evenement.getFullMediaPath();

        if (imageUrl != null && imageUrl.startsWith("/uploads/evenements/")) {
            imagePath = ctx + "/images/evenements/" 
                      + imageUrl.substring("/uploads/evenements/".length());
        }
    }
%>
                <img src="<%= imagePath %>"
                     alt="<%= evenement != null ? evenement.getTitre() : "Événement" %>"
                     class="event-image-detail">
              </div>
            </div>
            
            <!-- Informations rapides -->
            <div class="card shadow">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-info fe-16"></i> Informations
                </h5>
              </div>
              <div class="card-body">
                <div class="info-card">
                  <h6 class="text-muted mb-2">Créé par</h6>
                  <p class="mb-0">Utilisateur #<%= evenement.getUtilisateurId() %></p>
                  <small class="text-muted">
                    Le <fmt:formatDate value="<%= evenement.getCreatedAtAsDate() %>" pattern="dd/MM/yyyy 'à' HH:mm" />
                  </small>
                </div>
                
                <div class="info-card">
                  <h6 class="text-muted mb-2">Dernière modification</h6>
                  <p class="mb-0">
                    <% if (evenement.getUpdatedAtAsDate() != null) { %>
                    <fmt:formatDate value="<%= evenement.getUpdatedAtAsDate() %>" pattern="dd/MM/yyyy HH:mm" />
                    <% } else { %>
                    <span class="text-muted">Jamais modifié</span>
                    <% } %>
                  </p>
                </div>
                
                
                
                <div class="info-card">
                  <h6 class="text-muted mb-2">Capacité</h6>
                  <p class="mb-0">
                    <i class="fe fe-users fe-12 text-info"></i> 
                    <% if (evenement.getCapaciteTotale() > 0) { %>
                      <%= evenement.getCapaciteTotale() %> personnes
                      <% if (tables != null && !tables.isEmpty()) { %>
                        <small class="d-block text-muted">(<%= tables.size() %> type(s) de table)</small>
                      <% } %>
                    <% } else { %>
                      <span class="text-muted">Non spécifiée</span>
                    <% } %>
                  </p>
                </div>
              </div>
            </div>
            
            <!-- Liste des tables -->
            <% if (tables != null && !tables.isEmpty()) { %>
            <div class="card shadow mt-4">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-grid fe-16"></i> Types de tables disponibles
                </h5>
              </div>
              <div class="card-body">
                <% for (TypeTableEvenement table : tables) { %>
                <div class="table-item">
                  <div class="d-flex justify-content-between align-items-center mb-1">
                    <strong><%= table.getNom() %></strong>
                    <span class="table-price">
                      <fmt:formatNumber value="<%= table.getPrix() %>" type="currency" currencyCode="HTG" />
                    </span>
                  </div>
                  <% if (table.getDescription() != null && !table.getDescription().isEmpty()) { %>
                  <p class="mb-1 small"><%= table.getDescription() %></p>
                  <% } %>
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="table-capacity">
                      <i class="fe fe-users fe-12"></i> <%= table.getCapacite() %> personne(s)
                    </span>
                    <span class="badge <%= "ACTIF".equals(table.getStatut()) ? "bg-success" : "bg-warning" %>">
                      <%= table.getStatut() %>
                    </span>
                  </div>
                </div>
                <% } %>
                <div class="text-center mt-3">
                  <a href="EvenementServlet?action=gerertables&id=<%= evenement.getId() %>" 
                     class="btn btn-sm btn-outline-primary">
                    <i class="fe fe-settings fe-12"></i> Gérer les tables
                  </a>
                </div>
              </div>
            </div>
            <% } %>
            
            <!-- Réservations récentes -->
            <% if (recentReservations != null && !recentReservations.isEmpty()) { %>
            <div class="card shadow mt-4">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-clock fe-16"></i> Réservations récentes
                </h5>
              </div>
              <div class="card-body">
                <% for (ReservationEvenement reservation : recentReservations) { %>
                <div class="recent-reservation">
                  <div class="d-flex justify-content-between align-items-center mb-1">
                    <strong>#<%= reservation.getId() %></strong>
                    <span class="badge badge-reservation statut-<%= reservation.getStatut() %>">
                      <%= reservation.getStatut() %>
                    </span>
                  </div>
                  <small class="text-muted">
                    <i class="fe fe-user fe-12"></i> 
                    <% if (reservation.getUtilisateur() != null) { %>
                    <%= reservation.getUtilisateur().getNom() %> <%= reservation.getUtilisateur().getPrenom() %>
                    <% } else if (reservation.getNomPersonne() != null) { %>
                    <%= reservation.getNomPersonne() %>
                    <% } else { %>
                    Client #<%= reservation.getUtilisateurId() %>
                    <% } %>
                  </small><br>
                  <small class="text-muted">
                    <i class="fe fe-calendar fe-12"></i>
                    <% if (reservation.getDateReservationAsDate() != null) { %>
                    <fmt:formatDate value="<%= reservation.getDateReservationAsDate() %>" pattern="dd/MM HH:mm" />
                    <% } %>
                  </small>
                </div>
                <% } %>
                <div class="text-center mt-3">
                  <a href="EvenementServlet?action=reservationsevent&id=<%= evenement.getId() %>" 
                     class="btn btn-sm btn-outline-primary">
                    <i class="fe fe-list fe-12"></i> Voir toutes
                  </a>
                </div>
              </div>
            </div>
            <% } %>
          </div>
          
          <!-- Colonne droite : Description et actions -->
          <div class="col-lg-8">
            <!-- Description -->
            <div class="card shadow mb-4">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-file-text fe-16"></i> Description
                </h5>
              </div>
              <div class="card-body">
                <% if (evenement.getDescription() != null && !evenement.getDescription().isEmpty()) { %>
                <div class="event-description" style="line-height: 1.6;">
                  <%= evenement.getDescription().replace("\n", "<br>") %>
                </div>
                <% } else { %>
                <p class="text-muted font-italic">Aucune description fournie.</p>
                <% } %>
              </div>
            </div>
            
            <!-- Statistiques -->
            <div class="row mb-4">
              <div class="col-md-3 col-6">
                <div class="stat-card stat-total">
                  <h3 class="mb-1"><%= totalReservations %></h3>
                  <p class="mb-0">Total</p>
                  <small>Réservations</small>
                </div>
              </div>
              <div class="col-md-3 col-6">
                <div class="stat-card stat-confirmed">
                  <h3 class="mb-1"><%= confirmedReservations %></h3>
                  <p class="mb-0">Confirmées</p>
                  <small><%= totalReservations > 0 ? String.format("%.1f", (confirmedReservations * 100.0 / totalReservations)) : 0 %>%</small>
                </div>
              </div>
              <div class="col-md-3 col-6">
                <div class="stat-card stat-pending">
                  <h3 class="mb-1"><%= pendingReservations %></h3>
                  <p class="mb-0">En attente</p>
                  <small><%= totalReservations > 0 ? String.format("%.1f", (pendingReservations * 100.0 / totalReservations)) : 0 %>%</small>
                </div>
              </div>
              <div class="col-md-3 col-6">
                <div class="stat-card stat-cancelled">
                  <h3 class="mb-1"><%= cancelledReservations %></h3>
                  <p class="mb-0">Annulées</p>
                  <small><%= totalReservations > 0 ? String.format("%.1f", (cancelledReservations * 100.0 / totalReservations)) : 0 %>%</small>
                </div>
              </div>
            </div>
            
            <% if (totalRevenue > 0) { %>
            <div class="card shadow mb-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h5 class="mb-1">Revenu généré</h5>
                    <p class="text-muted mb-0">Total des réservations confirmées</p>
                  </div>
                  <div class="text-right">
                    <h3 class="text-success mb-0">
                      <fmt:formatNumber value="<%= totalRevenue %>" type="currency" currencyCode="HTG" />
                    </h3>
                    <small class="text-muted">HTG</small>
                  </div>
                </div>
              </div>
            </div>
            <% } %>
            
            <!-- Actions -->
            <div class="card shadow">
              <div class="card-header">
                <h5 class="card-title mb-0">
                  <i class="fe fe-settings fe-16"></i> Actions
                </h5>
              </div>
              <div class="card-body">
                <div class="action-btn-group">
                  <!-- Actions principales -->
                  <a href="EvenementServlet?action=modifier&id=<%= evenement.getId() %>" 
                     class="btn btn-primary">
                    <i class="fe fe-edit fe-16"></i> Modifier
                  </a>
                  
                  <a href="EvenementServlet?action=reservationsevent&id=<%= evenement.getId() %>" 
                     class="btn btn-info">
                    <i class="fe fe-users fe-16"></i> Réservations
                  </a>
                  
                  <% if ("VISIBLE".equals(evenement.getStatut())) { %>
                  <a href="EvenementServlet?action=desactiver&id=<%= evenement.getId() %>" 
                     class="btn btn-warning"
                     onclick="return confirm('Masquer cet événement ? Il ne sera plus visible par le public.')">
                    <i class="fe fe-eye-off fe-16"></i> Masquer
                  </a>
                  <% } else if ("HIDDEN".equals(evenement.getStatut())) { %>
                  <a href="EvenementServlet?action=reactiver&id=<%= evenement.getId() %>" 
                     class="btn btn-success"
                     onclick="return confirm('Rendre cet événement visible au public ?')">
                    <i class="fe fe-eye fe-16"></i> Rendre visible
                  </a>
                  <% } %>
                  
                  <!-- Gestion des tables -->
                  <% if (tables != null && !tables.isEmpty()) { %>
                  <a href="EvenementServlet?action=gerertables&id=<%= evenement.getId() %>" 
                     class="btn btn-outline-primary">
                    <i class="fe fe-grid fe-16"></i> Gérer tables
                  </a>
                  <% } %>
                  
                  <!-- Actions secondaires -->
                  <div class="dropdown d-inline-block">
                    <button class="btn btn-secondary dropdown-toggle" type="button" 
                            id="moreActions" data-toggle="dropdown">
                      <i class="fe fe-more-vertical fe-16"></i> Plus
                    </button>
                    <div class="dropdown-menu">
                     
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item text-danger" 
                         href="EvenementServlet?action=supprimer&id=<%= evenement.getId() %>"
                         onclick="return confirmSuppression()">
                        <i class="fe fe-trash-2 fe-16"></i> Supprimer
                      </a>
                    </div>
                  </div>
                  
                  <a href="EvenementServlet?action=lister" class="btn btn-outline-secondary">
                    <i class="fe fe-arrow-left fe-16"></i> Retour
                  </a>
                </div>
                
                <!-- Actions rapides pour les réservations -->
                <% if (totalReservations > 0) { %>
                <div class="mt-4 pt-3 border-top">
                  <h6 class="text-muted mb-2">Actions rapides sur les réservations</h6>
                  <div class="d-flex flex-wrap gap-2">
                    <% if (pendingReservations > 0) { %>
                    <a href="EvenementServlet?action=reservationsevent&id=<%= evenement.getId() %>&statut=EN_ATTENTE" 
                       class="btn btn-sm btn-outline-warning">
                      <i class="fe fe-clock fe-12"></i> Voir <%= pendingReservations %> en attente
                    </a>
                    <% } %>
                    <% if (confirmedReservations > 0) { %>
                    <a href="EvenementServlet?action=reservationsevent&id=<%= evenement.getId() %>&statut=CONFIRMEE" 
                       class="btn btn-sm btn-outline-success">
                      <i class="fe fe-check fe-12"></i> Voir <%= confirmedReservations %> confirmées
                    </a>
                    <% } %>
                  </div>
                </div>
                <% } %>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />

<script>
$(document).ready(function() {
    // Toast notifications
    <%
    String toastMessage = (String) session.getAttribute("ToastAdmErrorNotif");
    String toastType = (String) session.getAttribute("toastType");
    if (toastMessage != null) {
    %>
        showToast('<%= toastType %>', '<%= toastMessage %>');
    <%
        session.removeAttribute("ToastAdmErrorNotif");
        session.removeAttribute("toastType");
    }
    %>
    
    <%
    String successMessage = (String) session.getAttribute("ToastAdmSuccesNotif");
    if (successMessage != null) {
    %>
        showToast('success', '<%= successMessage %>');
    <%
        session.removeAttribute("ToastAdmSuccesNotif");
    }
    %>
});

function confirmSuppression() {
    return confirm('⚠️ Êtes-vous sûr de vouloir supprimer définitivement cet événement ?\n\n' +
                   'Cette action supprimera également toutes les réservations associées.\n' +
                   'Cette action est irréversible !');
}

function showToast(type, message) {
    // Créer le conteneur toast s'il n'existe pas
    if ($('.toast-container').length === 0) {
        $('body').append('<div class="toast-container position-fixed top-0 end-0 p-3"></div>');
    }
    
    const toastId = 'toast-' + Date.now();
    const toast = $(`
        <div id="${toastId}" class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `);
    
    $('.toast-container').append(toast);
    const bsToast = new bootstrap.Toast(toast[0]);
    bsToast.show();
    
    // Supprimer après fermeture
    toast.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}
</script>